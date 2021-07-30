package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Slider */
public class Slider extends Widget {
    private float animateDuration;
    private float animateFromValue;
    private Interpolation animateInterpolation;
    private float animateTime;
    int draggingPointer;
    private float max;
    private float min;
    private float sliderPos;
    private float stepSize;
    private SliderStyle style;
    private float value;
    private final boolean vertical;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public Slider(float min2, float max2, float stepSize2, boolean vertical2, Skin skin) {
        this(min2, max2, stepSize2, vertical2, (SliderStyle) skin.get("default-" + (vertical2 ? "vertical" : "horizontal"), SliderStyle.class));
    }

    public Slider(float min2, float max2, float stepSize2, boolean vertical2, Skin skin, String styleName) {
        this(min2, max2, stepSize2, vertical2, (SliderStyle) skin.get(styleName, SliderStyle.class));
    }

    public Slider(float min2, float max2, float stepSize2, boolean vertical2, SliderStyle style2) {
        this.draggingPointer = -1;
        this.animateInterpolation = Interpolation.linear;
        if (min2 > max2) {
            throw new IllegalArgumentException("min must be > max: " + min2 + " > " + max2);
        } else if (stepSize2 <= 0.0f) {
            throw new IllegalArgumentException("stepSize must be > 0: " + stepSize2);
        } else {
            setStyle(style2);
            this.min = min2;
            this.max = max2;
            this.stepSize = stepSize2;
            this.vertical = vertical2;
            this.value = min2;
            setWidth(getPrefWidth());
            setHeight(getPrefHeight());
            addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (Slider.this.draggingPointer != -1) {
                        return false;
                    }
                    Slider.this.draggingPointer = pointer;
                    Slider.this.calculatePositionAndValue(x, y);
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (pointer == Slider.this.draggingPointer) {
                        Slider.this.draggingPointer = -1;
                        Slider.this.calculatePositionAndValue(x, y);
                    }
                }

                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    Slider.this.calculatePositionAndValue(x, y);
                }
            });
        }
    }

    public void setStyle(SliderStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style2;
        invalidateHierarchy();
    }

    public SliderStyle getStyle() {
        return this.style;
    }

    public void act(float delta) {
        super.act(delta);
        this.animateTime -= delta;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        Drawable knob = this.style.knob;
        Drawable bg = this.style.background;
        Drawable knobBefore = this.style.knobBefore;
        Drawable knobAfter = this.style.knobAfter;
        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float knobHeight = knob == null ? 0.0f : knob.getMinHeight();
        float knobWidth = knob == null ? 0.0f : knob.getMinWidth();
        float value2 = getVisualValue();
        batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
        if (this.vertical) {
            bg.draw(batch, x + ((float) ((int) ((width - bg.getMinWidth()) * 0.5f))), y, bg.getMinWidth(), height);
            float sliderPosHeight = height - (bg.getTopHeight() + bg.getBottomHeight());
            this.sliderPos = ((value2 - this.min) / (this.max - this.min)) * (sliderPosHeight - knobHeight);
            this.sliderPos = Math.max(0.0f, this.sliderPos);
            this.sliderPos = Math.min(sliderPosHeight - knobHeight, this.sliderPos) + bg.getBottomHeight();
            float knobHeightHalf = knobHeight * 0.5f;
            if (knobBefore != null) {
                knobBefore.draw(batch, x + ((float) ((int) ((width - knobBefore.getMinWidth()) * 0.5f))), y, knobBefore.getMinWidth(), (float) ((int) (this.sliderPos + knobHeightHalf)));
            }
            if (knobAfter != null) {
                knobAfter.draw(batch, x + ((float) ((int) ((width - knobAfter.getMinWidth()) * 0.5f))), y + ((float) ((int) (this.sliderPos + knobHeightHalf))), knobAfter.getMinWidth(), height - ((float) ((int) (this.sliderPos + knobHeightHalf))));
            }
            if (knob != null) {
                knob.draw(batch, x + ((float) ((int) ((width - knobWidth) * 0.5f))), (float) ((int) (this.sliderPos + y)), knobWidth, knobHeight);
                return;
            }
            return;
        }
        bg.draw(batch, x, y + ((float) ((int) ((height - bg.getMinHeight()) * 0.5f))), width, bg.getMinHeight());
        float sliderPosWidth = width - (bg.getLeftWidth() + bg.getRightWidth());
        this.sliderPos = ((value2 - this.min) / (this.max - this.min)) * (sliderPosWidth - knobWidth);
        this.sliderPos = Math.max(0.0f, this.sliderPos);
        this.sliderPos = Math.min(sliderPosWidth - knobWidth, this.sliderPos) + bg.getLeftWidth();
        float knobHeightHalf2 = knobHeight * 0.5f;
        if (knobBefore != null) {
            knobBefore.draw(batch, x, y + ((float) ((int) ((height - knobBefore.getMinHeight()) * 0.5f))), (float) ((int) (this.sliderPos + knobHeightHalf2)), knobBefore.getMinHeight());
        }
        if (knobAfter != null) {
            knobAfter.draw(batch, x + ((float) ((int) (this.sliderPos + knobHeightHalf2))), y + ((float) ((int) ((height - knobAfter.getMinHeight()) * 0.5f))), width - ((float) ((int) (this.sliderPos + knobHeightHalf2))), knobAfter.getMinHeight());
        }
        if (knob != null) {
            knob.draw(batch, (float) ((int) (this.sliderPos + x)), (float) ((int) (((height - knobHeight) * 0.5f) + y)), knobWidth, knobHeight);
        }
    }

    /* access modifiers changed from: package-private */
    public void calculatePositionAndValue(float x, float y) {
        float value2;
        Drawable knob = this.style.knob;
        Drawable bg = this.style.background;
        float oldPosition = this.sliderPos;
        if (this.vertical) {
            float height = (getHeight() - bg.getTopHeight()) - bg.getBottomHeight();
            float knobHeight = knob == null ? 0.0f : knob.getMinHeight();
            this.sliderPos = (y - bg.getBottomHeight()) - (knobHeight * 0.5f);
            this.sliderPos = Math.max(0.0f, this.sliderPos);
            this.sliderPos = Math.min(height - knobHeight, this.sliderPos);
            value2 = this.min + ((this.max - this.min) * (this.sliderPos / (height - knobHeight)));
        } else {
            float width = (getWidth() - bg.getLeftWidth()) - bg.getRightWidth();
            float knobWidth = knob == null ? 0.0f : knob.getMinWidth();
            this.sliderPos = (x - bg.getLeftWidth()) - (knobWidth * 0.5f);
            this.sliderPos = Math.max(0.0f, this.sliderPos);
            this.sliderPos = Math.min(width - knobWidth, this.sliderPos);
            value2 = this.min + ((this.max - this.min) * (this.sliderPos / (width - knobWidth)));
        }
        setValue(value2);
        if (value2 == value2) {
            this.sliderPos = oldPosition;
        }
    }

    public boolean isDragging() {
        return this.draggingPointer != -1;
    }

    public float getValue() {
        return this.value;
    }

    public float getVisualValue() {
        if (this.animateTime > 0.0f) {
            return this.animateInterpolation.apply(this.animateFromValue, this.value, 1.0f - (this.animateTime / this.animateDuration));
        }
        return this.value;
    }

    public void setValue(float value2) {
        if (value2 < this.min || value2 > this.max) {
            throw new IllegalArgumentException("value must be >= min and <= max: " + value2);
        }
        float value3 = MathUtils.clamp(((float) Math.round(value2 / this.stepSize)) * this.stepSize, this.min, this.max);
        float oldValue = this.value;
        if (value3 != oldValue) {
            float oldVisualValue = getVisualValue();
            this.value = value3;
            ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
            if (fire(changeEvent)) {
                this.value = oldValue;
            } else if (this.animateDuration > 0.0f) {
                this.animateFromValue = oldVisualValue;
                this.animateTime = this.animateDuration;
            }
            Pools.free(changeEvent);
        }
    }

    public void setRange(float min2, float max2) {
        if (min2 >= max2) {
            throw new IllegalArgumentException("min must be < max");
        }
        this.min = min2;
        this.max = max2;
        setValue(min2);
    }

    public void setStepSize(float stepSize2) {
        if (stepSize2 <= 0.0f) {
            throw new IllegalArgumentException("steps must be > 0: " + stepSize2);
        }
        this.stepSize = stepSize2;
    }

    public float getPrefWidth() {
        if (!this.vertical) {
            return 140.0f;
        }
        return Math.max(this.style.knob == null ? 0.0f : this.style.knob.getMinWidth(), this.style.background.getMinWidth());
    }

    public float getPrefHeight() {
        if (this.vertical) {
            return 140.0f;
        }
        return Math.max(this.style.knob == null ? 0.0f : this.style.knob.getMinHeight(), this.style.background.getMinHeight());
    }

    public float getMinValue() {
        return this.min;
    }

    public float getMaxValue() {
        return this.max;
    }

    public float getStepSize() {
        return this.stepSize;
    }

    public void setAnimateDuration(float duration) {
        this.animateDuration = duration;
    }

    public void setAnimateInterpolation(Interpolation animateInterpolation2) {
        if (animateInterpolation2 == null) {
            throw new IllegalArgumentException("animateInterpolation cannot be null.");
        }
        this.animateInterpolation = animateInterpolation2;
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Slider$SliderStyle */
    public static class SliderStyle {
        public Drawable background;
        public Drawable knob;
        public Drawable knobAfter;
        public Drawable knobBefore;

        public SliderStyle() {
        }

        public SliderStyle(Drawable background2, Drawable knob2) {
            this.background = background2;
            this.knob = knob2;
        }

        public SliderStyle(SliderStyle style) {
            this.background = style.background;
            this.knob = style.knob;
        }
    }
}
