package com.badlogic.gdx;

import com.badlogic.gdx.utils.Clipboard;

public interface Application {
    public static final int LOG_DEBUG = 3;
    public static final int LOG_ERROR = 1;
    public static final int LOG_INFO = 2;
    public static final int LOG_NONE = 0;

    public enum ApplicationType {
        Android,
        Desktop,
        Applet,
        WebGL,
        iOS
    }

    void addLifecycleListener(LifecycleListener lifecycleListener);

    void debug(String str, String str2);

    void debug(String str, String str2, Throwable th);

    void error(String str, String str2);

    void error(String str, String str2, Throwable th);

    void exit();

    Audio getAudio();

    Clipboard getClipboard();

    Files getFiles();

    Graphics getGraphics();

    Input getInput();

    long getJavaHeap();

    long getNativeHeap();

    Net getNet();

    Preferences getPreferences(String str);

    ApplicationType getType();

    int getVersion();

    void log(String str, String str2);

    void log(String str, String str2, Exception exc);

    void postRunnable(Runnable runnable);

    void removeLifecycleListener(LifecycleListener lifecycleListener);

    void setLogLevel(int i);
}
