package com.relay.android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.relay.android.network.RelayClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private float lastX;
    private float lastY;
    private RelayClient relayClient;
    private final Gson gson = new Gson();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relayClient = new RelayClient("ws://192.168.0.115:8080/ws");

        GestureDetector gestureDetector = new GestureDetector(
                this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        relayClient.leftClick();
                        return true;
                    }
                }
        );

        View trackpad = findViewById(R.id.main);
        trackpad.setOnTouchListener((view, event) -> {

            gestureDetector.onTouchEvent(event);

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

                    relayClient.move((int)dx, (int)dy);

                    break;
            }
            return true;
        });
    }
}















