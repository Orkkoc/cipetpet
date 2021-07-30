package com.badlogic.gdx.graphics.g3d.model.keyframe;

import com.badlogic.gdx.graphics.g3d.model.Animation;

public class KeyframedAnimation extends Animation {
    public final float frameDuration;
    public final Keyframe[] keyframes;

    public KeyframedAnimation(String name, float frameDuration2, Keyframe[] keyframes2) {
        super(name, ((float) keyframes2.length) * frameDuration2);
        this.frameDuration = frameDuration2;
        this.keyframes = keyframes2;
    }
}
