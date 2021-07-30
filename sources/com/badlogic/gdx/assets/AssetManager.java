package com.badlogic.gdx.assets;

import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.MusicLoader;
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TileAtlasLoader;
import com.badlogic.gdx.assets.loaders.TileMapRendererLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.scenes.scene2d.p000ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class AssetManager implements Disposable {
    final ObjectMap<String, Array<String>> assetDependencies;
    final ObjectMap<String, Class> assetTypes;
    final ObjectMap<Class, ObjectMap<String, RefCountedContainer>> assets;
    AssetErrorListener listener;
    final Array<AssetDescriptor> loadQueue;
    int loaded;
    final ObjectMap<Class, AssetLoader> loaders;
    Logger log;
    Stack<AssetLoadingTask> tasks;
    final ExecutorService threadPool;
    int toLoad;

    public AssetManager() {
        this(new InternalFileHandleResolver());
    }

    public AssetManager(FileHandleResolver resolver) {
        this.assets = new ObjectMap<>();
        this.assetTypes = new ObjectMap<>();
        this.assetDependencies = new ObjectMap<>();
        this.loaders = new ObjectMap<>();
        this.loadQueue = new Array<>();
        this.tasks = new Stack<>();
        this.listener = null;
        this.loaded = 0;
        this.toLoad = 0;
        this.log = new Logger(AssetManager.class.getSimpleName(), 0);
        setLoader(BitmapFont.class, new BitmapFontLoader(resolver));
        setLoader(Music.class, new MusicLoader(resolver));
        setLoader(Pixmap.class, new PixmapLoader(resolver));
        setLoader(Sound.class, new SoundLoader(resolver));
        setLoader(TextureAtlas.class, new TextureAtlasLoader(resolver));
        setLoader(Texture.class, new TextureLoader(resolver));
        setLoader(Skin.class, new SkinLoader(resolver));
        setLoader(TileAtlas.class, new TileAtlasLoader(resolver));
        setLoader(TileMapRenderer.class, new TileMapRendererLoader(resolver));
        this.threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "AssetManager-Loader-Thread");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public synchronized <T> T get(String fileName) {
        T asset;
        Class<T> type = this.assetTypes.get(fileName);
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        asset = assetContainer.getObject(type);
        if (asset == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        T asset;
        ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
        if (assetsByType == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        RefCountedContainer assetContainer = assetsByType.get(fileName);
        if (assetContainer == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        asset = assetContainer.getObject(type);
        if (asset == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return asset;
    }

    public synchronized void unload(String fileName) {
        int foundIndex = -1;
        int i = 0;
        while (true) {
            if (i >= this.loadQueue.size) {
                break;
            } else if (this.loadQueue.get(i).fileName.equals(fileName)) {
                foundIndex = i;
                break;
            } else {
                i++;
            }
        }
        if (foundIndex != -1) {
            this.loadQueue.removeIndex(foundIndex);
            this.log.debug("Unload (from queue): " + fileName);
        } else {
            if (this.tasks.size() > 0) {
                AssetLoadingTask currAsset = (AssetLoadingTask) this.tasks.firstElement();
                if (currAsset.assetDesc.fileName.equals(fileName)) {
                    currAsset.cancel = true;
                    this.log.debug("Unload (from tasks): " + fileName);
                }
            }
            Class type = this.assetTypes.get(fileName);
            if (type == null) {
                throw new GdxRuntimeException("Asset not loaded: " + fileName);
            }
            RefCountedContainer assetRef = (RefCountedContainer) this.assets.get(type).get(fileName);
            assetRef.decRefCount();
            if (assetRef.getRefCount() <= 0) {
                this.log.debug("Unload (dispose): " + fileName);
                if (assetRef.getObject(Object.class) instanceof Disposable) {
                    ((Disposable) assetRef.getObject(Object.class)).dispose();
                }
                this.assetTypes.remove(fileName);
                this.assets.get(type).remove(fileName);
            } else {
                this.log.debug("Unload (decrement): " + fileName);
            }
            Array<String> dependencies = this.assetDependencies.get(fileName);
            if (dependencies != null) {
                Iterator i$ = dependencies.iterator();
                while (i$.hasNext()) {
                    unload(i$.next());
                }
            }
            if (assetRef.getRefCount() <= 0) {
                this.assetDependencies.remove(fileName);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x003d A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized <T> boolean containsAsset(T r8) {
        /*
            r7 = this;
            r5 = 0
            monitor-enter(r7)
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.ObjectMap<java.lang.String, com.badlogic.gdx.assets.RefCountedContainer>> r4 = r7.assets     // Catch:{ all -> 0x003f }
            java.lang.Class r6 = r8.getClass()     // Catch:{ all -> 0x003f }
            java.lang.Object r3 = r4.get(r6)     // Catch:{ all -> 0x003f }
            com.badlogic.gdx.utils.ObjectMap r3 = (com.badlogic.gdx.utils.ObjectMap) r3     // Catch:{ all -> 0x003f }
            if (r3 != 0) goto L_0x0013
            r4 = r5
        L_0x0011:
            monitor-exit(r7)
            return r4
        L_0x0013:
            com.badlogic.gdx.utils.ObjectMap$Keys r4 = r3.keys()     // Catch:{ all -> 0x003f }
            java.util.Iterator r1 = r4.iterator()     // Catch:{ all -> 0x003f }
        L_0x001b:
            boolean r4 = r1.hasNext()     // Catch:{ all -> 0x003f }
            if (r4 == 0) goto L_0x003d
            java.lang.Object r0 = r1.next()     // Catch:{ all -> 0x003f }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x003f }
            java.lang.Object r4 = r3.get(r0)     // Catch:{ all -> 0x003f }
            com.badlogic.gdx.assets.RefCountedContainer r4 = (com.badlogic.gdx.assets.RefCountedContainer) r4     // Catch:{ all -> 0x003f }
            java.lang.Class<java.lang.Object> r6 = java.lang.Object.class
            java.lang.Object r2 = r4.getObject(r6)     // Catch:{ all -> 0x003f }
            if (r2 == r8) goto L_0x003b
            boolean r4 = r8.equals(r2)     // Catch:{ all -> 0x003f }
            if (r4 == 0) goto L_0x001b
        L_0x003b:
            r4 = 1
            goto L_0x0011
        L_0x003d:
            r4 = r5
            goto L_0x0011
        L_0x003f:
            r4 = move-exception
            monitor-exit(r7)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.containsAsset(java.lang.Object):boolean");
    }

    public synchronized <T> String getAssetFileName(T asset) {
        String fileName;
        Iterator<Class> it = this.assets.keys().iterator();
        loop0:
        while (true) {
            if (!it.hasNext()) {
                fileName = null;
                break;
            }
            ObjectMap<String, RefCountedContainer> typedAssets = this.assets.get(it.next());
            Iterator i$ = typedAssets.keys().iterator();
            while (true) {
                if (i$.hasNext()) {
                    fileName = i$.next();
                    T otherAsset = typedAssets.get(fileName).getObject(Object.class);
                    if (otherAsset != asset) {
                        if (asset.equals(otherAsset)) {
                            break loop0;
                        }
                    } else {
                        break loop0;
                    }
                }
            }
        }
        return fileName;
    }

    public synchronized boolean isLoaded(String fileName) {
        boolean containsKey;
        if (fileName == null) {
            containsKey = false;
        } else {
            containsKey = this.assetTypes.containsKey(fileName);
        }
        return containsKey;
    }

    public synchronized boolean isLoaded(String fileName, Class type) {
        boolean z = false;
        synchronized (this) {
            ObjectMap<String, RefCountedContainer> assetsByType = this.assets.get(type);
            if (assetsByType != null) {
                RefCountedContainer assetContainer = assetsByType.get(fileName);
                if (!(assetContainer == null || assetContainer.getObject(type) == null)) {
                    z = true;
                }
            }
        }
        return z;
    }

    public synchronized <T> void load(String fileName, Class<T> type) {
        load(fileName, type, (AssetLoaderParameters) null);
    }

    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        if (this.loaders.get(type) == null) {
            throw new GdxRuntimeException("No loader for type: " + type.getSimpleName());
        }
        if (this.loadQueue.size == 0) {
            this.loaded = 0;
            this.toLoad = 0;
        }
        int i = 0;
        while (i < this.loadQueue.size) {
            AssetDescriptor desc = this.loadQueue.get(i);
            if (!desc.fileName.equals(fileName) || desc.type.equals(type)) {
                i++;
            } else {
                throw new GdxRuntimeException("Asset with name '" + fileName + "' already in preload queue, but has different type (expected: " + type.getSimpleName() + ", found: " + desc.type.getSimpleName() + ")");
            }
        }
        int i2 = 0;
        while (i2 < this.tasks.size()) {
            AssetDescriptor desc2 = ((AssetLoadingTask) this.tasks.get(i2)).assetDesc;
            if (!desc2.fileName.equals(fileName) || desc2.type.equals(type)) {
                i2++;
            } else {
                throw new GdxRuntimeException("Asset with name '" + fileName + "' already in task list, but has different type (expected: " + type.getSimpleName() + ", found: " + desc2.type.getSimpleName() + ")");
            }
        }
        Class otherType = this.assetTypes.get(fileName);
        if (otherType == null || otherType.equals(type)) {
            this.toLoad++;
            AssetDescriptor assetDesc = new AssetDescriptor(fileName, type, parameter);
            this.loadQueue.add(assetDesc);
            this.log.debug("Queued: " + assetDesc);
        } else {
            throw new GdxRuntimeException("Asset with name '" + fileName + "' already loaded, but has different type (expected: " + type.getSimpleName() + ", found: " + otherType.getSimpleName() + ")");
        }
    }

    public synchronized void load(AssetDescriptor desc) {
        load(desc.fileName, desc.type, desc.params);
    }

    private void disposeDependencies(String fileName) {
        Array<String> dependencies = this.assetDependencies.get(fileName);
        if (dependencies != null) {
            Iterator i$ = dependencies.iterator();
            while (i$.hasNext()) {
                disposeDependencies(i$.next());
            }
        }
        Object asset = ((RefCountedContainer) this.assets.get(this.assetTypes.get(fileName)).get(fileName)).getObject(Object.class);
        if (asset instanceof Disposable) {
            ((Disposable) asset).dispose();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002f, code lost:
        if (r4.tasks.size() != 0) goto L_0x0031;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean update() {
        /*
            r4 = this;
            r2 = 0
            r1 = 1
            monitor-enter(r4)
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r3 = r4.tasks     // Catch:{ Throwable -> 0x001d }
            int r3 = r3.size()     // Catch:{ Throwable -> 0x001d }
            if (r3 != 0) goto L_0x0031
        L_0x000b:
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r3 = r4.loadQueue     // Catch:{ Throwable -> 0x001d }
            int r3 = r3.size     // Catch:{ Throwable -> 0x001d }
            if (r3 == 0) goto L_0x0029
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r3 = r4.tasks     // Catch:{ Throwable -> 0x001d }
            int r3 = r3.size()     // Catch:{ Throwable -> 0x001d }
            if (r3 != 0) goto L_0x0029
            r4.nextTask()     // Catch:{ Throwable -> 0x001d }
            goto L_0x000b
        L_0x001d:
            r0 = move-exception
            r4.handleTaskError(r0)     // Catch:{ all -> 0x0049 }
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r3 = r4.loadQueue     // Catch:{ all -> 0x0049 }
            int r3 = r3.size     // Catch:{ all -> 0x0049 }
            if (r3 != 0) goto L_0x0047
        L_0x0027:
            monitor-exit(r4)
            return r1
        L_0x0029:
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r3 = r4.tasks     // Catch:{ Throwable -> 0x001d }
            int r3 = r3.size()     // Catch:{ Throwable -> 0x001d }
            if (r3 == 0) goto L_0x0027
        L_0x0031:
            boolean r3 = r4.updateTask()     // Catch:{ Throwable -> 0x001d }
            if (r3 == 0) goto L_0x0045
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.assets.AssetDescriptor> r3 = r4.loadQueue     // Catch:{ Throwable -> 0x001d }
            int r3 = r3.size     // Catch:{ Throwable -> 0x001d }
            if (r3 != 0) goto L_0x0045
            java.util.Stack<com.badlogic.gdx.assets.AssetLoadingTask> r3 = r4.tasks     // Catch:{ Throwable -> 0x001d }
            int r3 = r3.size()     // Catch:{ Throwable -> 0x001d }
            if (r3 == 0) goto L_0x0027
        L_0x0045:
            r1 = r2
            goto L_0x0027
        L_0x0047:
            r1 = r2
            goto L_0x0027
        L_0x0049:
            r1 = move-exception
            monitor-exit(r4)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.assets.AssetManager.update():boolean");
    }

    public synchronized boolean update(int millis) {
        boolean done;
        long endTime = System.nanoTime() + ((long) (millis * 1000));
        while (true) {
            done = update();
            if (!done && System.currentTimeMillis() <= endTime) {
                Thread.yield();
            }
        }
        return done;
    }

    public void finishLoading() {
        this.log.debug("Waiting for loading to complete...");
        while (!update()) {
            Thread.yield();
        }
        this.log.debug("Loading complete.");
    }

    /* access modifiers changed from: package-private */
    public synchronized void injectDependency(String parentAssetFilename, AssetDescriptor dependendAssetDesc) {
        Array<String> dependencies = this.assetDependencies.get(parentAssetFilename);
        if (dependencies == null) {
            dependencies = new Array<>();
            this.assetDependencies.put(parentAssetFilename, dependencies);
        }
        dependencies.add(dependendAssetDesc.fileName);
        if (isLoaded(dependendAssetDesc.fileName)) {
            this.log.debug("Dependency already loaded: " + dependendAssetDesc);
            ((RefCountedContainer) this.assets.get(this.assetTypes.get(dependendAssetDesc.fileName)).get(dependendAssetDesc.fileName)).incRefCount();
            incrementRefCountedDependencies(dependendAssetDesc.fileName);
        } else {
            this.log.info("Loading dependency: " + dependendAssetDesc);
            addTask(dependendAssetDesc);
        }
    }

    private void nextTask() {
        AssetDescriptor assetDesc = this.loadQueue.removeIndex(0);
        if (isLoaded(assetDesc.fileName)) {
            this.log.debug("Already loaded: " + assetDesc);
            ((RefCountedContainer) this.assets.get(this.assetTypes.get(assetDesc.fileName)).get(assetDesc.fileName)).incRefCount();
            incrementRefCountedDependencies(assetDesc.fileName);
            this.loaded++;
            return;
        }
        this.log.info("Loading: " + assetDesc);
        addTask(assetDesc);
    }

    private void addTask(AssetDescriptor assetDesc) {
        AssetLoader loader = this.loaders.get(assetDesc.type);
        if (loader == null) {
            throw new GdxRuntimeException("No loader for type: " + assetDesc.type.getSimpleName());
        }
        this.tasks.push(new AssetLoadingTask(this, assetDesc, loader, this.threadPool));
    }

    private boolean updateTask() {
        AssetLoadingTask task = this.tasks.peek();
        if (!task.update()) {
            return false;
        }
        this.assetTypes.put(task.assetDesc.fileName, task.assetDesc.type);
        ObjectMap<String, RefCountedContainer> typeToAssets = this.assets.get(task.assetDesc.type);
        if (typeToAssets == null) {
            typeToAssets = new ObjectMap<>();
            this.assets.put(task.assetDesc.type, typeToAssets);
        }
        typeToAssets.put(task.assetDesc.fileName, new RefCountedContainer(task.getAsset()));
        if (this.tasks.size() == 1) {
            this.loaded++;
        }
        this.tasks.pop();
        if (task.cancel) {
            unload(task.assetDesc.fileName);
            return true;
        }
        if (!(task.assetDesc.params == null || task.assetDesc.params.loadedCallback == null)) {
            task.assetDesc.params.loadedCallback.finishedLoading(this, task.assetDesc.fileName, task.assetDesc.type);
        }
        this.log.debug("Loaded: " + (((float) (TimeUtils.nanoTime() - task.startTime)) / 1000000.0f) + "ms " + task.assetDesc);
        return true;
    }

    private void incrementRefCountedDependencies(String parent) {
        Array<String> dependencies = this.assetDependencies.get(parent);
        if (dependencies != null) {
            Iterator i$ = dependencies.iterator();
            while (i$.hasNext()) {
                String dependency = i$.next();
                ((RefCountedContainer) this.assets.get(this.assetTypes.get(dependency)).get(dependency)).incRefCount();
                incrementRefCountedDependencies(dependency);
            }
        }
    }

    private void handleTaskError(Throwable t) {
        this.log.error("Error loading asset.", t);
        if (this.tasks.isEmpty()) {
            throw new GdxRuntimeException(t);
        }
        AssetLoadingTask task = this.tasks.pop();
        AssetDescriptor assetDesc = task.assetDesc;
        if (task.dependenciesLoaded && task.dependencies != null) {
            Iterator i$ = task.dependencies.iterator();
            while (i$.hasNext()) {
                unload(i$.next().fileName);
            }
        }
        this.tasks.clear();
        if (this.listener != null) {
            this.listener.error(assetDesc.fileName, assetDesc.type, t);
            return;
        }
        throw new GdxRuntimeException(t);
    }

    public synchronized <T, P extends AssetLoaderParameters<T>> void setLoader(Class<T> type, AssetLoader<T, P> loader) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        } else if (loader == null) {
            throw new IllegalArgumentException("loader cannot be null.");
        } else {
            this.log.debug("Loader set: " + type.getSimpleName() + " -> " + loader.getClass().getSimpleName());
            this.loaders.put(type, loader);
        }
    }

    public synchronized int getLoadedAssets() {
        return this.assetTypes.size;
    }

    public synchronized int getQueuedAssets() {
        return this.loadQueue.size + this.tasks.size();
    }

    public synchronized float getProgress() {
        float f = 1.0f;
        synchronized (this) {
            if (this.toLoad != 0) {
                f = Math.min(1.0f, ((float) this.loaded) / ((float) this.toLoad));
            }
        }
        return f;
    }

    public synchronized void setErrorListener(AssetErrorListener listener2) {
        this.listener = listener2;
    }

    public synchronized void dispose() {
        this.log.debug("Disposing.");
        clear();
        this.threadPool.shutdown();
        try {
            this.threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            new GdxRuntimeException("Couldn't shutdown loading thread");
        }
        return;
    }

    public synchronized void clear() {
        this.loadQueue.clear();
        do {
        } while (!update());
        ObjectIntMap<String> dependencyCount = new ObjectIntMap<>();
        while (this.assetTypes.size > 0) {
            dependencyCount.clear();
            Array<String> assets2 = this.assetTypes.keys().toArray();
            Iterator i$ = assets2.iterator();
            while (i$.hasNext()) {
                dependencyCount.put(i$.next(), 0);
            }
            Iterator i$2 = assets2.iterator();
            while (i$2.hasNext()) {
                Array<String> dependencies = this.assetDependencies.get(i$2.next());
                if (dependencies != null) {
                    Iterator i$3 = dependencies.iterator();
                    while (i$3.hasNext()) {
                        String dependency = i$3.next();
                        dependencyCount.put(dependency, dependencyCount.get(dependency, 0) + 1);
                    }
                }
            }
            Iterator i$4 = assets2.iterator();
            while (i$4.hasNext()) {
                String asset = i$4.next();
                if (dependencyCount.get(asset, 0) == 0) {
                    unload(asset);
                }
            }
        }
        this.assets.clear();
        this.assetTypes.clear();
        this.assetDependencies.clear();
        this.loaded = 0;
        this.toLoad = 0;
        this.loadQueue.clear();
        this.tasks.clear();
    }

    public Logger getLogger() {
        return this.log;
    }

    public synchronized int getReferenceCount(String fileName) {
        Class type;
        type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        return ((RefCountedContainer) this.assets.get(type).get(fileName)).getRefCount();
    }

    public synchronized void setReferenceCount(String fileName, int refCount) {
        Class type = this.assetTypes.get(fileName);
        if (type == null) {
            throw new GdxRuntimeException("Asset not loaded: " + fileName);
        }
        ((RefCountedContainer) this.assets.get(type).get(fileName)).setRefCount(refCount);
    }

    public synchronized String getDiagnostics() {
        StringBuffer buffer;
        buffer = new StringBuffer();
        Iterator<String> it = this.assetTypes.keys().iterator();
        while (it.hasNext()) {
            String fileName = it.next();
            buffer.append(fileName);
            buffer.append(", ");
            Class type = this.assetTypes.get(fileName);
            Array<String> dependencies = this.assetDependencies.get(fileName);
            buffer.append(type.getSimpleName());
            buffer.append(", refs: ");
            buffer.append(((RefCountedContainer) this.assets.get(type).get(fileName)).getRefCount());
            if (dependencies != null) {
                buffer.append(", deps: [");
                Iterator i$ = dependencies.iterator();
                while (i$.hasNext()) {
                    buffer.append(i$.next());
                    buffer.append(",");
                }
                buffer.append("]");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public synchronized Array<String> getAssetNames() {
        return this.assetTypes.keys().toArray();
    }

    public synchronized Array<String> getDependencies(String fileName) {
        return this.assetDependencies.get(fileName);
    }

    public synchronized Class getAssetType(String fileName) {
        return this.assetTypes.get(fileName);
    }
}
