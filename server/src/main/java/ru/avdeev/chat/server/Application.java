package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.PropertyReader;
import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.server.services.InMemoryUserService;
import ru.avdeev.chat.server.services.SQLiteUserService;
import ru.avdeev.chat.server.services.UserService;

import java.sql.SQLException;

public class Application {

    public static void main(String[] args) {

        UserService userService;
        String userServiceName = PropertyReader.getInstance().get("userService.name");
        int port = Integer.parseInt(PropertyReader.getInstance().get("port"));
        if (userServiceName.equals("SQLiteUserService")) {
            try {
                userService = SQLiteUserService.getInstance();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        } else {
            userService = InMemoryUserService.getInstance();
        }

        userService.addUser(new User("Сергей"), "avdey", "123");
        userService.addUser(new User("Иван"), "ivan", "456");
        userService.addUser(new User("Петя"), "petr", "789");

        new Server(port, userService).start();
    }

    public static int[] getArrayAfterFour(int arr[]) {

        int fIndex = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 4)fIndex = i;
        }
        if (fIndex == -1)throw new RuntimeException("Four not found");

        int result[] = new int[arr.length - fIndex - 1];
        for (int i = fIndex + 1, j = 0; i < arr.length; i++, j++) {
            result[j] = arr[i];
        }
        return result;
    }

    public static boolean findOneFour(int arr[]) {
        boolean one = false;
        boolean four = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1) one = true;
            if (arr[i] == 4) four = true;
            if (one || four) break;
        }
        return one || four;
    }


}
