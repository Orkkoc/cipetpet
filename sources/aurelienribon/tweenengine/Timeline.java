package aurelienribon.tweenengine;

import aurelienribon.tweenengine.Pool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Timeline extends BaseTween<Timeline> {
    static final /* synthetic */ boolean $assertionsDisabled = (!Timeline.class.desiredAssertionStatus());
    static final Pool<Timeline> pool = new Pool<Timeline>(10, poolCallback) {
        /* access modifiers changed from: protected */
        public Timeline create() {
            return new Timeline();
        }
    };
    private static final Pool.Callback<Timeline> poolCallback = new Pool.Callback<Timeline>() {
        public void onPool(Timeline obj) {
            obj.reset();
        }

        public void onUnPool(Timeline obj) {
            obj.reset();
        }
    };
    private final List<BaseTween<?>> children;
    private Timeline current;
    private boolean isBuilt;
    private Modes mode;
    private Timeline parent;

    private enum Modes {
        SEQUENCE,
        PARALLEL
    }

    public static int getPoolSize() {
        return pool.size();
    }

    public static void ensurePoolCapacity(int minCapacity) {
        pool.ensureCapacity(minCapacity);
    }

    public static Timeline createSequence() {
        Timeline tl = pool.get();
        tl.setup(Modes.SEQUENCE);
        return tl;
    }

    public static Timeline createParallel() {
        Timeline tl = pool.get();
        tl.setup(Modes.PARALLEL);
        return tl;
    }

    private Timeline() {
        this.children = new ArrayList(10);
        reset();
    }

    /* access modifiers changed from: protected */
    public void reset() {
        super.reset();
        this.children.clear();
        this.parent = null;
        this.current = null;
        this.isBuilt = false;
    }

    private void setup(Modes mode2) {
        this.mode = mode2;
        this.current = this;
    }

    public Timeline push(Tween tween) {
        if (this.isBuilt) {
            throw new RuntimeException("You can't push anything to a timeline once it is started");
        }
        this.current.children.add(tween);
        return this;
    }

    public Timeline push(Timeline timeline) {
        if (this.isBuilt) {
            throw new RuntimeException("You can't push anything to a timeline once it is started");
        } else if (timeline.current != timeline) {
            throw new RuntimeException("You forgot to call a few 'end()' statements in your pushed timeline");
        } else {
            timeline.parent = this.current;
            this.current.children.add(timeline);
            return this;
        }
    }

    public Timeline pushPause(float time) {
        if (this.isBuilt) {
            throw new RuntimeException("You can't push anything to a timeline once it is started");
        }
        this.current.children.add(Tween.mark().delay(time));
        return this;
    }

    public Timeline beginSequence() {
        if (this.isBuilt) {
            throw new RuntimeException("You can't push anything to a timeline once it is started");
        }
        Timeline tl = pool.get();
        tl.parent = this.current;
        tl.mode = Modes.SEQUENCE;
        this.current.children.add(tl);
        this.current = tl;
        return this;
    }

    public Timeline beginParallel() {
        if (this.isBuilt) {
            throw new RuntimeException("You can't push anything to a timeline once it is started");
        }
        Timeline tl = pool.get();
        tl.parent = this.current;
        tl.mode = Modes.PARALLEL;
        this.current.children.add(tl);
        this.current = tl;
        return this;
    }

    public Timeline end() {
        if (this.isBuilt) {
            throw new RuntimeException("You can't push anything to a timeline once it is started");
        } else if (this.current == this) {
            throw new RuntimeException("Nothing to end...");
        } else {
            this.current = this.current.parent;
            return this;
        }
    }

    public List<BaseTween<?>> getChildren() {
        if (this.isBuilt) {
            return Collections.unmodifiableList(this.current.children);
        }
        return this.current.children;
    }

    public Timeline build() {
        if (!this.isBuilt) {
            this.duration = 0.0f;
            for (int i = 0; i < this.children.size(); i++) {
                BaseTween<?> obj = this.children.get(i);
                if (obj.getRepeatCount() < 0) {
                    throw new RuntimeException("You can't push an object with infinite repetitions in a timeline");
                }
                obj.build();
                switch (this.mode) {
                    case SEQUENCE:
                        float tDelay = this.duration;
                        this.duration += obj.getFullDuration();
                        obj.delay += tDelay;
                        break;
                    case PARALLEL:
                        this.duration = Math.max(this.duration, obj.getFullDuration());
                        break;
                }
            }
            this.isBuilt = true;
        }
        return this;
    }

    public Timeline start() {
        super.start();
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).start();
        }
        return this;
    }

    public void free() {
        for (int i = this.children.size() - 1; i >= 0; i--) {
            this.children.remove(i).free();
        }
        pool.free(this);
    }

    /* access modifiers changed from: protected */
    public void updateOverride(int step, int lastStep, boolean isIterationStep, float delta) {
        float dt;
        if (isIterationStep || step <= lastStep) {
            if (isIterationStep || step >= lastStep) {
                if (!$assertionsDisabled && !isIterationStep) {
                    throw new AssertionError();
                } else if (step > lastStep) {
                    if (isReverse(step)) {
                        forceEndValues();
                        int n = this.children.size();
                        for (int i = 0; i < n; i++) {
                            this.children.get(i).update(delta);
                        }
                        return;
                    }
                    forceStartValues();
                    int n2 = this.children.size();
                    for (int i2 = 0; i2 < n2; i2++) {
                        this.children.get(i2).update(delta);
                    }
                } else if (step >= lastStep) {
                    if (isReverse(step)) {
                        dt = -delta;
                    } else {
                        dt = delta;
                    }
                    if (delta >= 0.0f) {
                        int n3 = this.children.size();
                        for (int i3 = 0; i3 < n3; i3++) {
                            this.children.get(i3).update(dt);
                        }
                        return;
                    }
                    for (int i4 = this.children.size() - 1; i4 >= 0; i4--) {
                        this.children.get(i4).update(dt);
                    }
                } else if (isReverse(step)) {
                    forceStartValues();
                    for (int i5 = this.children.size() - 1; i5 >= 0; i5--) {
                        this.children.get(i5).update(delta);
                    }
                } else {
                    forceEndValues();
                    for (int i6 = this.children.size() - 1; i6 >= 0; i6--) {
                        this.children.get(i6).update(delta);
                    }
                }
            } else if ($assertionsDisabled || delta <= 0.0f) {
                float dt2 = isReverse(lastStep) ? (-delta) - 1.0f : delta + 1.0f;
                for (int i7 = this.children.size() - 1; i7 >= 0; i7--) {
                    this.children.get(i7).update(dt2);
                }
            } else {
                throw new AssertionError();
            }
        } else if ($assertionsDisabled || delta >= 0.0f) {
            float dt3 = isReverse(lastStep) ? (-delta) - 1.0f : delta + 1.0f;
            int n4 = this.children.size();
            for (int i8 = 0; i8 < n4; i8++) {
                this.children.get(i8).update(dt3);
            }
        } else {
            throw new AssertionError();
        }
    }

    /* access modifiers changed from: protected */
    public void forceStartValues() {
        for (int i = this.children.size() - 1; i >= 0; i--) {
            this.children.get(i).forceToStart();
        }
    }

    /* access modifiers changed from: protected */
    public void forceEndValues() {
        int n = this.children.size();
        for (int i = 0; i < n; i++) {
            this.children.get(i).forceToEnd(this.duration);
        }
    }

    /* access modifiers changed from: protected */
    public boolean containsTarget(Object target) {
        int n = this.children.size();
        for (int i = 0; i < n; i++) {
            if (this.children.get(i).containsTarget(target)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean containsTarget(Object target, int tweenType) {
        int n = this.children.size();
        for (int i = 0; i < n; i++) {
            if (this.children.get(i).containsTarget(target, tweenType)) {
                return true;
            }
        }
        return false;
    }
}
