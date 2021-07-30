package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

public class FrictionJoint extends Joint {
    private native float jniGetMaxForce(long j);

    private native float jniGetMaxTorque(long j);

    private native void jniSetMaxForce(long j, float f);

    private native void jniSetMaxTorque(long j, float f);

    public FrictionJoint(World world, long addr) {
        super(world, addr);
    }

    public void setMaxForce(float force) {
        jniSetMaxForce(this.addr, force);
    }

    public float getMaxForce() {
        return jniGetMaxForce(this.addr);
    }

    public void setMaxTorque(float torque) {
        jniSetMaxTorque(this.addr, torque);
    }

    public float getMaxTorque() {
        return jniGetMaxTorque(this.addr);
    }
}
