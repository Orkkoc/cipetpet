package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class GearJoint extends Joint {
    private native float jniGetRatio(long j);

    private native void jniSetRatio(long j, float f);

    public GearJoint(World world, long addr) {
        super(world, addr);
    }

    public void setRatio(float ratio) {
        jniSetRatio(this.addr, ratio);
    }

    public float getRatio() {
        return jniGetRatio(this.addr);
    }
}
