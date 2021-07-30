package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.NumberUtils;

public class PolygonSpriteBatch {
    private int blendDstFunc;
    private int blendSrcFunc;
    private boolean blendingDisabled;
    private Mesh[] buffers;
    float color;
    private final Matrix4 combinedMatrix;
    private int currBufferIdx;
    private ShaderProgram customShader;
    private boolean drawing;
    private int idx;
    private Texture lastTexture;
    public int maxVerticesInBatch;
    private Mesh mesh;
    private boolean ownsShader;
    private final Matrix4 projectionMatrix;
    public int renderCalls;
    private final ShaderProgram shader;
    private Color tempColor;
    public int totalRenderCalls;
    private final Matrix4 transformMatrix;
    private final float[] vertices;

    public PolygonSpriteBatch() {
        this(4000);
    }

    public PolygonSpriteBatch(int size) {
        this(size, (ShaderProgram) null);
    }

    public PolygonSpriteBatch(int size, ShaderProgram defaultShader) {
        this(size, 1, defaultShader);
    }

    public PolygonSpriteBatch(int size, int buffers2) {
        this(size, buffers2, (ShaderProgram) null);
    }

    public PolygonSpriteBatch(int size, int buffers2, ShaderProgram defaultShader) {
        this.lastTexture = null;
        this.idx = 0;
        this.currBufferIdx = 0;
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.combinedMatrix = new Matrix4();
        this.drawing = false;
        this.blendingDisabled = false;
        this.blendSrcFunc = 770;
        this.blendDstFunc = 771;
        this.color = Color.WHITE.toFloatBits();
        this.tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderCalls = 0;
        this.totalRenderCalls = 0;
        this.maxVerticesInBatch = 0;
        this.customShader = null;
        this.buffers = new Mesh[buffers2];
        for (int i = 0; i < buffers2; i++) {
            this.buffers[i] = new Mesh(Mesh.VertexDataType.VertexArray, false, size, 0, new VertexAttribute(0, 2, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(5, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(3, 2, "a_texCoord0"));
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        this.vertices = new float[(size * 5)];
        this.mesh = this.buffers[0];
        if (!Gdx.graphics.isGL20Available() || defaultShader != null) {
            this.shader = defaultShader;
            return;
        }
        this.shader = createDefaultShader();
        this.ownsShader = true;
    }

    public static ShaderProgram createDefaultShader() {
        ShaderProgram shader2 = new ShaderProgram("attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n", "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying LOWP vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}");
        if (shader2.isCompiled()) {
            return shader2;
        }
        throw new IllegalArgumentException("couldn't compile shader: " + shader2.getLog());
    }

    public void begin() {
        if (this.drawing) {
            throw new IllegalStateException("you have to call PolygonSpriteBatch.end() first");
        }
        this.renderCalls = 0;
        Gdx.f12gl.glDepthMask(false);
        if (!Gdx.graphics.isGL20Available()) {
            Gdx.f12gl.glEnable(3553);
        } else if (this.customShader != null) {
            this.customShader.begin();
        } else {
            this.shader.begin();
        }
        setupMatrices();
        this.idx = 0;
        this.lastTexture = null;
        this.drawing = true;
    }

    public void end() {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before end.");
        }
        if (this.idx > 0) {
            renderMesh();
        }
        this.lastTexture = null;
        this.idx = 0;
        this.drawing = false;
        GLCommon gl = Gdx.f12gl;
        gl.glDepthMask(true);
        if (isBlendingEnabled()) {
            gl.glDisable(3042);
        }
        if (!Gdx.graphics.isGL20Available()) {
            gl.glDisable(3553);
        } else if (this.customShader != null) {
            this.customShader.end();
        } else {
            this.shader.end();
        }
    }

    public void setColor(Color tint) {
        this.color = tint.toFloatBits();
    }

    public void setColor(float r, float g, float b, float a) {
        this.color = NumberUtils.intToFloatColor((((int) (255.0f * a)) << 24) | (((int) (255.0f * b)) << 16) | (((int) (255.0f * g)) << 8) | ((int) (255.0f * r)));
    }

    public void setColor(float color2) {
        this.color = color2;
    }

    public Color getColor() {
        int intBits = NumberUtils.floatToIntColor(this.color);
        Color color2 = this.tempColor;
        color2.f70r = ((float) (intBits & 255)) / 255.0f;
        color2.f69g = ((float) ((intBits >>> 8) & 255)) / 255.0f;
        color2.f68b = ((float) ((intBits >>> 16) & 255)) / 255.0f;
        color2.f67a = ((float) ((intBits >>> 24) & 255)) / 255.0f;
        return color2;
    }

    public void draw(PolygonRegion region, float x, float y) {
        draw(region, x, y, (float) region.getRegion().getRegionWidth(), (float) region.getRegion().getRegionHeight());
    }

    public void draw(PolygonRegion region, float x, float y, float width, float height) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        Texture texture = region.getRegion().texture;
        if (texture != this.lastTexture) {
            switchTexture(texture);
        }
        float[] localVertices = region.getLocalVertices();
        float[] texCoords = region.getTextureCoords();
        if (this.idx + localVertices.length > this.vertices.length) {
            renderMesh();
        }
        float sX = width / ((float) region.getRegion().getRegionWidth());
        float sY = height / ((float) region.getRegion().getRegionHeight());
        for (int i = 0; i < localVertices.length; i += 2) {
            float[] fArr = this.vertices;
            int i2 = this.idx;
            this.idx = i2 + 1;
            fArr[i2] = (localVertices[i] * sX) + x;
            float[] fArr2 = this.vertices;
            int i3 = this.idx;
            this.idx = i3 + 1;
            fArr2[i3] = (localVertices[i + 1] * sY) + y;
            float[] fArr3 = this.vertices;
            int i4 = this.idx;
            this.idx = i4 + 1;
            fArr3[i4] = this.color;
            float[] fArr4 = this.vertices;
            int i5 = this.idx;
            this.idx = i5 + 1;
            fArr4[i5] = texCoords[i];
            float[] fArr5 = this.vertices;
            int i6 = this.idx;
            this.idx = i6 + 1;
            fArr5[i6] = texCoords[i + 1];
        }
    }

    public void draw(PolygonRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        Texture texture = region.getRegion().texture;
        if (texture != this.lastTexture) {
            switchTexture(texture);
        }
        float[] localVertices = region.getLocalVertices();
        float[] texCoords = region.getTextureCoords();
        if (this.idx + localVertices.length > this.vertices.length) {
            renderMesh();
        }
        float worldOriginX = x + originX;
        float worldOriginY = y + originY;
        float sX = width / ((float) region.getRegion().getRegionWidth());
        float sY = height / ((float) region.getRegion().getRegionHeight());
        float cos = MathUtils.cosDeg(rotation);
        float sin = MathUtils.sinDeg(rotation);
        for (int i = 0; i < localVertices.length; i += 2) {
            float fx = (localVertices[i] * sX) - originX;
            float fy = (localVertices[i + 1] * sY) - originY;
            if (scaleX != 1.0f || scaleY != 1.0f) {
                fx *= scaleX;
                fy *= scaleY;
            }
            float[] fArr = this.vertices;
            int i2 = this.idx;
            this.idx = i2 + 1;
            fArr[i2] = ((cos * fx) - (sin * fy)) + worldOriginX;
            float[] fArr2 = this.vertices;
            int i3 = this.idx;
            this.idx = i3 + 1;
            fArr2[i3] = (sin * fx) + (cos * fy) + worldOriginY;
            float[] fArr3 = this.vertices;
            int i4 = this.idx;
            this.idx = i4 + 1;
            fArr3[i4] = this.color;
            float[] fArr4 = this.vertices;
            int i5 = this.idx;
            this.idx = i5 + 1;
            fArr4[i5] = texCoords[i];
            float[] fArr5 = this.vertices;
            int i6 = this.idx;
            this.idx = i6 + 1;
            fArr5[i6] = texCoords[i + 1];
        }
    }

    public void draw(PolygonRegion region, float[] spriteVertices, int offset, int length) {
        if (!this.drawing) {
            throw new IllegalStateException("PolygonSpriteBatch.begin must be called before draw.");
        }
        Texture texture = region.getRegion().texture;
        if (texture != this.lastTexture) {
            switchTexture(texture);
        }
        if (this.idx + length > this.vertices.length) {
            renderMesh();
        }
        if (length <= this.vertices.length) {
            System.arraycopy(spriteVertices, offset, this.vertices, this.idx, length);
            this.idx += length;
        }
    }

    public void flush() {
        renderMesh();
    }

    private void renderMesh() {
        if (this.idx != 0) {
            this.renderCalls++;
            this.totalRenderCalls++;
            int verticesInBatch = this.idx / 5;
            if (verticesInBatch > this.maxVerticesInBatch) {
                this.maxVerticesInBatch = verticesInBatch;
            }
            this.lastTexture.bind();
            this.mesh.setVertices(this.vertices, 0, this.idx);
            if (this.blendingDisabled) {
                Gdx.f12gl.glDisable(3042);
            } else {
                Gdx.f12gl.glEnable(3042);
                Gdx.f12gl.glBlendFunc(this.blendSrcFunc, this.blendDstFunc);
            }
            if (!Gdx.graphics.isGL20Available()) {
                this.mesh.render(4, 0, verticesInBatch);
            } else if (this.customShader != null) {
                this.mesh.render(this.customShader, 4, 0, verticesInBatch);
            } else {
                this.mesh.render(this.shader, 4, 0, verticesInBatch);
            }
            this.idx = 0;
            this.currBufferIdx++;
            if (this.currBufferIdx == this.buffers.length) {
                this.currBufferIdx = 0;
            }
            this.mesh = this.buffers[this.currBufferIdx];
        }
    }

    public void disableBlending() {
        renderMesh();
        this.blendingDisabled = true;
    }

    public void enableBlending() {
        renderMesh();
        this.blendingDisabled = false;
    }

    public void setBlendFunction(int srcFunc, int dstFunc) {
        renderMesh();
        this.blendSrcFunc = srcFunc;
        this.blendDstFunc = dstFunc;
    }

    public void dispose() {
        for (Mesh dispose : this.buffers) {
            dispose.dispose();
        }
        if (this.ownsShader && this.shader != null) {
            this.shader.dispose();
        }
    }

    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }

    public void setProjectionMatrix(Matrix4 projection) {
        if (this.drawing) {
            flush();
        }
        this.projectionMatrix.set(projection);
        if (this.drawing) {
            setupMatrices();
        }
    }

    public void setTransformMatrix(Matrix4 transform) {
        if (this.drawing) {
            flush();
        }
        this.transformMatrix.set(transform);
        if (this.drawing) {
            setupMatrices();
        }
    }

    private void setupMatrices() {
        if (!Gdx.graphics.isGL20Available()) {
            GL10 gl = Gdx.gl10;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadMatrixf(this.projectionMatrix.val, 0);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadMatrixf(this.transformMatrix.val, 0);
            return;
        }
        this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
        if (this.customShader != null) {
            this.customShader.setUniformMatrix("u_proj", this.projectionMatrix);
            this.customShader.setUniformMatrix("u_trans", this.transformMatrix);
            this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
            return;
        }
        this.shader.setUniformMatrix("u_projectionViewMatrix", this.combinedMatrix);
        this.shader.setUniformi("u_texture", 0);
    }

    private void switchTexture(Texture texture) {
        renderMesh();
        this.lastTexture = texture;
    }

    public void setShader(ShaderProgram shader2) {
        if (this.drawing) {
            flush();
            if (this.customShader != null) {
                this.customShader.end();
            } else {
                this.shader.end();
            }
        }
        this.customShader = shader2;
        if (this.drawing) {
            if (this.customShader != null) {
                this.customShader.begin();
            } else {
                this.shader.begin();
            }
            setupMatrices();
        }
    }

    public boolean isBlendingEnabled() {
        return !this.blendingDisabled;
    }
}
