package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class ActorGestureListener implements EventListener {
    Actor actor;
    private final GestureDetector detector;
    InputEvent event;
    Actor touchDownTarget;

    public ActorGestureListener() {
        this(20.0f, 0.4f, 1.1f, 0.15f);
    }

    public ActorGestureListener(float halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay) {
        this.detector = new GestureDetector(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, new GestureDetector.GestureAdapter() {
            private final Vector2 initialPointer1 = new Vector2();
            private final Vector2 initialPointer2 = new Vector2();
            private final Vector2 pointer1 = new Vector2();
            private final Vector2 pointer2 = new Vector2();

            public boolean tap(float stageX, float stageY, int count, int button) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(Vector2.tmp.set(stageX, stageY));
                ActorGestureListener.this.tap(ActorGestureListener.this.event, Vector2.tmp.f165x, Vector2.tmp.f166y, count, button);
                return true;
            }

            public boolean longPress(float stageX, float stageY) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(Vector2.tmp.set(stageX, stageY));
                return ActorGestureListener.this.longPress(ActorGestureListener.this.actor, Vector2.tmp.f165x, Vector2.tmp.f166y);
            }

            public boolean fling(float velocityX, float velocityY, int button) {
                ActorGestureListener.this.fling(ActorGestureListener.this.event, velocityX, velocityY, button);
                return true;
            }

            public boolean pan(float stageX, float stageY, float deltaX, float deltaY) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(Vector2.tmp.set(stageX, stageY));
                ActorGestureListener.this.pan(ActorGestureListener.this.event, Vector2.tmp.f165x, Vector2.tmp.f166y, deltaX, deltaY);
                return true;
            }

            public boolean zoom(float initialDistance, float distance) {
                ActorGestureListener.this.zoom(ActorGestureListener.this.event, initialDistance, distance);
                return true;
            }

            public boolean pinch(Vector2 stageInitialPointer1, Vector2 stageInitialPointer2, Vector2 stagePointer1, Vector2 stagePointer2) {
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.initialPointer1.set(stageInitialPointer1));
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.initialPointer2.set(stageInitialPointer2));
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.pointer1.set(stagePointer1));
                ActorGestureListener.this.actor.stageToLocalCoordinates(this.pointer2.set(stagePointer2));
                ActorGestureListener.this.pinch(ActorGestureListener.this.event, this.initialPointer1, this.initialPointer2, this.pointer1, this.pointer2);
                return true;
            }
        });
    }

    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) {
            return false;
        }
        InputEvent event2 = (InputEvent) e;
        switch (event2.getType()) {
            case touchDown:
                this.actor = event2.getListenerActor();
                this.touchDownTarget = event2.getTarget();
                this.detector.touchDown(event2.getStageX(), event2.getStageY(), event2.getPointer(), event2.getButton());
                this.actor.stageToLocalCoordinates(Vector2.tmp.set(event2.getStageX(), event2.getStageY()));
                touchDown(event2, Vector2.tmp.f165x, Vector2.tmp.f166y, event2.getPointer(), event2.getButton());
                return true;
            case touchUp:
                this.event = event2;
                this.actor = event2.getListenerActor();
                this.detector.touchUp(event2.getStageX(), event2.getStageY(), event2.getPointer(), event2.getButton());
                this.actor.stageToLocalCoordinates(Vector2.tmp.set(event2.getStageX(), event2.getStageY()));
                touchUp(event2, Vector2.tmp.f165x, Vector2.tmp.f166y, event2.getPointer(), event2.getButton());
                return true;
            case touchDragged:
                this.event = event2;
                this.actor = event2.getListenerActor();
                this.detector.touchDragged(event2.getStageX(), event2.getStageY(), event2.getPointer());
                return true;
            default:
                return false;
        }
    }

    public void touchDown(InputEvent event2, float x, float y, int pointer, int button) {
    }

    public void touchUp(InputEvent event2, float x, float y, int pointer, int button) {
    }

    public void tap(InputEvent event2, float x, float y, int count, int button) {
    }

    public boolean longPress(Actor actor2, float x, float y) {
        return false;
    }

    public void fling(InputEvent event2, float velocityX, float velocityY, int button) {
    }

    public void pan(InputEvent event2, float x, float y, float deltaX, float deltaY) {
    }

    public void zoom(InputEvent event2, float initialDistance, float distance) {
    }

    public void pinch(InputEvent event2, Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
    }

    public GestureDetector getGestureDetector() {
        return this.detector;
    }

    public Actor getTouchDownTarget() {
        return this.touchDownTarget;
    }
}
