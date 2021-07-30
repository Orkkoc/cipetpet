package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Image */
public class Image extends Widget {
    private int align;
    private Drawable drawable;
    private float imageHeight;
    private float imageWidth;
    private float imageX;
    private float imageY;
    private Scaling scaling;

    public Image() {
        this((Drawable) null);
    }

    public Image(NinePatch patch) {
        this(new NinePatchDrawable(patch), Scaling.stretch, 1);
    }

    public Image(TextureRegion region) {
        this(new TextureRegionDrawable(region), Scaling.stretch, 1);
    }

    public Image(Texture texture) {
        this((Drawable) new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public Image(Skin skin, String drawableName) {
        this(skin.getDrawable(drawableName), Scaling.stretch, 1);
    }

    public Image(Drawable drawable2) {
        this(drawable2, Scaling.stretch, 1);
    }

    public Image(Drawable drawable2, Scaling scaling2) {
        this(drawable2, scaling2, 1);
    }

    public Image(Drawable drawable2, Scaling scaling2, int align2) {
        this.align = 1;
        setDrawable(drawable2);
        this.scaling = scaling2;
        this.align = align2;
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public void layout() {
        if (this.drawable != null) {
            float regionWidth = this.drawable.getMinWidth();
            float regionHeight = this.drawable.getMinHeight();
            float width = getWidth();
            float height = getHeight();
            Vector2 size = this.scaling.apply(regionWidth, regionHeight, width, height);
            this.imageWidth = size.f165x;
            this.imageHeight = size.f166y;
            if ((this.align & 8) != 0) {
                this.imageX = 0.0f;
            } else if ((this.align & 16) != 0) {
                this.imageX = (float) ((int) (width - this.imageWidth));
            } else {
                this.imageX = (float) ((int) ((width / 2.0f) - (this.imageWidth / 2.0f)));
            }
            if ((this.align & 2) != 0) {
                this.imageY = (float) ((int) (height - this.imageHeight));
            } else if ((this.align & 4) != 0) {
                this.imageY = 0.0f;
            } else {
                this.imageY = (float) ((int) ((height / 2.0f) - (this.imageHeight / 2.0f)));
            }
        }
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        validate();
        Color color = getColor();
        batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
        float x = getX();
        float y = getY();
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        if (this.drawable == null) {
            return;
        }
        if (this.drawable.getClass() == TextureRegionDrawable.class) {
            TextureRegion region = ((TextureRegionDrawable) this.drawable).getRegion();
            float rotation = getRotation();
            if (scaleX == 1.0f && scaleY == 1.0f && rotation == 0.0f) {
                batch.draw(region, x + this.imageX, y + this.imageY, this.imageWidth, this.imageHeight);
                return;
            }
            batch.draw(region, x + this.imageX, y + this.imageY, getOriginX() - this.imageX, getOriginY() - this.imageY, this.imageWidth, this.imageHeight, scaleX, scaleY, rotation);
            return;
        }
        this.drawable.draw(batch, x + this.imageX, y + this.imageY, this.imageWidth * scaleX, this.imageHeight * scaleY);
    }

    public void setDrawable(Drawable drawable2) {
        if (drawable2 != null) {
            if (this.drawable != drawable2) {
                if (!(getPrefWidth() == drawable2.getMinWidth() && getPrefHeight() == drawable2.getMinHeight())) {
                    invalidateHierarchy();
                }
            } else {
                return;
            }
        } else if (!(getPrefWidth() == 0.0f && getPrefHeight() == 0.0f)) {
            invalidateHierarchy();
        }
        this.drawable = drawable2;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setScaling(Scaling scaling2) {
        if (scaling2 == null) {
            throw new IllegalArgumentException("scaling cannot be null.");
        }
        this.scaling = scaling2;
    }

    public void setAlign(int align2) {
        this.align = align2;
    }

    public float getMinWidth() {
        return 0.0f;
    }

    public float getMinHeight() {
        return 0.0f;
    }

    public float getPrefWidth() {
        if (this.drawable != null) {
            return this.drawable.getMinWidth();
        }
        return 0.0f;
    }

    public float getPrefHeight() {
        if (this.drawable != null) {
            return this.drawable.getMinHeight();
        }
        return 0.0f;
    }

    public float getImageX() {
        return this.imageX;
    }

    public float getImageY() {
        return this.imageY;
    }

    public float getImageWidth() {
        return this.imageWidth;
    }

    public float getImageHeight() {
        return this.imageHeight;
    }
}
