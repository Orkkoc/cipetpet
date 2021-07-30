package com.badlogic.gdx.utils.compression.p001lz;

import java.io.IOException;

/* renamed from: com.badlogic.gdx.utils.compression.lz.BinTree */
public class BinTree extends InWindow {
    private static final int[] CrcTable = new int[256];
    static final int kBT2HashSize = 65536;
    static final int kEmptyHashValue = 0;
    static final int kHash2Size = 1024;
    static final int kHash3Offset = 1024;
    static final int kHash3Size = 65536;
    static final int kMaxValForNormalize = 1073741823;
    static final int kStartMaxLen = 1;
    boolean HASH_ARRAY = true;
    int _cutValue = 255;
    int _cyclicBufferPos;
    int _cyclicBufferSize = 0;
    int[] _hash;
    int _hashMask;
    int _hashSizeSum = 0;
    int _matchMaxLen;
    int[] _son;
    int kFixHashSize = 66560;
    int kMinMatchCheck = 4;
    int kNumHashDirectBytes = 0;

    public void SetType(int numHashBytes) {
        this.HASH_ARRAY = numHashBytes > 2;
        if (this.HASH_ARRAY) {
            this.kNumHashDirectBytes = 0;
            this.kMinMatchCheck = 4;
            this.kFixHashSize = 66560;
            return;
        }
        this.kNumHashDirectBytes = 2;
        this.kMinMatchCheck = 3;
        this.kFixHashSize = 0;
    }

    public void Init() throws IOException {
        super.Init();
        for (int i = 0; i < this._hashSizeSum; i++) {
            this._hash[i] = 0;
        }
        this._cyclicBufferPos = 0;
        ReduceOffsets(-1);
    }

    public void MovePos() throws IOException {
        int i = this._cyclicBufferPos + 1;
        this._cyclicBufferPos = i;
        if (i >= this._cyclicBufferSize) {
            this._cyclicBufferPos = 0;
        }
        super.MovePos();
        if (this._pos == kMaxValForNormalize) {
            Normalize();
        }
    }

    public boolean Create(int historySize, int keepAddBufferBefore, int matchMaxLen, int keepAddBufferAfter) {
        if (historySize > 1073741567) {
            return false;
        }
        this._cutValue = (matchMaxLen >> 1) + 16;
        super.Create(historySize + keepAddBufferBefore, matchMaxLen + keepAddBufferAfter, ((((historySize + keepAddBufferBefore) + matchMaxLen) + keepAddBufferAfter) / 2) + 256);
        this._matchMaxLen = matchMaxLen;
        int cyclicBufferSize = historySize + 1;
        if (this._cyclicBufferSize != cyclicBufferSize) {
            this._cyclicBufferSize = cyclicBufferSize;
            this._son = new int[(cyclicBufferSize * 2)];
        }
        int hs = 65536;
        if (this.HASH_ARRAY) {
            int hs2 = historySize - 1;
            int hs3 = hs2 | (hs2 >> 1);
            int hs4 = hs3 | (hs3 >> 2);
            int hs5 = hs4 | (hs4 >> 4);
            int hs6 = ((hs5 | (hs5 >> 8)) >> 1) | 65535;
            if (hs6 > 16777216) {
                hs6 >>= 1;
            }
            this._hashMask = hs6;
            hs = hs6 + 1 + this.kFixHashSize;
        }
        if (hs != this._hashSizeSum) {
            this._hashSizeSum = hs;
            this._hash = new int[hs];
        }
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: byte} */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0210, code lost:
        r25 = r28._son;
        r28._son[r23] = 0;
        r25[r22] = 0;
        r19 = r20;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int GetMatches(int[] r29) throws java.io.IOException {
        /*
            r28 = this;
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            r0 = r28
            int r0 = r0._matchMaxLen
            r26 = r0
            int r25 = r25 + r26
            r0 = r28
            int r0 = r0._streamPos
            r26 = r0
            r0 = r25
            r1 = r26
            if (r0 > r1) goto L_0x0228
            r0 = r28
            int r0 = r0._matchMaxLen
            r16 = r0
        L_0x0020:
            r19 = 0
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            r0 = r28
            int r0 = r0._cyclicBufferSize
            r26 = r0
            r0 = r25
            r1 = r26
            if (r0 <= r1) goto L_0x0248
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            r0 = r28
            int r0 = r0._cyclicBufferSize
            r26 = r0
            int r17 = r25 - r26
        L_0x0042:
            r0 = r28
            int r0 = r0._bufferOffset
            r25 = r0
            r0 = r28
            int r0 = r0._pos
            r26 = r0
            int r4 = r25 + r26
            r18 = 1
            r10 = 0
            r11 = 0
            r0 = r28
            boolean r0 = r0.HASH_ARRAY
            r25 = r0
            if (r25 == 0) goto L_0x024c
            int[] r25 = CrcTable
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            byte r26 = r26[r4]
            r0 = r26
            r0 = r0 & 255(0xff, float:3.57E-43)
            r26 = r0
            r25 = r25[r26]
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            int r27 = r4 + 1
            byte r26 = r26[r27]
            r0 = r26
            r0 = r0 & 255(0xff, float:3.57E-43)
            r26 = r0
            r24 = r25 ^ r26
            r0 = r24
            r10 = r0 & 1023(0x3ff, float:1.434E-42)
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            int r26 = r4 + 2
            byte r25 = r25[r26]
            r0 = r25
            r0 = r0 & 255(0xff, float:3.57E-43)
            r25 = r0
            int r25 = r25 << 8
            r24 = r24 ^ r25
            r25 = 65535(0xffff, float:9.1834E-41)
            r11 = r24 & r25
            int[] r25 = CrcTable
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            int r27 = r4 + 3
            byte r26 = r26[r27]
            r0 = r26
            r0 = r0 & 255(0xff, float:3.57E-43)
            r26 = r0
            r25 = r25[r26]
            int r25 = r25 << 5
            r25 = r25 ^ r24
            r0 = r28
            int r0 = r0._hashMask
            r26 = r0
            r12 = r25 & r26
        L_0x00bd:
            r0 = r28
            int[] r0 = r0._hash
            r25 = r0
            r0 = r28
            int r0 = r0.kFixHashSize
            r26 = r0
            int r26 = r26 + r12
            r5 = r25[r26]
            r0 = r28
            boolean r0 = r0.HASH_ARRAY
            r25 = r0
            if (r25 == 0) goto L_0x0182
            r0 = r28
            int[] r0 = r0._hash
            r25 = r0
            r6 = r25[r10]
            r0 = r28
            int[] r0 = r0._hash
            r25 = r0
            int r0 = r11 + 1024
            r26 = r0
            r7 = r25[r26]
            r0 = r28
            int[] r0 = r0._hash
            r25 = r0
            r0 = r28
            int r0 = r0._pos
            r26 = r0
            r25[r10] = r26
            r0 = r28
            int[] r0 = r0._hash
            r25 = r0
            int r0 = r11 + 1024
            r26 = r0
            r0 = r28
            int r0 = r0._pos
            r27 = r0
            r25[r26] = r27
            r0 = r17
            if (r6 <= r0) goto L_0x013f
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            r0 = r28
            int r0 = r0._bufferOffset
            r26 = r0
            int r26 = r26 + r6
            byte r25 = r25[r26]
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            byte r26 = r26[r4]
            r0 = r25
            r1 = r26
            if (r0 != r1) goto L_0x013f
            int r20 = r19 + 1
            r18 = 2
            r29[r19] = r18
            int r19 = r20 + 1
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            int r25 = r25 - r6
            int r25 = r25 + -1
            r29[r20] = r25
        L_0x013f:
            r0 = r17
            if (r7 <= r0) goto L_0x017a
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            r0 = r28
            int r0 = r0._bufferOffset
            r26 = r0
            int r26 = r26 + r7
            byte r25 = r25[r26]
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            byte r26 = r26[r4]
            r0 = r25
            r1 = r26
            if (r0 != r1) goto L_0x017a
            if (r7 != r6) goto L_0x0165
            int r19 = r19 + -2
        L_0x0165:
            int r20 = r19 + 1
            r18 = 3
            r29[r19] = r18
            int r19 = r20 + 1
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            int r25 = r25 - r7
            int r25 = r25 + -1
            r29[r20] = r25
            r6 = r7
        L_0x017a:
            if (r19 == 0) goto L_0x0182
            if (r6 != r5) goto L_0x0182
            int r19 = r19 + -2
            r18 = 1
        L_0x0182:
            r0 = r28
            int[] r0 = r0._hash
            r25 = r0
            r0 = r28
            int r0 = r0.kFixHashSize
            r26 = r0
            int r26 = r26 + r12
            r0 = r28
            int r0 = r0._pos
            r27 = r0
            r25[r26] = r27
            r0 = r28
            int r0 = r0._cyclicBufferPos
            r25 = r0
            int r25 = r25 << 1
            int r22 = r25 + 1
            r0 = r28
            int r0 = r0._cyclicBufferPos
            r25 = r0
            int r23 = r25 << 1
            r0 = r28
            int r15 = r0.kNumHashDirectBytes
            r14 = r15
            r0 = r28
            int r0 = r0.kNumHashDirectBytes
            r25 = r0
            if (r25 == 0) goto L_0x0201
            r0 = r17
            if (r5 <= r0) goto L_0x0201
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            r0 = r28
            int r0 = r0._bufferOffset
            r26 = r0
            int r26 = r26 + r5
            r0 = r28
            int r0 = r0.kNumHashDirectBytes
            r27 = r0
            int r26 = r26 + r27
            byte r25 = r25[r26]
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            r0 = r28
            int r0 = r0.kNumHashDirectBytes
            r27 = r0
            int r27 = r27 + r4
            byte r26 = r26[r27]
            r0 = r25
            r1 = r26
            if (r0 == r1) goto L_0x0201
            int r20 = r19 + 1
            r0 = r28
            int r0 = r0.kNumHashDirectBytes
            r18 = r0
            r29[r19] = r18
            int r19 = r20 + 1
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            int r25 = r25 - r5
            int r25 = r25 + -1
            r29[r20] = r25
        L_0x0201:
            r0 = r28
            int r2 = r0._cutValue
            r3 = r2
            r20 = r19
        L_0x0208:
            r0 = r17
            if (r5 <= r0) goto L_0x0372
            int r2 = r3 + -1
            if (r3 != 0) goto L_0x0270
        L_0x0210:
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r0 = r28
            int[] r0 = r0._son
            r26 = r0
            r27 = 0
            r26[r23] = r27
            r25[r22] = r27
            r19 = r20
        L_0x0224:
            r28.MovePos()
        L_0x0227:
            return r19
        L_0x0228:
            r0 = r28
            int r0 = r0._streamPos
            r25 = r0
            r0 = r28
            int r0 = r0._pos
            r26 = r0
            int r16 = r25 - r26
            r0 = r28
            int r0 = r0.kMinMatchCheck
            r25 = r0
            r0 = r16
            r1 = r25
            if (r0 >= r1) goto L_0x0020
            r28.MovePos()
            r19 = 0
            goto L_0x0227
        L_0x0248:
            r17 = 0
            goto L_0x0042
        L_0x024c:
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            byte r25 = r25[r4]
            r0 = r25
            r0 = r0 & 255(0xff, float:3.57E-43)
            r25 = r0
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            int r27 = r4 + 1
            byte r26 = r26[r27]
            r0 = r26
            r0 = r0 & 255(0xff, float:3.57E-43)
            r26 = r0
            int r26 = r26 << 8
            r12 = r25 ^ r26
            goto L_0x00bd
        L_0x0270:
            r0 = r28
            int r0 = r0._pos
            r25 = r0
            int r9 = r25 - r5
            r0 = r28
            int r0 = r0._cyclicBufferPos
            r25 = r0
            r0 = r25
            if (r9 > r0) goto L_0x030c
            r0 = r28
            int r0 = r0._cyclicBufferPos
            r25 = r0
            int r25 = r25 - r9
        L_0x028a:
            int r8 = r25 << 1
            r0 = r28
            int r0 = r0._bufferOffset
            r25 = r0
            int r21 = r25 + r5
            int r13 = java.lang.Math.min(r14, r15)
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            int r26 = r21 + r13
            byte r25 = r25[r26]
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            int r27 = r4 + r13
            byte r26 = r26[r27]
            r0 = r25
            r1 = r26
            if (r0 != r1) goto L_0x031e
        L_0x02b2:
            int r13 = r13 + 1
            r0 = r16
            if (r13 == r0) goto L_0x02d2
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            int r26 = r21 + r13
            byte r25 = r25[r26]
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            int r27 = r4 + r13
            byte r26 = r26[r27]
            r0 = r25
            r1 = r26
            if (r0 == r1) goto L_0x02b2
        L_0x02d2:
            r0 = r18
            if (r0 >= r13) goto L_0x031e
            int r19 = r20 + 1
            r18 = r13
            r29[r20] = r13
            int r20 = r19 + 1
            int r25 = r9 + -1
            r29[r19] = r25
            r0 = r16
            if (r13 != r0) goto L_0x031e
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r0 = r28
            int[] r0 = r0._son
            r26 = r0
            r26 = r26[r8]
            r25[r23] = r26
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r0 = r28
            int[] r0 = r0._son
            r26 = r0
            int r27 = r8 + 1
            r26 = r26[r27]
            r25[r22] = r26
            r19 = r20
            goto L_0x0224
        L_0x030c:
            r0 = r28
            int r0 = r0._cyclicBufferPos
            r25 = r0
            int r25 = r25 - r9
            r0 = r28
            int r0 = r0._cyclicBufferSize
            r26 = r0
            int r25 = r25 + r26
            goto L_0x028a
        L_0x031e:
            r19 = r20
            r0 = r28
            byte[] r0 = r0._bufferBase
            r25 = r0
            int r26 = r21 + r13
            byte r25 = r25[r26]
            r0 = r25
            r0 = r0 & 255(0xff, float:3.57E-43)
            r25 = r0
            r0 = r28
            byte[] r0 = r0._bufferBase
            r26 = r0
            int r27 = r4 + r13
            byte r26 = r26[r27]
            r0 = r26
            r0 = r0 & 255(0xff, float:3.57E-43)
            r26 = r0
            r0 = r25
            r1 = r26
            if (r0 >= r1) goto L_0x035e
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r25[r23] = r5
            int r23 = r8 + 1
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r5 = r25[r23]
            r15 = r13
        L_0x0359:
            r3 = r2
            r20 = r19
            goto L_0x0208
        L_0x035e:
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r25[r22] = r5
            r22 = r8
            r0 = r28
            int[] r0 = r0._son
            r25 = r0
            r5 = r25[r22]
            r14 = r13
            goto L_0x0359
        L_0x0372:
            r2 = r3
            goto L_0x0210
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.compression.p001lz.BinTree.GetMatches(int[]):int");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v9, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v11, resolved type: byte} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v41, resolved type: byte} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x01dc A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void Skip(int r24) throws java.io.IOException {
        /*
            r23 = this;
        L_0x0000:
            r0 = r23
            int r0 = r0._pos
            r20 = r0
            r0 = r23
            int r0 = r0._matchMaxLen
            r21 = r0
            int r20 = r20 + r21
            r0 = r23
            int r0 = r0._streamPos
            r21 = r0
            r0 = r20
            r1 = r21
            if (r0 > r1) goto L_0x0137
            r0 = r23
            int r14 = r0._matchMaxLen
        L_0x001e:
            r0 = r23
            int r0 = r0._pos
            r20 = r0
            r0 = r23
            int r0 = r0._cyclicBufferSize
            r21 = r0
            r0 = r20
            r1 = r21
            if (r0 <= r1) goto L_0x0153
            r0 = r23
            int r0 = r0._pos
            r20 = r0
            r0 = r23
            int r0 = r0._cyclicBufferSize
            r21 = r0
            int r15 = r20 - r21
        L_0x003e:
            r0 = r23
            int r0 = r0._bufferOffset
            r20 = r0
            r0 = r23
            int r0 = r0._pos
            r21 = r0
            int r4 = r20 + r21
            r0 = r23
            boolean r0 = r0.HASH_ARRAY
            r20 = r0
            if (r20 == 0) goto L_0x0156
            int[] r20 = CrcTable
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            byte r21 = r21[r4]
            r0 = r21
            r0 = r0 & 255(0xff, float:3.57E-43)
            r21 = r0
            r20 = r20[r21]
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            int r22 = r4 + 1
            byte r21 = r21[r22]
            r0 = r21
            r0 = r0 & 255(0xff, float:3.57E-43)
            r21 = r0
            r19 = r20 ^ r21
            r0 = r19
            r8 = r0 & 1023(0x3ff, float:1.434E-42)
            r0 = r23
            int[] r0 = r0._hash
            r20 = r0
            r0 = r23
            int r0 = r0._pos
            r21 = r0
            r20[r8] = r21
            r0 = r23
            byte[] r0 = r0._bufferBase
            r20 = r0
            int r21 = r4 + 2
            byte r20 = r20[r21]
            r0 = r20
            r0 = r0 & 255(0xff, float:3.57E-43)
            r20 = r0
            int r20 = r20 << 8
            r19 = r19 ^ r20
            r20 = 65535(0xffff, float:9.1834E-41)
            r9 = r19 & r20
            r0 = r23
            int[] r0 = r0._hash
            r20 = r0
            int r0 = r9 + 1024
            r21 = r0
            r0 = r23
            int r0 = r0._pos
            r22 = r0
            r20[r21] = r22
            int[] r20 = CrcTable
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            int r22 = r4 + 3
            byte r21 = r21[r22]
            r0 = r21
            r0 = r0 & 255(0xff, float:3.57E-43)
            r21 = r0
            r20 = r20[r21]
            int r20 = r20 << 5
            r20 = r20 ^ r19
            r0 = r23
            int r0 = r0._hashMask
            r21 = r0
            r10 = r20 & r21
        L_0x00d5:
            r0 = r23
            int[] r0 = r0._hash
            r20 = r0
            r0 = r23
            int r0 = r0.kFixHashSize
            r21 = r0
            int r21 = r21 + r10
            r5 = r20[r21]
            r0 = r23
            int[] r0 = r0._hash
            r20 = r0
            r0 = r23
            int r0 = r0.kFixHashSize
            r21 = r0
            int r21 = r21 + r10
            r0 = r23
            int r0 = r0._pos
            r22 = r0
            r20[r21] = r22
            r0 = r23
            int r0 = r0._cyclicBufferPos
            r20 = r0
            int r20 = r20 << 1
            int r17 = r20 + 1
            r0 = r23
            int r0 = r0._cyclicBufferPos
            r20 = r0
            int r18 = r20 << 1
            r0 = r23
            int r13 = r0.kNumHashDirectBytes
            r12 = r13
            r0 = r23
            int r2 = r0._cutValue
            r3 = r2
        L_0x0117:
            if (r5 <= r15) goto L_0x0261
            int r2 = r3 + -1
            if (r3 != 0) goto L_0x017a
        L_0x011d:
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r0 = r23
            int[] r0 = r0._son
            r21 = r0
            r22 = 0
            r21[r18] = r22
            r20[r17] = r22
        L_0x012f:
            r23.MovePos()
        L_0x0132:
            int r24 = r24 + -1
            if (r24 != 0) goto L_0x0000
            return
        L_0x0137:
            r0 = r23
            int r0 = r0._streamPos
            r20 = r0
            r0 = r23
            int r0 = r0._pos
            r21 = r0
            int r14 = r20 - r21
            r0 = r23
            int r0 = r0.kMinMatchCheck
            r20 = r0
            r0 = r20
            if (r14 >= r0) goto L_0x001e
            r23.MovePos()
            goto L_0x0132
        L_0x0153:
            r15 = 0
            goto L_0x003e
        L_0x0156:
            r0 = r23
            byte[] r0 = r0._bufferBase
            r20 = r0
            byte r20 = r20[r4]
            r0 = r20
            r0 = r0 & 255(0xff, float:3.57E-43)
            r20 = r0
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            int r22 = r4 + 1
            byte r21 = r21[r22]
            r0 = r21
            r0 = r0 & 255(0xff, float:3.57E-43)
            r21 = r0
            int r21 = r21 << 8
            r10 = r20 ^ r21
            goto L_0x00d5
        L_0x017a:
            r0 = r23
            int r0 = r0._pos
            r20 = r0
            int r7 = r20 - r5
            r0 = r23
            int r0 = r0._cyclicBufferPos
            r20 = r0
            r0 = r20
            if (r7 > r0) goto L_0x0200
            r0 = r23
            int r0 = r0._cyclicBufferPos
            r20 = r0
            int r20 = r20 - r7
        L_0x0194:
            int r6 = r20 << 1
            r0 = r23
            int r0 = r0._bufferOffset
            r20 = r0
            int r16 = r20 + r5
            int r11 = java.lang.Math.min(r12, r13)
            r0 = r23
            byte[] r0 = r0._bufferBase
            r20 = r0
            int r21 = r16 + r11
            byte r20 = r20[r21]
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            int r22 = r4 + r11
            byte r21 = r21[r22]
            r0 = r20
            r1 = r21
            if (r0 != r1) goto L_0x0211
        L_0x01bc:
            int r11 = r11 + 1
            if (r11 == r14) goto L_0x01da
            r0 = r23
            byte[] r0 = r0._bufferBase
            r20 = r0
            int r21 = r16 + r11
            byte r20 = r20[r21]
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            int r22 = r4 + r11
            byte r21 = r21[r22]
            r0 = r20
            r1 = r21
            if (r0 == r1) goto L_0x01bc
        L_0x01da:
            if (r11 != r14) goto L_0x0211
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r0 = r23
            int[] r0 = r0._son
            r21 = r0
            r21 = r21[r6]
            r20[r18] = r21
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r0 = r23
            int[] r0 = r0._son
            r21 = r0
            int r22 = r6 + 1
            r21 = r21[r22]
            r20[r17] = r21
            goto L_0x012f
        L_0x0200:
            r0 = r23
            int r0 = r0._cyclicBufferPos
            r20 = r0
            int r20 = r20 - r7
            r0 = r23
            int r0 = r0._cyclicBufferSize
            r21 = r0
            int r20 = r20 + r21
            goto L_0x0194
        L_0x0211:
            r0 = r23
            byte[] r0 = r0._bufferBase
            r20 = r0
            int r21 = r16 + r11
            byte r20 = r20[r21]
            r0 = r20
            r0 = r0 & 255(0xff, float:3.57E-43)
            r20 = r0
            r0 = r23
            byte[] r0 = r0._bufferBase
            r21 = r0
            int r22 = r4 + r11
            byte r21 = r21[r22]
            r0 = r21
            r0 = r0 & 255(0xff, float:3.57E-43)
            r21 = r0
            r0 = r20
            r1 = r21
            if (r0 >= r1) goto L_0x024d
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r20[r18] = r5
            int r18 = r6 + 1
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r5 = r20[r18]
            r13 = r11
        L_0x024a:
            r3 = r2
            goto L_0x0117
        L_0x024d:
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r20[r17] = r5
            r17 = r6
            r0 = r23
            int[] r0 = r0._son
            r20 = r0
            r5 = r20[r17]
            r12 = r11
            goto L_0x024a
        L_0x0261:
            r2 = r3
            goto L_0x011d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.compression.p001lz.BinTree.Skip(int):void");
    }

    /* access modifiers changed from: package-private */
    public void NormalizeLinks(int[] items, int numItems, int subValue) {
        int value;
        for (int i = 0; i < numItems; i++) {
            int value2 = items[i];
            if (value2 <= subValue) {
                value = 0;
            } else {
                value = value2 - subValue;
            }
            items[i] = value;
        }
    }

    /* access modifiers changed from: package-private */
    public void Normalize() {
        int subValue = this._pos - this._cyclicBufferSize;
        NormalizeLinks(this._son, this._cyclicBufferSize * 2, subValue);
        NormalizeLinks(this._hash, this._hashSizeSum, subValue);
        ReduceOffsets(subValue);
    }

    public void SetCutValue(int cutValue) {
        this._cutValue = cutValue;
    }

    static {
        for (int i = 0; i < 256; i++) {
            int r = i;
            for (int j = 0; j < 8; j++) {
                if ((r & 1) != 0) {
                    r = (r >>> 1) ^ -306674912;
                } else {
                    r >>>= 1;
                }
            }
            CrcTable[i] = r;
        }
    }
}
