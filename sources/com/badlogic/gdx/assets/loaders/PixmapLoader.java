package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

public class PixmapLoader extends AsynchronousAssetLoader<Pixmap, PixmapParameter> {
    Pixmap pixmap;

    public static class PixmapParameter extends AssetLoaderParameters<Pixmap> {
    }

    public PixmapLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public void loadAsync(AssetManager manager, String fileName, PixmapParameter parameter) {
        this.pixmap = null;
        this.pixmap = new Pixmap(resolve(fileName));
    }

    public Pixmap loadSync(AssetManager manager, String fileName, PixmapParameter parameter) {
        return this.pixmap;
    }

    public Array<AssetDescriptor> getDependencies(String fileName, PixmapParameter parameter) {
        return null;
    }
}
