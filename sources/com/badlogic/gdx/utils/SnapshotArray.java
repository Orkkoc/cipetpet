package com.badlogic.gdx.utils;

import java.util.Comparator;

public class SnapshotArray<T> extends Array<T> {
    private T[] recycled;
    private T[] snapshot;
    private int snapshots;

    public SnapshotArray() {
    }

    public SnapshotArray(Array array) {
        super(array);
    }

    public SnapshotArray(boolean ordered, int capacity, Class<T> arrayType) {
        super(ordered, capacity, arrayType);
    }

    public SnapshotArray(boolean ordered, int capacity) {
        super(ordered, capacity);
    }

    public SnapshotArray(boolean ordered, T[] array) {
        super(ordered, array);
    }

    public SnapshotArray(Class<T> arrayType) {
        super(arrayType);
    }

    public SnapshotArray(int capacity) {
        super(capacity);
    }

    public SnapshotArray(T[] array) {
        super(array);
    }

    public T[] begin() {
        this.snapshot = this.items;
        this.snapshots++;
        return this.items;
    }

    public void end() {
        this.snapshots = Math.max(0, this.snapshots - 1);
        if (this.snapshot != null) {
            if (this.snapshot != this.items && this.snapshots == 0) {
                this.recycled = this.snapshot;
                int n = this.recycled.length;
                for (int i = 0; i < n; i++) {
                    this.recycled[i] = null;
                }
            }
            this.snapshot = null;
        }
    }

    private void modified() {
        if (this.snapshot != null && this.snapshot == this.items) {
            if (this.recycled == null || this.recycled.length < this.size) {
                resize(this.items.length);
                return;
            }
            System.arraycopy(this.items, 0, this.recycled, 0, this.size);
            this.items = this.recycled;
            this.recycled = null;
        }
    }

    public void set(int index, T value) {
        modified();
        super.set(index, value);
    }

    public void insert(int index, T value) {
        modified();
        super.insert(index, value);
    }

    public void swap(int first, int second) {
        modified();
        super.swap(first, second);
    }

    public boolean removeValue(T value, boolean identity) {
        modified();
        return super.removeValue(value, identity);
    }

    public T removeIndex(int index) {
        modified();
        return super.removeIndex(index);
    }

    public T pop() {
        modified();
        return super.pop();
    }

    public void clear() {
        modified();
        super.clear();
    }

    public void sort() {
        modified();
        super.sort();
    }

    public void sort(Comparator<T> comparator) {
        modified();
        super.sort(comparator);
    }

    public void reverse() {
        modified();
        super.reverse();
    }

    public void shuffle() {
        modified();
        super.shuffle();
    }

    public void truncate(int newSize) {
        modified();
        super.truncate(newSize);
    }
}
