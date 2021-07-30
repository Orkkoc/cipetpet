package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Button */
public class Button extends Table {
    ButtonGroup buttonGroup;
    private ClickListener clickListener;
    boolean isChecked;
    boolean isDisabled;
    private ButtonStyle style;

    public Button(Skin skin) {
        super(skin);
        initialize();
        setStyle((ButtonStyle) skin.get(ButtonStyle.class));
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public Button(Skin skin, String styleName) {
        super(skin);
        initialize();
        setStyle((ButtonStyle) skin.get(styleName, ButtonStyle.class));
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public Button(Actor child, Skin skin, String styleName) {
        this(child, (ButtonStyle) skin.get(styleName, ButtonStyle.class));
    }

    public Button(Actor child, ButtonStyle style2) {
        initialize();
        add(child);
        setStyle(style2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public Button(ButtonStyle style2) {
        initialize();
        setStyle(style2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    private void initialize() {
        setTouchable(Touchable.enabled);
        C00911 r0 = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!Button.this.isDisabled) {
                    boolean z = Button.this.isChecked;
                    Button.this.setChecked(!Button.this.isChecked);
                }
            }
        };
        this.clickListener = r0;
        addListener(r0);
    }

    public Button(Drawable up) {
        this(new ButtonStyle(up, (Drawable) null, (Drawable) null));
    }

    public Button(Drawable up, Drawable down) {
        this(new ButtonStyle(up, down, (Drawable) null));
    }

    public Button(Drawable up, Drawable down, Drawable checked) {
        this(new ButtonStyle(up, down, checked));
    }

    public Button(Actor child, Skin skin) {
        this(child, (ButtonStyle) skin.get(ButtonStyle.class));
    }

    public void setChecked(boolean isChecked2) {
        if (this.isChecked != isChecked2) {
            if (this.buttonGroup == null || this.buttonGroup.canCheck(this, isChecked2)) {
                this.isChecked = isChecked2;
                if (!this.isDisabled) {
                    ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
                    if (fire(changeEvent)) {
                        this.isChecked = !isChecked2;
                    }
                    Pools.free(changeEvent);
                }
            }
        }
    }

    public void toggle() {
        setChecked(!this.isChecked);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public boolean isPressed() {
        return this.clickListener.isPressed();
    }

    public boolean isOver() {
        return this.clickListener.isOver();
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public void setDisabled(boolean isDisabled2) {
        this.isDisabled = isDisabled2;
    }

    public void setStyle(ButtonStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style2;
        Drawable background = style2.f182up;
        if (background == null && (background = style2.down) == null) {
            background = style2.checked;
        }
        if (background != null) {
            padBottom(background.getBottomHeight());
            padTop(background.getTopHeight());
            padLeft(background.getLeftWidth());
            padRight(background.getRightWidth());
        }
        invalidateHierarchy();
    }

    public ButtonStyle getStyle() {
        return this.style;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        Drawable background;
        Drawable background2;
        float offsetX;
        float offsetY;
        validate();
        if (!isPressed() || this.isDisabled) {
            if (this.isDisabled && this.style.disabled != null) {
                background = this.style.disabled;
            } else if (!this.isChecked || this.style.checked == null) {
                background = (!isOver() || this.style.over == null) ? this.style.f182up : this.style.over;
            } else {
                background = (!isOver() || this.style.checkedOver == null) ? this.style.checked : this.style.checkedOver;
            }
            offsetX = this.style.unpressedOffsetX;
            offsetY = this.style.unpressedOffsetY;
        } else {
            background2 = this.style.down == null ? this.style.f182up : this.style.down;
            offsetX = this.style.pressedOffsetX;
            offsetY = this.style.pressedOffsetY;
        }
        if (background2 != null) {
            Color color = getColor();
            batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
            background2.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        Array<Actor> children = getChildren();
        for (int i = 0; i < children.size; i++) {
            children.get(i).translate(offsetX, offsetY);
        }
        super.draw(batch, parentAlpha);
        for (int i2 = 0; i2 < children.size; i2++) {
            children.get(i2).translate(-offsetX, -offsetY);
        }
    }

    /* access modifiers changed from: protected */
    public void drawBackground(SpriteBatch batch, float parentAlpha) {
    }

    public float getPrefWidth() {
        float width = super.getPrefWidth();
        if (this.style.f182up != null) {
            width = Math.max(width, this.style.f182up.getMinWidth());
        }
        if (this.style.down != null) {
            width = Math.max(width, this.style.down.getMinWidth());
        }
        if (this.style.checked != null) {
            return Math.max(width, this.style.checked.getMinWidth());
        }
        return width;
    }

    public float getPrefHeight() {
        float height = super.getPrefHeight();
        if (this.style.f182up != null) {
            height = Math.max(height, this.style.f182up.getMinHeight());
        }
        if (this.style.down != null) {
            height = Math.max(height, this.style.down.getMinHeight());
        }
        if (this.style.checked != null) {
            return Math.max(height, this.style.checked.getMinHeight());
        }
        return height;
    }

    public float getMinWidth() {
        return getPrefWidth();
    }

    public float getMinHeight() {
        return getPrefHeight();
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Button$ButtonStyle */
    public static class ButtonStyle {
        public Drawable checked;
        public Drawable checkedOver;
        public Drawable disabled;
        public Drawable down;
        public Drawable over;
        public float pressedOffsetX;
        public float pressedOffsetY;
        public float unpressedOffsetX;
        public float unpressedOffsetY;

        /* renamed from: up */
        public Drawable f182up;

        public ButtonStyle() {
        }

        public ButtonStyle(Drawable up, Drawable down2, Drawable checked2) {
            this.f182up = up;
            this.down = down2;
            this.checked = checked2;
        }

        public ButtonStyle(ButtonStyle style) {
            this.f182up = style.f182up;
            this.down = style.down;
            this.over = style.over;
            this.checked = style.checked;
            this.checkedOver = style.checkedOver;
            this.disabled = style.disabled;
            this.pressedOffsetX = style.pressedOffsetX;
            this.pressedOffsetY = style.pressedOffsetY;
            this.unpressedOffsetX = style.unpressedOffsetX;
            this.unpressedOffsetY = style.unpressedOffsetY;
        }
    }
}
