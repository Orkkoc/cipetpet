package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import java.util.ArrayList;

public class Body {
    protected long addr;
    private ArrayList<Fixture> fixtures = new ArrayList<>(2);
    protected ArrayList<JointEdge> joints = new ArrayList<>(2);
    public final Vector2 linVelLoc = new Vector2();
    public final Vector2 linVelWorld = new Vector2();
    private final Vector2 linearVelocity = new Vector2();
    private final Vector2 localCenter = new Vector2();
    private final Vector2 localPoint = new Vector2();
    public final Vector2 localPoint2 = new Vector2();
    public final Vector2 localVector = new Vector2();
    private final MassData massData = new MassData();
    private final Vector2 position = new Vector2();
    private final float[] tmp = new float[4];
    private final Transform transform = new Transform();
    private Object userData;
    private final World world;
    private final Vector2 worldCenter = new Vector2();
    private final Vector2 worldVector = new Vector2();

    private native void jniApplyAngularImpulse(long j, float f);

    private native void jniApplyForce(long j, float f, float f2, float f3, float f4);

    private native void jniApplyForceToCenter(long j, float f, float f2);

    private native void jniApplyLinearImpulse(long j, float f, float f2, float f3, float f4);

    private native void jniApplyTorque(long j, float f);

    private native long jniCreateFixture(long j, long j2, float f);

    private native long jniCreateFixture(long j, long j2, float f, float f2, float f3, boolean z, short s, short s2, short s3);

    private native void jniDestroyFixture(long j, long j2);

    private native float jniGetAngle(long j);

    private native float jniGetAngularDamping(long j);

    private native float jniGetAngularVelocity(long j);

    private native float jniGetGravityScale(long j);

    private native float jniGetInertia(long j);

    private native float jniGetLinearDamping(long j);

    private native void jniGetLinearVelocity(long j, float[] fArr);

    private native void jniGetLinearVelocityFromLocalPoint(long j, float f, float f2, float[] fArr);

    private native void jniGetLinearVelocityFromWorldPoint(long j, float f, float f2, float[] fArr);

    private native void jniGetLocalCenter(long j, float[] fArr);

    private native void jniGetLocalPoint(long j, float f, float f2, float[] fArr);

    private native void jniGetLocalVector(long j, float f, float f2, float[] fArr);

    private native float jniGetMass(long j);

    private native void jniGetMassData(long j, float[] fArr);

    private native void jniGetPosition(long j, float[] fArr);

    private native void jniGetTransform(long j, float[] fArr);

    private native int jniGetType(long j);

    private native void jniGetWorldCenter(long j, float[] fArr);

    private native void jniGetWorldPoint(long j, float f, float f2, float[] fArr);

    private native void jniGetWorldVector(long j, float f, float f2, float[] fArr);

    private native boolean jniIsActive(long j);

    private native boolean jniIsAwake(long j);

    private native boolean jniIsBullet(long j);

    private native boolean jniIsFixedRotation(long j);

    private native boolean jniIsSleepingAllowed(long j);

    private native void jniResetMassData(long j);

    private native void jniSetActive(long j, boolean z);

    private native void jniSetAngularDamping(long j, float f);

    private native void jniSetAngularVelocity(long j, float f);

    private native void jniSetAwake(long j, boolean z);

    private native void jniSetBullet(long j, boolean z);

    private native void jniSetFixedRotation(long j, boolean z);

    private native void jniSetGravityScale(long j, float f);

    private native void jniSetLinearDamping(long j, float f);

    private native void jniSetLinearVelocity(long j, float f, float f2);

    private native void jniSetMassData(long j, float f, float f2, float f3, float f4);

    private native void jniSetSleepingAllowed(long j, boolean z);

    private native void jniSetTransform(long j, float f, float f2, float f3);

    private native void jniSetTransform(long j, float f, float f2, float f3, boolean z);

    private native void jniSetType(long j, int i);

    protected Body(World world2, long addr2) {
        this.world = world2;
        this.addr = addr2;
    }

    /* access modifiers changed from: protected */
    public void reset(long addr2) {
        this.addr = addr2;
        this.userData = null;
        for (int i = 0; i < this.fixtures.size(); i++) {
            this.world.freeFixtures.free(this.fixtures.get(i));
        }
        this.fixtures.clear();
        this.joints.clear();
    }

    public Fixture createFixture(FixtureDef def) {
        long fixtureAddr = jniCreateFixture(this.addr, def.shape.addr, def.friction, def.restitution, def.density, def.isSensor, def.filter.categoryBits, def.filter.maskBits, def.filter.groupIndex);
        Fixture fixture = this.world.freeFixtures.obtain();
        fixture.reset(this, fixtureAddr);
        this.world.fixtures.put(fixture.addr, fixture);
        this.fixtures.add(fixture);
        return fixture;
    }

    public Fixture createFixture(Shape shape, float density) {
        long fixtureAddr = jniCreateFixture(this.addr, shape.addr, density);
        Fixture fixture = this.world.freeFixtures.obtain();
        fixture.reset(this, fixtureAddr);
        this.world.fixtures.put(fixture.addr, fixture);
        this.fixtures.add(fixture);
        return fixture;
    }

    public void destroyFixture(Fixture fixture) {
        jniDestroyFixture(this.addr, fixture.addr);
        this.world.fixtures.remove(fixture.addr);
        this.fixtures.remove(fixture);
        this.world.freeFixtures.free(fixture);
    }

    public void setTransform(Vector2 position2, float angle) {
        jniSetTransform(this.addr, position2.f165x, position2.f166y, angle);
    }

    public void setTransform(float x, float y, float angle) {
        jniSetTransform(this.addr, x, y, angle);
    }

    public void setTransform(float x, float y, float angle, boolean updateContacts) {
        jniSetTransform(this.addr, x, y, angle, updateContacts);
    }

    public Transform getTransform() {
        jniGetTransform(this.addr, this.transform.vals);
        return this.transform;
    }

    public Vector2 getPosition() {
        jniGetPosition(this.addr, this.tmp);
        this.position.f165x = this.tmp[0];
        this.position.f166y = this.tmp[1];
        return this.position;
    }

    public float getAngle() {
        return jniGetAngle(this.addr);
    }

    public Vector2 getWorldCenter() {
        jniGetWorldCenter(this.addr, this.tmp);
        this.worldCenter.f165x = this.tmp[0];
        this.worldCenter.f166y = this.tmp[1];
        return this.worldCenter;
    }

    public Vector2 getLocalCenter() {
        jniGetLocalCenter(this.addr, this.tmp);
        this.localCenter.f165x = this.tmp[0];
        this.localCenter.f166y = this.tmp[1];
        return this.localCenter;
    }

    public void setLinearVelocity(Vector2 v) {
        jniSetLinearVelocity(this.addr, v.f165x, v.f166y);
    }

    public void setLinearVelocity(float vX, float vY) {
        jniSetLinearVelocity(this.addr, vX, vY);
    }

    public Vector2 getLinearVelocity() {
        jniGetLinearVelocity(this.addr, this.tmp);
        this.linearVelocity.f165x = this.tmp[0];
        this.linearVelocity.f166y = this.tmp[1];
        return this.linearVelocity;
    }

    public void setAngularVelocity(float omega) {
        jniSetAngularVelocity(this.addr, omega);
    }

    public float getAngularVelocity() {
        return jniGetAngularVelocity(this.addr);
    }

    public void applyForce(Vector2 force, Vector2 point) {
        jniApplyForce(this.addr, force.f165x, force.f166y, point.f165x, point.f166y);
    }

    public void applyForce(float forceX, float forceY, float pointX, float pointY) {
        jniApplyForce(this.addr, forceX, forceY, pointX, pointY);
    }

    public void applyForceToCenter(Vector2 force) {
        jniApplyForceToCenter(this.addr, force.f165x, force.f166y);
    }

    public void applyForceToCenter(float forceX, float forceY) {
        jniApplyForceToCenter(this.addr, forceX, forceY);
    }

    public void applyTorque(float torque) {
        jniApplyTorque(this.addr, torque);
    }

    public void applyLinearImpulse(Vector2 impulse, Vector2 point) {
        jniApplyLinearImpulse(this.addr, impulse.f165x, impulse.f166y, point.f165x, point.f166y);
    }

    public void applyLinearImpulse(float impulseX, float impulseY, float pointX, float pointY) {
        jniApplyLinearImpulse(this.addr, impulseX, impulseY, pointX, pointY);
    }

    public void applyAngularImpulse(float impulse) {
        jniApplyAngularImpulse(this.addr, impulse);
    }

    public float getMass() {
        return jniGetMass(this.addr);
    }

    public float getInertia() {
        return jniGetInertia(this.addr);
    }

    public MassData getMassData() {
        jniGetMassData(this.addr, this.tmp);
        this.massData.mass = this.tmp[0];
        this.massData.center.f165x = this.tmp[1];
        this.massData.center.f166y = this.tmp[2];
        this.massData.f179I = this.tmp[3];
        return this.massData;
    }

    public void setMassData(MassData data) {
        jniSetMassData(this.addr, data.mass, data.center.f165x, data.center.f166y, data.f179I);
    }

    public void resetMassData() {
        jniResetMassData(this.addr);
    }

    public Vector2 getWorldPoint(Vector2 localPoint3) {
        jniGetWorldPoint(this.addr, localPoint3.f165x, localPoint3.f166y, this.tmp);
        this.localPoint.f165x = this.tmp[0];
        this.localPoint.f166y = this.tmp[1];
        return this.localPoint;
    }

    public Vector2 getWorldVector(Vector2 localVector2) {
        jniGetWorldVector(this.addr, localVector2.f165x, localVector2.f166y, this.tmp);
        this.worldVector.f165x = this.tmp[0];
        this.worldVector.f166y = this.tmp[1];
        return this.worldVector;
    }

    public Vector2 getLocalPoint(Vector2 worldPoint) {
        jniGetLocalPoint(this.addr, worldPoint.f165x, worldPoint.f166y, this.tmp);
        this.localPoint2.f165x = this.tmp[0];
        this.localPoint2.f166y = this.tmp[1];
        return this.localPoint2;
    }

    public Vector2 getLocalVector(Vector2 worldVector2) {
        jniGetLocalVector(this.addr, worldVector2.f165x, worldVector2.f166y, this.tmp);
        this.localVector.f165x = this.tmp[0];
        this.localVector.f166y = this.tmp[1];
        return this.localVector;
    }

    public Vector2 getLinearVelocityFromWorldPoint(Vector2 worldPoint) {
        jniGetLinearVelocityFromWorldPoint(this.addr, worldPoint.f165x, worldPoint.f166y, this.tmp);
        this.linVelWorld.f165x = this.tmp[0];
        this.linVelWorld.f166y = this.tmp[1];
        return this.linVelWorld;
    }

    public Vector2 getLinearVelocityFromLocalPoint(Vector2 localPoint3) {
        jniGetLinearVelocityFromLocalPoint(this.addr, localPoint3.f165x, localPoint3.f166y, this.tmp);
        this.linVelLoc.f165x = this.tmp[0];
        this.linVelLoc.f166y = this.tmp[1];
        return this.linVelLoc;
    }

    public float getLinearDamping() {
        return jniGetLinearDamping(this.addr);
    }

    public void setLinearDamping(float linearDamping) {
        jniSetLinearDamping(this.addr, linearDamping);
    }

    public float getAngularDamping() {
        return jniGetAngularDamping(this.addr);
    }

    public void setAngularDamping(float angularDamping) {
        jniSetAngularDamping(this.addr, angularDamping);
    }

    public void setType(BodyDef.BodyType type) {
        jniSetType(this.addr, type.getValue());
    }

    public BodyDef.BodyType getType() {
        int type = jniGetType(this.addr);
        if (type == 0) {
            return BodyDef.BodyType.StaticBody;
        }
        if (type == 1) {
            return BodyDef.BodyType.KinematicBody;
        }
        if (type == 2) {
            return BodyDef.BodyType.DynamicBody;
        }
        return BodyDef.BodyType.StaticBody;
    }

    public void setBullet(boolean flag) {
        jniSetBullet(this.addr, flag);
    }

    public boolean isBullet() {
        return jniIsBullet(this.addr);
    }

    public void setSleepingAllowed(boolean flag) {
        jniSetSleepingAllowed(this.addr, flag);
    }

    public boolean isSleepingAllowed() {
        return jniIsSleepingAllowed(this.addr);
    }

    public void setAwake(boolean flag) {
        jniSetAwake(this.addr, flag);
    }

    public boolean isAwake() {
        return jniIsAwake(this.addr);
    }

    public void setActive(boolean flag) {
        jniSetActive(this.addr, flag);
    }

    public boolean isActive() {
        return jniIsActive(this.addr);
    }

    public void setFixedRotation(boolean flag) {
        jniSetFixedRotation(this.addr, flag);
    }

    public boolean isFixedRotation() {
        return jniIsFixedRotation(this.addr);
    }

    public ArrayList<Fixture> getFixtureList() {
        return this.fixtures;
    }

    public ArrayList<JointEdge> getJointList() {
        return this.joints;
    }

    public float getGravityScale() {
        return jniGetGravityScale(this.addr);
    }

    public void setGravityScale(float scale) {
        jniSetGravityScale(this.addr, scale);
    }

    public World getWorld() {
        return this.world;
    }

    public Object getUserData() {
        return this.userData;
    }

    public void setUserData(Object userData2) {
        this.userData = userData2;
    }
}
