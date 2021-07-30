package com.badlogic.gdx.utils.compression.lzma;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.compression.ICodeProgress;
import com.badlogic.gdx.utils.compression.p001lz.BinTree;
import com.badlogic.gdx.utils.compression.rangecoder.BitTreeEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Encoder {
    public static final int EMatchFinderTypeBT2 = 0;
    public static final int EMatchFinderTypeBT4 = 1;
    static byte[] g_FastPos = new byte[GL10.GL_EXP];
    static final int kDefaultDictionaryLogSize = 22;
    static final int kIfinityPrice = 268435455;
    static final int kNumFastBytesDefault = 32;
    public static final int kNumLenSpecSymbols = 16;
    static final int kNumOpts = 4096;
    public static final int kPropSize = 5;
    int _additionalOffset;
    int _alignPriceCount;
    int[] _alignPrices = new int[16];
    int _dictionarySize = 4194304;
    int _dictionarySizePrev = -1;
    int _distTableSize = 44;
    int[] _distancesPrices = new int[512];
    boolean _finished;
    InputStream _inStream;
    short[] _isMatch = new short[192];
    short[] _isRep = new short[12];
    short[] _isRep0Long = new short[192];
    short[] _isRepG0 = new short[12];
    short[] _isRepG1 = new short[12];
    short[] _isRepG2 = new short[12];
    LenPriceTableEncoder _lenEncoder = new LenPriceTableEncoder();
    LiteralEncoder _literalEncoder = new LiteralEncoder();
    int _longestMatchLength;
    boolean _longestMatchWasFound;
    int[] _matchDistances = new int[548];
    BinTree _matchFinder = null;
    int _matchFinderType = 1;
    int _matchPriceCount;
    boolean _needReleaseMFStream = false;
    int _numDistancePairs;
    int _numFastBytes = 32;
    int _numFastBytesPrev = -1;
    int _numLiteralContextBits = 3;
    int _numLiteralPosStateBits = 0;
    Optimal[] _optimum = new Optimal[4096];
    int _optimumCurrentIndex;
    int _optimumEndIndex;
    BitTreeEncoder _posAlignEncoder = new BitTreeEncoder(4);
    short[] _posEncoders = new short[114];
    BitTreeEncoder[] _posSlotEncoder = new BitTreeEncoder[4];
    int[] _posSlotPrices = new int[256];
    int _posStateBits = 2;
    int _posStateMask = 3;
    byte _previousByte;
    com.badlogic.gdx.utils.compression.rangecoder.Encoder _rangeEncoder = new com.badlogic.gdx.utils.compression.rangecoder.Encoder();
    int[] _repDistances = new int[4];
    LenPriceTableEncoder _repMatchLenEncoder = new LenPriceTableEncoder();
    int _state = Base.StateInit();
    boolean _writeEndMark = false;
    int backRes;
    boolean[] finished = new boolean[1];
    long nowPos64;
    long[] processedInSize = new long[1];
    long[] processedOutSize = new long[1];
    byte[] properties = new byte[5];
    int[] repLens = new int[4];
    int[] reps = new int[4];
    int[] tempPrices = new int[128];

    static {
        int c = 2;
        g_FastPos[0] = 0;
        g_FastPos[1] = 1;
        for (int slotFast = 2; slotFast < 22; slotFast++) {
            int k = 1 << ((slotFast >> 1) - 1);
            int j = 0;
            while (j < k) {
                g_FastPos[c] = (byte) slotFast;
                j++;
                c++;
            }
        }
    }

    static int GetPosSlot(int pos) {
        if (pos < 2048) {
            return g_FastPos[pos];
        }
        if (pos < 2097152) {
            return g_FastPos[pos >> 10] + 20;
        }
        return g_FastPos[pos >> 20] + 40;
    }

    static int GetPosSlot2(int pos) {
        if (pos < 131072) {
            return g_FastPos[pos >> 6] + 12;
        }
        if (pos < 134217728) {
            return g_FastPos[pos >> 16] + 32;
        }
        return g_FastPos[pos >> 26] + 52;
    }

    /* access modifiers changed from: package-private */
    public void BaseInit() {
        this._state = Base.StateInit();
        this._previousByte = 0;
        for (int i = 0; i < 4; i++) {
            this._repDistances[i] = 0;
        }
    }

    class LiteralEncoder {
        Encoder2[] m_Coders;
        int m_NumPosBits;
        int m_NumPrevBits;
        int m_PosMask;

        class Encoder2 {
            short[] m_Encoders = new short[768];

            Encoder2() {
            }

            public void Init() {
                com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this.m_Encoders);
            }

            public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, byte symbol) throws IOException {
                int context = 1;
                for (int i = 7; i >= 0; i--) {
                    int bit = (symbol >> i) & 1;
                    rangeEncoder.Encode(this.m_Encoders, context, bit);
                    context = (context << 1) | bit;
                }
            }

            public void EncodeMatched(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, byte matchByte, byte symbol) throws IOException {
                int context = 1;
                boolean same = true;
                for (int i = 7; i >= 0; i--) {
                    int bit = (symbol >> i) & 1;
                    int state = context;
                    if (same) {
                        int matchBit = (matchByte >> i) & 1;
                        state += (matchBit + 1) << 8;
                        same = matchBit == bit;
                    }
                    rangeEncoder.Encode(this.m_Encoders, state, bit);
                    context = (context << 1) | bit;
                }
            }

            public int GetPrice(boolean matchMode, byte matchByte, byte symbol) {
                int price = 0;
                int context = 1;
                int i = 7;
                if (matchMode) {
                    while (true) {
                        if (i < 0) {
                            break;
                        }
                        int matchBit = (matchByte >> i) & 1;
                        int bit = (symbol >> i) & 1;
                        price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[((matchBit + 1) << 8) + context], bit);
                        context = (context << 1) | bit;
                        if (matchBit != bit) {
                            i--;
                            break;
                        }
                        i--;
                    }
                }
                while (i >= 0) {
                    int bit2 = (symbol >> i) & 1;
                    price += com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this.m_Encoders[context], bit2);
                    context = (context << 1) | bit2;
                    i--;
                }
                return price;
            }
        }

        LiteralEncoder() {
        }

        public void Create(int numPosBits, int numPrevBits) {
            if (this.m_Coders == null || this.m_NumPrevBits != numPrevBits || this.m_NumPosBits != numPosBits) {
                this.m_NumPosBits = numPosBits;
                this.m_PosMask = (1 << numPosBits) - 1;
                this.m_NumPrevBits = numPrevBits;
                int numStates = 1 << (this.m_NumPrevBits + this.m_NumPosBits);
                this.m_Coders = new Encoder2[numStates];
                for (int i = 0; i < numStates; i++) {
                    this.m_Coders[i] = new Encoder2();
                }
            }
        }

        public void Init() {
            int numStates = 1 << (this.m_NumPrevBits + this.m_NumPosBits);
            for (int i = 0; i < numStates; i++) {
                this.m_Coders[i].Init();
            }
        }

        public Encoder2 GetSubCoder(int pos, byte prevByte) {
            return this.m_Coders[((this.m_PosMask & pos) << this.m_NumPrevBits) + ((prevByte & 255) >>> (8 - this.m_NumPrevBits))];
        }
    }

    class LenEncoder {
        short[] _choice = new short[2];
        BitTreeEncoder _highCoder = new BitTreeEncoder(8);
        BitTreeEncoder[] _lowCoder = new BitTreeEncoder[16];
        BitTreeEncoder[] _midCoder = new BitTreeEncoder[16];

        public LenEncoder() {
            for (int posState = 0; posState < 16; posState++) {
                this._lowCoder[posState] = new BitTreeEncoder(3);
                this._midCoder[posState] = new BitTreeEncoder(3);
            }
        }

        public void Init(int numPosStates) {
            com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._choice);
            for (int posState = 0; posState < numPosStates; posState++) {
                this._lowCoder[posState].Init();
                this._midCoder[posState].Init();
            }
            this._highCoder.Init();
        }

        public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            if (symbol < 8) {
                rangeEncoder.Encode(this._choice, 0, 0);
                this._lowCoder[posState].Encode(rangeEncoder, symbol);
                return;
            }
            int symbol2 = symbol - 8;
            rangeEncoder.Encode(this._choice, 0, 1);
            if (symbol2 < 8) {
                rangeEncoder.Encode(this._choice, 1, 0);
                this._midCoder[posState].Encode(rangeEncoder, symbol2);
                return;
            }
            rangeEncoder.Encode(this._choice, 1, 1);
            this._highCoder.Encode(rangeEncoder, symbol2 - 8);
        }

        public void SetPrices(int posState, int numSymbols, int[] prices, int st) {
            int a0 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[0]);
            int a1 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[0]);
            int b0 = a1 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._choice[1]);
            int b1 = a1 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._choice[1]);
            int i = 0;
            while (i < 8) {
                if (i < numSymbols) {
                    prices[st + i] = this._lowCoder[posState].GetPrice(i) + a0;
                    i++;
                } else {
                    return;
                }
            }
            while (i < 16) {
                if (i < numSymbols) {
                    prices[st + i] = this._midCoder[posState].GetPrice(i - 8) + b0;
                    i++;
                } else {
                    return;
                }
            }
            while (i < numSymbols) {
                prices[st + i] = this._highCoder.GetPrice((i - 8) - 8) + b1;
                i++;
            }
        }
    }

    class LenPriceTableEncoder extends LenEncoder {
        int[] _counters = new int[16];
        int[] _prices = new int[4352];
        int _tableSize;

        LenPriceTableEncoder() {
            super();
        }

        public void SetTableSize(int tableSize) {
            this._tableSize = tableSize;
        }

        public int GetPrice(int symbol, int posState) {
            return this._prices[(posState * Base.kNumLenSymbols) + symbol];
        }

        /* access modifiers changed from: package-private */
        public void UpdateTable(int posState) {
            SetPrices(posState, this._tableSize, this._prices, posState * Base.kNumLenSymbols);
            this._counters[posState] = this._tableSize;
        }

        public void UpdateTables(int numPosStates) {
            for (int posState = 0; posState < numPosStates; posState++) {
                UpdateTable(posState);
            }
        }

        public void Encode(com.badlogic.gdx.utils.compression.rangecoder.Encoder rangeEncoder, int symbol, int posState) throws IOException {
            super.Encode(rangeEncoder, symbol, posState);
            int[] iArr = this._counters;
            int i = iArr[posState] - 1;
            iArr[posState] = i;
            if (i == 0) {
                UpdateTable(posState);
            }
        }
    }

    class Optimal {
        public int BackPrev;
        public int BackPrev2;
        public int Backs0;
        public int Backs1;
        public int Backs2;
        public int Backs3;
        public int PosPrev;
        public int PosPrev2;
        public boolean Prev1IsChar;
        public boolean Prev2;
        public int Price;
        public int State;

        Optimal() {
        }

        public void MakeAsChar() {
            this.BackPrev = -1;
            this.Prev1IsChar = false;
        }

        public void MakeAsShortRep() {
            this.BackPrev = 0;
            this.Prev1IsChar = false;
        }

        public boolean IsShortRep() {
            return this.BackPrev == 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void Create() {
        if (this._matchFinder == null) {
            BinTree bt = new BinTree();
            int numHashBytes = 4;
            if (this._matchFinderType == 0) {
                numHashBytes = 2;
            }
            bt.SetType(numHashBytes);
            this._matchFinder = bt;
        }
        this._literalEncoder.Create(this._numLiteralPosStateBits, this._numLiteralContextBits);
        if (this._dictionarySize != this._dictionarySizePrev || this._numFastBytesPrev != this._numFastBytes) {
            this._matchFinder.Create(this._dictionarySize, 4096, this._numFastBytes, 274);
            this._dictionarySizePrev = this._dictionarySize;
            this._numFastBytesPrev = this._numFastBytes;
        }
    }

    public Encoder() {
        for (int i = 0; i < 4096; i++) {
            this._optimum[i] = new Optimal();
        }
        for (int i2 = 0; i2 < 4; i2++) {
            this._posSlotEncoder[i2] = new BitTreeEncoder(6);
        }
    }

    /* access modifiers changed from: package-private */
    public void SetWriteEndMarkerMode(boolean writeEndMarker) {
        this._writeEndMark = writeEndMarker;
    }

    /* access modifiers changed from: package-private */
    public void Init() {
        BaseInit();
        this._rangeEncoder.Init();
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isMatch);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep0Long);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRep);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG0);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG1);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._isRepG2);
        com.badlogic.gdx.utils.compression.rangecoder.Encoder.InitBitModels(this._posEncoders);
        this._literalEncoder.Init();
        for (int i = 0; i < 4; i++) {
            this._posSlotEncoder[i].Init();
        }
        this._lenEncoder.Init(1 << this._posStateBits);
        this._repMatchLenEncoder.Init(1 << this._posStateBits);
        this._posAlignEncoder.Init();
        this._longestMatchWasFound = false;
        this._optimumEndIndex = 0;
        this._optimumCurrentIndex = 0;
        this._additionalOffset = 0;
    }

    /* access modifiers changed from: package-private */
    public int ReadMatchDistances() throws IOException {
        int lenRes = 0;
        this._numDistancePairs = this._matchFinder.GetMatches(this._matchDistances);
        if (this._numDistancePairs > 0 && (lenRes = this._matchDistances[this._numDistancePairs - 2]) == this._numFastBytes) {
            lenRes += this._matchFinder.GetMatchLen(lenRes - 1, this._matchDistances[this._numDistancePairs - 1], 273 - lenRes);
        }
        this._additionalOffset++;
        return lenRes;
    }

    /* access modifiers changed from: package-private */
    public void MovePos(int num) throws IOException {
        if (num > 0) {
            this._matchFinder.Skip(num);
            this._additionalOffset += num;
        }
    }

    /* access modifiers changed from: package-private */
    public int GetRepLen1Price(int state, int posState) {
        return com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep0Long[(state << 4) + posState]);
    }

    /* access modifiers changed from: package-private */
    public int GetPureRepPrice(int repIndex, int state, int posState) {
        if (repIndex == 0) {
            return com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG0[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep0Long[(state << 4) + posState]);
        }
        int price = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG0[state]);
        if (repIndex == 1) {
            return price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRepG1[state]);
        }
        return price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRepG1[state]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice(this._isRepG2[state], repIndex - 2);
    }

    /* access modifiers changed from: package-private */
    public int GetRepPrice(int repIndex, int len, int state, int posState) {
        return GetPureRepPrice(repIndex, state, posState) + this._repMatchLenEncoder.GetPrice(len - 2, posState);
    }

    /* access modifiers changed from: package-private */
    public int GetPosLenPrice(int pos, int len, int posState) {
        int price;
        int lenToPosState = Base.GetLenToPosState(len);
        if (pos < 128) {
            price = this._distancesPrices[(lenToPosState * 128) + pos];
        } else {
            price = this._posSlotPrices[(lenToPosState << 6) + GetPosSlot2(pos)] + this._alignPrices[pos & 15];
        }
        return this._lenEncoder.GetPrice(len - 2, posState) + price;
    }

    /* access modifiers changed from: package-private */
    public int Backward(int cur) {
        this._optimumEndIndex = cur;
        int posMem = this._optimum[cur].PosPrev;
        int backMem = this._optimum[cur].BackPrev;
        do {
            if (this._optimum[cur].Prev1IsChar) {
                this._optimum[posMem].MakeAsChar();
                this._optimum[posMem].PosPrev = posMem - 1;
                if (this._optimum[cur].Prev2) {
                    this._optimum[posMem - 1].Prev1IsChar = false;
                    this._optimum[posMem - 1].PosPrev = this._optimum[cur].PosPrev2;
                    this._optimum[posMem - 1].BackPrev = this._optimum[cur].BackPrev2;
                }
            }
            int posPrev = posMem;
            int backCur = backMem;
            backMem = this._optimum[posPrev].BackPrev;
            posMem = this._optimum[posPrev].PosPrev;
            this._optimum[posPrev].BackPrev = backCur;
            this._optimum[posPrev].PosPrev = cur;
            cur = posPrev;
        } while (cur > 0);
        this.backRes = this._optimum[0].BackPrev;
        this._optimumCurrentIndex = this._optimum[0].PosPrev;
        return this._optimumCurrentIndex;
    }

    /* access modifiers changed from: package-private */
    public int GetOptimum(int position) throws IOException {
        int lenMain;
        int state;
        int pos;
        int state2;
        int shortRepPrice;
        int state3;
        if (this._optimumEndIndex != this._optimumCurrentIndex) {
            int lenRes = this._optimum[this._optimumCurrentIndex].PosPrev - this._optimumCurrentIndex;
            this.backRes = this._optimum[this._optimumCurrentIndex].BackPrev;
            this._optimumCurrentIndex = this._optimum[this._optimumCurrentIndex].PosPrev;
            return lenRes;
        }
        this._optimumEndIndex = 0;
        this._optimumCurrentIndex = 0;
        if (!this._longestMatchWasFound) {
            lenMain = ReadMatchDistances();
        } else {
            lenMain = this._longestMatchLength;
            this._longestMatchWasFound = false;
        }
        int numDistancePairs = this._numDistancePairs;
        int numAvailableBytes = this._matchFinder.GetNumAvailableBytes() + 1;
        if (numAvailableBytes < 2) {
            this.backRes = -1;
            return 1;
        }
        if (numAvailableBytes > 273) {
        }
        int repMaxIndex = 0;
        for (int i = 0; i < 4; i++) {
            this.reps[i] = this._repDistances[i];
            this.repLens[i] = this._matchFinder.GetMatchLen(-1, this.reps[i], Base.kMatchMaxLen);
            if (this.repLens[i] > this.repLens[repMaxIndex]) {
                repMaxIndex = i;
            }
        }
        if (this.repLens[repMaxIndex] >= this._numFastBytes) {
            this.backRes = repMaxIndex;
            int lenRes2 = this.repLens[repMaxIndex];
            MovePos(lenRes2 - 1);
            return lenRes2;
        }
        if (lenMain >= this._numFastBytes) {
            this.backRes = this._matchDistances[numDistancePairs - 1] + 4;
            MovePos(lenMain - 1);
            return lenMain;
        }
        byte currentByte = this._matchFinder.GetIndexByte(-1);
        byte matchByte = this._matchFinder.GetIndexByte(((0 - this._repDistances[0]) - 1) - 1);
        if (lenMain >= 2 || currentByte == matchByte || this.repLens[repMaxIndex] >= 2) {
            this._optimum[0].State = this._state;
            int posState = position & this._posStateMask;
            this._optimum[1].Price = this._literalEncoder.GetSubCoder(position, this._previousByte).GetPrice(!Base.StateIsCharState(this._state), matchByte, currentByte) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(this._state << 4) + posState]);
            this._optimum[1].MakeAsChar();
            int matchPrice = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(this._state << 4) + posState]);
            int repMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[this._state]);
            if (matchByte == currentByte) {
                int shortRepPrice2 = repMatchPrice + GetRepLen1Price(this._state, posState);
                if (shortRepPrice2 < this._optimum[1].Price) {
                    this._optimum[1].Price = shortRepPrice2;
                    this._optimum[1].MakeAsShortRep();
                }
            }
            int lenEnd = lenMain >= this.repLens[repMaxIndex] ? lenMain : this.repLens[repMaxIndex];
            if (lenEnd < 2) {
                this.backRes = this._optimum[1].BackPrev;
                return 1;
            }
            this._optimum[1].PosPrev = 0;
            this._optimum[0].Backs0 = this.reps[0];
            this._optimum[0].Backs1 = this.reps[1];
            this._optimum[0].Backs2 = this.reps[2];
            this._optimum[0].Backs3 = this.reps[3];
            int len = lenEnd;
            while (true) {
                int len2 = len - 1;
                this._optimum[len].Price = kIfinityPrice;
                if (len2 < 2) {
                    break;
                }
                len = len2;
            }
            for (int i2 = 0; i2 < 4; i2++) {
                int repLen = this.repLens[i2];
                if (repLen >= 2) {
                    int price = repMatchPrice + GetPureRepPrice(i2, this._state, posState);
                    do {
                        int curAndLenPrice = price + this._repMatchLenEncoder.GetPrice(repLen - 2, posState);
                        Optimal optimum = this._optimum[repLen];
                        if (curAndLenPrice < optimum.Price) {
                            optimum.Price = curAndLenPrice;
                            optimum.PosPrev = 0;
                            optimum.BackPrev = i2;
                            optimum.Prev1IsChar = false;
                        }
                        repLen--;
                    } while (repLen >= 2);
                }
            }
            int normalMatchPrice = matchPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep[this._state]);
            int len3 = this.repLens[0] >= 2 ? this.repLens[0] + 1 : 2;
            if (len3 <= lenMain) {
                int offs = 0;
                while (len3 > this._matchDistances[offs]) {
                    offs += 2;
                }
                while (true) {
                    int distance = this._matchDistances[offs + 1];
                    int curAndLenPrice2 = normalMatchPrice + GetPosLenPrice(distance, len3, posState);
                    Optimal optimum2 = this._optimum[len3];
                    if (curAndLenPrice2 < optimum2.Price) {
                        optimum2.Price = curAndLenPrice2;
                        optimum2.PosPrev = 0;
                        optimum2.BackPrev = distance + 4;
                        optimum2.Prev1IsChar = false;
                    }
                    if (len3 == this._matchDistances[offs]) {
                        offs += 2;
                        if (offs == numDistancePairs) {
                            break;
                        }
                    }
                    len3++;
                }
            }
            int cur = 0;
            while (true) {
                cur++;
                if (cur == lenEnd) {
                    return Backward(cur);
                }
                int newLen = ReadMatchDistances();
                int numDistancePairs2 = this._numDistancePairs;
                if (newLen >= this._numFastBytes) {
                    this._longestMatchLength = newLen;
                    this._longestMatchWasFound = true;
                    return Backward(cur);
                }
                position++;
                int posPrev = this._optimum[cur].PosPrev;
                if (this._optimum[cur].Prev1IsChar) {
                    posPrev--;
                    if (this._optimum[cur].Prev2) {
                        int state4 = this._optimum[this._optimum[cur].PosPrev2].State;
                        if (this._optimum[cur].BackPrev2 < 4) {
                            state3 = Base.StateUpdateRep(state4);
                        } else {
                            state3 = Base.StateUpdateMatch(state4);
                        }
                    } else {
                        state3 = this._optimum[posPrev].State;
                    }
                    state = Base.StateUpdateChar(state3);
                } else {
                    state = this._optimum[posPrev].State;
                }
                if (posPrev != cur - 1) {
                    if (!this._optimum[cur].Prev1IsChar || !this._optimum[cur].Prev2) {
                        pos = this._optimum[cur].BackPrev;
                        if (pos < 4) {
                            state2 = Base.StateUpdateRep(state);
                        } else {
                            state2 = Base.StateUpdateMatch(state);
                        }
                    } else {
                        posPrev = this._optimum[cur].PosPrev2;
                        pos = this._optimum[cur].BackPrev2;
                        state2 = Base.StateUpdateRep(state);
                    }
                    Optimal opt = this._optimum[posPrev];
                    if (pos >= 4) {
                        this.reps[0] = pos - 4;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs1;
                        this.reps[3] = opt.Backs2;
                    } else if (pos == 0) {
                        this.reps[0] = opt.Backs0;
                        this.reps[1] = opt.Backs1;
                        this.reps[2] = opt.Backs2;
                        this.reps[3] = opt.Backs3;
                    } else if (pos == 1) {
                        this.reps[0] = opt.Backs1;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs2;
                        this.reps[3] = opt.Backs3;
                    } else if (pos == 2) {
                        this.reps[0] = opt.Backs2;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs1;
                        this.reps[3] = opt.Backs3;
                    } else {
                        this.reps[0] = opt.Backs3;
                        this.reps[1] = opt.Backs0;
                        this.reps[2] = opt.Backs1;
                        this.reps[3] = opt.Backs2;
                    }
                } else if (this._optimum[cur].IsShortRep()) {
                    state2 = Base.StateUpdateShortRep(state);
                } else {
                    state2 = Base.StateUpdateChar(state);
                }
                this._optimum[cur].State = state2;
                this._optimum[cur].Backs0 = this.reps[0];
                this._optimum[cur].Backs1 = this.reps[1];
                this._optimum[cur].Backs2 = this.reps[2];
                this._optimum[cur].Backs3 = this.reps[3];
                int curPrice = this._optimum[cur].Price;
                byte currentByte2 = this._matchFinder.GetIndexByte(-1);
                byte matchByte2 = this._matchFinder.GetIndexByte(((0 - this.reps[0]) - 1) - 1);
                int posState2 = position & this._posStateMask;
                int curAnd1Price = curPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state2 << 4) + posState2]) + this._literalEncoder.GetSubCoder(position, this._matchFinder.GetIndexByte(-2)).GetPrice(!Base.StateIsCharState(state2), matchByte2, currentByte2);
                Optimal nextOptimum = this._optimum[cur + 1];
                boolean nextIsChar = false;
                if (curAnd1Price < nextOptimum.Price) {
                    nextOptimum.Price = curAnd1Price;
                    nextOptimum.PosPrev = cur;
                    nextOptimum.MakeAsChar();
                    nextIsChar = true;
                }
                int matchPrice2 = curPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state2 << 4) + posState2]);
                int repMatchPrice2 = matchPrice2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state2]);
                if (matchByte2 == currentByte2 && ((nextOptimum.PosPrev >= cur || nextOptimum.BackPrev != 0) && (shortRepPrice = repMatchPrice2 + GetRepLen1Price(state2, posState2)) <= nextOptimum.Price)) {
                    nextOptimum.Price = shortRepPrice;
                    nextOptimum.PosPrev = cur;
                    nextOptimum.MakeAsShortRep();
                    nextIsChar = true;
                }
                int numAvailableBytesFull = Math.min(4095 - cur, this._matchFinder.GetNumAvailableBytes() + 1);
                int numAvailableBytes2 = numAvailableBytesFull;
                if (numAvailableBytes2 >= 2) {
                    if (numAvailableBytes2 > this._numFastBytes) {
                        numAvailableBytes2 = this._numFastBytes;
                    }
                    if (!nextIsChar && matchByte2 != currentByte2) {
                        int lenTest2 = this._matchFinder.GetMatchLen(0, this.reps[0], Math.min(numAvailableBytesFull - 1, this._numFastBytes));
                        if (lenTest2 >= 2) {
                            int state22 = Base.StateUpdateChar(state2);
                            int posStateNext = (position + 1) & this._posStateMask;
                            int nextRepMatchPrice = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state22 << 4) + posStateNext]) + curAnd1Price + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state22]);
                            int offset = cur + 1 + lenTest2;
                            while (lenEnd < offset) {
                                lenEnd++;
                                this._optimum[lenEnd].Price = kIfinityPrice;
                            }
                            int curAndLenPrice3 = nextRepMatchPrice + GetRepPrice(0, lenTest2, state22, posStateNext);
                            Optimal optimum3 = this._optimum[offset];
                            if (curAndLenPrice3 < optimum3.Price) {
                                optimum3.Price = curAndLenPrice3;
                                optimum3.PosPrev = cur + 1;
                                optimum3.BackPrev = 0;
                                optimum3.Prev1IsChar = true;
                                optimum3.Prev2 = false;
                            }
                        }
                    }
                    int startLen = 2;
                    for (int repIndex = 0; repIndex < 4; repIndex++) {
                        int lenTest = this._matchFinder.GetMatchLen(-1, this.reps[repIndex], numAvailableBytes2);
                        if (lenTest >= 2) {
                            int lenTestTemp = lenTest;
                            while (true) {
                                if (lenEnd < cur + lenTest) {
                                    lenEnd++;
                                    this._optimum[lenEnd].Price = kIfinityPrice;
                                } else {
                                    int curAndLenPrice4 = repMatchPrice2 + GetRepPrice(repIndex, lenTest, state2, posState2);
                                    Optimal optimum4 = this._optimum[cur + lenTest];
                                    if (curAndLenPrice4 < optimum4.Price) {
                                        optimum4.Price = curAndLenPrice4;
                                        optimum4.PosPrev = cur;
                                        optimum4.BackPrev = repIndex;
                                        optimum4.Prev1IsChar = false;
                                    }
                                    lenTest--;
                                    if (lenTest < 2) {
                                        break;
                                    }
                                }
                            }
                            int lenTest3 = lenTestTemp;
                            if (repIndex == 0) {
                                startLen = lenTest3 + 1;
                            }
                            if (lenTest3 < numAvailableBytesFull) {
                                int lenTest22 = this._matchFinder.GetMatchLen(lenTest3, this.reps[repIndex], Math.min((numAvailableBytesFull - 1) - lenTest3, this._numFastBytes));
                                if (lenTest22 >= 2) {
                                    int state23 = Base.StateUpdateRep(state2);
                                    int curAndLenCharPrice = GetRepPrice(repIndex, lenTest3, state2, posState2) + repMatchPrice2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state23 << 4) + ((position + lenTest3) & this._posStateMask)]) + this._literalEncoder.GetSubCoder(position + lenTest3, this._matchFinder.GetIndexByte((lenTest3 - 1) - 1)).GetPrice(true, this._matchFinder.GetIndexByte((lenTest3 - 1) - (this.reps[repIndex] + 1)), this._matchFinder.GetIndexByte(lenTest3 - 1));
                                    int state24 = Base.StateUpdateChar(state23);
                                    int posStateNext2 = (position + lenTest3 + 1) & this._posStateMask;
                                    int nextRepMatchPrice2 = curAndLenCharPrice + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state24 << 4) + posStateNext2]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state24]);
                                    int offset2 = lenTest3 + 1 + lenTest22;
                                    while (lenEnd < cur + offset2) {
                                        lenEnd++;
                                        this._optimum[lenEnd].Price = kIfinityPrice;
                                    }
                                    int curAndLenPrice5 = nextRepMatchPrice2 + GetRepPrice(0, lenTest22, state24, posStateNext2);
                                    Optimal optimum5 = this._optimum[cur + offset2];
                                    if (curAndLenPrice5 < optimum5.Price) {
                                        optimum5.Price = curAndLenPrice5;
                                        optimum5.PosPrev = cur + lenTest3 + 1;
                                        optimum5.BackPrev = 0;
                                        optimum5.Prev1IsChar = true;
                                        optimum5.Prev2 = true;
                                        optimum5.PosPrev2 = cur;
                                        optimum5.BackPrev2 = repIndex;
                                    }
                                }
                            }
                        }
                    }
                    if (newLen > numAvailableBytes2) {
                        newLen = numAvailableBytes2;
                        int numDistancePairs3 = 0;
                        while (newLen > this._matchDistances[numDistancePairs3]) {
                            numDistancePairs3 += 2;
                        }
                        this._matchDistances[numDistancePairs3] = newLen;
                        numDistancePairs2 = numDistancePairs3 + 2;
                    }
                    if (newLen >= startLen) {
                        int normalMatchPrice2 = matchPrice2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isRep[state2]);
                        while (lenEnd < cur + newLen) {
                            lenEnd++;
                            this._optimum[lenEnd].Price = kIfinityPrice;
                        }
                        int offs2 = 0;
                        while (startLen > this._matchDistances[offs2]) {
                            offs2 += 2;
                        }
                        int lenTest4 = startLen;
                        while (true) {
                            int curBack = this._matchDistances[offs2 + 1];
                            int curAndLenPrice6 = normalMatchPrice2 + GetPosLenPrice(curBack, lenTest4, posState2);
                            Optimal optimum6 = this._optimum[cur + lenTest4];
                            if (curAndLenPrice6 < optimum6.Price) {
                                optimum6.Price = curAndLenPrice6;
                                optimum6.PosPrev = cur;
                                optimum6.BackPrev = curBack + 4;
                                optimum6.Prev1IsChar = false;
                            }
                            if (lenTest4 == this._matchDistances[offs2]) {
                                if (lenTest4 < numAvailableBytesFull) {
                                    int lenTest23 = this._matchFinder.GetMatchLen(lenTest4, curBack, Math.min((numAvailableBytesFull - 1) - lenTest4, this._numFastBytes));
                                    if (lenTest23 >= 2) {
                                        int state25 = Base.StateUpdateMatch(state2);
                                        int curAndLenCharPrice2 = com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice0(this._isMatch[(state25 << 4) + ((position + lenTest4) & this._posStateMask)]) + curAndLenPrice6 + this._literalEncoder.GetSubCoder(position + lenTest4, this._matchFinder.GetIndexByte((lenTest4 - 1) - 1)).GetPrice(true, this._matchFinder.GetIndexByte((lenTest4 - (curBack + 1)) - 1), this._matchFinder.GetIndexByte(lenTest4 - 1));
                                        int state26 = Base.StateUpdateChar(state25);
                                        int posStateNext3 = (position + lenTest4 + 1) & this._posStateMask;
                                        int nextRepMatchPrice3 = curAndLenCharPrice2 + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isMatch[(state26 << 4) + posStateNext3]) + com.badlogic.gdx.utils.compression.rangecoder.Encoder.GetPrice1(this._isRep[state26]);
                                        int offset3 = lenTest4 + 1 + lenTest23;
                                        while (lenEnd < cur + offset3) {
                                            lenEnd++;
                                            this._optimum[lenEnd].Price = kIfinityPrice;
                                        }
                                        int curAndLenPrice7 = nextRepMatchPrice3 + GetRepPrice(0, lenTest23, state26, posStateNext3);
                                        Optimal optimum7 = this._optimum[cur + offset3];
                                        if (curAndLenPrice7 < optimum7.Price) {
                                            optimum7.Price = curAndLenPrice7;
                                            optimum7.PosPrev = cur + lenTest4 + 1;
                                            optimum7.BackPrev = 0;
                                            optimum7.Prev1IsChar = true;
                                            optimum7.Prev2 = true;
                                            optimum7.PosPrev2 = cur;
                                            optimum7.BackPrev2 = curBack + 4;
                                        }
                                    }
                                }
                                offs2 += 2;
                                if (offs2 == numDistancePairs2) {
                                    break;
                                }
                            }
                            lenTest4++;
                        }
                    }
                }
            }
        } else {
            this.backRes = -1;
            return 1;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean ChangePair(int smallDist, int bigDist) {
        return smallDist < 33554432 && bigDist >= (smallDist << 7);
    }

    /* access modifiers changed from: package-private */
    public void WriteEndMarker(int posState) throws IOException {
        if (this._writeEndMark) {
            this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + posState, 1);
            this._rangeEncoder.Encode(this._isRep, this._state, 0);
            this._state = Base.StateUpdateMatch(this._state);
            this._lenEncoder.Encode(this._rangeEncoder, 0, posState);
            this._posSlotEncoder[Base.GetLenToPosState(2)].Encode(this._rangeEncoder, 63);
            int i = 1073741824 - 1;
            this._rangeEncoder.EncodeDirectBits(67108863, 26);
            this._posAlignEncoder.ReverseEncode(this._rangeEncoder, 15);
        }
    }

    /* access modifiers changed from: package-private */
    public void Flush(int nowPos) throws IOException {
        ReleaseMFStream();
        WriteEndMarker(this._posStateMask & nowPos);
        this._rangeEncoder.FlushData();
        this._rangeEncoder.FlushStream();
    }

    public void CodeOneBlock(long[] inSize, long[] outSize, boolean[] finished2) throws IOException {
        inSize[0] = 0;
        outSize[0] = 0;
        finished2[0] = true;
        if (this._inStream != null) {
            this._matchFinder.SetStream(this._inStream);
            this._matchFinder.Init();
            this._needReleaseMFStream = true;
            this._inStream = null;
        }
        if (!this._finished) {
            this._finished = true;
            long progressPosValuePrev = this.nowPos64;
            if (this.nowPos64 == 0) {
                if (this._matchFinder.GetNumAvailableBytes() == 0) {
                    Flush((int) this.nowPos64);
                    return;
                }
                ReadMatchDistances();
                this._rangeEncoder.Encode(this._isMatch, (this._state << 4) + (((int) this.nowPos64) & this._posStateMask), 0);
                this._state = Base.StateUpdateChar(this._state);
                byte curByte = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                this._literalEncoder.GetSubCoder((int) this.nowPos64, this._previousByte).Encode(this._rangeEncoder, curByte);
                this._previousByte = curByte;
                this._additionalOffset--;
                this.nowPos64++;
            }
            if (this._matchFinder.GetNumAvailableBytes() == 0) {
                Flush((int) this.nowPos64);
                return;
            }
            while (true) {
                int len = GetOptimum((int) this.nowPos64);
                int pos = this.backRes;
                int posState = ((int) this.nowPos64) & this._posStateMask;
                int complexState = (this._state << 4) + posState;
                if (len == 1 && pos == -1) {
                    this._rangeEncoder.Encode(this._isMatch, complexState, 0);
                    byte curByte2 = this._matchFinder.GetIndexByte(0 - this._additionalOffset);
                    LiteralEncoder.Encoder2 subCoder = this._literalEncoder.GetSubCoder((int) this.nowPos64, this._previousByte);
                    if (!Base.StateIsCharState(this._state)) {
                        subCoder.EncodeMatched(this._rangeEncoder, this._matchFinder.GetIndexByte(((0 - this._repDistances[0]) - 1) - this._additionalOffset), curByte2);
                    } else {
                        subCoder.Encode(this._rangeEncoder, curByte2);
                    }
                    this._previousByte = curByte2;
                    this._state = Base.StateUpdateChar(this._state);
                } else {
                    this._rangeEncoder.Encode(this._isMatch, complexState, 1);
                    if (pos < 4) {
                        this._rangeEncoder.Encode(this._isRep, this._state, 1);
                        if (pos == 0) {
                            this._rangeEncoder.Encode(this._isRepG0, this._state, 0);
                            if (len == 1) {
                                this._rangeEncoder.Encode(this._isRep0Long, complexState, 0);
                            } else {
                                this._rangeEncoder.Encode(this._isRep0Long, complexState, 1);
                            }
                        } else {
                            this._rangeEncoder.Encode(this._isRepG0, this._state, 1);
                            if (pos == 1) {
                                this._rangeEncoder.Encode(this._isRepG1, this._state, 0);
                            } else {
                                this._rangeEncoder.Encode(this._isRepG1, this._state, 1);
                                this._rangeEncoder.Encode(this._isRepG2, this._state, pos - 2);
                            }
                        }
                        if (len == 1) {
                            this._state = Base.StateUpdateShortRep(this._state);
                        } else {
                            this._repMatchLenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                            this._state = Base.StateUpdateRep(this._state);
                        }
                        int distance = this._repDistances[pos];
                        if (pos != 0) {
                            for (int i = pos; i >= 1; i--) {
                                this._repDistances[i] = this._repDistances[i - 1];
                            }
                            this._repDistances[0] = distance;
                        }
                    } else {
                        this._rangeEncoder.Encode(this._isRep, this._state, 0);
                        this._state = Base.StateUpdateMatch(this._state);
                        this._lenEncoder.Encode(this._rangeEncoder, len - 2, posState);
                        int pos2 = pos - 4;
                        int posSlot = GetPosSlot(pos2);
                        this._posSlotEncoder[Base.GetLenToPosState(len)].Encode(this._rangeEncoder, posSlot);
                        if (posSlot >= 4) {
                            int footerBits = (posSlot >> 1) - 1;
                            int baseVal = ((posSlot & 1) | 2) << footerBits;
                            int posReduced = pos2 - baseVal;
                            if (posSlot < 14) {
                                BitTreeEncoder.ReverseEncode(this._posEncoders, (baseVal - posSlot) - 1, this._rangeEncoder, footerBits, posReduced);
                            } else {
                                this._rangeEncoder.EncodeDirectBits(posReduced >> 4, footerBits - 4);
                                this._posAlignEncoder.ReverseEncode(this._rangeEncoder, posReduced & 15);
                                this._alignPriceCount++;
                            }
                        }
                        int distance2 = pos2;
                        for (int i2 = 3; i2 >= 1; i2--) {
                            this._repDistances[i2] = this._repDistances[i2 - 1];
                        }
                        this._repDistances[0] = distance2;
                        this._matchPriceCount++;
                    }
                    this._previousByte = this._matchFinder.GetIndexByte((len - 1) - this._additionalOffset);
                }
                this._additionalOffset -= len;
                this.nowPos64 += (long) len;
                if (this._additionalOffset == 0) {
                    if (this._matchPriceCount >= 128) {
                        FillDistancesPrices();
                    }
                    if (this._alignPriceCount >= 16) {
                        FillAlignPrices();
                    }
                    inSize[0] = this.nowPos64;
                    outSize[0] = this._rangeEncoder.GetProcessedSizeAdd();
                    if (this._matchFinder.GetNumAvailableBytes() == 0) {
                        Flush((int) this.nowPos64);
                        return;
                    } else if (this.nowPos64 - progressPosValuePrev >= 4096) {
                        this._finished = false;
                        finished2[0] = false;
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ReleaseMFStream() {
        if (this._matchFinder != null && this._needReleaseMFStream) {
            this._matchFinder.ReleaseStream();
            this._needReleaseMFStream = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void SetOutStream(OutputStream outStream) {
        this._rangeEncoder.SetStream(outStream);
    }

    /* access modifiers changed from: package-private */
    public void ReleaseOutStream() {
        this._rangeEncoder.ReleaseStream();
    }

    /* access modifiers changed from: package-private */
    public void ReleaseStreams() {
        ReleaseMFStream();
        ReleaseOutStream();
    }

    /* access modifiers changed from: package-private */
    public void SetStreams(InputStream inStream, OutputStream outStream, long inSize, long outSize) {
        this._inStream = inStream;
        this._finished = false;
        Create();
        SetOutStream(outStream);
        Init();
        FillDistancesPrices();
        FillAlignPrices();
        this._lenEncoder.SetTableSize((this._numFastBytes + 1) - 2);
        this._lenEncoder.UpdateTables(1 << this._posStateBits);
        this._repMatchLenEncoder.SetTableSize((this._numFastBytes + 1) - 2);
        this._repMatchLenEncoder.UpdateTables(1 << this._posStateBits);
        this.nowPos64 = 0;
    }

    public void Code(InputStream inStream, OutputStream outStream, long inSize, long outSize, ICodeProgress progress) throws IOException {
        this._needReleaseMFStream = false;
        try {
            SetStreams(inStream, outStream, inSize, outSize);
            while (true) {
                CodeOneBlock(this.processedInSize, this.processedOutSize, this.finished);
                if (!this.finished[0]) {
                    if (progress != null) {
                        progress.SetProgress(this.processedInSize[0], this.processedOutSize[0]);
                    }
                } else {
                    return;
                }
            }
        } finally {
            ReleaseStreams();
        }
    }

    public void WriteCoderProperties(OutputStream outStream) throws IOException {
        this.properties[0] = (byte) ((((this._posStateBits * 5) + this._numLiteralPosStateBits) * 9) + this._numLiteralContextBits);
        for (int i = 0; i < 4; i++) {
            this.properties[i + 1] = (byte) (this._dictionarySize >> (i * 8));
        }
        outStream.write(this.properties, 0, 5);
    }

    /* access modifiers changed from: package-private */
    public void FillDistancesPrices() {
        for (int i = 4; i < 128; i++) {
            int posSlot = GetPosSlot(i);
            int footerBits = (posSlot >> 1) - 1;
            int baseVal = ((posSlot & 1) | 2) << footerBits;
            this.tempPrices[i] = BitTreeEncoder.ReverseGetPrice(this._posEncoders, (baseVal - posSlot) - 1, footerBits, i - baseVal);
        }
        for (int lenToPosState = 0; lenToPosState < 4; lenToPosState++) {
            BitTreeEncoder encoder = this._posSlotEncoder[lenToPosState];
            int st = lenToPosState << 6;
            for (int posSlot2 = 0; posSlot2 < this._distTableSize; posSlot2++) {
                this._posSlotPrices[st + posSlot2] = encoder.GetPrice(posSlot2);
            }
            for (int posSlot3 = 14; posSlot3 < this._distTableSize; posSlot3++) {
                int[] iArr = this._posSlotPrices;
                int i2 = st + posSlot3;
                iArr[i2] = iArr[i2] + ((((posSlot3 >> 1) - 1) - 4) << 6);
            }
            int st2 = lenToPosState * 128;
            int i3 = 0;
            while (i3 < 4) {
                this._distancesPrices[st2 + i3] = this._posSlotPrices[st + i3];
                i3++;
            }
            while (i3 < 128) {
                this._distancesPrices[st2 + i3] = this._posSlotPrices[GetPosSlot(i3) + st] + this.tempPrices[i3];
                i3++;
            }
        }
        this._matchPriceCount = 0;
    }

    /* access modifiers changed from: package-private */
    public void FillAlignPrices() {
        for (int i = 0; i < 16; i++) {
            this._alignPrices[i] = this._posAlignEncoder.ReverseGetPrice(i);
        }
        this._alignPriceCount = 0;
    }

    public boolean SetAlgorithm(int algorithm) {
        return true;
    }

    public boolean SetDictionarySize(int dictionarySize) {
        if (dictionarySize < 1 || dictionarySize > 536870912) {
            return false;
        }
        this._dictionarySize = dictionarySize;
        int dicLogSize = 0;
        while (dictionarySize > (1 << dicLogSize)) {
            dicLogSize++;
        }
        this._distTableSize = dicLogSize * 2;
        return true;
    }

    public boolean SetNumFastBytes(int numFastBytes) {
        if (numFastBytes < 5 || numFastBytes > 273) {
            return false;
        }
        this._numFastBytes = numFastBytes;
        return true;
    }

    public boolean SetMatchFinder(int matchFinderIndex) {
        if (matchFinderIndex < 0 || matchFinderIndex > 2) {
            return false;
        }
        int matchFinderIndexPrev = this._matchFinderType;
        this._matchFinderType = matchFinderIndex;
        if (!(this._matchFinder == null || matchFinderIndexPrev == this._matchFinderType)) {
            this._dictionarySizePrev = -1;
            this._matchFinder = null;
        }
        return true;
    }

    public boolean SetLcLpPb(int lc, int lp, int pb) {
        if (lp < 0 || lp > 4 || lc < 0 || lc > 8 || pb < 0 || pb > 4) {
            return false;
        }
        this._numLiteralPosStateBits = lp;
        this._numLiteralContextBits = lc;
        this._posStateBits = pb;
        this._posStateMask = (1 << this._posStateBits) - 1;
        return true;
    }

    public void SetEndMarkerMode(boolean endMarkerMode) {
        this._writeEndMark = endMarkerMode;
    }
}
