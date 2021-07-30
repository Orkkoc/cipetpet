package com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountingDataInputStream implements DataInput {

    /* renamed from: in */
    DataInputStream f140in;
    int readBytes = 0;

    public CountingDataInputStream(InputStream in) {
        this.f140in = new DataInputStream(in);
    }

    public int getReadBytes() {
        return this.readBytes;
    }

    public void readFully(byte[] b) throws IOException {
        this.readBytes += b.length;
        this.f140in.readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        this.readBytes += len;
        this.f140in.readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException {
        int skipped = this.f140in.skipBytes(n);
        this.readBytes += skipped;
        return skipped;
    }

    public boolean readBoolean() throws IOException {
        this.readBytes++;
        return this.f140in.readBoolean();
    }

    public byte readByte() throws IOException {
        this.readBytes++;
        return this.f140in.readByte();
    }

    public int readUnsignedByte() throws IOException {
        this.readBytes++;
        return this.f140in.readUnsignedByte();
    }

    public short readShort() throws IOException {
        this.readBytes += 2;
        return this.f140in.readShort();
    }

    public int readUnsignedShort() throws IOException {
        this.readBytes += 2;
        return this.f140in.readUnsignedShort();
    }

    public char readChar() throws IOException {
        this.readBytes += 2;
        return this.f140in.readChar();
    }

    public int readInt() throws IOException {
        this.readBytes += 4;
        return this.f140in.readInt();
    }

    public long readLong() throws IOException {
        this.readBytes += 8;
        return this.f140in.readLong();
    }

    public float readFloat() throws IOException {
        this.readBytes += 4;
        return this.f140in.readFloat();
    }

    public double readDouble() throws IOException {
        this.readBytes += 8;
        return this.f140in.readDouble();
    }

    public String readLine() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public String readUTF() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
