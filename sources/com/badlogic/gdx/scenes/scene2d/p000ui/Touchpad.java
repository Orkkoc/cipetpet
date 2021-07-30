package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Touchpad */
public class Touchpad extends Widget {
    private final Circle deadzoneBounds;
    private float deadzoneRadius;
    private final Circle knobBounds;
    private final Vector2 knobPercent;
    private final Vector2 knobPosition;
    private TouchpadStyle style;
    private final Circle touchBounds;
    boolean touched;

    public Touchpad(float deadzoneRadius2, Skin skin) {
        this(deadzoneRadius2, (TouchpadStyle) skin.get(TouchpadStyle.class));
    }

    public Touchpad(float deadzoneRadius2, Skin skin, String styleName) {
        this(deadzoneRadius2, (TouchpadStyle) skin.get(styleName, TouchpadStyle.class));
    }

    public Touchpad(float deadzoneRadius2, TouchpadStyle style2) {
        this.knobBounds = new Circle(0.0f, 0.0f, 0.0f);
        this.touchBounds = new Circle(0.0f, 0.0f, 0.0f);
        this.deadzoneBounds = new Circle(0.0f, 0.0f, 0.0f);
        this.knobPosition = new Vector2();
        this.knobPercent = new Vector2();
        if (deadzoneRadius2 < 0.0f) {
            throw new IllegalArgumentException("deadzoneRadius must be > 0");
        }
        this.deadzoneRadius = deadzoneRadius2;
        this.knobPosition.set(getWidth() / 2.0f, getHeight() / 2.0f);
        setStyle(style2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Touchpad.this.touched) {
                    return false;
                }
                Touchpad.this.touched = true;
                Touchpad.this.calculatePositionAndValue(x, y, false);
                return true;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Touchpad.this.calculatePositionAndValue(x, y, false);
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Touchpad.this.touched = false;
                Touchpad.this.calculatePositionAndValue(x, y, true);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void calculatePositionAndValue(float x, float y, boolean isTouchUp) {
        float oldPositionX = this.knobPosition.f165x;
        float oldPositionY = this.knobPosition.f166y;
        float oldPercentX = this.knobPercent.f165x;
        float oldPercentY = this.knobPercent.f166y;
        float centerX = this.knobBounds.f146x;
        float centerY = this.knobBounds.f147y;
        this.knobPosition.set(centerX, centerY);
        this.knobPercent.set(0.0f, 0.0f);
        if (!isTouchUp && !this.deadzoneBounds.contains(x, y)) {
            this.knobPercent.set((x - centerX) / this.knobBounds.radius, (y - centerY) / this.knobBounds.radius);
            float length = this.knobPercent.len();
            if (length > 1.0f) {
                this.knobPercent.mul(1.0f / length);
            }
            if (this.knobBounds.contains(x, y)) {
                this.knobPosition.set(x, y);
            } else {
                this.knobPosition.set(this.knobPercent).nor().mul(this.knobBounds.radius).add(this.knobBounds.f146x, this.knobBounds.f147y);
            }
        }
        if (oldPercentX != this.knobPercent.f165x || oldPercentY != this.knobPercent.f166y) {
            ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
            if (fire(changeEvent)) {
                this.knobPercent.set(oldPercentX, oldPercentY);
                this.knobPosition.set(oldPositionX, oldPositionY);
            }
            Pools.free(changeEvent);
        }
    }

    public void setStyle(TouchpadStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null");
        }
        this.style = style2;
        invalidateHierarchy();
    }

    public TouchpadStyle getStyle() {
        return this.style;
    }

    /* Debug info: failed to restart local var, previous not found, register: 1 */
    public Actor hit(float x, float y, boolean touchable) {
        if (this.touchBounds.contains(x, y)) {
            return this;
        }
        return null;
    }

    public void layout() {
        float halfWidth = getWidth() / 2.0f;
        float halfHeight = getHeight() / 2.0f;
        float radius = Math.min(halfWidth, halfHeight);
        this.touchBounds.set(halfWidth, halfHeight, radius);
        if (this.style.knob != null) {
            radius -= Math.max(this.style.knob.getMinWidth(), this.style.knob.getMinHeight()) / 2.0f;
        }
        this.knobBounds.set(halfWidth, halfHeight, radius);
        this.deadzoneBounds.set(halfWidth, halfHeight, this.deadzoneRadius);
        this.knobPosition.set(halfWidth, halfHeight);
        this.knobPercent.set(0.0f, 0.0f);
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        validate();
        Color c = getColor();
        batch.setColor(c.f70r, c.f69g, c.f68b, c.f67a * parentAlpha);
        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();
        Drawable bg = this.style.background;
        if (bg != null) {
            bg.draw(batch, x, y, w, h);
        }
        Drawable knob = this.style.knob;
        if (knob != null) {
            knob.draw(batch, x + (this.knobPosition.f165x - (knob.getMinWidth() / 2.0f)), y + (this.knobPosition.f166y - (knob.getMinHeight() / 2.0f)), knob.getMinWidth(), knob.getMinHeight());
        }
    }

    public float getPrefWidth() {
        if (this.style.background != null) {
            return this.style.background.getMinWidth();
        }
        return 0.0f;
    }

    public float getPrefHeight() {
        if (this.style.background != null) {
            return this.style.background.getMinHeight();
        }
        return 0.0f;
    }

    public boolean isTouched() {
        return this.touched;
    }

    public void setDeadzone(float deadzoneRadius2) {
        if (deadzoneRadius2 < 0.0f) {
            throw new IllegalArgumentException("deadzoneRadius must be > 0");
        }
        this.deadzoneRadius = deadzoneRadius2;
        invalidate();
    }

    public float getKnobX() {
        return this.knobPosition.f165x;
    }

    public float getKnobY() {
        return this.knobPosition.f166y;
    }

    public float getKnobPercentX() {
        return this.knobPercent.f165x;
    }

    public float getKnobPercentY() {
        return this.knobPercent.f166y;
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Touchpad$TouchpadStyle */
    public static class TouchpadStyle {
        public Drawable background;
        public Drawable knob;

        public TouchpadStyle() {
        }

        public TouchpadStyle(Drawable background2, Drawable knob2) {
            this.background = background2;
            this.knob = knob2;
        }

        public TouchpadStyle(TouchpadStyle style) {
            this.background = style.background;
            this.knob = style.knob;
        }
    }
}
