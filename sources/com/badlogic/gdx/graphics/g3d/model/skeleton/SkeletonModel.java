package com.badlogic.gdx.graphics.g3d.model.skeleton;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute;
import com.badlogic.gdx.graphics.g3d.model.AnimatedModel;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class SkeletonModel implements AnimatedModel {
    private static final BoundingBox tmpBox = new BoundingBox();
    protected SkeletonAnimation[] animations;
    public final Skeleton skeleton;
    public final SkeletonSubMesh[] subMeshes;

    /* renamed from: v */
    final Vector3 f141v = new Vector3();

    public SkeletonModel(Skeleton skeleton2, SubMesh[] subMeshes2) {
        this.skeleton = skeleton2;
        this.subMeshes = new SkeletonSubMesh[subMeshes2.length];
        for (int i = 0; i < subMeshes2.length; i++) {
            this.subMeshes[i] = subMeshes2[i];
        }
        setMaterial(new Material("default", new MaterialAttribute[0]));
    }

    public void setBindPose() {
        this.skeleton.setBindPose();
        for (SkeletonSubMesh skin : this.subMeshes) {
            skin(skin, this.skeleton.combinedMatrices);
        }
    }

    public void setAnimation(String animation, float time, boolean loop) {
        this.skeleton.setAnimation(animation, time);
        for (SkeletonSubMesh skin : this.subMeshes) {
            skin(skin, this.skeleton.combinedMatrices);
        }
    }

    public void skin(SkeletonSubMesh subMesh, Array<Matrix4> boneMatrices) {
        int nidx;
        int stride = subMesh.mesh.getVertexSize() / 4;
        int numVertices = subMesh.mesh.getNumVertices();
        int idx = 0;
        if (subMesh.mesh.getVertexAttribute(2) == null) {
            nidx = -1;
        } else {
            nidx = subMesh.mesh.getVertexAttribute(2).offset / 4;
        }
        float[] vertices = subMesh.vertices;
        float[] skinnedVertices = subMesh.skinnedVertices;
        System.arraycopy(subMesh.vertices, 0, skinnedVertices, 0, subMesh.vertices.length);
        int i = 0;
        while (i < numVertices) {
            int[] boneIndices = subMesh.boneAssignments[i];
            float[] boneWeights = subMesh.boneWeights[i];
            float ox = vertices[idx];
            float oy = vertices[idx + 1];
            float oz = vertices[idx + 2];
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            float onx = 0.0f;
            float ony = 0.0f;
            float onz = 0.0f;
            float nx = 0.0f;
            float ny = 0.0f;
            float nz = 0.0f;
            if (nidx != -1) {
                onx = vertices[nidx];
                ony = vertices[nidx + 1];
                onz = vertices[nidx + 2];
            }
            for (int j = 0; j < boneIndices.length; j++) {
                int boneIndex = boneIndices[j];
                float weight = boneWeights[j];
                this.f141v.set(ox, oy, oz);
                this.f141v.mul(boneMatrices.get(boneIndex));
                x += this.f141v.f170x * weight;
                y += this.f141v.f171y * weight;
                z += this.f141v.f172z * weight;
                if (nidx != -1) {
                    this.f141v.set(onx, ony, onz);
                    this.f141v.rot(boneMatrices.get(boneIndex));
                    nx += this.f141v.f170x * weight;
                    ny += this.f141v.f171y * weight;
                    nz += this.f141v.f172z * weight;
                }
            }
            skinnedVertices[idx] = x;
            skinnedVertices[idx + 1] = y;
            skinnedVertices[idx + 2] = z;
            if (nidx != -1) {
                skinnedVertices[nidx] = nx;
                skinnedVertices[nidx + 1] = ny;
                skinnedVertices[nidx + 2] = nz;
            }
            i++;
            idx += stride;
            nidx += stride;
        }
        subMesh.mesh.setVertices(skinnedVertices);
    }

    public void render() {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            SkeletonSubMesh subMesh = this.subMeshes[i];
            if (i == 0) {
                subMesh.material.bind();
            } else if (!this.subMeshes[i - 1].material.equals(subMesh.material)) {
                subMesh.material.bind();
            }
            subMesh.mesh.render(subMesh.primitiveType);
        }
    }

    public void render(ShaderProgram program) {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            SkeletonSubMesh subMesh = this.subMeshes[i];
            if (i == 0) {
                subMesh.material.bind(program);
            } else if (!this.subMeshes[i - 1].material.equals(subMesh.material)) {
                subMesh.material.bind(program);
            }
            subMesh.mesh.render(program, subMesh.primitiveType);
        }
    }

    public void setMaterials(Material... materials) {
        if (materials.length != this.subMeshes.length) {
            throw new UnsupportedOperationException("number of materials must equal number of sub-meshes");
        }
        int len = materials.length;
        for (int i = 0; i < len; i++) {
            this.subMeshes[i].material = materials[i];
        }
    }

    public void setMaterial(Material material) {
        for (SkeletonSubMesh skeletonSubMesh : this.subMeshes) {
            skeletonSubMesh.material = material;
        }
    }

    public SubMesh getSubMesh(String name) {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            if (this.subMeshes[i].name.equals(name)) {
                return this.subMeshes[i];
            }
        }
        return null;
    }

    public SubMesh[] getSubMeshes() {
        return this.subMeshes;
    }

    public SkeletonAnimation getAnimation(String name) {
        return this.skeleton.animations.get(name);
    }

    public SkeletonAnimation[] getAnimations() {
        if (this.animations == null || this.animations.length != this.skeleton.animations.size) {
            this.animations = new SkeletonAnimation[this.skeleton.animations.size];
            int i = 0;
            Iterator i$ = this.skeleton.animations.values().iterator();
            while (i$.hasNext()) {
                this.animations[i] = i$.next();
                i++;
            }
        }
        return this.animations;
    }

    public Model getSubModel(String... subMeshNames) {
        return null;
    }

    public void getBoundingBox(BoundingBox bbox) {
        bbox.inf();
        for (SkeletonSubMesh skeletonSubMesh : this.subMeshes) {
            skeletonSubMesh.mesh.calculateBoundingBox(tmpBox);
            bbox.ext(tmpBox);
        }
    }

    public void dispose() {
        for (SkeletonSubMesh skeletonSubMesh : this.subMeshes) {
            skeletonSubMesh.mesh.dispose();
        }
    }
}
