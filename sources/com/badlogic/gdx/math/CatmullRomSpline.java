package com.badlogic.gdx.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CatmullRomSpline implements Serializable {
    private static final long serialVersionUID = -3290464799289771451L;

    /* renamed from: T1 */
    Vector3 f144T1 = new Vector3();

    /* renamed from: T2 */
    Vector3 f145T2 = new Vector3();
    private List<Vector3> controlPoints = new ArrayList();

    public void add(Vector3 point) {
        this.controlPoints.add(point);
    }

    public List<Vector3> getControlPoints() {
        return this.controlPoints;
    }

    public List<Vector3> getPath(int numPoints) {
        ArrayList<Vector3> points = new ArrayList<>();
        if (this.controlPoints.size() >= 4) {
            Vector3 T1 = new Vector3();
            Vector3 T2 = new Vector3();
            for (int i = 1; i <= this.controlPoints.size() - 3; i++) {
                points.add(this.controlPoints.get(i));
                float increment = 1.0f / ((float) (numPoints + 1));
                float t = increment;
                T1.set(this.controlPoints.get(i + 1)).sub(this.controlPoints.get(i - 1)).mul(0.5f);
                T2.set(this.controlPoints.get(i + 2)).sub(this.controlPoints.get(i)).mul(0.5f);
                for (int j = 0; j < numPoints; j++) {
                    Vector3 point = new Vector3(this.controlPoints.get(i)).mul(((((2.0f * t) * t) * t) - ((3.0f * t) * t)) + 1.0f);
                    point.add(this.controlPoints.get(i + 1).tmp().mul((-2.0f * t * t * t) + (3.0f * t * t)));
                    point.add(T1.tmp().mul((((t * t) * t) - ((2.0f * t) * t)) + t));
                    point.add(T2.tmp().mul(((t * t) * t) - (t * t)));
                    points.add(point);
                    t += increment;
                }
            }
            if (this.controlPoints.size() >= 4) {
                points.add(this.controlPoints.get(this.controlPoints.size() - 2));
            }
        }
        return points;
    }

    public void getPath(Vector3[] points, int numPoints) {
        int idx = 0;
        if (this.controlPoints.size() >= 4) {
            int i = 1;
            while (i <= this.controlPoints.size() - 3) {
                int idx2 = idx + 1;
                points[idx].set(this.controlPoints.get(i));
                float increment = 1.0f / ((float) (numPoints + 1));
                float t = increment;
                this.f144T1.set(this.controlPoints.get(i + 1)).sub(this.controlPoints.get(i - 1)).mul(0.5f);
                this.f145T2.set(this.controlPoints.get(i + 2)).sub(this.controlPoints.get(i)).mul(0.5f);
                int j = 0;
                while (j < numPoints) {
                    Vector3 point = points[idx2].set(this.controlPoints.get(i)).mul(((((2.0f * t) * t) * t) - ((3.0f * t) * t)) + 1.0f);
                    point.add(this.controlPoints.get(i + 1).tmp().mul((-2.0f * t * t * t) + (3.0f * t * t)));
                    point.add(this.f144T1.tmp().mul((((t * t) * t) - ((2.0f * t) * t)) + t));
                    point.add(this.f145T2.tmp().mul(((t * t) * t) - (t * t)));
                    t += increment;
                    j++;
                    idx2++;
                }
                i++;
                idx = idx2;
            }
            points[idx].set(this.controlPoints.get(this.controlPoints.size() - 2));
        }
    }

    public List<Vector3> getTangents(int numPoints) {
        ArrayList<Vector3> tangents = new ArrayList<>();
        if (this.controlPoints.size() >= 4) {
            Vector3 T1 = new Vector3();
            Vector3 T2 = new Vector3();
            for (int i = 1; i <= this.controlPoints.size() - 3; i++) {
                float increment = 1.0f / ((float) (numPoints + 1));
                float t = increment;
                T1.set(this.controlPoints.get(i + 1)).sub(this.controlPoints.get(i - 1)).mul(0.5f);
                T2.set(this.controlPoints.get(i + 2)).sub(this.controlPoints.get(i)).mul(0.5f);
                tangents.add(new Vector3(T1).nor());
                for (int j = 0; j < numPoints; j++) {
                    Vector3 point = new Vector3(this.controlPoints.get(i)).mul(((6.0f * t) * t) - (6.0f * t));
                    point.add(this.controlPoints.get(i + 1).tmp().mul((-6.0f * t * t) + (6.0f * t)));
                    point.add(T1.tmp().mul((((3.0f * t) * t) - (4.0f * t)) + 1.0f));
                    point.add(T2.tmp().mul(((3.0f * t) * t) - (2.0f * t)));
                    tangents.add(point.nor());
                    t += increment;
                }
            }
            if (this.controlPoints.size() >= 4) {
                tangents.add(T1.set(this.controlPoints.get(this.controlPoints.size() - 1)).sub(this.controlPoints.get(this.controlPoints.size() - 3)).mul(0.5f).cpy().nor());
            }
        }
        return tangents;
    }

    public List<Vector3> getTangentNormals2D(int numPoints) {
        ArrayList<Vector3> tangents = new ArrayList<>();
        if (this.controlPoints.size() >= 4) {
            Vector3 T1 = new Vector3();
            Vector3 T2 = new Vector3();
            for (int i = 1; i <= this.controlPoints.size() - 3; i++) {
                float increment = 1.0f / ((float) (numPoints + 1));
                float t = increment;
                T1.set(this.controlPoints.get(i + 1)).sub(this.controlPoints.get(i - 1)).mul(0.5f);
                T2.set(this.controlPoints.get(i + 2)).sub(this.controlPoints.get(i)).mul(0.5f);
                Vector3 normal = new Vector3(T1).nor();
                float x = normal.f170x;
                normal.f170x = normal.f171y;
                normal.f171y = -x;
                tangents.add(normal);
                for (int j = 0; j < numPoints; j++) {
                    Vector3 point = new Vector3(this.controlPoints.get(i)).mul(((6.0f * t) * t) - (6.0f * t));
                    point.add(this.controlPoints.get(i + 1).tmp().mul((-6.0f * t * t) + (6.0f * t)));
                    point.add(T1.tmp().mul((((3.0f * t) * t) - (4.0f * t)) + 1.0f));
                    point.add(T2.tmp().mul(((3.0f * t) * t) - (2.0f * t)));
                    point.nor();
                    float x2 = point.f170x;
                    point.f170x = point.f171y;
                    point.f171y = -x2;
                    tangents.add(point);
                    t += increment;
                }
            }
        }
        return tangents;
    }

    public List<Vector3> getTangentNormals(int numPoints, Vector3 up) {
        List<Vector3> tangents = getTangents(numPoints);
        ArrayList<Vector3> normals = new ArrayList<>();
        for (Vector3 tangent : tangents) {
            normals.add(new Vector3(tangent).crs(up).nor());
        }
        return normals;
    }

    public List<Vector3> getTangentNormals(int numPoints, List<Vector3> up) {
        List<Vector3> tangents = getTangents(numPoints);
        ArrayList<Vector3> normals = new ArrayList<>();
        int i = 0;
        for (Vector3 tangent : tangents) {
            normals.add(new Vector3(tangent).crs(up.get(i)).nor());
            i++;
        }
        return normals;
    }
}
