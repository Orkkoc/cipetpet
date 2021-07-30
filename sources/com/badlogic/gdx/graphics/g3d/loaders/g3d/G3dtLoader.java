package com.badlogic.gdx.graphics.g3d.loaders.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.KeyframedModelLoader;
import com.badlogic.gdx.graphics.g3d.loaders.StillModelLoader;
import com.badlogic.gdx.graphics.g3d.model.keyframe.Keyframe;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedAnimation;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedSubMesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class G3dtLoader {
    static String line = null;
    static int lineNum = 0;

    public static KeyframedModel loadKeyframedModel(FileHandle handle, boolean flipV) {
        return loadKeyframedModel(handle.read(), flipV);
    }

    public static StillModel loadStillModel(FileHandle handle, boolean flipV) {
        return loadStillModel(handle.read(), flipV);
    }

    public static StillModel loadStillModel(InputStream stream, boolean flipV) {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        lineNum = 1;
        try {
            if (!readString(in).equals("g3dt-still-1.0")) {
                throw new GdxRuntimeException("incorrect version");
            }
            int numMeshes = readInt(in);
            StillSubMesh[] subMeshes = new StillSubMesh[numMeshes];
            for (int i = 0; i < numMeshes; i++) {
                subMeshes[i] = readStillSubMesh(in, flipV);
            }
            return new StillModel(subMeshes);
        } catch (Throwable e) {
            throw new GdxRuntimeException("Couldn't read keyframed model, error in line " + lineNum + ", '" + line + "' : " + e.getMessage(), e);
        }
    }

    private static StillSubMesh readStillSubMesh(BufferedReader in, boolean flipV) throws IOException {
        String name = readString(in);
        IntArray indices = readFaces(in);
        int numVertices = readInt(in);
        int numAttributes = readInt(in);
        if (!readString(in).equals("position")) {
            throw new GdxRuntimeException("first attribute must be position.");
        }
        int numUvs = 0;
        boolean hasNormals = false;
        int i = 1;
        while (i < numAttributes) {
            String attributeType = readString(in);
            if (attributeType.equals("normal") || attributeType.equals("uv")) {
                if (attributeType.equals("normal")) {
                    if (i != 1) {
                        throw new GdxRuntimeException("attribute normal must be second attribute");
                    }
                    hasNormals = true;
                }
                if (attributeType.equals("uv")) {
                    numUvs++;
                }
                i++;
            } else {
                throw new GdxRuntimeException("attribute name must be normal or uv");
            }
        }
        VertexAttribute[] vertexAttributes = createVertexAttributes(hasNormals, numUvs);
        int vertexSize = new VertexAttributes(vertexAttributes).vertexSize / 4;
        float[] vertices = new float[(numVertices * vertexSize)];
        int idx = 0;
        int uvOffset = hasNormals ? 6 : 3;
        for (int i2 = 0; i2 < numVertices; i2++) {
            readFloatArray(in, vertices, idx);
            if (flipV) {
                for (int j = idx + uvOffset + 1; j < idx + uvOffset + (numUvs * 2); j += 2) {
                    vertices[j] = 1.0f - vertices[j];
                }
            }
            idx += vertexSize;
        }
        Mesh mesh = new Mesh(true, numVertices, indices.size, vertexAttributes);
        mesh.setVertices(vertices);
        mesh.setIndices(convertToShortArray(indices));
        return new StillSubMesh(name, mesh, 4);
    }

    public static KeyframedModel loadKeyframedModel(InputStream stream, boolean flipV) {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        lineNum = 1;
        try {
            if (!readString(in).equals("g3dt-keyframed-1.0")) {
                throw new GdxRuntimeException("incorrect version");
            }
            int numMeshes = readInt(in);
            KeyframedSubMesh[] subMeshes = new KeyframedSubMesh[numMeshes];
            for (int i = 0; i < numMeshes; i++) {
                subMeshes[i] = readMesh(in, flipV);
            }
            KeyframedModel model = new KeyframedModel(subMeshes);
            model.setAnimation(model.getAnimations()[0].name, 0.0f, false);
            return model;
        } catch (Throwable e) {
            throw new GdxRuntimeException("Couldn't read keyframed model, error in line " + lineNum + ", '" + line + "' : " + e.getMessage(), e);
        }
    }

    private static KeyframedSubMesh readMesh(BufferedReader in, boolean flipV) throws IOException {
        String name = readString(in);
        IntArray indices = readFaces(in);
        int numVertices = readInt(in);
        int numAttributes = readInt(in);
        if (!readString(in).equals("position")) {
            throw new GdxRuntimeException("first attribute must be position.");
        }
        Array<FloatArray> uvSets = new Array<>();
        boolean hasNormals = false;
        int i = 1;
        while (i < numAttributes) {
            String attributeType = readString(in);
            if (attributeType.equals("normal") || attributeType.equals("uv")) {
                if (attributeType.equals("normal")) {
                    if (i != 1) {
                        throw new GdxRuntimeException("attribute normal must be second attribute");
                    }
                    hasNormals = true;
                }
                if (attributeType.equals("uv")) {
                    uvSets.add(readUVSet(in, numVertices, flipV));
                }
                i++;
            } else {
                throw new GdxRuntimeException("attribute name must be normal or uv");
            }
        }
        int animatedComponents = hasNormals ? 6 : 3;
        VertexAttribute[] createVertexAttributes = createVertexAttributes(hasNormals, uvSets.size);
        int numAnimations = readInt(in);
        ObjectMap<String, KeyframedAnimation> animations = new ObjectMap<>(numAnimations);
        for (int i2 = 0; i2 < numAnimations; i2++) {
            String animationName = readString(in);
            int numKeyframes = readInt(in);
            float frameDuration = readFloat(in);
            Keyframe[] keyframes = new Keyframe[numKeyframes];
            float time = 0.0f;
            new FloatArray(animatedComponents);
            for (int frame = 0; frame < numKeyframes; frame++) {
                float[] vertices = new float[(numVertices * animatedComponents)];
                int idx = 0;
                for (int j = 0; j < numVertices; j++) {
                    idx = readFloatArray(in, vertices, idx);
                }
                keyframes[frame] = new Keyframe(time, vertices);
                time += frameDuration;
            }
            animations.put(animationName, new KeyframedAnimation(animationName, frameDuration, keyframes));
        }
        KeyframedSubMesh keyframedSubMesh = new KeyframedSubMesh(name, new Mesh(Mesh.VertexDataType.VertexArray, false, numVertices, indices.size, createVertexAttributes(hasNormals, uvSets.size)), buildVertices(numVertices, hasNormals, uvSets), animations, animatedComponents, 4);
        keyframedSubMesh.mesh.setIndices(convertToShortArray(indices));
        keyframedSubMesh.mesh.setVertices(keyframedSubMesh.blendedVertices);
        return keyframedSubMesh;
    }

    private static float[] buildVertices(int numVertices, boolean hasNormals, Array<FloatArray> uvSets) {
        float[] vertices = new float[(((hasNormals ? 3 : 0) + 3 + (uvSets.size * 2)) * numVertices)];
        int idxUv = 0;
        int i = 0;
        int idx = 0;
        while (i < numVertices) {
            int idx2 = idx + 1;
            vertices[idx] = 0.0f;
            int idx3 = idx2 + 1;
            vertices[idx2] = 0.0f;
            int idx4 = idx3 + 1;
            vertices[idx3] = 0.0f;
            if (hasNormals) {
                int idx5 = idx4 + 1;
                vertices[idx4] = 0.0f;
                int idx6 = idx5 + 1;
                vertices[idx5] = 0.0f;
                vertices[idx6] = 0.0f;
                idx4 = idx6 + 1;
            }
            for (int j = 0; j < uvSets.size; j++) {
                int idx7 = idx4 + 1;
                vertices[idx4] = uvSets.get(j).get(idxUv);
                idx4 = idx7 + 1;
                vertices[idx7] = uvSets.get(j).get(idxUv + 1);
            }
            idxUv += 2;
            i++;
            idx = idx4;
        }
        return vertices;
    }

    private static VertexAttribute[] createVertexAttributes(boolean hasNormals, int uvs) {
        int i;
        int idx;
        if (hasNormals) {
            i = 1;
        } else {
            i = 0;
        }
        VertexAttribute[] attributes = new VertexAttribute[(i + 1 + uvs)];
        int idx2 = 0 + 1;
        attributes[0] = new VertexAttribute(0, 3, ShaderProgram.POSITION_ATTRIBUTE);
        if (hasNormals) {
            idx = idx2 + 1;
            attributes[idx2] = new VertexAttribute(2, 3, ShaderProgram.NORMAL_ATTRIBUTE);
        } else {
            idx = idx2;
        }
        int i2 = 0;
        int idx3 = idx;
        while (i2 < uvs) {
            attributes[idx3] = new VertexAttribute(3, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + i2);
            i2++;
            idx3++;
        }
        return attributes;
    }

    private static FloatArray readUVSet(BufferedReader in, int numVertices, boolean flipV) throws IOException {
        FloatArray uvSet = new FloatArray(numVertices * 2);
        FloatArray uv = new FloatArray(2);
        for (int i = 0; i < numVertices; i++) {
            readFloatArray(in, uv);
            uvSet.add(uv.items[0]);
            uvSet.add(flipV ? 1.0f - uv.items[1] : uv.items[1]);
        }
        return uvSet;
    }

    private static IntArray readFaces(BufferedReader in) throws NumberFormatException, IOException {
        int numFaces = readInt(in);
        IntArray faceIndices = new IntArray();
        IntArray triangles = new IntArray();
        IntArray indices = new IntArray();
        for (int face = 0; face < numFaces; face++) {
            readIntArray(in, faceIndices);
            int numIndices = faceIndices.get(0);
            triangles.clear();
            int baseIndex = faceIndices.get(1);
            for (int i = 2; i < numIndices; i++) {
                triangles.add(baseIndex);
                triangles.add(faceIndices.items[i]);
                triangles.add(faceIndices.items[i + 1]);
            }
            indices.addAll(triangles);
        }
        indices.shrink();
        return indices;
    }

    private static short[] convertToShortArray(IntArray array) {
        short[] shortArray = new short[array.size];
        for (int i = 0; i < array.size; i++) {
            shortArray[i] = (short) array.items[i];
        }
        return shortArray;
    }

    private static float readFloat(BufferedReader in) throws NumberFormatException, IOException {
        lineNum++;
        return Float.parseFloat(read(in).trim());
    }

    private static int readInt(BufferedReader in) throws NumberFormatException, IOException {
        lineNum++;
        return (int) Math.floor((double) Float.parseFloat(read(in).trim()));
    }

    private static String readString(BufferedReader in) throws IOException {
        lineNum++;
        return read(in);
    }

    private static void readFloatArray(BufferedReader in, FloatArray array) throws IOException {
        lineNum++;
        array.clear();
        for (String trim : read(in).split(",")) {
            array.add(Float.parseFloat(trim.trim()));
        }
    }

    private static int readFloatArray(BufferedReader in, float[] array, int idx) throws IOException {
        lineNum++;
        String[] tokens = read(in).split(",");
        int len = tokens.length;
        int i = 0;
        int idx2 = idx;
        while (i < len) {
            array[idx2] = Float.parseFloat(tokens[i].trim());
            i++;
            idx2++;
        }
        return idx2;
    }

    private static void readIntArray(BufferedReader in, IntArray array) throws IOException {
        array.clear();
        for (String trim : read(in).split(",")) {
            array.add(Integer.parseInt(trim.trim()));
        }
    }

    private static String read(BufferedReader in) throws IOException {
        line = in.readLine();
        return line;
    }

    public static class G3dtStillModelLoader implements StillModelLoader {
        public StillModel load(FileHandle handle, ModelLoaderHints hints) {
            return G3dtLoader.loadStillModel(handle, hints.flipV);
        }
    }

    public static class G3dtKeyframedModelLoader implements KeyframedModelLoader {
        public KeyframedModel load(FileHandle handle, ModelLoaderHints hints) {
            return G3dtLoader.loadKeyframedModel(handle, hints.flipV);
        }
    }
}
