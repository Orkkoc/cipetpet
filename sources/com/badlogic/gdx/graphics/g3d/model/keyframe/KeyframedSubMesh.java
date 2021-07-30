package com.badlogic.gdx.graphics.g3d.model.keyframe;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.ObjectMap;

public class KeyframedSubMesh extends SubMesh {
    public final int animatedComponents;
    public final ObjectMap<String, KeyframedAnimation> animations;
    public final float[] blendedVertices;

    public KeyframedSubMesh(String name, Mesh mesh, float[] blendedVertices2, ObjectMap<String, KeyframedAnimation> animations2, int animatedComponents2, int primitiveType) {
        super(name, mesh, primitiveType);
        this.blendedVertices = blendedVertices2;
        this.animations = animations2;
        this.animatedComponents = animatedComponents2;
    }

    public void getBoundingBox(BoundingBox bbox) {
        this.mesh.calculateBoundingBox(bbox);
    }
}
