package net.uzlezz.crypman.gui.enums;

import lombok.Getter;

@Getter
public enum StagePath {
    LOGIN("/views/login.fxml"),
    REGISTER("/views/register.fxml"),
    PORTFOLIO("/views/portfolio.fxml");

    private final String path;

    StagePath(String path) {
        this.path = path;
    }
}
