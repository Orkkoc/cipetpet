package com.badlogic.gdx.graphics.g3d.model.still;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.math.collision.BoundingBox;

public class StillSubMesh extends SubMesh {
    public StillSubMesh(String name, Mesh mesh, int primitiveType, Material material) {
        super(name, mesh, primitiveType, material);
    }

    public StillSubMesh(String name, Mesh mesh, int primitiveType) {
        super(name, mesh, primitiveType);
    }

    public void getBoundingBox(BoundingBox bbox) {
        this.mesh.calculateBoundingBox(bbox);
    }
}
