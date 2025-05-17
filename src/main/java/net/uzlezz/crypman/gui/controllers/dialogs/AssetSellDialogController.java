package net.uzlezz.crypman.gui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.data.Cryptocurrency;
import net.uzlezz.crypman.gui.data.PortfolioAction;
import net.uzlezz.crypman.gui.utils.AlertUtils;
import net.uzlezz.crypman.gui.utils.CustomEvents;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.views.AssetPurchaseListCell;
import net.uzlezz.crypman.gui.views.AssetSellListCell;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.*;
import net.uzlezz.crypman.network.schemas.response.Asset;
import net.uzlezz.crypman.network.schemas.response.OptimizationResult;
import net.uzlezz.crypman.network.schemas.response.Portfolio;
import net.uzlezz.crypman.network.schemas.response.PortfolioStats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Getter
public class AssetSellDialogController implements Initializable {
    @FXML
    private Label totalReturnsLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private ListView<Asset> assetListView;
    private boolean isCancelled = true;

    private final Map<Cryptocurrency, AssetSellListCell> amountMap = new HashMap<>();
    private BigDecimal totalReturns;

    public void onConfirmButtonPressed() {
        if (totalReturns.compareTo(BigDecimal.ZERO) == 0) {
            AlertUtils.showError("Ошибка", "Выберите активы");
            return;
        }
        if (AlertUtils.showConfirmation("Подтвердите действие",
                String.format("Вы действительно хотите продать криптовалюту на сумму $%.2f?", totalReturns)) != ButtonType.OK) return;

        List<SellOrder> orders = amountMap.entrySet().stream()
                .filter(x -> x.getValue().getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(x -> new SellOrder(x.getKey().getName(), x.getValue().getAmount()))
                .toList();
        var response = ServerClient.getInstance().sendRequest(new Request("portfolio/sell", new SellOrders(orders)));
        ResponseFailureUtils.handle(response);

        isCancelled = false;
        ((Stage)assetListView.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var response = ServerClient.getInstance().sendRequest(new Request("portfolio/list"));
        ResponseFailureUtils.handle(response);

        assetListView.setCellFactory(listView -> new AssetSellListCell(assetListView, this));
        assetListView.addEventHandler(CustomEvents.ASSET_AMOUNT_CHANGED, x -> {
            totalReturns = amountMap.entrySet().stream()
                    .map(entry -> entry.getValue().getAmount().multiply(entry.getKey().getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalReturnsLabel.setText("$" + totalReturns.setScale(2, RoundingMode.HALF_EVEN));
        });

        assetListView.getItems().setAll(((Portfolio)response.getPayload()).getAssets());
    }

}
