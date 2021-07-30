package com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks;

import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import com.badlogic.gdx.utils.Array;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChunkWriter {
    Chunk currChunk = this.root;
    final Chunk root = new Chunk(G3dConstants.G3D_ROOT);

    class Chunk {
        final Array<Chunk> children = new Array<>();

        /* renamed from: id */
        final int f139id;
        final DataOutputStream out = new DataOutputStream(this.payload);
        final Chunk parent;
        final ByteArrayOutputStream payload = new ByteArrayOutputStream();

        public Chunk(int id) {
            this.f139id = id;
            this.parent = null;
        }

        public Chunk(int id, Chunk parent2) {
            this.f139id = id;
            this.parent = parent2;
        }
    }

    public void newChunk(int id) {
        Chunk chunk = new Chunk(id, this.currChunk);
        this.currChunk.children.add(chunk);
        this.currChunk = chunk;
    }

    public void endChunk() {
        this.currChunk = this.currChunk.parent;
    }

    public void writeByte(int v) {
        try {
            this.currChunk.out.writeByte(v);
        } catch (IOException e) {
        }
    }

    public void writeShort(short v) {
        try {
            this.currChunk.out.writeShort(v);
        } catch (IOException e) {
        }
    }

    public void writeInt(int v) {
        try {
            this.currChunk.out.writeInt(v);
        } catch (IOException e) {
        }
    }

    public void writeLong(long v) {
        try {
            this.currChunk.out.writeLong(v);
        } catch (IOException e) {
        }
    }

    public void writeFloat(float v) {
        try {
            this.currChunk.out.writeFloat(v);
        } catch (IOException e) {
        }
    }

    public void writeDouble(double v) {
        try {
            this.currChunk.out.writeDouble(v);
        } catch (IOException e) {
        }
    }

    public void writeString(String v) {
        try {
            byte[] bytes = v.getBytes("UTF-8");
            this.currChunk.out.writeInt(bytes.length);
            this.currChunk.out.write(bytes);
        } catch (IOException e) {
        }
    }

    public void writeToStream(OutputStream out) throws IOException {
        writeToStream(this.root, new DataOutputStream(out));
    }

    private void writeToStream(Chunk chunk, DataOutputStream out) throws IOException {
        out.writeInt(chunk.f139id);
        out.writeInt(chunk.payload.size());
        out.writeInt(chunk.children.size);
        out.write(chunk.payload.toByteArray());
        for (int i = 0; i < chunk.children.size; i++) {
            writeToStream(chunk.children.get(i), out);
        }
    }

    public void writeBytes(byte[] v) {
        try {
            this.currChunk.out.writeInt(v.length);
            for (byte writeByte : v) {
                this.currChunk.out.writeByte(writeByte);
            }
        } catch (IOException e) {
        }
    }

    public void writeShorts(short[] v) {
        try {
            this.currChunk.out.writeInt(v.length);
            for (short writeShort : v) {
                this.currChunk.out.writeShort(writeShort);
            }
        } catch (IOException e) {
        }
    }

    public void writeInts(int[] v) {
        try {
            this.currChunk.out.writeInt(v.length);
            for (int writeInt : v) {
                this.currChunk.out.writeInt(writeInt);
            }
        } catch (IOException e) {
        }
    }

    public void writeLongs(long[] v) {
        try {
            this.currChunk.out.writeInt(v.length);
            for (long writeLong : v) {
                this.currChunk.out.writeLong(writeLong);
            }
        } catch (IOException e) {
        }
    }

    public void writeFloats(float[] v) {
        try {
            this.currChunk.out.writeInt(v.length);
            for (float writeFloat : v) {
                this.currChunk.out.writeFloat(writeFloat);
            }
        } catch (IOException e) {
        }
    }

    public void writeDoubles(double[] v) {
        try {
            this.currChunk.out.writeInt(v.length);
            for (double writeDouble : v) {
                this.currChunk.out.writeDouble(writeDouble);
            }
        } catch (IOException e) {
        }
    }
}
