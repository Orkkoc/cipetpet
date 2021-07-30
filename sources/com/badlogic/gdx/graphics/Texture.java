package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.ETC1TextureData;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.glutils.MipMapGenerator;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Texture implements Disposable {
    private static AssetManager assetManager;
    private static final IntBuffer buffer = BufferUtils.newIntBuffer(1);
    private static boolean enforcePotImages = true;
    static final Map<Application, List<Texture>> managedTextures = new HashMap();
    TextureData data;
    int glHandle;
    TextureFilter magFilter;
    TextureFilter minFilter;
    TextureWrap uWrap;
    TextureWrap vWrap;

    public enum TextureFilter {
        Nearest(9728),
        Linear(9729),
        MipMap(9987),
        MipMapNearestNearest(9984),
        MipMapLinearNearest(9985),
        MipMapNearestLinear(9986),
        MipMapLinearLinear(9987);
        
        final int glEnum;

        private TextureFilter(int glEnum2) {
            this.glEnum = glEnum2;
        }

        public boolean isMipMap() {
            return (this.glEnum == 9728 || this.glEnum == 9729) ? false : true;
        }

        public int getGLEnum() {
            return this.glEnum;
        }
    }

    public enum TextureWrap {
        MirroredRepeat(GL20.GL_MIRRORED_REPEAT),
        ClampToEdge(33071),
        Repeat(10497);
        
        final int glEnum;

        private TextureWrap(int glEnum2) {
            this.glEnum = glEnum2;
        }

        public int getGLEnum() {
            return this.glEnum;
        }
    }

    public Texture(String internalPath) {
        this(Gdx.files.internal(internalPath));
    }

    public Texture(FileHandle file) {
        this(file, (Pixmap.Format) null, false);
    }

    public Texture(FileHandle file, boolean useMipMaps) {
        this(file, (Pixmap.Format) null, useMipMaps);
    }

    public Texture(FileHandle file, Pixmap.Format format, boolean useMipMaps) {
        this.minFilter = TextureFilter.Nearest;
        this.magFilter = TextureFilter.Nearest;
        this.uWrap = TextureWrap.ClampToEdge;
        this.vWrap = TextureWrap.ClampToEdge;
        if (file.name().endsWith(".etc1")) {
            create(new ETC1TextureData(file, useMipMaps));
        } else {
            create(new FileTextureData(file, (Pixmap) null, format, useMipMaps));
        }
    }

    public Texture(Pixmap pixmap) {
        this((TextureData) new PixmapTextureData(pixmap, (Pixmap.Format) null, false, false));
    }

    public Texture(Pixmap pixmap, boolean useMipMaps) {
        this((TextureData) new PixmapTextureData(pixmap, (Pixmap.Format) null, useMipMaps, false));
    }

    public Texture(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps) {
        this((TextureData) new PixmapTextureData(pixmap, format, useMipMaps, false));
    }

    public Texture(int width, int height, Pixmap.Format format) {
        this((TextureData) new PixmapTextureData(new Pixmap(width, height, format), (Pixmap.Format) null, false, true));
    }

    public Texture(TextureData data2) {
        this.minFilter = TextureFilter.Nearest;
        this.magFilter = TextureFilter.Nearest;
        this.uWrap = TextureWrap.ClampToEdge;
        this.vWrap = TextureWrap.ClampToEdge;
        create(data2);
    }

    private void create(TextureData data2) {
        this.glHandle = createGLHandle();
        load(data2);
        if (data2.isManaged()) {
            addManagedTexture(Gdx.app, this);
        }
    }

    public static int createGLHandle() {
        buffer.position(0);
        buffer.limit(buffer.capacity());
        Gdx.f12gl.glGenTextures(1, buffer);
        return buffer.get(0);
    }

    public void load(TextureData data2) {
        if (this.data == null || data2.isManaged() == this.data.isManaged()) {
            this.data = data2;
            if (!data2.isPrepared()) {
                data2.prepare();
            }
            if (data2.getType() == TextureData.TextureDataType.Pixmap) {
                Pixmap pixmap = data2.consumePixmap();
                uploadImageData(pixmap);
                if (data2.disposePixmap()) {
                    pixmap.dispose();
                }
                setFilter(this.minFilter, this.magFilter);
                setWrap(this.uWrap, this.vWrap);
            }
            if (data2.getType() == TextureData.TextureDataType.Compressed) {
                Gdx.f12gl.glBindTexture(3553, this.glHandle);
                data2.consumeCompressedData();
                setFilter(this.minFilter, this.magFilter);
                setWrap(this.uWrap, this.vWrap);
            }
            if (data2.getType() == TextureData.TextureDataType.Float) {
                Gdx.f12gl.glBindTexture(3553, this.glHandle);
                data2.consumeCompressedData();
                setFilter(this.minFilter, this.magFilter);
                setWrap(this.uWrap, this.vWrap);
            }
            Gdx.f12gl.glBindTexture(3553, 0);
            return;
        }
        throw new GdxRuntimeException("New data must have the same managed status as the old data");
    }

    private void uploadImageData(Pixmap pixmap) {
        if (!enforcePotImages || Gdx.gl20 != null || (MathUtils.isPowerOfTwo(this.data.getWidth()) && MathUtils.isPowerOfTwo(this.data.getHeight()))) {
            boolean disposePixmap = false;
            if (this.data.getFormat() != pixmap.getFormat()) {
                Pixmap tmp = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), this.data.getFormat());
                Pixmap.Blending blend = Pixmap.getBlending();
                Pixmap.setBlending(Pixmap.Blending.None);
                tmp.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                Pixmap.setBlending(blend);
                pixmap = tmp;
                disposePixmap = true;
            }
            Gdx.f12gl.glBindTexture(3553, this.glHandle);
            Gdx.f12gl.glPixelStorei(3317, 1);
            if (this.data.useMipMaps()) {
                MipMapGenerator.generateMipMap(pixmap, pixmap.getWidth(), pixmap.getHeight(), disposePixmap);
                return;
            }
            Gdx.f12gl.glTexImage2D(3553, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
            if (disposePixmap) {
                pixmap.dispose();
                return;
            }
            return;
        }
        throw new GdxRuntimeException("Texture width and height must be powers of two: " + this.data.getWidth() + "x" + this.data.getHeight());
    }

    private void reload() {
        if (!this.data.isManaged()) {
            throw new GdxRuntimeException("Tried to reload unmanaged Texture");
        }
        this.glHandle = createGLHandle();
        load(this.data);
    }

    public void bind() {
        Gdx.f12gl.glBindTexture(3553, this.glHandle);
    }

    public void bind(int unit) {
        Gdx.f12gl.glActiveTexture(33984 + unit);
        Gdx.f12gl.glBindTexture(3553, this.glHandle);
    }

    public void draw(Pixmap pixmap, int x, int y) {
        if (this.data.isManaged()) {
            throw new GdxRuntimeException("can't draw to a managed texture");
        }
        Gdx.f12gl.glBindTexture(3553, this.glHandle);
        Gdx.f12gl.glTexSubImage2D(3553, 0, x, y, pixmap.getWidth(), pixmap.getHeight(), pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
    }

    public int getWidth() {
        return this.data.getWidth();
    }

    public int getHeight() {
        return this.data.getHeight();
    }

    public TextureFilter getMinFilter() {
        return this.minFilter;
    }

    public TextureFilter getMagFilter() {
        return this.magFilter;
    }

    public TextureWrap getUWrap() {
        return this.uWrap;
    }

    public TextureWrap getVWrap() {
        return this.vWrap;
    }

    public TextureData getTextureData() {
        return this.data;
    }

    public boolean isManaged() {
        return this.data.isManaged();
    }

    public int getTextureObjectHandle() {
        return this.glHandle;
    }

    public void setWrap(TextureWrap u, TextureWrap v) {
        this.uWrap = u;
        this.vWrap = v;
        bind();
        Gdx.f12gl.glTexParameterf(3553, 10242, (float) u.getGLEnum());
        Gdx.f12gl.glTexParameterf(3553, 10243, (float) v.getGLEnum());
    }

    public void setFilter(TextureFilter minFilter2, TextureFilter magFilter2) {
        this.minFilter = minFilter2;
        this.magFilter = magFilter2;
        bind();
        Gdx.f12gl.glTexParameterf(3553, 10241, (float) minFilter2.getGLEnum());
        Gdx.f12gl.glTexParameterf(3553, 10240, (float) magFilter2.getGLEnum());
    }

    public void dispose() {
        if (this.glHandle != 0) {
            buffer.put(0, this.glHandle);
            Gdx.f12gl.glDeleteTextures(1, buffer);
            if (this.data.isManaged() && managedTextures.get(Gdx.app) != null) {
                managedTextures.get(Gdx.app).remove(this);
            }
            this.glHandle = 0;
        }
    }

    public static void setEnforcePotImages(boolean enforcePotImages2) {
        enforcePotImages = enforcePotImages2;
    }

    private static void addManagedTexture(Application app, Texture texture) {
        List<Texture> managedTexureList = managedTextures.get(app);
        if (managedTexureList == null) {
            managedTexureList = new ArrayList<>();
        }
        managedTexureList.add(texture);
        managedTextures.put(app, managedTexureList);
    }

    public static void clearAllTextures(Application app) {
        managedTextures.remove(app);
    }

    public static void invalidateAllTextures(Application app) {
        List<Texture> managedTexureList = managedTextures.get(app);
        if (managedTexureList != null) {
            if (assetManager == null) {
                for (int i = 0; i < managedTexureList.size(); i++) {
                    managedTexureList.get(i).reload();
                }
                return;
            }
            assetManager.finishLoading();
            List<Texture> textures = new ArrayList<>(managedTexureList);
            for (Texture texture : textures) {
                String fileName = assetManager.getAssetFileName(texture);
                if (fileName == null) {
                    texture.reload();
                } else {
                    final int refCount = assetManager.getReferenceCount(fileName);
                    assetManager.setReferenceCount(fileName, 0);
                    texture.glHandle = 0;
                    TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
                    params.textureData = texture.getTextureData();
                    params.minFilter = texture.getMinFilter();
                    params.magFilter = texture.getMagFilter();
                    params.wrapU = texture.getUWrap();
                    params.wrapV = texture.getVWrap();
                    params.genMipMaps = texture.data.useMipMaps();
                    params.texture = texture;
                    params.loadedCallback = new AssetLoaderParameters.LoadedCallback() {
                        public void finishedLoading(AssetManager assetManager, String fileName, Class type) {
                            assetManager.setReferenceCount(fileName, refCount);
                        }
                    };
                    assetManager.unload(fileName);
                    texture.glHandle = createGLHandle();
                    assetManager.load(fileName, Texture.class, params);
                }
            }
            managedTexureList.clear();
            managedTexureList.addAll(textures);
        }
    }

    public static void setAssetManager(AssetManager manager) {
        assetManager = manager;
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed textures/app: { ");
        for (Application app : managedTextures.keySet()) {
            builder.append(managedTextures.get(app).size());
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static int getNumManagedTextures() {
        return managedTextures.get(Gdx.app).size();
    }
}
