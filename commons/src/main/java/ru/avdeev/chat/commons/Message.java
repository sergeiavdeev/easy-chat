package ru.avdeev.chat.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Message {

    private MessageType type;
    private final List<String> params;
    private final StringBuilder sb;
    public static final String REGEX = "&~&";

    public Message(String message) {

        String[] params = message.split(REGEX);

        setType(params[0]);

        sb = new StringBuilder(type.toString());
        this.params = new ArrayList<>();
        this.params.addAll(Arrays.asList(params).subList(1, params.length));
    }

    public Message(MessageType type, String[] params) {

        this.type = type;
        this.params = Arrays.asList(params);
        sb = new StringBuilder(type.toString());
    }

    @Override
    public String toString() {

        for (String param : params) {
            sb.append(REGEX).append(param);
        }
        return sb.toString();
    }

    public MessageType getType() {
        return type;
    }

    public List<String> getParams() {
        return params;
    }

    private void setType(String t) {
        switch (t) {
            case "/REQUEST_AUTH":
                type = MessageType.REQUEST_AUTH;
                break;
            case "/RESPONSE_AUTH_OK":
                type = MessageType.RESPONSE_AUTH_OK;
                break;
            case "/RESPONSE_AUTH_ERROR":
                type = MessageType.RESPONSE_AUTH_ERROR;
                break;
            case "/SEND_ALL":
                type = MessageType.SEND_ALL;
                break;
            case "/SEND_PRIVATE":
                type = MessageType.SEND_PRIVATE;
                break;
            case "/USER_ONLINE":
                type = MessageType.USER_ONLINE;
                break;
            case "/USER_OFFLINE":
                type = MessageType.USER_OFFLINE;
                break;
            case "/REQUEST_USER_NAME_CHANGE":
                type = MessageType.REQUEST_USER_NAME_CHANGE;
                break;
            case "/RESPONSE_USER_NAME_CHANGE_OK":
                type = MessageType.RESPONSE_USER_NAME_CHANGE_OK;
                break;
            case "/RESPONSE_USER_NAME_CHANGE_ERROR":
                type = MessageType.RESPONSE_USER_NAME_CHANGE_ERROR;
                break;
            case "/REQUEST_USER_PASSWORD_CHANGE":
                type = MessageType.REQUEST_USER_PASSWORD_CHANGE;
                break;
            case "/RESPONSE_USER_PASSWORD_CHANGE_OK":
                type = MessageType.RESPONSE_USER_PASSWORD_CHANGE_OK;
                break;
            case "/RESPONSE_USER_PASSWORD_CHANGE_ERROR":
                type = MessageType.RESPONSE_USER_PASSWORD_CHANGE_ERROR;
                break;
            default:
                type = MessageType.UNDEFINED;
                break;
        }
    }
}
