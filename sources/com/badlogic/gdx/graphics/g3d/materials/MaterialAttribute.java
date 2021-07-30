package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class MaterialAttribute {
    private static final String FLAG = "Flag";
    protected final boolean isPooled = true;
    public String name;

    public abstract void bind();

    public abstract void bind(ShaderProgram shaderProgram);

    public abstract MaterialAttribute copy();

    public abstract void free();

    public abstract MaterialAttribute pooledCopy();

    public abstract void set(MaterialAttribute materialAttribute);

    protected MaterialAttribute() {
    }

    public MaterialAttribute(String name2) {
        this.name = name2;
    }

    public String getShaderFlag() {
        return this.name + FLAG;
    }
}
