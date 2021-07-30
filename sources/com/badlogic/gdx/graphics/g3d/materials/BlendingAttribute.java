package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;

public class BlendingAttribute extends MaterialAttribute {
    private static final Pool<BlendingAttribute> pool = new Pool<BlendingAttribute>() {
        /* access modifiers changed from: protected */
        public BlendingAttribute newObject() {
            return new BlendingAttribute();
        }
    };
    public static final String translucent = "translucent";
    public int blendDstFunc;
    public int blendSrcFunc;

    protected BlendingAttribute() {
    }

    public BlendingAttribute(String name) {
        this(name, 770, 771);
    }

    public BlendingAttribute(String name, int srcFunc, int dstFunc) {
        super(name);
        this.blendSrcFunc = srcFunc;
        this.blendDstFunc = dstFunc;
    }

    public void bind() {
        Gdx.f12gl.glBlendFunc(this.blendSrcFunc, this.blendDstFunc);
    }

    public void bind(ShaderProgram program) {
        Gdx.f12gl.glBlendFunc(this.blendSrcFunc, this.blendDstFunc);
    }

    public MaterialAttribute copy() {
        return new BlendingAttribute(this.name, this.blendSrcFunc, this.blendDstFunc);
    }

    public void set(MaterialAttribute attr) {
        BlendingAttribute blendAttr = (BlendingAttribute) attr;
        this.name = blendAttr.name;
        this.blendDstFunc = blendAttr.blendDstFunc;
        this.blendSrcFunc = blendAttr.blendSrcFunc;
    }

    public MaterialAttribute pooledCopy() {
        BlendingAttribute attr = pool.obtain();
        attr.set(this);
        return attr;
    }

    public void free() {
        if (this.isPooled) {
            pool.free(this);
        }
    }
}
