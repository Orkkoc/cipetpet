package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;

public class AndroidInput implements Input, View.OnKeyListener, View.OnTouchListener {

    /* renamed from: R */
    final float[] f63R = new float[9];
    public boolean accelerometerAvailable = false;
    private SensorEventListener accelerometerListener;
    private final float[] accelerometerValues = new float[3];
    final Application app;
    private float azimuth = 0.0f;
    private boolean catchBack = false;
    private boolean catchMenu = false;
    private boolean compassAvailable = false;
    private SensorEventListener compassListener;
    private final AndroidApplicationConfiguration config;
    final Context context;
    private long currentEventTimeStamp = System.nanoTime();
    int[] deltaX = new int[20];
    int[] deltaY = new int[20];
    private Handler handle;
    final boolean hasMultitouch;
    private float inclination = 0.0f;
    private boolean justTouched = false;
    ArrayList<KeyEvent> keyEvents = new ArrayList<>();
    ArrayList<View.OnKeyListener> keyListeners = new ArrayList<>();
    boolean keyboardAvailable;
    private IntMap<Object> keys = new IntMap<>();
    private final float[] magneticFieldValues = new float[3];
    private SensorManager manager;
    private final Input.Orientation nativeOrientation;
    private final AndroidOnscreenKeyboard onscreenKeyboard;
    final float[] orientation = new float[3];
    private float pitch = 0.0f;
    private InputProcessor processor;
    int[] realId = new int[10];
    boolean requestFocus = true;
    private float roll = 0.0f;
    private int sleepTime = 0;
    private String text = null;
    private Input.TextInputListener textListener = null;
    ArrayList<TouchEvent> touchEvents = new ArrayList<>();
    private final AndroidTouchHandler touchHandler;
    int[] touchX = new int[20];
    int[] touchY = new int[20];
    boolean[] touched = new boolean[20];
    Pool<KeyEvent> usedKeyEvents = new Pool<KeyEvent>(16, 1000) {
        /* access modifiers changed from: protected */
        public KeyEvent newObject() {
            return new KeyEvent();
        }
    };
    Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {
        /* access modifiers changed from: protected */
        public TouchEvent newObject() {
            return new TouchEvent();
        }
    };
    protected final Vibrator vibrator;

    static class KeyEvent {
        static final int KEY_DOWN = 0;
        static final int KEY_TYPED = 2;
        static final int KEY_UP = 1;
        char keyChar;
        int keyCode;
        long timeStamp;
        int type;

        KeyEvent() {
        }
    }

    static class TouchEvent {
        static final int TOUCH_DOWN = 0;
        static final int TOUCH_DRAGGED = 2;
        static final int TOUCH_UP = 1;
        int pointer;
        long timeStamp;
        int type;

        /* renamed from: x */
        int f64x;

        /* renamed from: y */
        int f65y;

        TouchEvent() {
        }
    }

    public AndroidInput(Application activity, Context context2, Object view, AndroidApplicationConfiguration config2) {
        if (view instanceof View) {
            View v = (View) view;
            v.setOnKeyListener(this);
            v.setOnTouchListener(this);
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.requestFocusFromTouch();
        }
        this.config = config2;
        this.onscreenKeyboard = new AndroidOnscreenKeyboard(context2, new Handler(), this);
        for (int i = 0; i < this.realId.length; i++) {
            this.realId[i] = -1;
        }
        this.handle = new Handler();
        this.app = activity;
        this.context = context2;
        this.sleepTime = config2.touchSleepTime;
        if (Integer.parseInt(Build.VERSION.SDK) >= 5) {
            this.touchHandler = new AndroidMultiTouchHandler();
        } else {
            this.touchHandler = new AndroidSingleTouchHandler();
        }
        this.hasMultitouch = this.touchHandler.supportsMultitouch(context2);
        this.vibrator = (Vibrator) context2.getSystemService("vibrator");
        int rotation = getRotation();
        Graphics.DisplayMode mode = this.app.getGraphics().getDesktopDisplayMode();
        if (((rotation == 0 || rotation == 180) && mode.width >= mode.height) || ((rotation == 90 || rotation == 270) && mode.width <= mode.height)) {
            this.nativeOrientation = Input.Orientation.Landscape;
        } else {
            this.nativeOrientation = Input.Orientation.Portrait;
        }
    }

    public float getAccelerometerX() {
        return this.accelerometerValues[0];
    }

    public float getAccelerometerY() {
        return this.accelerometerValues[1];
    }

    public float getAccelerometerZ() {
        return this.accelerometerValues[2];
    }

    public void getTextInput(final Input.TextInputListener listener, final String title, final String text2) {
        this.handle.post(new Runnable() {
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(AndroidInput.this.context);
                alert.setTitle(title);
                final EditText input = new EditText(AndroidInput.this.context);
                input.setText(text2);
                input.setSingleLine();
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                listener.input(input.getText().toString());
                            }
                        });
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                listener.canceled();
                            }
                        });
                    }
                });
                alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface arg0) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                listener.canceled();
                            }
                        });
                    }
                });
                alert.show();
            }
        });
    }

    public void getPlaceholderTextInput(final Input.TextInputListener listener, final String title, final String placeholder) {
        this.handle.post(new Runnable() {
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(AndroidInput.this.context);
                alert.setTitle(title);
                final EditText input = new EditText(AndroidInput.this.context);
                input.setHint(placeholder);
                input.setSingleLine();
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                listener.input(input.getText().toString());
                            }
                        });
                    }
                });
                alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface arg0) {
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                listener.canceled();
                            }
                        });
                    }
                });
                alert.show();
            }
        });
    }

    public int getX() {
        int i;
        synchronized (this) {
            i = this.touchX[0];
        }
        return i;
    }

    public int getY() {
        int i;
        synchronized (this) {
            i = this.touchY[0];
        }
        return i;
    }

    public int getX(int pointer) {
        int i;
        synchronized (this) {
            i = this.touchX[pointer];
        }
        return i;
    }

    public int getY(int pointer) {
        int i;
        synchronized (this) {
            i = this.touchY[pointer];
        }
        return i;
    }

    public boolean isTouched(int pointer) {
        boolean z;
        synchronized (this) {
            z = this.touched[pointer];
        }
        return z;
    }

    public boolean isKeyPressed(int key) {
        boolean containsKey;
        synchronized (this) {
            if (key == -1) {
                containsKey = this.keys.size > 0;
            } else {
                containsKey = this.keys.containsKey(key);
            }
        }
        return containsKey;
    }

    public boolean isTouched() {
        boolean z;
        synchronized (this) {
            z = this.touched[0];
        }
        return z;
    }

    public void setInputProcessor(InputProcessor processor2) {
        synchronized (this) {
            this.processor = processor2;
        }
    }

    /* access modifiers changed from: package-private */
    public void processEvents() {
        synchronized (this) {
            this.justTouched = false;
            if (this.processor != null) {
                InputProcessor processor2 = this.processor;
                int len = this.keyEvents.size();
                for (int i = 0; i < len; i++) {
                    KeyEvent e = this.keyEvents.get(i);
                    this.currentEventTimeStamp = e.timeStamp;
                    switch (e.type) {
                        case 0:
                            processor2.keyDown(e.keyCode);
                            break;
                        case 1:
                            processor2.keyUp(e.keyCode);
                            break;
                        case 2:
                            processor2.keyTyped(e.keyChar);
                            break;
                    }
                    this.usedKeyEvents.free(e);
                }
                int len2 = this.touchEvents.size();
                for (int i2 = 0; i2 < len2; i2++) {
                    TouchEvent e2 = this.touchEvents.get(i2);
                    this.currentEventTimeStamp = e2.timeStamp;
                    switch (e2.type) {
                        case 0:
                            processor2.touchDown(e2.f64x, e2.f65y, e2.pointer, 0);
                            this.justTouched = true;
                            break;
                        case 1:
                            processor2.touchUp(e2.f64x, e2.f65y, e2.pointer, 0);
                            break;
                        case 2:
                            processor2.touchDragged(e2.f64x, e2.f65y, e2.pointer);
                            break;
                    }
                    this.usedTouchEvents.free(e2);
                }
            } else {
                int len3 = this.touchEvents.size();
                for (int i3 = 0; i3 < len3; i3++) {
                    TouchEvent e3 = this.touchEvents.get(i3);
                    if (e3.type == 0) {
                        this.justTouched = true;
                    }
                    this.usedTouchEvents.free(e3);
                }
                int len4 = this.keyEvents.size();
                for (int i4 = 0; i4 < len4; i4++) {
                    this.usedKeyEvents.free(this.keyEvents.get(i4));
                }
            }
            if (this.touchEvents.size() == 0) {
                for (int i5 = 0; i5 < this.deltaX.length; i5++) {
                    this.deltaX[0] = 0;
                    this.deltaY[0] = 0;
                }
            }
            this.keyEvents.clear();
            this.touchEvents.clear();
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (this.requestFocus && view != null) {
            view.requestFocus();
            view.requestFocusFromTouch();
            this.requestFocus = false;
        }
        this.touchHandler.onTouch(event, this);
        if (this.sleepTime == 0) {
            return true;
        }
        try {
            Thread.sleep((long) this.sleepTime);
            return true;
        } catch (InterruptedException e) {
            return true;
        }
    }

    public void onTap(int x, int y) {
        postTap(x, y);
    }

    public void onDrop(int x, int y) {
        postTap(x, y);
    }

    /* access modifiers changed from: protected */
    public void postTap(int x, int y) {
        synchronized (this) {
            TouchEvent event = this.usedTouchEvents.obtain();
            event.timeStamp = System.nanoTime();
            event.pointer = 0;
            event.f64x = x;
            event.f65y = y;
            event.type = 0;
            this.touchEvents.add(event);
            TouchEvent event2 = this.usedTouchEvents.obtain();
            event2.timeStamp = System.nanoTime();
            event2.pointer = 0;
            event2.f64x = x;
            event2.f65y = y;
            event2.type = 1;
            this.touchEvents.add(event2);
        }
        Gdx.app.getGraphics().requestRendering();
    }

    public boolean onKey(View v, int keyCode, android.view.KeyEvent e) {
        int n = this.keyListeners.size();
        for (int i = 0; i < n; i++) {
            if (this.keyListeners.get(i).onKey(v, keyCode, e)) {
                return true;
            }
        }
        synchronized (this) {
            char character = (char) e.getUnicodeChar();
            if (keyCode == 67) {
                character = 8;
            }
            switch (e.getAction()) {
                case 0:
                    KeyEvent event = this.usedKeyEvents.obtain();
                    event.keyChar = 0;
                    event.keyCode = e.getKeyCode();
                    event.type = 0;
                    if (keyCode == 4 && e.isAltPressed()) {
                        keyCode = 255;
                        event.keyCode = 255;
                    }
                    this.keyEvents.add(event);
                    this.keys.put(event.keyCode, null);
                    break;
                case 1:
                    KeyEvent event2 = this.usedKeyEvents.obtain();
                    event2.keyChar = 0;
                    event2.keyCode = e.getKeyCode();
                    event2.type = 1;
                    if (keyCode == 4 && e.isAltPressed()) {
                        keyCode = 255;
                        event2.keyCode = 255;
                    }
                    this.keyEvents.add(event2);
                    KeyEvent event3 = this.usedKeyEvents.obtain();
                    event3.keyChar = character;
                    event3.keyCode = 0;
                    event3.type = 2;
                    this.keyEvents.add(event3);
                    if (keyCode != 255) {
                        this.keys.remove(e.getKeyCode());
                        break;
                    } else {
                        this.keys.remove(255);
                        break;
                    }
            }
            this.app.getGraphics().requestRendering();
        }
        if (keyCode == 255) {
            return true;
        }
        if (!this.catchBack || keyCode != 4) {
            return this.catchMenu && keyCode == 82;
        }
        return true;
    }

    public void setOnscreenKeyboardVisible(final boolean visible) {
        this.handle.post(new Runnable() {
            public void run() {
                InputMethodManager manager = (InputMethodManager) AndroidInput.this.context.getSystemService("input_method");
                if (visible) {
                    View view = ((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView();
                    view.setFocusable(true);
                    view.setFocusableInTouchMode(true);
                    manager.showSoftInput(((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView(), 0);
                    return;
                }
                manager.hideSoftInputFromWindow(((AndroidGraphics) AndroidInput.this.app.getGraphics()).getView().getWindowToken(), 0);
            }
        });
    }

    public void setCatchBackKey(boolean catchBack2) {
        this.catchBack = catchBack2;
    }

    public void setCatchMenuKey(boolean catchMenu2) {
        this.catchMenu = catchMenu2;
    }

    public void vibrate(int milliseconds) {
        this.vibrator.vibrate((long) milliseconds);
    }

    public void vibrate(long[] pattern, int repeat) {
        this.vibrator.vibrate(pattern, repeat);
    }

    public void cancelVibrate() {
        this.vibrator.cancel();
    }

    public boolean justTouched() {
        return this.justTouched;
    }

    public boolean isButtonPressed(int button) {
        if (button == 0) {
            return isTouched();
        }
        return false;
    }

    private void updateOrientation() {
        if (SensorManager.getRotationMatrix(this.f63R, (float[]) null, this.accelerometerValues, this.magneticFieldValues)) {
            SensorManager.getOrientation(this.f63R, this.orientation);
            this.azimuth = (float) Math.toDegrees((double) this.orientation[0]);
            this.pitch = (float) Math.toDegrees((double) this.orientation[1]);
            this.roll = (float) Math.toDegrees((double) this.orientation[2]);
        }
    }

    public void getRotationMatrix(float[] matrix) {
        SensorManager.getRotationMatrix(matrix, (float[]) null, this.accelerometerValues, this.magneticFieldValues);
    }

    public float getAzimuth() {
        if (!this.compassAvailable) {
            return 0.0f;
        }
        updateOrientation();
        return this.azimuth;
    }

    public float getPitch() {
        if (!this.compassAvailable) {
            return 0.0f;
        }
        updateOrientation();
        return this.pitch;
    }

    public float getRoll() {
        if (!this.compassAvailable) {
            return 0.0f;
        }
        updateOrientation();
        return this.roll;
    }

    /* access modifiers changed from: package-private */
    public void registerSensorListeners() {
        if (this.config.useAccelerometer) {
            this.manager = (SensorManager) this.context.getSystemService("sensor");
            if (this.manager.getSensorList(1).size() == 0) {
                this.accelerometerAvailable = false;
            } else {
                this.accelerometerListener = new SensorListener(this.nativeOrientation, this.accelerometerValues, this.magneticFieldValues);
                this.accelerometerAvailable = this.manager.registerListener(this.accelerometerListener, this.manager.getSensorList(1).get(0), 1);
            }
        } else {
            this.accelerometerAvailable = false;
        }
        if (this.config.useCompass) {
            if (this.manager == null) {
                this.manager = (SensorManager) this.context.getSystemService("sensor");
            }
            Sensor sensor = this.manager.getDefaultSensor(2);
            if (sensor != null) {
                this.compassAvailable = this.accelerometerAvailable;
                if (this.compassAvailable) {
                    this.compassListener = new SensorListener(this.nativeOrientation, this.accelerometerValues, this.magneticFieldValues);
                    this.compassAvailable = this.manager.registerListener(this.compassListener, sensor, 1);
                }
            } else {
                this.compassAvailable = false;
            }
        } else {
            this.compassAvailable = false;
        }
        Gdx.app.log("AndroidInput", "sensor listener setup");
    }

    /* access modifiers changed from: package-private */
    public void unregisterSensorListeners() {
        if (this.manager != null) {
            if (this.accelerometerListener != null) {
                this.manager.unregisterListener(this.accelerometerListener);
                this.accelerometerListener = null;
            }
            if (this.compassListener != null) {
                this.manager.unregisterListener(this.compassListener);
                this.compassListener = null;
            }
            this.manager = null;
        }
        Gdx.app.log("AndroidInput", "sensor listener tear down");
    }

    public InputProcessor getInputProcessor() {
        return this.processor;
    }

    public boolean isPeripheralAvailable(Input.Peripheral peripheral) {
        if (peripheral == Input.Peripheral.Accelerometer) {
            return this.accelerometerAvailable;
        }
        if (peripheral == Input.Peripheral.Compass) {
            return this.compassAvailable;
        }
        if (peripheral == Input.Peripheral.HardwareKeyboard) {
            return this.keyboardAvailable;
        }
        if (peripheral == Input.Peripheral.OnscreenKeyboard) {
            return true;
        }
        if (peripheral == Input.Peripheral.Vibrator) {
            if (this.vibrator == null) {
                return false;
            }
            return true;
        } else if (peripheral == Input.Peripheral.MultitouchScreen) {
            return this.hasMultitouch;
        } else {
            return false;
        }
    }

    public int getFreePointerIndex() {
        int len = this.realId.length;
        for (int i = 0; i < len; i++) {
            if (this.realId[i] == -1) {
                return i;
            }
        }
        int[] tmp = new int[(this.realId.length + 1)];
        System.arraycopy(this.realId, 0, tmp, 0, this.realId.length);
        this.realId = tmp;
        return tmp.length - 1;
    }

    public int lookUpPointerIndex(int pointerId) {
        int len = this.realId.length;
        for (int i = 0; i < len; i++) {
            if (this.realId[i] == pointerId) {
                return i;
            }
        }
        StringBuffer buf = new StringBuffer();
        for (int i2 = 0; i2 < len; i2++) {
            buf.append(i2 + ":" + this.realId[i2] + " ");
        }
        Gdx.app.log("AndroidInput", "Pointer ID lookup failed: " + pointerId + ", " + buf.toString());
        return -1;
    }

    public int getRotation() {
        int orientation2;
        if (this.context instanceof Activity) {
            orientation2 = ((Activity) this.context).getWindowManager().getDefaultDisplay().getOrientation();
        } else {
            orientation2 = ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay().getOrientation();
        }
        switch (orientation2) {
            case 0:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                return 0;
        }
    }

    public Input.Orientation getNativeOrientation() {
        return this.nativeOrientation;
    }

    public void setCursorCatched(boolean catched) {
    }

    public boolean isCursorCatched() {
        return false;
    }

    public int getDeltaX() {
        return this.deltaX[0];
    }

    public int getDeltaX(int pointer) {
        return this.deltaX[pointer];
    }

    public int getDeltaY() {
        return this.deltaY[0];
    }

    public int getDeltaY(int pointer) {
        return this.deltaY[pointer];
    }

    public void setCursorPosition(int x, int y) {
    }

    public long getCurrentEventTime() {
        return this.currentEventTimeStamp;
    }

    public void addKeyListener(View.OnKeyListener listener) {
        this.keyListeners.add(listener);
    }

    private class SensorListener implements SensorEventListener {
        final float[] accelerometerValues;
        final float[] magneticFieldValues;
        final Input.Orientation nativeOrientation;

        SensorListener(Input.Orientation nativeOrientation2, float[] accelerometerValues2, float[] magneticFieldValues2) {
            this.accelerometerValues = accelerometerValues2;
            this.magneticFieldValues = magneticFieldValues2;
            this.nativeOrientation = nativeOrientation2;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == 1) {
                if (this.nativeOrientation == Input.Orientation.Portrait) {
                    System.arraycopy(event.values, 0, this.accelerometerValues, 0, this.accelerometerValues.length);
                } else {
                    this.accelerometerValues[0] = event.values[1];
                    this.accelerometerValues[1] = -event.values[0];
                    this.accelerometerValues[2] = event.values[2];
                }
            }
            if (event.sensor.getType() == 2) {
                System.arraycopy(event.values, 0, this.magneticFieldValues, 0, this.magneticFieldValues.length);
            }
        }
    }
}
