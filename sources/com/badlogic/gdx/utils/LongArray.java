package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Arrays;

public class LongArray {
    public long[] items;
    public boolean ordered;
    public int size;

    public LongArray() {
        this(true, 16);
    }

    public LongArray(int capacity) {
        this(true, capacity);
    }

    public LongArray(boolean ordered2, int capacity) {
        this.ordered = ordered2;
        this.items = new long[capacity];
    }

    public LongArray(LongArray array) {
        this.ordered = array.ordered;
        this.size = array.size;
        this.items = new long[this.size];
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    public LongArray(long[] array) {
        this(true, array);
    }

    public LongArray(boolean ordered2, long[] array) {
        this(ordered2, array.length);
        this.size = array.length;
        System.arraycopy(array, 0, this.items, 0, this.size);
    }

    public void add(long value) {
        long[] items2 = this.items;
        if (this.size == items2.length) {
            items2 = resize(Math.max(8, (int) (((float) this.size) * 1.75f)));
        }
        int i = this.size;
        this.size = i + 1;
        items2[i] = value;
    }

    public void addAll(LongArray array) {
        addAll(array, 0, array.size);
    }

    public void addAll(LongArray array, int offset, int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        addAll(array.items, offset, length);
    }

    public void addAll(long[] array) {
        addAll(array, 0, array.length);
    }

    public void addAll(long[] array, int offset, int length) {
        long[] items2 = this.items;
        int sizeNeeded = (this.size + length) - offset;
        if (sizeNeeded >= items2.length) {
            items2 = resize(Math.max(8, (int) (((float) sizeNeeded) * 1.75f)));
        }
        System.arraycopy(array, offset, items2, this.size, length);
        this.size += length;
    }

    public long get(int index) {
        if (index < this.size) {
            return this.items[index];
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public void set(int index, long value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.items[index] = value;
    }

    public void insert(int index, long value) {
        long[] items2 = this.items;
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
            long[] items2 = this.items;
            long firstValue = items2[first];
            items2[first] = items2[second];
            items2[second] = firstValue;
        }
    }

    public boolean contains(long value) {
        long[] items2 = this.items;
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

    public int indexOf(long value) {
        long[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (items2[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(char value) {
        long[] items2 = this.items;
        for (int i = this.size - 1; i >= 0; i--) {
            if (items2[i] == ((long) value)) {
                return i;
            }
        }
        return -1;
    }

    public boolean removeValue(long value) {
        long[] items2 = this.items;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            if (items2[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public long removeIndex(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        long[] items2 = this.items;
        long value = items2[index];
        this.size--;
        if (this.ordered) {
            System.arraycopy(items2, index + 1, items2, index, this.size - index);
        } else {
            items2[index] = items2[this.size];
        }
        return value;
    }

    public boolean removeAll(LongArray array) {
        int size2 = this.size;
        int startSize = size2;
        long[] items2 = this.items;
        int n = array.size;
        for (int i = 0; i < n; i++) {
            long item = array.get(i);
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

    public long pop() {
        long[] jArr = this.items;
        int i = this.size - 1;
        this.size = i;
        return jArr[i];
    }

    public long peek() {
        return this.items[this.size - 1];
    }

    public long first() {
        return this.items[0];
    }

    public void clear() {
        this.size = 0;
    }

    public void shrink() {
        resize(this.size);
    }

    public long[] ensureCapacity(int additionalCapacity) {
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.items.length) {
            resize(Math.max(8, sizeNeeded));
        }
        return this.items;
    }

    /* access modifiers changed from: protected */
    public long[] resize(int newSize) {
        long[] newItems = new long[newSize];
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
            long temp = this.items[i];
            this.items[i] = this.items[ii];
            this.items[ii] = temp;
        }
    }

    public void shuffle() {
        for (int i = this.size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            long temp = this.items[i];
            this.items[i] = this.items[ii];
            this.items[ii] = temp;
        }
    }

    public void truncate(int newSize) {
        if (this.size > newSize) {
            this.size = newSize;
        }
    }

    public long random() {
        if (this.size == 0) {
            return 0;
        }
        return this.items[MathUtils.random(0, this.size - 1)];
    }

    public long[] toArray() {
        long[] array = new long[this.size];
        System.arraycopy(this.items, 0, array, 0, this.size);
        return array;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof LongArray)) {
            return false;
        }
        LongArray array = (LongArray) object;
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
        long[] items2 = this.items;
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
        long[] items2 = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append(items2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(separator);
            buffer.append(items2[i]);
        }
        return buffer.toString();
    }
}
