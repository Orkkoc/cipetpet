package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.io.Serializable;

public class Vector3 implements Serializable {

    /* renamed from: X */
    public static final Vector3 f167X = new Vector3(1.0f, 0.0f, 0.0f);

    /* renamed from: Y */
    public static final Vector3 f168Y = new Vector3(0.0f, 1.0f, 0.0f);

    /* renamed from: Z */
    public static final Vector3 f169Z = new Vector3(0.0f, 0.0f, 1.0f);
    public static final Vector3 Zero = new Vector3(0.0f, 0.0f, 0.0f);
    private static final long serialVersionUID = 3840054589595372522L;
    public static final Vector3 tmp = new Vector3();
    public static final Vector3 tmp2 = new Vector3();
    public static final Vector3 tmp3 = new Vector3();
    private static final Matrix4 tmpMat = new Matrix4();

    /* renamed from: x */
    public float f170x;

    /* renamed from: y */
    public float f171y;

    /* renamed from: z */
    public float f172z;

    public Vector3() {
    }

    public Vector3(float x, float y, float z) {
        set(x, y, z);
    }

    public Vector3(Vector3 vector) {
        set(vector);
    }

    public Vector3(float[] values) {
        set(values[0], values[1], values[2]);
    }

    public Vector3 set(float x, float y, float z) {
        this.f170x = x;
        this.f171y = y;
        this.f172z = z;
        return this;
    }

    public Vector3 set(Vector3 vector) {
        return set(vector.f170x, vector.f171y, vector.f172z);
    }

    public Vector3 set(float[] values) {
        return set(values[0], values[1], values[2]);
    }

    public Vector3 cpy() {
        return new Vector3(this);
    }

    public Vector3 tmp() {
        return tmp.set(this);
    }

    public Vector3 tmp2() {
        return tmp2.set(this);
    }

    /* access modifiers changed from: package-private */
    public Vector3 tmp3() {
        return tmp3.set(this);
    }

    public Vector3 add(Vector3 vector) {
        return add(vector.f170x, vector.f171y, vector.f172z);
    }

    public Vector3 add(float x, float y, float z) {
        return set(this.f170x + x, this.f171y + y, this.f172z + z);
    }

    public Vector3 add(float values) {
        return set(this.f170x + values, this.f171y + values, this.f172z + values);
    }

    public Vector3 sub(Vector3 a_vec) {
        return sub(a_vec.f170x, a_vec.f171y, a_vec.f172z);
    }

    public Vector3 sub(float x, float y, float z) {
        return set(this.f170x - x, this.f171y - y, this.f172z - z);
    }

    public Vector3 sub(float value) {
        return set(this.f170x - value, this.f171y - value, this.f172z - value);
    }

    public Vector3 mul(float value) {
        return set(this.f170x * value, this.f171y * value, this.f172z * value);
    }

    public Vector3 mul(Vector3 other) {
        return mul(other.f170x, other.f171y, other.f172z);
    }

    public Vector3 mul(float vx, float vy, float vz) {
        return set(this.f170x * vx, this.f171y * vy, this.f172z * vz);
    }

    public Vector3 div(float value) {
        return mul(1.0f / value);
    }

    public Vector3 div(float vx, float vy, float vz) {
        return mul(1.0f / vx, 1.0f / vy, 1.0f / vz);
    }

    public Vector3 div(Vector3 other) {
        return mul(1.0f / other.f170x, 1.0f / other.f171y, 1.0f / other.f172z);
    }

    public float len() {
        return (float) Math.sqrt((double) ((this.f170x * this.f170x) + (this.f171y * this.f171y) + (this.f172z * this.f172z)));
    }

    public float len2() {
        return (this.f170x * this.f170x) + (this.f171y * this.f171y) + (this.f172z * this.f172z);
    }

    public boolean idt(Vector3 vector) {
        return this.f170x == vector.f170x && this.f171y == vector.f171y && this.f172z == vector.f172z;
    }

    public float dst(Vector3 vector) {
        float a = vector.f170x - this.f170x;
        float b = vector.f171y - this.f171y;
        float c = vector.f172z - this.f172z;
        return (float) Math.sqrt((double) ((a * a) + (b * b) + (c * c)));
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public Vector3 nor() {
        float len = len();
        return len == 0.0f ? this : div(len);
    }

    public float dot(Vector3 vector) {
        return (this.f170x * vector.f170x) + (this.f171y * vector.f171y) + (this.f172z * vector.f172z);
    }

    public Vector3 crs(Vector3 vector) {
        return set((this.f171y * vector.f172z) - (this.f172z * vector.f171y), (this.f172z * vector.f170x) - (this.f170x * vector.f172z), (this.f170x * vector.f171y) - (this.f171y * vector.f170x));
    }

    public Vector3 crs(float x, float y, float z) {
        return set((this.f171y * z) - (this.f172z * y), (this.f172z * x) - (this.f170x * z), (this.f170x * y) - (this.f171y * x));
    }

    public Vector3 mul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return set((this.f170x * l_mat[0]) + (this.f171y * l_mat[4]) + (this.f172z * l_mat[8]) + l_mat[12], (this.f170x * l_mat[1]) + (this.f171y * l_mat[5]) + (this.f172z * l_mat[9]) + l_mat[13], (this.f170x * l_mat[2]) + (this.f171y * l_mat[6]) + (this.f172z * l_mat[10]) + l_mat[14]);
    }

    public Vector3 prj(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        float l_w = (this.f170x * l_mat[3]) + (this.f171y * l_mat[7]) + (this.f172z * l_mat[11]) + l_mat[15];
        return set(((((this.f170x * l_mat[0]) + (this.f171y * l_mat[4])) + (this.f172z * l_mat[8])) + l_mat[12]) / l_w, ((((this.f170x * l_mat[1]) + (this.f171y * l_mat[5])) + (this.f172z * l_mat[9])) + l_mat[13]) / l_w, ((((this.f170x * l_mat[2]) + (this.f171y * l_mat[6])) + (this.f172z * l_mat[10])) + l_mat[14]) / l_w);
    }

    public Vector3 rot(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return set((this.f170x * l_mat[0]) + (this.f171y * l_mat[4]) + (this.f172z * l_mat[8]), (this.f170x * l_mat[1]) + (this.f171y * l_mat[5]) + (this.f172z * l_mat[9]), (this.f170x * l_mat[2]) + (this.f171y * l_mat[6]) + (this.f172z * l_mat[10]));
    }

    public Vector3 rotate(float angle, float axisX, float axisY, float axisZ) {
        return rotate(tmp.set(axisX, axisY, axisZ), angle);
    }

    public Vector3 rotate(Vector3 axis, float angle) {
        tmpMat.setToRotation(axis, angle);
        return mul(tmpMat);
    }

    public boolean isUnit() {
        return len() == 1.0f;
    }

    public boolean isZero() {
        return this.f170x == 0.0f && this.f171y == 0.0f && this.f172z == 0.0f;
    }

    public Vector3 lerp(Vector3 target, float alpha) {
        Vector3 r = mul(1.0f - alpha);
        r.add(target.tmp().mul(alpha));
        return r;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public Vector3 slerp(Vector3 target, float alpha) {
        float dot = dot(target);
        if (((double) dot) > 0.99995d || ((double) dot) < 0.9995d) {
            add(target.tmp().sub(this).mul(alpha));
            nor();
            return this;
        }
        if (dot > 1.0f) {
            dot = 1.0f;
        }
        if (dot < -1.0f) {
            dot = -1.0f;
        }
        float theta = ((float) Math.acos((double) dot)) * alpha;
        Vector3 v2 = target.tmp().sub(this.f170x * dot, this.f171y * dot, this.f172z * dot);
        v2.nor();
        return mul((float) Math.cos((double) theta)).add(v2.mul((float) Math.sin((double) theta))).nor();
    }

    public String toString() {
        return this.f170x + "," + this.f171y + "," + this.f172z;
    }

    public float dot(float x, float y, float z) {
        return (this.f170x * x) + (this.f171y * y) + (this.f172z * z);
    }

    public float dst2(Vector3 point) {
        float a = point.f170x - this.f170x;
        float b = point.f171y - this.f171y;
        float c = point.f172z - this.f172z;
        return (a * a) + (b * b) + (c * c);
    }

    public float dst2(float x, float y, float z) {
        float a = x - this.f170x;
        float b = y - this.f171y;
        float c = z - this.f172z;
        return (a * a) + (b * b) + (c * c);
    }

    public float dst(float x, float y, float z) {
        return (float) Math.sqrt((double) dst2(x, y, z));
    }

    public int hashCode() {
        return ((((NumberUtils.floatToIntBits(this.f170x) + 31) * 31) + NumberUtils.floatToIntBits(this.f171y)) * 31) + NumberUtils.floatToIntBits(this.f172z);
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
        Vector3 other = (Vector3) obj;
        if (NumberUtils.floatToIntBits(this.f170x) != NumberUtils.floatToIntBits(other.f170x)) {
            return false;
        }
        if (NumberUtils.floatToIntBits(this.f171y) != NumberUtils.floatToIntBits(other.f171y)) {
            return false;
        }
        if (NumberUtils.floatToIntBits(this.f172z) != NumberUtils.floatToIntBits(other.f172z)) {
            return false;
        }
        return true;
    }

    public boolean epsilonEquals(Vector3 obj, float epsilon) {
        if (obj != null && Math.abs(obj.f170x - this.f170x) <= epsilon && Math.abs(obj.f171y - this.f171y) <= epsilon && Math.abs(obj.f172z - this.f172z) <= epsilon) {
            return true;
        }
        return false;
    }

    public boolean epsilonEquals(float x, float y, float z, float epsilon) {
        if (Math.abs(x - this.f170x) <= epsilon && Math.abs(y - this.f171y) <= epsilon && Math.abs(z - this.f172z) <= epsilon) {
            return true;
        }
        return false;
    }

    public Vector3 scale(float scalarX, float scalarY, float scalarZ) {
        this.f170x *= scalarX;
        this.f171y *= scalarY;
        this.f172z *= scalarZ;
        return this;
    }
}
