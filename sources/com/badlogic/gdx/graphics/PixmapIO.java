package com.badlogic.gdx.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PixmapIO {
    public static void writeCIM(FileHandle file, Pixmap pixmap) {
        CIM.write(file, pixmap);
    }

    public static Pixmap readCIM(FileHandle file) {
        return CIM.read(file);
    }

    public static void writePNG(FileHandle file, Pixmap pixmap) {
        try {
            file.writeBytes(PNG.write(pixmap), false);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error writing PNG: " + file, ex);
        }
    }

    private static class CIM {
        private static final int BUFFER_SIZE = 32000;
        private static final byte[] readBuffer = new byte[BUFFER_SIZE];
        private static final byte[] writeBuffer = new byte[BUFFER_SIZE];

        private CIM() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:30:0x009c A[SYNTHETIC, Splitter:B:30:0x009c] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static void write(com.badlogic.gdx.files.FileHandle r11, com.badlogic.gdx.graphics.Pixmap r12) {
            /*
                r4 = 0
                java.util.zip.DeflaterOutputStream r0 = new java.util.zip.DeflaterOutputStream     // Catch:{ Exception -> 0x00a7 }
                r8 = 0
                java.io.OutputStream r8 = r11.write(r8)     // Catch:{ Exception -> 0x00a7 }
                r0.<init>(r8)     // Catch:{ Exception -> 0x00a7 }
                java.io.DataOutputStream r5 = new java.io.DataOutputStream     // Catch:{ Exception -> 0x00a7 }
                r5.<init>(r0)     // Catch:{ Exception -> 0x00a7 }
                int r8 = r12.getWidth()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r5.writeInt(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r8 = r12.getHeight()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r5.writeInt(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                com.badlogic.gdx.graphics.Pixmap$Format r8 = r12.getFormat()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r8 = com.badlogic.gdx.graphics.Pixmap.Format.toGdx2DPixmapFormat(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r5.writeInt(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                java.nio.ByteBuffer r6 = r12.getPixels()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r8 = 0
                r6.position(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r8 = r6.capacity()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r6.limit(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r8 = r6.capacity()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r7 = r8 % 32000
                int r8 = r6.capacity()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r3 = r8 / 32000
                byte[] r9 = writeBuffer     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                monitor-enter(r9)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r2 = 0
            L_0x0048:
                if (r2 >= r3) goto L_0x0057
                byte[] r8 = writeBuffer     // Catch:{ all -> 0x0075 }
                r6.get(r8)     // Catch:{ all -> 0x0075 }
                byte[] r8 = writeBuffer     // Catch:{ all -> 0x0075 }
                r5.write(r8)     // Catch:{ all -> 0x0075 }
                int r2 = r2 + 1
                goto L_0x0048
            L_0x0057:
                byte[] r8 = writeBuffer     // Catch:{ all -> 0x0075 }
                r10 = 0
                r6.get(r8, r10, r7)     // Catch:{ all -> 0x0075 }
                byte[] r8 = writeBuffer     // Catch:{ all -> 0x0075 }
                r10 = 0
                r5.write(r8, r10, r7)     // Catch:{ all -> 0x0075 }
                monitor-exit(r9)     // Catch:{ all -> 0x0075 }
                r8 = 0
                r6.position(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                int r8 = r6.capacity()     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                r6.limit(r8)     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
                if (r5 == 0) goto L_0x0074
                r5.close()     // Catch:{ Exception -> 0x00a0 }
            L_0x0074:
                return
            L_0x0075:
                r8 = move-exception
                monitor-exit(r9)     // Catch:{ all -> 0x0075 }
                throw r8     // Catch:{ Exception -> 0x0078, all -> 0x00a4 }
            L_0x0078:
                r1 = move-exception
                r4 = r5
            L_0x007a:
                com.badlogic.gdx.utils.GdxRuntimeException r8 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0099 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0099 }
                r9.<init>()     // Catch:{ all -> 0x0099 }
                java.lang.String r10 = "Couldn't write Pixmap to file '"
                java.lang.StringBuilder r9 = r9.append(r10)     // Catch:{ all -> 0x0099 }
                java.lang.StringBuilder r9 = r9.append(r11)     // Catch:{ all -> 0x0099 }
                java.lang.String r10 = "'"
                java.lang.StringBuilder r9 = r9.append(r10)     // Catch:{ all -> 0x0099 }
                java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0099 }
                r8.<init>(r9, r1)     // Catch:{ all -> 0x0099 }
                throw r8     // Catch:{ all -> 0x0099 }
            L_0x0099:
                r8 = move-exception
            L_0x009a:
                if (r4 == 0) goto L_0x009f
                r4.close()     // Catch:{ Exception -> 0x00a2 }
            L_0x009f:
                throw r8
            L_0x00a0:
                r8 = move-exception
                goto L_0x0074
            L_0x00a2:
                r9 = move-exception
                goto L_0x009f
            L_0x00a4:
                r8 = move-exception
                r4 = r5
                goto L_0x009a
            L_0x00a7:
                r1 = move-exception
                goto L_0x007a
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.PixmapIO.CIM.write(com.badlogic.gdx.files.FileHandle, com.badlogic.gdx.graphics.Pixmap):void");
        }

        /* JADX WARNING: Removed duplicated region for block: B:22:0x0072 A[SYNTHETIC, Splitter:B:22:0x0072] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static com.badlogic.gdx.graphics.Pixmap read(com.badlogic.gdx.files.FileHandle r12) {
            /*
                r3 = 0
                java.io.DataInputStream r4 = new java.io.DataInputStream     // Catch:{ Exception -> 0x008f }
                java.util.zip.InflaterInputStream r9 = new java.util.zip.InflaterInputStream     // Catch:{ Exception -> 0x008f }
                java.io.BufferedInputStream r10 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x008f }
                java.io.InputStream r11 = r12.read()     // Catch:{ Exception -> 0x008f }
                r10.<init>(r11)     // Catch:{ Exception -> 0x008f }
                r9.<init>(r10)     // Catch:{ Exception -> 0x008f }
                r4.<init>(r9)     // Catch:{ Exception -> 0x008f }
                int r8 = r4.readInt()     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                int r2 = r4.readInt()     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                int r9 = r4.readInt()     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                com.badlogic.gdx.graphics.Pixmap$Format r1 = com.badlogic.gdx.graphics.Pixmap.Format.fromGdx2DPixmapFormat(r9)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                com.badlogic.gdx.graphics.Pixmap r6 = new com.badlogic.gdx.graphics.Pixmap     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                r6.<init>((int) r8, (int) r2, (com.badlogic.gdx.graphics.Pixmap.Format) r1)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                java.nio.ByteBuffer r5 = r6.getPixels()     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                r9 = 0
                r5.position(r9)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                int r9 = r5.capacity()     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                r5.limit(r9)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                byte[] r10 = readBuffer     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                monitor-enter(r10)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                r7 = 0
            L_0x003c:
                byte[] r9 = readBuffer     // Catch:{ all -> 0x004b }
                int r7 = r4.read(r9)     // Catch:{ all -> 0x004b }
                if (r7 <= 0) goto L_0x0076
                byte[] r9 = readBuffer     // Catch:{ all -> 0x004b }
                r11 = 0
                r5.put(r9, r11, r7)     // Catch:{ all -> 0x004b }
                goto L_0x003c
            L_0x004b:
                r9 = move-exception
                monitor-exit(r10)     // Catch:{ all -> 0x004b }
                throw r9     // Catch:{ Exception -> 0x004e, all -> 0x008c }
            L_0x004e:
                r0 = move-exception
                r3 = r4
            L_0x0050:
                com.badlogic.gdx.utils.GdxRuntimeException r9 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x006f }
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x006f }
                r10.<init>()     // Catch:{ all -> 0x006f }
                java.lang.String r11 = "Couldn't read Pixmap from file '"
                java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ all -> 0x006f }
                java.lang.StringBuilder r10 = r10.append(r12)     // Catch:{ all -> 0x006f }
                java.lang.String r11 = "'"
                java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ all -> 0x006f }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x006f }
                r9.<init>(r10, r0)     // Catch:{ all -> 0x006f }
                throw r9     // Catch:{ all -> 0x006f }
            L_0x006f:
                r9 = move-exception
            L_0x0070:
                if (r3 == 0) goto L_0x0075
                r3.close()     // Catch:{ Exception -> 0x008a }
            L_0x0075:
                throw r9
            L_0x0076:
                monitor-exit(r10)     // Catch:{ all -> 0x004b }
                r9 = 0
                r5.position(r9)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                int r9 = r5.capacity()     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                r5.limit(r9)     // Catch:{ Exception -> 0x004e, all -> 0x008c }
                if (r4 == 0) goto L_0x0087
                r4.close()     // Catch:{ Exception -> 0x0088 }
            L_0x0087:
                return r6
            L_0x0088:
                r9 = move-exception
                goto L_0x0087
            L_0x008a:
                r10 = move-exception
                goto L_0x0075
            L_0x008c:
                r9 = move-exception
                r3 = r4
                goto L_0x0070
            L_0x008f:
                r0 = move-exception
                goto L_0x0050
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.PixmapIO.CIM.read(com.badlogic.gdx.files.FileHandle):com.badlogic.gdx.graphics.Pixmap");
        }
    }

    private static class PNG {
        static final int ZLIB_BLOCK_SIZE = 32000;
        static int[] crcTable;

        private PNG() {
        }

        static byte[] write(Pixmap pixmap) throws IOException {
            byte[] signature = {-119, 80, 78, 71, 13, 10, 26, 10};
            byte[] header = createHeaderChunk(pixmap.getWidth(), pixmap.getHeight());
            byte[] data = createDataChunk(pixmap);
            byte[] trailer = createTrailerChunk();
            ByteArrayOutputStream png = new ByteArrayOutputStream(signature.length + header.length + data.length + trailer.length);
            png.write(signature);
            png.write(header);
            png.write(data);
            png.write(trailer);
            return png.toByteArray();
        }

        private static byte[] createHeaderChunk(int width, int height) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(13);
            DataOutputStream chunk = new DataOutputStream(baos);
            chunk.writeInt(width);
            chunk.writeInt(height);
            chunk.writeByte(8);
            chunk.writeByte(6);
            chunk.writeByte(0);
            chunk.writeByte(0);
            chunk.writeByte(0);
            return toChunk("IHDR", baos.toByteArray());
        }

        private static byte[] createDataChunk(Pixmap pixmap) throws IOException {
            int width = pixmap.getWidth();
            int height = pixmap.getHeight();
            byte[] raw = new byte[((width * 4 * height) + height)];
            int dest = 0;
            for (int y = 0; y < height; y++) {
                raw[dest] = 0;
                dest++;
                for (int x = 0; x < width; x++) {
                    int mask = pixmap.getPixel(x, y) & -1;
                    int dest2 = dest + 1;
                    raw[dest] = (byte) ((mask >> 24) & 255);
                    int dest3 = dest2 + 1;
                    raw[dest2] = (byte) ((mask >> 16) & 255);
                    int dest4 = dest3 + 1;
                    raw[dest3] = (byte) ((mask >> 8) & 255);
                    dest = dest4 + 1;
                    raw[dest4] = (byte) (mask & 255);
                }
            }
            return toChunk("IDAT", toZLIB(raw));
        }

        private static byte[] createTrailerChunk() throws IOException {
            return toChunk("IEND", new byte[0]);
        }

        private static byte[] toChunk(String id, byte[] raw) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 12);
            DataOutputStream chunk = new DataOutputStream(baos);
            chunk.writeInt(raw.length);
            byte[] bid = new byte[4];
            for (int i = 0; i < 4; i++) {
                bid[i] = (byte) id.charAt(i);
            }
            chunk.write(bid);
            chunk.write(raw);
            chunk.writeInt(updateCRC(updateCRC(-1, bid), raw) ^ -1);
            return baos.toByteArray();
        }

        private static void createCRCTable() {
            crcTable = new int[256];
            for (int i = 0; i < 256; i++) {
                int c = i;
                for (int k = 0; k < 8; k++) {
                    c = (c & 1) > 0 ? -306674912 ^ (c >>> 1) : c >>> 1;
                }
                crcTable[i] = c;
            }
        }

        private static int updateCRC(int crc, byte[] raw) {
            if (crcTable == null) {
                createCRCTable();
            }
            for (byte element : raw) {
                crc = crcTable[(crc ^ element) & 255] ^ (crc >>> 8);
            }
            return crc;
        }

        private static byte[] toZLIB(byte[] raw) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 6 + ((raw.length / ZLIB_BLOCK_SIZE) * 5));
            DataOutputStream zlib = new DataOutputStream(baos);
            zlib.writeByte(8);
            zlib.writeByte(29);
            int pos = 0;
            while (raw.length - pos > ZLIB_BLOCK_SIZE) {
                writeUncompressedDeflateBlock(zlib, false, raw, pos, 32000);
                pos += ZLIB_BLOCK_SIZE;
            }
            writeUncompressedDeflateBlock(zlib, true, raw, pos, (char) (raw.length - pos));
            zlib.writeInt(calcADLER32(raw));
            return baos.toByteArray();
        }

        private static void writeUncompressedDeflateBlock(DataOutputStream zlib, boolean last, byte[] raw, int off, char len) throws IOException {
            zlib.writeByte((byte) (last ? 1 : 0));
            zlib.writeByte((byte) (len & 255));
            zlib.writeByte((byte) ((len & 65280) >> 8));
            zlib.writeByte((byte) ((len ^ 65535) & 255));
            zlib.writeByte((byte) (((len ^ 65535) & 65280) >> 8));
            zlib.write(raw, off, len);
        }

        /* JADX WARNING: type inference failed for: r6v0, types: [byte[]] */
        /* JADX WARNING: type inference failed for: r4v3, types: [byte] */
        /* JADX WARNING: type inference failed for: r4v4, types: [int, byte] */
        /* JADX WARNING: type inference failed for: r0v2, types: [byte] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int calcADLER32(byte[] r6) {
            /*
                r5 = 65521(0xfff1, float:9.1814E-41)
                r2 = 1
                r3 = 0
                r1 = 0
            L_0x0006:
                int r4 = r6.length
                if (r1 >= r4) goto L_0x001f
                byte r4 = r6[r1]
                if (r4 < 0) goto L_0x001a
                byte r0 = r6[r1]
            L_0x000f:
                int r4 = r2 + r0
                int r2 = r4 % r5
                int r4 = r3 + r2
                int r3 = r4 % r5
                int r1 = r1 + 1
                goto L_0x0006
            L_0x001a:
                byte r4 = r6[r1]
                int r0 = r4 + 256
                goto L_0x000f
            L_0x001f:
                int r4 = r3 << 16
                int r4 = r4 + r2
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.PixmapIO.PNG.calcADLER32(byte[]):int");
        }
    }
}
