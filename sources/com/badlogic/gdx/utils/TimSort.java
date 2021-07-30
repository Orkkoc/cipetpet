package com.badlogic.gdx.utils;

import java.util.Arrays;
import java.util.Comparator;

class TimSort<T> {
    private static final boolean DEBUG = false;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private static final int MIN_GALLOP = 7;
    private static final int MIN_MERGE = 32;

    /* renamed from: a */
    private T[] f187a;

    /* renamed from: c */
    private Comparator<? super T> f188c;
    private int minGallop;
    private final int[] runBase;
    private final int[] runLen;
    private int stackSize;
    private T[] tmp;
    private int tmpCount;

    TimSort() {
        this.minGallop = 7;
        this.stackSize = 0;
        this.tmp = (Object[]) new Object[256];
        this.runBase = new int[40];
        this.runLen = new int[40];
    }

    public void doSort(T[] a, Comparator<T> c, int lo, int hi) {
        int force;
        this.stackSize = 0;
        rangeCheck(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a, lo, hi, lo + countRunAndMakeAscending(a, lo, hi, c), c);
                return;
            }
            this.f187a = a;
            this.f188c = c;
            this.tmpCount = 0;
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a, lo, hi, c);
                if (runLen2 < minRun) {
                    if (nRemaining <= minRun) {
                        force = nRemaining;
                    } else {
                        force = minRun;
                    }
                    binarySort(a, lo, lo + force, lo + runLen2, c);
                    runLen2 = force;
                }
                pushRun(lo, runLen2);
                mergeCollapse();
                lo += runLen2;
                nRemaining -= runLen2;
            } while (nRemaining != 0);
            mergeForceCollapse();
            this.f187a = null;
            this.f188c = null;
            T[] tmp2 = this.tmp;
            int n = this.tmpCount;
            for (int i = 0; i < n; i++) {
                tmp2[i] = null;
            }
        }
    }

    private TimSort(T[] a, Comparator<? super T> c) {
        this.minGallop = 7;
        this.stackSize = 0;
        this.f187a = a;
        this.f188c = c;
        int len = a.length;
        this.tmp = (Object[]) new Object[(len < 512 ? len >>> 1 : 256)];
        int stackLen = len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static <T> void sort(T[] a, Comparator<? super T> c) {
        sort(a, 0, a.length, c);
    }

    static <T> void sort(T[] a, int lo, int hi, Comparator<? super T> c) {
        if (c == null) {
            Arrays.sort(a, lo, hi);
            return;
        }
        rangeCheck(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }
        if (nRemaining < 32) {
            binarySort(a, lo, hi, lo + countRunAndMakeAscending(a, lo, hi, c), c);
            return;
        }
        TimSort<T> ts = new TimSort<>(a, c);
        int minRun = minRunLength(nRemaining);
        do {
            int runLen2 = countRunAndMakeAscending(a, lo, hi, c);
            if (runLen2 < minRun) {
                int force = nRemaining <= minRun ? nRemaining : minRun;
                binarySort(a, lo, lo + force, lo + runLen2, c);
                runLen2 = force;
            }
            ts.pushRun(lo, runLen2);
            ts.mergeCollapse();
            lo += runLen2;
            nRemaining -= runLen2;
        } while (nRemaining != 0);
        ts.mergeForceCollapse();
    }

    private static <T> void binarySort(T[] a, int lo, int hi, int start, Comparator<? super T> c) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            T pivot = a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (c.compare(pivot, a[mid]) < 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            int n = start - left;
            switch (n) {
                case 1:
                    break;
                case 2:
                    a[left + 2] = a[left + 1];
                    break;
                default:
                    System.arraycopy(a, left, a, left + 1, n);
                    continue;
            }
            a[left + 1] = a[left];
            a[left] = pivot;
            start++;
        }
    }

    private static <T> int countRunAndMakeAscending(T[] a, int lo, int hi, Comparator<? super T> c) {
        int runHi;
        int runHi2 = lo + 1;
        if (runHi2 == hi) {
            return 1;
        }
        int runHi3 = runHi2 + 1;
        if (c.compare(a[runHi2], a[lo]) < 0) {
            runHi = runHi3;
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) < 0) {
                runHi++;
            }
            reverseRange(a, lo, runHi);
        } else {
            int runHi4 = runHi3;
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) >= 0) {
                runHi4 = runHi + 1;
            }
        }
        return runHi - lo;
    }

    private static void reverseRange(Object[] a, int lo, int hi) {
        int hi2 = hi - 1;
        for (int lo2 = lo; lo2 < hi2; lo2++) {
            Object t = a[lo2];
            a[lo2] = a[hi2];
            a[hi2] = t;
            hi2--;
        }
    }

    private static int minRunLength(int n) {
        int r = 0;
        while (n >= 32) {
            r |= n & 1;
            n >>= 1;
        }
        return n + r;
    }

    private void pushRun(int runBase2, int runLen2) {
        this.runBase[this.stackSize] = runBase2;
        this.runLen[this.stackSize] = runLen2;
        this.stackSize++;
    }

    private void mergeCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] <= this.runLen[n] + this.runLen[n + 1]) {
                if (this.runLen[n - 1] < this.runLen[n + 1]) {
                    n--;
                }
                mergeAt(n);
            } else if (this.runLen[n] <= this.runLen[n + 1]) {
                mergeAt(n);
            } else {
                return;
            }
        }
    }

    private void mergeForceCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] < this.runLen[n + 1]) {
                n--;
            }
            mergeAt(n);
        }
    }

    private void mergeAt(int i) {
        int len2;
        int base1 = this.runBase[i];
        int len1 = this.runLen[i];
        int base2 = this.runBase[i + 1];
        int len22 = this.runLen[i + 1];
        this.runLen[i] = len1 + len22;
        if (i == this.stackSize - 3) {
            this.runBase[i + 1] = this.runBase[i + 2];
            this.runLen[i + 1] = this.runLen[i + 2];
        }
        this.stackSize--;
        int k = gallopRight(this.f187a[base2], this.f187a, base1, len1, 0, this.f188c);
        int base12 = base1 + k;
        int len12 = len1 - k;
        if (len12 != 0 && (len2 = gallopLeft(this.f187a[(base12 + len12) - 1], this.f187a, base2, len22, len22 - 1, this.f188c)) != 0) {
            if (len12 <= len2) {
                mergeLo(base12, len12, base2, len2);
            } else {
                mergeHi(base12, len12, base2, len2);
            }
        }
    }

    private static <T> int gallopLeft(T key, T[] a, int base, int len, int hint, Comparator<? super T> c) {
        int lastOfs;
        int ofs;
        int lastOfs2 = 0;
        int ofs2 = 1;
        if (c.compare(key, a[base + hint]) > 0) {
            int maxOfs = len - hint;
            while (ofs2 < maxOfs && c.compare(key, a[base + hint + ofs2]) > 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs;
                }
            }
            if (ofs2 > maxOfs) {
                ofs2 = maxOfs;
            }
            lastOfs = lastOfs2 + hint;
            ofs = ofs2 + hint;
        } else {
            int maxOfs2 = hint + 1;
            while (ofs2 < maxOfs2 && c.compare(key, a[(base + hint) - ofs2]) <= 0) {
                lastOfs2 = ofs2;
                int ofs3 = (ofs2 << 1) + 1;
                if (ofs3 <= 0) {
                    ofs3 = maxOfs2;
                }
            }
            if (ofs2 > maxOfs2) {
                ofs2 = maxOfs2;
            }
            int tmp2 = lastOfs2;
            lastOfs = hint - ofs2;
            ofs = hint - tmp2;
        }
        int lastOfs3 = lastOfs + 1;
        while (lastOfs3 < ofs) {
            int m = lastOfs3 + ((ofs - lastOfs3) >>> 1);
            if (c.compare(key, a[base + m]) > 0) {
                lastOfs3 = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static <T> int gallopRight(T key, T[] a, int base, int len, int hint, Comparator<? super T> c) {
        int lastOfs;
        int ofs;
        int ofs2 = 1;
        int lastOfs2 = 0;
        if (c.compare(key, a[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while (ofs2 < maxOfs && c.compare(key, a[(base + hint) - ofs2]) < 0) {
                lastOfs2 = ofs2;
                ofs2 = (ofs2 << 1) + 1;
                if (ofs2 <= 0) {
                    ofs2 = maxOfs;
                }
            }
            if (ofs2 > maxOfs) {
                ofs2 = maxOfs;
            }
            int tmp2 = lastOfs2;
            lastOfs = hint - ofs2;
            ofs = hint - tmp2;
        } else {
            int maxOfs2 = len - hint;
            while (ofs2 < maxOfs2 && c.compare(key, a[base + hint + ofs2]) >= 0) {
                lastOfs2 = ofs2;
                int ofs3 = (ofs2 << 1) + 1;
                if (ofs3 <= 0) {
                    ofs3 = maxOfs2;
                }
            }
            if (ofs2 > maxOfs2) {
                ofs2 = maxOfs2;
            }
            lastOfs = lastOfs2 + hint;
            ofs = ofs2 + hint;
        }
        int lastOfs3 = lastOfs + 1;
        while (lastOfs3 < ofs) {
            int m = lastOfs3 + ((ofs - lastOfs3) >>> 1);
            if (c.compare(key, a[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs3 = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00ba, code lost:
        r18 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00bc, code lost:
        r15 = gallopRight(r10[r18], r4, r5, r24, 0, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00c5, code lost:
        if (r15 == 0) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00c7, code lost:
        java.lang.System.arraycopy(r4, r5, r10, r19, r15);
        r19 = r19 + r15;
        r5 = r5 + r15;
        r24 = r24 - r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00d4, code lost:
        if (r24 > 1) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00d6, code lost:
        r11 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00d9, code lost:
        r20 = r19 + 1;
        r11 = r18 + 1;
        r10[r19] = r10[r18];
        r26 = r26 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00e3, code lost:
        if (r26 != 0) goto L_0x00e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00e5, code lost:
        r19 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00e8, code lost:
        r16 = gallopLeft(r4[r5], r10, r11, r26, 0, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00f2, code lost:
        if (r16 == 0) goto L_0x0154;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00f4, code lost:
        java.lang.System.arraycopy(r10, r11, r10, r20, r16);
        r19 = r20 + r16;
        r11 = r11 + r16;
        r26 = r26 - r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0101, code lost:
        if (r26 == 0) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0103, code lost:
        r20 = r19 + 1;
        r17 = r5 + 1;
        r10[r19] = r4[r5];
        r24 = r24 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0110, code lost:
        if (r24 != 1) goto L_0x0118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0112, code lost:
        r19 = r20;
        r5 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0118, code lost:
        r21 = r21 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x011b, code lost:
        if (r15 < 7) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x011d, code lost:
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0122, code lost:
        if (r16 < 7) goto L_0x0137;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0124, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0126, code lost:
        if ((r3 | r6) != false) goto L_0x014c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0128, code lost:
        if (r21 >= 0) goto L_0x012c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x012a, code lost:
        r21 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0134, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0137, code lost:
        r3 = DEBUG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x014c, code lost:
        r19 = r20;
        r18 = r11;
        r5 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0154, code lost:
        r19 = r20;
     */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00ba A[EDGE_INSN: B:69:0x00ba->B:25:0x00ba ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeLo(int r23, int r24, int r25, int r26) {
        /*
            r22 = this;
            r0 = r22
            T[] r10 = r0.f187a
            r0 = r22
            r1 = r24
            java.lang.Object[] r4 = r0.ensureCapacity(r1)
            r3 = 0
            r0 = r23
            r1 = r24
            java.lang.System.arraycopy(r10, r0, r4, r3, r1)
            r5 = 0
            r11 = r25
            r19 = r23
            int r20 = r19 + 1
            int r18 = r11 + 1
            r3 = r10[r11]
            r10[r19] = r3
            int r26 = r26 + -1
            if (r26 != 0) goto L_0x0031
            r0 = r20
            r1 = r24
            java.lang.System.arraycopy(r4, r5, r10, r0, r1)
            r19 = r20
            r11 = r18
        L_0x0030:
            return
        L_0x0031:
            r3 = 1
            r0 = r24
            if (r0 != r3) goto L_0x004a
            r0 = r18
            r1 = r20
            r2 = r26
            java.lang.System.arraycopy(r10, r0, r10, r1, r2)
            int r3 = r20 + r26
            r6 = r4[r5]
            r10[r3] = r6
            r19 = r20
            r11 = r18
            goto L_0x0030
        L_0x004a:
            r0 = r22
            java.util.Comparator<? super T> r8 = r0.f188c
            r0 = r22
            int r0 = r0.minGallop
            r21 = r0
            r19 = r20
            r11 = r18
        L_0x0058:
            r15 = 0
            r16 = 0
        L_0x005b:
            r3 = r10[r11]
            r6 = r4[r5]
            int r3 = r8.compare(r3, r6)
            if (r3 >= 0) goto L_0x0098
            int r20 = r19 + 1
            int r18 = r11 + 1
            r3 = r10[r11]
            r10[r19] = r3
            int r16 = r16 + 1
            r15 = 0
            int r26 = r26 + -1
            if (r26 != 0) goto L_0x0157
            r19 = r20
            r11 = r18
        L_0x0078:
            r3 = 1
            r0 = r21
            if (r0 >= r3) goto L_0x007f
            r21 = 1
        L_0x007f:
            r0 = r21
            r1 = r22
            r1.minGallop = r0
            r3 = 1
            r0 = r24
            if (r0 != r3) goto L_0x0139
            r0 = r19
            r1 = r26
            java.lang.System.arraycopy(r10, r11, r10, r0, r1)
            int r3 = r19 + r26
            r6 = r4[r5]
            r10[r3] = r6
            goto L_0x0030
        L_0x0098:
            int r20 = r19 + 1
            int r17 = r5 + 1
            r3 = r4[r5]
            r10[r19] = r3
            int r15 = r15 + 1
            r16 = 0
            int r24 = r24 + -1
            r3 = 1
            r0 = r24
            if (r0 != r3) goto L_0x00b0
            r19 = r20
            r5 = r17
            goto L_0x0078
        L_0x00b0:
            r19 = r20
            r5 = r17
        L_0x00b4:
            r3 = r15 | r16
            r0 = r21
            if (r3 < r0) goto L_0x005b
            r18 = r11
        L_0x00bc:
            r3 = r10[r18]
            r7 = 0
            r6 = r24
            int r15 = gallopRight(r3, r4, r5, r6, r7, r8)
            if (r15 == 0) goto L_0x00d9
            r0 = r19
            java.lang.System.arraycopy(r4, r5, r10, r0, r15)
            int r19 = r19 + r15
            int r5 = r5 + r15
            int r24 = r24 - r15
            r3 = 1
            r0 = r24
            if (r0 > r3) goto L_0x00d9
            r11 = r18
            goto L_0x0078
        L_0x00d9:
            int r20 = r19 + 1
            int r11 = r18 + 1
            r3 = r10[r18]
            r10[r19] = r3
            int r26 = r26 + -1
            if (r26 != 0) goto L_0x00e8
            r19 = r20
            goto L_0x0078
        L_0x00e8:
            r9 = r4[r5]
            r13 = 0
            r12 = r26
            r14 = r8
            int r16 = gallopLeft(r9, r10, r11, r12, r13, r14)
            if (r16 == 0) goto L_0x0154
            r0 = r20
            r1 = r16
            java.lang.System.arraycopy(r10, r11, r10, r0, r1)
            int r19 = r20 + r16
            int r11 = r11 + r16
            int r26 = r26 - r16
            if (r26 == 0) goto L_0x0078
        L_0x0103:
            int r20 = r19 + 1
            int r17 = r5 + 1
            r3 = r4[r5]
            r10[r19] = r3
            int r24 = r24 + -1
            r3 = 1
            r0 = r24
            if (r0 != r3) goto L_0x0118
            r19 = r20
            r5 = r17
            goto L_0x0078
        L_0x0118:
            int r21 = r21 + -1
            r3 = 7
            if (r15 < r3) goto L_0x0134
            r3 = 1
            r6 = r3
        L_0x011f:
            r3 = 7
            r0 = r16
            if (r0 < r3) goto L_0x0137
            r3 = 1
        L_0x0125:
            r3 = r3 | r6
            if (r3 != 0) goto L_0x014c
            if (r21 >= 0) goto L_0x012c
            r21 = 0
        L_0x012c:
            int r21 = r21 + 2
            r19 = r20
            r5 = r17
            goto L_0x0058
        L_0x0134:
            r3 = 0
            r6 = r3
            goto L_0x011f
        L_0x0137:
            r3 = 0
            goto L_0x0125
        L_0x0139:
            if (r24 != 0) goto L_0x0143
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.String r6 = "Comparison method violates its general contract!"
            r3.<init>(r6)
            throw r3
        L_0x0143:
            r0 = r19
            r1 = r24
            java.lang.System.arraycopy(r4, r5, r10, r0, r1)
            goto L_0x0030
        L_0x014c:
            r19 = r20
            r18 = r11
            r5 = r17
            goto L_0x00bc
        L_0x0154:
            r19 = r20
            goto L_0x0103
        L_0x0157:
            r19 = r20
            r11 = r18
            goto L_0x00b4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.TimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00c2, code lost:
        r14 = r25 - gallopRight(r9[r18], r3, r24, r25, r25 - 1, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00d0, code lost:
        if (r14 == 0) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00d2, code lost:
        r20 = r20 - r14;
        r16 = r16 - r14;
        r25 = r25 - r14;
        java.lang.System.arraycopy(r3, r16 + 1, r3, r20 + 1, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00df, code lost:
        if (r25 == 0) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00e1, code lost:
        r21 = r20 - 1;
        r19 = r18 - 1;
        r3[r20] = r9[r18];
        r27 = r27 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ee, code lost:
        if (r27 != 1) goto L_0x00f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00f0, code lost:
        r20 = r21;
        r18 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00f5, code lost:
        r15 = r27 - gallopLeft(r3[r16], r9, 0, r27, r27 - 1, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0103, code lost:
        if (r15 == 0) goto L_0x0164;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0105, code lost:
        r20 = r21 - r15;
        r18 = r19 - r15;
        r27 = r27 - r15;
        java.lang.System.arraycopy(r9, r18 + 1, r3, r20 + 1, r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0115, code lost:
        if (r27 <= 1) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0117, code lost:
        r21 = r20 - 1;
        r17 = r16 - 1;
        r3[r20] = r3[r16];
        r25 = r25 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0121, code lost:
        if (r25 != 0) goto L_0x0129;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0123, code lost:
        r20 = r21;
        r16 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0129, code lost:
        r22 = r22 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x012c, code lost:
        if (r14 < 7) goto L_0x0143;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x012e, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0131, code lost:
        if (r15 < 7) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0133, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0135, code lost:
        if ((r2 | r4) != false) goto L_0x015e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0137, code lost:
        if (r22 >= 0) goto L_0x013b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0139, code lost:
        r22 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0143, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0146, code lost:
        r2 = DEBUG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x015e, code lost:
        r20 = r21;
        r16 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0164, code lost:
        r20 = r21;
        r18 = r19;
     */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00c2 A[EDGE_INSN: B:67:0x00c2->B:25:0x00c2 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeHi(int r24, int r25, int r26, int r27) {
        /*
            r23 = this;
            r0 = r23
            T[] r3 = r0.f187a
            r0 = r23
            r1 = r27
            java.lang.Object[] r9 = r0.ensureCapacity(r1)
            r2 = 0
            r0 = r26
            r1 = r27
            java.lang.System.arraycopy(r3, r0, r9, r2, r1)
            int r2 = r24 + r25
            int r16 = r2 + -1
            int r18 = r27 + -1
            int r2 = r26 + r27
            int r20 = r2 + -1
            int r21 = r20 + -1
            int r17 = r16 + -1
            r2 = r3[r16]
            r3[r20] = r2
            int r25 = r25 + -1
            if (r25 != 0) goto L_0x0039
            r2 = 0
            int r4 = r27 + -1
            int r4 = r21 - r4
            r0 = r27
            java.lang.System.arraycopy(r9, r2, r3, r4, r0)
            r20 = r21
            r16 = r17
        L_0x0038:
            return
        L_0x0039:
            r2 = 1
            r0 = r27
            if (r0 != r2) goto L_0x0050
            int r20 = r21 - r25
            int r16 = r17 - r25
            int r2 = r16 + 1
            int r4 = r20 + 1
            r0 = r25
            java.lang.System.arraycopy(r3, r2, r3, r4, r0)
            r2 = r9[r18]
            r3[r20] = r2
            goto L_0x0038
        L_0x0050:
            r0 = r23
            java.util.Comparator<? super T> r7 = r0.f188c
            r0 = r23
            int r0 = r0.minGallop
            r22 = r0
            r20 = r21
            r16 = r17
        L_0x005e:
            r14 = 0
            r15 = 0
        L_0x0060:
            r2 = r9[r18]
            r4 = r3[r16]
            int r2 = r7.compare(r2, r4)
            if (r2 >= 0) goto L_0x00a1
            int r21 = r20 + -1
            int r17 = r16 + -1
            r2 = r3[r16]
            r3[r20] = r2
            int r14 = r14 + 1
            r15 = 0
            int r25 = r25 + -1
            if (r25 != 0) goto L_0x0169
            r20 = r21
            r16 = r17
        L_0x007d:
            r2 = 1
            r0 = r22
            if (r0 >= r2) goto L_0x0084
            r22 = 1
        L_0x0084:
            r0 = r22
            r1 = r23
            r1.minGallop = r0
            r2 = 1
            r0 = r27
            if (r0 != r2) goto L_0x0148
            int r20 = r20 - r25
            int r16 = r16 - r25
            int r2 = r16 + 1
            int r4 = r20 + 1
            r0 = r25
            java.lang.System.arraycopy(r3, r2, r3, r4, r0)
            r2 = r9[r18]
            r3[r20] = r2
            goto L_0x0038
        L_0x00a1:
            int r21 = r20 + -1
            int r19 = r18 + -1
            r2 = r9[r18]
            r3[r20] = r2
            int r15 = r15 + 1
            r14 = 0
            int r27 = r27 + -1
            r2 = 1
            r0 = r27
            if (r0 != r2) goto L_0x00b8
            r20 = r21
            r18 = r19
            goto L_0x007d
        L_0x00b8:
            r20 = r21
            r18 = r19
        L_0x00bc:
            r2 = r14 | r15
            r0 = r22
            if (r2 < r0) goto L_0x0060
        L_0x00c2:
            r2 = r9[r18]
            int r6 = r25 + -1
            r4 = r24
            r5 = r25
            int r2 = gallopRight(r2, r3, r4, r5, r6, r7)
            int r14 = r25 - r2
            if (r14 == 0) goto L_0x00e1
            int r20 = r20 - r14
            int r16 = r16 - r14
            int r25 = r25 - r14
            int r2 = r16 + 1
            int r4 = r20 + 1
            java.lang.System.arraycopy(r3, r2, r3, r4, r14)
            if (r25 == 0) goto L_0x007d
        L_0x00e1:
            int r21 = r20 + -1
            int r19 = r18 + -1
            r2 = r9[r18]
            r3[r20] = r2
            int r27 = r27 + -1
            r2 = 1
            r0 = r27
            if (r0 != r2) goto L_0x00f5
            r20 = r21
            r18 = r19
            goto L_0x007d
        L_0x00f5:
            r8 = r3[r16]
            r10 = 0
            int r12 = r27 + -1
            r11 = r27
            r13 = r7
            int r2 = gallopLeft(r8, r9, r10, r11, r12, r13)
            int r15 = r27 - r2
            if (r15 == 0) goto L_0x0164
            int r20 = r21 - r15
            int r18 = r19 - r15
            int r27 = r27 - r15
            int r2 = r18 + 1
            int r4 = r20 + 1
            java.lang.System.arraycopy(r9, r2, r3, r4, r15)
            r2 = 1
            r0 = r27
            if (r0 <= r2) goto L_0x007d
        L_0x0117:
            int r21 = r20 + -1
            int r17 = r16 + -1
            r2 = r3[r16]
            r3[r20] = r2
            int r25 = r25 + -1
            if (r25 != 0) goto L_0x0129
            r20 = r21
            r16 = r17
            goto L_0x007d
        L_0x0129:
            int r22 = r22 + -1
            r2 = 7
            if (r14 < r2) goto L_0x0143
            r2 = 1
            r4 = r2
        L_0x0130:
            r2 = 7
            if (r15 < r2) goto L_0x0146
            r2 = 1
        L_0x0134:
            r2 = r2 | r4
            if (r2 != 0) goto L_0x015e
            if (r22 >= 0) goto L_0x013b
            r22 = 0
        L_0x013b:
            int r22 = r22 + 2
            r20 = r21
            r16 = r17
            goto L_0x005e
        L_0x0143:
            r2 = 0
            r4 = r2
            goto L_0x0130
        L_0x0146:
            r2 = 0
            goto L_0x0134
        L_0x0148:
            if (r27 != 0) goto L_0x0152
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r4 = "Comparison method violates its general contract!"
            r2.<init>(r4)
            throw r2
        L_0x0152:
            r2 = 0
            int r4 = r27 + -1
            int r4 = r20 - r4
            r0 = r27
            java.lang.System.arraycopy(r9, r2, r3, r4, r0)
            goto L_0x0038
        L_0x015e:
            r20 = r21
            r16 = r17
            goto L_0x00c2
        L_0x0164:
            r20 = r21
            r18 = r19
            goto L_0x0117
        L_0x0169:
            r20 = r21
            r16 = r17
            goto L_0x00bc
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.TimSort.mergeHi(int, int, int, int):void");
    }

    private T[] ensureCapacity(int minCapacity) {
        int newSize;
        this.tmpCount = Math.max(this.tmpCount, minCapacity);
        if (this.tmp.length < minCapacity) {
            int newSize2 = minCapacity;
            int newSize3 = newSize2 | (newSize2 >> 1);
            int newSize4 = newSize3 | (newSize3 >> 2);
            int newSize5 = newSize4 | (newSize4 >> 4);
            int newSize6 = newSize5 | (newSize5 >> 8);
            int newSize7 = (newSize6 | (newSize6 >> 16)) + 1;
            if (newSize7 < 0) {
                newSize = minCapacity;
            } else {
                newSize = Math.min(newSize7, this.f187a.length >>> 1);
            }
            this.tmp = (Object[]) new Object[newSize];
        }
        return this.tmp;
    }

    private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        } else if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        } else if (toIndex > arrayLen) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }
}
