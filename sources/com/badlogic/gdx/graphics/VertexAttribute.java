package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class VertexAttribute {
    public String alias;
    public final int numComponents;
    public int offset;
    public final int usage;

    public VertexAttribute(int usage2, int numComponents2, String alias2) {
        this.usage = usage2;
        this.numComponents = numComponents2;
        this.alias = alias2;
    }

    public static VertexAttribute Position() {
        return new VertexAttribute(0, 3, ShaderProgram.POSITION_ATTRIBUTE);
    }

    public static VertexAttribute TexCoords(int unit) {
        return new VertexAttribute(3, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + unit);
    }

    public static VertexAttribute Normal() {
        return new VertexAttribute(2, 3, ShaderProgram.NORMAL_ATTRIBUTE);
    }

    public static VertexAttribute Color() {
        return new VertexAttribute(5, 4, ShaderProgram.COLOR_ATTRIBUTE);
    }

    public static VertexAttribute ColorUnpacked() {
        return new VertexAttribute(1, 4, ShaderProgram.COLOR_ATTRIBUTE);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VertexAttribute)) {
            return false;
        }
        VertexAttribute other = (VertexAttribute) obj;
        if (this.usage == other.usage && this.numComponents == other.numComponents && this.alias.equals(other.alias)) {
            return true;
        }
        return false;
    }
}
