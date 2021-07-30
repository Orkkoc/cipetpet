package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.p000ui.Button;
import com.badlogic.gdx.scenes.scene2d.p000ui.Label;
import com.badlogic.gdx.scenes.scene2d.p000ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton */
public class ImageTextButton extends Button {
    private final Image image;
    private final Label label;
    private ImageTextButtonStyle style;

    public ImageTextButton(String text, Skin skin) {
        this(text, (ImageTextButtonStyle) skin.get(ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public ImageTextButton(String text, Skin skin, String styleName) {
        this(text, (ImageTextButtonStyle) skin.get(styleName, ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public ImageTextButton(String text, ImageTextButtonStyle style2) {
        super((Button.ButtonStyle) style2);
        this.style = style2;
        defaults().space(3.0f);
        this.image = new Image();
        this.image.setScaling(Scaling.fit);
        add((Actor) this.image);
        this.label = new Label((CharSequence) text, new Label.LabelStyle(style2.font, style2.fontColor));
        this.label.setAlignment(1);
        add((Actor) this.label);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (!(style2 instanceof ImageTextButtonStyle)) {
            throw new IllegalArgumentException("style must be a ImageTextButtonStyle.");
        }
        super.setStyle(style2);
        this.style = (ImageTextButtonStyle) style2;
        if (this.image != null) {
            updateImage();
        }
        if (this.label != null) {
            ImageTextButtonStyle textButtonStyle = (ImageTextButtonStyle) style2;
            Label.LabelStyle labelStyle = this.label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = textButtonStyle.fontColor;
            this.label.setStyle(labelStyle);
        }
    }

    public ImageTextButtonStyle getStyle() {
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
        Color fontColor;
        updateImage();
        if (this.isDisabled && this.style.disabledFontColor != null) {
            fontColor = this.style.disabledFontColor;
        } else if (isPressed() && this.style.downFontColor != null) {
            fontColor = this.style.downFontColor;
        } else if (!this.isChecked || this.style.checkedFontColor == null) {
            fontColor = (!isOver() || this.style.overFontColor == null) ? this.style.fontColor : this.style.overFontColor;
        } else {
            fontColor = (!isOver() || this.style.checkedOverFontColor == null) ? this.style.checkedFontColor : this.style.checkedOverFontColor;
        }
        if (fontColor != null) {
            this.label.getStyle().fontColor = fontColor;
        }
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return this.image;
    }

    public Cell getImageCell() {
        return getCell(this.image);
    }

    public Label getLabel() {
        return this.label;
    }

    public Cell getLabelCell() {
        return getCell(this.label);
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public CharSequence getText() {
        return this.label.getText();
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton$ImageTextButtonStyle */
    public static class ImageTextButtonStyle extends TextButton.TextButtonStyle {
        public Drawable imageChecked;
        public Drawable imageCheckedOver;
        public Drawable imageDisabled;
        public Drawable imageDown;
        public Drawable imageOver;
        public Drawable imageUp;

        public ImageTextButtonStyle() {
        }

        public ImageTextButtonStyle(Drawable up, Drawable down, Drawable checked) {
            super(up, down, checked);
        }

        public ImageTextButtonStyle(ImageTextButtonStyle style) {
            super(style);
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            if (style.downFontColor != null) {
                this.downFontColor = new Color(style.downFontColor);
            }
            if (style.overFontColor != null) {
                this.overFontColor = new Color(style.overFontColor);
            }
            if (style.checkedFontColor != null) {
                this.checkedFontColor = new Color(style.checkedFontColor);
            }
            if (style.checkedOverFontColor != null) {
                this.checkedOverFontColor = new Color(style.checkedOverFontColor);
            }
            if (style.disabledFontColor != null) {
                this.disabledFontColor = new Color(style.disabledFontColor);
            }
            if (style.imageUp != null) {
                this.imageUp = style.imageUp;
            }
            if (style.imageDown != null) {
                this.imageDown = style.imageDown;
            }
            if (style.imageOver != null) {
                this.imageOver = style.imageOver;
            }
            if (style.imageChecked != null) {
                this.imageChecked = style.imageChecked;
            }
            if (style.imageCheckedOver != null) {
                this.imageCheckedOver = style.imageCheckedOver;
            }
            if (style.imageDisabled != null) {
                this.imageDisabled = style.imageDisabled;
            }
        }
    }
}
