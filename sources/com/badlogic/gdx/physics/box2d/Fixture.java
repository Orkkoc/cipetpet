package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Fixture {
    protected long addr;
    private Body body;
    private final Filter filter = new Filter();
    protected Shape shape;
    private final short[] tmp = new short[3];
    protected Object userData;

    private native float jniGetDensity(long j);

    private native void jniGetFilterData(long j, short[] sArr);

    private native float jniGetFriction(long j);

    private native float jniGetRestitution(long j);

    private native long jniGetShape(long j);

    private native int jniGetType(long j);

    private native boolean jniIsSensor(long j);

    private native void jniRefilter(long j);

    private native void jniSetDensity(long j, float f);

    private native void jniSetFilterData(long j, short s, short s2, short s3);

    private native void jniSetFriction(long j, float f);

    private native void jniSetRestitution(long j, float f);

    private native void jniSetSensor(long j, boolean z);

    private native boolean jniTestPoint(long j, float f, float f2);

    protected Fixture(Body body2, long addr2) {
        this.body = body2;
        this.addr = addr2;
    }

    /* access modifiers changed from: protected */
    public void reset(Body body2, long addr2) {
        this.body = body2;
        this.addr = addr2;
        this.shape = null;
        this.userData = null;
    }

    public Shape.Type getType() {
        switch (jniGetType(this.addr)) {
            case 0:
                return Shape.Type.Circle;
            case 1:
                return Shape.Type.Edge;
            case 2:
                return Shape.Type.Polygon;
            case 3:
                return Shape.Type.Chain;
            default:
                throw new GdxRuntimeException("Unknown shape type!");
        }
    }

    public Shape getShape() {
        if (this.shape == null) {
            long shapeAddr = jniGetShape(this.addr);
            if (shapeAddr == 0) {
                throw new GdxRuntimeException("Null shape address!");
            }
            switch (Shape.jniGetType(shapeAddr)) {
                case 0:
                    this.shape = new CircleShape(shapeAddr);
                    break;
                case 1:
                    this.shape = new EdgeShape(shapeAddr);
                    break;
                case 2:
                    this.shape = new PolygonShape(shapeAddr);
                    break;
                case 3:
                    this.shape = new ChainShape(shapeAddr);
                    break;
                default:
                    throw new GdxRuntimeException("Unknown shape type!");
            }
        }
        return this.shape;
    }

    public void setSensor(boolean sensor) {
        jniSetSensor(this.addr, sensor);
    }

    public boolean isSensor() {
        return jniIsSensor(this.addr);
    }

    public void setFilterData(Filter filter2) {
        jniSetFilterData(this.addr, filter2.categoryBits, filter2.maskBits, filter2.groupIndex);
    }

    public Filter getFilterData() {
        jniGetFilterData(this.addr, this.tmp);
        this.filter.maskBits = this.tmp[0];
        this.filter.categoryBits = this.tmp[1];
        this.filter.groupIndex = this.tmp[2];
        return this.filter;
    }

    public void refilter() {
        jniRefilter(this.addr);
    }

    public Body getBody() {
        return this.body;
    }

    public boolean testPoint(Vector2 p) {
        return jniTestPoint(this.addr, p.f165x, p.f166y);
    }

    public boolean testPoint(float x, float y) {
        return jniTestPoint(this.addr, x, y);
    }

    public void setDensity(float density) {
        jniSetDensity(this.addr, density);
    }

    public float getDensity() {
        return jniGetDensity(this.addr);
    }

    public float getFriction() {
        return jniGetFriction(this.addr);
    }

    public void setFriction(float friction) {
        jniSetFriction(this.addr, friction);
    }

    public float getRestitution() {
        return jniGetRestitution(this.addr);
    }

    public void setRestitution(float restitution) {
        jniSetRestitution(this.addr, restitution);
    }

    public void setUserData(Object userData2) {
        this.userData = userData2;
    }

    public Object getUserData() {
        return this.userData;
    }
}
