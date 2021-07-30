package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Bounce extends TweenEquation {

    /* renamed from: IN */
    public static final Bounce f1IN = new Bounce() {
        public final float compute(float t) {
            return 1.0f - OUT.compute(1.0f - t);
        }

        public String toString() {
            return "Bounce.IN";
        }
    };
    public static final Bounce INOUT = new Bounce() {
        public final float compute(float t) {
            if (t < 0.5f) {
                return f1IN.compute(2.0f * t) * 0.5f;
            }
            return (OUT.compute((2.0f * t) - 1.0f) * 0.5f) + 0.5f;
        }

        public String toString() {
            return "Bounce.INOUT";
        }
    };
    public static final Bounce OUT = new Bounce() {
        public final float compute(float t) {
            if (((double) t) < 0.36363636363636365d) {
                return 7.5625f * t * t;
            }
            if (((double) t) < 0.7272727272727273d) {
                float t2 = t - 0.54545456f;
                return (7.5625f * t2 * t2) + 0.75f;
            } else if (((double) t) < 0.9090909090909091d) {
                float t3 = t - 0.8181818f;
                return (7.5625f * t3 * t3) + 0.9375f;
            } else {
                float t4 = t - 0.95454544f;
                return (7.5625f * t4 * t4) + 0.984375f;
            }
        }

        public String toString() {
            return "Bounce.OUT";
        }
    };
}
