package net.uzlezz.crypman.gui.utils;

import javafx.event.Event;
import javafx.event.EventType;

public class CustomEvents extends Event {
    public static final EventType<CustomEvents> ASSET_AMOUNT_CHANGED = new EventType<>(ANY, "_ASSET_AMOUNT_CHANGED");

    public CustomEvents(EventType<? extends Event> eventType){
        super(eventType);
    }
}
