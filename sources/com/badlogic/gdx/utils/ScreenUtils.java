package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import java.nio.ByteBuffer;

public class ScreenUtils {
    public static TextureRegion getFrameBufferTexture() {
        return getFrameBufferTexture(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public static TextureRegion getFrameBufferTexture(int x, int y, int w, int h) {
        Gdx.f12gl.glPixelStorei(3333, 1);
        int potW = MathUtils.nextPowerOfTwo(w);
        int potH = MathUtils.nextPowerOfTwo(h);
        Pixmap pixmap = new Pixmap(potW, potH, Pixmap.Format.RGBA8888);
        Gdx.f12gl.glReadPixels(x, y, potW, potH, 6408, 5121, pixmap.getPixels());
        TextureRegion textureRegion = new TextureRegion(new Texture(pixmap), 0, h, w, -h);
        pixmap.dispose();
        return textureRegion;
    }

    public static byte[] getFrameBufferPixels(boolean flipY) {
        return getFrameBufferPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), flipY);
    }

    public static byte[] getFrameBufferPixels(int x, int y, int w, int h, boolean flipY) {
        Gdx.f12gl.glPixelStorei(3333, 1);
        ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 4);
        Gdx.f12gl.glReadPixels(x, y, w, h, 6408, 5121, pixels);
        byte[] lines = new byte[(w * h * 4)];
        if (flipY) {
            int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position(((h - i) - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
        } else {
            pixels.clear();
            pixels.get(lines);
        }
        return lines;
    }
}
