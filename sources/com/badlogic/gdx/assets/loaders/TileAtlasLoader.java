package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class TileAtlasLoader extends AsynchronousAssetLoader<TileAtlas, TileAtlasParameter> {

    public static class TileAtlasParameter extends AssetLoaderParameters<TileAtlas> {
        public String inputDirectory;
        public String tileMapFile;
    }

    public TileAtlasLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public void loadAsync(AssetManager manager, String fileName, TileAtlasParameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Missing TileAtlasParameter: " + fileName);
        }
    }

    public TileAtlas loadSync(AssetManager manager, String fileName, TileAtlasParameter parameter) {
        if (parameter != null) {
            return null;
        }
        throw new IllegalArgumentException("Missing TileAtlasParameter: " + fileName);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, TileAtlasParameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Missing TileAtlasParameter: " + fileName);
        }
        Array<AssetDescriptor> deps = new Array<>();
        deps.add(new AssetDescriptor(parameter.tileMapFile, TiledMap.class));
        return deps;
    }
}
