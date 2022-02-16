package ru.avdeev.chat.server.services;

import ru.avdeev.chat.commons.User;

public interface UserService {
    public User getUser(int id);
    public User getUserByLogin(String login);
    public User addUser(User user, String login, String password);
    public void deleteUser(int id);
    public User auth(String login, String password);
    public void changeName(int id, String name);
    public void setPassword(int id, String password);
}
