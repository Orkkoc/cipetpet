package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PolygonRegion {
    private float[] localVertices;
    private final TextureRegion region;
    private float[] texCoords;

    public PolygonRegion(TextureRegion region2, FileHandle file) {
        this.region = region2;
        if (file == null) {
            throw new IllegalArgumentException("region cannot be null.");
        }
        loadPolygonDefinition(file);
    }

    public PolygonRegion(TextureRegion region2, float[] vertices) {
        this.region = region2;
        EarClippingTriangulator ect = new EarClippingTriangulator();
        List<Vector2> polygonVectors = new ArrayList<>();
        for (int i = 0; i < vertices.length; i += 2) {
            polygonVectors.add(new Vector2(vertices[i], vertices[i + 1]));
        }
        List<Vector2> triangulatedVectors = ect.computeTriangles(polygonVectors);
        this.localVertices = new float[(triangulatedVectors.size() * 2)];
        this.texCoords = new float[(triangulatedVectors.size() * 2)];
        float f = region2.f107u2 - region2.f106u;
        float f2 = region2.f109v2 - region2.f108v;
        for (int i2 = 0; i2 < triangulatedVectors.size(); i2++) {
            this.localVertices[i2 * 2] = triangulatedVectors.get(i2).f165x;
            this.localVertices[(i2 * 2) + 1] = triangulatedVectors.get(i2).f166y;
            this.texCoords[i2 * 2] = region2.f106u + ((this.localVertices[i2 * 2] - ((float) region2.getRegionX())) / ((float) region2.getRegionWidth()));
            this.texCoords[(i2 * 2) + 1] = region2.f108v + (1.0f - ((this.localVertices[(i2 * 2) + 1] - ((float) region2.getRegionY())) / ((float) region2.getRegionHeight())));
        }
    }

    private void loadPolygonDefinition(FileHandle file) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 64);
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    try {
                        reader.close();
                        return;
                    } catch (IOException e) {
                        return;
                    }
                } else if (line.startsWith("v")) {
                    String[] vertices = line.substring(1).trim().split(",");
                    this.localVertices = new float[vertices.length];
                    for (int i = 0; i < vertices.length; i += 2) {
                        this.localVertices[i] = Float.parseFloat(vertices[i]);
                        this.localVertices[i + 1] = Float.parseFloat(vertices[i + 1]);
                    }
                } else if (line.startsWith("u")) {
                    String[] texCoords2 = line.substring(1).trim().split(",");
                    float[] localTexCoords = new float[texCoords2.length];
                    for (int i2 = 0; i2 < texCoords2.length; i2 += 2) {
                        localTexCoords[i2] = Float.parseFloat(texCoords2[i2]);
                        localTexCoords[i2 + 1] = Float.parseFloat(texCoords2[i2 + 1]);
                    }
                    this.texCoords = calculateAtlasTexCoords(localTexCoords);
                }
            } catch (IOException e2) {
                throw new GdxRuntimeException("Error reading polygon shape file: " + file);
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (IOException e3) {
                }
                throw th;
            }
        }
    }

    private float[] calculateAtlasTexCoords(float[] localTexCoords) {
        float uvWidth = this.region.f107u2 - this.region.f106u;
        float uvHeight = this.region.f109v2 - this.region.f108v;
        for (int i = 0; i < localTexCoords.length; i += 2) {
            localTexCoords[i] = this.region.f106u + (localTexCoords[i] * uvWidth);
            localTexCoords[i + 1] = this.region.f108v + (localTexCoords[i + 1] * uvHeight);
        }
        return localTexCoords;
    }

    public float[] getLocalVertices() {
        return this.localVertices;
    }

    public float[] getTextureCoords() {
        return this.texCoords;
    }

    public TextureRegion getRegion() {
        return this.region;
    }
}
