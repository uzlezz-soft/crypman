package net.uzlezz.crypman.gui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import net.uzlezz.crypman.gui.utils.AlertUtils;

import java.math.BigDecimal;

@Getter
public class AddMoneyDialogController {
    @FXML
    private TextField moneyAmountField;

    private boolean isCancelled = true;
    private BigDecimal moneyAmount;

    public void onAddMoneyButtonPressed() {
        try {
            moneyAmount = new BigDecimal(moneyAmountField.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showError("Ошибка", "Некорректное значение");
            return;
        }
        if (moneyAmount.compareTo(BigDecimal.ZERO) <= 0) {
            AlertUtils.showError("Ошиюка", "Введите значение больше 0");
            return;
        }

        isCancelled = false;
        ((Stage)moneyAmountField.getScene().getWindow()).close();
    }
}
