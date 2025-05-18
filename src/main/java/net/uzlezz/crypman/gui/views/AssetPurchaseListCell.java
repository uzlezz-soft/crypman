package net.uzlezz.crypman.gui.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import lombok.Getter;
import net.uzlezz.crypman.gui.controllers.dialogs.AssetPurchaseDialogController;
import net.uzlezz.crypman.gui.controllers.embedded.AssetPurchaseCellController;
import net.uzlezz.crypman.gui.data.Cryptocurrency;
import net.uzlezz.crypman.gui.utils.CustomEvents;
import net.uzlezz.crypman.gui.utils.ResponseFailureUtils;
import net.uzlezz.crypman.network.Request;
import net.uzlezz.crypman.network.ServerClient;
import net.uzlezz.crypman.network.schemas.request.CryptocurrencyStats;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class AssetPurchaseListCell extends ListCell<Cryptocurrency> {

    private FXMLLoader loader;
    private AssetPurchaseCellController controller;

    @Getter
    private BigDecimal amount = BigDecimal.ZERO;

    private ListView<Cryptocurrency> owner;
    private AssetPurchaseDialogController owningController;

    private Cryptocurrency cryptocurrency;

    public AssetPurchaseListCell(ListView<Cryptocurrency> owner, AssetPurchaseDialogController controller) {
        super();
        this.owner = owner;
        owningController = controller;
    }

    @Override
    protected void updateItem(Cryptocurrency crypto, boolean empty) {
        super.updateItem(crypto, empty);

        if (empty || crypto == null) {
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/views/embedded/asset_buy_cell.fxml"));
                try {
                    Parent root = loader.load();
                    controller = loader.getController();
                    setGraphic(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cryptocurrency = crypto;
            owningController.getAmountMap().put(crypto, this);

            controller.getNameLabel().setText(crypto.getFriendlyName());
            controller.getAmountLabel().setText(String.format("%.9f %s", amount, crypto.getName()));
            controller.getPriceLabel().setText("$" + String.format(crypto.getPrice().compareTo(new BigDecimal("0.001")) < 0 ?
                    "%.4f" : crypto.getPrice().compareTo(new BigDecimal("0.01")) < 0 ? "%.3f" : "%.2f", crypto.getPrice()));
            controller.getAmountField().textProperty().addListener((ov, oldText, newText) -> {
                if (newText != null) {
                    try {
                        var newAmount = newText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(newText);
                        if (newAmount.compareTo(BigDecimal.ZERO) < 0) return;
                        amount = newAmount;
                    } catch (NumberFormatException ignored) {
                        amount = BigDecimal.ZERO;
                    }

                    var cryptoAmount = amount.divide(crypto.getPrice(), 15, RoundingMode.HALF_EVEN);
                    controller.getAmountLabel().setText(String.format("%.9f %s", cryptoAmount, crypto.getName()));
                    owner.fireEvent(new CustomEvents(CustomEvents.ASSET_AMOUNT_CHANGED));
                }
            });
            refreshRiskAndReturns(owningController.getDaysPeriod());

            Image icon = loadIcon(crypto.getIcon());
            controller.getIconView().setImage(icon);
        }
    }

    private Image loadIcon(String assetName) {
        String path = "/images/crypto/" + assetName.toLowerCase();
        InputStream stream = getClass().getResourceAsStream(path);
        return stream != null ? new Image(stream) : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crypto/default.png")));
    }

    public void setAmount(String amount) {
        controller.getAmountField().setText(amount);
    }

    public void refreshRiskAndReturns(int daysPeriod) {
        double risk = 0.0;
        double returns = 0.0;
        try {
            var response = ServerClient.getInstance().sendRequest(new Request("market/stats", new CryptocurrencyStats(cryptocurrency.getName(), daysPeriod)));
            ResponseFailureUtils.handle(response);

            var payload = (net.uzlezz.crypman.network.schemas.response.CryptocurrencyStats)response.getPayload();
            risk = payload.getRisk();
            returns = payload.getReturns();
        } catch (RuntimeException ignored) {}
        controller.getRiskLabel().setText(String.format("%.2f%%", risk * 100.0));
        controller.getReturnsLabel().setText(String.format("%.2f%%", returns * 100.0));
    }
}