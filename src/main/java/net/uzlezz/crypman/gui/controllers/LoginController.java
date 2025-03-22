package net.uzlezz.crypman.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.AlertUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void onLoginButtonPressed() {
        var bundle = ResourceBundle.getBundle("server");
        var address = bundle.getString("SERVER_ADDRESS");
        var port = Integer.parseInt(bundle.getString("SERVER_PORT"));

        try (var socket = new Socket(address, port)) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onRegButtonPressed() {
        StageLoader.load(usernameField.getScene(), StagePath.REGISTER);
    }
}
