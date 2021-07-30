package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class RemoteInput implements Runnable, Input {
    public static int DEFAULT_PORT = 8190;
    private float[] accel;
    private float[] compass;
    public final String[] ips;
    boolean[] isTouched;
    boolean justTouched;
    Set<Integer> keys;
    private boolean multiTouch;
    private final int port;
    InputProcessor processor;
    private float remoteHeight;
    private float remoteWidth;
    private ServerSocket serverSocket;
    int[] touchX;
    int[] touchY;

    class KeyEvent {
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

    class TouchEvent {
        static final int TOUCH_DOWN = 0;
        static final int TOUCH_DRAGGED = 2;
        static final int TOUCH_UP = 1;
        int pointer;
        long timeStamp;
        int type;

        /* renamed from: x */
        int f142x;

        /* renamed from: y */
        int f143y;

        TouchEvent() {
        }
    }

    class EventTrigger implements Runnable {
        KeyEvent keyEvent;
        TouchEvent touchEvent;

        public EventTrigger(TouchEvent touchEvent2, KeyEvent keyEvent2) {
            this.touchEvent = touchEvent2;
            this.keyEvent = keyEvent2;
        }

        public void run() {
            RemoteInput.this.justTouched = false;
            if (RemoteInput.this.processor != null) {
                if (this.touchEvent != null) {
                    RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.f142x;
                    RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.f143y;
                    switch (this.touchEvent.type) {
                        case 0:
                            RemoteInput.this.processor.touchDown(this.touchEvent.f142x, this.touchEvent.f143y, this.touchEvent.pointer, 0);
                            RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                            RemoteInput.this.justTouched = true;
                            break;
                        case 1:
                            RemoteInput.this.processor.touchUp(this.touchEvent.f142x, this.touchEvent.f143y, this.touchEvent.pointer, 0);
                            RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                            break;
                        case 2:
                            RemoteInput.this.processor.touchDragged(this.touchEvent.f142x, this.touchEvent.f143y, this.touchEvent.pointer);
                            break;
                    }
                }
                if (this.keyEvent != null) {
                    switch (this.keyEvent.type) {
                        case 0:
                            RemoteInput.this.processor.keyDown(this.keyEvent.keyCode);
                            RemoteInput.this.keys.add(Integer.valueOf(this.keyEvent.keyCode));
                            return;
                        case 1:
                            RemoteInput.this.processor.keyUp(this.keyEvent.keyCode);
                            RemoteInput.this.keys.remove(Integer.valueOf(this.keyEvent.keyCode));
                            return;
                        case 2:
                            RemoteInput.this.processor.keyTyped(this.keyEvent.keyChar);
                            return;
                        default:
                            return;
                    }
                }
            } else {
                if (this.touchEvent != null) {
                    RemoteInput.this.touchX[this.touchEvent.pointer] = this.touchEvent.f142x;
                    RemoteInput.this.touchY[this.touchEvent.pointer] = this.touchEvent.f143y;
                    if (this.touchEvent.type == 0) {
                        RemoteInput.this.isTouched[this.touchEvent.pointer] = true;
                        RemoteInput.this.justTouched = true;
                    }
                    if (this.touchEvent.type == 1) {
                        RemoteInput.this.isTouched[this.touchEvent.pointer] = false;
                    }
                }
                if (this.keyEvent != null) {
                    if (this.keyEvent.type == 0) {
                        RemoteInput.this.keys.add(Integer.valueOf(this.keyEvent.keyCode));
                    }
                    if (this.keyEvent.type == 1) {
                        RemoteInput.this.keys.remove(Integer.valueOf(this.keyEvent.keyCode));
                    }
                }
            }
        }
    }

    public RemoteInput() {
        this(DEFAULT_PORT);
    }

    public RemoteInput(int port2) {
        this.accel = new float[3];
        this.compass = new float[3];
        this.multiTouch = false;
        this.remoteWidth = 0.0f;
        this.remoteHeight = 0.0f;
        this.keys = new HashSet();
        this.touchX = new int[20];
        this.touchY = new int[20];
        this.isTouched = new boolean[20];
        this.justTouched = false;
        this.processor = null;
        try {
            this.port = port2;
            this.serverSocket = new ServerSocket(port2);
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            this.ips = new String[allByName.length];
            for (int i = 0; i < allByName.length; i++) {
                this.ips[i] = allByName[i].getHostAddress();
            }
        } catch (Exception e) {
            throw new GdxRuntimeException("Couldn't open listening socket at port '" + port2 + "'", e);
        }
    }

    public void run() {
        while (true) {
            try {
                System.out.println("listening, port " + this.port);
                Socket socket = this.serverSocket.accept();
                socket.setTcpNoDelay(true);
                socket.setSoTimeout(3000);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                this.multiTouch = in.readBoolean();
                while (true) {
                    KeyEvent keyEvent = null;
                    TouchEvent touchEvent = null;
                    switch (in.readInt()) {
                        case 0:
                            keyEvent = new KeyEvent();
                            keyEvent.keyCode = in.readInt();
                            keyEvent.type = 0;
                            break;
                        case 1:
                            keyEvent = new KeyEvent();
                            keyEvent.keyCode = in.readInt();
                            keyEvent.type = 1;
                            break;
                        case 2:
                            keyEvent = new KeyEvent();
                            keyEvent.keyChar = in.readChar();
                            keyEvent.type = 2;
                            break;
                        case 3:
                            touchEvent = new TouchEvent();
                            touchEvent.f142x = (int) ((((float) in.readInt()) / this.remoteWidth) * ((float) Gdx.graphics.getWidth()));
                            touchEvent.f143y = (int) ((((float) in.readInt()) / this.remoteHeight) * ((float) Gdx.graphics.getHeight()));
                            touchEvent.pointer = in.readInt();
                            touchEvent.type = 0;
                            break;
                        case 4:
                            touchEvent = new TouchEvent();
                            touchEvent.f142x = (int) ((((float) in.readInt()) / this.remoteWidth) * ((float) Gdx.graphics.getWidth()));
                            touchEvent.f143y = (int) ((((float) in.readInt()) / this.remoteHeight) * ((float) Gdx.graphics.getHeight()));
                            touchEvent.pointer = in.readInt();
                            touchEvent.type = 1;
                            break;
                        case 5:
                            touchEvent = new TouchEvent();
                            touchEvent.f142x = (int) ((((float) in.readInt()) / this.remoteWidth) * ((float) Gdx.graphics.getWidth()));
                            touchEvent.f143y = (int) ((((float) in.readInt()) / this.remoteHeight) * ((float) Gdx.graphics.getHeight()));
                            touchEvent.pointer = in.readInt();
                            touchEvent.type = 2;
                            break;
                        case 6:
                            this.accel[0] = in.readFloat();
                            this.accel[1] = in.readFloat();
                            this.accel[2] = in.readFloat();
                            break;
                        case 7:
                            this.compass[0] = in.readFloat();
                            this.compass[1] = in.readFloat();
                            this.compass[2] = in.readFloat();
                            break;
                        case 8:
                            this.remoteWidth = in.readFloat();
                            this.remoteHeight = in.readFloat();
                            break;
                    }
                    Gdx.app.postRunnable(new EventTrigger(touchEvent, keyEvent));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public float getAccelerometerX() {
        return this.accel[0];
    }

    public float getAccelerometerY() {
        return this.accel[1];
    }

    public float getAccelerometerZ() {
        return this.accel[2];
    }

    public int getX() {
        return this.touchX[0];
    }

    public int getX(int pointer) {
        return this.touchX[pointer];
    }

    public int getY() {
        return this.touchY[0];
    }

    public int getY(int pointer) {
        return this.touchY[pointer];
    }

    public boolean isTouched() {
        return this.isTouched[0];
    }

    public boolean justTouched() {
        return this.justTouched;
    }

    public boolean isTouched(int pointer) {
        return this.isTouched[pointer];
    }

    public boolean isButtonPressed(int button) {
        if (button != 0) {
            return false;
        }
        for (boolean z : this.isTouched) {
            if (z) {
                return true;
            }
        }
        return false;
    }

    public boolean isKeyPressed(int key) {
        return this.keys.contains(Integer.valueOf(key));
    }

    public void getTextInput(Input.TextInputListener listener, String title, String text) {
        Gdx.app.getInput().getTextInput(listener, title, text);
    }

    public void getPlaceholderTextInput(Input.TextInputListener listener, String title, String placeholder) {
        Gdx.app.getInput().getPlaceholderTextInput(listener, title, placeholder);
    }

    public void setOnscreenKeyboardVisible(boolean visible) {
    }

    public void vibrate(int milliseconds) {
    }

    public void vibrate(long[] pattern, int repeat) {
    }

    public void cancelVibrate() {
    }

    public float getAzimuth() {
        return this.compass[0];
    }

    public float getPitch() {
        return this.compass[1];
    }

    public float getRoll() {
        return this.compass[2];
    }

    public void setCatchBackKey(boolean catchBack) {
    }

    public void setInputProcessor(InputProcessor processor2) {
        this.processor = processor2;
    }

    public InputProcessor getInputProcessor() {
        return this.processor;
    }

    public String[] getIPs() {
        return this.ips;
    }

    public boolean isPeripheralAvailable(Input.Peripheral peripheral) {
        if (peripheral == Input.Peripheral.Accelerometer || peripheral == Input.Peripheral.Compass) {
            return true;
        }
        if (peripheral == Input.Peripheral.MultitouchScreen) {
            return this.multiTouch;
        }
        return false;
    }

    public int getRotation() {
        return 0;
    }

    public Input.Orientation getNativeOrientation() {
        return Input.Orientation.Landscape;
    }

    public void setCursorCatched(boolean catched) {
    }

    public boolean isCursorCatched() {
        return false;
    }

    public int getDeltaX() {
        return 0;
    }

    public int getDeltaX(int pointer) {
        return 0;
    }

    public int getDeltaY() {
        return 0;
    }

    public int getDeltaY(int pointer) {
        return 0;
    }

    public void setCursorPosition(int x, int y) {
    }

    public void setCatchMenuKey(boolean catchMenu) {
    }

    public long getCurrentEventTime() {
        return 0;
    }

    public void getRotationMatrix(float[] matrix) {
    }
}
