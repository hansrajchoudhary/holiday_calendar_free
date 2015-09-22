package com.apps.calendar;

import android.view.MotionEvent;
import android.view.View;

public class SwipeViewDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to right
        RL, // Right to left
        TB, // Top to bottom
        BT, // Bottom to top
        None // Action not found
    }

    private static final int HORIZONTAL_MIN_DISTANCE = 30; // The minimum
    // distance for
    // horizontal swipe
    private static final int VERTICAL_MIN_DISTANCE = 80; // The minimum distance
    // for vertical
    // swipe
    private float downX, downY, upX, upY; // Coordinates
    private Action mSwipeDetected = Action.None; // Last action

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    /**
     * Swipe detection
     */@Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
        case MotionEvent.ACTION_MOVE:
            {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {

                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {

                        mSwipeDetected = Action.RL;
                        return true;
                    }
                } else

                // vertical swipe detection
                if (Math.abs(deltaY) > VERTICAL_MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {

                        mSwipeDetected = Action.TB;
                        return false;
                    }
                    if (deltaY > 0) {

                        mSwipeDetected = Action.BT;
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

}