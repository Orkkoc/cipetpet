package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class SpriteCache implements Disposable {
    private static final float[] tempVertices = new float[30];
    private ArrayList<Cache> caches;
    private float color;
    private final Matrix4 combinedMatrix;
    private final ArrayList<Integer> counts;
    private Cache currentCache;
    private ShaderProgram customShader;
    private boolean drawing;
    private final Mesh mesh;
    private final Matrix4 projectionMatrix;
    private final ShaderProgram shader;
    private Color tempColor;
    private final ArrayList<Texture> textures;
    private final Matrix4 transformMatrix;

    public SpriteCache() {
        this(1000, false);
    }

    public SpriteCache(int size, boolean useIndices) {
        this(size, createDefaultShader(), useIndices);
    }

    public SpriteCache(int size, ShaderProgram shader2, boolean useIndices) {
        int i;
        this.transformMatrix = new Matrix4();
        this.projectionMatrix = new Matrix4();
        this.caches = new ArrayList<>();
        this.combinedMatrix = new Matrix4();
        this.textures = new ArrayList<>(8);
        this.counts = new ArrayList<>(8);
        this.color = Color.WHITE.toFloatBits();
        this.tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.customShader = null;
        this.shader = shader2;
        int i2 = size * (useIndices ? 4 : 6);
        if (useIndices) {
            i = size * 6;
        } else {
            i = 0;
        }
        this.mesh = new Mesh(true, i2, i, new VertexAttribute(0, 2, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(5, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(3, 2, "a_texCoord0"));
        this.mesh.setAutoBind(false);
        if (useIndices) {
            int length = size * 6;
            short[] indices = new short[length];
            short j = 0;
            int i3 = 0;
            while (i3 < length) {
                indices[i3 + 0] = j;
                indices[i3 + 1] = (short) (j + 1);
                indices[i3 + 2] = (short) (j + 2);
                indices[i3 + 3] = (short) (j + 2);
                indices[i3 + 4] = (short) (j + 3);
                indices[i3 + 5] = j;
                i3 += 6;
                j = (short) (j + 4);
            }
            this.mesh.setIndices(indices);
        }
        this.projectionMatrix.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
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

    public void beginCache() {
        if (this.currentCache != null) {
            throw new IllegalStateException("endCache must be called before begin.");
        }
        if (this.mesh.getNumIndices() > 0) {
        }
        this.currentCache = new Cache(this.caches.size(), this.mesh.getVerticesBuffer().limit());
        this.caches.add(this.currentCache);
        this.mesh.getVerticesBuffer().compact();
    }

    public void beginCache(int cacheID) {
        if (this.currentCache != null) {
            throw new IllegalStateException("endCache must be called before begin.");
        } else if (cacheID == this.caches.size() - 1) {
            this.mesh.getVerticesBuffer().limit(this.caches.remove(cacheID).offset);
            beginCache();
        } else {
            this.currentCache = this.caches.get(cacheID);
            this.mesh.getVerticesBuffer().position(this.currentCache.offset);
        }
    }

    public int endCache() {
        if (this.currentCache == null) {
            throw new IllegalStateException("beginCache must be called before endCache.");
        }
        Cache cache = this.currentCache;
        int cacheCount = this.mesh.getVerticesBuffer().position() - cache.offset;
        if (cache.textures == null) {
            cache.maxCount = cacheCount;
            cache.textureCount = this.textures.size();
            cache.textures = (Texture[]) this.textures.toArray(new Texture[cache.textureCount]);
            cache.counts = new int[cache.textureCount];
            int n = this.counts.size();
            for (int i = 0; i < n; i++) {
                cache.counts[i] = this.counts.get(i).intValue();
            }
            this.mesh.getVerticesBuffer().flip();
        } else if (cacheCount > cache.maxCount) {
            throw new GdxRuntimeException("If a cache is not the last created, it cannot be redefined with more entries than when it was first created: " + cacheCount + " (" + cache.maxCount + " max)");
        } else {
            cache.textureCount = this.textures.size();
            if (cache.textures.length < cache.textureCount) {
                cache.textures = new Texture[cache.textureCount];
            }
            int n2 = cache.textureCount;
            for (int i2 = 0; i2 < n2; i2++) {
                cache.textures[i2] = this.textures.get(i2);
            }
            if (cache.counts.length < cache.textureCount) {
                cache.counts = new int[cache.textureCount];
            }
            int n3 = cache.textureCount;
            for (int i3 = 0; i3 < n3; i3++) {
                cache.counts[i3] = this.counts.get(i3).intValue();
            }
            FloatBuffer vertices = this.mesh.getVerticesBuffer();
            vertices.position(0);
            Cache lastCache = this.caches.get(this.caches.size() - 1);
            vertices.limit(lastCache.offset + lastCache.maxCount);
        }
        this.currentCache = null;
        this.textures.clear();
        this.counts.clear();
        return cache.f105id;
    }

    public void clear() {
        this.caches.clear();
        this.mesh.getVerticesBuffer().clear().flip();
    }

    public void add(Texture texture, float[] vertices, int offset, int length) {
        if (this.currentCache == null) {
            throw new IllegalStateException("beginCache must be called before add.");
        }
        int count = (length / ((this.mesh.getNumIndices() > 0 ? 4 : 6) * 5)) * 6;
        int lastIndex = this.textures.size() - 1;
        if (lastIndex < 0 || this.textures.get(lastIndex) != texture) {
            this.textures.add(texture);
            this.counts.add(Integer.valueOf(count));
        } else {
            this.counts.set(lastIndex, Integer.valueOf(this.counts.get(lastIndex).intValue() + count));
        }
        this.mesh.getVerticesBuffer().put(vertices, offset, length);
    }

    public void add(Texture texture, float x, float y) {
        float fx2 = x + ((float) texture.getWidth());
        float fy2 = y + ((float) texture.getHeight());
        tempVertices[0] = x;
        tempVertices[1] = y;
        tempVertices[2] = this.color;
        tempVertices[3] = 0.0f;
        tempVertices[4] = 1.0f;
        tempVertices[5] = x;
        tempVertices[6] = fy2;
        tempVertices[7] = this.color;
        tempVertices[8] = 0.0f;
        tempVertices[9] = 0.0f;
        tempVertices[10] = fx2;
        tempVertices[11] = fy2;
        tempVertices[12] = this.color;
        tempVertices[13] = 1.0f;
        tempVertices[14] = 0.0f;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = fx2;
            tempVertices[16] = y;
            tempVertices[17] = this.color;
            tempVertices[18] = 1.0f;
            tempVertices[19] = 1.0f;
            add(texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = fx2;
        tempVertices[16] = fy2;
        tempVertices[17] = this.color;
        tempVertices[18] = 1.0f;
        tempVertices[19] = 0.0f;
        tempVertices[20] = fx2;
        tempVertices[21] = y;
        tempVertices[22] = this.color;
        tempVertices[23] = 1.0f;
        tempVertices[24] = 1.0f;
        tempVertices[25] = x;
        tempVertices[26] = y;
        tempVertices[27] = this.color;
        tempVertices[28] = 0.0f;
        tempVertices[29] = 1.0f;
        add(texture, tempVertices, 0, 30);
    }

    public void add(Texture texture, float x, float y, int srcWidth, int srcHeight, float u, float v, float u2, float v2, float color2) {
        float fx2 = x + ((float) srcWidth);
        float fy2 = y + ((float) srcHeight);
        tempVertices[0] = x;
        tempVertices[1] = y;
        tempVertices[2] = color2;
        tempVertices[3] = u;
        tempVertices[4] = v;
        tempVertices[5] = x;
        tempVertices[6] = fy2;
        tempVertices[7] = color2;
        tempVertices[8] = u;
        tempVertices[9] = v2;
        tempVertices[10] = fx2;
        tempVertices[11] = fy2;
        tempVertices[12] = color2;
        tempVertices[13] = u2;
        tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = fx2;
            tempVertices[16] = y;
            tempVertices[17] = color2;
            tempVertices[18] = u2;
            tempVertices[19] = v;
            add(texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = fx2;
        tempVertices[16] = fy2;
        tempVertices[17] = color2;
        tempVertices[18] = u2;
        tempVertices[19] = v2;
        tempVertices[20] = fx2;
        tempVertices[21] = y;
        tempVertices[22] = color2;
        tempVertices[23] = u2;
        tempVertices[24] = v;
        tempVertices[25] = x;
        tempVertices[26] = y;
        tempVertices[27] = color2;
        tempVertices[28] = u;
        tempVertices[29] = v;
        add(texture, tempVertices, 0, 30);
    }

    public void add(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        float invTexWidth = 1.0f / ((float) texture.getWidth());
        float invTexHeight = 1.0f / ((float) texture.getHeight());
        float u = ((float) srcX) * invTexWidth;
        float v = ((float) (srcY + srcHeight)) * invTexHeight;
        float u2 = ((float) (srcX + srcWidth)) * invTexWidth;
        float v2 = ((float) srcY) * invTexHeight;
        float fx2 = x + ((float) srcWidth);
        float fy2 = y + ((float) srcHeight);
        tempVertices[0] = x;
        tempVertices[1] = y;
        tempVertices[2] = this.color;
        tempVertices[3] = u;
        tempVertices[4] = v;
        tempVertices[5] = x;
        tempVertices[6] = fy2;
        tempVertices[7] = this.color;
        tempVertices[8] = u;
        tempVertices[9] = v2;
        tempVertices[10] = fx2;
        tempVertices[11] = fy2;
        tempVertices[12] = this.color;
        tempVertices[13] = u2;
        tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = fx2;
            tempVertices[16] = y;
            tempVertices[17] = this.color;
            tempVertices[18] = u2;
            tempVertices[19] = v;
            add(texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = fx2;
        tempVertices[16] = fy2;
        tempVertices[17] = this.color;
        tempVertices[18] = u2;
        tempVertices[19] = v2;
        tempVertices[20] = fx2;
        tempVertices[21] = y;
        tempVertices[22] = this.color;
        tempVertices[23] = u2;
        tempVertices[24] = v;
        tempVertices[25] = x;
        tempVertices[26] = y;
        tempVertices[27] = this.color;
        tempVertices[28] = u;
        tempVertices[29] = v;
        add(texture, tempVertices, 0, 30);
    }

    public void add(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        float invTexWidth = 1.0f / ((float) texture.getWidth());
        float invTexHeight = 1.0f / ((float) texture.getHeight());
        float u = ((float) srcX) * invTexWidth;
        float v = ((float) (srcY + srcHeight)) * invTexHeight;
        float u2 = ((float) (srcX + srcWidth)) * invTexWidth;
        float v2 = ((float) srcY) * invTexHeight;
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
        tempVertices[0] = x;
        tempVertices[1] = y;
        tempVertices[2] = this.color;
        tempVertices[3] = u;
        tempVertices[4] = v;
        tempVertices[5] = x;
        tempVertices[6] = fy2;
        tempVertices[7] = this.color;
        tempVertices[8] = u;
        tempVertices[9] = v2;
        tempVertices[10] = fx2;
        tempVertices[11] = fy2;
        tempVertices[12] = this.color;
        tempVertices[13] = u2;
        tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = fx2;
            tempVertices[16] = y;
            tempVertices[17] = this.color;
            tempVertices[18] = u2;
            tempVertices[19] = v;
            add(texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = fx2;
        tempVertices[16] = fy2;
        tempVertices[17] = this.color;
        tempVertices[18] = u2;
        tempVertices[19] = v2;
        tempVertices[20] = fx2;
        tempVertices[21] = y;
        tempVertices[22] = this.color;
        tempVertices[23] = u2;
        tempVertices[24] = v;
        tempVertices[25] = x;
        tempVertices[26] = y;
        tempVertices[27] = this.color;
        tempVertices[28] = u;
        tempVertices[29] = v;
        add(texture, tempVertices, 0, 30);
    }

    public void add(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
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
        float invTexWidth = 1.0f / ((float) texture.getWidth());
        float invTexHeight = 1.0f / ((float) texture.getHeight());
        float u = ((float) srcX) * invTexWidth;
        float v = ((float) (srcY + srcHeight)) * invTexHeight;
        float u2 = ((float) (srcX + srcWidth)) * invTexWidth;
        float v2 = ((float) srcY) * invTexHeight;
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
        tempVertices[0] = x12;
        tempVertices[1] = y12;
        tempVertices[2] = this.color;
        tempVertices[3] = u;
        tempVertices[4] = v;
        tempVertices[5] = x22;
        tempVertices[6] = y22;
        tempVertices[7] = this.color;
        tempVertices[8] = u;
        tempVertices[9] = v2;
        tempVertices[10] = x32;
        tempVertices[11] = y32;
        tempVertices[12] = this.color;
        tempVertices[13] = u2;
        tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = x42;
            tempVertices[16] = y42;
            tempVertices[17] = this.color;
            tempVertices[18] = u2;
            tempVertices[19] = v;
            add(texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = x32;
        tempVertices[16] = y32;
        tempVertices[17] = this.color;
        tempVertices[18] = u2;
        tempVertices[19] = v2;
        tempVertices[20] = x42;
        tempVertices[21] = y42;
        tempVertices[22] = this.color;
        tempVertices[23] = u2;
        tempVertices[24] = v;
        tempVertices[25] = x12;
        tempVertices[26] = y12;
        tempVertices[27] = this.color;
        tempVertices[28] = u;
        tempVertices[29] = v;
        add(texture, tempVertices, 0, 30);
    }

    public void add(TextureRegion region, float x, float y) {
        add(region, x, y, (float) region.getRegionWidth(), (float) region.getRegionHeight());
    }

    public void add(TextureRegion region, float x, float y, float width, float height) {
        float fx2 = x + width;
        float fy2 = y + height;
        float u = region.f106u;
        float v = region.f109v2;
        float u2 = region.f107u2;
        float v2 = region.f108v;
        tempVertices[0] = x;
        tempVertices[1] = y;
        tempVertices[2] = this.color;
        tempVertices[3] = u;
        tempVertices[4] = v;
        tempVertices[5] = x;
        tempVertices[6] = fy2;
        tempVertices[7] = this.color;
        tempVertices[8] = u;
        tempVertices[9] = v2;
        tempVertices[10] = fx2;
        tempVertices[11] = fy2;
        tempVertices[12] = this.color;
        tempVertices[13] = u2;
        tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = fx2;
            tempVertices[16] = y;
            tempVertices[17] = this.color;
            tempVertices[18] = u2;
            tempVertices[19] = v;
            add(region.texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = fx2;
        tempVertices[16] = fy2;
        tempVertices[17] = this.color;
        tempVertices[18] = u2;
        tempVertices[19] = v2;
        tempVertices[20] = fx2;
        tempVertices[21] = y;
        tempVertices[22] = this.color;
        tempVertices[23] = u2;
        tempVertices[24] = v;
        tempVertices[25] = x;
        tempVertices[26] = y;
        tempVertices[27] = this.color;
        tempVertices[28] = u;
        tempVertices[29] = v;
        add(region.texture, tempVertices, 0, 30);
    }

    public void add(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;
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
        float x32 = x3 + worldOriginX;
        float y32 = y3 + worldOriginY;
        float x42 = x4 + worldOriginX;
        float y42 = y4 + worldOriginY;
        float u = region.f106u;
        float v = region.f109v2;
        float u2 = region.f107u2;
        float v2 = region.f108v;
        tempVertices[0] = x12;
        tempVertices[1] = y12;
        tempVertices[2] = this.color;
        tempVertices[3] = u;
        tempVertices[4] = v;
        tempVertices[5] = x2 + worldOriginX;
        tempVertices[6] = y2 + worldOriginY;
        tempVertices[7] = this.color;
        tempVertices[8] = u;
        tempVertices[9] = v2;
        tempVertices[10] = x32;
        tempVertices[11] = y32;
        tempVertices[12] = this.color;
        tempVertices[13] = u2;
        tempVertices[14] = v2;
        if (this.mesh.getNumIndices() > 0) {
            tempVertices[15] = x42;
            tempVertices[16] = y42;
            tempVertices[17] = this.color;
            tempVertices[18] = u2;
            tempVertices[19] = v;
            add(region.texture, tempVertices, 0, 20);
            return;
        }
        tempVertices[15] = x32;
        tempVertices[16] = y32;
        tempVertices[17] = this.color;
        tempVertices[18] = u2;
        tempVertices[19] = v2;
        tempVertices[20] = x42;
        tempVertices[21] = y42;
        tempVertices[22] = this.color;
        tempVertices[23] = u2;
        tempVertices[24] = v;
        tempVertices[25] = x12;
        tempVertices[26] = y12;
        tempVertices[27] = this.color;
        tempVertices[28] = u;
        tempVertices[29] = v;
        add(region.texture, tempVertices, 0, 30);
    }

    public void add(Sprite sprite) {
        if (this.mesh.getNumIndices() > 0) {
            add(sprite.getTexture(), sprite.getVertices(), 0, 20);
            return;
        }
        float[] spriteVertices = sprite.getVertices();
        System.arraycopy(spriteVertices, 0, tempVertices, 0, 15);
        System.arraycopy(spriteVertices, 10, tempVertices, 15, 5);
        System.arraycopy(spriteVertices, 15, tempVertices, 20, 5);
        System.arraycopy(spriteVertices, 0, tempVertices, 25, 5);
        add(sprite.getTexture(), tempVertices, 0, 30);
    }

    public void begin() {
        if (this.drawing) {
            throw new IllegalStateException("end must be called before begin.");
        }
        if (!Gdx.graphics.isGL20Available()) {
            GL10 gl = Gdx.gl10;
            gl.glDepthMask(false);
            gl.glEnable(3553);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadMatrixf(this.projectionMatrix.val, 0);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadMatrixf(this.transformMatrix.val, 0);
            this.mesh.bind();
        } else {
            this.combinedMatrix.set(this.projectionMatrix).mul(this.transformMatrix);
            Gdx.gl20.glDepthMask(false);
            if (this.customShader != null) {
                this.customShader.begin();
                this.customShader.setUniformMatrix("u_proj", this.projectionMatrix);
                this.customShader.setUniformMatrix("u_trans", this.transformMatrix);
                this.customShader.setUniformMatrix("u_projTrans", this.combinedMatrix);
                this.customShader.setUniformi("u_texture", 0);
            } else {
                this.shader.begin();
                this.shader.setUniformMatrix("u_projectionViewMatrix", this.combinedMatrix);
                this.shader.setUniformi("u_texture", 0);
            }
            this.mesh.bind(this.shader);
        }
        this.drawing = true;
    }

    public void end() {
        if (!this.drawing) {
            throw new IllegalStateException("begin must be called before end.");
        }
        this.drawing = false;
        if (!Gdx.graphics.isGL20Available()) {
            GL10 gl = Gdx.gl10;
            gl.glDepthMask(true);
            gl.glDisable(3553);
            this.mesh.unbind();
            return;
        }
        this.shader.end();
        Gdx.gl20.glDepthMask(true);
        this.mesh.unbind(this.shader);
    }

    public void draw(int cacheID) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteCache.begin must be called before draw.");
        }
        Cache cache = this.caches.get(cacheID);
        int offset = (cache.offset / ((this.mesh.getNumIndices() > 0 ? 4 : 6) * 5)) * 6;
        Texture[] textures2 = cache.textures;
        int[] counts2 = cache.counts;
        if (Gdx.graphics.isGL20Available()) {
            int n = cache.textureCount;
            for (int i = 0; i < n; i++) {
                int count = counts2[i];
                textures2[i].bind();
                if (this.customShader != null) {
                    this.mesh.render(this.customShader, 4, offset, count);
                } else {
                    this.mesh.render(this.shader, 4, offset, count);
                }
                offset += count;
            }
            return;
        }
        int n2 = cache.textureCount;
        for (int i2 = 0; i2 < n2; i2++) {
            int count2 = counts2[i2];
            textures2[i2].bind();
            this.mesh.render(4, offset, count2);
            offset += count2;
        }
    }

    public void draw(int cacheID, int offset, int length) {
        if (!this.drawing) {
            throw new IllegalStateException("SpriteCache.begin must be called before draw.");
        }
        Cache cache = this.caches.get(cacheID);
        int offset2 = (offset * 6) + cache.offset;
        int length2 = length * 6;
        Texture[] textures2 = cache.textures;
        int[] counts2 = cache.counts;
        if (Gdx.graphics.isGL20Available()) {
            int i = 0;
            int n = cache.textureCount;
            while (i < n) {
                textures2[i].bind();
                int count = counts2[i];
                if (count > length2) {
                    i = n;
                    count = length2;
                } else {
                    length2 -= count;
                }
                if (this.customShader != null) {
                    this.mesh.render(this.customShader, 4, offset2, count);
                } else {
                    this.mesh.render(this.shader, 4, offset2, count);
                }
                offset2 += count;
                i++;
            }
            return;
        }
        int i2 = 0;
        int n2 = cache.textureCount;
        while (i2 < n2) {
            textures2[i2].bind();
            int count2 = counts2[i2];
            if (count2 > length2) {
                i2 = n2;
                count2 = length2;
            } else {
                length2 -= count2;
            }
            this.mesh.render(4, offset2, count2);
            offset2 += count2;
            i2++;
        }
    }

    public void dispose() {
        this.mesh.dispose();
        if (this.shader != null) {
            this.shader.dispose();
        }
    }

    public Matrix4 getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4 projection) {
        if (this.drawing) {
            throw new IllegalStateException("Can't set the matrix within begin/end.");
        }
        this.projectionMatrix.set(projection);
    }

    public Matrix4 getTransformMatrix() {
        return this.transformMatrix;
    }

    public void setTransformMatrix(Matrix4 transform) {
        if (this.drawing) {
            throw new IllegalStateException("Can't set the matrix within begin/end.");
        }
        this.transformMatrix.set(transform);
    }

    private static class Cache {
        int[] counts;

        /* renamed from: id */
        final int f105id;
        int maxCount;
        final int offset;
        int textureCount;
        Texture[] textures;

        public Cache(int id, int offset2) {
            this.f105id = id;
            this.offset = offset2;
        }
    }

    static ShaderProgram createDefaultShader() {
        if (!Gdx.graphics.isGL20Available()) {
            return null;
        }
        ShaderProgram shader2 = new ShaderProgram("attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n", "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}");
        if (shader2.isCompiled()) {
            return shader2;
        }
        throw new IllegalArgumentException("Error compiling shader: " + shader2.getLog());
    }

    public void setShader(ShaderProgram shader2) {
        this.customShader = shader2;
    }
}
