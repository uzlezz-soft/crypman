package net.uzlezz.crypman.gui.controllers.embedded;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;

@Getter
public class PortfolioHistoryCellController {
    @FXML
    private ImageView iconView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label balanceChangeLabel;
    @FXML
    private Label cryptoChangeLabel;
}
