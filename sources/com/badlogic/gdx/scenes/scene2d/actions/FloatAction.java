package com.badlogic.gdx.scenes.scene2d.actions;

public class FloatAction extends TemporalAction {
    private float end;
    private float start;
    private float value;

    public FloatAction() {
        this.start = 0.0f;
        this.end = 1.0f;
    }

    public FloatAction(float start2, float end2) {
        this.start = start2;
        this.end = end2;
    }

    /* access modifiers changed from: protected */
    public void begin() {
        this.value = this.start;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.value = this.start + ((this.end - this.start) * percent);
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value2) {
        this.value = value2;
    }

    public float getStart() {
        return this.start;
    }

    public void setStart(float start2) {
        this.start = start2;
    }

    public float getEnd() {
        return this.end;
    }

    public void setEnd(float end2) {
        this.end = end2;
    }
}
