package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLU;

public class AndroidGLU implements GLU {
    public void gluLookAt(GL10 gl, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        android.opengl.GLU.gluLookAt(((AndroidGL10) gl).f58gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void gluOrtho2D(GL10 gl, float left, float right, float bottom, float top) {
        android.opengl.GLU.gluOrtho2D(((AndroidGL10) gl).f58gl, left, right, bottom, top);
    }

    public void gluPerspective(GL10 gl, float fovy, float aspect, float zNear, float zFar) {
        android.opengl.GLU.gluPerspective(((AndroidGL10) gl).f58gl, fovy, aspect, zNear, zFar);
    }

    public boolean gluProject(float objX, float objY, float objZ, float[] model, int modelOffset, float[] project, int projectOffset, int[] view, int viewOffset, float[] win, int winOffset) {
        if (android.opengl.GLU.gluProject(objX, objY, objZ, model, modelOffset, project, projectOffset, view, viewOffset, win, winOffset) == 1) {
            return true;
        }
        return false;
    }

    public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int modelOffset, float[] project, int projectOffset, int[] view, int viewOffset, float[] obj, int objOffset) {
        if (android.opengl.GLU.gluUnProject(winX, winY, winZ, model, modelOffset, project, projectOffset, view, viewOffset, obj, objOffset) == 1) {
            return true;
        }
        return false;
    }
}
