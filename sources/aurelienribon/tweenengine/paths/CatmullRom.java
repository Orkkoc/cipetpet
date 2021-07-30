package aurelienribon.tweenengine.paths;

import aurelienribon.tweenengine.TweenPath;

public class CatmullRom implements TweenPath {
    public float compute(float t, float[] points, int pointsCnt) {
        int segment = Math.min(Math.max((int) Math.floor((double) (((float) (pointsCnt - 1)) * t)), 0), pointsCnt - 2);
        float t2 = (((float) (pointsCnt - 1)) * t) - ((float) segment);
        if (segment == 0) {
            return catmullRomSpline(points[0], points[0], points[1], points[2], t2);
        } else if (segment == pointsCnt - 2) {
            return catmullRomSpline(points[pointsCnt - 3], points[pointsCnt - 2], points[pointsCnt - 1], points[pointsCnt - 1], t2);
        } else {
            return catmullRomSpline(points[segment - 1], points[segment], points[segment + 1], points[segment + 2], t2);
        }
    }

    private float catmullRomSpline(float a, float b, float c, float d, float t) {
        return (b * (((((2.0f * t) * t) * t) - ((3.0f * t) * t)) + 1.0f)) + (c * ((-2.0f * t * t * t) + (3.0f * t * t))) + ((c - a) * 0.5f * ((((t * t) * t) - ((2.0f * t) * t)) + t)) + ((d - b) * 0.5f * (((t * t) * t) - (t * t)));
    }
}
