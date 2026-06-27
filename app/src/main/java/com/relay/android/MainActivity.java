package com.relay.android;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.relay.android.network.RelayClient;
import com.relay.android.protocol.MouseButton;

public class MainActivity extends AppCompatActivity {
    private float lastX;
    private float lastY;
    private float startCentroidX;
    private float startCentroidY;
    private float lastCentroidX;
    private float lastCentroidY;
    private float moveAccumulatorX = 0f;
    private float moveAccumulatorY = 0f;
    private float zoomRecognition = 0f;
    private float scrollAccumulator = 0f;
    private boolean twoFingerTap = false;
    private int touchSlop;
    private RelayClient relayClient;

    private enum GestureMode {
        NONE,
        SCROLL,
        ZOOM
    }
    private GestureMode gestureMode = GestureMode.NONE;

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
                        relayClient.click(MouseButton.LEFT);
                        return true;
                    }
                }
        );

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(
                this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                        return true;
                    }
                    @Override
                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
                        float delta = detector.getScaleFactor() - 1f;
                        if (gestureMode == GestureMode.NONE) {
                            zoomRecognition += delta;
                            if (Math.abs(zoomRecognition) >= 0.03f) {
                                gestureMode = GestureMode.ZOOM;
                            }
                        }
                        if (gestureMode == GestureMode.ZOOM) {
                            relayClient.zoom(delta);
                        }
                        return true;
                    }
                    @Override
                    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {

                    }
                }
        );

        View trackpad = findViewById(R.id.main);
        trackpad.setOnTouchListener((view, event) -> {
            gestureDetector.onTouchEvent(event);
            scaleGestureDetector.onTouchEvent(event);
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
                        // Accumulate fractional movement.
                        moveAccumulatorX += moveDX;
                        moveAccumulatorY += moveDY;
                        // Extract whole pixels.
                        int sendDX = (int) moveAccumulatorX;
                        int sendDY = (int) moveAccumulatorY;
                        if (sendDX != 0 || sendDY != 0) {
                            relayClient.move(sendDX, sendDY);
                            // Keep only the fractional remainder.
                            moveAccumulatorX -= sendDX;
                            moveAccumulatorY -= sendDY;
                        }

                    } else if (event.getPointerCount() == 2) {
                        float centroidX = (event.getX(0) + event.getX(1)) / 2f;
                        float centroidY = (event.getY(0) + event.getY(1)) / 2f;
                        float totalDX = centroidX - startCentroidX;
                        float totalDY = centroidY - startCentroidY;
                        if (gestureMode == GestureMode.NONE && (Math.abs(totalDX) > touchSlop || Math.abs(totalDY) > touchSlop)) {
                            if (!scaleGestureDetector.isInProgress()) {
                                gestureMode = GestureMode.SCROLL;
                            }
                        }

                        if (gestureMode == GestureMode.SCROLL) {
                            float scrollDY = centroidY - lastCentroidY;
                            scrollAccumulator += scrollDY;
                            int amount = (int) scrollAccumulator;
                            if (amount != 0) {
                                relayClient.scroll(amount);
                                scrollAccumulator -= amount;
                            }
                        }
                        lastCentroidX = centroidX;
                        lastCentroidY = centroidY;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    if (event.getPointerCount() == 2) {
                        twoFingerTap = true;
                        zoomRecognition = 0f;
                        gestureMode = GestureMode.NONE;
                        startCentroidX = (event.getX(0) + event.getX(1)) / 2f;
                        startCentroidY = (event.getY(0) + event.getY(1)) / 2f;
                        lastCentroidX = startCentroidX;
                        lastCentroidY = startCentroidY;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getPointerCount() == 2 && twoFingerTap && gestureMode == GestureMode.NONE) {
                        relayClient.click(MouseButton.RIGHT);
                    }
                    twoFingerTap = false;
                    zoomRecognition = 0f;
                    gestureMode = GestureMode.NONE;
                    scrollAccumulator = 0f;
                    break;

                case MotionEvent.ACTION_UP:
                    moveAccumulatorX = 0f;
                    moveAccumulatorY = 0f;
                    break;
            }
            return true;
        });
    }
}