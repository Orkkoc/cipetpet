package com.badlogic.gdx.backends.android;

import android.content.res.AssetManager;
import android.os.Environment;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

public class AndroidFiles implements Files {
    protected final AssetManager assets;
    protected final String localpath;
    protected final String sdcard = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/");

    public AndroidFiles(AssetManager assets2) {
        this.assets = assets2;
        this.localpath = this.sdcard;
    }

    public AndroidFiles(AssetManager assets2, String localpath2) {
        this.assets = assets2;
        this.localpath = !localpath2.endsWith("/") ? localpath2 + "/" : localpath2;
    }

    public FileHandle getFileHandle(String path, Files.FileType type) {
        return new AndroidFileHandle(type == Files.FileType.Internal ? this.assets : null, path, type);
    }

    public FileHandle classpath(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.Classpath);
    }

    public FileHandle internal(String path) {
        return new AndroidFileHandle(this.assets, path, Files.FileType.Internal);
    }

    public FileHandle external(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.External);
    }

    public FileHandle absolute(String path) {
        return new AndroidFileHandle((AssetManager) null, path, Files.FileType.Absolute);
    }

    public FileHandle local(String path) {
        return new AndroidFileHandle((AssetManager) null, this.localpath + path, Files.FileType.Local);
    }

    public String getExternalStoragePath() {
        return this.sdcard;
    }

    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public String getLocalStoragePath() {
        return this.localpath;
    }

    public boolean isLocalStorageAvailable() {
        return true;
    }
}
