package com.badlogic.gdx.graphics;

public interface GLU {
    void gluLookAt(GL10 gl10, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9);

    void gluOrtho2D(GL10 gl10, float f, float f2, float f3, float f4);

    void gluPerspective(GL10 gl10, float f, float f2, float f3, float f4);

    boolean gluProject(float f, float f2, float f3, float[] fArr, int i, float[] fArr2, int i2, int[] iArr, int i3, float[] fArr3, int i4);

    boolean gluUnProject(float f, float f2, float f3, float[] fArr, int i, float[] fArr2, int i2, int[] iArr, int i3, float[] fArr3, int i4);
}
