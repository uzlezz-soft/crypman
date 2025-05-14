package net.uzlezz.crypman.gui.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import net.uzlezz.crypman.MarketState;
import net.uzlezz.crypman.gui.controllers.embedded.AssetCellController;
import net.uzlezz.crypman.gui.controllers.embedded.PortfolioHistoryCellController;
import net.uzlezz.crypman.gui.data.Cryptocurrency;
import net.uzlezz.crypman.network.schemas.response.Asset;
import net.uzlezz.crypman.network.schemas.response.PortfolioActionRecord;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Objects;

public class PortfolioHistoryListCell extends ListCell<PortfolioActionRecord> {

    private FXMLLoader loader;
    private PortfolioHistoryCellController controller;

    @Override
    protected void updateItem(PortfolioActionRecord record, boolean empty) {
        super.updateItem(record, empty);

        if (empty || record == null) {
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/views/embedded/portfolio_history_cell.fxml"));
                try {
                    Parent root = loader.load();
                    controller = loader.getController();
                    setGraphic(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Cryptocurrency crypto = null;
            if (!Objects.equals(record.getCryptocurrencyName(), ""))
                crypto = MarketState.getCryptocurrency(record.getCryptocurrencyName());

            controller.getNameLabel().setText(record.getAction());
            controller.getBalanceChangeLabel().setText(String.format("$%.2f", record.getBalanceDelta()));
            if (crypto != null)
                controller.getAmountLabel().setText(String.format("%.9f %s", record.getCryptocurrencyDelta(), record.getCryptocurrencyName()));
            else
                controller.getAmountLabel().setText("");


            Image icon = loadIcon(crypto != null ? "crypto/" + crypto.getIcon() : "money.png");
            controller.getIconView().setImage(icon);
        }
    }

    private Image loadIcon(String assetName) {
        String path = "/images/" + assetName.toLowerCase();
        InputStream stream = getClass().getResourceAsStream(path);
        return stream != null ? new Image(stream) : new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/crypto/default.png")));
    }
}