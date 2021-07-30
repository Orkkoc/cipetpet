package com.badlogic.gdx.files;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dConstants;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class FileHandle {
    protected File file;
    protected Files.FileType type;

    protected FileHandle() {
    }

    public FileHandle(String fileName) {
        this.file = new File(fileName);
        this.type = Files.FileType.Absolute;
    }

    public FileHandle(File file2) {
        this.file = file2;
        this.type = Files.FileType.Absolute;
    }

    protected FileHandle(String fileName, Files.FileType type2) {
        this.type = type2;
        this.file = new File(fileName);
    }

    protected FileHandle(File file2, Files.FileType type2) {
        this.file = file2;
        this.type = type2;
    }

    public String path() {
        return this.file.getPath().replace('\\', '/');
    }

    public String name() {
        return this.file.getName();
    }

    public String extension() {
        String name = this.file.getName();
        int dotIndex = name.lastIndexOf(46);
        if (dotIndex == -1) {
            return "";
        }
        return name.substring(dotIndex + 1);
    }

    public String nameWithoutExtension() {
        String name = this.file.getName();
        int dotIndex = name.lastIndexOf(46);
        return dotIndex == -1 ? name : name.substring(0, dotIndex);
    }

    public String pathWithoutExtension() {
        String path = this.file.getPath().replace('\\', '/');
        int dotIndex = path.lastIndexOf(46);
        return dotIndex == -1 ? path : path.substring(0, dotIndex);
    }

    public Files.FileType type() {
        return this.type;
    }

    public File file() {
        if (this.type == Files.FileType.External) {
            return new File(Gdx.files.getExternalStoragePath(), this.file.getPath());
        }
        return this.file;
    }

    public InputStream read() {
        if (this.type == Files.FileType.Classpath || ((this.type == Files.FileType.Internal && !this.file.exists()) || (this.type == Files.FileType.Local && !this.file.exists()))) {
            InputStream input = FileHandle.class.getResourceAsStream("/" + this.file.getPath().replace('\\', '/'));
            if (input != null) {
                return input;
            }
            throw new GdxRuntimeException("File not found: " + this.file + " (" + this.type + ")");
        }
        try {
            return new FileInputStream(file());
        } catch (Exception ex) {
            if (file().isDirectory()) {
                throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
            }
            throw new GdxRuntimeException("Error reading file: " + this.file + " (" + this.type + ")", ex);
        }
    }

    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    public Reader reader() {
        return new InputStreamReader(read());
    }

    public Reader reader(String charset) {
        try {
            return new InputStreamReader(read(), charset);
        } catch (UnsupportedEncodingException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
    }

    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(new InputStreamReader(read()), bufferSize);
    }

    public BufferedReader reader(int bufferSize, String charset) {
        try {
            return new BufferedReader(new InputStreamReader(read(), charset), bufferSize);
        } catch (UnsupportedEncodingException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        }
    }

    public String readString() {
        return readString((String) null);
    }

    public String readString(String charset) {
        InputStreamReader reader;
        int fileLength = (int) length();
        if (fileLength == 0) {
            fileLength = 512;
        }
        StringBuilder output = new StringBuilder(fileLength);
        InputStreamReader reader2 = null;
        if (charset == null) {
            try {
                reader = new InputStreamReader(read());
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error reading layout file: " + this, ex);
            } catch (Throwable th) {
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e) {
                    }
                }
                throw th;
            }
        } else {
            reader = new InputStreamReader(read(), charset);
        }
        char[] buffer = new char[256];
        while (true) {
            int length = reader.read(buffer);
            if (length == -1) {
                break;
            }
            output.append(buffer, 0, length);
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e2) {
            }
        }
        return output.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0062 A[SYNTHETIC, Splitter:B:30:0x0062] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] readBytes() {
        /*
            r13 = this;
            r12 = -1
            r11 = 0
            long r9 = r13.length()
            int r5 = (int) r9
            if (r5 != 0) goto L_0x000b
            r5 = 512(0x200, float:7.175E-43)
        L_0x000b:
            byte[] r1 = new byte[r5]
            r7 = 0
            java.io.InputStream r4 = r13.read()
        L_0x0012:
            int r9 = r1.length     // Catch:{ IOException -> 0x0045 }
            int r9 = r9 - r7
            int r2 = r4.read(r1, r7, r9)     // Catch:{ IOException -> 0x0045 }
            if (r2 != r12) goto L_0x0029
        L_0x001a:
            if (r4 == 0) goto L_0x001f
            r4.close()     // Catch:{ IOException -> 0x0066 }
        L_0x001f:
            int r9 = r1.length
            if (r7 >= r9) goto L_0x0028
            byte[] r6 = new byte[r7]
            java.lang.System.arraycopy(r1, r11, r6, r11, r7)
            r1 = r6
        L_0x0028:
            return r1
        L_0x0029:
            int r7 = r7 + r2
            int r9 = r1.length     // Catch:{ IOException -> 0x0045 }
            if (r7 != r9) goto L_0x0012
            int r0 = r4.read()     // Catch:{ IOException -> 0x0045 }
            if (r0 == r12) goto L_0x001a
            int r9 = r1.length     // Catch:{ IOException -> 0x0045 }
            int r9 = r9 * 2
            byte[] r6 = new byte[r9]     // Catch:{ IOException -> 0x0045 }
            r9 = 0
            r10 = 0
            java.lang.System.arraycopy(r1, r9, r6, r10, r7)     // Catch:{ IOException -> 0x0045 }
            r1 = r6
            int r8 = r7 + 1
            byte r9 = (byte) r0
            r1[r7] = r9     // Catch:{ IOException -> 0x006d, all -> 0x006a }
            r7 = r8
            goto L_0x0012
        L_0x0045:
            r3 = move-exception
        L_0x0046:
            com.badlogic.gdx.utils.GdxRuntimeException r9 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x005f }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x005f }
            r10.<init>()     // Catch:{ all -> 0x005f }
            java.lang.String r11 = "Error reading file: "
            java.lang.StringBuilder r10 = r10.append(r11)     // Catch:{ all -> 0x005f }
            java.lang.StringBuilder r10 = r10.append(r13)     // Catch:{ all -> 0x005f }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x005f }
            r9.<init>(r10, r3)     // Catch:{ all -> 0x005f }
            throw r9     // Catch:{ all -> 0x005f }
        L_0x005f:
            r9 = move-exception
        L_0x0060:
            if (r4 == 0) goto L_0x0065
            r4.close()     // Catch:{ IOException -> 0x0068 }
        L_0x0065:
            throw r9
        L_0x0066:
            r9 = move-exception
            goto L_0x001f
        L_0x0068:
            r10 = move-exception
            goto L_0x0065
        L_0x006a:
            r9 = move-exception
            r7 = r8
            goto L_0x0060
        L_0x006d:
            r3 = move-exception
            r7 = r8
            goto L_0x0046
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.files.FileHandle.readBytes():byte[]");
    }

    public int readBytes(byte[] bytes, int offset, int size) {
        InputStream input = read();
        int position = 0;
        while (true) {
            try {
                int count = input.read(bytes, offset + position, size - position);
                if (count <= 0) {
                    break;
                }
                position += count;
            } catch (IOException ex) {
                throw new GdxRuntimeException("Error reading file: " + this, ex);
            } catch (Throwable th) {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
                throw th;
            }
        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException e2) {
            }
        }
        return position - offset;
    }

    public OutputStream write(boolean append) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
        } else if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
        } else {
            parent().mkdirs();
            try {
                return new FileOutputStream(file(), append);
            } catch (Exception ex) {
                if (file().isDirectory()) {
                    throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
                }
                throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
            }
        }
    }

    public void write(InputStream input, boolean append) {
        OutputStream output = null;
        try {
            output = write(append);
            byte[] buffer = new byte[G3dConstants.STILL_MODEL];
            while (true) {
                int length = input.read(buffer);
                if (length == -1) {
                    break;
                }
                output.write(buffer, 0, length);
            }
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e2) {
                }
            }
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error stream writing to file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e3) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e4) {
                }
            }
            throw th;
        }
    }

    public Writer writer(boolean append) {
        return writer(append, (String) null);
    }

    public Writer writer(boolean append, String charset) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot write to a classpath file: " + this.file);
        } else if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot write to an internal file: " + this.file);
        } else {
            parent().mkdirs();
            try {
                FileOutputStream output = new FileOutputStream(file(), append);
                if (charset == null) {
                    return new OutputStreamWriter(output);
                }
                return new OutputStreamWriter(output, charset);
            } catch (IOException ex) {
                if (file().isDirectory()) {
                    throw new GdxRuntimeException("Cannot open a stream to a directory: " + this.file + " (" + this.type + ")", ex);
                }
                throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
            }
        }
    }

    public void writeString(String string, boolean append) {
        writeString(string, append, (String) null);
    }

    public void writeString(String string, boolean append, String charset) {
        Writer writer = null;
        try {
            writer = writer(append, charset);
            writer.write(string);
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e2) {
                }
            }
            throw th;
        }
    }

    public void writeBytes(byte[] bytes, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes);
            try {
                output.close();
            } catch (IOException e) {
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            try {
                output.close();
            } catch (IOException e2) {
            }
            throw th;
        }
    }

    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        OutputStream output = write(append);
        try {
            output.write(bytes, offset, length);
            try {
                output.close();
            } catch (IOException e) {
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error writing file: " + this.file + " (" + this.type + ")", ex);
        } catch (Throwable th) {
            try {
                output.close();
            } catch (IOException e2) {
            }
            throw th;
        }
    }

    public FileHandle[] list() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
        }
        String[] relativePaths = file().list();
        if (relativePaths == null) {
            return new FileHandle[0];
        }
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int n = relativePaths.length;
        for (int i = 0; i < n; i++) {
            handles[i] = child(relativePaths[i]);
        }
        return handles;
    }

    public FileHandle[] list(String suffix) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot list a classpath directory: " + this.file);
        }
        String[] relativePaths = file().list();
        if (relativePaths == null) {
            return new FileHandle[0];
        }
        FileHandle[] handles = new FileHandle[relativePaths.length];
        int count = 0;
        for (String path : relativePaths) {
            if (path.endsWith(suffix)) {
                handles[count] = child(path);
                count++;
            }
        }
        if (count >= relativePaths.length) {
            return handles;
        }
        FileHandle[] newHandles = new FileHandle[count];
        System.arraycopy(handles, 0, newHandles, 0, count);
        return newHandles;
    }

    public boolean isDirectory() {
        if (this.type == Files.FileType.Classpath) {
            return false;
        }
        return file().isDirectory();
    }

    public FileHandle child(String name) {
        if (this.file.getPath().length() == 0) {
            return new FileHandle(new File(name), this.type);
        }
        return new FileHandle(new File(this.file, name), this.type);
    }

    public FileHandle sibling(String name) {
        if (this.file.getPath().length() != 0) {
            return new FileHandle(new File(this.file.getParent(), name), this.type);
        }
        throw new GdxRuntimeException("Cannot get the sibling of the root.");
    }

    public FileHandle parent() {
        File parent = this.file.getParentFile();
        if (parent == null) {
            if (this.type == Files.FileType.Absolute) {
                parent = new File("/");
            } else {
                parent = new File("");
            }
        }
        return new FileHandle(parent, this.type);
    }

    public void mkdirs() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot mkdirs with a classpath file: " + this.file);
        } else if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot mkdirs with an internal file: " + this.file);
        } else {
            file().mkdirs();
        }
    }

    public boolean exists() {
        switch (this.type) {
            case Internal:
                if (this.file.exists()) {
                    return true;
                }
                break;
            case Classpath:
                break;
            default:
                return file().exists();
        }
        return FileHandle.class.getResource(new StringBuilder().append("/").append(this.file.getPath().replace('\\', '/')).toString()) != null;
    }

    public boolean delete() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            return file().delete();
        } else {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
    }

    public boolean deleteDirectory() {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        } else if (this.type != Files.FileType.Internal) {
            return deleteDirectory(file());
        } else {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        }
    }

    public void emptyDirectory() {
        emptyDirectory(false);
    }

    public void emptyDirectory(boolean preserveTree) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot delete a classpath file: " + this.file);
        } else if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot delete an internal file: " + this.file);
        } else {
            emptyDirectory(file(), preserveTree);
        }
    }

    public void copyTo(FileHandle dest) {
        boolean sourceDir = isDirectory();
        if (!sourceDir) {
            if (dest.isDirectory()) {
                dest = dest.child(name());
            }
            copyFile(this, dest);
            return;
        }
        if (!dest.exists()) {
            dest.mkdirs();
            if (!dest.isDirectory()) {
                throw new GdxRuntimeException("Destination directory cannot be created: " + dest);
            }
        } else if (!dest.isDirectory()) {
            throw new GdxRuntimeException("Destination exists but is not a directory: " + dest);
        }
        if (!sourceDir) {
            dest = dest.child(name());
        }
        copyDirectory(this, dest);
    }

    public void moveTo(FileHandle dest) {
        if (this.type == Files.FileType.Classpath) {
            throw new GdxRuntimeException("Cannot move a classpath file: " + this.file);
        } else if (this.type == Files.FileType.Internal) {
            throw new GdxRuntimeException("Cannot move an internal file: " + this.file);
        } else {
            copyTo(dest);
            delete();
        }
    }

    public long length() {
        if (this.type != Files.FileType.Classpath && (this.type != Files.FileType.Internal || this.file.exists())) {
            return file().length();
        }
        InputStream input = read();
        try {
            long available = (long) input.available();
            try {
                input.close();
                return available;
            } catch (IOException e) {
                return available;
            }
        } catch (Exception e2) {
            try {
                input.close();
            } catch (IOException e3) {
            }
            return 0;
        } catch (Throwable th) {
            try {
                input.close();
            } catch (IOException e4) {
            }
            throw th;
        }
    }

    public long lastModified() {
        return file().lastModified();
    }

    public String toString() {
        return this.file.getPath().replace('\\', '/');
    }

    public static FileHandle tempFile(String prefix) {
        try {
            return new FileHandle(File.createTempFile(prefix, (String) null));
        } catch (IOException ex) {
            throw new GdxRuntimeException("Unable to create temp file.", ex);
        }
    }

    public static FileHandle tempDirectory(String prefix) {
        try {
            File file2 = File.createTempFile(prefix, (String) null);
            if (!file2.delete()) {
                throw new IOException("Unable to delete temp file: " + file2);
            } else if (file2.mkdir()) {
                return new FileHandle(file2);
            } else {
                throw new IOException("Unable to create temp directory: " + file2);
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Unable to create temp file.", ex);
        }
    }

    private static void emptyDirectory(File file2, boolean preserveTree) {
        File[] files;
        if (file2.exists() && (files = file2.listFiles()) != null) {
            int n = files.length;
            for (int i = 0; i < n; i++) {
                if (!files[i].isDirectory()) {
                    files[i].delete();
                } else if (preserveTree) {
                    emptyDirectory(files[i], true);
                } else {
                    deleteDirectory(files[i]);
                }
            }
        }
    }

    private static boolean deleteDirectory(File file2) {
        emptyDirectory(file2, false);
        return file2.delete();
    }

    private static void copyFile(FileHandle source, FileHandle dest) {
        try {
            dest.write(source.read(), false);
        } catch (Exception ex) {
            throw new GdxRuntimeException("Error copying source file: " + source.file + " (" + source.type + ")\n" + "To destination: " + dest.file + " (" + dest.type + ")", ex);
        }
    }

    private static void copyDirectory(FileHandle sourceDir, FileHandle destDir) {
        destDir.mkdirs();
        for (FileHandle srcFile : sourceDir.list()) {
            FileHandle destFile = destDir.child(srcFile.name());
            if (srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile);
            } else {
                copyFile(srcFile, destFile);
            }
        }
    }
}
