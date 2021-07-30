package com.badlogic.gdx.graphics.g3d.model;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class SubMesh {
    public Material material;
    public Mesh mesh;
    public String name;
    public int primitiveType;

    public abstract void getBoundingBox(BoundingBox boundingBox);

    public SubMesh(String name2, Mesh mesh2, int primitiveType2, Material material2) {
        this.name = name2;
        setMesh(mesh2);
        this.primitiveType = primitiveType2;
        this.material = material2;
    }

    public SubMesh(String name2, Mesh mesh2, int primitiveType2) {
        this(name2, mesh2, primitiveType2, (Material) null);
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void setMesh(Mesh mesh2) {
        this.mesh = mesh2;
    }
}
