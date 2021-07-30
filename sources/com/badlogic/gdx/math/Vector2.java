package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Vector2 implements Serializable {

    /* renamed from: X */
    public static final Vector2 f163X = new Vector2(1.0f, 0.0f);

    /* renamed from: Y */
    public static final Vector2 f164Y = new Vector2(0.0f, 1.0f);
    public static final Vector2 Zero = new Vector2(0.0f, 0.0f);
    private static final long serialVersionUID = 913902788239530931L;
    public static final Vector2 tmp = new Vector2();
    public static final Vector2 tmp2 = new Vector2();
    public static final Vector2 tmp3 = new Vector2();

    /* renamed from: x */
    public float f165x;

    /* renamed from: y */
    public float f166y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.f165x = x;
        this.f166y = y;
    }

    public Vector2(Vector2 v) {
        set(v);
    }

    public Vector2 cpy() {
        return new Vector2(this);
    }

    public float len() {
        return (float) Math.sqrt((double) ((this.f165x * this.f165x) + (this.f166y * this.f166y)));
    }

    public float len2() {
        return (this.f165x * this.f165x) + (this.f166y * this.f166y);
    }

    public Vector2 set(Vector2 v) {
        this.f165x = v.f165x;
        this.f166y = v.f166y;
        return this;
    }

    public Vector2 set(float x, float y) {
        this.f165x = x;
        this.f166y = y;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        this.f165x -= v.f165x;
        this.f166y -= v.f166y;
        return this;
    }

    public Vector2 nor() {
        float len = len();
        if (len != 0.0f) {
            this.f165x /= len;
            this.f166y /= len;
        }
        return this;
    }

    public Vector2 add(Vector2 v) {
        this.f165x += v.f165x;
        this.f166y += v.f166y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.f165x += x;
        this.f166y += y;
        return this;
    }

    public float dot(Vector2 v) {
        return (this.f165x * v.f165x) + (this.f166y * v.f166y);
    }

    public Vector2 mul(float scalar) {
        this.f165x *= scalar;
        this.f166y *= scalar;
        return this;
    }

    public Vector2 mul(float x, float y) {
        this.f165x *= x;
        this.f166y *= y;
        return this;
    }

    public Vector2 div(float value) {
        return mul(1.0f / value);
    }

    public Vector2 div(float vx, float vy) {
        return mul(1.0f / vx, 1.0f / vy);
    }

    public Vector2 div(Vector2 other) {
        return mul(1.0f / other.f165x, 1.0f / other.f166y);
    }

    public float dst(Vector2 v) {
        float x_d = v.f165x - this.f165x;
        float y_d = v.f166y - this.f166y;
        return (float) Math.sqrt((double) ((x_d * x_d) + (y_d * y_d)));
    }

    public float dst(float x, float y) {
        float x_d = x - this.f165x;
        float y_d = y - this.f166y;
        return (float) Math.sqrt((double) ((x_d * x_d) + (y_d * y_d)));
    }

    public float dst2(Vector2 v) {
        float x_d = v.f165x - this.f165x;
        float y_d = v.f166y - this.f166y;
        return (x_d * x_d) + (y_d * y_d);
    }

    public float dst2(float x, float y) {
        float x_d = x - this.f165x;
        float y_d = y - this.f166y;
        return (x_d * x_d) + (y_d * y_d);
    }

    public String toString() {
        return "[" + this.f165x + ":" + this.f166y + "]";
    }

    public Vector2 sub(float x, float y) {
        this.f165x -= x;
        this.f166y -= y;
        return this;
    }

    public Vector2 tmp() {
        return tmp.set(this);
    }

    public Vector2 mul(Matrix3 mat) {
        this.f165x = (this.f165x * mat.val[0]) + (this.f166y * mat.val[3]) + mat.val[6];
        this.f166y = (this.f165x * mat.val[1]) + (this.f166y * mat.val[4]) + mat.val[7];
        return this;
    }

    public float crs(Vector2 v) {
        return (this.f165x * v.f166y) - (this.f166y * v.f165x);
    }

    public float crs(float x, float y) {
        return (this.f165x * y) - (this.f166y * x);
    }

    public float angle() {
        float angle = ((float) Math.atan2((double) this.f166y, (double) this.f165x)) * 57.295776f;
        if (angle < 0.0f) {
            return angle + 360.0f;
        }
        return angle;
    }

    public void setAngle(float angle) {
        set(len(), 0.0f);
        rotate(angle);
    }

    public Vector2 rotate(float degrees) {
        float rad = degrees * 0.017453292f;
        float cos = (float) Math.cos((double) rad);
        float sin = (float) Math.sin((double) rad);
        this.f165x = (this.f165x * cos) - (this.f166y * sin);
        this.f166y = (this.f165x * sin) + (this.f166y * cos);
        return this;
    }

    public Vector2 lerp(Vector2 target, float alpha) {
        Vector2 r = mul(1.0f - alpha);
        r.add(target.tmp().mul(alpha));
        return r;
    }

    public int hashCode() {
        return ((NumberUtils.floatToIntBits(this.f165x) + 31) * 31) + NumberUtils.floatToIntBits(this.f166y);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vector2 other = (Vector2) obj;
        if (NumberUtils.floatToIntBits(this.f165x) != NumberUtils.floatToIntBits(other.f165x)) {
            return false;
        }
        if (NumberUtils.floatToIntBits(this.f166y) != NumberUtils.floatToIntBits(other.f166y)) {
            return false;
        }
        return true;
    }

    public boolean epsilonEquals(Vector2 obj, float epsilon) {
        if (obj != null && Math.abs(obj.f165x - this.f165x) <= epsilon && Math.abs(obj.f166y - this.f166y) <= epsilon) {
            return true;
        }
        return false;
    }
}
