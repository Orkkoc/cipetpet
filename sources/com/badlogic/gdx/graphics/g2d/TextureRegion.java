package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;
import java.lang.reflect.Array;

public class TextureRegion {
    int regionHeight;
    int regionWidth;
    Texture texture;

    /* renamed from: u */
    float f106u;

    /* renamed from: u2 */
    float f107u2;

    /* renamed from: v */
    float f108v;

    /* renamed from: v2 */
    float f109v2;

    public TextureRegion() {
    }

    public TextureRegion(Texture texture2) {
        if (texture2 == null) {
            throw new IllegalArgumentException("texture cannot be null.");
        }
        this.texture = texture2;
        setRegion(0, 0, texture2.getWidth(), texture2.getHeight());
    }

    public TextureRegion(Texture texture2, int width, int height) {
        this.texture = texture2;
        setRegion(0, 0, width, height);
    }

    public TextureRegion(Texture texture2, int x, int y, int width, int height) {
        this.texture = texture2;
        setRegion(x, y, width, height);
    }

    public TextureRegion(Texture texture2, float u, float v, float u2, float v2) {
        this.texture = texture2;
        setRegion(u, v, u2, v2);
    }

    public TextureRegion(TextureRegion region) {
        setRegion(region);
    }

    public TextureRegion(TextureRegion region, int x, int y, int width, int height) {
        setRegion(region, x, y, width, height);
    }

    public void setRegion(Texture texture2) {
        this.texture = texture2;
        setRegion(0, 0, texture2.getWidth(), texture2.getHeight());
    }

    public void setRegion(int x, int y, int width, int height) {
        float invTexWidth = 1.0f / ((float) this.texture.getWidth());
        float invTexHeight = 1.0f / ((float) this.texture.getHeight());
        setRegion(((float) x) * invTexWidth, ((float) y) * invTexHeight, ((float) (x + width)) * invTexWidth, ((float) (y + height)) * invTexHeight);
        this.regionWidth = Math.abs(width);
        this.regionHeight = Math.abs(height);
    }

    public void setRegion(float u, float v, float u2, float v2) {
        this.f106u = u;
        this.f108v = v;
        this.f107u2 = u2;
        this.f109v2 = v2;
        this.regionWidth = Math.round(Math.abs(u2 - u) * ((float) this.texture.getWidth()));
        this.regionHeight = Math.round(Math.abs(v2 - v) * ((float) this.texture.getHeight()));
    }

    public void setRegion(TextureRegion region) {
        this.texture = region.texture;
        setRegion(region.f106u, region.f108v, region.f107u2, region.f109v2);
    }

    public void setRegion(TextureRegion region, int x, int y, int width, int height) {
        this.texture = region.texture;
        setRegion(region.getRegionX() + x, region.getRegionY() + y, width, height);
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture2) {
        this.texture = texture2;
    }

    public float getU() {
        return this.f106u;
    }

    public void setU(float u) {
        this.f106u = u;
        this.regionWidth = Math.round(Math.abs(this.f107u2 - u) * ((float) this.texture.getWidth()));
    }

    public float getV() {
        return this.f108v;
    }

    public void setV(float v) {
        this.f108v = v;
        this.regionHeight = Math.round(Math.abs(this.f109v2 - v) * ((float) this.texture.getHeight()));
    }

    public float getU2() {
        return this.f107u2;
    }

    public void setU2(float u2) {
        this.f107u2 = u2;
        this.regionWidth = Math.round(Math.abs(u2 - this.f106u) * ((float) this.texture.getWidth()));
    }

    public float getV2() {
        return this.f109v2;
    }

    public void setV2(float v2) {
        this.f109v2 = v2;
        this.regionHeight = Math.round(Math.abs(v2 - this.f108v) * ((float) this.texture.getHeight()));
    }

    public int getRegionX() {
        return Math.round(this.f106u * ((float) this.texture.getWidth()));
    }

    public void setRegionX(int x) {
        setU(((float) x) / ((float) this.texture.getWidth()));
    }

    public int getRegionY() {
        return Math.round(this.f108v * ((float) this.texture.getHeight()));
    }

    public void setRegionY(int y) {
        setV(((float) y) / ((float) this.texture.getHeight()));
    }

    public int getRegionWidth() {
        return this.regionWidth;
    }

    public void setRegionWidth(int width) {
        setU2(this.f106u + (((float) width) / ((float) this.texture.getWidth())));
    }

    public int getRegionHeight() {
        return this.regionHeight;
    }

    public void setRegionHeight(int height) {
        setV2(this.f108v + (((float) height) / ((float) this.texture.getHeight())));
    }

    public void flip(boolean x, boolean y) {
        if (x) {
            float temp = this.f106u;
            this.f106u = this.f107u2;
            this.f107u2 = temp;
        }
        if (y) {
            float temp2 = this.f108v;
            this.f108v = this.f109v2;
            this.f109v2 = temp2;
        }
    }

    public boolean isFlipX() {
        return this.f106u > this.f107u2;
    }

    public boolean isFlipY() {
        return this.f108v > this.f109v2;
    }

    public void scroll(float xAmount, float yAmount) {
        if (xAmount != 0.0f) {
            float width = (this.f107u2 - this.f106u) * ((float) this.texture.getWidth());
            this.f106u = (this.f106u + xAmount) % 1.0f;
            this.f107u2 = this.f106u + (width / ((float) this.texture.getWidth()));
        }
        if (yAmount != 0.0f) {
            float height = (this.f109v2 - this.f108v) * ((float) this.texture.getHeight());
            this.f108v = (this.f108v + yAmount) % 1.0f;
            this.f109v2 = this.f108v + (height / ((float) this.texture.getHeight()));
        }
    }

    public TextureRegion[][] split(int tileWidth, int tileHeight) {
        int x = getRegionX();
        int y = getRegionY();
        int width = this.regionWidth;
        int rows = this.regionHeight / tileHeight;
        int cols = width / tileWidth;
        int startX = x;
        TextureRegion[][] tiles = (TextureRegion[][]) Array.newInstance(TextureRegion.class, new int[]{rows, cols});
        int row = 0;
        while (row < rows) {
            int x2 = startX;
            int col = 0;
            while (col < cols) {
                tiles[row][col] = new TextureRegion(this.texture, x2, y, tileWidth, tileHeight);
                col++;
                x2 += tileWidth;
            }
            row++;
            y += tileHeight;
        }
        return tiles;
    }

    public static TextureRegion[][] split(Texture texture2, int tileWidth, int tileHeight) {
        return new TextureRegion(texture2).split(tileWidth, tileHeight);
    }
}
