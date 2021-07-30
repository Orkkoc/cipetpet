package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class World implements Disposable {
    private final long addr;
    protected final LongMap<Body> bodies = new LongMap<>(100);
    private final Contact contact = new Contact(this, 0);
    private long[] contactAddrs = new long[200];
    protected ContactFilter contactFilter = null;
    protected ContactListener contactListener = null;
    private final ArrayList<Contact> contacts = new ArrayList<>();
    protected final LongMap<Fixture> fixtures = new LongMap<>(100);
    protected final Pool<Body> freeBodies = new Pool<Body>(100, 200) {
        /* access modifiers changed from: protected */
        public Body newObject() {
            return new Body(World.this, 0);
        }
    };
    private final ArrayList<Contact> freeContacts = new ArrayList<>();
    protected final Pool<Fixture> freeFixtures = new Pool<Fixture>(100, 200) {
        /* access modifiers changed from: protected */
        public Fixture newObject() {
            return new Fixture((Body) null, 0);
        }
    };
    final Vector2 gravity = new Vector2();
    private final ContactImpulse impulse = new ContactImpulse(this, 0);
    protected final LongMap<Joint> joints = new LongMap<>(100);
    private final Manifold manifold = new Manifold(0);
    private QueryCallback queryCallback = null;
    private RayCastCallback rayCastCallback = null;
    private Vector2 rayNormal = new Vector2();
    private Vector2 rayPoint = new Vector2();
    final float[] tmpGravity = new float[2];

    public static native float getVelocityThreshold();

    private native void jniClearForces(long j);

    private native long jniCreateBody(long j, int i, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, float f9);

    private native long jniCreateDistanceJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7);

    private native long jniCreateFrictionJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6);

    private native long jniCreateGearJoint(long j, long j2, long j3, boolean z, long j4, long j5, float f);

    private native long jniCreateMouseJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5);

    private native long jniCreatePrismaticJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z2, float f8, float f9, boolean z3, float f10, float f11);

    private native long jniCreatePulleyJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11);

    private native long jniCreateRevoluteJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, boolean z2, float f6, float f7, boolean z3, float f8, float f9);

    private native long jniCreateRopeJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5);

    private native long jniCreateWeldJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5);

    private native long jniCreateWheelJoint(long j, long j2, long j3, boolean z, float f, float f2, float f3, float f4, float f5, float f6, boolean z2, float f7, float f8, float f9, float f10);

    private native void jniDestroyBody(long j, long j2);

    private native void jniDestroyJoint(long j, long j2);

    private native void jniDispose(long j);

    private native boolean jniGetAutoClearForces(long j);

    private native int jniGetBodyCount(long j);

    private native int jniGetContactCount(long j);

    private native void jniGetContactList(long j, long[] jArr);

    private native void jniGetGravity(long j, float[] fArr);

    private native int jniGetJointcount(long j);

    private native int jniGetProxyCount(long j);

    private native boolean jniIsLocked(long j);

    private native void jniQueryAABB(long j, float f, float f2, float f3, float f4);

    private native void jniRayCast(long j, float f, float f2, float f3, float f4);

    private native void jniSetAutoClearForces(long j, boolean z);

    private native void jniSetContiousPhysics(long j, boolean z);

    private native void jniSetGravity(long j, float f, float f2);

    private native void jniSetWarmStarting(long j, boolean z);

    private native void jniStep(long j, float f, int i, int i2);

    private native long newWorld(float f, float f2, boolean z);

    private native void setUseDefaultContactFilter(boolean z);

    public static native void setVelocityThreshold(float f);

    public World(Vector2 gravity2, boolean doSleep) {
        this.addr = newWorld(gravity2.f165x, gravity2.f166y, doSleep);
        this.contacts.ensureCapacity(this.contactAddrs.length);
        this.freeContacts.ensureCapacity(this.contactAddrs.length);
        for (int i = 0; i < this.contactAddrs.length; i++) {
            this.freeContacts.add(new Contact(this, 0));
        }
    }

    public void setDestructionListener(DestructionListener listener) {
    }

    public void setContactFilter(ContactFilter filter) {
        this.contactFilter = filter;
        setUseDefaultContactFilter(filter == null);
    }

    public void setContactListener(ContactListener listener) {
        this.contactListener = listener;
    }

    public Body createBody(BodyDef def) {
        long bodyAddr = jniCreateBody(this.addr, def.type.getValue(), def.position.f165x, def.position.f166y, def.angle, def.linearVelocity.f165x, def.linearVelocity.f166y, def.angularVelocity, def.linearDamping, def.angularDamping, def.allowSleep, def.awake, def.fixedRotation, def.bullet, def.active, def.gravityScale);
        Body body = this.freeBodies.obtain();
        body.reset(bodyAddr);
        this.bodies.put(body.addr, body);
        return body;
    }

    public void destroyBody(Body body) {
        body.setUserData((Object) null);
        this.bodies.remove(body.addr);
        List<Fixture> fixtureList = body.getFixtureList();
        while (!fixtureList.isEmpty()) {
            this.fixtures.remove(fixtureList.remove(0).addr).setUserData((Object) null);
        }
        List<JointEdge> jointList = body.getJointList();
        while (!jointList.isEmpty()) {
            destroyJoint(body.getJointList().get(0).joint);
        }
        jniDestroyBody(this.addr, body.addr);
        this.freeBodies.free(body);
    }

    public Joint createJoint(JointDef def) {
        long jointAddr = createProperJoint(def);
        Joint joint = null;
        if (def.type == JointDef.JointType.DistanceJoint) {
            joint = new DistanceJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.FrictionJoint) {
            joint = new FrictionJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.GearJoint) {
            joint = new GearJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.MouseJoint) {
            joint = new MouseJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.PrismaticJoint) {
            joint = new PrismaticJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.PulleyJoint) {
            joint = new PulleyJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.RevoluteJoint) {
            joint = new RevoluteJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.WeldJoint) {
            joint = new WeldJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.RopeJoint) {
            joint = new RopeJoint(this, jointAddr);
        }
        if (def.type == JointDef.JointType.WheelJoint) {
            joint = new WheelJoint(this, jointAddr);
        }
        if (joint != null) {
            this.joints.put(joint.addr, joint);
        }
        JointEdge jointEdgeA = new JointEdge(def.bodyB, joint);
        JointEdge jointEdgeB = new JointEdge(def.bodyA, joint);
        joint.jointEdgeA = jointEdgeA;
        joint.jointEdgeB = jointEdgeB;
        def.bodyA.joints.add(jointEdgeA);
        def.bodyB.joints.add(jointEdgeB);
        return joint;
    }

    private long createProperJoint(JointDef def) {
        if (def.type == JointDef.JointType.DistanceJoint) {
            DistanceJointDef d = (DistanceJointDef) def;
            return jniCreateDistanceJoint(this.addr, d.bodyA.addr, d.bodyB.addr, d.collideConnected, d.localAnchorA.f165x, d.localAnchorA.f166y, d.localAnchorB.f165x, d.localAnchorB.f166y, d.length, d.frequencyHz, d.dampingRatio);
        } else if (def.type == JointDef.JointType.FrictionJoint) {
            FrictionJointDef d2 = (FrictionJointDef) def;
            return jniCreateFrictionJoint(this.addr, d2.bodyA.addr, d2.bodyB.addr, d2.collideConnected, d2.localAnchorA.f165x, d2.localAnchorA.f166y, d2.localAnchorB.f165x, d2.localAnchorB.f166y, d2.maxForce, d2.maxTorque);
        } else if (def.type == JointDef.JointType.GearJoint) {
            GearJointDef d3 = (GearJointDef) def;
            return jniCreateGearJoint(this.addr, d3.bodyA.addr, d3.bodyB.addr, d3.collideConnected, d3.joint1.addr, d3.joint2.addr, d3.ratio);
        } else if (def.type == JointDef.JointType.MouseJoint) {
            MouseJointDef d4 = (MouseJointDef) def;
            return jniCreateMouseJoint(this.addr, d4.bodyA.addr, d4.bodyB.addr, d4.collideConnected, d4.target.f165x, d4.target.f166y, d4.maxForce, d4.frequencyHz, d4.dampingRatio);
        } else if (def.type == JointDef.JointType.PrismaticJoint) {
            PrismaticJointDef d5 = (PrismaticJointDef) def;
            return jniCreatePrismaticJoint(this.addr, d5.bodyA.addr, d5.bodyB.addr, d5.collideConnected, d5.localAnchorA.f165x, d5.localAnchorA.f166y, d5.localAnchorB.f165x, d5.localAnchorB.f166y, d5.localAxisA.f165x, d5.localAxisA.f166y, d5.referenceAngle, d5.enableLimit, d5.lowerTranslation, d5.upperTranslation, d5.enableMotor, d5.maxMotorForce, d5.motorSpeed);
        } else if (def.type == JointDef.JointType.PulleyJoint) {
            PulleyJointDef d6 = (PulleyJointDef) def;
            return jniCreatePulleyJoint(this.addr, d6.bodyA.addr, d6.bodyB.addr, d6.collideConnected, d6.groundAnchorA.f165x, d6.groundAnchorA.f166y, d6.groundAnchorB.f165x, d6.groundAnchorB.f166y, d6.localAnchorA.f165x, d6.localAnchorA.f166y, d6.localAnchorB.f165x, d6.localAnchorB.f166y, d6.lengthA, d6.lengthB, d6.ratio);
        } else if (def.type == JointDef.JointType.RevoluteJoint) {
            RevoluteJointDef d7 = (RevoluteJointDef) def;
            return jniCreateRevoluteJoint(this.addr, d7.bodyA.addr, d7.bodyB.addr, d7.collideConnected, d7.localAnchorA.f165x, d7.localAnchorA.f166y, d7.localAnchorB.f165x, d7.localAnchorB.f166y, d7.referenceAngle, d7.enableLimit, d7.lowerAngle, d7.upperAngle, d7.enableMotor, d7.motorSpeed, d7.maxMotorTorque);
        } else if (def.type == JointDef.JointType.WeldJoint) {
            WeldJointDef d8 = (WeldJointDef) def;
            return jniCreateWeldJoint(this.addr, d8.bodyA.addr, d8.bodyB.addr, d8.collideConnected, d8.localAnchorA.f165x, d8.localAnchorA.f166y, d8.localAnchorB.f165x, d8.localAnchorB.f166y, d8.referenceAngle);
        } else if (def.type == JointDef.JointType.RopeJoint) {
            RopeJointDef d9 = (RopeJointDef) def;
            return jniCreateRopeJoint(this.addr, d9.bodyA.addr, d9.bodyB.addr, d9.collideConnected, d9.localAnchorA.f165x, d9.localAnchorA.f166y, d9.localAnchorB.f165x, d9.localAnchorB.f166y, d9.maxLength);
        } else if (def.type != JointDef.JointType.WheelJoint) {
            return 0;
        } else {
            WheelJointDef d10 = (WheelJointDef) def;
            return jniCreateWheelJoint(this.addr, d10.bodyA.addr, d10.bodyB.addr, d10.collideConnected, d10.localAnchorA.f165x, d10.localAnchorA.f166y, d10.localAnchorB.f165x, d10.localAnchorB.f166y, d10.localAxisA.f165x, d10.localAxisA.f166y, d10.enableMotor, d10.maxMotorTorque, d10.motorSpeed, d10.frequencyHz, d10.dampingRatio);
        }
    }

    public void destroyJoint(Joint joint) {
        this.joints.remove(joint.addr);
        joint.jointEdgeA.other.joints.remove(joint.jointEdgeB);
        joint.jointEdgeB.other.joints.remove(joint.jointEdgeA);
        jniDestroyJoint(this.addr, joint.addr);
    }

    public void step(float timeStep, int velocityIterations, int positionIterations) {
        jniStep(this.addr, timeStep, velocityIterations, positionIterations);
    }

    public void clearForces() {
        jniClearForces(this.addr);
    }

    public void setWarmStarting(boolean flag) {
        jniSetWarmStarting(this.addr, flag);
    }

    public void setContinuousPhysics(boolean flag) {
        jniSetContiousPhysics(this.addr, flag);
    }

    public int getProxyCount() {
        return jniGetProxyCount(this.addr);
    }

    public int getBodyCount() {
        return jniGetBodyCount(this.addr);
    }

    public int getJointCount() {
        return jniGetJointcount(this.addr);
    }

    public int getContactCount() {
        return jniGetContactCount(this.addr);
    }

    public void setGravity(Vector2 gravity2) {
        jniSetGravity(this.addr, gravity2.f165x, gravity2.f166y);
    }

    public Vector2 getGravity() {
        jniGetGravity(this.addr, this.tmpGravity);
        this.gravity.f165x = this.tmpGravity[0];
        this.gravity.f166y = this.tmpGravity[1];
        return this.gravity;
    }

    public boolean isLocked() {
        return jniIsLocked(this.addr);
    }

    public void setAutoClearForces(boolean flag) {
        jniSetAutoClearForces(this.addr, flag);
    }

    public boolean getAutoClearForces() {
        return jniGetAutoClearForces(this.addr);
    }

    public void QueryAABB(QueryCallback callback, float lowerX, float lowerY, float upperX, float upperY) {
        this.queryCallback = callback;
        jniQueryAABB(this.addr, lowerX, lowerY, upperX, upperY);
    }

    public List<Contact> getContactList() {
        int numContacts = getContactCount();
        if (numContacts > this.contactAddrs.length) {
            int newSize = numContacts * 2;
            this.contactAddrs = new long[newSize];
            this.contacts.ensureCapacity(newSize);
            this.freeContacts.ensureCapacity(newSize);
        }
        if (numContacts > this.freeContacts.size()) {
            int freeConts = this.freeContacts.size();
            for (int i = 0; i < numContacts - freeConts; i++) {
                this.freeContacts.add(new Contact(this, 0));
            }
        }
        jniGetContactList(this.addr, this.contactAddrs);
        this.contacts.clear();
        for (int i2 = 0; i2 < numContacts; i2++) {
            Contact contact2 = this.freeContacts.get(i2);
            contact2.addr = this.contactAddrs[i2];
            this.contacts.add(contact2);
        }
        return this.contacts;
    }

    public Iterator<Body> getBodies() {
        return this.bodies.values();
    }

    public Iterator<Joint> getJoints() {
        return this.joints.values();
    }

    public void dispose() {
        jniDispose(this.addr);
    }

    private boolean contactFilter(long fixtureA, long fixtureB) {
        boolean collide;
        if (this.contactFilter != null) {
            return this.contactFilter.shouldCollide(this.fixtures.get(fixtureA), this.fixtures.get(fixtureB));
        }
        Filter filterA = this.fixtures.get(fixtureA).getFilterData();
        Filter filterB = this.fixtures.get(fixtureB).getFilterData();
        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }
        if ((filterA.maskBits & filterB.categoryBits) == 0 || (filterA.categoryBits & filterB.maskBits) == 0) {
            collide = false;
        } else {
            collide = true;
        }
        return collide;
    }

    private void beginContact(long contactAddr) {
        this.contact.addr = contactAddr;
        if (this.contactListener != null) {
            this.contactListener.beginContact(this.contact);
        }
    }

    private void endContact(long contactAddr) {
        this.contact.addr = contactAddr;
        if (this.contactListener != null) {
            this.contactListener.endContact(this.contact);
        }
    }

    private void preSolve(long contactAddr, long manifoldAddr) {
        this.contact.addr = contactAddr;
        this.manifold.addr = manifoldAddr;
        if (this.contactListener != null) {
            this.contactListener.preSolve(this.contact, this.manifold);
        }
    }

    private void postSolve(long contactAddr, long impulseAddr) {
        this.contact.addr = contactAddr;
        this.impulse.addr = impulseAddr;
        if (this.contactListener != null) {
            this.contactListener.postSolve(this.contact, this.impulse);
        }
    }

    private boolean reportFixture(long addr2) {
        if (this.queryCallback != null) {
            return this.queryCallback.reportFixture(this.fixtures.get(addr2));
        }
        return false;
    }

    public void rayCast(RayCastCallback callback, Vector2 point1, Vector2 point2) {
        this.rayCastCallback = callback;
        jniRayCast(this.addr, point1.f165x, point1.f166y, point2.f165x, point2.f166y);
    }

    private float reportRayFixture(long addr2, float pX, float pY, float nX, float nY, float fraction) {
        if (this.rayCastCallback == null) {
            return 0.0f;
        }
        this.rayPoint.f165x = pX;
        this.rayPoint.f166y = pY;
        this.rayNormal.f165x = nX;
        this.rayNormal.f166y = nY;
        return this.rayCastCallback.reportRayFixture(this.fixtures.get(addr2), this.rayPoint, this.rayNormal, fraction);
    }
}
