package com.badlogic.gdx.graphics.g3d.loaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelLoaderHints;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public interface StillModelLoader extends ModelLoader {
    StillModel load(FileHandle fileHandle, ModelLoaderHints modelLoaderHints);
}
