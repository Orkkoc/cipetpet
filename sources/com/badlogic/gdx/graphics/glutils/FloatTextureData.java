package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.FloatBuffer;

public class FloatTextureData implements TextureData {
    FloatBuffer buffer;
    int height = 0;
    boolean isPrepared = false;
    int width = 0;

    public FloatTextureData(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Float;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void prepare() {
        if (this.isPrepared) {
            throw new GdxRuntimeException("Already prepared");
        }
        this.buffer = BufferUtils.newFloatBuffer(this.width * this.height * 4);
        this.isPrepared = true;
    }

    public void consumeCompressedData() {
        if (!Gdx.graphics.supportsExtension("texture_float")) {
            throw new GdxRuntimeException("Extension OES_TEXTURE_FLOAT not supported!");
        } else if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS || Gdx.app.getType() == Application.ApplicationType.WebGL) {
            Gdx.f12gl.glTexImage2D(3553, 0, 6408, this.width, this.height, 0, 6408, 5126, this.buffer);
        } else {
            Gdx.f12gl.glTexImage2D(3553, 0, 34836, this.width, this.height, 0, 6408, 5126, this.buffer);
        }
    }

    public Pixmap consumePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    public boolean disposePixmap() {
        throw new GdxRuntimeException("This TextureData implementation does not return a Pixmap");
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Pixmap.Format getFormat() {
        return Pixmap.Format.RGBA8888;
    }

    public boolean useMipMaps() {
        return false;
    }

    public boolean isManaged() {
        return true;
    }
}
