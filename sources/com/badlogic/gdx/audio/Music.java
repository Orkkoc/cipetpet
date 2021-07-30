package com.badlogic.gdx.audio;

import com.badlogic.gdx.utils.Disposable;

public interface Music extends Disposable {
    void dispose();

    float getPosition();

    boolean isLooping();

    boolean isPlaying();

    void pause();

    void play();

    void setLooping(boolean z);

    void setVolume(float f);

    void stop();
}
