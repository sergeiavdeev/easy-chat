package ru.avdeev.chat.client.network;

public interface MessageProcessor {
    void processMessage(String message);
}
