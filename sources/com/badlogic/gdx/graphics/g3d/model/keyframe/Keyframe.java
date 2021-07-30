package com.badlogic.gdx.graphics.g3d.model.keyframe;

public class Keyframe {
    public final float timeStamp;
    public final float[] vertices;

    public Keyframe(float timeStamp2, float[] vertices2) {
        this.timeStamp = timeStamp2;
        this.vertices = vertices2;
    }
}
