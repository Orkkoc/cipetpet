package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.materials.Material;

public class AnimatedModelNode extends StillModelNode implements AnimatedModelInstance {
    public String animation;
    public boolean looping;
    public float time;

    public AnimatedModelNode() {
    }

    public AnimatedModelNode(Material[] materials) {
        super(materials);
    }

    public String getAnimation() {
        return this.animation;
    }

    public float getAnimationTime() {
        return this.time;
    }

    public boolean isLooping() {
        return this.looping;
    }

    public AnimatedModelNode copy() {
        AnimatedModelNode copy = new AnimatedModelNode();
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
        copy.animation = this.animation;
        copy.time = this.time;
        copy.looping = this.looping;
        return copy;
    }
}
