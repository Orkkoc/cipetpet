package com.badlogic.gdx.graphics.g3d.model;

public interface AnimatedModel extends Model {
    Animation getAnimation(String str);

    Animation[] getAnimations();

    void setAnimation(String str, float f, boolean z);
}
