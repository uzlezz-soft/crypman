package net.uzlezz.crypman.gui.controllers.embedded;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;

@Getter
public class AssetCellController {
    @FXML
    private ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label secondaryPriceLabel;
}
