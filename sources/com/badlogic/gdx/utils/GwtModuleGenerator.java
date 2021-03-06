package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class GwtModuleGenerator {
    private static void gatherJavaFiles(FileHandle dir, Set<String> names, Map<String, FileHandle> fileHandles, boolean recursive) {
        if (!dir.name().equals(".svn")) {
            for (FileHandle file : dir.list()) {
                if (file.isDirectory() && recursive) {
                    gatherJavaFiles(file, names, fileHandles, recursive);
                } else if (file.extension().equals("java")) {
                    System.out.println(file.name());
                    if (names.contains(file.name())) {
                        System.out.println(file.name() + " duplicate!");
                    }
                    names.add(file.name());
                    fileHandles.put(file.name(), file);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Set<String> excludes = new HashSet<>();
        Map<String, FileHandle> excludesHandles = new HashMap<>();
        System.out.println("Excludes -------------------------------------------------");
        gatherJavaFiles(new FileHandle("../backends/gdx-backends-gwt/src/com/badlogic/gdx/backends/gwt/emu/com/badlogic/gdx"), excludes, excludesHandles, true);
        System.out.println("#" + excludes.size());
        excludes.add("GdxBuild.java");
        excludes.add("GdxNativesLoader.java");
        excludes.add("GwtModuleGenerator.java");
        excludes.add("SharedLibraryLoader.java");
        excludes.add("Gdx2DPixmap.java");
        excludes.add("PixmapIO.java");
        excludes.add("ETC1.java");
        excludes.add("ETC1TextureData.java");
        excludes.add("ScreenUtils.java");
        excludes.add("RemoteInput.java");
        excludes.add("RemoteSender.java");
        excludes.add("TiledLoader.java");
        excludes.add("TileMapRendererLoader.java");
        excludes.add("AtomicQueue.java");
        excludes.add("LittleEndianInputStream.java");
        excludes.add("PauseableThread.java");
        excludes.add("DesktopClipboard.java");
        excludes.add("AndroidClipboard.java");
        Set<String> includes = new HashSet<>();
        Map<String, FileHandle> includesHandles = new TreeMap<>();
        System.out.println("Includes -------------------------------------------------");
        gatherJavaFiles(new FileHandle("src"), includes, includesHandles, true);
        System.out.println("#" + includes.size());
        for (String include : includes) {
            if (excludes.contains(include)) {
                FileHandle fileHandle = includesHandles.get(include);
                FileHandle fileHandle2 = excludesHandles.get(include);
                includesHandles.remove(include);
                System.out.println("excluded '" + include + "'");
            }
        }
        System.out.println("diff: " + includesHandles.size());
        StringWriter writer = new StringWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        XmlWriter builder = new XmlWriter(writer);
        builder.element("module").attribute("rename-to", "com.badlogic.gdx");
        builder.element("inherits").attribute("name", "com.esotericsoftware.tablelayout").pop();
        builder.element("source").attribute("path", "gdx");
        for (String include2 : includesHandles.keySet()) {
            builder.element("include").attribute("name", includesHandles.get(include2).path().replace("\\", "/").replace("src/com/badlogic/gdx/", "")).pop();
        }
        builder.element("include").attribute("name", "graphics/g2d/Animation.java").pop();
        builder.element("include").attribute("name", "graphics/g3d/Animation.java").pop();
        builder.pop();
        builder.pop();
        builder.close();
        System.out.println(writer);
        new FileHandle("src/com/badlogic/gdx.gwt.xml").writeString(writer.toString(), false);
    }
}
