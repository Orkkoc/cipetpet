package com.badlogic.gdx.assets;

import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class AssetLoadingTask implements Callable<Void> {
    Object asset = null;
    final AssetDescriptor assetDesc;
    volatile boolean asyncDone = false;
    boolean cancel = false;
    Array<AssetDescriptor> dependencies;
    boolean dependenciesLoaded = false;
    Future<Void> depsFuture = null;
    Future<Void> loadFuture = null;
    final AssetLoader loader;
    AssetManager manager;
    final long startTime;
    final ExecutorService threadPool;
    int ticks = 0;

    public AssetLoadingTask(AssetManager manager2, AssetDescriptor assetDesc2, AssetLoader loader2, ExecutorService threadPool2) {
        this.manager = manager2;
        this.assetDesc = assetDesc2;
        this.loader = loader2;
        this.threadPool = threadPool2;
        this.startTime = manager2.log.getLevel() == 3 ? TimeUtils.nanoTime() : 0;
    }

    public Void call() throws Exception {
        AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader) this.loader;
        if (!this.dependenciesLoaded) {
            this.dependencies = asyncLoader.getDependencies(this.assetDesc.fileName, this.assetDesc.params);
            if (this.dependencies != null) {
                Iterator i$ = this.dependencies.iterator();
                while (i$.hasNext()) {
                    this.manager.injectDependency(this.assetDesc.fileName, i$.next());
                }
                return null;
            }
            asyncLoader.loadAsync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
            this.asyncDone = true;
            return null;
        }
        asyncLoader.loadAsync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
        return null;
    }

    public boolean update() {
        this.ticks++;
        if (this.loader instanceof SynchronousAssetLoader) {
            handleSyncLoader();
        } else {
            handleAsyncLoader();
        }
        return this.asset != null;
    }

    private void handleSyncLoader() {
        SynchronousAssetLoader syncLoader = (SynchronousAssetLoader) this.loader;
        if (!this.dependenciesLoaded) {
            this.dependenciesLoaded = true;
            this.dependencies = syncLoader.getDependencies(this.assetDesc.fileName, this.assetDesc.params);
            if (this.dependencies == null) {
                this.asset = syncLoader.load(this.manager, this.assetDesc.fileName, this.assetDesc.params);
                return;
            }
            Iterator i$ = this.dependencies.iterator();
            while (i$.hasNext()) {
                this.manager.injectDependency(this.assetDesc.fileName, i$.next());
            }
            return;
        }
        this.asset = syncLoader.load(this.manager, this.assetDesc.fileName, this.assetDesc.params);
    }

    private void handleAsyncLoader() {
        AsynchronousAssetLoader asyncLoader = (AsynchronousAssetLoader) this.loader;
        if (!this.dependenciesLoaded) {
            if (this.depsFuture == null) {
                this.depsFuture = this.threadPool.submit(this);
            } else if (this.depsFuture.isDone()) {
                try {
                    this.depsFuture.get();
                    this.dependenciesLoaded = true;
                    if (this.asyncDone) {
                        this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
                    }
                } catch (Exception e) {
                    throw new GdxRuntimeException("Couldn't load dependencies of asset '" + this.assetDesc.fileName + "'", e);
                }
            }
        } else if (this.loadFuture == null && !this.asyncDone) {
            this.loadFuture = this.threadPool.submit(this);
        } else if (this.asyncDone) {
            this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
        } else if (this.loadFuture.isDone()) {
            try {
                this.loadFuture.get();
                this.asset = asyncLoader.loadSync(this.manager, this.assetDesc.fileName, this.assetDesc.params);
            } catch (Exception e2) {
                throw new GdxRuntimeException("Couldn't load asset '" + this.assetDesc.fileName + "'", e2);
            }
        }
    }

    public Object getAsset() {
        return this.asset;
    }
}
