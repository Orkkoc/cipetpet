package com.badlogic.gdx.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class TileMapRendererLoader extends SynchronousAssetLoader<TileMapRenderer, TileMapParameter> {

    public static class TileMapParameter extends AssetLoaderParameters<TileMapRenderer> {
        public final String imageDirectory;
        public final int tilesPerBlockX;
        public final int tilesPerBlockY;
        public final float unitsPerTileX;
        public final float unitsPerTileY;

        public TileMapParameter(String imageDirectory2, int tilesPerBlockX2, int tilesPerBlockY2) {
            this.imageDirectory = imageDirectory2;
            this.tilesPerBlockX = tilesPerBlockX2;
            this.tilesPerBlockY = tilesPerBlockY2;
            this.unitsPerTileX = 0.0f;
            this.unitsPerTileY = 0.0f;
        }

        public TileMapParameter(String imageDirectory2, int tilesPerBlockX2, int tilesPerBlockY2, float unitsPerTileX2, float unitsPerTileY2) {
            this.imageDirectory = imageDirectory2;
            this.tilesPerBlockX = tilesPerBlockX2;
            this.tilesPerBlockY = tilesPerBlockY2;
            this.unitsPerTileX = unitsPerTileX2;
            this.unitsPerTileY = unitsPerTileY2;
        }
    }

    public TileMapRendererLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public Array<AssetDescriptor> getDependencies(String fileName, TileMapParameter parameter) {
        if (parameter != null) {
            return null;
        }
        throw new IllegalArgumentException("Missing TileMapRendererParameter: " + fileName);
    }

    public TileMapRenderer load(AssetManager assetManager, String fileName, TileMapParameter parameter) {
        TiledMap map = TiledLoader.createMap(resolve(fileName));
        TileAtlas atlas = new TileAtlas(map, resolve(parameter.imageDirectory));
        if (parameter.unitsPerTileX == 0.0f || parameter.unitsPerTileY == 0.0f) {
            return new TileMapRenderer(map, atlas, parameter.tilesPerBlockX, parameter.tilesPerBlockY);
        }
        return new TileMapRenderer(map, atlas, parameter.tilesPerBlockX, parameter.tilesPerBlockY, parameter.unitsPerTileX, parameter.unitsPerTileY);
    }
}
