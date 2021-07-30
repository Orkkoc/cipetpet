package com.badlogic.gdx.backends.android.surfaceview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.Writer;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

public class GLBaseSurfaceView extends GLSurfaceView implements SurfaceHolder.Callback {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean DRAW_TWICE_AFTER_SIZE_CHANGED = true;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    static final GLThreadManager sGLThreadManager = new GLThreadManager();
    int mDebugFlags;
    GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
    EGLContextFactory mEGLContextFactory;
    EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    GLWrapper mGLWrapper;
    boolean mSizeChanged = DRAW_TWICE_AFTER_SIZE_CHANGED;

    public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    public interface EGLWindowSurfaceFactory {
        EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void destroySurface(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    public GLBaseSurfaceView(Context context) {
        super(context);
        init();
    }

    public GLBaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
    }

    public void setGLWrapper(GLWrapper glWrapper) {
        this.mGLWrapper = glWrapper;
    }

    public void setDebugFlags(int debugFlags) {
        this.mDebugFlags = debugFlags;
    }

    public int getDebugFlags() {
        return this.mDebugFlags;
    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(DRAW_TWICE_AFTER_SIZE_CHANGED);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory();
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.mGLThread = new GLThread(renderer);
        this.mGLThread.start();
    }

    public void setEGLContextFactory(EGLContextFactory factory) {
        checkRenderThreadState();
        this.mEGLContextFactory = factory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = factory;
    }

    public void setEGLConfigChooser(GLSurfaceView.EGLConfigChooser configChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = configChooser;
    }

    public void setEGLConfigChooser(boolean needDepth) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new SimpleEGLConfigChooser(needDepth));
    }

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    public void setRenderMode(int renderMode) {
        this.mGLThread.setRenderMode(renderMode);
    }

    public int getRenderMode() {
        return this.mGLThread.getRenderMode();
    }

    public void requestRender() {
        this.mGLThread.requestRender();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mGLThread.surfaceCreated();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mGLThread.surfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        this.mGLThread.onWindowResize(w, h);
    }

    public void onPause() {
        this.mGLThread.onPause();
    }

    public void onResume() {
        this.mGLThread.onResume();
    }

    public void queueEvent(Runnable r) {
        this.mGLThread.queueEvent(r);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mGLThread.requestExitAndWait();
    }

    static class DefaultContextFactory implements EGLContextFactory {
        DefaultContextFactory() {
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, (int[]) null);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        DefaultWindowSurfaceFactory() {
        }

        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            return egl.eglCreateWindowSurface(display, config, nativeWindow, (int[]) null);
        }

        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    private static abstract class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {
        protected int[] mConfigSpec;

        /* access modifiers changed from: package-private */
        public abstract EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public BaseConfigChooser(int[] configSpec) {
            this.mConfigSpec = configSpec;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, this.mConfigSpec, (EGLConfig[]) null, 0, num_config);
            int numConfigs = num_config[0];
            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, this.mConfigSpec, configs, numConfigs, num_config);
            EGLConfig config = chooseConfig(egl, display, configs);
            if (config != null) {
                return config;
            }
            throw new IllegalArgumentException("No config chosen");
        }
    }

    private static class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

        public ComponentSizeChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
            super(new int[]{12324, redSize, 12323, greenSize, 12322, blueSize, 12321, alphaSize, 12325, depthSize, 12326, stencilSize, 12344});
            this.mRedSize = redSize;
            this.mGreenSize = greenSize;
            this.mBlueSize = blueSize;
            this.mAlphaSize = alphaSize;
            this.mDepthSize = depthSize;
            this.mStencilSize = stencilSize;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            EGLConfig closestConfig = null;
            int closestDistance = 1000;
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, 12325, 0);
                int s = findConfigAttrib(egl, display, config, 12326, 0);
                if (d >= this.mDepthSize && s >= this.mStencilSize) {
                    int distance = Math.abs(findConfigAttrib(egl, display, config, 12324, 0) - this.mRedSize) + Math.abs(findConfigAttrib(egl, display, config, 12323, 0) - this.mGreenSize) + Math.abs(findConfigAttrib(egl, display, config, 12322, 0) - this.mBlueSize) + Math.abs(findConfigAttrib(egl, display, config, 12321, 0) - this.mAlphaSize);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestConfig = config;
                    }
                }
            }
            return closestConfig;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
                return this.mValue[0];
            }
            return defaultValue;
        }
    }

    private static class SimpleEGLConfigChooser extends ComponentSizeChooser {
        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public SimpleEGLConfigChooser(boolean withDepthBuffer) {
            super(4, 4, 4, 0, withDepthBuffer ? 16 : 0, 0);
            this.mRedSize = 5;
            this.mGreenSize = 6;
            this.mBlueSize = 5;
        }
    }

    private class EglHelper {
        EGL10 mEgl;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;

        public EglHelper() {
        }

        public void start() {
            this.mEgl = (EGL10) EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.mEgl.eglInitialize(this.mEglDisplay, new int[2]);
            this.mEglConfig = GLBaseSurfaceView.this.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
            this.mEglContext = GLBaseSurfaceView.this.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
            if (this.mEglContext == null || this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                throw new RuntimeException("createContext failed");
            }
            this.mEglSurface = null;
        }

        public GL createSurface(SurfaceHolder holder) {
            if (!(this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE)) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLBaseSurfaceView.this.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
            }
            this.mEglSurface = GLBaseSurfaceView.this.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, holder);
            if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                throwEglException("createWindowSurface");
            }
            if (!this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                throwEglException("eglMakeCurrent");
            }
            GL gl = this.mEglContext.getGL();
            if (GLBaseSurfaceView.this.mGLWrapper != null) {
                gl = GLBaseSurfaceView.this.mGLWrapper.wrap(gl);
            }
            if ((GLBaseSurfaceView.this.mDebugFlags & 3) == 0) {
                return gl;
            }
            int configFlags = 0;
            Writer log = null;
            if ((GLBaseSurfaceView.this.mDebugFlags & 1) != 0) {
                configFlags = 0 | 1;
            }
            if ((GLBaseSurfaceView.this.mDebugFlags & 2) != 0) {
                log = new LogWriter();
            }
            return GLDebugHelper.wrap(gl, configFlags, log);
        }

        public boolean swap() {
            this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
            if (this.mEgl.eglGetError() != 12302) {
                return GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
            }
            return false;
        }

        public void destroySurface() {
            if (this.mEglSurface != null && this.mEglSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                GLBaseSurfaceView.this.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
                this.mEglSurface = null;
            }
        }

        public void finish() {
            if (this.mEglContext != null) {
                GLBaseSurfaceView.this.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                this.mEglContext = null;
            }
            if (this.mEglDisplay != null) {
                this.mEgl.eglTerminate(this.mEglDisplay);
                this.mEglDisplay = null;
            }
        }

        private void throwEglException(String function) {
            throw new RuntimeException(function + " failed: " + this.mEgl.eglGetError());
        }
    }

    class GLThread extends Thread {
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue = new ArrayList<>();
        boolean mExited;
        private boolean mHasSurface;
        private boolean mHaveEgl;
        private int mHeight = 0;
        private boolean mPaused;
        private boolean mRenderComplete;
        private int mRenderMode = 1;
        private GLSurfaceView.Renderer mRenderer;
        private boolean mRequestRender = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
        private boolean mShouldExit;
        private boolean mWaitingForSurface;
        private int mWidth = 0;

        GLThread(GLSurfaceView.Renderer renderer) {
            this.mRenderer = renderer;
        }

        public void run() {
            setName("GLThread " + getId());
            try {
                guardedRun();
            } catch (InterruptedException e) {
            } finally {
                GLBaseSurfaceView.sGLThreadManager.threadExiting(this);
            }
        }

        private void stopEglLocked() {
            if (this.mHaveEgl) {
                this.mHaveEgl = false;
                this.mEglHelper.destroySurface();
                this.mEglHelper.finish();
                GLBaseSurfaceView.sGLThreadManager.releaseEglSurfaceLocked(this);
            }
        }

        /* JADX INFO: finally extract failed */
        /* JADX WARNING: type inference failed for: r9v47, types: [javax.microedition.khronos.opengles.GL] */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0039, code lost:
            if (r3 == null) goto L_0x00e4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            r3.run();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x003e, code lost:
            r3 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x00e4, code lost:
            if (r1 == false) goto L_0x0100;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
            r4 = r13.mEglHelper.createSurface(r13.this$0.getHolder());
            r13.mRenderer.onSurfaceCreated(r4, r13.mEglHelper.mEglConfig);
            r1 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:0x0100, code lost:
            if (r6 == false) goto L_0x0108;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:89:0x0102, code lost:
            r13.mRenderer.onSurfaceChanged(r4, r7, r5);
            r6 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:90:0x0108, code lost:
            r13.mRenderer.onDrawFrame(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x0113, code lost:
            if (r13.mEglHelper.swap() != false) goto L_0x0115;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:92:0x0115, code lost:
            if (r8 == false) goto L_0x0012;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x0117, code lost:
            r2 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void guardedRun() throws java.lang.InterruptedException {
            /*
                r13 = this;
                r12 = 1
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$EglHelper r9 = new com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$EglHelper
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView r10 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.this
                r9.<init>()
                r13.mEglHelper = r9
                r4 = 0
                r1 = 0
                r6 = 0
                r8 = 0
                r2 = 0
                r7 = 0
                r5 = 0
                r3 = 0
            L_0x0012:
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r10 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00d0 }
                monitor-enter(r10)     // Catch:{ all -> 0x00d0 }
            L_0x0015:
                boolean r9 = r13.mShouldExit     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x0025
                monitor-exit(r10)     // Catch:{ all -> 0x00cd }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r10 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager
                monitor-enter(r10)
                r13.stopEglLocked()     // Catch:{ all -> 0x0022 }
                monitor-exit(r10)     // Catch:{ all -> 0x0022 }
                return
            L_0x0022:
                r9 = move-exception
                monitor-exit(r10)     // Catch:{ all -> 0x0022 }
                throw r9
            L_0x0025:
                java.util.ArrayList<java.lang.Runnable> r9 = r13.mEventQueue     // Catch:{ all -> 0x00cd }
                boolean r9 = r9.isEmpty()     // Catch:{ all -> 0x00cd }
                if (r9 != 0) goto L_0x0040
                java.util.ArrayList<java.lang.Runnable> r9 = r13.mEventQueue     // Catch:{ all -> 0x00cd }
                r11 = 0
                java.lang.Object r9 = r9.remove(r11)     // Catch:{ all -> 0x00cd }
                r0 = r9
                java.lang.Runnable r0 = (java.lang.Runnable) r0     // Catch:{ all -> 0x00cd }
                r3 = r0
            L_0x0038:
                monitor-exit(r10)     // Catch:{ all -> 0x00cd }
                if (r3 == 0) goto L_0x00e4
                r3.run()     // Catch:{ all -> 0x00d0 }
                r3 = 0
                goto L_0x0012
            L_0x0040:
                boolean r9 = r13.mHaveEgl     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x004b
                boolean r9 = r13.mPaused     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x004b
                r13.stopEglLocked()     // Catch:{ all -> 0x00cd }
            L_0x004b:
                boolean r9 = r13.mHasSurface     // Catch:{ all -> 0x00cd }
                if (r9 != 0) goto L_0x0062
                boolean r9 = r13.mWaitingForSurface     // Catch:{ all -> 0x00cd }
                if (r9 != 0) goto L_0x0062
                boolean r9 = r13.mHaveEgl     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x005a
                r13.stopEglLocked()     // Catch:{ all -> 0x00cd }
            L_0x005a:
                r9 = 1
                r13.mWaitingForSurface = r9     // Catch:{ all -> 0x00cd }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                r9.notifyAll()     // Catch:{ all -> 0x00cd }
            L_0x0062:
                boolean r9 = r13.mHasSurface     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x0072
                boolean r9 = r13.mWaitingForSurface     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x0072
                r9 = 0
                r13.mWaitingForSurface = r9     // Catch:{ all -> 0x00cd }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                r9.notifyAll()     // Catch:{ all -> 0x00cd }
            L_0x0072:
                if (r2 == 0) goto L_0x007e
                r8 = 0
                r2 = 0
                r9 = 1
                r13.mRenderComplete = r9     // Catch:{ all -> 0x00cd }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                r9.notifyAll()     // Catch:{ all -> 0x00cd }
            L_0x007e:
                boolean r9 = r13.mPaused     // Catch:{ all -> 0x00cd }
                if (r9 != 0) goto L_0x00dd
                boolean r9 = r13.mHasSurface     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x00dd
                int r9 = r13.mWidth     // Catch:{ all -> 0x00cd }
                if (r9 <= 0) goto L_0x00dd
                int r9 = r13.mHeight     // Catch:{ all -> 0x00cd }
                if (r9 <= 0) goto L_0x00dd
                boolean r9 = r13.mRequestRender     // Catch:{ all -> 0x00cd }
                if (r9 != 0) goto L_0x0096
                int r9 = r13.mRenderMode     // Catch:{ all -> 0x00cd }
                if (r9 != r12) goto L_0x00dd
            L_0x0096:
                boolean r9 = r13.mHaveEgl     // Catch:{ all -> 0x00cd }
                if (r9 != 0) goto L_0x00b1
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                boolean r9 = r9.tryAcquireEglSurfaceLocked(r13)     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x00b1
                r9 = 1
                r13.mHaveEgl = r9     // Catch:{ all -> 0x00cd }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$EglHelper r9 = r13.mEglHelper     // Catch:{ all -> 0x00cd }
                r9.start()     // Catch:{ all -> 0x00cd }
                r1 = 1
                r6 = 1
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                r9.notifyAll()     // Catch:{ all -> 0x00cd }
            L_0x00b1:
                boolean r9 = r13.mHaveEgl     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x00dd
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.this     // Catch:{ all -> 0x00cd }
                boolean r9 = r9.mSizeChanged     // Catch:{ all -> 0x00cd }
                if (r9 == 0) goto L_0x00d9
                r6 = 1
                int r7 = r13.mWidth     // Catch:{ all -> 0x00cd }
                int r5 = r13.mHeight     // Catch:{ all -> 0x00cd }
                r8 = 1
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.this     // Catch:{ all -> 0x00cd }
                r11 = 0
                r9.mSizeChanged = r11     // Catch:{ all -> 0x00cd }
            L_0x00c6:
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                r9.notifyAll()     // Catch:{ all -> 0x00cd }
                goto L_0x0038
            L_0x00cd:
                r9 = move-exception
                monitor-exit(r10)     // Catch:{ all -> 0x00cd }
                throw r9     // Catch:{ all -> 0x00d0 }
            L_0x00d0:
                r9 = move-exception
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r10 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager
                monitor-enter(r10)
                r13.stopEglLocked()     // Catch:{ all -> 0x011a }
                monitor-exit(r10)     // Catch:{ all -> 0x011a }
                throw r9
            L_0x00d9:
                r9 = 0
                r13.mRequestRender = r9     // Catch:{ all -> 0x00cd }
                goto L_0x00c6
            L_0x00dd:
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$GLThreadManager r9 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.sGLThreadManager     // Catch:{ all -> 0x00cd }
                r9.wait()     // Catch:{ all -> 0x00cd }
                goto L_0x0015
            L_0x00e4:
                if (r1 == 0) goto L_0x0100
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$EglHelper r9 = r13.mEglHelper     // Catch:{ all -> 0x00d0 }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView r10 = com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.this     // Catch:{ all -> 0x00d0 }
                android.view.SurfaceHolder r10 = r10.getHolder()     // Catch:{ all -> 0x00d0 }
                javax.microedition.khronos.opengles.GL r9 = r9.createSurface(r10)     // Catch:{ all -> 0x00d0 }
                r0 = r9
                javax.microedition.khronos.opengles.GL10 r0 = (javax.microedition.khronos.opengles.GL10) r0     // Catch:{ all -> 0x00d0 }
                r4 = r0
                android.opengl.GLSurfaceView$Renderer r9 = r13.mRenderer     // Catch:{ all -> 0x00d0 }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$EglHelper r10 = r13.mEglHelper     // Catch:{ all -> 0x00d0 }
                javax.microedition.khronos.egl.EGLConfig r10 = r10.mEglConfig     // Catch:{ all -> 0x00d0 }
                r9.onSurfaceCreated(r4, r10)     // Catch:{ all -> 0x00d0 }
                r1 = 0
            L_0x0100:
                if (r6 == 0) goto L_0x0108
                android.opengl.GLSurfaceView$Renderer r9 = r13.mRenderer     // Catch:{ all -> 0x00d0 }
                r9.onSurfaceChanged(r4, r7, r5)     // Catch:{ all -> 0x00d0 }
                r6 = 0
            L_0x0108:
                android.opengl.GLSurfaceView$Renderer r9 = r13.mRenderer     // Catch:{ all -> 0x00d0 }
                r9.onDrawFrame(r4)     // Catch:{ all -> 0x00d0 }
                com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView$EglHelper r9 = r13.mEglHelper     // Catch:{ all -> 0x00d0 }
                boolean r9 = r9.swap()     // Catch:{ all -> 0x00d0 }
                if (r9 != 0) goto L_0x0115
            L_0x0115:
                if (r8 == 0) goto L_0x0012
                r2 = 1
                goto L_0x0012
            L_0x011a:
                r9 = move-exception
                monitor-exit(r10)     // Catch:{ all -> 0x011a }
                throw r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceView.GLThread.guardedRun():void");
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mRenderMode = renderMode;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public int getRenderMode() {
            int i;
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                i = this.mRenderMode;
            }
            return i;
        }

        public void requestRender() {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mRequestRender = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void surfaceCreated() {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mHasSurface = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void surfaceDestroyed() {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mHasSurface = false;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && !this.mExited) {
                    try {
                        GLBaseSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mPaused = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void onResume() {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mPaused = false;
                this.mRequestRender = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void onWindowResize(int w, int h) {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mWidth = w;
                this.mHeight = h;
                GLBaseSurfaceView.this.mSizeChanged = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                this.mRequestRender = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                this.mRenderComplete = false;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused && !this.mRenderComplete) {
                    try {
                        GLBaseSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestExitAndWait() {
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mShouldExit = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        GLBaseSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void queueEvent(Runnable r) {
            if (r == null) {
                throw new IllegalArgumentException("r must not be null");
            }
            synchronized (GLBaseSurfaceView.sGLThreadManager) {
                this.mEventQueue.add(r);
                GLBaseSurfaceView.sGLThreadManager.notifyAll();
            }
        }
    }

    static class LogWriter extends Writer {
        private StringBuilder mBuilder = new StringBuilder();

        LogWriter() {
        }

        public void close() {
            flushBuilder();
        }

        public void flush() {
            flushBuilder();
        }

        public void write(char[] buf, int offset, int count) {
            for (int i = 0; i < count; i++) {
                char c = buf[offset + i];
                if (c == 10) {
                    flushBuilder();
                } else {
                    this.mBuilder.append(c);
                }
            }
        }

        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                Log.v("GLSurfaceView", this.mBuilder.toString());
                this.mBuilder.delete(0, this.mBuilder.length());
            }
        }
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    static class GLThreadManager {
        private GLThread mEglOwner;

        GLThreadManager() {
        }

        public synchronized void threadExiting(GLThread thread) {
            thread.mExited = GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }

        public boolean tryAcquireEglSurfaceLocked(GLThread thread) {
            if (this.mEglOwner != thread && this.mEglOwner != null) {
                return false;
            }
            this.mEglOwner = thread;
            notifyAll();
            return GLBaseSurfaceView.DRAW_TWICE_AFTER_SIZE_CHANGED;
        }

        public void releaseEglSurfaceLocked(GLThread thread) {
            if (this.mEglOwner == thread) {
                this.mEglOwner = null;
            }
            notifyAll();
        }
    }
}
