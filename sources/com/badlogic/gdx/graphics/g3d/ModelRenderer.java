package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.model.AnimatedModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public interface ModelRenderer {
    void begin();

    void draw(AnimatedModel animatedModel, AnimatedModelInstance animatedModelInstance);

    void draw(StillModel stillModel, StillModelInstance stillModelInstance);

    void end();
}
