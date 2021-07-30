package com.badlogic.gdx.backends.android;

import android.content.SharedPreferences;
import com.badlogic.gdx.Preferences;
import java.util.Map;

public class AndroidPreferences implements Preferences {
    SharedPreferences.Editor editor;
    SharedPreferences sharedPrefs;

    public AndroidPreferences(SharedPreferences preferences) {
        this.sharedPrefs = preferences;
    }

    public void putBoolean(String key, boolean val) {
        edit();
        this.editor.putBoolean(key, val);
    }

    public void putInteger(String key, int val) {
        edit();
        this.editor.putInt(key, val);
    }

    public void putLong(String key, long val) {
        edit();
        this.editor.putLong(key, val);
    }

    public void putFloat(String key, float val) {
        edit();
        this.editor.putFloat(key, val);
    }

    public void putString(String key, String val) {
        edit();
        this.editor.putString(key, val);
    }

    public void put(Map<String, ?> vals) {
        edit();
        for (Map.Entry<String, ?> val : vals.entrySet()) {
            if (val.getValue() instanceof Boolean) {
                putBoolean(val.getKey(), ((Boolean) val.getValue()).booleanValue());
            }
            if (val.getValue() instanceof Integer) {
                putInteger(val.getKey(), ((Integer) val.getValue()).intValue());
            }
            if (val.getValue() instanceof Long) {
                putLong(val.getKey(), ((Long) val.getValue()).longValue());
            }
            if (val.getValue() instanceof String) {
                putString(val.getKey(), (String) val.getValue());
            }
            if (val.getValue() instanceof Float) {
                putFloat(val.getKey(), ((Float) val.getValue()).floatValue());
            }
        }
    }

    public boolean getBoolean(String key) {
        return this.sharedPrefs.getBoolean(key, false);
    }

    public int getInteger(String key) {
        return this.sharedPrefs.getInt(key, 0);
    }

    public long getLong(String key) {
        return this.sharedPrefs.getLong(key, 0);
    }

    public float getFloat(String key) {
        return this.sharedPrefs.getFloat(key, 0.0f);
    }

    public String getString(String key) {
        return this.sharedPrefs.getString(key, "");
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this.sharedPrefs.getBoolean(key, defValue);
    }

    public int getInteger(String key, int defValue) {
        return this.sharedPrefs.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return this.sharedPrefs.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return this.sharedPrefs.getFloat(key, defValue);
    }

    public String getString(String key, String defValue) {
        return this.sharedPrefs.getString(key, defValue);
    }

    public Map<String, ?> get() {
        return this.sharedPrefs.getAll();
    }

    public boolean contains(String key) {
        return this.sharedPrefs.contains(key);
    }

    public void clear() {
        edit();
        this.editor.clear();
    }

    public void flush() {
        if (this.editor != null) {
            this.editor.commit();
            this.editor = null;
        }
    }

    public void remove(String key) {
        edit();
        this.editor.remove(key);
    }

    private void edit() {
        if (this.editor == null) {
            this.editor = this.sharedPrefs.edit();
        }
    }
}
