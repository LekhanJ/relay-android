package com.relay.android.protocol;

public enum MouseButton {
    LEFT("left"),
    RIGHT("right"),
    MIDDLE("middle");

    private final String value;

    MouseButton(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}