package com.badlogic.gdx.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

public class XmlReader {
    private static final byte[] _xml_actions = init__xml_actions_0();
    private static final short[] _xml_index_offsets = init__xml_index_offsets_0();
    private static final byte[] _xml_indicies = init__xml_indicies_0();
    private static final byte[] _xml_key_offsets = init__xml_key_offsets_0();
    private static final byte[] _xml_range_lengths = init__xml_range_lengths_0();
    private static final byte[] _xml_single_lengths = init__xml_single_lengths_0();
    private static final byte[] _xml_trans_actions = init__xml_trans_actions_0();
    private static final char[] _xml_trans_keys = init__xml_trans_keys_0();
    private static final byte[] _xml_trans_targs = init__xml_trans_targs_0();
    static final int xml_en_elementBody = 15;
    static final int xml_en_main = 1;
    static final int xml_error = 0;
    static final int xml_first_final = 34;
    static final int xml_start = 1;
    private Element current;
    private final Array<Element> elements = new Array<>(8);
    private Element root;
    private final StringBuilder textBuffer = new StringBuilder(64);

    public Element parse(String xml) {
        char[] data = xml.toCharArray();
        return parse(data, 0, data.length);
    }

    public Element parse(Reader reader) throws IOException {
        char[] data = new char[1024];
        int offset = 0;
        while (true) {
            int length = reader.read(data, offset, data.length - offset);
            if (length == -1) {
                return parse(data, 0, offset);
            }
            if (length == 0) {
                char[] newData = new char[(data.length * 2)];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            } else {
                offset += length;
            }
        }
    }

    public Element parse(InputStream input) throws IOException {
        return parse((Reader) new InputStreamReader(input, "ISO-8859-1"));
    }

    public Element parse(FileHandle file) throws IOException {
        try {
            return parse(file.read());
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: short} */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x000d, code lost:
        continue;
        continue;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0102, code lost:
        if (r39[r31 + 1] != '[') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x010e, code lost:
        if (r39[r31 + 2] != 'C') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x011a, code lost:
        if (r39[r31 + 3] != 'D') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0126, code lost:
        if (r39[r31 + 4] != 'A') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0132, code lost:
        if (r39[r31 + 5] != 'T') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x013e, code lost:
        if (r39[r31 + 6] != 'A') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x014a, code lost:
        if (r39[r31 + 7] != '[') goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x014c, code lost:
        r31 = r31 + 8;
        r28 = r31 + 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x015a, code lost:
        if (r39[r28 - 2] != ']') goto L_0x0172;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0166, code lost:
        if (r39[r28 - 1] != ']') goto L_0x0172;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0170, code lost:
        if (r39[r28] == '>') goto L_0x0175;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0172, code lost:
        r28 = r28 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0175, code lost:
        text(new java.lang.String(r39, r31, (r28 - r31) - 2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x018d, code lost:
        r17 = 15;
        r6 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x019a, code lost:
        if (r39[r28] == '>') goto L_0x018d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x019c, code lost:
        r28 = r28 + 1;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r17v4, types: [byte] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0286 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0214  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.badlogic.gdx.utils.XmlReader.Element parse(char[] r39, int r40, int r41) {
        /*
            r38 = this;
            r28 = r40
            r29 = r41
            r31 = 0
            r15 = 0
            r24 = 0
            r17 = 1
            r13 = 0
            r6 = 0
        L_0x000d:
            switch(r6) {
                case 0: goto L_0x002f;
                case 1: goto L_0x003b;
                case 2: goto L_0x02d7;
                default: goto L_0x0010;
            }
        L_0x0010:
            r0 = r28
            r1 = r29
            if (r0 >= r1) goto L_0x0323
            r26 = 1
            r25 = 0
        L_0x001a:
            r0 = r25
            r1 = r28
            if (r0 >= r1) goto L_0x02e7
            char r33 = r39[r25]
            r34 = 10
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x002c
            int r26 = r26 + 1
        L_0x002c:
            int r25 = r25 + 1
            goto L_0x001a
        L_0x002f:
            r0 = r28
            r1 = r29
            if (r0 != r1) goto L_0x0037
            r6 = 4
            goto L_0x000d
        L_0x0037:
            if (r17 != 0) goto L_0x003b
            r6 = 5
            goto L_0x000d
        L_0x003b:
            byte[] r33 = _xml_key_offsets
            byte r7 = r33[r17]
            short[] r33 = _xml_index_offsets
            short r13 = r33[r17]
            byte[] r33 = _xml_single_lengths
            byte r8 = r33[r17]
            if (r8 <= 0) goto L_0x0052
            r9 = r7
            int r33 = r7 + r8
            int r14 = r33 + -1
        L_0x004e:
            if (r14 >= r9) goto L_0x008b
            int r7 = r7 + r8
            int r13 = r13 + r8
        L_0x0052:
            byte[] r33 = _xml_range_lengths
            byte r8 = r33[r17]
            if (r8 <= 0) goto L_0x0062
            r9 = r7
            int r33 = r8 << 1
            int r33 = r33 + r7
            int r14 = r33 + -2
        L_0x005f:
            if (r14 >= r9) goto L_0x00b4
            int r13 = r13 + r8
        L_0x0062:
            byte[] r33 = _xml_indicies
            byte r13 = r33[r13]
            byte[] r33 = _xml_trans_targs
            byte r17 = r33[r13]
            byte[] r33 = _xml_trans_actions
            byte r33 = r33[r13]
            if (r33 == 0) goto L_0x02d7
            byte[] r33 = _xml_trans_actions
            byte r4 = r33[r13]
            byte[] r33 = _xml_actions
            int r5 = r4 + 1
            byte r11 = r33[r4]
            r12 = r11
        L_0x007b:
            int r11 = r12 + -1
            if (r12 <= 0) goto L_0x02d7
            byte[] r33 = _xml_actions
            int r4 = r5 + 1
            byte r33 = r33[r5]
            switch(r33) {
                case 0: goto L_0x00e3;
                case 1: goto L_0x00e6;
                case 2: goto L_0x01b9;
                case 3: goto L_0x01c3;
                case 4: goto L_0x01cb;
                case 5: goto L_0x01d2;
                case 6: goto L_0x01e1;
                case 7: goto L_0x01f9;
                default: goto L_0x0088;
            }
        L_0x0088:
            r12 = r11
            r5 = r4
            goto L_0x007b
        L_0x008b:
            int r33 = r14 - r9
            int r33 = r33 >> 1
            int r10 = r9 + r33
            char r33 = r39[r28]
            char[] r34 = _xml_trans_keys
            char r34 = r34[r10]
            r0 = r33
            r1 = r34
            if (r0 >= r1) goto L_0x00a0
            int r14 = r10 + -1
            goto L_0x004e
        L_0x00a0:
            char r33 = r39[r28]
            char[] r34 = _xml_trans_keys
            char r34 = r34[r10]
            r0 = r33
            r1 = r34
            if (r0 <= r1) goto L_0x00af
            int r9 = r10 + 1
            goto L_0x004e
        L_0x00af:
            int r33 = r10 - r7
            int r13 = r13 + r33
            goto L_0x0062
        L_0x00b4:
            int r33 = r14 - r9
            int r33 = r33 >> 1
            r33 = r33 & -2
            int r10 = r9 + r33
            char r33 = r39[r28]
            char[] r34 = _xml_trans_keys
            char r34 = r34[r10]
            r0 = r33
            r1 = r34
            if (r0 >= r1) goto L_0x00cb
            int r14 = r10 + -2
            goto L_0x005f
        L_0x00cb:
            char r33 = r39[r28]
            char[] r34 = _xml_trans_keys
            int r35 = r10 + 1
            char r34 = r34[r35]
            r0 = r33
            r1 = r34
            if (r0 <= r1) goto L_0x00dc
            int r9 = r10 + 2
            goto L_0x005f
        L_0x00dc:
            int r33 = r10 - r7
            int r33 = r33 >> 1
            int r13 = r13 + r33
            goto L_0x0062
        L_0x00e3:
            r31 = r28
            goto L_0x0088
        L_0x00e6:
            char r16 = r39[r31]
            r33 = 63
            r0 = r16
            r1 = r33
            if (r0 == r1) goto L_0x00f8
            r33 = 33
            r0 = r16
            r1 = r33
            if (r0 != r1) goto L_0x019f
        L_0x00f8:
            int r33 = r31 + 1
            char r33 = r39[r33]
            r34 = 91
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r33 = r31 + 2
            char r33 = r39[r33]
            r34 = 67
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r33 = r31 + 3
            char r33 = r39[r33]
            r34 = 68
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r33 = r31 + 4
            char r33 = r39[r33]
            r34 = 65
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r33 = r31 + 5
            char r33 = r39[r33]
            r34 = 84
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r33 = r31 + 6
            char r33 = r39[r33]
            r34 = 65
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r33 = r31 + 7
            char r33 = r39[r33]
            r34 = 91
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0192
            int r31 = r31 + 8
            int r28 = r31 + 2
        L_0x0150:
            int r33 = r28 + -2
            char r33 = r39[r33]
            r34 = 93
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0172
            int r33 = r28 + -1
            char r33 = r39[r33]
            r34 = 93
            r0 = r33
            r1 = r34
            if (r0 != r1) goto L_0x0172
            char r33 = r39[r28]
            r34 = 62
            r0 = r33
            r1 = r34
            if (r0 == r1) goto L_0x0175
        L_0x0172:
            int r28 = r28 + 1
            goto L_0x0150
        L_0x0175:
            java.lang.String r33 = new java.lang.String
            int r34 = r28 - r31
            int r34 = r34 + -2
            r0 = r33
            r1 = r39
            r2 = r31
            r3 = r34
            r0.<init>(r1, r2, r3)
            r0 = r38
            r1 = r33
            r0.text(r1)
        L_0x018d:
            r17 = 15
            r6 = 2
            goto L_0x000d
        L_0x0192:
            char r33 = r39[r28]
            r34 = 62
            r0 = r33
            r1 = r34
            if (r0 == r1) goto L_0x018d
            int r28 = r28 + 1
            goto L_0x0192
        L_0x019f:
            r24 = 1
            java.lang.String r33 = new java.lang.String
            int r34 = r28 - r31
            r0 = r33
            r1 = r39
            r2 = r31
            r3 = r34
            r0.<init>(r1, r2, r3)
            r0 = r38
            r1 = r33
            r0.open(r1)
            goto L_0x0088
        L_0x01b9:
            r24 = 0
            r38.close()
            r17 = 15
            r6 = 2
            goto L_0x000d
        L_0x01c3:
            r38.close()
            r17 = 15
            r6 = 2
            goto L_0x000d
        L_0x01cb:
            if (r24 == 0) goto L_0x0088
            r17 = 15
            r6 = 2
            goto L_0x000d
        L_0x01d2:
            java.lang.String r15 = new java.lang.String
            int r33 = r28 - r31
            r0 = r39
            r1 = r31
            r2 = r33
            r15.<init>(r0, r1, r2)
            goto L_0x0088
        L_0x01e1:
            java.lang.String r33 = new java.lang.String
            int r34 = r28 - r31
            r0 = r33
            r1 = r39
            r2 = r31
            r3 = r34
            r0.<init>(r1, r2, r3)
            r0 = r38
            r1 = r33
            r0.attribute(r15, r1)
            goto L_0x0088
        L_0x01f9:
            r21 = r28
        L_0x01fb:
            r0 = r21
            r1 = r31
            if (r0 == r1) goto L_0x0208
            int r33 = r21 + -1
            char r33 = r39[r33]
            switch(r33) {
                case 9: goto L_0x0223;
                case 10: goto L_0x0223;
                case 13: goto L_0x0223;
                case 32: goto L_0x0223;
                default: goto L_0x0208;
            }
        L_0x0208:
            r18 = r31
            r22 = 0
            r19 = r18
        L_0x020e:
            r0 = r19
            r1 = r21
            if (r0 == r1) goto L_0x0286
            int r18 = r19 + 1
            char r33 = r39[r19]
            r34 = 38
            r0 = r33
            r1 = r34
            if (r0 == r1) goto L_0x0226
            r19 = r18
            goto L_0x020e
        L_0x0223:
            int r21 = r21 + -1
            goto L_0x01fb
        L_0x0226:
            r23 = r18
            r19 = r18
        L_0x022a:
            r0 = r19
            r1 = r21
            if (r0 == r1) goto L_0x0372
            int r18 = r19 + 1
            char r33 = r39[r19]
            r34 = 59
            r0 = r33
            r1 = r34
            if (r0 == r1) goto L_0x023f
            r19 = r18
            goto L_0x022a
        L_0x023f:
            r0 = r38
            com.badlogic.gdx.utils.StringBuilder r0 = r0.textBuffer
            r33 = r0
            int r34 = r23 - r31
            int r34 = r34 + -1
            r0 = r33
            r1 = r39
            r2 = r31
            r3 = r34
            r0.append((char[]) r1, (int) r2, (int) r3)
            java.lang.String r27 = new java.lang.String
            int r33 = r18 - r23
            int r33 = r33 + -1
            r0 = r27
            r1 = r39
            r2 = r23
            r3 = r33
            r0.<init>(r1, r2, r3)
            r0 = r38
            r1 = r27
            java.lang.String r32 = r0.entity(r1)
            r0 = r38
            com.badlogic.gdx.utils.StringBuilder r0 = r0.textBuffer
            r33 = r0
            if (r32 == 0) goto L_0x0283
        L_0x0275:
            r0 = r33
            r1 = r32
            r0.append((java.lang.String) r1)
            r31 = r18
            r22 = 1
        L_0x0280:
            r19 = r18
            goto L_0x020e
        L_0x0283:
            r32 = r27
            goto L_0x0275
        L_0x0286:
            if (r22 == 0) goto L_0x02bf
            r0 = r31
            r1 = r21
            if (r0 >= r1) goto L_0x02a1
            r0 = r38
            com.badlogic.gdx.utils.StringBuilder r0 = r0.textBuffer
            r33 = r0
            int r34 = r21 - r31
            r0 = r33
            r1 = r39
            r2 = r31
            r3 = r34
            r0.append((char[]) r1, (int) r2, (int) r3)
        L_0x02a1:
            r0 = r38
            com.badlogic.gdx.utils.StringBuilder r0 = r0.textBuffer
            r33 = r0
            java.lang.String r33 = r33.toString()
            r0 = r38
            r1 = r33
            r0.text(r1)
            r0 = r38
            com.badlogic.gdx.utils.StringBuilder r0 = r0.textBuffer
            r33 = r0
            r34 = 0
            r33.setLength(r34)
            goto L_0x0088
        L_0x02bf:
            java.lang.String r33 = new java.lang.String
            int r34 = r21 - r31
            r0 = r33
            r1 = r39
            r2 = r31
            r3 = r34
            r0.<init>(r1, r2, r3)
            r0 = r38
            r1 = r33
            r0.text(r1)
            goto L_0x0088
        L_0x02d7:
            if (r17 != 0) goto L_0x02dc
            r6 = 5
            goto L_0x000d
        L_0x02dc:
            int r28 = r28 + 1
            r0 = r28
            r1 = r29
            if (r0 == r1) goto L_0x0010
            r6 = 1
            goto L_0x000d
        L_0x02e7:
            com.badlogic.gdx.utils.SerializationException r33 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r34 = new java.lang.StringBuilder
            r34.<init>()
            java.lang.String r35 = "Error parsing XML on line "
            java.lang.StringBuilder r34 = r34.append(r35)
            r0 = r34
            r1 = r26
            java.lang.StringBuilder r34 = r0.append(r1)
            java.lang.String r35 = " near: "
            java.lang.StringBuilder r34 = r34.append(r35)
            java.lang.String r35 = new java.lang.String
            r36 = 32
            int r37 = r29 - r28
            int r36 = java.lang.Math.min(r36, r37)
            r0 = r35
            r1 = r39
            r2 = r28
            r3 = r36
            r0.<init>(r1, r2, r3)
            java.lang.StringBuilder r34 = r34.append(r35)
            java.lang.String r34 = r34.toString()
            r33.<init>((java.lang.String) r34)
            throw r33
        L_0x0323:
            r0 = r38
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.XmlReader$Element> r0 = r0.elements
            r33 = r0
            r0 = r33
            int r0 = r0.size
            r33 = r0
            if (r33 == 0) goto L_0x0363
            r0 = r38
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.XmlReader$Element> r0 = r0.elements
            r33 = r0
            java.lang.Object r20 = r33.peek()
            com.badlogic.gdx.utils.XmlReader$Element r20 = (com.badlogic.gdx.utils.XmlReader.Element) r20
            r0 = r38
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.XmlReader$Element> r0 = r0.elements
            r33 = r0
            r33.clear()
            com.badlogic.gdx.utils.SerializationException r33 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r34 = new java.lang.StringBuilder
            r34.<init>()
            java.lang.String r35 = "Error parsing XML, unclosed element: "
            java.lang.StringBuilder r34 = r34.append(r35)
            java.lang.String r35 = r20.getName()
            java.lang.StringBuilder r34 = r34.append(r35)
            java.lang.String r34 = r34.toString()
            r33.<init>((java.lang.String) r34)
            throw r33
        L_0x0363:
            r0 = r38
            com.badlogic.gdx.utils.XmlReader$Element r0 = r0.root
            r30 = r0
            r33 = 0
            r0 = r33
            r1 = r38
            r1.root = r0
            return r30
        L_0x0372:
            r18 = r19
            goto L_0x0280
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.XmlReader.parse(char[], int, int):com.badlogic.gdx.utils.XmlReader$Element");
    }

    private static byte[] init__xml_actions_0() {
        return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1, 7, 2, 0, 6, 2, 1, 4, 2, 2, 4};
    }

    private static byte[] init__xml_key_offsets_0() {
        return new byte[]{0, 0, 4, 9, 14, 20, 26, 30, 35, 36, 37, 42, 46, 50, 51, 52, 56, 57, 62, 67, 73, 79, 83, 88, 89, 90, 95, 99, 103, 104, 108, 109, 110, 111, 112, 115};
    }

    private static char[] init__xml_trans_keys_0() {
        return new char[]{' ', '<', 9, 13, ' ', '/', '>', 9, 13, ' ', '/', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '=', 9, 13, ' ', '\"', '\'', 9, 13, '\"', '\"', ' ', '/', '>', 9, 13, ' ', '>', 9, 13, ' ', '>', 9, 13, '\'', '\'', ' ', '<', 9, 13, '<', ' ', '/', '>', 9, 13, ' ', '/', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '/', '=', '>', 9, 13, ' ', '=', 9, 13, ' ', '\"', '\'', 9, 13, '\"', '\"', ' ', '/', '>', 9, 13, ' ', '>', 9, 13, ' ', '>', 9, 13, '<', ' ', '/', 9, 13, '>', '>', '\'', '\'', ' ', 9, 13, 0};
    }

    private static byte[] init__xml_single_lengths_0() {
        return new byte[]{0, 2, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 1, 2, 1, 3, 3, 4, 4, 2, 3, 1, 1, 3, 2, 2, 1, 2, 1, 1, 1, 1, 1, 0};
    }

    private static byte[] init__xml_range_lengths_0() {
        return new byte[]{0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0};
    }

    private static short[] init__xml_index_offsets_0() {
        return new short[]{0, 0, 4, 9, 14, 20, 26, 30, 35, 37, 39, 44, 48, 52, 54, 56, 60, 62, 67, 72, 78, 84, 88, 93, 95, 97, 102, 106, 110, 112, 116, 118, 120, 122, 124, 127};
    }

    private static byte[] init__xml_indicies_0() {
        byte[] bArr = new byte[Input.Keys.CONTROL_LEFT];
        // fill-array-data instruction
        bArr[0] = 0;
        bArr[1] = 2;
        bArr[2] = 0;
        bArr[3] = 1;
        bArr[4] = 2;
        bArr[5] = 1;
        bArr[6] = 1;
        bArr[7] = 2;
        bArr[8] = 3;
        bArr[9] = 5;
        bArr[10] = 6;
        bArr[11] = 7;
        bArr[12] = 5;
        bArr[13] = 4;
        bArr[14] = 9;
        bArr[15] = 10;
        bArr[16] = 1;
        bArr[17] = 11;
        bArr[18] = 9;
        bArr[19] = 8;
        bArr[20] = 13;
        bArr[21] = 1;
        bArr[22] = 14;
        bArr[23] = 1;
        bArr[24] = 13;
        bArr[25] = 12;
        bArr[26] = 15;
        bArr[27] = 16;
        bArr[28] = 15;
        bArr[29] = 1;
        bArr[30] = 16;
        bArr[31] = 17;
        bArr[32] = 18;
        bArr[33] = 16;
        bArr[34] = 1;
        bArr[35] = 20;
        bArr[36] = 19;
        bArr[37] = 22;
        bArr[38] = 21;
        bArr[39] = 9;
        bArr[40] = 10;
        bArr[41] = 11;
        bArr[42] = 9;
        bArr[43] = 1;
        bArr[44] = 23;
        bArr[45] = 24;
        bArr[46] = 23;
        bArr[47] = 1;
        bArr[48] = 25;
        bArr[49] = 11;
        bArr[50] = 25;
        bArr[51] = 1;
        bArr[52] = 20;
        bArr[53] = 26;
        bArr[54] = 22;
        bArr[55] = 27;
        bArr[56] = 29;
        bArr[57] = 30;
        bArr[58] = 29;
        bArr[59] = 28;
        bArr[60] = 32;
        bArr[61] = 31;
        bArr[62] = 30;
        bArr[63] = 34;
        bArr[64] = 1;
        bArr[65] = 30;
        bArr[66] = 33;
        bArr[67] = 36;
        bArr[68] = 37;
        bArr[69] = 38;
        bArr[70] = 36;
        bArr[71] = 35;
        bArr[72] = 40;
        bArr[73] = 41;
        bArr[74] = 1;
        bArr[75] = 42;
        bArr[76] = 40;
        bArr[77] = 39;
        bArr[78] = 44;
        bArr[79] = 1;
        bArr[80] = 45;
        bArr[81] = 1;
        bArr[82] = 44;
        bArr[83] = 43;
        bArr[84] = 46;
        bArr[85] = 47;
        bArr[86] = 46;
        bArr[87] = 1;
        bArr[88] = 47;
        bArr[89] = 48;
        bArr[90] = 49;
        bArr[91] = 47;
        bArr[92] = 1;
        bArr[93] = 51;
        bArr[94] = 50;
        bArr[95] = 53;
        bArr[96] = 52;
        bArr[97] = 40;
        bArr[98] = 41;
        bArr[99] = 42;
        bArr[100] = 40;
        bArr[101] = 1;
        bArr[102] = 54;
        bArr[103] = 55;
        bArr[104] = 54;
        bArr[105] = 1;
        bArr[106] = 56;
        bArr[107] = 42;
        bArr[108] = 56;
        bArr[109] = 1;
        bArr[110] = 57;
        bArr[111] = 1;
        bArr[112] = 57;
        bArr[113] = 34;
        bArr[114] = 57;
        bArr[115] = 1;
        bArr[116] = 1;
        bArr[117] = 58;
        bArr[118] = 59;
        bArr[119] = 58;
        bArr[120] = 51;
        bArr[121] = 60;
        bArr[122] = 53;
        bArr[123] = 61;
        bArr[124] = 62;
        bArr[125] = 62;
        bArr[126] = 1;
        bArr[127] = 1;
        bArr[128] = 0;
        return bArr;
    }

    private static byte[] init__xml_trans_targs_0() {
        return new byte[]{1, 0, 2, 3, 3, 4, 11, 34, 5, 4, 11, 34, 5, 6, 7, 6, 7, 8, 13, 9, 10, 9, 10, 12, 34, 12, 14, 14, 16, 15, 17, 16, 17, 18, 30, 18, 19, 26, 28, 20, 19, 26, 28, 20, 21, 22, 21, 22, 23, 32, 24, 25, 24, 25, 27, 28, 27, 29, 31, 35, 33, 33, 34};
    }

    private static byte[] init__xml_trans_actions_0() {
        return new byte[]{0, 0, 0, 1, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 1, 0, 1, 0, 0, 0, 15, 1, 0, 0, 3, 3, 20, 1, 0, 0, 9, 0, 11, 11, 0, 0, 0, 0, 1, 17, 0, 13, 5, 23, 0, 0, 0, 7, 1, 0, 0};
    }

    /* access modifiers changed from: protected */
    public void open(String name) {
        Element child = new Element(name, this.current);
        Element parent = this.current;
        if (parent != null) {
            parent.addChild(child);
        }
        this.elements.add(child);
        this.current = child;
    }

    /* access modifiers changed from: protected */
    public void attribute(String name, String value) {
        this.current.setAttribute(name, value);
    }

    /* access modifiers changed from: protected */
    public String entity(String name) {
        if (name.equals("lt")) {
            return "<";
        }
        if (name.equals("gt")) {
            return ">";
        }
        if (name.equals("amp")) {
            return "&";
        }
        if (name.equals("apos")) {
            return "'";
        }
        if (name.equals("quot")) {
            return "\"";
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void text(String text) {
        String existing = this.current.getText();
        Element element = this.current;
        if (existing != null) {
            text = existing + text;
        }
        element.setText(text);
    }

    /* access modifiers changed from: protected */
    public void close() {
        this.root = this.elements.pop();
        this.current = this.elements.size > 0 ? this.elements.peek() : null;
    }

    public static class Element {
        private ObjectMap<String, String> attributes;
        private Array<Element> children;
        private final String name;
        private Element parent;
        private String text;

        public Element(String name2, Element parent2) {
            this.name = name2;
            this.parent = parent2;
        }

        public String getName() {
            return this.name;
        }

        public ObjectMap<String, String> getAttributes() {
            return this.attributes;
        }

        public String getAttribute(String name2) {
            if (this.attributes == null) {
                throw new GdxRuntimeException("Element " + name2 + " doesn't have attribute: " + name2);
            }
            String value = this.attributes.get(name2);
            if (value != null) {
                return value;
            }
            throw new GdxRuntimeException("Element " + name2 + " doesn't have attribute: " + name2);
        }

        public String getAttribute(String name2, String defaultValue) {
            String value;
            if (this.attributes == null || (value = this.attributes.get(name2)) == null) {
                return defaultValue;
            }
            return value;
        }

        public void setAttribute(String name2, String value) {
            if (this.attributes == null) {
                this.attributes = new ObjectMap<>(8);
            }
            this.attributes.put(name2, value);
        }

        public int getChildCount() {
            if (this.children == null) {
                return 0;
            }
            return this.children.size;
        }

        public Element getChild(int i) {
            if (this.children != null) {
                return this.children.get(i);
            }
            throw new GdxRuntimeException("Element has no children: " + this.name);
        }

        public void addChild(Element element) {
            if (this.children == null) {
                this.children = new Array<>(8);
            }
            this.children.add(element);
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text2) {
            this.text = text2;
        }

        public void removeChild(int index) {
            if (this.children != null) {
                this.children.removeIndex(index);
            }
        }

        public void removeChild(Element child) {
            if (this.children != null) {
                this.children.removeValue(child, true);
            }
        }

        public void remove() {
            this.parent.removeChild(this);
        }

        public Element getParent() {
            return this.parent;
        }

        public String toString() {
            return toString("");
        }

        public String toString(String indent) {
            StringBuilder buffer = new StringBuilder(128);
            buffer.append(indent);
            buffer.append('<');
            buffer.append(this.name);
            if (this.attributes != null) {
                Iterator i$ = this.attributes.entries().iterator();
                while (i$.hasNext()) {
                    ObjectMap.Entry<String, String> entry = i$.next();
                    buffer.append(' ');
                    buffer.append((String) entry.key);
                    buffer.append("=\"");
                    buffer.append((String) entry.value);
                    buffer.append('\"');
                }
            }
            if (this.children == null && (this.text == null || this.text.length() == 0)) {
                buffer.append("/>");
            } else {
                buffer.append(">\n");
                String childIndent = indent + 9;
                if (this.text != null && this.text.length() > 0) {
                    buffer.append(childIndent);
                    buffer.append(this.text);
                    buffer.append(10);
                }
                if (this.children != null) {
                    Iterator i$2 = this.children.iterator();
                    while (i$2.hasNext()) {
                        buffer.append(i$2.next().toString(childIndent));
                        buffer.append(10);
                    }
                }
                buffer.append(indent);
                buffer.append("</");
                buffer.append(this.name);
                buffer.append('>');
            }
            return buffer.toString();
        }

        public Element getChildByName(String name2) {
            if (this.children == null) {
                return null;
            }
            for (int i = 0; i < this.children.size; i++) {
                Element element = this.children.get(i);
                if (element.name.equals(name2)) {
                    return element;
                }
            }
            return null;
        }

        public Element getChildByNameRecursive(String name2) {
            if (this.children == null) {
                return null;
            }
            for (int i = 0; i < this.children.size; i++) {
                Element element = this.children.get(i);
                if (element.name.equals(name2)) {
                    return element;
                }
                Element found = element.getChildByNameRecursive(name2);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }

        public Array<Element> getChildrenByName(String name2) {
            Array<Element> result = new Array<>();
            if (this.children != null) {
                for (int i = 0; i < this.children.size; i++) {
                    Element child = this.children.get(i);
                    if (child.name.equals(name2)) {
                        result.add(child);
                    }
                }
            }
            return result;
        }

        public Array<Element> getChildrenByNameRecursively(String name2) {
            Array<Element> result = new Array<>();
            getChildrenByNameRecursively(name2, result);
            return result;
        }

        private void getChildrenByNameRecursively(String name2, Array<Element> result) {
            if (this.children != null) {
                for (int i = 0; i < this.children.size; i++) {
                    Element child = this.children.get(i);
                    if (child.name.equals(name2)) {
                        result.add(child);
                    }
                    child.getChildrenByNameRecursively(name2, result);
                }
            }
        }

        public float getFloatAttribute(String name2) {
            return Float.parseFloat(getAttribute(name2));
        }

        public float getFloatAttribute(String name2, float defaultValue) {
            String value = getAttribute(name2, (String) null);
            return value == null ? defaultValue : Float.parseFloat(value);
        }

        public int getIntAttribute(String name2) {
            return Integer.parseInt(getAttribute(name2));
        }

        public int getIntAttribute(String name2, int defaultValue) {
            String value = getAttribute(name2, (String) null);
            return value == null ? defaultValue : Integer.parseInt(value);
        }

        public boolean getBooleanAttribute(String name2) {
            return Boolean.parseBoolean(getAttribute(name2));
        }

        public boolean getBooleanAttribute(String name2, boolean defaultValue) {
            String value = getAttribute(name2, (String) null);
            return value == null ? defaultValue : Boolean.parseBoolean(value);
        }

        public String get(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return value;
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public String get(String name2, String defaultValue) {
            String value;
            if (this.attributes != null && (value = this.attributes.get(name2)) != null) {
                return value;
            }
            Element child = getChildByName(name2);
            if (child == null) {
                return defaultValue;
            }
            String value2 = child.getText();
            if (value2 == null) {
                return defaultValue;
            }
            return value2;
        }

        public int getInt(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return Integer.parseInt(value);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public int getInt(String name2, int defaultValue) {
            String value = get(name2, (String) null);
            return value == null ? defaultValue : Integer.parseInt(value);
        }

        public float getFloat(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return Float.parseFloat(value);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public float getFloat(String name2, float defaultValue) {
            String value = get(name2, (String) null);
            return value == null ? defaultValue : Float.parseFloat(value);
        }

        public boolean getBoolean(String name2) {
            String value = get(name2, (String) null);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
            throw new GdxRuntimeException("Element " + this.name + " doesn't have attribute or child: " + name2);
        }

        public boolean getBoolean(String name2, boolean defaultValue) {
            String value = get(name2, (String) null);
            return value == null ? defaultValue : Boolean.parseBoolean(value);
        }
    }
}
