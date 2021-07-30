package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.ETC1;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ETC1TextureData implements TextureData {
    ETC1.ETC1Data data;
    FileHandle file;
    int height;
    boolean isPrepared;
    boolean useMipMaps;
    int width;

    public ETC1TextureData(FileHandle file2) {
        this(file2, false);
    }

    public ETC1TextureData(FileHandle file2, boolean useMipMaps2) {
        this.width = 0;
        this.height = 0;
        this.isPrepared = false;
        this.file = file2;
        this.useMipMaps = useMipMaps2;
    }

    public ETC1TextureData(ETC1.ETC1Data encodedImage, boolean useMipMaps2) {
        this.width = 0;
        this.height = 0;
        this.isPrepared = false;
        this.data = encodedImage;
        this.useMipMaps = useMipMaps2;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Compressed;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void prepare() {
        if (this.isPrepared) {
            throw new GdxRuntimeException("Already prepared");
        } else if (this.file == null && this.data == null) {
            throw new GdxRuntimeException("Can only load once from ETC1Data");
        } else {
            if (this.file != null) {
                this.data = new ETC1.ETC1Data(this.file);
            }
            this.width = this.data.width;
            this.height = this.data.height;
            this.isPrepared = true;
        }
    }

    public void consumeCompressedData() {
        if (!this.isPrepared) {
            throw new GdxRuntimeException("Call prepare() before calling consumeCompressedData()");
        }
        if (!Gdx.graphics.supportsExtension("GL_OES_compressed_ETC1_RGB8_texture") || !Gdx.graphics.isGL20Available()) {
            Pixmap pixmap = ETC1.decodeImage(this.data, Pixmap.Format.RGB565);
            Gdx.f12gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
            if (this.useMipMaps) {
                MipMapGenerator.generateMipMap(pixmap, pixmap.getWidth(), pixmap.getHeight(), false);
            }
            pixmap.dispose();
            this.useMipMaps = false;
        } else {
            Gdx.f12gl.glCompressedTexImage2D(3553, 0, ETC1.ETC1_RGB8_OES, this.width, this.height, 0, this.data.compressedData.capacity() - this.data.dataOffset, this.data.compressedData);
            if (useMipMaps()) {
                Gdx.gl20.glGenerateMipmap(3553);
            }
        }
        this.data.dispose();
        this.data = null;
        this.isPrepared = false;
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
        return Pixmap.Format.RGB565;
    }

    public boolean useMipMaps() {
        return this.useMipMaps;
    }

    public boolean isManaged() {
        return true;
    }
}
