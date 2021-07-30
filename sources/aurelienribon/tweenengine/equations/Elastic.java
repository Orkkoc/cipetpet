package aurelienribon.tweenengine.equations;

import aurelienribon.tweenengine.TweenEquation;

public abstract class Elastic extends TweenEquation {

    /* renamed from: IN */
    public static final Elastic f4IN = new Elastic() {
        public final float compute(float t) {
            float s;
            float a = this.param_a;
            float p = this.param_p;
            if (t == 0.0f) {
                return 0.0f;
            }
            if (t == 1.0f) {
                return 1.0f;
            }
            if (!this.setP) {
                p = 0.3f;
            }
            if (!this.setA || a < 1.0f) {
                a = 1.0f;
                s = p / 4.0f;
            } else {
                s = (p / 6.2831855f) * ((float) Math.asin((double) (1.0f / a)));
            }
            float t2 = t - 1.0f;
            return -(((float) Math.pow(2.0d, (double) (10.0f * t2))) * a * ((float) Math.sin((double) (((t2 - s) * 6.2831855f) / p))));
        }

        public String toString() {
            return "Elastic.IN";
        }
    };
    public static final Elastic INOUT = new Elastic() {
        public final float compute(float t) {
            float s;
            float a = this.param_a;
            float p = this.param_p;
            if (t == 0.0f) {
                return 0.0f;
            }
            float t2 = t * 2.0f;
            if (t2 == 2.0f) {
                return 1.0f;
            }
            if (!this.setP) {
                p = 0.45000002f;
            }
            if (!this.setA || a < 1.0f) {
                a = 1.0f;
                s = p / 4.0f;
            } else {
                s = (p / 6.2831855f) * ((float) Math.asin((double) (1.0f / a)));
            }
            if (t2 < 1.0f) {
                float t3 = t2 - 1.0f;
                return -0.5f * ((float) Math.pow(2.0d, (double) (10.0f * t3))) * a * ((float) Math.sin((double) (((t3 - s) * 6.2831855f) / p)));
            }
            float t4 = t2 - 1.0f;
            return (((float) Math.pow(2.0d, (double) (-10.0f * t4))) * a * ((float) Math.sin((double) (((t4 - s) * 6.2831855f) / p))) * 0.5f) + 1.0f;
        }

        public String toString() {
            return "Elastic.INOUT";
        }
    };
    public static final Elastic OUT = new Elastic() {
        public final float compute(float t) {
            float s;
            float a = this.param_a;
            float p = this.param_p;
            if (t == 0.0f) {
                return 0.0f;
            }
            if (t == 1.0f) {
                return 1.0f;
            }
            if (!this.setP) {
                p = 0.3f;
            }
            if (!this.setA || a < 1.0f) {
                a = 1.0f;
                s = p / 4.0f;
            } else {
                s = (p / 6.2831855f) * ((float) Math.asin((double) (1.0f / a)));
            }
            return (((float) Math.pow(2.0d, (double) (-10.0f * t))) * a * ((float) Math.sin((double) (((t - s) * 6.2831855f) / p)))) + 1.0f;
        }

        public String toString() {
            return "Elastic.OUT";
        }
    };

    /* renamed from: PI */
    private static final float f5PI = 3.1415927f;
    protected float param_a;
    protected float param_p;
    protected boolean setA = false;
    protected boolean setP = false;

    /* renamed from: a */
    public Elastic mo120a(float a) {
        this.param_a = a;
        this.setA = true;
        return this;
    }

    /* renamed from: p */
    public Elastic mo121p(float p) {
        this.param_p = p;
        this.setP = true;
        return this;
    }
}
