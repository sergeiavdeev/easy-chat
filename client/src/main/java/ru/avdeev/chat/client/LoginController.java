package ru.avdeev.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ru.avdeev.chat.client.network.NetworkService;
import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;

import java.io.IOException;

public class LoginController {
    public TextField loginField;
    public PasswordField passwordField;
    public VBox loginPanel;

    public void sendAuth(ActionEvent actionEvent) {

        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            return;
        }

        NetworkService networkService = NetworkService.getInstance();

        networkService.addMessageProcessor((message -> {
            Message inMessage = new Message(message);
            switch (inMessage.getType()) {
                case RESPONSE_AUTH_OK:
                    loginPanel.setVisible(false);
                    break;
                case RESPONSE_AUTH_ERROR:
                    Platform.runLater(() -> showError("Wrong login or password"));
                    break;
            }
        }));

        if (!networkService.isConnected()) {
            try {
                networkService.connect();
            } catch (IOException e) {
                //e.printStackTrace();
                showError(e.getMessage());
                return;
            }
        }
        networkService.sendMessage(new Message(MessageType.REQUEST_AUTH, new String[]{login, password}));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "An error occurred: " + message,
                ButtonType.OK);
        alert.showAndWait();
    }
}
