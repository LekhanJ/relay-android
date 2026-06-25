package com.relay.android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.appcompat.app.AppCompatActivity;

import com.relay.android.network.RelayClient;

public class MainActivity extends AppCompatActivity {
    private float lastX;
    private float lastY;
    private float startCentroidX;
    private float startCentroidY;
    private float lastCentroidX;
    private float lastCentroidY;
    private boolean twoFingerTap = false;
    private boolean moved = false;
    private int touchSlop;
    private RelayClient relayClient;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

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
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1) {
                        float currX = event.getX();
                        float currY = event.getY();
                        float moveDX = currX - lastX;
                        float moveDY = currY - lastY;
                        lastX = currX;
                        lastY = currY;
                        relayClient.move((int) moveDX, (int) moveDY);

                    } else if (event.getPointerCount() == 2) {
                        float centroidX = (event.getX(0) + event.getX(1)) / 2f;
                        float centroidY = (event.getY(0) + event.getY(1)) / 2f;
                        float totalDX = centroidX - startCentroidX;
                        float totalDY = centroidY - startCentroidY;
                        if (Math.abs(totalDX) > touchSlop || Math.abs(totalDY) > touchSlop) {
                            moved = true;
                        }
                        if (moved) {
                            float scrollDY = centroidY - lastCentroidY;
                            relayClient.scroll((int) scrollDY);
                        }
                        lastCentroidX = centroidX;
                        lastCentroidY = centroidY;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    if (event.getPointerCount() == 2) {
                        twoFingerTap = true;
                        moved = false;
                        startCentroidX = (event.getX(0) + event.getX(1)) / 2f;
                        startCentroidY = (event.getY(0) + event.getY(1)) / 2f;
                        lastCentroidX = startCentroidX;
                        lastCentroidY = startCentroidY;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getPointerCount() == 2 && twoFingerTap && !moved) {
                        relayClient.rightClick();
                    }
                    twoFingerTap = false;
                    moved = false;
                    break;

                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        });
    }
}