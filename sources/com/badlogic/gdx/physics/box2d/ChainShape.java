package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

public class ChainShape extends Shape {
    private static float[] verts = new float[2];
    boolean isLooped = false;

    private native void jniCreateChain(long j, float[] fArr, int i);

    private native void jniCreateLoop(long j, float[] fArr, int i);

    private native void jniGetVertex(long j, int i, float[] fArr);

    private native int jniGetVertexCount(long j);

    private native void jniSetNextVertex(long j, float f, float f2);

    private native void jniSetPrevVertex(long j, float f, float f2);

    private native long newChainShape();

    public ChainShape() {
        this.addr = newChainShape();
    }

    ChainShape(long addr) {
        this.addr = addr;
    }

    public Shape.Type getType() {
        return Shape.Type.Chain;
    }

    public void createLoop(Vector2[] vertices) {
        float[] verts2 = new float[(vertices.length * 2)];
        int i = 0;
        int j = 0;
        while (i < vertices.length * 2) {
            verts2[i] = vertices[j].f165x;
            verts2[i + 1] = vertices[j].f166y;
            i += 2;
            j++;
        }
        jniCreateLoop(this.addr, verts2, verts2.length / 2);
        this.isLooped = true;
    }

    public void createChain(Vector2[] vertices) {
        float[] verts2 = new float[(vertices.length * 2)];
        int i = 0;
        int j = 0;
        while (i < vertices.length * 2) {
            verts2[i] = vertices[j].f165x;
            verts2[i + 1] = vertices[j].f166y;
            i += 2;
            j++;
        }
        jniCreateChain(this.addr, verts2, verts2.length / 2);
        this.isLooped = false;
    }

    public void setPrevVertex(Vector2 prevVertex) {
        setPrevVertex(prevVertex.f165x, prevVertex.f166y);
    }

    public void setPrevVertex(float prevVertexX, float prevVertexY) {
        jniSetPrevVertex(this.addr, prevVertexX, prevVertexY);
    }

    public void setNextVertex(Vector2 nextVertex) {
        setNextVertex(nextVertex.f165x, nextVertex.f166y);
    }

    public void setNextVertex(float nextVertexX, float nextVertexY) {
        jniSetNextVertex(this.addr, nextVertexX, nextVertexY);
    }

    public int getVertexCount() {
        return jniGetVertexCount(this.addr);
    }

    public void getVertex(int index, Vector2 vertex) {
        jniGetVertex(this.addr, index, verts);
        vertex.f165x = verts[0];
        vertex.f166y = verts[1];
    }

    public boolean isLooped() {
        return this.isLooped;
    }
}
