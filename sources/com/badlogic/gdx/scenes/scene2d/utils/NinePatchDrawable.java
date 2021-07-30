package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NinePatchDrawable extends BaseDrawable {
    private NinePatch patch;

    public NinePatchDrawable() {
    }

    public NinePatchDrawable(NinePatch patch2) {
        setPatch(patch2);
    }

    public NinePatchDrawable(NinePatchDrawable drawable) {
        super(drawable);
        setPatch(drawable.patch);
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        this.patch.draw(batch, x, y, width, height);
    }

    public void setPatch(NinePatch patch2) {
        this.patch = patch2;
        setMinWidth(patch2.getTotalWidth());
        setMinHeight(patch2.getTotalHeight());
        setTopHeight(patch2.getPadTop());
        setRightWidth(patch2.getPadRight());
        setBottomHeight(patch2.getPadBottom());
        setLeftWidth(patch2.getPadLeft());
    }

    public NinePatch getPatch() {
        return this.patch;
    }
}
