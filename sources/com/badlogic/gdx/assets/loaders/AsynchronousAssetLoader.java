package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

public abstract class AsynchronousAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {
    public abstract void loadAsync(AssetManager assetManager, String str, P p);

    public abstract T loadSync(AssetManager assetManager, String str, P p);

    public AsynchronousAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }
}
