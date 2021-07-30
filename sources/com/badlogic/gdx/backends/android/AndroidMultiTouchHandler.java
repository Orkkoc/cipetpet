package com.badlogic.gdx.backends.android;

import android.content.Context;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidInput;

public class AndroidMultiTouchHandler implements AndroidTouchHandler {
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTouch(android.view.MotionEvent r14, com.badlogic.gdx.backends.android.AndroidInput r15) {
        /*
            r13 = this;
            int r0 = r14.getAction()
            r8 = r0 & 255(0xff, float:3.57E-43)
            int r0 = r14.getAction()
            r1 = 65280(0xff00, float:9.1477E-41)
            r0 = r0 & r1
            int r12 = r0 >> 8
            int r11 = r14.getPointerId(r12)
            r3 = 0
            r4 = 0
            r5 = 0
            long r6 = java.lang.System.nanoTime()
            monitor-enter(r15)
            switch(r8) {
                case 0: goto L_0x002a;
                case 1: goto L_0x005d;
                case 2: goto L_0x0091;
                case 3: goto L_0x005d;
                case 4: goto L_0x005d;
                case 5: goto L_0x002a;
                case 6: goto L_0x005d;
                default: goto L_0x001f;
            }
        L_0x001f:
            monitor-exit(r15)     // Catch:{ all -> 0x005a }
            com.badlogic.gdx.Application r0 = com.badlogic.gdx.Gdx.app
            com.badlogic.gdx.Graphics r0 = r0.getGraphics()
            r0.requestRendering()
            return
        L_0x002a:
            int r5 = r15.getFreePointerIndex()     // Catch:{ all -> 0x005a }
            int[] r0 = r15.realId     // Catch:{ all -> 0x005a }
            r0[r5] = r11     // Catch:{ all -> 0x005a }
            float r0 = r14.getX(r12)     // Catch:{ all -> 0x005a }
            int r3 = (int) r0     // Catch:{ all -> 0x005a }
            float r0 = r14.getY(r12)     // Catch:{ all -> 0x005a }
            int r4 = (int) r0     // Catch:{ all -> 0x005a }
            r2 = 0
            r0 = r13
            r1 = r15
            r0.postTouchEvent(r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x005a }
            int[] r0 = r15.touchX     // Catch:{ all -> 0x005a }
            r0[r5] = r3     // Catch:{ all -> 0x005a }
            int[] r0 = r15.touchY     // Catch:{ all -> 0x005a }
            r0[r5] = r4     // Catch:{ all -> 0x005a }
            int[] r0 = r15.deltaX     // Catch:{ all -> 0x005a }
            r1 = 0
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            int[] r0 = r15.deltaY     // Catch:{ all -> 0x005a }
            r1 = 0
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            boolean[] r0 = r15.touched     // Catch:{ all -> 0x005a }
            r1 = 1
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            goto L_0x001f
        L_0x005a:
            r0 = move-exception
            monitor-exit(r15)     // Catch:{ all -> 0x005a }
            throw r0
        L_0x005d:
            int r5 = r15.lookUpPointerIndex(r11)     // Catch:{ all -> 0x005a }
            r0 = -1
            if (r5 == r0) goto L_0x001f
            int[] r0 = r15.realId     // Catch:{ all -> 0x005a }
            r1 = -1
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            float r0 = r14.getX(r12)     // Catch:{ all -> 0x005a }
            int r3 = (int) r0     // Catch:{ all -> 0x005a }
            float r0 = r14.getY(r12)     // Catch:{ all -> 0x005a }
            int r4 = (int) r0     // Catch:{ all -> 0x005a }
            r2 = 1
            r0 = r13
            r1 = r15
            r0.postTouchEvent(r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x005a }
            int[] r0 = r15.touchX     // Catch:{ all -> 0x005a }
            r0[r5] = r3     // Catch:{ all -> 0x005a }
            int[] r0 = r15.touchY     // Catch:{ all -> 0x005a }
            r0[r5] = r4     // Catch:{ all -> 0x005a }
            int[] r0 = r15.deltaX     // Catch:{ all -> 0x005a }
            r1 = 0
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            int[] r0 = r15.deltaY     // Catch:{ all -> 0x005a }
            r1 = 0
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            boolean[] r0 = r15.touched     // Catch:{ all -> 0x005a }
            r1 = 0
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            goto L_0x001f
        L_0x0091:
            int r10 = r14.getPointerCount()     // Catch:{ all -> 0x005a }
            r9 = 0
        L_0x0096:
            if (r9 >= r10) goto L_0x001f
            r12 = r9
            int r11 = r14.getPointerId(r12)     // Catch:{ all -> 0x005a }
            float r0 = r14.getX(r12)     // Catch:{ all -> 0x005a }
            int r3 = (int) r0     // Catch:{ all -> 0x005a }
            float r0 = r14.getY(r12)     // Catch:{ all -> 0x005a }
            int r4 = (int) r0     // Catch:{ all -> 0x005a }
            int r5 = r15.lookUpPointerIndex(r11)     // Catch:{ all -> 0x005a }
            r0 = -1
            if (r5 != r0) goto L_0x00b1
        L_0x00ae:
            int r9 = r9 + 1
            goto L_0x0096
        L_0x00b1:
            r2 = 2
            r0 = r13
            r1 = r15
            r0.postTouchEvent(r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x005a }
            int[] r0 = r15.deltaX     // Catch:{ all -> 0x005a }
            int[] r1 = r15.touchX     // Catch:{ all -> 0x005a }
            r1 = r1[r5]     // Catch:{ all -> 0x005a }
            int r1 = r3 - r1
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            int[] r0 = r15.deltaY     // Catch:{ all -> 0x005a }
            int[] r1 = r15.touchY     // Catch:{ all -> 0x005a }
            r1 = r1[r5]     // Catch:{ all -> 0x005a }
            int r1 = r4 - r1
            r0[r5] = r1     // Catch:{ all -> 0x005a }
            int[] r0 = r15.touchX     // Catch:{ all -> 0x005a }
            r0[r5] = r3     // Catch:{ all -> 0x005a }
            int[] r0 = r15.touchY     // Catch:{ all -> 0x005a }
            r0[r5] = r4     // Catch:{ all -> 0x005a }
            goto L_0x00ae
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidMultiTouchHandler.onTouch(android.view.MotionEvent, com.badlogic.gdx.backends.android.AndroidInput):void");
    }

    private void logAction(int action, int pointer) {
        String actionStr;
        if (action == 0) {
            actionStr = "DOWN";
        } else if (action == 5) {
            actionStr = "POINTER DOWN";
        } else if (action == 1) {
            actionStr = "UP";
        } else if (action == 6) {
            actionStr = "POINTER UP";
        } else if (action == 4) {
            actionStr = "OUTSIDE";
        } else if (action == 3) {
            actionStr = "CANCEL";
        } else if (action == 2) {
            actionStr = "MOVE";
        } else {
            actionStr = "UNKNOWN (" + action + ")";
        }
        Gdx.app.log("AndroidMultiTouchHandler", "action " + actionStr + ", Android pointer id: " + pointer);
    }

    private void postTouchEvent(AndroidInput input, int type, int x, int y, int pointer, long timeStamp) {
        AndroidInput.TouchEvent event = input.usedTouchEvents.obtain();
        event.timeStamp = timeStamp;
        event.pointer = pointer;
        event.f64x = x;
        event.f65y = y;
        event.type = type;
        input.touchEvents.add(event);
    }

    public boolean supportsMultitouch(Context activity) {
        return activity.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch");
    }
}
