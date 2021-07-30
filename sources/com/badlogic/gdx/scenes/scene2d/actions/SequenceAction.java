package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

public class SequenceAction extends ParallelAction {
    private int index;

    public boolean act(float delta) {
        if (this.index >= this.actions.size) {
            return true;
        }
        if (((Action) this.actions.get(this.index)).act(delta)) {
            this.index++;
            if (this.index > this.actions.size) {
                return true;
            }
        }
        return false;
    }

    public void restart() {
        super.restart();
        this.index = 0;
    }
}
