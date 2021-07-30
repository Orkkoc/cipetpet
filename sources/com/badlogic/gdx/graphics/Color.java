package com.badlogic.gdx.graphics;

import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.utils.NumberUtils;

public class Color {
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Color CLEAR = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f, 1.0f);
    public static final Color DARK_GRAY = new Color(0.25f, 0.25f, 0.25f, 1.0f);
    public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color LIGHT_GRAY = new Color(0.75f, 0.75f, 0.75f, 1.0f);
    public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f, 1.0f);
    public static final Color ORANGE = new Color(1.0f, 0.78f, 0.0f, 1.0f);
    public static final Color PINK = new Color(1.0f, 0.68f, 0.68f, 1.0f);
    public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    public static Color tmp = new Color();

    /* renamed from: a */
    public float f67a;

    /* renamed from: b */
    public float f68b;

    /* renamed from: g */
    public float f69g;

    /* renamed from: r */
    public float f70r;

    public Color() {
    }

    public Color(float r, float g, float b, float a) {
        this.f70r = r;
        this.f69g = g;
        this.f68b = b;
        this.f67a = a;
        clamp();
    }

    public Color(Color color) {
        set(color);
    }

    public Color set(Color color) {
        this.f70r = color.f70r;
        this.f69g = color.f69g;
        this.f68b = color.f68b;
        this.f67a = color.f67a;
        clamp();
        return this;
    }

    public Color mul(Color color) {
        this.f70r *= color.f70r;
        this.f69g *= color.f69g;
        this.f68b *= color.f68b;
        this.f67a *= color.f67a;
        clamp();
        return this;
    }

    public Color mul(float value) {
        this.f70r *= value;
        this.f69g *= value;
        this.f68b *= value;
        this.f67a *= value;
        clamp();
        return this;
    }

    public Color add(Color color) {
        this.f70r += color.f70r;
        this.f69g += color.f69g;
        this.f68b += color.f68b;
        this.f67a += color.f67a;
        clamp();
        return this;
    }

    public Color sub(Color color) {
        this.f70r -= color.f70r;
        this.f69g -= color.f69g;
        this.f68b -= color.f68b;
        this.f67a -= color.f67a;
        clamp();
        return this;
    }

    public void clamp() {
        if (this.f70r < 0.0f) {
            this.f70r = 0.0f;
        } else if (this.f70r > 1.0f) {
            this.f70r = 1.0f;
        }
        if (this.f69g < 0.0f) {
            this.f69g = 0.0f;
        } else if (this.f69g > 1.0f) {
            this.f69g = 1.0f;
        }
        if (this.f68b < 0.0f) {
            this.f68b = 0.0f;
        } else if (this.f68b > 1.0f) {
            this.f68b = 1.0f;
        }
        if (this.f67a < 0.0f) {
            this.f67a = 0.0f;
        } else if (this.f67a > 1.0f) {
            this.f67a = 1.0f;
        }
    }

    public void set(float r, float g, float b, float a) {
        this.f70r = r;
        this.f69g = g;
        this.f68b = b;
        this.f67a = a;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (toIntBits() != ((Color) o).toIntBits()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        int i;
        int i2;
        int i3 = 0;
        if (this.f70r != 0.0f) {
            result = NumberUtils.floatToIntBits(this.f70r);
        } else {
            result = 0;
        }
        int i4 = result * 31;
        if (this.f69g != 0.0f) {
            i = NumberUtils.floatToIntBits(this.f69g);
        } else {
            i = 0;
        }
        int i5 = (i4 + i) * 31;
        if (this.f68b != 0.0f) {
            i2 = NumberUtils.floatToIntBits(this.f68b);
        } else {
            i2 = 0;
        }
        int i6 = (i5 + i2) * 31;
        if (this.f67a != 0.0f) {
            i3 = NumberUtils.floatToIntBits(this.f67a);
        }
        return i6 + i3;
    }

    public float toFloatBits() {
        return NumberUtils.intToFloatColor((((int) (this.f67a * 255.0f)) << 24) | (((int) (this.f68b * 255.0f)) << 16) | (((int) (this.f69g * 255.0f)) << 8) | ((int) (this.f70r * 255.0f)));
    }

    public int toIntBits() {
        return (((int) (this.f67a * 255.0f)) << 24) | (((int) (this.f68b * 255.0f)) << 16) | (((int) (this.f69g * 255.0f)) << 8) | ((int) (this.f70r * 255.0f));
    }

    public String toString() {
        String value = Integer.toHexString((((int) (this.f70r * 255.0f)) << 24) | (((int) (this.f69g * 255.0f)) << 16) | (((int) (this.f68b * 255.0f)) << 8) | ((int) (this.f67a * 255.0f)));
        while (value.length() < 8) {
            value = "0" + value;
        }
        return value;
    }

    public static Color valueOf(String hex) {
        return new Color(((float) Integer.valueOf(hex.substring(0, 2), 16).intValue()) / 255.0f, ((float) Integer.valueOf(hex.substring(2, 4), 16).intValue()) / 255.0f, ((float) Integer.valueOf(hex.substring(4, 6), 16).intValue()) / 255.0f, ((float) (hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16).intValue())) / 255.0f);
    }

    public static float toFloatBits(int r, int g, int b, int a) {
        return NumberUtils.intToFloatColor((a << 24) | (b << 16) | (g << 8) | r);
    }

    public static float toFloatBits(float r, float g, float b, float a) {
        return NumberUtils.intToFloatColor((((int) (255.0f * a)) << 24) | (((int) (255.0f * b)) << 16) | (((int) (255.0f * g)) << 8) | ((int) (255.0f * r)));
    }

    public static int toIntBits(int r, int g, int b, int a) {
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    public static int alpha(float alpha) {
        return (int) (255.0f * alpha);
    }

    public static int luminanceAlpha(float luminance, float alpha) {
        return (((int) (luminance * 255.0f)) << 8) | ((int) (255.0f * alpha));
    }

    public static int rgb565(float r, float g, float b) {
        return (((int) (r * 31.0f)) << 11) | (((int) (63.0f * g)) << 5) | ((int) (b * 31.0f));
    }

    public static int rgba4444(float r, float g, float b, float a) {
        return (((int) (r * 15.0f)) << 12) | (((int) (g * 15.0f)) << 8) | (((int) (b * 15.0f)) << 4) | ((int) (a * 15.0f));
    }

    public static int rgb888(float r, float g, float b) {
        return (((int) (r * 255.0f)) << 16) | (((int) (g * 255.0f)) << 8) | ((int) (b * 255.0f));
    }

    public static int rgba8888(float r, float g, float b, float a) {
        return (((int) (r * 255.0f)) << 24) | (((int) (g * 255.0f)) << 16) | (((int) (b * 255.0f)) << 8) | ((int) (a * 255.0f));
    }

    public static int rgb565(Color color) {
        return (((int) (color.f70r * 31.0f)) << 11) | (((int) (color.f69g * 63.0f)) << 5) | ((int) (color.f68b * 31.0f));
    }

    public static int rgba4444(Color color) {
        return (((int) (color.f70r * 15.0f)) << 12) | (((int) (color.f69g * 15.0f)) << 8) | (((int) (color.f68b * 15.0f)) << 4) | ((int) (color.f67a * 15.0f));
    }

    public static int rgb888(Color color) {
        return (((int) (color.f70r * 255.0f)) << 16) | (((int) (color.f69g * 255.0f)) << 8) | ((int) (color.f68b * 255.0f));
    }

    public static int rgba8888(Color color) {
        return (((int) (color.f70r * 255.0f)) << 24) | (((int) (color.f69g * 255.0f)) << 16) | (((int) (color.f68b * 255.0f)) << 8) | ((int) (color.f67a * 255.0f));
    }

    public static void rgb565ToColor(Color color, int value) {
        color.f70r = ((float) ((63488 & value) >>> 11)) / 31.0f;
        color.f69g = ((float) ((value & 2016) >>> 5)) / 63.0f;
        color.f68b = ((float) ((value & 31) >>> 0)) / 31.0f;
    }

    public static void rgba4444ToColor(Color color, int value) {
        color.f70r = ((float) ((61440 & value) >>> 12)) / 15.0f;
        color.f69g = ((float) ((value & 3840) >>> 8)) / 15.0f;
        color.f68b = ((float) ((value & TweenCallback.ANY_BACKWARD) >>> 4)) / 15.0f;
        color.f67a = ((float) (value & 15)) / 15.0f;
    }

    public static void rgb888ToColor(Color color, int value) {
        color.f70r = ((float) ((16711680 & value) >>> 16)) / 255.0f;
        color.f69g = ((float) ((65280 & value) >>> 8)) / 255.0f;
        color.f68b = ((float) (value & 255)) / 255.0f;
    }

    public static void rgba8888ToColor(Color color, int value) {
        color.f70r = ((float) ((-16777216 & value) >>> 24)) / 255.0f;
        color.f69g = ((float) ((16711680 & value) >>> 16)) / 255.0f;
        color.f68b = ((float) ((65280 & value) >>> 8)) / 255.0f;
        color.f67a = ((float) (value & 255)) / 255.0f;
    }

    public Color tmp() {
        return tmp.set(this);
    }

    public Color cpy() {
        return new Color(this);
    }
}
