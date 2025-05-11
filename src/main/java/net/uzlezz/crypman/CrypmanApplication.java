package net.uzlezz.crypman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ResponseStatus;
import net.uzlezz.crypman.network.ServerClient;

import java.io.IOException;
import java.util.Objects;

public class CrypmanApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.png"))));

        FXMLLoader fxmlLoader = new FXMLLoader(CrypmanApplication.class.getResource("/views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        ServerClient.getInstance();
    }

    @Override
    public void stop() {
        var client = ServerClient.getInstance();
        if (client == null) return;

        var request = new Request("DISCONNECT");
        var response = client.sendRequest(request);

        if (response != null && response.getStatus() == ResponseStatus.OK) {
            System.out.println("Disconnected successfully from the server");
        }
        client.disconnect();
    }

    public static void main(String[] args) {
        launch();
    }
}