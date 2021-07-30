package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Gdx2DPixmap implements Disposable {
    public static final int GDX2D_BLEND_NONE = 0;
    public static final int GDX2D_BLEND_SRC_OVER = 1;
    public static final int GDX2D_FORMAT_ALPHA = 1;
    public static final int GDX2D_FORMAT_LUMINANCE_ALPHA = 2;
    public static final int GDX2D_FORMAT_RGB565 = 5;
    public static final int GDX2D_FORMAT_RGB888 = 3;
    public static final int GDX2D_FORMAT_RGBA4444 = 6;
    public static final int GDX2D_FORMAT_RGBA8888 = 4;
    public static final int GDX2D_SCALE_LINEAR = 1;
    public static final int GDX2D_SCALE_NEAREST = 0;
    final long basePtr;
    final int format;
    final int height;
    final long[] nativeData = new long[4];
    final ByteBuffer pixelPtr;
    final int width;

    private static native void clear(long j, int i);

    private static native void drawCircle(long j, int i, int i2, int i3, int i4);

    private static native void drawLine(long j, int i, int i2, int i3, int i4, int i5);

    private static native void drawPixmap(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    private static native void drawRect(long j, int i, int i2, int i3, int i4, int i5);

    private static native void fillCircle(long j, int i, int i2, int i3, int i4);

    private static native void fillRect(long j, int i, int i2, int i3, int i4, int i5);

    private static native void free(long j);

    public static native String getFailureReason();

    private static native int getPixel(long j, int i, int i2);

    private static native ByteBuffer load(long[] jArr, byte[] bArr, int i, int i2, int i3);

    private static native ByteBuffer newPixmap(long[] jArr, int i, int i2, int i3);

    public static native void setBlend(int i);

    private static native void setPixel(long j, int i, int i2, int i3);

    public static native void setScale(int i);

    static {
        setBlend(1);
        setScale(1);
    }

    public Gdx2DPixmap(byte[] encodedData, int offset, int len, int requestedFormat) throws IOException {
        this.pixelPtr = load(this.nativeData, encodedData, offset, len, requestedFormat);
        if (this.pixelPtr == null) {
            throw new IOException("couldn't load pixmap " + getFailureReason());
        }
        this.basePtr = this.nativeData[0];
        this.width = (int) this.nativeData[1];
        this.height = (int) this.nativeData[2];
        this.format = (int) this.nativeData[3];
    }

    public Gdx2DPixmap(InputStream in, int requestedFormat) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        while (true) {
            int readBytes = in.read(buffer);
            if (readBytes == -1) {
                break;
            }
            bytes.write(buffer, 0, readBytes);
        }
        byte[] buffer2 = bytes.toByteArray();
        this.pixelPtr = load(this.nativeData, buffer2, 0, buffer2.length, requestedFormat);
        if (this.pixelPtr == null) {
            throw new IOException("couldn't load pixmap " + getFailureReason());
        }
        this.basePtr = this.nativeData[0];
        this.width = (int) this.nativeData[1];
        this.height = (int) this.nativeData[2];
        this.format = (int) this.nativeData[3];
    }

    public Gdx2DPixmap(int width2, int height2, int format2) throws IllegalArgumentException {
        this.pixelPtr = newPixmap(this.nativeData, width2, height2, format2);
        if (this.pixelPtr == null) {
            throw new IllegalArgumentException("couldn't load pixmap");
        }
        this.basePtr = this.nativeData[0];
        this.width = (int) this.nativeData[1];
        this.height = (int) this.nativeData[2];
        this.format = (int) this.nativeData[3];
    }

    public Gdx2DPixmap(ByteBuffer pixelPtr2, long[] nativeData2) {
        this.pixelPtr = pixelPtr2;
        this.basePtr = nativeData2[0];
        this.width = (int) nativeData2[1];
        this.height = (int) nativeData2[2];
        this.format = (int) nativeData2[3];
    }

    public void dispose() {
        free(this.basePtr);
    }

    public void clear(int color) {
        clear(this.basePtr, color);
    }

    public void setPixel(int x, int y, int color) {
        setPixel(this.basePtr, x, y, color);
    }

    public int getPixel(int x, int y) {
        return getPixel(this.basePtr, x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        drawLine(this.basePtr, x, y, x2, y2, color);
    }

    public void drawRect(int x, int y, int width2, int height2, int color) {
        drawRect(this.basePtr, x, y, width2, height2, color);
    }

    public void drawCircle(int x, int y, int radius, int color) {
        drawCircle(this.basePtr, x, y, radius, color);
    }

    public void fillRect(int x, int y, int width2, int height2, int color) {
        fillRect(this.basePtr, x, y, width2, height2, color);
    }

    public void fillCircle(int x, int y, int radius, int color) {
        fillCircle(this.basePtr, x, y, radius, color);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int dstX, int dstY, int width2, int height2) {
        drawPixmap(src.basePtr, this.basePtr, srcX, srcY, width2, height2, dstX, dstY, width2, height2);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        drawPixmap(src.basePtr, this.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }

    public static Gdx2DPixmap newPixmap(InputStream in, int requestedFormat) {
        try {
            return new Gdx2DPixmap(in, requestedFormat);
        } catch (IOException e) {
            return null;
        }
    }

    public static Gdx2DPixmap newPixmap(int width2, int height2, int format2) {
        try {
            return new Gdx2DPixmap(width2, height2, format2);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ByteBuffer getPixels() {
        return this.pixelPtr;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getFormat() {
        return this.format;
    }

    public int getGLInternalFormat() {
        switch (this.format) {
            case 1:
                return 6406;
            case 2:
                return 6410;
            case 3:
            case 5:
                return 6407;
            case 4:
            case 6:
                return 6408;
            default:
                throw new GdxRuntimeException("unknown format: " + this.format);
        }
    }

    public int getGLFormat() {
        return getGLInternalFormat();
    }

    public int getGLType() {
        switch (this.format) {
            case 1:
            case 2:
            case 3:
            case 4:
                return 5121;
            case 5:
                return 33635;
            case 6:
                return 32819;
            default:
                throw new GdxRuntimeException("unknown format: " + this.format);
        }
    }

    public String getFormatString() {
        switch (this.format) {
            case 1:
                return "alpha";
            case 2:
                return "luminance alpha";
            case 3:
                return "rgb888";
            case 4:
                return "rgba8888";
            case 5:
                return "rgb565";
            case 6:
                return "rgba4444";
            default:
                return "unknown";
        }
    }
}
