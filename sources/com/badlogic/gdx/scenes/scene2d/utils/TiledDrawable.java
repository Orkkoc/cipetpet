package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TiledDrawable extends TextureRegionDrawable {
    public TiledDrawable() {
    }

    public TiledDrawable(TextureRegion region) {
        super(region);
    }

    public TiledDrawable(TextureRegionDrawable drawable) {
        super(drawable);
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        TextureRegion region = getRegion();
        float regionWidth = (float) region.getRegionWidth();
        float regionHeight = (float) region.getRegionHeight();
        float remainingX = width % regionWidth;
        float remainingY = height % regionHeight;
        float startX = x;
        float startY = y;
        float endX = (x + width) - remainingX;
        float endY = (y + height) - remainingY;
        while (x < endX) {
            y = startY;
            while (y < endY) {
                batch.draw(region, x, y, regionWidth, regionHeight);
                y += regionHeight;
            }
            x += regionWidth;
        }
        Texture texture = region.getTexture();
        float u = region.getU();
        float v2 = region.getV2();
        if (remainingX > 0.0f) {
            float u2 = u + (remainingX / ((float) texture.getWidth()));
            float v = region.getV();
            y = startY;
            while (y < endY) {
                batch.draw(texture, x, y, remainingX, regionHeight, u, v2, u2, v);
                y = y + regionHeight;
            }
            if (remainingY > 0.0f) {
                batch.draw(texture, x, y, remainingX, remainingY, u, v2, u2, v2 - (remainingY / ((float) texture.getHeight())));
            }
        }
        if (remainingY > 0.0f) {
            float u22 = region.getU2();
            float v3 = v2 - (remainingY / ((float) texture.getHeight()));
            for (float x2 = startX; x2 < endX; x2 += regionWidth) {
                batch.draw(texture, x2, y, regionWidth, remainingY, u, v2, u22, v3);
            }
        }
    }
}
