package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Comparator;
import java.util.Iterator;

public class CameraGroupStrategy implements GroupStrategy, Disposable {
    private static final int GROUP_BLEND = 1;
    private static final int GROUP_OPAQUE = 0;
    Pool<Array<Decal>> arrayPool;
    Camera camera;
    private final Comparator<Decal> cameraSorter;
    ObjectMap<DecalMaterial, Array<Decal>> materialGroups;
    ShaderProgram shader;
    Array<Array<Decal>> usedArrays;

    public CameraGroupStrategy(final Camera camera2) {
        this(camera2, new Comparator<Decal>() {
            public int compare(Decal o1, Decal o2) {
                return (int) Math.signum(Camera.this.position.dst(o2.position) - Camera.this.position.dst(o1.position));
            }
        });
    }

    public CameraGroupStrategy(Camera camera2, Comparator<Decal> sorter) {
        this.arrayPool = new Pool<Array<Decal>>(16) {
            /* access modifiers changed from: protected */
            public Array<Decal> newObject() {
                return new Array<>();
            }
        };
        this.usedArrays = new Array<>();
        this.materialGroups = new ObjectMap<>();
        this.camera = camera2;
        this.cameraSorter = sorter;
        createDefaultShader();
    }

    public void setCamera(Camera camera2) {
        this.camera = camera2;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public int decideGroup(Decal decal) {
        return decal.getMaterial().isOpaque() ? 0 : 1;
    }

    public void beforeGroup(int group, Array<Decal> contents) {
        if (group == 1) {
            Gdx.f12gl.glEnable(3042);
            contents.sort(this.cameraSorter);
            return;
        }
        int n = contents.size;
        for (int i = 0; i < n; i++) {
            Decal decal = contents.get(i);
            Array<Decal> materialGroup = this.materialGroups.get(decal.material);
            if (materialGroup == null) {
                materialGroup = this.arrayPool.obtain();
                materialGroup.clear();
                this.usedArrays.add(materialGroup);
                this.materialGroups.put(decal.material, materialGroup);
            }
            materialGroup.add(decal);
        }
        contents.clear();
        Iterator i$ = this.materialGroups.values().iterator();
        while (i$.hasNext()) {
            contents.addAll((Array) i$.next());
        }
        this.materialGroups.clear();
        this.arrayPool.freeAll(this.usedArrays);
        this.usedArrays.clear();
    }

    public void afterGroup(int group) {
        if (group == 1) {
            Gdx.f12gl.glDisable(3042);
        }
    }

    public void beforeGroups() {
        Gdx.f12gl.glEnable(2929);
        if (this.shader != null) {
            this.shader.begin();
            this.shader.setUniformMatrix("u_projectionViewMatrix", this.camera.combined);
            this.shader.setUniformi("u_texture", 0);
            return;
        }
        Gdx.f12gl.glEnable(3553);
        Gdx.gl10.glMatrixMode(GL10.GL_PROJECTION);
        Gdx.gl10.glLoadMatrixf(this.camera.projection.val, 0);
        Gdx.gl10.glMatrixMode(GL10.GL_MODELVIEW);
        Gdx.gl10.glLoadMatrixf(this.camera.view.val, 0);
    }

    public void afterGroups() {
        if (this.shader != null) {
            this.shader.end();
        }
        Gdx.f12gl.glDisable(3553);
        Gdx.f12gl.glDisable(2929);
    }

    private void createDefaultShader() {
        if (Gdx.graphics.isGL20Available()) {
            this.shader = new ShaderProgram("attribute vec4 a_position;\nattribute vec4 a_color;\nattribute vec2 a_texCoord0;\nuniform mat4 u_projectionViewMatrix;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\n\nvoid main()\n{\n   v_color = a_color;\n   v_texCoords = a_texCoord0;\n   gl_Position =  u_projectionViewMatrix * a_position;\n}\n", "#ifdef GL_ES\nprecision mediump float;\n#endif\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nuniform sampler2D u_texture;\nvoid main()\n{\n  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n}");
            if (!this.shader.isCompiled()) {
                throw new IllegalArgumentException("couldn't compile shader: " + this.shader.getLog());
            }
        }
    }

    public ShaderProgram getGroupShader(int group) {
        return this.shader;
    }

    public void dispose() {
        if (this.shader != null) {
            this.shader.dispose();
        }
    }
}
