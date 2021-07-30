package com.badlogic.gdx.graphics.g3d.model.keyframe;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.AnimatedModel;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class KeyframedModel implements AnimatedModel, Disposable {
    private static final BoundingBox tmpBox = new BoundingBox();
    protected final KeyframedAnimation[] animations;
    public final KeyframedSubMesh[] subMeshes;

    public KeyframedModel(KeyframedSubMesh[] subMeshes2) {
        this.subMeshes = subMeshes2;
        Array<KeyframedAnimation> meshAnims = subMeshes2[0].animations.values().toArray();
        this.animations = new KeyframedAnimation[meshAnims.size];
        for (int i = 0; i < this.animations.length; i++) {
            this.animations[i] = meshAnims.get(i);
        }
        checkValidity();
    }

    private void checkValidity() {
        for (int i = 0; i < this.subMeshes.length; i++) {
            if (this.subMeshes[i].animations.size != this.animations.length) {
                throw new GdxRuntimeException("number of animations in subMesh[0] is not the same in subMesh[" + i + "]. All sub-meshes must have the same animations and number of frames");
            }
        }
        for (KeyframedAnimation anim : this.animations) {
            int j = 0;
            while (j < this.subMeshes.length) {
                KeyframedAnimation otherAnim = this.subMeshes[j].animations.get(anim.name);
                if (otherAnim == null) {
                    throw new GdxRuntimeException("animation '" + anim.name + "' missing in subMesh[" + j + "]");
                } else if (otherAnim.frameDuration != anim.frameDuration) {
                    throw new GdxRuntimeException("animation '" + anim.name + "' in subMesh[" + j + "] has different frame duration than the same animation in subMesh[0]");
                } else if (otherAnim.keyframes.length != anim.keyframes.length) {
                    throw new GdxRuntimeException("animation '" + anim.name + "' in subMesh[" + j + "] has different number of keyframes than the same animation in subMesh[0]");
                } else {
                    j++;
                }
            }
        }
    }

    public void render() {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            KeyframedSubMesh subMesh = this.subMeshes[i];
            if (i == 0 || !this.subMeshes[i - 1].material.equals(subMesh.material)) {
                subMesh.material.bind();
            }
            subMesh.mesh.render(subMesh.primitiveType);
        }
    }

    public void render(ShaderProgram program) {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            KeyframedSubMesh subMesh = this.subMeshes[i];
            if (i == 0 || !this.subMeshes[i - 1].material.equals(subMesh.material)) {
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
        for (KeyframedSubMesh keyframedSubMesh : this.subMeshes) {
            keyframedSubMesh.material = material;
        }
    }

    public KeyframedSubMesh getSubMesh(String name) {
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

    public void setAnimation(String animation, float time, boolean loop) {
        int i;
        int len = this.subMeshes.length;
        int i2 = 0;
        while (i2 < len) {
            KeyframedSubMesh subMesh = this.subMeshes[i2];
            KeyframedAnimation anim = subMesh.animations.get(animation);
            if (anim == null) {
                throw new IllegalArgumentException("No animation with name '" + animation + "' in submesh #" + i2);
            } else if (time < 0.0f || time > anim.totalDuration) {
                throw new IllegalArgumentException("time must be 0 <= time <= animation duration");
            } else {
                int startIndex = (int) Math.floor((double) (time / anim.frameDuration));
                Keyframe startFrame = anim.keyframes[startIndex];
                Keyframe[] keyframeArr = anim.keyframes;
                if (anim.keyframes.length - 1 != startIndex) {
                    i = startIndex + 1;
                } else if (loop) {
                    i = 0;
                } else {
                    i = startIndex;
                }
                Keyframe endFrame = keyframeArr[i];
                int numComponents = subMesh.animatedComponents;
                float[] src = startFrame.vertices;
                int srcLen = numComponents * subMesh.mesh.getNumVertices();
                float[] dst = subMesh.blendedVertices;
                int dstInc = (subMesh.mesh.getVertexSize() / 4) - numComponents;
                if (startFrame == endFrame) {
                    int srcIdx = 0;
                    int dstIdx = 0;
                    while (srcIdx < srcLen) {
                        int j = 0;
                        int dstIdx2 = dstIdx;
                        int srcIdx2 = srcIdx;
                        while (j < numComponents) {
                            dst[dstIdx2] = src[srcIdx2];
                            j++;
                            dstIdx2++;
                            srcIdx2++;
                        }
                        dstIdx = dstIdx2 + dstInc;
                        srcIdx = srcIdx2;
                    }
                } else {
                    float[] src2 = endFrame.vertices;
                    float alpha = (time - (((float) startIndex) * anim.frameDuration)) / anim.frameDuration;
                    int srcIdx3 = 0;
                    int dstIdx3 = 0;
                    while (srcIdx3 < srcLen) {
                        int j2 = 0;
                        int dstIdx4 = dstIdx3;
                        int srcIdx4 = srcIdx3;
                        while (j2 < numComponents) {
                            float valSrc = src[srcIdx4];
                            dst[dstIdx4] = ((src2[srcIdx4] - valSrc) * alpha) + valSrc;
                            j2++;
                            dstIdx4++;
                            srcIdx4++;
                        }
                        dstIdx3 = dstIdx4 + dstInc;
                        srcIdx3 = srcIdx4;
                    }
                }
                subMesh.mesh.setVertices(dst);
                i2++;
            }
        }
    }

    public KeyframedAnimation getAnimation(String name) {
        return this.subMeshes[0].animations.get(name);
    }

    public KeyframedAnimation[] getAnimations() {
        return this.animations;
    }

    public Model getSubModel(String... subMeshNames) {
        return null;
    }

    public void getBoundingBox(BoundingBox bbox) {
        bbox.inf();
        for (KeyframedSubMesh keyframedSubMesh : this.subMeshes) {
            keyframedSubMesh.mesh.calculateBoundingBox(tmpBox);
            bbox.ext(tmpBox);
        }
    }

    public void dispose() {
        for (KeyframedSubMesh keyframedSubMesh : this.subMeshes) {
            keyframedSubMesh.mesh.dispose();
        }
    }
}
