package com.badlogic.gdx.graphics.g3d.loaders.md2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.KeyframedModelLoader;
import com.badlogic.gdx.graphics.g3d.model.keyframe.Keyframe;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedAnimation;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedSubMesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.LittleEndianInputStream;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MD2Loader implements KeyframedModelLoader {
    private final byte[] charBuffer = new byte[16];

    public KeyframedModel load(FileHandle file, ModelLoaderHints hints) {
        float frameDuration = 0.2f;
        if (hints instanceof MD2LoaderHints) {
            frameDuration = ((MD2LoaderHints) hints).frameDuration;
        }
        return load(file, frameDuration);
    }

    public KeyframedModel load(FileHandle fileHandle, float frameDuration) {
        InputStream in = fileHandle.read();
        try {
            return load(in, frameDuration);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public KeyframedModel load(InputStream in, float frameDuration) {
        try {
            byte[] bytes = loadBytes(in);
            MD2Header header = loadHeader(bytes);
            return buildModel(header, loadTriangles(header, bytes), loadTexCoords(header, bytes), loadFrames(header, bytes), frameDuration);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private KeyframedModel buildModel(MD2Header header, MD2Triangle[] triangles, float[] texCoords, MD2Frame[] frames, float frameDuration) {
        int idx;
        ArrayList<VertexIndices> vertCombos = new ArrayList<>();
        short[] indices = new short[(triangles.length * 3)];
        int idx2 = 0;
        short vertIdx = 0;
        int i = 0;
        while (i < triangles.length) {
            MD2Triangle triangle = triangles[i];
            int j = 0;
            while (true) {
                idx = idx2;
                if (j >= 3) {
                    break;
                }
                VertexIndices vert = null;
                boolean contains = false;
                int k = 0;
                while (true) {
                    if (k >= vertCombos.size()) {
                        break;
                    }
                    VertexIndices vIdx = vertCombos.get(k);
                    if (vIdx.vIdx == triangle.vertices[j] && vIdx.tIdx == triangle.texCoords[j]) {
                        vert = vIdx;
                        contains = true;
                        break;
                    }
                    k++;
                }
                if (!contains) {
                    vert = new VertexIndices(triangle.vertices[j], triangle.texCoords[j], vertIdx);
                    vertCombos.add(vert);
                    vertIdx = (short) (vertIdx + 1);
                }
                idx2 = idx + 1;
                indices[idx] = vert.nIdx;
                j++;
            }
            i++;
            idx2 = idx;
        }
        int idx3 = 0;
        float[] uvs = new float[(vertCombos.size() * 2)];
        for (int i2 = 0; i2 < vertCombos.size(); i2++) {
            VertexIndices vtI = vertCombos.get(i2);
            int idx4 = idx3 + 1;
            uvs[idx3] = texCoords[vtI.tIdx * 2];
            idx3 = idx4 + 1;
            uvs[idx4] = texCoords[(vtI.tIdx * 2) + 1];
        }
        for (int i3 = 0; i3 < frames.length; i3++) {
            MD2Frame frame = frames[i3];
            int idx5 = 0;
            float[] newVerts = new float[(vertCombos.size() * 6)];
            for (int j2 = 0; j2 < vertCombos.size(); j2++) {
                VertexIndices vIdx2 = vertCombos.get(j2);
                int idx6 = idx5 + 1;
                newVerts[idx5] = frame.vertices[vIdx2.vIdx * 3];
                int idx7 = idx6 + 1;
                newVerts[idx6] = frame.vertices[(vIdx2.vIdx * 3) + 1];
                int idx8 = idx7 + 1;
                newVerts[idx7] = frame.vertices[(vIdx2.vIdx * 3) + 2];
                int idx9 = idx8 + 1;
                newVerts[idx8] = MD2Normals.normals[frame.normalIndices[vIdx2.vIdx]][1];
                int idx10 = idx9 + 1;
                newVerts[idx9] = MD2Normals.normals[frame.normalIndices[vIdx2.vIdx]][2];
                idx5 = idx10 + 1;
                newVerts[idx10] = MD2Normals.normals[frame.normalIndices[vIdx2.vIdx]][0];
            }
            frame.vertices = newVerts;
        }
        header.numVertices = vertCombos.size();
        float[] blendedVertices = new float[(header.numVertices * 8)];
        MD2Frame frame2 = frames[0];
        int idx11 = 0;
        int idxV = 0;
        int idxT = 0;
        for (int i4 = 0; i4 < header.numVertices; i4++) {
            VertexIndices vertexIndices = vertCombos.get(i4);
            int idx12 = idx11 + 1;
            int idxV2 = idxV + 1;
            blendedVertices[idx11] = frame2.vertices[idxV];
            int idx13 = idx12 + 1;
            int idxV3 = idxV2 + 1;
            blendedVertices[idx12] = frame2.vertices[idxV2];
            int idx14 = idx13 + 1;
            int idxV4 = idxV3 + 1;
            blendedVertices[idx13] = frame2.vertices[idxV3];
            int idx15 = idx14 + 1;
            int idxV5 = idxV4 + 1;
            blendedVertices[idx14] = frame2.vertices[idxV4];
            int idx16 = idx15 + 1;
            int idxV6 = idxV5 + 1;
            blendedVertices[idx15] = frame2.vertices[idxV5];
            int idx17 = idx16 + 1;
            idxV = idxV6 + 1;
            blendedVertices[idx16] = frame2.vertices[idxV6];
            int idx18 = idx17 + 1;
            int idxT2 = idxT + 1;
            blendedVertices[idx17] = uvs[idxT];
            idx11 = idx18 + 1;
            idxT = idxT2 + 1;
            blendedVertices[idx18] = uvs[idxT2];
        }
        ObjectMap<String, KeyframedAnimation> animations = new ObjectMap<>();
        String lastName = frames[0].name;
        int beginFrame = 0;
        for (int frameNum = 1; frameNum < frames.length; frameNum++) {
            if (!frames[frameNum].name.equals(lastName) || frameNum == frames.length - 1) {
                KeyframedAnimation keyframedAnimation = new KeyframedAnimation(lastName, frameDuration, new Keyframe[(frameNum - beginFrame)]);
                for (int subFrame = beginFrame; subFrame < frameNum; subFrame++) {
                    int absFrameNum = subFrame - beginFrame;
                    MD2Frame frame3 = frames[subFrame];
                    float[] vertices = new float[(header.numVertices * 6)];
                    int idx19 = 0;
                    int idxV7 = 0;
                    for (int i5 = 0; i5 < header.numVertices; i5++) {
                        int idx20 = idx19 + 1;
                        int idxV8 = idxV7 + 1;
                        vertices[idx19] = frame3.vertices[idxV7];
                        int idx21 = idx20 + 1;
                        int idxV9 = idxV8 + 1;
                        vertices[idx20] = frame3.vertices[idxV8];
                        int idx22 = idx21 + 1;
                        int idxV10 = idxV9 + 1;
                        vertices[idx21] = frame3.vertices[idxV9];
                        int idx23 = idx22 + 1;
                        int idxV11 = idxV10 + 1;
                        vertices[idx22] = frame3.vertices[idxV10];
                        int idx24 = idx23 + 1;
                        int idxV12 = idxV11 + 1;
                        vertices[idx23] = frame3.vertices[idxV11];
                        idx19 = idx24 + 1;
                        idxV7 = idxV12 + 1;
                        vertices[idx24] = frame3.vertices[idxV12];
                    }
                    keyframedAnimation.keyframes[absFrameNum] = new Keyframe(((float) absFrameNum) * frameDuration, vertices);
                    animations.put(keyframedAnimation.name, keyframedAnimation);
                }
                lastName = frames[frameNum].name;
                beginFrame = frameNum;
            }
        }
        KeyframedAnimation animation = new KeyframedAnimation("all", frameDuration, new Keyframe[frames.length]);
        for (int frameNum2 = 0; frameNum2 < frames.length; frameNum2++) {
            MD2Frame frame4 = frames[frameNum2];
            float[] vertices2 = new float[(header.numVertices * 6)];
            int idx25 = 0;
            int idxV13 = 0;
            for (int i6 = 0; i6 < header.numVertices; i6++) {
                int idx26 = idx25 + 1;
                int idxV14 = idxV13 + 1;
                vertices2[idx25] = frame4.vertices[idxV13];
                int idx27 = idx26 + 1;
                int idxV15 = idxV14 + 1;
                vertices2[idx26] = frame4.vertices[idxV14];
                int idx28 = idx27 + 1;
                int idxV16 = idxV15 + 1;
                vertices2[idx27] = frame4.vertices[idxV15];
                int idx29 = idx28 + 1;
                int idxV17 = idxV16 + 1;
                vertices2[idx28] = frame4.vertices[idxV16];
                int idx30 = idx29 + 1;
                int idxV18 = idxV17 + 1;
                vertices2[idx29] = frame4.vertices[idxV17];
                idx25 = idx30 + 1;
                idxV13 = idxV18 + 1;
                vertices2[idx30] = frame4.vertices[idxV18];
            }
            animation.keyframes[frameNum2] = new Keyframe(((float) frameNum2) * frameDuration, vertices2);
        }
        Mesh mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, header.numVertices, indices.length, new VertexAttribute(0, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(2, 3, ShaderProgram.NORMAL_ATTRIBUTE), new VertexAttribute(3, 2, "a_texCoord0"));
        mesh.setIndices(indices);
        animations.put("all", animation);
        KeyframedModel keyframedModel = new KeyframedModel(new KeyframedSubMesh[]{new KeyframedSubMesh("md2-mesh", mesh, blendedVertices, animations, 6, 4)});
        keyframedModel.setAnimation("all", 0.0f, false);
        return keyframedModel;
    }

    private float[] buildTexCoords(MD2Header header, MD2Triangle[] triangles, float[] texCoords) {
        float[] uvs = new float[(header.numVertices * 2)];
        for (MD2Triangle triangle : triangles) {
            for (int j = 0; j < 3; j++) {
                int uvIdx = triangle.vertices[j] * 2;
                uvs[uvIdx] = texCoords[triangle.texCoords[j] * 2];
                uvs[uvIdx + 1] = texCoords[(triangle.texCoords[j] * 2) + 1];
            }
        }
        return uvs;
    }

    private short[] buildIndices(MD2Triangle[] triangles) {
        short[] indices = new short[(triangles.length * 3)];
        int idx = 0;
        int i = 0;
        while (i < triangles.length) {
            MD2Triangle triangle = triangles[i];
            int idx2 = idx + 1;
            indices[idx] = triangle.vertices[0];
            int idx3 = idx2 + 1;
            indices[idx2] = triangle.vertices[1];
            indices[idx3] = triangle.vertices[2];
            i++;
            idx = idx3 + 1;
        }
        return indices;
    }

    private MD2Frame[] loadFrames(MD2Header header, byte[] bytes) throws IOException {
        LittleEndianInputStream in = new LittleEndianInputStream(new ByteArrayInputStream(bytes));
        in.skip((long) header.offsetFrames);
        MD2Frame[] frames = new MD2Frame[header.numFrames];
        for (int i = 0; i < header.numFrames; i++) {
            frames[i] = loadFrame(header, in);
        }
        in.close();
        return frames;
    }

    private MD2Frame loadFrame(MD2Header header, LittleEndianInputStream in) throws IOException {
        MD2Frame frame = new MD2Frame();
        frame.vertices = new float[(header.numVertices * 3)];
        frame.normalIndices = new int[header.numVertices];
        float scaleX = in.readFloat();
        float scaleY = in.readFloat();
        float scaleZ = in.readFloat();
        float transX = in.readFloat();
        float transY = in.readFloat();
        float transZ = in.readFloat();
        in.read(this.charBuffer);
        int len = 0;
        int i = 0;
        while (true) {
            if (i >= this.charBuffer.length) {
                break;
            } else if (this.charBuffer[i] == 0) {
                len = i;
                break;
            } else {
                i++;
            }
        }
        frame.name = new String(this.charBuffer, 0, len);
        int vertIdx = 0;
        int i2 = 0;
        while (i2 < header.numVertices) {
            float x = (((float) in.read()) * scaleX) + transX;
            float y = (((float) in.read()) * scaleY) + transY;
            float z = (((float) in.read()) * scaleZ) + transZ;
            int vertIdx2 = vertIdx + 1;
            frame.vertices[vertIdx] = y;
            int vertIdx3 = vertIdx2 + 1;
            frame.vertices[vertIdx2] = z;
            frame.vertices[vertIdx3] = x;
            frame.normalIndices[i2] = in.read();
            i2++;
            vertIdx = vertIdx3 + 1;
        }
        return frame;
    }

    private MD2Triangle[] loadTriangles(MD2Header header, byte[] bytes) throws IOException {
        LittleEndianInputStream in = new LittleEndianInputStream(new ByteArrayInputStream(bytes));
        in.skip((long) header.offsetTriangles);
        MD2Triangle[] triangles = new MD2Triangle[header.numTriangles];
        for (int i = 0; i < header.numTriangles; i++) {
            MD2Triangle triangle = new MD2Triangle();
            triangle.vertices[0] = in.readShort();
            triangle.vertices[1] = in.readShort();
            triangle.vertices[2] = in.readShort();
            triangle.texCoords[0] = in.readShort();
            triangle.texCoords[1] = in.readShort();
            triangle.texCoords[2] = in.readShort();
            triangles[i] = triangle;
        }
        in.close();
        return triangles;
    }

    private float[] loadTexCoords(MD2Header header, byte[] bytes) throws IOException {
        LittleEndianInputStream in = new LittleEndianInputStream(new ByteArrayInputStream(bytes));
        in.skip((long) header.offsetTexCoords);
        float[] texCoords = new float[(header.numTexCoords * 2)];
        float width = (float) header.skinWidth;
        float height = (float) header.skinHeight;
        for (int i = 0; i < header.numTexCoords * 2; i += 2) {
            short u = in.readShort();
            short v = in.readShort();
            texCoords[i] = ((float) u) / width;
            texCoords[i + 1] = ((float) v) / height;
        }
        in.close();
        return texCoords;
    }

    private MD2Header loadHeader(byte[] bytes) throws IOException {
        LittleEndianInputStream in = new LittleEndianInputStream(new ByteArrayInputStream(bytes));
        MD2Header header = new MD2Header();
        header.ident = in.readInt();
        header.version = in.readInt();
        header.skinWidth = in.readInt();
        header.skinHeight = in.readInt();
        header.frameSize = in.readInt();
        header.numSkins = in.readInt();
        header.numVertices = in.readInt();
        header.numTexCoords = in.readInt();
        header.numTriangles = in.readInt();
        header.numGLCommands = in.readInt();
        header.numFrames = in.readInt();
        header.offsetSkin = in.readInt();
        header.offsetTexCoords = in.readInt();
        header.offsetTriangles = in.readInt();
        header.offsetFrames = in.readInt();
        header.offsetGLCommands = in.readInt();
        header.offsetEnd = in.readInt();
        in.close();
        return header;
    }

    private byte[] loadBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int readBytes = in.read(buffer);
            if (readBytes > 0) {
                out.write(buffer, 0, readBytes);
            } else {
                out.close();
                return out.toByteArray();
            }
        }
    }

    public class VertexIndices {
        public short nIdx;
        public short tIdx;
        public short vIdx;

        public VertexIndices(short vIdx2, short tIdx2, short nIdx2) {
            this.vIdx = vIdx2;
            this.tIdx = tIdx2;
            this.nIdx = nIdx2;
        }

        public int hashCode() {
            return ((this.tIdx + 31) * 31) + this.vIdx;
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
            VertexIndices other = (VertexIndices) obj;
            if (this.tIdx != other.tIdx) {
                return false;
            }
            if (this.vIdx != other.vIdx) {
                return false;
            }
            return true;
        }
    }

    public static class MD2LoaderHints extends ModelLoaderHints {
        public final float frameDuration;

        public MD2LoaderHints(float frameDuration2) {
            super(false);
            this.frameDuration = frameDuration2;
        }
    }
}
