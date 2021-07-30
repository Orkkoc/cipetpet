package com.badlogic.gdx.graphics.g3d.model.skeleton;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import java.util.HashMap;
import java.util.Map;

public class Skeleton {
    private static final Matrix4 IDENTITY = new Matrix4();
    public final Array<SkeletonKeyframe> animPoseJoints = new Array<>();
    public final ObjectMap<String, SkeletonAnimation> animations = new ObjectMap<>();
    public final Array<SkeletonKeyframe> bindPoseJoints = new Array<>();
    public final Array<Matrix4> combinedMatrices = new Array<>();
    public final Array<SkeletonJoint> hierarchy = new Array<>();
    public final Array<String> jointNames = new Array<>();
    public final Map<String, Integer> namesToIndices = new HashMap();
    public final Array<Matrix4> offsetMatrices = new Array<>();
    private final Matrix4 rotMatrix = new Matrix4();
    public final Array<Matrix4> sceneMatrices = new Array<>();

    public void buildFromHierarchy() {
        this.jointNames.clear();
        this.namesToIndices.clear();
        this.bindPoseJoints.clear();
        this.animPoseJoints.clear();
        this.offsetMatrices.clear();
        this.sceneMatrices.clear();
        for (int i = 0; i < this.hierarchy.size; i++) {
            recursiveFill(this.hierarchy.get(i));
        }
        calculateMatrices(this.bindPoseJoints);
        calculateOffsetMatrices();
    }

    private void recursiveFill(SkeletonJoint joint) {
        joint.index = this.bindPoseJoints.size;
        joint.parentIndex = joint.parent != null ? joint.parent.index : -1;
        SkeletonKeyframe keyFrame = new SkeletonKeyframe();
        keyFrame.position.set(joint.position);
        keyFrame.scale.set(joint.scale);
        keyFrame.rotation.set(joint.rotation);
        keyFrame.parentIndex = joint.parentIndex;
        this.jointNames.add(joint.name);
        this.namesToIndices.put(joint.name, Integer.valueOf(joint.index));
        this.bindPoseJoints.add(keyFrame);
        SkeletonKeyframe animKeyframe = new SkeletonKeyframe();
        animKeyframe.parentIndex = joint.parentIndex;
        this.animPoseJoints.add(animKeyframe);
        this.offsetMatrices.add(new Matrix4());
        this.sceneMatrices.add(new Matrix4());
        this.combinedMatrices.add(new Matrix4());
        int len = joint.children.size;
        for (int i = 0; i < len; i++) {
            recursiveFill(joint.children.get(i));
        }
    }

    /* access modifiers changed from: protected */
    public void calculateOffsetMatrices() {
        for (int i = 0; i < this.offsetMatrices.size; i++) {
            this.offsetMatrices.get(i).set(this.sceneMatrices.get(i)).inv();
        }
    }

    /* access modifiers changed from: protected */
    public void calculateMatrices(Array<SkeletonKeyframe> joints) {
        for (int i = 0; i < joints.size; i++) {
            SkeletonKeyframe joint = joints.get(i);
            Matrix4 sceneMatrix = this.sceneMatrices.get(i);
            Matrix4 parentMatrix = joint.parentIndex != -1 ? this.sceneMatrices.get(joint.parentIndex) : IDENTITY;
            Matrix4 combinedMatrix = this.combinedMatrices.get(i);
            joint.rotation.toMatrix(this.rotMatrix.val);
            this.rotMatrix.trn(joint.position);
            this.rotMatrix.scl(joint.scale);
            sceneMatrix.set(parentMatrix);
            sceneMatrix.mul(this.rotMatrix);
            combinedMatrix.set(sceneMatrix);
            combinedMatrix.mul(this.offsetMatrices.get(i));
        }
    }

    public void setAnimation(String name, float time) {
        SkeletonKeyframe endFrame;
        SkeletonAnimation anim = this.animations.get(name);
        if (anim == null) {
            throw new IllegalArgumentException("Animation with name '" + name + "' does not exist");
        } else if (time < 0.0f || time > anim.totalDuration) {
            throw new IllegalArgumentException("time must be 0 <= time <= animation duration");
        } else {
            int len = anim.perJointkeyFrames.length;
            for (int i = 0; i < len; i++) {
                SkeletonKeyframe[] jointTrack = anim.perJointkeyFrames[i];
                int idx = 0;
                int len2 = jointTrack.length;
                int j = 0;
                while (true) {
                    if (j >= len2) {
                        break;
                    } else if (jointTrack[j].timeStamp >= time) {
                        idx = Math.max(0, j - 1);
                        break;
                    } else {
                        j++;
                    }
                }
                SkeletonKeyframe startFrame = jointTrack[idx];
                if (idx + 1 == len2) {
                    endFrame = startFrame;
                } else {
                    endFrame = jointTrack[idx + 1];
                }
                float alpha = 0.0f;
                if (startFrame != endFrame) {
                    alpha = Math.min(1.0f, (time - startFrame.timeStamp) / (endFrame.timeStamp - startFrame.timeStamp));
                }
                SkeletonKeyframe animFrame = this.animPoseJoints.get(i);
                animFrame.position.set(startFrame.position).lerp(endFrame.position, alpha);
                animFrame.scale.set(startFrame.scale).lerp(endFrame.scale, alpha);
                animFrame.rotation.set(startFrame.rotation).slerp(endFrame.rotation, alpha);
            }
            calculateMatrices(this.animPoseJoints);
        }
    }

    public void setBindPose() {
        calculateMatrices(this.bindPoseJoints);
    }
}
