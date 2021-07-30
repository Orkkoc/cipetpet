package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Segment implements Serializable {
    private static final long serialVersionUID = 2739667069736519602L;

    /* renamed from: a */
    public final Vector3 f173a = new Vector3();

    /* renamed from: b */
    public final Vector3 f174b = new Vector3();

    public Segment(Vector3 a, Vector3 b) {
        this.f173a.set(a);
        this.f174b.set(b);
    }

    public Segment(float aX, float aY, float aZ, float bX, float bY, float bZ) {
        this.f173a.set(aX, aY, aZ);
        this.f174b.set(bX, bY, bZ);
    }
}
