package ru.avdeev.chat.server.services;

import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.commons.Utils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryUserService implements UserService{

    private static InMemoryUserService instance;
    private final HashMap<Integer, UserEntity> users;
    private int counter;

    private InMemoryUserService() {
        users = new HashMap<>();
        counter = 1;
    }

    public static InMemoryUserService getInstance() {

        if (instance == null) {
            instance = new InMemoryUserService();
        }
        return instance;
    }

    @Override
    public User getUser(int id) {
        return users.get(id).user;
    }

    @Override
    public User getUserByLogin(String login) {

        UserEntity entity = getUserEntityByLogin(login);
        if (entity != null)return entity.user;

        return null;
    }

    @Override
    public User addUser(User user, String login, String password) {

        User newUser = new User(counter, user.getName());
        users.put(counter, new UserEntity(newUser, Utils.hash(login), Utils.hash(password)));
        counter++;
        return newUser;
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public User auth(String login, String password) {

        UserEntity usr = getUserEntityByLogin(login);

        if (usr != null && usr.password.equals(Utils.hash(password))) {
            return usr.user;
        }
        return null;
    }

    @Override
    public void changeName(int id, String name) {

        UserEntity usr = users.get(id);
        usr.user.setName(name);
    }

    @Override
    public void setPassword(int id, String password) {

        UserEntity usr = users.get(id);
        usr.password = Utils.hash(password);
    }

    private static class UserEntity {
        private final User user;
        private final String login;
        private String password;

        public UserEntity(User user, String login, String password) {
            this.user = user;
            this.login = login;
            this.password = password;
        }
    }

    private UserEntity getUserEntityByLogin(String login) {

        AtomicReference<UserEntity> usr = new AtomicReference<>();
        users.forEach((key, value) -> {
            if (value.login.equals(Utils.hash(login))) {
                usr.set(value);
            }
        });

        return usr.get();
    }
}
