package com.badlogic.gdx.backends.android;

import android.os.Build;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.surfaceview.GLBaseSurfaceViewLW;

public abstract class AndroidLiveWallpaperService extends WallpaperService {
    static boolean DEBUG = false;
    protected static volatile int runningEngines = 0;
    final String TAG = "AndroidLiveWallpaperService";

    public abstract AndroidApplicationConfiguration createConfig();

    public abstract ApplicationListener createListener(boolean z);

    public abstract void offsetChange(ApplicationListener applicationListener, float f, float f2, float f3, float f4, int i, int i2);

    public void onCreate() {
        if (DEBUG) {
            Log.d("AndroidLiveWallpaperService", " > LibdgxWallpaperService - onCreate()");
        }
        super.onCreate();
    }

    public WallpaperService.Engine onCreateEngine() {
        return new AndroidWallpaperEngine();
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d("AndroidLiveWallpaperService", " > LibdgxWallpaperService - onDestroy()");
        }
        super.onDestroy();
    }

    public class AndroidWallpaperEngine extends WallpaperService.Engine {
        protected AndroidLiveWallpaper app;
        protected ApplicationListener listener;
        protected GLBaseSurfaceViewLW view;

        public AndroidWallpaperEngine() {
            super(AndroidLiveWallpaperService.this);
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > MyEngine() " + hashCode());
            }
        }

        public Bundle onCommand(String pAction, int pX, int pY, int pZ, Bundle pExtras, boolean pResultRequested) {
            if (AndroidLiveWallpaperService.DEBUG) {
                Log.d("AndroidLiveWallpaperService", " > onCommand(" + pAction + " " + pX + " " + pY + " " + pZ + " " + pExtras + " " + pResultRequested + ")");
            }
            return super.onCommand(pAction, pX, pY, pZ, pExtras, pResultRequested);
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            AndroidLiveWallpaperService.runningEngines++;
            if (AndroidLiveWallpaperService.DEBUG) {
                Log.d("AndroidLiveWallpaperService", " > onCreate() " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            super.onCreate(surfaceHolder);
            this.app = new AndroidLiveWallpaper(AndroidLiveWallpaperService.this, this);
            AndroidApplicationConfiguration config = AndroidLiveWallpaperService.this.createConfig();
            this.listener = AndroidLiveWallpaperService.this.createListener(isPreview());
            this.app.initialize(this.listener, config);
            this.view = ((AndroidGraphicsLiveWallpaper) this.app.getGraphics()).getView();
            if (config.getTouchEventsForLiveWallpaper && Integer.parseInt(Build.VERSION.SDK) < 9) {
                setTouchEventsEnabled(true);
            }
        }

        public void onDestroy() {
            AndroidLiveWallpaperService.runningEngines--;
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onDestroy() " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            this.view.onDestroy();
            if (this.listener != null) {
                this.listener.dispose();
            }
            this.app.onDestroy();
            super.onDestroy();
        }

        public void onPause() {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onPause() " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            this.app.onPause();
            this.view.onPause();
        }

        public void onResume() {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onResume() " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            this.app.onResume();
            this.view.onResume();
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onSurfaceChanged() " + isPreview() + " " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onSurfaceCreated() " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            super.onSurfaceCreated(holder);
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onSurfaceDestroyed() " + hashCode() + ", running: " + AndroidLiveWallpaperService.runningEngines);
            }
            super.onSurfaceDestroyed(holder);
        }

        public void onVisibilityChanged(boolean visible) {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onVisibilityChanged(" + visible + ") " + hashCode());
            }
            if (visible) {
                onResume();
            } else {
                onPause();
            }
            super.onVisibilityChanged(visible);
        }

        public void onTouchEvent(MotionEvent event) {
            this.app.input.onTouch((View) null, event);
        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            if (AndroidLiveWallpaperService.DEBUG) {
                AndroidLiveWallpaperService.this.getClass();
                Log.d("AndroidLiveWallpaperService", " > onOffsetChanged(" + xOffset + " " + yOffset + " " + xOffsetStep + " " + yOffsetStep + " " + xPixelOffset + " " + yPixelOffset + ") " + hashCode());
            }
            final float f = xOffset;
            final float f2 = yOffset;
            final float f3 = xOffsetStep;
            final float f4 = yOffsetStep;
            final int i = xPixelOffset;
            final int i2 = yPixelOffset;
            this.app.postRunnable(new Runnable() {
                public void run() {
                    AndroidLiveWallpaperService.this.offsetChange(AndroidWallpaperEngine.this.listener, f, f2, f3, f4, i, i2);
                }
            });
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
        }
    }
}
