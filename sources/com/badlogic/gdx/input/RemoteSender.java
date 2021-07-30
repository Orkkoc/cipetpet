package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import java.io.DataOutputStream;
import java.net.Socket;

public class RemoteSender implements InputProcessor {
    public static final int ACCEL = 6;
    public static final int COMPASS = 7;
    public static final int KEY_DOWN = 0;
    public static final int KEY_TYPED = 2;
    public static final int KEY_UP = 1;
    public static final int SIZE = 8;
    public static final int TOUCH_DOWN = 3;
    public static final int TOUCH_DRAGGED = 5;
    public static final int TOUCH_UP = 4;
    private boolean connected = false;
    private DataOutputStream out;

    public RemoteSender(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(3000);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.out.writeBoolean(Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen));
            this.connected = true;
            Gdx.input.setInputProcessor(this);
        } catch (Exception e) {
            Gdx.app.log("RemoteSender", "couldn't connect to " + ip + ":" + port);
        }
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendUpdate() {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r1 = r3.connected     // Catch:{ all -> 0x007e }
            if (r1 != 0) goto L_0x0007
            monitor-exit(r3)     // Catch:{ all -> 0x007e }
        L_0x0006:
            return
        L_0x0007:
            monitor-exit(r3)     // Catch:{ all -> 0x007e }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            r2 = 6
            r1.writeInt(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Input r2 = com.badlogic.gdx.Gdx.input     // Catch:{ Throwable -> 0x0076 }
            float r2 = r2.getAccelerometerX()     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Input r2 = com.badlogic.gdx.Gdx.input     // Catch:{ Throwable -> 0x0076 }
            float r2 = r2.getAccelerometerY()     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Input r2 = com.badlogic.gdx.Gdx.input     // Catch:{ Throwable -> 0x0076 }
            float r2 = r2.getAccelerometerZ()     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            r2 = 7
            r1.writeInt(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Input r2 = com.badlogic.gdx.Gdx.input     // Catch:{ Throwable -> 0x0076 }
            float r2 = r2.getAzimuth()     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Input r2 = com.badlogic.gdx.Gdx.input     // Catch:{ Throwable -> 0x0076 }
            float r2 = r2.getPitch()     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Input r2 = com.badlogic.gdx.Gdx.input     // Catch:{ Throwable -> 0x0076 }
            float r2 = r2.getRoll()     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            r2 = 8
            r1.writeInt(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Graphics r2 = com.badlogic.gdx.Gdx.graphics     // Catch:{ Throwable -> 0x0076 }
            int r2 = r2.getWidth()     // Catch:{ Throwable -> 0x0076 }
            float r2 = (float) r2     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            java.io.DataOutputStream r1 = r3.out     // Catch:{ Throwable -> 0x0076 }
            com.badlogic.gdx.Graphics r2 = com.badlogic.gdx.Gdx.graphics     // Catch:{ Throwable -> 0x0076 }
            int r2 = r2.getHeight()     // Catch:{ Throwable -> 0x0076 }
            float r2 = (float) r2     // Catch:{ Throwable -> 0x0076 }
            r1.writeFloat(r2)     // Catch:{ Throwable -> 0x0076 }
            goto L_0x0006
        L_0x0076:
            r0 = move-exception
            r1 = 0
            r3.out = r1
            r1 = 0
            r3.connected = r1
            goto L_0x0006
        L_0x007e:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x007e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.input.RemoteSender.sendUpdate():void");
    }

    public boolean keyDown(int keycode) {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(0);
                    this.out.writeInt(keycode);
                } catch (Throwable th) {
                    synchronized (this) {
                        this.connected = false;
                    }
                }
            }
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(1);
                    this.out.writeInt(keycode);
                } catch (Throwable th) {
                    synchronized (this) {
                        this.connected = false;
                    }
                }
            }
        }
        return false;
    }

    public boolean keyTyped(char character) {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(2);
                    this.out.writeChar(character);
                } catch (Throwable th) {
                    synchronized (this) {
                        this.connected = false;
                    }
                }
            }
        }
        return false;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(3);
                    this.out.writeInt(x);
                    this.out.writeInt(y);
                    this.out.writeInt(pointer);
                } catch (Throwable th) {
                    synchronized (this) {
                        this.connected = false;
                    }
                }
            }
        }
        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(4);
                    this.out.writeInt(x);
                    this.out.writeInt(y);
                    this.out.writeInt(pointer);
                } catch (Throwable th) {
                    synchronized (this) {
                        this.connected = false;
                    }
                }
            }
        }
        return false;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        synchronized (this) {
            if (this.connected) {
                try {
                    this.out.writeInt(5);
                    this.out.writeInt(x);
                    this.out.writeInt(y);
                    this.out.writeInt(pointer);
                } catch (Throwable th) {
                    synchronized (this) {
                        this.connected = false;
                    }
                }
            }
        }
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this) {
            z = this.connected;
        }
        return z;
    }
}
