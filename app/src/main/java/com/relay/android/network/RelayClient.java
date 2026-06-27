package com.relay.android.network;

import com.google.gson.Gson;
import com.relay.android.protocol.Message;
import com.relay.android.protocol.MessageType;
import com.relay.android.protocol.MouseButton;

public class RelayClient {

    private final RelayWebSocketClient relayWebSocketClient;
    private final Gson gson;

    public RelayClient(String serverUrl) {
        gson = new Gson();
        relayWebSocketClient = new RelayWebSocketClient();
        relayWebSocketClient.connect(serverUrl);
    }

    public void move(int dx, int dy) {
        Message message = new Message();
        message.type = MessageType.MOUSE_MOVE.getValue();
        message.dx = dx;
        message.dy = dy;
        send(message);
    }

    public void click(MouseButton button) {
        Message message = new Message();
        message.type = MessageType.MOUSE_CLICK.getValue();
        message.button = button.getValue();
        send(message);
    }

    public void scroll(int amount) {
        Message message = new Message();
        message.type = MessageType.SCROLL.getValue();
        message.amount = amount;
        send(message);
    }

    public void zoom(float delta) {
        Message message = new Message();
        message.type = MessageType.ZOOM.getValue();
        message.delta = delta;
        send(message);
    }

    public void keyDown(String key) {
        Message message = new Message();
        message.type = MessageType.KEY_DOWN.getValue();
        message.key = key;
        send(message);
    }

    public void keyUp(String key) {
        Message message = new Message();
        message.type = MessageType.KEY_UP.getValue();
        message.key = key;
        send(message);
    }

    private void send(Message message) {
        relayWebSocketClient.send(gson.toJson(message));
    }

    public void disconnect() {
        relayWebSocketClient.disconnect();
    }
}