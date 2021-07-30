package com.badlogic.gdx.utils;

public abstract class Pool<T> {
    private final Array<T> freeObjects;
    public final int max;

    public interface Poolable {
        void reset();
    }

    /* access modifiers changed from: protected */
    public abstract T newObject();

    public Pool() {
        this(16, Integer.MAX_VALUE);
    }

    public Pool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    public Pool(int initialCapacity, int max2) {
        this.freeObjects = new Array<>(false, initialCapacity);
        this.max = max2;
    }

    public T obtain() {
        return this.freeObjects.size == 0 ? newObject() : this.freeObjects.pop();
    }

    public void free(T object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        if (this.freeObjects.size < this.max) {
            this.freeObjects.add(object);
        }
        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
    }

    public void freeAll(Array<T> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        for (int i = 0; i < objects.size; i++) {
            T object = objects.get(i);
            if (object != null) {
                if (this.freeObjects.size < this.max) {
                    this.freeObjects.add(object);
                }
                if (object instanceof Poolable) {
                    ((Poolable) object).reset();
                }
            }
        }
    }

    public void clear() {
        this.freeObjects.clear();
    }
}
