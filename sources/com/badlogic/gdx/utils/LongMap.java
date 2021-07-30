package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongMap<V> {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries;
    boolean hasZeroValue;
    private int hashShift;
    long[] keyTable;
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
    V zeroValue;

    public LongMap() {
        this(32, 0.8f);
    }

    public LongMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public LongMap(int initialCapacity, float loadFactor2) {
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
            this.hashShift = 63 - Long.numberOfTrailingZeros((long) this.capacity);
            this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) this.capacity))) * 2);
            this.pushIterations = Math.max(Math.min(this.capacity, 8), ((int) Math.sqrt((double) this.capacity)) / 8);
            this.keyTable = new long[(this.capacity + this.stashCapacity)];
            this.valueTable = (Object[]) new Object[this.keyTable.length];
        }
    }

    public V put(long key, V value) {
        if (key == 0) {
            V oldValue = this.zeroValue;
            this.zeroValue = value;
            if (this.hasZeroValue) {
                return oldValue;
            }
            this.hasZeroValue = true;
            this.size++;
            return oldValue;
        }
        long[] keyTable2 = this.keyTable;
        int index1 = (int) (((long) this.mask) & key);
        long key1 = keyTable2[index1];
        if (key1 == key) {
            V oldValue2 = this.valueTable[index1];
            this.valueTable[index1] = value;
            return oldValue2;
        }
        int index2 = hash2(key);
        long key2 = keyTable2[index2];
        if (key2 == key) {
            V oldValue3 = this.valueTable[index2];
            this.valueTable[index2] = value;
            return oldValue3;
        }
        int index3 = hash3(key);
        long key3 = keyTable2[index3];
        if (key3 == key) {
            V oldValue4 = this.valueTable[index3];
            this.valueTable[index3] = value;
            return oldValue4;
        }
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (keyTable2[i] == key) {
                V oldValue5 = this.valueTable[i];
                this.valueTable[i] = value;
                return oldValue5;
            }
            i++;
        }
        if (key1 == 0) {
            keyTable2[index1] = key;
            this.valueTable[index1] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key2 == 0) {
            keyTable2[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key3 == 0) {
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

    public void putAll(LongMap<V> map) {
        Iterator i$ = map.entries().iterator();
        while (i$.hasNext()) {
            Entry<V> entry = i$.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(long key, V value) {
        if (key == 0) {
            this.zeroValue = value;
            this.hasZeroValue = true;
            return;
        }
        int index1 = (int) (((long) this.mask) & key);
        long key1 = this.keyTable[index1];
        if (key1 == 0) {
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
        int index2 = hash2(key);
        long key2 = this.keyTable[index2];
        if (key2 == 0) {
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
        int index3 = hash3(key);
        long key3 = this.keyTable[index3];
        if (key3 == 0) {
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

    private void push(long insertKey, V insertValue, int index1, long key1, int index2, long key2, int index3, long key3) {
        long evictedKey;
        V evictedValue;
        long[] keyTable2 = this.keyTable;
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
            index1 = (int) (((long) mask2) & evictedKey);
            key1 = keyTable2[index1];
            if (key1 == 0) {
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
            index2 = hash2(evictedKey);
            key2 = keyTable2[index2];
            if (key2 == 0) {
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
            index3 = hash3(evictedKey);
            key3 = keyTable2[index3];
            if (key3 == 0) {
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

    private void putStash(long key, V value) {
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

    public V get(long key) {
        if (key == 0) {
            return this.zeroValue;
        }
        int index = (int) (((long) this.mask) & key);
        if (this.keyTable[index] != key) {
            index = hash2(key);
            if (this.keyTable[index] != key) {
                index = hash3(key);
                if (this.keyTable[index] != key) {
                    return getStash(key, (Object) null);
                }
            }
        }
        return this.valueTable[index];
    }

    public V get(long key, V defaultValue) {
        if (key == 0) {
            return this.zeroValue;
        }
        int index = (int) (((long) this.mask) & key);
        if (this.keyTable[index] != key) {
            index = hash2(key);
            if (this.keyTable[index] != key) {
                index = hash3(key);
                if (this.keyTable[index] != key) {
                    return getStash(key, defaultValue);
                }
            }
        }
        return this.valueTable[index];
    }

    private V getStash(long key, V defaultValue) {
        long[] keyTable2 = this.keyTable;
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

    public V remove(long key) {
        if (key != 0) {
            int index = (int) (((long) this.mask) & key);
            if (this.keyTable[index] == key) {
                this.keyTable[index] = 0;
                V v = this.valueTable[index];
                this.valueTable[index] = null;
                this.size--;
                return v;
            }
            int index2 = hash2(key);
            if (this.keyTable[index2] == key) {
                this.keyTable[index2] = 0;
                V v2 = this.valueTable[index2];
                this.valueTable[index2] = null;
                this.size--;
                return v2;
            }
            int index3 = hash3(key);
            if (this.keyTable[index3] != key) {
                return removeStash(key);
            }
            this.keyTable[index3] = 0;
            V v3 = this.valueTable[index3];
            this.valueTable[index3] = null;
            this.size--;
            return v3;
        } else if (!this.hasZeroValue) {
            return null;
        } else {
            V v4 = this.zeroValue;
            this.zeroValue = null;
            this.hasZeroValue = false;
            this.size--;
            return v4;
        }
    }

    /* access modifiers changed from: package-private */
    public V removeStash(long key) {
        long[] keyTable2 = this.keyTable;
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
        long[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 > 0) {
                keyTable2[i] = 0;
                valueTable2[i] = null;
            } else {
                this.size = 0;
                this.stashSize = 0;
                this.zeroValue = null;
                this.hasZeroValue = false;
                return;
            }
        }
    }

    public boolean containsValue(Object value, boolean identity) {
        V[] valueTable2 = this.valueTable;
        if (value == null) {
            if (!this.hasZeroValue || this.zeroValue != null) {
                long[] keyTable2 = this.keyTable;
                int i = this.capacity + this.stashSize;
                while (true) {
                    int i2 = i;
                    i = i2 - 1;
                    if (i2 <= 0) {
                        break;
                    } else if (keyTable2[i] != 0 && valueTable2[i] == null) {
                        return true;
                    }
                }
            } else {
                return true;
            }
        } else if (identity) {
            if (value != this.zeroValue) {
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
            } else {
                return true;
            }
        } else if (!this.hasZeroValue || !value.equals(this.zeroValue)) {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5;
                i5 = i6 - 1;
                if (i6 <= 0) {
                    break;
                } else if (value.equals(valueTable2[i5])) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean containsKey(long key) {
        if (key == 0) {
            return this.hasZeroValue;
        }
        if (this.keyTable[(int) (((long) this.mask) & key)] != key) {
            if (this.keyTable[hash2(key)] != key) {
                if (this.keyTable[hash3(key)] != key) {
                    return containsKeyStash(key);
                }
            }
        }
        return true;
    }

    private boolean containsKeyStash(long key) {
        long[] keyTable2 = this.keyTable;
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

    public long findKey(Object value, boolean identity, long notFound) {
        V[] valueTable2 = this.valueTable;
        if (value == null) {
            if (this.hasZeroValue && this.zeroValue == null) {
                return 0;
            }
            long[] keyTable2 = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i;
                i = i2 - 1;
                if (i2 <= 0) {
                    return notFound;
                }
                if (keyTable2[i] != 0 && valueTable2[i] == null) {
                    return keyTable2[i];
                }
            }
        } else if (identity) {
            if (value == this.zeroValue) {
                return 0;
            }
            int i3 = this.capacity + this.stashSize;
            while (true) {
                int i4 = i3;
                i3 = i4 - 1;
                if (i4 <= 0) {
                    return notFound;
                }
                if (valueTable2[i3] == value) {
                    return this.keyTable[i3];
                }
            }
        } else if (this.hasZeroValue && value.equals(this.zeroValue)) {
            return 0;
        } else {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5;
                i5 = i6 - 1;
                if (i6 <= 0) {
                    return notFound;
                }
                if (value.equals(valueTable2[i5])) {
                    return this.keyTable[i5];
                }
            }
        }
    }

    public void ensureCapacity(int additionalCapacity) {
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.threshold) {
            resize(MathUtils.nextPowerOfTwo((int) (((float) sizeNeeded) / this.loadFactor)));
        }
    }

    private void resize(int newSize) {
        int i;
        int oldEndIndex = this.capacity + this.stashSize;
        this.capacity = newSize;
        this.threshold = (int) (((float) newSize) * this.loadFactor);
        this.mask = newSize - 1;
        this.hashShift = 63 - Long.numberOfTrailingZeros((long) newSize);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) newSize))) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), ((int) Math.sqrt((double) newSize)) / 8);
        long[] oldKeyTable = this.keyTable;
        V[] oldValueTable = this.valueTable;
        this.keyTable = new long[(this.stashCapacity + newSize)];
        this.valueTable = (Object[]) new Object[(this.stashCapacity + newSize)];
        if (this.hasZeroValue) {
            i = 1;
        } else {
            i = 0;
        }
        this.size = i;
        this.stashSize = 0;
        for (int i2 = 0; i2 < oldEndIndex; i2++) {
            long key = oldKeyTable[i2];
            if (key != 0) {
                putResize(key, oldValueTable[i2]);
            }
        }
    }

    private int hash2(long h) {
        long h2 = h * -1262997959;
        return (int) (((h2 >>> this.hashShift) ^ h2) & ((long) this.mask));
    }

    private int hash3(long h) {
        long h2 = h * -825114047;
        return (int) (((h2 >>> this.hashShift) ^ h2) & ((long) this.mask));
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        long[] keyTable2 = this.keyTable;
        V[] valueTable2 = this.valueTable;
        int i = keyTable2.length;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 <= 0) {
                break;
            }
            long key = keyTable2[i];
            if (key != 0) {
                buffer.append(key);
                buffer.append('=');
                buffer.append((Object) valueTable2[i]);
                break;
            }
        }
        while (true) {
            int i3 = i;
            i = i3 - 1;
            if (i3 > 0) {
                long key2 = keyTable2[i];
                if (key2 != 0) {
                    buffer.append(", ");
                    buffer.append(key2);
                    buffer.append('=');
                    buffer.append((Object) valueTable2[i]);
                }
            } else {
                buffer.append(']');
                return buffer.toString();
            }
        }
    }

    public Entries<V> entries() {
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

    public Keys keys() {
        if (this.keys == null) {
            this.keys = new Keys(this);
        } else {
            this.keys.reset();
        }
        return this.keys;
    }

    public static class Entry<V> {
        public long key;
        public V value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static class MapIterator<V> {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        final LongMap<V> map;
        int nextIndex;

        public MapIterator(LongMap<V> map2) {
            this.map = map2;
            reset();
        }

        public void reset() {
            this.currentIndex = INDEX_ILLEGAL;
            this.nextIndex = -1;
            if (this.map.hasZeroValue) {
                this.hasNext = true;
            } else {
                findNextIndex();
            }
        }

        /* access modifiers changed from: package-private */
        public void findNextIndex() {
            this.hasNext = false;
            long[] keyTable = this.map.keyTable;
            int n = this.map.capacity + this.map.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == 0);
            this.hasNext = true;
        }

        public void remove() {
            if (this.currentIndex == -1 && this.map.hasZeroValue) {
                this.map.zeroValue = null;
                this.map.hasZeroValue = false;
            } else if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            } else if (this.currentIndex >= this.map.capacity) {
                this.map.removeStashIndex(this.currentIndex);
            } else {
                this.map.keyTable[this.currentIndex] = 0;
                this.map.valueTable[this.currentIndex] = null;
            }
            this.currentIndex = INDEX_ILLEGAL;
            LongMap<V> longMap = this.map;
            longMap.size--;
        }
    }

    public static class Entries<V> extends MapIterator<V> implements Iterable<Entry<V>>, Iterator<Entry<V>> {
        private Entry<V> entry = new Entry<>();

        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(LongMap map) {
            super(map);
        }

        public Entry<V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            long[] keyTable = this.map.keyTable;
            if (this.nextIndex == -1) {
                this.entry.key = 0;
                this.entry.value = this.map.zeroValue;
            } else {
                this.entry.key = keyTable[this.nextIndex];
                this.entry.value = this.map.valueTable[this.nextIndex];
            }
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return this.entry;
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public Iterator<Entry<V>> iterator() {
            return this;
        }
    }

    public static class Values<V> extends MapIterator<V> implements Iterable<V>, Iterator<V> {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(LongMap<V> map) {
            super(map);
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public V next() {
            V value;
            if (this.nextIndex == -1) {
                value = this.map.zeroValue;
            } else {
                value = this.map.valueTable[this.nextIndex];
            }
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
    }

    public static class Keys extends MapIterator {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(LongMap map) {
            super(map);
        }

        public long next() {
            long key = this.nextIndex == -1 ? 0 : this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        public LongArray toArray() {
            LongArray array = new LongArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
