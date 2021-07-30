package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.StringBuilder;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Label */
public class Label extends Widget {
    private final BitmapFont.TextBounds bounds;
    private BitmapFontCache cache;
    private float fontScaleX;
    private float fontScaleY;
    private int labelAlign;
    private float lastPrefHeight;
    private BitmapFont.HAlignment lineAlign;
    private boolean sizeInvalid;
    private LabelStyle style;
    private final StringBuilder text;
    private boolean wrap;

    public Label(CharSequence text2, Skin skin) {
        this(text2, (LabelStyle) skin.get(LabelStyle.class));
    }

    public Label(CharSequence text2, Skin skin, String styleName) {
        this(text2, (LabelStyle) skin.get(styleName, LabelStyle.class));
    }

    public Label(CharSequence text2, Skin skin, String fontName, Color color) {
        this(text2, new LabelStyle(skin.getFont(fontName), color));
    }

    public Label(CharSequence text2, Skin skin, String fontName, String colorName) {
        this(text2, new LabelStyle(skin.getFont(fontName), skin.getColor(colorName)));
    }

    public Label(CharSequence text2, LabelStyle style2) {
        this.bounds = new BitmapFont.TextBounds();
        this.text = new StringBuilder();
        this.labelAlign = 8;
        this.lineAlign = BitmapFont.HAlignment.LEFT;
        this.sizeInvalid = true;
        this.fontScaleX = 1.0f;
        this.fontScaleY = 1.0f;
        if (text2 != null) {
            this.text.append(text2);
        }
        setStyle(style2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
    }

    public void setStyle(LabelStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        } else if (style2.font == null) {
            throw new IllegalArgumentException("Missing LabelStyle font.");
        } else {
            this.style = style2;
            this.cache = new BitmapFontCache(style2.font, style2.font.usesIntegerPositions());
            invalidateHierarchy();
        }
    }

    public LabelStyle getStyle() {
        return this.style;
    }

    public void setText(CharSequence newText) {
        if (!(newText instanceof StringBuilder)) {
            if (newText == null) {
                newText = "";
            }
            if (!textEquals(newText)) {
                this.text.setLength(0);
                this.text.append(newText);
            } else {
                return;
            }
        } else if (!this.text.equals(newText)) {
            this.text.setLength(0);
            this.text.append((StringBuilder) newText);
        } else {
            return;
        }
        invalidateHierarchy();
    }

    private boolean textEquals(CharSequence other) {
        int length = this.text.length;
        char[] chars = this.text.chars;
        if (length != other.length()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (chars[i] != other.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public CharSequence getText() {
        return this.text;
    }

    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void computeSize() {
        this.sizeInvalid = false;
        if (this.wrap) {
            float width = getWidth();
            if (this.style.background != null) {
                width -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
            }
            this.bounds.set(this.cache.getFont().getWrappedBounds(this.text, width));
        } else {
            this.bounds.set(this.cache.getFont().getMultiLineBounds(this.text));
        }
        this.bounds.width *= this.fontScaleX;
        this.bounds.height *= this.fontScaleY;
    }

    public void layout() {
        float y;
        float f = 0.0f;
        if (this.sizeInvalid) {
            computeSize();
        }
        if (this.wrap) {
            float prefHeight = getPrefHeight();
            if (prefHeight != this.lastPrefHeight) {
                this.lastPrefHeight = prefHeight;
                invalidateHierarchy();
            }
        }
        BitmapFont font = this.cache.getFont();
        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();
        if (!(this.fontScaleX == 1.0f && this.fontScaleY == 1.0f)) {
            font.setScale(this.fontScaleX, this.fontScaleY);
        }
        Drawable background = this.style.background;
        float width = getWidth();
        float height = getHeight();
        float x = 0.0f;
        float y2 = 0.0f;
        if (background != null) {
            x = background.getLeftWidth();
            y2 = background.getBottomHeight();
            width -= background.getLeftWidth() + background.getRightWidth();
            height -= background.getBottomHeight() + background.getTopHeight();
        }
        if ((this.labelAlign & 2) != 0) {
            if (!this.cache.getFont().isFlipped()) {
                f = height - this.bounds.height;
            }
            y = y2 + f + this.style.font.getDescent();
        } else if ((this.labelAlign & 4) != 0) {
            if (this.cache.getFont().isFlipped()) {
                f = height - this.bounds.height;
            }
            y = (y2 + f) - this.style.font.getDescent();
        } else {
            y = y2 + ((float) ((int) ((height - this.bounds.height) / 2.0f)));
        }
        if (!this.cache.getFont().isFlipped()) {
            y += this.bounds.height;
        }
        if ((this.labelAlign & 8) == 0) {
            if ((this.labelAlign & 16) != 0) {
                x += width - this.bounds.width;
            } else {
                x += (float) ((int) ((width - this.bounds.width) / 2.0f));
            }
        }
        if (this.wrap) {
            this.cache.setWrappedText(this.text, x, y, this.bounds.width, this.lineAlign);
        } else {
            this.cache.setMultiLineText(this.text, x, y, this.bounds.width, this.lineAlign);
        }
        if (this.fontScaleX != 1.0f || this.fontScaleY != 1.0f) {
            font.setScale(oldScaleX, oldScaleY);
        }
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        validate();
        Color color = getColor();
        if (this.style.background != null) {
            batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
            this.style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        this.cache.setColor(this.style.fontColor == null ? color : Color.tmp.set(color).mul(this.style.fontColor));
        this.cache.setPosition(getX(), getY());
        this.cache.draw(batch, color.f67a * parentAlpha);
    }

    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            computeSize();
        }
        float width = this.bounds.width;
        Drawable background = this.style.background;
        if (background != null) {
            return width + background.getLeftWidth() + background.getRightWidth();
        }
        return width;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        float height = this.bounds.height - (this.style.font.getDescent() * 2.0f);
        Drawable background = this.style.background;
        if (background != null) {
            return height + background.getTopHeight() + background.getBottomHeight();
        }
        return height;
    }

    public BitmapFont.TextBounds getTextBounds() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.bounds;
    }

    public void setWrap(boolean wrap2) {
        this.wrap = wrap2;
        invalidateHierarchy();
    }

    public void setAlignment(int wrapAlign) {
        setAlignment(wrapAlign, wrapAlign);
    }

    public void setAlignment(int labelAlign2, int lineAlign2) {
        this.labelAlign = labelAlign2;
        if ((lineAlign2 & 8) != 0) {
            this.lineAlign = BitmapFont.HAlignment.LEFT;
        } else if ((lineAlign2 & 16) != 0) {
            this.lineAlign = BitmapFont.HAlignment.RIGHT;
        } else {
            this.lineAlign = BitmapFont.HAlignment.CENTER;
        }
        invalidate();
    }

    public void setFontScale(float fontScale) {
        this.fontScaleX = fontScale;
        this.fontScaleY = fontScale;
        invalidateHierarchy();
    }

    public void setFontScale(float fontScaleX2, float fontScaleY2) {
        this.fontScaleX = fontScaleX2;
        this.fontScaleY = fontScaleY2;
        invalidateHierarchy();
    }

    public float getFontScaleX() {
        return this.fontScaleX;
    }

    public void setFontScaleX(float fontScaleX2) {
        this.fontScaleX = fontScaleX2;
        invalidateHierarchy();
    }

    public float getFontScaleY() {
        return this.fontScaleY;
    }

    public void setFontScaleY(float fontScaleY2) {
        this.fontScaleY = fontScaleY2;
        invalidateHierarchy();
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Label$LabelStyle */
    public static class LabelStyle {
        public Drawable background;
        public BitmapFont font;
        public Color fontColor;

        public LabelStyle() {
        }

        public LabelStyle(BitmapFont font2, Color fontColor2) {
            this.font = font2;
            this.fontColor = fontColor2;
        }

        public LabelStyle(LabelStyle style) {
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
        }
    }
}
