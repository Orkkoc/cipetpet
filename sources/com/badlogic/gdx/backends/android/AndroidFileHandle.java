package com.badlogic.gdx.backends.android;

import android.content.res.AssetManager;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AndroidFileHandle extends FileHandle {
    final AssetManager assets;

    AndroidFileHandle(AssetManager assets2, String fileName, Files.FileType type) {
        super(fileName.replace('\\', '/'), type);
        this.assets = assets2;
    }

    AndroidFileHandle(AssetManager assets2, File file, Files.FileType type) {
        super(file, type);
        this.assets = assets2;
    }

    public FileHandle child(String name) {
        String name2 = name.replace('\\', '/');
        if (this.file.getPath().length() == 0) {
            return new AndroidFileHandle(this.assets, new File(name2), this.type);
        }
        return new AndroidFileHandle(this.assets, new File(this.file, name2), this.type);
    }

    public FileHandle sibling(String name) {
        String name2 = name.replace('\\', '/');
        if (this.file.getPath().length() != 0) {
            return new AndroidFileHandle(this.assets, new File(this.file.getParent(), name2), this.type);
        }
        throw new GdxRuntimeException("Cannot get the sibling of the root.");
    }

    public FileHandle parent() {
        File parent = this.file.getParentFile();
        if (parent == null) {
            if (this.type == Files.FileType.Absolute) {
                parent = new File("/");
            } else {
                parent = new File("");
            }
        }
        return new AndroidFileHandle(this.assets, parent, this.type);
    }

    public InputStream read() {
        if (this.type != Files.FileType.Internal) {
            return super.read();
        }
        try {
            return this.assets.open(this.file.getPath());
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public FileHandle[] list() {
        if (this.type != Files.FileType.Internal) {
            return super.list();
        }
        try {
            String[] relativePaths = this.assets.list(this.file.getPath());
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int n = handles.length;
            for (int i = 0; i < n; i++) {
                handles[i] = new AndroidFileHandle(this.assets, new File(this.file, relativePaths[i]), this.type);
            }
            return handles;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error listing children: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public FileHandle[] list(String suffix) {
        if (this.type != Files.FileType.Internal) {
            return super.list();
        }
        try {
            String[] relativePaths = this.assets.list(this.file.getPath());
            FileHandle[] handles = new FileHandle[relativePaths.length];
            int count = 0;
            int n = handles.length;
            for (int i = 0; i < n; i++) {
                String path = relativePaths[i];
                if (path.endsWith(suffix)) {
                    handles[count] = new AndroidFileHandle(this.assets, new File(this.file, path), this.type);
                    count++;
                }
            }
            if (count >= relativePaths.length) {
                return handles;
            }
            FileHandle[] newHandles = new FileHandle[count];
            System.arraycopy(handles, 0, newHandles, 0, count);
            return newHandles;
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error listing children: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public boolean isDirectory() {
        if (this.type != Files.FileType.Internal) {
            return super.isDirectory();
        }
        try {
            if (this.assets.list(this.file.getPath()).length > 0) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean exists() {
        if (this.type != Files.FileType.Internal) {
            return super.exists();
        }
        String fileName = this.file.getPath();
        try {
            this.assets.open(fileName).close();
            return true;
        } catch (Exception e) {
            try {
                if (this.assets.list(fileName).length <= 0) {
                    return false;
                }
                return true;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public long length() {
        if (this.type == Files.FileType.Internal) {
            try {
                return this.assets.openFd(this.file.getPath()).getLength();
            } catch (IOException e) {
            }
        }
        return super.length();
    }

    public long lastModified() {
        return super.lastModified();
    }
}
