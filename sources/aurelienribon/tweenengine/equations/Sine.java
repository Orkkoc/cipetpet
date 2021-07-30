package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Sine extends TweenEquation {

    /* renamed from: IN */
    public static final Sine f10IN = new Sine() {
        public final float compute(float t) {
            return ((float) (-Math.cos((double) (1.5707964f * t)))) + 1.0f;
        }

        public String toString() {
            return "Sine.IN";
        }
    };
    public static final Sine INOUT = new Sine() {
        public final float compute(float t) {
            return -0.5f * (((float) Math.cos((double) (3.1415927f * t))) - 1.0f);
        }

        public String toString() {
            return "Sine.INOUT";
        }
    };
    public static final Sine OUT = new Sine() {
        public final float compute(float t) {
            return (float) Math.sin((double) (1.5707964f * t));
        }

        public String toString() {
            return "Sine.OUT";
        }
    };

    /* renamed from: PI */
    private static final float f11PI = 3.1415927f;
}
