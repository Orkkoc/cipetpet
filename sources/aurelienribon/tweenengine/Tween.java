package aurelienribon.tweenengine;

import aurelienribon.tweenengine.Pool;
import aurelienribon.tweenengine.equations.Quad;
import java.util.HashMap;
import java.util.Map;

public final class Tween extends BaseTween<Tween> {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int INFINITY = -1;
    private static int combinedAttrsLimit = 3;
    private static final Pool<Tween> pool = new Pool<Tween>(20, poolCallback) {
        /* access modifiers changed from: protected */
        public Tween create() {
            return new Tween();
        }
    };
    private static final Pool.Callback<Tween> poolCallback = new Pool.Callback<Tween>() {
        public void onPool(Tween obj) {
            obj.reset();
        }

        public void onUnPool(Tween obj) {
            obj.reset();
        }
    };
    private static final Map<Class<?>, TweenAccessor<?>> registeredAccessors = new HashMap();
    private static int waypointsLimit = 0;
    private TweenAccessor<Object> accessor;
    private float[] accessorBuffer;
    private int combinedAttrsCnt;
    private TweenEquation equation;
    private boolean isFrom;
    private boolean isRelative;
    private TweenPath path;
    private float[] pathBuffer;
    private final float[] startValues;
    private Object target;
    private Class<?> targetClass;
    private final float[] targetValues;
    private int type;
    private final float[] waypoints;
    private int waypointsCnt;

    static {
        boolean z;
        if (!Tween.class.desiredAssertionStatus()) {
            z = true;
        } else {
            z = false;
        }
        $assertionsDisabled = z;
    }

    public static void setCombinedAttributesLimit(int limit) {
        combinedAttrsLimit = limit;
    }

    public static void setWaypointsLimit(int limit) {
        waypointsLimit = limit;
    }

    public static String getVersion() {
        return "6.3.3";
    }

    public static int getPoolSize() {
        return pool.size();
    }

    public static void ensurePoolCapacity(int minCapacity) {
        pool.ensureCapacity(minCapacity);
    }

    public static void registerAccessor(Class<?> someClass, TweenAccessor<?> defaultAccessor) {
        registeredAccessors.put(someClass, defaultAccessor);
    }

    public static TweenAccessor<?> getRegisteredAccessor(Class<?> someClass) {
        return registeredAccessors.get(someClass);
    }

    /* renamed from: to */
    public static Tween m0to(Object target2, int tweenType, float duration) {
        Tween tween = pool.get();
        tween.setup(target2, tweenType, duration);
        tween.ease(Quad.INOUT);
        tween.path(TweenPaths.catmullRom);
        return tween;
    }

    public static Tween from(Object target2, int tweenType, float duration) {
        Tween tween = pool.get();
        tween.setup(target2, tweenType, duration);
        tween.ease(Quad.INOUT);
        tween.path(TweenPaths.catmullRom);
        tween.isFrom = true;
        return tween;
    }

    public static Tween set(Object target2, int tweenType) {
        Tween tween = pool.get();
        tween.setup(target2, tweenType, 0.0f);
        tween.ease(Quad.INOUT);
        return tween;
    }

    public static Tween call(TweenCallback callback) {
        Tween tween = pool.get();
        tween.setup((Object) null, -1, 0.0f);
        tween.setCallback(callback);
        tween.setCallbackTriggers(2);
        return tween;
    }

    public static Tween mark() {
        Tween tween = pool.get();
        tween.setup((Object) null, -1, 0.0f);
        return tween;
    }

    private Tween() {
        this.startValues = new float[combinedAttrsLimit];
        this.targetValues = new float[combinedAttrsLimit];
        this.waypoints = new float[(waypointsLimit * combinedAttrsLimit)];
        this.accessorBuffer = new float[combinedAttrsLimit];
        this.pathBuffer = new float[((waypointsLimit + 2) * combinedAttrsLimit)];
        reset();
    }

    /* access modifiers changed from: protected */
    public void reset() {
        super.reset();
        this.target = null;
        this.targetClass = null;
        this.accessor = null;
        this.type = -1;
        this.equation = null;
        this.path = null;
        this.isRelative = $assertionsDisabled;
        this.isFrom = $assertionsDisabled;
        this.waypointsCnt = 0;
        this.combinedAttrsCnt = 0;
        if (this.accessorBuffer.length != combinedAttrsLimit) {
            this.accessorBuffer = new float[combinedAttrsLimit];
        }
        if (this.pathBuffer.length != (waypointsLimit + 2) * combinedAttrsLimit) {
            this.pathBuffer = new float[((waypointsLimit + 2) * combinedAttrsLimit)];
        }
    }

    private void setup(Object target2, int tweenType, float duration) {
        if (duration < 0.0f) {
            throw new RuntimeException("Duration can't be negative");
        }
        this.target = target2;
        this.targetClass = target2 != null ? findTargetClass() : null;
        this.type = tweenType;
        this.duration = duration;
    }

    private Class<?> findTargetClass() {
        if (registeredAccessors.containsKey(this.target.getClass())) {
            return this.target.getClass();
        }
        if (this.target instanceof TweenAccessor) {
            return this.target.getClass();
        }
        Class<?> parentClass = this.target.getClass().getSuperclass();
        while (parentClass != null && !registeredAccessors.containsKey(parentClass)) {
            parentClass = parentClass.getSuperclass();
        }
        return parentClass;
    }

    public Tween ease(TweenEquation easeEquation) {
        this.equation = easeEquation;
        return this;
    }

    public Tween cast(Class<?> targetClass2) {
        if (isStarted()) {
            throw new RuntimeException("You can't cast the target of a tween once it is started");
        }
        this.targetClass = targetClass2;
        return this;
    }

    public Tween target(float targetValue) {
        this.targetValues[0] = targetValue;
        return this;
    }

    public Tween target(float targetValue1, float targetValue2) {
        this.targetValues[0] = targetValue1;
        this.targetValues[1] = targetValue2;
        return this;
    }

    public Tween target(float targetValue1, float targetValue2, float targetValue3) {
        this.targetValues[0] = targetValue1;
        this.targetValues[1] = targetValue2;
        this.targetValues[2] = targetValue3;
        return this;
    }

    public Tween target(float... targetValues2) {
        if (targetValues2.length > combinedAttrsLimit) {
            throwCombinedAttrsLimitReached();
        }
        System.arraycopy(targetValues2, 0, this.targetValues, 0, targetValues2.length);
        return this;
    }

    public Tween targetRelative(float targetValue) {
        this.isRelative = true;
        float[] fArr = this.targetValues;
        if (isInitialized()) {
            targetValue += this.startValues[0];
        }
        fArr[0] = targetValue;
        return this;
    }

    public Tween targetRelative(float targetValue1, float targetValue2) {
        this.isRelative = true;
        float[] fArr = this.targetValues;
        if (isInitialized()) {
            targetValue1 += this.startValues[0];
        }
        fArr[0] = targetValue1;
        float[] fArr2 = this.targetValues;
        if (isInitialized()) {
            targetValue2 += this.startValues[1];
        }
        fArr2[1] = targetValue2;
        return this;
    }

    public Tween targetRelative(float targetValue1, float targetValue2, float targetValue3) {
        this.isRelative = true;
        float[] fArr = this.targetValues;
        if (isInitialized()) {
            targetValue1 += this.startValues[0];
        }
        fArr[0] = targetValue1;
        float[] fArr2 = this.targetValues;
        if (isInitialized()) {
            targetValue2 += this.startValues[1];
        }
        fArr2[1] = targetValue2;
        float[] fArr3 = this.targetValues;
        if (isInitialized()) {
            targetValue3 += this.startValues[2];
        }
        fArr3[2] = targetValue3;
        return this;
    }

    public Tween targetRelative(float... targetValues2) {
        if (targetValues2.length > combinedAttrsLimit) {
            throwCombinedAttrsLimitReached();
        }
        for (int i = 0; i < targetValues2.length; i++) {
            this.targetValues[i] = isInitialized() ? targetValues2[i] + this.startValues[i] : targetValues2[i];
        }
        this.isRelative = true;
        return this;
    }

    public Tween waypoint(float targetValue) {
        if (this.waypointsCnt == waypointsLimit) {
            throwWaypointsLimitReached();
        }
        this.waypoints[this.waypointsCnt] = targetValue;
        this.waypointsCnt++;
        return this;
    }

    public Tween waypoint(float targetValue1, float targetValue2) {
        if (this.waypointsCnt == waypointsLimit) {
            throwWaypointsLimitReached();
        }
        this.waypoints[this.waypointsCnt * 2] = targetValue1;
        this.waypoints[(this.waypointsCnt * 2) + 1] = targetValue2;
        this.waypointsCnt++;
        return this;
    }

    public Tween waypoint(float targetValue1, float targetValue2, float targetValue3) {
        if (this.waypointsCnt == waypointsLimit) {
            throwWaypointsLimitReached();
        }
        this.waypoints[this.waypointsCnt * 3] = targetValue1;
        this.waypoints[(this.waypointsCnt * 3) + 1] = targetValue2;
        this.waypoints[(this.waypointsCnt * 3) + 2] = targetValue3;
        this.waypointsCnt++;
        return this;
    }

    public Tween waypoint(float... targetValues2) {
        if (this.waypointsCnt == waypointsLimit) {
            throwWaypointsLimitReached();
        }
        System.arraycopy(targetValues2, 0, this.waypoints, this.waypointsCnt * targetValues2.length, targetValues2.length);
        this.waypointsCnt++;
        return this;
    }

    public Tween path(TweenPath path2) {
        this.path = path2;
        return this;
    }

    public Object getTarget() {
        return this.target;
    }

    public int getType() {
        return this.type;
    }

    public TweenEquation getEasing() {
        return this.equation;
    }

    public float[] getTargetValues() {
        return this.targetValues;
    }

    public int getCombinedAttributesCount() {
        return this.combinedAttrsCnt;
    }

    public TweenAccessor<?> getAccessor() {
        return this.accessor;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Tween build() {
        if (this.target != null) {
            this.accessor = registeredAccessors.get(this.targetClass);
            if (this.accessor == null && (this.target instanceof TweenAccessor)) {
                this.accessor = (TweenAccessor) this.target;
            }
            if (this.accessor != null) {
                this.combinedAttrsCnt = this.accessor.getValues(this.target, this.type, this.accessorBuffer);
                if (this.combinedAttrsCnt > combinedAttrsLimit) {
                    throwCombinedAttrsLimitReached();
                }
            } else {
                throw new RuntimeException("No TweenAccessor was found for the target");
            }
        }
        return this;
    }

    public void free() {
        pool.free(this);
    }

    /* access modifiers changed from: protected */
    public void initializeOverride() {
        float f;
        float f2;
        if (this.target != null) {
            this.accessor.getValues(this.target, this.type, this.startValues);
            for (int i = 0; i < this.combinedAttrsCnt; i++) {
                float[] fArr = this.targetValues;
                float f3 = fArr[i];
                if (this.isRelative) {
                    f = this.startValues[i];
                } else {
                    f = 0.0f;
                }
                fArr[i] = f + f3;
                for (int ii = 0; ii < this.waypointsCnt; ii++) {
                    float[] fArr2 = this.waypoints;
                    int i2 = (this.combinedAttrsCnt * ii) + i;
                    float f4 = fArr2[i2];
                    if (this.isRelative) {
                        f2 = this.startValues[i];
                    } else {
                        f2 = 0.0f;
                    }
                    fArr2[i2] = f2 + f4;
                }
                if (this.isFrom) {
                    float tmp = this.startValues[i];
                    this.startValues[i] = this.targetValues[i];
                    this.targetValues[i] = tmp;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateOverride(int step, int lastStep, boolean isIterationStep, float delta) {
        if (this.target != null && this.equation != null) {
            if (!isIterationStep && step > lastStep) {
                this.accessor.setValues(this.target, this.type, isReverse(lastStep) ? this.startValues : this.targetValues);
            } else if (!isIterationStep && step < lastStep) {
                this.accessor.setValues(this.target, this.type, isReverse(lastStep) ? this.targetValues : this.startValues);
            } else if (!$assertionsDisabled && !isIterationStep) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && getCurrentTime() < 0.0f) {
                throw new AssertionError();
            } else if (!$assertionsDisabled && getCurrentTime() > this.duration) {
                throw new AssertionError();
            } else if (this.duration < 1.0E-11f && delta > -1.0E-11f) {
                this.accessor.setValues(this.target, this.type, isReverse(step) ? this.targetValues : this.startValues);
            } else if (this.duration >= 1.0E-11f || delta >= 1.0E-11f) {
                float t = this.equation.compute((isReverse(step) ? this.duration - getCurrentTime() : getCurrentTime()) / this.duration);
                if (this.waypointsCnt == 0 || this.path == null) {
                    for (int i = 0; i < this.combinedAttrsCnt; i++) {
                        this.accessorBuffer[i] = this.startValues[i] + ((this.targetValues[i] - this.startValues[i]) * t);
                    }
                } else {
                    for (int i2 = 0; i2 < this.combinedAttrsCnt; i2++) {
                        this.pathBuffer[0] = this.startValues[i2];
                        this.pathBuffer[this.waypointsCnt + 1] = this.targetValues[i2];
                        for (int ii = 0; ii < this.waypointsCnt; ii++) {
                            this.pathBuffer[ii + 1] = this.waypoints[(this.combinedAttrsCnt * ii) + i2];
                        }
                        this.accessorBuffer[i2] = this.path.compute(t, this.pathBuffer, this.waypointsCnt + 2);
                    }
                }
                this.accessor.setValues(this.target, this.type, this.accessorBuffer);
            } else {
                this.accessor.setValues(this.target, this.type, isReverse(step) ? this.startValues : this.targetValues);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void forceStartValues() {
        if (this.target != null) {
            this.accessor.setValues(this.target, this.type, this.startValues);
        }
    }

    /* access modifiers changed from: protected */
    public void forceEndValues() {
        if (this.target != null) {
            this.accessor.setValues(this.target, this.type, this.targetValues);
        }
    }

    /* access modifiers changed from: protected */
    public boolean containsTarget(Object target2) {
        if (this.target == target2) {
            return true;
        }
        return $assertionsDisabled;
    }

    /* access modifiers changed from: protected */
    public boolean containsTarget(Object target2, int tweenType) {
        if (this.target == target2 && this.type == tweenType) {
            return true;
        }
        return $assertionsDisabled;
    }

    private void throwCombinedAttrsLimitReached() {
        throw new RuntimeException("You cannot combine more than " + combinedAttrsLimit + " " + "attributes in a tween. You can raise this limit with " + "Tween.setCombinedAttributesLimit(), which should be called once " + "in application initialization code.");
    }

    private void throwWaypointsLimitReached() {
        throw new RuntimeException("You cannot add more than " + waypointsLimit + " " + "waypoints to a tween. You can raise this limit with " + "Tween.setWaypointsLimit(), which should be called once in " + "application initialization code.");
    }
}
