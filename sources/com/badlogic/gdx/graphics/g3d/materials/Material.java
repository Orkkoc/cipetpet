package com.badlogic.gdx.graphics.g3d.materials;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class Material implements Iterable<MaterialAttribute> {
    private Array<MaterialAttribute> attributes;
    protected boolean hasTexture;
    protected String name;
    protected boolean needBlending;
    protected ShaderProgram shader;

    public Material() {
        this.attributes = new Array<>(2);
    }

    public Material(String name2, Array<MaterialAttribute> attributes2) {
        this.name = name2;
        this.attributes = attributes2;
        checkAttributes();
    }

    public Material(String name2, MaterialAttribute... attributes2) {
        this(name2, (Array<MaterialAttribute>) new Array((T[]) attributes2));
    }

    /* access modifiers changed from: protected */
    public void checkAttributes() {
        this.needBlending = false;
        this.hasTexture = false;
        for (int i = 0; i < this.attributes.size; i++) {
            if (!this.needBlending && (this.attributes.get(i) instanceof BlendingAttribute)) {
                this.needBlending = true;
            } else if (!this.hasTexture && (this.attributes.get(i) instanceof TextureAttribute)) {
                this.hasTexture = true;
            }
        }
    }

    public void bind() {
        for (int i = 0; i < this.attributes.size; i++) {
            this.attributes.get(i).bind();
        }
    }

    public void bind(ShaderProgram program) {
        for (int i = 0; i < this.attributes.size; i++) {
            this.attributes.get(i).bind(program);
        }
    }

    public String getName() {
        return this.name;
    }

    public void addAttribute(MaterialAttribute... attributes2) {
        for (int i = 0; i < attributes2.length; i++) {
            if (attributes2[i] instanceof BlendingAttribute) {
                this.needBlending = true;
            } else if (attributes2[i] instanceof TextureAttribute) {
                this.hasTexture = true;
            }
            this.attributes.add(attributes2[i]);
        }
    }

    public void removeAttribute(MaterialAttribute... attributes2) {
        for (MaterialAttribute removeValue : attributes2) {
            this.attributes.removeValue(removeValue, true);
        }
        checkAttributes();
    }

    public void clearAttributes() {
        this.attributes.clear();
        this.needBlending = false;
    }

    public MaterialAttribute getAttribute(int index) {
        if (index < 0 || index >= this.attributes.size) {
            return null;
        }
        return this.attributes.get(index);
    }

    public int getNumberOfAttributes() {
        return this.attributes.size;
    }

    public Material copy() {
        Array<MaterialAttribute> attributes2 = new Array<>(this.attributes.size);
        for (int i = 0; i < attributes2.size; i++) {
            attributes2.add(this.attributes.get(i).copy());
        }
        Material copy = new Material(this.name, attributes2);
        copy.shader = this.shader;
        return copy;
    }

    public int hashCode() {
        return ((this.attributes.hashCode() + 31) * 31) + (this.name == null ? 0 : this.name.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Material other = (Material) obj;
        if (other.attributes.size != this.attributes.size) {
            return false;
        }
        for (int i = 0; i < this.attributes.size; i++) {
            if (!this.attributes.get(i).equals(other.attributes.get(i))) {
                return false;
            }
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public boolean shaderEquals(Material other) {
        if (this == other) {
            return true;
        }
        int len = this.attributes.size;
        if (len != other.attributes.size) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            String str = this.attributes.get(i).name;
            if (str == null) {
                return false;
            }
            boolean matchFound = false;
            int j = 0;
            while (true) {
                if (j >= len) {
                    break;
                } else if (str.equals(other.attributes.get(j).name)) {
                    matchFound = true;
                    break;
                } else {
                    j++;
                }
            }
            if (!matchFound) {
                return false;
            }
        }
        return true;
    }

    public void setPooled(Material material) {
        this.name = material.name;
        this.shader = material.shader;
        this.needBlending = material.needBlending;
        this.hasTexture = material.hasTexture;
        this.attributes.clear();
        int len = material.attributes.size;
        for (int i = 0; i < len; i++) {
            this.attributes.add(material.attributes.get(i).pooledCopy());
        }
    }

    public boolean isNeedBlending() {
        return this.needBlending;
    }

    public boolean hasTexture() {
        return this.hasTexture;
    }

    public ShaderProgram getShader() {
        return this.shader;
    }

    public void setShader(ShaderProgram shader2) {
        this.shader = shader2;
    }

    public void resetShader() {
        this.shader = null;
    }

    public Iterator<MaterialAttribute> iterator() {
        return this.attributes.iterator();
    }
}
