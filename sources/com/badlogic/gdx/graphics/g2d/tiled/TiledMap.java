package com.badlogic.gdx.graphics.g2d.tiled;

import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TiledMap {
    public int height;
    public ArrayList<TiledLayer> layers = new ArrayList<>(4);
    public ArrayList<TiledObjectGroup> objectGroups = new ArrayList<>(1);
    public String orientation;
    public HashMap<String, String> properties = new HashMap<>(2);
    public int tileHeight;
    private ArrayList<TileProperty> tileProperties = new ArrayList<>(0);
    public ArrayList<TileSet> tileSets = new ArrayList<>(5);
    public int tileWidth;
    public FileHandle tmxFile;
    public int width;

    public void setTileProperty(int id, String name, String value) {
        Iterator i$ = this.tileProperties.iterator();
        while (i$.hasNext()) {
            TileProperty tp = i$.next();
            if (tp.f110id == id) {
                tp.propertyMap.put(name, value);
                return;
            }
        }
        TileProperty tempProperty = new TileProperty();
        tempProperty.f110id = id;
        tempProperty.propertyMap.put(name, value);
        this.tileProperties.add(tempProperty);
    }

    public String getTileProperty(int id, String name) {
        Iterator i$ = this.tileProperties.iterator();
        while (i$.hasNext()) {
            TileProperty tp = i$.next();
            if (tp.f110id == id) {
                return tp.propertyMap.get(name);
            }
        }
        return null;
    }

    class TileProperty {

        /* renamed from: id */
        int f110id;
        HashMap<String, String> propertyMap = new HashMap<>();

        TileProperty() {
        }
    }
}
