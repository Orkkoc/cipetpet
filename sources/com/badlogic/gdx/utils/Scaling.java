package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.Vector2;

public enum Scaling {
    fit,
    fill,
    fillX,
    fillY,
    stretch,
    stretchX,
    stretchY,
    none;
    
    private static Vector2 temp;

    static {
        temp = new Vector2();
    }

    public Vector2 apply(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
        switch (this) {
            case fit:
                float scale = targetHeight / targetWidth > sourceHeight / sourceWidth ? targetWidth / sourceWidth : targetHeight / sourceHeight;
                temp.f165x = sourceWidth * scale;
                temp.f166y = sourceHeight * scale;
                break;
            case fill:
                float scale2 = targetHeight / targetWidth < sourceHeight / sourceWidth ? targetWidth / sourceWidth : targetHeight / sourceHeight;
                temp.f165x = sourceWidth * scale2;
                temp.f166y = sourceHeight * scale2;
                break;
            case fillX:
                float targetRatio = targetHeight / targetWidth;
                float f = sourceHeight / sourceWidth;
                float scale3 = targetWidth / sourceWidth;
                temp.f165x = sourceWidth * scale3;
                temp.f166y = sourceHeight * scale3;
                break;
            case fillY:
                float targetRatio2 = targetHeight / targetWidth;
                float f2 = sourceHeight / sourceWidth;
                float scale4 = targetHeight / sourceHeight;
                temp.f165x = sourceWidth * scale4;
                temp.f166y = sourceHeight * scale4;
                break;
            case stretch:
                temp.f165x = targetWidth;
                temp.f166y = targetHeight;
                break;
            case stretchX:
                temp.f165x = targetWidth;
                temp.f166y = sourceHeight;
                break;
            case stretchY:
                temp.f165x = sourceWidth;
                temp.f166y = targetHeight;
                break;
            case none:
                temp.f165x = sourceWidth;
                temp.f166y = sourceHeight;
                break;
        }
        return temp;
    }
}
