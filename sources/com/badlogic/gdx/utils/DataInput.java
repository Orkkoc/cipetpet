package com.badlogic.gdx.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataInput extends DataInputStream {
    private char[] chars = new char[32];

    public DataInput(InputStream in) {
        super(in);
    }

    public int readInt(boolean optimizePositive) throws IOException {
        int b = read();
        int result = b & 127;
        if ((b & 128) != 0) {
            int b2 = read();
            result |= (b2 & 127) << 7;
            if ((b2 & 128) != 0) {
                int b3 = read();
                result |= (b3 & 127) << 14;
                if ((b3 & 128) != 0) {
                    int b4 = read();
                    result |= (b4 & 127) << 21;
                    if ((b4 & 128) != 0) {
                        result |= (read() & 127) << 28;
                    }
                }
            }
        }
        return optimizePositive ? result : (result >>> 1) ^ (-(result & 1));
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0024  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String readString() throws java.io.IOException {
        /*
            r7 = this;
            r5 = 1
            int r1 = r7.readInt(r5)
            switch(r1) {
                case 0: goto L_0x002e;
                case 1: goto L_0x0030;
                default: goto L_0x0008;
            }
        L_0x0008:
            int r1 = r1 + -1
            char[] r5 = r7.chars
            int r5 = r5.length
            if (r5 >= r1) goto L_0x0013
            char[] r5 = new char[r1]
            r7.chars = r5
        L_0x0013:
            char[] r4 = r7.chars
            r2 = 0
            r0 = 0
            r3 = r2
        L_0x0018:
            if (r3 >= r1) goto L_0x0022
            int r0 = r7.read()
            r5 = 127(0x7f, float:1.78E-43)
            if (r0 <= r5) goto L_0x0033
        L_0x0022:
            if (r3 >= r1) goto L_0x0027
            r7.readUtf8_slow(r1, r3, r0)
        L_0x0027:
            java.lang.String r5 = new java.lang.String
            r6 = 0
            r5.<init>(r4, r6, r1)
        L_0x002d:
            return r5
        L_0x002e:
            r5 = 0
            goto L_0x002d
        L_0x0030:
            java.lang.String r5 = ""
            goto L_0x002d
        L_0x0033:
            int r2 = r3 + 1
            char r5 = (char) r0
            r4[r3] = r5
            r3 = r2
            goto L_0x0018
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.DataInput.readString():java.lang.String");
    }

    private void readUtf8_slow(int charCount, int charIndex, int b) throws IOException {
        char[] chars2 = this.chars;
        while (true) {
            switch (b >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    chars2[charIndex] = (char) b;
                    break;
                case 12:
                case 13:
                    chars2[charIndex] = (char) (((b & 31) << 6) | (read() & 63));
                    break;
                case 14:
                    chars2[charIndex] = (char) (((b & 15) << 12) | ((read() & 63) << 6) | (read() & 63));
                    break;
            }
            charIndex++;
            if (charIndex < charCount) {
                b = read() & 255;
            } else {
                return;
            }
        }
    }
}
