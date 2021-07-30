package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Frustum {
    protected static final Vector3[] clipSpacePlanePoints = {new Vector3(-1.0f, -1.0f, -1.0f), new Vector3(1.0f, -1.0f, -1.0f), new Vector3(1.0f, 1.0f, -1.0f), new Vector3(-1.0f, 1.0f, -1.0f), new Vector3(-1.0f, -1.0f, 1.0f), new Vector3(1.0f, -1.0f, 1.0f), new Vector3(1.0f, 1.0f, 1.0f), new Vector3(-1.0f, 1.0f, 1.0f)};
    protected static final float[] clipSpacePlanePointsArray = new float[24];
    public final Vector3[] planePoints = {new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3(), new Vector3()};
    protected final float[] planePointsArray = new float[24];
    public final Plane[] planes = new Plane[6];

    static {
        Vector3[] arr$ = clipSpacePlanePoints;
        int len$ = arr$.length;
        int i$ = 0;
        int j = 0;
        while (i$ < len$) {
            Vector3 v = arr$[i$];
            int j2 = j + 1;
            clipSpacePlanePointsArray[j] = v.f170x;
            int j3 = j2 + 1;
            clipSpacePlanePointsArray[j2] = v.f171y;
            clipSpacePlanePointsArray[j3] = v.f172z;
            i$++;
            j = j3 + 1;
        }
    }

    public Frustum() {
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new Plane(new Vector3(), 0.0f);
        }
    }

    public void update(Matrix4 inverseProjectionView) {
        System.arraycopy(clipSpacePlanePointsArray, 0, this.planePointsArray, 0, clipSpacePlanePointsArray.length);
        Matrix4.prj(inverseProjectionView.val, this.planePointsArray, 0, 8, 3);
        int i = 0;
        int j = 0;
        while (true) {
            int j2 = j;
            if (i < 8) {
                Vector3 v = this.planePoints[i];
                int j3 = j2 + 1;
                v.f170x = this.planePointsArray[j2];
                int j4 = j3 + 1;
                v.f171y = this.planePointsArray[j3];
                j = j4 + 1;
                v.f172z = this.planePointsArray[j4];
                i++;
            } else {
                this.planes[0].set(this.planePoints[1], this.planePoints[0], this.planePoints[2]);
                this.planes[1].set(this.planePoints[4], this.planePoints[5], this.planePoints[7]);
                this.planes[2].set(this.planePoints[0], this.planePoints[4], this.planePoints[3]);
                this.planes[3].set(this.planePoints[5], this.planePoints[1], this.planePoints[6]);
                this.planes[4].set(this.planePoints[2], this.planePoints[3], this.planePoints[6]);
                this.planes[5].set(this.planePoints[4], this.planePoints[0], this.planePoints[1]);
                return;
            }
        }
    }

    public boolean pointInFrustum(Vector3 point) {
        for (Plane testPoint : this.planes) {
            if (testPoint.testPoint(point) == Plane.PlaneSide.Back) {
                return false;
            }
        }
        return true;
    }

    public boolean sphereInFrustum(Vector3 center, float radius) {
        for (int i = 0; i < 6; i++) {
            if ((this.planes[i].normal.f170x * center.f170x) + (this.planes[i].normal.f171y * center.f171y) + (this.planes[i].normal.f172z * center.f172z) < (-radius) - this.planes[i].f154d) {
                return false;
            }
        }
        return true;
    }

    public boolean sphereInFrustumWithoutNearFar(Vector3 center, float radius) {
        for (int i = 2; i < 6; i++) {
            if ((this.planes[i].normal.f170x * center.f170x) + (this.planes[i].normal.f171y * center.f171y) + (this.planes[i].normal.f172z * center.f172z) < (-radius) - this.planes[i].f154d) {
                return false;
            }
        }
        return true;
    }

    public boolean boundsInFrustum(BoundingBox bounds) {
        int len2 = this.planes.length;
        for (int i = 0; i < len2; i++) {
            int out = 0;
            for (Vector3 testPoint : bounds.getCorners()) {
                if (this.planes[i].testPoint(testPoint) == Plane.PlaneSide.Back) {
                    out++;
                }
            }
            if (out == 8) {
                return false;
            }
        }
        return true;
    }
}
