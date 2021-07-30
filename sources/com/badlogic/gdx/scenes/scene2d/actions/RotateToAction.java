package com.badlogic.gdx.scenes.scene2d.actions;

public class RotateToAction extends TemporalAction {
    private float end;
    private float start;

    /* access modifiers changed from: protected */
    public void begin() {
        this.start = this.actor.getRotation();
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.actor.setRotation(this.start + ((this.end - this.start) * percent));
    }

    public float getRotation() {
        return this.end;
    }

    public void setRotation(float rotation) {
        this.end = rotation;
    }
}
