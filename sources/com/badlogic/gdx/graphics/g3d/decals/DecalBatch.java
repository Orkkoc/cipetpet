package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SortedIntList;
import java.util.Iterator;

public class DecalBatch implements Disposable {
    private static final int DEFAULT_SIZE = 1000;
    private final SortedIntList<Array<Decal>> groupList;
    private final Pool<Array<Decal>> groupPool;
    private GroupStrategy groupStrategy;
    private Mesh mesh;
    private final Array<Array<Decal>> usedGroups;
    private float[] vertices;

    public DecalBatch() {
        this(DEFAULT_SIZE, new DefaultGroupStrategy());
    }

    public DecalBatch(GroupStrategy groupStrategy2) {
        this(DEFAULT_SIZE, groupStrategy2);
    }

    public DecalBatch(int size, GroupStrategy groupStrategy2) {
        this.groupList = new SortedIntList<>();
        this.groupPool = new Pool<Array<Decal>>(16) {
            /* access modifiers changed from: protected */
            public Array<Decal> newObject() {
                return new Array<>(false, 100);
            }
        };
        this.usedGroups = new Array<>(16);
        initialize(size);
        setGroupStrategy(groupStrategy2);
    }

    public void setGroupStrategy(GroupStrategy groupStrategy2) {
        this.groupStrategy = groupStrategy2;
    }

    public void initialize(int size) {
        this.vertices = new float[(size * 24)];
        this.mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6, new VertexAttribute(0, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(5, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(3, 2, "a_texCoord0"));
        short[] indices = new short[(size * 6)];
        int v = 0;
        int i = 0;
        while (i < indices.length) {
            indices[i] = (short) v;
            indices[i + 1] = (short) (v + 2);
            indices[i + 2] = (short) (v + 1);
            indices[i + 3] = (short) (v + 1);
            indices[i + 4] = (short) (v + 2);
            indices[i + 5] = (short) (v + 3);
            i += 6;
            v += 4;
        }
        this.mesh.setIndices(indices);
    }

    public int getSize() {
        return this.vertices.length / 24;
    }

    public void add(Decal decal) {
        DecalMaterial material = decal.getMaterial();
        int groupIndex = this.groupStrategy.decideGroup(decal);
        Array<Decal> targetGroup = this.groupList.get(groupIndex);
        if (targetGroup == null) {
            targetGroup = this.groupPool.obtain();
            targetGroup.clear();
            this.usedGroups.add(targetGroup);
            this.groupList.insert(groupIndex, targetGroup);
        }
        targetGroup.add(decal);
    }

    public void flush() {
        render();
        clear();
    }

    /* access modifiers changed from: protected */
    public void render() {
        this.groupStrategy.beforeGroups();
        Iterator i$ = this.groupList.iterator();
        while (i$.hasNext()) {
            SortedIntList.Node<Array<Decal>> group = i$.next();
            this.groupStrategy.beforeGroup(group.index, (Array) group.value);
            render(this.groupStrategy.getGroupShader(group.index), (Array) group.value);
            this.groupStrategy.afterGroup(group.index);
        }
        this.groupStrategy.afterGroups();
    }

    private void render(ShaderProgram shader, Array<Decal> decals) {
        DecalMaterial lastMaterial = null;
        int idx = 0;
        Iterator i$ = decals.iterator();
        while (i$.hasNext()) {
            Decal decal = i$.next();
            if (lastMaterial == null || !lastMaterial.equals(decal.getMaterial())) {
                if (idx > 0) {
                    flush(shader, idx);
                    idx = 0;
                }
                decal.material.set();
                lastMaterial = decal.material;
            }
            decal.update();
            System.arraycopy(decal.vertices, 0, this.vertices, idx, decal.vertices.length);
            idx += decal.vertices.length;
            if (idx == this.vertices.length) {
                flush(shader, idx);
                idx = 0;
            }
        }
        if (idx > 0) {
            flush(shader, idx);
        }
    }

    /* access modifiers changed from: protected */
    public void flush(ShaderProgram shader, int verticesPosition) {
        this.mesh.setVertices(this.vertices, 0, verticesPosition);
        if (shader != null) {
            this.mesh.render(shader, 4, 0, verticesPosition / 4);
        } else {
            this.mesh.render(4, 0, verticesPosition / 4);
        }
    }

    /* access modifiers changed from: protected */
    public void clear() {
        this.groupList.clear();
        this.groupPool.freeAll(this.usedGroups);
        this.usedGroups.clear();
    }

    public void dispose() {
        clear();
        this.vertices = null;
        this.mesh.dispose();
    }
}
