package com.badlogic.gdx.utils;

public class Pools {
    private static final ObjectMap<Class, ReflectionPool> typePools = new ObjectMap<>();

    public static <T> Pool<T> get(Class<T> type) {
        ReflectionPool pool = typePools.get(type);
        if (pool != null) {
            return pool;
        }
        ReflectionPool pool2 = new ReflectionPool(type, 4, 100);
        typePools.put(type, pool2);
        return pool2;
    }

    public static <T> T obtain(Class<T> type) {
        return get(type).obtain();
    }

    public static void free(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        ReflectionPool pool = typePools.get(object.getClass());
        if (pool == null) {
            throw new IllegalArgumentException("No objects have been obtained of type: " + object.getClass().getName());
        }
        pool.free(object);
    }

    public static void freeAll(Array objects) {
        if (objects == null) {
            throw new IllegalArgumentException("objects cannot be null.");
        }
        int n = objects.size;
        for (int i = 0; i < n; i++) {
            Object object = objects.get(i);
            ReflectionPool pool = typePools.get(object.getClass());
            if (pool == null) {
                throw new IllegalArgumentException("No objects have been obtained of type: " + object.getClass().getName());
            }
            pool.free(object);
        }
    }

    private Pools() {
    }
}
