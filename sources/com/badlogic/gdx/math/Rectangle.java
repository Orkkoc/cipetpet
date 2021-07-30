package com.badlogic.gdx.math;

import java.io.Serializable;

public class Rectangle implements Serializable {
    private static final long serialVersionUID = 5733252015138115702L;
    public static final Rectangle tmp = new Rectangle();
    public static final Rectangle tmp2 = new Rectangle();
    public float height;
    public float width;

    /* renamed from: x */
    public float f161x;

    /* renamed from: y */
    public float f162y;

    public Rectangle() {
    }

    public Rectangle(float x, float y, float width2, float height2) {
        this.f161x = x;
        this.f162y = y;
        this.width = width2;
        this.height = height2;
    }

    public Rectangle(Rectangle rect) {
        this.f161x = rect.f161x;
        this.f162y = rect.f162y;
        this.width = rect.width;
        this.height = rect.height;
    }

    public float getX() {
        return this.f161x;
    }

    public void setX(float x) {
        this.f161x = x;
    }

    public float getY() {
        return this.f162y;
    }

    public void setY(float y) {
        this.f162y = y;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width2) {
        this.width = width2;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height2) {
        this.height = height2;
    }

    public boolean contains(Rectangle rectangle) {
        float xmin = rectangle.f161x;
        float xmax = xmin + rectangle.width;
        float ymin = rectangle.f162y;
        float ymax = ymin + rectangle.height;
        return xmin > this.f161x && xmin < this.f161x + this.width && xmax > this.f161x && xmax < this.f161x + this.width && ymin > this.f162y && ymin < this.f162y + this.height && ymax > this.f162y && ymax < this.f162y + this.height;
    }

    public boolean overlaps(Rectangle rectangle) {
        return this.f161x <= rectangle.f161x + rectangle.width && this.f161x + this.width >= rectangle.f161x && this.f162y <= rectangle.f162y + rectangle.height && this.f162y + this.height >= rectangle.f162y;
    }

    public void set(float x, float y, float width2, float height2) {
        this.f161x = x;
        this.f162y = y;
        this.width = width2;
        this.height = height2;
    }

    public boolean contains(float x, float y) {
        return this.f161x < x && this.f161x + this.width > x && this.f162y < y && this.f162y + this.height > y;
    }

    public void set(Rectangle rect) {
        this.f161x = rect.f161x;
        this.f162y = rect.f162y;
        this.width = rect.width;
        this.height = rect.height;
    }

    public void merge(Rectangle rect) {
        float minX = Math.min(this.f161x, rect.f161x);
        float maxX = Math.max(this.f161x + this.width, rect.f161x + rect.width);
        this.f161x = minX;
        this.width = maxX - minX;
        float minY = Math.min(this.f162y, rect.f162y);
        float maxY = Math.max(this.f162y + this.height, rect.f162y + rect.height);
        this.f162y = minY;
        this.height = maxY - minY;
    }

    public String toString() {
        return this.f161x + "," + this.f162y + "," + this.width + "," + this.height;
    }
}
