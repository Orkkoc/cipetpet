package com.badlogic.gdx;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.GLU;

public interface Graphics {

    public enum GraphicsType {
        AndroidGL,
        LWJGL,
        Angle,
        WebGL,
        iOSGL
    }

    BufferFormat getBufferFormat();

    float getDeltaTime();

    float getDensity();

    DisplayMode getDesktopDisplayMode();

    DisplayMode[] getDisplayModes();

    int getFramesPerSecond();

    GL10 getGL10();

    GL11 getGL11();

    GL20 getGL20();

    GLCommon getGLCommon();

    GLU getGLU();

    int getHeight();

    float getPpcX();

    float getPpcY();

    float getPpiX();

    float getPpiY();

    float getRawDeltaTime();

    GraphicsType getType();

    int getWidth();

    boolean isContinuousRendering();

    boolean isFullscreen();

    boolean isGL11Available();

    boolean isGL20Available();

    void requestRendering();

    void setContinuousRendering(boolean z);

    boolean setDisplayMode(int i, int i2, boolean z);

    boolean setDisplayMode(DisplayMode displayMode);

    void setTitle(String str);

    void setVSync(boolean z);

    boolean supportsDisplayModeChange();

    boolean supportsExtension(String str);

    public static class DisplayMode {
        public final int bitsPerPixel;
        public final int height;
        public final int refreshRate;
        public final int width;

        protected DisplayMode(int width2, int height2, int refreshRate2, int bitsPerPixel2) {
            this.width = width2;
            this.height = height2;
            this.refreshRate = refreshRate2;
            this.bitsPerPixel = bitsPerPixel2;
        }

        public String toString() {
            return this.width + "x" + this.height + ", bpp: " + this.bitsPerPixel + ", hz: " + this.refreshRate;
        }
    }

    public static class BufferFormat {

        /* renamed from: a */
        public final int f13a;

        /* renamed from: b */
        public final int f14b;
        public final boolean coverageSampling;
        public final int depth;

        /* renamed from: g */
        public final int f15g;

        /* renamed from: r */
        public final int f16r;
        public final int samples;
        public final int stencil;

        public BufferFormat(int r, int g, int b, int a, int depth2, int stencil2, int samples2, boolean coverageSampling2) {
            this.f16r = r;
            this.f15g = g;
            this.f14b = b;
            this.f13a = a;
            this.depth = depth2;
            this.stencil = stencil2;
            this.samples = samples2;
            this.coverageSampling = coverageSampling2;
        }

        public String toString() {
            return "r: " + this.f16r + ", g: " + this.f15g + ", b: " + this.f14b + ", a: " + this.f13a + ", depth: " + this.depth + ", stencil: " + this.stencil + ", num samples: " + this.samples + ", coverage sampling: " + this.coverageSampling;
        }
    }
}
