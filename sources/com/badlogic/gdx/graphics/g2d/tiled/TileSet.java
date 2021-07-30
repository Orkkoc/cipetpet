package com.badlogic.gdx.graphics.g2d.tiled;

public class TileSet {
    public int firstgid;
    public String imageName;
    public int margin = 0;
    public String name;
    public int spacing = 0;
    public int tileHeight;
    public int tileWidth;

    protected TileSet() {
    }

    protected TileSet(TileSet set) {
        this.firstgid = set.firstgid;
        this.tileWidth = set.tileWidth;
        this.tileHeight = set.tileHeight;
        this.margin = set.margin;
        this.spacing = set.spacing;
        this.imageName = set.imageName;
        this.name = set.name;
    }
}
