package net.uzlezz.crypman.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.gui.views.AssetListCell;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.CryptocurrencyRegistration;
import net.uzlezz.crypman.network.schemas.response.Asset;
import net.uzlezz.crypman.gui.data.Cryptocurrency;
import net.uzlezz.crypman.network.schemas.response.Portfolio;
import net.uzlezz.crypman.network.schemas.response.UserInfo;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MarketControlController implements Initializable {
    @FXML
    private TableView<Cryptocurrency> cryptoTableView;
    @FXML
    private TableColumn<Cryptocurrency, String> nameColumn;
    @FXML
    private TableColumn<Cryptocurrency, String> friendlyNameColumn;
    @FXML
    private TableColumn<Cryptocurrency, String> iconColumn;
    @FXML
    private Label usernameLabel;

    private final ObservableList<Cryptocurrency> cryptocurrencies = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            var response = ServerClient.getInstance().sendRequest(new Request("user/getInfo"));
            ResponseFailureUtils.handle(response);
            usernameLabel.setText(((UserInfo) response.getPayload()).getUserName());

            refreshCryptoList();
        } catch (RuntimeException e) {
            StageLoader.load(usernameLabel.getScene(), StagePath.PORTFOLIO);
        }

        nameColumn.setCellFactory(new PropertyValueFactory<>("name"));
        friendlyNameColumn.setCellFactory(new PropertyValueFactory<>("friendlyName"));
        iconColumn.setCellFactory(new PropertyValueFactory<>("icon"));
        cryptoTableView.setItems(cryptocurrencies);
    }

    private void refreshCryptoList() {
        var response = ServerClient.getInstance().sendRequest(new Request("market/actives"));
        ResponseFailureUtils.handle(response);
        MarketState.setCryptocurrencyList((List<net.uzlezz.crypman.network.schemas.response.Cryptocurrency>) response.getPayload());

        cryptocurrencies.clear();
        cryptocurrencies.addAll(MarketState.getCryptocurrencies());
    }

    public void onRefreshButtonPressed() {
        refreshCryptoList();
    }

    public void onBackButtonPressed() {
        StageLoader.load(usernameLabel.getScene(), StagePath.PORTFOLIO);
    }
}
