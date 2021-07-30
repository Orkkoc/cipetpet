package com.esotericsoftware.tablelayout;

import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Toolkit;
import com.esotericsoftware.tablelayout.Value;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTableLayout<C, T extends C, L extends BaseTableLayout, K extends Toolkit<C, T, L>> {
    public static final int BOTTOM = 4;
    public static final int CENTER = 1;
    public static final int LEFT = 8;
    public static final int RIGHT = 16;
    public static final int TOP = 2;
    int align = 1;
    private final Cell cellDefaults = Cell.defaults(this);
    private final ArrayList<Cell> cells = new ArrayList<>(4);
    private final ArrayList<Cell> columnDefaults = new ArrayList<>(2);
    private float[] columnMinWidth;
    private float[] columnPrefWidth;
    private float[] columnWeightedWidth;
    private float[] columnWidth;
    private int columns;
    Debug debug = Debug.none;
    private float[] expandHeight;
    private float[] expandWidth;
    Value padBottom;
    Value padLeft;
    Value padRight;
    Value padTop;
    private Cell rowDefaults;
    private float[] rowHeight;
    private float[] rowMinHeight;
    private float[] rowPrefHeight;
    private float[] rowWeightedHeight;
    private int rows;
    private boolean sizeInvalid = true;
    T table;
    private float tableMinHeight;
    private float tableMinWidth;
    private float tablePrefHeight;
    private float tablePrefWidth;
    K toolkit;

    public enum Debug {
        none,
        all,
        table,
        cell,
        widget
    }

    public abstract void invalidateHierarchy();

    public BaseTableLayout(K toolkit2) {
        this.toolkit = toolkit2;
    }

    public void invalidate() {
        this.sizeInvalid = true;
    }

    public Cell<C> add(C widget) {
        Cell columnDefaults2;
        Cell cell = new Cell(this);
        cell.widget = widget;
        if (this.cells.size() > 0) {
            Cell lastCell = this.cells.get(this.cells.size() - 1);
            if (!lastCell.endRow) {
                cell.column = lastCell.column + lastCell.colspan.intValue();
                cell.row = lastCell.row;
            } else {
                cell.row = lastCell.row + 1;
            }
            if (cell.row > 0) {
                int i = this.cells.size() - 1;
                loop0:
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    Cell other = this.cells.get(i);
                    int column = other.column;
                    int nn = column + other.colspan.intValue();
                    while (column < nn) {
                        if (column == cell.column) {
                            cell.cellAboveIndex = i;
                            break loop0;
                        }
                        column++;
                    }
                    i--;
                }
            }
        }
        this.cells.add(cell);
        cell.set(this.cellDefaults);
        if (cell.column < this.columnDefaults.size() && (columnDefaults2 = this.columnDefaults.get(cell.column)) != null) {
            cell.merge(columnDefaults2);
        }
        cell.merge(this.rowDefaults);
        if (widget != null) {
            this.toolkit.addChild(this.table, widget);
        }
        return cell;
    }

    public Cell row() {
        if (this.cells.size() > 0) {
            endRow();
        }
        this.rowDefaults = new Cell(this);
        return this.rowDefaults;
    }

    private void endRow() {
        int rowColumns = 0;
        for (int i = this.cells.size() - 1; i >= 0; i--) {
            Cell cell = this.cells.get(i);
            if (cell.endRow) {
                break;
            }
            rowColumns += cell.colspan.intValue();
        }
        this.columns = Math.max(this.columns, rowColumns);
        this.rows++;
        this.cells.get(this.cells.size() - 1).endRow = true;
        invalidate();
    }

    public Cell columnDefaults(int column) {
        Cell cell;
        if (this.columnDefaults.size() > column) {
            cell = this.columnDefaults.get(column);
        } else {
            cell = null;
        }
        if (cell == null) {
            cell = new Cell(this);
            if (column >= this.columnDefaults.size()) {
                for (int i = this.columnDefaults.size(); i < column; i++) {
                    this.columnDefaults.add((Object) null);
                }
                this.columnDefaults.add(cell);
            } else {
                this.columnDefaults.set(column, cell);
            }
        }
        return cell;
    }

    public void reset() {
        clear();
        this.padTop = null;
        this.padLeft = null;
        this.padBottom = null;
        this.padRight = null;
        this.align = 1;
        if (this.debug != Debug.none) {
            this.toolkit.clearDebugRectangles(this);
        }
        this.debug = Debug.none;
        this.cellDefaults.set(Cell.defaults(this));
        this.columnDefaults.clear();
    }

    public void clear() {
        for (int i = this.cells.size() - 1; i >= 0; i--) {
            Object widget = this.cells.get(i).widget;
            if (widget != null) {
                this.toolkit.removeChild(this.table, widget);
            }
        }
        this.cells.clear();
        this.rows = 0;
        this.columns = 0;
        this.rowDefaults = null;
        invalidate();
    }

    public Cell getCell(C widget) {
        int n = this.cells.size();
        for (int i = 0; i < n; i++) {
            Cell c = this.cells.get(i);
            if (c.widget == widget) {
                return c;
            }
        }
        return null;
    }

    public List<Cell> getCells() {
        return this.cells;
    }

    public void setToolkit(K toolkit2) {
        this.toolkit = toolkit2;
    }

    public T getTable() {
        return this.table;
    }

    public void setTable(T table2) {
        this.table = table2;
    }

    public float getMinWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tableMinWidth;
    }

    public float getMinHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tableMinHeight;
    }

    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tablePrefWidth;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tablePrefHeight;
    }

    public Cell defaults() {
        return this.cellDefaults;
    }

    public L pad(Value pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        this.sizeInvalid = true;
        return this;
    }

    public L pad(Value top, Value left, Value bottom, Value right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        this.sizeInvalid = true;
        return this;
    }

    public L padTop(Value padTop2) {
        this.padTop = padTop2;
        this.sizeInvalid = true;
        return this;
    }

    public L padLeft(Value padLeft2) {
        this.padLeft = padLeft2;
        this.sizeInvalid = true;
        return this;
    }

    public L padBottom(Value padBottom2) {
        this.padBottom = padBottom2;
        this.sizeInvalid = true;
        return this;
    }

    public L padRight(Value padRight2) {
        this.padRight = padRight2;
        this.sizeInvalid = true;
        return this;
    }

    public L pad(float pad) {
        this.padTop = new Value.FixedValue(pad);
        this.padLeft = new Value.FixedValue(pad);
        this.padBottom = new Value.FixedValue(pad);
        this.padRight = new Value.FixedValue(pad);
        this.sizeInvalid = true;
        return this;
    }

    public L pad(float top, float left, float bottom, float right) {
        this.padTop = new Value.FixedValue(top);
        this.padLeft = new Value.FixedValue(left);
        this.padBottom = new Value.FixedValue(bottom);
        this.padRight = new Value.FixedValue(right);
        this.sizeInvalid = true;
        return this;
    }

    public L padTop(float padTop2) {
        this.padTop = new Value.FixedValue(padTop2);
        this.sizeInvalid = true;
        return this;
    }

    public L padLeft(float padLeft2) {
        this.padLeft = new Value.FixedValue(padLeft2);
        this.sizeInvalid = true;
        return this;
    }

    public L padBottom(float padBottom2) {
        this.padBottom = new Value.FixedValue(padBottom2);
        this.sizeInvalid = true;
        return this;
    }

    public L padRight(float padRight2) {
        this.padRight = new Value.FixedValue(padRight2);
        this.sizeInvalid = true;
        return this;
    }

    public L align(int align2) {
        this.align = align2;
        return this;
    }

    public L center() {
        this.align = 1;
        return this;
    }

    public L top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public L left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public L bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public L right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    public L debug() {
        this.debug = Debug.all;
        invalidate();
        return this;
    }

    public L debugTable() {
        this.debug = Debug.table;
        invalidate();
        return this;
    }

    public L debugCell() {
        this.debug = Debug.cell;
        invalidate();
        return this;
    }

    public L debugWidget() {
        this.debug = Debug.widget;
        invalidate();
        return this;
    }

    public L debug(Debug debug2) {
        this.debug = debug2;
        if (debug2 == Debug.none) {
            this.toolkit.clearDebugRectangles(this);
        } else {
            invalidate();
        }
        return this;
    }

    public Debug getDebug() {
        return this.debug;
    }

    public Value getPadTopValue() {
        return this.padTop;
    }

    public float getPadTop() {
        if (this.padTop == null) {
            return 0.0f;
        }
        return this.padTop.height((Object) this);
    }

    public Value getPadLeftValue() {
        return this.padLeft;
    }

    public float getPadLeft() {
        if (this.padLeft == null) {
            return 0.0f;
        }
        return this.padLeft.width((Object) this);
    }

    public Value getPadBottomValue() {
        return this.padBottom;
    }

    public float getPadBottom() {
        if (this.padBottom == null) {
            return 0.0f;
        }
        return this.padBottom.height((Object) this);
    }

    public Value getPadRightValue() {
        return this.padRight;
    }

    public float getPadRight() {
        if (this.padRight == null) {
            return 0.0f;
        }
        return this.padRight.width((Object) this);
    }

    public int getAlign() {
        return this.align;
    }

    public int getRow(float y) {
        int row = 0;
        float y2 = y + m5h(this.padTop);
        int i = 0;
        int n = this.cells.size();
        if (n == 0) {
            return -1;
        }
        while (i < n && !this.cells.get(i).isEndRow()) {
            i++;
        }
        int i2 = i;
        while (true) {
            if (i2 >= n) {
                int i3 = i2;
                break;
            }
            int i4 = i2 + 1;
            Cell c = this.cells.get(i2);
            if (c.getIgnore()) {
                i2 = i4;
            } else if (c.widgetY + c.computedPadTop > y2) {
                break;
            } else {
                if (c.endRow) {
                    row++;
                }
                i2 = i4;
            }
        }
        return this.rows - row;
    }

    private float[] ensureSize(float[] array, int size) {
        if (array == null || array.length < size) {
            return new float[size];
        }
        int n = array.length;
        for (int i = 0; i < n; i++) {
            array[i] = 0.0f;
        }
        return array;
    }

    /* renamed from: w */
    private float m7w(Value value) {
        if (value == null) {
            return 0.0f;
        }
        return value.width((Object) this.table);
    }

    /* renamed from: h */
    private float m5h(Value value) {
        if (value == null) {
            return 0.0f;
        }
        return value.height((Object) this.table);
    }

    /* renamed from: w */
    private float m8w(Value value, Cell cell) {
        if (value == null) {
            return 0.0f;
        }
        return value.width(cell);
    }

    /* renamed from: h */
    private float m6h(Value value, Cell cell) {
        if (value == null) {
            return 0.0f;
        }
        return value.height(cell);
    }

    private void computeSize() {
        float f;
        float h;
        this.sizeInvalid = false;
        K k = this.toolkit;
        ArrayList<Cell> cells2 = this.cells;
        if (cells2.size() > 0 && !cells2.get(cells2.size() - 1).endRow) {
            endRow();
        }
        this.columnMinWidth = ensureSize(this.columnMinWidth, this.columns);
        this.rowMinHeight = ensureSize(this.rowMinHeight, this.rows);
        this.columnPrefWidth = ensureSize(this.columnPrefWidth, this.columns);
        this.rowPrefHeight = ensureSize(this.rowPrefHeight, this.rows);
        this.columnWidth = ensureSize(this.columnWidth, this.columns);
        this.rowHeight = ensureSize(this.rowHeight, this.rows);
        this.expandWidth = ensureSize(this.expandWidth, this.columns);
        this.expandHeight = ensureSize(this.expandHeight, this.rows);
        float spaceRightLast = 0.0f;
        int n = cells2.size();
        for (int i = 0; i < n; i++) {
            Cell c = cells2.get(i);
            if (!c.ignore.booleanValue()) {
                if (c.expandY.intValue() != 0 && this.expandHeight[c.row] == 0.0f) {
                    this.expandHeight[c.row] = (float) c.expandY.intValue();
                }
                if (c.colspan.intValue() == 1 && c.expandX.intValue() != 0 && this.expandWidth[c.column] == 0.0f) {
                    this.expandWidth[c.column] = (float) c.expandX.intValue();
                }
                c.computedPadLeft = (c.column == 0 ? 0.0f : Math.max(0.0f, m8w(c.spaceLeft, c) - spaceRightLast)) + m8w(c.padLeft, c);
                c.computedPadTop = m6h(c.padTop, c);
                if (c.cellAboveIndex != -1) {
                    Cell above = cells2.get(c.cellAboveIndex);
                    c.computedPadTop += Math.max(0.0f, m6h(c.spaceTop, c) - m6h(above.spaceBottom, above));
                }
                float spaceRight = m8w(c.spaceRight, c);
                float w = m8w(c.padRight, c);
                if (c.column + c.colspan.intValue() == this.columns) {
                    f = 0.0f;
                } else {
                    f = spaceRight;
                }
                c.computedPadRight = f + w;
                float h2 = m6h(c.padBottom, c);
                if (c.row == this.rows - 1) {
                    h = 0.0f;
                } else {
                    h = m6h(c.spaceBottom, c);
                }
                c.computedPadBottom = h + h2;
                spaceRightLast = spaceRight;
                float prefWidth = m8w(c.prefWidth, c);
                float prefHeight = m6h(c.prefHeight, c);
                float minWidth = m8w(c.minWidth, c);
                float minHeight = m6h(c.minHeight, c);
                float maxWidth = m8w(c.maxWidth, c);
                float maxHeight = m6h(c.maxHeight, c);
                if (prefWidth < minWidth) {
                    prefWidth = minWidth;
                }
                if (prefHeight < minHeight) {
                    prefHeight = minHeight;
                }
                if (maxWidth > 0.0f && prefWidth > maxWidth) {
                    prefWidth = maxWidth;
                }
                if (maxHeight > 0.0f && prefHeight > maxHeight) {
                    prefHeight = maxHeight;
                }
                if (c.colspan.intValue() == 1) {
                    float hpadding = c.computedPadLeft + c.computedPadRight;
                    this.columnPrefWidth[c.column] = Math.max(this.columnPrefWidth[c.column], prefWidth + hpadding);
                    this.columnMinWidth[c.column] = Math.max(this.columnMinWidth[c.column], minWidth + hpadding);
                }
                float vpadding = c.computedPadTop + c.computedPadBottom;
                this.rowPrefHeight[c.row] = Math.max(this.rowPrefHeight[c.row], prefHeight + vpadding);
                this.rowMinHeight[c.row] = Math.max(this.rowMinHeight[c.row], minHeight + vpadding);
            }
        }
        int n2 = cells2.size();
        for (int i2 = 0; i2 < n2; i2++) {
            Cell c2 = cells2.get(i2);
            if (!c2.ignore.booleanValue() && c2.expandX.intValue() != 0) {
                int column = c2.column;
                int nn = column + c2.colspan.intValue();
                while (true) {
                    if (column < nn) {
                        if (this.expandWidth[column] != 0.0f) {
                            break;
                        }
                        column++;
                    } else {
                        int column2 = c2.column;
                        int nn2 = column2 + c2.colspan.intValue();
                        while (column2 < nn2) {
                            this.expandWidth[column2] = (float) c2.expandX.intValue();
                            column2++;
                        }
                    }
                }
            }
        }
        int n3 = cells2.size();
        for (int i3 = 0; i3 < n3; i3++) {
            Cell c3 = cells2.get(i3);
            if (!c3.ignore.booleanValue() && c3.colspan.intValue() != 1) {
                float minWidth2 = m8w(c3.minWidth, c3);
                float prefWidth2 = m8w(c3.prefWidth, c3);
                float maxWidth2 = m8w(c3.maxWidth, c3);
                if (prefWidth2 < minWidth2) {
                    prefWidth2 = minWidth2;
                }
                if (maxWidth2 > 0.0f && prefWidth2 > maxWidth2) {
                    prefWidth2 = maxWidth2;
                }
                float spannedMinWidth = 0.0f;
                float spannedPrefWidth = 0.0f;
                int column3 = c3.column;
                int nn3 = column3 + c3.colspan.intValue();
                while (column3 < nn3) {
                    spannedMinWidth += this.columnMinWidth[column3];
                    spannedPrefWidth += this.columnPrefWidth[column3];
                    column3++;
                }
                float totalExpandWidth = 0.0f;
                int column4 = c3.column;
                int nn4 = column4 + c3.colspan.intValue();
                while (column4 < nn4) {
                    totalExpandWidth += this.expandWidth[column4];
                    column4++;
                }
                float extraMinWidth = Math.max(0.0f, minWidth2 - spannedMinWidth);
                float extraPrefWidth = Math.max(0.0f, prefWidth2 - spannedPrefWidth);
                int column5 = c3.column;
                int nn5 = column5 + c3.colspan.intValue();
                while (column5 < nn5) {
                    float ratio = totalExpandWidth == 0.0f ? 1.0f / ((float) c3.colspan.intValue()) : this.expandWidth[column5] / totalExpandWidth;
                    float[] fArr = this.columnMinWidth;
                    fArr[column5] = fArr[column5] + (extraMinWidth * ratio);
                    float[] fArr2 = this.columnPrefWidth;
                    fArr2[column5] = fArr2[column5] + (extraPrefWidth * ratio);
                    column5++;
                }
            }
        }
        float uniformMinWidth = 0.0f;
        float uniformMinHeight = 0.0f;
        float uniformPrefWidth = 0.0f;
        float uniformPrefHeight = 0.0f;
        int n4 = cells2.size();
        for (int i4 = 0; i4 < n4; i4++) {
            Cell c4 = cells2.get(i4);
            if (!c4.ignore.booleanValue()) {
                if (c4.uniformX == Boolean.TRUE && c4.colspan.intValue() == 1) {
                    float hpadding2 = c4.computedPadLeft + c4.computedPadRight;
                    uniformMinWidth = Math.max(uniformMinWidth, this.columnMinWidth[c4.column] - hpadding2);
                    uniformPrefWidth = Math.max(uniformPrefWidth, this.columnPrefWidth[c4.column] - hpadding2);
                }
                if (c4.uniformY == Boolean.TRUE) {
                    float vpadding2 = c4.computedPadTop + c4.computedPadBottom;
                    uniformMinHeight = Math.max(uniformMinHeight, this.rowMinHeight[c4.row] - vpadding2);
                    uniformPrefHeight = Math.max(uniformPrefHeight, this.rowPrefHeight[c4.row] - vpadding2);
                }
            }
        }
        if (uniformPrefWidth > 0.0f || uniformPrefHeight > 0.0f) {
            int n5 = cells2.size();
            for (int i5 = 0; i5 < n5; i5++) {
                Cell c5 = cells2.get(i5);
                if (!c5.ignore.booleanValue()) {
                    if (uniformPrefWidth > 0.0f && c5.uniformX == Boolean.TRUE && c5.colspan.intValue() == 1) {
                        float hpadding3 = c5.computedPadLeft + c5.computedPadRight;
                        this.columnMinWidth[c5.column] = uniformMinWidth + hpadding3;
                        this.columnPrefWidth[c5.column] = uniformPrefWidth + hpadding3;
                    }
                    if (uniformPrefHeight > 0.0f && c5.uniformY == Boolean.TRUE) {
                        float vpadding3 = c5.computedPadTop + c5.computedPadBottom;
                        this.rowMinHeight[c5.row] = uniformMinHeight + vpadding3;
                        this.rowPrefHeight[c5.row] = uniformPrefHeight + vpadding3;
                    }
                }
            }
        }
        this.tableMinWidth = 0.0f;
        this.tableMinHeight = 0.0f;
        this.tablePrefWidth = 0.0f;
        this.tablePrefHeight = 0.0f;
        for (int i6 = 0; i6 < this.columns; i6++) {
            this.tableMinWidth += this.columnMinWidth[i6];
            this.tablePrefWidth += this.columnPrefWidth[i6];
        }
        for (int i7 = 0; i7 < this.rows; i7++) {
            this.tableMinHeight += this.rowMinHeight[i7];
            this.tablePrefHeight += Math.max(this.rowMinHeight[i7], this.rowPrefHeight[i7]);
        }
        float hpadding4 = m7w(this.padLeft) + m7w(this.padRight);
        float vpadding4 = m5h(this.padTop) + m5h(this.padBottom);
        this.tableMinWidth += hpadding4;
        this.tableMinHeight += vpadding4;
        this.tablePrefWidth = Math.max(this.tablePrefWidth + hpadding4, this.tableMinWidth);
        this.tablePrefHeight = Math.max(this.tablePrefHeight + vpadding4, this.tableMinHeight);
    }

    public void layout(float layoutX, float layoutY, float layoutWidth, float layoutHeight) {
        float[] columnWeightedWidth2;
        float[] rowWeightedHeight2;
        float currentX;
        Toolkit toolkit2 = this.toolkit;
        ArrayList<Cell> cells2 = this.cells;
        if (this.sizeInvalid) {
            computeSize();
        }
        float hpadding = m7w(this.padLeft) + m7w(this.padRight);
        float vpadding = m5h(this.padTop) + m5h(this.padBottom);
        float totalMinWidth = 0.0f;
        float totalMinHeight = 0.0f;
        float totalExpandWidth = 0.0f;
        float totalExpandHeight = 0.0f;
        for (int i = 0; i < this.columns; i++) {
            totalMinWidth += this.columnMinWidth[i];
            totalExpandWidth += this.expandWidth[i];
        }
        for (int i2 = 0; i2 < this.rows; i2++) {
            totalMinHeight += this.rowMinHeight[i2];
            totalExpandHeight += this.expandHeight[i2];
        }
        float totalGrowWidth = this.tablePrefWidth - totalMinWidth;
        if (totalGrowWidth == 0.0f) {
            columnWeightedWidth2 = this.columnMinWidth;
        } else {
            float extraWidth = Math.min(totalGrowWidth, Math.max(0.0f, layoutWidth - totalMinWidth));
            columnWeightedWidth2 = ensureSize(this.columnWeightedWidth, this.columns);
            this.columnWeightedWidth = columnWeightedWidth2;
            for (int i3 = 0; i3 < this.columns; i3++) {
                columnWeightedWidth2[i3] = this.columnMinWidth[i3] + (extraWidth * ((this.columnPrefWidth[i3] - this.columnMinWidth[i3]) / totalGrowWidth));
            }
        }
        float totalGrowHeight = this.tablePrefHeight - totalMinHeight;
        if (totalGrowHeight == 0.0f) {
            rowWeightedHeight2 = this.rowMinHeight;
        } else {
            rowWeightedHeight2 = ensureSize(this.rowWeightedHeight, this.rows);
            this.rowWeightedHeight = rowWeightedHeight2;
            float extraHeight = Math.min(totalGrowHeight, Math.max(0.0f, layoutHeight - totalMinHeight));
            for (int i4 = 0; i4 < this.rows; i4++) {
                rowWeightedHeight2[i4] = this.rowMinHeight[i4] + (extraHeight * ((this.rowPrefHeight[i4] - this.rowMinHeight[i4]) / totalGrowHeight));
            }
        }
        int n = cells2.size();
        for (int i5 = 0; i5 < n; i5++) {
            Cell c = cells2.get(i5);
            if (!c.ignore.booleanValue()) {
                float spannedWeightedWidth = 0.0f;
                int column = c.column;
                int nn = column + c.colspan.intValue();
                while (column < nn) {
                    spannedWeightedWidth += columnWeightedWidth2[column];
                    column++;
                }
                float weightedHeight = rowWeightedHeight2[c.row];
                float prefWidth = m8w(c.prefWidth, c);
                float prefHeight = m6h(c.prefHeight, c);
                float minWidth = m8w(c.minWidth, c);
                float minHeight = m6h(c.minHeight, c);
                float maxWidth = m8w(c.maxWidth, c);
                float maxHeight = m6h(c.maxHeight, c);
                if (prefWidth < minWidth) {
                    prefWidth = minWidth;
                }
                if (prefHeight < minHeight) {
                    prefHeight = minHeight;
                }
                if (maxWidth > 0.0f && prefWidth > maxWidth) {
                    prefWidth = maxWidth;
                }
                if (maxHeight > 0.0f && prefHeight > maxHeight) {
                    prefHeight = maxHeight;
                }
                c.widgetWidth = Math.min((spannedWeightedWidth - c.computedPadLeft) - c.computedPadRight, prefWidth);
                c.widgetHeight = Math.min((weightedHeight - c.computedPadTop) - c.computedPadBottom, prefHeight);
                if (c.colspan.intValue() == 1) {
                    this.columnWidth[c.column] = Math.max(this.columnWidth[c.column], spannedWeightedWidth);
                }
                this.rowHeight[c.row] = Math.max(this.rowHeight[c.row], weightedHeight);
            }
        }
        if (totalExpandWidth > 0.0f) {
            float extra = layoutWidth - hpadding;
            for (int i6 = 0; i6 < this.columns; i6++) {
                extra -= this.columnWidth[i6];
            }
            float used = 0.0f;
            int lastIndex = 0;
            for (int i7 = 0; i7 < this.columns; i7++) {
                if (this.expandWidth[i7] != 0.0f) {
                    float amount = (this.expandWidth[i7] * extra) / totalExpandWidth;
                    float[] fArr = this.columnWidth;
                    fArr[i7] = fArr[i7] + amount;
                    used += amount;
                    lastIndex = i7;
                }
            }
            float[] fArr2 = this.columnWidth;
            fArr2[lastIndex] = fArr2[lastIndex] + (extra - used);
        }
        if (totalExpandHeight > 0.0f) {
            float extra2 = layoutHeight - vpadding;
            for (int i8 = 0; i8 < this.rows; i8++) {
                extra2 -= this.rowHeight[i8];
            }
            float used2 = 0.0f;
            int lastIndex2 = 0;
            for (int i9 = 0; i9 < this.rows; i9++) {
                if (this.expandHeight[i9] != 0.0f) {
                    float amount2 = (this.expandHeight[i9] * extra2) / totalExpandHeight;
                    float[] fArr3 = this.rowHeight;
                    fArr3[i9] = fArr3[i9] + amount2;
                    used2 += amount2;
                    lastIndex2 = i9;
                }
            }
            float[] fArr4 = this.rowHeight;
            fArr4[lastIndex2] = fArr4[lastIndex2] + (extra2 - used2);
        }
        int n2 = cells2.size();
        for (int i10 = 0; i10 < n2; i10++) {
            Cell c2 = cells2.get(i10);
            if (!c2.ignore.booleanValue() && c2.colspan.intValue() != 1) {
                float extraWidth2 = 0.0f;
                int column2 = c2.column;
                int nn2 = column2 + c2.colspan.intValue();
                while (column2 < nn2) {
                    extraWidth2 += columnWeightedWidth2[column2] - this.columnWidth[column2];
                    column2++;
                }
                float extraWidth3 = (extraWidth2 - Math.max(0.0f, c2.computedPadLeft + c2.computedPadRight)) / ((float) c2.colspan.intValue());
                if (extraWidth3 > 0.0f) {
                    int column3 = c2.column;
                    int nn3 = column3 + c2.colspan.intValue();
                    while (column3 < nn3) {
                        float[] fArr5 = this.columnWidth;
                        fArr5[column3] = fArr5[column3] + extraWidth3;
                        column3++;
                    }
                }
            }
        }
        float tableWidth = hpadding;
        float tableHeight = vpadding;
        for (int i11 = 0; i11 < this.columns; i11++) {
            tableWidth += this.columnWidth[i11];
        }
        for (int i12 = 0; i12 < this.rows; i12++) {
            tableHeight += this.rowHeight[i12];
        }
        float x = layoutX + m7w(this.padLeft);
        if ((this.align & 16) != 0) {
            x += layoutWidth - tableWidth;
        } else if ((this.align & 8) == 0) {
            x += (layoutWidth - tableWidth) / 2.0f;
        }
        float y = layoutY + m7w(this.padTop);
        if ((this.align & 4) != 0) {
            y += layoutHeight - tableHeight;
        } else if ((this.align & 2) == 0) {
            y += (layoutHeight - tableHeight) / 2.0f;
        }
        float currentX2 = x;
        float currentY = y;
        int n3 = cells2.size();
        for (int i13 = 0; i13 < n3; i13++) {
            Cell c3 = cells2.get(i13);
            if (!c3.ignore.booleanValue()) {
                float spannedCellWidth = 0.0f;
                int column4 = c3.column;
                int nn4 = column4 + c3.colspan.intValue();
                while (column4 < nn4) {
                    spannedCellWidth += this.columnWidth[column4];
                    column4++;
                }
                float spannedCellWidth2 = spannedCellWidth - (c3.computedPadLeft + c3.computedPadRight);
                float currentX3 = currentX2 + c3.computedPadLeft;
                if (c3.fillX.floatValue() > 0.0f) {
                    c3.widgetWidth = c3.fillX.floatValue() * spannedCellWidth2;
                    float maxWidth2 = m8w(c3.maxWidth, c3);
                    if (maxWidth2 > 0.0f) {
                        c3.widgetWidth = Math.min(c3.widgetWidth, maxWidth2);
                    }
                }
                if (c3.fillY.floatValue() > 0.0f) {
                    c3.widgetHeight = ((this.rowHeight[c3.row] * c3.fillY.floatValue()) - c3.computedPadTop) - c3.computedPadBottom;
                    float maxHeight2 = m6h(c3.maxHeight, c3);
                    if (maxHeight2 > 0.0f) {
                        c3.widgetHeight = Math.min(c3.widgetHeight, maxHeight2);
                    }
                }
                if ((c3.align.intValue() & 8) != 0) {
                    c3.widgetX = currentX3;
                } else if ((c3.align.intValue() & 16) != 0) {
                    c3.widgetX = (currentX3 + spannedCellWidth2) - c3.widgetWidth;
                } else {
                    c3.widgetX = ((spannedCellWidth2 - c3.widgetWidth) / 2.0f) + currentX3;
                }
                if ((c3.align.intValue() & 2) != 0) {
                    c3.widgetY = c3.computedPadTop + currentY;
                } else if ((c3.align.intValue() & 4) != 0) {
                    c3.widgetY = ((this.rowHeight[c3.row] + currentY) - c3.widgetHeight) - c3.computedPadBottom;
                } else {
                    c3.widgetY = ((((this.rowHeight[c3.row] - c3.widgetHeight) + c3.computedPadTop) - c3.computedPadBottom) / 2.0f) + currentY;
                }
                if (c3.endRow) {
                    currentX2 = x;
                    currentY += this.rowHeight[c3.row];
                } else {
                    currentX2 = currentX3 + c3.computedPadRight + spannedCellWidth2;
                }
            }
        }
        if (this.debug != Debug.none) {
            toolkit2.clearDebugRectangles(this);
            float currentX4 = x;
            float currentY2 = y;
            if (this.debug == Debug.table || this.debug == Debug.all) {
                toolkit2.addDebugRectangle(this, Debug.table, layoutX, layoutY, layoutWidth, layoutHeight);
                toolkit2.addDebugRectangle(this, Debug.table, x, y, tableWidth - hpadding, tableHeight - vpadding);
            }
            int i14 = 0;
            int n4 = cells2.size();
            while (i14 < n4) {
                Cell c4 = cells2.get(i14);
                if (c4.ignore.booleanValue()) {
                    currentX = currentX4;
                } else {
                    if (this.debug == Debug.widget || this.debug == Debug.all) {
                        toolkit2.addDebugRectangle(this, Debug.widget, c4.widgetX, c4.widgetY, c4.widgetWidth, c4.widgetHeight);
                    }
                    float spannedCellWidth3 = 0.0f;
                    int column5 = c4.column;
                    int nn5 = column5 + c4.colspan.intValue();
                    while (column5 < nn5) {
                        spannedCellWidth3 += this.columnWidth[column5];
                        column5++;
                    }
                    float spannedCellWidth4 = spannedCellWidth3 - (c4.computedPadLeft + c4.computedPadRight);
                    float currentX5 = currentX4 + c4.computedPadLeft;
                    if (this.debug == Debug.cell || this.debug == Debug.all) {
                        toolkit2.addDebugRectangle(this, Debug.cell, currentX5, currentY2 + c4.computedPadTop, spannedCellWidth4, (this.rowHeight[c4.row] - c4.computedPadTop) - c4.computedPadBottom);
                    }
                    if (c4.endRow) {
                        currentX = x;
                        currentY2 += this.rowHeight[c4.row];
                    } else {
                        currentX = currentX5 + c4.computedPadRight + spannedCellWidth4;
                    }
                }
                i14++;
                currentX4 = currentX;
            }
        }
    }
}
