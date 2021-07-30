package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.p000ui.TableToolkit;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;
import java.util.List;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TableLayout */
class TableLayout extends BaseTableLayout<Actor, Table, TableLayout, TableToolkit> {
    Array<TableToolkit.DebugRect> debugRects;
    private ImmediateModeRenderer debugRenderer;
    boolean round = true;

    public TableLayout() {
        super((TableToolkit) Toolkit.instance);
    }

    public void layout() {
        Table table = (Table) getTable();
        float width = table.getWidth();
        float height = table.getHeight();
        super.layout(0.0f, 0.0f, width, height);
        List<Cell> cells = getCells();
        if (this.round) {
            int n = cells.size();
            for (int i = 0; i < n; i++) {
                Cell c = cells.get(i);
                if (!c.getIgnore()) {
                    float widgetWidth = (float) Math.round(c.getWidgetWidth());
                    float widgetHeight = (float) Math.round(c.getWidgetHeight());
                    float widgetX = (float) Math.round(c.getWidgetX());
                    float widgetY = (height - ((float) Math.round(c.getWidgetY()))) - widgetHeight;
                    c.setWidgetX(widgetX);
                    c.setWidgetY(widgetY);
                    c.setWidgetWidth(widgetWidth);
                    c.setWidgetHeight(widgetHeight);
                    Actor actor = (Actor) c.getWidget();
                    if (actor != null) {
                        actor.setX(widgetX);
                        actor.setY(widgetY);
                        if (actor.getWidth() != widgetWidth || actor.getHeight() != widgetHeight) {
                            actor.setWidth(widgetWidth);
                            actor.setHeight(widgetHeight);
                            if (actor instanceof Layout) {
                                ((Layout) actor).invalidate();
                            }
                        }
                    }
                }
            }
        } else {
            int n2 = cells.size();
            for (int i2 = 0; i2 < n2; i2++) {
                Cell c2 = cells.get(i2);
                if (!c2.getIgnore()) {
                    float widgetWidth2 = c2.getWidgetWidth();
                    float widgetHeight2 = c2.getWidgetHeight();
                    float widgetX2 = c2.getWidgetX();
                    float widgetY2 = (height - c2.getWidgetY()) - widgetHeight2;
                    c2.setWidgetX(widgetX2);
                    c2.setWidgetY(widgetY2);
                    c2.setWidgetWidth(widgetWidth2);
                    c2.setWidgetHeight(widgetHeight2);
                    Actor actor2 = (Actor) c2.getWidget();
                    if (actor2 != null) {
                        actor2.setX(widgetX2);
                        actor2.setY(widgetY2);
                        if (actor2.getWidth() != widgetWidth2 || actor2.getHeight() != widgetHeight2) {
                            actor2.setWidth(widgetWidth2);
                            actor2.setHeight(widgetHeight2);
                            if (actor2 instanceof Layout) {
                                ((Layout) actor2).invalidate();
                            }
                        }
                    }
                }
            }
        }
        Array<Actor> children = table.getChildren();
        int n3 = children.size;
        for (int i3 = 0; i3 < n3; i3++) {
            Actor child = children.get(i3);
            if (child instanceof Layout) {
                ((Layout) child).validate();
            }
        }
    }

    public void invalidateHierarchy() {
        super.invalidate();
        ((Table) getTable()).invalidateHierarchy();
    }

    private void toStageCoordinates(Actor actor, Vector2 point) {
        point.f165x += actor.getX();
        point.f166y += actor.getY();
        toStageCoordinates(actor.getParent(), point);
    }

    public void drawDebug(SpriteBatch batch) {
        if (getDebug() != BaseTableLayout.Debug.none && this.debugRects != null) {
            if (this.debugRenderer == null) {
                if (Gdx.graphics.isGL20Available()) {
                    this.debugRenderer = new ImmediateModeRenderer20(64, false, true, 0);
                } else {
                    this.debugRenderer = new ImmediateModeRenderer10(64);
                }
            }
            float x = 0.0f;
            float y = 0.0f;
            for (Actor parent = (Actor) getTable(); parent != null; parent = parent.getParent()) {
                if (parent instanceof Group) {
                    x += parent.getX();
                    y += parent.getY();
                }
            }
            this.debugRenderer.begin(batch.getProjectionMatrix(), 1);
            int n = this.debugRects.size;
            for (int i = 0; i < n; i++) {
                TableToolkit.DebugRect rect = this.debugRects.get(i);
                float x1 = x + rect.f161x;
                float y1 = (rect.f162y + y) - rect.height;
                float x2 = x1 + rect.width;
                float y2 = y1 + rect.height;
                float r = rect.type == BaseTableLayout.Debug.cell ? 1.0f : 0.0f;
                float g = rect.type == BaseTableLayout.Debug.widget ? 1.0f : 0.0f;
                float b = rect.type == BaseTableLayout.Debug.table ? 1.0f : 0.0f;
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x1, y1, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x1, y2, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x1, y2, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x2, y2, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x2, y2, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x2, y1, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x2, y1, 0.0f);
                this.debugRenderer.color(r, g, b, 1.0f);
                this.debugRenderer.vertex(x1, y1, 0.0f);
                if (this.debugRenderer.getNumVertices() == 64) {
                    this.debugRenderer.end();
                    this.debugRenderer.begin(batch.getProjectionMatrix(), 1);
                }
            }
            this.debugRenderer.end();
        }
    }
}
