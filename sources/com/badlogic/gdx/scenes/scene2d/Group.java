package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class Group extends Actor implements Cullable {
    private final Matrix4 batchTransform = new Matrix4();
    private final SnapshotArray<Actor> children = new SnapshotArray<>(true, 4, Actor.class);
    private Rectangle cullingArea;
    private final Matrix3 localTransform = new Matrix3();
    private final Matrix4 oldBatchTransform = new Matrix4();
    private final Vector2 point = new Vector2();
    private boolean transform = true;
    private final Matrix3 worldTransform = new Matrix3();

    public void act(float delta) {
        super.act(delta);
        Actor[] actors = (Actor[]) this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; i++) {
            actors[i].act(delta);
        }
        this.children.end();
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        if (this.transform) {
            applyTransform(batch, computeTransform());
        }
        drawChildren(batch, parentAlpha);
        if (this.transform) {
            resetTransform(batch);
        }
    }

    /* access modifiers changed from: protected */
    public void drawChildren(SpriteBatch batch, float parentAlpha) {
        float parentAlpha2 = parentAlpha * getColor().f67a;
        SnapshotArray<Actor> children2 = this.children;
        Actor[] actors = (Actor[]) children2.begin();
        Rectangle cullingArea2 = this.cullingArea;
        if (cullingArea2 != null) {
            float cullLeft = cullingArea2.f161x;
            float cullRight = cullLeft + cullingArea2.width;
            float cullBottom = cullingArea2.f162y;
            float cullTop = cullBottom + cullingArea2.height;
            if (this.transform) {
                int n = children2.size;
                for (int i = 0; i < n; i++) {
                    Actor child = actors[i];
                    if (child.isVisible()) {
                        float x = child.getX();
                        float y = child.getY();
                        if (x <= cullRight && y <= cullTop && child.getWidth() + x >= cullLeft && child.getHeight() + y >= cullBottom) {
                            child.draw(batch, parentAlpha2);
                        }
                    }
                }
                batch.flush();
            } else {
                float offsetX = getX();
                float offsetY = getY();
                setX(0.0f);
                setY(0.0f);
                int n2 = children2.size;
                for (int i2 = 0; i2 < n2; i2++) {
                    Actor child2 = actors[i2];
                    if (child2.isVisible()) {
                        float x2 = child2.getX();
                        float y2 = child2.getY();
                        if (x2 <= cullRight && y2 <= cullTop && child2.getWidth() + x2 >= cullLeft && child2.getHeight() + y2 >= cullBottom) {
                            child2.setX(x2 + offsetX);
                            child2.setY(y2 + offsetY);
                            child2.draw(batch, parentAlpha2);
                            child2.setX(x2);
                            child2.setY(y2);
                        }
                    }
                }
                setX(offsetX);
                setY(offsetY);
            }
        } else if (this.transform) {
            int n3 = children2.size;
            for (int i3 = 0; i3 < n3; i3++) {
                Actor child3 = actors[i3];
                if (child3.isVisible()) {
                    child3.draw(batch, parentAlpha2);
                }
            }
            batch.flush();
        } else {
            float offsetX2 = getX();
            float offsetY2 = getY();
            setX(0.0f);
            setY(0.0f);
            int n4 = children2.size;
            for (int i4 = 0; i4 < n4; i4++) {
                Actor child4 = actors[i4];
                if (child4.isVisible()) {
                    float x3 = child4.getX();
                    float y3 = child4.getY();
                    child4.setX(x3 + offsetX2);
                    child4.setY(y3 + offsetY2);
                    child4.draw(batch, parentAlpha2);
                    child4.setX(x3);
                    child4.setY(y3);
                }
            }
            setX(offsetX2);
            setY(offsetY2);
        }
        children2.end();
    }

    /* access modifiers changed from: protected */
    public void applyTransform(SpriteBatch batch, Matrix4 transform2) {
        this.oldBatchTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform2);
    }

    /* access modifiers changed from: protected */
    public Matrix4 computeTransform() {
        Matrix3 matrix3 = this.worldTransform;
        float originX = getOriginX();
        float originY = getOriginY();
        float rotation = getRotation();
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        if (originX == 0.0f && originY == 0.0f) {
            this.localTransform.idt();
        } else {
            this.localTransform.setToTranslation(originX, originY);
        }
        if (rotation != 0.0f) {
            this.localTransform.rotate(rotation);
        }
        if (!(scaleX == 1.0f && scaleY == 1.0f)) {
            this.localTransform.scale(scaleX, scaleY);
        }
        if (!(originX == 0.0f && originY == 0.0f)) {
            this.localTransform.translate(-originX, -originY);
        }
        this.localTransform.trn(getX(), getY());
        Group parentGroup = getParent();
        while (parentGroup != null && !parentGroup.transform) {
            parentGroup = parentGroup.getParent();
        }
        if (parentGroup != null) {
            this.worldTransform.set(parentGroup.worldTransform);
            this.worldTransform.mul(this.localTransform);
        } else {
            this.worldTransform.set(this.localTransform);
        }
        this.batchTransform.set(this.worldTransform);
        return this.batchTransform;
    }

    /* access modifiers changed from: protected */
    public void resetTransform(SpriteBatch batch) {
        batch.setTransformMatrix(this.oldBatchTransform);
    }

    public void setCullingArea(Rectangle cullingArea2) {
        this.cullingArea = cullingArea2;
    }

    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() == Touchable.disabled) {
            return null;
        }
        Array<Actor> children2 = this.children;
        for (int i = children2.size - 1; i >= 0; i--) {
            Actor child = children2.get(i);
            if (child.isVisible()) {
                child.parentToLocalCoordinates(this.point.set(x, y));
                Actor hit = child.hit(this.point.f165x, this.point.f166y, touchable);
                if (hit != null) {
                    return hit;
                }
            }
        }
        return super.hit(x, y, touchable);
    }

    /* access modifiers changed from: protected */
    public void childrenChanged() {
    }

    public void addActor(Actor actor) {
        actor.remove();
        this.children.add(actor);
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public void addActorAt(int index, Actor actor) {
        actor.remove();
        if (index >= this.children.size) {
            this.children.add(actor);
        } else {
            this.children.insert(index, actor);
        }
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public void addActorBefore(Actor actorBefore, Actor actor) {
        actor.remove();
        this.children.insert(this.children.indexOf(actorBefore, true), actor);
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public void addActorAfter(Actor actorAfter, Actor actor) {
        actor.remove();
        int index = this.children.indexOf(actorAfter, true);
        if (index == this.children.size) {
            this.children.add(actor);
        } else {
            this.children.insert(index + 1, actor);
        }
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public boolean removeActor(Actor actor) {
        if (!this.children.removeValue(actor, true)) {
            return false;
        }
        Stage stage = getStage();
        if (stage != null) {
            stage.unfocus(actor);
        }
        actor.setParent((Group) null);
        actor.setStage((Stage) null);
        childrenChanged();
        return true;
    }

    public void clear() {
        Actor[] actors = (Actor[]) this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; i++) {
            Actor child = actors[i];
            child.setStage((Stage) null);
            child.setParent((Group) null);
        }
        this.children.end();
        this.children.clear();
        childrenChanged();
    }

    public Actor findActor(String name) {
        Actor actor;
        Array<Actor> children2 = this.children;
        int n = children2.size;
        for (int i = 0; i < n; i++) {
            if (name.equals(children2.get(i).getName())) {
                return children2.get(i);
            }
        }
        int n2 = children2.size;
        for (int i2 = 0; i2 < n2; i2++) {
            Actor child = children2.get(i2);
            if ((child instanceof Group) && (actor = ((Group) child).findActor(name)) != null) {
                return actor;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setStage(Stage stage) {
        super.setStage(stage);
        Array<Actor> children2 = this.children;
        int n = children2.size;
        for (int i = 0; i < n; i++) {
            children2.get(i).setStage(stage);
        }
    }

    public boolean swapActor(int first, int second) {
        int maxIndex = this.children.size;
        if (first < 0 || first >= maxIndex || second < 0 || second >= maxIndex) {
            return false;
        }
        this.children.swap(first, second);
        return true;
    }

    public boolean swapActor(Actor first, Actor second) {
        int firstIndex = this.children.indexOf(first, true);
        int secondIndex = this.children.indexOf(second, true);
        if (firstIndex == -1 || secondIndex == -1) {
            return false;
        }
        this.children.swap(firstIndex, secondIndex);
        return true;
    }

    public SnapshotArray<Actor> getChildren() {
        return this.children;
    }

    public void setTransform(boolean transform2) {
        this.transform = transform2;
    }

    public boolean isTransform() {
        return this.transform;
    }

    public Vector2 localToDescendantCoordinates(Actor descendant, Vector2 localCoords) {
        Group parent = descendant.getParent();
        if (parent == null) {
            throw new IllegalArgumentException("Child is not a descendant: " + descendant);
        }
        if (parent != this) {
            localToDescendantCoordinates(parent, localCoords);
        }
        descendant.parentToLocalCoordinates(localCoords);
        return localCoords;
    }
}
