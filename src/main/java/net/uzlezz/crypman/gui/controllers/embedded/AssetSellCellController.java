package net.uzlezz.crypman.gui.controllers.embedded;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.Getter;

@Getter
public class AssetSellCellController {
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label returnsLabel;
    @FXML
    private Slider amountSlider;
    @FXML
    private TextField amountField;
    @FXML
    private ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label priceLabel;
}
