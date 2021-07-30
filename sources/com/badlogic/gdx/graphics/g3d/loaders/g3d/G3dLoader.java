package com.badlogic.gdx.graphics.g3d.loaders.g3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.KeyframedModelLoader;
import com.badlogic.gdx.graphics.g3d.loaders.SkeletonModelLoader;
import com.badlogic.gdx.graphics.g3d.loaders.StillModelLoader;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.chunks.ChunkReader;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute;
import com.badlogic.gdx.graphics.g3d.model.keyframe.Keyframe;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedAnimation;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedSubMesh;
import com.badlogic.gdx.graphics.g3d.model.skeleton.Skeleton;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonAnimation;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonJoint;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonKeyframe;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonModel;
import com.badlogic.gdx.graphics.g3d.model.skeleton.SkeletonSubMesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.IOException;
import java.io.InputStream;

public class G3dLoader {
    public static StillModel loadStillModel(FileHandle handle) {
        InputStream in = null;
        try {
            in = handle.read();
            ChunkReader.Chunk root = ChunkReader.readChunks(in);
            if (root.getId() != 1194542106) {
                throw new GdxRuntimeException("Invalid root tag id: " + root.getId());
            }
            ChunkReader.Chunk version = root.getChild(1);
            if (version == null) {
                throw new GdxRuntimeException("No version chunk found");
            }
            int major = version.readByte();
            int minor = version.readByte();
            if (major == 0 && minor == 1) {
                ChunkReader.Chunk stillModel = root.getChild(G3dConstants.STILL_MODEL);
                if (stillModel == null) {
                    throw new GdxRuntimeException("No stillmodel chunk found");
                }
                int numSubMeshes = stillModel.readInt();
                StillSubMesh[] meshes = new StillSubMesh[numSubMeshes];
                ChunkReader.Chunk[] meshChunks = stillModel.getChildren(4352);
                if (meshChunks.length != numSubMeshes) {
                    throw new GdxRuntimeException("Number of submeshes not equal to number specified in still model chunk, expected " + numSubMeshes + ", got " + meshChunks.length);
                }
                for (int i = 0; i < numSubMeshes; i++) {
                    ChunkReader.Chunk subMesh = meshChunks[i];
                    String name = subMesh.readString();
                    int primitiveType = subMesh.readInt();
                    ChunkReader.Chunk attributes = subMesh.getChild(G3dConstants.VERTEX_ATTRIBUTES);
                    if (attributes == null) {
                        throw new GdxRuntimeException("No vertex attribute chunk given");
                    }
                    int numAttributes = attributes.readInt();
                    ChunkReader.Chunk[] attributeChunks = attributes.getChildren(G3dConstants.VERTEX_ATTRIBUTE);
                    if (attributeChunks.length != numAttributes) {
                        new GdxRuntimeException("Number of attributes not equal to number specified in attributes chunk, expected " + numAttributes + ", got " + attributeChunks.length);
                    }
                    VertexAttribute[] vertAttribs = new VertexAttribute[numAttributes];
                    for (int j = 0; j < numAttributes; j++) {
                        vertAttribs[j] = new VertexAttribute(attributeChunks[j].readInt(), attributeChunks[j].readInt(), attributeChunks[j].readString());
                    }
                    ChunkReader.Chunk vertices = subMesh.getChild(G3dConstants.VERTEX_LIST);
                    int numVertices = vertices.readInt();
                    float[] vertexData = vertices.readFloats();
                    ChunkReader.Chunk indices = subMesh.getChild(G3dConstants.INDEX_LIST);
                    int numIndices = indices.readInt();
                    short[] indexData = indices.readShorts();
                    StillSubMesh mesh = new StillSubMesh(name, new Mesh(true, numVertices, numIndices, vertAttribs), primitiveType);
                    mesh.mesh.setVertices(vertexData);
                    mesh.mesh.setIndices(indexData);
                    mesh.material = new Material("default", new MaterialAttribute[0]);
                    meshes[i] = mesh;
                }
                StillModel stillModel2 = new StillModel(meshes);
                stillModel2.setMaterial(new Material("default", new MaterialAttribute[0]));
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                return stillModel2;
            }
            throw new GdxRuntimeException("Invalid version, required 0.1, got " + major + "." + minor);
        } catch (IOException e2) {
            throw new GdxRuntimeException("Couldn't load still model from '" + handle.name() + "', " + e2.getMessage(), e2);
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public static KeyframedModel loadKeyframedModel(FileHandle handle) {
        InputStream in = null;
        try {
            in = handle.read();
            ChunkReader.Chunk root = ChunkReader.readChunks(in);
            if (root.getId() != 1194542106) {
                throw new GdxRuntimeException("Invalid root tag id: " + root.getId());
            }
            ChunkReader.Chunk version = root.getChild(1);
            if (version == null) {
                throw new GdxRuntimeException("No version chunk found");
            }
            int major = version.readByte();
            int minor = version.readByte();
            if (major == 0 && minor == 1) {
                ChunkReader.Chunk stillModel = root.getChild(G3dConstants.KEYFRAMED_MODEL);
                if (stillModel == null) {
                    throw new GdxRuntimeException("No stillmodel chunk found");
                }
                int numSubMeshes = stillModel.readInt();
                KeyframedSubMesh[] meshes = new KeyframedSubMesh[numSubMeshes];
                ChunkReader.Chunk[] meshChunks = stillModel.getChildren(8704);
                if (meshChunks.length != numSubMeshes) {
                    throw new GdxRuntimeException("Number of submeshes not equal to number specified in still model chunk, expected " + numSubMeshes + ", got " + meshChunks.length);
                }
                for (int i = 0; i < numSubMeshes; i++) {
                    ChunkReader.Chunk subMesh = meshChunks[i];
                    String meshName = subMesh.readString();
                    int primitiveType = subMesh.readInt();
                    int animatedComponents = subMesh.readInt();
                    int numAnimations = subMesh.readInt();
                    ChunkReader.Chunk attributes = subMesh.getChild(G3dConstants.VERTEX_ATTRIBUTES);
                    if (attributes == null) {
                        throw new GdxRuntimeException("No vertex attribute chunk given");
                    }
                    int numAttributes = attributes.readInt();
                    ChunkReader.Chunk[] attributeChunks = attributes.getChildren(G3dConstants.VERTEX_ATTRIBUTE);
                    if (attributeChunks.length != numAttributes) {
                        new GdxRuntimeException("Number of attributes not equal to number specified in attributes chunk, expected " + numAttributes + ", got " + attributeChunks.length);
                    }
                    VertexAttribute[] vertAttribs = new VertexAttribute[numAttributes];
                    for (int j = 0; j < numAttributes; j++) {
                        vertAttribs[j] = new VertexAttribute(attributeChunks[j].readInt(), attributeChunks[j].readInt(), attributeChunks[j].readString());
                    }
                    ChunkReader.Chunk vertices = subMesh.getChild(G3dConstants.VERTEX_LIST);
                    int numVertices = vertices.readInt();
                    float[] vertexData = vertices.readFloats();
                    ChunkReader.Chunk indices = subMesh.getChild(G3dConstants.INDEX_LIST);
                    int numIndices = indices.readInt();
                    short[] indexData = indices.readShorts();
                    ObjectMap<String, KeyframedAnimation> animations = new ObjectMap<>();
                    ChunkReader.Chunk[] animationChunks = subMesh.getChildren(8960);
                    if (numAnimations != animationChunks.length) {
                        throw new GdxRuntimeException("number of keyframed animations not equal to number specified in keyframed submesh chunk, was " + animationChunks.length + ", expected " + numAnimations);
                    }
                    for (int j2 = 0; j2 < numAnimations; j2++) {
                        ChunkReader.Chunk animationChunk = animationChunks[j2];
                        String animationName = animationChunk.readString();
                        float frameDuration = animationChunk.readFloat();
                        int numKeyframes = animationChunk.readInt();
                        Keyframe[] keyframes = new Keyframe[numKeyframes];
                        ChunkReader.Chunk[] keyframeChunks = animationChunk.getChildren(G3dConstants.KEYFRAMED_FRAME);
                        if (numKeyframes != keyframeChunks.length) {
                            throw new GdxRuntimeException("number of keyframes not equal to number specified in keyframed animation, was " + keyframeChunks.length + ", expected " + numKeyframes);
                        }
                        for (int k = 0; k < numKeyframes; k++) {
                            ChunkReader.Chunk keyframeChunk = keyframeChunks[k];
                            keyframes[k] = new Keyframe(keyframeChunk.readFloat(), keyframeChunk.readFloats());
                        }
                        animations.put(animationName, new KeyframedAnimation(animationName, frameDuration, keyframes));
                    }
                    Mesh mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, numVertices, numIndices, vertAttribs);
                    meshes[i] = new KeyframedSubMesh(meshName, mesh, vertexData, animations, animatedComponents, primitiveType);
                    mesh.setVertices(vertexData);
                    mesh.setIndices(indexData);
                }
                KeyframedModel keyframedModel = new KeyframedModel(meshes);
                keyframedModel.setMaterial(new Material("default", new MaterialAttribute[0]));
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                return keyframedModel;
            }
            throw new GdxRuntimeException("Invalid version, required 0.1, got " + major + "." + minor);
        } catch (IOException e2) {
            throw new GdxRuntimeException("Couldn't load still model from '" + handle.name() + "', " + e2.getMessage(), e2);
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    public static SkeletonModel loadSkeletonModel(FileHandle handle) {
        InputStream in = null;
        try {
            in = handle.read();
            ChunkReader.Chunk root = ChunkReader.readChunks(in);
            if (root.getId() != 1194542106) {
                throw new GdxRuntimeException("Invalid root tag id: " + root.getId());
            }
            ChunkReader.Chunk version = root.getChild(1);
            if (version == null) {
                throw new GdxRuntimeException("No version chunk found");
            }
            int major = version.readByte();
            int minor = version.readByte();
            if (major == 0 && minor == 1) {
                ChunkReader.Chunk skeletonModel = root.getChild(16384);
                if (skeletonModel == null) {
                    throw new GdxRuntimeException("No skeletonModel chunk found");
                }
                int numSubMeshes = skeletonModel.readInt();
                SkeletonSubMesh[] meshes = new SkeletonSubMesh[numSubMeshes];
                ChunkReader.Chunk[] meshChunks = skeletonModel.getChildren(G3dConstants.SKELETON_SUBMESH);
                if (meshChunks.length != numSubMeshes) {
                    throw new GdxRuntimeException("Number of submeshes not equal to number specified in still model chunk, expected " + numSubMeshes + ", got " + meshChunks.length);
                }
                for (int i = 0; i < numSubMeshes; i++) {
                    ChunkReader.Chunk subMeshChunk = meshChunks[i];
                    ChunkReader.Chunk attributes = subMeshChunk.getChild(G3dConstants.VERTEX_ATTRIBUTES);
                    if (attributes == null) {
                        throw new GdxRuntimeException("No vertex attribute chunk given");
                    }
                    int numAttributes = attributes.readInt();
                    ChunkReader.Chunk[] attributeChunks = attributes.getChildren(G3dConstants.VERTEX_ATTRIBUTE);
                    if (attributeChunks.length != numAttributes) {
                        new GdxRuntimeException("Number of attributes not equal to number specified in attributes chunk, expected " + numAttributes + ", got " + attributeChunks.length);
                    }
                    VertexAttribute[] vertAttribs = new VertexAttribute[numAttributes];
                    for (int j = 0; j < numAttributes; j++) {
                        vertAttribs[j] = new VertexAttribute(attributeChunks[j].readInt(), attributeChunks[j].readInt(), attributeChunks[j].readString());
                    }
                    ChunkReader.Chunk vertices = subMeshChunk.getChild(G3dConstants.VERTEX_LIST);
                    int numVertices = vertices.readInt();
                    float[] meshVertices = vertices.readFloats();
                    ChunkReader.Chunk indices = subMeshChunk.getChild(G3dConstants.INDEX_LIST);
                    int numIndices = indices.readInt();
                    short[] meshIndices = indices.readShorts();
                    ChunkReader.Chunk boneWeights = subMeshChunk.getChild(G3dConstants.BONE_WEIGHTS);
                    int numBonesWeights = boneWeights.readInt();
                    ChunkReader.Chunk[] boneWeightChunks = boneWeights.getChildren(G3dConstants.BONE_WEIGHT);
                    if (attributeChunks.length != numAttributes) {
                        new GdxRuntimeException("Number of bone weights not equal to number specified in bone weights chunk, expected " + numBonesWeights + ", got " + boneWeightChunks.length);
                    }
                    float[][] meshBoneWeights = new float[numBonesWeights][];
                    for (int j2 = 0; j2 < numBonesWeights; j2++) {
                        int readInt = boneWeightChunks[j2].readInt();
                        meshBoneWeights[j2] = boneWeightChunks[j2].readFloats();
                    }
                    ChunkReader.Chunk boneAssignments = subMeshChunk.getChild(G3dConstants.BONE_ASSIGNMENTS);
                    int numBoneAssignments = boneAssignments.readInt();
                    ChunkReader.Chunk[] boneAssignmentChunks = boneAssignments.getChildren(G3dConstants.BONE_ASSIGNMENT);
                    if (boneAssignmentChunks.length != numBoneAssignments) {
                        new GdxRuntimeException("Number of bone assignment not equal to number specified in bone assignment chunk, expected " + numBoneAssignments + ", got " + boneAssignmentChunks.length);
                    }
                    int[][] meshBoneAssignments = new int[numBoneAssignments][];
                    for (int j3 = 0; j3 < numBoneAssignments; j3++) {
                        int readInt2 = boneAssignmentChunks[j3].readInt();
                        meshBoneAssignments[j3] = boneAssignmentChunks[j3].readInts();
                    }
                    SkeletonSubMesh skeletonSubMesh = new SkeletonSubMesh(subMeshChunk.readString(), new Mesh(false, numVertices, numIndices, vertAttribs), subMeshChunk.readInt());
                    skeletonSubMesh.indices = meshIndices;
                    skeletonSubMesh.boneAssignments = meshBoneAssignments;
                    skeletonSubMesh.boneWeights = meshBoneWeights;
                    skeletonSubMesh.vertices = meshVertices;
                    skeletonSubMesh.mesh.setVertices(skeletonSubMesh.vertices);
                    skeletonSubMesh.mesh.setIndices(skeletonSubMesh.indices);
                    skeletonSubMesh.skinnedVertices = new float[skeletonSubMesh.vertices.length];
                    System.arraycopy(skeletonSubMesh.vertices, 0, skeletonSubMesh.skinnedVertices, 0, skeletonSubMesh.vertices.length);
                    meshes[i] = skeletonSubMesh;
                }
                Skeleton skeleton = new Skeleton();
                ChunkReader.Chunk skeletonChunk = skeletonModel.getChild(G3dConstants.SKELETON);
                ChunkReader.Chunk hierarchy = skeletonChunk.getChild(G3dConstants.SKELETON_HIERARCHY);
                int numHierarchyJoints = hierarchy.readInt();
                for (int i2 = 0; i2 < numHierarchyJoints; i2++) {
                    skeleton.hierarchy.add(readSkeletonJoint(hierarchy));
                }
                ChunkReader.Chunk animations = skeletonChunk.getChild(G3dConstants.SKELETON_ANIMATIONS);
                int numAnimations = animations.readInt();
                ChunkReader.Chunk[] animationChunks = animations.getChildren(G3dConstants.SKELETON_ANIMATION);
                if (animationChunks.length != numAnimations) {
                    new GdxRuntimeException("Number of animations not equal to number specified in animations chunk, expected " + numAnimations + ", got " + animationChunks.length);
                }
                for (int i3 = 0; i3 < numAnimations; i3++) {
                    ChunkReader.Chunk animation = animationChunks[i3];
                    String name = animation.readString();
                    float totalDuration = animation.readFloat();
                    int numJoints = animation.readInt();
                    SkeletonKeyframe[][] perJointKeyFrames = new SkeletonKeyframe[numJoints][];
                    for (int j4 = 0; j4 < numJoints; j4++) {
                        int numFrames = animation.readInt();
                        perJointKeyFrames[j4] = new SkeletonKeyframe[numFrames];
                        for (int k = 0; k < numFrames; k++) {
                            SkeletonKeyframe frame = new SkeletonKeyframe();
                            frame.timeStamp = animation.readFloat();
                            frame.parentIndex = animation.readInt();
                            frame.position.f170x = animation.readFloat();
                            frame.position.f171y = animation.readFloat();
                            frame.position.f172z = animation.readFloat();
                            frame.rotation.f157w = animation.readFloat();
                            frame.rotation.f158x = animation.readFloat();
                            frame.rotation.f159y = animation.readFloat();
                            frame.rotation.f160z = animation.readFloat();
                            frame.scale.f170x = animation.readFloat();
                            frame.scale.f171y = animation.readFloat();
                            frame.scale.f172z = animation.readFloat();
                            perJointKeyFrames[j4][k] = frame;
                        }
                    }
                    skeleton.animations.put(name, new SkeletonAnimation(name, totalDuration, perJointKeyFrames));
                }
                skeleton.buildFromHierarchy();
                SkeletonModel skeletonModel2 = new SkeletonModel(skeleton, meshes);
                skeletonModel2.setMaterial(new Material("default", new MaterialAttribute[0]));
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                return skeletonModel2;
            }
            throw new GdxRuntimeException("Invalid version, required 0.1, got " + major + "." + minor);
        } catch (IOException e2) {
            throw new GdxRuntimeException("Couldn't load skeleton model from '" + handle.name() + "', " + e2.getMessage(), e2);
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    private static SkeletonJoint readSkeletonJoint(ChunkReader.Chunk jointChunk) {
        SkeletonJoint joint = new SkeletonJoint();
        joint.name = jointChunk.readString();
        joint.position.f170x = jointChunk.readFloat();
        joint.position.f171y = jointChunk.readFloat();
        joint.position.f172z = jointChunk.readFloat();
        joint.rotation.f157w = jointChunk.readFloat();
        joint.rotation.f158x = jointChunk.readFloat();
        joint.rotation.f159y = jointChunk.readFloat();
        joint.rotation.f160z = jointChunk.readFloat();
        joint.scale.f170x = jointChunk.readFloat();
        joint.scale.f171y = jointChunk.readFloat();
        joint.scale.f172z = jointChunk.readFloat();
        int count = jointChunk.readInt();
        for (int i = 0; i < count; i++) {
            SkeletonJoint child = readSkeletonJoint(jointChunk);
            child.parent = joint;
            joint.children.add(child);
        }
        return joint;
    }

    public static class G3dStillModelLoader implements StillModelLoader {
        public StillModel load(FileHandle handle, ModelLoaderHints hints) {
            return G3dLoader.loadStillModel(handle);
        }
    }

    public static class G3dKeyframedModelLoader implements KeyframedModelLoader {
        public KeyframedModel load(FileHandle handle, ModelLoaderHints hints) {
            return G3dLoader.loadKeyframedModel(handle);
        }
    }

    public static class G3dSkeletonModelLoader implements SkeletonModelLoader {
        public SkeletonModel load(FileHandle handle, ModelLoaderHints hints) {
            return G3dLoader.loadSkeletonModel(handle);
        }
    }
}
