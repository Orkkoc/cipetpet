package com.badlogic.gdx.scenes.scene2d.actions;

public class IntAction extends TemporalAction {
    private int end;
    private int start;
    private int value;

    public IntAction() {
        this.start = 0;
        this.end = 1;
    }

    public IntAction(int start2, int end2) {
        this.start = start2;
        this.end = end2;
    }

    /* access modifiers changed from: protected */
    public void begin() {
        this.value = this.start;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.value = (int) (((float) this.start) + (((float) (this.end - this.start)) * percent));
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value2) {
        this.value = value2;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start2) {
        this.start = start2;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end2) {
        this.end = end2;
    }
}
