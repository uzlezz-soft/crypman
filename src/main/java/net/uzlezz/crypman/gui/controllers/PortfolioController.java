package net.uzlezz.crypman.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.gui.views.AssetListCell;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.response.Asset;
import net.uzlezz.crypman.network.schemas.response.Cryptocurrency;
import net.uzlezz.crypman.network.schemas.response.Portfolio;
import net.uzlezz.crypman.network.schemas.response.UserInfo;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PortfolioController implements Initializable {
    @FXML
    private Label usernameLabel;
    @FXML
    private Text totalPriceText;

    @FXML
    private ListView<Asset> assetListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetListView.setCellFactory(listView -> new AssetListCell());

        var response = ServerClient.getInstance().sendRequest(new Request("user/getInfo"));
        ResponseFailureUtils.handle(response);
        usernameLabel.setText(((UserInfo)response.getPayload()).getUserName());

        refreshPortfolio();
    }

    public void onRefreshButtonPressed() {
        refreshPortfolio();
    }

    private void refreshPortfolio() {
        var response = ServerClient.getInstance().sendRequest(new Request("market/actives"));
        ResponseFailureUtils.handle(response);
        MarketState.setCryptocurrencyList((List<Cryptocurrency>) response.getPayload());

        response = ServerClient.getInstance().sendRequest(new Request("portfolio/list"));
        ResponseFailureUtils.handle(response);

        var portfolio = (Portfolio)response.getPayload();
        assetListView.getItems().setAll(portfolio.getAssets());

        var totalPrice = portfolio.getAssets().stream().reduce(
                new BigDecimal(0),
                (subtotal, asset) -> subtotal.add(asset.getAmount().multiply(MarketState.getCryptocurrency(asset.getName()).getPrice())),
                BigDecimal::add);
        totalPriceText.setText(String.format("$%.2f", totalPrice));
    }

    public void onLogoutButtonPressed() {
        ResponseFailureUtils.handle(ServerClient.getInstance().sendRequest(new Request("user/logout")));
        StageLoader.load(usernameLabel.getScene(), StagePath.LOGIN);
    }
}
