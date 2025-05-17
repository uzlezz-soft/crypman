package net.uzlezz.crypman.gui.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.controllers.dialogs.AddMoneyDialogController;
import net.uzlezz.crypman.gui.controllers.dialogs.AssetPurchaseDialogController;
import net.uzlezz.crypman.gui.controllers.dialogs.AssetSellDialogController;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.AlertUtils;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.gui.views.AssetListCell;
import net.uzlezz.crypman.gui.views.PortfolioHistoryListCell;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.Deposit;
import net.uzlezz.crypman.network.schemas.request.SellOrder;
import net.uzlezz.crypman.network.schemas.request.SellOrders;
import net.uzlezz.crypman.network.schemas.response.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PortfolioController implements Initializable {
    @FXML
    private Label balanceLabel;
    @FXML
    private  ComboBox<String> chartTypeComboBox;
    @FXML
    private  LineChart<Number, Number> priceChart;
    @FXML
    private  NumberAxis xAxis;
    @FXML
    private  NumberAxis yAxis;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button marketControlButton;
    @FXML
    private Text totalPriceText;

    @FXML
    private ListView<Asset> assetListView;
    @FXML
    private ListView<PortfolioActionRecord> portfolioHistoryListView;

    private FileChooser portfolioHistoryFileChooser;

    private final ZoneId localZoneId = ZoneId.systemDefault();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetListView.setCellFactory(listView -> new AssetListCell());
        assetListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldAsset, newAsset) -> {
            if (newAsset != null)
                chartTypeComboBox.getSelectionModel().select(newAsset.getName());
        });

        portfolioHistoryListView.setCellFactory(listView -> new PortfolioHistoryListCell());

        portfolioHistoryFileChooser = new FileChooser();
        portfolioHistoryFileChooser.setTitle("Файл с историей портфеля");
        portfolioHistoryFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));

        updateUserInfo();

        xAxis.setTickLabelFormatter(new StringConverter<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            public String toString(Number object) {
                long epochSeconds = object.longValue();
                LocalDateTime localTime = Instant.ofEpochSecond(epochSeconds).atZone(localZoneId).toLocalDateTime();
                return formatter.format(localTime);
            }

            @Override
            public Number fromString(String string) {
                // Ненужно, если не редактируем вручную
                return 0;
            }
        });

        refreshPortfolio();
    }

    private void updateUserInfo() {

        var response = ServerClient.getInstance().sendRequest(new Request("user/getInfo"));
        ResponseFailureUtils.handle(response);
        var userInfo = (UserInfo)response.getPayload();
        usernameLabel.setText(userInfo.getUserName());

        if (userInfo.getRoles().contains("Admin")) {
            marketControlButton.setVisible(true);
        }

        balanceLabel.setText(String.format("$%.2f", userInfo.getBalance()));
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

        var previousSelection = chartTypeComboBox.getSelectionModel().getSelectedItem();
        chartTypeComboBox.getItems().clear();
        chartTypeComboBox.getItems().addAll(MarketState.getCryptocurrencies().stream()
                .map(x -> x.getName()).toList());
        if (previousSelection == null)
            chartTypeComboBox.getSelectionModel().selectFirst();
        else
            chartTypeComboBox.getSelectionModel().select(previousSelection);

        populateChart();

        portfolioHistoryListView.getItems().setAll(fetchPortfolioHistory());
    }

    private List<PortfolioActionRecord> fetchPortfolioHistory() {
        var response = ServerClient.getInstance().sendRequest(new Request("portfolio/history"));
        ResponseFailureUtils.handle(response);
        return ((List<PortfolioActionRecord>)response.getPayload()).stream()
                .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime())).toList();
    }

    public void onLogoutButtonPressed() {
        if (AlertUtils.showConfirmation("Вы действительно хотите выйти?", "") == ButtonType.OK) {
            ResponseFailureUtils.handle(ServerClient.getInstance().sendRequest(new Request("user/logout")));
            StageLoader.load(usernameLabel.getScene(), StagePath.LOGIN);
        }
    }

    public void onNewButtonPressed() {
        StageLoader.load(usernameLabel.getScene(), StagePath.MARKET_CONTROL);
    }

    public void onChartTypeChanged() {
        populateChart();
    }

    private void populateChart() {
        populateChart(chartTypeComboBox.getValue());
    }

    private void populateChart(String cryptoName) {
        Platform.runLater(() -> {
            var type = cryptoName != null ? cryptoName : "BTC";
            var response = ServerClient.getInstance().sendRequest(new Request("market/priceHistory", type));
            ResponseFailureUtils.handle(response);
            populateChart((List<PriceRecord>)response.getPayload());
        });
    }

    private void populateChart(List<PriceRecord> priceRecords) {
        ZonedDateTime now = ZonedDateTime.now(localZoneId);
        ZonedDateTime oneWeekAgo = now.minusDays(7);

        long nowEpoch = now.toEpochSecond();
        long weekAgoEpoch = oneWeekAgo.toEpochSecond();

        var filtered = priceRecords.stream()
                .map(record -> new AbstractMap.SimpleEntry<>(record.getTime().atZone(localZoneId).toEpochSecond(), record.getValue()))
                .filter(entry -> entry.getKey() <= nowEpoch)
                .sorted(Comparator.comparingLong(Map.Entry::getKey))
                .toList();
        if (filtered.isEmpty()) {
            priceChart.getData().clear();
            return;
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("История цен");

        BigDecimal minPrice = filtered.get(0).getValue();
        BigDecimal maxPrice = filtered.get(0).getValue();

        for (var entry : filtered) {
            BigDecimal value = entry.getValue();
            series.getData().add(new XYChart.Data<>(entry.getKey(), value));

            if (value.compareTo(minPrice) < 0) minPrice = value;
            if (value.compareTo(maxPrice) > 0) maxPrice = value;
        }

        Map.Entry<Long, BigDecimal> last = filtered.get(filtered.size() - 1);
        if (last.getKey() < nowEpoch) {
            series.getData().add(new XYChart.Data<>(nowEpoch, last.getValue()));
        }

        priceChart.getData().clear();
        priceChart.getData().add(series);

        BigDecimal finalMinPrice = minPrice;
        BigDecimal finalMaxPrice = maxPrice;
        Platform.runLater(() -> {
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(weekAgoEpoch);
            xAxis.setUpperBound(nowEpoch);
            xAxis.setTickUnit((nowEpoch - weekAgoEpoch) / 10.0);

            yAxis.setAutoRanging(false);
            double minY = Math.max(finalMinPrice.doubleValue(), 0.0);
            double maxY = finalMaxPrice.doubleValue();

            double padding = (maxY - minY) * 0.1;
            if (padding == 0) padding = maxY * 0.05;

            yAxis.setLowerBound(minY - padding);
            yAxis.setUpperBound(maxY + padding);
            yAxis.setTickUnit((maxY + padding - (minY - padding)) / 10.0);
        });
    }

    public void addMoneyButtonPressed() {
        Stage dialog = new Stage();

        var loader = new FXMLLoader(StageLoader.class.getResource(StagePath.ADD_MONEY_DIALOG.getPath()));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Ошибка", "");
            return;
        }

        dialog.setScene(new Scene(root));
        dialog.setTitle("Пополнение баланса");
        dialog.centerOnScreen();

        dialog.initOwner(usernameLabel.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        var controller = (AddMoneyDialogController)loader.getController();
        if (!controller.isCancelled()) {
            var payload = new Deposit(controller.getMoneyAmount());
            var response = ServerClient.getInstance().sendRequest(new Request("user/deposit", payload));
            ResponseFailureUtils.handle(response);

            updateUserInfo();
            refreshPortfolio();
        }
    }

    public void buyButtonPressed() {
        Stage dialog = new Stage();
        var loader = new FXMLLoader(StageLoader.class.getResource(StagePath.PURCHASE_ASSETS_DIALOG.getPath()));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Ошибка", "");
            return;
        }
        var controller = (AssetPurchaseDialogController)loader.getController();

        dialog.setScene(new Scene(root));
        dialog.setTitle("Покупка активов");
        dialog.centerOnScreen();

        controller.getBalanceLabel().setText(balanceLabel.getText());

        dialog.initOwner(usernameLabel.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        if (!controller.isCancelled()) {
            updateUserInfo();
            refreshPortfolio();
        }
    }

    public void sellButtonPressed() {
        Stage dialog = new Stage();
        var loader = new FXMLLoader(StageLoader.class.getResource(StagePath.SELL_ASSETS_DIALOG.getPath()));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Ошибка", "");
            return;
        }
        var controller = (AssetSellDialogController)loader.getController();

        dialog.setScene(new Scene(root));
        dialog.setTitle("Продажа активов");
        dialog.centerOnScreen();

        controller.getBalanceLabel().setText(balanceLabel.getText());

        dialog.initOwner(usernameLabel.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        if (!controller.isCancelled()) {
            updateUserInfo();
            refreshPortfolio();
        }
    }

    public void exportPortfolioHistoryButtonPressed() {
        var history = fetchPortfolioHistory();

        var file = portfolioHistoryFileChooser.showSaveDialog(usernameLabel.getScene().getWindow());
        if (file == null) return;

        try (var writer = new BufferedWriter(new FileWriter(file))) {
            history.stream()
                    .map(x -> String.format("[%s] [%10s] баланс %20s%s\n", dateTimeFormatter.format(x.getDateTime()), x.getAction(),
                            String.format("$%.4f", x.getBalanceDelta()), Objects.equals(x.getCryptocurrencyName(), "") ? ""
                            : String.format("     криптовалюта %20s", String.format("%.9f %s", x.getCryptocurrencyDelta(), x.getCryptocurrencyName()))))
                    .forEach(str -> {
                        try {
                            writer.write(str);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
