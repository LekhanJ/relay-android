package com.relay.android.protocol;

public enum MessageType {
    MOUSE_MOVE("mouse_move"),
    LEFT_CLICK("left_click"),
    RIGHT_CLICK("right_click"),
    SCROLL("scroll"),
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
