package com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks;

import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class ChunkReader {

    public static class Chunk {
        Array<Chunk> children = new Array<>();

        /* renamed from: id */
        int f137id;

        /* renamed from: in */
        CountingDataInputStream f138in;
        int offset = 0;
        Chunk parent;
        ByteArrayInputStream payload;
        byte[] payloadBytes;

        protected Chunk(int id, Chunk parent2, byte[] bytes, int offset2, int size) throws IOException {
            this.f137id = id;
            this.parent = parent2;
            this.payload = new ByteArrayInputStream(bytes, offset2, size);
            this.payloadBytes = bytes;
            this.offset = offset2;
            this.f138in = new CountingDataInputStream(this.payload);
        }

        public int getId() {
            return this.f137id;
        }

        public Chunk getParent() {
            return this.parent;
        }

        public Array<Chunk> getChildren() {
            return this.children;
        }

        public int readByte() {
            try {
                return this.f138in.readByte();
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public short readShort() {
            try {
                return this.f138in.readShort();
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public int readInt() {
            try {
                return this.f138in.readInt();
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public long readLong() {
            try {
                return this.f138in.readLong();
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public float readFloat() {
            try {
                return this.f138in.readFloat();
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public double readDouble() {
            try {
                return this.f138in.readDouble();
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public byte[] readBytes() {
            try {
                int len = this.f138in.readInt();
                byte[] v = new byte[len];
                for (int i = 0; i < len; i++) {
                    v[i] = this.f138in.readByte();
                }
                return v;
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public short[] readShorts() {
            try {
                int len = this.f138in.readInt();
                short[] v = new short[len];
                for (int i = 0; i < len; i++) {
                    v[i] = this.f138in.readShort();
                }
                return v;
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public int[] readInts() {
            try {
                int len = this.f138in.readInt();
                int[] v = new int[len];
                for (int i = 0; i < len; i++) {
                    v[i] = this.f138in.readInt();
                }
                return v;
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public long[] readLongs() {
            try {
                int len = this.f138in.readInt();
                long[] v = new long[len];
                for (int i = 0; i < len; i++) {
                    v[i] = this.f138in.readLong();
                }
                return v;
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public float[] readFloats() {
            try {
                int len = this.f138in.readInt();
                float[] v = new float[len];
                for (int i = 0; i < len; i++) {
                    v[i] = this.f138in.readFloat();
                }
                return v;
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public double[] readDoubles() {
            try {
                int len = this.f138in.readInt();
                double[] v = new double[len];
                for (int i = 0; i < len; i++) {
                    v[i] = this.f138in.readDouble();
                }
                return v;
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public String readString() {
            try {
                byte[] bytes = new byte[this.f138in.readInt()];
                this.f138in.readFully(bytes);
                return new String(bytes, "UTF-8");
            } catch (IOException e) {
                throw new GdxRuntimeException("Couldn't read payload, " + e.getMessage(), e);
            }
        }

        public Chunk getChild(int id) {
            for (int i = 0; i < this.children.size; i++) {
                Chunk child = this.children.get(i);
                if (child.getId() == id) {
                    return child;
                }
            }
            return null;
        }

        public Chunk[] getChildren(int id) {
            Array<Chunk> meshes = new Array<>(true, 16, Chunk.class);
            for (int i = 0; i < this.children.size; i++) {
                Chunk child = this.children.get(i);
                if (child.getId() == id) {
                    meshes.add(child);
                }
            }
            meshes.shrink();
            return (Chunk[]) meshes.items;
        }
    }

    public static Chunk readChunks(InputStream in) throws IOException {
        return loadChunks(in, 0);
    }

    private static Chunk loadChunks(InputStream in, int fileSize) throws IOException {
        byte[] bytes = readStream(in, fileSize);
        return loadChunk(new CountingDataInputStream(new ByteArrayInputStream(bytes)), bytes);
    }

    private static Chunk loadChunk(CountingDataInputStream din, byte[] bytes) throws IOException {
        int id = din.readInt();
        int payloadSize = din.readInt();
        int numChildren = din.readInt();
        int offset = din.getReadBytes();
        din.skipBytes(payloadSize);
        Chunk chunk = new Chunk(id, (Chunk) null, bytes, offset, payloadSize);
        for (int i = 0; i < numChildren; i++) {
            Chunk child = loadChunk(din, bytes);
            child.parent = chunk;
            chunk.children.add(child);
        }
        return chunk;
    }

    private static byte[] readStream(InputStream in, int size) throws IOException {
        if (size == 0) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[10240];
            while (true) {
                int readBytes = in.read(buffer);
                if (readBytes == -1) {
                    return bytes.toByteArray();
                }
                bytes.write(buffer, 0, readBytes);
            }
        } else {
            byte[] bytes2 = new byte[size];
            new DataInputStream(in).readFully(bytes2);
            return bytes2;
        }
    }

    public static void printChunks(Chunk chunk) {
        printChunks(chunk, 0);
    }

    private static void printChunks(Chunk chunk, int level) {
        String id;
        String payload = null;
        switch (chunk.getId()) {
            case 1:
                id = "VERSION_INFO";
                payload = rep("   ", level + 1) + "major: " + chunk.readByte() + ", minor: " + chunk.readByte();
                break;
            case G3dConstants.STILL_MODEL:
                id = "STILL_MODEL";
                payload = rep("   ", level + 1) + "#submeshes: " + chunk.readInt();
                break;
            case 4352:
                id = "STILL_SUBMESH";
                payload = rep("   ", level + 1) + "name: " + chunk.readString() + ", primitive type: " + chunk.readInt();
                break;
            case G3dConstants.VERTEX_LIST:
                id = "VERTEX_LIST";
                payload = rep("   ", level + 1) + "#vertices: " + chunk.readInt() + ": " + Arrays.toString(chunk.readFloats()).substring(0, 400);
                break;
            case G3dConstants.INDEX_LIST:
                id = "INDEX_LIST";
                payload = rep("   ", level + 1) + "#indices: " + chunk.readInt() + ": " + Arrays.toString(chunk.readShorts()).substring(0, 400);
                break;
            case G3dConstants.VERTEX_ATTRIBUTES:
                id = "VERTEX_ATTRIBUTES";
                payload = rep("   ", level + 1) + "#attributes: " + chunk.readInt();
                break;
            case G3dConstants.VERTEX_ATTRIBUTE:
                id = "VERTEX_ATTRIBUTE";
                payload = rep("   ", level + 1) + "usage: " + chunk.readInt() + ", components: " + chunk.readInt() + ", name: " + chunk.readString();
                break;
            case G3dConstants.G3D_ROOT:
                id = "G3D_ROOT";
                break;
            default:
                id = "unknown [" + null + "]";
                payload = rep("   ", level + 1) + "unknown";
                break;
        }
        System.out.println(rep("   ", level) + id + " {");
        if (payload != null) {
            System.out.println(payload);
        }
        Iterator i$ = chunk.getChildren().iterator();
        while (i$.hasNext()) {
            printChunks(i$.next(), level + 1);
        }
        System.out.println(rep("   ", level) + "}");
    }

    private static String rep(String c, int n) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < n; i++) {
            buf.append(c);
        }
        return buf.toString();
    }
}
