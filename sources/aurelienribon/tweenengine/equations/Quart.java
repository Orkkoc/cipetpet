package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Quart extends TweenEquation {

    /* renamed from: IN */
    public static final Quart f8IN = new Quart() {
        public final float compute(float t) {
            return t * t * t * t;
        }

        public String toString() {
            return "Quart.IN";
        }
    };
    public static final Quart INOUT = new Quart() {
        public final float compute(float t) {
            float t2 = t * 2.0f;
            if (t2 < 1.0f) {
                return 0.5f * t2 * t2 * t2 * t2;
            }
            float t3 = t2 - 2.0f;
            return -0.5f * ((((t3 * t3) * t3) * t3) - 2.0f);
        }

        public String toString() {
            return "Quart.INOUT";
        }
    };
    public static final Quart OUT = new Quart() {
        public final float compute(float t) {
            float t2 = t - 1.0f;
            return -((((t2 * t2) * t2) * t2) - 1.0f);
        }

        public String toString() {
            return "Quart.OUT";
        }
    };
}
