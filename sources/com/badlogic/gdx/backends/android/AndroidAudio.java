package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AndroidAudio implements Audio {
    private final AudioManager manager;
    protected final List<AndroidMusic> musics = new ArrayList();
    private final SoundPool soundPool;

    public AndroidAudio(Context context, AndroidApplicationConfiguration config) {
        this.soundPool = new SoundPool(config.maxSimultaneousSounds, 3, 100);
        this.manager = (AudioManager) context.getSystemService("audio");
        if (context instanceof Activity) {
            ((Activity) context).setVolumeControlStream(3);
        }
    }

    /* access modifiers changed from: protected */
    public void pause() {
        synchronized (this.musics) {
            for (AndroidMusic music : this.musics) {
                if (music.isPlaying()) {
                    music.wasPlaying = true;
                    music.pause();
                } else {
                    music.wasPlaying = false;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void resume() {
        synchronized (this.musics) {
            for (int i = 0; i < this.musics.size(); i++) {
                if (this.musics.get(i).wasPlaying) {
                    this.musics.get(i).play();
                }
            }
        }
    }

    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        return new AndroidAudioDevice(samplingRate, isMono);
    }

    public Music newMusic(FileHandle file) {
        AndroidMusic music;
        AndroidFileHandle aHandle = (AndroidFileHandle) file;
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (aHandle.type() == Files.FileType.Internal) {
            try {
                AssetFileDescriptor descriptor = aHandle.assets.openFd(aHandle.path());
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                mediaPlayer.prepare();
                music = new AndroidMusic(this, mediaPlayer);
                synchronized (this.musics) {
                    this.musics.add(music);
                }
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
            }
        } else {
            try {
                mediaPlayer.setDataSource(aHandle.file().getPath());
                mediaPlayer.prepare();
                music = new AndroidMusic(this, mediaPlayer);
                synchronized (this.musics) {
                    this.musics.add(music);
                }
            } catch (Exception ex2) {
                throw new GdxRuntimeException("Error loading audio file: " + file, ex2);
            }
        }
        return music;
    }

    public Sound newSound(FileHandle file) {
        AndroidFileHandle aHandle = (AndroidFileHandle) file;
        if (aHandle.type() == Files.FileType.Internal) {
            try {
                AssetFileDescriptor descriptor = aHandle.assets.openFd(aHandle.path());
                AndroidSound sound = new AndroidSound(this.soundPool, this.manager, this.soundPool.load(descriptor, 1));
                descriptor.close();
                return sound;
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error loading audio file: " + file + "\nNote: Internal audio files must be placed in the assets directory.", ex);
            }
        } else {
            try {
                return new AndroidSound(this.soundPool, this.manager, this.soundPool.load(aHandle.file().getPath(), 1));
            } catch (Exception ex2) {
                throw new GdxRuntimeException("Error loading audio file: " + file, ex2);
            }
        }
    }

    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        return new AndroidAudioRecorder(samplingRate, isMono);
    }

    public void dispose() {
        synchronized (this.musics) {
            Iterator i$ = new ArrayList<>(this.musics).iterator();
            while (i$.hasNext()) {
                i$.next().dispose();
            }
        }
        this.soundPool.release();
    }
}
