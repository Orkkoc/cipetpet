package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.collision.BoundingBox;

public interface Model {
    void dispose();

    void getBoundingBox(BoundingBox boundingBox);

    SubMesh getSubMesh(String str);

    SubMesh[] getSubMeshes();

    Model getSubModel(String... strArr);

    void render();

    void render(ShaderProgram shaderProgram);

    void setMaterial(Material material);

    void setMaterials(Material... materialArr);
}
