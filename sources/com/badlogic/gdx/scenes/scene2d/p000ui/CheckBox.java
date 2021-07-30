package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.p000ui.Button;
import com.badlogic.gdx.scenes.scene2d.p000ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.CheckBox */
public class CheckBox extends TextButton {
    private Image image;
    private CheckBoxStyle style;

    public CheckBox(String text, Skin skin) {
        this(text, (CheckBoxStyle) skin.get(CheckBoxStyle.class));
    }

    public CheckBox(String text, Skin skin, String styleName) {
        this(text, (CheckBoxStyle) skin.get(styleName, CheckBoxStyle.class));
    }

    public CheckBox(String text, CheckBoxStyle style2) {
        super(text, (TextButton.TextButtonStyle) style2);
        clear();
        Image image2 = new Image(style2.checkboxOff);
        this.image = image2;
        add((Actor) image2);
        Label label = getLabel();
        add((Actor) label);
        label.setAlignment(8);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public void setStyle(Button.ButtonStyle style2) {
        if (!(style2 instanceof CheckBoxStyle)) {
            throw new IllegalArgumentException("style must be a CheckBoxStyle.");
        }
        super.setStyle(style2);
        this.style = (CheckBoxStyle) style2;
    }

    public CheckBoxStyle getStyle() {
        return this.style;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        Drawable checkbox;
        if (this.isChecked && this.style.checkboxOn != null) {
            checkbox = this.style.checkboxOn;
        } else if (!isOver() || this.style.checkboxOver == null) {
            checkbox = this.style.checkboxOff;
        } else {
            checkbox = this.style.checkboxOver;
        }
        this.image.setDrawable(checkbox);
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return this.image;
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.CheckBox$CheckBoxStyle */
    public static class CheckBoxStyle extends TextButton.TextButtonStyle {
        public Drawable checkboxOff;
        public Drawable checkboxOn;
        public Drawable checkboxOver;

        public CheckBoxStyle() {
        }

        public CheckBoxStyle(Drawable checkboxOff2, Drawable checkboxOn2, BitmapFont font, Color fontColor) {
            this.checkboxOff = checkboxOff2;
            this.checkboxOn = checkboxOn2;
            this.font = font;
            this.fontColor = fontColor;
        }

        public CheckBoxStyle(CheckBoxStyle style) {
            this.checkboxOff = style.checkboxOff;
            this.checkboxOn = style.checkboxOn;
            this.font = style.font;
            this.fontColor = new Color(style.fontColor);
        }
    }
}
