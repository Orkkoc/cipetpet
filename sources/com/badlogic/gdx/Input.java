package com.badlogic.gdx;

public interface Input {

    public static class Buttons {
        public static final int LEFT = 0;
        public static final int MIDDLE = 2;
        public static final int RIGHT = 1;
    }

    public static class Keys {

        /* renamed from: A */
        public static final int f17A = 29;
        public static final int ALT_LEFT = 57;
        public static final int ALT_RIGHT = 58;
        public static final int ANY_KEY = -1;
        public static final int APOSTROPHE = 75;

        /* renamed from: AT */
        public static final int f18AT = 77;

        /* renamed from: B */
        public static final int f19B = 30;
        public static final int BACK = 4;
        public static final int BACKSLASH = 73;
        public static final int BACKSPACE = 67;
        public static final int BUTTON_A = 96;
        public static final int BUTTON_B = 97;
        public static final int BUTTON_C = 98;
        public static final int BUTTON_CIRCLE = 255;
        public static final int BUTTON_L1 = 102;
        public static final int BUTTON_L2 = 104;
        public static final int BUTTON_MODE = 110;
        public static final int BUTTON_R1 = 103;
        public static final int BUTTON_R2 = 105;
        public static final int BUTTON_SELECT = 109;
        public static final int BUTTON_START = 108;
        public static final int BUTTON_THUMBL = 106;
        public static final int BUTTON_THUMBR = 107;
        public static final int BUTTON_X = 99;
        public static final int BUTTON_Y = 100;
        public static final int BUTTON_Z = 101;

        /* renamed from: C */
        public static final int f20C = 31;
        public static final int CALL = 5;
        public static final int CAMERA = 27;
        public static final int CENTER = 23;
        public static final int CLEAR = 28;
        public static final int COLON = 243;
        public static final int COMMA = 55;
        public static final int CONTROL_LEFT = 129;
        public static final int CONTROL_RIGHT = 130;

        /* renamed from: D */
        public static final int f21D = 32;
        public static final int DEL = 67;
        public static final int DOWN = 20;
        public static final int DPAD_CENTER = 23;
        public static final int DPAD_DOWN = 20;
        public static final int DPAD_LEFT = 21;
        public static final int DPAD_RIGHT = 22;
        public static final int DPAD_UP = 19;

        /* renamed from: E */
        public static final int f22E = 33;
        public static final int END = 132;
        public static final int ENDCALL = 6;
        public static final int ENTER = 66;
        public static final int ENVELOPE = 65;
        public static final int EQUALS = 70;
        public static final int ESCAPE = 131;
        public static final int EXPLORER = 64;

        /* renamed from: F */
        public static final int f23F = 34;

        /* renamed from: F1 */
        public static final int f24F1 = 244;
        public static final int F10 = 253;
        public static final int F11 = 254;
        public static final int F12 = 255;

        /* renamed from: F2 */
        public static final int f25F2 = 245;

        /* renamed from: F3 */
        public static final int f26F3 = 246;

        /* renamed from: F4 */
        public static final int f27F4 = 247;

        /* renamed from: F5 */
        public static final int f28F5 = 248;

        /* renamed from: F6 */
        public static final int f29F6 = 249;

        /* renamed from: F7 */
        public static final int f30F7 = 250;

        /* renamed from: F8 */
        public static final int f31F8 = 251;

        /* renamed from: F9 */
        public static final int f32F9 = 252;
        public static final int FOCUS = 80;
        public static final int FORWARD_DEL = 112;

        /* renamed from: G */
        public static final int f33G = 35;
        public static final int GRAVE = 68;

        /* renamed from: H */
        public static final int f34H = 36;
        public static final int HEADSETHOOK = 79;
        public static final int HOME = 3;

        /* renamed from: I */
        public static final int f35I = 37;
        public static final int INSERT = 133;

        /* renamed from: J */
        public static final int f36J = 38;

        /* renamed from: K */
        public static final int f37K = 39;

        /* renamed from: L */
        public static final int f38L = 40;
        public static final int LEFT = 21;
        public static final int LEFT_BRACKET = 71;

        /* renamed from: M */
        public static final int f39M = 41;
        public static final int MEDIA_FAST_FORWARD = 90;
        public static final int MEDIA_NEXT = 87;
        public static final int MEDIA_PLAY_PAUSE = 85;
        public static final int MEDIA_PREVIOUS = 88;
        public static final int MEDIA_REWIND = 89;
        public static final int MEDIA_STOP = 86;
        public static final int MENU = 82;
        public static final int META_ALT_LEFT_ON = 16;
        public static final int META_ALT_ON = 2;
        public static final int META_ALT_RIGHT_ON = 32;
        public static final int META_SHIFT_LEFT_ON = 64;
        public static final int META_SHIFT_ON = 1;
        public static final int META_SHIFT_RIGHT_ON = 128;
        public static final int META_SYM_ON = 4;
        public static final int MINUS = 69;
        public static final int MUTE = 91;

        /* renamed from: N */
        public static final int f40N = 42;
        public static final int NOTIFICATION = 83;
        public static final int NUM = 78;
        public static final int NUM_0 = 7;
        public static final int NUM_1 = 8;
        public static final int NUM_2 = 9;
        public static final int NUM_3 = 10;
        public static final int NUM_4 = 11;
        public static final int NUM_5 = 12;
        public static final int NUM_6 = 13;
        public static final int NUM_7 = 14;
        public static final int NUM_8 = 15;
        public static final int NUM_9 = 16;

        /* renamed from: O */
        public static final int f41O = 43;

        /* renamed from: P */
        public static final int f42P = 44;
        public static final int PAGE_DOWN = 93;
        public static final int PAGE_UP = 92;
        public static final int PERIOD = 56;
        public static final int PICTSYMBOLS = 94;
        public static final int PLUS = 81;
        public static final int POUND = 18;
        public static final int POWER = 26;

        /* renamed from: Q */
        public static final int f43Q = 45;

        /* renamed from: R */
        public static final int f44R = 46;
        public static final int RIGHT = 22;
        public static final int RIGHT_BRACKET = 72;

        /* renamed from: S */
        public static final int f45S = 47;
        public static final int SEARCH = 84;
        public static final int SEMICOLON = 74;
        public static final int SHIFT_LEFT = 59;
        public static final int SHIFT_RIGHT = 60;
        public static final int SLASH = 76;
        public static final int SOFT_LEFT = 1;
        public static final int SOFT_RIGHT = 2;
        public static final int SPACE = 62;
        public static final int STAR = 17;
        public static final int SWITCH_CHARSET = 95;
        public static final int SYM = 63;

        /* renamed from: T */
        public static final int f46T = 48;
        public static final int TAB = 61;

        /* renamed from: U */
        public static final int f47U = 49;
        public static final int UNKNOWN = 0;

        /* renamed from: UP */
        public static final int f48UP = 19;

        /* renamed from: V */
        public static final int f49V = 50;
        public static final int VOLUME_DOWN = 25;
        public static final int VOLUME_UP = 24;

        /* renamed from: W */
        public static final int f50W = 51;

        /* renamed from: X */
        public static final int f51X = 52;

        /* renamed from: Y */
        public static final int f52Y = 53;

        /* renamed from: Z */
        public static final int f53Z = 54;
    }

    public enum Orientation {
        Landscape,
        Portrait
    }

    public enum Peripheral {
        HardwareKeyboard,
        OnscreenKeyboard,
        MultitouchScreen,
        Accelerometer,
        Compass,
        Vibrator
    }

    public interface TextInputListener {
        void canceled();

        void input(String str);
    }

    void cancelVibrate();

    float getAccelerometerX();

    float getAccelerometerY();

    float getAccelerometerZ();

    float getAzimuth();

    long getCurrentEventTime();

    int getDeltaX();

    int getDeltaX(int i);

    int getDeltaY();

    int getDeltaY(int i);

    InputProcessor getInputProcessor();

    Orientation getNativeOrientation();

    float getPitch();

    void getPlaceholderTextInput(TextInputListener textInputListener, String str, String str2);

    float getRoll();

    int getRotation();

    void getRotationMatrix(float[] fArr);

    void getTextInput(TextInputListener textInputListener, String str, String str2);

    int getX();

    int getX(int i);

    int getY();

    int getY(int i);

    boolean isButtonPressed(int i);

    boolean isCursorCatched();

    boolean isKeyPressed(int i);

    boolean isPeripheralAvailable(Peripheral peripheral);

    boolean isTouched();

    boolean isTouched(int i);

    boolean justTouched();

    void setCatchBackKey(boolean z);

    void setCatchMenuKey(boolean z);

    void setCursorCatched(boolean z);

    void setCursorPosition(int i, int i2);

    void setInputProcessor(InputProcessor inputProcessor);

    void setOnscreenKeyboardVisible(boolean z);

    void vibrate(int i);

    void vibrate(long[] jArr, int i);
}
