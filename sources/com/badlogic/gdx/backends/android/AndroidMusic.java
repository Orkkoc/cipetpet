package com.badlogic.gdx.backends.android;

import android.media.MediaPlayer;
import com.badlogic.gdx.audio.Music;
import java.io.IOException;

public class AndroidMusic implements Music {
    private final AndroidAudio audio;
    private boolean isPrepared = true;
    private MediaPlayer player;
    protected boolean wasPlaying = false;

    AndroidMusic(AndroidAudio audio2, MediaPlayer player2) {
        this.audio = audio2;
        this.player = player2;
    }

    public void dispose() {
        if (this.player != null) {
            try {
                if (this.player.isPlaying()) {
                    this.player.stop();
                }
                this.player.release();
                this.player = null;
                synchronized (this.audio.musics) {
                    this.audio.musics.remove(this);
                }
            } catch (Throwable th) {
                this.player = null;
                synchronized (this.audio.musics) {
                    this.audio.musics.remove(this);
                    throw th;
                }
            }
        }
    }

    public boolean isLooping() {
        return this.player.isLooping();
    }

    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    public void pause() {
        if (this.player.isPlaying()) {
            this.player.pause();
        }
    }

    public void play() {
        if (!this.player.isPlaying()) {
            try {
                if (!this.isPrepared) {
                    this.player.prepare();
                    this.isPrepared = true;
                }
                this.player.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void setLooping(boolean isLooping) {
        this.player.setLooping(isLooping);
    }

    public void setVolume(float volume) {
        this.player.setVolume(volume, volume);
    }

    public void stop() {
        if (this.isPrepared) {
            this.player.seekTo(0);
        }
        this.player.stop();
        this.isPrepared = false;
    }

    public float getPosition() {
        return ((float) this.player.getCurrentPosition()) / 1000.0f;
    }
}
