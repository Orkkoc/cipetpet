package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class OrthographicCamera extends Camera {
    private final Vector3 tmp = new Vector3();
    public float zoom = 1.0f;

    public OrthographicCamera() {
        this.near = 0.0f;
    }

    public OrthographicCamera(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.near = 0.0f;
        update();
    }

    public OrthographicCamera(float viewportWidth, float viewportHeight, float diamondAngle) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.near = 0.0f;
        findDirectionForIsoView(diamondAngle, 1.0E-8f, 20);
        update();
    }

    public void findDirectionForIsoView(float targetAngle, float epsilon, int maxIterations) {
        float start = targetAngle - 5.0f;
        float end = targetAngle + 5.0f;
        float mid = targetAngle;
        int iterations = 0;
        float aMid = 0.0f;
        while (true) {
            if (Math.abs(targetAngle - aMid) <= epsilon) {
                break;
            }
            int iterations2 = iterations + 1;
            if (iterations >= maxIterations) {
                int i = iterations2;
                break;
            }
            aMid = calculateAngle(mid);
            if (targetAngle < aMid) {
                end = mid;
            } else {
                start = mid;
            }
            mid = start + ((end - start) / 2.0f);
            iterations = iterations2;
        }
        this.position.set(calculateDirection(mid));
        this.position.f171y = -this.position.f171y;
        lookAt(0.0f, 0.0f, 0.0f);
        normalizeUp();
    }

    private float calculateAngle(float a) {
        this.position.set(calculateDirection(a).mul(30.0f));
        lookAt(0.0f, 0.0f, 0.0f);
        normalizeUp();
        update();
        Vector3 orig = new Vector3(0.0f, 0.0f, 0.0f);
        Vector3 vec = new Vector3(1.0f, 0.0f, 0.0f);
        project(orig);
        project(vec);
        return new Vector2(vec.f170x - orig.f170x, -(vec.f171y - orig.f171y)).angle();
    }

    private Vector3 calculateDirection(float angle) {
        Matrix4 transform = new Matrix4();
        Vector3 dir = new Vector3(-1.0f, 0.0f, 1.0f).nor();
        transform.setToRotation(new Vector3(1.0f, 0.0f, 1.0f).nor(), angle);
        dir.mul(transform).nor();
        return dir;
    }

    public void update() {
        update(true);
    }

    public void update(boolean updateFrustum) {
        this.projection.setToOrtho((this.zoom * (-this.viewportWidth)) / 2.0f, (this.zoom * this.viewportWidth) / 2.0f, (this.zoom * (-this.viewportHeight)) / 2.0f, (this.zoom * this.viewportHeight) / 2.0f, Math.abs(this.near), Math.abs(this.far));
        this.view.setToLookAt(this.position, this.tmp.set(this.position).add(this.direction), this.f66up);
        this.combined.set(this.projection);
        Matrix4.mul(this.combined.val, this.view.val);
        if (updateFrustum) {
            this.invProjectionView.set(this.combined);
            Matrix4.inv(this.invProjectionView.val);
            this.frustum.update(this.invProjectionView);
        }
    }

    public void setToOrtho(boolean yDown) {
        setToOrtho(yDown, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
    }

    public void setToOrtho(boolean yDown, float viewportWidth, float viewportHeight) {
        if (yDown) {
            this.f66up.set(0.0f, -1.0f, 0.0f);
            this.direction.set(0.0f, 0.0f, 1.0f);
        }
        this.position.set((this.zoom * viewportWidth) / 2.0f, (this.zoom * viewportHeight) / 2.0f, 0.0f);
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        update();
    }

    public void rotate(float angle) {
        rotate(this.direction, angle);
    }

    public void translate(float x, float y) {
        translate(x, y, 0.0f);
    }

    public void translate(Vector2 vec) {
        translate(vec.f165x, vec.f166y, 0.0f);
    }
}
