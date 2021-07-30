package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class Transform {
    public static final int COS = 2;
    public static final int POS_X = 0;
    public static final int POS_Y = 1;
    public static final int SIN = 3;
    private Vector2 position = new Vector2();
    public float[] vals = new float[4];

    public Transform() {
    }

    public Transform(Vector2 position2, float angle) {
        setPosition(position2);
        setRotation(angle);
    }

    public Vector2 mul(Vector2 v) {
        v.f165x = this.vals[0] + (this.vals[2] * v.f165x) + ((-this.vals[3]) * v.f166y);
        v.f166y = this.vals[1] + (this.vals[3] * v.f165x) + (this.vals[2] * v.f166y);
        return v;
    }

    public Vector2 getPosition() {
        return this.position.set(this.vals[0], this.vals[1]);
    }

    public void setRotation(float angle) {
        float c = (float) Math.cos((double) angle);
        float s = (float) Math.sin((double) angle);
        this.vals[2] = c;
        this.vals[3] = s;
    }

    public float getRotation() {
        return (float) Math.atan2((double) this.vals[3], (double) this.vals[2]);
    }

    public void setPosition(Vector2 pos) {
        this.vals[0] = pos.f165x;
        this.vals[1] = pos.f166y;
    }
}
