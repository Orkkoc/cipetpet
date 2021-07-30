package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.p000ui.Skin;
import com.badlogic.gdx.utils.Array;

public class SkinLoader extends AsynchronousAssetLoader<Skin, SkinParameter> {
    public SkinLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, SkinParameter parameter) {
        Array<AssetDescriptor> deps = new Array<>();
        if (parameter == null) {
            deps.add(new AssetDescriptor(resolve(fileName).pathWithoutExtension() + ".atlas", TextureAtlas.class));
        } else {
            deps.add(new AssetDescriptor(parameter.textureAtlasPath, TextureAtlas.class));
        }
        return deps;
    }

    public void loadAsync(AssetManager manager, String fileName, SkinParameter parameter) {
    }

    public Skin loadSync(AssetManager manager, String fileName, SkinParameter parameter) {
        String textureAtlasPath;
        if (parameter == null) {
            textureAtlasPath = resolve(fileName).pathWithoutExtension() + ".atlas";
        } else {
            textureAtlasPath = parameter.textureAtlasPath;
        }
        return new Skin(resolve(fileName), (TextureAtlas) manager.get(textureAtlasPath, TextureAtlas.class));
    }

    public static class SkinParameter extends AssetLoaderParameters<Skin> {
        public final String textureAtlasPath;

        public SkinParameter(String textureAtlasPath2) {
            this.textureAtlasPath = textureAtlasPath2;
        }
    }
}
