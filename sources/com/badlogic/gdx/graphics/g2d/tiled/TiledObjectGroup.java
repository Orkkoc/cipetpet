package com.badlogic.gdx.graphics.g2d.tiled;

import java.util.ArrayList;
import java.util.HashMap;

public class TiledObjectGroup {
    public int height;
    public String name;
    public ArrayList<TiledObject> objects = new ArrayList<>();
    public HashMap<String, String> properties = new HashMap<>();
    public int width;
}
