package net.uzlezz.crypman.gui.enums;

import lombok.Getter;

@Getter
public enum StagePath {
    LOGIN("/views/login.fxml"),
    REGISTER("/views/register.fxml"),
    PORTFOLIO("/views/portfolio.fxml"),
    MARKET_CONTROL("/views/market_control.fxml"),

    ADD_MONEY_DIALOG("/views/dialogs/add_money.fxml");

    private final String path;

    StagePath(String path) {
        this.path = path;
    }
}
