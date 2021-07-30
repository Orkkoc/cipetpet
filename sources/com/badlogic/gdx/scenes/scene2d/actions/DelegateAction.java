package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class DelegateAction extends Action {
    protected Action action;

    public void setAction(Action action2) {
        this.action = action2;
    }

    public Action getAction() {
        return this.action;
    }

    public void restart() {
        if (this.action != null) {
            this.action.restart();
        }
    }

    public void reset() {
        super.reset();
        this.action = null;
    }

    public void setActor(Actor actor) {
        if (this.action != null) {
            this.action.setActor(actor);
        }
        super.setActor(actor);
    }

    public String toString() {
        return super.toString() + (this.action == null ? "" : "(" + this.action + ")");
    }
}
