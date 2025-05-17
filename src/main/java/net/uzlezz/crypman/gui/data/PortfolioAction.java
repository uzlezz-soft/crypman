package net.uzlezz.crypman.gui.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.uzlezz.crypman.network.schemas.response.PortfolioActionRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PortfolioAction {
    private final ObjectProperty<LocalDateTime> date;
    private final StringProperty action;
    private final StringProperty balanceDelta;
    private final StringProperty cryptocurrencyName;
    private final StringProperty cryptocurrencyDelta;

    public PortfolioAction(PortfolioActionRecord action) {
        date = new SimpleObjectProperty<>(action.getDateTime());
        this.action = new SimpleStringProperty(action.getAction());
        balanceDelta = new SimpleStringProperty(String.format("$%.4f", action.getBalanceDelta()));
        cryptocurrencyName = new SimpleStringProperty(action.getCryptocurrencyName() == null ? "-" : action.getCryptocurrencyName());
        cryptocurrencyDelta = new SimpleStringProperty(action.getCryptocurrencyName() == null ? "-"
                : String.format("%s%.9f", action.getCryptocurrencyDelta().compareTo(BigDecimal.ZERO) > 0 ? "+" : "",
                action.getCryptocurrencyDelta()));
    }

    public LocalDateTime getDate() { return date.get(); }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }

    public String getAction() { return action.get(); }
    public StringProperty actionProperty() { return action; }

    public String getBalanceDelta() { return balanceDelta.get(); }
    public StringProperty balanceDeltaProperty() { return balanceDelta; }

    public String getCryptocurrencyName() { return cryptocurrencyName.get(); }
    public StringProperty cryptocurrencyNameProperty() { return cryptocurrencyName; }

    public String getCryptocurrencyDelta() { return cryptocurrencyDelta.get(); }
    public StringProperty cryptocurrencyDeltaProperty() { return cryptocurrencyDelta; }
}
