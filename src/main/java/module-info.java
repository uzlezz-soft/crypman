module net.uzlezz.crypman {
    requires javafx.controls;
    requires javafx.fxml;
    //requires javafx.web;
    requires java.sql;
    requires java.desktop;
    requires static lombok;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens net.uzlezz.crypman to javafx.fxml;
    exports net.uzlezz.crypman;
    exports net.uzlezz.crypman.gui.data;
    exports net.uzlezz.crypman.gui.controllers;
    exports net.uzlezz.crypman.gui.controllers.embedded;
    exports net.uzlezz.crypman.gui.controllers.dialogs;
    exports net.uzlezz.crypman.gui.enums;
    exports net.uzlezz.crypman.gui.utils;
    exports net.uzlezz.crypman.gui.views;
    exports net.uzlezz.crypman.network.schemas.request;
    exports net.uzlezz.crypman.network.schemas.response;
    exports net.uzlezz.crypman.network;
    opens net.uzlezz.crypman.gui.controllers to javafx.fxml;
    opens net.uzlezz.crypman.gui.controllers.embedded to javafx.fxml;
    opens net.uzlezz.crypman.gui.controllers.dialogs to javafx.fxml;
    opens net.uzlezz.crypman.gui.data to javafx.base;
}