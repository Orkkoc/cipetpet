package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayMap<K, V> {
    private Entries entries;
    public K[] keys;
    private Keys keysIter;
    public boolean ordered;
    public int size;
    public V[] values;
    private Values valuesIter;

    public ArrayMap() {
        this(true, 16);
    }

    public ArrayMap(int capacity) {
        this(true, capacity);
    }

    public ArrayMap(boolean ordered2, int capacity) {
        this.ordered = ordered2;
        this.keys = (Object[]) new Object[capacity];
        this.values = (Object[]) new Object[capacity];
    }

    public ArrayMap(boolean ordered2, int capacity, Class<K> keyArrayType, Class<V> valueArrayType) {
        this.ordered = ordered2;
        this.keys = (Object[]) Array.newInstance(keyArrayType, capacity);
        this.values = (Object[]) Array.newInstance(valueArrayType, capacity);
    }

    public ArrayMap(Class<K> keyArrayType, Class<V> valueArrayType) {
        this(false, 16, keyArrayType, valueArrayType);
    }

    public ArrayMap(ArrayMap array) {
        this(array.ordered, array.size, array.keys.getClass().getComponentType(), array.values.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.keys, 0, this.keys, 0, this.size);
        System.arraycopy(array.values, 0, this.values, 0, this.size);
    }

    public void put(K key, V value) {
        if (this.size == this.keys.length) {
            resize(Math.max(8, (int) (((float) this.size) * 1.75f)));
        }
        int index = indexOfKey(key);
        if (index == -1) {
            index = this.size;
            this.size = index + 1;
        }
        this.keys[index] = key;
        this.values[index] = value;
    }

    public void put(K key, V value, int index) {
        if (this.size == this.keys.length) {
            resize(Math.max(8, (int) (((float) this.size) * 1.75f)));
        }
        int existingIndex = indexOfKey(key);
        if (existingIndex != -1) {
            removeIndex(existingIndex);
        }
        System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
        System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
        this.keys[index] = key;
        this.values[index] = value;
        this.size++;
    }

    public void addAll(ArrayMap map) {
        addAll(map, 0, map.size);
    }

    public void addAll(ArrayMap map, int offset, int length) {
        if (offset + length > map.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + map.size);
        }
        int sizeNeeded = (this.size + length) - offset;
        if (sizeNeeded >= this.keys.length) {
            resize(Math.max(8, (int) (((float) sizeNeeded) * 1.75f)));
        }
        System.arraycopy(map.keys, offset, this.keys, this.size, length);
        System.arraycopy(map.values, offset, this.values, this.size, length);
        this.size += length;
    }

    public V get(K key) {
        Object[] keys2 = this.keys;
        int i = this.size - 1;
        if (key == null) {
            while (i >= 0) {
                if (keys2[i] == key) {
                    return this.values[i];
                }
                i--;
            }
        } else {
            while (i >= 0) {
                if (key.equals(keys2[i])) {
                    return this.values[i];
                }
                i--;
            }
        }
        return null;
    }

    public K getKey(V v, boolean identity) {
        Object[] values2 = this.values;
        int i = this.size - 1;
        if (identity || values2 == null) {
            while (i >= 0) {
                if (values2[i] == values2) {
                    return this.keys[i];
                }
                i--;
            }
        } else {
            while (i >= 0) {
                if (values2.equals(values2[i])) {
                    return this.keys[i];
                }
                i--;
            }
        }
        return null;
    }

    public K getKeyAt(int index) {
        if (index < this.size) {
            return this.keys[index];
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public V getValueAt(int index) {
        if (index < this.size) {
            return this.values[index];
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public K firstKey() {
        return this.keys[0];
    }

    public V firstValue() {
        return this.values[0];
    }

    public void setKey(int index, K key) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.keys[index] = key;
    }

    public void setValue(int index, V value) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        this.values[index] = value;
    }

    public void insert(int index, K key, V value) {
        if (this.size == this.keys.length) {
            resize(Math.max(8, (int) (((float) this.size) * 1.75f)));
        }
        if (this.ordered) {
            System.arraycopy(this.keys, index, this.keys, index + 1, this.size - index);
            System.arraycopy(this.values, index, this.values, index + 1, this.size - index);
        } else {
            this.keys[this.size] = this.keys[index];
            this.values[this.size] = this.values[index];
        }
        this.size++;
        this.keys[index] = key;
        this.values[index] = value;
    }

    public boolean containsKey(K key) {
        int i;
        K[] keys2 = this.keys;
        int i2 = this.size - 1;
        if (key == null) {
            do {
                i = i2;
                if (i >= 0) {
                    i2 = i - 1;
                }
            } while (keys2[i] != key);
            return true;
        }
        do {
            i = i2;
            if (i >= 0) {
                i2 = i - 1;
            }
        } while (!key.equals(keys2[i]));
        return true;
        return false;
    }

    public boolean containsValue(V value, boolean identity) {
        int i;
        V[] values2 = this.values;
        int i2 = this.size - 1;
        if (identity || value == null) {
            do {
                i = i2;
                if (i >= 0) {
                    i2 = i - 1;
                }
            } while (values2[i] != value);
            return true;
        }
        do {
            i = i2;
            if (i >= 0) {
                i2 = i - 1;
            }
        } while (!value.equals(values2[i]));
        return true;
        return false;
    }

    public int indexOfKey(K key) {
        Object[] keys2 = this.keys;
        if (key == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (keys2[i] == key) {
                    return i;
                }
            }
        } else {
            int n2 = this.size;
            for (int i2 = 0; i2 < n2; i2++) {
                if (key.equals(keys2[i2])) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public int indexOfValue(V value, boolean identity) {
        Object[] values2 = this.values;
        if (identity || value == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (values2[i] == value) {
                    return i;
                }
            }
        } else {
            int n2 = this.size;
            for (int i2 = 0; i2 < n2; i2++) {
                if (value.equals(values2[i2])) {
                    return i2;
                }
            }
        }
        return -1;
    }

    public V removeKey(K key) {
        Object[] keys2 = this.keys;
        if (key == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (keys2[i] == key) {
                    V value = this.values[i];
                    removeIndex(i);
                    return value;
                }
            }
        } else {
            int n2 = this.size;
            for (int i2 = 0; i2 < n2; i2++) {
                if (key.equals(keys2[i2])) {
                    V value2 = this.values[i2];
                    removeIndex(i2);
                    return value2;
                }
            }
        }
        return null;
    }

    public boolean removeValue(V value, boolean identity) {
        Object[] values2 = this.values;
        if (identity || value == null) {
            int n = this.size;
            for (int i = 0; i < n; i++) {
                if (values2[i] == value) {
                    removeIndex(i);
                    return true;
                }
            }
        } else {
            int n2 = this.size;
            for (int i2 = 0; i2 < n2; i2++) {
                if (value.equals(values2[i2])) {
                    removeIndex(i2);
                    return true;
                }
            }
        }
        return false;
    }

    public void removeIndex(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        Object[] keys2 = this.keys;
        this.size--;
        if (this.ordered) {
            System.arraycopy(keys2, index + 1, keys2, index, this.size - index);
            System.arraycopy(this.values, index + 1, this.values, index, this.size - index);
        } else {
            keys2[index] = keys2[this.size];
            this.values[index] = this.values[this.size];
        }
        keys2[this.size] = null;
        this.values[this.size] = null;
    }

    public K peekKey() {
        return this.keys[this.size - 1];
    }

    public V peekValue() {
        return this.values[this.size - 1];
    }

    public void clear() {
        K[] keys2 = this.keys;
        V[] values2 = this.values;
        int n = this.size;
        for (int i = 0; i < n; i++) {
            keys2[i] = null;
            values2[i] = null;
        }
        this.size = 0;
    }

    public void shrink() {
        resize(this.size);
    }

    public void ensureCapacity(int additionalCapacity) {
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.keys.length) {
            resize(Math.max(8, sizeNeeded));
        }
    }

    /* access modifiers changed from: protected */
    public void resize(int newSize) {
        K[] newKeys = (Object[]) ((Object[]) Array.newInstance(this.keys.getClass().getComponentType(), newSize));
        System.arraycopy(this.keys, 0, newKeys, 0, Math.min(this.keys.length, newKeys.length));
        this.keys = newKeys;
        V[] newValues = (Object[]) ((Object[]) Array.newInstance(this.values.getClass().getComponentType(), newSize));
        System.arraycopy(this.values, 0, newValues, 0, Math.min(this.values.length, newValues.length));
        this.values = newValues;
    }

    public void reverse() {
        int lastIndex = this.size - 1;
        int n = this.size / 2;
        for (int i = 0; i < n; i++) {
            int ii = lastIndex - i;
            K tempKey = this.keys[i];
            this.keys[i] = this.keys[ii];
            this.keys[ii] = tempKey;
            V tempValue = this.values[i];
            this.values[i] = this.values[ii];
            this.values[ii] = tempValue;
        }
    }

    public void shuffle() {
        for (int i = this.size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            K tempKey = this.keys[i];
            this.keys[i] = this.keys[ii];
            this.keys[ii] = tempKey;
            V tempValue = this.values[i];
            this.values[i] = this.values[ii];
            this.values[ii] = tempValue;
        }
    }

    public void truncate(int newSize) {
        if (this.size > newSize) {
            for (int i = newSize; i < this.size; i++) {
                this.keys[i] = null;
                this.values[i] = null;
            }
            this.size = newSize;
        }
    }

    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        K[] keys2 = this.keys;
        V[] values2 = this.values;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        buffer.append((Object) keys2[0]);
        buffer.append('=');
        buffer.append((Object) values2[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append((Object) keys2[i]);
            buffer.append('=');
            buffer.append((Object) values2[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }

    public Entries<K, V> entries() {
        if (this.entries == null) {
            this.entries = new Entries(this);
        } else {
            this.entries.reset();
        }
        return this.entries;
    }

    public Values<V> values() {
        if (this.valuesIter == null) {
            this.valuesIter = new Values(this);
        } else {
            this.valuesIter.reset();
        }
        return this.valuesIter;
    }

    public Keys<K> keys() {
        if (this.keysIter == null) {
            this.keysIter = new Keys(this);
        } else {
            this.keysIter.reset();
        }
        return this.keysIter;
    }

    public static class Entries<K, V> implements Iterable<ObjectMap.Entry<K, V>>, Iterator<ObjectMap.Entry<K, V>> {
        ObjectMap.Entry<K, V> entry = new ObjectMap.Entry<>();
        int index;
        private final ArrayMap<K, V> map;

        public Entries(ArrayMap<K, V> map2) {
            this.map = map2;
        }

        public boolean hasNext() {
            return this.index < this.map.size;
        }

        public Iterator<ObjectMap.Entry<K, V>> iterator() {
            return this;
        }

        public ObjectMap.Entry<K, V> next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            this.entry.key = this.map.keys[this.index];
            ObjectMap.Entry<K, V> entry2 = this.entry;
            V[] vArr = this.map.values;
            int i = this.index;
            this.index = i + 1;
            entry2.value = vArr[i];
            return this.entry;
        }

        public void remove() {
            this.index--;
            this.map.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }
    }

    public static class Values<V> implements Iterable<V>, Iterator<V> {
        int index;
        private final ArrayMap<Object, V> map;

        public Values(ArrayMap<Object, V> map2) {
            this.map = map2;
        }

        public boolean hasNext() {
            return this.index < this.map.size;
        }

        public Iterator<V> iterator() {
            return this;
        }

        public V next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            V[] vArr = this.map.values;
            int i = this.index;
            this.index = i + 1;
            return vArr[i];
        }

        public void remove() {
            this.index--;
            this.map.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }
    }

    public static class Keys<K> implements Iterable<K>, Iterator<K> {
        int index;
        private final ArrayMap<K, Object> map;

        public Keys(ArrayMap<K, Object> map2) {
            this.map = map2;
        }

        public boolean hasNext() {
            return this.index < this.map.size;
        }

        public Iterator<K> iterator() {
            return this;
        }

        public K next() {
            if (this.index >= this.map.size) {
                throw new NoSuchElementException(String.valueOf(this.index));
            }
            K[] kArr = this.map.keys;
            int i = this.index;
            this.index = i + 1;
            return kArr[i];
        }

        public void remove() {
            this.index--;
            this.map.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }
    }
}
