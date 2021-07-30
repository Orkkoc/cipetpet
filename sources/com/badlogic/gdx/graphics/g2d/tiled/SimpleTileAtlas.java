package com.badlogic.gdx.graphics.g2d.tiled;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import java.lang.reflect.Array;
import java.util.Iterator;

public class SimpleTileAtlas extends TileAtlas {
    public SimpleTileAtlas(TiledMap map, FileHandle inputDir) {
        Iterator i$ = map.tileSets.iterator();
        while (i$.hasNext()) {
            TileSet set = i$.next();
            Pixmap pixmap = new Pixmap(inputDir.child(set.imageName));
            int originalWidth = pixmap.getWidth();
            int originalHeight = pixmap.getHeight();
            if (!MathUtils.isPowerOfTwo(originalWidth) || !MathUtils.isPowerOfTwo(originalHeight)) {
                int width = MathUtils.nextPowerOfTwo(originalWidth);
                int height = MathUtils.nextPowerOfTwo(originalHeight);
                Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
                potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, width, height);
                pixmap.dispose();
                pixmap = potPixmap;
            }
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            this.textures.add(texture);
            int idx = 0;
            TextureRegion[][] regions = split(texture, originalWidth, originalHeight, map.tileWidth, map.tileHeight, set.spacing, set.margin);
            for (int y = 0; y < regions[0].length; y++) {
                int x = 0;
                while (x < regions.length) {
                    this.regionsMap.put(set.firstgid + idx, regions[x][y]);
                    x++;
                    idx++;
                }
            }
        }
    }

    private static TextureRegion[][] split(Texture texture, int totalWidth, int totalHeight, int width, int height, int spacing, int margin) {
        int xSlices = (totalWidth - margin) / (width + spacing);
        int ySlices = (totalHeight - margin) / (height + spacing);
        TextureRegion[][] res = (TextureRegion[][]) Array.newInstance(TextureRegion.class, new int[]{xSlices, ySlices});
        for (int x = 0; x < xSlices; x++) {
            for (int y = 0; y < ySlices; y++) {
                res[x][y] = new TextureRegion(texture, margin + ((width + spacing) * x), margin + ((height + spacing) * y), width, height);
            }
        }
        return res;
    }
}
