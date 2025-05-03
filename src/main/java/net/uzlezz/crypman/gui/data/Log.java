package net.uzlezz.crypman.gui.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Log {
    private final ObjectProperty<LocalDateTime> date;
    private final StringProperty userName;
    private final StringProperty info;

    public Log(net.uzlezz.crypman.network.schemas.response.Log log) {
        this.date = new SimpleObjectProperty<>(log.getDateTime());
        this.userName = new SimpleStringProperty(log.getUserName());
        this.info = new SimpleStringProperty(log.getInfo());
    }

    public LocalDateTime getDate() { return date.get(); }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }

    public String getUserName() { return userName.get(); }
    public StringProperty userNameProperty() { return userName; }

    public String getInfo() { return info.get(); }
    public StringProperty infoProperty() { return info; }
}
