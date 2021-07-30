package com.badlogic.gdx.graphics.g3d.loaders.wavefront;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.loaders.StillModelLoader;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.FloatArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class ObjLoader implements StillModelLoader {
    final ArrayList<Group> groups = new ArrayList<>(10);
    final FloatArray norms = new FloatArray(300);
    final FloatArray uvs = new FloatArray(200);
    final FloatArray verts = new FloatArray(300);

    public StillModel loadObj(FileHandle file) {
        return loadObj(file, false);
    }

    public StillModel loadObj(FileHandle file, boolean flipV) {
        return loadObj(file, file.parent(), flipV);
    }

    public StillModel loadObj(FileHandle file, FileHandle textureDir, boolean flipV) {
        int i;
        int vi;
        int i2;
        char firstChar;
        MtlLoader mtl = new MtlLoader();
        Group activeGroup = new Group("default");
        this.groups.add(activeGroup);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.read()), G3dConstants.STILL_MODEL);
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] tokens = line.split("\\s+");
                if (!(tokens[0].length() == 0 || (firstChar = tokens[0].toLowerCase().charAt(0)) == '#')) {
                    if (firstChar == 'v') {
                        if (tokens[0].length() == 1) {
                            this.verts.add(Float.parseFloat(tokens[1]));
                            this.verts.add(Float.parseFloat(tokens[2]));
                            this.verts.add(Float.parseFloat(tokens[3]));
                        } else if (tokens[0].charAt(1) == 'n') {
                            this.norms.add(Float.parseFloat(tokens[1]));
                            this.norms.add(Float.parseFloat(tokens[2]));
                            this.norms.add(Float.parseFloat(tokens[3]));
                        } else if (tokens[0].charAt(1) == 't') {
                            this.uvs.add(Float.parseFloat(tokens[1]));
                            this.uvs.add(flipV ? 1.0f - Float.parseFloat(tokens[2]) : Float.parseFloat(tokens[2]));
                        }
                    } else if (firstChar == 'f') {
                        ArrayList<Integer> faces = activeGroup.faces;
                        int i3 = 1;
                        while (i3 < tokens.length - 2) {
                            String[] parts = tokens[1].split("/");
                            faces.add(Integer.valueOf(getIndex(parts[0], this.verts.size)));
                            if (parts.length > 2) {
                                if (i3 == 1) {
                                    activeGroup.hasNorms = true;
                                }
                                faces.add(Integer.valueOf(getIndex(parts[2], this.norms.size)));
                            }
                            if (parts.length > 1 && parts[1].length() > 0) {
                                if (i3 == 1) {
                                    activeGroup.hasUVs = true;
                                }
                                faces.add(Integer.valueOf(getIndex(parts[1], this.uvs.size)));
                            }
                            int i4 = i3 + 1;
                            String[] parts2 = tokens[i4].split("/");
                            faces.add(Integer.valueOf(getIndex(parts2[0], this.verts.size)));
                            if (parts2.length > 2) {
                                faces.add(Integer.valueOf(getIndex(parts2[2], this.norms.size)));
                            }
                            if (parts2.length > 1 && parts2[1].length() > 0) {
                                faces.add(Integer.valueOf(getIndex(parts2[1], this.uvs.size)));
                            }
                            int i5 = i4 + 1;
                            String[] parts3 = tokens[i5].split("/");
                            faces.add(Integer.valueOf(getIndex(parts3[0], this.verts.size)));
                            if (parts3.length > 2) {
                                faces.add(Integer.valueOf(getIndex(parts3[2], this.norms.size)));
                            }
                            if (parts3.length > 1 && parts3[1].length() > 0) {
                                faces.add(Integer.valueOf(getIndex(parts3[1], this.uvs.size)));
                            }
                            activeGroup.numFaces++;
                            i3 = i5 - 1;
                        }
                    } else if (firstChar == 'o' || firstChar == 'g') {
                        if (tokens.length > 1) {
                            activeGroup = setActiveGroup(tokens[1]);
                        } else {
                            activeGroup = setActiveGroup("default");
                        }
                    } else if (tokens[0].equals("mtllib")) {
                        String path = "";
                        if (file.path().contains("/")) {
                            path = file.path().substring(0, file.path().lastIndexOf(47) + 1);
                        }
                        mtl.load(path + tokens[1], textureDir);
                    } else if (tokens[0].equals("usemtl")) {
                        if (tokens.length == 1) {
                            activeGroup.materialName = "default";
                        } else {
                            activeGroup.materialName = tokens[1];
                        }
                    }
                }
            } catch (IOException e) {
                return null;
            }
        }
        bufferedReader.close();
        int i6 = 0;
        while (i6 < this.groups.size()) {
            if (this.groups.get(i6).numFaces < 1) {
                this.groups.remove(i6);
                i6--;
            }
            i6++;
        }
        if (this.groups.size() < 1) {
            return null;
        }
        int numGroups = this.groups.size();
        StillModel stillModel = new StillModel(new StillSubMesh[numGroups]);
        for (int g = 0; g < numGroups; g++) {
            Group group = this.groups.get(g);
            ArrayList<Integer> faces2 = group.faces;
            int numElements = faces2.size();
            int numFaces = group.numFaces;
            boolean hasNorms = group.hasNorms;
            boolean hasUVs = group.hasUVs;
            int i7 = numFaces * 3;
            int i8 = (hasNorms ? 3 : 0) + 3;
            if (hasUVs) {
                i = 2;
            } else {
                i = 0;
            }
            float[] finalVerts = new float[((i + i8) * i7)];
            int i9 = 0;
            int vi2 = 0;
            while (true) {
                int vi3 = vi2;
                int i10 = i9;
                if (i10 >= numElements) {
                    break;
                }
                int i11 = i10 + 1;
                int vertIndex = faces2.get(i10).intValue() * 3;
                int vi4 = vi3 + 1;
                int vertIndex2 = vertIndex + 1;
                finalVerts[vi3] = this.verts.get(vertIndex);
                int vi5 = vi4 + 1;
                finalVerts[vi4] = this.verts.get(vertIndex2);
                int vi6 = vi5 + 1;
                finalVerts[vi5] = this.verts.get(vertIndex2 + 1);
                if (hasNorms) {
                    i2 = i11 + 1;
                    int normIndex = faces2.get(i11).intValue() * 3;
                    int vi7 = vi6 + 1;
                    int normIndex2 = normIndex + 1;
                    finalVerts[vi6] = this.norms.get(normIndex);
                    int vi8 = vi7 + 1;
                    finalVerts[vi7] = this.norms.get(normIndex2);
                    vi = vi8 + 1;
                    finalVerts[vi8] = this.norms.get(normIndex2 + 1);
                } else {
                    vi = vi6;
                    i2 = i11;
                }
                if (hasUVs) {
                    i9 = i2 + 1;
                    int uvIndex = faces2.get(i2).intValue() * 2;
                    int vi9 = vi + 1;
                    finalVerts[vi] = this.uvs.get(uvIndex);
                    finalVerts[vi9] = this.uvs.get(uvIndex + 1);
                    vi2 = vi9 + 1;
                } else {
                    vi2 = vi;
                    i9 = i2;
                }
            }
            int numIndices = numFaces * 3 >= 32767 ? 0 : numFaces * 3;
            short[] finalIndices = new short[numIndices];
            if (numIndices > 0) {
                for (int i12 = 0; i12 < numIndices; i12++) {
                    finalIndices[i12] = (short) i12;
                }
            }
            ArrayList<VertexAttribute> attributes = new ArrayList<>();
            attributes.add(new VertexAttribute(0, 3, ShaderProgram.POSITION_ATTRIBUTE));
            if (hasNorms) {
                attributes.add(new VertexAttribute(2, 3, ShaderProgram.NORMAL_ATTRIBUTE));
            }
            if (hasUVs) {
                attributes.add(new VertexAttribute(3, 2, "a_texCoord0"));
            }
            Mesh mesh = new Mesh(true, numFaces * 3, numIndices, (VertexAttribute[]) attributes.toArray(new VertexAttribute[attributes.size()]));
            mesh.setVertices(finalVerts);
            if (numIndices > 0) {
                mesh.setIndices(finalIndices);
            }
            StillSubMesh stillSubMesh = new StillSubMesh(group.name, mesh, 4);
            stillSubMesh.material = mtl.getMaterial(group.materialName);
            stillModel.subMeshes[g] = stillSubMesh;
        }
        if (this.verts.size > 0) {
            this.verts.clear();
        }
        if (this.norms.size > 0) {
            this.norms.clear();
        }
        if (this.uvs.size > 0) {
            this.uvs.clear();
        }
        if (this.groups.size() <= 0) {
            return stillModel;
        }
        this.groups.clear();
        return stillModel;
    }

    private Group setActiveGroup(String name) {
        Iterator i$ = this.groups.iterator();
        while (i$.hasNext()) {
            Group group = i$.next();
            if (group.name.equals(name)) {
                return group;
            }
        }
        Group group2 = new Group(name);
        this.groups.add(group2);
        return group2;
    }

    private int getIndex(String index, int size) {
        if (index == null || index.length() == 0) {
            return 0;
        }
        int idx = Integer.parseInt(index);
        if (idx < 0) {
            return size + idx;
        }
        return idx - 1;
    }

    private class Group {
        ArrayList<Integer> faces = new ArrayList<>(200);
        boolean hasNorms;
        boolean hasUVs;
        Material mat = new Material("", new MaterialAttribute[0]);
        String materialName = "default";
        final String name;
        int numFaces = 0;

        Group(String name2) {
            this.name = name2;
        }
    }

    public StillModel load(FileHandle handle, ModelLoaderHints hints) {
        return loadObj(handle, hints.flipV);
    }
}
