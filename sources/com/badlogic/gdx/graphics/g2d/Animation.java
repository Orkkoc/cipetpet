package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Animation {
    public static final int LOOP = 2;
    public static final int LOOP_PINGPONG = 4;
    public static final int LOOP_RANDOM = 5;
    public static final int LOOP_REVERSED = 3;
    public static final int NORMAL = 0;
    public static final int REVERSED = 1;
    public final float animationDuration;
    public final float frameDuration;
    final TextureRegion[] keyFrames;
    private int playMode = 0;

    public Animation(float frameDuration2, Array<? extends TextureRegion> keyFrames2) {
        this.frameDuration = frameDuration2;
        this.animationDuration = ((float) keyFrames2.size) * frameDuration2;
        this.keyFrames = new TextureRegion[keyFrames2.size];
        int n = keyFrames2.size;
        for (int i = 0; i < n; i++) {
            this.keyFrames[i] = (TextureRegion) keyFrames2.get(i);
        }
        this.playMode = 0;
    }

    public Animation(float frameDuration2, Array<? extends TextureRegion> keyFrames2, int playType) {
        this.frameDuration = frameDuration2;
        this.animationDuration = ((float) keyFrames2.size) * frameDuration2;
        this.keyFrames = new TextureRegion[keyFrames2.size];
        int n = keyFrames2.size;
        for (int i = 0; i < n; i++) {
            this.keyFrames[i] = (TextureRegion) keyFrames2.get(i);
        }
        this.playMode = playType;
    }

    public Animation(float frameDuration2, TextureRegion... keyFrames2) {
        this.frameDuration = frameDuration2;
        this.animationDuration = ((float) keyFrames2.length) * frameDuration2;
        this.keyFrames = keyFrames2;
        this.playMode = 0;
    }

    public TextureRegion getKeyFrame(float stateTime, boolean looping) {
        if (!looping || !(this.playMode == 0 || this.playMode == 1)) {
            if (!(looping || this.playMode == 0 || this.playMode == 1)) {
                if (this.playMode == 3) {
                    this.playMode = 1;
                } else {
                    this.playMode = 2;
                }
            }
        } else if (this.playMode == 0) {
            this.playMode = 2;
        } else {
            this.playMode = 3;
        }
        return getKeyFrame(stateTime);
    }

    public TextureRegion getKeyFrame(float stateTime) {
        return this.keyFrames[getKeyFrameIndex(stateTime)];
    }

    public int getKeyFrameIndex(float stateTime) {
        int frameNumber;
        int frameNumber2 = (int) (stateTime / this.frameDuration);
        if (this.keyFrames.length == 1) {
            return 0;
        }
        switch (this.playMode) {
            case 0:
                frameNumber = Math.min(this.keyFrames.length - 1, frameNumber2);
                break;
            case 1:
                frameNumber = Math.max((this.keyFrames.length - frameNumber2) - 1, 0);
                break;
            case 2:
                frameNumber = frameNumber2 % this.keyFrames.length;
                break;
            case 3:
                frameNumber = (this.keyFrames.length - (frameNumber2 % this.keyFrames.length)) - 1;
                break;
            case 4:
                frameNumber = frameNumber2 % ((this.keyFrames.length * 2) - 2);
                if (frameNumber >= this.keyFrames.length) {
                    frameNumber = (this.keyFrames.length - 2) - (frameNumber - this.keyFrames.length);
                    break;
                }
                break;
            case 5:
                frameNumber = MathUtils.random(this.keyFrames.length - 1);
                break;
            default:
                frameNumber = Math.min(this.keyFrames.length - 1, frameNumber2);
                break;
        }
        return frameNumber;
    }

    public void setPlayMode(int playMode2) {
        this.playMode = playMode2;
    }

    public boolean isAnimationFinished(float stateTime) {
        boolean z = true;
        if (this.playMode != 0 && this.playMode != 1) {
            return false;
        }
        if (this.keyFrames.length - 1 >= ((int) (stateTime / this.frameDuration))) {
            z = false;
        }
        return z;
    }
}
