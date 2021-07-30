package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;

public class PolygonSprite {
    private Rectangle bounds = new Rectangle();
    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private boolean dirty;
    private float height;
    private float originX;
    private float originY;
    PolygonRegion region;
    private float rotation;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float[] vertices;
    private float width;

    /* renamed from: x */
    private float f81x;

    /* renamed from: y */
    private float f82y;

    public PolygonSprite(PolygonRegion region2) {
        setRegion(region2);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setSize((float) region2.getRegion().getRegionWidth(), (float) region2.getRegion().getRegionHeight());
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public PolygonSprite(PolygonSprite sprite) {
        set(sprite);
    }

    public void set(PolygonSprite sprite) {
        if (sprite == null) {
            throw new IllegalArgumentException("sprite cannot be null.");
        }
        setRegion(sprite.region);
        this.f81x = sprite.f81x;
        this.f82y = sprite.f82y;
        this.width = sprite.width;
        this.height = sprite.height;
        this.originX = sprite.originX;
        this.originY = sprite.originY;
        this.rotation = sprite.rotation;
        this.scaleX = sprite.scaleX;
        this.scaleY = sprite.scaleY;
        this.color.set(sprite.color);
        this.dirty = sprite.dirty;
    }

    public void setBounds(float x, float y, float width2, float height2) {
        this.f81x = x;
        this.f82y = y;
        this.width = width2;
        this.height = height2;
        this.dirty = true;
    }

    public void setSize(float width2, float height2) {
        this.width = width2;
        this.height = height2;
        this.dirty = true;
    }

    public void setPosition(float x, float y) {
        translate(x - this.f81x, y - this.f82y);
    }

    public void setX(float x) {
        translateX(x - this.f81x);
    }

    public void setY(float y) {
        translateY(y - this.f82y);
    }

    public void translateX(float xAmount) {
        this.f81x += xAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            for (int i = 0; i < vertices2.length; i += 5) {
                vertices2[i] = vertices2[i] + xAmount;
            }
        }
    }

    public void translateY(float yAmount) {
        this.f82y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            for (int i = 0; i < vertices2.length; i += 5) {
                int i2 = i + 1;
                vertices2[i2] = vertices2[i2] + yAmount;
            }
        }
    }

    public void translate(float xAmount, float yAmount) {
        this.f81x += xAmount;
        this.f82y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            for (int i = 0; i < vertices2.length; i += 5) {
                vertices2[i] = vertices2[i] + xAmount;
                int i2 = i + 1;
                vertices2[i2] = vertices2[i2] + yAmount;
            }
        }
    }

    public void setColor(Color tint) {
        float color2 = tint.toFloatBits();
        float[] vertices2 = this.vertices;
        for (int i = 0; i < vertices2.length; i += 5) {
            vertices2[i + 2] = color2;
        }
    }

    public void setColor(float r, float g, float b, float a) {
        float color2 = NumberUtils.intToFloatColor((((int) (255.0f * a)) << 24) | (((int) (255.0f * b)) << 16) | (((int) (255.0f * g)) << 8) | ((int) (255.0f * r)));
        float[] vertices2 = this.vertices;
        for (int i = 0; i < vertices2.length; i += 5) {
            vertices2[i + 2] = color2;
        }
    }

    public void setOrigin(float originX2, float originY2) {
        this.originX = originX2;
        this.originY = originY2;
        this.dirty = true;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
        this.dirty = true;
    }

    public void rotate(float degrees) {
        this.rotation += degrees;
        this.dirty = true;
    }

    public void setScale(float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
        this.dirty = true;
    }

    public void setScale(float scaleX2, float scaleY2) {
        this.scaleX = scaleX2;
        this.scaleY = scaleY2;
        this.dirty = true;
    }

    public void scale(float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        this.dirty = true;
    }

    public float[] getVertices() {
        if (this.dirty) {
            this.dirty = false;
            float worldOriginX = this.f81x + this.originX;
            float worldOriginY = this.f82y + this.originY;
            float sX = this.width / ((float) this.region.getRegion().getRegionWidth());
            float sY = this.height / ((float) this.region.getRegion().getRegionHeight());
            float[] localVertices = this.region.getLocalVertices();
            float cos = MathUtils.cosDeg(this.rotation);
            float sin = MathUtils.sinDeg(this.rotation);
            for (int i = 0; i < localVertices.length; i += 2) {
                float fx = localVertices[i] * sX;
                float fy = localVertices[i + 1] * sY;
                float fx2 = fx - this.originX;
                float fy2 = fy - this.originY;
                if (this.scaleX != 1.0f || ((double) this.scaleY) != 1.0d) {
                    fx2 *= this.scaleX;
                    fy2 *= this.scaleY;
                }
                float[] fArr = this.vertices;
                int i2 = (i / 2) * 5;
                fArr[i2] = ((cos * fx2) - (sin * fy2)) + worldOriginX;
                this.vertices[((i / 2) * 5) + 1] = (sin * fx2) + (cos * fy2) + worldOriginY;
            }
        }
        return this.vertices;
    }

    public Rectangle getBoundingRectangle() {
        float[] vertices2 = getVertices();
        float minx = vertices2[0];
        float miny = vertices2[1];
        float maxx = vertices2[0];
        float maxy = vertices2[1];
        for (int i = 0; i < vertices2.length; i += 5) {
            if (minx > vertices2[i]) {
                minx = vertices2[i];
            }
            if (maxx < vertices2[i]) {
                maxx = vertices2[i];
            }
            if (miny > vertices2[i + 1]) {
                miny = vertices2[i + 1];
            }
            if (maxy < vertices2[i + 1]) {
                maxy = vertices2[i + 1];
            }
        }
        this.bounds.f161x = minx;
        this.bounds.f162y = miny;
        this.bounds.width = maxx - minx;
        this.bounds.height = maxy - miny;
        return this.bounds;
    }

    public void draw(PolygonSpriteBatch spriteBatch) {
        spriteBatch.draw(this.region, getVertices(), 0, this.vertices.length);
    }

    public void draw(PolygonSpriteBatch spriteBatch, float alphaModulation) {
        Color color2 = getColor();
        float oldAlpha = color2.f67a;
        color2.f67a *= alphaModulation;
        setColor(color2);
        draw(spriteBatch);
        color2.f67a = oldAlpha;
        setColor(color2);
    }

    public float getX() {
        return this.f81x;
    }

    public float getY() {
        return this.f82y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getOriginX() {
        return this.originX;
    }

    public float getOriginY() {
        return this.originY;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public Color getColor() {
        int intBits = NumberUtils.floatToIntColor(this.vertices[2]);
        Color color2 = this.color;
        color2.f70r = ((float) (intBits & 255)) / 255.0f;
        color2.f69g = ((float) ((intBits >>> 8) & 255)) / 255.0f;
        color2.f68b = ((float) ((intBits >>> 16) & 255)) / 255.0f;
        color2.f67a = ((float) ((intBits >>> 24) & 255)) / 255.0f;
        return color2;
    }

    public void setRegion(PolygonRegion region2) {
        this.region = region2;
        float[] localVertices = region2.getLocalVertices();
        float[] localTextureCoords = region2.getTextureCoords();
        if (this.vertices == null || localVertices.length != this.vertices.length) {
            this.vertices = new float[((localVertices.length / 2) * 5)];
        }
        for (int i = 0; i < localVertices.length / 2; i++) {
            this.vertices[i * 5] = localVertices[i * 2];
            this.vertices[(i * 5) + 1] = localVertices[(i * 2) + 1];
            this.vertices[(i * 5) + 2] = this.color.toFloatBits();
            this.vertices[(i * 5) + 3] = localTextureCoords[i * 2];
            this.vertices[(i * 5) + 4] = localTextureCoords[(i * 2) + 1];
        }
    }
}
