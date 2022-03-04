package ru.avdeev.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;
import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.server.services.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final List<ClientHandler> clients;
    private final UserService userService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Logger logger = LogManager.getLogger();

    public Server(int port, UserService userService) {
        this.port = port;
        this.userService = userService;
        clients = new ArrayList<>();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port {}", port);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                executorService.execute(ClientHandler.createInstance(socket, this));
                logger.info("Client connected");
            }
        } catch (IOException e) {
            logger.error(e);
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
        logger.info("Client {} disconnected", client.getUser().getName());
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
