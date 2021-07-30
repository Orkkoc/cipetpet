package com.badlogic.gdx.backends.android;

import android.os.Build;
import android.os.Debug;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;

class AndroidLiveWallpaper implements Application {
    protected AndroidAudio audio;
    AndroidClipboard clipboard;
    private WallpaperService.Engine engine;
    protected final Array<Runnable> executedRunnables = new Array<>();
    protected AndroidFiles files;
    protected boolean firstResume = true;
    protected AndroidGraphicsLiveWallpaper graphics;
    protected AndroidInput input;
    protected final Array<LifecycleListener> lifecycleListeners = new Array<>();
    protected ApplicationListener listener;
    protected int logLevel = 2;
    protected AndroidNet net;
    protected final Array<Runnable> runnables = new Array<>();
    protected WallpaperService service;

    static {
        GdxNativesLoader.load();
    }

    public AndroidLiveWallpaper(WallpaperService service2, WallpaperService.Engine engine2) {
        this.service = service2;
        this.engine = engine2;
    }

    public void initialize(ApplicationListener listener2, AndroidApplicationConfiguration config) {
        this.graphics = new AndroidGraphicsLiveWallpaper(this, config, config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
        this.input = AndroidInputFactory.newAndroidInput(this, getService(), (Object) null, config);
        this.audio = new AndroidAudio(getService(), config);
        this.files = new AndroidFiles(getService().getAssets(), getService().getFilesDir().getAbsolutePath());
        this.listener = listener2;
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
    }

    public void onPause() {
        this.graphics.pause();
        if (this.audio != null) {
            this.audio.pause();
        }
        this.input.unregisterSensorListeners();
    }

    public void onResume() {
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        ((AndroidInput) getInput()).registerSensorListeners();
        if (this.audio != null) {
            this.audio.resume();
        }
        if (!this.firstResume) {
            this.graphics.resume();
        } else {
            this.firstResume = false;
        }
    }

    public void onDestroy() {
        this.graphics.clearManagedCaches();
    }

    public WallpaperService getService() {
        return this.service;
    }

    public WallpaperService.Engine getEngine() {
        return this.engine;
    }

    public ApplicationListener getListener() {
        return this.listener;
    }

    public void postRunnable(Runnable runnable) {
        synchronized (this.runnables) {
            this.runnables.add(runnable);
        }
    }

    public Audio getAudio() {
        return this.audio;
    }

    public Files getFiles() {
        return this.files;
    }

    public Graphics getGraphics() {
        return this.graphics;
    }

    public Input getInput() {
        return this.input;
    }

    public Net getNet() {
        return this.net;
    }

    public Application.ApplicationType getType() {
        return Application.ApplicationType.Android;
    }

    public int getVersion() {
        return Integer.parseInt(Build.VERSION.SDK);
    }

    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public long getNativeHeap() {
        return Debug.getNativeHeapAllocatedSize();
    }

    public Preferences getPreferences(String name) {
        return new AndroidPreferences(this.service.getSharedPreferences(name, 0));
    }

    public Clipboard getClipboard() {
        if (this.clipboard == null) {
            this.clipboard = new AndroidClipboard(this.service);
        }
        return this.clipboard;
    }

    public void debug(String tag, String message) {
        if (this.logLevel >= 3) {
            Log.d(tag, message);
        }
    }

    public void debug(String tag, String message, Throwable exception) {
        if (this.logLevel >= 3) {
            Log.d(tag, message, exception);
        }
    }

    public void log(String tag, String message) {
        if (this.logLevel >= 2) {
            Log.i(tag, message);
        }
    }

    public void log(String tag, String message, Exception exception) {
        if (this.logLevel >= 2) {
            Log.i(tag, message, exception);
        }
    }

    public void error(String tag, String message) {
        if (this.logLevel >= 1) {
            Log.e(tag, message);
        }
    }

    public void error(String tag, String message, Throwable exception) {
        if (this.logLevel >= 1) {
            Log.e(tag, message, exception);
        }
    }

    public void setLogLevel(int logLevel2) {
        this.logLevel = logLevel2;
    }

    public void exit() {
    }

    public void addLifecycleListener(LifecycleListener listener2) {
        synchronized (this.lifecycleListeners) {
            this.lifecycleListeners.add(listener2);
        }
    }

    public void removeLifecycleListener(LifecycleListener listener2) {
        synchronized (this.lifecycleListeners) {
            this.lifecycleListeners.removeValue(listener2, true);
        }
    }
}
