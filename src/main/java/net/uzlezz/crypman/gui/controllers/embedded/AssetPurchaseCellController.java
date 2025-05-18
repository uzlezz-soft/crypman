package net.uzlezz.crypman.gui.controllers.embedded;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.Getter;

@Getter
public class AssetPurchaseCellController {
    @FXML
    private Label riskLabel;
    @FXML
    private Label returnsLabel;
    @FXML
    private TextField amountField;
    @FXML
    private ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label priceLabel;
}
