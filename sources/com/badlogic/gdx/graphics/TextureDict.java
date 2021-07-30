package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.Iterator;

public class TextureDict {
    private static ObjectMap<String, TextureRef> sDictionary = new ObjectMap<>();

    public static TextureRef loadTexture(String path) {
        return loadTexture(path, Texture.TextureFilter.MipMap, Texture.TextureFilter.Linear, Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    public static TextureRef loadTexture(String path, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, Texture.TextureWrap uwrap, Texture.TextureWrap vwrap) {
        if (sDictionary.containsKey(path)) {
            TextureRef ref = sDictionary.get(path);
            ref.addRef();
            return ref;
        }
        Texture newTex = new Texture(Gdx.app.getFiles().getFileHandle(path, Files.FileType.Internal), minFilter.isMipMap() || magFilter.isMipMap());
        newTex.setFilter(minFilter, magFilter);
        newTex.setWrap(uwrap, vwrap);
        TextureRef ref2 = new TextureRef(path, newTex);
        sDictionary.put(path, ref2);
        return ref2;
    }

    public static void removeTexture(String path) {
        sDictionary.remove(path);
    }

    public static void unloadAll() {
        Iterator i$ = sDictionary.values().iterator();
        while (i$.hasNext()) {
            i$.next().dispose();
        }
        sDictionary.clear();
    }
}
