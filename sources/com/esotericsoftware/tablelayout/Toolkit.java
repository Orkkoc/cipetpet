package com.esotericsoftware.tablelayout;

import com.esotericsoftware.tablelayout.BaseTableLayout;

public abstract class Toolkit<C, T extends C, L extends BaseTableLayout> {
    public static Toolkit instance;

    public abstract void addChild(C c, C c2);

    public abstract void addDebugRectangle(L l, BaseTableLayout.Debug debug, float f, float f2, float f3, float f4);

    public abstract void clearDebugRectangles(L l);

    public abstract float getHeight(C c);

    public abstract float getMaxHeight(C c);

    public abstract float getMaxWidth(C c);

    public abstract float getMinHeight(C c);

    public abstract float getMinWidth(C c);

    public abstract float getPrefHeight(C c);

    public abstract float getPrefWidth(C c);

    public abstract float getWidth(C c);

    public abstract void removeChild(C c, C c2);

    public void setWidget(L layout, Cell cell, C widget) {
        if (cell.widget != widget) {
            removeChild(layout.table, cell.widget);
            cell.widget = widget;
            if (widget != null) {
                addChild(layout.table, widget);
            }
        }
    }

    public float width(float value) {
        return value;
    }

    public float height(float value) {
        return value;
    }
}
