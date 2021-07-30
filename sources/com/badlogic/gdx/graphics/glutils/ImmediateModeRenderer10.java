package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.FloatBuffer;

public class ImmediateModeRenderer10 implements ImmediateModeRenderer {
    private float[] colors;
    private FloatBuffer colorsBuffer;
    private boolean hasCols;
    private boolean hasNors;
    private boolean hasTexCoords;
    private int idxCols;
    private int idxNors;
    private int idxPos;
    private int idxTexCoords;
    private final int maxVertices;
    private float[] normals;
    private FloatBuffer normalsBuffer;
    private int numVertices;
    private float[] positions;
    private FloatBuffer positionsBuffer;
    private int primitiveType;
    private float[] texCoords;
    private FloatBuffer texCoordsBuffer;

    public ImmediateModeRenderer10() {
        this(2000);
    }

    public ImmediateModeRenderer10(int maxVertices2) {
        this.idxPos = 0;
        this.idxCols = 0;
        this.idxNors = 0;
        this.idxTexCoords = 0;
        this.maxVertices = maxVertices2;
        if (Gdx.graphics.isGL20Available()) {
            throw new GdxRuntimeException("ImmediateModeRenderer can only be used with OpenGL ES 1.0/1.1");
        }
        this.positions = new float[(maxVertices2 * 3)];
        this.positionsBuffer = BufferUtils.newFloatBuffer(maxVertices2 * 3);
        this.colors = new float[(maxVertices2 * 4)];
        this.colorsBuffer = BufferUtils.newFloatBuffer(maxVertices2 * 4);
        this.normals = new float[(maxVertices2 * 3)];
        this.normalsBuffer = BufferUtils.newFloatBuffer(maxVertices2 * 3);
        this.texCoords = new float[(maxVertices2 * 2)];
        this.texCoordsBuffer = BufferUtils.newFloatBuffer(maxVertices2 * 2);
    }

    public void begin(Matrix4 projModelView, int primitiveType2) {
        GL10 gl = Gdx.gl10;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(projModelView.val, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        begin(primitiveType2);
    }

    public void begin(int primitiveType2) {
        this.primitiveType = primitiveType2;
        this.numVertices = 0;
        this.idxPos = 0;
        this.idxCols = 0;
        this.idxNors = 0;
        this.idxTexCoords = 0;
        this.hasCols = false;
        this.hasNors = false;
        this.hasTexCoords = false;
    }

    public void color(float r, float g, float b, float a) {
        this.colors[this.idxCols] = r;
        this.colors[this.idxCols + 1] = g;
        this.colors[this.idxCols + 2] = b;
        this.colors[this.idxCols + 3] = a;
        this.hasCols = true;
    }

    public void normal(float x, float y, float z) {
        this.normals[this.idxNors] = x;
        this.normals[this.idxNors + 1] = y;
        this.normals[this.idxNors + 2] = z;
        this.hasNors = true;
    }

    public void texCoord(float u, float v) {
        this.texCoords[this.idxTexCoords] = u;
        this.texCoords[this.idxTexCoords + 1] = v;
        this.hasTexCoords = true;
    }

    public void vertex(float x, float y, float z) {
        float[] fArr = this.positions;
        int i = this.idxPos;
        this.idxPos = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.positions;
        int i2 = this.idxPos;
        this.idxPos = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.positions;
        int i3 = this.idxPos;
        this.idxPos = i3 + 1;
        fArr3[i3] = z;
        if (this.hasCols) {
            this.idxCols += 4;
        }
        if (this.hasNors) {
            this.idxNors += 3;
        }
        if (this.hasTexCoords) {
            this.idxTexCoords += 2;
        }
        this.numVertices++;
    }

    public int getNumVertices() {
        return this.numVertices;
    }

    public int getMaxVertices() {
        return this.maxVertices;
    }

    public void end() {
        if (this.idxPos != 0) {
            GL10 gl = Gdx.gl10;
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            this.positionsBuffer.clear();
            BufferUtils.copy(this.positions, (Buffer) this.positionsBuffer, this.idxPos, 0);
            gl.glVertexPointer(3, 5126, 0, this.positionsBuffer);
            if (this.hasCols) {
                gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                this.colorsBuffer.clear();
                BufferUtils.copy(this.colors, (Buffer) this.colorsBuffer, this.idxCols, 0);
                gl.glColorPointer(4, 5126, 0, this.colorsBuffer);
            }
            if (this.hasNors) {
                gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
                this.normalsBuffer.clear();
                BufferUtils.copy(this.normals, (Buffer) this.normalsBuffer, this.idxNors, 0);
                gl.glNormalPointer(5126, 0, this.normalsBuffer);
            }
            if (this.hasTexCoords) {
                gl.glClientActiveTexture(33984);
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                this.texCoordsBuffer.clear();
                BufferUtils.copy(this.texCoords, (Buffer) this.texCoordsBuffer, this.idxTexCoords, 0);
                gl.glTexCoordPointer(2, 5126, 0, this.texCoordsBuffer);
            }
            gl.glDrawArrays(this.primitiveType, 0, this.idxPos / 3);
            if (this.hasCols) {
                gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            }
            if (this.hasNors) {
                gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
            }
            if (this.hasTexCoords) {
                gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            }
        }
    }

    public void vertex(Vector3 point) {
        vertex(point.f170x, point.f171y, point.f172z);
    }

    public void dispose() {
    }
}
