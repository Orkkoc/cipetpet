package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Back extends TweenEquation {

    /* renamed from: IN */
    public static final Back f0IN = new Back() {
        public final float compute(float t) {
            float s = this.param_s;
            return t * t * (((1.0f + s) * t) - s);
        }

        public String toString() {
            return "Back.IN";
        }
    };
    public static final Back INOUT = new Back() {
        public final float compute(float t) {
            float s = this.param_s;
            float t2 = t * 2.0f;
            if (t2 < 1.0f) {
                float s2 = s * 1.525f;
                return t2 * t2 * (((1.0f + s2) * t2) - s2) * 0.5f;
            }
            float t3 = t2 - 2.0f;
            float s3 = s * 1.525f;
            return ((t3 * t3 * (((1.0f + s3) * t3) + s3)) + 2.0f) * 0.5f;
        }

        public String toString() {
            return "Back.INOUT";
        }
    };
    public static final Back OUT = new Back() {
        public final float compute(float t) {
            float s = this.param_s;
            float t2 = t - 1.0f;
            return (t2 * t2 * (((s + 1.0f) * t2) + s)) + 1.0f;
        }

        public String toString() {
            return "Back.OUT";
        }
    };
    protected float param_s = 1.70158f;

    /* renamed from: s */
    public Back mo107s(float s) {
        this.param_s = s;
        return this;
    }
}
