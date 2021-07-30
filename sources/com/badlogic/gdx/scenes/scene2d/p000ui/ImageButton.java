package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.p000ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ImageButton */
public class ImageButton extends Button {
    private final Image image;
    private ImageButtonStyle style;

    public ImageButton(Skin skin) {
        this((ImageButtonStyle) skin.get(ImageButtonStyle.class));
    }

    public ImageButton(Skin skin, String styleName) {
        this((ImageButtonStyle) skin.get(styleName, ImageButtonStyle.class));
    }

    public ImageButton(ImageButtonStyle style2) {
        super((Button.ButtonStyle) style2);
        this.image = new Image();
        this.image.setScaling(Scaling.fit);
        add((Actor) this.image);
        setStyle(style2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public ImageButton(Drawable imageUp) {
        this(new ImageButtonStyle((Drawable) null, (Drawable) null, (Drawable) null, imageUp, (Drawable) null, (Drawable) null));
    }

    public ImageButton(Drawable imageUp, Drawable imageDown) {
        this(new ImageButtonStyle((Drawable) null, (Drawable) null, (Drawable) null, imageUp, imageDown, (Drawable) null));
    }

    public ImageButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        this(new ImageButtonStyle((Drawable) null, (Drawable) null, (Drawable) null, imageUp, imageDown, imageChecked));
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (!(style2 instanceof ImageButtonStyle)) {
            throw new IllegalArgumentException("style must be an ImageButtonStyle.");
        }
        super.setStyle(style2);
        this.style = (ImageButtonStyle) style2;
        if (this.image != null) {
            updateImage();
        }
    }

    public ImageButtonStyle getStyle() {
        return this.style;
    }

    private void updateImage() {
        boolean isPressed = isPressed();
        if (this.isDisabled && this.style.imageDisabled != null) {
            this.image.setDrawable(this.style.imageDisabled);
        } else if (isPressed && this.style.imageDown != null) {
            this.image.setDrawable(this.style.imageDown);
        } else if (this.isChecked && this.style.imageChecked != null) {
            this.image.setDrawable((this.style.imageCheckedOver == null || !isOver()) ? this.style.imageChecked : this.style.imageCheckedOver);
        } else if (isOver() && this.style.imageOver != null) {
            this.image.setDrawable(this.style.imageOver);
        } else if (this.style.imageUp != null) {
            this.image.setDrawable(this.style.imageUp);
        }
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        updateImage();
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return this.image;
    }

    public Cell getImageCell() {
        return getCell(this.image);
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ImageButton$ImageButtonStyle */
    public static class ImageButtonStyle extends Button.ButtonStyle {
        public Drawable imageChecked;
        public Drawable imageCheckedOver;
        public Drawable imageDisabled;
        public Drawable imageDown;
        public Drawable imageOver;
        public Drawable imageUp;

        public ImageButtonStyle() {
        }

        public ImageButtonStyle(Drawable up, Drawable down, Drawable checked, Drawable imageUp2, Drawable imageDown2, Drawable imageChecked2) {
            super(up, down, checked);
            this.imageUp = imageUp2;
            this.imageDown = imageDown2;
            this.imageChecked = imageChecked2;
        }

        public ImageButtonStyle(ImageButtonStyle style) {
            super(style);
            this.imageUp = style.imageUp;
            this.imageDown = style.imageDown;
            this.imageOver = style.imageOver;
            this.imageChecked = style.imageChecked;
            this.imageCheckedOver = style.imageCheckedOver;
            this.imageDisabled = style.imageDisabled;
        }

        public ImageButtonStyle(Button.ButtonStyle style) {
            super(style);
        }
    }
}
