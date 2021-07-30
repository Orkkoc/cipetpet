package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public interface StillModelInstance {
    float getBoundingSphereRadius();

    Material[] getMaterials();

    Vector3 getSortCenter();

    Matrix4 getTransform();
}
