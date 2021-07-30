package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VertexArray implements VertexData {
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    boolean isBound;

    public VertexArray(int numVertices, VertexAttribute... attributes2) {
        this(numVertices, new VertexAttributes(attributes2));
    }

    public VertexArray(int numVertices, VertexAttributes attributes2) {
        this.isBound = false;
        this.attributes = attributes2;
        this.byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        this.buffer = this.byteBuffer.asFloatBuffer();
        this.buffer.flip();
        this.byteBuffer.flip();
    }

    public void dispose() {
        BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
    }

    public FloatBuffer getBuffer() {
        return this.buffer;
    }

    public int getNumVertices() {
        return (this.buffer.limit() * 4) / this.attributes.vertexSize;
    }

    public int getNumMaxVertices() {
        return this.byteBuffer.capacity() / this.attributes.vertexSize;
    }

    public void setVertices(float[] vertices, int offset, int count) {
        BufferUtils.copy(vertices, (Buffer) this.byteBuffer, count, offset);
        this.buffer.position(0);
        this.buffer.limit(count);
    }

    public void bind() {
        GL10 gl = Gdx.gl10;
        int textureUnit = 0;
        int numAttributes = this.attributes.size();
        this.byteBuffer.limit(this.buffer.limit() * 4);
        for (int i = 0; i < numAttributes; i++) {
            VertexAttribute attribute = this.attributes.get(i);
            switch (attribute.usage) {
                case 0:
                    this.byteBuffer.position(attribute.offset);
                    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                    gl.glVertexPointer(attribute.numComponents, 5126, this.attributes.vertexSize, this.byteBuffer);
                    break;
                case 1:
                case 5:
                    int colorType = 5126;
                    if (attribute.usage == 5) {
                        colorType = 5121;
                    }
                    this.byteBuffer.position(attribute.offset);
                    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                    gl.glColorPointer(attribute.numComponents, colorType, this.attributes.vertexSize, this.byteBuffer);
                    break;
                case 2:
                    this.byteBuffer.position(attribute.offset);
                    gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
                    gl.glNormalPointer(5126, this.attributes.vertexSize, this.byteBuffer);
                    break;
                case 3:
                    gl.glClientActiveTexture(33984 + textureUnit);
                    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    this.byteBuffer.position(attribute.offset);
                    gl.glTexCoordPointer(attribute.numComponents, 5126, this.attributes.vertexSize, this.byteBuffer);
                    textureUnit++;
                    break;
            }
        }
        this.isBound = true;
    }

    public void unbind() {
        GL10 gl = Gdx.gl10;
        int textureUnit = 0;
        int numAttributes = this.attributes.size();
        for (int i = 0; i < numAttributes; i++) {
            switch (this.attributes.get(i).usage) {
                case 1:
                case 5:
                    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
                    break;
                case 2:
                    gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
                    break;
                case 3:
                    gl.glClientActiveTexture(33984 + textureUnit);
                    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    textureUnit++;
                    break;
            }
        }
        this.byteBuffer.position(0);
        this.isBound = false;
    }

    public void bind(ShaderProgram shader) {
        GL20 gl20 = Gdx.gl20;
        int numAttributes = this.attributes.size();
        this.byteBuffer.limit(this.buffer.limit() * 4);
        for (int i = 0; i < numAttributes; i++) {
            VertexAttribute attribute = this.attributes.get(i);
            shader.enableVertexAttribute(attribute.alias);
            int colorType = 5126;
            boolean normalize = false;
            if (attribute.usage == 5) {
                colorType = 5121;
                normalize = true;
            }
            this.byteBuffer.position(attribute.offset);
            shader.setVertexAttribute(attribute.alias, attribute.numComponents, colorType, normalize, this.attributes.vertexSize, (Buffer) this.byteBuffer);
        }
        this.isBound = true;
    }

    public void unbind(ShaderProgram shader) {
        GL20 gl20 = Gdx.gl20;
        int numAttributes = this.attributes.size();
        for (int i = 0; i < numAttributes; i++) {
            shader.disableVertexAttribute(this.attributes.get(i).alias);
        }
        this.isBound = false;
    }

    public VertexAttributes getAttributes() {
        return this.attributes;
    }
}
