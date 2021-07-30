package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class StillModelNode implements StillModelInstance {
    private static final float[] vec3 = {0.0f, 0.0f, 0.0f};
    public Material[] materials;
    public final Matrix4 matrix;
    public final Vector3 origin;
    public float radius;
    public final Vector3 transformedPosition;

    public StillModelNode() {
        this((Material[]) null);
    }

    public StillModelNode(Material[] materials2) {
        this.origin = new Vector3();
        this.transformedPosition = new Vector3();
        this.matrix = new Matrix4();
        this.materials = materials2;
    }

    public Matrix4 getTransform() {
        return this.matrix;
    }

    public Vector3 getSortCenter() {
        vec3[0] = this.origin.f170x;
        vec3[1] = this.origin.f171y;
        vec3[2] = this.origin.f172z;
        Matrix4.mulVec(this.matrix.val, vec3);
        this.transformedPosition.f170x = vec3[0];
        this.transformedPosition.f171y = vec3[1];
        this.transformedPosition.f172z = vec3[2];
        return this.transformedPosition;
    }

    public Material[] getMaterials() {
        return this.materials;
    }

    public float getBoundingSphereRadius() {
        return this.radius;
    }

    public StillModelNode copy() {
        StillModelNode copy = new StillModelNode();
        if (this.materials != null) {
            int len = this.materials.length;
            Material[] mats = new Material[len];
            for (int i = 0; i < len; i++) {
                mats[i] = this.materials[i].copy();
            }
            copy.materials = mats;
        }
        copy.matrix.set(this.matrix.val);
        copy.origin.set(this.origin);
        copy.radius = this.radius;
        copy.transformedPosition.set(this.transformedPosition);
        return copy;
    }
}
