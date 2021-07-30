package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pools;

public class Actor {
    private final Array<Action> actions = new Array<>(0);
    private final DelayedRemovalArray<EventListener> captureListeners = new DelayedRemovalArray<>(0);
    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private float height;
    private final DelayedRemovalArray<EventListener> listeners = new DelayedRemovalArray<>(0);
    private String name;
    private float originX;
    private float originY;
    private Group parent;
    private float rotation;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private Stage stage;
    private Touchable touchable = Touchable.enabled;
    private boolean visible = true;
    private float width;

    /* renamed from: x */
    private float f180x;

    /* renamed from: y */
    private float f181y;

    public void draw(SpriteBatch batch, float parentAlpha) {
    }

    public void act(float delta) {
        int i = 0;
        int n = this.actions.size;
        while (i < n) {
            Action action = this.actions.get(i);
            if (action.act(delta)) {
                this.actions.removeIndex(i);
                action.setActor((Actor) null);
                i--;
                n--;
            }
            i++;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        notify(r8, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0052, code lost:
        if (r8.isStopped() == false) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0054, code lost:
        r5 = r8.isCancelled();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0057, code lost:
        r0.clear();
        com.badlogic.gdx.utils.Pools.free(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        notify(r8, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0067, code lost:
        if (r8.getBubbles() != false) goto L_0x0074;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0069, code lost:
        r5 = r8.isCancelled();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006c, code lost:
        r0.clear();
        com.badlogic.gdx.utils.Pools.free(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0078, code lost:
        if (r8.isStopped() == false) goto L_0x0085;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x007a, code lost:
        r5 = r8.isCancelled();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007d, code lost:
        r0.clear();
        com.badlogic.gdx.utils.Pools.free(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0085, code lost:
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r3 = r0.size;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0088, code lost:
        if (r2 >= r3) goto L_0x00a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x008a, code lost:
        r0.get(r2).notify(r8, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0098, code lost:
        if (r8.isStopped() == false) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x009a, code lost:
        r5 = r8.isCancelled();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009d, code lost:
        r0.clear();
        com.badlogic.gdx.utils.Pools.free(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a5, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r5 = r8.isCancelled();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ab, code lost:
        r0.clear();
        com.badlogic.gdx.utils.Pools.free(r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean fire(com.badlogic.gdx.scenes.scene2d.Event r8) {
        /*
            r7 = this;
            com.badlogic.gdx.scenes.scene2d.Stage r5 = r8.getStage()
            if (r5 != 0) goto L_0x000d
            com.badlogic.gdx.scenes.scene2d.Stage r5 = r7.getStage()
            r8.setStage(r5)
        L_0x000d:
            r8.setTarget(r7)
            java.lang.Class<com.badlogic.gdx.utils.Array> r5 = com.badlogic.gdx.utils.Array.class
            java.lang.Object r0 = com.badlogic.gdx.utils.Pools.obtain(r5)
            com.badlogic.gdx.utils.Array r0 = (com.badlogic.gdx.utils.Array) r0
            com.badlogic.gdx.scenes.scene2d.Group r4 = r7.getParent()
        L_0x001c:
            if (r4 == 0) goto L_0x0026
            r0.add(r4)
            com.badlogic.gdx.scenes.scene2d.Group r4 = r4.getParent()
            goto L_0x001c
        L_0x0026:
            int r5 = r0.size     // Catch:{ all -> 0x00b3 }
            int r2 = r5 + -1
        L_0x002a:
            if (r2 < 0) goto L_0x004a
            java.lang.Object r1 = r0.get(r2)     // Catch:{ all -> 0x00b3 }
            com.badlogic.gdx.scenes.scene2d.Group r1 = (com.badlogic.gdx.scenes.scene2d.Group) r1     // Catch:{ all -> 0x00b3 }
            r5 = 1
            r1.notify(r8, r5)     // Catch:{ all -> 0x00b3 }
            boolean r5 = r8.isStopped()     // Catch:{ all -> 0x00b3 }
            if (r5 == 0) goto L_0x0047
            boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x00b3 }
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
        L_0x0046:
            return r5
        L_0x0047:
            int r2 = r2 + -1
            goto L_0x002a
        L_0x004a:
            r5 = 1
            r7.notify(r8, r5)     // Catch:{ all -> 0x00b3 }
            boolean r5 = r8.isStopped()     // Catch:{ all -> 0x00b3 }
            if (r5 == 0) goto L_0x005f
            boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x00b3 }
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
            goto L_0x0046
        L_0x005f:
            r5 = 0
            r7.notify(r8, r5)     // Catch:{ all -> 0x00b3 }
            boolean r5 = r8.getBubbles()     // Catch:{ all -> 0x00b3 }
            if (r5 != 0) goto L_0x0074
            boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x00b3 }
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
            goto L_0x0046
        L_0x0074:
            boolean r5 = r8.isStopped()     // Catch:{ all -> 0x00b3 }
            if (r5 == 0) goto L_0x0085
            boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x00b3 }
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
            goto L_0x0046
        L_0x0085:
            r2 = 0
            int r3 = r0.size     // Catch:{ all -> 0x00b3 }
        L_0x0088:
            if (r2 >= r3) goto L_0x00a8
            java.lang.Object r5 = r0.get(r2)     // Catch:{ all -> 0x00b3 }
            com.badlogic.gdx.scenes.scene2d.Group r5 = (com.badlogic.gdx.scenes.scene2d.Group) r5     // Catch:{ all -> 0x00b3 }
            r6 = 0
            r5.notify(r8, r6)     // Catch:{ all -> 0x00b3 }
            boolean r5 = r8.isStopped()     // Catch:{ all -> 0x00b3 }
            if (r5 == 0) goto L_0x00a5
            boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x00b3 }
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
            goto L_0x0046
        L_0x00a5:
            int r2 = r2 + 1
            goto L_0x0088
        L_0x00a8:
            boolean r5 = r8.isCancelled()     // Catch:{ all -> 0x00b3 }
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
            goto L_0x0046
        L_0x00b3:
            r5 = move-exception
            r0.clear()
            com.badlogic.gdx.utils.Pools.free(r0)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.Actor.fire(com.badlogic.gdx.scenes.scene2d.Event):boolean");
    }

    public boolean notify(Event event, boolean capture) {
        if (event.getTarget() == null) {
            throw new IllegalArgumentException("The event target cannot be null.");
        }
        DelayedRemovalArray<EventListener> listeners2 = capture ? this.captureListeners : this.listeners;
        if (listeners2.size == 0) {
            return event.isCancelled();
        }
        event.setListenerActor(this);
        event.setCapture(capture);
        if (event.getStage() == null) {
            event.setStage(this.stage);
        }
        listeners2.begin();
        int n = listeners2.size;
        for (int i = 0; i < n; i++) {
            EventListener listener = listeners2.get(i);
            if (listener.handle(event)) {
                event.handle();
                if (event instanceof InputEvent) {
                    InputEvent inputEvent = (InputEvent) event;
                    if (inputEvent.getType() == InputEvent.Type.touchDown) {
                        event.getStage().addTouchFocus(listener, this, inputEvent.getTarget(), inputEvent.getPointer(), inputEvent.getButton());
                    }
                }
            }
        }
        listeners2.end();
        return event.isCancelled();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public Actor hit(float x, float y, boolean touchable2) {
        if (touchable2 && this.touchable != Touchable.enabled) {
            return null;
        }
        if (x < 0.0f || x >= this.width || y < 0.0f || y >= this.height) {
            this = null;
        }
        return this;
    }

    public boolean remove() {
        if (this.parent != null) {
            return this.parent.removeActor(this);
        }
        return false;
    }

    public boolean addListener(EventListener listener) {
        if (this.listeners.contains(listener, true)) {
            return false;
        }
        this.listeners.add(listener);
        return true;
    }

    public boolean removeListener(EventListener listener) {
        return this.listeners.removeValue(listener, true);
    }

    public Array<EventListener> getListeners() {
        return this.listeners;
    }

    public boolean addCaptureListener(EventListener listener) {
        if (!this.captureListeners.contains(listener, true)) {
            this.captureListeners.add(listener);
        }
        return true;
    }

    public boolean removeCaptureListener(EventListener listener) {
        return this.captureListeners.removeValue(listener, true);
    }

    public Array<EventListener> getCaptureListeners() {
        return this.captureListeners;
    }

    public void addAction(Action action) {
        action.setActor(this);
        this.actions.add(action);
    }

    public void removeAction(Action action) {
        if (this.actions.removeValue(action, true)) {
            action.setActor((Actor) null);
        }
    }

    public Array<Action> getActions() {
        return this.actions;
    }

    public void clearActions() {
        for (int i = this.actions.size - 1; i >= 0; i--) {
            this.actions.get(i).setActor((Actor) null);
        }
        this.actions.clear();
    }

    public Stage getStage() {
        return this.stage;
    }

    /* access modifiers changed from: protected */
    public void setStage(Stage stage2) {
        this.stage = stage2;
    }

    public boolean isDescendantOf(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        for (Actor parent2 = this; parent2 != null; parent2 = parent2.getParent()) {
            if (parent2 == actor) {
                return true;
            }
        }
        return false;
    }

    public boolean isAscendantOf(Actor actor) {
        if (actor == null) {
            throw new IllegalArgumentException("actor cannot be null.");
        }
        while (actor != null) {
            if (actor == this) {
                return true;
            }
            actor = actor.getParent();
        }
        return false;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public Group getParent() {
        return this.parent;
    }

    /* access modifiers changed from: protected */
    public void setParent(Group parent2) {
        this.parent = parent2;
    }

    public Touchable getTouchable() {
        return this.touchable;
    }

    public void setTouchable(Touchable touchable2) {
        this.touchable = touchable2;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible2) {
        this.visible = visible2;
    }

    public float getX() {
        return this.f180x;
    }

    public void setX(float x) {
        this.f180x = x;
    }

    public float getY() {
        return this.f181y;
    }

    public void setY(float y) {
        this.f181y = y;
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void translate(float x, float y) {
        setX(this.f180x + x);
        setY(this.f181y + y);
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width2) {
        this.width = width2;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height2) {
        this.height = height2;
    }

    public float getTop() {
        return getY() + getHeight();
    }

    public float getRight() {
        return getX() + getWidth();
    }

    public void setSize(float width2, float height2) {
        setWidth(width2);
        setHeight(height2);
    }

    public void size(float size) {
        setWidth(this.width + size);
        setHeight(this.height + size);
    }

    public void size(float width2, float height2) {
        setWidth(this.width + width2);
        setHeight(this.height + height2);
    }

    public void setBounds(float x, float y, float width2, float height2) {
        setX(x);
        setY(y);
        setWidth(width2);
        setHeight(height2);
    }

    public float getOriginX() {
        return this.originX;
    }

    public void setOriginX(float originX2) {
        this.originX = originX2;
    }

    public float getOriginY() {
        return this.originY;
    }

    public void setOriginY(float originY2) {
        this.originY = originY2;
    }

    public void setOrigin(float originX2, float originY2) {
        setOriginX(originX2);
        setOriginY(originY2);
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public void setScaleX(float scaleX2) {
        this.scaleX = scaleX2;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleY(float scaleY2) {
        this.scaleY = scaleY2;
    }

    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    public void setScale(float scaleX2, float scaleY2) {
        setScaleX(scaleX2);
        setScaleY(scaleY2);
    }

    public void scale(float scale) {
        setScaleX(this.scaleX + scale);
        setScaleY(this.scaleY + scale);
    }

    public void scale(float scaleX2, float scaleY2) {
        setScaleX(this.scaleX + scaleX2);
        setScaleY(this.scaleY + scaleY2);
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
    }

    public void rotate(float amountInDegrees) {
        setRotation(this.rotation + amountInDegrees);
    }

    public void setColor(Color color2) {
        this.color.set(color2);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public Color getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void toFront() {
        setZIndex(Integer.MAX_VALUE);
    }

    public void toBack() {
        setZIndex(0);
    }

    public void setZIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("ZIndex cannot be < 0.");
        }
        Group parent2 = getParent();
        if (parent2 != null) {
            Array<Actor> children = parent2.getChildren();
            if (children.size != 1 && children.removeValue(this, true)) {
                if (index >= children.size) {
                    children.add(this);
                } else {
                    children.insert(index, this);
                }
            }
        }
    }

    public int getZIndex() {
        Group parent2 = getParent();
        if (parent2 == null) {
            return -1;
        }
        return parent2.getChildren().indexOf(this, true);
    }

    public boolean clipBegin() {
        return clipBegin(getX(), getY(), getWidth(), getHeight());
    }

    public boolean clipBegin(float x, float y, float width2, float height2) {
        Rectangle tableBounds = Rectangle.tmp;
        tableBounds.f161x = x;
        tableBounds.f162y = y;
        tableBounds.width = width2;
        tableBounds.height = height2;
        Stage stage2 = getStage();
        Rectangle scissorBounds = (Rectangle) Pools.obtain(Rectangle.class);
        ScissorStack.calculateScissors(stage2.getCamera(), stage2.getSpriteBatch().getTransformMatrix(), tableBounds, scissorBounds);
        if (ScissorStack.pushScissors(scissorBounds)) {
            return true;
        }
        Pools.free(scissorBounds);
        return false;
    }

    public void clipEnd() {
        Pools.free(ScissorStack.popScissors());
    }

    public Vector2 screenToLocalCoordinates(Vector2 screenCoords) {
        Stage stage2 = getStage();
        return stage2 == null ? screenCoords : stageToLocalCoordinates(stage2.screenToStageCoordinates(screenCoords));
    }

    public Vector2 stageToLocalCoordinates(Vector2 stageCoords) {
        if (this.parent != null) {
            this.parent.stageToLocalCoordinates(stageCoords);
            parentToLocalCoordinates(stageCoords);
        }
        return stageCoords;
    }

    public Vector2 localToStageCoordinates(Vector2 localCoords) {
        Actor actor = this;
        while (actor != null) {
            if (actor.getRotation() == 0.0f && actor.getScaleX() == 1.0f && actor.getScaleY() == 1.0f) {
                localCoords.f165x += actor.getX();
                localCoords.f166y += actor.getY();
                actor = actor.getParent();
            } else {
                throw new GdxRuntimeException("Only unrotated and unscaled actors may use this method.");
            }
        }
        return localCoords;
    }

    public Vector2 localToParentCoordinates(Vector2 localCoords) {
        if (getRotation() == 0.0f && getScaleX() == 1.0f && getScaleY() == 1.0f) {
            localCoords.f165x += getX();
            localCoords.f166y += getY();
            return localCoords;
        }
        throw new GdxRuntimeException("Only unrotated and unscaled actors may use this method.");
    }

    public Vector2 localToAscendantCoordinates(Actor ascendant, Vector2 localCoords) {
        Actor actor = this;
        while (actor.getParent() != null) {
            actor.localToParentCoordinates(localCoords);
            actor = actor.getParent();
            if (actor == ascendant) {
                break;
            }
        }
        return localCoords;
    }

    public Vector2 parentToLocalCoordinates(Vector2 parentCoords) {
        float rotation2 = getRotation();
        float scaleX2 = getScaleX();
        float scaleY2 = getScaleY();
        float childX = getX();
        float childY = getY();
        if (rotation2 != 0.0f) {
            float cos = (float) Math.cos((double) (0.017453292f * rotation2));
            float sin = (float) Math.sin((double) (0.017453292f * rotation2));
            float originX2 = getOriginX();
            float originY2 = getOriginY();
            if (scaleX2 == 1.0f && scaleY2 == 1.0f) {
                if (originX2 == 0.0f && originY2 == 0.0f) {
                    float tox = parentCoords.f165x - childX;
                    float toy = parentCoords.f166y - childY;
                    parentCoords.f165x = (tox * cos) + (toy * sin);
                    parentCoords.f166y = ((-sin) * tox) + (toy * cos);
                } else {
                    float fx = -originX2;
                    float fy = -originY2;
                    float tox2 = parentCoords.f165x - (((cos * fx) - (sin * fy)) + (childX + originX2));
                    float toy2 = parentCoords.f166y - (((sin * fx) + (cos * fy)) + (childY + originY2));
                    parentCoords.f165x = (tox2 * cos) + (toy2 * sin);
                    parentCoords.f166y = ((-sin) * tox2) + (toy2 * cos);
                }
            } else if (originX2 == 0.0f && originY2 == 0.0f) {
                float tox3 = parentCoords.f165x - childX;
                float toy3 = parentCoords.f166y - childY;
                parentCoords.f165x = ((tox3 * cos) + (toy3 * sin)) / scaleX2;
                parentCoords.f166y = (((-sin) * tox3) + (toy3 * cos)) / scaleY2;
            } else {
                float fx2 = (-originX2) * scaleX2;
                float fy2 = (-originY2) * scaleY2;
                float tox4 = parentCoords.f165x - (((cos * fx2) - (sin * fy2)) + (childX + originX2));
                float toy4 = parentCoords.f166y - (((sin * fx2) + (cos * fy2)) + (childY + originY2));
                parentCoords.f165x = ((tox4 * cos) + (toy4 * sin)) / scaleX2;
                parentCoords.f166y = (((-sin) * tox4) + (toy4 * cos)) / scaleY2;
            }
        } else if (scaleX2 == 1.0f && scaleY2 == 1.0f) {
            parentCoords.f165x -= childX;
            parentCoords.f166y -= childY;
        } else {
            float originX3 = getOriginX();
            float originY3 = getOriginY();
            if (originX3 == 0.0f && originY3 == 0.0f) {
                parentCoords.f165x = (parentCoords.f165x - childX) / scaleX2;
                parentCoords.f166y = (parentCoords.f166y - childY) / scaleY2;
            } else {
                parentCoords.f165x = (((parentCoords.f165x - childX) - originX3) / scaleX2) + originX3;
                parentCoords.f166y = (((parentCoords.f166y - childY) - originY3) / scaleY2) + originY3;
            }
        }
        return parentCoords;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r1 = getClass().getName();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString() {
        /*
            r4 = this;
            java.lang.String r1 = r4.name
            if (r1 != 0) goto L_0x001b
            java.lang.Class r2 = r4.getClass()
            java.lang.String r1 = r2.getName()
            r2 = 46
            int r0 = r1.lastIndexOf(r2)
            r2 = -1
            if (r0 == r2) goto L_0x001b
            int r2 = r0 + 1
            java.lang.String r1 = r1.substring(r2)
        L_0x001b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.StringBuilder r2 = r2.append(r1)
            java.lang.String r3 = " "
            java.lang.StringBuilder r2 = r2.append(r3)
            float r3 = r4.f180x
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = ","
            java.lang.StringBuilder r2 = r2.append(r3)
            float r3 = r4.f181y
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = " "
            java.lang.StringBuilder r2 = r2.append(r3)
            float r3 = r4.width
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = "x"
            java.lang.StringBuilder r2 = r2.append(r3)
            float r3 = r4.height
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.Actor.toString():java.lang.String");
    }
}
