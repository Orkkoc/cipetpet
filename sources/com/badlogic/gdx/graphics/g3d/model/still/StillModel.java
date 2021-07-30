package com.badlogic.gdx.graphics.g3d.model.still;

import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.Model;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.collision.BoundingBox;
import java.util.ArrayList;

public class StillModel implements Model {
    private static final BoundingBox tmpBox = new BoundingBox();
    public final StillSubMesh[] subMeshes;

    public StillModel(SubMesh... subMeshes2) {
        this.subMeshes = new StillSubMesh[subMeshes2.length];
        for (int i = 0; i < subMeshes2.length; i++) {
            this.subMeshes[i] = subMeshes2[i];
        }
    }

    public void render() {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            StillSubMesh subMesh = this.subMeshes[i];
            if (i == 0) {
                subMesh.material.bind();
            } else if (!this.subMeshes[i - 1].material.equals(subMesh.material)) {
                subMesh.material.bind();
            }
            subMesh.mesh.render(subMesh.primitiveType);
        }
    }

    public void render(ShaderProgram program) {
        int len = this.subMeshes.length;
        for (int i = 0; i < len; i++) {
            StillSubMesh subMesh = this.subMeshes[i];
            if (i == 0) {
                subMesh.material.bind(program);
            } else if (!this.subMeshes[i - 1].material.equals(subMesh.material)) {
                subMesh.material.bind(program);
            }
            subMesh.mesh.render(program, subMesh.primitiveType);
        }
    }

    public Model getSubModel(String... subMeshNames) {
        ArrayList<SubMesh> subMeshes2 = new ArrayList<>();
        for (String name : subMeshNames) {
            for (StillSubMesh subMesh : this.subMeshes) {
                if (name.equals(subMesh.name)) {
                    subMeshes2.add(subMesh);
                }
            }
        }
        if (subMeshes2.size() > 0) {
            return new StillModel((SubMesh[]) subMeshes2.toArray(new StillSubMesh[subMeshes2.size()]));
        }
        return null;
    }

    public StillSubMesh getSubMesh(String name) {
        for (StillSubMesh subMesh : this.subMeshes) {
            if (subMesh.name.equals(name)) {
                return subMesh;
            }
        }
        return null;
    }

    public SubMesh[] getSubMeshes() {
        return this.subMeshes;
    }

    public void setMaterials(Material... materials) {
        if (materials.length != this.subMeshes.length) {
            throw new UnsupportedOperationException("number of materials must equal number of sub-meshes");
        }
        int len = materials.length;
        for (int i = 0; i < len; i++) {
            this.subMeshes[i].material = materials[i];
        }
    }

    public void setMaterial(Material material) {
        for (StillSubMesh stillSubMesh : this.subMeshes) {
            stillSubMesh.material = material;
        }
    }

    public void getBoundingBox(BoundingBox bbox) {
        bbox.inf();
        for (StillSubMesh stillSubMesh : this.subMeshes) {
            stillSubMesh.mesh.calculateBoundingBox(tmpBox);
            bbox.ext(tmpBox);
        }
    }

    public void dispose() {
        for (StillSubMesh stillSubMesh : this.subMeshes) {
            stillSubMesh.mesh.dispose();
        }
    }
}
