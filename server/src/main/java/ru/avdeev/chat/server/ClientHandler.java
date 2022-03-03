package ru.avdeev.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;
import ru.avdeev.chat.commons.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ClientHandler implements Runnable {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final Server server;
    private User user;
    private final Logger logger = LogManager.getLogger();

    public static ClientHandler createInstance(Socket socket, Server server) {
        return new ClientHandler(socket, server);
    }

    private ClientHandler(Socket socket, Server server) {

        this.server = server;

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean auth() {

        setTimeout();

        logger.info("Authorization start...");
        boolean isAuth = false;
        while (true) {
            try {
                String message = inputStream.readUTF();
                logger.trace("Received message {}", message);
                Message inMessage = new Message(message);
                if (inMessage.getType() == MessageType.REQUEST_AUTH) {

                    user = server.getUserService().auth(inMessage.getParams().get(0), inMessage.getParams().get(1));
                    if (user != null) {
                        outputStream.writeUTF(
                            new Message(MessageType.RESPONSE_AUTH_OK,
                                    new String[]{user.getIdString(), user.getName()}
                            ).toString()
                        );
                        server.addClient(this);
                        isAuth = true;
                        break;
                    } else {
                        outputStream.writeUTF(
                                new Message(MessageType.RESPONSE_AUTH_ERROR,
                                        new String[]{"Auth error", "Wrong login or password"}
                                ).toString()
                        );
                    }
                }
            } catch (IOException e) {
                logger.info("Unknown client disconnected");
                logger.error(e);
                Thread.currentThread().interrupt();
                break;
            }
        }
        return isAuth;
    }

    private void handleMessage(String message) {

        logger.trace("Received message {}", message);
        Message inMessage = new Message(message);
        switch (inMessage.getType()) {
            case SEND_ALL:
                server.broadcastMessage(user, inMessage.getParams().get(0));
                break;
            case SEND_PRIVATE:
                server.privateMessage(
                        user,
                        server.getUserService().getUser(Integer.parseInt(inMessage.getParams().get(0))),
                        inMessage.getParams().get(1)
                );
                break;
            case REQUEST_USER_NAME_CHANGE:
                server.changeUserName(user.getId(), inMessage.getParams().get(1));
                break;
            case REQUEST_USER_PASSWORD_CHANGE:
                server.changeUserPassword(user.getId(), inMessage.getParams().get(2));
                break;
            default:
                break;
        }
    }

    public void send(Message message) {
        try {
            outputStream.writeUTF(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    private void setTimeout() {
        new Thread(() -> {
            try {
                sleep(120 * 1000);
                if (user == null) {
                    inputStream.close();
                    outputStream.close();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void run() {

        if (!auth())return;
        logger.info("Client auth success with name {}({})", user.getName(), user.getId());
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String message = inputStream.readUTF();
                handleMessage(message);
            } catch (IOException e) {
                server.removeClient(this);
                Thread.currentThread().interrupt();
            }
        }
    }
}
