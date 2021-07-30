package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BaseDrawable implements Drawable {
    private float bottomHeight;
    private float leftWidth;
    private float minHeight;
    private float minWidth;
    private float rightWidth;
    private float topHeight;

    public BaseDrawable() {
    }

    public BaseDrawable(Drawable drawable) {
        this.leftWidth = drawable.getLeftWidth();
        this.rightWidth = drawable.getRightWidth();
        this.topHeight = drawable.getTopHeight();
        this.bottomHeight = drawable.getBottomHeight();
        this.minWidth = drawable.getMinWidth();
        this.minHeight = drawable.getMinHeight();
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
    }

    public float getLeftWidth() {
        return this.leftWidth;
    }

    public void setLeftWidth(float leftWidth2) {
        this.leftWidth = leftWidth2;
    }

    public float getRightWidth() {
        return this.rightWidth;
    }

    public void setRightWidth(float rightWidth2) {
        this.rightWidth = rightWidth2;
    }

    public float getTopHeight() {
        return this.topHeight;
    }

    public void setTopHeight(float topHeight2) {
        this.topHeight = topHeight2;
    }

    public float getBottomHeight() {
        return this.bottomHeight;
    }

    public void setBottomHeight(float bottomHeight2) {
        this.bottomHeight = bottomHeight2;
    }

    public float getMinWidth() {
        return this.minWidth;
    }

    public void setMinWidth(float minWidth2) {
        this.minWidth = minWidth2;
    }

    public float getMinHeight() {
        return this.minHeight;
    }

    public void setMinHeight(float minHeight2) {
        this.minHeight = minHeight2;
    }
}
