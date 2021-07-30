package com.badlogic.gdx.graphics;

import java.nio.Buffer;
import java.nio.IntBuffer;

public interface GLCommon {
    public static final int GL_GENERATE_MIPMAP = 33169;
    public static final int GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 34047;
    public static final int GL_TEXTURE_MAX_ANISOTROPY_EXT = 34046;

    void glActiveTexture(int i);

    void glBindTexture(int i, int i2);

    void glBlendFunc(int i, int i2);

    void glClear(int i);

    void glClearColor(float f, float f2, float f3, float f4);

    void glClearDepthf(float f);

    void glClearStencil(int i);

    void glColorMask(boolean z, boolean z2, boolean z3, boolean z4);

    void glCompressedTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer);

    void glCompressedTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    void glCopyTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    void glCopyTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    void glCullFace(int i);

    void glDeleteTextures(int i, IntBuffer intBuffer);

    void glDepthFunc(int i);

    void glDepthMask(boolean z);

    void glDepthRangef(float f, float f2);

    void glDisable(int i);

    void glDrawArrays(int i, int i2, int i3);

    void glDrawElements(int i, int i2, int i3, Buffer buffer);

    void glEnable(int i);

    void glFinish();

    void glFlush();

    void glFrontFace(int i);

    void glGenTextures(int i, IntBuffer intBuffer);

    int glGetError();

    void glGetIntegerv(int i, IntBuffer intBuffer);

    String glGetString(int i);

    void glHint(int i, int i2);

    void glLineWidth(float f);

    void glPixelStorei(int i, int i2);

    void glPolygonOffset(float f, float f2);

    void glReadPixels(int i, int i2, int i3, int i4, int i5, int i6, Buffer buffer);

    void glScissor(int i, int i2, int i3, int i4);

    void glStencilFunc(int i, int i2, int i3);

    void glStencilMask(int i);

    void glStencilOp(int i, int i2, int i3);

    void glTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    void glTexParameterf(int i, int i2, float f);

    void glTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    void glViewport(int i, int i2, int i3, int i4);
}
