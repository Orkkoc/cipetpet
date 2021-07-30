package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.NumberUtils;

public class Decal {

    /* renamed from: C1 */
    public static final int f113C1 = 3;

    /* renamed from: C2 */
    public static final int f114C2 = 9;

    /* renamed from: C3 */
    public static final int f115C3 = 15;

    /* renamed from: C4 */
    public static final int f116C4 = 21;
    public static final int SIZE = 24;

    /* renamed from: U1 */
    public static final int f117U1 = 4;

    /* renamed from: U2 */
    public static final int f118U2 = 10;

    /* renamed from: U3 */
    public static final int f119U3 = 16;

    /* renamed from: U4 */
    public static final int f120U4 = 22;

    /* renamed from: V1 */
    public static final int f121V1 = 5;

    /* renamed from: V2 */
    public static final int f122V2 = 11;

    /* renamed from: V3 */
    public static final int f123V3 = 17;

    /* renamed from: V4 */
    public static final int f124V4 = 23;
    private static final int VERTEX_SIZE = 6;

    /* renamed from: X1 */
    public static final int f125X1 = 0;

    /* renamed from: X2 */
    public static final int f126X2 = 6;

    /* renamed from: X3 */
    public static final int f127X3 = 12;

    /* renamed from: X4 */
    public static final int f128X4 = 18;
    protected static final Vector3 X_AXIS = new Vector3(1.0f, 0.0f, 0.0f);

    /* renamed from: Y1 */
    public static final int f129Y1 = 1;

    /* renamed from: Y2 */
    public static final int f130Y2 = 7;

    /* renamed from: Y3 */
    public static final int f131Y3 = 13;

    /* renamed from: Y4 */
    public static final int f132Y4 = 19;
    protected static final Vector3 Y_AXIS = new Vector3(0.0f, 1.0f, 0.0f);

    /* renamed from: Z1 */
    public static final int f133Z1 = 2;

    /* renamed from: Z2 */
    public static final int f134Z2 = 8;

    /* renamed from: Z3 */
    public static final int f135Z3 = 14;

    /* renamed from: Z4 */
    public static final int f136Z4 = 20;
    protected static final Vector3 Z_AXIS = new Vector3(0.0f, 0.0f, 1.0f);
    static final Vector3 dir = new Vector3();
    protected static Quaternion rotator = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
    private static Vector3 tmp = new Vector3();
    private static Vector3 tmp2 = new Vector3();
    protected Vector2 dimensions = new Vector2();
    protected DecalMaterial material = new DecalMaterial();
    protected Vector3 position = new Vector3();
    protected Quaternion rotation = new Quaternion();
    protected Vector2 scale = new Vector2(1.0f, 1.0f);
    public Vector2 transformationOffset = null;
    protected boolean updated = false;
    public int value;
    protected float[] vertices = new float[24];

    protected Decal() {
    }

    public void setColor(float r, float g, float b, float a) {
        float color = NumberUtils.intToFloatColor((((int) (255.0f * a)) << 24) | (((int) (255.0f * b)) << 16) | (((int) (255.0f * g)) << 8) | ((int) (255.0f * r)));
        this.vertices[3] = color;
        this.vertices[9] = color;
        this.vertices[15] = color;
        this.vertices[21] = color;
    }

    public void setRotationX(float angle) {
        this.rotation.set(X_AXIS, angle);
        this.updated = false;
    }

    public void setRotationY(float angle) {
        this.rotation.set(Y_AXIS, angle);
        this.updated = false;
    }

    public void setRotationZ(float angle) {
        this.rotation.set(Z_AXIS, angle);
        this.updated = false;
    }

    public void rotateX(float angle) {
        rotator.set(X_AXIS, angle);
        this.rotation.mul(rotator);
        this.updated = false;
    }

    public void rotateY(float angle) {
        rotator.set(Y_AXIS, angle);
        this.rotation.mul(rotator);
        this.updated = false;
    }

    public void rotateZ(float angle) {
        rotator.set(Z_AXIS, angle);
        this.rotation.mul(rotator);
        this.updated = false;
    }

    public void setRotation(Vector3 dir2, Vector3 up) {
        tmp.set(up).crs(dir2).nor();
        tmp2.set(dir2).crs(tmp).nor();
        this.rotation.setFromAxes(tmp.f170x, tmp2.f170x, dir2.f170x, tmp.f171y, tmp2.f171y, dir2.f171y, tmp.f172z, tmp2.f172z, dir2.f172z);
        this.updated = false;
    }

    public Quaternion getRotation() {
        return this.rotation;
    }

    public void translateX(float units) {
        this.position.f170x += units;
        this.updated = false;
    }

    public void setX(float x) {
        this.position.f170x = x;
        this.updated = false;
    }

    public float getX() {
        return this.position.f170x;
    }

    public void translateY(float units) {
        this.position.f171y += units;
        this.updated = false;
    }

    public void setY(float y) {
        this.position.f171y = y;
        this.updated = false;
    }

    public float getY() {
        return this.position.f171y;
    }

    public void translateZ(float units) {
        this.position.f172z += units;
        this.updated = false;
    }

    public void setZ(float z) {
        this.position.f172z = z;
        this.updated = false;
    }

    public float getZ() {
        return this.position.f172z;
    }

    public void translate(float x, float y, float z) {
        this.position.add(x, y, z);
        this.updated = false;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        this.updated = false;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public void setScaleX(float scale2) {
        this.scale.f165x = scale2;
        this.updated = false;
    }

    public float getScaleX() {
        return this.scale.f165x;
    }

    public void setScaleY(float scale2) {
        this.scale.f166y += scale2;
        this.updated = false;
    }

    public float getScaleY() {
        return this.scale.f166y;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scale.set(scaleX, scaleY);
        this.updated = false;
    }

    public void setScale(float scale2) {
        this.scale.set(scale2, scale2);
        this.updated = false;
    }

    public void setWidth(float width) {
        this.dimensions.f165x = width;
        this.updated = false;
    }

    public float getWidth() {
        return this.dimensions.f165x;
    }

    public void setHeight(float height) {
        this.dimensions.f166y = height;
        this.updated = false;
    }

    public float getHeight() {
        return this.dimensions.f166y;
    }

    public void setDimensions(float width, float height) {
        this.dimensions.set(width, height);
        this.updated = false;
    }

    public float[] getVertices() {
        return this.vertices;
    }

    /* access modifiers changed from: protected */
    public void update() {
        if (!this.updated) {
            resetVertices();
            transformVertices();
        }
    }

    /* access modifiers changed from: protected */
    public void transformVertices() {
        float ty;
        float tx;
        if (this.transformationOffset != null) {
            tx = -this.transformationOffset.f165x;
            ty = -this.transformationOffset.f166y;
        } else {
            ty = 0.0f;
            tx = 0.0f;
        }
        float x = (this.vertices[0] + tx) * this.scale.f165x;
        float y = (this.vertices[1] + ty) * this.scale.f166y;
        float z = this.vertices[2];
        this.vertices[0] = ((this.rotation.f157w * x) + (this.rotation.f159y * z)) - (this.rotation.f160z * y);
        this.vertices[1] = ((this.rotation.f157w * y) + (this.rotation.f160z * x)) - (this.rotation.f158x * z);
        this.vertices[2] = ((this.rotation.f157w * z) + (this.rotation.f158x * y)) - (this.rotation.f159y * x);
        float w = (((-this.rotation.f158x) * x) - (this.rotation.f159y * y)) - (this.rotation.f160z * z);
        this.rotation.conjugate();
        float x2 = this.vertices[0];
        float y2 = this.vertices[1];
        float z2 = this.vertices[2];
        this.vertices[0] = (((this.rotation.f158x * w) + (this.rotation.f157w * x2)) + (this.rotation.f160z * y2)) - (this.rotation.f159y * z2);
        this.vertices[1] = (((this.rotation.f159y * w) + (this.rotation.f157w * y2)) + (this.rotation.f158x * z2)) - (this.rotation.f160z * x2);
        this.vertices[2] = (((this.rotation.f160z * w) + (this.rotation.f157w * z2)) + (this.rotation.f159y * x2)) - (this.rotation.f158x * y2);
        this.rotation.conjugate();
        float[] fArr = this.vertices;
        fArr[0] = fArr[0] + (this.position.f170x - tx);
        float[] fArr2 = this.vertices;
        fArr2[1] = fArr2[1] + (this.position.f171y - ty);
        float[] fArr3 = this.vertices;
        fArr3[2] = fArr3[2] + this.position.f172z;
        float x3 = (this.vertices[6] + tx) * this.scale.f165x;
        float y3 = (this.vertices[7] + ty) * this.scale.f166y;
        float z3 = this.vertices[8];
        this.vertices[6] = ((this.rotation.f157w * x3) + (this.rotation.f159y * z3)) - (this.rotation.f160z * y3);
        this.vertices[7] = ((this.rotation.f157w * y3) + (this.rotation.f160z * x3)) - (this.rotation.f158x * z3);
        this.vertices[8] = ((this.rotation.f157w * z3) + (this.rotation.f158x * y3)) - (this.rotation.f159y * x3);
        float w2 = (((-this.rotation.f158x) * x3) - (this.rotation.f159y * y3)) - (this.rotation.f160z * z3);
        this.rotation.conjugate();
        float x4 = this.vertices[6];
        float y4 = this.vertices[7];
        float z4 = this.vertices[8];
        this.vertices[6] = (((this.rotation.f158x * w2) + (this.rotation.f157w * x4)) + (this.rotation.f160z * y4)) - (this.rotation.f159y * z4);
        this.vertices[7] = (((this.rotation.f159y * w2) + (this.rotation.f157w * y4)) + (this.rotation.f158x * z4)) - (this.rotation.f160z * x4);
        this.vertices[8] = (((this.rotation.f160z * w2) + (this.rotation.f157w * z4)) + (this.rotation.f159y * x4)) - (this.rotation.f158x * y4);
        this.rotation.conjugate();
        float[] fArr4 = this.vertices;
        fArr4[6] = fArr4[6] + (this.position.f170x - tx);
        float[] fArr5 = this.vertices;
        fArr5[7] = fArr5[7] + (this.position.f171y - ty);
        float[] fArr6 = this.vertices;
        fArr6[8] = fArr6[8] + this.position.f172z;
        float x5 = (this.vertices[12] + tx) * this.scale.f165x;
        float y5 = (this.vertices[13] + ty) * this.scale.f166y;
        float z5 = this.vertices[14];
        this.vertices[12] = ((this.rotation.f157w * x5) + (this.rotation.f159y * z5)) - (this.rotation.f160z * y5);
        this.vertices[13] = ((this.rotation.f157w * y5) + (this.rotation.f160z * x5)) - (this.rotation.f158x * z5);
        this.vertices[14] = ((this.rotation.f157w * z5) + (this.rotation.f158x * y5)) - (this.rotation.f159y * x5);
        float w3 = (((-this.rotation.f158x) * x5) - (this.rotation.f159y * y5)) - (this.rotation.f160z * z5);
        this.rotation.conjugate();
        float x6 = this.vertices[12];
        float y6 = this.vertices[13];
        float z6 = this.vertices[14];
        this.vertices[12] = (((this.rotation.f158x * w3) + (this.rotation.f157w * x6)) + (this.rotation.f160z * y6)) - (this.rotation.f159y * z6);
        this.vertices[13] = (((this.rotation.f159y * w3) + (this.rotation.f157w * y6)) + (this.rotation.f158x * z6)) - (this.rotation.f160z * x6);
        this.vertices[14] = (((this.rotation.f160z * w3) + (this.rotation.f157w * z6)) + (this.rotation.f159y * x6)) - (this.rotation.f158x * y6);
        this.rotation.conjugate();
        float[] fArr7 = this.vertices;
        fArr7[12] = fArr7[12] + (this.position.f170x - tx);
        float[] fArr8 = this.vertices;
        fArr8[13] = fArr8[13] + (this.position.f171y - ty);
        float[] fArr9 = this.vertices;
        fArr9[14] = fArr9[14] + this.position.f172z;
        float x7 = (this.vertices[18] + tx) * this.scale.f165x;
        float y7 = (this.vertices[19] + ty) * this.scale.f166y;
        float z7 = this.vertices[20];
        this.vertices[18] = ((this.rotation.f157w * x7) + (this.rotation.f159y * z7)) - (this.rotation.f160z * y7);
        this.vertices[19] = ((this.rotation.f157w * y7) + (this.rotation.f160z * x7)) - (this.rotation.f158x * z7);
        this.vertices[20] = ((this.rotation.f157w * z7) + (this.rotation.f158x * y7)) - (this.rotation.f159y * x7);
        float w4 = (((-this.rotation.f158x) * x7) - (this.rotation.f159y * y7)) - (this.rotation.f160z * z7);
        this.rotation.conjugate();
        float x8 = this.vertices[18];
        float y8 = this.vertices[19];
        float z8 = this.vertices[20];
        this.vertices[18] = (((this.rotation.f158x * w4) + (this.rotation.f157w * x8)) + (this.rotation.f160z * y8)) - (this.rotation.f159y * z8);
        this.vertices[19] = (((this.rotation.f159y * w4) + (this.rotation.f157w * y8)) + (this.rotation.f158x * z8)) - (this.rotation.f160z * x8);
        this.vertices[20] = (((this.rotation.f160z * w4) + (this.rotation.f157w * z8)) + (this.rotation.f159y * x8)) - (this.rotation.f158x * y8);
        this.rotation.conjugate();
        float[] fArr10 = this.vertices;
        fArr10[18] = fArr10[18] + (this.position.f170x - tx);
        float[] fArr11 = this.vertices;
        fArr11[19] = fArr11[19] + (this.position.f171y - ty);
        float[] fArr12 = this.vertices;
        fArr12[20] = fArr12[20] + this.position.f172z;
        this.updated = true;
    }

    /* access modifiers changed from: protected */
    public void resetVertices() {
        float left = (-this.dimensions.f165x) / 2.0f;
        float right = left + this.dimensions.f165x;
        float top = this.dimensions.f166y / 2.0f;
        float bottom = top - this.dimensions.f166y;
        this.vertices[0] = left;
        this.vertices[1] = top;
        this.vertices[2] = 0.0f;
        this.vertices[6] = right;
        this.vertices[7] = top;
        this.vertices[8] = 0.0f;
        this.vertices[12] = left;
        this.vertices[13] = bottom;
        this.vertices[14] = 0.0f;
        this.vertices[18] = right;
        this.vertices[19] = bottom;
        this.vertices[20] = 0.0f;
        this.updated = false;
    }

    /* access modifiers changed from: protected */
    public void updateUVs() {
        TextureRegion tr = this.material.textureRegion;
        this.vertices[4] = tr.getU();
        this.vertices[5] = tr.getV();
        this.vertices[10] = tr.getU2();
        this.vertices[11] = tr.getV();
        this.vertices[16] = tr.getU();
        this.vertices[17] = tr.getV2();
        this.vertices[22] = tr.getU2();
        this.vertices[23] = tr.getV2();
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.material.textureRegion = textureRegion;
        updateUVs();
    }

    public TextureRegion getTextureRegion() {
        return this.material.textureRegion;
    }

    public void setBlending(int srcBlendFactor, int dstBlendFactor) {
        this.material.srcBlendFactor = srcBlendFactor;
        this.material.dstBlendFactor = dstBlendFactor;
    }

    public DecalMaterial getMaterial() {
        return this.material;
    }

    public void lookAt(Vector3 position2, Vector3 up) {
        dir.set(position2).sub(this.position).nor();
        setRotation(dir, up);
    }

    public static Decal newDecal(TextureRegion textureRegion) {
        return newDecal((float) textureRegion.getRegionWidth(), (float) textureRegion.getRegionHeight(), textureRegion, -1, -1);
    }

    public static Decal newDecal(TextureRegion textureRegion, boolean hasTransparency) {
        int i = -1;
        float regionWidth = (float) textureRegion.getRegionWidth();
        float regionHeight = (float) textureRegion.getRegionHeight();
        int i2 = hasTransparency ? 770 : -1;
        if (hasTransparency) {
            i = 771;
        }
        return newDecal(regionWidth, regionHeight, textureRegion, i2, i);
    }

    public static Decal newDecal(float width, float height, TextureRegion textureRegion) {
        return newDecal(width, height, textureRegion, -1, -1);
    }

    public static Decal newDecal(float width, float height, TextureRegion textureRegion, boolean hasTransparency) {
        int i = -1;
        int i2 = hasTransparency ? 770 : -1;
        if (hasTransparency) {
            i = 771;
        }
        return newDecal(width, height, textureRegion, i2, i);
    }

    public static Decal newDecal(float width, float height, TextureRegion textureRegion, int srcBlendFactor, int dstBlendFactor) {
        Decal decal = new Decal();
        decal.setTextureRegion(textureRegion);
        decal.setBlending(srcBlendFactor, dstBlendFactor);
        decal.dimensions.f165x = width;
        decal.dimensions.f166y = height;
        decal.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        return decal;
    }
}
