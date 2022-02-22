package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.PropertyReader;
import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.server.services.InMemoryUserService;
import ru.avdeev.chat.server.services.SQLiteUserService;
import ru.avdeev.chat.server.services.UserService;

import java.sql.SQLException;

public class Application {

    public static void main(String[] args) {

        UserService userService = InMemoryUserService.getInstance();
        userService.addUser(new User("Сергей"), "avdey", "123");
        userService.addUser(new User("Иван"), "ivan", "456");
        userService.addUser(new User("Петя"), "petr", "789");

        UserService dbService;
        try {
            dbService = SQLiteUserService.create();
            dbService.addUser(new User("Сергей"), "avdey", "123");
            dbService.addUser(new User("Иван"), "ivan", "456");
            dbService.addUser(new User("Петя"), "petr", "789");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        new Server(
                Integer.parseInt(PropertyReader.getInstance().get("port")),
                dbService
        ).start();
    }
}
