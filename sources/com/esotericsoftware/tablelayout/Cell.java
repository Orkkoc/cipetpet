package com.esotericsoftware.tablelayout;

import com.esotericsoftware.tablelayout.Value;

public class Cell<C> {
    Integer align;
    int cellAboveIndex = -1;
    Integer colspan;
    int column;
    float computedPadBottom;
    float computedPadLeft;
    float computedPadRight;
    float computedPadTop;
    boolean endRow;
    Integer expandX;
    Integer expandY;
    Float fillX;
    Float fillY;
    Boolean ignore;
    private final BaseTableLayout layout;
    Value maxHeight;
    Value maxWidth;
    Value minHeight;
    Value minWidth;
    Value padBottom;
    Value padLeft;
    Value padRight;
    Value padTop;
    Value prefHeight;
    Value prefWidth;
    int row;
    Value spaceBottom;
    Value spaceLeft;
    Value spaceRight;
    Value spaceTop;
    Boolean uniformX;
    Boolean uniformY;
    C widget;
    float widgetHeight;
    float widgetWidth;
    float widgetX;
    float widgetY;

    Cell(BaseTableLayout layout2) {
        this.layout = layout2;
    }

    /* access modifiers changed from: package-private */
    public void set(Cell defaults) {
        this.minWidth = defaults.minWidth;
        this.minHeight = defaults.minHeight;
        this.prefWidth = defaults.prefWidth;
        this.prefHeight = defaults.prefHeight;
        this.maxWidth = defaults.maxWidth;
        this.maxHeight = defaults.maxHeight;
        this.spaceTop = defaults.spaceTop;
        this.spaceLeft = defaults.spaceLeft;
        this.spaceBottom = defaults.spaceBottom;
        this.spaceRight = defaults.spaceRight;
        this.padTop = defaults.padTop;
        this.padLeft = defaults.padLeft;
        this.padBottom = defaults.padBottom;
        this.padRight = defaults.padRight;
        this.fillX = defaults.fillX;
        this.fillY = defaults.fillY;
        this.align = defaults.align;
        this.expandX = defaults.expandX;
        this.expandY = defaults.expandY;
        this.ignore = defaults.ignore;
        this.colspan = defaults.colspan;
        this.uniformX = defaults.uniformX;
        this.uniformY = defaults.uniformY;
    }

    /* access modifiers changed from: package-private */
    public void merge(Cell cell) {
        if (cell != null) {
            if (cell.minWidth != null) {
                this.minWidth = cell.minWidth;
            }
            if (cell.minHeight != null) {
                this.minHeight = cell.minHeight;
            }
            if (cell.prefWidth != null) {
                this.prefWidth = cell.prefWidth;
            }
            if (cell.prefHeight != null) {
                this.prefHeight = cell.prefHeight;
            }
            if (cell.maxWidth != null) {
                this.maxWidth = cell.maxWidth;
            }
            if (cell.maxHeight != null) {
                this.maxHeight = cell.maxHeight;
            }
            if (cell.spaceTop != null) {
                this.spaceTop = cell.spaceTop;
            }
            if (cell.spaceLeft != null) {
                this.spaceLeft = cell.spaceLeft;
            }
            if (cell.spaceBottom != null) {
                this.spaceBottom = cell.spaceBottom;
            }
            if (cell.spaceRight != null) {
                this.spaceRight = cell.spaceRight;
            }
            if (cell.padTop != null) {
                this.padTop = cell.padTop;
            }
            if (cell.padLeft != null) {
                this.padLeft = cell.padLeft;
            }
            if (cell.padBottom != null) {
                this.padBottom = cell.padBottom;
            }
            if (cell.padRight != null) {
                this.padRight = cell.padRight;
            }
            if (cell.fillX != null) {
                this.fillX = cell.fillX;
            }
            if (cell.fillY != null) {
                this.fillY = cell.fillY;
            }
            if (cell.align != null) {
                this.align = cell.align;
            }
            if (cell.expandX != null) {
                this.expandX = cell.expandX;
            }
            if (cell.expandY != null) {
                this.expandY = cell.expandY;
            }
            if (cell.ignore != null) {
                this.ignore = cell.ignore;
            }
            if (cell.colspan != null) {
                this.colspan = cell.colspan;
            }
            if (cell.uniformX != null) {
                this.uniformX = cell.uniformX;
            }
            if (cell.uniformY != null) {
                this.uniformY = cell.uniformY;
            }
        }
    }

    public Cell setWidget(C widget2) {
        this.layout.toolkit.setWidget(this.layout, this, widget2);
        return this;
    }

    public C getWidget() {
        return this.widget;
    }

    public boolean hasWidget() {
        return this.widget != null;
    }

    public Cell size(Value size) {
        this.minWidth = size;
        this.minHeight = size;
        this.prefWidth = size;
        this.prefHeight = size;
        this.maxWidth = size;
        this.maxHeight = size;
        return this;
    }

    public Cell size(Value width, Value height) {
        this.minWidth = width;
        this.minHeight = height;
        this.prefWidth = width;
        this.prefHeight = height;
        this.maxWidth = width;
        this.maxHeight = height;
        return this;
    }

    public Cell size(float size) {
        size((Value) new Value.FixedValue(size));
        return this;
    }

    public Cell size(float width, float height) {
        size((Value) new Value.FixedValue(width), (Value) new Value.FixedValue(height));
        return this;
    }

    public Cell width(Value width) {
        this.minWidth = width;
        this.prefWidth = width;
        this.maxWidth = width;
        return this;
    }

    public Cell width(float width) {
        width((Value) new Value.FixedValue(width));
        return this;
    }

    public Cell height(Value height) {
        this.minHeight = height;
        this.prefHeight = height;
        this.maxHeight = height;
        return this;
    }

    public Cell height(float height) {
        height((Value) new Value.FixedValue(height));
        return this;
    }

    public Cell minSize(Value size) {
        this.minWidth = size;
        this.minHeight = size;
        return this;
    }

    public Cell minSize(Value width, Value height) {
        this.minWidth = width;
        this.minHeight = height;
        return this;
    }

    public Cell minWidth(Value minWidth2) {
        this.minWidth = minWidth2;
        return this;
    }

    public Cell minHeight(Value minHeight2) {
        this.minHeight = minHeight2;
        return this;
    }

    public Cell minSize(float size) {
        this.minWidth = new Value.FixedValue(size);
        this.minHeight = new Value.FixedValue(size);
        return this;
    }

    public Cell minSize(float width, float height) {
        this.minWidth = new Value.FixedValue(width);
        this.minHeight = new Value.FixedValue(height);
        return this;
    }

    public Cell minWidth(float minWidth2) {
        this.minWidth = new Value.FixedValue(minWidth2);
        return this;
    }

    public Cell minHeight(float minHeight2) {
        this.minHeight = new Value.FixedValue(minHeight2);
        return this;
    }

    public Cell prefSize(Value size) {
        this.prefWidth = size;
        this.prefHeight = size;
        return this;
    }

    public Cell prefSize(Value width, Value height) {
        this.prefWidth = width;
        this.prefHeight = height;
        return this;
    }

    public Cell prefWidth(Value prefWidth2) {
        this.prefWidth = prefWidth2;
        return this;
    }

    public Cell prefHeight(Value prefHeight2) {
        this.prefHeight = prefHeight2;
        return this;
    }

    public Cell prefSize(float width, float height) {
        this.prefWidth = new Value.FixedValue(width);
        this.prefHeight = new Value.FixedValue(height);
        return this;
    }

    public Cell prefSize(float size) {
        this.prefWidth = new Value.FixedValue(size);
        this.prefHeight = new Value.FixedValue(size);
        return this;
    }

    public Cell prefWidth(float prefWidth2) {
        this.prefWidth = new Value.FixedValue(prefWidth2);
        return this;
    }

    public Cell prefHeight(float prefHeight2) {
        this.prefHeight = new Value.FixedValue(prefHeight2);
        return this;
    }

    public Cell maxSize(Value size) {
        this.maxWidth = size;
        this.maxHeight = size;
        return this;
    }

    public Cell maxSize(Value width, Value height) {
        this.maxWidth = width;
        this.maxHeight = height;
        return this;
    }

    public Cell maxWidth(Value maxWidth2) {
        this.maxWidth = maxWidth2;
        return this;
    }

    public Cell maxHeight(Value maxHeight2) {
        this.maxHeight = maxHeight2;
        return this;
    }

    public Cell maxSize(float size) {
        this.maxWidth = new Value.FixedValue(size);
        this.maxHeight = new Value.FixedValue(size);
        return this;
    }

    public Cell maxSize(float width, float height) {
        this.maxWidth = new Value.FixedValue(width);
        this.maxHeight = new Value.FixedValue(height);
        return this;
    }

    public Cell maxWidth(float maxWidth2) {
        this.maxWidth = new Value.FixedValue(maxWidth2);
        return this;
    }

    public Cell maxHeight(float maxHeight2) {
        this.maxHeight = new Value.FixedValue(maxHeight2);
        return this;
    }

    public Cell space(Value space) {
        this.spaceTop = space;
        this.spaceLeft = space;
        this.spaceBottom = space;
        this.spaceRight = space;
        return this;
    }

    public Cell space(Value top, Value left, Value bottom, Value right) {
        this.spaceTop = top;
        this.spaceLeft = left;
        this.spaceBottom = bottom;
        this.spaceRight = right;
        return this;
    }

    public Cell spaceTop(Value spaceTop2) {
        this.spaceTop = spaceTop2;
        return this;
    }

    public Cell spaceLeft(Value spaceLeft2) {
        this.spaceLeft = spaceLeft2;
        return this;
    }

    public Cell spaceBottom(Value spaceBottom2) {
        this.spaceBottom = spaceBottom2;
        return this;
    }

    public Cell spaceRight(Value spaceRight2) {
        this.spaceRight = spaceRight2;
        return this;
    }

    public Cell space(float space) {
        if (space < 0.0f) {
            throw new IllegalArgumentException("space cannot be < 0.");
        }
        Value value = new Value.FixedValue(space);
        this.spaceTop = value;
        this.spaceLeft = value;
        this.spaceBottom = value;
        this.spaceRight = value;
        return this;
    }

    public Cell space(float top, float left, float bottom, float right) {
        if (top < 0.0f) {
            throw new IllegalArgumentException("top cannot be < 0.");
        } else if (left < 0.0f) {
            throw new IllegalArgumentException("left cannot be < 0.");
        } else if (bottom < 0.0f) {
            throw new IllegalArgumentException("bottom cannot be < 0.");
        } else if (right < 0.0f) {
            throw new IllegalArgumentException("right cannot be < 0.");
        } else {
            this.spaceTop = new Value.FixedValue(top);
            this.spaceLeft = new Value.FixedValue(left);
            this.spaceBottom = new Value.FixedValue(bottom);
            this.spaceRight = new Value.FixedValue(right);
            return this;
        }
    }

    public Cell spaceTop(float spaceTop2) {
        if (spaceTop2 < 0.0f) {
            throw new IllegalArgumentException("spaceTop cannot be < 0.");
        }
        this.spaceTop = new Value.FixedValue(spaceTop2);
        return this;
    }

    public Cell spaceLeft(float spaceLeft2) {
        if (spaceLeft2 < 0.0f) {
            throw new IllegalArgumentException("spaceLeft cannot be < 0.");
        }
        this.spaceLeft = new Value.FixedValue(spaceLeft2);
        return this;
    }

    public Cell spaceBottom(float spaceBottom2) {
        if (spaceBottom2 < 0.0f) {
            throw new IllegalArgumentException("spaceBottom cannot be < 0.");
        }
        this.spaceBottom = new Value.FixedValue(spaceBottom2);
        return this;
    }

    public Cell spaceRight(float spaceRight2) {
        if (spaceRight2 < 0.0f) {
            throw new IllegalArgumentException("spaceRight cannot be < 0.");
        }
        this.spaceRight = new Value.FixedValue(spaceRight2);
        return this;
    }

    public Cell pad(Value pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }

    public Cell pad(Value top, Value left, Value bottom, Value right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }

    public Cell padTop(Value padTop2) {
        this.padTop = padTop2;
        return this;
    }

    public Cell padLeft(Value padLeft2) {
        this.padLeft = padLeft2;
        return this;
    }

    public Cell padBottom(Value padBottom2) {
        this.padBottom = padBottom2;
        return this;
    }

    public Cell padRight(Value padRight2) {
        this.padRight = padRight2;
        return this;
    }

    public Cell pad(float pad) {
        Value value = new Value.FixedValue(pad);
        this.padTop = value;
        this.padLeft = value;
        this.padBottom = value;
        this.padRight = value;
        return this;
    }

    public Cell pad(float top, float left, float bottom, float right) {
        this.padTop = new Value.FixedValue(top);
        this.padLeft = new Value.FixedValue(left);
        this.padBottom = new Value.FixedValue(bottom);
        this.padRight = new Value.FixedValue(right);
        return this;
    }

    public Cell padTop(float padTop2) {
        this.padTop = new Value.FixedValue(padTop2);
        return this;
    }

    public Cell padLeft(float padLeft2) {
        this.padLeft = new Value.FixedValue(padLeft2);
        return this;
    }

    public Cell padBottom(float padBottom2) {
        this.padBottom = new Value.FixedValue(padBottom2);
        return this;
    }

    public Cell padRight(float padRight2) {
        this.padRight = new Value.FixedValue(padRight2);
        return this;
    }

    public Cell fill() {
        this.fillX = Float.valueOf(1.0f);
        this.fillY = Float.valueOf(1.0f);
        return this;
    }

    public Cell fillX() {
        this.fillX = Float.valueOf(1.0f);
        return this;
    }

    public Cell fillY() {
        this.fillY = Float.valueOf(1.0f);
        return this;
    }

    public Cell fill(Float x, Float y) {
        this.fillX = x;
        this.fillY = y;
        return this;
    }

    public Cell fill(boolean x, boolean y) {
        float f;
        float f2 = 1.0f;
        if (x) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        this.fillX = Float.valueOf(f);
        if (!y) {
            f2 = 0.0f;
        }
        this.fillY = Float.valueOf(f2);
        return this;
    }

    public Cell fill(boolean fill) {
        float f;
        float f2 = 1.0f;
        if (fill) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        this.fillX = Float.valueOf(f);
        if (!fill) {
            f2 = 0.0f;
        }
        this.fillY = Float.valueOf(f2);
        return this;
    }

    public Cell align(Integer align2) {
        this.align = align2;
        return this;
    }

    public Cell center() {
        this.align = 1;
        return this;
    }

    public Cell top() {
        if (this.align == null) {
            this.align = 2;
        } else {
            this.align = Integer.valueOf(this.align.intValue() | 2);
            this.align = Integer.valueOf(this.align.intValue() & -5);
        }
        return this;
    }

    public Cell left() {
        if (this.align == null) {
            this.align = 8;
        } else {
            this.align = Integer.valueOf(this.align.intValue() | 8);
            this.align = Integer.valueOf(this.align.intValue() & -17);
        }
        return this;
    }

    public Cell bottom() {
        if (this.align == null) {
            this.align = 4;
        } else {
            this.align = Integer.valueOf(this.align.intValue() | 4);
            this.align = Integer.valueOf(this.align.intValue() & -3);
        }
        return this;
    }

    public Cell right() {
        if (this.align == null) {
            this.align = 16;
        } else {
            this.align = Integer.valueOf(this.align.intValue() | 16);
            this.align = Integer.valueOf(this.align.intValue() & -9);
        }
        return this;
    }

    public Cell expand() {
        this.expandX = 1;
        this.expandY = 1;
        return this;
    }

    public Cell expandX() {
        this.expandX = 1;
        return this;
    }

    public Cell expandY() {
        this.expandY = 1;
        return this;
    }

    public Cell expand(Integer x, Integer y) {
        this.expandX = x;
        this.expandY = y;
        return this;
    }

    public Cell expand(boolean x, boolean y) {
        int i;
        int i2 = 1;
        if (x) {
            i = 1;
        } else {
            i = 0;
        }
        this.expandX = Integer.valueOf(i);
        if (!y) {
            i2 = 0;
        }
        this.expandY = Integer.valueOf(i2);
        return this;
    }

    public Cell ignore(Boolean ignore2) {
        this.ignore = ignore2;
        return this;
    }

    public Cell ignore() {
        this.ignore = true;
        return this;
    }

    public boolean getIgnore() {
        return this.ignore != null && this.ignore.booleanValue();
    }

    public Cell colspan(Integer colspan2) {
        this.colspan = colspan2;
        return this;
    }

    public Cell uniform() {
        this.uniformX = true;
        this.uniformY = true;
        return this;
    }

    public Cell uniformX() {
        this.uniformX = true;
        return this;
    }

    public Cell uniformY() {
        this.uniformY = true;
        return this;
    }

    public Cell uniform(Boolean x, Boolean y) {
        this.uniformX = x;
        this.uniformY = y;
        return this;
    }

    public float getWidgetX() {
        return this.widgetX;
    }

    public void setWidgetX(float widgetX2) {
        this.widgetX = widgetX2;
    }

    public float getWidgetY() {
        return this.widgetY;
    }

    public void setWidgetY(float widgetY2) {
        this.widgetY = widgetY2;
    }

    public float getWidgetWidth() {
        return this.widgetWidth;
    }

    public void setWidgetWidth(float widgetWidth2) {
        this.widgetWidth = widgetWidth2;
    }

    public float getWidgetHeight() {
        return this.widgetHeight;
    }

    public void setWidgetHeight(float widgetHeight2) {
        this.widgetHeight = widgetHeight2;
    }

    public int getColumn() {
        return this.column;
    }

    public int getRow() {
        return this.row;
    }

    public Value getMinWidthValue() {
        return this.minWidth;
    }

    public float getMinWidth() {
        if (this.minWidth == null) {
            return 0.0f;
        }
        return this.minWidth.width(this);
    }

    public Value getMinHeightValue() {
        return this.minHeight;
    }

    public float getMinHeight() {
        if (this.minHeight == null) {
            return 0.0f;
        }
        return this.minHeight.height(this);
    }

    public Value getPrefWidthValue() {
        return this.prefWidth;
    }

    public float getPrefWidth() {
        if (this.prefWidth == null) {
            return 0.0f;
        }
        return this.prefWidth.width(this);
    }

    public Value getPrefHeightValue() {
        return this.prefHeight;
    }

    public float getPrefHeight() {
        if (this.prefHeight == null) {
            return 0.0f;
        }
        return this.prefHeight.height(this);
    }

    public Value getMaxWidthValue() {
        return this.maxWidth;
    }

    public float getMaxWidth() {
        if (this.maxWidth == null) {
            return 0.0f;
        }
        return this.maxWidth.width(this);
    }

    public Value getMaxHeightValue() {
        return this.maxHeight;
    }

    public float getMaxHeight() {
        if (this.maxHeight == null) {
            return 0.0f;
        }
        return this.maxHeight.height(this);
    }

    public Value getSpaceTopValue() {
        return this.spaceTop;
    }

    public float getSpaceTop() {
        if (this.spaceTop == null) {
            return 0.0f;
        }
        return this.spaceTop.height(this);
    }

    public Value getSpaceLeftValue() {
        return this.spaceLeft;
    }

    public float getSpaceLeft() {
        if (this.spaceLeft == null) {
            return 0.0f;
        }
        return this.spaceLeft.width(this);
    }

    public Value getSpaceBottomValue() {
        return this.spaceBottom;
    }

    public float getSpaceBottom() {
        if (this.spaceBottom == null) {
            return 0.0f;
        }
        return this.spaceBottom.height(this);
    }

    public Value getSpaceRightValue() {
        return this.spaceRight;
    }

    public float getSpaceRight() {
        if (this.spaceRight == null) {
            return 0.0f;
        }
        return this.spaceRight.width(this);
    }

    public Value getPadTopValue() {
        return this.padTop;
    }

    public float getPadTop() {
        if (this.padTop == null) {
            return 0.0f;
        }
        return this.padTop.height(this);
    }

    public Value getPadLeftValue() {
        return this.padLeft;
    }

    public float getPadLeft() {
        if (this.padLeft == null) {
            return 0.0f;
        }
        return this.padLeft.width(this);
    }

    public Value getPadBottomValue() {
        return this.padBottom;
    }

    public float getPadBottom() {
        if (this.padBottom == null) {
            return 0.0f;
        }
        return this.padBottom.height(this);
    }

    public Value getPadRightValue() {
        return this.padRight;
    }

    public float getPadRight() {
        if (this.padRight == null) {
            return 0.0f;
        }
        return this.padRight.width(this);
    }

    public Float getFillX() {
        return this.fillX;
    }

    public Float getFillY() {
        return this.fillY;
    }

    public Integer getAlign() {
        return this.align;
    }

    public Integer getExpandX() {
        return this.expandX;
    }

    public Integer getExpandY() {
        return this.expandY;
    }

    public Integer getColspan() {
        return this.colspan;
    }

    public Boolean getUniformX() {
        return this.uniformX;
    }

    public Boolean getUniformY() {
        return this.uniformY;
    }

    public boolean isEndRow() {
        return this.endRow;
    }

    public float getComputedPadTop() {
        return this.computedPadTop;
    }

    public float getComputedPadLeft() {
        return this.computedPadLeft;
    }

    public float getComputedPadBottom() {
        return this.computedPadBottom;
    }

    public float getComputedPadRight() {
        return this.computedPadRight;
    }

    public Cell row() {
        return this.layout.row();
    }

    public BaseTableLayout getLayout() {
        return this.layout;
    }

    static Cell defaults(BaseTableLayout layout2) {
        Cell defaults = new Cell(layout2);
        defaults.minWidth = Value.minWidth;
        defaults.minHeight = Value.minHeight;
        defaults.prefWidth = Value.prefWidth;
        defaults.prefHeight = Value.prefHeight;
        defaults.maxWidth = Value.maxWidth;
        defaults.maxHeight = Value.maxHeight;
        defaults.spaceTop = Value.zero;
        defaults.spaceLeft = Value.zero;
        defaults.spaceBottom = Value.zero;
        defaults.spaceRight = Value.zero;
        defaults.padTop = Value.zero;
        defaults.padLeft = Value.zero;
        defaults.padBottom = Value.zero;
        defaults.padRight = Value.zero;
        defaults.fillX = Float.valueOf(0.0f);
        defaults.fillY = Float.valueOf(0.0f);
        defaults.align = 1;
        defaults.expandX = 0;
        defaults.expandY = 0;
        defaults.ignore = false;
        defaults.colspan = 1;
        return defaults;
    }
}
