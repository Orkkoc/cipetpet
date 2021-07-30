package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class ScissorStack {
    private static Array<Rectangle> scissors = new Array<>();
    static Vector3 tmp = new Vector3();
    static final Rectangle viewport = new Rectangle();

    public static boolean pushScissors(Rectangle scissor) {
        fix(scissor);
        if (scissors.size != 0) {
            Rectangle parent = scissors.get(scissors.size - 1);
            float minX = Math.max(parent.f161x, scissor.f161x);
            float maxX = Math.min(parent.f161x + parent.width, scissor.f161x + scissor.width);
            if (maxX - minX < 1.0f) {
                return false;
            }
            float minY = Math.max(parent.f162y, scissor.f162y);
            float maxY = Math.min(parent.f162y + parent.height, scissor.f162y + scissor.height);
            if (maxY - minY < 1.0f) {
                return false;
            }
            scissor.f161x = minX;
            scissor.f162y = minY;
            scissor.width = maxX - minX;
            scissor.height = Math.max(1.0f, maxY - minY);
        } else if (scissor.width < 1.0f || scissor.height < 1.0f) {
            return false;
        } else {
            Gdx.f12gl.glEnable(3089);
        }
        scissors.add(scissor);
        Gdx.f12gl.glScissor((int) scissor.f161x, (int) scissor.f162y, (int) scissor.width, (int) scissor.height);
        return true;
    }

    public static Rectangle popScissors() {
        Rectangle old = scissors.pop();
        if (scissors.size == 0) {
            Gdx.f12gl.glDisable(3089);
        } else {
            Rectangle scissor = scissors.peek();
            Gdx.f12gl.glScissor((int) scissor.f161x, (int) scissor.f162y, (int) scissor.width, (int) scissor.height);
        }
        return old;
    }

    private static void fix(Rectangle rect) {
        rect.f161x = (float) Math.round(rect.f161x);
        rect.f162y = (float) Math.round(rect.f162y);
        rect.width = (float) Math.round(rect.width);
        rect.height = (float) Math.round(rect.height);
        if (rect.width < 0.0f) {
            rect.width = -rect.width;
            rect.f161x -= rect.width;
        }
        if (rect.height < 0.0f) {
            rect.height = -rect.height;
            rect.f162y -= rect.height;
        }
    }

    public static void calculateScissors(Camera camera, Matrix4 batchTransform, Rectangle area, Rectangle scissor) {
        tmp.set(area.f161x, area.f162y, 0.0f);
        tmp.mul(batchTransform);
        camera.project(tmp);
        scissor.f161x = tmp.f170x;
        scissor.f162y = tmp.f171y;
        tmp.set(area.f161x + area.width, area.f162y + area.height, 0.0f);
        tmp.mul(batchTransform);
        camera.project(tmp);
        scissor.width = tmp.f170x - scissor.f161x;
        scissor.height = tmp.f171y - scissor.f162y;
    }

    public static Rectangle getViewport() {
        if (scissors.size == 0) {
            viewport.set(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
            return viewport;
        }
        viewport.set(scissors.peek());
        return viewport;
    }

    public static void toWindowCoordinates(Camera camera, Matrix4 transformMatrix, Vector2 point) {
        tmp.set(point.f165x, point.f166y, 0.0f);
        tmp.mul(transformMatrix);
        camera.project(tmp);
        tmp.f171y = ((float) Gdx.graphics.getHeight()) - tmp.f171y;
        point.f165x = tmp.f170x;
        point.f166y = tmp.f171y;
    }
}
