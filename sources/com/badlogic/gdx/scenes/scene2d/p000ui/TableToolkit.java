package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Toolkit;
import java.lang.reflect.InvocationTargetException;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TableToolkit */
class TableToolkit extends Toolkit<Actor, Table, TableLayout> {
    static boolean drawDebug;

    TableToolkit() {
    }

    public void addChild(Actor parent, Actor child) {
        child.remove();
        try {
            parent.getClass().getMethod("setWidget", new Class[]{Actor.class}).invoke(parent, new Object[]{child});
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Error calling setWidget.", ex);
        } catch (Exception e) {
            ((Group) parent).addActor(child);
        }
    }

    public void removeChild(Actor parent, Actor child) {
        ((Group) parent).removeActor(child);
    }

    public float getMinWidth(Actor actor) {
        if (actor instanceof Layout) {
            return ((Layout) actor).getMinWidth();
        }
        return actor.getWidth();
    }

    public float getMinHeight(Actor actor) {
        if (actor instanceof Layout) {
            return ((Layout) actor).getMinHeight();
        }
        return actor.getHeight();
    }

    public float getPrefWidth(Actor actor) {
        if (actor instanceof Layout) {
            return ((Layout) actor).getPrefWidth();
        }
        return actor.getWidth();
    }

    public float getPrefHeight(Actor actor) {
        if (actor instanceof Layout) {
            return ((Layout) actor).getPrefHeight();
        }
        return actor.getHeight();
    }

    public float getMaxWidth(Actor actor) {
        if (actor instanceof Layout) {
            return ((Layout) actor).getMaxWidth();
        }
        return 0.0f;
    }

    public float getMaxHeight(Actor actor) {
        if (actor instanceof Layout) {
            return ((Layout) actor).getMaxHeight();
        }
        return 0.0f;
    }

    public float getWidth(Actor widget) {
        return widget.getWidth();
    }

    public float getHeight(Actor widget) {
        return widget.getHeight();
    }

    public void clearDebugRectangles(TableLayout layout) {
        if (layout.debugRects != null) {
            layout.debugRects.clear();
        }
    }

    public void addDebugRectangle(TableLayout layout, BaseTableLayout.Debug type, float x, float y, float w, float h) {
        drawDebug = true;
        if (layout.debugRects == null) {
            layout.debugRects = new Array<>();
        }
        layout.debugRects.add(new DebugRect(type, x, ((Table) layout.getTable()).getHeight() - y, w, h));
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TableToolkit$DebugRect */
    static class DebugRect extends Rectangle {
        final BaseTableLayout.Debug type;

        public DebugRect(BaseTableLayout.Debug type2, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.type = type2;
        }
    }
}
