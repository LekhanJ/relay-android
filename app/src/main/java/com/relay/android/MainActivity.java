package com.relay.android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private float lastX;
    private float lastY;

    private WebSocketClient webSocketClient;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webSocketClient = new WebSocketClient();
        webSocketClient.connect("ws://192.168.0.115:8080/ws");

        View trackpad = findViewById(R.id.main);
        trackpad.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float currX = event.getX();
                    float currY = event.getY();

                    float dx = currX - lastX;
                    float dy = currY - lastY;

                    lastX = currX;
                    lastY = currY;

                    String message = "{\"type\":\"move\",\"dx\":"
                                    + dx +
                                    ",\"dy\":"
                                    + dy +
                                    "}";

                    webSocketClient.send(message);

                    break;
            }
            return true;
        });
    }
}















