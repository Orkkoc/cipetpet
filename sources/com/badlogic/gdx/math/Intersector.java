package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import java.util.Arrays;
import java.util.List;

public final class Intersector {
    static Vector3 best = new Vector3();
    private static final Vector3 dir = new Vector3();

    /* renamed from: i */
    private static final Vector3 f148i = new Vector3();
    static Vector3 intersection = new Vector3();

    /* renamed from: p */
    private static final Plane f149p = new Plane(new Vector3(), 0.0f);
    private static final Vector3 start = new Vector3();
    static Vector3 tmp = new Vector3();
    static Vector3 tmp1 = new Vector3();
    static Vector3 tmp2 = new Vector3();
    static Vector3 tmp3 = new Vector3();

    /* renamed from: v0 */
    private static final Vector3 f150v0 = new Vector3();

    /* renamed from: v1 */
    private static final Vector3 f151v1 = new Vector3();

    /* renamed from: v2 */
    private static final Vector3 f152v2 = new Vector3();

    public static class MinimumTranslationVector {
        public float depth = 0.0f;
        public Vector2 normal = new Vector2();
    }

    public static float getLowestPositiveRoot(float a, float b, float c) {
        float det = (b * b) - ((4.0f * a) * c);
        if (det < 0.0f) {
            return Float.NaN;
        }
        float sqrtD = (float) Math.sqrt((double) det);
        float invA = 1.0f / (2.0f * a);
        float r1 = ((-b) - sqrtD) * invA;
        float r2 = ((-b) + sqrtD) * invA;
        if (r1 > r2) {
            float tmp4 = r2;
            r2 = r1;
            r1 = tmp4;
        }
        if (r1 > 0.0f) {
            return r1;
        }
        if (r2 > 0.0f) {
            return r2;
        }
        return Float.NaN;
    }

    public static boolean isPointInTriangle(Vector3 point, Vector3 t1, Vector3 t2, Vector3 t3) {
        f150v0.set(t1).sub(point);
        f151v1.set(t2).sub(point);
        f152v2.set(t3).sub(point);
        float ab = f150v0.dot(f151v1);
        float ac = f150v0.dot(f152v2);
        float bc = f151v1.dot(f152v2);
        if ((bc * ac) - (f152v2.dot(f152v2) * ab) < 0.0f) {
            return false;
        }
        if ((ab * bc) - (ac * f151v1.dot(f151v1)) >= 0.0f) {
            return true;
        }
        return false;
    }

    public static boolean intersectSegmentPlane(Vector3 start2, Vector3 end, Plane plane, Vector3 intersection2) {
        Vector3 dir2 = end.tmp().sub(start2);
        float t = (-(start2.dot(plane.getNormal()) + plane.getD())) / dir2.dot(plane.getNormal());
        if (t < 0.0f || t > 1.0f) {
            return false;
        }
        intersection2.set(start2).add(dir2.mul(t));
        return true;
    }

    public static int pointLineSide(Vector2 linePoint1, Vector2 linePoint2, Vector2 point) {
        return (int) Math.signum(((linePoint2.f165x - linePoint1.f165x) * (point.f166y - linePoint1.f166y)) - ((linePoint2.f166y - linePoint1.f166y) * (point.f165x - linePoint1.f165x)));
    }

    public static int pointLineSide(float linePoint1X, float linePoint1Y, float linePoint2X, float linePoint2Y, float pointX, float pointY) {
        return (int) Math.signum(((linePoint2X - linePoint1X) * (pointY - linePoint1Y)) - ((linePoint2Y - linePoint1Y) * (pointX - linePoint1X)));
    }

    public static boolean isPointInPolygon(List<Vector2> polygon, Vector2 point) {
        int j = polygon.size() - 1;
        boolean oddNodes = false;
        for (int i = 0; i < polygon.size(); i++) {
            if ((polygon.get(i).f166y < point.f166y && polygon.get(j).f166y >= point.f166y) || (polygon.get(j).f166y < point.f166y && polygon.get(i).f166y >= point.f166y)) {
                if (((polygon.get(j).f165x - polygon.get(i).f165x) * ((point.f166y - polygon.get(i).f166y) / (polygon.get(j).f166y - polygon.get(i).f166y))) + polygon.get(i).f165x < point.f165x) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }

    public static float distanceLinePoint(Vector2 start2, Vector2 end, Vector2 point) {
        tmp.set(end.f165x, end.f166y, 0.0f);
        float l2 = tmp.sub(start2.f165x, start2.f166y, 0.0f).len2();
        if (l2 == 0.0f) {
            return point.dst(start2);
        }
        tmp.set(point.f165x, point.f166y, 0.0f);
        tmp.sub(start2.f165x, start2.f166y, 0.0f);
        tmp2.set(end.f165x, end.f166y, 0.0f);
        tmp2.sub(start2.f165x, start2.f166y, 0.0f);
        float t = tmp.dot(tmp2) / l2;
        if (t < 0.0f) {
            return point.dst(start2);
        }
        if (t > 1.0f) {
            return point.dst(end);
        }
        tmp.set(end.f165x, end.f166y, 0.0f);
        tmp.sub(start2.f165x, start2.f166y, 0.0f).mul(t).add(start2.f165x, start2.f166y, 0.0f);
        return tmp2.set(point.f165x, point.f166y, 0.0f).dst(tmp);
    }

    public static float distanceLinePoint(float startX, float startY, float endX, float endY, float pointX, float pointY) {
        return Math.abs(((pointX - startX) * (endY - startY)) - ((pointY - startY) * (endX - startX))) / ((float) Math.sqrt((double) (((endX - startX) * (endX - startX)) + ((endY - startY) * (endY - startY)))));
    }

    public static boolean intersectSegmentCircle(Vector2 start2, Vector2 end, Vector2 center, float squareRadius) {
        tmp.set(end.f165x - start2.f165x, end.f166y - start2.f166y, 0.0f);
        tmp1.set(center.f165x - start2.f165x, center.f166y - start2.f166y, 0.0f);
        float l = tmp.len();
        float u = tmp1.dot(tmp.nor());
        if (u <= 0.0f) {
            tmp2.set(start2.f165x, start2.f166y, 0.0f);
        } else if (u >= l) {
            tmp2.set(end.f165x, end.f166y, 0.0f);
        } else {
            tmp3.set(tmp.mul(u));
            tmp2.set(tmp3.f170x + start2.f165x, tmp3.f171y + start2.f166y, 0.0f);
        }
        float x = center.f165x - tmp2.f170x;
        float y = center.f166y - tmp2.f171y;
        if ((x * x) + (y * y) <= squareRadius) {
            return true;
        }
        return false;
    }

    public static float intersectSegmentCircleDisplace(Vector2 start2, Vector2 end, Vector2 point, float radius, Vector2 displacement) {
        float d = start2.dst(end);
        float u = (((point.f165x - start2.f165x) * (end.f165x - start2.f165x)) + ((point.f166y - start2.f166y) * (end.f166y - start2.f166y))) / (d * d);
        if (u < 0.0f || u > 1.0f) {
            return Float.POSITIVE_INFINITY;
        }
        tmp.set(end.f165x, end.f166y, 0.0f).sub(start2.f165x, start2.f166y, 0.0f);
        tmp2.set(start2.f165x, start2.f166y, 0.0f).add(tmp.mul(u));
        float d2 = tmp2.dst(point.f165x, point.f166y, 0.0f);
        if (d2 >= radius) {
            return Float.POSITIVE_INFINITY;
        }
        displacement.set(point).sub(tmp2.f170x, tmp2.f171y).nor();
        return d2;
    }

    public static boolean intersectRayPlane(Ray ray, Plane plane, Vector3 intersection2) {
        float denom = ray.direction.dot(plane.getNormal());
        if (denom != 0.0f) {
            float t = (-(ray.origin.dot(plane.getNormal()) + plane.getD())) / denom;
            if (t < 0.0f) {
                return false;
            }
            if (intersection2 != null) {
                intersection2.set(ray.origin).add(ray.direction.tmp().mul(t));
            }
            return true;
        } else if (plane.testPoint(ray.origin) != Plane.PlaneSide.OnPlane) {
            return false;
        } else {
            if (intersection2 != null) {
                intersection2.set(ray.origin);
            }
            return true;
        }
    }

    public static float intersectLinePlane(float x, float y, float z, float x2, float y2, float z2, Plane plane, Vector3 intersection2) {
        Vector3 direction = tmp.set(x2, y2, z2).sub(x, y, z);
        Vector3 origin = tmp2.set(x, y, z);
        float denom = direction.dot(plane.getNormal());
        if (denom != 0.0f) {
            float t = (-(origin.dot(plane.getNormal()) + plane.getD())) / denom;
            if (t < 0.0f || t > 1.0f || intersection2 == null) {
                return t;
            }
            intersection2.set(origin).add(direction.mul(t));
            return t;
        } else if (plane.testPoint(origin) != Plane.PlaneSide.OnPlane) {
            return -1.0f;
        } else {
            if (intersection2 != null) {
                intersection2.set(origin);
            }
            return 0.0f;
        }
    }

    public static boolean intersectRayTriangle(Ray ray, Vector3 t1, Vector3 t2, Vector3 t3, Vector3 intersection2) {
        f149p.set(t1, t2, t3);
        if (!intersectRayPlane(ray, f149p, f148i)) {
            return false;
        }
        f150v0.set(t3).sub(t1);
        f151v1.set(t2).sub(t1);
        f152v2.set(f148i).sub(t1);
        float dot00 = f150v0.dot(f150v0);
        float dot01 = f150v0.dot(f151v1);
        float dot02 = f150v0.dot(f152v2);
        float dot11 = f151v1.dot(f151v1);
        float dot12 = f151v1.dot(f152v2);
        float denom = (dot00 * dot11) - (dot01 * dot01);
        if (denom == 0.0f) {
            return false;
        }
        float u = ((dot11 * dot02) - (dot01 * dot12)) / denom;
        float v = ((dot00 * dot12) - (dot01 * dot02)) / denom;
        if (u < 0.0f || v < 0.0f || u + v > 1.0f) {
            return false;
        }
        if (intersection2 != null) {
            intersection2.set(f148i);
        }
        return true;
    }

    public static boolean intersectRaySphere(Ray ray, Vector3 center, float radius, Vector3 intersection2) {
        float q;
        dir.set(ray.direction).nor();
        start.set(ray.origin);
        float b = 2.0f * dir.dot(start.tmp().sub(center));
        float c = start.dst2(center) - (radius * radius);
        float disc = (b * b) - (4.0f * c);
        if (disc < 0.0f) {
            return false;
        }
        float distSqrt = (float) Math.sqrt((double) disc);
        if (b < 0.0f) {
            q = ((-b) - distSqrt) / 2.0f;
        } else {
            q = ((-b) + distSqrt) / 2.0f;
        }
        float t0 = q / 1.0f;
        float t1 = c / q;
        if (t0 > t1) {
            float temp = t0;
            t0 = t1;
            t1 = temp;
        }
        if (t1 < 0.0f) {
            return false;
        }
        if (t0 < 0.0f) {
            if (intersection2 != null) {
                intersection2.set(start).add(dir.tmp().mul(t1));
            }
            return true;
        }
        if (intersection2 != null) {
            intersection2.set(start).add(dir.tmp().mul(t0));
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean intersectRayBounds(Ray ray, BoundingBox box, Vector3 intersection2) {
        Vector3.tmp.set(ray.origin);
        Vector3.tmp2.set(ray.origin);
        Vector3.tmp.sub(box.min);
        Vector3.tmp.sub(box.max);
        if (Vector3.tmp.f170x > 0.0f && Vector3.tmp.f171y > 0.0f && Vector3.tmp.f172z > 0.0f && Vector3.tmp2.f170x < 0.0f && Vector3.tmp2.f171y < 0.0f && Vector3.tmp2.f172z < 0.0f) {
            return true;
        }
        float lowest = 0.0f;
        boolean hit = false;
        if (ray.origin.f170x <= box.min.f170x && ray.direction.f170x > 0.0f) {
            float t = (box.min.f170x - ray.origin.f170x) / ray.direction.f170x;
            if (t >= 0.0f) {
                Vector3.tmp3.set(ray.direction).mul(t).add(ray.origin);
                if (Vector3.tmp3.f171y >= box.min.f171y && Vector3.tmp3.f171y <= box.max.f171y && Vector3.tmp3.f172z >= box.min.f172z && Vector3.tmp3.f172z <= box.max.f172z && (0 == 0 || t < 0.0f)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        if (ray.origin.f170x >= box.max.f170x && ray.direction.f170x < 0.0f) {
            float t2 = (box.max.f170x - ray.origin.f170x) / ray.direction.f170x;
            if (t2 >= 0.0f) {
                Vector3.tmp3.set(ray.direction).mul(t2).add(ray.origin);
                if (Vector3.tmp3.f171y >= box.min.f171y && Vector3.tmp3.f171y <= box.max.f171y && Vector3.tmp3.f172z >= box.min.f172z && Vector3.tmp3.f172z <= box.max.f172z && (!hit || t2 < lowest)) {
                    hit = true;
                    lowest = t2;
                }
            }
        }
        if (ray.origin.f171y <= box.min.f171y && ray.direction.f171y > 0.0f) {
            float t3 = (box.min.f171y - ray.origin.f171y) / ray.direction.f171y;
            if (t3 >= 0.0f) {
                Vector3.tmp3.set(ray.direction).mul(t3).add(ray.origin);
                if (Vector3.tmp3.f170x >= box.min.f170x && Vector3.tmp3.f170x <= box.max.f170x && Vector3.tmp3.f172z >= box.min.f172z && Vector3.tmp3.f172z <= box.max.f172z && (!hit || t3 < lowest)) {
                    hit = true;
                    lowest = t3;
                }
            }
        }
        if (ray.origin.f171y >= box.max.f171y && ray.direction.f171y < 0.0f) {
            float t4 = (box.max.f171y - ray.origin.f171y) / ray.direction.f171y;
            if (t4 >= 0.0f) {
                Vector3.tmp3.set(ray.direction).mul(t4).add(ray.origin);
                if (Vector3.tmp3.f170x >= box.min.f170x && Vector3.tmp3.f170x <= box.max.f170x && Vector3.tmp3.f172z >= box.min.f172z && Vector3.tmp3.f172z <= box.max.f172z && (!hit || t4 < lowest)) {
                    hit = true;
                    lowest = t4;
                }
            }
        }
        if (ray.origin.f172z <= box.min.f171y && ray.direction.f172z > 0.0f) {
            float t5 = (box.min.f172z - ray.origin.f172z) / ray.direction.f172z;
            if (t5 >= 0.0f) {
                Vector3.tmp3.set(ray.direction).mul(t5).add(ray.origin);
                if (Vector3.tmp3.f170x >= box.min.f170x && Vector3.tmp3.f170x <= box.max.f170x && Vector3.tmp3.f171y >= box.min.f171y && Vector3.tmp3.f171y <= box.max.f171y && (!hit || t5 < lowest)) {
                    hit = true;
                    lowest = t5;
                }
            }
        }
        if (ray.origin.f172z >= box.max.f172z && ray.direction.f172z < 0.0f) {
            float t6 = (box.max.f172z - ray.origin.f172z) / ray.direction.f172z;
            if (t6 >= 0.0f) {
                Vector3.tmp3.set(ray.direction).mul(t6).add(ray.origin);
                if (Vector3.tmp3.f170x >= box.min.f170x && Vector3.tmp3.f170x <= box.max.f170x && Vector3.tmp3.f171y >= box.min.f171y && Vector3.tmp3.f171y <= box.max.f171y && (!hit || t6 < lowest)) {
                    hit = true;
                    lowest = t6;
                }
            }
        }
        if (!hit || intersection2 == null) {
            return hit;
        }
        intersection2.set(ray.direction).mul(lowest).add(ray.origin);
        return hit;
    }

    public static boolean intersectRayBoundsFast(Ray ray, BoundingBox box) {
        float min;
        float max;
        float divX = 1.0f / ray.direction.f170x;
        float divY = 1.0f / ray.direction.f171y;
        float divZ = 1.0f / ray.direction.f172z;
        float a = (box.min.f170x - ray.origin.f170x) * divX;
        float b = (box.max.f170x - ray.origin.f170x) * divX;
        if (a < b) {
            min = a;
            max = b;
        } else {
            min = b;
            max = a;
        }
        float a2 = (box.min.f171y - ray.origin.f171y) * divY;
        float b2 = (box.max.f171y - ray.origin.f171y) * divY;
        if (a2 > b2) {
            float t = a2;
            a2 = b2;
            b2 = t;
        }
        if (a2 > min) {
            min = a2;
        }
        if (b2 < max) {
            max = b2;
        }
        float a3 = (box.min.f172z - ray.origin.f172z) * divZ;
        float b3 = (box.max.f172z - ray.origin.f172z) * divZ;
        if (a3 > b3) {
            float t2 = a3;
            a3 = b3;
            b3 = t2;
        }
        if (a3 > min) {
            min = a3;
        }
        if (b3 < max) {
            max = b3;
        }
        if (max < 0.0f || max < min) {
            return false;
        }
        return true;
    }

    public static boolean intersectRayTriangles(Ray ray, float[] triangles, Vector3 intersection2) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if ((triangles.length / 3) % 3 != 0) {
            throw new RuntimeException("triangle list size is not a multiple of 3");
        }
        for (int i = 0; i < triangles.length - 6; i += 9) {
            if (intersectRayTriangle(ray, tmp1.set(triangles[i], triangles[i + 1], triangles[i + 2]), tmp2.set(triangles[i + 3], triangles[i + 4], triangles[i + 5]), tmp3.set(triangles[i + 6], triangles[i + 7], triangles[i + 8]), tmp)) {
                float dist = ray.origin.tmp().sub(tmp).len2();
                if (dist < min_dist) {
                    min_dist = dist;
                    best.set(tmp);
                    hit = true;
                }
            }
        }
        if (!hit) {
            return false;
        }
        if (intersection2 == null) {
            return true;
        }
        intersection2.set(best);
        return true;
    }

    public static boolean intersectRayTriangles(Ray ray, float[] vertices, short[] indices, int vertexSize, Vector3 intersection2) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (indices.length % 3 != 0) {
            throw new RuntimeException("triangle list size is not a multiple of 3");
        }
        for (int i = 0; i < indices.length; i += 3) {
            int i1 = indices[i] * vertexSize;
            int i2 = indices[i + 1] * vertexSize;
            int i3 = indices[i + 2] * vertexSize;
            if (intersectRayTriangle(ray, tmp1.set(vertices[i1], vertices[i1 + 1], vertices[i1 + 2]), tmp2.set(vertices[i2], vertices[i2 + 1], vertices[i2 + 2]), tmp3.set(vertices[i3], vertices[i3 + 1], vertices[i3 + 2]), tmp)) {
                float dist = ray.origin.tmp().sub(tmp).len2();
                if (dist < min_dist) {
                    min_dist = dist;
                    best.set(tmp);
                    hit = true;
                }
            }
        }
        if (!hit) {
            return false;
        }
        if (intersection2 != null) {
            intersection2.set(best);
        }
        return true;
    }

    public static boolean intersectRayTriangles(Ray ray, List<Vector3> triangles, Vector3 intersection2) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (triangles.size() % 3 != 0) {
            throw new RuntimeException("triangle list size is not a multiple of 3");
        }
        for (int i = 0; i < triangles.size() - 2; i += 3) {
            if (intersectRayTriangle(ray, triangles.get(i), triangles.get(i + 1), triangles.get(i + 2), tmp)) {
                float dist = ray.origin.tmp().sub(tmp).len2();
                if (dist < min_dist) {
                    min_dist = dist;
                    best.set(tmp);
                    hit = true;
                }
            }
        }
        if (!hit) {
            return false;
        }
        if (intersection2 != null) {
            intersection2.set(best);
        }
        return true;
    }

    public static boolean intersectRectangles(Rectangle a, Rectangle b) {
        return a.getX() <= b.getX() + b.getWidth() && a.getX() + a.getWidth() >= b.getX() && a.getY() <= b.getY() + b.getHeight() && a.getY() + a.getHeight() >= b.getY();
    }

    public static boolean intersectLines(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection2) {
        float x1 = p1.f165x;
        float y1 = p1.f166y;
        float x2 = p2.f165x;
        float y2 = p2.f166y;
        float x3 = p3.f165x;
        float y3 = p3.f166y;
        float x4 = p4.f165x;
        float y4 = p4.f166y;
        float det1 = det(x1, y1, x2, y2);
        float det2 = det(x3, y3, x4, y4);
        float det3 = det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
        intersection2.f165x = det(det1, x1 - x2, det2, x3 - x4) / det3;
        intersection2.f166y = det(det1, y1 - y2, det2, y3 - y4) / det3;
        return true;
    }

    public static boolean intersectSegments(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection2) {
        float x1 = p1.f165x;
        float y1 = p1.f166y;
        float x2 = p2.f165x;
        float y2 = p2.f166y;
        float x3 = p3.f165x;
        float y3 = p3.f166y;
        float x4 = p4.f165x;
        float y4 = p4.f166y;
        float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        if (d == 0.0f) {
            return false;
        }
        float ua = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3))) / d;
        float ub = (((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3))) / d;
        if (ua < 0.0f || ua > 1.0f || ub < 0.0f || ub > 1.0f) {
            return false;
        }
        if (intersection2 != null) {
            intersection2.set(((x2 - x1) * ua) + x1, ((y2 - y1) * ua) + y1);
        }
        return true;
    }

    static float det(float a, float b, float c, float d) {
        return (a * d) - (b * c);
    }

    static double detd(double a, double b, double c, double d) {
        return (a * d) - (b * c);
    }

    public static boolean overlapCircles(Circle c1, Circle c2) {
        float x = c1.f146x - c2.f146x;
        float y = c1.f147y - c2.f147y;
        float distance = (x * x) + (y * y);
        float radiusSum = c1.radius + c2.radius;
        return distance <= radiusSum * radiusSum;
    }

    public static boolean overlapRectangles(Rectangle r1, Rectangle r2) {
        if (r1.f161x >= r2.f161x + r2.width || r1.f161x + r1.width <= r2.f161x || r1.f162y >= r2.f162y + r2.height || r1.f162y + r1.height <= r2.f162y) {
            return false;
        }
        return true;
    }

    public static boolean overlapCircleRectangle(Circle c, Rectangle r) {
        float closestX = c.f146x;
        float closestY = c.f147y;
        if (c.f146x < r.f161x) {
            closestX = r.f161x;
        } else if (c.f146x > r.f161x + r.width) {
            closestX = r.f161x + r.width;
        }
        if (c.f147y < r.f162y) {
            closestY = r.f162y;
        } else if (c.f147y > r.f162y + r.height) {
            closestY = r.f162y + r.height;
        }
        float closestX2 = closestX - c.f146x;
        float closestY2 = closestY - c.f147y;
        if ((closestX2 * closestX2) + (closestY2 * closestY2) < c.radius * c.radius) {
            return true;
        }
        return false;
    }

    public static boolean overlapConvexPolygons(Polygon p1, Polygon p2) {
        return overlapConvexPolygons(p1, p2, (MinimumTranslationVector) null);
    }

    public static boolean overlapConvexPolygons(Polygon p1, Polygon p2, MinimumTranslationVector mtv) {
        return overlapConvexPolygons(p1.getTransformedVertices(), p2.getTransformedVertices(), mtv);
    }

    public static boolean overlapConvexPolygons(float[] verts1, float[] verts2, MinimumTranslationVector mtv) {
        float overlap = Float.MAX_VALUE;
        float smallestAxisX = 0.0f;
        float smallestAxisY = 0.0f;
        int numAxes1 = verts1.length;
        for (int i = 0; i < numAxes1; i += 2) {
            float x1 = verts1[i];
            float y1 = verts1[i + 1];
            float x2 = verts1[(i + 2) % numAxes1];
            float axisX = y1 - verts1[(i + 3) % numAxes1];
            float axisY = -(x1 - x2);
            float length = (float) Math.sqrt((double) ((axisX * axisX) + (axisY * axisY)));
            float axisX2 = axisX / length;
            float axisY2 = axisY / length;
            float min1 = (verts1[0] * axisX2) + (verts1[1] * axisY2);
            float max1 = min1;
            for (int j = 2; j < verts1.length; j += 2) {
                float p = (verts1[j] * axisX2) + (verts1[j + 1] * axisY2);
                if (p < min1) {
                    min1 = p;
                } else if (p > max1) {
                    max1 = p;
                }
            }
            float min2 = (verts2[0] * axisX2) + (verts2[1] * axisY2);
            float max2 = min2;
            for (int j2 = 2; j2 < verts2.length; j2 += 2) {
                float p2 = (verts2[j2] * axisX2) + (verts2[j2 + 1] * axisY2);
                if (p2 < min2) {
                    min2 = p2;
                } else if (p2 > max2) {
                    max2 = p2;
                }
            }
            if ((min1 > min2 || max1 < min2) && (min2 > min1 || max2 < min1)) {
                return false;
            }
            float o = Math.min(max1, max2) - Math.max(min1, min2);
            if ((min1 < min2 && max1 > max2) || (min2 < min1 && max2 > max1)) {
                float mins = Math.abs(min1 - min2);
                float maxs = Math.abs(max1 - max2);
                if (mins < maxs) {
                    axisX2 = -axisX2;
                    axisY2 = -axisY2;
                    o += mins;
                } else {
                    o += maxs;
                }
            }
            if (o < overlap) {
                overlap = o;
                smallestAxisX = axisX2;
                smallestAxisY = axisY2;
            }
        }
        int numAxes2 = verts2.length;
        for (int i2 = 0; i2 < numAxes2; i2 += 2) {
            float x12 = verts2[i2];
            float y12 = verts2[i2 + 1];
            float x22 = verts2[(i2 + 2) % numAxes2];
            float axisX3 = y12 - verts2[(i2 + 3) % numAxes2];
            float axisY3 = -(x12 - x22);
            float length2 = (float) Math.sqrt((double) ((axisX3 * axisX3) + (axisY3 * axisY3)));
            float axisX4 = axisX3 / length2;
            float axisY4 = axisY3 / length2;
            float min12 = (verts1[0] * axisX4) + (verts1[1] * axisY4);
            float max12 = min12;
            for (int j3 = 2; j3 < verts1.length; j3 += 2) {
                float p3 = (verts1[j3] * axisX4) + (verts1[j3 + 1] * axisY4);
                if (p3 < min12) {
                    min12 = p3;
                } else if (p3 > max12) {
                    max12 = p3;
                }
            }
            float min22 = (verts2[0] * axisX4) + (verts2[1] * axisY4);
            float max22 = min22;
            for (int j4 = 2; j4 < verts2.length; j4 += 2) {
                float p4 = (verts2[j4] * axisX4) + (verts2[j4 + 1] * axisY4);
                if (p4 < min22) {
                    min22 = p4;
                } else if (p4 > max22) {
                    max22 = p4;
                }
            }
            if ((min12 > min22 || max12 < min22) && (min22 > min12 || max22 < min12)) {
                return false;
            }
            float o2 = Math.min(max12, max22) - Math.max(min12, min22);
            if ((min12 < min22 && max12 > max22) || (min22 < min12 && max22 > max12)) {
                float mins2 = Math.abs(min12 - min22);
                float maxs2 = Math.abs(max12 - max22);
                if (mins2 < maxs2) {
                    axisX4 = -axisX4;
                    axisY4 = -axisY4;
                    o2 += mins2;
                } else {
                    o2 += maxs2;
                }
            }
            if (o2 < overlap) {
                overlap = o2;
                smallestAxisX = axisX4;
                smallestAxisY = axisY4;
            }
        }
        if (mtv != null) {
            mtv.normal.set(smallestAxisX, smallestAxisY);
            mtv.depth = overlap;
        }
        return true;
    }

    public static void splitTriangle(float[] triangle, Plane plane, SplitTriangle split) {
        boolean r1;
        boolean r2;
        boolean r3;
        int i;
        boolean z;
        boolean z2 = true;
        int stride = triangle.length / 3;
        if (plane.testPoint(triangle[0], triangle[1], triangle[2]) == Plane.PlaneSide.Back) {
            r1 = true;
        } else {
            r1 = false;
        }
        if (plane.testPoint(triangle[stride + 0], triangle[stride + 1], triangle[stride + 2]) == Plane.PlaneSide.Back) {
            r2 = true;
        } else {
            r2 = false;
        }
        if (plane.testPoint(triangle[(stride * 2) + 0], triangle[(stride * 2) + 1], triangle[(stride * 2) + 2]) == Plane.PlaneSide.Back) {
            r3 = true;
        } else {
            r3 = false;
        }
        split.reset();
        if (r1 == r2 && r2 == r3) {
            split.total = 1;
            if (r1) {
                split.numBack = 1;
                System.arraycopy(triangle, 0, split.back, 0, triangle.length);
                return;
            }
            split.numFront = 1;
            System.arraycopy(triangle, 0, split.front, 0, triangle.length);
            return;
        }
        split.total = 3;
        if (r1) {
            i = 1;
        } else {
            i = 0;
        }
        split.numFront = (r3 ? 1 : 0) + i + (r2 ? 1 : 0);
        split.numBack = split.total - split.numFront;
        split.setSide(r1);
        int second = stride;
        if (r1 != r2) {
            splitEdge(triangle, 0, second, stride, plane, split.edgeSplit, 0);
            split.add(triangle, 0, stride);
            split.add(split.edgeSplit, 0, stride);
            if (!split.getSide()) {
                z = true;
            } else {
                z = false;
            }
            split.setSide(z);
            split.add(split.edgeSplit, 0, stride);
        } else {
            split.add(triangle, 0, stride);
        }
        int first = stride;
        int second2 = stride + stride;
        if (r2 != r3) {
            splitEdge(triangle, first, second2, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first, stride);
            split.add(split.edgeSplit, 0, stride);
            split.setSide(!split.getSide());
            split.add(split.edgeSplit, 0, stride);
        } else {
            split.add(triangle, first, stride);
        }
        int first2 = stride + stride;
        if (r3 != r1) {
            splitEdge(triangle, first2, 0, stride, plane, split.edgeSplit, 0);
            split.add(triangle, first2, stride);
            split.add(split.edgeSplit, 0, stride);
            if (split.getSide()) {
                z2 = false;
            }
            split.setSide(z2);
            split.add(split.edgeSplit, 0, stride);
        } else {
            split.add(triangle, first2, stride);
        }
        if (split.numFront == 2) {
            System.arraycopy(split.front, stride * 2, split.front, stride * 3, stride * 2);
            System.arraycopy(split.front, 0, split.front, stride * 5, stride);
            return;
        }
        System.arraycopy(split.back, stride * 2, split.back, stride * 3, stride * 2);
        System.arraycopy(split.back, 0, split.back, stride * 5, stride);
    }

    private static void splitEdge(float[] vertices, int s, int e, int stride, Plane plane, float[] split, int offset) {
        float t = intersectLinePlane(vertices[s], vertices[s + 1], vertices[s + 2], vertices[e], vertices[e + 1], vertices[e + 2], plane, intersection);
        split[offset + 0] = intersection.f170x;
        split[offset + 1] = intersection.f171y;
        split[offset + 2] = intersection.f172z;
        for (int i = 3; i < stride; i++) {
            float a = vertices[s + i];
            split[offset + i] = ((vertices[e + i] - a) * t) + a;
        }
    }

    public static void main(String[] args) {
        Plane plane = new Plane(new Vector3(1.0f, 0.0f, 0.0f), 0.0f);
        SplitTriangle split = new SplitTriangle(3);
        splitTriangle(new float[]{-10.0f, 0.0f, 10.0f, -1.0f, 0.0f, 0.0f, -10.0f, 0.0f, 10.0f}, plane, split);
        System.out.println(split);
        splitTriangle(new float[]{-10.0f, 0.0f, 10.0f, 10.0f, 0.0f, 0.0f, -10.0f, 0.0f, -10.0f}, plane, split);
        System.out.println(split);
    }

    public static class SplitTriangle {
        public float[] back;
        int backOffset = 0;
        float[] edgeSplit;
        public float[] front;
        boolean frontCurrent = false;
        int frontOffset = 0;
        public int numBack;
        public int numFront;
        public int total;

        public SplitTriangle(int numAttributes) {
            this.front = new float[(numAttributes * 3 * 2)];
            this.back = new float[(numAttributes * 3 * 2)];
            this.edgeSplit = new float[numAttributes];
        }

        public String toString() {
            return "SplitTriangle [front=" + Arrays.toString(this.front) + ", back=" + Arrays.toString(this.back) + ", numFront=" + this.numFront + ", numBack=" + this.numBack + ", total=" + this.total + "]";
        }

        /* access modifiers changed from: package-private */
        public void setSide(boolean front2) {
            this.frontCurrent = front2;
        }

        /* access modifiers changed from: package-private */
        public boolean getSide() {
            return this.frontCurrent;
        }

        /* access modifiers changed from: package-private */
        public void add(float[] vertex, int offset, int stride) {
            if (this.frontCurrent) {
                System.arraycopy(vertex, offset, this.front, this.frontOffset, stride);
                this.frontOffset += stride;
                return;
            }
            System.arraycopy(vertex, offset, this.back, this.backOffset, stride);
            this.backOffset += stride;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.frontCurrent = false;
            this.frontOffset = 0;
            this.backOffset = 0;
            this.numFront = 0;
            this.numBack = 0;
            this.total = 0;
        }
    }
}
