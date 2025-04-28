package net.uzlezz.crypman.gui.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.math.BigDecimal;

public class Cryptocurrency {
    private final StringProperty name;
    private final StringProperty friendlyName;
    private final StringProperty icon;
    private final StringProperty priceProperty;
    @Getter
    private final BigDecimal price;

    public Cryptocurrency(net.uzlezz.crypman.network.schemas.response.Cryptocurrency crypto) {
        this.name = new SimpleStringProperty(crypto.getName());
        this.friendlyName = new SimpleStringProperty(crypto.getFriendlyName());
        this.icon = new SimpleStringProperty(crypto.getIcon());

        this.price = crypto.getPrice();
        this.priceProperty = new SimpleStringProperty(price.toString());
    }

    public String getName() { return name.get(); }
    public StringProperty name() { return name; }

    public String getFriendlyName() { return friendlyName.get(); }
    public StringProperty friendlyName() { return friendlyName; }

    public String getIcon() { return icon.get(); }
    public StringProperty icon() { return icon; }

    public StringProperty price() { return priceProperty; }
}
