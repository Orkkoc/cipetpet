package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Expo extends TweenEquation {

    /* renamed from: IN */
    public static final Expo f6IN = new Expo() {
        public final float compute(float t) {
            if (t == 0.0f) {
                return 0.0f;
            }
            return (float) Math.pow(2.0d, (double) (10.0f * (t - 1.0f)));
        }

        public String toString() {
            return "Expo.IN";
        }
    };
    public static final Expo INOUT = new Expo() {
        public final float compute(float t) {
            if (t == 0.0f) {
                return 0.0f;
            }
            if (t == 1.0f) {
                return 1.0f;
            }
            float t2 = t * 2.0f;
            if (t2 < 1.0f) {
                return ((float) Math.pow(2.0d, (double) (10.0f * (t2 - 1.0f)))) * 0.5f;
            }
            return ((-((float) Math.pow(2.0d, (double) (-10.0f * (t2 - 1.0f))))) + 2.0f) * 0.5f;
        }

        public String toString() {
            return "Expo.INOUT";
        }
    };
    public static final Expo OUT = new Expo() {
        public final float compute(float t) {
            if (t == 1.0f) {
                return 1.0f;
            }
            return 1.0f + (-((float) Math.pow(2.0d, (double) (-10.0f * t))));
        }

        public String toString() {
            return "Expo.OUT";
        }
    };
}
