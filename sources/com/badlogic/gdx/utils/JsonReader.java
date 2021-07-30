package com.badlogic.gdx.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonReader {
    private static final byte[] _json_actions = init__json_actions_0();
    private static final byte[] _json_eof_actions = init__json_eof_actions_0();
    private static final short[] _json_index_offsets = init__json_index_offsets_0();
    private static final short[] _json_key_offsets = init__json_key_offsets_0();
    private static final byte[] _json_range_lengths = init__json_range_lengths_0();
    private static final byte[] _json_single_lengths = init__json_single_lengths_0();
    private static final byte[] _json_trans_actions = init__json_trans_actions_0();
    private static final char[] _json_trans_keys = init__json_trans_keys_0();
    private static final byte[] _json_trans_targs = init__json_trans_targs_0();
    static final int json_en_array = 49;
    static final int json_en_main = 1;
    static final int json_en_object = 9;
    static final int json_error = 0;
    static final int json_first_final = 76;
    static final int json_start = 1;
    private Object current;
    private final Array elements = new Array(8);
    private Object root;

    public Object parse(String json) {
        char[] data = json.toCharArray();
        return parse(data, 0, data.length);
    }

    public Object parse(Reader reader) {
        try {
            char[] data = new char[1024];
            int offset = 0;
            while (true) {
                int length = reader.read(data, offset, data.length - offset);
                if (length == -1) {
                    break;
                } else if (length == 0) {
                    char[] newData = new char[(data.length * 2)];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    data = newData;
                } else {
                    offset += length;
                }
            }
            Object parse = parse(data, 0, offset);
            try {
                reader.close();
            } catch (IOException e) {
            }
            return parse;
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        } catch (Throwable th) {
            try {
                reader.close();
            } catch (IOException e2) {
            }
            throw th;
        }
    }

    public Object parse(InputStream input) {
        try {
            return parse((Reader) new InputStreamReader(input, "ISO-8859-1"));
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public Object parse(FileHandle file) {
        try {
            return parse(file.read());
        } catch (Exception ex) {
            throw new SerializationException("Error parsing file: " + file, ex);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v0, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v4, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v7, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v9, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v10, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v11, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v12, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v13, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v8, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v14, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v15, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v16, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v17, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: short} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: short} */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0406, code lost:
        if (r20 != 0) goto L_0x040b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0408, code lost:
        r11 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x040b, code lost:
        r32 = r32 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0411, code lost:
        if (r32 == r34) goto L_0x0416;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0413, code lost:
        r11 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0416, code lost:
        r20 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0035, code lost:
        r20 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0035, code lost:
        r20 = r20;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0604  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009c A[Catch:{ RuntimeException -> 0x015d }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0040  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object parse(char[] r46, int r47, int r48) {
        /*
            r45 = this;
            r32 = r47
            r34 = r48
            r24 = r34
            r38 = 0
            r41 = 4
            r0 = r41
            int[] r0 = new int[r0]
            r37 = r0
            r36 = 0
            com.badlogic.gdx.utils.Array r29 = new com.badlogic.gdx.utils.Array
            r41 = 8
            r0 = r29
            r1 = r41
            r0.<init>((int) r1)
            r30 = 0
            r22 = 0
            r33 = 0
            r21 = 0
            if (r21 == 0) goto L_0x002c
            java.io.PrintStream r41 = java.lang.System.out
            r41.println()
        L_0x002c:
            r20 = 1
            r38 = 0
            r18 = 0
            r11 = 0
            r39 = r38
        L_0x0035:
            switch(r11) {
                case 0: goto L_0x0059;
                case 1: goto L_0x0065;
                case 2: goto L_0x0406;
                case 3: goto L_0x0038;
                case 4: goto L_0x0416;
                default: goto L_0x0038;
            }
        L_0x0038:
            r38 = r39
        L_0x003a:
            r0 = r32
            r1 = r34
            if (r0 >= r1) goto L_0x0604
            r27 = 1
            r26 = 0
        L_0x0044:
            r0 = r26
            r1 = r32
            if (r0 >= r1) goto L_0x05c8
            char r41 = r46[r26]
            r42 = 10
            r0 = r41
            r1 = r42
            if (r0 != r1) goto L_0x0056
            int r27 = r27 + 1
        L_0x0056:
            int r26 = r26 + 1
            goto L_0x0044
        L_0x0059:
            r0 = r32
            r1 = r34
            if (r0 != r1) goto L_0x0061
            r11 = 4
            goto L_0x0035
        L_0x0061:
            if (r20 != 0) goto L_0x0065
            r11 = 5
            goto L_0x0035
        L_0x0065:
            short[] r41 = _json_key_offsets     // Catch:{ RuntimeException -> 0x015d }
            short r12 = r41[r20]     // Catch:{ RuntimeException -> 0x015d }
            short[] r41 = _json_index_offsets     // Catch:{ RuntimeException -> 0x015d }
            short r18 = r41[r20]     // Catch:{ RuntimeException -> 0x015d }
            byte[] r41 = _json_single_lengths     // Catch:{ RuntimeException -> 0x015d }
            byte r13 = r41[r20]     // Catch:{ RuntimeException -> 0x015d }
            if (r13 <= 0) goto L_0x007f
            r14 = r12
            int r41 = r12 + r13
            int r19 = r41 + -1
        L_0x0078:
            r0 = r19
            if (r0 >= r14) goto L_0x00b9
            int r12 = r12 + r13
            int r18 = r18 + r13
        L_0x007f:
            byte[] r41 = _json_range_lengths     // Catch:{ RuntimeException -> 0x015d }
            byte r13 = r41[r20]     // Catch:{ RuntimeException -> 0x015d }
            if (r13 <= 0) goto L_0x0092
            r14 = r12
            int r41 = r13 << 1
            int r41 = r41 + r12
            int r19 = r41 + -2
        L_0x008c:
            r0 = r19
            if (r0 >= r14) goto L_0x00e2
            int r18 = r18 + r13
        L_0x0092:
            byte[] r41 = _json_trans_targs     // Catch:{ RuntimeException -> 0x015d }
            byte r20 = r41[r18]     // Catch:{ RuntimeException -> 0x015d }
            byte[] r41 = _json_trans_actions     // Catch:{ RuntimeException -> 0x015d }
            byte r41 = r41[r18]     // Catch:{ RuntimeException -> 0x015d }
            if (r41 == 0) goto L_0x0406
            byte[] r41 = _json_trans_actions     // Catch:{ RuntimeException -> 0x015d }
            byte r9 = r41[r18]     // Catch:{ RuntimeException -> 0x015d }
            byte[] r41 = _json_actions     // Catch:{ RuntimeException -> 0x015d }
            int r10 = r9 + 1
            byte r16 = r41[r9]     // Catch:{ RuntimeException -> 0x015d }
            r17 = r16
        L_0x00a8:
            int r16 = r17 + -1
            if (r17 <= 0) goto L_0x0406
            byte[] r41 = _json_actions     // Catch:{ RuntimeException -> 0x015d }
            int r9 = r10 + 1
            byte r41 = r41[r10]     // Catch:{ RuntimeException -> 0x015d }
            switch(r41) {
                case 0: goto L_0x0111;
                case 1: goto L_0x0118;
                case 2: goto L_0x011b;
                case 3: goto L_0x0164;
                case 4: goto L_0x01cb;
                case 5: goto L_0x022a;
                case 6: goto L_0x0270;
                case 7: goto L_0x02b6;
                case 8: goto L_0x02f6;
                case 9: goto L_0x0369;
                case 10: goto L_0x037e;
                case 11: goto L_0x03f1;
                default: goto L_0x00b5;
            }     // Catch:{ RuntimeException -> 0x015d }
        L_0x00b5:
            r17 = r16
            r10 = r9
            goto L_0x00a8
        L_0x00b9:
            int r41 = r19 - r14
            int r41 = r41 >> 1
            int r15 = r14 + r41
            char r41 = r46[r32]     // Catch:{ RuntimeException -> 0x015d }
            char[] r42 = _json_trans_keys     // Catch:{ RuntimeException -> 0x015d }
            char r42 = r42[r15]     // Catch:{ RuntimeException -> 0x015d }
            r0 = r41
            r1 = r42
            if (r0 >= r1) goto L_0x00ce
            int r19 = r15 + -1
            goto L_0x0078
        L_0x00ce:
            char r41 = r46[r32]     // Catch:{ RuntimeException -> 0x015d }
            char[] r42 = _json_trans_keys     // Catch:{ RuntimeException -> 0x015d }
            char r42 = r42[r15]     // Catch:{ RuntimeException -> 0x015d }
            r0 = r41
            r1 = r42
            if (r0 <= r1) goto L_0x00dd
            int r14 = r15 + 1
            goto L_0x0078
        L_0x00dd:
            int r41 = r15 - r12
            int r18 = r18 + r41
            goto L_0x0092
        L_0x00e2:
            int r41 = r19 - r14
            int r41 = r41 >> 1
            r41 = r41 & -2
            int r15 = r14 + r41
            char r41 = r46[r32]     // Catch:{ RuntimeException -> 0x015d }
            char[] r42 = _json_trans_keys     // Catch:{ RuntimeException -> 0x015d }
            char r42 = r42[r15]     // Catch:{ RuntimeException -> 0x015d }
            r0 = r41
            r1 = r42
            if (r0 >= r1) goto L_0x00f9
            int r19 = r15 + -2
            goto L_0x008c
        L_0x00f9:
            char r41 = r46[r32]     // Catch:{ RuntimeException -> 0x015d }
            char[] r42 = _json_trans_keys     // Catch:{ RuntimeException -> 0x015d }
            int r43 = r15 + 1
            char r42 = r42[r43]     // Catch:{ RuntimeException -> 0x015d }
            r0 = r41
            r1 = r42
            if (r0 <= r1) goto L_0x010a
            int r14 = r15 + 2
            goto L_0x008c
        L_0x010a:
            int r41 = r15 - r12
            int r41 = r41 >> 1
            int r18 = r18 + r41
            goto L_0x0092
        L_0x0111:
            r36 = r32
            r30 = 0
            r22 = 0
            goto L_0x00b5
        L_0x0118:
            r30 = 1
            goto L_0x00b5
        L_0x011b:
            java.lang.String r28 = new java.lang.String     // Catch:{ RuntimeException -> 0x015d }
            int r41 = r32 - r36
            r0 = r28
            r1 = r46
            r2 = r36
            r3 = r41
            r0.<init>(r1, r2, r3)     // Catch:{ RuntimeException -> 0x015d }
            r36 = r32
            if (r30 == 0) goto L_0x0136
            r0 = r45
            r1 = r28
            java.lang.String r28 = r0.unescape(r1)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0136:
            if (r21 == 0) goto L_0x0154
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "name: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0154:
            r0 = r29
            r1 = r28
            r0.add(r1)     // Catch:{ RuntimeException -> 0x015d }
            goto L_0x00b5
        L_0x015d:
            r25 = move-exception
            r38 = r39
        L_0x0160:
            r33 = r25
            goto L_0x003a
        L_0x0164:
            if (r22 != 0) goto L_0x00b5
            java.lang.String r40 = new java.lang.String     // Catch:{ RuntimeException -> 0x015d }
            int r41 = r32 - r36
            r0 = r40
            r1 = r46
            r2 = r36
            r3 = r41
            r0.<init>(r1, r2, r3)     // Catch:{ RuntimeException -> 0x015d }
            r36 = r32
            if (r30 == 0) goto L_0x0181
            r0 = r45
            r1 = r40
            java.lang.String r40 = r0.unescape(r1)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0181:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x01c8
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x0191:
            if (r21 == 0) goto L_0x01bd
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "string: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "="
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r40
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x01bd:
            r0 = r45
            r1 = r28
            r2 = r40
            r0.string(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            goto L_0x00b5
        L_0x01c8:
            r28 = 0
            goto L_0x0191
        L_0x01cb:
            java.lang.String r40 = new java.lang.String     // Catch:{ RuntimeException -> 0x015d }
            int r41 = r32 - r36
            r0 = r40
            r1 = r46
            r2 = r36
            r3 = r41
            r0.<init>(r1, r2, r3)     // Catch:{ RuntimeException -> 0x015d }
            r36 = r32
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x0227
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x01ec:
            if (r21 == 0) goto L_0x0218
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "number: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "="
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            float r43 = java.lang.Float.parseFloat(r40)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0218:
            float r41 = java.lang.Float.parseFloat(r40)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r45
            r1 = r28
            r2 = r41
            r0.number(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            goto L_0x00b5
        L_0x0227:
            r28 = 0
            goto L_0x01ec
        L_0x022a:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x026d
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x023a:
            if (r21 == 0) goto L_0x025e
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "boolean: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "=true"
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x025e:
            r41 = 1
            r0 = r45
            r1 = r28
            r2 = r41
            r0.bool(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            r22 = 1
            goto L_0x00b5
        L_0x026d:
            r28 = 0
            goto L_0x023a
        L_0x0270:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x02b3
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x0280:
            if (r21 == 0) goto L_0x02a4
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "boolean: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "=false"
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x02a4:
            r41 = 0
            r0 = r45
            r1 = r28
            r2 = r41
            r0.bool(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            r22 = 1
            goto L_0x00b5
        L_0x02b3:
            r28 = 0
            goto L_0x0280
        L_0x02b6:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x02f3
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x02c6:
            if (r21 == 0) goto L_0x02e4
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "null: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x02e4:
            r41 = 0
            r0 = r45
            r1 = r28
            r2 = r41
            r0.string(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            r22 = 1
            goto L_0x00b5
        L_0x02f3:
            r28 = 0
            goto L_0x02c6
        L_0x02f6:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x0366
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x0306:
            if (r21 == 0) goto L_0x0324
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "startObject: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0324:
            r0 = r45
            r1 = r28
            r0.startObject(r1)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r37
            int r0 = r0.length     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            r0 = r39
            r1 = r41
            if (r0 != r1) goto L_0x035b
            r0 = r37
            int r0 = r0.length     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            int r41 = r41 * 2
            r0 = r41
            int[] r0 = new int[r0]     // Catch:{ RuntimeException -> 0x015d }
            r31 = r0
            r41 = 0
            r42 = 0
            r0 = r37
            int r0 = r0.length     // Catch:{ RuntimeException -> 0x015d }
            r43 = r0
            r0 = r37
            r1 = r41
            r2 = r31
            r3 = r42
            r4 = r43
            java.lang.System.arraycopy(r0, r1, r2, r3, r4)     // Catch:{ RuntimeException -> 0x015d }
            r37 = r31
        L_0x035b:
            int r38 = r39 + 1
            r37[r39] = r20     // Catch:{ RuntimeException -> 0x064c }
            r20 = 9
            r11 = 2
            r39 = r38
            goto L_0x0035
        L_0x0366:
            r28 = 0
            goto L_0x0306
        L_0x0369:
            if (r21 == 0) goto L_0x0372
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = "endObject"
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0372:
            r45.pop()     // Catch:{ RuntimeException -> 0x015d }
            int r38 = r39 + -1
            r20 = r37[r38]     // Catch:{ RuntimeException -> 0x064c }
            r11 = 2
            r39 = r38
            goto L_0x0035
        L_0x037e:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x03ee
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x038e:
            if (r21 == 0) goto L_0x03ac
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "startArray: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x03ac:
            r0 = r45
            r1 = r28
            r0.startArray(r1)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r37
            int r0 = r0.length     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            r0 = r39
            r1 = r41
            if (r0 != r1) goto L_0x03e3
            r0 = r37
            int r0 = r0.length     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            int r41 = r41 * 2
            r0 = r41
            int[] r0 = new int[r0]     // Catch:{ RuntimeException -> 0x015d }
            r31 = r0
            r41 = 0
            r42 = 0
            r0 = r37
            int r0 = r0.length     // Catch:{ RuntimeException -> 0x015d }
            r43 = r0
            r0 = r37
            r1 = r41
            r2 = r31
            r3 = r42
            r4 = r43
            java.lang.System.arraycopy(r0, r1, r2, r3, r4)     // Catch:{ RuntimeException -> 0x015d }
            r37 = r31
        L_0x03e3:
            int r38 = r39 + 1
            r37[r39] = r20     // Catch:{ RuntimeException -> 0x064c }
            r20 = 49
            r11 = 2
            r39 = r38
            goto L_0x0035
        L_0x03ee:
            r28 = 0
            goto L_0x038e
        L_0x03f1:
            if (r21 == 0) goto L_0x03fa
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = "endArray"
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x03fa:
            r45.pop()     // Catch:{ RuntimeException -> 0x015d }
            int r38 = r39 + -1
            r20 = r37[r38]     // Catch:{ RuntimeException -> 0x064c }
            r11 = 2
            r39 = r38
            goto L_0x0035
        L_0x0406:
            if (r20 != 0) goto L_0x040b
            r11 = 5
            goto L_0x0035
        L_0x040b:
            int r32 = r32 + 1
            r0 = r32
            r1 = r34
            if (r0 == r1) goto L_0x0416
            r11 = 1
            goto L_0x0035
        L_0x0416:
            r0 = r32
            r1 = r24
            if (r0 != r1) goto L_0x0038
            byte[] r41 = _json_eof_actions     // Catch:{ RuntimeException -> 0x015d }
            byte r5 = r41[r20]     // Catch:{ RuntimeException -> 0x015d }
            byte[] r41 = _json_actions     // Catch:{ RuntimeException -> 0x015d }
            int r6 = r5 + 1
            byte r7 = r41[r5]     // Catch:{ RuntimeException -> 0x015d }
            r8 = r7
        L_0x0427:
            int r7 = r8 + -1
            if (r8 <= 0) goto L_0x0038
            byte[] r41 = _json_actions     // Catch:{ RuntimeException -> 0x015d }
            int r5 = r6 + 1
            byte r41 = r41[r6]     // Catch:{ RuntimeException -> 0x015d }
            switch(r41) {
                case 3: goto L_0x0437;
                case 4: goto L_0x049d;
                case 5: goto L_0x04fc;
                case 6: goto L_0x0542;
                case 7: goto L_0x0588;
                default: goto L_0x0434;
            }     // Catch:{ RuntimeException -> 0x015d }
        L_0x0434:
            r8 = r7
            r6 = r5
            goto L_0x0427
        L_0x0437:
            if (r22 != 0) goto L_0x0434
            java.lang.String r40 = new java.lang.String     // Catch:{ RuntimeException -> 0x015d }
            int r41 = r32 - r36
            r0 = r40
            r1 = r46
            r2 = r36
            r3 = r41
            r0.<init>(r1, r2, r3)     // Catch:{ RuntimeException -> 0x015d }
            r36 = r32
            if (r30 == 0) goto L_0x0454
            r0 = r45
            r1 = r40
            java.lang.String r40 = r0.unescape(r1)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0454:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x049a
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x0464:
            if (r21 == 0) goto L_0x0490
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "string: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "="
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r40
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0490:
            r0 = r45
            r1 = r28
            r2 = r40
            r0.string(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            goto L_0x0434
        L_0x049a:
            r28 = 0
            goto L_0x0464
        L_0x049d:
            java.lang.String r40 = new java.lang.String     // Catch:{ RuntimeException -> 0x015d }
            int r41 = r32 - r36
            r0 = r40
            r1 = r46
            r2 = r36
            r3 = r41
            r0.<init>(r1, r2, r3)     // Catch:{ RuntimeException -> 0x015d }
            r36 = r32
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x04f9
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x04be:
            if (r21 == 0) goto L_0x04ea
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "number: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "="
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            float r43 = java.lang.Float.parseFloat(r40)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x04ea:
            float r41 = java.lang.Float.parseFloat(r40)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r45
            r1 = r28
            r2 = r41
            r0.number(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            goto L_0x0434
        L_0x04f9:
            r28 = 0
            goto L_0x04be
        L_0x04fc:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x053f
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x050c:
            if (r21 == 0) goto L_0x0530
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "boolean: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "=true"
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0530:
            r41 = 1
            r0 = r45
            r1 = r28
            r2 = r41
            r0.bool(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            r22 = 1
            goto L_0x0434
        L_0x053f:
            r28 = 0
            goto L_0x050c
        L_0x0542:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x0585
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x0552:
            if (r21 == 0) goto L_0x0576
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "boolean: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "=false"
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x0576:
            r41 = 0
            r0 = r45
            r1 = r28
            r2 = r41
            r0.bool(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            r22 = 1
            goto L_0x0434
        L_0x0585:
            r28 = 0
            goto L_0x0552
        L_0x0588:
            r0 = r29
            int r0 = r0.size     // Catch:{ RuntimeException -> 0x015d }
            r41 = r0
            if (r41 <= 0) goto L_0x05c5
            java.lang.Object r41 = r29.pop()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r41 = (java.lang.String) r41     // Catch:{ RuntimeException -> 0x015d }
            r28 = r41
        L_0x0598:
            if (r21 == 0) goto L_0x05b6
            java.io.PrintStream r41 = java.lang.System.out     // Catch:{ RuntimeException -> 0x015d }
            java.lang.StringBuilder r42 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x015d }
            r42.<init>()     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r43 = "null: "
            java.lang.StringBuilder r42 = r42.append(r43)     // Catch:{ RuntimeException -> 0x015d }
            r0 = r42
            r1 = r28
            java.lang.StringBuilder r42 = r0.append(r1)     // Catch:{ RuntimeException -> 0x015d }
            java.lang.String r42 = r42.toString()     // Catch:{ RuntimeException -> 0x015d }
            r41.println(r42)     // Catch:{ RuntimeException -> 0x015d }
        L_0x05b6:
            r41 = 0
            r0 = r45
            r1 = r28
            r2 = r41
            r0.string(r1, r2)     // Catch:{ RuntimeException -> 0x015d }
            r22 = 1
            goto L_0x0434
        L_0x05c5:
            r28 = 0
            goto L_0x0598
        L_0x05c8:
            com.badlogic.gdx.utils.SerializationException r41 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r42 = new java.lang.StringBuilder
            r42.<init>()
            java.lang.String r43 = "Error parsing JSON on line "
            java.lang.StringBuilder r42 = r42.append(r43)
            r0 = r42
            r1 = r27
            java.lang.StringBuilder r42 = r0.append(r1)
            java.lang.String r43 = " near: "
            java.lang.StringBuilder r42 = r42.append(r43)
            java.lang.String r43 = new java.lang.String
            int r44 = r34 - r32
            r0 = r43
            r1 = r46
            r2 = r32
            r3 = r44
            r0.<init>(r1, r2, r3)
            java.lang.StringBuilder r42 = r42.append(r43)
            java.lang.String r42 = r42.toString()
            r0 = r41
            r1 = r42
            r2 = r33
            r0.<init>(r1, r2)
            throw r41
        L_0x0604:
            r0 = r45
            com.badlogic.gdx.utils.Array r0 = r0.elements
            r41 = r0
            r0 = r41
            int r0 = r0.size
            r41 = r0
            if (r41 == 0) goto L_0x063d
            r0 = r45
            com.badlogic.gdx.utils.Array r0 = r0.elements
            r41 = r0
            java.lang.Object r23 = r41.peek()
            r0 = r45
            com.badlogic.gdx.utils.Array r0 = r0.elements
            r41 = r0
            r41.clear()
            r0 = r23
            boolean r0 = r0 instanceof com.badlogic.gdx.utils.OrderedMap
            r41 = r0
            if (r41 == 0) goto L_0x0635
            com.badlogic.gdx.utils.SerializationException r41 = new com.badlogic.gdx.utils.SerializationException
            java.lang.String r42 = "Error parsing JSON, unmatched brace."
            r41.<init>((java.lang.String) r42)
            throw r41
        L_0x0635:
            com.badlogic.gdx.utils.SerializationException r41 = new com.badlogic.gdx.utils.SerializationException
            java.lang.String r42 = "Error parsing JSON, unmatched bracket."
            r41.<init>((java.lang.String) r42)
            throw r41
        L_0x063d:
            r0 = r45
            java.lang.Object r0 = r0.root
            r35 = r0
            r41 = 0
            r0 = r41
            r1 = r45
            r1.root = r0
            return r35
        L_0x064c:
            r25 = move-exception
            goto L_0x0160
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.JsonReader.parse(char[], int, int):java.lang.Object");
    }

    private static byte[] init__json_actions_0() {
        return new byte[]{0, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 8, 1, 9, 1, 10, 1, 11, 2, 0, 2, 2, 0, 3, 2, 3, 9, 2, 3, 11, 2, 4, 9, 2, 4, 11, 2, 5, 3, 2, 6, 3, 2, 7, 3, 3, 5, 3, 9, 3, 5, 3, 11, 3, 6, 3, 9, 3, 6, 3, 11, 3, 7, 3, 9, 3, 7, 3, 11};
    }

    private static short[] init__json_key_offsets_0() {
        return new short[]{0, 0, 18, 20, 22, 31, 33, 35, 39, 41, 56, 58, 60, 64, 82, 84, 86, 91, 105, 112, 114, 123, 125, 133, 137, 139, 145, 154, 161, 163, 173, 175, 184, 188, 190, 197, 205, 213, 221, 229, 236, 244, 252, 260, 267, 275, 283, 291, 298, 307, 327, 329, 331, 336, 355, 362, 364, 374, 376, 385, 389, 391, 398, 406, 414, 422, 430, 437, 445, 453, 461, 468, 476, 484, 492, 499, 508, 511, 518, 526, 533, 538, 546, 554, 562, 570, 577, 585, 593, 601, 608, 616, 624, 632, 639, 639};
    }

    private static char[] init__json_trans_keys_0() {
        return new char[]{' ', '\"', '$', '-', '[', '_', 'f', 'n', 't', '{', 9, 13, '0', '9', 'A', 'Z', 'a', 'z', '\"', '\\', '\"', '\\', '\"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', '0', '9', '0', '9', '+', '-', '0', '9', '0', '9', ' ', '\"', '$', ',', '-', '_', '}', 9, 13, '0', '9', 'A', 'Z', 'a', 'z', '\"', '\\', '\"', '\\', ' ', ':', 9, 13, ' ', '\"', '$', '-', '[', '_', 'f', 'n', 't', '{', 9, 13, '0', '9', 'A', 'Z', 'a', 'z', '\"', '\\', '\"', '\\', ' ', ',', '}', 9, 13, ' ', '\"', '$', '-', '_', '}', 9, 13, '0', '9', 'A', 'Z', 'a', 'z', ' ', ',', ':', ']', '}', 9, 13, '0', '9', ' ', '.', ':', 'E', 'e', 9, 13, '0', '9', '0', '9', ' ', ':', 'E', 'e', 9, 13, '0', '9', '+', '-', '0', '9', '0', '9', ' ', ':', 9, 13, '0', '9', '\"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', ' ', ',', ':', ']', '}', 9, 13, '0', '9', ' ', ',', '.', 'E', 'e', '}', 9, 13, '0', '9', '0', '9', ' ', ',', 'E', 'e', '}', 9, 13, '0', '9', '+', '-', '0', '9', '0', '9', ' ', ',', '}', 9, 13, '0', '9', ' ', ',', ':', ']', 'a', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', 's', '}', 9, 13, ' ', ',', ':', ']', 'e', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', ',', ':', ']', 'u', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', ',', ':', ']', 'r', '}', 9, 13, ' ', ',', ':', ']', 'u', '}', 9, 13, ' ', ',', ':', ']', 'e', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, '\"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', ' ', '\"', '$', ',', '-', '[', ']', '_', 'f', 'n', 't', '{', 9, 13, '0', '9', 'A', 'Z', 'a', 'z', '\"', '\\', '\"', '\\', ' ', ',', ']', 9, 13, ' ', '\"', '$', '-', '[', ']', '_', 'f', 'n', 't', '{', 9, 13, '0', '9', 'A', 'Z', 'a', 'z', ' ', ',', ':', ']', '}', 9, 13, '0', '9', ' ', ',', '.', 'E', ']', 'e', 9, 13, '0', '9', '0', '9', ' ', ',', 'E', ']', 'e', 9, 13, '0', '9', '+', '-', '0', '9', '0', '9', ' ', ',', ']', 9, 13, '0', '9', ' ', ',', ':', ']', 'a', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', 's', '}', 9, 13, ' ', ',', ':', ']', 'e', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', ',', ':', ']', 'u', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', ',', ':', ']', 'r', '}', 9, 13, ' ', ',', ':', ']', 'u', '}', 9, 13, ' ', ',', ':', ']', 'e', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, '\"', '/', '\\', 'b', 'f', 'n', 'r', 't', 'u', ' ', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', '.', 'E', 'e', 9, 13, '0', '9', ' ', 'E', 'e', 9, 13, '0', '9', ' ', 9, 13, '0', '9', ' ', ',', ':', ']', 'a', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', 's', '}', 9, 13, ' ', ',', ':', ']', 'e', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', ',', ':', ']', 'u', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', 'l', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, ' ', ',', ':', ']', 'r', '}', 9, 13, ' ', ',', ':', ']', 'u', '}', 9, 13, ' ', ',', ':', ']', 'e', '}', 9, 13, ' ', ',', ':', ']', '}', 9, 13, 0};
    }

    private static byte[] init__json_single_lengths_0() {
        return new byte[]{0, 10, 2, 2, 7, 0, 0, 2, 0, 7, 2, 2, 2, 10, 2, 2, 3, 6, 5, 0, 5, 0, 4, 2, 0, 2, 7, 5, 0, 6, 0, 5, 2, 0, 3, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 7, 12, 2, 2, 3, 11, 5, 0, 6, 0, 5, 2, 0, 3, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 7, 1, 5, 4, 3, 1, 6, 6, 6, 6, 5, 6, 6, 6, 5, 6, 6, 6, 5, 0, 0};
    }

    private static byte[] init__json_range_lengths_0() {
        return new byte[]{0, 4, 0, 0, 1, 1, 1, 1, 1, 4, 0, 0, 1, 4, 0, 0, 1, 4, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 0, 0, 1, 4, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0};
    }

    private static short[] init__json_index_offsets_0() {
        return new short[]{0, 0, 15, 18, 21, 30, 32, 34, 38, 40, 52, 55, 58, 62, 77, 80, 83, 88, 99, 106, 108, 116, 118, 125, 129, 131, 136, 145, 152, 154, 163, 165, 173, 177, 179, 185, 193, 201, 209, 217, 224, 232, 240, 248, 255, 263, 271, 279, 286, 295, 312, 315, 318, 323, 339, 346, 348, 357, 359, 367, 371, 373, 379, 387, 395, 403, 411, 418, 426, 434, 442, 449, 457, 465, 473, 480, 489, 492, 499, 506, 512, 516, 524, 532, 540, 548, 555, 563, 571, 579, 586, 594, 602, 610, 617, 618};
    }

    private static byte[] init__json_trans_targs_0() {
        return new byte[]{1, 2, 77, 5, 76, 77, 81, 86, 90, 76, 1, 78, 77, 77, 0, 76, 4, 3, 76, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 78, 0, 79, 0, 8, 8, 80, 0, 80, 0, 9, 10, 18, 17, 19, 18, 94, 9, 18, 18, 18, 0, 12, 48, 11, 12, 48, 11, 12, 13, 12, 0, 13, 14, 27, 28, 16, 27, 35, 40, 44, 16, 13, 29, 27, 27, 0, 16, 26, 15, 16, 26, 15, 16, 17, 94, 16, 0, 17, 10, 18, 19, 18, 94, 17, 18, 18, 18, 0, 12, 0, 13, 0, 0, 12, 18, 20, 0, 12, 21, 13, 23, 23, 12, 20, 0, 22, 0, 12, 13, 23, 23, 12, 22, 0, 24, 24, 25, 0, 25, 0, 12, 13, 12, 25, 0, 15, 15, 15, 15, 15, 15, 15, 15, 0, 16, 17, 0, 0, 94, 16, 27, 29, 0, 16, 17, 30, 32, 32, 94, 16, 29, 0, 31, 0, 16, 17, 32, 32, 94, 16, 31, 0, 33, 33, 34, 0, 34, 0, 16, 17, 94, 16, 34, 0, 16, 17, 0, 0, 36, 94, 16, 27, 16, 17, 0, 0, 37, 94, 16, 27, 16, 17, 0, 0, 38, 94, 16, 27, 16, 17, 0, 0, 39, 94, 16, 27, 16, 17, 0, 0, 94, 16, 27, 16, 17, 0, 0, 41, 94, 16, 27, 16, 17, 0, 0, 42, 94, 16, 27, 16, 17, 0, 0, 43, 94, 16, 27, 16, 17, 0, 0, 94, 16, 27, 16, 17, 0, 0, 45, 94, 16, 27, 16, 17, 0, 0, 46, 94, 16, 27, 16, 17, 0, 0, 47, 94, 16, 27, 16, 17, 0, 0, 94, 16, 27, 11, 11, 11, 11, 11, 11, 11, 11, 0, 49, 50, 54, 53, 55, 52, 95, 54, 62, 67, 71, 52, 49, 56, 54, 54, 0, 52, 75, 51, 52, 75, 51, 52, 53, 95, 52, 0, 53, 50, 54, 55, 52, 95, 54, 62, 67, 71, 52, 53, 56, 54, 54, 0, 52, 53, 0, 95, 0, 52, 54, 56, 0, 52, 53, 57, 59, 95, 59, 52, 56, 0, 58, 0, 52, 53, 59, 95, 59, 52, 58, 0, 60, 60, 61, 0, 61, 0, 52, 53, 95, 52, 61, 0, 52, 53, 0, 95, 63, 0, 52, 54, 52, 53, 0, 95, 64, 0, 52, 54, 52, 53, 0, 95, 65, 0, 52, 54, 52, 53, 0, 95, 66, 0, 52, 54, 52, 53, 0, 95, 0, 52, 54, 52, 53, 0, 95, 68, 0, 52, 54, 52, 53, 0, 95, 69, 0, 52, 54, 52, 53, 0, 95, 70, 0, 52, 54, 52, 53, 0, 95, 0, 52, 54, 52, 53, 0, 95, 72, 0, 52, 54, 52, 53, 0, 95, 73, 0, 52, 54, 52, 53, 0, 95, 74, 0, 52, 54, 52, 53, 0, 95, 0, 52, 54, 51, 51, 51, 51, 51, 51, 51, 51, 0, 76, 76, 0, 76, 0, 0, 0, 0, 76, 77, 76, 6, 7, 7, 76, 78, 0, 76, 7, 7, 76, 79, 0, 76, 76, 80, 0, 76, 0, 0, 0, 82, 0, 76, 77, 76, 0, 0, 0, 83, 0, 76, 77, 76, 0, 0, 0, 84, 0, 76, 77, 76, 0, 0, 0, 85, 0, 76, 77, 76, 0, 0, 0, 0, 76, 77, 76, 0, 0, 0, 87, 0, 76, 77, 76, 0, 0, 0, 88, 0, 76, 77, 76, 0, 0, 0, 89, 0, 76, 77, 76, 0, 0, 0, 0, 76, 77, 76, 0, 0, 0, 91, 0, 76, 77, 76, 0, 0, 0, 92, 0, 76, 77, 76, 0, 0, 0, 93, 0, 76, 77, 76, 0, 0, 0, 0, 76, 77, 0, 0, 0};
    }

    private static byte[] init__json_trans_actions_0() {
        return new byte[]{0, 0, 1, 1, 15, 1, 1, 1, 1, 11, 0, 1, 1, 1, 0, 22, 1, 1, 7, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 13, 0, 1, 1, 1, 0, 19, 1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 15, 1, 1, 1, 1, 11, 0, 1, 1, 1, 0, 22, 1, 1, 7, 0, 0, 0, 0, 13, 0, 0, 0, 0, 1, 1, 1, 13, 0, 1, 1, 1, 0, 5, 0, 5, 0, 0, 5, 0, 0, 0, 5, 0, 5, 0, 0, 5, 0, 0, 0, 0, 5, 5, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 7, 7, 0, 0, 25, 7, 0, 0, 0, 9, 9, 0, 0, 0, 31, 9, 0, 0, 0, 0, 9, 9, 0, 0, 31, 9, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 31, 9, 0, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 40, 40, 0, 0, 54, 40, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 43, 43, 0, 0, 62, 43, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 7, 7, 0, 0, 0, 25, 7, 0, 37, 37, 0, 0, 46, 37, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 1, 0, 1, 15, 17, 1, 1, 1, 1, 11, 0, 1, 1, 1, 0, 22, 1, 1, 7, 0, 0, 0, 0, 17, 0, 0, 0, 0, 1, 1, 15, 17, 1, 1, 1, 1, 11, 0, 1, 1, 1, 0, 7, 7, 0, 28, 0, 7, 0, 0, 0, 9, 9, 0, 0, 34, 0, 9, 0, 0, 0, 0, 9, 9, 0, 34, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 9, 9, 34, 9, 0, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 40, 40, 0, 58, 0, 40, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 43, 43, 0, 66, 0, 43, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 7, 7, 0, 28, 0, 0, 7, 0, 37, 37, 0, 50, 0, 37, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 7, 0, 0, 0, 0, 7, 0, 9, 0, 0, 0, 9, 0, 0, 9, 0, 0, 9, 0, 0, 9, 9, 0, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 40, 0, 0, 0, 0, 40, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 43, 0, 0, 0, 0, 43, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 7, 0, 0, 0, 0, 0, 7, 0, 37, 0, 0, 0, 0, 37, 0, 0, 0, 0};
    }

    private static byte[] init__json_eof_actions_0() {
        return new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 9, 9, 9, 7, 7, 7, 7, 40, 7, 7, 7, 43, 7, 7, 7, 37, 0, 0};
    }

    private void set(String name, Object value) {
        if (this.current instanceof OrderedMap) {
            ((OrderedMap) this.current).put(name, value);
        } else if (this.current instanceof Array) {
            ((Array) this.current).add(value);
        } else {
            this.root = value;
        }
    }

    /* access modifiers changed from: protected */
    public void startObject(String name) {
        OrderedMap value = new OrderedMap();
        if (this.current != null) {
            set(name, value);
        }
        this.elements.add(value);
        this.current = value;
    }

    /* access modifiers changed from: protected */
    public void startArray(String name) {
        Array value = new Array();
        if (this.current != null) {
            set(name, value);
        }
        this.elements.add(value);
        this.current = value;
    }

    /* access modifiers changed from: protected */
    public void pop() {
        this.root = this.elements.pop();
        this.current = this.elements.size > 0 ? this.elements.peek() : null;
    }

    /* access modifiers changed from: protected */
    public void string(String name, String value) {
        set(name, value);
    }

    /* access modifiers changed from: protected */
    public void number(String name, float value) {
        set(name, Float.valueOf(value));
    }

    /* access modifiers changed from: protected */
    public void bool(String name, boolean value) {
        set(name, Boolean.valueOf(value));
    }

    private String unescape(String value) {
        int length = value.length();
        StringBuilder buffer = new StringBuilder(length + 16);
        int i = 0;
        while (true) {
            if (i < length) {
                int i2 = i + 1;
                char c = value.charAt(i);
                if (c != '\\') {
                    buffer.append(c);
                    i = i2;
                } else if (i2 != length) {
                    i = i2 + 1;
                    char c2 = value.charAt(i2);
                    if (c2 == 'u') {
                        buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
                        i += 4;
                    } else {
                        switch (c2) {
                            case Input.Keys.f23F:
                            case Input.Keys.f45S:
                            case Input.Keys.PAGE_UP:
                                break;
                            case Input.Keys.BUTTON_C:
                                c2 = 8;
                                break;
                            case Input.Keys.BUTTON_L1:
                                c2 = 12;
                                break;
                            case Input.Keys.BUTTON_MODE:
                                c2 = 10;
                                break;
                            case 'r':
                                c2 = 13;
                                break;
                            case 't':
                                c2 = 9;
                                break;
                            default:
                                throw new SerializationException("Illegal escaped character: \\" + c2);
                        }
                        buffer.append(c2);
                    }
                }
            } else {
                int i3 = i;
            }
        }
        return buffer.toString();
    }
}
