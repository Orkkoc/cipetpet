package com.badlogic.gdx.math;

import com.badlogic.gdx.utils.NumberUtils;
import java.util.Random;

public class MathUtils {
    private static final int ATAN2_BITS = 7;
    private static final int ATAN2_BITS2 = 14;
    private static final int ATAN2_COUNT = 16384;
    static final int ATAN2_DIM = ((int) Math.sqrt(BIG_ENOUGH_FLOOR));
    private static final int ATAN2_MASK = 16383;
    private static final double BIG_ENOUGH_CEIL = NumberUtils.longBitsToDouble(NumberUtils.doubleToLongBits(16385.0d) - 1);
    private static final double BIG_ENOUGH_FLOOR = 16384.0d;
    private static final int BIG_ENOUGH_INT = 16384;
    private static final double BIG_ENOUGH_ROUND = 16384.5d;
    private static final double CEIL = 0.9999999d;
    private static final float INV_ATAN2_DIM_MINUS_1 = (1.0f / ((float) (ATAN2_DIM - 1)));

    /* renamed from: PI */
    public static final float f153PI = 3.1415927f;
    private static final int SIN_BITS = 13;
    private static final int SIN_COUNT = 8192;
    private static final int SIN_MASK = 8191;
    private static final float degFull = 360.0f;
    public static final float degRad = 0.017453292f;
    private static final float degToIndex = 22.755556f;
    public static final float degreesToRadians = 0.017453292f;
    public static final float nanoToSec = 1.0E-9f;
    public static final float radDeg = 57.295776f;
    private static final float radFull = 6.2831855f;
    private static final float radToIndex = 1303.7972f;
    public static final float radiansToDegrees = 57.295776f;
    public static Random random = new Random();

    private static class Sin {
        static final float[] table = new float[8192];

        private Sin() {
        }

        static {
            for (int i = 0; i < 8192; i++) {
                table[i] = (float) Math.sin((double) (((((float) i) + 0.5f) / 8192.0f) * MathUtils.radFull));
            }
            for (int i2 = 0; i2 < 360; i2 += 90) {
                table[((int) (((float) i2) * MathUtils.degToIndex)) & MathUtils.SIN_MASK] = (float) Math.sin((double) (((float) i2) * 0.017453292f));
            }
        }
    }

    private static class Cos {
        static final float[] table = new float[8192];

        private Cos() {
        }

        static {
            for (int i = 0; i < 8192; i++) {
                table[i] = (float) Math.cos((double) (((((float) i) + 0.5f) / 8192.0f) * MathUtils.radFull));
            }
            for (int i2 = 0; i2 < 360; i2 += 90) {
                table[((int) (((float) i2) * MathUtils.degToIndex)) & MathUtils.SIN_MASK] = (float) Math.cos((double) (((float) i2) * 0.017453292f));
            }
        }
    }

    public static final float sin(float radians) {
        return Sin.table[((int) (radToIndex * radians)) & SIN_MASK];
    }

    public static final float cos(float radians) {
        return Cos.table[((int) (radToIndex * radians)) & SIN_MASK];
    }

    public static final float sinDeg(float degrees) {
        return Sin.table[((int) (degToIndex * degrees)) & SIN_MASK];
    }

    public static final float cosDeg(float degrees) {
        return Cos.table[((int) (degToIndex * degrees)) & SIN_MASK];
    }

    private static class Atan2 {
        static final float[] table = new float[16384];

        private Atan2() {
        }

        static {
            for (int i = 0; i < MathUtils.ATAN2_DIM; i++) {
                for (int j = 0; j < MathUtils.ATAN2_DIM; j++) {
                    float x0 = ((float) i) / ((float) MathUtils.ATAN2_DIM);
                    table[(MathUtils.ATAN2_DIM * j) + i] = (float) Math.atan2((double) (((float) j) / ((float) MathUtils.ATAN2_DIM)), (double) x0);
                }
            }
        }
    }

    public static final float atan2(float y, float x) {
        float mul;
        float mul2;
        float add;
        float f;
        if (x < INV_ATAN2_DIM_MINUS_1) {
            if (y < INV_ATAN2_DIM_MINUS_1) {
                y = -y;
                mul2 = 1.0f;
            } else {
                mul2 = -1.0f;
            }
            x = -x;
            add = -3.1415927f;
        } else {
            if (y < INV_ATAN2_DIM_MINUS_1) {
                y = -y;
                mul = -1.0f;
            } else {
                mul = 1.0f;
            }
            add = INV_ATAN2_DIM_MINUS_1;
        }
        if (x < y) {
            f = y;
        } else {
            f = x;
        }
        float invDiv = 1.0f / (f * INV_ATAN2_DIM_MINUS_1);
        return (Atan2.table[(ATAN2_DIM * ((int) (y * invDiv))) + ((int) (x * invDiv))] + add) * mul2;
    }

    public static final int random(int range) {
        return random.nextInt(range + 1);
    }

    public static final int random(int start, int end) {
        return random.nextInt((end - start) + 1) + start;
    }

    public static final boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static final float random() {
        return random.nextFloat();
    }

    public static final float random(float range) {
        return random.nextFloat() * range;
    }

    public static final float random(float start, float end) {
        return (random.nextFloat() * (end - start)) + start;
    }

    public static int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        int value2 = value - 1;
        int value3 = value2 | (value2 >> 1);
        int value4 = value3 | (value3 >> 2);
        int value5 = value4 | (value4 >> 4);
        int value6 = value5 | (value5 >> 8);
        return (value6 | (value6 >> 16)) + 1;
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && ((value + -1) & value) == 0;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return value > max ? max : value;
    }

    public static short clamp(short value, short min, short max) {
        if (value < min) {
            return min;
        }
        return value > max ? max : value;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        return value > max ? max : value;
    }

    public static int floor(float x) {
        return ((int) (((double) x) + BIG_ENOUGH_FLOOR)) - 16384;
    }

    public static int floorPositive(float x) {
        return (int) x;
    }

    public static int ceil(float x) {
        return ((int) (((double) x) + BIG_ENOUGH_CEIL)) - 16384;
    }

    public static int ceilPositive(float x) {
        return (int) (((double) x) + CEIL);
    }

    public static int round(float x) {
        return ((int) (((double) x) + BIG_ENOUGH_ROUND)) - 16384;
    }

    public static int roundPositive(float x) {
        return (int) (0.5f + x);
    }
}
