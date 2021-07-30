package com.badlogic.gdx.utils;

import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SharedLibraryLoader {
    public static boolean is64Bit;
    public static boolean isAndroid;
    public static boolean isLinux;
    public static boolean isMac;
    public static boolean isWindows;
    private static HashSet<String> loadedLibraries = new HashSet<>();
    private String nativesJar;

    static {
        isWindows = System.getProperty("os.name").contains("Windows");
        isLinux = System.getProperty("os.name").contains("Linux");
        isMac = System.getProperty("os.name").contains("Mac");
        isAndroid = false;
        is64Bit = System.getProperty("os.arch").equals("amd64");
        String vm = System.getProperty("java.vm.name");
        if (vm != null && vm.contains("Dalvik")) {
            isAndroid = true;
            isWindows = false;
            isLinux = false;
            isMac = false;
            is64Bit = false;
        }
    }

    public SharedLibraryLoader() {
    }

    public SharedLibraryLoader(String nativesJar2) {
        this.nativesJar = nativesJar2;
    }

    public String crc(InputStream input) {
        if (input == null) {
            return "" + System.nanoTime();
        }
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[G3dConstants.STILL_MODEL];
        while (true) {
            try {
                int length = input.read(buffer);
                if (length == -1) {
                    break;
                }
                crc.update(buffer, 0, length);
            } catch (Exception e) {
                try {
                    input.close();
                } catch (Exception e2) {
                }
            }
        }
        return Long.toString(crc.getValue());
    }

    public String mapLibraryName(String libraryName) {
        if (isWindows) {
            return libraryName + (is64Bit ? "64.dll" : ".dll");
        } else if (isLinux) {
            return "lib" + libraryName + (is64Bit ? "64.so" : ".so");
        } else if (isMac) {
            return "lib" + libraryName + ".dylib";
        } else {
            return libraryName;
        }
    }

    public synchronized void load(String libraryName) {
        String libraryName2 = mapLibraryName(libraryName);
        if (!loadedLibraries.contains(libraryName2)) {
            try {
                if (isAndroid) {
                    System.loadLibrary(libraryName2);
                } else {
                    System.load(extractFile(libraryName2, (String) null).getAbsolutePath());
                }
                loadedLibraries.add(libraryName2);
            } catch (Throwable ex) {
                throw new GdxRuntimeException("Couldn't load shared library '" + libraryName2 + "' for target: " + System.getProperty("os.name") + (is64Bit ? ", 64-bit" : ", 32-bit"), ex);
            }
        }
    }

    private InputStream readFile(String path) {
        if (this.nativesJar == null) {
            return SharedLibraryLoader.class.getResourceAsStream("/" + path);
        }
        try {
            ZipFile file = new ZipFile(this.nativesJar);
            ZipEntry entry = file.getEntry(path);
            if (entry != null) {
                return file.getInputStream(entry);
            }
            throw new GdxRuntimeException("Couldn't find '" + path + "' in JAR: " + this.nativesJar);
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading '" + path + "' in JAR: " + this.nativesJar, ex);
        }
    }

    public File extractFile(String sourcePath, String dirName) throws IOException {
        String sourceCrc = crc(readFile(sourcePath));
        if (dirName == null) {
            dirName = sourceCrc;
        }
        File extractedDir = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + dirName);
        File extractedFile = new File(extractedDir, new File(sourcePath).getName());
        String extractedCrc = null;
        if (extractedFile.exists()) {
            try {
                extractedCrc = crc(new FileInputStream(extractedFile));
            } catch (FileNotFoundException e) {
            }
        }
        if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
            try {
                InputStream input = readFile(sourcePath);
                if (input == null) {
                    return null;
                }
                extractedDir.mkdirs();
                FileOutputStream output = new FileOutputStream(extractedFile);
                byte[] buffer = new byte[G3dConstants.STILL_MODEL];
                while (true) {
                    int length = input.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    output.write(buffer, 0, length);
                }
                input.close();
                output.close();
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error extracting file: " + sourcePath, ex);
            }
        }
        if (!extractedFile.exists()) {
            return null;
        }
        return extractedFile;
    }
}
