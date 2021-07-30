package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Cubic extends TweenEquation {

    /* renamed from: IN */
    public static final Cubic f3IN = new Cubic() {
        public final float compute(float t) {
            return t * t * t;
        }

        public String toString() {
            return "Cubic.IN";
        }
    };
    public static final Cubic INOUT = new Cubic() {
        public final float compute(float t) {
            float t2 = t * 2.0f;
            if (t2 < 1.0f) {
                return 0.5f * t2 * t2 * t2;
            }
            float t3 = t2 - 2.0f;
            return ((t3 * t3 * t3) + 2.0f) * 0.5f;
        }

        public String toString() {
            return "Cubic.INOUT";
        }
    };
    public static final Cubic OUT = new Cubic() {
        public final float compute(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * t2) + 1.0f;
        }

        public String toString() {
            return "Cubic.OUT";
        }
    };
}
