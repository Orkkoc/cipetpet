package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.Pixmap;

public interface TextureData {

    public enum TextureDataType {
        Pixmap,
        Compressed,
        Float
    }

    void consumeCompressedData();

    Pixmap consumePixmap();

    boolean disposePixmap();

    Pixmap.Format getFormat();

    int getHeight();

    TextureDataType getType();

    int getWidth();

    boolean isManaged();

    boolean isPrepared();

    void prepare();

    boolean useMipMaps();
}
