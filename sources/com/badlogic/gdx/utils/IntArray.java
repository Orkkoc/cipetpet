package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Arrays;

public class IntArray {
    public int[] items;
    public boolean ordered;
    public int size;

    public IntArray() {
        this(true, 16);
    }

    public IntArray(int capacity) {
        this(true, capacity);
    }

    public IntArray(boolean ordered2, int capacity) {
        this.ordered = ordered2;
        this.items = new int[capacity];
    }

    public IntArray(IntArray array) {
        this.ordered = array.ordered;
        this.size = array.size;
        this.items = new int[this.size];
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    public IntArray(int[] array) {
        this(true, array);
    }

    public IntArray(boolean ordered2, int[] array) {
        this(ordered2, array.length);
        this.size = array.length;
        System.arraycopy(array, 0, this.items, 0, this.size);
    }

    public void add(int value) {
        int[] items2 = this.items;
        if (this.size == items2.length) {
            items2 = resize(Math.max(8, (int) (((float) this.size) * 1.75f)));
        }
        int i = this.size;
        this.size = i + 1;
        items2[i] = value;
    }

    public void addAll(IntArray array) {
        addAll(array, 0, array.size);
    }

    public void addAll(IntArray array, int offset, int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        addAll(array.items, offset, length);
    }

    public void addAll(int[] array) {
        addAll(array, 0, array.length);
    }

    public void addAll(int[] array, int offset, int length) {
        int[] items2 = this.items;
        int sizeNeeded = (this.size + length) - offset;
        if (sizeNeeded >= items2.length) {
            items2 = resize(Math.max(8, (int) (((float) sizeNeeded) * 1.75f)));
        }
        System.arraycopy(array, offset, items2, this.size, length);
        this.size += length;
    }

    public int get(int index) {
        if (index < this.size) {
            return this.items[index];
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public void set(int index, int value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.items[index] = value;
    }

    public void insert(int index, int value) {
        int[] items2 = this.items;
        if (this.size == items2.length) {
            items2 = resize(Math.max(8, (int) (((float) this.size) * 1.75f)));
        }
        if (this.ordered) {
            System.arraycopy(items2, index, items2, index + 1, this.size - index);
        } else {
            items2[this.size] = items2[index];
        }
        this.size++;
        items2[index] = value;
    }

    public void swap(int first, int second) {
        if (first >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(first));
        } else if (second >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(second));
        } else {
            int[] items2 = this.items;
            int firstValue = items2[first];
            items2[first] = items2[second];
            items2[second] = firstValue;
        }
    }

    public boolean contains(int value) {
        int[] items2 = this.items;
        int i = this.size - 1;
        while (i >= 0) {
            int i2 = i - 1;
            if (items2[i] == value) {
                return true;
            }
            i = i2;
        }
        int i3 = i;
        return false;
    }

    public int indexOf(int value) {
        int[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (items2[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(int value) {
        int[] items2 = this.items;
        for (int i = this.size - 1; i >= 0; i--) {
            if (items2[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public boolean removeValue(int value) {
        int[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (items2[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public int removeIndex(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        int[] items2 = this.items;
        int value = items2[index];
        this.size--;
        if (this.ordered) {
            System.arraycopy(items2, index + 1, items2, index, this.size - index);
        } else {
            items2[index] = items2[this.size];
        }
        return value;
    }

    public boolean removeAll(IntArray array) {
        int size2 = this.size;
        int startSize = size2;
        int[] items2 = this.items;
        int n = array.size;
        for (int i = 0; i < n; i++) {
            int item = array.get(i);
            int ii = 0;
            while (true) {
                if (ii >= size2) {
                    break;
                } else if (item == items2[ii]) {
                    removeIndex(ii);
                    size2--;
                    break;
                } else {
                    ii++;
                }
            }
        }
        return size2 != startSize;
    }

    public int pop() {
        int[] iArr = this.items;
        int i = this.size - 1;
        this.size = i;
        return iArr[i];
    }

    public int peek() {
        return this.items[this.size - 1];
    }

    public int first() {
        return this.items[0];
    }

    public void clear() {
        this.size = 0;
    }

    public void shrink() {
        resize(this.size);
    }

    public int[] ensureCapacity(int additionalCapacity) {
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.items.length) {
            resize(Math.max(8, sizeNeeded));
        }
        return this.items;
    }

    /* access modifiers changed from: protected */
    public int[] resize(int newSize) {
        int[] newItems = new int[newSize];
        System.arraycopy(this.items, 0, newItems, 0, Math.min(this.size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void sort() {
        Arrays.sort(this.items, 0, this.size);
    }

    public void reverse() {
        int lastIndex = this.size - 1;
        int n = this.size / 2;
        for (int i = 0; i < n; i++) {
            int ii = lastIndex - i;
            int temp = this.items[i];
            this.items[i] = this.items[ii];
            this.items[ii] = temp;
        }
    }

    public void shuffle() {
        for (int i = this.size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            int temp = this.items[i];
            this.items[i] = this.items[ii];
            this.items[ii] = temp;
        }
    }

    public void truncate(int newSize) {
        if (this.size > newSize) {
            this.size = newSize;
        }
    }

    public int random() {
        if (this.size == 0) {
            return 0;
        }
        return this.items[MathUtils.random(0, this.size - 1)];
    }

    public int[] toArray() {
        int[] array = new int[this.size];
        System.arraycopy(this.items, 0, array, 0, this.size);
        return array;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof IntArray)) {
            return false;
        }
        IntArray array = (IntArray) object;
        int n = this.size;
        if (n != array.size) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (this.items[i] != array.items[i]) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        int[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append(items2[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public String toString(String separator) {
        if (this.size == 0) {
            return "";
        }
        int[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append(items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(separator);
            buffer.append(items2[i]);
        }
        return buffer.toString();
    }
}
