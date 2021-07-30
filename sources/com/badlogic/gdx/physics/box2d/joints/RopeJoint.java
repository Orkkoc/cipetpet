package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class RopeJoint extends Joint {
    private native float jniGetMaxLength(long j);

    private native float jniSetMaxLength(long j, float f);

    public RopeJoint(World world, long addr) {
        super(world, addr);
    }

    public float getMaxLength() {
        return jniGetMaxLength(this.addr);
    }

    public void setMaxLength(float length) {
        jniSetMaxLength(this.addr, length);
    }
}
