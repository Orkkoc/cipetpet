package com.badlogic.gdx;

import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        copyStream(input, output, G3dConstants.KEYFRAMED_MODEL);
    }

    public static void copyStream(InputStream input, OutputStream output, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        while (true) {
            int bytesRead = input.read(buffer);
            if (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
            } else {
                return;
            }
        }
    }
}
