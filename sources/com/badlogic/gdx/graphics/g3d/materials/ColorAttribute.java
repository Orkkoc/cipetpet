package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;

public class ColorAttribute extends MaterialAttribute {
    public static final String diffuse = "diffuseColor";
    public static final String emissive = "emissiveColor";
    public static final String fog = "fogColor";
    private static final Pool<ColorAttribute> pool = new Pool<ColorAttribute>() {
        /* access modifiers changed from: protected */
        public ColorAttribute newObject() {
            return new ColorAttribute();
        }
    };
    public static final String rim = "rimColor";
    public static final String specular = "specularColor";
    public final Color color = new Color();

    protected ColorAttribute() {
    }

    public ColorAttribute(Color color2, String name) {
        super(name);
        this.color.set(color2);
    }

    public void bind() {
        if (Gdx.gl10 == null) {
            throw new RuntimeException("Can't call ColorAttribute.bind() in a GL20 context");
        } else if (diffuse.equals(diffuse)) {
            Gdx.gl10.glColor4f(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        }
    }

    public void bind(ShaderProgram program) {
        program.setUniformf(this.name, this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
    }

    public MaterialAttribute copy() {
        return new ColorAttribute(this.color, this.name);
    }

    public void set(MaterialAttribute attr) {
        ColorAttribute colAttr = (ColorAttribute) attr;
        this.name = colAttr.name;
        Color c = colAttr.color;
        this.color.f70r = c.f70r;
        this.color.f69g = c.f69g;
        this.color.f68b = c.f68b;
        this.color.f67a = c.f67a;
    }

    public MaterialAttribute pooledCopy() {
        ColorAttribute attr = pool.obtain();
        attr.set(this);
        return attr;
    }

    public void free() {
        if (this.isPooled) {
            pool.free(this);
        }
    }
}
