package com.badlogic.gdx;

import java.util.Map;

public interface Preferences {
    void clear();

    boolean contains(String str);

    void flush();

    Map<String, ?> get();

    boolean getBoolean(String str);

    boolean getBoolean(String str, boolean z);

    float getFloat(String str);

    float getFloat(String str, float f);

    int getInteger(String str);

    int getInteger(String str, int i);

    long getLong(String str);

    long getLong(String str, long j);

    String getString(String str);

    String getString(String str, String str2);

    void put(Map<String, ?> map);

    void putBoolean(String str, boolean z);

    void putFloat(String str, float f);

    void putInteger(String str, int i);

    void putLong(String str, long j);

    void putString(String str, String str2);

    void remove(String str);
}
