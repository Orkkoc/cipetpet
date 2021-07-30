package com.badlogic.gdx.backends.android.surfaceview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import java.io.Writer;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

public class GLSurfaceViewCupcake extends SurfaceView implements SurfaceHolder.Callback {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    static final Object sEglLock = new Object();
    private int mDebugFlags;
    GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
    private GLThread mGLThread;
    GLWrapper mGLWrapper;
    private boolean mHasSurface;
    private int mRenderMode;
    private GLSurfaceView.Renderer mRenderer;
    private int mSurfaceHeight;
    private int mSurfaceWidth;
    final ResolutionStrategy resolutionStrategy;

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    public GLSurfaceViewCupcake(Context context, ResolutionStrategy resolutionStrategy2) {
        super(context);
        this.resolutionStrategy = resolutionStrategy2;
        init();
    }

    public GLSurfaceViewCupcake(Context context, AttributeSet attrs, ResolutionStrategy resolutionStrategy2) {
        super(context, attrs);
        this.resolutionStrategy = resolutionStrategy2;
        init();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ResolutionStrategy.MeasuredDimension measures = this.resolutionStrategy.calcMeasures(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measures.width, measures.height);
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(2);
        this.mRenderMode = 1;
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
        if (this.mRenderer != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
        this.mRenderer = renderer;
    }

    public void setEGLConfigChooser(GLSurfaceView.EGLConfigChooser configChooser) {
        if (this.mRenderer != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
        this.mEGLConfigChooser = configChooser;
    }

    public void setEGLConfigChooser(boolean needDepth) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new SimpleEGLConfigChooser(needDepth));
    }

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
        setEGLConfigChooser((GLSurfaceView.EGLConfigChooser) new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    public void setRenderMode(int renderMode) {
        this.mRenderMode = renderMode;
        if (this.mGLThread != null) {
            this.mGLThread.setRenderMode(renderMode);
        }
    }

    public int getRenderMode() {
        return this.mRenderMode;
    }

    public void requestRender() {
        GLThread thread = this.mGLThread;
        if (thread != null) {
            thread.requestRender();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.mGLThread != null) {
            this.mGLThread.surfaceCreated();
        }
        this.mHasSurface = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mGLThread != null) {
            this.mGLThread.surfaceDestroyed();
        }
        this.mHasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.mGLThread != null) {
            this.mGLThread.onWindowResize(w, h);
        }
        this.mSurfaceWidth = w;
        this.mSurfaceHeight = h;
    }

    public void onPause() {
        this.mGLThread.onPause();
        this.mGLThread.requestExitAndWait();
        this.mGLThread = null;
    }

    public void onResume() {
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(true);
        }
        this.mGLThread = new GLThread(this.mRenderer);
        this.mGLThread.start();
        this.mGLThread.setRenderMode(this.mRenderMode);
        if (this.mHasSurface) {
            this.mGLThread.surfaceCreated();
        }
        if (this.mSurfaceWidth > 0 && this.mSurfaceHeight > 0) {
            this.mGLThread.onWindowResize(this.mSurfaceWidth, this.mSurfaceHeight);
        }
        this.mGLThread.onResume();
    }

    public void queueEvent(Runnable r) {
        if (this.mGLThread != null) {
            this.mGLThread.queueEvent(r);
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
                int distance = Math.abs(findConfigAttrib(egl, display, config, 12324, 0) - this.mRedSize) + Math.abs(findConfigAttrib(egl, display, config, 12323, 0) - this.mGreenSize) + Math.abs(findConfigAttrib(egl, display, config, 12322, 0) - this.mBlueSize) + Math.abs(findConfigAttrib(egl, display, config, 12321, 0) - this.mAlphaSize) + Math.abs(findConfigAttrib(egl, display, config, 12325, 0) - this.mDepthSize) + Math.abs(findConfigAttrib(egl, display, config, 12326, 0) - this.mStencilSize);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestConfig = config;
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
            this.mEglConfig = GLSurfaceViewCupcake.this.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
            this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL10.EGL_NO_CONTEXT, (int[]) null);
            this.mEglSurface = null;
        }

        public GL createSurface(SurfaceHolder holder) {
            if (this.mEglSurface != null) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
            }
            this.mEglSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, holder, (int[]) null);
            this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
            GL gl = this.mEglContext.getGL();
            if (GLSurfaceViewCupcake.this.mGLWrapper != null) {
                return GLSurfaceViewCupcake.this.mGLWrapper.wrap(gl);
            }
            return gl;
        }

        public boolean swap() {
            this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
            return this.mEgl.eglGetError() != 12302;
        }

        public void finish() {
            if (this.mEglSurface != null) {
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglSurface);
                this.mEglSurface = null;
            }
            if (this.mEglContext != null) {
                this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
                this.mEglContext = null;
            }
            if (this.mEglDisplay != null) {
                this.mEgl.eglTerminate(this.mEglDisplay);
                this.mEglDisplay = null;
            }
        }
    }

    class GLThread extends Thread {
        private boolean mDone = false;
        private EglHelper mEglHelper;
        private ArrayList<Runnable> mEventQueue = new ArrayList<>();
        private boolean mHasSurface;
        private int mHeight = 0;
        private boolean mPaused;
        private int mRenderMode = 1;
        private GLSurfaceView.Renderer mRenderer;
        private boolean mRequestRender = true;
        private boolean mSizeChanged;
        private int mWidth = 0;

        GLThread(GLSurfaceView.Renderer renderer) {
            this.mRenderer = renderer;
            this.mSizeChanged = true;
            setName("GLThread");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
            return;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r2 = this;
                java.lang.Object r1 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake.sEglLock     // Catch:{ InterruptedException -> 0x000b, all -> 0x000d }
                monitor-enter(r1)     // Catch:{ InterruptedException -> 0x000b, all -> 0x000d }
                r2.guardedRun()     // Catch:{ all -> 0x0008 }
                monitor-exit(r1)     // Catch:{ all -> 0x0008 }
            L_0x0007:
                return
            L_0x0008:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0008 }
                throw r0     // Catch:{ InterruptedException -> 0x000b, all -> 0x000d }
            L_0x000b:
                r0 = move-exception
                goto L_0x0007
            L_0x000d:
                r0 = move-exception
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake.GLThread.run():void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
            if (r10.mPaused == false) goto L_0x002e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
            r10.mEglHelper.finish();
            r3 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0032, code lost:
            if (needToWait() == false) goto L_0x0038;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0034, code lost:
            wait();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003a, code lost:
            if (r10.mDone == false) goto L_0x0043;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
            r0 = r10.mSizeChanged;
            r7 = r10.mWidth;
            r2 = r10.mHeight;
            r10.mSizeChanged = false;
            r10.mRequestRender = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0050, code lost:
            if (r3 == false) goto L_0x0059;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0052, code lost:
            r10.mEglHelper.start();
            r6 = true;
            r0 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0059, code lost:
            if (r0 == false) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x005b, code lost:
            r1 = (javax.microedition.khronos.opengles.GL10) r10.mEglHelper.createSurface(r10.this$0.getHolder());
            r5 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x006a, code lost:
            if (r6 == false) goto L_0x0076;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x006c, code lost:
            r10.mRenderer.onSurfaceCreated(r1, r10.mEglHelper.mEglConfig);
            r6 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0076, code lost:
            if (r5 == false) goto L_0x007e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0078, code lost:
            r10.mRenderer.onSurfaceChanged(r1, r7, r2);
            r5 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x007e, code lost:
            if (r7 <= 0) goto L_0x0011;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x0080, code lost:
            if (r2 <= 0) goto L_0x0011;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x0082, code lost:
            r10.mRenderer.onDrawFrame(r1);
            r10.mEglHelper.swap();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void guardedRun() throws java.lang.InterruptedException {
            /*
                r10 = this;
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = new com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake r9 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake.this
                r8.<init>()
                r10.mEglHelper = r8
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = r10.mEglHelper
                r8.start()
                r1 = 0
                r6 = 1
                r5 = 1
            L_0x0011:
                boolean r8 = r10.mDone
                if (r8 != 0) goto L_0x003d
                r3 = 0
                monitor-enter(r10)
            L_0x0017:
                java.lang.Runnable r4 = r10.getEvent()     // Catch:{ all -> 0x0021 }
                if (r4 == 0) goto L_0x0024
                r4.run()     // Catch:{ all -> 0x0021 }
                goto L_0x0017
            L_0x0021:
                r8 = move-exception
                monitor-exit(r10)     // Catch:{ all -> 0x0021 }
                throw r8
            L_0x0024:
                boolean r8 = r10.mPaused     // Catch:{ all -> 0x0021 }
                if (r8 == 0) goto L_0x002e
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = r10.mEglHelper     // Catch:{ all -> 0x0021 }
                r8.finish()     // Catch:{ all -> 0x0021 }
                r3 = 1
            L_0x002e:
                boolean r8 = r10.needToWait()     // Catch:{ all -> 0x0021 }
                if (r8 == 0) goto L_0x0038
                r10.wait()     // Catch:{ all -> 0x0021 }
                goto L_0x002e
            L_0x0038:
                boolean r8 = r10.mDone     // Catch:{ all -> 0x0021 }
                if (r8 == 0) goto L_0x0043
                monitor-exit(r10)     // Catch:{ all -> 0x0021 }
            L_0x003d:
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = r10.mEglHelper
                r8.finish()
                return
            L_0x0043:
                boolean r0 = r10.mSizeChanged     // Catch:{ all -> 0x0021 }
                int r7 = r10.mWidth     // Catch:{ all -> 0x0021 }
                int r2 = r10.mHeight     // Catch:{ all -> 0x0021 }
                r8 = 0
                r10.mSizeChanged = r8     // Catch:{ all -> 0x0021 }
                r8 = 0
                r10.mRequestRender = r8     // Catch:{ all -> 0x0021 }
                monitor-exit(r10)     // Catch:{ all -> 0x0021 }
                if (r3 == 0) goto L_0x0059
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = r10.mEglHelper
                r8.start()
                r6 = 1
                r0 = 1
            L_0x0059:
                if (r0 == 0) goto L_0x006a
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = r10.mEglHelper
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake r9 = com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake.this
                android.view.SurfaceHolder r9 = r9.getHolder()
                javax.microedition.khronos.opengles.GL r1 = r8.createSurface(r9)
                javax.microedition.khronos.opengles.GL10 r1 = (javax.microedition.khronos.opengles.GL10) r1
                r5 = 1
            L_0x006a:
                if (r6 == 0) goto L_0x0076
                android.opengl.GLSurfaceView$Renderer r8 = r10.mRenderer
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r9 = r10.mEglHelper
                javax.microedition.khronos.egl.EGLConfig r9 = r9.mEglConfig
                r8.onSurfaceCreated(r1, r9)
                r6 = 0
            L_0x0076:
                if (r5 == 0) goto L_0x007e
                android.opengl.GLSurfaceView$Renderer r8 = r10.mRenderer
                r8.onSurfaceChanged(r1, r7, r2)
                r5 = 0
            L_0x007e:
                if (r7 <= 0) goto L_0x0011
                if (r2 <= 0) goto L_0x0011
                android.opengl.GLSurfaceView$Renderer r8 = r10.mRenderer
                r8.onDrawFrame(r1)
                com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake$EglHelper r8 = r10.mEglHelper
                r8.swap()
                goto L_0x0011
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.backends.android.surfaceview.GLSurfaceViewCupcake.GLThread.guardedRun():void");
        }

        private boolean needToWait() {
            if (this.mDone) {
                return false;
            }
            if (this.mPaused || !this.mHasSurface) {
                return true;
            }
            if (this.mWidth <= 0 || this.mHeight <= 0 || (!this.mRequestRender && this.mRenderMode != 1)) {
                return true;
            }
            return false;
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (this) {
                this.mRenderMode = renderMode;
                if (renderMode == 1) {
                    notify();
                }
            }
        }

        public int getRenderMode() {
            int i;
            synchronized (this) {
                i = this.mRenderMode;
            }
            return i;
        }

        public void requestRender() {
            synchronized (this) {
                this.mRequestRender = true;
                notify();
            }
        }

        public void surfaceCreated() {
            synchronized (this) {
                this.mHasSurface = true;
                notify();
            }
        }

        public void surfaceDestroyed() {
            synchronized (this) {
                this.mHasSurface = false;
                notify();
            }
        }

        public void onPause() {
            synchronized (this) {
                this.mPaused = true;
            }
        }

        public void onResume() {
            synchronized (this) {
                this.mPaused = false;
                notify();
            }
        }

        public void onWindowResize(int w, int h) {
            synchronized (this) {
                this.mWidth = w;
                this.mHeight = h;
                this.mSizeChanged = true;
                notify();
            }
        }

        public void requestExitAndWait() {
            synchronized (this) {
                this.mDone = true;
                notify();
            }
            try {
                join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void queueEvent(Runnable r) {
            synchronized (this) {
                this.mEventQueue.add(r);
            }
        }

        private Runnable getEvent() {
            synchronized (this) {
                if (this.mEventQueue.size() <= 0) {
                    return null;
                }
                Runnable remove = this.mEventQueue.remove(0);
                return remove;
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
}
