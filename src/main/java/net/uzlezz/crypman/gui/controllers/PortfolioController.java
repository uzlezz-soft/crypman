package net.uzlezz.crypman.gui.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.gui.views.AssetListCell;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.response.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PortfolioController implements Initializable {
    public ComboBox<String> chartTypeComboBox;
    public LineChart<Number, Number> priceChart;
    public NumberAxis xAxis;
    public NumberAxis yAxis;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button marketControlButton;
    @FXML
    private Text totalPriceText;

    @FXML
    private ListView<Asset> assetListView;

    private final ZoneId localZoneId = ZoneId.systemDefault();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assetListView.setCellFactory(listView -> new AssetListCell());
        assetListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldAsset, newAsset) ->
                chartTypeComboBox.getSelectionModel().select(newAsset.getName()));

        var response = ServerClient.getInstance().sendRequest(new Request("user/getInfo"));
        ResponseFailureUtils.handle(response);
        var userInfo = (UserInfo)response.getPayload();
        usernameLabel.setText(userInfo.getUserName());

        if (userInfo.getRoles().contains("Admin")) {
            marketControlButton.setVisible(true);
        }

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
    }

    public void onLogoutButtonPressed() {
        ResponseFailureUtils.handle(ServerClient.getInstance().sendRequest(new Request("user/logout")));
        StageLoader.load(usernameLabel.getScene(), StagePath.LOGIN);
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

        // Преобразуем и фильтруем
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

        // Добавляем точку справа, если нет данных вплоть до nowEpoch
        Map.Entry<Long, BigDecimal> last = filtered.get(filtered.size() - 1);
        if (last.getKey() < nowEpoch) {
            series.getData().add(new XYChart.Data<>(nowEpoch, last.getValue()));
        }

        priceChart.getData().clear();
        priceChart.getData().add(series);

        BigDecimal finalMinPrice = minPrice;
        BigDecimal finalMaxPrice = maxPrice;
        Platform.runLater(() -> {
            // Ось X
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(weekAgoEpoch);
            xAxis.setUpperBound(nowEpoch);
            xAxis.setTickUnit((nowEpoch - weekAgoEpoch) / 10.0);

            // Ось Y
            yAxis.setAutoRanging(false);
            double minY = Math.max(finalMinPrice.doubleValue(), 0.0);
            double maxY = finalMaxPrice.doubleValue();

            double padding = (maxY - minY) * 0.1; // 10% от диапазона
            if (padding == 0) padding = maxY * 0.05; // если значения одинаковы

            yAxis.setLowerBound(minY - padding);
            yAxis.setUpperBound(maxY + padding);
            yAxis.setTickUnit((maxY + padding - (minY - padding)) / 10.0);
        });
    }

}
