package com.badlogic.gdx.graphics.g3d.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.model.Model;

public interface ModelLoader {
    Model load(FileHandle fileHandle, ModelLoaderHints modelLoaderHints);
}
