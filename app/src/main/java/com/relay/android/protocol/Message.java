package com.relay.android.protocol;

public class Message {
    public String type;

    // Mouse movement
    public Integer dx;
    public Integer dy;

    // Mouse click
    public String button;

    // Keyboard
    public String key;

    // Scroll
    public Integer amount;

    // Zoom
    public Float delta;
}