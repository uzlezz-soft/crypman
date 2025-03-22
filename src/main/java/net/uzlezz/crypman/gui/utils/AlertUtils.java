package net.uzlezz.crypman.gui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertUtils {
    private String header;
    private String message;
    private Alert.AlertType type;

    public void show() {
        complete().showAndWait();
    }

    public ButtonType showWithConfirmation() {
        return complete().showAndWait().orElse(ButtonType.CANCEL);
    }

    public Alert complete() {
        var alert = new Alert(type);
        alert.setTitle("Crypman");
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }

    public static void showError(String header, String content) {
        builder()
                .type(Alert.AlertType.ERROR)
                .header(header)
                .message(content)
                .build()
                .show();
    }

    public static void showWarning(String header, String content) {
        builder()
                .type(Alert.AlertType.WARNING)
                .header(header)
                .message(content)
                .build()
                .show();
    }

    public static void showInformation(String header, String content) {
        builder()
                .type(Alert.AlertType.INFORMATION)
                .header(header)
                .message(content)
                .build()
                .show();
    }

    public static ButtonType showConfirmation(String header, String content) {
        return builder()
                .type(Alert.AlertType.CONFIRMATION)
                .header(header)
                .message(content)
                .build()
                .showWithConfirmation();
    }
}
