package com.badlogic.gdx.graphics.g2d.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import java.util.StringTokenizer;

public class TileMapRenderer implements Disposable {
    private static final int FLAG_FLIP_X = Integer.MIN_VALUE;
    private static final int FLAG_FLIP_Y = 1073741824;
    private static final int FLAG_ROTATE = 536870912;
    private static final int MASK_CLEAR = -536870912;
    private int[] allLayers;
    private TileAtlas atlas;
    private int[][][] blendedCacheId;
    private IntArray blendedTiles;
    private SpriteCache cache;
    private int currentCol;
    private int currentLayer;
    private int currentRow;
    private int initialCol;
    private int initialRow;
    private boolean isSimpleTileAtlas;
    private int lastCol;
    private int lastRow;
    private TiledMap map;
    private int mapHeightUnits;
    private int mapWidthUnits;
    private int[][][] normalCacheId;
    public float overdrawX;
    public float overdrawY;
    private int tileHeight;
    private int tileWidth;
    private int tilesPerBlockX;
    private int tilesPerBlockY;
    Vector3 tmp;
    private float unitsPerBlockX;
    private float unitsPerBlockY;
    private float unitsPerTileX;
    private float unitsPerTileY;

    public TileMapRenderer(TiledMap map2, TileAtlas atlas2, int tilesPerBlockX2, int tilesPerBlockY2) {
        this(map2, atlas2, tilesPerBlockX2, tilesPerBlockY2, (float) map2.tileWidth, (float) map2.tileHeight);
    }

    public TileMapRenderer(TiledMap map2, TileAtlas atlas2, int tilesPerBlockX2, int tilesPerBlockY2, float unitsPerTileX2, float unitsPerTileY2) {
        this(map2, atlas2, tilesPerBlockX2, tilesPerBlockY2, unitsPerTileX2, unitsPerTileY2, (ShaderProgram) null);
    }

    public TileMapRenderer(TiledMap map2, TileAtlas atlas2, int tilesPerBlockX2, int tilesPerBlockY2, ShaderProgram shader) {
        this(map2, atlas2, tilesPerBlockX2, tilesPerBlockY2, (float) map2.tileWidth, (float) map2.tileHeight, shader);
    }

    public TileMapRenderer(TiledMap map2, TileAtlas atlas2, int tilesPerBlockX2, int tilesPerBlockY2, float unitsPerTileX2, float unitsPerTileY2, ShaderProgram shader) {
        IntArray blendedTilesArray;
        this.isSimpleTileAtlas = false;
        this.tmp = new Vector3();
        int[][][] tileMap = new int[map2.layers.size()][][];
        for (int i = 0; i < map2.layers.size(); i++) {
            tileMap[i] = map2.layers.get(i).tiles;
        }
        for (int i2 = 0; i2 < map2.tileSets.size(); i2++) {
            if (((float) (map2.tileSets.get(i2).tileHeight - map2.tileHeight)) > this.overdrawY * unitsPerTileY2) {
                this.overdrawY = ((float) (map2.tileSets.get(i2).tileHeight - map2.tileHeight)) / unitsPerTileY2;
            }
            if (((float) (map2.tileSets.get(i2).tileWidth - map2.tileWidth)) > this.overdrawX * unitsPerTileX2) {
                this.overdrawX = ((float) (map2.tileSets.get(i2).tileWidth - map2.tileWidth)) / unitsPerTileX2;
            }
        }
        String blendedTiles2 = map2.properties.get("blended tiles");
        if (blendedTiles2 != null) {
            blendedTilesArray = createFromCSV(blendedTiles2);
        } else {
            blendedTilesArray = new IntArray(0);
        }
        init(tileMap, atlas2, map2.tileWidth, map2.tileHeight, unitsPerTileX2, unitsPerTileY2, blendedTilesArray, tilesPerBlockX2, tilesPerBlockY2, shader);
        this.map = map2;
    }

    public TileMapRenderer(int[][][] map2, TileAtlas atlas2, int tileWidth2, int tileHeight2, float unitsPerTileX2, float unitsPerTileY2, IntArray blendedTiles2, int tilesPerBlockX2, int tilesPerBlockY2) {
        this.isSimpleTileAtlas = false;
        this.tmp = new Vector3();
        init(map2, atlas2, tileWidth2, tileHeight2, unitsPerTileX2, unitsPerTileY2, blendedTiles2, tilesPerBlockX2, tilesPerBlockY2, (ShaderProgram) null);
    }

    public TileMapRenderer(int[][][] map2, TileAtlas atlas2, int tileWidth2, int tileHeight2, float unitsPerTileX2, float unitsPerTileY2, IntArray blendedTiles2, int tilesPerBlockX2, int tilesPerBlockY2, ShaderProgram shader) {
        this.isSimpleTileAtlas = false;
        this.tmp = new Vector3();
        init(map2, atlas2, tileWidth2, tileHeight2, unitsPerTileX2, unitsPerTileY2, blendedTiles2, tilesPerBlockX2, tilesPerBlockY2, shader);
    }

    private void init(int[][][] map2, TileAtlas atlas2, int tileWidth2, int tileHeight2, float unitsPerTileX2, float unitsPerTileY2, IntArray blendedTiles2, int tilesPerBlockX2, int tilesPerBlockY2, ShaderProgram shader) {
        this.atlas = atlas2;
        this.tileWidth = tileWidth2;
        this.tileHeight = tileHeight2;
        this.unitsPerTileX = unitsPerTileX2;
        this.unitsPerTileY = unitsPerTileY2;
        this.blendedTiles = blendedTiles2;
        this.tilesPerBlockX = tilesPerBlockX2;
        this.tilesPerBlockY = tilesPerBlockY2;
        this.unitsPerBlockX = ((float) tilesPerBlockX2) * unitsPerTileX2;
        this.unitsPerBlockY = ((float) tilesPerBlockY2) * unitsPerTileY2;
        this.isSimpleTileAtlas = atlas2 instanceof SimpleTileAtlas;
        this.allLayers = new int[map2.length];
        int maxCacheSize = 0;
        int maxHeight = 0;
        int maxWidth = 0;
        for (int layer = 0; layer < map2.length; layer++) {
            this.allLayers[layer] = layer;
            if (map2[layer].length > maxHeight) {
                maxHeight = map2[layer].length;
            }
            for (int row = 0; row < map2[layer].length; row++) {
                if (map2[layer][row].length > maxWidth) {
                    maxWidth = map2[layer][row].length;
                }
                for (int i : map2[layer][row]) {
                    if (i != 0) {
                        maxCacheSize++;
                    }
                }
            }
        }
        this.mapHeightUnits = (int) (((float) maxHeight) * unitsPerTileY2);
        this.mapWidthUnits = (int) (((float) maxWidth) * unitsPerTileX2);
        if (shader == null) {
            this.cache = new SpriteCache(maxCacheSize, false);
        } else {
            this.cache = new SpriteCache(maxCacheSize, shader, false);
        }
        this.normalCacheId = new int[map2.length][][];
        this.blendedCacheId = new int[map2.length][][];
        for (int layer2 = 0; layer2 < map2.length; layer2++) {
            this.normalCacheId[layer2] = new int[MathUtils.ceil(((float) map2[layer2].length) / ((float) tilesPerBlockY2))][];
            this.blendedCacheId[layer2] = new int[MathUtils.ceil(((float) map2[layer2].length) / ((float) tilesPerBlockY2))][];
            for (int row2 = 0; row2 < this.normalCacheId[layer2].length; row2++) {
                this.normalCacheId[layer2][row2] = new int[MathUtils.ceil(((float) map2[layer2][row2].length) / ((float) tilesPerBlockX2))];
                this.blendedCacheId[layer2][row2] = new int[MathUtils.ceil(((float) map2[layer2][row2].length) / ((float) tilesPerBlockX2))];
                for (int col = 0; col < this.normalCacheId[layer2][row2].length; col++) {
                    if (this.isSimpleTileAtlas) {
                        this.blendedCacheId[layer2][row2][col] = addBlock(map2[layer2], row2, col, false);
                    } else {
                        this.normalCacheId[layer2][row2][col] = addBlock(map2[layer2], row2, col, false);
                        this.blendedCacheId[layer2][row2][col] = addBlock(map2[layer2], row2, col, true);
                    }
                }
            }
        }
    }

    private int addBlock(int[][] layer, int blockRow, int blockCol, boolean blended) {
        TextureRegion reg;
        this.cache.beginCache();
        int firstCol = blockCol * this.tilesPerBlockX;
        int firstRow = blockRow * this.tilesPerBlockY;
        int lastCol2 = firstCol + this.tilesPerBlockX;
        int lastRow2 = firstRow + this.tilesPerBlockY;
        float offsetX = (((float) this.tileWidth) - this.unitsPerTileX) / 2.0f;
        float offsetY = (((float) this.tileHeight) - this.unitsPerTileY) / 2.0f;
        int row = firstRow;
        while (row < lastRow2 && row < layer.length) {
            int col = firstCol;
            while (col < lastCol2 && col < layer[row].length) {
                int tile = layer[row][col];
                boolean flipX = (FLAG_FLIP_X & tile) != 0;
                boolean flipY = (FLAG_FLIP_Y & tile) != 0;
                boolean rotate = (FLAG_ROTATE & tile) != 0;
                int tile2 = tile & 536870911;
                if (!(tile2 == 0 || blended != this.blendedTiles.contains(tile2) || (reg = this.atlas.getRegion(tile2)) == null)) {
                    float x = (((float) col) * this.unitsPerTileX) - offsetX;
                    float y = (((float) ((layer.length - row) - 1)) * this.unitsPerTileY) - offsetY;
                    float width = (float) reg.getRegionWidth();
                    float height = (float) reg.getRegionHeight();
                    float originX = width * 0.5f;
                    float originY = height * 0.5f;
                    float scaleX = this.unitsPerTileX / ((float) this.tileWidth);
                    float scaleY = this.unitsPerTileY / ((float) this.tileHeight);
                    float rotation = 0.0f;
                    int sourceX = reg.getRegionX();
                    int sourceY = reg.getRegionY();
                    int sourceWidth = reg.getRegionWidth();
                    int sourceHeight = reg.getRegionHeight();
                    if (!rotate) {
                        if (flipX) {
                            sourceX += sourceWidth;
                            sourceWidth = -sourceWidth;
                        }
                        if (flipY) {
                            sourceY += sourceHeight;
                            sourceHeight = -sourceHeight;
                        }
                    } else if (flipX && flipY) {
                        rotation = -90.0f;
                        sourceX += sourceWidth;
                        sourceWidth = -sourceWidth;
                    } else if (flipX && !flipY) {
                        rotation = -90.0f;
                    } else if (flipY && !flipX) {
                        rotation = 90.0f;
                    } else if (!flipY && !flipX) {
                        rotation = -90.0f;
                        sourceY += sourceHeight;
                        sourceHeight = -sourceHeight;
                    }
                    this.cache.add(reg.getTexture(), x, y, originX, originY, width, height, scaleX, scaleY, rotation, sourceX, sourceY, sourceWidth, sourceHeight, false, false);
                }
                col++;
            }
            row++;
        }
        return this.cache.endCache();
    }

    public void render() {
        render(0.0f, 0.0f, (float) getMapWidthUnits(), (float) getMapHeightUnits());
    }

    public void render(float x, float y, float width, float height) {
        render(x, y, width, height, this.allLayers);
    }

    public void render(OrthographicCamera cam) {
        render(cam, this.allLayers);
    }

    public void render(OrthographicCamera cam, int[] layers) {
        getProjectionMatrix().set(cam.combined);
        this.tmp.set(0.0f, 0.0f, 0.0f);
        cam.unproject(this.tmp);
        render(this.tmp.f170x, this.tmp.f171y, cam.zoom * cam.viewportWidth, cam.zoom * cam.viewportHeight, layers);
    }

    public void render(float x, float y, float width, float height, int[] layers) {
        int i;
        this.lastRow = (int) ((((float) this.mapHeightUnits) - ((y - height) + this.overdrawY)) / this.unitsPerBlockY);
        this.initialRow = (int) ((((float) this.mapHeightUnits) - (y - this.overdrawY)) / this.unitsPerBlockY);
        this.initialRow = this.initialRow > 0 ? this.initialRow : 0;
        this.lastCol = (int) (((x + width) + this.overdrawX) / this.unitsPerBlockX);
        this.initialCol = (int) ((x - this.overdrawX) / this.unitsPerBlockX);
        if (this.initialCol > 0) {
            i = this.initialCol;
        } else {
            i = 0;
        }
        this.initialCol = i;
        Gdx.f12gl.glBlendFunc(770, 771);
        this.cache.begin();
        if (this.isSimpleTileAtlas) {
            Gdx.f12gl.glEnable(3042);
            this.currentLayer = 0;
            while (this.currentLayer < layers.length) {
                this.currentRow = this.initialRow;
                while (this.currentRow <= this.lastRow && this.currentRow < getLayerHeightInBlocks(this.currentLayer)) {
                    this.currentCol = this.initialCol;
                    while (this.currentCol <= this.lastCol && this.currentCol < getLayerWidthInBlocks(this.currentLayer, this.currentRow)) {
                        this.cache.draw(this.blendedCacheId[layers[this.currentLayer]][this.currentRow][this.currentCol]);
                        this.currentCol++;
                    }
                    this.currentRow++;
                }
                this.currentLayer++;
            }
        } else {
            this.currentLayer = 0;
            while (this.currentLayer < layers.length) {
                this.currentRow = this.initialRow;
                while (this.currentRow <= this.lastRow && this.currentRow < getLayerHeightInBlocks(this.currentLayer)) {
                    this.currentCol = this.initialCol;
                    while (this.currentCol <= this.lastCol && this.currentCol < getLayerWidthInBlocks(this.currentLayer, this.currentRow)) {
                        Gdx.f12gl.glDisable(3042);
                        this.cache.draw(this.normalCacheId[layers[this.currentLayer]][this.currentRow][this.currentCol]);
                        Gdx.f12gl.glEnable(3042);
                        this.cache.draw(this.blendedCacheId[layers[this.currentLayer]][this.currentRow][this.currentCol]);
                        this.currentCol++;
                    }
                    this.currentRow++;
                }
                this.currentLayer++;
            }
        }
        this.cache.end();
        Gdx.f12gl.glDisable(3042);
    }

    private int getLayerWidthInBlocks(int layer, int row) {
        int normalCacheWidth = 0;
        if (!(this.normalCacheId == null || this.normalCacheId[layer] == null || this.normalCacheId[layer][row] == null)) {
            normalCacheWidth = this.normalCacheId[layer][row].length;
        }
        int blendedCacheWidth = 0;
        if (!(this.blendedCacheId == null || this.blendedCacheId[layer] == null || this.blendedCacheId[layer][row] == null)) {
            blendedCacheWidth = this.blendedCacheId[layer][row].length;
        }
        return Math.max(normalCacheWidth, blendedCacheWidth);
    }

    private int getLayerHeightInBlocks(int layer) {
        int normalCacheHeight = 0;
        if (!(this.normalCacheId == null || this.normalCacheId[layer] == null)) {
            normalCacheHeight = this.normalCacheId[layer].length;
        }
        int blendedCacheHeight = 0;
        if (!(this.blendedCacheId == null || this.blendedCacheId[layer] == null)) {
            blendedCacheHeight = this.blendedCacheId[layer].length;
        }
        return Math.max(normalCacheHeight, blendedCacheHeight);
    }

    public Matrix4 getProjectionMatrix() {
        return this.cache.getProjectionMatrix();
    }

    public Matrix4 getTransformMatrix() {
        return this.cache.getTransformMatrix();
    }

    public int getRow(int worldY) {
        return (int) (((float) worldY) / this.unitsPerTileY);
    }

    public int getCol(int worldX) {
        return (int) (((float) worldX) / this.unitsPerTileX);
    }

    public int getInitialRow() {
        return this.initialRow;
    }

    public int getInitialCol() {
        return this.initialCol;
    }

    public int getLastRow() {
        return this.lastRow;
    }

    public int getLastCol() {
        return this.lastCol;
    }

    public float getUnitsPerTileX() {
        return this.unitsPerTileX;
    }

    public float getUnitsPerTileY() {
        return this.unitsPerTileY;
    }

    public int getMapHeightUnits() {
        return this.mapHeightUnits;
    }

    public int getMapWidthUnits() {
        return this.mapWidthUnits;
    }

    private static int parseIntWithDefault(String string, int defaultValue) {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void dispose() {
        this.cache.dispose();
    }

    public TiledMap getMap() {
        return this.map;
    }

    public TileAtlas getAtlas() {
        return this.atlas;
    }

    private static IntArray createFromCSV(String values) {
        IntArray list = new IntArray(false, (values.length() + 1) / 2);
        StringTokenizer st = new StringTokenizer(values, ",");
        while (st.hasMoreTokens()) {
            list.add(Integer.parseInt(st.nextToken()));
        }
        list.shrink();
        return list;
    }
}
