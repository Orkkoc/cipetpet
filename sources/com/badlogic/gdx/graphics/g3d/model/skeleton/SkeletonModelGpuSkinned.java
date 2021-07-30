package com.badlogic.gdx.graphics.g3d.model.skeleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

public class SkeletonModelGpuSkinned extends SkeletonModel {
    public static final String BoneIndexAttribue = "a_boneIndex";
    public static final String BoneWeightAttribue = "a_boneWeight";

    public SkeletonModelGpuSkinned(Skeleton skeleton, SkeletonSubMesh[] subMeshes) {
        super(skeleton, subMeshes);
    }

    public static SkeletonModel CreateFromSkeletonModel(SkeletonModel skeletonModel) {
        if (Gdx.gl20 == null) {
            return skeletonModel;
        }
        SkeletonModelGpuSkinned model = new SkeletonModelGpuSkinned(skeletonModel.skeleton, skeletonModel.subMeshes);
        model.setupGpuSkin();
        return model;
    }

    public void setupGpuSkin() {
        for (SkeletonSubMesh skeletonSubMesh : this.subMeshes) {
            setupGpuSkin(skeletonSubMesh);
        }
    }

    private void setupGpuSkin(SkeletonSubMesh subMesh) {
        float f;
        VertexAttributes oldAttributes = subMesh.mesh.getVertexAttributes();
        int oldAttributeCount = oldAttributes.size();
        VertexAttribute[] attributeArray = new VertexAttribute[(oldAttributeCount + 2)];
        for (int i = 0; i < oldAttributeCount; i++) {
            attributeArray[i] = oldAttributes.get(i);
        }
        attributeArray[oldAttributeCount] = new VertexAttribute(4, 4, BoneIndexAttribue);
        attributeArray[oldAttributeCount + 1] = new VertexAttribute(4, 4, BoneWeightAttribue);
        VertexAttributes newAttributes = new VertexAttributes(attributeArray);
        Mesh newMesh = new Mesh(true, subMesh.mesh.getMaxVertices(), subMesh.mesh.getMaxIndices(), newAttributes);
        int stride = subMesh.mesh.getVertexSize() / 4;
        int newStride = newMesh.getVertexSize() / 4;
        int numVertices = subMesh.mesh.getNumVertices();
        int idx = 0;
        int newIdx = 0;
        int bidx = -1;
        int widx = -1;
        for (int i2 = 0; i2 < newAttributes.size(); i2++) {
            VertexAttribute a = newAttributes.get(i2);
            if (a.alias.equals(BoneIndexAttribue)) {
                bidx = a.offset / 4;
            } else if (a.alias.equals(BoneWeightAttribue)) {
                widx = a.offset / 4;
            }
        }
        if (bidx < 0 || widx < 0) {
            throw new IllegalArgumentException("Need Shader with bone index and bone wieght vectors to use GPU skinning");
        }
        float[] vertices = subMesh.vertices;
        float[] skinnedVertices = new float[(newStride * numVertices)];
        int i3 = 0;
        while (i3 < numVertices) {
            int[] boneIndices = subMesh.boneAssignments[i3];
            float[] boneWeights = subMesh.boneWeights[i3];
            System.arraycopy(vertices, idx, skinnedVertices, newIdx, stride);
            skinnedVertices[bidx] = boneIndices.length > 0 ? (float) boneIndices[0] : 0.0f;
            skinnedVertices[bidx + 1] = boneIndices.length > 1 ? (float) boneIndices[1] : 0.0f;
            skinnedVertices[bidx + 2] = boneIndices.length > 2 ? (float) boneIndices[2] : 0.0f;
            skinnedVertices[bidx + 3] = boneIndices.length > 3 ? (float) boneIndices[3] : 0.0f;
            skinnedVertices[widx] = boneWeights.length > 0 ? boneWeights[0] : 0.0f;
            skinnedVertices[widx + 1] = boneWeights.length > 1 ? boneWeights[1] : 0.0f;
            skinnedVertices[widx + 2] = boneWeights.length > 2 ? boneWeights[2] : 0.0f;
            int i4 = widx + 3;
            if (boneWeights.length > 3) {
                f = boneWeights[3];
            } else {
                f = 0.0f;
            }
            skinnedVertices[i4] = f;
            i3++;
            idx += stride;
            newIdx += newStride;
            bidx += newStride;
            widx += newStride;
        }
        newMesh.setVertices(skinnedVertices);
        newMesh.setIndices(subMesh.indices);
        subMesh.mesh.dispose();
        subMesh.mesh = newMesh;
        subMesh.skinnedVertices = null;
        subMesh.vertices = skinnedVertices;
    }

    public void setAnimation(String animation, float time, boolean loop) {
        this.skeleton.setAnimation(animation, time);
    }
}
