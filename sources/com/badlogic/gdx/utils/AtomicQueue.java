package com.badlogic.gdx.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicQueue<T> {
    private final AtomicReferenceArray<T> queue;
    private final AtomicInteger readIndex = new AtomicInteger();
    private final AtomicInteger writeIndex = new AtomicInteger();

    public AtomicQueue(int capacity) {
        this.queue = new AtomicReferenceArray<>(capacity);
    }

    private int next(int idx) {
        return (idx + 1) & (this.queue.length() - 1);
    }

    public boolean put(T value) {
        int write = this.writeIndex.get();
        int read = this.readIndex.get();
        int next = next(write);
        if (next == read) {
            return false;
        }
        this.queue.set(write, value);
        this.writeIndex.set(next);
        return true;
    }

    public T poll() {
        int read = this.readIndex.get();
        if (read == this.writeIndex.get()) {
            return null;
        }
        T t = this.queue.get(read);
        this.readIndex.set(next(read));
        return t;
    }
}
