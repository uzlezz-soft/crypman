package net.uzlezz.crypman.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.StageLoader;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmationPasswordField;

    @FXML
    public void onRegButtonPressed() {

    }

    @FXML
    public void onLoginButtonPressed() {
        StageLoader.load(usernameField.getScene(), StagePath.LOGIN);
    }
}
