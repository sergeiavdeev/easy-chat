package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;
import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.server.services.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private final int port;
    private final List<ClientHandler> clients;
    private final UserService userService;

    public Server(int port, UserService userService) {
        this.port = port;
        this.userService = userService;
        clients = new ArrayList<>();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Server started on port %d\n", port);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                ClientHandler.createInstance(socket, this).handle();
                System.out.println("Client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        for (ClientHandler clientHandler : clients) {

            if (clientHandler == client)continue;

            clientHandler.send(new Message(MessageType.USER_ONLINE,
                    new String[]{client.getUser().getIdString(), client.getUser().getName()})
            );

            client.send(
                    new Message(MessageType.USER_ONLINE,
                            new String[]{clientHandler.getUser().getIdString(), clientHandler.getUser().getName()})
            );
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.printf("Client %s disconnected\n", client.getUser().getName());
        User disconnectedUser = client.getUser();
        if (!isUserOnline(disconnectedUser)) {
            sendUserOffline(disconnectedUser);
        }
    }

    public void broadcastMessage(User sender, String message) {
        for (ClientHandler client : clients) {
            client.send(new Message(MessageType.SEND_ALL,
                    new String[]{sender.toString(), message}));
        }
    }

    public void privateMessage(User sender, User receiver, String message) {

        for (ClientHandler client : clients) {
            if (client.getUser().equals(receiver)) {
                client.send(new Message(MessageType.SEND_PRIVATE,
                        new String[]{sender.toString(), message}));
            }
        }
    }

    public void changeUserName(int userId, String name) {
        userService.changeName(userId, name);
        sendChangeUserNameOk(userId, name);
    }

    public void changeUserPassword(int userId, String password) {
        userService.setPassword(userId, password);
    }

    private void sendUserOffline(User user) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.send(
                    new Message(MessageType.USER_OFFLINE,
                            new String[]{user.getIdString(), user.getName()})
            );
        }
    }

    private boolean isUserOnline(User user) {
        for (ClientHandler client : clients) {
            if (client.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    private void sendChangeUserNameOk(int userId, String name) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.send(
                    new Message(MessageType.RESPONSE_USER_NAME_CHANGE_OK,
                            new String[]{String.valueOf(userId), name})
            );
            if (clientHandler.getUser().getId() == userId) {
                clientHandler.getUser().setName(name);
            }
        }
    }
}
