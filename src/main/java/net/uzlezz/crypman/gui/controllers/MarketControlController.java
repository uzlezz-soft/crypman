package net.uzlezz.crypman.gui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.enums.StagePath;
import net.uzlezz.crypman.gui.utils.AlertUtils;
import net.uzlezz.crypman.gui.utils.CSVReader;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.gui.utils.StageLoader;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.CryptocurrencyRegistration;
import net.uzlezz.crypman.network.schemas.request.NewPriceRecord;
import net.uzlezz.crypman.gui.data.Cryptocurrency;
import net.uzlezz.crypman.network.schemas.request.NewPriceRecordList;
import net.uzlezz.crypman.network.schemas.response.PriceRecord;
import net.uzlezz.crypman.network.schemas.response.UserInfo;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MarketControlController implements Initializable {
    @FXML
    private LineChart priceChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker priceDatePicker;
    @FXML
    private TextField priceTimeField;
    @FXML
    private TextField addNameField;
    @FXML
    private TextField addFriendlyNameField;
    @FXML
    private TextField addIconField;
    @FXML
    private TextField editNameField;
    @FXML
    private TextField editFriendlyNameField;
    @FXML
    private TextField editIconField;
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

    private final ZoneId localZoneId = ZoneId.systemDefault();
    private FileChooser csvFileChooser;

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

        csvFileChooser = new FileChooser();
        csvFileChooser.setTitle("Выбор файла с ценами");
        csvFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        friendlyNameColumn.setCellValueFactory(new PropertyValueFactory<>("friendlyName"));
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
        cryptoTableView.setItems(cryptocurrencies);

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

        cryptoTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldCrypto, newCrypto) ->
                populateChart(newCrypto.getName()));
    }

    private void refreshCryptoList() {
        var response = ServerClient.getInstance().sendRequest(new Request("market/actives"));
        ResponseFailureUtils.handle(response);
        MarketState.setCryptocurrencyList((List<net.uzlezz.crypman.network.schemas.response.Cryptocurrency>) response.getPayload());

        cryptocurrencies.clear();
        cryptocurrencies.addAll(MarketState.getCryptocurrencies());
    }

    private void populateChart() {
        var crypto = cryptoTableView.getSelectionModel().getSelectedItem();
        populateChart(crypto != null ? crypto.getName() : null);
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

    public void onRefreshButtonPressed() {
        refreshCryptoList();
        populateChart();
    }

    public void onBackButtonPressed() {
        StageLoader.load(usernameLabel.getScene(), StagePath.PORTFOLIO);
    }

    // TODO: load images from server
    private Image loadIcon(String assetName) {
        String path = "/images/crypto/" + assetName.toLowerCase() + ".png";
        InputStream stream = getClass().getResourceAsStream(path);
        return stream != null ? new Image(stream) : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crypto/default.png")));
    }

    public void onAddButtonPressed() {
        if (addNameField.getText().isEmpty()) {
            AlertUtils.showError("Ошибка", "Поле 'Идентификатор' должно быть заполнено");
            return;
        }
        if (addFriendlyNameField.getText().isEmpty()) {
            AlertUtils.showError("Ошибка", "Поле 'Имя' должно быть заполнено");
            return;
        }
        if (addIconField.getText().isEmpty()) {
            AlertUtils.showError("Ошибка", "Поле 'Иконка' должно быть заполнено");
            return;
        }

        var payload = new CryptocurrencyRegistration(addNameField.getText(),
                addFriendlyNameField.getText(), addIconField.getText());

        if (AlertUtils.showConfirmation("Подтвердите действие",
                String.format("Вы действительно хотите зарегистрировать криптовалюту %s (%s) с иконкой %s?",
                        payload.getFriendlyName(), payload.getName(), payload.getIcon())) == ButtonType.OK) {
            var response = ServerClient.getInstance().sendRequest(new Request("market/register", payload));
            ResponseFailureUtils.handle(response);

            refreshCryptoList();
        }
    }

    public void onEditButtonPressed() {
    }

    public void onRegisterPriceButtonPressed() {
        var crypto = cryptoTableView.getSelectionModel().getSelectedItem();
        if (crypto == null) {
            AlertUtils.showError("Ошибка", "Выберите криптовалюту из таблицы");
            return;
        }

        LocalDateTime dateTime;
        BigDecimal price;
        try {
            dateTime = LocalDateTime.of(priceDatePicker.getValue(), LocalTime.parse(priceTimeField.getText()));
        } catch (DateTimeParseException e) {
            AlertUtils.showError("Ошибка", "Некорректное время");
            return;
        }

        try {
            price = new BigDecimal(priceField.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showError("Ошибка", "Некорректная цена");
            return;
        }

        var payload = new NewPriceRecord(crypto.getName(), dateTime, price);
        var response = ServerClient.getInstance().sendRequest(new Request("market/recordPrice", payload));
        ResponseFailureUtils.handle(response);

        populateChart(payload.getCryptoName());
    }

    public void loadPriceCSVButtonPressed() {
        var crypto = cryptoTableView.getSelectionModel().getSelectedItem();
        if (crypto == null) {
            AlertUtils.showError("Ошибка", "Выберите криптовалюту из таблицы");
            return;
        }

        var file = csvFileChooser.showOpenDialog(usernameLabel.getScene().getWindow());
        if (file == null) return;

        List<NewPriceRecord> records;
        try {
            var data = CSVReader.read(file);
            records = data.stream()
                    .map(values -> new NewPriceRecord(
                            crypto.getName(),
                            LocalDateTime.parse(values.get(0), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            new BigDecimal(values.get(1))))
                    .toList();
        } catch (RuntimeException e) {
            AlertUtils.showError("Ошибка", "Произошла ошибка при прочтении файла");
            throw e;
        }
        if (records.isEmpty()) {
            AlertUtils.showInformation("Уведомомление", "В файле 0 записей");
            return;
        }

        if (AlertUtils.showConfirmation("Подтвердите действие",
                "Вы действительно хотите загрузить " + records.size() + " записей цен?") == ButtonType.OK) {
            var payload = new NewPriceRecordList(records);
            var response = ServerClient.getInstance().sendRequest(new Request("market/recordPrices", payload));
            ResponseFailureUtils.handle(response);

            populateChart(records.get(0).getCryptoName());
        }
    }
}
