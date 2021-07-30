package com.badlogic.gdx.math;

import java.io.Serializable;

public class Quaternion implements Serializable {
    private static final float NORMALIZATION_TOLERANCE = 1.0E-5f;
    private static final long serialVersionUID = -7661875440774897168L;
    private static Quaternion tmp1 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
    private static Quaternion tmp2 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);

    /* renamed from: w */
    public float f157w;

    /* renamed from: x */
    public float f158x;

    /* renamed from: y */
    public float f159y;

    /* renamed from: z */
    public float f160z;

    public Quaternion(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    public Quaternion() {
        idt();
    }

    public Quaternion(Quaternion quaternion) {
        set(quaternion);
    }

    public Quaternion(Vector3 axis, float angle) {
        set(axis, angle);
    }

    public Quaternion set(float x, float y, float z, float w) {
        this.f158x = x;
        this.f159y = y;
        this.f160z = z;
        this.f157w = w;
        return this;
    }

    public Quaternion set(Quaternion quaternion) {
        return set(quaternion.f158x, quaternion.f159y, quaternion.f160z, quaternion.f157w);
    }

    public Quaternion set(Vector3 axis, float angle) {
        float l_ang = (float) Math.toRadians((double) angle);
        float l_sin = (float) Math.sin((double) (l_ang / 2.0f));
        return set(axis.f170x * l_sin, axis.f171y * l_sin, axis.f172z * l_sin, (float) Math.cos((double) (l_ang / 2.0f))).nor();
    }

    public Quaternion cpy() {
        return new Quaternion(this);
    }

    public float len() {
        return (float) Math.sqrt((double) ((this.f158x * this.f158x) + (this.f159y * this.f159y) + (this.f160z * this.f160z) + (this.f157w * this.f157w)));
    }

    public String toString() {
        return "[" + this.f158x + "|" + this.f159y + "|" + this.f160z + "|" + this.f157w + "]";
    }

    public Quaternion setEulerAngles(float yaw, float pitch, float roll) {
        float yaw2 = (float) Math.toRadians((double) yaw);
        float pitch2 = (float) Math.toRadians((double) pitch);
        float num9 = ((float) Math.toRadians((double) roll)) * 0.5f;
        float num6 = (float) Math.sin((double) num9);
        float num5 = (float) Math.cos((double) num9);
        float num8 = pitch2 * 0.5f;
        float num4 = (float) Math.sin((double) num8);
        float num3 = (float) Math.cos((double) num8);
        float num7 = yaw2 * 0.5f;
        float num2 = (float) Math.sin((double) num7);
        float num = (float) Math.cos((double) num7);
        float f1 = num * num4;
        float f2 = num2 * num3;
        float f3 = num * num3;
        float f4 = num2 * num4;
        this.f158x = (f1 * num5) + (f2 * num6);
        this.f159y = (f2 * num5) - (f1 * num6);
        this.f160z = (f3 * num6) - (f4 * num5);
        this.f157w = (f3 * num5) + (f4 * num6);
        return this;
    }

    public float len2() {
        return (this.f158x * this.f158x) + (this.f159y * this.f159y) + (this.f160z * this.f160z) + (this.f157w * this.f157w);
    }

    public Quaternion nor() {
        float len = len2();
        if (len != 0.0f && Math.abs(len - 1.0f) > NORMALIZATION_TOLERANCE) {
            float len2 = (float) Math.sqrt((double) len);
            this.f157w /= len2;
            this.f158x /= len2;
            this.f159y /= len2;
            this.f160z /= len2;
        }
        return this;
    }

    public Quaternion conjugate() {
        this.f158x = -this.f158x;
        this.f159y = -this.f159y;
        this.f160z = -this.f160z;
        return this;
    }

    public void transform(Vector3 v) {
        tmp2.set(this);
        tmp2.conjugate();
        tmp2.mulLeft(tmp1.set(v.f170x, v.f171y, v.f172z, 0.0f)).mulLeft(this);
        v.f170x = tmp2.f158x;
        v.f171y = tmp2.f159y;
        v.f172z = tmp2.f160z;
    }

    public Quaternion mul(Quaternion q) {
        float newX = (((this.f157w * q.f158x) + (this.f158x * q.f157w)) + (this.f159y * q.f160z)) - (this.f160z * q.f159y);
        float newY = (((this.f157w * q.f159y) + (this.f159y * q.f157w)) + (this.f160z * q.f158x)) - (this.f158x * q.f160z);
        float newZ = (((this.f157w * q.f160z) + (this.f160z * q.f157w)) + (this.f158x * q.f159y)) - (this.f159y * q.f158x);
        this.f158x = newX;
        this.f159y = newY;
        this.f160z = newZ;
        this.f157w = (((this.f157w * q.f157w) - (this.f158x * q.f158x)) - (this.f159y * q.f159y)) - (this.f160z * q.f160z);
        return this;
    }

    public Quaternion mulLeft(Quaternion q) {
        float newX = (((q.f157w * this.f158x) + (q.f158x * this.f157w)) + (q.f159y * this.f160z)) - (q.f160z * this.f159y);
        float newY = (((q.f157w * this.f159y) + (q.f159y * this.f157w)) + (q.f160z * this.f158x)) - (q.f158x * this.f160z);
        float newZ = (((q.f157w * this.f160z) + (q.f160z * this.f157w)) + (q.f158x * this.f159y)) - (q.f159y * this.f158x);
        this.f158x = newX;
        this.f159y = newY;
        this.f160z = newZ;
        this.f157w = (((q.f157w * this.f157w) - (q.f158x * this.f158x)) - (q.f159y * this.f159y)) - (q.f160z * this.f160z);
        return this;
    }

    public void toMatrix(float[] matrix) {
        float xx = this.f158x * this.f158x;
        float xy = this.f158x * this.f159y;
        float xz = this.f158x * this.f160z;
        float xw = this.f158x * this.f157w;
        float yy = this.f159y * this.f159y;
        float yz = this.f159y * this.f160z;
        float yw = this.f159y * this.f157w;
        float zz = this.f160z * this.f160z;
        float zw = this.f160z * this.f157w;
        matrix[0] = 1.0f - ((yy + zz) * 2.0f);
        matrix[4] = (xy - zw) * 2.0f;
        matrix[8] = (xz + yw) * 2.0f;
        matrix[12] = 0.0f;
        matrix[1] = (xy + zw) * 2.0f;
        matrix[5] = 1.0f - ((xx + zz) * 2.0f);
        matrix[9] = (yz - xw) * 2.0f;
        matrix[13] = 0.0f;
        matrix[2] = (xz - yw) * 2.0f;
        matrix[6] = (yz + xw) * 2.0f;
        matrix[10] = 1.0f - ((xx + yy) * 2.0f);
        matrix[14] = 0.0f;
        matrix[3] = 0.0f;
        matrix[7] = 0.0f;
        matrix[11] = 0.0f;
        matrix[15] = 1.0f;
    }

    public Quaternion idt() {
        set(0.0f, 0.0f, 0.0f, 1.0f);
        return this;
    }

    public Quaternion setFromAxis(Vector3 axis, float angle) {
        return setFromAxis(axis.f170x, axis.f171y, axis.f172z, angle);
    }

    public Quaternion setFromAxis(float x, float y, float z, float angle) {
        float l_ang = angle * 0.017453292f;
        float l_sin = MathUtils.sin(l_ang / 2.0f);
        return set(x * l_sin, y * l_sin, z * l_sin, MathUtils.cos(l_ang / 2.0f)).nor();
    }

    public Quaternion setFromMatrix(Matrix4 matrix) {
        return setFromAxes(matrix.val[0], matrix.val[4], matrix.val[8], matrix.val[1], matrix.val[5], matrix.val[9], matrix.val[2], matrix.val[6], matrix.val[10]);
    }

    public Quaternion setFromAxes(float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
        double z;
        double x;
        double y;
        double w;
        float m00 = xx;
        float m01 = xy;
        float m02 = xz;
        float m10 = yx;
        float m11 = yy;
        float m12 = yz;
        float m20 = zx;
        float m21 = zy;
        float m22 = zz;
        float t = m00 + m11 + m22;
        if (t >= 0.0f) {
            double s = Math.sqrt((double) (1.0f + t));
            w = 0.5d * s;
            double s2 = 0.5d / s;
            x = ((double) (m21 - m12)) * s2;
            y = ((double) (m02 - m20)) * s2;
            z = ((double) (m10 - m01)) * s2;
        } else if (m00 > m11 && m00 > m22) {
            double s3 = Math.sqrt(((1.0d + ((double) m00)) - ((double) m11)) - ((double) m22));
            x = s3 * 0.5d;
            double s4 = 0.5d / s3;
            y = ((double) (m10 + m01)) * s4;
            z = ((double) (m02 + m20)) * s4;
            w = ((double) (m21 - m12)) * s4;
        } else if (m11 > m22) {
            double s5 = Math.sqrt(((1.0d + ((double) m11)) - ((double) m00)) - ((double) m22));
            y = s5 * 0.5d;
            double s6 = 0.5d / s5;
            x = ((double) (m10 + m01)) * s6;
            z = ((double) (m21 + m12)) * s6;
            w = ((double) (m02 - m20)) * s6;
        } else {
            double s7 = Math.sqrt(((1.0d + ((double) m22)) - ((double) m00)) - ((double) m11));
            z = s7 * 0.5d;
            double s8 = 0.5d / s7;
            x = ((double) (m02 + m20)) * s8;
            y = ((double) (m21 + m12)) * s8;
            w = ((double) (m10 - m01)) * s8;
        }
        return set((float) x, (float) y, (float) z, (float) w);
    }

    public Quaternion setFromCross(Vector3 v1, Vector3 v2) {
        return setFromAxis(Vector3.tmp.crs(Vector3.tmp2), (float) Math.acos((double) MathUtils.clamp(Vector3.tmp.set(v1).nor().dot(Vector3.tmp2.set(v2).nor()), -1.0f, 1.0f)));
    }

    public Quaternion setFromCross(float x1, float y1, float z1, float x2, float y2, float z2) {
        return setFromAxis(Vector3.tmp.crs(Vector3.tmp2), (float) Math.acos((double) MathUtils.clamp(Vector3.tmp.set(x1, y1, z1).nor().dot(Vector3.tmp2.set(x2, y2, z2).nor()), -1.0f, 1.0f)));
    }

    public Quaternion slerp(Quaternion end, float alpha) {
        if (!equals(end)) {
            float result = dot(end);
            if (((double) result) < 0.0d) {
                end.mul(-1.0f);
                result = -result;
            }
            float scale0 = 1.0f - alpha;
            float scale1 = alpha;
            if (((double) (1.0f - result)) > 0.1d) {
                double theta = Math.acos((double) result);
                double invSinTheta = 1.0d / Math.sin(theta);
                scale0 = (float) (Math.sin(((double) (1.0f - alpha)) * theta) * invSinTheta);
                scale1 = (float) (Math.sin(((double) alpha) * theta) * invSinTheta);
            }
            set((this.f158x * scale0) + (end.f158x * scale1), (this.f159y * scale0) + (end.f159y * scale1), (this.f160z * scale0) + (end.f160z * scale1), (this.f157w * scale0) + (end.f157w * scale1));
        }
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quaternion)) {
            return false;
        }
        Quaternion comp = (Quaternion) o;
        if (this.f158x == comp.f158x && this.f159y == comp.f159y && this.f160z == comp.f160z && this.f157w == comp.f157w) {
            return true;
        }
        return false;
    }

    public float dot(Quaternion other) {
        return (this.f158x * other.f158x) + (this.f159y * other.f159y) + (this.f160z * other.f160z) + (this.f157w * other.f157w);
    }

    public Quaternion mul(float scalar) {
        this.f158x *= scalar;
        this.f159y *= scalar;
        this.f160z *= scalar;
        this.f157w *= scalar;
        return this;
    }
}
