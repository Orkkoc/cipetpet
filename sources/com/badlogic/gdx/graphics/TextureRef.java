package com.badlogic.gdx.graphics;

public class TextureRef {
    public String Name;
    private int mRefCount = 1;
    private Texture mTexture;

    public TextureRef(String name, Texture texture) {
        this.Name = name;
        this.mTexture = texture;
    }

    public void addRef() {
        this.mRefCount++;
    }

    public int unload() {
        int i = this.mRefCount - 1;
        this.mRefCount = i;
        if (i == 0) {
            this.mTexture.dispose();
            this.mTexture = null;
            TextureDict.removeTexture(this.Name);
        }
        return this.mRefCount;
    }

    public void dispose() {
        this.mTexture.dispose();
    }

    public void bind() {
        this.mTexture.bind();
    }

    public Texture get() {
        return this.mTexture;
    }
}
