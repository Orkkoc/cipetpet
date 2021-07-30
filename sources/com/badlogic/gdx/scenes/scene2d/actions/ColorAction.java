package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;

public class ColorAction extends TemporalAction {
    private Color color;
    private final Color end = new Color();
    private float startA;
    private float startB;
    private float startG;
    private float startR;

    /* access modifiers changed from: protected */
    public void begin() {
        if (this.color == null) {
            this.color = this.actor.getColor();
        }
        this.startR = this.color.f70r;
        this.startG = this.color.f69g;
        this.startB = this.color.f68b;
        this.startA = this.color.f67a;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.color.set(this.startR + ((this.end.f70r - this.startR) * percent), this.startG + ((this.end.f69g - this.startG) * percent), this.startB + ((this.end.f68b - this.startB) * percent), this.startA + ((this.end.f67a - this.startA) * percent));
    }

    public void reset() {
        super.reset();
        this.color = null;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color2) {
        this.color = color2;
    }

    public Color getEndColor() {
        return this.end;
    }

    public void setEndColor(Color color2) {
        this.end.set(color2);
    }
}
