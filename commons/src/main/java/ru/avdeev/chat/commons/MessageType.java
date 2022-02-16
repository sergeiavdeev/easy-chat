package ru.avdeev.chat.commons;

public enum MessageType {
    REQUEST_AUTH ("/REQUEST_AUTH"),
    RESPONSE_AUTH_OK ("/RESPONSE_AUTH_OK"),
    RESPONSE_AUTH_ERROR ("/RESPONSE_AUTH_ERROR"),

    SEND_ALL ("/SEND_ALL"),
    SEND_PRIVATE ("/SEND_PRIVATE"),

    USER_ONLINE ("/USER_ONLINE"),
    USER_OFFLINE ("/USER_OFFLINE"),

    UNDEFINED ("/UNDEFINED");


    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
