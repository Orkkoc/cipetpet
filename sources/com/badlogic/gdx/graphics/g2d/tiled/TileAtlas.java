package com.badlogic.gdx.graphics.g2d.tiled;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public class TileAtlas implements Disposable {
    protected IntMap<TextureRegion> regionsMap = new IntMap<>();
    protected final HashSet<Texture> textures = new HashSet<>(1);

    protected TileAtlas() {
    }

    public TileAtlas(TiledMap map, FileHandle inputDir) {
        Iterator<TileSet> it = map.tileSets.iterator();
        while (it.hasNext()) {
            TileSet set = it.next();
            FileHandle packfile = getRelativeFileHandle(inputDir, removeExtension(set.imageName) + " packfile");
            Iterator i$ = new TextureAtlas(packfile, packfile.parent(), false).findRegions(removeExtension(removePath(set.imageName))).iterator();
            while (i$.hasNext()) {
                TextureAtlas.AtlasRegion reg = i$.next();
                this.regionsMap.put(reg.index + set.firstgid, reg);
                if (!this.textures.contains(reg.getTexture())) {
                    this.textures.add(reg.getTexture());
                }
            }
        }
    }

    public TextureRegion getRegion(int id) {
        return this.regionsMap.get(id);
    }

    public void dispose() {
        Iterator i$ = this.textures.iterator();
        while (i$.hasNext()) {
            i$.next().dispose();
        }
        this.textures.clear();
    }

    private static String removeExtension(String s) {
        int extensionIndex = s.lastIndexOf(".");
        return extensionIndex == -1 ? s : s.substring(0, extensionIndex);
    }

    private static String removePath(String s) {
        String temp;
        int index = s.lastIndexOf(92);
        if (index != -1) {
            temp = s.substring(index + 1);
        } else {
            temp = s;
        }
        int index2 = temp.lastIndexOf(47);
        if (index2 != -1) {
            return s.substring(index2 + 1);
        }
        return s;
    }

    private static FileHandle getRelativeFileHandle(FileHandle path, String relativePath) {
        if (relativePath.trim().length() == 0) {
            return path;
        }
        FileHandle child = path;
        StringTokenizer tokenizer = new StringTokenizer(relativePath, "\\/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("..")) {
                child = child.parent();
            } else {
                child = child.child(token);
            }
        }
        return child;
    }

    public void flipRegions(boolean x, boolean y) {
        Iterator i$ = this.regionsMap.values().iterator();
        while (i$.hasNext()) {
            i$.next().flip(x, y);
        }
    }
}
