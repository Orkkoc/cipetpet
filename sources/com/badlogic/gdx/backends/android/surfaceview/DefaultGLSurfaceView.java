package com.badlogic.gdx.backends.android.surfaceview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;

public class DefaultGLSurfaceView extends GLSurfaceView {
    final ResolutionStrategy resolutionStrategy;

    public DefaultGLSurfaceView(Context context, ResolutionStrategy resolutionStrategy2) {
        super(context);
        this.resolutionStrategy = resolutionStrategy2;
    }

    public DefaultGLSurfaceView(Context context, AttributeSet attrs, ResolutionStrategy resolutionStrategy2) {
        super(context, attrs);
        this.resolutionStrategy = resolutionStrategy2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ResolutionStrategy.MeasuredDimension measures = this.resolutionStrategy.calcMeasures(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measures.width, measures.height);
    }
}
