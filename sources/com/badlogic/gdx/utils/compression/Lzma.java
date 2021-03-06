package com.badlogic.gdx.utils.compression;

import com.badlogic.gdx.utils.compression.lzma.Decoder;
import com.badlogic.gdx.utils.compression.lzma.Encoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Lzma {

    static class CommandLine {
        public static final int kBenchmak = 2;
        public static final int kDecode = 1;
        public static final int kEncode = 0;
        public int Algorithm = 2;
        public int Command = -1;
        public int DictionarySize = 8388608;
        public boolean DictionarySizeIsDefined = false;
        public boolean Eos = false;

        /* renamed from: Fb */
        public int f189Fb = 128;
        public boolean FbIsDefined = false;
        public String InFile;

        /* renamed from: Lc */
        public int f190Lc = 3;

        /* renamed from: Lp */
        public int f191Lp = 0;
        public int MatchFinder = 1;
        public int NumBenchmarkPasses = 10;
        public String OutFile;

        /* renamed from: Pb */
        public int f192Pb = 2;

        CommandLine() {
        }
    }

    public static void compress(InputStream in, OutputStream out) throws IOException {
        long fileSize;
        CommandLine params = new CommandLine();
        boolean eos = false;
        if (params.Eos) {
            eos = true;
        }
        Encoder encoder = new Encoder();
        if (!encoder.SetAlgorithm(params.Algorithm)) {
            throw new RuntimeException("Incorrect compression mode");
        } else if (!encoder.SetDictionarySize(params.DictionarySize)) {
            throw new RuntimeException("Incorrect dictionary size");
        } else if (!encoder.SetNumFastBytes(params.f189Fb)) {
            throw new RuntimeException("Incorrect -fb value");
        } else if (!encoder.SetMatchFinder(params.MatchFinder)) {
            throw new RuntimeException("Incorrect -mf value");
        } else if (!encoder.SetLcLpPb(params.f190Lc, params.f191Lp, params.f192Pb)) {
            throw new RuntimeException("Incorrect -lc or -lp or -pb value");
        } else {
            encoder.SetEndMarkerMode(eos);
            encoder.WriteCoderProperties(out);
            if (eos) {
                fileSize = -1;
            } else {
                fileSize = (long) in.available();
                if (fileSize == 0) {
                    fileSize = -1;
                }
            }
            for (int i = 0; i < 8; i++) {
                out.write(((int) (fileSize >>> (i * 8))) & 255);
            }
            encoder.Code(in, out, -1, -1, (ICodeProgress) null);
        }
    }

    public static void decompress(InputStream in, OutputStream out) throws IOException {
        byte[] properties = new byte[5];
        if (in.read(properties, 0, 5) != 5) {
            throw new RuntimeException("input .lzma file is too short");
        }
        Decoder decoder = new Decoder();
        if (!decoder.SetDecoderProperties(properties)) {
            throw new RuntimeException("Incorrect stream properties");
        }
        long outSize = 0;
        for (int i = 0; i < 8; i++) {
            int v = in.read();
            if (v < 0) {
                throw new RuntimeException("Can't read stream size");
            }
            outSize |= ((long) v) << (i * 8);
        }
        if (!decoder.Code(in, out, outSize)) {
            throw new RuntimeException("Error in data stream");
        }
    }
}
