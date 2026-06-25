package com.relay.android.network;

import com.google.gson.Gson;
import com.relay.android.protocol.Message;
import com.relay.android.protocol.MessageType;

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
        relayWebSocketClient.send(gson.toJson(message));
    }

    public void leftClick() {
        Message message = new Message();
        message.type = MessageType.LEFT_CLICK.getValue();
        relayWebSocketClient.send(gson.toJson(message));
    }

    public void disconnect() {
        relayWebSocketClient.disconnect();
    }
}
