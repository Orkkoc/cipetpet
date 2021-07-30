package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.math.Vector2;

public class InputListener implements EventListener {
    public boolean handle(Event e) {
        if (!(e instanceof InputEvent)) {
            return false;
        }
        InputEvent event = (InputEvent) e;
        switch (event.getType()) {
            case keyDown:
                return keyDown(event, event.getKeyCode());
            case keyUp:
                return keyUp(event, event.getKeyCode());
            case keyTyped:
                return keyTyped(event, event.getCharacter());
            default:
                Vector2 coords = Vector2.tmp.set(event.getStageX(), event.getStageY());
                event.getListenerActor().stageToLocalCoordinates(coords);
                switch (event.getType()) {
                    case touchDown:
                        return touchDown(event, coords.f165x, coords.f166y, event.getPointer(), event.getButton());
                    case touchUp:
                        touchUp(event, coords.f165x, coords.f166y, event.getPointer(), event.getButton());
                        return true;
                    case touchDragged:
                        touchDragged(event, coords.f165x, coords.f166y, event.getPointer());
                        return true;
                    case mouseMoved:
                        return mouseMoved(event, coords.f165x, coords.f166y);
                    case scrolled:
                        return scrolled(event, coords.f165x, coords.f166y, event.getScrollAmount());
                    case enter:
                        enter(event, coords.f165x, coords.f166y, event.getPointer(), event.getRelatedActor());
                        return false;
                    case exit:
                        exit(event, coords.f165x, coords.f166y, event.getPointer(), event.getRelatedActor());
                        return false;
                    default:
                        return false;
                }
        }
    }

    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return false;
    }

    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
    }

    public void touchDragged(InputEvent event, float x, float y, int pointer) {
    }

    public boolean mouseMoved(InputEvent event, float x, float y) {
        return false;
    }

    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    }

    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    }

    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        return false;
    }

    public boolean keyDown(InputEvent event, int keycode) {
        return false;
    }

    public boolean keyUp(InputEvent event, int keycode) {
        return false;
    }

    public boolean keyTyped(InputEvent event, char character) {
        return false;
    }
}
