package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IdentityMap<K, V> {
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries;
    private int hashShift;
    K[] keyTable;
    private Keys keys;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    V[] valueTable;
    private Values values;

    public IdentityMap() {
        this(32, 0.8f);
    }

    public IdentityMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IdentityMap(int initialCapacity, float loadFactor2) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
        } else if (this.capacity > 1073741824) {
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
        } else {
            this.capacity = MathUtils.nextPowerOfTwo(initialCapacity);
            if (loadFactor2 <= 0.0f) {
                throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor2);
            }
            this.loadFactor = loadFactor2;
            this.threshold = (int) (((float) this.capacity) * loadFactor2);
            this.mask = this.capacity - 1;
            this.hashShift = 31 - Integer.numberOfTrailingZeros(this.capacity);
            this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) this.capacity))) * 2);
            this.pushIterations = Math.max(Math.min(this.capacity, 8), ((int) Math.sqrt((double) this.capacity)) / 8);
            this.keyTable = (Object[]) new Object[(this.capacity + this.stashCapacity)];
            this.valueTable = (Object[]) new Object[this.keyTable.length];
        }
    }

    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        K[] keyTable2 = this.keyTable;
        int hashCode = System.identityHashCode(key);
        int index1 = hashCode & this.mask;
        K key1 = keyTable2[index1];
        if (key1 == key) {
            V oldValue = this.valueTable[index1];
            this.valueTable[index1] = value;
            return oldValue;
        }
        int index2 = hash2(hashCode);
        K key2 = keyTable2[index2];
        if (key2 == key) {
            V oldValue2 = this.valueTable[index2];
            this.valueTable[index2] = value;
            return oldValue2;
        }
        int index3 = hash3(hashCode);
        K key3 = keyTable2[index3];
        if (key3 == key) {
            V oldValue3 = this.valueTable[index3];
            this.valueTable[index3] = value;
            return oldValue3;
        }
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (keyTable2[i] == key) {
                V oldValue4 = this.valueTable[i];
                this.valueTable[i] = value;
                return oldValue4;
            }
            i++;
        }
        if (key1 == null) {
            keyTable2[index1] = key;
            this.valueTable[index1] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key2 == null) {
            keyTable2[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key3 == null) {
            keyTable2[index3] = key;
            this.valueTable[index3] = value;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else {
            push(key, value, index1, key1, index2, key2, index3, key3);
            return null;
        }
    }

    private void putResize(K key, V value) {
        int hashCode = System.identityHashCode(key);
        int index1 = hashCode & this.mask;
        K key1 = this.keyTable[index1];
        if (key1 == null) {
            this.keyTable[index1] = key;
            this.valueTable[index1] = value;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(hashCode);
        K key2 = this.keyTable[index2];
        if (key2 == null) {
            this.keyTable[index2] = key;
            this.valueTable[index2] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(hashCode);
        K key3 = this.keyTable[index3];
        if (key3 == null) {
            this.keyTable[index3] = key;
            this.valueTable[index3] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        push(key, value, index1, key1, index2, key2, index3, key3);
    }

    private void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
        K evictedKey;
        V evictedValue;
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int mask2 = this.mask;
        int i = 0;
        int pushIterations2 = this.pushIterations;
        while (true) {
            switch (MathUtils.random(2)) {
                case 0:
                    evictedKey = key1;
                    evictedValue = valueTable2[index1];
                    keyTable2[index1] = insertKey;
                    valueTable2[index1] = insertValue;
                    break;
                case 1:
                    evictedKey = key2;
                    evictedValue = valueTable2[index2];
                    keyTable2[index2] = insertKey;
                    valueTable2[index2] = insertValue;
                    break;
                default:
                    evictedKey = key3;
                    evictedValue = valueTable2[index3];
                    keyTable2[index3] = insertKey;
                    valueTable2[index3] = insertValue;
                    break;
            }
            int hashCode = System.identityHashCode(evictedKey);
            index1 = hashCode & mask2;
            key1 = keyTable2[index1];
            if (key1 == null) {
                keyTable2[index1] = evictedKey;
                valueTable2[index1] = evictedValue;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index2 = hash2(hashCode);
            key2 = keyTable2[index2];
            if (key2 == null) {
                keyTable2[index2] = evictedKey;
                valueTable2[index2] = evictedValue;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index3 = hash3(hashCode);
            key3 = keyTable2[index3];
            if (key3 == null) {
                keyTable2[index3] = evictedKey;
                valueTable2[index3] = evictedValue;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            i++;
            if (i == pushIterations2) {
                putStash(evictedKey, evictedValue);
                return;
            } else {
                insertKey = evictedKey;
                insertValue = evictedValue;
            }
        }
    }

    private void putStash(K key, V value) {
        if (this.stashSize == this.stashCapacity) {
            resize(this.capacity << 1);
            put(key, value);
            return;
        }
        int index = this.capacity + this.stashSize;
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        this.stashSize++;
        this.size++;
    }

    public V get(K key) {
        int hashCode = System.identityHashCode(key);
        int index = hashCode & this.mask;
        if (key != this.keyTable[index]) {
            index = hash2(hashCode);
            if (key != this.keyTable[index]) {
                index = hash3(hashCode);
                if (key != this.keyTable[index]) {
                    return getStash(key, (Object) null);
                }
            }
        }
        return this.valueTable[index];
    }

    public V get(K key, V defaultValue) {
        int hashCode = System.identityHashCode(key);
        int index = hashCode & this.mask;
        if (key != this.keyTable[index]) {
            index = hash2(hashCode);
            if (key != this.keyTable[index]) {
                index = hash3(hashCode);
                if (key != this.keyTable[index]) {
                    return getStash(key, defaultValue);
                }
            }
        }
        return this.valueTable[index];
    }

    private V getStash(K key, V defaultValue) {
        K[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (keyTable2[i] == key) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public V remove(K key) {
        int hashCode = System.identityHashCode(key);
        int index = hashCode & this.mask;
        if (this.keyTable[index] == key) {
            this.keyTable[index] = null;
            V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            this.size--;
            return oldValue;
        }
        int index2 = hash2(hashCode);
        if (this.keyTable[index2] == key) {
            this.keyTable[index2] = null;
            V oldValue2 = this.valueTable[index2];
            this.valueTable[index2] = null;
            this.size--;
            return oldValue2;
        }
        int index3 = hash3(hashCode);
        if (this.keyTable[index3] != key) {
            return removeStash(key);
        }
        this.keyTable[index3] = null;
        V oldValue3 = this.valueTable[index3];
        this.valueTable[index3] = null;
        this.size--;
        return oldValue3;
    }

    /* access modifiers changed from: package-private */
    public V removeStash(K key) {
        K[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (keyTable2[i] == key) {
                V oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
            i++;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            this.keyTable[index] = this.keyTable[lastIndex];
            this.valueTable[index] = this.valueTable[lastIndex];
            this.valueTable[lastIndex] = null;
            return;
        }
        this.valueTable[index] = null;
    }

    public void clear() {
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 > 0) {
                keyTable2[i] = null;
                valueTable2[i] = null;
            } else {
                this.size = 0;
                this.stashSize = 0;
                return;
            }
        }
    }

    public boolean containsValue(Object value, boolean identity) {
        V[] valueTable2 = this.valueTable;
        if (value != null) {
            if (!identity) {
                int i = this.capacity + this.stashSize;
                while (true) {
                    int i2 = i;
                    i = i2 - 1;
                    if (i2 <= 0) {
                        break;
                    } else if (value.equals(valueTable2[i])) {
                        return true;
                    }
                }
            } else {
                int i3 = this.capacity + this.stashSize;
                while (true) {
                    int i4 = i3;
                    i3 = i4 - 1;
                    if (i4 <= 0) {
                        break;
                    } else if (valueTable2[i3] == value) {
                        return true;
                    }
                }
            }
        } else {
            K[] keyTable2 = this.keyTable;
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5;
                i5 = i6 - 1;
                if (i6 <= 0) {
                    break;
                } else if (keyTable2[i5] != null && valueTable2[i5] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsKey(K key) {
        int hashCode = System.identityHashCode(key);
        if (key != this.keyTable[hashCode & this.mask]) {
            if (key != this.keyTable[hash2(hashCode)]) {
                if (key != this.keyTable[hash3(hashCode)]) {
                    return containsKeyStash(key);
                }
            }
        }
        return true;
    }

    private boolean containsKeyStash(K key) {
        K[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (keyTable2[i] == key) {
                return true;
            }
            i++;
        }
        return false;
    }

    public K findKey(Object value, boolean identity) {
        V[] valueTable2 = this.valueTable;
        if (value != null) {
            if (!identity) {
                int i = this.capacity + this.stashSize;
                while (true) {
                    int i2 = i;
                    i = i2 - 1;
                    if (i2 <= 0) {
                        break;
                    } else if (value.equals(valueTable2[i])) {
                        return this.keyTable[i];
                    }
                }
            } else {
                int i3 = this.capacity + this.stashSize;
                while (true) {
                    int i4 = i3;
                    i3 = i4 - 1;
                    if (i4 <= 0) {
                        break;
                    } else if (valueTable2[i3] == value) {
                        return this.keyTable[i3];
                    }
                }
            }
        } else {
            K[] keyTable2 = this.keyTable;
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5;
                i5 = i6 - 1;
                if (i6 <= 0) {
                    break;
                } else if (keyTable2[i5] != null && valueTable2[i5] == null) {
                    return keyTable2[i5];
                }
            }
        }
        return null;
    }

    public void ensureCapacity(int additionalCapacity) {
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.threshold) {
            resize(MathUtils.nextPowerOfTwo((int) (((float) sizeNeeded) / this.loadFactor)));
        }
    }

    private void resize(int newSize) {
        int oldEndIndex = this.capacity + this.stashSize;
        this.capacity = newSize;
        this.threshold = (int) (((float) newSize) * this.loadFactor);
        this.mask = newSize - 1;
        this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) newSize))) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), ((int) Math.sqrt((double) newSize)) / 8);
        K[] oldKeyTable = this.keyTable;
        V[] oldValueTable = this.valueTable;
        this.keyTable = (Object[]) new Object[(this.stashCapacity + newSize)];
        this.valueTable = (Object[]) new Object[(this.stashCapacity + newSize)];
        this.size = 0;
        this.stashSize = 0;
        for (int i = 0; i < oldEndIndex; i++) {
            K key = oldKeyTable[i];
            if (key != null) {
                putResize(key, oldValueTable[i]);
            }
        }
    }

    private int hash2(int h) {
        int h2 = h * PRIME2;
        return ((h2 >>> this.hashShift) ^ h2) & this.mask;
    }

    private int hash3(int h) {
        int h2 = h * PRIME3;
        return ((h2 >>> this.hashShift) ^ h2) & this.mask;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        K[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int i = keyTable2.length;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 > 0) {
                K key = keyTable2[i];
                if (key != null) {
                    buffer.append((Object) key);
                    buffer.append('=');
                    buffer.append((Object) valueTable2[i]);
                    break;
                }
            } else {
                break;
            }
        }
        while (true) {
            int i3 = i;
            i = i3 - 1;
            if (i3 > 0) {
                K key2 = keyTable2[i];
                if (key2 != null) {
                    buffer.append(", ");
                    buffer.append((Object) key2);
                    buffer.append('=');
                    buffer.append((Object) valueTable2[i]);
                }
            } else {
                buffer.append(']');
                return buffer.toString();
            }
        }
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
        if (this.values == null) {
            this.values = new Values(this);
        } else {
            this.values.reset();
        }
        return this.values;
    }

    public Keys<K> keys() {
        if (this.keys == null) {
            this.keys = new Keys(this);
        } else {
            this.keys.reset();
        }
        return this.keys;
    }

    public static class Entry<K, V> {
        public K key;
        public V value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static class MapIterator<K, V> {
        int currentIndex;
        public boolean hasNext;
        final IdentityMap<K, V> map;
        int nextIndex;

        public MapIterator(IdentityMap<K, V> map2) {
            this.map = map2;
            reset();
        }

        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            findNextIndex();
        }

        /* access modifiers changed from: package-private */
        public void findNextIndex() {
            this.hasNext = false;
            K[] keyTable = this.map.keyTable;
            int n = this.map.capacity + this.map.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == null);
            this.hasNext = true;
        }

        public void remove() {
            if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            if (this.currentIndex >= this.map.capacity) {
                this.map.removeStashIndex(this.currentIndex);
            } else {
                this.map.keyTable[this.currentIndex] = null;
                this.map.valueTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            IdentityMap<K, V> identityMap = this.map;
            identityMap.size--;
        }
    }

    public static class Entries<K, V> extends MapIterator<K, V> implements Iterable<Entry<K, V>>, Iterator<Entry<K, V>> {
        private Entry<K, V> entry = new Entry<>();

        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(IdentityMap<K, V> map) {
            super(map);
        }

        public Entry<K, V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            K[] keyTable = this.map.keyTable;
            this.entry.key = keyTable[this.nextIndex];
            this.entry.value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return this.entry;
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public Iterator<Entry<K, V>> iterator() {
            return this;
        }
    }

    public static class Values<V> extends MapIterator<Object, V> implements Iterable<V>, Iterator<V> {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(IdentityMap<?, V> map) {
            super(map);
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public V next() {
            V value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return value;
        }

        public Iterator<V> iterator() {
            return this;
        }

        public Array<V> toArray() {
            Array array = new Array(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        public void toArray(Array<V> array) {
            while (this.hasNext) {
                array.add(next());
            }
        }
    }

    public static class Keys<K> extends MapIterator<K, Object> implements Iterable<K>, Iterator<K> {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(IdentityMap<K, ?> map) {
            super(map);
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public K next() {
            K key = this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        public Iterator<K> iterator() {
            return this;
        }

        public Array<K> toArray() {
            Array array = new Array(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
