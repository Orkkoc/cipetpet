package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.NumberUtils;

public class Sprite extends TextureRegion {
    static final int SPRITE_SIZE = 20;
    static final int VERTEX_SIZE = 5;
    private Rectangle bounds;
    private final Color color;
    private boolean dirty;
    float height;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX;
    private float scaleY;
    final float[] vertices;
    float width;

    /* renamed from: x */
    private float f83x;

    /* renamed from: y */
    private float f84y;

    public Sprite() {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public Sprite(Texture texture) {
        this(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    public Sprite(Texture texture, int srcWidth, int srcHeight) {
        this(texture, 0, 0, srcWidth, srcHeight);
    }

    public Sprite(Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        if (texture == null) {
            throw new IllegalArgumentException("texture cannot be null.");
        }
        this.texture = texture;
        setRegion(srcX, srcY, srcWidth, srcHeight);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setSize((float) Math.abs(srcWidth), (float) Math.abs(srcHeight));
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public Sprite(TextureRegion region) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        setRegion(region);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setSize((float) region.getRegionWidth(), (float) region.getRegionHeight());
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public Sprite(TextureRegion region, int srcX, int srcY, int srcWidth, int srcHeight) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        setRegion(region, srcX, srcY, srcWidth, srcHeight);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setSize((float) Math.abs(srcWidth), (float) Math.abs(srcHeight));
        setOrigin(this.width / 2.0f, this.height / 2.0f);
    }

    public Sprite(Sprite sprite) {
        this.vertices = new float[20];
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dirty = true;
        set(sprite);
    }

    public void set(Sprite sprite) {
        if (sprite == null) {
            throw new IllegalArgumentException("sprite cannot be null.");
        }
        System.arraycopy(sprite.vertices, 0, this.vertices, 0, 20);
        this.texture = sprite.texture;
        this.f106u = sprite.f106u;
        this.f108v = sprite.f108v;
        this.f107u2 = sprite.f107u2;
        this.f109v2 = sprite.f109v2;
        this.f83x = sprite.f83x;
        this.f84y = sprite.f84y;
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
        this.f83x = x;
        this.f84y = y;
        this.width = width2;
        this.height = height2;
        if (!this.dirty) {
            float x2 = x + width2;
            float y2 = y + height2;
            float[] vertices2 = this.vertices;
            vertices2[0] = x;
            vertices2[1] = y;
            vertices2[5] = x;
            vertices2[6] = y2;
            vertices2[10] = x2;
            vertices2[11] = y2;
            vertices2[15] = x2;
            vertices2[16] = y;
            if (this.rotation != 0.0f || this.scaleX != 1.0f || this.scaleY != 1.0f) {
                this.dirty = true;
            }
        }
    }

    public void setSize(float width2, float height2) {
        this.width = width2;
        this.height = height2;
        if (!this.dirty) {
            float x2 = this.f83x + width2;
            float y2 = this.f84y + height2;
            float[] vertices2 = this.vertices;
            vertices2[0] = this.f83x;
            vertices2[1] = this.f84y;
            vertices2[5] = this.f83x;
            vertices2[6] = y2;
            vertices2[10] = x2;
            vertices2[11] = y2;
            vertices2[15] = x2;
            vertices2[16] = this.f84y;
            if (this.rotation != 0.0f || this.scaleX != 1.0f || this.scaleY != 1.0f) {
                this.dirty = true;
            }
        }
    }

    public void setPosition(float x, float y) {
        translate(x - this.f83x, y - this.f84y);
    }

    public void setX(float x) {
        translateX(x - this.f83x);
    }

    public void setY(float y) {
        translateY(y - this.f84y);
    }

    public void translateX(float xAmount) {
        this.f83x += xAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            vertices2[0] = vertices2[0] + xAmount;
            vertices2[5] = vertices2[5] + xAmount;
            vertices2[10] = vertices2[10] + xAmount;
            vertices2[15] = vertices2[15] + xAmount;
        }
    }

    public void translateY(float yAmount) {
        this.f84y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            vertices2[1] = vertices2[1] + yAmount;
            vertices2[6] = vertices2[6] + yAmount;
            vertices2[11] = vertices2[11] + yAmount;
            vertices2[16] = vertices2[16] + yAmount;
        }
    }

    public void translate(float xAmount, float yAmount) {
        this.f83x += xAmount;
        this.f84y += yAmount;
        if (!this.dirty) {
            float[] vertices2 = this.vertices;
            vertices2[0] = vertices2[0] + xAmount;
            vertices2[1] = vertices2[1] + yAmount;
            vertices2[5] = vertices2[5] + xAmount;
            vertices2[6] = vertices2[6] + yAmount;
            vertices2[10] = vertices2[10] + xAmount;
            vertices2[11] = vertices2[11] + yAmount;
            vertices2[15] = vertices2[15] + xAmount;
            vertices2[16] = vertices2[16] + yAmount;
        }
    }

    public void setColor(Color tint) {
        float color2 = tint.toFloatBits();
        float[] vertices2 = this.vertices;
        vertices2[2] = color2;
        vertices2[7] = color2;
        vertices2[12] = color2;
        vertices2[17] = color2;
    }

    public void setColor(float r, float g, float b, float a) {
        float color2 = NumberUtils.intToFloatColor((((int) (255.0f * a)) << 24) | (((int) (255.0f * b)) << 16) | (((int) (255.0f * g)) << 8) | ((int) (255.0f * r)));
        float[] vertices2 = this.vertices;
        vertices2[2] = color2;
        vertices2[7] = color2;
        vertices2[12] = color2;
        vertices2[17] = color2;
    }

    public void setColor(float color2) {
        float[] vertices2 = this.vertices;
        vertices2[2] = color2;
        vertices2[7] = color2;
        vertices2[12] = color2;
        vertices2[17] = color2;
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

    public void rotate90(boolean clockwise) {
        float[] vertices2 = this.vertices;
        if (clockwise) {
            float temp = vertices2[4];
            vertices2[4] = vertices2[19];
            vertices2[19] = vertices2[14];
            vertices2[14] = vertices2[9];
            vertices2[9] = temp;
            float temp2 = vertices2[3];
            vertices2[3] = vertices2[18];
            vertices2[18] = vertices2[13];
            vertices2[13] = vertices2[8];
            vertices2[8] = temp2;
            return;
        }
        float temp3 = vertices2[4];
        vertices2[4] = vertices2[9];
        vertices2[9] = vertices2[14];
        vertices2[14] = vertices2[19];
        vertices2[19] = temp3;
        float temp4 = vertices2[3];
        vertices2[3] = vertices2[8];
        vertices2[8] = vertices2[13];
        vertices2[13] = vertices2[18];
        vertices2[18] = temp4;
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
            float[] vertices2 = this.vertices;
            float localX = -this.originX;
            float localY = -this.originY;
            float localX2 = localX + this.width;
            float localY2 = localY + this.height;
            float worldOriginX = this.f83x - localX;
            float worldOriginY = this.f84y - localY;
            if (!(this.scaleX == 1.0f && this.scaleY == 1.0f)) {
                localX *= this.scaleX;
                localY *= this.scaleY;
                localX2 *= this.scaleX;
                localY2 *= this.scaleY;
            }
            if (this.rotation != 0.0f) {
                float cos = MathUtils.cosDeg(this.rotation);
                float sin = MathUtils.sinDeg(this.rotation);
                float localXCos = localX * cos;
                float localXSin = localX * sin;
                float localY2Cos = localY2 * cos;
                float localY2Sin = localY2 * sin;
                float x1 = (localXCos - (localY * sin)) + worldOriginX;
                float y1 = (localY * cos) + localXSin + worldOriginY;
                vertices2[0] = x1;
                vertices2[1] = y1;
                float x2 = (localXCos - localY2Sin) + worldOriginX;
                float y2 = localY2Cos + localXSin + worldOriginY;
                vertices2[5] = x2;
                vertices2[6] = y2;
                float x3 = ((localX2 * cos) - localY2Sin) + worldOriginX;
                float y3 = localY2Cos + (localX2 * sin) + worldOriginY;
                vertices2[10] = x3;
                vertices2[11] = y3;
                vertices2[15] = (x3 - x2) + x1;
                vertices2[16] = y3 - (y2 - y1);
            } else {
                float x12 = localX + worldOriginX;
                float y12 = localY + worldOriginY;
                float x22 = localX2 + worldOriginX;
                float y22 = localY2 + worldOriginY;
                vertices2[0] = x12;
                vertices2[1] = y12;
                vertices2[5] = x12;
                vertices2[6] = y22;
                vertices2[10] = x22;
                vertices2[11] = y22;
                vertices2[15] = x22;
                vertices2[16] = y12;
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
        if (minx > vertices2[5]) {
            minx = vertices2[5];
        }
        if (minx > vertices2[10]) {
            minx = vertices2[10];
        }
        if (minx > vertices2[15]) {
            minx = vertices2[15];
        }
        if (maxx < vertices2[5]) {
            maxx = vertices2[5];
        }
        if (maxx < vertices2[10]) {
            maxx = vertices2[10];
        }
        if (maxx < vertices2[15]) {
            maxx = vertices2[15];
        }
        if (miny > vertices2[6]) {
            miny = vertices2[6];
        }
        if (miny > vertices2[11]) {
            miny = vertices2[11];
        }
        if (miny > vertices2[16]) {
            miny = vertices2[16];
        }
        if (maxy < vertices2[6]) {
            maxy = vertices2[6];
        }
        if (maxy < vertices2[11]) {
            maxy = vertices2[11];
        }
        if (maxy < vertices2[16]) {
            maxy = vertices2[16];
        }
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        this.bounds.f161x = minx;
        this.bounds.f162y = miny;
        this.bounds.width = maxx - minx;
        this.bounds.height = maxy - miny;
        return this.bounds;
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(this.texture, getVertices(), 0, 20);
    }

    public void draw(SpriteBatch spriteBatch, float alphaModulation) {
        Color color2 = getColor();
        float oldAlpha = color2.f67a;
        color2.f67a *= alphaModulation;
        setColor(color2);
        draw(spriteBatch);
        color2.f67a = oldAlpha;
        setColor(color2);
    }

    public float getX() {
        return this.f83x;
    }

    public float getY() {
        return this.f84y;
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
        float f = this.vertices[2];
        int intBits = NumberUtils.floatToIntColor(this.vertices[2]);
        Color color2 = this.color;
        color2.f70r = ((float) (intBits & 255)) / 255.0f;
        color2.f69g = ((float) ((intBits >>> 8) & 255)) / 255.0f;
        color2.f68b = ((float) ((intBits >>> 16) & 255)) / 255.0f;
        color2.f67a = ((float) ((intBits >>> 24) & 255)) / 255.0f;
        return color2;
    }

    public void setRegion(float u, float v, float u2, float v2) {
        super.setRegion(u, v, u2, v2);
        float[] vertices2 = this.vertices;
        vertices2[3] = u;
        vertices2[4] = v2;
        vertices2[8] = u;
        vertices2[9] = v;
        vertices2[13] = u2;
        vertices2[14] = v;
        vertices2[18] = u2;
        vertices2[19] = v2;
    }

    public void setU(float u) {
        super.setU(u);
        this.vertices[3] = u;
        this.vertices[8] = u;
    }

    public void setV(float v) {
        super.setV(v);
        this.vertices[9] = v;
        this.vertices[14] = v;
    }

    public void setU2(float u2) {
        super.setU2(u2);
        this.vertices[13] = u2;
        this.vertices[18] = u2;
    }

    public void setV2(float v2) {
        super.setV2(v2);
        this.vertices[4] = v2;
        this.vertices[19] = v2;
    }

    public void flip(boolean x, boolean y) {
        super.flip(x, y);
        float[] vertices2 = this.vertices;
        if (x) {
            float temp = vertices2[3];
            vertices2[3] = vertices2[13];
            vertices2[13] = temp;
            float temp2 = vertices2[8];
            vertices2[8] = vertices2[18];
            vertices2[18] = temp2;
        }
        if (y) {
            float temp3 = vertices2[4];
            vertices2[4] = vertices2[14];
            vertices2[14] = temp3;
            float temp4 = vertices2[9];
            vertices2[9] = vertices2[19];
            vertices2[19] = temp4;
        }
    }

    public void scroll(float xAmount, float yAmount) {
        float[] vertices2 = this.vertices;
        if (xAmount != 0.0f) {
            float u = (vertices2[3] + xAmount) % 1.0f;
            float u2 = u + (this.width / ((float) this.texture.getWidth()));
            this.f106u = u;
            this.f107u2 = u2;
            vertices2[3] = u;
            vertices2[8] = u;
            vertices2[13] = u2;
            vertices2[18] = u2;
        }
        if (yAmount != 0.0f) {
            float v = (vertices2[9] + yAmount) % 1.0f;
            float v2 = v + (this.height / ((float) this.texture.getHeight()));
            this.f108v = v;
            this.f109v2 = v2;
            vertices2[4] = v2;
            vertices2[9] = v;
            vertices2[14] = v;
            vertices2[19] = v2;
        }
    }
}
