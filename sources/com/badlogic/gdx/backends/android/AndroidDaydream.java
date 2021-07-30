package com.badlogic.gdx.backends.android;

import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import java.lang.reflect.Method;

public class AndroidDaydream extends DreamService implements Application {
    protected AndroidAudio audio;
    AndroidClipboard clipboard;
    protected final Array<Runnable> executedRunnables = new Array<>();
    protected AndroidFiles files;
    protected boolean firstResume = true;
    protected AndroidGraphicsDaydream graphics;
    protected Handler handler;
    protected AndroidInput input;
    protected final Array<LifecycleListener> lifecycleListeners = new Array<>();
    protected ApplicationListener listener;
    protected int logLevel = 2;
    protected AndroidNet net;
    protected final Array<Runnable> runnables = new Array<>();
    protected PowerManager.WakeLock wakeLock = null;

    static {
        GdxNativesLoader.load();
    }

    public void initialize(ApplicationListener listener2, boolean useGL2IfAvailable) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useGL20 = useGL2IfAvailable;
        initialize(listener2, config);
    }

    public void initialize(ApplicationListener listener2, AndroidApplicationConfiguration config) {
        this.graphics = new AndroidGraphicsDaydream(this, config, config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
        this.input = AndroidInputFactory.newAndroidInput(this, this, this.graphics.view, config);
        this.audio = new AndroidAudio(this, config);
        this.files = new AndroidFiles(getAssets(), getFilesDir().getAbsolutePath());
        this.listener = listener2;
        this.handler = new Handler();
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        Gdx.net = getNet();
        setFullscreen(true);
        setContentView(this.graphics.getView(), createLayoutParams());
        createWakeLock(config);
        hideStatusBar(config);
    }

    /* access modifiers changed from: protected */
    public FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
        layoutParams.gravity = 17;
        return layoutParams;
    }

    /* access modifiers changed from: protected */
    public void createWakeLock(AndroidApplicationConfiguration config) {
        if (config.useWakelock) {
            this.wakeLock = ((PowerManager) getSystemService("power")).newWakeLock(26, "libgdx wakelock");
        }
    }

    /* access modifiers changed from: protected */
    public void hideStatusBar(AndroidApplicationConfiguration config) {
        if (config.hideStatusBar && getVersion() >= 11) {
            View rootView = getWindow().getDecorView();
            Class<View> cls = View.class;
            try {
                Method m = cls.getMethod("setSystemUiVisibility", new Class[]{Integer.TYPE});
                m.invoke(rootView, new Object[]{0});
                m.invoke(rootView, new Object[]{1});
            } catch (Exception e) {
                log("AndroidApplication", "Can't hide status bar", e);
            }
        }
    }

    public View initializeForView(ApplicationListener listener2, boolean useGL2IfAvailable) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useGL20 = useGL2IfAvailable;
        return initializeForView(listener2, config);
    }

    public View initializeForView(ApplicationListener listener2, AndroidApplicationConfiguration config) {
        this.graphics = new AndroidGraphicsDaydream(this, config, config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
        this.input = AndroidInputFactory.newAndroidInput(this, this, this.graphics.view, config);
        this.audio = new AndroidAudio(this, config);
        this.files = new AndroidFiles(getAssets(), getFilesDir().getAbsolutePath());
        this.listener = listener2;
        this.handler = new Handler();
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        Gdx.net = getNet();
        createWakeLock(config);
        hideStatusBar(config);
        return this.graphics.getView();
    }

    public void onDreamingStopped() {
        if (this.wakeLock != null) {
            this.wakeLock.release();
        }
        boolean isContinuous = this.graphics.isContinuousRendering();
        this.graphics.setContinuousRendering(true);
        this.graphics.pause();
        this.input.unregisterSensorListeners();
        int[] realId = this.input.realId;
        for (int i = 0; i < realId.length; i++) {
            realId[i] = -1;
        }
        this.graphics.clearManagedCaches();
        this.graphics.destroy();
        this.graphics.setContinuousRendering(isContinuous);
        if (!(this.graphics == null || this.graphics.view == null)) {
            if (this.graphics.view instanceof GLSurfaceViewCupcake) {
                ((GLSurfaceViewCupcake) this.graphics.view).onPause();
            }
            if (this.graphics.view instanceof GLSurfaceView) {
                ((GLSurfaceView) this.graphics.view).onPause();
            }
        }
        super.onDreamingStopped();
    }

    public void onDreamingStarted() {
        if (this.wakeLock != null) {
            this.wakeLock.acquire();
        }
        Gdx.app = this;
        Gdx.input = getInput();
        Gdx.audio = getAudio();
        Gdx.files = getFiles();
        Gdx.graphics = getGraphics();
        Gdx.net = getNet();
        ((AndroidInput) getInput()).registerSensorListeners();
        if (!(this.graphics == null || this.graphics.view == null)) {
            if (this.graphics.view instanceof GLSurfaceViewCupcake) {
                ((GLSurfaceViewCupcake) this.graphics.view).onResume();
            }
            if (this.graphics.view instanceof GLSurfaceView) {
                ((GLSurfaceView) this.graphics.view).onResume();
            }
        }
        if (!this.firstResume) {
            this.graphics.resume();
        } else {
            this.firstResume = false;
        }
        super.onDreamingStarted();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
        return new AndroidPreferences(getSharedPreferences(name, 0));
    }

    public Clipboard getClipboard() {
        if (this.clipboard == null) {
            this.clipboard = new AndroidClipboard(this);
        }
        return this.clipboard;
    }

    public void postRunnable(Runnable runnable) {
        synchronized (this.runnables) {
            this.runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }

    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        boolean keyboardAvailable = false;
        if (config.hardKeyboardHidden == 1) {
            keyboardAvailable = true;
        }
        this.input.keyboardAvailable = keyboardAvailable;
    }

    public void exit() {
        this.handler.post(new Runnable() {
            public void run() {
                AndroidDaydream.this.finish();
            }
        });
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
