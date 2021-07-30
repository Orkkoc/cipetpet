package com.badlogic.gdx.utils;

public class Base64Coder {
    private static char[] map1 = new char[64];
    private static byte[] map2 = new byte[128];
    private static final String systemLineSeparator = "\n";

    static {
        int i;
        int i2 = 0;
        char c = 'A';
        while (true) {
            i = i2;
            if (c > 'Z') {
                break;
            }
            i2 = i + 1;
            map1[i] = c;
            c = (char) (c + 1);
        }
        char c2 = 'a';
        while (c2 <= 'z') {
            map1[i] = c2;
            c2 = (char) (c2 + 1);
            i++;
        }
        char c3 = '0';
        while (c3 <= '9') {
            map1[i] = c3;
            c3 = (char) (c3 + 1);
            i++;
        }
        int i3 = i + 1;
        map1[i] = '+';
        int i4 = i3 + 1;
        map1[i3] = '/';
        for (int i5 = 0; i5 < map2.length; i5++) {
            map2[i5] = -1;
        }
        for (int i6 = 0; i6 < 64; i6++) {
            map2[map1[i6]] = (byte) i6;
        }
    }

    public static String encodeString(String s) {
        return new String(encode(s.getBytes()));
    }

    public static String encodeLines(byte[] in) {
        return encodeLines(in, 0, in.length, 76, systemLineSeparator);
    }

    public static String encodeLines(byte[] in, int iOff, int iLen, int lineLen, String lineSeparator) {
        int blockLen = (lineLen * 3) / 4;
        if (blockLen <= 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder buf = new StringBuilder((((iLen + 2) / 3) * 4) + (lineSeparator.length() * (((iLen + blockLen) - 1) / blockLen)));
        int ip = 0;
        while (ip < iLen) {
            int l = Math.min(iLen - ip, blockLen);
            buf.append(encode(in, iOff + ip, l));
            buf.append(lineSeparator);
            ip += l;
        }
        return buf.toString();
    }

    public static char[] encode(byte[] in) {
        return encode(in, 0, in.length);
    }

    public static char[] encode(byte[] in, int iLen) {
        return encode(in, 0, iLen);
    }

    public static char[] encode(byte[] in, int iOff, int iLen) {
        int i1;
        int ip;
        int i2;
        int ip2;
        int oDataLen = ((iLen * 4) + 2) / 3;
        char[] out = new char[(((iLen + 2) / 3) * 4)];
        int iEnd = iOff + iLen;
        int op = 0;
        int ip3 = iOff;
        while (ip3 < iEnd) {
            int ip4 = ip3 + 1;
            int i0 = in[ip3] & 255;
            if (ip4 < iEnd) {
                ip = ip4 + 1;
                i1 = in[ip4] & 255;
            } else {
                i1 = 0;
                ip = ip4;
            }
            if (ip < iEnd) {
                ip2 = ip + 1;
                i2 = in[ip] & 255;
            } else {
                i2 = 0;
                ip2 = ip;
            }
            int o2 = ((i1 & 15) << 2) | (i2 >>> 6);
            int o3 = i2 & 63;
            int op2 = op + 1;
            out[op] = map1[i0 >>> 2];
            int op3 = op2 + 1;
            out[op2] = map1[((i0 & 3) << 4) | (i1 >>> 4)];
            out[op3] = op3 < oDataLen ? map1[o2] : '=';
            int op4 = op3 + 1;
            out[op4] = op4 < oDataLen ? map1[o3] : '=';
            op = op4 + 1;
            ip3 = ip2;
        }
        return out;
    }

    public static String decodeString(String s) {
        return new String(decode(s));
    }

    public static byte[] decodeLines(String s) {
        char[] buf = new char[s.length()];
        int p = 0;
        for (int ip = 0; ip < s.length(); ip++) {
            char c = s.charAt(ip);
            if (!(c == ' ' || c == 13 || c == 10 || c == 9)) {
                buf[p] = c;
                p++;
            }
        }
        return decode(buf, 0, p);
    }

    public static byte[] decode(String s) {
        return decode(s.toCharArray());
    }

    public static byte[] decode(char[] in) {
        return decode(in, 0, in.length);
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r8v2, types: [char] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r9v2, types: [char] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] decode(char[] r22, int r23, int r24) {
        /*
            int r20 = r24 % 4
            if (r20 == 0) goto L_0x000c
            java.lang.IllegalArgumentException r20 = new java.lang.IllegalArgumentException
            java.lang.String r21 = "Length of Base64 encoded input string is not a multiple of 4."
            r20.<init>(r21)
            throw r20
        L_0x000c:
            if (r24 <= 0) goto L_0x001f
            int r20 = r23 + r24
            int r20 = r20 + -1
            char r20 = r22[r20]
            r21 = 61
            r0 = r20
            r1 = r21
            if (r0 != r1) goto L_0x001f
            int r24 = r24 + -1
            goto L_0x000c
        L_0x001f:
            int r20 = r24 * 3
            int r16 = r20 / 4
            r0 = r16
            byte[] r0 = new byte[r0]
            r19 = r0
            r11 = r23
            int r10 = r23 + r24
            r17 = 0
            r18 = r17
            r12 = r11
        L_0x0032:
            if (r12 >= r10) goto L_0x00ca
            int r11 = r12 + 1
            char r6 = r22[r12]
            int r12 = r11 + 1
            char r7 = r22[r11]
            if (r12 >= r10) goto L_0x0069
            int r11 = r12 + 1
            char r8 = r22[r12]
            r12 = r11
        L_0x0043:
            if (r12 >= r10) goto L_0x006c
            int r11 = r12 + 1
            char r9 = r22[r12]
        L_0x0049:
            r20 = 127(0x7f, float:1.78E-43)
            r0 = r20
            if (r6 > r0) goto L_0x0061
            r20 = 127(0x7f, float:1.78E-43)
            r0 = r20
            if (r7 > r0) goto L_0x0061
            r20 = 127(0x7f, float:1.78E-43)
            r0 = r20
            if (r8 > r0) goto L_0x0061
            r20 = 127(0x7f, float:1.78E-43)
            r0 = r20
            if (r9 <= r0) goto L_0x0070
        L_0x0061:
            java.lang.IllegalArgumentException r20 = new java.lang.IllegalArgumentException
            java.lang.String r21 = "Illegal character in Base64 encoded data."
            r20.<init>(r21)
            throw r20
        L_0x0069:
            r8 = 65
            goto L_0x0043
        L_0x006c:
            r9 = 65
            r11 = r12
            goto L_0x0049
        L_0x0070:
            byte[] r20 = map2
            byte r2 = r20[r6]
            byte[] r20 = map2
            byte r3 = r20[r7]
            byte[] r20 = map2
            byte r4 = r20[r8]
            byte[] r20 = map2
            byte r5 = r20[r9]
            if (r2 < 0) goto L_0x0088
            if (r3 < 0) goto L_0x0088
            if (r4 < 0) goto L_0x0088
            if (r5 >= 0) goto L_0x0090
        L_0x0088:
            java.lang.IllegalArgumentException r20 = new java.lang.IllegalArgumentException
            java.lang.String r21 = "Illegal character in Base64 encoded data."
            r20.<init>(r21)
            throw r20
        L_0x0090:
            int r20 = r2 << 2
            int r21 = r3 >>> 4
            r13 = r20 | r21
            r20 = r3 & 15
            int r20 = r20 << 4
            int r21 = r4 >>> 2
            r14 = r20 | r21
            r20 = r4 & 3
            int r20 = r20 << 6
            r15 = r20 | r5
            int r17 = r18 + 1
            byte r0 = (byte) r13
            r20 = r0
            r19[r18] = r20
            r0 = r17
            r1 = r16
            if (r0 >= r1) goto L_0x00ce
            int r18 = r17 + 1
            byte r0 = (byte) r14
            r20 = r0
            r19[r17] = r20
        L_0x00b8:
            r0 = r18
            r1 = r16
            if (r0 >= r1) goto L_0x00cb
            int r17 = r18 + 1
            byte r0 = (byte) r15
            r20 = r0
            r19[r18] = r20
        L_0x00c5:
            r18 = r17
            r12 = r11
            goto L_0x0032
        L_0x00ca:
            return r19
        L_0x00cb:
            r17 = r18
            goto L_0x00c5
        L_0x00ce:
            r18 = r17
            goto L_0x00b8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Base64Coder.decode(char[], int, int):byte[]");
    }

    private Base64Coder() {
    }
}
