package com.badlogic.gdx.graphics.g3d.model.skeleton;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class SkeletonKeyframe {
    public int parentIndex = -1;
    public final Vector3 position = new Vector3();
    public final Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    public final Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);
    public float timeStamp = 0.0f;

    public String toString() {
        return "time: " + this.timeStamp + ", " + "parent: " + this.parentIndex + ", " + "position: " + this.position + ", " + "scale: " + this.scale + ", " + "rotation: " + this.rotation;
    }
}
