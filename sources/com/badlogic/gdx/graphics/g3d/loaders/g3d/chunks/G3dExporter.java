package com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import com.badlogic.gdx.graphics.g3d.model.keyframe.Keyframe;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedAnimation;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedSubMesh;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonAnimation;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonJoint;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonKeyframe;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonModel;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonSubMesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class G3dExporter {
    public static void export(StillModel model, FileHandle file) {
        ChunkWriter writer = new ChunkWriter();
        writer.newChunk(1);
        writer.writeByte(0);
        writer.writeByte(1);
        writer.endChunk();
        writer.newChunk(G3dConstants.STILL_MODEL);
        writer.writeInt(model.subMeshes.length);
        for (StillSubMesh mesh : model.subMeshes) {
            writer.newChunk(4352);
            writer.writeString(mesh.name == null ? "" : mesh.name);
            writer.writeInt(mesh.primitiveType);
            writer.newChunk(G3dConstants.VERTEX_ATTRIBUTES);
            writer.writeInt(mesh.mesh.getVertexAttributes().size());
            for (int i = 0; i < mesh.mesh.getVertexAttributes().size(); i++) {
                VertexAttribute attribute = mesh.mesh.getVertexAttributes().get(i);
                writer.newChunk(G3dConstants.VERTEX_ATTRIBUTE);
                writer.writeInt(attribute.usage);
                writer.writeInt(attribute.numComponents);
                writer.writeString(attribute.alias);
                writer.endChunk();
            }
            writer.endChunk();
            writer.newChunk(G3dConstants.VERTEX_LIST);
            float[] vertices = new float[((mesh.mesh.getNumVertices() * mesh.mesh.getVertexSize()) / 4)];
            mesh.mesh.getVertices(vertices);
            writer.writeInt(mesh.mesh.getNumVertices());
            writer.writeFloats(vertices);
            writer.endChunk();
            writer.newChunk(G3dConstants.INDEX_LIST);
            int numShorts = mesh.mesh.getNumIndices();
            short[] indices = new short[numShorts];
            mesh.mesh.getIndices(indices);
            writer.writeInt(numShorts);
            writer.writeShorts(indices);
            writer.endChunk();
            writer.endChunk();
        }
        writer.endChunk();
        OutputStream out = null;
        try {
            out = file.write(false);
            writer.writeToStream(out);
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            throw new GdxRuntimeException("An error occured while exporting the still model, " + e2.getMessage(), e2);
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public static void export(KeyframedModel model, FileHandle file) {
        String str;
        ChunkWriter writer = new ChunkWriter();
        writer.newChunk(1);
        writer.writeByte(0);
        writer.writeByte(1);
        writer.endChunk();
        writer.newChunk(G3dConstants.KEYFRAMED_MODEL);
        writer.writeInt(model.subMeshes.length);
        for (KeyframedSubMesh mesh : model.subMeshes) {
            writer.newChunk(8704);
            if (mesh.name == null) {
                str = "";
            } else {
                str = mesh.name;
            }
            writer.writeString(str);
            writer.writeInt(mesh.primitiveType);
            writer.writeInt(mesh.animatedComponents);
            writer.writeInt(mesh.animations.size);
            writer.newChunk(G3dConstants.VERTEX_ATTRIBUTES);
            writer.writeInt(mesh.mesh.getVertexAttributes().size());
            for (int i = 0; i < mesh.mesh.getVertexAttributes().size(); i++) {
                VertexAttribute attribute = mesh.mesh.getVertexAttributes().get(i);
                writer.newChunk(G3dConstants.VERTEX_ATTRIBUTE);
                writer.writeInt(attribute.usage);
                writer.writeInt(attribute.numComponents);
                writer.writeString(attribute.alias);
                writer.endChunk();
            }
            writer.endChunk();
            writer.newChunk(G3dConstants.VERTEX_LIST);
            float[] vertices = new float[((mesh.mesh.getNumVertices() * mesh.mesh.getVertexSize()) / 4)];
            mesh.mesh.getVertices(vertices);
            writer.writeInt(mesh.mesh.getNumVertices());
            writer.writeFloats(vertices);
            writer.endChunk();
            writer.newChunk(G3dConstants.INDEX_LIST);
            short[] indices = new short[mesh.mesh.getNumIndices()];
            mesh.mesh.getIndices(indices);
            writer.writeInt(mesh.mesh.getNumIndices());
            writer.writeShorts(indices);
            writer.endChunk();
            Iterator<String> it = mesh.animations.keys().iterator();
            while (it.hasNext()) {
                KeyframedAnimation animation = mesh.animations.get(it.next());
                writer.newChunk(8960);
                writer.writeString(animation.name);
                writer.writeFloat(animation.frameDuration);
                writer.writeInt(animation.keyframes.length);
                for (Keyframe keyframe : animation.keyframes) {
                    writer.newChunk(G3dConstants.KEYFRAMED_FRAME);
                    writer.writeFloat(keyframe.timeStamp);
                    writer.writeFloats(keyframe.vertices);
                    writer.endChunk();
                }
                writer.endChunk();
            }
            writer.endChunk();
        }
        writer.endChunk();
        OutputStream out = null;
        try {
            out = file.write(false);
            writer.writeToStream(out);
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            throw new GdxRuntimeException("An error occured while exporting the still model, " + e2.getMessage(), e2);
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public static void export(SkeletonModel model, FileHandle file) {
        String str;
        ChunkWriter writer = new ChunkWriter();
        writer.newChunk(1);
        writer.writeByte(0);
        writer.writeByte(1);
        writer.endChunk();
        writer.newChunk(16384);
        writer.writeInt(model.subMeshes.length);
        for (SkeletonSubMesh mesh : model.subMeshes) {
            writer.newChunk(G3dConstants.SKELETON_SUBMESH);
            if (mesh.name == null) {
                str = "";
            } else {
                str = mesh.name;
            }
            writer.writeString(str);
            writer.writeInt(mesh.primitiveType);
            writer.newChunk(G3dConstants.VERTEX_ATTRIBUTES);
            writer.writeInt(mesh.mesh.getVertexAttributes().size());
            for (int i = 0; i < mesh.mesh.getVertexAttributes().size(); i++) {
                VertexAttribute attribute = mesh.mesh.getVertexAttributes().get(i);
                writer.newChunk(G3dConstants.VERTEX_ATTRIBUTE);
                writer.writeInt(attribute.usage);
                writer.writeInt(attribute.numComponents);
                writer.writeString(attribute.alias);
                writer.endChunk();
            }
            writer.endChunk();
            writer.newChunk(G3dConstants.VERTEX_LIST);
            int numVertices = (mesh.mesh.getNumVertices() * mesh.mesh.getVertexSize()) / 4;
            writer.writeInt(mesh.mesh.getNumVertices());
            writer.writeFloats(mesh.vertices);
            writer.endChunk();
            writer.newChunk(G3dConstants.INDEX_LIST);
            writer.writeInt(mesh.mesh.getNumIndices());
            writer.writeShorts(mesh.indices);
            writer.endChunk();
            writer.newChunk(G3dConstants.BONE_WEIGHTS);
            writer.writeInt(mesh.boneWeights.length);
            for (float[] array : mesh.boneWeights) {
                writer.newChunk(G3dConstants.BONE_WEIGHT);
                writer.writeInt(array.length);
                writer.writeFloats(array);
                writer.endChunk();
            }
            writer.endChunk();
            writer.newChunk(G3dConstants.BONE_ASSIGNMENTS);
            writer.writeInt(mesh.boneAssignments.length);
            for (int[] array2 : mesh.boneAssignments) {
                writer.newChunk(G3dConstants.BONE_ASSIGNMENT);
                writer.writeInt(array2.length);
                writer.writeInts(array2);
                writer.endChunk();
            }
            writer.endChunk();
            writer.endChunk();
        }
        writer.newChunk(G3dConstants.SKELETON);
        writer.newChunk(G3dConstants.SKELETON_HIERARCHY);
        writer.writeInt(model.skeleton.hierarchy.size);
        Iterator i$ = model.skeleton.hierarchy.iterator();
        while (i$.hasNext()) {
            writeSkeletonJoint(writer, i$.next());
        }
        writer.endChunk();
        writer.newChunk(G3dConstants.SKELETON_ANIMATIONS);
        writer.writeInt(model.skeleton.animations.size);
        Iterator i$2 = model.skeleton.animations.keys().iterator();
        while (i$2.hasNext()) {
            String animationName = i$2.next();
            writer.newChunk(G3dConstants.SKELETON_ANIMATION);
            writer.writeString(animationName);
            SkeletonAnimation animation = model.skeleton.animations.get(animationName);
            writer.writeFloat(animation.totalDuration);
            writer.writeInt(animation.perJointkeyFrames.length);
            for (SkeletonKeyframe[] array3 : animation.perJointkeyFrames) {
                writer.writeInt(array3.length);
                for (SkeletonKeyframe frame : array3) {
                    writer.writeFloat(frame.timeStamp);
                    writer.writeInt(frame.parentIndex);
                    writer.writeFloat(frame.position.f170x);
                    writer.writeFloat(frame.position.f171y);
                    writer.writeFloat(frame.position.f172z);
                    writer.writeFloat(frame.rotation.f157w);
                    writer.writeFloat(frame.rotation.f158x);
                    writer.writeFloat(frame.rotation.f159y);
                    writer.writeFloat(frame.rotation.f160z);
                    writer.writeFloat(frame.scale.f170x);
                    writer.writeFloat(frame.scale.f171y);
                    writer.writeFloat(frame.scale.f172z);
                }
            }
            writer.endChunk();
        }
        writer.endChunk();
        writer.endChunk();
        writer.endChunk();
        OutputStream out = null;
        try {
            out = file.write(false);
            writer.writeToStream(out);
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            throw new GdxRuntimeException("An error occured while exporting the still model, " + e2.getMessage(), e2);
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    private static void writeSkeletonJoint(ChunkWriter writer, SkeletonJoint joint) {
        writer.writeString(joint.name);
        writer.writeFloat(joint.position.f170x);
        writer.writeFloat(joint.position.f171y);
        writer.writeFloat(joint.position.f172z);
        writer.writeFloat(joint.rotation.f157w);
        writer.writeFloat(joint.rotation.f158x);
        writer.writeFloat(joint.rotation.f159y);
        writer.writeFloat(joint.rotation.f160z);
        writer.writeFloat(joint.scale.f170x);
        writer.writeFloat(joint.scale.f171y);
        writer.writeFloat(joint.scale.f172z);
        writer.writeInt(joint.children.size);
        Iterator i$ = joint.children.iterator();
        while (i$.hasNext()) {
            writeSkeletonJoint(writer, i$.next());
        }
    }
}
