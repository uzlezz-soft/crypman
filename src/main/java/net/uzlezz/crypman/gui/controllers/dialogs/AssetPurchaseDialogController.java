package net.uzlezz.crypman.gui.controllers.dialogs;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.data.Cryptocurrency;
import net.uzlezz.crypman.gui.utils.AlertUtils;
import net.uzlezz.crypman.gui.utils.CustomEvents;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.views.AssetListCell;
import net.uzlezz.crypman.gui.views.AssetPurchaseListCell;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.BuyOrder;
import net.uzlezz.crypman.network.schemas.request.BuyOrders;
import net.uzlezz.crypman.network.schemas.request.CryptocurrencyGroup;
import net.uzlezz.crypman.network.schemas.request.Optimization;
import net.uzlezz.crypman.network.schemas.response.OptimizationResult;
import net.uzlezz.crypman.network.schemas.response.PortfolioStats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class AssetPurchaseDialogController implements Initializable {

    @FXML
    private Label optimizationStatusLabel;
    @FXML
    private Label totalRiskLabel;
    @FXML
    private Label totalReturnLabel;
    @FXML
    private HBox targetReturnHBox;
    @FXML
    private HBox targetReturnSliderContainer;
    @FXML
    private Slider targetReturnSlider;
    @FXML
    private Label targetReturnLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private ListView<Cryptocurrency> assetListView;
    private boolean isCancelled = true;

    private final Map<Cryptocurrency, AssetPurchaseListCell> amountMap = new HashMap<>();
    private BigDecimal totalCost;

    public void onConfirmButtonPressed() {
        if (totalCost.compareTo(new BigDecimal(balanceLabel.getText().replace("$", ""))) > 0) {
            AlertUtils.showError("Ошибка", "Недостаточно средств");
            return;
        }
        if (AlertUtils.showConfirmation("Подтвердите действие",
                String.format("Вы действительно хотите купить криптовалюту на сумму $%.2f?", totalCost)) != ButtonType.OK) return;

        List<BuyOrder> orders = amountMap.entrySet().stream()
                .map(x -> new BuyOrder(x.getKey().getName(), x.getValue().getAmount()))
                .toList();
        var response = ServerClient.getInstance().sendRequest(new Request("portfolio/buy", new BuyOrders(orders)));
        ResponseFailureUtils.handle(response);

        isCancelled = false;
        ((Stage)assetListView.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetListView.setCellFactory(listView -> new AssetPurchaseListCell(assetListView, this));
        assetListView.addEventHandler(CustomEvents.ASSET_AMOUNT_CHANGED, x -> {
            totalCost = amountMap.entrySet().stream()
                    .map(entry -> entry.getValue().getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalPriceLabel.setText("$" + totalCost.setScale(2, RoundingMode.HALF_EVEN));
        });

        targetReturnSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
            targetReturnLabel.setText(String.format("%.4f%%", Math.pow(2, newValue.doubleValue())));
        });
        targetReturnSlider.valueProperty().set(Math.log(1) / Math.log(2));

        targetReturnSliderContainer.widthProperty().addListener((ov, oldWidth, newWidth) -> targetReturnSlider.setPrefWidth(newWidth.doubleValue()));
        targetReturnSliderContainer.prefWidthProperty().bind(targetReturnHBox.widthProperty().multiply(0.7));

        assetListView.getItems().setAll(MarketState.getCryptocurrencies());
    }

    private void updateStats(double risk, double returns) {
        totalRiskLabel.setText(String.format("%.4f%%", risk * 100));
        totalReturnLabel.setText(String.format("%.4f%%", returns * 100));
    }

    public void onComputeStatsButtonPressed() {
        var payload = new CryptocurrencyGroup(7, amountMap.entrySet().stream()
                .collect(Collectors.toMap(x -> x.getKey().getName(), x -> x.getValue().getAmount()
                        .divide(totalCost, 15, RoundingMode.HALF_EVEN).doubleValue())));
        var response = ServerClient.getInstance().sendRequest(new Request("portfolio/calculateStats", payload));
        ResponseFailureUtils.handle(response);

        var stats = (PortfolioStats)response.getPayload();
        updateStats(stats.getRisk(), stats.getReturns());
    }

    private void applyOptimization(Optimization optimization) {
        var response = ServerClient.getInstance().sendRequest(new Request("portfolio/optimize", optimization));
        ResponseFailureUtils.handle(response);
        var result = (OptimizationResult)response.getPayload();

        var totalAmount = result.getWeights().values().stream().reduce(0.0, Double::sum);
        if (totalAmount == 0) {
            optimizationStatusLabel.setText("ошибка");
            //AlertUtils.showError("Ошибка", "Оптимизация не удалась");
            return;
        }

        var currentTotalCost = totalCost.add(BigDecimal.ZERO);
        updateStats(result.getStats().getRisk(), result.getStats().getReturns());
        for (var entry : result.getWeights().entrySet()) {
            var crypto = MarketState.getCryptocurrency(entry.getKey());
            var amount = new BigDecimal(entry.getValue());
            if (amount.compareTo(new BigDecimal("0.001")) < 0) amount = BigDecimal.ZERO;
            amountMap.get(crypto).setAmount(currentTotalCost.multiply(amount).toString());
        }
        optimizationStatusLabel.setText("успех");
    }

    public void onOptimizeForTargetReturnButtonPressed() {
        var payload = new Optimization(true, Double.valueOf(targetReturnLabel.getText().replace('%', '0')) / 100.0, 7,
                amountMap.keySet().stream().map(Cryptocurrency::getName).toList());
        applyOptimization(payload);
    }

    public void onOptimizeForMinRiskButtonPressed() {
        var payload = new Optimization(false, 0.0, 7,
                amountMap.keySet().stream().map(Cryptocurrency::getName).toList());
        applyOptimization(payload);
    }

}
