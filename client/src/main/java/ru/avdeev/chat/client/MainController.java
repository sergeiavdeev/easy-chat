package ru.avdeev.chat.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ru.avdeev.chat.client.network.MessageProcessor;
import ru.avdeev.chat.client.network.NetworkService;

import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;
import ru.avdeev.chat.commons.User;

import java.net.URL;

import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable, MessageProcessor {

    private NetworkService networkService;
    private User user;
    private final ObservableSet<User> contacts = FXCollections.observableSet();

    @FXML
    public VBox chatPanel;

    @FXML
    public ListView<User> contactList;

    @FXML
    public TextArea chatArea;

    @FXML
    public TextField messageField;

    @FXML
    public Button btnSend;

    private MediaPlayer player;

    public void sendMessage() {
        String message = messageField.getText();
        if (message.isEmpty()) {
            return;
        }

        User contact = contactList.getSelectionModel().getSelectedItem();
        if (contact == null || contact.getId() == 0) {
            //networkService.sendMessage(Helper.createMessage("/broadcast", message, ""));
            networkService.sendMessage(new Message(MessageType.SEND_ALL, new String[]{message}));
        } else {
            //networkService.sendMessage(Helper.createMessage("/private", contact, message));
            networkService.sendMessage(new Message(MessageType.SEND_PRIVATE, new String[]{contact.getIdString(), message}));
            chatArea.appendText(user.getName() + ": " + message + System.lineSeparator());
        }

        messageField.clear();
        player.stop();
        player.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        contacts.addListener((Change<? extends User> change) -> {
            if (change.wasAdded()) {
                if (!change.getElementAdded().equals(user))
                    contactList.getItems().add(change.getElementAdded());
            }
            if (change.wasRemoved()) {
                contactList.getItems().remove(change.getElementRemoved());
            }
        });

        contactList.getItems().add(new User("ALL"));

        Media sound = new Media(Objects.requireNonNull(getClass().getClassLoader().getResource("sound/switch.mp3")).toString());
        player = new MediaPlayer(sound);

        this.networkService = NetworkService.getInstance();
        this.networkService.addMessageProcessor(this);
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseIncomingMessage(message));
    }

    private void parseIncomingMessage(String message) {
        Message inMessage = new Message(message);
        switch (inMessage.getType()) {
            case RESPONSE_AUTH_OK:
                user = new User(Integer.parseInt(inMessage.getParams().get(0)), inMessage.getParams().get(1));
                chatPanel.setVisible(true);
                ChatApplication.getStage().setTitle("Easy Chat - " + user.getName());
                break;
            case SEND_ALL:
            case SEND_PRIVATE:
                chatArea.appendText(inMessage.getParams().get(0) + ": " + inMessage.getParams().get(1) + System.lineSeparator());
                break;
            case USER_ONLINE:
                contacts.add(new User(Integer.parseInt(inMessage.getParams().get(0)), inMessage.getParams().get(1)));
                break;
            case USER_OFFLINE:
                contacts.remove(new User(Integer.parseInt(inMessage.getParams().get(0)), inMessage.getParams().get(1)));
                break;
        }
    }
}