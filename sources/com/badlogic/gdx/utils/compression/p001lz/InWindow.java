package com.badlogic.gdx.utils.compression.p001lz;

import java.io.IOException;
import java.io.InputStream;

/* renamed from: com.badlogic.gdx.utils.compression.lz.InWindow */
public class InWindow {
    public int _blockSize;
    public byte[] _bufferBase;
    public int _bufferOffset;
    int _keepSizeAfter;
    int _keepSizeBefore;
    int _pointerToLastSafePosition;
    public int _pos;
    int _posLimit;
    InputStream _stream;
    boolean _streamEndWasReached;
    public int _streamPos;

    public void MoveBlock() {
        int offset = (this._bufferOffset + this._pos) - this._keepSizeBefore;
        if (offset > 0) {
            offset--;
        }
        int numBytes = (this._bufferOffset + this._streamPos) - offset;
        for (int i = 0; i < numBytes; i++) {
            this._bufferBase[i] = this._bufferBase[offset + i];
        }
        this._bufferOffset -= offset;
    }

    /* JADX WARNING: CFG modification limit reached, blocks count: 118 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void ReadBlock() throws java.io.IOException {
        /*
            r7 = this;
            boolean r3 = r7._streamEndWasReached
            if (r3 == 0) goto L_0x001a
        L_0x0004:
            return
        L_0x0005:
            int r3 = r7._streamPos
            int r3 = r3 + r0
            r7._streamPos = r3
            int r3 = r7._streamPos
            int r4 = r7._pos
            int r5 = r7._keepSizeAfter
            int r4 = r4 + r5
            if (r3 < r4) goto L_0x001a
            int r3 = r7._streamPos
            int r4 = r7._keepSizeAfter
            int r3 = r3 - r4
            r7._posLimit = r3
        L_0x001a:
            int r3 = r7._bufferOffset
            int r3 = 0 - r3
            int r4 = r7._blockSize
            int r3 = r3 + r4
            int r4 = r7._streamPos
            int r2 = r3 - r4
            if (r2 == 0) goto L_0x0004
            java.io.InputStream r3 = r7._stream
            byte[] r4 = r7._bufferBase
            int r5 = r7._bufferOffset
            int r6 = r7._streamPos
            int r5 = r5 + r6
            int r0 = r3.read(r4, r5, r2)
            r3 = -1
            if (r0 != r3) goto L_0x0005
            int r3 = r7._streamPos
            r7._posLimit = r3
            int r3 = r7._bufferOffset
            int r4 = r7._posLimit
            int r1 = r3 + r4
            int r3 = r7._pointerToLastSafePosition
            if (r1 <= r3) goto L_0x004c
            int r3 = r7._pointerToLastSafePosition
            int r4 = r7._bufferOffset
            int r3 = r3 - r4
            r7._posLimit = r3
        L_0x004c:
            r3 = 1
            r7._streamEndWasReached = r3
            goto L_0x0004
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.compression.p001lz.InWindow.ReadBlock():void");
    }

    /* access modifiers changed from: package-private */
    public void Free() {
        this._bufferBase = null;
    }

    public void Create(int keepSizeBefore, int keepSizeAfter, int keepSizeReserv) {
        this._keepSizeBefore = keepSizeBefore;
        this._keepSizeAfter = keepSizeAfter;
        int blockSize = keepSizeBefore + keepSizeAfter + keepSizeReserv;
        if (this._bufferBase == null || this._blockSize != blockSize) {
            Free();
            this._blockSize = blockSize;
            this._bufferBase = new byte[this._blockSize];
        }
        this._pointerToLastSafePosition = this._blockSize - keepSizeAfter;
    }

    public void SetStream(InputStream stream) {
        this._stream = stream;
    }

    public void ReleaseStream() {
        this._stream = null;
    }

    public void Init() throws IOException {
        this._bufferOffset = 0;
        this._pos = 0;
        this._streamPos = 0;
        this._streamEndWasReached = false;
        ReadBlock();
    }

    public void MovePos() throws IOException {
        this._pos++;
        if (this._pos > this._posLimit) {
            if (this._bufferOffset + this._pos > this._pointerToLastSafePosition) {
                MoveBlock();
            }
            ReadBlock();
        }
    }

    public byte GetIndexByte(int index) {
        return this._bufferBase[this._bufferOffset + this._pos + index];
    }

    public int GetMatchLen(int index, int distance, int limit) {
        if (this._streamEndWasReached && this._pos + index + limit > this._streamPos) {
            limit = this._streamPos - (this._pos + index);
        }
        int distance2 = distance + 1;
        int pby = this._bufferOffset + this._pos + index;
        int i = 0;
        while (i < limit && this._bufferBase[pby + i] == this._bufferBase[(pby + i) - distance2]) {
            i++;
        }
        return i;
    }

    public int GetNumAvailableBytes() {
        return this._streamPos - this._pos;
    }

    public void ReduceOffsets(int subValue) {
        this._bufferOffset += subValue;
        this._posLimit -= subValue;
        this._pos -= subValue;
        this._streamPos -= subValue;
    }
}
