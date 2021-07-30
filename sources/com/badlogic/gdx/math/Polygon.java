package com.badlogic.gdx.math;

public class Polygon {
    private Rectangle bounds;
    private boolean dirty = true;
    private final float[] localVertices;
    private float originX;
    private float originY;
    private float rotation;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float[] worldVertices;

    /* renamed from: x */
    private float f155x;

    /* renamed from: y */
    private float f156y;

    public Polygon(float[] vertices) {
        if (vertices.length < 6) {
            throw new IllegalArgumentException("polygons must contain at least 3 points.");
        }
        this.localVertices = vertices;
    }

    public float[] getVertices() {
        return this.localVertices;
    }

    public float[] getTransformedVertices() {
        if (!this.dirty) {
            return this.worldVertices;
        }
        this.dirty = false;
        float[] localVertices2 = this.localVertices;
        if (this.worldVertices == null || this.worldVertices.length < localVertices2.length) {
            this.worldVertices = new float[localVertices2.length];
        }
        float[] worldVertices2 = this.worldVertices;
        float positionX = this.f155x;
        float positionY = this.f156y;
        float originX2 = this.originX;
        float originY2 = this.originY;
        float scaleX2 = this.scaleX;
        float scaleY2 = this.scaleY;
        boolean scale = (scaleX2 == 1.0f && scaleY2 == 1.0f) ? false : true;
        float rotation2 = this.rotation;
        float cos = MathUtils.cosDeg(rotation2);
        float sin = MathUtils.sinDeg(rotation2);
        int n = localVertices2.length;
        for (int i = 0; i < n; i += 2) {
            float x = localVertices2[i] - originX2;
            float y = localVertices2[i + 1] - originY2;
            if (scale) {
                x *= scaleX2;
                y *= scaleY2;
            }
            if (rotation2 != 0.0f) {
                float oldX = x;
                x = (cos * x) - (sin * y);
                y = (sin * oldX) + (cos * y);
            }
            worldVertices2[i] = positionX + x + originX2;
            worldVertices2[i + 1] = positionY + y + originY2;
        }
        return worldVertices2;
    }

    public void setOrigin(float originX2, float originY2) {
        this.originX = originX2;
        this.originY = originY2;
        this.dirty = true;
    }

    public void setPosition(float x, float y) {
        this.f155x = x;
        this.f156y = y;
        this.dirty = true;
    }

    public void translate(float x, float y) {
        this.f155x += x;
        this.f156y += y;
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

    public void dirty() {
        this.dirty = true;
    }

    public float area() {
        float area = 0.0f;
        float[] vertices = getTransformedVertices();
        int numFloats = vertices.length;
        for (int i = 0; i < numFloats; i += 2) {
            int y2 = (i + 3) % numFloats;
            area = (area + (vertices[i] * vertices[y2])) - (vertices[(i + 2) % numFloats] * vertices[i + 1]);
        }
        return area * 0.5f;
    }

    public Rectangle getBoundingRectangle() {
        float[] vertices = getTransformedVertices();
        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];
        int numFloats = vertices.length;
        for (int i = 2; i < numFloats; i += 2) {
            if (minX > vertices[i]) {
                minX = vertices[i];
            }
            if (minY > vertices[i + 1]) {
                minY = vertices[i + 1];
            }
            if (maxX < vertices[i]) {
                maxX = vertices[i];
            }
            if (maxY < vertices[i + 1]) {
                maxY = vertices[i + 1];
            }
        }
        if (this.bounds == null) {
            this.bounds = new Rectangle();
        }
        this.bounds.f161x = minX;
        this.bounds.f162y = minY;
        this.bounds.width = maxX - minX;
        this.bounds.height = maxY - minY;
        return this.bounds;
    }

    public boolean contains(float x, float y) {
        float[] vertices = getTransformedVertices();
        int numFloats = vertices.length;
        int intersects = 0;
        for (int i = 0; i < numFloats; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2 = vertices[(i + 2) % numFloats];
            float y2 = vertices[(i + 3) % numFloats];
            if (((y1 <= y && y < y2) || (y2 <= y && y < y1)) && x < (((x2 - x1) / (y2 - y1)) * (y - y1)) + x1) {
                intersects++;
            }
        }
        if ((intersects & 1) == 1) {
            return true;
        }
        return false;
    }

    public float getX() {
        return this.f155x;
    }

    public float getY() {
        return this.f156y;
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
}
