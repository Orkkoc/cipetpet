package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class TextureAtlasLoader extends SynchronousAssetLoader<TextureAtlas, TextureAtlasParameter> {
    TextureAtlas.TextureAtlasData data;

    public TextureAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public TextureAtlas load(AssetManager assetManager, String fileName, TextureAtlasParameter parameter) {
        Iterator i$ = this.data.getPages().iterator();
        while (i$.hasNext()) {
            TextureAtlas.TextureAtlasData.Page page = i$.next();
            page.texture = (Texture) assetManager.get(page.textureFile.path().replaceAll("\\\\", "/"), Texture.class);
        }
        return new TextureAtlas(this.data);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, TextureAtlasParameter parameter) {
        FileHandle atlasFile = resolve(fileName);
        FileHandle imgDir = atlasFile.parent();
        if (parameter != null) {
            this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, parameter.flip);
        } else {
            this.data = new TextureAtlas.TextureAtlasData(atlasFile, imgDir, false);
        }
        Array<AssetDescriptor> dependencies = new Array<>();
        Iterator i$ = this.data.getPages().iterator();
        while (i$.hasNext()) {
            TextureAtlas.TextureAtlasData.Page page = i$.next();
            FileHandle handle = resolve(page.textureFile.path());
            TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
            params.format = page.format;
            params.genMipMaps = page.useMipMaps;
            params.minFilter = page.minFilter;
            params.magFilter = page.magFilter;
            dependencies.add(new AssetDescriptor(handle.path().replaceAll("\\\\", "/"), Texture.class, params));
        }
        return dependencies;
    }

    public static class TextureAtlasParameter extends AssetLoaderParameters<TextureAtlas> {
        public boolean flip = false;

        public TextureAtlasParameter() {
        }

        public TextureAtlasParameter(boolean flip2) {
            this.flip = flip2;
        }
    }
}
