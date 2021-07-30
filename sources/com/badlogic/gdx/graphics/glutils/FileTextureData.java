package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class FileTextureData implements TextureData {
    public static boolean copyToPOT;
    final FileHandle file;
    Pixmap.Format format;
    int height = 0;
    boolean isPrepared = false;
    Pixmap pixmap;
    boolean useMipMaps;
    int width = 0;

    public FileTextureData(FileHandle file2, Pixmap preloadedPixmap, Pixmap.Format format2, boolean useMipMaps2) {
        this.file = file2;
        this.pixmap = preloadedPixmap;
        this.format = format2;
        this.useMipMaps = useMipMaps2;
        if (this.pixmap != null) {
            this.pixmap = ensurePot(this.pixmap);
            this.width = this.pixmap.getWidth();
            this.height = this.pixmap.getHeight();
            if (format2 == null) {
                this.format = this.pixmap.getFormat();
            }
        }
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void prepare() {
        if (this.isPrepared) {
            throw new GdxRuntimeException("Already prepared");
        }
        if (this.pixmap == null) {
            if (this.file.extension().equals("cim")) {
                this.pixmap = PixmapIO.readCIM(this.file);
            } else {
                this.pixmap = ensurePot(new Pixmap(this.file));
            }
            this.width = this.pixmap.getWidth();
            this.height = this.pixmap.getHeight();
            if (this.format == null) {
                this.format = this.pixmap.getFormat();
            }
        }
        this.isPrepared = true;
    }

    private Pixmap ensurePot(Pixmap pixmap2) {
        if (Gdx.gl20 == null && copyToPOT) {
            int pixmapWidth = pixmap2.getWidth();
            int pixmapHeight = pixmap2.getHeight();
            int potWidth = MathUtils.nextPowerOfTwo(pixmapWidth);
            int potHeight = MathUtils.nextPowerOfTwo(pixmapHeight);
            if (!(pixmapWidth == potWidth && pixmapHeight == potHeight)) {
                Pixmap tmp = new Pixmap(potWidth, potHeight, pixmap2.getFormat());
                tmp.drawPixmap(pixmap2, 0, 0, 0, 0, pixmapWidth, pixmapHeight);
                pixmap2.dispose();
                return tmp;
            }
        }
        return pixmap2;
    }

    public Pixmap consumePixmap() {
        if (!this.isPrepared) {
            throw new GdxRuntimeException("Call prepare() before calling getPixmap()");
        }
        this.isPrepared = false;
        Pixmap pixmap2 = this.pixmap;
        this.pixmap = null;
        return pixmap2;
    }

    public boolean disposePixmap() {
        return true;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Pixmap.Format getFormat() {
        return this.format;
    }

    public boolean useMipMaps() {
        return this.useMipMaps;
    }

    public boolean isManaged() {
        return true;
    }

    public FileHandle getFileHandle() {
        return this.file;
    }

    public TextureData.TextureDataType getType() {
        return TextureData.TextureDataType.Pixmap;
    }

    public void consumeCompressedData() {
        throw new GdxRuntimeException("This TextureData implementation does not upload data itself");
    }
}
