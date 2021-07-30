package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.view.MotionEvent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidInput;

public class AndroidSingleTouchHandler implements AndroidTouchHandler {
    public void onTouch(MotionEvent event, AndroidInput input) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int oldX = input.touchX[0];
        int oldY = input.touchY[0];
        input.touchX[0] = x;
        input.touchY[0] = y;
        long timeStamp = event.getEventTime() * 1000000;
        if (event.getAction() == 0) {
            postTouchEvent(input, 0, x, y, 0, timeStamp);
            input.touched[0] = true;
            input.deltaX[0] = 0;
            input.deltaY[0] = 0;
        } else if (event.getAction() == 2) {
            postTouchEvent(input, 2, x, y, 0, timeStamp);
            input.touched[0] = true;
            input.deltaX[0] = x - oldX;
            input.deltaY[0] = y - oldY;
        } else if (event.getAction() == 1) {
            postTouchEvent(input, 1, x, y, 0, timeStamp);
            input.touched[0] = false;
            input.deltaX[0] = 0;
            input.deltaY[0] = 0;
        } else if (event.getAction() == 3) {
            postTouchEvent(input, 1, x, y, 0, timeStamp);
            input.touched[0] = false;
            input.deltaX[0] = 0;
            input.deltaY[0] = 0;
        }
    }

    private void postTouchEvent(AndroidInput input, int type, int x, int y, int pointer, long timeStamp) {
        synchronized (input) {
            AndroidInput.TouchEvent event = input.usedTouchEvents.obtain();
            event.timeStamp = timeStamp;
            event.pointer = 0;
            event.f64x = x;
            event.f65y = y;
            event.type = type;
            input.touchEvents.add(event);
        }
        Gdx.app.getGraphics().requestRendering();
    }

    public boolean supportsMultitouch(Context activity) {
        return false;
    }
}
