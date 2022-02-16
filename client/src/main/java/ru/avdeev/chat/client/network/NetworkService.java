package ru.avdeev.chat.client.network;

import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.PropertyReader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkService {

    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final List<MessageProcessor> messageProcessor;

    private static NetworkService instance;

    private NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
        this.messageProcessor = new ArrayList<>();
    }

    public static NetworkService getInstance() {

        if (instance == null) {
            instance = new NetworkService(
                    PropertyReader.getInstance().get("host"),
                    Integer.parseInt(PropertyReader.getInstance().get("port")));
        }
        return instance;
    }

    public void addMessageProcessor(MessageProcessor processor) {
        this.messageProcessor.add(processor);
    }

    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        readMessages();
    }

    public void readMessages() {
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                    String message = inputStream.readUTF();
                    processMessage(message);
                }
            } catch (IOException e) {
                close();
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeUTF(message.toString());
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String message) {
        System.out.println(message);
        for (MessageProcessor process : messageProcessor) {
            process.processMessage(message);
        }
    }
}
