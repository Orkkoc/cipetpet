package com.badlogic.gdx.backends.android.surfaceview;

import android.service.wallpaper.WallpaperService;
import android.util.AttributeSet;

public class DefaultGLSurfaceViewLW extends GLBaseSurfaceViewLW {
    final ResolutionStrategy resolutionStrategy;

    public DefaultGLSurfaceViewLW(WallpaperService.Engine engine, ResolutionStrategy resolutionStrategy2) {
        super(engine);
        this.resolutionStrategy = resolutionStrategy2;
    }

    public DefaultGLSurfaceViewLW(WallpaperService.Engine engine, AttributeSet attrs, ResolutionStrategy resolutionStrategy2) {
        super(engine, attrs);
        this.resolutionStrategy = resolutionStrategy2;
    }
}
