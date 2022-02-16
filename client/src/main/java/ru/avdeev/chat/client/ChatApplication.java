package ru.avdeev.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/main-window.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Easy Chat");
        mainStage = stage;
        stage.show();
    }

    public static void run(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return mainStage;
    }
}
