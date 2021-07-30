package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;

public class ETC1 {
    public static int ETC1_RGB8_OES = 36196;
    public static int PKM_HEADER_SIZE = 16;

    private static native void decodeImage(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2, int i3, int i4, int i5);

    private static native ByteBuffer encodeImage(ByteBuffer byteBuffer, int i, int i2, int i3, int i4);

    private static native ByteBuffer encodeImagePKM(ByteBuffer byteBuffer, int i, int i2, int i3, int i4);

    public static native void formatHeader(ByteBuffer byteBuffer, int i, int i2, int i3);

    public static native int getCompressedDataSize(int i, int i2);

    static native int getHeightPKM(ByteBuffer byteBuffer, int i);

    static native int getWidthPKM(ByteBuffer byteBuffer, int i);

    static native boolean isValidPKM(ByteBuffer byteBuffer, int i);

    public static final class ETC1Data implements Disposable {
        public final ByteBuffer compressedData;
        public final int dataOffset;
        public final int height;
        public final int width;

        public ETC1Data(int width2, int height2, ByteBuffer compressedData2, int dataOffset2) {
            this.width = width2;
            this.height = height2;
            this.compressedData = compressedData2;
            this.dataOffset = dataOffset2;
        }

        /* JADX WARNING: Removed duplicated region for block: B:15:0x0059 A[SYNTHETIC, Splitter:B:15:0x0059] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public ETC1Data(com.badlogic.gdx.files.FileHandle r11) {
            /*
                r10 = this;
                r9 = 0
                r10.<init>()
                r6 = 10240(0x2800, float:1.4349E-41)
                byte[] r0 = new byte[r6]
                r3 = 0
                java.io.DataInputStream r4 = new java.io.DataInputStream     // Catch:{ Exception -> 0x0096 }
                java.io.BufferedInputStream r6 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0096 }
                java.util.zip.GZIPInputStream r7 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x0096 }
                java.io.InputStream r8 = r11.read()     // Catch:{ Exception -> 0x0096 }
                r7.<init>(r8)     // Catch:{ Exception -> 0x0096 }
                r6.<init>(r7)     // Catch:{ Exception -> 0x0096 }
                r4.<init>(r6)     // Catch:{ Exception -> 0x0096 }
                int r2 = r4.readInt()     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                java.nio.ByteBuffer r6 = com.badlogic.gdx.utils.BufferUtils.newUnsafeByteBuffer((int) r2)     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                r10.compressedData = r6     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                r5 = 0
            L_0x0027:
                int r5 = r4.read(r0)     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                r6 = -1
                if (r5 == r6) goto L_0x005d
                java.nio.ByteBuffer r6 = r10.compressedData     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                r7 = 0
                r6.put(r0, r7, r5)     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                goto L_0x0027
            L_0x0035:
                r1 = move-exception
                r3 = r4
            L_0x0037:
                com.badlogic.gdx.utils.GdxRuntimeException r6 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0056 }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
                r7.<init>()     // Catch:{ all -> 0x0056 }
                java.lang.String r8 = "Couldn't load pkm file '"
                java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x0056 }
                java.lang.StringBuilder r7 = r7.append(r11)     // Catch:{ all -> 0x0056 }
                java.lang.String r8 = "'"
                java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x0056 }
                java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0056 }
                r6.<init>(r7, r1)     // Catch:{ all -> 0x0056 }
                throw r6     // Catch:{ all -> 0x0056 }
            L_0x0056:
                r6 = move-exception
            L_0x0057:
                if (r3 == 0) goto L_0x005c
                r3.close()     // Catch:{ Exception -> 0x0091 }
            L_0x005c:
                throw r6
            L_0x005d:
                java.nio.ByteBuffer r6 = r10.compressedData     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                r7 = 0
                r6.position(r7)     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                java.nio.ByteBuffer r6 = r10.compressedData     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                java.nio.ByteBuffer r7 = r10.compressedData     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                int r7 = r7.capacity()     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                r6.limit(r7)     // Catch:{ Exception -> 0x0035, all -> 0x0093 }
                if (r4 == 0) goto L_0x0073
                r4.close()     // Catch:{ Exception -> 0x008f }
            L_0x0073:
                java.nio.ByteBuffer r6 = r10.compressedData
                int r6 = com.badlogic.gdx.graphics.glutils.ETC1.getWidthPKM(r6, r9)
                r10.width = r6
                java.nio.ByteBuffer r6 = r10.compressedData
                int r6 = com.badlogic.gdx.graphics.glutils.ETC1.getHeightPKM(r6, r9)
                r10.height = r6
                int r6 = com.badlogic.gdx.graphics.glutils.ETC1.PKM_HEADER_SIZE
                r10.dataOffset = r6
                java.nio.ByteBuffer r6 = r10.compressedData
                int r7 = r10.dataOffset
                r6.position(r7)
                return
            L_0x008f:
                r6 = move-exception
                goto L_0x0073
            L_0x0091:
                r7 = move-exception
                goto L_0x005c
            L_0x0093:
                r6 = move-exception
                r3 = r4
                goto L_0x0057
            L_0x0096:
                r1 = move-exception
                goto L_0x0037
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.glutils.ETC1.ETC1Data.<init>(com.badlogic.gdx.files.FileHandle):void");
        }

        public boolean hasPKMHeader() {
            return this.dataOffset == 16;
        }

        /* JADX WARNING: Removed duplicated region for block: B:20:0x0089 A[SYNTHETIC, Splitter:B:20:0x0089] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void write(com.badlogic.gdx.files.FileHandle r10) {
            /*
                r9 = this;
                r7 = 0
                r3 = 0
                r6 = 10240(0x2800, float:1.4349E-41)
                byte[] r0 = new byte[r6]
                r5 = 0
                java.nio.ByteBuffer r6 = r9.compressedData
                r6.position(r7)
                java.nio.ByteBuffer r6 = r9.compressedData
                java.nio.ByteBuffer r7 = r9.compressedData
                int r7 = r7.capacity()
                r6.limit(r7)
                java.io.DataOutputStream r4 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x0066 }
                java.util.zip.GZIPOutputStream r6 = new java.util.zip.GZIPOutputStream     // Catch:{ Exception -> 0x0066 }
                r7 = 0
                java.io.OutputStream r7 = r10.write(r7)     // Catch:{ Exception -> 0x0066 }
                r6.<init>(r7)     // Catch:{ Exception -> 0x0066 }
                r4.<init>(r6)     // Catch:{ Exception -> 0x0066 }
                java.nio.ByteBuffer r6 = r9.compressedData     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                int r6 = r6.capacity()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                r4.writeInt(r6)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
            L_0x002f:
                java.nio.ByteBuffer r6 = r9.compressedData     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                int r6 = r6.capacity()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                if (r5 == r6) goto L_0x004e
                java.nio.ByteBuffer r6 = r9.compressedData     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                int r6 = r6.remaining()     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                int r7 = r0.length     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                int r1 = java.lang.Math.min(r6, r7)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                java.nio.ByteBuffer r6 = r9.compressedData     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                r7 = 0
                r6.get(r0, r7, r1)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                r6 = 0
                r4.write(r0, r6, r1)     // Catch:{ Exception -> 0x0094, all -> 0x0091 }
                int r5 = r5 + r1
                goto L_0x002f
            L_0x004e:
                if (r4 == 0) goto L_0x0053
                r4.close()     // Catch:{ Exception -> 0x008d }
            L_0x0053:
                java.nio.ByteBuffer r6 = r9.compressedData
                int r7 = r9.dataOffset
                r6.position(r7)
                java.nio.ByteBuffer r6 = r9.compressedData
                java.nio.ByteBuffer r7 = r9.compressedData
                int r7 = r7.capacity()
                r6.limit(r7)
                return
            L_0x0066:
                r2 = move-exception
            L_0x0067:
                com.badlogic.gdx.utils.GdxRuntimeException r6 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0086 }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0086 }
                r7.<init>()     // Catch:{ all -> 0x0086 }
                java.lang.String r8 = "Couldn't write PKM file to '"
                java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x0086 }
                java.lang.StringBuilder r7 = r7.append(r10)     // Catch:{ all -> 0x0086 }
                java.lang.String r8 = "'"
                java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x0086 }
                java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0086 }
                r6.<init>(r7, r2)     // Catch:{ all -> 0x0086 }
                throw r6     // Catch:{ all -> 0x0086 }
            L_0x0086:
                r6 = move-exception
            L_0x0087:
                if (r3 == 0) goto L_0x008c
                r3.close()     // Catch:{ Exception -> 0x008f }
            L_0x008c:
                throw r6
            L_0x008d:
                r6 = move-exception
                goto L_0x0053
            L_0x008f:
                r7 = move-exception
                goto L_0x008c
            L_0x0091:
                r6 = move-exception
                r3 = r4
                goto L_0x0087
            L_0x0094:
                r2 = move-exception
                r3 = r4
                goto L_0x0067
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.glutils.ETC1.ETC1Data.write(com.badlogic.gdx.files.FileHandle):void");
        }

        public void dispose() {
            BufferUtils.disposeUnsafeByteBuffer(this.compressedData);
        }

        public String toString() {
            if (!hasPKMHeader()) {
                return "raw [" + this.width + "x" + this.height + "], compressed: " + (this.compressedData.capacity() - ETC1.PKM_HEADER_SIZE);
            }
            return (ETC1.isValidPKM(this.compressedData, 0) ? "valid" : "invalid") + " pkm [" + ETC1.getWidthPKM(this.compressedData, 0) + "x" + ETC1.getHeightPKM(this.compressedData, 0) + "], compressed: " + (this.compressedData.capacity() - ETC1.PKM_HEADER_SIZE);
        }
    }

    private static int getPixelSize(Pixmap.Format format) {
        if (format == Pixmap.Format.RGB565) {
            return 2;
        }
        if (format == Pixmap.Format.RGB888) {
            return 3;
        }
        throw new GdxRuntimeException("Can only handle RGB565 or RGB888 images");
    }

    public static ETC1Data encodeImage(Pixmap pixmap) {
        ByteBuffer compressedData = encodeImage(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), getPixelSize(pixmap.getFormat()));
        BufferUtils.newUnsafeByteBuffer(compressedData);
        return new ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 0);
    }

    public static ETC1Data encodeImagePKM(Pixmap pixmap) {
        ByteBuffer compressedData = encodeImagePKM(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), getPixelSize(pixmap.getFormat()));
        BufferUtils.newUnsafeByteBuffer(compressedData);
        return new ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 16);
    }

    public static Pixmap decodeImage(ETC1Data etc1Data, Pixmap.Format format) {
        int dataOffset;
        int width;
        int height;
        if (etc1Data.hasPKMHeader()) {
            dataOffset = 16;
            width = getWidthPKM(etc1Data.compressedData, 0);
            height = getHeightPKM(etc1Data.compressedData, 0);
        } else {
            dataOffset = 0;
            width = etc1Data.width;
            height = etc1Data.height;
        }
        int pixelSize = getPixelSize(format);
        Pixmap pixmap = new Pixmap(width, height, format);
        decodeImage(etc1Data.compressedData, dataOffset, pixmap.getPixels(), 0, width, height, pixelSize);
        return pixmap;
    }
}
