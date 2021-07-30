package com.badlogic.gdx.backends.android;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake;
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
import java.lang.reflect.Method;
import java.util.Iterator;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public final class AndroidGraphicsDaydream implements Graphics, GLSurfaceView.Renderer {
    AndroidDaydream app;
    private Graphics.BufferFormat bufferFormat = new Graphics.BufferFormat(5, 6, 5, 0, 16, 0, 0, false);
    private final AndroidApplicationConfiguration config;
    volatile boolean created = false;
    private float deltaTime = 0.0f;
    private float density = 1.0f;
    volatile boolean destroy = false;
    EGLContext eglContext;
    String extensions;
    private int fps;
    private long frameStart = System.nanoTime();
    private int frames = 0;

    /* renamed from: gl */
    GLCommon f61gl;
    GL10 gl10;
    GL11 gl11;
    GL20 gl20;
    GLU glu;
    int height;
    private boolean isContinuous = true;
    private long lastFrameTime = System.nanoTime();
    private WindowedMean mean = new WindowedMean(5);
    volatile boolean pause = false;
    private float ppcX = 0.0f;
    private float ppcY = 0.0f;
    private float ppiX = 0.0f;
    private float ppiY = 0.0f;
    volatile boolean resume = false;
    volatile boolean running = false;
    Object synch = new Object();
    int[] value = new int[1];
    final View view;
    int width;

    public AndroidGraphicsDaydream(AndroidDaydream daydream, AndroidApplicationConfiguration config2, ResolutionStrategy resolutionStrategy) {
        this.config = config2;
        this.view = createGLSurfaceView(daydream, config2.useGL20, resolutionStrategy);
        setPreserveContext(this.view);
        this.view.setFocusable(true);
        this.view.setFocusableInTouchMode(true);
        this.app = daydream;
    }

    private void setPreserveContext(View view2) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 11 && (view2 instanceof GLSurfaceView20)) {
            Method method = null;
            try {
                Method[] arr$ = view2.getClass().getMethods();
                int len$ = arr$.length;
                int i$ = 0;
                while (true) {
                    if (i$ >= len$) {
                        break;
                    }
                    Method m = arr$[i$];
                    if (m.getName().equals("setPreserveEGLContextOnPause")) {
                        method = m;
                        break;
                    }
                    i$++;
                }
                if (method != null) {
                    method.invoke((GLSurfaceView20) view2, new Object[]{true});
                }
            } catch (Exception e) {
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake} */
    /* JADX WARNING: type inference failed for: r0v1, types: [android.opengl.GLSurfaceView, com.badlogic.gdx.backends.android.AndroidGraphicsDaydream$1] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.View createGLSurfaceView(android.service.dreams.DreamService r10, boolean r11, final com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy r12) {
        /*
            r9 = this;
            android.opengl.GLSurfaceView$EGLConfigChooser r7 = r9.getEglConfigChooser()
            if (r11 == 0) goto L_0x0036
            boolean r1 = r9.checkGL20()
            if (r1 == 0) goto L_0x0036
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20 r0 = new com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20
            r0.<init>(r10, r12)
            if (r7 == 0) goto L_0x001a
            r0.setEGLConfigChooser(r7)
        L_0x0016:
            r0.setRenderer(r9)
        L_0x0019:
            return r0
        L_0x001a:
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r1 = r9.config
            int r1 = r1.f57r
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r2 = r9.config
            int r2 = r2.f56g
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r3 = r9.config
            int r3 = r3.f55b
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r4 = r9.config
            int r4 = r4.f54a
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r5 = r9.config
            int r5 = r5.depth
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r6 = r9.config
            int r6 = r6.stencil
            r0.setEGLConfigChooser(r1, r2, r3, r4, r5, r6)
            goto L_0x0016
        L_0x0036:
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r1 = r9.config
            r2 = 0
            r1.useGL20 = r2
            android.opengl.GLSurfaceView$EGLConfigChooser r7 = r9.getEglConfigChooser()
            java.lang.String r1 = android.os.Build.VERSION.SDK
            int r8 = java.lang.Integer.parseInt(r1)
            r1 = 11
            if (r8 < r1) goto L_0x0073
            com.badlogic.gdx.backends.android.AndroidGraphicsDaydream$1 r0 = new com.badlogic.gdx.backends.android.AndroidGraphicsDaydream$1
            r0.<init>(r10, r12)
            if (r7 == 0) goto L_0x0057
            r0.setEGLConfigChooser(r7)
        L_0x0053:
            r0.setRenderer(r9)
            goto L_0x0019
        L_0x0057:
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r1 = r9.config
            int r1 = r1.f57r
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r2 = r9.config
            int r2 = r2.f56g
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r3 = r9.config
            int r3 = r3.f55b
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r4 = r9.config
            int r4 = r4.f54a
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r5 = r9.config
            int r5 = r5.depth
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r6 = r9.config
            int r6 = r6.stencil
            r0.setEGLConfigChooser(r1, r2, r3, r4, r5, r6)
            goto L_0x0053
        L_0x0073:
            com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake r0 = new com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake
            r0.<init>(r10, r12)
            if (r7 == 0) goto L_0x0081
            r0.setEGLConfigChooser((android.opengl.GLSurfaceView.EGLConfigChooser) r7)
        L_0x007d:
            r0.setRenderer(r9)
            goto L_0x0019
        L_0x0081:
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r1 = r9.config
            int r1 = r1.f57r
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r2 = r9.config
            int r2 = r2.f56g
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r3 = r9.config
            int r3 = r3.f55b
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r4 = r9.config
            int r4 = r4.f54a
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r5 = r9.config
            int r5 = r5.depth
            com.badlogic.gdx.backends.android.AndroidApplicationConfiguration r6 = r9.config
            int r6 = r6.stencil
            r0.setEGLConfigChooser(r1, r2, r3, r4, r5, r6)
            goto L_0x007d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidGraphicsDaydream.createGLSurfaceView(android.service.dreams.DreamService, boolean, com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy):android.view.View");
    }

    private GLSurfaceView.EGLConfigChooser getEglConfigChooser() {
        return new GdxEglConfigChooser(this.config.f57r, this.config.f56g, this.config.f55b, this.config.f54a, this.config.depth, this.config.stencil, this.config.numSamples, this.config.useGL20);
    }

    private void updatePpi() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.app.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.ppiX = metrics.xdpi;
        this.ppiY = metrics.ydpi;
        this.ppcX = metrics.xdpi / 2.54f;
        this.ppcY = metrics.ydpi / 2.54f;
        this.density = metrics.density;
    }

    private boolean checkGL20() {
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

    private static boolean isPowerOfTwo(int value2) {
        return value2 != 0 && ((value2 + -1) & value2) == 0;
    }

    private void setupGL(javax.microedition.khronos.opengles.GL10 gl) {
        String renderer;
        if (this.gl10 == null && this.gl20 == null) {
            if (this.view instanceof GLSurfaceView20) {
                this.gl20 = new AndroidGL20();
                this.f61gl = this.gl20;
            } else {
                this.gl10 = new AndroidGL10(gl);
                this.f61gl = this.gl10;
                if ((gl instanceof javax.microedition.khronos.opengles.GL11) && (renderer = gl.glGetString(7937)) != null && !renderer.toLowerCase().contains("pixelflinger") && !Build.MODEL.equals("MB200") && !Build.MODEL.equals("MB220") && !Build.MODEL.contains("Behold")) {
                    this.gl11 = new AndroidGL11((javax.microedition.khronos.opengles.GL11) gl);
                    this.gl10 = this.gl11;
                }
            }
            this.glu = new AndroidGLU();
            Gdx.f12gl = this.f61gl;
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
        if (!this.created) {
            this.app.listener.create();
            this.created = true;
            synchronized (this) {
                this.running = true;
            }
        }
        this.app.listener.resize(width2, height2);
    }

    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl, EGLConfig config2) {
        this.eglContext = ((EGL10) EGLContext.getEGL()).eglGetCurrentContext();
        setupGL(gl);
        logConfig(config2);
        updatePpi();
        Mesh.invalidateAllMeshes(this.app);
        Texture.invalidateAllTextures(this.app);
        ShaderProgram.invalidateAllShaderPrograms(this.app);
        FrameBuffer.invalidateAllFrameBuffers(this.app);
        Gdx.app.log("AndroidGraphics", Mesh.getManagedStatus());
        Gdx.app.log("AndroidGraphics", Texture.getManagedStatus());
        Gdx.app.log("AndroidGraphics", ShaderProgram.getManagedStatus());
        Gdx.app.log("AndroidGraphics", FrameBuffer.getManagedStatus());
        Display display = this.app.getWindowManager().getDefaultDisplay();
        this.width = display.getWidth();
        this.height = display.getHeight();
        this.mean = new WindowedMean(5);
        this.lastFrameTime = System.nanoTime();
        gl.glViewport(0, 0, this.width, this.height);
    }

    private void logConfig(EGLConfig config2) {
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
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.AndroidGraphicsDaydream.pause():void");
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
            this.app.audio.resume();
            this.app.listener.resume();
            Gdx.app.log("AndroidGraphics", "resumed");
        }
        if (lrunning) {
            synchronized (this.app.runnables) {
                this.app.executedRunnables.clear();
                this.app.executedRunnables.addAll((Array) this.app.runnables);
                this.app.runnables.clear();
                for (int i = 0; i < this.app.executedRunnables.size; i++) {
                    try {
                        this.app.executedRunnables.get(i).run();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
            this.app.input.processEvents();
            this.app.listener.render();
        }
        if (lpause) {
            Array<LifecycleListener> listeners2 = this.app.lifecycleListeners;
            synchronized (listeners2) {
                Iterator i$2 = listeners2.iterator();
                while (i$2.hasNext()) {
                    i$2.next().pause();
                }
            }
            this.app.listener.pause();
            this.app.audio.pause();
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
            this.app.listener.dispose();
            this.app.audio.dispose();
            this.app.audio = null;
            Gdx.app.log("AndroidGraphics", "destroyed");
        }
        if (time - this.frameStart > 1000000000) {
            this.fps = this.frames;
            this.frames = 0;
            this.frameStart = time;
        }
        this.frames++;
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

    public View getView() {
        return this.view;
    }

    public GLCommon getGLCommon() {
        return this.f61gl;
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

    public float getDensity() {
        return this.density;
    }

    public GLU getGLU() {
        return this.glu;
    }

    public boolean supportsDisplayModeChange() {
        return false;
    }

    public boolean setDisplayMode(Graphics.DisplayMode displayMode) {
        return false;
    }

    public Graphics.DisplayMode[] getDisplayModes() {
        return new Graphics.DisplayMode[]{getDesktopDisplayMode()};
    }

    public boolean setDisplayMode(int width2, int height2, boolean fullscreen) {
        return false;
    }

    public void setTitle(String title) {
    }

    private class AndroidDisplayMode extends Graphics.DisplayMode {
        protected AndroidDisplayMode(int width, int height, int refreshRate, int bitsPerPixel) {
            super(width, height, refreshRate, bitsPerPixel);
        }
    }

    public Graphics.DisplayMode getDesktopDisplayMode() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.app.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new AndroidDisplayMode(metrics.widthPixels, metrics.heightPixels, 0, 0);
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

    public void setContinuousRendering(boolean isContinuous2) {
        if (this.view != null) {
            this.isContinuous = isContinuous2;
            int renderMode = isContinuous2 ? 1 : 0;
            if (this.view instanceof GLSurfaceViewCupcake) {
                ((GLSurfaceViewCupcake) this.view).setRenderMode(renderMode);
            }
            if (this.view instanceof GLSurfaceView) {
                ((GLSurfaceView) this.view).setRenderMode(renderMode);
            }
            this.mean.clear();
        }
    }

    public boolean isContinuousRendering() {
        return this.isContinuous;
    }

    public void requestRendering() {
        if (this.view != null) {
            if (this.view instanceof GLSurfaceViewCupcake) {
                ((GLSurfaceViewCupcake) this.view).requestRender();
            }
            if (this.view instanceof GLSurfaceView) {
                ((GLSurfaceView) this.view).requestRender();
            }
        }
    }

    public boolean isFullscreen() {
        return true;
    }
}
