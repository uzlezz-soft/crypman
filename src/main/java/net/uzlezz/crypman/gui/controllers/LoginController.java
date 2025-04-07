package net.uzlezz.crypman.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.UserLogin;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void onLoginButtonPressed() {
        var payload = new UserLogin(usernameField.getText(), passwordField.getText());
        var request = new Request("user/login", payload);
        var response = ServerClient.getInstance().sendRequest(request);
        ResponseFailureUtils.handle(response);

        request = new Request("user/getInfo", null);
        response = ServerClient.getInstance().sendRequest(request);
        ResponseFailureUtils.handle(response);

        System.out.printf("%s%n", response);
    }

    @FXML
    public void onRegButtonPressed() {
        StageLoader.load(usernameField.getScene(), StagePath.REGISTER);
    }
}
