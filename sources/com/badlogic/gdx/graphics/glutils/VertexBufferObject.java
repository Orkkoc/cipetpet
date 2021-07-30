package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBufferObject implements VertexData {
    static final IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    int bufferHandle;
    final ByteBuffer byteBuffer;
    boolean isBound;
    boolean isDirty;
    final boolean isStatic;
    final int usage;

    public VertexBufferObject(boolean isStatic2, int numVertices, VertexAttribute... attributes2) {
        this(isStatic2, numVertices, new VertexAttributes(attributes2));
    }

    public VertexBufferObject(boolean isStatic2, int numVertices, VertexAttributes attributes2) {
        this.isDirty = false;
        this.isBound = false;
        this.isStatic = isStatic2;
        this.attributes = attributes2;
        this.byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        this.buffer = this.byteBuffer.asFloatBuffer();
        this.buffer.flip();
        this.byteBuffer.flip();
        this.bufferHandle = createBufferObject();
        this.usage = isStatic2 ? 35044 : 35048;
    }

    private int createBufferObject() {
        if (Gdx.gl20 != null) {
            Gdx.gl20.glGenBuffers(1, tmpHandle);
        } else {
            Gdx.gl11.glGenBuffers(1, tmpHandle);
        }
        return tmpHandle.get(0);
    }

    public VertexAttributes getAttributes() {
        return this.attributes;
    }

    public int getNumVertices() {
        return (this.buffer.limit() * 4) / this.attributes.vertexSize;
    }

    public int getNumMaxVertices() {
        return this.byteBuffer.capacity() / this.attributes.vertexSize;
    }

    public FloatBuffer getBuffer() {
        this.isDirty = true;
        return this.buffer;
    }

    public void setVertices(float[] vertices, int offset, int count) {
        this.isDirty = true;
        BufferUtils.copy(vertices, (Buffer) this.byteBuffer, count, offset);
        this.buffer.position(0);
        this.buffer.limit(count);
        if (this.isBound) {
            if (Gdx.gl20 != null) {
                Gdx.gl20.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            } else {
                Gdx.gl11.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            }
            this.isDirty = false;
        }
    }

    public void bind() {
        GL11 gl = Gdx.gl11;
        gl.glBindBuffer(34962, this.bufferHandle);
        if (this.isDirty) {
            this.byteBuffer.limit(this.buffer.limit() * 4);
            gl.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
        int textureUnit = 0;
        int numAttributes = this.attributes.size();
        for (int i = 0; i < numAttributes; i++) {
            VertexAttribute attribute = this.attributes.get(i);
            switch (attribute.usage) {
                case 0:
                    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                    gl.glVertexPointer(attribute.numComponents, 5126, this.attributes.vertexSize, attribute.offset);
                    break;
                case 1:
                case 5:
                    int colorType = 5126;
                    if (attribute.usage == 5) {
                        colorType = 5121;
                    }
                    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                    gl.glColorPointer(attribute.numComponents, colorType, this.attributes.vertexSize, attribute.offset);
                    break;
                case 2:
                    gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
                    gl.glNormalPointer(5126, this.attributes.vertexSize, attribute.offset);
                    break;
                case 3:
                    gl.glClientActiveTexture(33984 + textureUnit);
                    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    gl.glTexCoordPointer(attribute.numComponents, 5126, this.attributes.vertexSize, attribute.offset);
                    textureUnit++;
                    break;
            }
        }
        this.isBound = true;
    }

    public void bind(ShaderProgram shader) {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(34962, this.bufferHandle);
        if (this.isDirty) {
            this.byteBuffer.limit(this.buffer.limit() * 4);
            gl.glBufferData(34962, this.byteBuffer.limit(), this.byteBuffer, this.usage);
            this.isDirty = false;
        }
        int numAttributes = this.attributes.size();
        for (int i = 0; i < numAttributes; i++) {
            VertexAttribute attribute = this.attributes.get(i);
            shader.enableVertexAttribute(attribute.alias);
            int colorType = 5126;
            boolean normalize = false;
            if (attribute.usage == 5) {
                colorType = 5121;
                normalize = true;
            }
            shader.setVertexAttribute(attribute.alias, attribute.numComponents, colorType, normalize, this.attributes.vertexSize, attribute.offset);
        }
        this.isBound = true;
    }

    public void unbind() {
        GL11 gl = Gdx.gl11;
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
        gl.glBindBuffer(34962, 0);
        this.isBound = false;
    }

    public void unbind(ShaderProgram shader) {
        GL20 gl = Gdx.gl20;
        int numAttributes = this.attributes.size();
        for (int i = 0; i < numAttributes; i++) {
            shader.disableVertexAttribute(this.attributes.get(i).alias);
        }
        gl.glBindBuffer(34962, 0);
        this.isBound = false;
    }

    public void invalidate() {
        this.bufferHandle = createBufferObject();
        this.isDirty = true;
    }

    public void dispose() {
        if (Gdx.gl20 != null) {
            tmpHandle.clear();
            tmpHandle.put(this.bufferHandle);
            tmpHandle.flip();
            GL20 gl = Gdx.gl20;
            gl.glBindBuffer(34962, 0);
            gl.glDeleteBuffers(1, tmpHandle);
            this.bufferHandle = 0;
        } else {
            tmpHandle.clear();
            tmpHandle.put(this.bufferHandle);
            tmpHandle.flip();
            GL11 gl2 = Gdx.gl11;
            gl2.glBindBuffer(34962, 0);
            gl2.glDeleteBuffers(1, tmpHandle);
            this.bufferHandle = 0;
        }
        BufferUtils.disposeUnsafeByteBuffer(this.byteBuffer);
    }
}
