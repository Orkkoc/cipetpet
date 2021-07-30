package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public abstract class Camera {
    public final Matrix4 combined = new Matrix4();
    public final Vector3 direction = new Vector3(0.0f, 0.0f, -1.0f);
    public float far = 100.0f;
    public final Frustum frustum = new Frustum();
    public final Matrix4 invProjectionView = new Matrix4();
    public float near = 1.0f;
    public final Vector3 position = new Vector3();
    public final Matrix4 projection = new Matrix4();
    final Ray ray = new Ray(new Vector3(), new Vector3());
    final Vector3 right = new Vector3();
    private final Matrix4 tmpMat = new Matrix4();
    private final Vector3 tmpVec = new Vector3();

    /* renamed from: up */
    public final Vector3 f66up = new Vector3(0.0f, 1.0f, 0.0f);
    public final Matrix4 view = new Matrix4();
    public float viewportHeight = 0.0f;
    public float viewportWidth = 0.0f;

    public abstract void update();

    public abstract void update(boolean z);

    public void apply(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(this.projection.val, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadMatrixf(this.view.val, 0);
    }

    public void lookAt(float x, float y, float z) {
        this.direction.set(x, y, z).sub(this.position).nor();
    }

    public void normalizeUp() {
        this.right.set(this.direction).crs(this.f66up).nor();
        this.f66up.set(this.right).crs(this.direction).nor();
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ) {
        rotate(this.tmpVec.set(axisX, axisY, axisZ), angle);
    }

    public void rotate(Vector3 axis, float angle) {
        this.tmpMat.setToRotation(axis, angle);
        this.direction.mul(this.tmpMat).nor();
        this.f66up.mul(this.tmpMat).nor();
    }

    public void rotateAround(Vector3 point, Vector3 axis, float angle) {
        this.tmpVec.set(point);
        this.tmpVec.sub(this.position);
        translate(this.tmpVec);
        rotate(axis, angle);
        this.tmpVec.rotate(axis, angle);
        translate(-this.tmpVec.f170x, -this.tmpVec.f171y, -this.tmpVec.f172z);
    }

    public void translate(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public void translate(Vector3 vec) {
        this.position.add(vec);
    }

    public void unproject(Vector3 vec, float viewportX, float viewportY, float viewportWidth2, float viewportHeight2) {
        float x = vec.f170x;
        float y = vec.f171y;
        vec.f170x = ((2.0f * (x - viewportX)) / viewportWidth2) - 1.0f;
        vec.f171y = ((2.0f * (((((float) Gdx.graphics.getHeight()) - y) - 1.0f) - viewportY)) / viewportHeight2) - 1.0f;
        vec.f172z = (vec.f172z * 2.0f) - 1.0f;
        vec.prj(this.invProjectionView);
    }

    public void unproject(Vector3 vec) {
        unproject(vec, 0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }

    public void project(Vector3 vec) {
        project(vec, 0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }

    public void project(Vector3 vec, float viewportX, float viewportY, float viewportWidth2, float viewportHeight2) {
        vec.prj(this.combined);
        vec.f170x = (((vec.f170x + 1.0f) * viewportWidth2) / 2.0f) + viewportX;
        vec.f171y = (((vec.f171y + 1.0f) * viewportHeight2) / 2.0f) + viewportY;
        vec.f172z = (vec.f172z + 1.0f) / 2.0f;
    }

    public Ray getPickRay(float x, float y, float viewportX, float viewportY, float viewportWidth2, float viewportHeight2) {
        unproject(this.ray.origin.set(x, y, 0.0f), viewportX, viewportY, viewportWidth2, viewportHeight2);
        unproject(this.ray.direction.set(x, y, 1.0f), viewportX, viewportY, viewportWidth2, viewportHeight2);
        this.ray.direction.sub(this.ray.origin).nor();
        return this.ray;
    }

    public Ray getPickRay(float x, float y) {
        return getPickRay(x, y, 0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }
}
