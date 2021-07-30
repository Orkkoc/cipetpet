package com.badlogic.gdx.scenes.scene2d.actions;

public class ScaleToAction extends TemporalAction {
    private float endX;
    private float endY;
    private float startX;
    private float startY;

    /* access modifiers changed from: protected */
    public void begin() {
        this.startX = this.actor.getScaleX();
        this.startY = this.actor.getScaleY();
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.actor.setScale(this.startX + ((this.endX - this.startX) * percent), this.startY + ((this.endY - this.startY) * percent));
    }

    public void setScale(float x, float y) {
        this.endX = x;
        this.endY = y;
    }

    public void setScale(float scale) {
        this.endX = scale;
        this.endY = scale;
    }

    public float getX() {
        return this.endX;
    }

    public void setX(float x) {
        this.endX = x;
    }

    public float getY() {
        return this.endY;
    }

    public void setY(float y) {
        this.endY = y;
    }
}
