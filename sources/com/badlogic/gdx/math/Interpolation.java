package com.badlogic.gdx.math;

public abstract class Interpolation {
    public static final Interpolation bounce = new Bounce(4);
    public static final Interpolation bounceIn = new BounceIn(4);
    public static final Interpolation bounceOut = new BounceOut(4);
    public static final Interpolation circle = new Interpolation() {
        public float apply(float a) {
            if (a <= 0.5f) {
                float a2 = a * 2.0f;
                return (1.0f - ((float) Math.sqrt((double) (1.0f - (a2 * a2))))) / 2.0f;
            }
            float a3 = (a - 1.0f) * 2.0f;
            return (((float) Math.sqrt((double) (1.0f - (a3 * a3)))) + 1.0f) / 2.0f;
        }
    };
    public static final Interpolation circleIn = new Interpolation() {
        public float apply(float a) {
            return 1.0f - ((float) Math.sqrt((double) (1.0f - (a * a))));
        }
    };
    public static final Interpolation circleOut = new Interpolation() {
        public float apply(float a) {
            float a2 = a - 1.0f;
            return (float) Math.sqrt((double) (1.0f - (a2 * a2)));
        }
    };
    public static final Elastic elastic = new Elastic(2.0f, 10.0f);
    public static final Elastic elasticIn = new ElasticIn(2.0f, 10.0f);
    public static final Elastic elasticOut = new ElasticOut(2.0f, 10.0f);
    public static final Interpolation exp10 = new Exp(2.0f, 10.0f);
    public static final Interpolation exp10In = new ExpIn(2.0f, 10.0f);
    public static final Interpolation exp10Out = new ExpOut(2.0f, 10.0f);
    public static final Interpolation exp5 = new Exp(2.0f, 5.0f);
    public static final Interpolation exp5In = new ExpIn(2.0f, 5.0f);
    public static final Interpolation exp5Out = new ExpOut(2.0f, 5.0f);
    public static final Interpolation fade = new Interpolation() {
        public float apply(float a) {
            return MathUtils.clamp(a * a * a * ((((6.0f * a) - 15.0f) * a) + 10.0f), 0.0f, 1.0f);
        }
    };
    public static final Interpolation linear = new Interpolation() {
        public float apply(float a) {
            return a;
        }
    };
    public static final Pow pow2 = new Pow(2);
    public static final PowIn pow2In = new PowIn(2);
    public static final PowOut pow2Out = new PowOut(2);
    public static final Pow pow3 = new Pow(3);
    public static final PowIn pow3In = new PowIn(3);
    public static final PowOut pow3Out = new PowOut(3);
    public static final Pow pow4 = new Pow(4);
    public static final PowIn pow4In = new PowIn(4);
    public static final PowOut pow4Out = new PowOut(4);
    public static final Pow pow5 = new Pow(5);
    public static final PowIn pow5In = new PowIn(5);
    public static final PowOut pow5Out = new PowOut(5);
    public static final Interpolation sine = new Interpolation() {
        public float apply(float a) {
            return (1.0f - MathUtils.cos(3.1415927f * a)) / 2.0f;
        }
    };
    public static final Interpolation sineIn = new Interpolation() {
        public float apply(float a) {
            return 1.0f - MathUtils.cos((3.1415927f * a) / 2.0f);
        }
    };
    public static final Interpolation sineOut = new Interpolation() {
        public float apply(float a) {
            return MathUtils.sin((3.1415927f * a) / 2.0f);
        }
    };
    public static final Interpolation swing = new Swing(1.5f);
    public static final Interpolation swingIn = new SwingIn(2.0f);
    public static final Interpolation swingOut = new SwingOut(2.0f);

    public abstract float apply(float f);

    public float apply(float start, float end, float a) {
        return ((end - start) * apply(a)) + start;
    }

    public static class Pow extends Interpolation {
        final int power;

        public Pow(int power2) {
            this.power = power2;
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                return ((float) Math.pow((double) (a * 2.0f), (double) this.power)) / 2.0f;
            }
            return (((float) Math.pow((double) ((a - 1.0f) * 2.0f), (double) this.power)) / ((float) (this.power % 2 == 0 ? -2 : 2))) + 1.0f;
        }
    }

    public static class PowIn extends Pow {
        public PowIn(int power) {
            super(power);
        }

        public float apply(float a) {
            return (float) Math.pow((double) a, (double) this.power);
        }
    }

    public static class PowOut extends Pow {
        public PowOut(int power) {
            super(power);
        }

        public float apply(float a) {
            return (((float) (this.power % 2 == 0 ? -1 : 1)) * ((float) Math.pow((double) (a - 1.0f), (double) this.power))) + 1.0f;
        }
    }

    public static class Exp extends Interpolation {
        final float min;
        final float power;
        final float scale = (1.0f / (1.0f - this.min));
        final float value;

        public Exp(float value2, float power2) {
            this.value = value2;
            this.power = power2;
            this.min = (float) Math.pow((double) value2, (double) (-power2));
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                return ((((float) Math.pow((double) this.value, (double) (this.power * ((a * 2.0f) - 1.0f)))) - this.min) * this.scale) / 2.0f;
            }
            return (2.0f - ((((float) Math.pow((double) this.value, (double) ((-this.power) * ((a * 2.0f) - 1.0f)))) - this.min) * this.scale)) / 2.0f;
        }
    }

    public static class ExpIn extends Exp {
        public ExpIn(float value, float power) {
            super(value, power);
        }

        public float apply(float a) {
            return (((float) Math.pow((double) this.value, (double) (this.power * (a - 1.0f)))) - this.min) * this.scale;
        }
    }

    public static class ExpOut extends Exp {
        public ExpOut(float value, float power) {
            super(value, power);
        }

        public float apply(float a) {
            return 1.0f - ((((float) Math.pow((double) this.value, (double) ((-this.power) * a))) - this.min) * this.scale);
        }
    }

    public static class Elastic extends Interpolation {
        final float power;
        final float value;

        public Elastic(float value2, float power2) {
            this.value = value2;
            this.power = power2;
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                float a2 = a * 2.0f;
                return ((((float) Math.pow((double) this.value, (double) (this.power * (a2 - 1.0f)))) * MathUtils.sin(a2 * 20.0f)) * 1.0955f) / 2.0f;
            }
            float a3 = (1.0f - a) * 2.0f;
            return 1.0f - (((((float) Math.pow((double) this.value, (double) (this.power * (a3 - 1.0f)))) * MathUtils.sin(a3 * 20.0f)) * 1.0955f) / 2.0f);
        }
    }

    public static class ElasticIn extends Elastic {
        public ElasticIn(float value, float power) {
            super(value, power);
        }

        public float apply(float a) {
            return ((float) Math.pow((double) this.value, (double) (this.power * (a - 1.0f)))) * MathUtils.sin(20.0f * a) * 1.0955f;
        }
    }

    public static class ElasticOut extends Elastic {
        public ElasticOut(float value, float power) {
            super(value, power);
        }

        public float apply(float a) {
            float a2 = 1.0f - a;
            return 1.0f - ((((float) Math.pow((double) this.value, (double) (this.power * (a2 - 1.0f)))) * MathUtils.sin(20.0f * a2)) * 1.0955f);
        }
    }

    public static class Bounce extends BounceOut {
        public Bounce(float[] widths, float[] heights) {
            super(widths, heights);
        }

        public Bounce(int bounces) {
            super(bounces);
        }

        private float out(float a) {
            float test = a + (this.widths[0] / 2.0f);
            if (test < this.widths[0]) {
                return (test / (this.widths[0] / 2.0f)) - 1.0f;
            }
            return super.apply(a);
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                return (1.0f - out(1.0f - (a * 2.0f))) / 2.0f;
            }
            return (out((a * 2.0f) - 1.0f) / 2.0f) + 0.5f;
        }
    }

    public static class BounceOut extends Interpolation {
        final float[] heights;
        final float[] widths;

        public BounceOut(float[] widths2, float[] heights2) {
            if (widths2.length != heights2.length) {
                throw new IllegalArgumentException("Must be the same number of widths and heights.");
            }
            this.widths = widths2;
            this.heights = heights2;
        }

        public BounceOut(int bounces) {
            if (bounces < 2 || bounces > 5) {
                throw new IllegalArgumentException("bounces cannot be < 2 or > 5: " + bounces);
            }
            this.widths = new float[bounces];
            this.heights = new float[bounces];
            this.heights[0] = 1.0f;
            switch (bounces) {
                case 2:
                    this.widths[0] = 0.6f;
                    this.widths[1] = 0.4f;
                    this.heights[1] = 0.33f;
                    break;
                case 3:
                    this.widths[0] = 0.4f;
                    this.widths[1] = 0.4f;
                    this.widths[2] = 0.2f;
                    this.heights[1] = 0.33f;
                    this.heights[2] = 0.1f;
                    break;
                case 4:
                    this.widths[0] = 0.34f;
                    this.widths[1] = 0.34f;
                    this.widths[2] = 0.2f;
                    this.widths[3] = 0.15f;
                    this.heights[1] = 0.26f;
                    this.heights[2] = 0.11f;
                    this.heights[3] = 0.03f;
                    break;
                case 5:
                    this.widths[0] = 0.3f;
                    this.widths[1] = 0.3f;
                    this.widths[2] = 0.2f;
                    this.widths[3] = 0.1f;
                    this.widths[4] = 0.1f;
                    this.heights[1] = 0.45f;
                    this.heights[2] = 0.3f;
                    this.heights[3] = 0.15f;
                    this.heights[4] = 0.06f;
                    break;
            }
            float[] fArr = this.widths;
            fArr[0] = fArr[0] * 2.0f;
        }

        public float apply(float a) {
            float a2 = a + (this.widths[0] / 2.0f);
            float width = 0.0f;
            float height = 0.0f;
            int i = 0;
            int n = this.widths.length;
            while (true) {
                if (i >= n) {
                    break;
                }
                width = this.widths[i];
                if (a2 <= width) {
                    height = this.heights[i];
                    break;
                }
                a2 -= width;
                i++;
            }
            float a3 = a2 / width;
            float z = (4.0f / width) * height * a3;
            return 1.0f - ((z - (z * a3)) * width);
        }
    }

    public static class BounceIn extends BounceOut {
        public BounceIn(float[] widths, float[] heights) {
            super(widths, heights);
        }

        public BounceIn(int bounces) {
            super(bounces);
        }

        public float apply(float a) {
            return 1.0f - super.apply(1.0f - a);
        }
    }

    public static class Swing extends Interpolation {
        private final float scale;

        public Swing(float scale2) {
            this.scale = 2.0f * scale2;
        }

        public float apply(float a) {
            if (a <= 0.5f) {
                float a2 = a * 2.0f;
                return ((a2 * a2) * (((this.scale + 1.0f) * a2) - this.scale)) / 2.0f;
            }
            float a3 = (a - 1.0f) * 2.0f;
            return (((a3 * a3) * (((this.scale + 1.0f) * a3) + this.scale)) / 2.0f) + 1.0f;
        }
    }

    public static class SwingOut extends Interpolation {
        private final float scale;

        public SwingOut(float scale2) {
            this.scale = scale2;
        }

        public float apply(float a) {
            float a2 = a - 1.0f;
            return (a2 * a2 * (((this.scale + 1.0f) * a2) + this.scale)) + 1.0f;
        }
    }

    public static class SwingIn extends Interpolation {
        private final float scale;

        public SwingIn(float scale2) {
            this.scale = scale2;
        }

        public float apply(float a) {
            return a * a * (((this.scale + 1.0f) * a) - this.scale);
        }
    }
}
