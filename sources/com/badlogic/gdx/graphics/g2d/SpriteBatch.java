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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.NumberUtils;

public class SpriteBatch implements Disposable {

    /* renamed from: C1 */
    public static final int f85C1 = 2;

    /* renamed from: C2 */
    public static final int f86C2 = 7;

    /* renamed from: C3 */
    public static final int f87C3 = 12;

    /* renamed from: C4 */
    public static final int f88C4 = 17;

    /* renamed from: U1 */
    public static final int f89U1 = 3;

    /* renamed from: U2 */
    public static final int f90U2 = 8;

    /* renamed from: U3 */
    public static final int f91U3 = 13;

    /* renamed from: U4 */
    public static final int f92U4 = 18;

    /* renamed from: V1 */
    public static final int f93V1 = 4;

    /* renamed from: V2 */
    public static final int f94V2 = 9;

    /* renamed from: V3 */
    public static final int f95V3 = 14;

    /* renamed from: V4 */
    public static final int f96V4 = 19;

    /* renamed from: X1 */
    public static final int f97X1 = 0;

    /* renamed from: X2 */
    public static final int f98X2 = 5;

    /* renamed from: X3 */
    public static final int f99X3 = 10;

    /* renamed from: X4 */
    public static final int f100X4 = 15;

    /* renamed from: Y1 */
    public static final int f101Y1 = 1;

    /* renamed from: Y2 */
    public static final int f102Y2 = 6;

    /* renamed from: Y3 */
    public static final int f103Y3 = 11;

    /* renamed from: Y4 */
    public static final int f104Y4 = 16;
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
    private float invTexHeight;
    private float invTexWidth;
    private Texture lastTexture;
    public int maxSpritesInBatch;
    private Mesh mesh;
    private boolean ownsShader;
    private final Matrix4 projectionMatrix;
    public int renderCalls;
    private final ShaderProgram shader;
    private Color tempColor;
    public int totalRenderCalls;
    private final Matrix4 transformMatrix;
    private final float[] vertices;

    public SpriteBatch() {
        this(1000);
    }

    public SpriteBatch(int size) {
        this(size, (ShaderProgram) null);
    }

    public SpriteBatch(int size, ShaderProgram defaultShader) {
        this(size, 1, defaultShader);
    }

    public SpriteBatch(int size, int buffers2) {
        this(size, buffers2, (ShaderProgram) null);
    }

    public SpriteBatch(int size, int buffers2, ShaderProgram defaultShader) {
        this.lastTexture = null;
        this.invTexWidth = 0.0f;
        this.invTexHeight = 0.0f;
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
        this.maxSpritesInBatch = 0;
        this.customShader = null;
        this.buffers = new Mesh[buffers2];
        for (int i = 0; i < buffers2; i++) {
            this.buffers[i] = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6, new VertexAttribute(0, 2, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(5, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(3, 2, "a_texCoord0"));
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        this.vertices = new float[(size * 20)];
        int len = size * 6;
        short[] indices = new short[len];
        short j = 0;
        int i2 = 0;
        while (i2 < len) {
            indices[i2 + 0] = (short) (j + 0);
            indices[i2 + 1] = (short) (j + 1);
            indices[i2 + 2] = (short) (j + 2);
            indices[i2 + 3] = (short) (j + 2);
            indices[i2 + 4] = (short) (j + 3);
            indices[i2 + 5] = (short) (j + 0);
            i2 += 6;
            j = (short) (j + 4);
        }
        for (int i3 = 0; i3 < buffers2; i3++) {
            this.buffers[i3].setIndices(indices);
        }
        this.mesh = this.buffers[0];
        if (!Gdx.graphics.isGL20Available() || defaultShader != null) {
            this.shader = defaultShader;
            return;
        }
        this.shader = createDefaultShader();
        this.ownsShader = true;
    }

    public static ShaderProgram createDefaultShader() {
        ShaderProgram shader2 = new ShaderProgram("attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projTrans;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projTrans * a_position;\n}\n", "#ifdef GL_ES\n#define LOWP lowp\nprecision mediump float;\n#else\n#define LOWP \n#endif\nvarying LOWP vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}");
        if (shader2.isCompiled()) {
            return shader2;
        }
        throw new IllegalArgumentException("couldn't compile shader: " + shader2.getLog());
    }

    public void begin() {
        if (this.drawing) {
            throw new IllegalStateException("you have to call SpriteBatch.end() first");
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
            throw new IllegalStateException("SpriteBatch.begin must be called before end.");
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

    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else {
            if (this.idx == this.vertices.length) {
                renderMesh();
            }
        }
        float worldOriginX = x + originX;
        float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (!(scaleX == 1.0f && scaleY == 1.0f)) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        float p1x = fx;
        float p1y = fy;
        float p2x = fx;
        float p2y = fy2;
        float p3x = fx2;
        float p3y = fy2;
        float p4x = fx2;
        float p4y = fy;
        if (rotation != 0.0f) {
            float cos = MathUtils.cosDeg(rotation);
            float sin = MathUtils.sinDeg(rotation);
            x1 = (cos * p1x) - (sin * p1y);
            y1 = (sin * p1x) + (cos * p1y);
            x2 = (cos * p2x) - (sin * p2y);
            y2 = (sin * p2x) + (cos * p2y);
            x3 = (cos * p3x) - (sin * p3y);
            y3 = (sin * p3x) + (cos * p3y);
            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;
            x2 = p2x;
            y2 = p2y;
            x3 = p3x;
            y3 = p3y;
            x4 = p4x;
            y4 = p4y;
        }
        float x12 = x1 + worldOriginX;
        float y12 = y1 + worldOriginY;
        float x22 = x2 + worldOriginX;
        float y22 = y2 + worldOriginY;
        float x32 = x3 + worldOriginX;
        float y32 = y3 + worldOriginY;
        float x42 = x4 + worldOriginX;
        float y42 = y4 + worldOriginY;
        float u = ((float) srcX) * this.invTexWidth;
        float v = ((float) (srcY + srcHeight)) * this.invTexHeight;
        float u2 = ((float) (srcX + srcWidth)) * this.invTexWidth;
        float v2 = ((float) srcY) * this.invTexHeight;
        if (flipX) {
            float tmp = u;
            u = u2;
            u2 = tmp;
        }
        if (flipY) {
            float tmp2 = v;
            v = v2;
            v2 = tmp2;
        }
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x12;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y12;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x22;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = y22;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = x32;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = y32;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u2;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v2;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = x42;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y42;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u2;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v;
    }

    public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else if (this.idx == this.vertices.length) {
            renderMesh();
        }
        float u = ((float) srcX) * this.invTexWidth;
        float v = ((float) (srcY + srcHeight)) * this.invTexHeight;
        float u2 = ((float) (srcX + srcWidth)) * this.invTexWidth;
        float v2 = ((float) srcY) * this.invTexHeight;
        float fx2 = x + width;
        float fy2 = y + height;
        if (flipX) {
            float tmp = u;
            u = u2;
            u2 = tmp;
        }
        if (flipY) {
            float tmp2 = v;
            v = v2;
            v2 = tmp2;
        }
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = fy2;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = fx2;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = fy2;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u2;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v2;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = fx2;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u2;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v;
    }

    public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else if (this.idx == this.vertices.length) {
            renderMesh();
        }
        float u = ((float) srcX) * this.invTexWidth;
        float v = ((float) (srcY + srcHeight)) * this.invTexHeight;
        float u2 = ((float) (srcX + srcWidth)) * this.invTexWidth;
        float v2 = ((float) srcY) * this.invTexHeight;
        float fx2 = x + ((float) srcWidth);
        float fy2 = y + ((float) srcHeight);
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = fy2;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = fx2;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = fy2;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u2;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v2;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = fx2;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u2;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v;
    }

    public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else if (this.idx == this.vertices.length) {
            renderMesh();
        }
        float fx2 = x + width;
        float fy2 = y + height;
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = fy2;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = fx2;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = fy2;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u2;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v2;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = fx2;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u2;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v;
    }

    public void draw(Texture texture, float x, float y) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else if (this.idx == this.vertices.length) {
            renderMesh();
        }
        float fx2 = x + ((float) texture.getWidth());
        float fy2 = y + ((float) texture.getHeight());
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = 0.0f;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = 1.0f;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = fy2;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = 0.0f;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = 0.0f;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = fx2;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = fy2;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = 1.0f;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = 0.0f;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = fx2;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = 1.0f;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = 1.0f;
    }

    public void draw(Texture texture, float x, float y, float width, float height) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else if (this.idx == this.vertices.length) {
            renderMesh();
        }
        float fx2 = x + width;
        float fy2 = y + height;
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = 0.0f;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = 1.0f;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = fy2;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = 0.0f;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = 0.0f;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = fx2;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = fy2;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = 1.0f;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = 0.0f;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = fx2;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = 1.0f;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = 1.0f;
    }

    public void draw(Texture texture, float[] spriteVertices, int offset, int length) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        if (texture != this.lastTexture) {
            switchTexture(texture);
        }
        int remainingVertices = this.vertices.length - this.idx;
        if (remainingVertices == 0) {
            renderMesh();
            remainingVertices = this.vertices.length;
        }
        int vertexCount = Math.min(remainingVertices, length - offset);
        System.arraycopy(spriteVertices, offset, this.vertices, this.idx, vertexCount);
        int offset2 = offset + vertexCount;
        this.idx += vertexCount;
        while (offset2 < length) {
            renderMesh();
            int vertexCount2 = Math.min(this.vertices.length, length - offset2);
            System.arraycopy(spriteVertices, offset2, this.vertices, 0, vertexCount2);
            offset2 += vertexCount2;
            this.idx += vertexCount2;
        }
    }

    public void draw(TextureRegion region, float x, float y) {
        draw(region, x, y, (float) region.getRegionWidth(), (float) region.getRegionHeight());
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        Texture texture = region.texture;
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else if (this.idx == this.vertices.length) {
            renderMesh();
        }
        float fx2 = x + width;
        float fy2 = y + height;
        float u = region.f106u;
        float v = region.f109v2;
        float u2 = region.f107u2;
        float v2 = region.f108v;
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = fy2;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = fx2;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = fy2;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u2;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v2;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = fx2;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u2;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v;
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        Texture texture = region.texture;
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else {
            if (this.idx == this.vertices.length) {
                renderMesh();
            }
        }
        float worldOriginX = x + originX;
        float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (!(scaleX == 1.0f && scaleY == 1.0f)) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        float p1x = fx;
        float p1y = fy;
        float p2x = fx;
        float p2y = fy2;
        float p3x = fx2;
        float p3y = fy2;
        float p4x = fx2;
        float p4y = fy;
        if (rotation != 0.0f) {
            float cos = MathUtils.cosDeg(rotation);
            float sin = MathUtils.sinDeg(rotation);
            x1 = (cos * p1x) - (sin * p1y);
            y1 = (sin * p1x) + (cos * p1y);
            x2 = (cos * p2x) - (sin * p2y);
            y2 = (sin * p2x) + (cos * p2y);
            x3 = (cos * p3x) - (sin * p3y);
            y3 = (sin * p3x) + (cos * p3y);
            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;
            x2 = p2x;
            y2 = p2y;
            x3 = p3x;
            y3 = p3y;
            x4 = p4x;
            y4 = p4y;
        }
        float u = region.f106u;
        float v = region.f109v2;
        float u2 = region.f107u2;
        float v2 = region.f108v;
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x1 + worldOriginX;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y1 + worldOriginY;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x2 + worldOriginX;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = y2 + worldOriginY;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = x3 + worldOriginX;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = y3 + worldOriginY;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u2;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v2;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = x4 + worldOriginX;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y4 + worldOriginY;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u2;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v;
    }

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
        float u1;
        float v1;
        float u2;
        float v2;
        float u3;
        float v3;
        float u4;
        float v4;
        if (!this.drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }
        Texture texture = region.texture;
        if (texture != this.lastTexture) {
            switchTexture(texture);
        } else {
            if (this.idx == this.vertices.length) {
                renderMesh();
            }
        }
        float worldOriginX = x + originX;
        float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;
        if (!(scaleX == 1.0f && scaleY == 1.0f)) {
            fx *= scaleX;
            fy *= scaleY;
            fx2 *= scaleX;
            fy2 *= scaleY;
        }
        float p1x = fx;
        float p1y = fy;
        float p2x = fx;
        float p2y = fy2;
        float p3x = fx2;
        float p3y = fy2;
        float p4x = fx2;
        float p4y = fy;
        if (rotation != 0.0f) {
            float cos = MathUtils.cosDeg(rotation);
            float sin = MathUtils.sinDeg(rotation);
            x1 = (cos * p1x) - (sin * p1y);
            y1 = (sin * p1x) + (cos * p1y);
            x2 = (cos * p2x) - (sin * p2y);
            y2 = (sin * p2x) + (cos * p2y);
            x3 = (cos * p3x) - (sin * p3y);
            y3 = (sin * p3x) + (cos * p3y);
            x4 = x1 + (x3 - x2);
            y4 = y3 - (y2 - y1);
        } else {
            x1 = p1x;
            y1 = p1y;
            x2 = p2x;
            y2 = p2y;
            x3 = p3x;
            y3 = p3y;
            x4 = p4x;
            y4 = p4y;
        }
        float x12 = x1 + worldOriginX;
        float y12 = y1 + worldOriginY;
        float x22 = x2 + worldOriginX;
        float y22 = y2 + worldOriginY;
        float x32 = x3 + worldOriginX;
        float y32 = y3 + worldOriginY;
        float x42 = x4 + worldOriginX;
        float y42 = y4 + worldOriginY;
        if (clockwise) {
            u1 = region.f107u2;
            v1 = region.f109v2;
            u2 = region.f106u;
            v2 = region.f109v2;
            u3 = region.f106u;
            v3 = region.f108v;
            u4 = region.f107u2;
            v4 = region.f108v;
        } else {
            u1 = region.f106u;
            v1 = region.f108v;
            u2 = region.f107u2;
            v2 = region.f108v;
            u3 = region.f107u2;
            v3 = region.f109v2;
            u4 = region.f106u;
            v4 = region.f109v2;
        }
        float[] fArr = this.vertices;
        int i = this.idx;
        this.idx = i + 1;
        fArr[i] = x12;
        float[] fArr2 = this.vertices;
        int i2 = this.idx;
        this.idx = i2 + 1;
        fArr2[i2] = y12;
        float[] fArr3 = this.vertices;
        int i3 = this.idx;
        this.idx = i3 + 1;
        fArr3[i3] = this.color;
        float[] fArr4 = this.vertices;
        int i4 = this.idx;
        this.idx = i4 + 1;
        fArr4[i4] = u1;
        float[] fArr5 = this.vertices;
        int i5 = this.idx;
        this.idx = i5 + 1;
        fArr5[i5] = v1;
        float[] fArr6 = this.vertices;
        int i6 = this.idx;
        this.idx = i6 + 1;
        fArr6[i6] = x22;
        float[] fArr7 = this.vertices;
        int i7 = this.idx;
        this.idx = i7 + 1;
        fArr7[i7] = y22;
        float[] fArr8 = this.vertices;
        int i8 = this.idx;
        this.idx = i8 + 1;
        fArr8[i8] = this.color;
        float[] fArr9 = this.vertices;
        int i9 = this.idx;
        this.idx = i9 + 1;
        fArr9[i9] = u2;
        float[] fArr10 = this.vertices;
        int i10 = this.idx;
        this.idx = i10 + 1;
        fArr10[i10] = v2;
        float[] fArr11 = this.vertices;
        int i11 = this.idx;
        this.idx = i11 + 1;
        fArr11[i11] = x32;
        float[] fArr12 = this.vertices;
        int i12 = this.idx;
        this.idx = i12 + 1;
        fArr12[i12] = y32;
        float[] fArr13 = this.vertices;
        int i13 = this.idx;
        this.idx = i13 + 1;
        fArr13[i13] = this.color;
        float[] fArr14 = this.vertices;
        int i14 = this.idx;
        this.idx = i14 + 1;
        fArr14[i14] = u3;
        float[] fArr15 = this.vertices;
        int i15 = this.idx;
        this.idx = i15 + 1;
        fArr15[i15] = v3;
        float[] fArr16 = this.vertices;
        int i16 = this.idx;
        this.idx = i16 + 1;
        fArr16[i16] = x42;
        float[] fArr17 = this.vertices;
        int i17 = this.idx;
        this.idx = i17 + 1;
        fArr17[i17] = y42;
        float[] fArr18 = this.vertices;
        int i18 = this.idx;
        this.idx = i18 + 1;
        fArr18[i18] = this.color;
        float[] fArr19 = this.vertices;
        int i19 = this.idx;
        this.idx = i19 + 1;
        fArr19[i19] = u4;
        float[] fArr20 = this.vertices;
        int i20 = this.idx;
        this.idx = i20 + 1;
        fArr20[i20] = v4;
    }

    public void flush() {
        renderMesh();
    }

    private void renderMesh() {
        if (this.idx != 0) {
            this.renderCalls++;
            this.totalRenderCalls++;
            int spritesInBatch = this.idx / 20;
            if (spritesInBatch > this.maxSpritesInBatch) {
                this.maxSpritesInBatch = spritesInBatch;
            }
            this.lastTexture.bind();
            this.mesh.setVertices(this.vertices, 0, this.idx);
            this.mesh.getIndicesBuffer().position(0);
            this.mesh.getIndicesBuffer().limit(spritesInBatch * 6);
            if (this.blendingDisabled) {
                Gdx.f12gl.glDisable(3042);
            } else {
                Gdx.f12gl.glEnable(3042);
                if (this.blendSrcFunc != -1) {
                    Gdx.f12gl.glBlendFunc(this.blendSrcFunc, this.blendDstFunc);
                }
            }
            if (!Gdx.graphics.isGL20Available()) {
                this.mesh.render(4, 0, spritesInBatch * 6);
            } else if (this.customShader != null) {
                this.mesh.render(this.customShader, 4, 0, spritesInBatch * 6);
            } else {
                this.mesh.render(this.shader, 4, 0, spritesInBatch * 6);
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
        if (!this.blendingDisabled) {
            renderMesh();
            this.blendingDisabled = true;
        }
    }

    public void enableBlending() {
        if (this.blendingDisabled) {
            renderMesh();
            this.blendingDisabled = false;
        }
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
            this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
            this.customShader.setUniformi("u_texture", 0);
            return;
        }
        this.shader.setUniformMatrix("u_projTrans", this.combinedMatrix);
        this.shader.setUniformi("u_texture", 0);
    }

    private void switchTexture(Texture texture) {
        renderMesh();
        this.lastTexture = texture;
        this.invTexWidth = 1.0f / ((float) texture.getWidth());
        this.invTexHeight = 1.0f / ((float) texture.getHeight());
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
