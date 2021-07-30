package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;

public class TextureAttribute extends MaterialAttribute {
    public static final int MAX_TEXTURE_UNITS = 16;
    public static final String diffuseTexture = "diffuseTexture";
    public static final String lightmapTexture = "lightmapTexture";
    private static final Pool<TextureAttribute> pool = new Pool<TextureAttribute>() {
        /* access modifiers changed from: protected */
        public TextureAttribute newObject() {
            return new TextureAttribute();
        }
    };
    public static final String specularTexture = "specularTexture";
    public int magFilter;
    public int minFilter;
    public Texture texture;
    public int uWrap;
    public int unit;
    public int vWrap;

    protected TextureAttribute() {
    }

    public TextureAttribute(Texture texture2, int unit2, String name, Texture.TextureFilter minFilter2, Texture.TextureFilter magFilter2, Texture.TextureWrap uWrap2, Texture.TextureWrap vWrap2) {
        this(texture2, unit2, name, minFilter2.getGLEnum(), magFilter2.getGLEnum(), uWrap2.getGLEnum(), vWrap2.getGLEnum());
    }

    public TextureAttribute(Texture texture2, int unit2, String name, int minFilter2, int magFilter2, int uWrap2, int vWrap2) {
        super(name);
        this.texture = texture2;
        if (unit2 > 16) {
            throw new RuntimeException("16 is max texture units supported");
        }
        this.unit = unit2;
        this.uWrap = uWrap2;
        this.vWrap = vWrap2;
        this.minFilter = minFilter2;
        this.magFilter = magFilter2;
    }

    public TextureAttribute(Texture texture2, int unit2, String name) {
        this(texture2, unit2, name, texture2.getMinFilter(), texture2.getMagFilter(), texture2.getUWrap(), texture2.getVWrap());
    }

    public void bind() {
        this.texture.bind(this.unit);
        Gdx.f12gl.glTexParameterf(3553, 10241, (float) this.minFilter);
        Gdx.f12gl.glTexParameterf(3553, 10240, (float) this.magFilter);
        Gdx.f12gl.glTexParameterf(3553, 10242, (float) this.uWrap);
        Gdx.f12gl.glTexParameterf(3553, 10243, (float) this.vWrap);
    }

    public void bind(ShaderProgram program) {
        this.texture.bind(this.unit);
        Gdx.f12gl.glTexParameterf(3553, 10241, (float) this.minFilter);
        Gdx.f12gl.glTexParameterf(3553, 10240, (float) this.magFilter);
        Gdx.f12gl.glTexParameterf(3553, 10242, (float) this.uWrap);
        Gdx.f12gl.glTexParameterf(3553, 10243, (float) this.vWrap);
        program.setUniformi(this.name, this.unit);
    }

    public MaterialAttribute copy() {
        return new TextureAttribute(this.texture, this.unit, this.name, this.minFilter, this.magFilter, this.uWrap, this.vWrap);
    }

    public void set(MaterialAttribute attr) {
        TextureAttribute texAttr = (TextureAttribute) attr;
        this.name = texAttr.name;
        this.texture = texAttr.texture;
        this.unit = texAttr.unit;
        this.magFilter = texAttr.magFilter;
        this.minFilter = texAttr.minFilter;
        this.uWrap = texAttr.uWrap;
        this.vWrap = texAttr.vWrap;
    }

    public boolean texturePortionEquals(TextureAttribute other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (this.texture == other.texture && this.unit == other.unit && this.minFilter == other.minFilter && this.magFilter == other.magFilter && this.uWrap == other.uWrap && this.vWrap == other.vWrap) {
            return true;
        }
        return false;
    }

    public MaterialAttribute pooledCopy() {
        TextureAttribute attr = pool.obtain();
        attr.set(this);
        return attr;
    }

    public void free() {
        if (this.isPooled) {
            pool.free(this);
        }
    }
}
