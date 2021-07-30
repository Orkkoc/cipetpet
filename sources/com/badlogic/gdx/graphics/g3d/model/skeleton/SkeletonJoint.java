package com.badlogic.gdx.graphics.g3d.model.skeleton;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class SkeletonJoint {
    public final Array<SkeletonJoint> children = new Array<>(1);
    public int index;
    public String name;
    public SkeletonJoint parent;
    public int parentIndex;
    public final Vector3 position = new Vector3();
    public final Quaternion rotation = new Quaternion(new Vector3(0.0f, 1.0f, 0.0f), 0.0f);
    public final Vector3 scale = new Vector3(1.0f, 1.0f, 1.0f);
}
