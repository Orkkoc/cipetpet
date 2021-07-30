package com.badlogic.gdx.graphics;

public final class VertexAttributes {
    private final VertexAttribute[] attributes;
    public final int vertexSize;

    public static final class Usage {
        public static final int Color = 1;
        public static final int ColorPacked = 5;
        public static final int Generic = 4;
        public static final int Normal = 2;
        public static final int Position = 0;
        public static final int TextureCoordinates = 3;
    }

    public VertexAttributes(VertexAttribute... attributes2) {
        if (attributes2.length == 0) {
            throw new IllegalArgumentException("attributes must be >= 1");
        }
        VertexAttribute[] list = new VertexAttribute[attributes2.length];
        for (int i = 0; i < attributes2.length; i++) {
            list[i] = attributes2[i];
        }
        this.attributes = list;
        checkValidity();
        this.vertexSize = calculateOffsets();
    }

    public int getOffset(int usage) {
        VertexAttribute vertexAttribute = findByUsage(usage);
        if (vertexAttribute == null) {
            return 0;
        }
        return vertexAttribute.offset / 4;
    }

    public VertexAttribute findByUsage(int usage) {
        int len = size();
        for (int i = 0; i < len; i++) {
            if (get(i).usage == usage) {
                return get(i);
            }
        }
        return null;
    }

    private int calculateOffsets() {
        int count = 0;
        for (VertexAttribute attribute : this.attributes) {
            attribute.offset = count;
            if (attribute.usage == 5) {
                count += 4;
            } else {
                count += attribute.numComponents * 4;
            }
        }
        return count;
    }

    private void checkValidity() {
        boolean pos = false;
        boolean cols = false;
        int i = 0;
        while (i < this.attributes.length) {
            VertexAttribute attribute = this.attributes[i];
            if (attribute.usage == 0) {
                if (pos) {
                    throw new IllegalArgumentException("two position attributes were specified");
                }
                pos = true;
            }
            if (attribute.usage != 2 || 0 == 0) {
                if (attribute.usage == 1 || attribute.usage == 5) {
                    if (attribute.numComponents != 4) {
                        throw new IllegalArgumentException("color attribute must have 4 components");
                    } else if (cols) {
                        throw new IllegalArgumentException("two color attributes were specified");
                    } else {
                        cols = true;
                    }
                }
                i++;
            } else {
                throw new IllegalArgumentException("two normal attributes were specified");
            }
        }
        if (!pos) {
            throw new IllegalArgumentException("no position attribute was specified");
        }
    }

    public int size() {
        return this.attributes.length;
    }

    public VertexAttribute get(int index) {
        return this.attributes[index];
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < this.attributes.length; i++) {
            builder.append("(");
            builder.append(this.attributes[i].alias);
            builder.append(", ");
            builder.append(this.attributes[i].usage);
            builder.append(", ");
            builder.append(this.attributes[i].numComponents);
            builder.append(", ");
            builder.append(this.attributes[i].offset);
            builder.append(")");
            builder.append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VertexAttributes)) {
            return false;
        }
        VertexAttributes other = (VertexAttributes) obj;
        if (this.attributes.length != other.size()) {
            return false;
        }
        for (int i = 0; i < this.attributes.length; i++) {
            if (!this.attributes[i].equals(other.attributes[i])) {
                return false;
            }
        }
        return true;
    }
}
