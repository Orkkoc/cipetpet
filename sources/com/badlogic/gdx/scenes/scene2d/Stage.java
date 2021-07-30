package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

public class Stage extends InputAdapter implements Disposable {
    private final SpriteBatch batch;
    private Camera camera;
    private float centerX;
    private float centerY;
    private float gutterHeight;
    private float gutterWidth;
    private float height;
    private Actor keyboardFocus;
    private Actor mouseOverActor;
    private int mouseScreenX;
    private int mouseScreenY;
    private final boolean ownsBatch;
    private Actor[] pointerOverActors;
    private int[] pointerScreenX;
    private int[] pointerScreenY;
    private boolean[] pointerTouched;
    private Group root;
    private Actor scrollFocus;
    private final Vector2 stageCoords;
    private SnapshotArray<TouchFocus> touchFocuses;
    private float width;

    public Stage() {
        this((float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight(), false);
    }

    public Stage(float width2, float height2, boolean keepAspectRatio) {
        this.stageCoords = new Vector2();
        this.pointerOverActors = new Actor[20];
        this.pointerTouched = new boolean[20];
        this.pointerScreenX = new int[20];
        this.pointerScreenY = new int[20];
        this.touchFocuses = new SnapshotArray<>(true, 4, TouchFocus.class);
        this.batch = new SpriteBatch();
        this.ownsBatch = true;
        initialize(width2, height2, keepAspectRatio);
    }

    public Stage(float width2, float height2, boolean keepAspectRatio, SpriteBatch batch2) {
        this.stageCoords = new Vector2();
        this.pointerOverActors = new Actor[20];
        this.pointerTouched = new boolean[20];
        this.pointerScreenX = new int[20];
        this.pointerScreenY = new int[20];
        this.touchFocuses = new SnapshotArray<>(true, 4, TouchFocus.class);
        this.batch = batch2;
        this.ownsBatch = false;
        initialize(width2, height2, keepAspectRatio);
    }

    private void initialize(float width2, float height2, boolean keepAspectRatio) {
        this.width = width2;
        this.height = height2;
        this.root = new Group();
        this.root.setStage(this);
        this.camera = new OrthographicCamera();
        setViewport(width2, height2, keepAspectRatio);
    }

    public void setViewport(float width2, float height2, boolean keepAspectRatio) {
        if (keepAspectRatio) {
            float screenWidth = (float) Gdx.graphics.getWidth();
            float screenHeight = (float) Gdx.graphics.getHeight();
            if (screenHeight / screenWidth < height2 / width2) {
                float lengthen = (screenWidth - (width2 * (screenHeight / height2))) * (height2 / screenHeight);
                this.width = width2 + lengthen;
                this.height = height2;
                this.gutterWidth = lengthen / 2.0f;
                this.gutterHeight = 0.0f;
            } else {
                float lengthen2 = (screenHeight - (height2 * (screenWidth / width2))) * (width2 / screenWidth);
                this.height = height2 + lengthen2;
                this.width = width2;
                this.gutterWidth = 0.0f;
                this.gutterHeight = lengthen2 / 2.0f;
            }
        } else {
            this.width = width2;
            this.height = height2;
            this.gutterWidth = 0.0f;
            this.gutterHeight = 0.0f;
        }
        this.centerX = this.width / 2.0f;
        this.centerY = this.height / 2.0f;
        this.camera.position.set(this.centerX, this.centerY, 0.0f);
        this.camera.viewportWidth = this.width;
        this.camera.viewportHeight = this.height;
    }

    public void draw() {
        this.camera.update();
        if (this.root.isVisible()) {
            this.batch.setProjectionMatrix(this.camera.combined);
            this.batch.begin();
            this.root.draw(this.batch, 1.0f);
            this.batch.end();
        }
    }

    public void act() {
        act(Math.min(Gdx.graphics.getDeltaTime(), 0.033333335f));
    }

    public void act(float delta) {
        int n = this.pointerOverActors.length;
        for (int pointer = 0; pointer < n; pointer++) {
            Actor overLast = this.pointerOverActors[pointer];
            if (this.pointerTouched[pointer]) {
                this.pointerOverActors[pointer] = fireEnterAndExit(overLast, this.pointerScreenX[pointer], this.pointerScreenY[pointer], pointer);
            } else if (overLast != null) {
                this.pointerOverActors[pointer] = null;
                screenToStageCoordinates(this.stageCoords.set((float) this.pointerScreenX[pointer], (float) this.pointerScreenY[pointer]));
                InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
                event.setType(InputEvent.Type.exit);
                event.setStage(this);
                event.setStageX(this.stageCoords.f165x);
                event.setStageY(this.stageCoords.f166y);
                event.setRelatedActor(overLast);
                event.setPointer(pointer);
                overLast.fire(event);
                Pools.free(event);
            }
        }
        Application.ApplicationType type = Gdx.app.getType();
        if (type == Application.ApplicationType.Desktop || type == Application.ApplicationType.Applet || type == Application.ApplicationType.WebGL) {
            this.mouseOverActor = fireEnterAndExit(this.mouseOverActor, this.mouseScreenX, this.mouseScreenY, -1);
        }
        this.root.act(delta);
    }

    private Actor fireEnterAndExit(Actor overLast, int screenX, int screenY, int pointer) {
        screenToStageCoordinates(this.stageCoords.set((float) screenX, (float) screenY));
        Actor over = hit(this.stageCoords.f165x, this.stageCoords.f166y, true);
        if (over == overLast) {
            return overLast;
        }
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setStageX(this.stageCoords.f165x);
        event.setStageY(this.stageCoords.f166y);
        event.setPointer(pointer);
        if (overLast != null) {
            event.setType(InputEvent.Type.exit);
            event.setRelatedActor(over);
            overLast.fire(event);
        }
        if (over != null) {
            event.setType(InputEvent.Type.enter);
            event.setRelatedActor(overLast);
            over.fire(event);
        }
        Pools.free(event);
        return over;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.pointerTouched[pointer] = true;
        this.pointerScreenX[pointer] = screenX;
        this.pointerScreenY[pointer] = screenY;
        screenToStageCoordinates(this.stageCoords.set((float) screenX, (float) screenY));
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchDown);
        event.setStage(this);
        event.setStageX(this.stageCoords.f165x);
        event.setStageY(this.stageCoords.f166y);
        event.setPointer(pointer);
        event.setButton(button);
        Actor target = hit(this.stageCoords.f165x, this.stageCoords.f166y, true);
        if (target == null) {
            target = this.root;
        }
        target.fire(event);
        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.pointerScreenX[pointer] = screenX;
        this.pointerScreenY[pointer] = screenY;
        if (this.touchFocuses.size == 0) {
            return false;
        }
        screenToStageCoordinates(this.stageCoords.set((float) screenX, (float) screenY));
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchDragged);
        event.setStage(this);
        event.setStageX(this.stageCoords.f165x);
        event.setStageY(this.stageCoords.f166y);
        event.setPointer(pointer);
        SnapshotArray<TouchFocus> touchFocuses2 = this.touchFocuses;
        TouchFocus[] focuses = (TouchFocus[]) touchFocuses2.begin();
        int n = touchFocuses2.size;
        for (int i = 0; i < n; i++) {
            TouchFocus focus = focuses[i];
            if (focus.pointer == pointer) {
                event.setTarget(focus.target);
                event.setListenerActor(focus.listenerActor);
                if (focus.listener.handle(event)) {
                    event.handle();
                }
            }
        }
        touchFocuses2.end();
        boolean isHandled = event.isHandled();
        Pools.free(event);
        return isHandled;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        this.pointerTouched[pointer] = false;
        this.pointerScreenX[pointer] = screenX;
        this.pointerScreenY[pointer] = screenY;
        if (this.touchFocuses.size == 0) {
            return false;
        }
        screenToStageCoordinates(this.stageCoords.set((float) screenX, (float) screenY));
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchUp);
        event.setStage(this);
        event.setStageX(this.stageCoords.f165x);
        event.setStageY(this.stageCoords.f166y);
        event.setPointer(pointer);
        event.setButton(button);
        SnapshotArray<TouchFocus> touchFocuses2 = this.touchFocuses;
        TouchFocus[] focuses = (TouchFocus[]) touchFocuses2.begin();
        int n = touchFocuses2.size;
        for (int i = 0; i < n; i++) {
            TouchFocus focus = focuses[i];
            if (focus.pointer == pointer && focus.button == button && touchFocuses2.removeValue(focus, true)) {
                event.setTarget(focus.target);
                event.setListenerActor(focus.listenerActor);
                if (focus.listener.handle(event)) {
                    event.handle();
                }
                Pools.free(focus);
            }
        }
        touchFocuses2.end();
        boolean isHandled = event.isHandled();
        Pools.free(event);
        return isHandled;
    }

    public boolean mouseMoved(int screenX, int screenY) {
        this.mouseScreenX = screenX;
        this.mouseScreenY = screenY;
        screenToStageCoordinates(this.stageCoords.set((float) screenX, (float) screenY));
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.mouseMoved);
        event.setStageX(this.stageCoords.f165x);
        event.setStageY(this.stageCoords.f166y);
        Actor target = hit(this.stageCoords.f165x, this.stageCoords.f166y, true);
        if (target == null) {
            target = this.root;
        }
        target.fire(event);
        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    public boolean scrolled(int amount) {
        Actor target = this.scrollFocus == null ? this.root : this.scrollFocus;
        screenToStageCoordinates(this.stageCoords.set((float) this.mouseScreenX, (float) this.mouseScreenY));
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.scrolled);
        event.setScrollAmount(amount);
        event.setStageX(this.stageCoords.f165x);
        event.setStageY(this.stageCoords.f166y);
        target.fire(event);
        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    public boolean keyDown(int keyCode) {
        Actor target = this.keyboardFocus == null ? this.root : this.keyboardFocus;
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.keyDown);
        event.setKeyCode(keyCode);
        target.fire(event);
        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    public boolean keyUp(int keyCode) {
        Actor target = this.keyboardFocus == null ? this.root : this.keyboardFocus;
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.keyUp);
        event.setKeyCode(keyCode);
        target.fire(event);
        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    public boolean keyTyped(char character) {
        Actor target = this.keyboardFocus == null ? this.root : this.keyboardFocus;
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.keyTyped);
        event.setCharacter(character);
        target.fire(event);
        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    public void addTouchFocus(EventListener listener, Actor listenerActor, Actor target, int pointer, int button) {
        TouchFocus focus = (TouchFocus) Pools.obtain(TouchFocus.class);
        focus.listenerActor = listenerActor;
        focus.target = target;
        focus.listener = listener;
        focus.pointer = pointer;
        focus.button = button;
        this.touchFocuses.add(focus);
    }

    public void removeTouchFocus(EventListener listener, Actor listenerActor, Actor target, int pointer, int button) {
        SnapshotArray<TouchFocus> touchFocuses2 = this.touchFocuses;
        for (int i = touchFocuses2.size - 1; i >= 0; i--) {
            TouchFocus focus = touchFocuses2.get(i);
            if (focus.listener == listener && focus.listenerActor == listenerActor && focus.target == target && focus.pointer == pointer && focus.button == button) {
                touchFocuses2.removeIndex(i);
                Pools.free(focus);
            }
        }
    }

    public void cancelTouchFocus() {
        cancelTouchFocus((EventListener) null, (Actor) null);
    }

    public void cancelTouchFocus(EventListener listener, Actor actor) {
        InputEvent event = (InputEvent) Pools.obtain(InputEvent.class);
        event.setStage(this);
        event.setType(InputEvent.Type.touchUp);
        event.setStageX(-2.14748365E9f);
        event.setStageY(-2.14748365E9f);
        SnapshotArray<TouchFocus> touchFocuses2 = this.touchFocuses;
        TouchFocus[] items = (TouchFocus[]) touchFocuses2.begin();
        int n = touchFocuses2.size;
        for (int i = 0; i < n; i++) {
            TouchFocus focus = items[i];
            if (!(focus.listener == listener && focus.listenerActor == actor) && touchFocuses2.removeValue(focus, true)) {
                event.setTarget(focus.target);
                event.setListenerActor(focus.listenerActor);
                event.setPointer(focus.pointer);
                event.setButton(focus.button);
                focus.listener.handle(event);
            }
        }
        touchFocuses2.end();
        Pools.free(event);
    }

    public void addActor(Actor actor) {
        this.root.addActor(actor);
    }

    public void addAction(Action action) {
        this.root.addAction(action);
    }

    public Array<Actor> getActors() {
        return this.root.getChildren();
    }

    public boolean addListener(EventListener listener) {
        return this.root.addListener(listener);
    }

    public boolean removeListener(EventListener listener) {
        return this.root.removeListener(listener);
    }

    public boolean addCaptureListener(EventListener listener) {
        return this.root.addCaptureListener(listener);
    }

    public boolean removeCaptureListener(EventListener listener) {
        return this.root.removeCaptureListener(listener);
    }

    public void clear() {
        unfocusAll();
        this.root.clear();
    }

    public void unfocusAll() {
        this.scrollFocus = null;
        this.keyboardFocus = null;
        cancelTouchFocus();
    }

    public void unfocus(Actor actor) {
        if (this.scrollFocus != null && this.scrollFocus.isDescendantOf(actor)) {
            this.scrollFocus = null;
        }
        if (this.keyboardFocus != null && this.keyboardFocus.isDescendantOf(actor)) {
            this.keyboardFocus = null;
        }
    }

    public void setKeyboardFocus(Actor actor) {
        if (this.keyboardFocus != actor) {
            FocusListener.FocusEvent event = (FocusListener.FocusEvent) Pools.obtain(FocusListener.FocusEvent.class);
            event.setStage(this);
            event.setType(FocusListener.FocusEvent.Type.keyboard);
            Actor oldKeyboardFocus = this.keyboardFocus;
            if (oldKeyboardFocus != null) {
                event.setFocused(false);
                event.setRelatedActor(actor);
                oldKeyboardFocus.fire(event);
            }
            if (!event.isCancelled()) {
                this.keyboardFocus = actor;
                if (actor != null) {
                    event.setFocused(true);
                    event.setRelatedActor(oldKeyboardFocus);
                    actor.fire(event);
                    if (event.isCancelled()) {
                        setKeyboardFocus(oldKeyboardFocus);
                    }
                }
            }
            Pools.free(event);
        }
    }

    public Actor getKeyboardFocus() {
        return this.keyboardFocus;
    }

    public void setScrollFocus(Actor actor) {
        if (this.scrollFocus != actor) {
            FocusListener.FocusEvent event = (FocusListener.FocusEvent) Pools.obtain(FocusListener.FocusEvent.class);
            event.setStage(this);
            event.setType(FocusListener.FocusEvent.Type.scroll);
            Actor oldScrollFocus = this.keyboardFocus;
            if (oldScrollFocus != null) {
                event.setFocused(false);
                event.setRelatedActor(actor);
                oldScrollFocus.fire(event);
            }
            if (!event.isCancelled()) {
                this.scrollFocus = actor;
                if (actor != null) {
                    event.setFocused(true);
                    event.setRelatedActor(oldScrollFocus);
                    actor.fire(event);
                    if (event.isCancelled()) {
                        setScrollFocus(oldScrollFocus);
                    }
                }
            }
            Pools.free(event);
        }
    }

    public Actor getScrollFocus() {
        return this.scrollFocus;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getGutterWidth() {
        return this.gutterWidth;
    }

    public float getGutterHeight() {
        return this.gutterHeight;
    }

    public SpriteBatch getSpriteBatch() {
        return this.batch;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void setCamera(Camera camera2) {
        this.camera = camera2;
    }

    public Group getRoot() {
        return this.root;
    }

    public Actor hit(float stageX, float stageY, boolean touchable) {
        Vector2 actorCoords = Vector2.tmp;
        this.root.parentToLocalCoordinates(actorCoords.set(stageX, stageY));
        return this.root.hit(actorCoords.f165x, actorCoords.f166y, touchable);
    }

    public Vector2 screenToStageCoordinates(Vector2 screenCoords) {
        this.camera.unproject(Vector3.tmp.set(screenCoords.f165x, screenCoords.f166y, 0.0f));
        screenCoords.f165x = Vector3.tmp.f170x;
        screenCoords.f166y = Vector3.tmp.f171y;
        return screenCoords;
    }

    public Vector2 stageToScreenCoordinates(Vector2 stageCoords2) {
        Vector3.tmp.set(stageCoords2.f165x, stageCoords2.f166y, 0.0f);
        this.camera.project(Vector3.tmp);
        stageCoords2.f165x = Vector3.tmp.f170x;
        stageCoords2.f166y = Vector3.tmp.f171y;
        return stageCoords2;
    }

    public Vector2 toScreenCoordinates(Vector2 coords, Matrix4 transformMatrix) {
        ScissorStack.toWindowCoordinates(this.camera, transformMatrix, coords);
        return coords;
    }

    public void dispose() {
        if (this.ownsBatch) {
            this.batch.dispose();
        }
    }

    public static final class TouchFocus implements Pool.Poolable {
        int button;
        EventListener listener;
        Actor listenerActor;
        int pointer;
        Actor target;

        public void reset() {
            this.listenerActor = null;
            this.listener = null;
        }
    }
}
