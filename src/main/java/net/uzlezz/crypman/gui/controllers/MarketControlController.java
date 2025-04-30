package net.uzlezz.crypman.gui.controllers;

import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.util.Callback;
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

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MarketControlController implements Initializable {
    @FXML
    private TableColumn<Cryptocurrency, String> nameColumn;
    @FXML
    private TableView<Cryptocurrency> cryptoTableView;
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

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        friendlyNameColumn.setCellValueFactory(new PropertyValueFactory<>("friendlyName"));
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
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

    private Image loadIcon(String assetName) {
        String path = "/images/crypto/" + assetName.toLowerCase() + ".png";
        InputStream stream = getClass().getResourceAsStream(path);
        return stream != null ? new Image(stream) : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crypto/default.png")));
    }
}
