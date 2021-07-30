package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.NumberUtils;

public class BitmapFontCache {
    private float color;
    private final BitmapFont font;
    private int idx;
    private boolean integer;
    private final Color tempColor;
    private final BitmapFont.TextBounds textBounds;
    private float[] vertices;

    /* renamed from: x */
    private float f75x;

    /* renamed from: y */
    private float f76y;

    public BitmapFontCache(BitmapFont font2) {
        this(font2, font2.usesIntegerPositions());
    }

    public BitmapFontCache(BitmapFont font2, boolean integer2) {
        this.vertices = new float[0];
        this.color = Color.WHITE.toFloatBits();
        this.tempColor = new Color(Color.WHITE);
        this.textBounds = new BitmapFont.TextBounds();
        this.integer = true;
        this.font = font2;
        this.integer = integer2;
    }

    public void setPosition(float x, float y) {
        translate(x - this.f75x, y - this.f76y);
    }

    public void translate(float xAmount, float yAmount) {
        if (xAmount != 0.0f || yAmount != 0.0f) {
            if (this.integer) {
                xAmount = (float) Math.round(xAmount);
                yAmount = (float) Math.round(yAmount);
            }
            this.f75x += xAmount;
            this.f76y += yAmount;
            float[] vertices2 = this.vertices;
            int n = this.idx;
            for (int i = 0; i < n; i += 5) {
                vertices2[i] = vertices2[i] + xAmount;
                int i2 = i + 1;
                vertices2[i2] = vertices2[i2] + yAmount;
            }
        }
    }

    public void setColor(float color2) {
        if (color2 != this.color) {
            this.color = color2;
            float[] vertices2 = this.vertices;
            int n = this.idx;
            for (int i = 2; i < n; i += 5) {
                vertices2[i] = color2;
            }
        }
    }

    public void setColor(Color tint) {
        float color2 = tint.toFloatBits();
        if (color2 != this.color) {
            this.color = color2;
            float[] vertices2 = this.vertices;
            int n = this.idx;
            for (int i = 2; i < n; i += 5) {
                vertices2[i] = color2;
            }
        }
    }

    public void setColor(float r, float g, float b, float a) {
        float color2 = NumberUtils.intToFloatColor((((int) (255.0f * a)) << 24) | (((int) (255.0f * b)) << 16) | (((int) (255.0f * g)) << 8) | ((int) (255.0f * r)));
        if (color2 != this.color) {
            this.color = color2;
            float[] vertices2 = this.vertices;
            int n = this.idx;
            for (int i = 2; i < n; i += 5) {
                vertices2[i] = color2;
            }
        }
    }

    public void setColor(Color tint, int start, int end) {
        float color2 = tint.toFloatBits();
        float[] vertices2 = this.vertices;
        int n = end * 20;
        for (int i = (start * 20) + 2; i < n; i += 5) {
            vertices2[i] = color2;
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(this.font.getRegion().getTexture(), this.vertices, 0, this.idx);
    }

    public void draw(SpriteBatch spriteBatch, float alphaModulation) {
        if (alphaModulation == 1.0f) {
            draw(spriteBatch);
            return;
        }
        Color color2 = getColor();
        float oldAlpha = color2.f67a;
        color2.f67a *= alphaModulation;
        setColor(color2);
        draw(spriteBatch);
        color2.f67a = oldAlpha;
        setColor(color2);
    }

    public Color getColor() {
        float f = this.color;
        int intBits = NumberUtils.floatToIntColor(this.color);
        Color color2 = this.tempColor;
        color2.f70r = ((float) (intBits & 255)) / 255.0f;
        color2.f69g = ((float) ((intBits >>> 8) & 255)) / 255.0f;
        color2.f68b = ((float) ((intBits >>> 16) & 255)) / 255.0f;
        color2.f67a = ((float) ((intBits >>> 24) & 255)) / 255.0f;
        return color2;
    }

    public void clear() {
        this.f75x = 0.0f;
        this.f76y = 0.0f;
        this.idx = 0;
    }

    private void require(int glyphCount) {
        int vertexCount = this.idx + (glyphCount * 20);
        if (this.vertices == null || this.vertices.length < vertexCount) {
            float[] newVertices = new float[vertexCount];
            System.arraycopy(this.vertices, 0, newVertices, 0, this.idx);
            this.vertices = newVertices;
        }
    }

    private float addToCache(CharSequence str, float x, float y, int start, int end) {
        int start2;
        int start3;
        float startX = x;
        BitmapFont.Glyph lastGlyph = null;
        BitmapFont.BitmapFontData data = this.font.data;
        if (data.scaleX == 1.0f && data.scaleY == 1.0f) {
            while (true) {
                start2 = start;
                if (start2 >= end) {
                    break;
                }
                start = start2 + 1;
                lastGlyph = data.getGlyph(str.charAt(start2));
                if (lastGlyph != null) {
                    addGlyph(lastGlyph, x + ((float) lastGlyph.xoffset), y + ((float) lastGlyph.yoffset), (float) lastGlyph.width, (float) lastGlyph.height);
                    x += (float) lastGlyph.xadvance;
                    start2 = start;
                    break;
                }
            }
            while (start2 < end) {
                int start4 = start2 + 1;
                char ch = str.charAt(start2);
                BitmapFont.Glyph g = data.getGlyph(ch);
                if (g != null) {
                    float x2 = x + ((float) lastGlyph.getKerning(ch));
                    lastGlyph = g;
                    addGlyph(lastGlyph, x2 + ((float) g.xoffset), y + ((float) g.yoffset), (float) g.width, (float) g.height);
                    x = x2 + ((float) g.xadvance);
                }
                start2 = start4;
            }
        } else {
            float scaleX = data.scaleX;
            float scaleY = data.scaleY;
            while (true) {
                start3 = start;
                if (start3 >= end) {
                    break;
                }
                start = start3 + 1;
                lastGlyph = data.getGlyph(str.charAt(start3));
                if (lastGlyph != null) {
                    addGlyph(lastGlyph, x + (((float) lastGlyph.xoffset) * scaleX), y + (((float) lastGlyph.yoffset) * scaleY), ((float) lastGlyph.width) * scaleX, ((float) lastGlyph.height) * scaleY);
                    x += ((float) lastGlyph.xadvance) * scaleX;
                    start3 = start;
                    break;
                }
            }
            while (start2 < end) {
                int start5 = start2 + 1;
                char ch2 = str.charAt(start2);
                BitmapFont.Glyph g2 = data.getGlyph(ch2);
                if (g2 != null) {
                    float x3 = x + (((float) lastGlyph.getKerning(ch2)) * scaleX);
                    lastGlyph = g2;
                    addGlyph(lastGlyph, x3 + (((float) g2.xoffset) * scaleX), y + (((float) g2.yoffset) * scaleY), ((float) g2.width) * scaleX, ((float) g2.height) * scaleY);
                    x = x3 + (((float) g2.xadvance) * scaleX);
                }
                start3 = start5;
            }
        }
        int i = start2;
        return x - startX;
    }

    private void addGlyph(BitmapFont.Glyph glyph, float x, float y, float width, float height) {
        float x2 = x + width;
        float y2 = y + height;
        float u = glyph.f71u;
        float u2 = glyph.f72u2;
        float v = glyph.f73v;
        float v2 = glyph.f74v2;
        float[] vertices2 = this.vertices;
        if (this.integer) {
            x = (float) Math.round(x);
            y = (float) Math.round(y);
            x2 = (float) Math.round(x2);
            y2 = (float) Math.round(y2);
        }
        int idx2 = this.idx;
        this.idx += 20;
        int idx3 = idx2 + 1;
        vertices2[idx2] = x;
        int idx4 = idx3 + 1;
        vertices2[idx3] = y;
        int idx5 = idx4 + 1;
        vertices2[idx4] = this.color;
        int idx6 = idx5 + 1;
        vertices2[idx5] = u;
        int idx7 = idx6 + 1;
        vertices2[idx6] = v;
        int idx8 = idx7 + 1;
        vertices2[idx7] = x;
        int idx9 = idx8 + 1;
        vertices2[idx8] = y2;
        int idx10 = idx9 + 1;
        vertices2[idx9] = this.color;
        int idx11 = idx10 + 1;
        vertices2[idx10] = u;
        int idx12 = idx11 + 1;
        vertices2[idx11] = v2;
        int idx13 = idx12 + 1;
        vertices2[idx12] = x2;
        int idx14 = idx13 + 1;
        vertices2[idx13] = y2;
        int idx15 = idx14 + 1;
        vertices2[idx14] = this.color;
        int idx16 = idx15 + 1;
        vertices2[idx15] = u2;
        int idx17 = idx16 + 1;
        vertices2[idx16] = v2;
        int idx18 = idx17 + 1;
        vertices2[idx17] = x2;
        int idx19 = idx18 + 1;
        vertices2[idx18] = y;
        int idx20 = idx19 + 1;
        vertices2[idx19] = this.color;
        vertices2[idx20] = u2;
        vertices2[idx20 + 1] = v;
    }

    public BitmapFont.TextBounds setText(CharSequence str, float x, float y) {
        clear();
        return addText(str, x, y, 0, str.length());
    }

    public BitmapFont.TextBounds setText(CharSequence str, float x, float y, int start, int end) {
        clear();
        return addText(str, x, y, start, end);
    }

    public BitmapFont.TextBounds addText(CharSequence str, float x, float y) {
        return addText(str, x, y, 0, str.length());
    }

    public BitmapFont.TextBounds addText(CharSequence str, float x, float y, int start, int end) {
        require(end - start);
        float y2 = y + this.font.data.ascent;
        this.textBounds.width = addToCache(str, x, y2, start, end);
        this.textBounds.height = this.font.data.capHeight;
        return this.textBounds;
    }

    public BitmapFont.TextBounds setMultiLineText(CharSequence str, float x, float y) {
        clear();
        return addMultiLineText(str, x, y, 0.0f, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds setMultiLineText(CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
        clear();
        return addMultiLineText(str, x, y, alignmentWidth, alignment);
    }

    public BitmapFont.TextBounds addMultiLineText(CharSequence str, float x, float y) {
        return addMultiLineText(str, x, y, 0.0f, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds addMultiLineText(CharSequence str, float x, float y, float alignmentWidth, BitmapFont.HAlignment alignment) {
        BitmapFont font2 = this.font;
        int length = str.length();
        require(length);
        float y2 = y + font2.data.ascent;
        float down = font2.data.down;
        float maxWidth = 0.0f;
        float f = y2;
        int start = 0;
        int numLines = 0;
        while (start < length) {
            int lineEnd = BitmapFont.indexOf(str, 10, start);
            float xOffset = 0.0f;
            if (alignment != BitmapFont.HAlignment.LEFT) {
                xOffset = alignmentWidth - font2.getBounds(str, start, lineEnd).width;
                if (alignment == BitmapFont.HAlignment.CENTER) {
                    xOffset /= 2.0f;
                }
            }
            maxWidth = Math.max(maxWidth, addToCache(str, x + xOffset, y2, start, lineEnd));
            start = lineEnd + 1;
            y2 += down;
            numLines++;
        }
        this.textBounds.width = maxWidth;
        this.textBounds.height = font2.data.capHeight + (((float) (numLines - 1)) * font2.data.lineHeight);
        return this.textBounds;
    }

    public BitmapFont.TextBounds setWrappedText(CharSequence str, float x, float y, float wrapWidth) {
        clear();
        return addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds setWrappedText(CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
        clear();
        return addWrappedText(str, x, y, wrapWidth, alignment);
    }

    public BitmapFont.TextBounds addWrappedText(CharSequence str, float x, float y, float wrapWidth) {
        return addWrappedText(str, x, y, wrapWidth, BitmapFont.HAlignment.LEFT);
    }

    public BitmapFont.TextBounds addWrappedText(CharSequence str, float x, float y, float wrapWidth, BitmapFont.HAlignment alignment) {
        BitmapFont font2 = this.font;
        int length = str.length();
        require(length);
        float y2 = y + font2.data.ascent;
        float down = font2.data.down;
        if (wrapWidth <= 0.0f) {
            wrapWidth = 2.14748365E9f;
        }
        float maxWidth = 0.0f;
        int start = 0;
        int numLines = 0;
        while (start < length) {
            int newLine = BitmapFont.indexOf(str, 10, start);
            while (start < newLine && BitmapFont.isWhitespace(str.charAt(start))) {
                start++;
            }
            int lineEnd = start + font2.computeVisibleGlyphs(str, start, newLine, wrapWidth);
            int nextStart = lineEnd + 1;
            if (lineEnd < newLine) {
                while (lineEnd > start && !BitmapFont.isWhitespace(str.charAt(lineEnd))) {
                    lineEnd--;
                }
                if (lineEnd != start) {
                    nextStart = lineEnd;
                    while (lineEnd > start) {
                        if (!BitmapFont.isWhitespace(str.charAt(lineEnd - 1))) {
                            break;
                        }
                        lineEnd--;
                    }
                } else {
                    if (nextStart > start + 1) {
                        nextStart--;
                    }
                    lineEnd = nextStart;
                }
            }
            if (lineEnd > start) {
                float xOffset = 0.0f;
                if (alignment != BitmapFont.HAlignment.LEFT) {
                    xOffset = wrapWidth - font2.getBounds(str, start, lineEnd).width;
                    if (alignment == BitmapFont.HAlignment.CENTER) {
                        xOffset /= 2.0f;
                    }
                }
                maxWidth = Math.max(maxWidth, addToCache(str, x + xOffset, y2, start, lineEnd));
            }
            start = nextStart;
            y2 += down;
            numLines++;
        }
        this.textBounds.width = maxWidth;
        this.textBounds.height = font2.data.capHeight + (((float) (numLines - 1)) * font2.data.lineHeight);
        return this.textBounds;
    }

    public BitmapFont.TextBounds getBounds() {
        return this.textBounds;
    }

    public float getX() {
        return this.f75x;
    }

    public float getY() {
        return this.f76y;
    }

    public BitmapFont getFont() {
        return this.font;
    }

    public void setUseIntegerPositions(boolean use) {
        this.integer = use;
    }

    public boolean usesIntegerPositions() {
        return this.integer;
    }

    public float[] getVertices() {
        return this.vertices;
    }
}
