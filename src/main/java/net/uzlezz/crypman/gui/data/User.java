package net.uzlezz.crypman.gui.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class User {
    private final StringProperty name;
    private final StringProperty balance;
    private final StringProperty roles;
    private final StringProperty status;

    public User(net.uzlezz.crypman.network.schemas.response.User user) {
        this.name = new SimpleStringProperty(user.getUserName());
        this.balance = new SimpleStringProperty(user.getBalance());
        this.roles = new SimpleStringProperty(user.getRoles());
        this.status = new SimpleStringProperty(user.getStatus());
    }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getBalance() { return balance.get(); }
    public StringProperty balanceProperty() { return balance; }

    public String getRoles() { return roles.get(); }
    public StringProperty rolesProperty() { return roles; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
