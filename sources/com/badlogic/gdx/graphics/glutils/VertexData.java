package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.Disposable;
import java.nio.FloatBuffer;

public interface VertexData extends Disposable {
    void bind();

    void bind(ShaderProgram shaderProgram);

    void dispose();

    VertexAttributes getAttributes();

    FloatBuffer getBuffer();

    int getNumMaxVertices();

    int getNumVertices();

    void setVertices(float[] fArr, int i, int i2);

    void unbind();

    void unbind(ShaderProgram shaderProgram);
}
