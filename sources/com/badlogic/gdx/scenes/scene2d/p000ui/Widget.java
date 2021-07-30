package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Widget */
public class Widget extends Actor implements Layout {
    private boolean fillParent;
    private boolean layoutEnabled = true;
    private boolean needsLayout = true;

    public float getMinWidth() {
        return getPrefWidth();
    }

    public float getMinHeight() {
        return getPrefHeight();
    }

    public float getPrefWidth() {
        return 0.0f;
    }

    public float getPrefHeight() {
        return 0.0f;
    }

    public float getMaxWidth() {
        return 0.0f;
    }

    public float getMaxHeight() {
        return 0.0f;
    }

    public void setLayoutEnabled(boolean enabled) {
        this.layoutEnabled = enabled;
        if (enabled) {
            invalidateHierarchy();
        }
    }

    public void validate() {
        float parentWidth;
        float parentHeight;
        if (this.layoutEnabled) {
            Group parent = getParent();
            if (this.fillParent && parent != null) {
                Stage stage = getStage();
                if (stage == null || parent != stage.getRoot()) {
                    parentWidth = parent.getWidth();
                    parentHeight = parent.getHeight();
                } else {
                    parentWidth = stage.getWidth();
                    parentHeight = stage.getHeight();
                }
                if (!(getWidth() == parentWidth && getHeight() == parentHeight)) {
                    setWidth(parentWidth);
                    setHeight(parentHeight);
                    invalidate();
                }
            }
            if (this.needsLayout) {
                this.needsLayout = false;
                layout();
            }
        }
    }

    public boolean needsLayout() {
        return this.needsLayout;
    }

    public void invalidate() {
        this.needsLayout = true;
    }

    public void invalidateHierarchy() {
        if (this.layoutEnabled) {
            invalidate();
            Group parent = getParent();
            if (parent instanceof Layout) {
                ((Layout) parent).invalidateHierarchy();
            }
        }
    }

    public void pack() {
        float newWidth = getPrefWidth();
        float newHeight = getPrefHeight();
        if (!(newWidth == getWidth() && newHeight == getHeight())) {
            setWidth(newWidth);
            setHeight(newHeight);
            invalidate();
        }
        validate();
    }

    public void setFillParent(boolean fillParent2) {
        this.fillParent = fillParent2;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        validate();
    }

    public void layout() {
    }
}
