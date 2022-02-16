package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.PropertyReader;
import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.server.services.InMemoryUserService;
import ru.avdeev.chat.server.services.UserService;

public class Application {

    public static void main(String[] args) {

        UserService userService = InMemoryUserService.getInstance();
        userService.addUser(new User("Сергей"), "avdey", "123");
        userService.addUser(new User("Иван"), "ivan", "456");
        userService.addUser(new User("Петя"), "petr", "789");

        System.out.println(userService.auth("avdey", "123"));

        new Server(
                Integer.parseInt(PropertyReader.getInstance().get("port")),
                userService
        ).start();
    }
}
