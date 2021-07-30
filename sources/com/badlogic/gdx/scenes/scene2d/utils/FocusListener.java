package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class FocusListener implements EventListener {
    public boolean handle(Event event) {
        if (event instanceof FocusEvent) {
            FocusEvent focusEvent = (FocusEvent) event;
            switch (focusEvent.getType()) {
                case keyboard:
                    keyboardFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
                    break;
                case scroll:
                    scrollFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
                    break;
            }
        }
        return false;
    }

    public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
    }

    public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
    }

    public static class FocusEvent extends Event {
        private boolean focused;
        private Actor relatedActor;
        private Type type;

        public enum Type {
            keyboard,
            scroll
        }

        public void reset() {
            super.reset();
            this.relatedActor = null;
        }

        public boolean isFocused() {
            return this.focused;
        }

        public void setFocused(boolean focused2) {
            this.focused = focused2;
        }

        public Type getType() {
            return this.type;
        }

        public void setType(Type focusType) {
            this.type = focusType;
        }

        public Actor getRelatedActor() {
            return this.relatedActor;
        }

        public void setRelatedActor(Actor relatedActor2) {
            this.relatedActor = relatedActor2;
        }
    }
}
