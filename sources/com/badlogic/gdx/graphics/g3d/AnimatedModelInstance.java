package com.badlogic.gdx.graphics.g3d;

public interface AnimatedModelInstance extends StillModelInstance {
    String getAnimation();

    float getAnimationTime();

    boolean isLooping();
}
