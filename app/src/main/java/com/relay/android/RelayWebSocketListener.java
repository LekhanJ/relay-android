package com.relay.android;

import androidx.annotation.NonNull;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RelayWebSocketListener extends WebSocketListener {

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        System.out.println("Connected");
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        System.out.println("Received: " + text);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        System.out.println("Closing");
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, Throwable t, Response response) {
        System.out.println("Failed: " + t.getMessage());
    }
}
