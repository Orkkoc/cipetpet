package aurelienribon.tweenengine;

public abstract class BaseTween<T> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseTween.class.desiredAssertionStatus());
    private TweenCallback callback;
    private int callbackTriggers;
    private float currentTime;
    protected float delay;
    private float deltaTime;
    protected float duration;
    boolean isAutoRemoveEnabled;
    boolean isAutoStartEnabled;
    private boolean isFinished;
    private boolean isInitialized;
    private boolean isIterationStep;
    private boolean isKilled;
    private boolean isPaused;
    private boolean isStarted;
    private boolean isYoyo;
    private int repeatCnt;
    private float repeatDelay;
    private int step;
    private Object userData;

    /* access modifiers changed from: protected */
    public abstract boolean containsTarget(Object obj);

    /* access modifiers changed from: protected */
    public abstract boolean containsTarget(Object obj, int i);

    /* access modifiers changed from: protected */
    public abstract void forceEndValues();

    /* access modifiers changed from: protected */
    public abstract void forceStartValues();

    /* access modifiers changed from: protected */
    public void reset() {
        this.step = -2;
        this.repeatCnt = 0;
        this.isYoyo = false;
        this.isIterationStep = false;
        this.deltaTime = 0.0f;
        this.currentTime = 0.0f;
        this.repeatDelay = 0.0f;
        this.duration = 0.0f;
        this.delay = 0.0f;
        this.isPaused = false;
        this.isKilled = false;
        this.isFinished = false;
        this.isInitialized = false;
        this.isStarted = false;
        this.callback = null;
        this.callbackTriggers = 8;
        this.userData = null;
        this.isAutoStartEnabled = true;
        this.isAutoRemoveEnabled = true;
    }

    public T build() {
        return this;
    }

    public T start() {
        build();
        this.currentTime = 0.0f;
        this.isStarted = true;
        return this;
    }

    public T start(TweenManager manager) {
        manager.add(this);
        return this;
    }

    public T delay(float delay2) {
        this.delay += delay2;
        return this;
    }

    public void kill() {
        this.isKilled = true;
    }

    public void free() {
    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        this.isPaused = false;
    }

    public T repeat(int count, float delay2) {
        if (this.isStarted) {
            throw new RuntimeException("You can't change the repetitions of a tween or timeline once it is started");
        }
        this.repeatCnt = count;
        if (delay2 < 0.0f) {
            delay2 = 0.0f;
        }
        this.repeatDelay = delay2;
        this.isYoyo = false;
        return this;
    }

    public T repeatYoyo(int count, float delay2) {
        if (this.isStarted) {
            throw new RuntimeException("You can't change the repetitions of a tween or timeline once it is started");
        }
        this.repeatCnt = count;
        if (delay2 < 0.0f) {
            delay2 = 0.0f;
        }
        this.repeatDelay = delay2;
        this.isYoyo = true;
        return this;
    }

    public T setCallback(TweenCallback callback2) {
        this.callback = callback2;
        return this;
    }

    public T setCallbackTriggers(int flags) {
        this.callbackTriggers = flags;
        return this;
    }

    public T setUserData(Object data) {
        this.userData = data;
        return this;
    }

    public float getDelay() {
        return this.delay;
    }

    public float getDuration() {
        return this.duration;
    }

    public int getRepeatCount() {
        return this.repeatCnt;
    }

    public float getRepeatDelay() {
        return this.repeatDelay;
    }

    public float getFullDuration() {
        if (this.repeatCnt < 0) {
            return -1.0f;
        }
        return this.delay + this.duration + ((this.repeatDelay + this.duration) * ((float) this.repeatCnt));
    }

    public Object getUserData() {
        return this.userData;
    }

    public int getStep() {
        return this.step;
    }

    public float getCurrentTime() {
        return this.currentTime;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public boolean isInitialized() {
        return this.isInitialized;
    }

    public boolean isFinished() {
        return this.isFinished || this.isKilled;
    }

    public boolean isYoyo() {
        return this.isYoyo;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    /* access modifiers changed from: protected */
    public void initializeOverride() {
    }

    /* access modifiers changed from: protected */
    public void updateOverride(int step2, int lastStep, boolean isIterationStep2, float delta) {
    }

    /* access modifiers changed from: protected */
    public void forceToStart() {
        this.currentTime = -this.delay;
        this.step = -1;
        this.isIterationStep = false;
        if (isReverse(0)) {
            forceEndValues();
        } else {
            forceStartValues();
        }
    }

    /* access modifiers changed from: protected */
    public void forceToEnd(float time) {
        this.currentTime = time - getFullDuration();
        this.step = (this.repeatCnt * 2) + 1;
        this.isIterationStep = false;
        if (isReverse(this.repeatCnt * 2)) {
            forceStartValues();
        } else {
            forceEndValues();
        }
    }

    /* access modifiers changed from: protected */
    public void callCallback(int type) {
        if (this.callback != null && (this.callbackTriggers & type) > 0) {
            this.callback.onEvent(type, this);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isReverse(int step2) {
        return this.isYoyo && Math.abs(step2 % 4) == 2;
    }

    /* access modifiers changed from: protected */
    public boolean isValid(int step2) {
        return (step2 >= 0 && step2 <= this.repeatCnt * 2) || this.repeatCnt < 0;
    }

    /* access modifiers changed from: protected */
    public void killTarget(Object target) {
        if (containsTarget(target)) {
            kill();
        }
    }

    /* access modifiers changed from: protected */
    public void killTarget(Object target, int tweenType) {
        if (containsTarget(target, tweenType)) {
            kill();
        }
    }

    public void update(float delta) {
        if (this.isStarted && !this.isPaused && !this.isKilled) {
            this.deltaTime = delta;
            if (!this.isInitialized) {
                initialize();
            }
            if (this.isInitialized) {
                testRelaunch();
                updateStep();
                testCompletion();
            }
            this.currentTime += this.deltaTime;
            this.deltaTime = 0.0f;
        }
    }

    private void initialize() {
        if (this.currentTime + this.deltaTime >= this.delay) {
            initializeOverride();
            this.isInitialized = true;
            this.isIterationStep = true;
            this.step = 0;
            this.deltaTime -= this.delay - this.currentTime;
            this.currentTime = 0.0f;
            callCallback(1);
            callCallback(2);
        }
    }

    private void testRelaunch() {
        if (this.isIterationStep || this.repeatCnt < 0 || this.step >= 0 || this.currentTime + this.deltaTime < 0.0f) {
            if (!this.isIterationStep && this.repeatCnt >= 0 && this.step > this.repeatCnt * 2 && this.currentTime + this.deltaTime < 0.0f) {
                if ($assertionsDisabled || this.step == (this.repeatCnt * 2) + 1) {
                    this.isIterationStep = true;
                    this.step = this.repeatCnt * 2;
                    float delta = 0.0f - this.currentTime;
                    this.deltaTime -= delta;
                    this.currentTime = this.duration;
                    callCallback(16);
                    callCallback(32);
                    updateOverride(this.step, this.step + 1, this.isIterationStep, delta);
                    return;
                }
                throw new AssertionError();
            }
        } else if ($assertionsDisabled || this.step == -1) {
            this.isIterationStep = true;
            this.step = 0;
            float delta2 = 0.0f - this.currentTime;
            this.deltaTime -= delta2;
            this.currentTime = 0.0f;
            callCallback(1);
            callCallback(2);
            updateOverride(this.step, this.step - 1, this.isIterationStep, delta2);
        } else {
            throw new AssertionError();
        }
    }

    private void updateStep() {
        while (isValid(this.step)) {
            if (!this.isIterationStep && this.currentTime + this.deltaTime <= 0.0f) {
                this.isIterationStep = true;
                this.step--;
                float delta = 0.0f - this.currentTime;
                this.deltaTime -= delta;
                this.currentTime = this.duration;
                if (isReverse(this.step)) {
                    forceStartValues();
                } else {
                    forceEndValues();
                }
                callCallback(32);
                updateOverride(this.step, this.step + 1, this.isIterationStep, delta);
            } else if (!this.isIterationStep && this.currentTime + this.deltaTime >= this.repeatDelay) {
                this.isIterationStep = true;
                this.step++;
                float delta2 = this.repeatDelay - this.currentTime;
                this.deltaTime -= delta2;
                this.currentTime = 0.0f;
                if (isReverse(this.step)) {
                    forceEndValues();
                } else {
                    forceStartValues();
                }
                callCallback(2);
                updateOverride(this.step, this.step - 1, this.isIterationStep, delta2);
            } else if (this.isIterationStep && this.currentTime + this.deltaTime < 0.0f) {
                this.isIterationStep = false;
                this.step--;
                float delta3 = 0.0f - this.currentTime;
                this.deltaTime -= delta3;
                this.currentTime = 0.0f;
                updateOverride(this.step, this.step + 1, this.isIterationStep, delta3);
                callCallback(64);
                if (this.step >= 0 || this.repeatCnt < 0) {
                    this.currentTime = this.repeatDelay;
                } else {
                    callCallback(128);
                }
            } else if (this.isIterationStep && this.currentTime + this.deltaTime > this.duration) {
                this.isIterationStep = false;
                this.step++;
                float delta4 = this.duration - this.currentTime;
                this.deltaTime -= delta4;
                this.currentTime = this.duration;
                updateOverride(this.step, this.step - 1, this.isIterationStep, delta4);
                callCallback(4);
                if (this.step > this.repeatCnt * 2 && this.repeatCnt >= 0) {
                    callCallback(8);
                }
                this.currentTime = 0.0f;
            } else if (this.isIterationStep) {
                float delta5 = this.deltaTime;
                this.deltaTime -= delta5;
                this.currentTime += delta5;
                updateOverride(this.step, this.step, this.isIterationStep, delta5);
                return;
            } else {
                float delta6 = this.deltaTime;
                this.deltaTime -= delta6;
                this.currentTime += delta6;
                return;
            }
        }
    }

    private void testCompletion() {
        this.isFinished = this.repeatCnt >= 0 && (this.step > this.repeatCnt * 2 || this.step < 0);
    }
}
