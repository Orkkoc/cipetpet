package com.badlogic.gdx.utils;

class ComparableTimSort {
    private static final boolean DEBUG = false;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private static final int MIN_GALLOP = 7;
    private static final int MIN_MERGE = 32;

    /* renamed from: a */
    private Object[] f184a;
    private int minGallop;
    private final int[] runBase;
    private final int[] runLen;
    private int stackSize;
    private Object[] tmp;

    ComparableTimSort() {
        this.minGallop = 7;
        this.stackSize = 0;
        this.tmp = new Object[256];
        this.runBase = new int[40];
        this.runLen = new int[40];
    }

    public void doSort(Object[] a, int lo, int hi) {
        this.stackSize = 0;
        rangeCheck(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a, lo, hi, lo + countRunAndMakeAscending(a, lo, hi));
                return;
            }
            this.f184a = a;
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a, lo, hi);
                if (runLen2 < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a, lo, lo + force, lo + runLen2);
                    runLen2 = force;
                }
                pushRun(lo, runLen2);
                mergeCollapse();
                lo += runLen2;
                nRemaining -= runLen2;
            } while (nRemaining != 0);
            mergeForceCollapse();
        }
    }

    private ComparableTimSort(Object[] a) {
        this.minGallop = 7;
        this.stackSize = 0;
        this.f184a = a;
        int len = a.length;
        this.tmp = new Object[(len < 512 ? len >>> 1 : 256)];
        int stackLen = len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static void sort(Object[] a) {
        sort(a, 0, a.length);
    }

    static void sort(Object[] a, int lo, int hi) {
        rangeCheck(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < 32) {
                binarySort(a, lo, hi, lo + countRunAndMakeAscending(a, lo, hi));
                return;
            }
            ComparableTimSort ts = new ComparableTimSort(a);
            int minRun = minRunLength(nRemaining);
            do {
                int runLen2 = countRunAndMakeAscending(a, lo, hi);
                if (runLen2 < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a, lo, lo + force, lo + runLen2);
                    runLen2 = force;
                }
                ts.pushRun(lo, runLen2);
                ts.mergeCollapse();
                lo += runLen2;
                nRemaining -= runLen2;
            } while (nRemaining != 0);
            ts.mergeForceCollapse();
        }
    }

    private static void binarySort(Object[] a, int lo, int hi, int start) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            Comparable<Object> pivot = a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (pivot.compareTo(a[mid]) < 0) {
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

    private static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
        int runHi;
        int runHi2 = lo + 1;
        if (runHi2 == hi) {
            return 1;
        }
        int runHi3 = runHi2 + 1;
        if (a[runHi2].compareTo(a[lo]) < 0) {
            runHi = runHi3;
            while (runHi < hi && a[runHi].compareTo(a[runHi - 1]) < 0) {
                runHi++;
            }
            reverseRange(a, lo, runHi);
        } else {
            int runHi4 = runHi3;
            while (runHi < hi && a[runHi].compareTo(a[runHi - 1]) >= 0) {
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
        int k = gallopRight((Comparable) this.f184a[base2], this.f184a, base1, len1, 0);
        int base12 = base1 + k;
        int len12 = len1 - k;
        if (len12 != 0 && (len2 = gallopLeft((Comparable) this.f184a[(base12 + len12) - 1], this.f184a, base2, len22, len22 - 1)) != 0) {
            if (len12 <= len2) {
                mergeLo(base12, len12, base2, len2);
            } else {
                mergeHi(base12, len12, base2, len2);
            }
        }
    }

    private static int gallopLeft(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int lastOfs;
        int ofs;
        int lastOfs2 = 0;
        int ofs2 = 1;
        if (key.compareTo(a[base + hint]) > 0) {
            int maxOfs = len - hint;
            while (ofs2 < maxOfs && key.compareTo(a[base + hint + ofs2]) > 0) {
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
            while (ofs2 < maxOfs2 && key.compareTo(a[(base + hint) - ofs2]) <= 0) {
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
            if (key.compareTo(a[base + m]) > 0) {
                lastOfs3 = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static int gallopRight(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int lastOfs;
        int ofs;
        int ofs2 = 1;
        int lastOfs2 = 0;
        if (key.compareTo(a[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while (ofs2 < maxOfs && key.compareTo(a[(base + hint) - ofs2]) < 0) {
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
            while (ofs2 < maxOfs2 && key.compareTo(a[base + hint + ofs2]) >= 0) {
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
            if (key.compareTo(a[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs3 = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x008e, code lost:
        r2 = gallopRight((java.lang.Comparable) r1[r6], r11, r4, r16, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0099, code lost:
        if (r2 == 0) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x009b, code lost:
        java.lang.System.arraycopy(r11, r4, r1, r8, r2);
        r8 = r8 + r2;
        r4 = r4 + r2;
        r16 = r16 - r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a5, code lost:
        if (r16 <= 1) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a7, code lost:
        r9 = r8 + 1;
        r7 = r6 + 1;
        r1[r8] = r1[r6];
        r18 = r18 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00b1, code lost:
        if (r18 != 0) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00b3, code lost:
        r8 = r9;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b6, code lost:
        r3 = gallopLeft((java.lang.Comparable) r11[r4], r1, r7, r18, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c1, code lost:
        if (r3 == 0) goto L_0x0112;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c3, code lost:
        java.lang.System.arraycopy(r1, r7, r1, r9, r3);
        r8 = r9 + r3;
        r6 = r7 + r3;
        r18 = r18 - r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00cc, code lost:
        if (r18 == 0) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ce, code lost:
        r9 = r8 + 1;
        r5 = r4 + 1;
        r1[r8] = r11[r4];
        r16 = r16 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00db, code lost:
        if (r16 != 1) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00dd, code lost:
        r8 = r9;
        r4 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00e1, code lost:
        r10 = r10 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00e4, code lost:
        if (r2 < 7) goto L_0x00f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00e6, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00e9, code lost:
        if (r3 < 7) goto L_0x00fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00eb, code lost:
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00ed, code lost:
        if ((r12 | r13) != false) goto L_0x010e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00ef, code lost:
        if (r10 >= 0) goto L_0x00f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00f1, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f8, code lost:
        r13 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00fb, code lost:
        r12 = DEBUG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x010e, code lost:
        r8 = r9;
        r4 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0112, code lost:
        r8 = r9;
        r6 = r7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x008e A[EDGE_INSN: B:67:0x008e->B:25:0x008e ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeLo(int r15, int r16, int r17, int r18) {
        /*
            r14 = this;
            java.lang.Object[] r1 = r14.f184a
            r0 = r16
            java.lang.Object[] r11 = r14.ensureCapacity(r0)
            r12 = 0
            r0 = r16
            java.lang.System.arraycopy(r1, r15, r11, r12, r0)
            r4 = 0
            r6 = r17
            r8 = r15
            int r9 = r8 + 1
            int r7 = r6 + 1
            r12 = r1[r6]
            r1[r8] = r12
            int r18 = r18 + -1
            if (r18 != 0) goto L_0x0026
            r0 = r16
            java.lang.System.arraycopy(r11, r4, r1, r9, r0)
            r8 = r9
            r6 = r7
        L_0x0025:
            return
        L_0x0026:
            r12 = 1
            r0 = r16
            if (r0 != r12) goto L_0x0039
            r0 = r18
            java.lang.System.arraycopy(r1, r7, r1, r9, r0)
            int r12 = r9 + r18
            r13 = r11[r4]
            r1[r12] = r13
            r8 = r9
            r6 = r7
            goto L_0x0025
        L_0x0039:
            int r10 = r14.minGallop
            r8 = r9
            r6 = r7
        L_0x003d:
            r2 = 0
            r3 = 0
        L_0x003f:
            r12 = r1[r6]
            java.lang.Comparable r12 = (java.lang.Comparable) r12
            r13 = r11[r4]
            int r12 = r12.compareTo(r13)
            if (r12 >= 0) goto L_0x0073
            int r9 = r8 + 1
            int r7 = r6 + 1
            r12 = r1[r6]
            r1[r8] = r12
            int r3 = r3 + 1
            r2 = 0
            int r18 = r18 + -1
            if (r18 != 0) goto L_0x0115
            r8 = r9
            r6 = r7
        L_0x005c:
            r12 = 1
            if (r10 >= r12) goto L_0x0060
            r10 = 1
        L_0x0060:
            r14.minGallop = r10
            r12 = 1
            r0 = r16
            if (r0 != r12) goto L_0x00fd
            r0 = r18
            java.lang.System.arraycopy(r1, r6, r1, r8, r0)
            int r12 = r8 + r18
            r13 = r11[r4]
            r1[r12] = r13
            goto L_0x0025
        L_0x0073:
            int r9 = r8 + 1
            int r5 = r4 + 1
            r12 = r11[r4]
            r1[r8] = r12
            int r2 = r2 + 1
            r3 = 0
            int r16 = r16 + -1
            r12 = 1
            r0 = r16
            if (r0 != r12) goto L_0x0088
            r8 = r9
            r4 = r5
            goto L_0x005c
        L_0x0088:
            r8 = r9
            r4 = r5
        L_0x008a:
            r12 = r2 | r3
            if (r12 < r10) goto L_0x003f
        L_0x008e:
            r12 = r1[r6]
            java.lang.Comparable r12 = (java.lang.Comparable) r12
            r13 = 0
            r0 = r16
            int r2 = gallopRight(r12, r11, r4, r0, r13)
            if (r2 == 0) goto L_0x00a7
            java.lang.System.arraycopy(r11, r4, r1, r8, r2)
            int r8 = r8 + r2
            int r4 = r4 + r2
            int r16 = r16 - r2
            r12 = 1
            r0 = r16
            if (r0 <= r12) goto L_0x005c
        L_0x00a7:
            int r9 = r8 + 1
            int r7 = r6 + 1
            r12 = r1[r6]
            r1[r8] = r12
            int r18 = r18 + -1
            if (r18 != 0) goto L_0x00b6
            r8 = r9
            r6 = r7
            goto L_0x005c
        L_0x00b6:
            r12 = r11[r4]
            java.lang.Comparable r12 = (java.lang.Comparable) r12
            r13 = 0
            r0 = r18
            int r3 = gallopLeft(r12, r1, r7, r0, r13)
            if (r3 == 0) goto L_0x0112
            java.lang.System.arraycopy(r1, r7, r1, r9, r3)
            int r8 = r9 + r3
            int r6 = r7 + r3
            int r18 = r18 - r3
            if (r18 == 0) goto L_0x005c
        L_0x00ce:
            int r9 = r8 + 1
            int r5 = r4 + 1
            r12 = r11[r4]
            r1[r8] = r12
            int r16 = r16 + -1
            r12 = 1
            r0 = r16
            if (r0 != r12) goto L_0x00e1
            r8 = r9
            r4 = r5
            goto L_0x005c
        L_0x00e1:
            int r10 = r10 + -1
            r12 = 7
            if (r2 < r12) goto L_0x00f8
            r12 = 1
            r13 = r12
        L_0x00e8:
            r12 = 7
            if (r3 < r12) goto L_0x00fb
            r12 = 1
        L_0x00ec:
            r12 = r12 | r13
            if (r12 != 0) goto L_0x010e
            if (r10 >= 0) goto L_0x00f2
            r10 = 0
        L_0x00f2:
            int r10 = r10 + 2
            r8 = r9
            r4 = r5
            goto L_0x003d
        L_0x00f8:
            r12 = 0
            r13 = r12
            goto L_0x00e8
        L_0x00fb:
            r12 = 0
            goto L_0x00ec
        L_0x00fd:
            if (r16 != 0) goto L_0x0107
            java.lang.IllegalArgumentException r12 = new java.lang.IllegalArgumentException
            java.lang.String r13 = "Comparison method violates its general contract!"
            r12.<init>(r13)
            throw r12
        L_0x0107:
            r0 = r16
            java.lang.System.arraycopy(r11, r4, r1, r8, r0)
            goto L_0x0025
        L_0x010e:
            r8 = r9
            r4 = r5
            goto L_0x008e
        L_0x0112:
            r8 = r9
            r6 = r7
            goto L_0x00ce
        L_0x0115:
            r8 = r9
            r6 = r7
            goto L_0x008a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.ComparableTimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00ad, code lost:
        r3 = r18 - gallopRight((java.lang.Comparable) r12[r7], r2, r17, r18, r18 - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00bd, code lost:
        if (r3 == 0) goto L_0x00cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00bf, code lost:
        r9 = r9 - r3;
        r5 = r5 - r3;
        r18 = r18 - r3;
        java.lang.System.arraycopy(r2, r5 + 1, r2, r9 + 1, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00ca, code lost:
        if (r18 == 0) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00cc, code lost:
        r10 = r9 - 1;
        r8 = r7 - 1;
        r2[r9] = r12[r7];
        r20 = r20 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00d9, code lost:
        if (r20 != 1) goto L_0x00de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00db, code lost:
        r9 = r10;
        r7 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00de, code lost:
        r4 = r20 - gallopLeft((java.lang.Comparable) r2[r5], r12, 0, r20, r20 - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ed, code lost:
        if (r4 == 0) goto L_0x0147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00ef, code lost:
        r9 = r10 - r4;
        r7 = r8 - r4;
        r20 = r20 - r4;
        java.lang.System.arraycopy(r12, r7 + 1, r2, r9 + 1, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ff, code lost:
        if (r20 <= 1) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0101, code lost:
        r10 = r9 - 1;
        r6 = r5 - 1;
        r2[r9] = r2[r5];
        r18 = r18 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x010b, code lost:
        if (r18 != 0) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x010d, code lost:
        r9 = r10;
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0111, code lost:
        r11 = r11 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0114, code lost:
        if (r3 < 7) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0116, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0119, code lost:
        if (r4 < 7) goto L_0x012b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x011b, code lost:
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x011d, code lost:
        if ((r13 | r14) != false) goto L_0x0143;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x011f, code lost:
        if (r11 >= 0) goto L_0x0122;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0121, code lost:
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0128, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x012b, code lost:
        r13 = DEBUG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0143, code lost:
        r9 = r10;
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0147, code lost:
        r9 = r10;
        r7 = r8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00ad A[EDGE_INSN: B:67:0x00ad->B:25:0x00ad ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeHi(int r17, int r18, int r19, int r20) {
        /*
            r16 = this;
            r0 = r16
            java.lang.Object[] r2 = r0.f184a
            r0 = r16
            r1 = r20
            java.lang.Object[] r12 = r0.ensureCapacity(r1)
            r13 = 0
            r0 = r19
            r1 = r20
            java.lang.System.arraycopy(r2, r0, r12, r13, r1)
            int r13 = r17 + r18
            int r5 = r13 + -1
            int r7 = r20 + -1
            int r13 = r19 + r20
            int r9 = r13 + -1
            int r10 = r9 + -1
            int r6 = r5 + -1
            r13 = r2[r5]
            r2[r9] = r13
            int r18 = r18 + -1
            if (r18 != 0) goto L_0x0037
            r13 = 0
            int r14 = r20 + -1
            int r14 = r10 - r14
            r0 = r20
            java.lang.System.arraycopy(r12, r13, r2, r14, r0)
            r9 = r10
            r5 = r6
        L_0x0036:
            return
        L_0x0037:
            r13 = 1
            r0 = r20
            if (r0 != r13) goto L_0x004e
            int r9 = r10 - r18
            int r5 = r6 - r18
            int r13 = r5 + 1
            int r14 = r9 + 1
            r0 = r18
            java.lang.System.arraycopy(r2, r13, r2, r14, r0)
            r13 = r12[r7]
            r2[r9] = r13
            goto L_0x0036
        L_0x004e:
            r0 = r16
            int r11 = r0.minGallop
            r9 = r10
            r5 = r6
        L_0x0054:
            r3 = 0
            r4 = 0
        L_0x0056:
            r13 = r12[r7]
            java.lang.Comparable r13 = (java.lang.Comparable) r13
            r14 = r2[r5]
            int r13 = r13.compareTo(r14)
            if (r13 >= 0) goto L_0x0092
            int r10 = r9 + -1
            int r6 = r5 + -1
            r13 = r2[r5]
            r2[r9] = r13
            int r3 = r3 + 1
            r4 = 0
            int r18 = r18 + -1
            if (r18 != 0) goto L_0x014a
            r9 = r10
            r5 = r6
        L_0x0073:
            r13 = 1
            if (r11 >= r13) goto L_0x0077
            r11 = 1
        L_0x0077:
            r0 = r16
            r0.minGallop = r11
            r13 = 1
            r0 = r20
            if (r0 != r13) goto L_0x012d
            int r9 = r9 - r18
            int r5 = r5 - r18
            int r13 = r5 + 1
            int r14 = r9 + 1
            r0 = r18
            java.lang.System.arraycopy(r2, r13, r2, r14, r0)
            r13 = r12[r7]
            r2[r9] = r13
            goto L_0x0036
        L_0x0092:
            int r10 = r9 + -1
            int r8 = r7 + -1
            r13 = r12[r7]
            r2[r9] = r13
            int r4 = r4 + 1
            r3 = 0
            int r20 = r20 + -1
            r13 = 1
            r0 = r20
            if (r0 != r13) goto L_0x00a7
            r9 = r10
            r7 = r8
            goto L_0x0073
        L_0x00a7:
            r9 = r10
            r7 = r8
        L_0x00a9:
            r13 = r3 | r4
            if (r13 < r11) goto L_0x0056
        L_0x00ad:
            r13 = r12[r7]
            java.lang.Comparable r13 = (java.lang.Comparable) r13
            int r14 = r18 + -1
            r0 = r17
            r1 = r18
            int r13 = gallopRight(r13, r2, r0, r1, r14)
            int r3 = r18 - r13
            if (r3 == 0) goto L_0x00cc
            int r9 = r9 - r3
            int r5 = r5 - r3
            int r18 = r18 - r3
            int r13 = r5 + 1
            int r14 = r9 + 1
            java.lang.System.arraycopy(r2, r13, r2, r14, r3)
            if (r18 == 0) goto L_0x0073
        L_0x00cc:
            int r10 = r9 + -1
            int r8 = r7 + -1
            r13 = r12[r7]
            r2[r9] = r13
            int r20 = r20 + -1
            r13 = 1
            r0 = r20
            if (r0 != r13) goto L_0x00de
            r9 = r10
            r7 = r8
            goto L_0x0073
        L_0x00de:
            r13 = r2[r5]
            java.lang.Comparable r13 = (java.lang.Comparable) r13
            r14 = 0
            int r15 = r20 + -1
            r0 = r20
            int r13 = gallopLeft(r13, r12, r14, r0, r15)
            int r4 = r20 - r13
            if (r4 == 0) goto L_0x0147
            int r9 = r10 - r4
            int r7 = r8 - r4
            int r20 = r20 - r4
            int r13 = r7 + 1
            int r14 = r9 + 1
            java.lang.System.arraycopy(r12, r13, r2, r14, r4)
            r13 = 1
            r0 = r20
            if (r0 <= r13) goto L_0x0073
        L_0x0101:
            int r10 = r9 + -1
            int r6 = r5 + -1
            r13 = r2[r5]
            r2[r9] = r13
            int r18 = r18 + -1
            if (r18 != 0) goto L_0x0111
            r9 = r10
            r5 = r6
            goto L_0x0073
        L_0x0111:
            int r11 = r11 + -1
            r13 = 7
            if (r3 < r13) goto L_0x0128
            r13 = 1
            r14 = r13
        L_0x0118:
            r13 = 7
            if (r4 < r13) goto L_0x012b
            r13 = 1
        L_0x011c:
            r13 = r13 | r14
            if (r13 != 0) goto L_0x0143
            if (r11 >= 0) goto L_0x0122
            r11 = 0
        L_0x0122:
            int r11 = r11 + 2
            r9 = r10
            r5 = r6
            goto L_0x0054
        L_0x0128:
            r13 = 0
            r14 = r13
            goto L_0x0118
        L_0x012b:
            r13 = 0
            goto L_0x011c
        L_0x012d:
            if (r20 != 0) goto L_0x0137
            java.lang.IllegalArgumentException r13 = new java.lang.IllegalArgumentException
            java.lang.String r14 = "Comparison method violates its general contract!"
            r13.<init>(r14)
            throw r13
        L_0x0137:
            r13 = 0
            int r14 = r20 + -1
            int r14 = r9 - r14
            r0 = r20
            java.lang.System.arraycopy(r12, r13, r2, r14, r0)
            goto L_0x0036
        L_0x0143:
            r9 = r10
            r5 = r6
            goto L_0x00ad
        L_0x0147:
            r9 = r10
            r7 = r8
            goto L_0x0101
        L_0x014a:
            r9 = r10
            r5 = r6
            goto L_0x00a9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.ComparableTimSort.mergeHi(int, int, int, int):void");
    }

    private Object[] ensureCapacity(int minCapacity) {
        int newSize;
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
                newSize = Math.min(newSize7, this.f184a.length >>> 1);
            }
            this.tmp = new Object[newSize];
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
