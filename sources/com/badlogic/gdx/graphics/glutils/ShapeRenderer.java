package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ShapeRenderer {
    Color color;
    Matrix4 combined;
    ShapeType currType;
    boolean matrixDirty;
    Matrix4 projView;
    ImmediateModeRenderer renderer;
    Matrix4 tmp;
    Matrix4 transform;

    public enum ShapeType {
        Point(0),
        Line(1),
        Rectangle(1),
        FilledRectangle(4),
        Box(1),
        Circle(1),
        FilledCircle(4),
        Triangle(1),
        FilledTriangle(4),
        Cone(1),
        FilledCone(4),
        Curve(1);
        
        private final int glType;

        private ShapeType(int glType2) {
            this.glType = glType2;
        }

        public int getGlType() {
            return this.glType;
        }
    }

    public ShapeRenderer() {
        this(5000);
    }

    public ShapeRenderer(int maxVertices) {
        this.matrixDirty = false;
        this.projView = new Matrix4();
        this.transform = new Matrix4();
        this.combined = new Matrix4();
        this.tmp = new Matrix4();
        this.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        this.currType = null;
        if (Gdx.graphics.isGL20Available()) {
            this.renderer = new ImmediateModeRenderer20(maxVertices, false, true, 0);
        } else {
            this.renderer = new ImmediateModeRenderer10(maxVertices);
        }
        this.projView.setToOrtho2D(0.0f, 0.0f, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        this.matrixDirty = true;
    }

    public void setColor(Color color2) {
        this.color.set(color2);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        this.projView.set(matrix);
        this.matrixDirty = true;
    }

    public void setTransformMatrix(Matrix4 matrix) {
        this.transform.set(matrix);
        this.matrixDirty = true;
    }

    public void identity() {
        this.transform.idt();
        this.matrixDirty = true;
    }

    public void translate(float x, float y, float z) {
        this.transform.translate(x, y, z);
        this.matrixDirty = true;
    }

    public void rotate(float axisX, float axisY, float axisZ, float angle) {
        this.transform.rotate(axisX, axisY, axisZ, angle);
        this.matrixDirty = true;
    }

    public void scale(float scaleX, float scaleY, float scaleZ) {
        this.transform.scale(scaleX, scaleY, scaleZ);
        this.matrixDirty = true;
    }

    public void begin(ShapeType type) {
        if (this.currType != null) {
            throw new GdxRuntimeException("Call end() before beginning a new shape batch");
        }
        this.currType = type;
        if (this.matrixDirty) {
            this.combined.set(this.projView);
            Matrix4.mul(this.combined.val, this.transform.val);
            this.matrixDirty = false;
        }
        this.renderer.begin(this.combined, this.currType.getGlType());
    }

    public void point(float x, float y, float z) {
        if (this.currType != ShapeType.Point) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Point)");
        }
        checkDirty();
        checkFlush(1);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z);
    }

    public void line(float x, float y, float z, float x2, float y2, float z2) {
        if (this.currType != ShapeType.Line) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
        }
        checkDirty();
        checkFlush(2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x2, y2, z2);
    }

    public void line(float x, float y, float x2, float y2) {
        if (this.currType != ShapeType.Line) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Line)");
        }
        checkDirty();
        checkFlush(2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x2, y2, 0.0f);
    }

    public void curve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2) {
        float dx1 = cx1 - x1;
        float dy1 = cy1 - y1;
        float dx2 = cx2 - cx1;
        float dy2 = cy2 - cy1;
        float dx3 = x2 - cx2;
        float dy3 = y2 - cy2;
        curve(x1, y1, cx1, cy1, cx2, cy2, x2, y2, ((int) Math.cbrt((double) (((float) Math.sqrt((double) ((dx1 * dx1) + (dy1 * dy1)))) + ((float) Math.sqrt((double) ((dx2 * dx2) + (dy2 * dy2)))) + ((float) Math.sqrt((double) ((dx3 * dx3) + (dy3 * dy3))))))) * 4);
    }

    public void curve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2, int segments) {
        if (this.currType != ShapeType.Curve) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Curve)");
        }
        checkDirty();
        checkFlush((segments * 2) + 2);
        float subdiv_step = 1.0f / ((float) segments);
        float subdiv_step2 = subdiv_step * subdiv_step;
        float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;
        float pre1 = 3.0f * subdiv_step;
        float pre2 = 3.0f * subdiv_step2;
        float pre4 = 6.0f * subdiv_step2;
        float pre5 = 6.0f * subdiv_step3;
        float tmp1x = (x1 - (2.0f * cx1)) + cx2;
        float tmp1y = (y1 - (2.0f * cy1)) + cy2;
        float tmp2x = (((cx1 - cx2) * 3.0f) - x1) + x2;
        float tmp2y = (((cy1 - cy2) * 3.0f) - y1) + y2;
        float fx = x1;
        float fy = y1;
        float dfx = ((cx1 - x1) * pre1) + (tmp1x * pre2) + (tmp2x * subdiv_step3);
        float dfy = ((cy1 - y1) * pre1) + (tmp1y * pre2) + (tmp2y * subdiv_step3);
        float ddfx = (tmp1x * pre4) + (tmp2x * pre5);
        float ddfy = (tmp1y * pre4) + (tmp2y * pre5);
        float dddfx = tmp2x * pre5;
        float dddfy = tmp2y * pre5;
        while (true) {
            int segments2 = segments;
            segments = segments2 - 1;
            if (segments2 > 0) {
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(fx, fy, 0.0f);
                fx += dfx;
                fy += dfy;
                dfx += ddfx;
                dfy += ddfy;
                ddfx += dddfx;
                ddfy += dddfy;
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(fx, fy, 0.0f);
            } else {
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(fx, fy, 0.0f);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x2, y2, 0.0f);
                return;
            }
        }
    }

    public void rect(float x, float y, float width, float height) {
        if (this.currType != ShapeType.Rectangle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Rectangle)");
        }
        checkDirty();
        checkFlush(8);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, 0.0f);
    }

    public void filledRect(float x, float y, float width, float height) {
        if (this.currType != ShapeType.FilledRectangle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.FilledRectangle)");
        }
        checkDirty();
        checkFlush(8);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, 0.0f);
    }

    public void filledRect(float x, float y, float width, float height, Color c1, Color c2, Color c3, Color c4) {
        if (this.currType != ShapeType.FilledRectangle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.FilledRectangle)");
        }
        checkDirty();
        checkFlush(8);
        this.renderer.color(c1.f70r, c1.f69g, c1.f68b, c1.f67a);
        this.renderer.vertex(x, y, 0.0f);
        this.renderer.color(c2.f70r, c2.f69g, c2.f68b, c2.f67a);
        this.renderer.vertex(x + width, y, 0.0f);
        this.renderer.color(c3.f70r, c3.f69g, c3.f68b, c3.f67a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(c3.f70r, c3.f69g, c3.f68b, c3.f67a);
        this.renderer.vertex(x + width, y + height, 0.0f);
        this.renderer.color(c4.f70r, c4.f69g, c4.f68b, c4.f67a);
        this.renderer.vertex(x, y + height, 0.0f);
        this.renderer.color(c1.f70r, c1.f69g, c1.f68b, c1.f67a);
        this.renderer.vertex(x, y, 0.0f);
    }

    public void box(float x, float y, float z, float width, float height, float depth) {
        if (this.currType != ShapeType.Box) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Box)");
        }
        checkDirty();
        checkFlush(16);
        float depth2 = -depth;
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, z);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + width, y + height, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y, z + depth2);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x, y + height, z + depth2);
    }

    public void circle(float x, float y, float radius) {
        circle(x, y, radius, (int) (6.0f * ((float) Math.cbrt((double) radius))));
    }

    public void circle(float x, float y, float radius, int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be >= 0.");
        } else if (this.currType != ShapeType.Circle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Circle)");
        } else {
            checkDirty();
            checkFlush((segments * 2) + 2);
            float angle = 6.283185f / ((float) segments);
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);
            float cx = radius;
            float cy = 0.0f;
            for (int i = 0; i < segments; i++) {
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
                float temp = cx;
                cx = (cos * cx) - (sin * cy);
                cy = (sin * temp) + (cos * cy);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
            }
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
            float f = cx;
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + radius, y + 0.0f, 0.0f);
        }
    }

    public void filledCircle(float x, float y, float radius) {
        filledCircle(x, y, radius, (int) (6.0f * ((float) Math.cbrt((double) radius))));
    }

    public void filledCircle(float x, float y, float radius, int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be >= 0.");
        } else if (this.currType != ShapeType.FilledCircle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.FilledCircle)");
        } else {
            checkDirty();
            checkFlush((segments * 3) + 3);
            int i = 360 / segments;
            float angle = 6.283185f / ((float) segments);
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);
            float cx = radius;
            float cy = 0.0f;
            int segments2 = segments - 1;
            for (int i2 = 0; i2 < segments2; i2++) {
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x, y, 0.0f);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
                float temp = cx;
                cx = (cos * cx) - (sin * cy);
                cy = (sin * temp) + (cos * cy);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, 0.0f);
            }
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x, y, 0.0f);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + cx, y + cy, 0.0f);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + radius, y + 0.0f, 0.0f);
        }
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (this.currType != ShapeType.Triangle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Triangle)");
        }
        checkDirty();
        checkFlush(6);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x1, y1, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x2, y2, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x2, y2, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x3, y3, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x3, y3, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x1, y1, 0.0f);
    }

    public void filledTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (this.currType != ShapeType.FilledTriangle) {
            throw new GdxRuntimeException("Must call begin(ShapeType.FilledTriangle)");
        }
        checkDirty();
        checkFlush(3);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x1, y1, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x2, y2, 0.0f);
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x3, y3, 0.0f);
    }

    public void cone(float x, float y, float z, float radius, float height) {
        cone(x, y, z, radius, height, (int) (6.0f * ((float) Math.cbrt((double) radius))));
    }

    public void cone(float x, float y, float z, float radius, float height, int segments) {
        if (this.currType != ShapeType.Cone) {
            throw new GdxRuntimeException("Must call begin(ShapeType.Cone)");
        }
        checkDirty();
        checkFlush((segments * 4) + 2);
        float angle = 6.283185f / ((float) segments);
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);
        float cx = radius;
        float cy = 0.0f;
        for (int i = 0; i < segments; i++) {
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + cx, y + cy, z);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x, y, z + height);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + cx, y + cy, z);
            float temp = cx;
            cx = (cos * cx) - (sin * cy);
            cy = (sin * temp) + (cos * cy);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + cx, y + cy, z);
        }
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + cx, y + cy, z);
        float f = cx;
        this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
        this.renderer.vertex(x + radius, y + 0.0f, z);
    }

    public void filledCone(float x, float y, float z, float radius, float height) {
        filledCone(x, y, z, radius, height, (int) (4.0f * ((float) Math.sqrt((double) radius))));
    }

    public void filledCone(float x, float y, float z, float radius, float height, int segments) {
        if (segments <= 0) {
            throw new IllegalArgumentException("segments must be >= 0.");
        } else if (this.currType != ShapeType.FilledCone) {
            throw new GdxRuntimeException("Must call begin(ShapeType.FilledCone)");
        } else {
            checkDirty();
            checkFlush((segments * 6) + 3);
            int i = 360 / segments;
            float angle = 6.283185f / ((float) segments);
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);
            float cx = radius;
            float cy = 0.0f;
            int segments2 = segments - 1;
            for (int i2 = 0; i2 < segments2; i2++) {
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x, y, z);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, z);
                float temp = cx;
                float temp2 = cy;
                cx = (cos * cx) - (sin * cy);
                cy = (sin * temp) + (cos * cy);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, z);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + temp, y + temp2, z);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x + cx, y + cy, z);
                this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
                this.renderer.vertex(x, y, z + height);
            }
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x, y, z);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + cx, y + cy, z);
            this.renderer.color(this.color.f70r, this.color.f69g, this.color.f68b, this.color.f67a);
            this.renderer.vertex(x + radius, y + 0.0f, z);
        }
    }

    private void checkDirty() {
        if (this.matrixDirty) {
            ShapeType type = this.currType;
            end();
            begin(type);
        }
    }

    private void checkFlush(int newVertices) {
        if (this.renderer.getMaxVertices() - this.renderer.getNumVertices() < newVertices) {
            ShapeType type = this.currType;
            end();
            begin(type);
        }
    }

    public void end() {
        this.renderer.end();
        this.currType = null;
    }

    public void flush() {
        ShapeType type = this.currType;
        end();
        begin(type);
    }

    public ShapeType getCurrentType() {
        return this.currType;
    }

    public void dispose() {
        this.renderer.dispose();
    }
}
