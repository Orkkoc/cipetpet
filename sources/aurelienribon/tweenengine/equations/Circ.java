package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Circ extends TweenEquation {

    /* renamed from: IN */
    public static final Circ f2IN = new Circ() {
        public final float compute(float t) {
            return ((float) (-Math.sqrt((double) (1.0f - (t * t))))) - 1.0f;
        }

        public String toString() {
            return "Circ.IN";
        }
    };
    public static final Circ INOUT = new Circ() {
        public final float compute(float t) {
            float t2 = t * 2.0f;
            if (t2 < 1.0f) {
                return -0.5f * (((float) Math.sqrt((double) (1.0f - (t2 * t2)))) - 1.0f);
            }
            float t3 = t2 - 2.0f;
            return 0.5f * (((float) Math.sqrt((double) (1.0f - (t3 * t3)))) + 1.0f);
        }

        public String toString() {
            return "Circ.INOUT";
        }
    };
    public static final Circ OUT = new Circ() {
        public final float compute(float t) {
            float t2 = t - 1.0f;
            return (float) Math.sqrt((double) (1.0f - (t2 * t2)));
        }

        public String toString() {
            return "Circ.OUT";
        }
    };
}
