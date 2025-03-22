module net.uzlezz.crypman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.desktop;
    requires static lombok;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens net.uzlezz.crypman to javafx.fxml;
    exports net.uzlezz.crypman;
    exports net.uzlezz.crypman.gui.controllers;
    opens net.uzlezz.crypman.gui.controllers to javafx.fxml;
}