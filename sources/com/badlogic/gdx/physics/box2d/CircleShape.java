package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

public class CircleShape extends Shape {
    private final Vector2 position = new Vector2();
    private final float[] tmp = new float[2];

    private native void jniGetPosition(long j, float[] fArr);

    private native void jniSetPosition(long j, float f, float f2);

    private native long newCircleShape();

    public CircleShape() {
        this.addr = newCircleShape();
    }

    protected CircleShape(long addr) {
        this.addr = addr;
    }

    public Shape.Type getType() {
        return Shape.Type.Circle;
    }

    public Vector2 getPosition() {
        jniGetPosition(this.addr, this.tmp);
        this.position.f165x = this.tmp[0];
        this.position.f166y = this.tmp[1];
        return this.position;
    }

    public void setPosition(Vector2 position2) {
        jniSetPosition(this.addr, position2.f165x, position2.f166y);
    }
}
