package com.badlogic.gdx.utils;

import java.lang.reflect.Constructor;

public class ReflectionPool<T> extends Pool<T> {
    private final Class<T> type;

    public ReflectionPool(Class<T> type2) {
        this.type = type2;
    }

    public ReflectionPool(Class<T> type2, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.type = type2;
    }

    public ReflectionPool(Class<T> type2, int initialCapacity) {
        super(initialCapacity);
        this.type = type2;
    }

    /* access modifiers changed from: protected */
    public T newObject() {
        Constructor ctor;
        try {
            return this.type.newInstance();
        } catch (Exception ex) {
            try {
                ctor = this.type.getConstructor((Class[]) null);
            } catch (Exception e) {
                try {
                    ctor = this.type.getDeclaredConstructor((Class[]) null);
                    ctor.setAccessible(true);
                } catch (NoSuchMethodException e2) {
                    throw new RuntimeException("Class cannot be created (missing no-arg constructor): " + this.type.getName());
                }
            }
            try {
                return ctor.newInstance(new Object[0]);
            } catch (Exception e3) {
                throw new GdxRuntimeException("Unable to create new instance: " + this.type.getName(), ex);
            }
        }
    }
}
