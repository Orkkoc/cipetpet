package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Quad extends TweenEquation {

    /* renamed from: IN */
    public static final Quad f7IN = new Quad() {
        public final float compute(float t) {
            return t * t;
        }

        public String toString() {
            return "Quad.IN";
        }
    };
    public static final Quad INOUT = new Quad() {
        public final float compute(float t) {
            float t2 = t * 2.0f;
            if (t2 < 1.0f) {
                return 0.5f * t2 * t2;
            }
            float t3 = t2 - 1.0f;
            return -0.5f * (((t3 - 2.0f) * t3) - 1.0f);
        }

        public String toString() {
            return "Quad.INOUT";
        }
    };
    public static final Quad OUT = new Quad() {
        public final float compute(float t) {
            return (-t) * (t - 2.0f);
        }

        public String toString() {
            return "Quad.OUT";
        }
    };
}
