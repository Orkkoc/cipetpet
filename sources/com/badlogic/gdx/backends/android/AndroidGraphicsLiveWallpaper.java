package com.badlogic.gdx.backends.android;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.DefaultGLSurfaceViewLW;
import com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceViewLW;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20LW;
import com.badlogic.gdx.backends.android.surfaceview.GdxEglConfigChooser;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.GLU;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public final class AndroidGraphicsLiveWallpaper implements Graphics, GLSurfaceView.Renderer {
    AndroidLiveWallpaper app;
    private Graphics.BufferFormat bufferFormat = new Graphics.BufferFormat(5, 6, 5, 0, 16, 0, 0, false);
    private final AndroidApplicationConfiguration config;
    volatile boolean created = false;
    protected float deltaTime = 0.0f;
    protected float density = 1.0f;
    volatile boolean destroy = false;
    protected EGLContext eglContext;
    protected String extensions;
    protected int fps;
    protected long frameStart = System.nanoTime();
    protected int frames = 0;

    /* renamed from: gl */
    protected GLCommon f62gl;
    protected GL10 gl10;
    protected GL11 gl11;
    protected GL20 gl20;
    protected GLU glu;
    int height;
    protected boolean isContinuous = true;
    protected long lastFrameTime = System.nanoTime();
    protected WindowedMean mean = new WindowedMean(5);
    volatile boolean pause = false;
    protected float ppcX = 0.0f;
    protected float ppcY = 0.0f;
    protected float ppiX = 0.0f;
    protected float ppiY = 0.0f;
    volatile boolean resume = false;
    volatile boolean running = false;
    Object synch = new Object();
    int[] value = new int[1];
    final GLBaseSurfaceViewLW view;
    int width;

    /* access modifiers changed from: protected */
    public boolean checkGL20() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, new int[2]);
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, new int[]{12324, 4, 12323, 4, 12322, 4, 12352, 4, 12344}, new EGLConfig[10], 10, num_config);
        egl.eglTerminate(display);
        return num_config[0] > 0;
    }

    public GL10 getGL10() {
        return this.gl10;
    }

    public GL11 getGL11() {
        return this.gl11;
    }

    public GL20 getGL20() {
        return this.gl20;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isGL11Available() {
        return this.gl11 != null;
    }

    public boolean isGL20Available() {
        return this.gl20 != null;
    }

    /* access modifiers changed from: protected */
    public void logConfig(EGLConfig config2) {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int r = getAttrib(egl, display, config2, 12324, 0);
        int g = getAttrib(egl, display, config2, 12323, 0);
        int b = getAttrib(egl, display, config2, 12322, 0);
        int a = getAttrib(egl, display, config2, 12321, 0);
        int d = getAttrib(egl, display, config2, 12325, 0);
        int s = getAttrib(egl, display, config2, 12326, 0);
        int samples = Math.max(getAttrib(egl, display, config2, 12337, 0), getAttrib(egl, display, config2, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV, 0));
        boolean coverageSample = getAttrib(egl, display, config2, GdxEglConfigChooser.EGL_COVERAGE_SAMPLES_NV, 0) != 0;
        Gdx.app.log("AndroidGraphics", "framebuffer: (" + r + ", " + g + ", " + b + ", " + a + ")");
        Gdx.app.log("AndroidGraphics", "depthbuffer: (" + d + ")");
        Gdx.app.log("AndroidGraphics", "stencilbuffer: (" + s + ")");
        Gdx.app.log("AndroidGraphics", "samples: (" + samples + ")");
        Gdx.app.log("AndroidGraphics", "coverage sampling: (" + coverageSample + ")");
        this.bufferFormat = new Graphics.BufferFormat(r, g, b, a, d, s, samples, coverageSample);
    }

    private int getAttrib(EGL10 egl, EGLDisplay display, EGLConfig config2, int attrib, int defValue) {
        if (egl.eglGetConfigAttrib(display, config2, attrib, this.value)) {
            return this.value[0];
        }
        return defValue;
    }

    /* access modifiers changed from: package-private */
    public void resume() {
        synchronized (this.synch) {
            this.running = true;
            this.resume = true;
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void pause() {
        /*
            r5 = this;
            java.lang.Object r2 = r5.synch
            monitor-enter(r2)
            boolean r1 = r5.running     // Catch:{ all -> 0x0024 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r2)     // Catch:{ all -> 0x0024 }
        L_0x0008:
            return
        L_0x0009:
            r1 = 0
            r5.running = r1     // Catch:{ all -> 0x0024 }
            r1 = 1
            r5.pause = r1     // Catch:{ all -> 0x0024 }
        L_0x000f:
            boolean r1 = r5.pause     // Catch:{ all -> 0x0024 }
            if (r1 == 0) goto L_0x0027
            java.lang.Object r1 = r5.synch     // Catch:{ InterruptedException -> 0x0019 }
            r1.wait()     // Catch:{ InterruptedException -> 0x0019 }
            goto L_0x000f
        L_0x0019:
            r0 = move-exception
            com.badlogic.gdx.Application r1 = com.badlogic.gdx.Gdx.app     // Catch:{ all -> 0x0024 }
            java.lang.String r3 = "AndroidGraphics"
            java.lang.String r4 = "waiting for pause synchronization failed!"
            r1.log(r3, r4)     // Catch:{ all -> 0x0024 }
            goto L_0x000f
        L_0x0024:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0024 }
            throw r1
        L_0x0027:
            monitor-exit(r2)     // Catch:{ all -> 0x0024 }
            goto L_0x0008
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidGraphicsLiveWallpaper.pause():void");
    }

    /* access modifiers changed from: package-private */
    public void destroy() {
        synchronized (this.synch) {
            this.running = false;
            this.destroy = true;
            while (this.destroy) {
                try {
                    this.synch.wait();
                } catch (InterruptedException e) {
                    Gdx.app.log("AndroidGraphics", "waiting for destroy synchronization failed!");
                }
            }
        }
    }

    public float getDeltaTime() {
        return this.mean.getMean() == 0.0f ? this.deltaTime : this.mean.getMean();
    }

    public float getRawDeltaTime() {
        return this.deltaTime;
    }

    public Graphics.GraphicsType getType() {
        return Graphics.GraphicsType.AndroidGL;
    }

    public int getFramesPerSecond() {
        return this.fps;
    }

    public GLCommon getGLCommon() {
        return this.f62gl;
    }

    public float getPpiX() {
        return this.ppiX;
    }

    public float getPpiY() {
        return this.ppiY;
    }

    public float getPpcX() {
        return this.ppcX;
    }

    public float getPpcY() {
        return this.ppcY;
    }

    public boolean supportsDisplayModeChange() {
        return false;
    }

    public boolean setDisplayMode(Graphics.DisplayMode displayMode) {
        return false;
    }

    public void setTitle(String title) {
    }

    class AndroidDisplayMode extends Graphics.DisplayMode {
        protected AndroidDisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
            super(width, height, refreshRate, bitsPerPixel);
        }
    }

    public Graphics.BufferFormat getBufferFormat() {
        return this.bufferFormat;
    }

    public void setVSync(boolean vsync) {
    }

    public boolean supportsExtension(String extension) {
        if (this.extensions == null) {
            this.extensions = Gdx.f12gl.glGetString(7939);
        }
        return this.extensions.contains(extension);
    }

    public boolean isContinuousRendering() {
        return this.isContinuous;
    }

    public boolean isFullscreen() {
        return true;
    }

    public AndroidGraphicsLiveWallpaper(AndroidLiveWallpaper app2, AndroidApplicationConfiguration config2, ResolutionStrategy resolutionStrategy) {
        this.config = config2;
        this.view = createGLSurfaceView(app2, config2.useGL20, resolutionStrategy);
        this.app = app2;
    }

    private GLBaseSurfaceViewLW createGLSurfaceView(AndroidLiveWallpaper app2, boolean useGL2, ResolutionStrategy resolutionStrategy) {
        GLBaseSurfaceViewLW gLBaseSurfaceViewLW;
        GLSurfaceView.EGLConfigChooser configChooser = getEglConfigChooser();
        if (!useGL2 || !checkGL20()) {
            this.config.useGL20 = false;
            GLSurfaceView.EGLConfigChooser configChooser2 = getEglConfigChooser();
            GLBaseSurfaceViewLW view2 = new DefaultGLSurfaceViewLW(app2.getEngine(), resolutionStrategy);
            if (configChooser2 != null) {
                view2.setEGLConfigChooser(configChooser2);
            } else {
                view2.setEGLConfigChooser(this.config.f57r, this.config.f56g, this.config.f55b, this.config.f54a, this.config.depth, this.config.stencil);
            }
            view2.setRenderer(this);
            gLBaseSurfaceViewLW = view2;
        } else {
            GLSurfaceView20LW view3 = new GLSurfaceView20LW(app2.getEngine(), resolutionStrategy);
            if (configChooser != null) {
                view3.setEGLConfigChooser(configChooser);
            } else {
                view3.setEGLConfigChooser(this.config.f57r, this.config.f56g, this.config.f55b, this.config.f54a, this.config.depth, this.config.stencil);
            }
            view3.setRenderer(this);
            gLBaseSurfaceViewLW = view3;
        }
        return gLBaseSurfaceViewLW;
    }

    private GLSurfaceView.EGLConfigChooser getEglConfigChooser() {
        return new GdxEglConfigChooser(this.config.f57r, this.config.f56g, this.config.f55b, this.config.f54a, this.config.depth, this.config.stencil, this.config.numSamples, this.config.useGL20);
    }

    private void updatePpi() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.app.getService().getSystemService("window")).getDefaultDisplay().getMetrics(metrics);
        this.ppiX = metrics.xdpi;
        this.ppiY = metrics.ydpi;
        this.ppcX = metrics.xdpi / 2.54f;
        this.ppcY = metrics.ydpi / 2.54f;
        this.density = metrics.density;
    }

    private void setupGL(javax.microedition.khronos.opengles.GL10 gl) {
        String renderer;
        if (this.gl10 == null && this.gl20 == null) {
            Gdx.app.log("AndroidGraphics", "GL20: " + checkGL20());
            if (this.view instanceof GLSurfaceView20LW) {
                this.gl20 = new AndroidGL20();
                this.f62gl = this.gl20;
            } else {
                this.gl10 = new AndroidGL10(gl);
                this.f62gl = this.gl10;
                if ((gl instanceof javax.microedition.khronos.opengles.GL11) && (renderer = gl.glGetString(7937)) != null && !renderer.toLowerCase().contains("pixelflinger") && !Build.MODEL.equals("MB200") && !Build.MODEL.equals("MB220") && !Build.MODEL.contains("Behold")) {
                    this.gl11 = new AndroidGL11((javax.microedition.khronos.opengles.GL11) gl);
                    this.gl10 = this.gl11;
                }
            }
            this.glu = new AndroidGLU();
            Gdx.f12gl = this.f62gl;
            Gdx.gl10 = this.gl10;
            Gdx.gl11 = this.gl11;
            Gdx.gl20 = this.gl20;
            Gdx.glu = this.glu;
            Gdx.app.log("AndroidGraphics", "OGL renderer: " + gl.glGetString(7937));
            Gdx.app.log("AndroidGraphics", "OGL vendor: " + gl.glGetString(7936));
            Gdx.app.log("AndroidGraphics", "OGL version: " + gl.glGetString(7938));
            Gdx.app.log("AndroidGraphics", "OGL extensions: " + gl.glGetString(7939));
        }
    }

    public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl, int width2, int height2) {
        this.width = width2;
        this.height = height2;
        updatePpi();
        gl.glViewport(0, 0, this.width, this.height);
        this.app.getListener().resize(width2, height2);
    }

    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl, EGLConfig config2) {
        setupGL(gl);
        logConfig(config2);
        updatePpi();
        Mesh.invalidateAllMeshes(this.app);
        Texture.invalidateAllTextures(this.app);
        ShaderProgram.invalidateAllShaderPrograms(this.app);
        FrameBuffer.invalidateAllFrameBuffers(this.app);
        Display display = ((WindowManager) this.app.getService().getSystemService("window")).getDefaultDisplay();
        this.width = display.getWidth();
        this.height = display.getHeight();
        this.mean = new WindowedMean(5);
        this.lastFrameTime = System.nanoTime();
        gl.glViewport(0, 0, this.width, this.height);
        if (!this.created) {
            this.app.getListener().create();
            this.created = true;
            synchronized (this) {
                this.running = true;
            }
        }
    }

    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        boolean lrunning;
        boolean lpause;
        boolean ldestroy;
        boolean lresume;
        long time = System.nanoTime();
        this.deltaTime = ((float) (time - this.lastFrameTime)) / 1.0E9f;
        this.lastFrameTime = time;
        this.mean.addValue(this.deltaTime);
        synchronized (this.synch) {
            lrunning = this.running;
            lpause = this.pause;
            ldestroy = this.destroy;
            lresume = this.resume;
            if (this.resume) {
                this.resume = false;
            }
            if (this.pause) {
                this.pause = false;
                this.synch.notifyAll();
            }
            if (this.destroy) {
                this.destroy = false;
                this.synch.notifyAll();
            }
        }
        if (lresume) {
            Array<LifecycleListener> listeners = this.app.lifecycleListeners;
            synchronized (listeners) {
                Iterator i$ = listeners.iterator();
                while (i$.hasNext()) {
                    i$.next().resume();
                }
            }
            this.app.getListener().resume();
            Gdx.app.log("AndroidGraphics", "resumed");
        }
        if (lrunning && !(Gdx.graphics.getGL10() == null && Gdx.graphics.getGL11() == null && Gdx.graphics.getGL20() == null)) {
            synchronized (this.app.runnables) {
                for (int i = 0; i < this.app.runnables.size; i++) {
                    this.app.runnables.get(i).run();
                }
                this.app.runnables.clear();
            }
            this.app.input.processEvents();
            this.app.getListener().render();
        }
        if (lpause) {
            Array<LifecycleListener> listeners2 = this.app.lifecycleListeners;
            synchronized (listeners2) {
                Iterator i$2 = listeners2.iterator();
                while (i$2.hasNext()) {
                    i$2.next().pause();
                }
            }
            this.app.getListener().pause();
            ((AndroidAudio) this.app.getAudio()).pause();
            Gdx.app.log("AndroidGraphics", "paused");
        }
        if (ldestroy) {
            Array<LifecycleListener> listeners3 = this.app.lifecycleListeners;
            synchronized (listeners3) {
                Iterator i$3 = listeners3.iterator();
                while (i$3.hasNext()) {
                    i$3.next().dispose();
                }
            }
            this.app.getListener().dispose();
            ((AndroidAudio) this.app.getAudio()).dispose();
            Gdx.app.log("AndroidGraphics", "destroyed");
        }
        if (time - this.frameStart > 1000000000) {
            this.fps = this.frames;
            this.frames = 0;
            this.frameStart = time;
        }
        this.frames++;
    }

    /* access modifiers changed from: protected */
    public void clearManagedCaches() {
        Mesh.clearAllMeshes(this.app);
        Texture.clearAllTextures(this.app);
        ShaderProgram.clearAllShaderPrograms(this.app);
        FrameBuffer.clearAllFrameBuffers(this.app);
        Gdx.app.log("AndroidGraphics", Mesh.getManagedStatus());
        Gdx.app.log("AndroidGraphics", Texture.getManagedStatus());
        Gdx.app.log("AndroidGraphics", ShaderProgram.getManagedStatus());
        Gdx.app.log("AndroidGraphics", FrameBuffer.getManagedStatus());
    }

    public GLBaseSurfaceViewLW getView() {
        return this.view;
    }

    public Graphics.DisplayMode[] getDisplayModes() {
        return new Graphics.DisplayMode[0];
    }

    public GLU getGLU() {
        return this.glu;
    }

    public float getDensity() {
        return this.density;
    }

    public Graphics.DisplayMode getDesktopDisplayMode() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.app.getService().getSystemService("window")).getDefaultDisplay().getMetrics(metrics);
        return new AndroidDisplayMode(metrics.widthPixels, metrics.heightPixels, 0, 0);
    }

    public boolean setDisplayMode(int width2, int height2, boolean fullscreen) {
        return false;
    }

    public void setContinuousRendering(boolean isContinuous2) {
        if (this.view != null) {
            this.isContinuous = isContinuous2;
            this.view.setRenderMode(isContinuous2 ? 1 : 0);
        }
    }

    public void requestRendering() {
        if (this.view != null) {
            this.view.requestRender();
        }
    }
}
