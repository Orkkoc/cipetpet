package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup */
public class VerticalGroup extends WidgetGroup {
    private int alignment;
    private float prefHeight;
    private float prefWidth;
    private boolean reverse;
    private boolean sizeInvalid = true;

    public VerticalGroup() {
        setTouchable(Touchable.childrenOnly);
    }

    public void setAlignment(int alignment2) {
        this.alignment = alignment2;
    }

    public void setReverse(boolean reverse2) {
        this.reverse = reverse2;
    }

    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void computeSize() {
        this.sizeInvalid = false;
        this.prefWidth = 0.0f;
        this.prefHeight = 0.0f;
        SnapshotArray<Actor> children = getChildren();
        int n = children.size;
        for (int i = 0; i < n; i++) {
            Actor child = children.get(i);
            if (child instanceof Layout) {
                Layout layout = (Layout) child;
                this.prefWidth = Math.max(this.prefWidth, layout.getPrefWidth());
                this.prefHeight += layout.getPrefHeight();
            } else {
                this.prefWidth = Math.max(this.prefWidth, child.getWidth());
                this.prefHeight += child.getHeight();
            }
        }
    }

    public void layout() {
        float width;
        float height;
        float x;
        float groupWidth = getWidth();
        float y = this.reverse ? 0.0f : getHeight();
        float dir = this.reverse ? 1.0f : -1.0f;
        SnapshotArray<Actor> children = getChildren();
        int n = children.size;
        for (int i = 0; i < n; i++) {
            Actor child = children.get(i);
            if (child instanceof Layout) {
                Layout layout = (Layout) child;
                width = layout.getPrefWidth();
                height = layout.getPrefHeight();
            } else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if ((this.alignment & 8) != 0) {
                x = 0.0f;
            } else if ((this.alignment & 16) != 0) {
                x = groupWidth - width;
            } else {
                x = (groupWidth - width) / 2.0f;
            }
            if (!this.reverse) {
                y += height * dir;
            }
            child.setBounds(x, y, width, height);
            if (this.reverse) {
                y += height * dir;
            }
        }
    }

    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }
}
