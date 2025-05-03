package net.uzlezz.crypman.gui.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.controllers.embedded.AssetCellController;
import net.uzlezz.crypman.network.schemas.response.Asset;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Objects;

public class AssetListCell extends ListCell<Asset> {

    private FXMLLoader loader;
    private AssetCellController controller;

    @Override
    protected void updateItem(Asset asset, boolean empty) {
        super.updateItem(asset, empty);

        if (empty || asset == null) {
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/views/embedded/asset_cell.fxml"));
                try {
                    Parent root = loader.load();
                    controller = loader.getController();
                    setGraphic(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            var crypto = MarketState.getCryptocurrency(asset.getName());

            controller.getNameLabel().setText(crypto.getFriendlyName());
            controller.getAmountLabel().setText(String.format("%.9f %s", asset.getAmount(), crypto.getName()));
            controller.getPriceLabel().setText("$" + String.format("%.2f", crypto.getPrice().multiply(asset.getAmount())));
            controller.getSecondaryPriceLabel().setText("$" + String.format(crypto.getPrice().compareTo(new BigDecimal("0.001")) < 0 ?
                    "%.4f" : crypto.getPrice().compareTo(new BigDecimal("0.01")) < 0 ? "%.3f" : "%.2f", crypto.getPrice()));

            Image icon = loadIcon(crypto.getIcon());
            controller.getIconView().setImage(icon);
        }
    }

    private Image loadIcon(String assetName) {
        String path = "/images/crypto/" + assetName.toLowerCase();
        InputStream stream = getClass().getResourceAsStream(path);
        return stream != null ? new Image(stream) : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crypto/default.png")));
    }
}