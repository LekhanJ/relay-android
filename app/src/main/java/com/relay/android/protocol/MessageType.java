package com.relay.android.protocol;

public enum MessageType {
    MOUSE_MOVE("mouse_move"),
    MOUSE_CLICK("mouse_click"),
    SCROLL("scroll"),
    ZOOM("zoom"),
    KEY_DOWN("key_down"),
    KEY_UP("key_up");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}