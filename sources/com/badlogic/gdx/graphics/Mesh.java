package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.IndexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectSubData;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mesh implements Disposable {
    public static boolean forceVBO = false;
    static final Map<Application, List<Mesh>> meshes = new HashMap();
    boolean autoBind = true;
    final IndexData indices;
    final boolean isVertexArray;
    final VertexData vertices;

    public enum VertexDataType {
        VertexArray,
        VertexBufferObject,
        VertexBufferObjectSubData
    }

    public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
        if (Gdx.gl20 == null && Gdx.gl11 == null && !forceVBO) {
            this.vertices = new VertexArray(maxVertices, attributes);
            this.indices = new IndexArray(maxIndices);
            this.isVertexArray = true;
        } else {
            this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObject(isStatic, maxIndices);
            this.isVertexArray = false;
        }
        addManagedMesh(Gdx.app, this);
    }

    public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
        if (Gdx.gl20 == null && Gdx.gl11 == null && !forceVBO) {
            this.vertices = new VertexArray(maxVertices, attributes);
            this.indices = new IndexArray(maxIndices);
            this.isVertexArray = true;
        } else {
            this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObject(isStatic, maxIndices);
            this.isVertexArray = false;
        }
        addManagedMesh(Gdx.app, this);
    }

    public Mesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
        if (type == VertexDataType.VertexBufferObject) {
            this.vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObject(isStatic, maxIndices);
            this.isVertexArray = false;
        } else if (type == VertexDataType.VertexBufferObjectSubData) {
            this.vertices = new VertexBufferObjectSubData(isStatic, maxVertices, attributes);
            this.indices = new IndexBufferObjectSubData(isStatic, maxIndices);
            this.isVertexArray = false;
        } else {
            this.vertices = new VertexArray(maxVertices, attributes);
            this.indices = new IndexArray(maxIndices);
            this.isVertexArray = true;
        }
        addManagedMesh(Gdx.app, this);
    }

    public static Mesh create(boolean isStatic, Mesh base, Matrix4[] transformations) {
        VertexAttribute posAttr = base.getVertexAttribute(0);
        int offset = posAttr.offset / 4;
        int numComponents = posAttr.numComponents;
        int numVertices = base.getNumVertices();
        int vertexSize = base.getVertexSize() / 4;
        int baseSize = numVertices * vertexSize;
        int numIndices = base.getNumIndices();
        float[] vertices2 = new float[(numVertices * vertexSize * transformations.length)];
        short[] indices2 = new short[(transformations.length * numIndices)];
        base.getIndices(indices2);
        for (int i = 0; i < transformations.length; i++) {
            base.getVertices(0, baseSize, vertices2, baseSize * i);
            transform(transformations[i], vertices2, vertexSize, offset, numComponents, numVertices * i, numVertices);
            if (i > 0) {
                for (int j = 0; j < numIndices; j++) {
                    indices2[(numIndices * i) + j] = (short) (indices2[j] + (numVertices * i));
                }
            }
        }
        Mesh result = new Mesh(isStatic, vertices2.length / vertexSize, indices2.length, base.getVertexAttributes());
        result.setVertices(vertices2);
        result.setIndices(indices2);
        return result;
    }

    public static Mesh create(boolean isStatic, Mesh[] meshes2) {
        return create(isStatic, meshes2, (Matrix4[]) null);
    }

    public static Mesh create(boolean isStatic, Mesh[] meshes2, Matrix4[] transformations) {
        if (transformations == null || transformations.length >= meshes2.length) {
            VertexAttributes attributes = meshes2[0].getVertexAttributes();
            int vertCount = meshes2[0].getNumVertices();
            int idxCount = meshes2[0].getNumIndices();
            for (int i = 1; i < meshes2.length; i++) {
                if (!meshes2[i].getVertexAttributes().equals(attributes)) {
                    throw new IllegalArgumentException("Inconsistent VertexAttributes");
                }
                vertCount += meshes2[i].getNumVertices();
                idxCount += meshes2[i].getNumIndices();
            }
            VertexAttribute posAttr = meshes2[0].getVertexAttribute(0);
            int offset = posAttr.offset / 4;
            int numComponents = posAttr.numComponents;
            int vertexSize = attributes.vertexSize / 4;
            float[] vertices2 = new float[(vertCount * vertexSize)];
            short[] indices2 = new short[idxCount];
            meshes2[0].getVertices(vertices2);
            meshes2[0].getIndices(indices2);
            int voffset = meshes2[0].getNumVertices() * vertexSize;
            int ioffset = meshes2[0].getNumIndices();
            int i2 = 1;
            while (i2 < meshes2.length) {
                Mesh mesh = meshes2[i2];
                int vsize = mesh.getNumVertices() * vertexSize;
                int isize = mesh.getNumIndices();
                mesh.getVertices(0, vsize, vertices2, voffset);
                if (transformations != null) {
                    transform(transformations[i2], vertices2, vertexSize, offset, numComponents, voffset / vertexSize, vsize / vertexSize);
                }
                mesh.getIndices(indices2, ioffset);
                while (0 < isize) {
                    indices2[ioffset + 0] = (short) (indices2[ioffset + 0] + voffset);
                    i2++;
                }
                voffset += vsize;
                ioffset += isize;
                i2++;
            }
            Mesh mesh2 = new Mesh(isStatic, vertices2.length / vertexSize, indices2.length, attributes);
            mesh2.setVertices(vertices2);
            mesh2.setIndices(indices2);
            return mesh2;
        }
        throw new IllegalArgumentException("Not enough transformations specified");
    }

    public void setVertices(float[] vertices2) {
        this.vertices.setVertices(vertices2, 0, vertices2.length);
    }

    public void setVertices(float[] vertices2, int offset, int count) {
        this.vertices.setVertices(vertices2, offset, count);
    }

    public void getVertices(float[] vertices2) {
        getVertices(0, -1, vertices2);
    }

    public void getVertices(int srcOffset, float[] vertices2) {
        getVertices(srcOffset, -1, vertices2);
    }

    public void getVertices(int srcOffset, int count, float[] vertices2) {
        getVertices(srcOffset, count, vertices2, 0);
    }

    public void getVertices(int srcOffset, int count, float[] vertices2, int destOffset) {
        int max = (getNumVertices() * getVertexSize()) / 4;
        if (count == -1 && (count = max - srcOffset) > vertices2.length - destOffset) {
            count = vertices2.length - destOffset;
        }
        if (srcOffset < 0 || count <= 0 || srcOffset + count > max || destOffset < 0 || destOffset >= vertices2.length) {
            throw new IndexOutOfBoundsException();
        } else if (vertices2.length - destOffset < count) {
            throw new IllegalArgumentException("not enough room in vertices array, has " + vertices2.length + " floats, needs " + count);
        } else {
            int pos = getVerticesBuffer().position();
            getVerticesBuffer().position(srcOffset);
            getVerticesBuffer().get(vertices2, destOffset, count);
            getVerticesBuffer().position(pos);
        }
    }

    public void setIndices(short[] indices2) {
        this.indices.setIndices(indices2, 0, indices2.length);
    }

    public void setIndices(short[] indices2, int offset, int count) {
        this.indices.setIndices(indices2, offset, count);
    }

    public void getIndices(short[] indices2) {
        getIndices(indices2, 0);
    }

    public void getIndices(short[] indices2, int destOffset) {
        if (indices2.length - destOffset < getNumIndices()) {
            throw new IllegalArgumentException("not enough room in indices array, has " + indices2.length + " floats, needs " + getNumIndices());
        }
        int pos = getIndicesBuffer().position();
        getIndicesBuffer().position(0);
        getIndicesBuffer().get(indices2, destOffset, getNumIndices());
        getIndicesBuffer().position(pos);
    }

    public int getNumIndices() {
        return this.indices.getNumIndices();
    }

    public int getNumVertices() {
        return this.vertices.getNumVertices();
    }

    public int getMaxVertices() {
        return this.vertices.getNumMaxVertices();
    }

    public int getMaxIndices() {
        return this.indices.getNumMaxIndices();
    }

    public int getVertexSize() {
        return this.vertices.getAttributes().vertexSize;
    }

    public void setAutoBind(boolean autoBind2) {
        this.autoBind = autoBind2;
    }

    public void bind() {
        if (Gdx.graphics.isGL20Available()) {
            throw new IllegalStateException("can't use this render method with OpenGL ES 2.0");
        }
        this.vertices.bind();
        if (!this.isVertexArray && this.indices.getNumIndices() > 0) {
            this.indices.bind();
        }
    }

    public void unbind() {
        if (Gdx.graphics.isGL20Available()) {
            throw new IllegalStateException("can't use this render method with OpenGL ES 2.0");
        }
        this.vertices.unbind();
        if (!this.isVertexArray && this.indices.getNumIndices() > 0) {
            this.indices.unbind();
        }
    }

    public void bind(ShaderProgram shader) {
        if (!Gdx.graphics.isGL20Available()) {
            throw new IllegalStateException("can't use this render method with OpenGL ES 1.x");
        }
        this.vertices.bind(shader);
        if (this.indices.getNumIndices() > 0) {
            this.indices.bind();
        }
    }

    public void unbind(ShaderProgram shader) {
        if (!Gdx.graphics.isGL20Available()) {
            throw new IllegalStateException("can't use this render method with OpenGL ES 1.x");
        }
        this.vertices.unbind(shader);
        if (this.indices.getNumIndices() > 0) {
            this.indices.unbind();
        }
    }

    public void render(int primitiveType) {
        render(primitiveType, 0, this.indices.getNumMaxIndices() > 0 ? getNumIndices() : getNumVertices());
    }

    public void render(int primitiveType, int offset, int count) {
        if (Gdx.graphics.isGL20Available()) {
            throw new IllegalStateException("can't use this render method with OpenGL ES 2.0");
        } else if (count != 0) {
            if (this.autoBind) {
                bind();
            }
            if (this.isVertexArray) {
                if (this.indices.getNumIndices() > 0) {
                    ShortBuffer buffer = this.indices.getBuffer();
                    int oldPosition = buffer.position();
                    int oldLimit = buffer.limit();
                    buffer.position(offset);
                    buffer.limit(offset + count);
                    Gdx.gl10.glDrawElements(primitiveType, count, 5123, buffer);
                    buffer.position(oldPosition);
                    buffer.limit(oldLimit);
                } else {
                    Gdx.gl10.glDrawArrays(primitiveType, offset, count);
                }
            } else if (this.indices.getNumIndices() > 0) {
                Gdx.gl11.glDrawElements(primitiveType, count, 5123, offset * 2);
            } else {
                Gdx.gl11.glDrawArrays(primitiveType, offset, count);
            }
            if (this.autoBind) {
                unbind();
            }
        }
    }

    public void render(ShaderProgram shader, int primitiveType) {
        render(shader, primitiveType, 0, this.indices.getNumMaxIndices() > 0 ? getNumIndices() : getNumVertices());
    }

    public void render(ShaderProgram shader, int primitiveType, int offset, int count) {
        if (!Gdx.graphics.isGL20Available()) {
            throw new IllegalStateException("can't use this render method with OpenGL ES 1.x");
        } else if (count != 0) {
            if (this.autoBind) {
                bind(shader);
            }
            if (this.isVertexArray) {
                if (this.indices.getNumIndices() > 0) {
                    ShortBuffer buffer = this.indices.getBuffer();
                    int oldPosition = buffer.position();
                    int oldLimit = buffer.limit();
                    buffer.position(offset);
                    buffer.limit(offset + count);
                    Gdx.gl20.glDrawElements(primitiveType, count, 5123, buffer);
                    buffer.position(oldPosition);
                    buffer.limit(oldLimit);
                } else {
                    Gdx.gl20.glDrawArrays(primitiveType, offset, count);
                }
            } else if (this.indices.getNumIndices() > 0) {
                Gdx.gl20.glDrawElements(primitiveType, count, 5123, offset * 2);
            } else {
                Gdx.gl20.glDrawArrays(primitiveType, offset, count);
            }
            if (this.autoBind) {
                unbind(shader);
            }
        }
    }

    public void dispose() {
        if (meshes.get(Gdx.app) != null) {
            meshes.get(Gdx.app).remove(this);
        }
        this.vertices.dispose();
        this.indices.dispose();
    }

    public VertexAttribute getVertexAttribute(int usage) {
        VertexAttributes attributes = this.vertices.getAttributes();
        int len = attributes.size();
        for (int i = 0; i < len; i++) {
            if (attributes.get(i).usage == usage) {
                return attributes.get(i);
            }
        }
        return null;
    }

    public VertexAttributes getVertexAttributes() {
        return this.vertices.getAttributes();
    }

    public FloatBuffer getVerticesBuffer() {
        return this.vertices.getBuffer();
    }

    public BoundingBox calculateBoundingBox() {
        BoundingBox bbox = new BoundingBox();
        calculateBoundingBox(bbox);
        return bbox;
    }

    public void calculateBoundingBox(BoundingBox bbox) {
        int numVertices = getNumVertices();
        if (numVertices == 0) {
            throw new GdxRuntimeException("No vertices defined");
        }
        FloatBuffer verts = this.vertices.getBuffer();
        bbox.inf();
        VertexAttribute posAttrib = getVertexAttribute(0);
        int offset = posAttrib.offset / 4;
        int vertexSize = this.vertices.getAttributes().vertexSize / 4;
        int idx = offset;
        switch (posAttrib.numComponents) {
            case 1:
                for (int i = 0; i < numVertices; i++) {
                    bbox.ext(verts.get(idx), 0.0f, 0.0f);
                    idx += vertexSize;
                }
                return;
            case 2:
                for (int i2 = 0; i2 < numVertices; i2++) {
                    bbox.ext(verts.get(idx), verts.get(idx + 1), 0.0f);
                    idx += vertexSize;
                }
                return;
            case 3:
                for (int i3 = 0; i3 < numVertices; i3++) {
                    bbox.ext(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                    idx += vertexSize;
                }
                return;
            default:
                return;
        }
    }

    public ShortBuffer getIndicesBuffer() {
        return this.indices.getBuffer();
    }

    private static void addManagedMesh(Application app, Mesh mesh) {
        List<Mesh> managedResources = meshes.get(app);
        if (managedResources == null) {
            managedResources = new ArrayList<>();
        }
        managedResources.add(mesh);
        meshes.put(app, managedResources);
    }

    public static void invalidateAllMeshes(Application app) {
        List<Mesh> meshesList = meshes.get(app);
        if (meshesList != null) {
            for (int i = 0; i < meshesList.size(); i++) {
                if (meshesList.get(i).vertices instanceof VertexBufferObject) {
                    ((VertexBufferObject) meshesList.get(i).vertices).invalidate();
                }
                meshesList.get(i).indices.invalidate();
            }
        }
    }

    public static void clearAllMeshes(Application app) {
        meshes.remove(app);
    }

    public static String getManagedStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Managed meshes/app: { ");
        for (Application app : meshes.keySet()) {
            builder.append(meshes.get(app).size());
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        VertexAttribute posAttr = getVertexAttribute(0);
        int offset = posAttr.offset / 4;
        int numComponents = posAttr.numComponents;
        int numVertices = getNumVertices();
        int vertexSize = getVertexSize() / 4;
        float[] vertices2 = new float[(numVertices * vertexSize)];
        getVertices(vertices2);
        int idx = offset;
        switch (numComponents) {
            case 1:
                for (int i = 0; i < numVertices; i++) {
                    vertices2[idx] = vertices2[idx] * scaleX;
                    idx += vertexSize;
                }
                break;
            case 2:
                for (int i2 = 0; i2 < numVertices; i2++) {
                    vertices2[idx] = vertices2[idx] * scaleX;
                    int i3 = idx + 1;
                    vertices2[i3] = vertices2[i3] * scaleY;
                    idx += vertexSize;
                }
                break;
            case 3:
                for (int i4 = 0; i4 < numVertices; i4++) {
                    vertices2[idx] = vertices2[idx] * scaleX;
                    int i5 = idx + 1;
                    vertices2[i5] = vertices2[i5] * scaleY;
                    int i6 = idx + 2;
                    vertices2[i6] = vertices2[i6] * scaleZ;
                    idx += vertexSize;
                }
                break;
        }
        setVertices(vertices2);
    }

    public void transform(Matrix4 matrix) {
        transform(matrix, 0, getNumVertices());
    }

    /* access modifiers changed from: protected */
    public void transform(Matrix4 matrix, int start, int count) {
        VertexAttribute posAttr = getVertexAttribute(0);
        int offset = posAttr.offset / 4;
        int vertexSize = getVertexSize() / 4;
        int numComponents = posAttr.numComponents;
        float[] vertices2 = new float[(getNumVertices() * vertexSize)];
        getVertices(0, vertices2.length, vertices2);
        transform(matrix, vertices2, vertexSize, offset, numComponents, start, count);
        setVertices(vertices2, 0, vertices2.length);
    }

    public static void transform(Matrix4 matrix, float[] vertices2, int vertexSize, int offset, int dimensions, int start, int count) {
        if (offset < 0 || dimensions < 1 || offset + dimensions > vertexSize) {
            throw new IndexOutOfBoundsException();
        } else if (start < 0 || count < 1 || (start + count) * vertexSize > vertices2.length) {
            throw new IndexOutOfBoundsException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize + ", length = " + vertices2.length);
        } else {
            Vector3 tmp = Vector3.tmp;
            int idx = offset + (start * vertexSize);
            switch (dimensions) {
                case 1:
                    for (int i = 0; i < count; i++) {
                        tmp.set(vertices2[idx], 0.0f, 0.0f).mul(matrix);
                        vertices2[idx] = tmp.f170x;
                        idx += vertexSize;
                    }
                    return;
                case 2:
                    for (int i2 = 0; i2 < count; i2++) {
                        tmp.set(vertices2[idx], vertices2[idx + 1], 0.0f).mul(matrix);
                        vertices2[idx] = tmp.f170x;
                        vertices2[idx + 1] = tmp.f171y;
                        idx += vertexSize;
                    }
                    return;
                case 3:
                    for (int i3 = 0; i3 < count; i3++) {
                        tmp.set(vertices2[idx], vertices2[idx + 1], vertices2[idx + 2]).mul(matrix);
                        vertices2[idx] = tmp.f170x;
                        vertices2[idx + 1] = tmp.f171y;
                        vertices2[idx + 2] = tmp.f172z;
                        idx += vertexSize;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public Mesh copy(boolean isStatic, boolean removeDuplicates, int[] usage) {
        Mesh result;
        int vertexSize = getVertexSize() / 4;
        int numVertices = getNumVertices();
        float[] vertices2 = new float[(numVertices * vertexSize)];
        getVertices(0, vertices2.length, vertices2);
        short[] checks = null;
        VertexAttribute[] attrs = null;
        short newVertexSize = 0;
        if (usage != null) {
            int size = 0;
            int as = 0;
            for (int i = 0; i < usage.length; i++) {
                if (getVertexAttribute(usage[i]) != null) {
                    size += getVertexAttribute(usage[i]).numComponents;
                    as++;
                }
            }
            if (size > 0) {
                attrs = new VertexAttribute[as];
                checks = new short[size];
                int idx = -1;
                int ai = -1;
                for (int vertexAttribute : usage) {
                    VertexAttribute a = getVertexAttribute(vertexAttribute);
                    if (a != null) {
                        for (int j = 0; j < a.numComponents; j++) {
                            idx++;
                            checks[idx] = (short) (a.offset + j);
                        }
                        ai++;
                        attrs[ai] = new VertexAttribute(a.usage, a.numComponents, a.alias);
                        newVertexSize += a.numComponents;
                    }
                }
            }
        }
        if (checks == null) {
            checks = new short[vertexSize];
            for (short i2 = 0; i2 < vertexSize; i2 = (short) (i2 + 1)) {
                checks[i2] = i2;
            }
            newVertexSize = vertexSize;
        }
        int numIndices = getNumIndices();
        short[] indices2 = null;
        if (numIndices > 0) {
            indices2 = new short[numIndices];
            getIndices(indices2);
            if (removeDuplicates || newVertexSize != vertexSize) {
                float[] tmp = new float[vertices2.length];
                int size2 = 0;
                for (int i3 = 0; i3 < numIndices; i3++) {
                    int idx1 = indices2[i3] * vertexSize;
                    short newIndex = -1;
                    if (removeDuplicates) {
                        for (short j2 = 0; j2 < size2 && newIndex < 0; j2 = (short) (j2 + 1)) {
                            int idx2 = j2 * newVertexSize;
                            boolean found = true;
                            for (int k = 0; k < checks.length && found; k++) {
                                if (tmp[idx2 + k] != vertices2[checks[k] + idx1]) {
                                    found = false;
                                }
                            }
                            if (found) {
                                newIndex = j2;
                            }
                        }
                    }
                    if (newIndex > 0) {
                        indices2[i3] = newIndex;
                    } else {
                        int idx3 = size2 * newVertexSize;
                        for (int j3 = 0; j3 < checks.length; j3++) {
                            tmp[idx3 + j3] = vertices2[checks[j3] + idx1];
                        }
                        indices2[i3] = (short) size2;
                        size2++;
                    }
                }
                vertices2 = tmp;
                numVertices = size2;
            }
        }
        if (attrs == null) {
            result = new Mesh(isStatic, numVertices, indices2 == null ? 0 : indices2.length, getVertexAttributes());
        } else {
            result = new Mesh(isStatic, numVertices, indices2 == null ? 0 : indices2.length, attrs);
        }
        result.setVertices(vertices2, 0, numVertices * newVertexSize);
        result.setIndices(indices2);
        return result;
    }

    public Mesh copy(boolean isStatic) {
        return copy(isStatic, false, (int[]) null);
    }
}
