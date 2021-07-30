package com.badlogic.gdx.scenes.scene2d.actions;

public class MoveToAction extends TemporalAction {
    private float endX;
    private float endY;
    private float startX;
    private float startY;

    /* access modifiers changed from: protected */
    public void begin() {
        this.startX = this.actor.getX();
        this.startY = this.actor.getY();
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.actor.setPosition(this.startX + ((this.endX - this.startX) * percent), this.startY + ((this.endY - this.startY) * percent));
    }

    public void setPosition(float x, float y) {
        this.endX = x;
        this.endY = y;
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
