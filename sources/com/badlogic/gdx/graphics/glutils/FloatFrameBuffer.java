package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

public class FloatFrameBuffer extends FrameBuffer {
    public FloatFrameBuffer(int width, int height, boolean hasDepth) {
        super((Pixmap.Format) null, width, height, hasDepth);
    }

    /* access modifiers changed from: protected */
    public void setupTexture() {
        this.colorTexture = new Texture((TextureData) new FloatTextureData(this.width, this.height));
        if (Gdx.app.getType() == Application.ApplicationType.Desktop || Gdx.app.getType() == Application.ApplicationType.Applet) {
            this.colorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } else {
            this.colorTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        this.colorTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }
}
