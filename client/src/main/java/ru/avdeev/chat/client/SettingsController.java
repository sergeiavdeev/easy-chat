package ru.avdeev.chat.client;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.avdeev.chat.client.network.NetworkService;

import javafx.event.ActionEvent;
import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public TextField nameField;
    public TextField oldPassField;
    public TextField passField;

    private NetworkService networkService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = NetworkService.getInstance();
        nameField.setText(networkService.getUser().getName());
    }

    public void saveSettings(ActionEvent event) {

        if (!nameField.getText().isEmpty()){
            networkService.sendMessage(new Message(MessageType.REQUEST_USER_NAME_CHANGE,
                    new String[]{
                            networkService.getUser().getIdString(),
                            nameField.getText()
                    })
            );
        }

        if (!oldPassField.getText().isEmpty()&&!passField.getText().isEmpty()) {
            networkService.sendMessage(new Message(MessageType.REQUEST_USER_PASSWORD_CHANGE,
                    new String[]{
                            networkService.getUser().getIdString(),
                            oldPassField.getText(),
                            passField.getText()
                    })
            );
        }
        close(event);
    }

    public void cancel(ActionEvent event) {
        close(event);
    }

    private void close(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
