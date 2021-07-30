package com.badlogic.gdx.graphics.g3d.model.skeleton;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.math.collision.BoundingBox;

public class SkeletonSubMesh extends SubMesh {
    public int[][] boneAssignments;
    public float[][] boneWeights;
    public short[] indices;
    public float[] skinnedVertices;
    public float[] vertices;

    public SkeletonSubMesh(String name, Mesh mesh, int primitiveType) {
        super(name, mesh, primitiveType);
    }

    public void getBoundingBox(BoundingBox bbox) {
        this.mesh.calculateBoundingBox(bbox);
    }

    public void setVertices(float[] vertices2) {
        this.vertices = vertices2;
    }

    public void setIndices(short[] indices2) {
        this.indices = indices2;
    }
}
