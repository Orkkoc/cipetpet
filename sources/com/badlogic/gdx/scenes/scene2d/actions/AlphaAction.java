package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;

public class AlphaAction extends TemporalAction {
    private Color color;
    private float end;
    private float start;

    /* access modifiers changed from: protected */
    public void begin() {
        if (this.color == null) {
            this.color = this.actor.getColor();
        }
        this.start = this.color.f67a;
    }

    /* access modifiers changed from: protected */
    public void update(float percent) {
        this.color.f67a = this.start + ((this.end - this.start) * percent);
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

    public float getAlpha() {
        return this.end;
    }

    public void setAlpha(float alpha) {
        this.end = alpha;
    }
}
