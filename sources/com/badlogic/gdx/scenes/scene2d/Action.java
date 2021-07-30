package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.utils.Pool;

public abstract class Action implements Pool.Poolable {
    protected Actor actor;
    private Pool pool;

    public abstract boolean act(float f);

    public void restart() {
    }

    public Actor getActor() {
        return this.actor;
    }

    public void setActor(Actor actor2) {
        this.actor = actor2;
        if (actor2 != null) {
            return;
        }
        if (this.pool != null) {
            this.pool.free(this);
            this.pool = null;
            return;
        }
        reset();
    }

    public void reset() {
        restart();
    }

    public Pool getPool() {
        return this.pool;
    }

    public void setPool(Pool pool2) {
        this.pool = pool2;
    }

    public String toString() {
        String name = getClass().getName();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex != -1) {
            name = name.substring(dotIndex + 1);
        }
        if (name.endsWith("Action")) {
            return name.substring(0, name.length() - 6);
        }
        return name;
    }
}
