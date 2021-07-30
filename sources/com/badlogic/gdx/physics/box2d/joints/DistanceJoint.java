package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class DistanceJoint extends Joint {
    private native float jniGetDampingRatio(long j);

    private native float jniGetFrequency(long j);

    private native float jniGetLength(long j);

    private native void jniSetDampingRatio(long j, float f);

    private native void jniSetFrequency(long j, float f);

    private native void jniSetLength(long j, float f);

    public DistanceJoint(World world, long addr) {
        super(world, addr);
    }

    public void setLength(float length) {
        jniSetLength(this.addr, length);
    }

    public float getLength() {
        return jniGetLength(this.addr);
    }

    public void setFrequency(float hz) {
        jniSetFrequency(this.addr, hz);
    }

    public float getFrequency() {
        return jniGetFrequency(this.addr);
    }

    public void setDampingRatio(float ratio) {
        jniSetDampingRatio(this.addr, ratio);
    }

    public float getDampingRatio() {
        return jniGetDampingRatio(this.addr);
    }
}
