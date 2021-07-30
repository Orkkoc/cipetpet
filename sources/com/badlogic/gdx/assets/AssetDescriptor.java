package com.badlogic.gdx.assets;

public class AssetDescriptor<T> {
    public final String fileName;
    public final AssetLoaderParameters params;
    public final Class<T> type;

    public AssetDescriptor(String fileName2, Class<T> assetType) {
        this(fileName2, assetType, (AssetLoaderParameters) null);
    }

    public AssetDescriptor(String fileName2, Class<T> assetType, AssetLoaderParameters<T> params2) {
        this.fileName = fileName2.replaceAll("\\\\", "/");
        this.type = assetType;
        this.params = params2;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.fileName);
        buffer.append(", ");
        buffer.append(this.type.getName());
        return buffer.toString();
    }
}
