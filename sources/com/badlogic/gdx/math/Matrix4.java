package com.badlogic.gdx.math;

import java.io.Serializable;

public class Matrix4 implements Serializable {
    public static final int M00 = 0;
    public static final int M01 = 4;
    public static final int M02 = 8;
    public static final int M03 = 12;
    public static final int M10 = 1;
    public static final int M11 = 5;
    public static final int M12 = 9;
    public static final int M13 = 13;
    public static final int M20 = 2;
    public static final int M21 = 6;
    public static final int M22 = 10;
    public static final int M23 = 14;
    public static final int M30 = 3;
    public static final int M31 = 7;
    public static final int M32 = 11;
    public static final int M33 = 15;
    static Vector3 l_vex = new Vector3();
    static Vector3 l_vey = new Vector3();
    static Vector3 l_vez = new Vector3();
    static Quaternion quat = new Quaternion();
    static Vector3 right = new Vector3();
    private static final long serialVersionUID = -2717655254359579617L;
    static Vector3 tmpForward = new Vector3();
    static final Matrix4 tmpMat = new Matrix4();
    static Vector3 tmpUp = new Vector3();
    static final Vector3 tmpV = new Vector3();
    static final Vector3 tmpVec = new Vector3();
    public final float[] tmp = new float[16];
    public final float[] val = new float[16];

    public static native float det(float[] fArr);

    public static native boolean inv(float[] fArr);

    public static native void mul(float[] fArr, float[] fArr2);

    public static native void mulVec(float[] fArr, float[] fArr2);

    public static native void mulVec(float[] fArr, float[] fArr2, int i, int i2, int i3);

    public static native void prj(float[] fArr, float[] fArr2);

    public static native void prj(float[] fArr, float[] fArr2, int i, int i2, int i3);

    public static native void rot(float[] fArr, float[] fArr2);

    public static native void rot(float[] fArr, float[] fArr2, int i, int i2, int i3);

    public Matrix4() {
        this.val[0] = 1.0f;
        this.val[5] = 1.0f;
        this.val[10] = 1.0f;
        this.val[15] = 1.0f;
    }

    public Matrix4(Matrix4 matrix) {
        set(matrix);
    }

    public Matrix4(float[] values) {
        set(values);
    }

    public Matrix4(Quaternion quaternion) {
        set(quaternion);
    }

    public Matrix4 set(Matrix4 matrix) {
        return set(matrix.val);
    }

    public Matrix4 set(float[] values) {
        System.arraycopy(values, 0, this.val, 0, this.val.length);
        return this;
    }

    public Matrix4 set(Quaternion quaternion) {
        float l_xx = quaternion.f158x * quaternion.f158x;
        float l_xy = quaternion.f158x * quaternion.f159y;
        float l_xz = quaternion.f158x * quaternion.f160z;
        float l_xw = quaternion.f158x * quaternion.f157w;
        float l_yy = quaternion.f159y * quaternion.f159y;
        float l_yz = quaternion.f159y * quaternion.f160z;
        float l_yw = quaternion.f159y * quaternion.f157w;
        float l_zz = quaternion.f160z * quaternion.f160z;
        float l_zw = quaternion.f160z * quaternion.f157w;
        this.val[0] = 1.0f - (2.0f * (l_yy + l_zz));
        this.val[4] = 2.0f * (l_xy - l_zw);
        this.val[8] = 2.0f * (l_xz + l_yw);
        this.val[12] = 0.0f;
        this.val[1] = 2.0f * (l_xy + l_zw);
        this.val[5] = 1.0f - (2.0f * (l_xx + l_zz));
        this.val[9] = 2.0f * (l_yz - l_xw);
        this.val[13] = 0.0f;
        this.val[2] = 2.0f * (l_xz - l_yw);
        this.val[6] = 2.0f * (l_yz + l_xw);
        this.val[10] = 1.0f - (2.0f * (l_xx + l_yy));
        this.val[14] = 0.0f;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }

    public Matrix4 set(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
        this.val[0] = xAxis.f170x;
        this.val[4] = xAxis.f171y;
        this.val[8] = xAxis.f172z;
        this.val[1] = yAxis.f170x;
        this.val[5] = yAxis.f171y;
        this.val[9] = yAxis.f172z;
        this.val[2] = -zAxis.f170x;
        this.val[6] = -zAxis.f171y;
        this.val[10] = -zAxis.f172z;
        this.val[12] = pos.f170x;
        this.val[13] = pos.f171y;
        this.val[14] = pos.f172z;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }

    public Matrix4 cpy() {
        return new Matrix4(this);
    }

    public Matrix4 trn(Vector3 vector) {
        float[] fArr = this.val;
        fArr[12] = fArr[12] + vector.f170x;
        float[] fArr2 = this.val;
        fArr2[13] = fArr2[13] + vector.f171y;
        float[] fArr3 = this.val;
        fArr3[14] = fArr3[14] + vector.f172z;
        return this;
    }

    public Matrix4 trn(float x, float y, float z) {
        float[] fArr = this.val;
        fArr[12] = fArr[12] + x;
        float[] fArr2 = this.val;
        fArr2[13] = fArr2[13] + y;
        float[] fArr3 = this.val;
        fArr3[14] = fArr3[14] + z;
        return this;
    }

    public float[] getValues() {
        return this.val;
    }

    public Matrix4 mul(Matrix4 matrix) {
        mul(this.val, matrix.val);
        return this;
    }

    public Matrix4 tra() {
        this.tmp[0] = this.val[0];
        this.tmp[4] = this.val[1];
        this.tmp[8] = this.val[2];
        this.tmp[12] = this.val[3];
        this.tmp[1] = this.val[4];
        this.tmp[5] = this.val[5];
        this.tmp[9] = this.val[6];
        this.tmp[13] = this.val[7];
        this.tmp[2] = this.val[8];
        this.tmp[6] = this.val[9];
        this.tmp[10] = this.val[10];
        this.tmp[14] = this.val[11];
        this.tmp[3] = this.val[12];
        this.tmp[7] = this.val[13];
        this.tmp[11] = this.val[14];
        this.tmp[15] = this.val[15];
        return set(this.tmp);
    }

    public Matrix4 idt() {
        this.val[0] = 1.0f;
        this.val[4] = 0.0f;
        this.val[8] = 0.0f;
        this.val[12] = 0.0f;
        this.val[1] = 0.0f;
        this.val[5] = 1.0f;
        this.val[9] = 0.0f;
        this.val[13] = 0.0f;
        this.val[2] = 0.0f;
        this.val[6] = 0.0f;
        this.val[10] = 1.0f;
        this.val[14] = 0.0f;
        this.val[3] = 0.0f;
        this.val[7] = 0.0f;
        this.val[11] = 0.0f;
        this.val[15] = 1.0f;
        return this;
    }

    public Matrix4 inv() {
        float l_det = (((((((((((((((((((((((((this.val[3] * this.val[6]) * this.val[9]) * this.val[12]) - (((this.val[2] * this.val[7]) * this.val[9]) * this.val[12])) - (((this.val[3] * this.val[5]) * this.val[10]) * this.val[12])) + (((this.val[1] * this.val[7]) * this.val[10]) * this.val[12])) + (((this.val[2] * this.val[5]) * this.val[11]) * this.val[12])) - (((this.val[1] * this.val[6]) * this.val[11]) * this.val[12])) - (((this.val[3] * this.val[6]) * this.val[8]) * this.val[13])) + (((this.val[2] * this.val[7]) * this.val[8]) * this.val[13])) + (((this.val[3] * this.val[4]) * this.val[10]) * this.val[13])) - (((this.val[0] * this.val[7]) * this.val[10]) * this.val[13])) - (((this.val[2] * this.val[4]) * this.val[11]) * this.val[13])) + (((this.val[0] * this.val[6]) * this.val[11]) * this.val[13])) + (((this.val[3] * this.val[5]) * this.val[8]) * this.val[14])) - (((this.val[1] * this.val[7]) * this.val[8]) * this.val[14])) - (((this.val[3] * this.val[4]) * this.val[9]) * this.val[14])) + (((this.val[0] * this.val[7]) * this.val[9]) * this.val[14])) + (((this.val[1] * this.val[4]) * this.val[11]) * this.val[14])) - (((this.val[0] * this.val[5]) * this.val[11]) * this.val[14])) - (((this.val[2] * this.val[5]) * this.val[8]) * this.val[15])) + (((this.val[1] * this.val[6]) * this.val[8]) * this.val[15])) + (((this.val[2] * this.val[4]) * this.val[9]) * this.val[15])) - (((this.val[0] * this.val[6]) * this.val[9]) * this.val[15])) - (((this.val[1] * this.val[4]) * this.val[10]) * this.val[15])) + (this.val[0] * this.val[5] * this.val[10] * this.val[15]);
        if (l_det == 0.0f) {
            throw new RuntimeException("non-invertible matrix");
        }
        float inv_det = 1.0f / l_det;
        this.tmp[0] = ((((((this.val[9] * this.val[14]) * this.val[7]) - ((this.val[13] * this.val[10]) * this.val[7])) + ((this.val[13] * this.val[6]) * this.val[11])) - ((this.val[5] * this.val[14]) * this.val[11])) - ((this.val[9] * this.val[6]) * this.val[15])) + (this.val[5] * this.val[10] * this.val[15]);
        this.tmp[4] = ((((((this.val[12] * this.val[10]) * this.val[7]) - ((this.val[8] * this.val[14]) * this.val[7])) - ((this.val[12] * this.val[6]) * this.val[11])) + ((this.val[4] * this.val[14]) * this.val[11])) + ((this.val[8] * this.val[6]) * this.val[15])) - ((this.val[4] * this.val[10]) * this.val[15]);
        this.tmp[8] = ((((((this.val[8] * this.val[13]) * this.val[7]) - ((this.val[12] * this.val[9]) * this.val[7])) + ((this.val[12] * this.val[5]) * this.val[11])) - ((this.val[4] * this.val[13]) * this.val[11])) - ((this.val[8] * this.val[5]) * this.val[15])) + (this.val[4] * this.val[9] * this.val[15]);
        this.tmp[12] = ((((((this.val[12] * this.val[9]) * this.val[6]) - ((this.val[8] * this.val[13]) * this.val[6])) - ((this.val[12] * this.val[5]) * this.val[10])) + ((this.val[4] * this.val[13]) * this.val[10])) + ((this.val[8] * this.val[5]) * this.val[14])) - ((this.val[4] * this.val[9]) * this.val[14]);
        this.tmp[1] = ((((((this.val[13] * this.val[10]) * this.val[3]) - ((this.val[9] * this.val[14]) * this.val[3])) - ((this.val[13] * this.val[2]) * this.val[11])) + ((this.val[1] * this.val[14]) * this.val[11])) + ((this.val[9] * this.val[2]) * this.val[15])) - ((this.val[1] * this.val[10]) * this.val[15]);
        this.tmp[5] = ((((((this.val[8] * this.val[14]) * this.val[3]) - ((this.val[12] * this.val[10]) * this.val[3])) + ((this.val[12] * this.val[2]) * this.val[11])) - ((this.val[0] * this.val[14]) * this.val[11])) - ((this.val[8] * this.val[2]) * this.val[15])) + (this.val[0] * this.val[10] * this.val[15]);
        this.tmp[9] = ((((((this.val[12] * this.val[9]) * this.val[3]) - ((this.val[8] * this.val[13]) * this.val[3])) - ((this.val[12] * this.val[1]) * this.val[11])) + ((this.val[0] * this.val[13]) * this.val[11])) + ((this.val[8] * this.val[1]) * this.val[15])) - ((this.val[0] * this.val[9]) * this.val[15]);
        this.tmp[13] = ((((((this.val[8] * this.val[13]) * this.val[2]) - ((this.val[12] * this.val[9]) * this.val[2])) + ((this.val[12] * this.val[1]) * this.val[10])) - ((this.val[0] * this.val[13]) * this.val[10])) - ((this.val[8] * this.val[1]) * this.val[14])) + (this.val[0] * this.val[9] * this.val[14]);
        this.tmp[2] = ((((((this.val[5] * this.val[14]) * this.val[3]) - ((this.val[13] * this.val[6]) * this.val[3])) + ((this.val[13] * this.val[2]) * this.val[7])) - ((this.val[1] * this.val[14]) * this.val[7])) - ((this.val[5] * this.val[2]) * this.val[15])) + (this.val[1] * this.val[6] * this.val[15]);
        this.tmp[6] = ((((((this.val[12] * this.val[6]) * this.val[3]) - ((this.val[4] * this.val[14]) * this.val[3])) - ((this.val[12] * this.val[2]) * this.val[7])) + ((this.val[0] * this.val[14]) * this.val[7])) + ((this.val[4] * this.val[2]) * this.val[15])) - ((this.val[0] * this.val[6]) * this.val[15]);
        this.tmp[10] = ((((((this.val[4] * this.val[13]) * this.val[3]) - ((this.val[12] * this.val[5]) * this.val[3])) + ((this.val[12] * this.val[1]) * this.val[7])) - ((this.val[0] * this.val[13]) * this.val[7])) - ((this.val[4] * this.val[1]) * this.val[15])) + (this.val[0] * this.val[5] * this.val[15]);
        this.tmp[14] = ((((((this.val[12] * this.val[5]) * this.val[2]) - ((this.val[4] * this.val[13]) * this.val[2])) - ((this.val[12] * this.val[1]) * this.val[6])) + ((this.val[0] * this.val[13]) * this.val[6])) + ((this.val[4] * this.val[1]) * this.val[14])) - ((this.val[0] * this.val[5]) * this.val[14]);
        this.tmp[3] = ((((((this.val[9] * this.val[6]) * this.val[3]) - ((this.val[5] * this.val[10]) * this.val[3])) - ((this.val[9] * this.val[2]) * this.val[7])) + ((this.val[1] * this.val[10]) * this.val[7])) + ((this.val[5] * this.val[2]) * this.val[11])) - ((this.val[1] * this.val[6]) * this.val[11]);
        this.tmp[7] = ((((((this.val[4] * this.val[10]) * this.val[3]) - ((this.val[8] * this.val[6]) * this.val[3])) + ((this.val[8] * this.val[2]) * this.val[7])) - ((this.val[0] * this.val[10]) * this.val[7])) - ((this.val[4] * this.val[2]) * this.val[11])) + (this.val[0] * this.val[6] * this.val[11]);
        this.tmp[11] = ((((((this.val[8] * this.val[5]) * this.val[3]) - ((this.val[4] * this.val[9]) * this.val[3])) - ((this.val[8] * this.val[1]) * this.val[7])) + ((this.val[0] * this.val[9]) * this.val[7])) + ((this.val[4] * this.val[1]) * this.val[11])) - ((this.val[0] * this.val[5]) * this.val[11]);
        this.tmp[15] = ((((((this.val[4] * this.val[9]) * this.val[2]) - ((this.val[8] * this.val[5]) * this.val[2])) + ((this.val[8] * this.val[1]) * this.val[6])) - ((this.val[0] * this.val[9]) * this.val[6])) - ((this.val[4] * this.val[1]) * this.val[10])) + (this.val[0] * this.val[5] * this.val[10]);
        this.val[0] = this.tmp[0] * inv_det;
        this.val[4] = this.tmp[4] * inv_det;
        this.val[8] = this.tmp[8] * inv_det;
        this.val[12] = this.tmp[12] * inv_det;
        this.val[1] = this.tmp[1] * inv_det;
        this.val[5] = this.tmp[5] * inv_det;
        this.val[9] = this.tmp[9] * inv_det;
        this.val[13] = this.tmp[13] * inv_det;
        this.val[2] = this.tmp[2] * inv_det;
        this.val[6] = this.tmp[6] * inv_det;
        this.val[10] = this.tmp[10] * inv_det;
        this.val[14] = this.tmp[14] * inv_det;
        this.val[3] = this.tmp[3] * inv_det;
        this.val[7] = this.tmp[7] * inv_det;
        this.val[11] = this.tmp[11] * inv_det;
        this.val[15] = this.tmp[15] * inv_det;
        return this;
    }

    public float det() {
        return (((((((((((((((((((((((((this.val[3] * this.val[6]) * this.val[9]) * this.val[12]) - (((this.val[2] * this.val[7]) * this.val[9]) * this.val[12])) - (((this.val[3] * this.val[5]) * this.val[10]) * this.val[12])) + (((this.val[1] * this.val[7]) * this.val[10]) * this.val[12])) + (((this.val[2] * this.val[5]) * this.val[11]) * this.val[12])) - (((this.val[1] * this.val[6]) * this.val[11]) * this.val[12])) - (((this.val[3] * this.val[6]) * this.val[8]) * this.val[13])) + (((this.val[2] * this.val[7]) * this.val[8]) * this.val[13])) + (((this.val[3] * this.val[4]) * this.val[10]) * this.val[13])) - (((this.val[0] * this.val[7]) * this.val[10]) * this.val[13])) - (((this.val[2] * this.val[4]) * this.val[11]) * this.val[13])) + (((this.val[0] * this.val[6]) * this.val[11]) * this.val[13])) + (((this.val[3] * this.val[5]) * this.val[8]) * this.val[14])) - (((this.val[1] * this.val[7]) * this.val[8]) * this.val[14])) - (((this.val[3] * this.val[4]) * this.val[9]) * this.val[14])) + (((this.val[0] * this.val[7]) * this.val[9]) * this.val[14])) + (((this.val[1] * this.val[4]) * this.val[11]) * this.val[14])) - (((this.val[0] * this.val[5]) * this.val[11]) * this.val[14])) - (((this.val[2] * this.val[5]) * this.val[8]) * this.val[15])) + (((this.val[1] * this.val[6]) * this.val[8]) * this.val[15])) + (((this.val[2] * this.val[4]) * this.val[9]) * this.val[15])) - (((this.val[0] * this.val[6]) * this.val[9]) * this.val[15])) - (((this.val[1] * this.val[4]) * this.val[10]) * this.val[15])) + (this.val[0] * this.val[5] * this.val[10] * this.val[15]);
    }

    public Matrix4 setToProjection(float near, float far, float fov, float aspectRatio) {
        idt();
        float l_fd = (float) (1.0d / Math.tan((((double) fov) * 0.017453292519943295d) / 2.0d));
        this.val[0] = l_fd / aspectRatio;
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 0.0f;
        this.val[5] = l_fd;
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = (far + near) / (near - far);
        this.val[11] = -1.0f;
        this.val[12] = 0.0f;
        this.val[13] = 0.0f;
        this.val[14] = ((2.0f * far) * near) / (near - far);
        this.val[15] = 0.0f;
        return this;
    }

    public Matrix4 setToOrtho2D(float x, float y, float width, float height) {
        setToOrtho(x, x + width, y, y + height, 0.0f, 1.0f);
        return this;
    }

    public Matrix4 setToOrtho2D(float x, float y, float width, float height, float near, float far) {
        setToOrtho(x, x + width, y, y + height, near, far);
        return this;
    }

    public Matrix4 setToOrtho(float left, float right2, float bottom, float top, float near, float far) {
        idt();
        this.val[0] = 2.0f / (right2 - left);
        this.val[1] = 0.0f;
        this.val[2] = 0.0f;
        this.val[3] = 0.0f;
        this.val[4] = 0.0f;
        this.val[5] = 2.0f / (top - bottom);
        this.val[6] = 0.0f;
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = -2.0f / (far - near);
        this.val[11] = 0.0f;
        this.val[12] = (-(right2 + left)) / (right2 - left);
        this.val[13] = (-(top + bottom)) / (top - bottom);
        this.val[14] = (-(far + near)) / (far - near);
        this.val[15] = 1.0f;
        return this;
    }

    public Matrix4 setToTranslation(Vector3 vector) {
        idt();
        this.val[12] = vector.f170x;
        this.val[13] = vector.f171y;
        this.val[14] = vector.f172z;
        return this;
    }

    public Matrix4 setToTranslation(float x, float y, float z) {
        idt();
        this.val[12] = x;
        this.val[13] = y;
        this.val[14] = z;
        return this;
    }

    public Matrix4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
        idt();
        this.val[12] = translation.f170x;
        this.val[13] = translation.f171y;
        this.val[14] = translation.f172z;
        this.val[0] = scaling.f170x;
        this.val[5] = scaling.f171y;
        this.val[10] = scaling.f172z;
        return this;
    }

    public Matrix4 setToTranslationAndScaling(float translationX, float translationY, float translationZ, float scalingX, float scalingY, float scalingZ) {
        idt();
        this.val[12] = translationX;
        this.val[13] = translationY;
        this.val[14] = translationZ;
        this.val[0] = scalingX;
        this.val[5] = scalingY;
        this.val[10] = scalingZ;
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 1 */
    public Matrix4 setToRotation(Vector3 axis, float angle) {
        if (angle != 0.0f) {
            return set(quat.set(axis, angle));
        }
        idt();
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public Matrix4 setToRotation(float axisX, float axisY, float axisZ, float angle) {
        if (angle != 0.0f) {
            return set(quat.set(tmpV.set(axisX, axisY, axisZ), angle));
        }
        idt();
        return this;
    }

    public Matrix4 setToRotation(Vector3 v1, Vector3 v2) {
        idt();
        return set(quat.setFromCross(v1, v2));
    }

    public Matrix4 setToRotation(float x1, float y1, float z1, float x2, float y2, float z2) {
        idt();
        return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
    }

    public Matrix4 setFromEulerAngles(float yaw, float pitch, float roll) {
        quat.setEulerAngles(yaw, pitch, roll);
        return set(quat);
    }

    public Matrix4 setToScaling(Vector3 vector) {
        idt();
        this.val[0] = vector.f170x;
        this.val[5] = vector.f171y;
        this.val[10] = vector.f172z;
        return this;
    }

    public Matrix4 setToScaling(float x, float y, float z) {
        idt();
        this.val[0] = x;
        this.val[5] = y;
        this.val[10] = z;
        return this;
    }

    public Matrix4 setToLookAt(Vector3 direction, Vector3 up) {
        l_vez.set(direction).nor();
        l_vex.set(direction).nor();
        l_vex.crs(up).nor();
        l_vey.set(l_vex).crs(l_vez).nor();
        idt();
        this.val[0] = l_vex.f170x;
        this.val[4] = l_vex.f171y;
        this.val[8] = l_vex.f172z;
        this.val[1] = l_vey.f170x;
        this.val[5] = l_vey.f171y;
        this.val[9] = l_vey.f172z;
        this.val[2] = -l_vez.f170x;
        this.val[6] = -l_vez.f171y;
        this.val[10] = -l_vez.f172z;
        return this;
    }

    public Matrix4 setToLookAt(Vector3 position, Vector3 target, Vector3 up) {
        tmpVec.set(target).sub(position);
        setToLookAt(tmpVec, up);
        mul(tmpMat.setToTranslation(position.tmp().mul(-1.0f)));
        return this;
    }

    public Matrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
        tmpForward.set(forward).nor();
        right.set(tmpForward).crs(up).nor();
        tmpUp.set(right).crs(tmpForward).nor();
        set(right, tmpUp, tmpForward, position);
        return this;
    }

    public String toString() {
        return "[" + this.val[0] + "|" + this.val[4] + "|" + this.val[8] + "|" + this.val[12] + "]\n" + "[" + this.val[1] + "|" + this.val[5] + "|" + this.val[9] + "|" + this.val[13] + "]\n" + "[" + this.val[2] + "|" + this.val[6] + "|" + this.val[10] + "|" + this.val[14] + "]\n" + "[" + this.val[3] + "|" + this.val[7] + "|" + this.val[11] + "|" + this.val[15] + "]\n";
    }

    public void lerp(Matrix4 matrix, float alpha) {
        for (int i = 0; i < 16; i++) {
            this.val[i] = (this.val[i] * (1.0f - alpha)) + (matrix.val[i] * alpha);
        }
    }

    public Matrix4 set(Matrix3 mat) {
        this.val[0] = mat.val[0];
        this.val[1] = mat.val[1];
        this.val[2] = mat.val[2];
        this.val[3] = 0.0f;
        this.val[4] = mat.val[3];
        this.val[5] = mat.val[4];
        this.val[6] = mat.val[5];
        this.val[7] = 0.0f;
        this.val[8] = 0.0f;
        this.val[9] = 0.0f;
        this.val[10] = 1.0f;
        this.val[11] = 0.0f;
        this.val[12] = mat.val[6];
        this.val[13] = mat.val[7];
        this.val[14] = 0.0f;
        this.val[15] = mat.val[8];
        return this;
    }

    public Matrix4 scl(Vector3 scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale.f170x;
        float[] fArr2 = this.val;
        fArr2[5] = fArr2[5] * scale.f171y;
        float[] fArr3 = this.val;
        fArr3[10] = fArr3[10] * scale.f172z;
        return this;
    }

    public Matrix4 scl(float x, float y, float z) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * x;
        float[] fArr2 = this.val;
        fArr2[5] = fArr2[5] * y;
        float[] fArr3 = this.val;
        fArr3[10] = fArr3[10] * z;
        return this;
    }

    public Matrix4 scl(float scale) {
        float[] fArr = this.val;
        fArr[0] = fArr[0] * scale;
        float[] fArr2 = this.val;
        fArr2[5] = fArr2[5] * scale;
        float[] fArr3 = this.val;
        fArr3[10] = fArr3[10] * scale;
        return this;
    }

    public void getTranslation(Vector3 position) {
        position.f170x = this.val[12];
        position.f171y = this.val[13];
        position.f172z = this.val[14];
    }

    public void getRotation(Quaternion rotation) {
        rotation.setFromMatrix(this);
    }

    public Matrix4 toNormalMatrix() {
        this.val[12] = 0.0f;
        this.val[13] = 0.0f;
        this.val[14] = 0.0f;
        return inv().tra();
    }

    public Matrix4 translate(Vector3 translation) {
        return translate(translation.f170x, translation.f171y, translation.f172z);
    }

    public Matrix4 translate(float x, float y, float z) {
        this.tmp[0] = 1.0f;
        this.tmp[4] = 0.0f;
        this.tmp[8] = 0.0f;
        this.tmp[12] = x;
        this.tmp[1] = 0.0f;
        this.tmp[5] = 1.0f;
        this.tmp[9] = 0.0f;
        this.tmp[13] = y;
        this.tmp[2] = 0.0f;
        this.tmp[6] = 0.0f;
        this.tmp[10] = 1.0f;
        this.tmp[14] = z;
        this.tmp[3] = 0.0f;
        this.tmp[7] = 0.0f;
        this.tmp[11] = 0.0f;
        this.tmp[15] = 1.0f;
        mul(this.val, this.tmp);
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 1 */
    public Matrix4 rotate(Vector3 axis, float angle) {
        if (angle == 0.0f) {
            return this;
        }
        quat.set(axis, angle);
        return rotate(quat);
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public Matrix4 rotate(float axisX, float axisY, float axisZ, float angle) {
        if (angle == 0.0f) {
            return this;
        }
        quat.set(tmpV.set(axisX, axisY, axisZ), angle);
        return rotate(quat);
    }

    public Matrix4 rotate(Quaternion rotation) {
        rotation.toMatrix(this.tmp);
        mul(this.val, this.tmp);
        return this;
    }

    public Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
        this.tmp[0] = scaleX;
        this.tmp[4] = 0.0f;
        this.tmp[8] = 0.0f;
        this.tmp[12] = 0.0f;
        this.tmp[1] = 0.0f;
        this.tmp[5] = scaleY;
        this.tmp[9] = 0.0f;
        this.tmp[13] = 0.0f;
        this.tmp[2] = 0.0f;
        this.tmp[6] = 0.0f;
        this.tmp[10] = scaleZ;
        this.tmp[14] = 0.0f;
        this.tmp[3] = 0.0f;
        this.tmp[7] = 0.0f;
        this.tmp[11] = 0.0f;
        this.tmp[15] = 1.0f;
        mul(this.val, this.tmp);
        return this;
    }
}
