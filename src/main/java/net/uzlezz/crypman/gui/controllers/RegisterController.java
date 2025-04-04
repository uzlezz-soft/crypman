package net.uzlezz.crypman.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.AlertUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmationPasswordField;

    @FXML
    public void onRegButtonPressed() {
        var request = new Request("user/register", null);
        var response = ServerClient.getInstance().sendRequest(request);

        AlertUtils.showInformation("Register", response.toString());
    }

    @FXML
    public void onLoginButtonPressed() {
        StageLoader.load(usernameField.getScene(), StagePath.LOGIN);
    }
}
