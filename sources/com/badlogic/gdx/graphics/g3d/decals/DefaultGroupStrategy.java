package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

public class DefaultGroupStrategy implements GroupStrategy {
    private static final int GROUP_BLEND = 1;
    private static final int GROUP_OPAQUE = 0;

    public int decideGroup(Decal decal) {
        return decal.getMaterial().isOpaque() ? 0 : 1;
    }

    public void beforeGroup(int group, Array<Decal> array) {
        if (group == 1) {
            Gdx.gl10.glEnable(3042);
        }
    }

    public void afterGroup(int group) {
        if (group == 1) {
            Gdx.gl10.glDisable(3042);
        }
    }

    public void beforeGroups() {
        Gdx.gl10.glEnable(3553);
    }

    public void afterGroups() {
        Gdx.gl10.glDisable(3553);
    }

    public ShaderProgram getGroupShader(int group) {
        return null;
    }
}
