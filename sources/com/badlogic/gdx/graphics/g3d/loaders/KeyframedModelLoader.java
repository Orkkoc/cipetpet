package com.badlogic.gdx.graphics.g3d.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;

public interface KeyframedModelLoader extends ModelLoader {
    KeyframedModel load(FileHandle fileHandle, ModelLoaderHints modelLoaderHints);
}
