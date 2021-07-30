package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

public abstract class SynchronousAssetLoader<T, P extends AssetLoaderParameters<T>> extends AssetLoader<T, P> {
    public abstract T load(AssetManager assetManager, String str, P p);

    public SynchronousAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }
}
