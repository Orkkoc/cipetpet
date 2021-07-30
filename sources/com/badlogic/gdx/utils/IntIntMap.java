package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntIntMap {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries;
    boolean hasZeroValue;
    private int hashShift;
    int[] keyTable;
    private Keys keys;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    int[] valueTable;
    private Values values;
    int zeroValue;

    public IntIntMap() {
        this(32, 0.8f);
    }

    public IntIntMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IntIntMap(int initialCapacity, float loadFactor2) {
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
            this.keyTable = new int[(this.capacity + this.stashCapacity)];
            this.valueTable = new int[this.keyTable.length];
        }
    }

    public void put(int key, int value) {
        if (key == 0) {
            this.zeroValue = value;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                this.size++;
                return;
            }
            return;
        }
        int[] keyTable2 = this.keyTable;
        int index1 = key & this.mask;
        int key1 = keyTable2[index1];
        if (key == key1) {
            this.valueTable[index1] = value;
            return;
        }
        int index2 = hash2(key);
        int key2 = keyTable2[index2];
        if (key == key2) {
            this.valueTable[index2] = value;
            return;
        }
        int index3 = hash3(key);
        int key3 = keyTable2[index3];
        if (key == key3) {
            this.valueTable[index3] = value;
            return;
        }
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (key == keyTable2[i]) {
                this.valueTable[i] = value;
                return;
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
        } else if (key2 == 0) {
            keyTable2[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else if (key3 == 0) {
            keyTable2[index3] = key;
            this.valueTable[index3] = value;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else {
            push(key, value, index1, key1, index2, key2, index3, key3);
        }
    }

    public void putAll(IntIntMap map) {
        Iterator i$ = map.entries().iterator();
        while (i$.hasNext()) {
            Entry entry = i$.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(int key, int value) {
        if (key == 0) {
            this.zeroValue = value;
            this.hasZeroValue = true;
            return;
        }
        int index1 = key & this.mask;
        int key1 = this.keyTable[index1];
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
        int key2 = this.keyTable[index2];
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
        int key3 = this.keyTable[index3];
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

    private void push(int insertKey, int insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
        int evictedKey;
        int evictedValue;
        int[] keyTable2 = this.keyTable;
        int[] valueTable2 = this.valueTable;
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
            index1 = evictedKey & mask2;
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

    private void putStash(int key, int value) {
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

    public int get(int key, int defaultValue) {
        if (key == 0) {
            return this.zeroValue;
        }
        int index = key & this.mask;
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

    private int getStash(int key, int defaultValue) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (key == keyTable2[i]) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public int getAndIncrement(int key, int defaultValue, int increment) {
        int index = key & this.mask;
        if (key != this.keyTable[index]) {
            index = hash2(key);
            if (key != this.keyTable[index]) {
                index = hash3(key);
                if (key != this.keyTable[index]) {
                    return getAndIncrementStash(key, defaultValue, increment);
                }
            }
        }
        int value = this.valueTable[index];
        this.valueTable[index] = value + increment;
        return value;
    }

    private int getAndIncrementStash(int key, int defaultValue, int increment) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (key == keyTable2[i]) {
                int value = this.valueTable[i];
                this.valueTable[i] = value + increment;
                return value;
            }
            i++;
        }
        put(key, defaultValue + increment);
        return defaultValue;
    }

    public int remove(int key, int defaultValue) {
        if (key != 0) {
            int index = key & this.mask;
            if (key == this.keyTable[index]) {
                this.keyTable[index] = 0;
                this.size--;
                return this.valueTable[index];
            }
            int index2 = hash2(key);
            if (key == this.keyTable[index2]) {
                this.keyTable[index2] = 0;
                this.size--;
                return this.valueTable[index2];
            }
            int index3 = hash3(key);
            if (key != this.keyTable[index3]) {
                return removeStash(key, defaultValue);
            }
            this.keyTable[index3] = 0;
            this.size--;
            return this.valueTable[index3];
        } else if (!this.hasZeroValue) {
            return defaultValue;
        } else {
            this.hasZeroValue = false;
            this.size--;
            return this.zeroValue;
        }
    }

    /* access modifiers changed from: package-private */
    public int removeStash(int key, int defaultValue) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (key == keyTable2[i]) {
                int oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
            i++;
        }
        return defaultValue;
    }

    /* access modifiers changed from: package-private */
    public void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            this.keyTable[index] = this.keyTable[lastIndex];
            this.valueTable[index] = this.valueTable[lastIndex];
        }
    }

    public void clear() {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 > 0) {
                keyTable2[i] = 0;
            } else {
                this.size = 0;
                this.stashSize = 0;
                return;
            }
        }
    }

    public boolean containsValue(int value) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return true;
        }
        int[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 <= 0) {
                return false;
            }
            if (valueTable2[i] == value) {
                return true;
            }
        }
    }

    public boolean containsKey(int key) {
        if (key == 0) {
            return this.hasZeroValue;
        }
        if (this.keyTable[key & this.mask] != key) {
            if (this.keyTable[hash2(key)] != key) {
                if (this.keyTable[hash3(key)] != key) {
                    return containsKeyStash(key);
                }
            }
        }
        return true;
    }

    private boolean containsKeyStash(int key) {
        int[] keyTable2 = this.keyTable;
        int i = this.capacity;
        int n = i + this.stashSize;
        while (i < n) {
            if (key == keyTable2[i]) {
                return true;
            }
            i++;
        }
        return false;
    }

    public int findKey(int value, int notFound) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return 0;
        }
        int[] valueTable2 = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 <= 0) {
                return notFound;
            }
            if (valueTable2[i] == value) {
                return this.keyTable[i];
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
        this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log((double) newSize))) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), ((int) Math.sqrt((double) newSize)) / 8);
        int[] oldKeyTable = this.keyTable;
        int[] oldValueTable = this.valueTable;
        this.keyTable = new int[(this.stashCapacity + newSize)];
        this.valueTable = new int[(this.stashCapacity + newSize)];
        if (this.hasZeroValue) {
            i = 1;
        } else {
            i = 0;
        }
        this.size = i;
        this.stashSize = 0;
        for (int i2 = 0; i2 < oldEndIndex; i2++) {
            int key = oldKeyTable[i2];
            if (key != 0) {
                putResize(key, oldValueTable[i2]);
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
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        int[] keyTable2 = this.keyTable;
        int[] valueTable2 = this.valueTable;
        int i = keyTable2.length;
        if (this.hasZeroValue) {
            buffer.append("0=");
            buffer.append(this.zeroValue);
        } else {
            while (true) {
                int i2 = i;
                i = i2 - 1;
                if (i2 > 0) {
                    int key = keyTable2[i];
                    if (key != 0) {
                        buffer.append(key);
                        buffer.append('=');
                        buffer.append(valueTable2[i]);
                        break;
                    }
                }
            }
        }
        while (true) {
            int i3 = i;
            i = i3 - 1;
            if (i3 > 0) {
                int key2 = keyTable2[i];
                if (key2 != 0) {
                    buffer.append(", ");
                    buffer.append(key2);
                    buffer.append('=');
                    buffer.append(valueTable2[i]);
                }
            } else {
                buffer.append('}');
                return buffer.toString();
            }
        }
    }

    public Entries entries() {
        if (this.entries == null) {
            this.entries = new Entries(this);
        } else {
            this.entries.reset();
        }
        return this.entries;
    }

    public Values values() {
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

    public static class Entry<K> {
        public int key;
        public int value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static class MapIterator<K> {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        final IntIntMap map;
        int nextIndex;

        public MapIterator(IntIntMap map2) {
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
            int[] keyTable = this.map.keyTable;
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
                this.map.hasZeroValue = false;
            } else if (this.currentIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            } else if (this.currentIndex >= this.map.capacity) {
                this.map.removeStashIndex(this.currentIndex);
            } else {
                this.map.keyTable[this.currentIndex] = 0;
            }
            this.currentIndex = INDEX_ILLEGAL;
            IntIntMap intIntMap = this.map;
            intIntMap.size--;
        }
    }

    public static class Entries extends MapIterator implements Iterable<Entry>, Iterator<Entry> {
        private Entry entry = new Entry();

        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(IntIntMap map) {
            super(map);
        }

        public Entry next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            int[] keyTable = this.map.keyTable;
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

        public Iterator<Entry> iterator() {
            return this;
        }
    }

    public static class Values extends MapIterator<Object> {
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(IntIntMap map) {
            super(map);
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public int next() {
            int value;
            if (this.nextIndex == -1) {
                value = this.map.zeroValue;
            } else {
                value = this.map.valueTable[this.nextIndex];
            }
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return value;
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.map.size);
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

        public Keys(IntIntMap map) {
            super(map);
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public int next() {
            int key = this.nextIndex == -1 ? 0 : this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
