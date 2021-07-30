package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

public class ImmediateModeRenderer20 implements ImmediateModeRenderer {
    private final int colorOffset;
    private final int maxVertices;
    private final Mesh mesh;
    private final int normalOffset;
    private int numSetTexCoords;
    private final int numTexCoords;
    private int numVertices;
    private boolean ownsShader;
    private int primitiveType;
    private final Matrix4 projModelView;
    private ShaderProgram shader;
    private final int texCoordOffset;
    private int vertexIdx;
    private final int vertexSize;
    private final float[] vertices;

    public ImmediateModeRenderer20(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        this(5000, hasNormals, hasColors, numTexCoords2, createDefaultShader(hasNormals, hasColors, numTexCoords2));
        this.ownsShader = true;
    }

    public ImmediateModeRenderer20(int maxVertices2, boolean hasNormals, boolean hasColors, int numTexCoords2) {
        this(maxVertices2, hasNormals, hasColors, numTexCoords2, createDefaultShader(hasNormals, hasColors, numTexCoords2));
        this.ownsShader = true;
    }

    public ImmediateModeRenderer20(int maxVertices2, boolean hasNormals, boolean hasColors, int numTexCoords2, ShaderProgram shader2) {
        int i;
        int i2 = 0;
        this.projModelView = new Matrix4();
        this.maxVertices = maxVertices2;
        this.numTexCoords = numTexCoords2;
        this.shader = shader2;
        this.mesh = new Mesh(false, maxVertices2, 0, buildVertexAttributes(hasNormals, hasColors, numTexCoords2));
        this.vertices = new float[((this.mesh.getVertexAttributes().vertexSize / 4) * maxVertices2)];
        this.vertexSize = this.mesh.getVertexAttributes().vertexSize / 4;
        this.normalOffset = this.mesh.getVertexAttribute(2) != null ? this.mesh.getVertexAttribute(2).offset / 4 : 0;
        if (this.mesh.getVertexAttribute(5) != null) {
            i = this.mesh.getVertexAttribute(5).offset / 4;
        } else {
            i = 0;
        }
        this.colorOffset = i;
        this.texCoordOffset = this.mesh.getVertexAttribute(3) != null ? this.mesh.getVertexAttribute(3).offset / 4 : i2;
    }

    private VertexAttribute[] buildVertexAttributes(boolean hasNormals, boolean hasColor, int numTexCoords2) {
        Array<VertexAttribute> attribs = new Array<>();
        attribs.add(new VertexAttribute(0, 3, ShaderProgram.POSITION_ATTRIBUTE));
        if (hasNormals) {
            attribs.add(new VertexAttribute(2, 3, ShaderProgram.NORMAL_ATTRIBUTE));
        }
        if (hasColor) {
            attribs.add(new VertexAttribute(5, 4, ShaderProgram.COLOR_ATTRIBUTE));
        }
        for (int i = 0; i < numTexCoords2; i++) {
            attribs.add(new VertexAttribute(3, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + i));
        }
        VertexAttribute[] array = new VertexAttribute[attribs.size];
        for (int i2 = 0; i2 < attribs.size; i2++) {
            array[i2] = attribs.get(i2);
        }
        return array;
    }

    public void setShader(ShaderProgram shader2) {
        if (this.ownsShader) {
            this.shader.dispose();
        }
        this.shader = shader2;
        this.ownsShader = false;
    }

    public void begin(Matrix4 projModelView2, int primitiveType2) {
        this.projModelView.set(projModelView2);
        this.primitiveType = primitiveType2;
    }

    public void color(float r, float g, float b, float a) {
        this.vertices[this.vertexIdx + this.colorOffset] = Color.toFloatBits(r, g, b, a);
    }

    public void texCoord(float u, float v) {
        int idx = this.vertexIdx + this.texCoordOffset;
        this.vertices[idx] = u;
        this.vertices[idx + 1] = v;
        this.numSetTexCoords += 2;
    }

    public void normal(float x, float y, float z) {
        int idx = this.vertexIdx + this.normalOffset;
        this.vertices[idx] = x;
        this.vertices[idx + 1] = y;
        this.vertices[idx + 2] = z;
    }

    public void vertex(float x, float y, float z) {
        int idx = this.vertexIdx;
        this.vertices[idx] = x;
        this.vertices[idx + 1] = y;
        this.vertices[idx + 2] = z;
        this.numSetTexCoords = 0;
        this.vertexIdx += this.vertexSize;
        this.numVertices++;
    }

    public void end() {
        if (this.numVertices != 0) {
            this.shader.begin();
            this.shader.setUniformMatrix("u_projModelView", this.projModelView);
            for (int i = 0; i < this.numTexCoords; i++) {
                this.shader.setUniformi("u_sampler" + i, i);
            }
            this.mesh.setVertices(this.vertices, 0, this.vertexIdx);
            this.mesh.render(this.shader, this.primitiveType);
            this.shader.end();
            this.numSetTexCoords = 0;
            this.vertexIdx = 0;
            this.numVertices = 0;
        }
    }

    public int getNumVertices() {
        return this.numVertices;
    }

    public int getMaxVertices() {
        return this.maxVertices;
    }

    public void dispose() {
        if (this.ownsShader && this.shader != null) {
            this.shader.dispose();
        }
        this.mesh.dispose();
    }

    private static String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        String shader2 = "attribute vec4 a_position;\n" + (hasNormals ? "attribute vec3 a_normal;\n" : "") + (hasColors ? "attribute vec4 a_color;\n" : "");
        for (int i = 0; i < numTexCoords2; i++) {
            shader2 = shader2 + "attribute vec2 a_texCoord" + i + ";\n";
        }
        String shader3 = (shader2 + "uniform mat4 u_projModelView;\n") + (hasColors ? "varying vec4 v_col;\n" : "");
        for (int i2 = 0; i2 < numTexCoords2; i2++) {
            shader3 = shader3 + "varying vec2 v_tex" + i2 + ";\n";
        }
        String shader4 = shader3 + "void main() {\n   gl_Position = u_projModelView * a_position;\n" + (hasColors ? "   v_col = a_color;\n" : "");
        for (int i3 = 0; i3 < numTexCoords2; i3++) {
            shader4 = shader4 + "   v_tex" + i3 + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i3 + ";\n";
        }
        return (shader4 + "   gl_PointSize = 1.0;\n") + "}\n";
    }

    private static String createFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        String shader2;
        String shader3 = "#ifdef GL_ES\nprecision highp float;\n#endif\n";
        if (hasColors) {
            shader3 = shader3 + "varying vec4 v_col;\n";
        }
        for (int i = 0; i < numTexCoords2; i++) {
            shader3 = (shader3 + "varying vec2 v_tex" + i + ";\n") + "uniform sampler2D u_sampler" + i + ";\n";
        }
        String shader4 = shader3 + "void main() {\n   gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
        if (numTexCoords2 > 0) {
            shader4 = shader4 + " * ";
        }
        for (int i2 = 0; i2 < numTexCoords2; i2++) {
            if (i2 == numTexCoords2 - 1) {
                shader2 = shader2 + " texture2D(u_sampler" + i2 + ",  v_tex" + i2 + ")";
            } else {
                shader2 = shader2 + " texture2D(u_sampler" + i2 + ",  v_tex" + i2 + ") *";
            }
        }
        return shader2 + ";\n}";
    }

    public static ShaderProgram createDefaultShader(boolean hasNormals, boolean hasColors, int numTexCoords2) {
        return new ShaderProgram(createVertexShader(hasNormals, hasColors, numTexCoords2), createFragmentShader(hasNormals, hasColors, numTexCoords2));
    }
}
