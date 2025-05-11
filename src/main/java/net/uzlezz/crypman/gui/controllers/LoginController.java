package net.uzlezz.crypman.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.UserLogin;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void onLoginButtonPressed() {
        var request = new Request("user/login", new UserLogin(usernameField.getText(), passwordField.getText()));
        var response = ServerClient.getInstance().sendRequest(request);
        ResponseFailureUtils.handle(response);

        StageLoader.load(usernameField.getScene(), StagePath.PORTFOLIO);
    }

    @FXML
    public void onRegButtonPressed() {
        StageLoader.load(usernameField.getScene(), StagePath.REGISTER);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ResponseFailureUtils.handle(ServerClient.getInstance().sendRequest(new Request("portfolio/calculate", 7)));
    }
}
