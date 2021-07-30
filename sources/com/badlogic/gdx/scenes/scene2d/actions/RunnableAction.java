package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

public class RunnableAction extends Action {
    private boolean ran;
    private Runnable runnable;

    public boolean act(float delta) {
        if (!this.ran) {
            run();
            this.ran = true;
        }
        return true;
    }

    public void run() {
        this.runnable.run();
    }

    public void restart() {
        this.ran = false;
    }

    public void reset() {
        super.reset();
        this.runnable = null;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public void setRunnable(Runnable runnable2) {
        this.runnable = runnable2;
    }
}
