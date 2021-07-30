package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureRegionDrawable extends BaseDrawable {
    private TextureRegion region;

    public TextureRegionDrawable() {
    }

    public TextureRegionDrawable(TextureRegion region2) {
        setRegion(region2);
    }

    public TextureRegionDrawable(TextureRegionDrawable drawable) {
        super(drawable);
        setRegion(drawable.region);
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(this.region, x, y, width, height);
    }

    public void setRegion(TextureRegion region2) {
        this.region = region2;
        setMinWidth((float) region2.getRegionWidth());
        setMinHeight((float) region2.getRegionHeight());
    }

    public TextureRegion getRegion() {
        return this.region;
    }
}
