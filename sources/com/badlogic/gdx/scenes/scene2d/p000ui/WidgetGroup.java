package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup */
public class WidgetGroup extends Group implements Layout {
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
        if (this.layoutEnabled != enabled) {
            this.layoutEnabled = enabled;
            setLayoutEnabled(this, enabled);
        }
    }

    private void setLayoutEnabled(Group parent, boolean enabled) {
        SnapshotArray<Actor> children = getChildren();
        int n = children.size;
        for (int i = 0; i < n; i++) {
            Actor actor = children.get(i);
            if (actor instanceof Layout) {
                ((Layout) actor).setLayoutEnabled(enabled);
            } else if (actor instanceof Group) {
                setLayoutEnabled((Group) actor, enabled);
            }
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
        invalidate();
        Group parent = getParent();
        if (parent instanceof Layout) {
            ((Layout) parent).invalidateHierarchy();
        }
    }

    /* access modifiers changed from: protected */
    public void childrenChanged() {
        invalidateHierarchy();
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

    public void layout() {
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        validate();
        super.draw(batch, parentAlpha);
    }
}
