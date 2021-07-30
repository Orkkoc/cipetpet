package com.badlogic.gdx.math.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;
import java.util.List;

public class BoundingBox implements Serializable {
    private static final long serialVersionUID = -1286036817192127343L;
    final Vector3 cnt;
    final Vector3[] crn;
    boolean crn_dirty;
    final Vector3 dim;
    public final Vector3 max;
    public final Vector3 min;

    public Vector3 getCenter() {
        return this.cnt;
    }

    /* access modifiers changed from: protected */
    public void updateCorners() {
        if (this.crn_dirty) {
            this.crn[0].set(this.min.f170x, this.min.f171y, this.min.f172z);
            this.crn[1].set(this.max.f170x, this.min.f171y, this.min.f172z);
            this.crn[2].set(this.max.f170x, this.max.f171y, this.min.f172z);
            this.crn[3].set(this.min.f170x, this.max.f171y, this.min.f172z);
            this.crn[4].set(this.min.f170x, this.min.f171y, this.max.f172z);
            this.crn[5].set(this.max.f170x, this.min.f171y, this.max.f172z);
            this.crn[6].set(this.max.f170x, this.max.f171y, this.max.f172z);
            this.crn[7].set(this.min.f170x, this.max.f171y, this.max.f172z);
            this.crn_dirty = false;
        }
    }

    public Vector3[] getCorners() {
        updateCorners();
        return this.crn;
    }

    public Vector3 getDimensions() {
        return this.dim;
    }

    public Vector3 getMin() {
        return this.min;
    }

    public synchronized Vector3 getMax() {
        return this.max;
    }

    public BoundingBox() {
        this.crn = new Vector3[8];
        this.min = new Vector3();
        this.max = new Vector3();
        this.cnt = new Vector3();
        this.dim = new Vector3();
        this.crn_dirty = true;
        this.crn_dirty = true;
        for (int l_idx = 0; l_idx < 8; l_idx++) {
            this.crn[l_idx] = new Vector3();
        }
        clr();
    }

    public BoundingBox(BoundingBox bounds) {
        this.crn = new Vector3[8];
        this.min = new Vector3();
        this.max = new Vector3();
        this.cnt = new Vector3();
        this.dim = new Vector3();
        this.crn_dirty = true;
        this.crn_dirty = true;
        for (int l_idx = 0; l_idx < 8; l_idx++) {
            this.crn[l_idx] = new Vector3();
        }
        set(bounds);
    }

    public BoundingBox(Vector3 minimum, Vector3 maximum) {
        this.crn = new Vector3[8];
        this.min = new Vector3();
        this.max = new Vector3();
        this.cnt = new Vector3();
        this.dim = new Vector3();
        this.crn_dirty = true;
        this.crn_dirty = true;
        for (int l_idx = 0; l_idx < 8; l_idx++) {
            this.crn[l_idx] = new Vector3();
        }
        set(minimum, maximum);
    }

    public BoundingBox set(BoundingBox bounds) {
        this.crn_dirty = true;
        return set(bounds.min, bounds.max);
    }

    public BoundingBox set(Vector3 minimum, Vector3 maximum) {
        this.min.set(minimum.f170x < maximum.f170x ? minimum.f170x : maximum.f170x, minimum.f171y < maximum.f171y ? minimum.f171y : maximum.f171y, minimum.f172z < maximum.f172z ? minimum.f172z : maximum.f172z);
        this.max.set(minimum.f170x > maximum.f170x ? minimum.f170x : maximum.f170x, minimum.f171y > maximum.f171y ? minimum.f171y : maximum.f171y, minimum.f172z > maximum.f172z ? minimum.f172z : maximum.f172z);
        this.cnt.set(this.min).add(this.max).mul(0.5f);
        this.dim.set(this.max).sub(this.min);
        this.crn_dirty = true;
        return this;
    }

    public BoundingBox set(Vector3[] points) {
        inf();
        for (Vector3 l_point : points) {
            ext(l_point);
        }
        this.crn_dirty = true;
        return this;
    }

    public BoundingBox set(List<Vector3> points) {
        inf();
        for (Vector3 l_point : points) {
            ext(l_point);
        }
        this.crn_dirty = true;
        return this;
    }

    public BoundingBox inf() {
        this.min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        this.max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        this.cnt.set(0.0f, 0.0f, 0.0f);
        this.dim.set(0.0f, 0.0f, 0.0f);
        this.crn_dirty = true;
        return this;
    }

    public BoundingBox ext(Vector3 point) {
        this.crn_dirty = true;
        return set(this.min.set(min(this.min.f170x, point.f170x), min(this.min.f171y, point.f171y), min(this.min.f172z, point.f172z)), this.max.set(Math.max(this.max.f170x, point.f170x), Math.max(this.max.f171y, point.f171y), Math.max(this.max.f172z, point.f172z)));
    }

    public BoundingBox clr() {
        this.crn_dirty = true;
        return set(this.min.set(0.0f, 0.0f, 0.0f), this.max.set(0.0f, 0.0f, 0.0f));
    }

    public boolean isValid() {
        return this.min.f170x < this.max.f170x && this.min.f171y < this.max.f171y && this.min.f172z < this.max.f172z;
    }

    public BoundingBox ext(BoundingBox a_bounds) {
        this.crn_dirty = true;
        return set(this.min.set(min(this.min.f170x, a_bounds.min.f170x), min(this.min.f171y, a_bounds.min.f171y), min(this.min.f172z, a_bounds.min.f172z)), this.max.set(max(this.max.f170x, a_bounds.max.f170x), max(this.max.f171y, a_bounds.max.f171y), max(this.max.f172z, a_bounds.max.f172z)));
    }

    public BoundingBox mul(Matrix4 matrix) {
        updateCorners();
        inf();
        for (Vector3 l_pnt : this.crn) {
            l_pnt.mul(matrix);
            this.min.set(min(this.min.f170x, l_pnt.f170x), min(this.min.f171y, l_pnt.f171y), min(this.min.f172z, l_pnt.f172z));
            this.max.set(max(this.max.f170x, l_pnt.f170x), max(this.max.f171y, l_pnt.f171y), max(this.max.f172z, l_pnt.f172z));
        }
        this.crn_dirty = true;
        return set(this.min, this.max);
    }

    public boolean contains(BoundingBox bounds) {
        if (!isValid()) {
            return true;
        }
        if (this.min.f170x > bounds.min.f170x) {
            return false;
        }
        if (this.min.f171y > bounds.min.f171y) {
            return false;
        }
        if (this.min.f172z > bounds.min.f172z) {
            return false;
        }
        if (this.max.f170x < bounds.max.f170x) {
            return false;
        }
        if (this.max.f171y < bounds.max.f171y) {
            return false;
        }
        if (this.max.f172z < bounds.max.f172z) {
            return false;
        }
        return true;
    }

    public boolean contains(Vector3 v) {
        if (this.min.f170x <= v.f170x && this.max.f170x >= v.f170x && this.min.f171y <= v.f171y && this.max.f171y >= v.f171y && this.min.f172z <= v.f172z && this.max.f172z >= v.f172z) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "[" + this.min + "|" + this.max + "]";
    }

    public BoundingBox ext(float x, float y, float z) {
        this.crn_dirty = true;
        return set(this.min.set(min(this.min.f170x, x), min(this.min.f171y, y), min(this.min.f172z, z)), this.max.set(max(this.max.f170x, x), max(this.max.f171y, y), max(this.max.f172z, z)));
    }

    static float min(float a, float b) {
        return a > b ? b : a;
    }

    static float max(float a, float b) {
        return a > b ? a : b;
    }
}
