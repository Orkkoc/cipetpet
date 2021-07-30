package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

public class JsonWriter extends Writer {
    private JsonObject current;
    private boolean named;
    private OutputType outputType = OutputType.json;
    private final Array<JsonObject> stack = new Array<>();
    Writer writer;

    public JsonWriter(Writer writer2) {
        this.writer = writer2;
    }

    public void setOutputType(OutputType outputType2) {
        this.outputType = outputType2;
    }

    public JsonWriter name(String name) throws IOException {
        if (this.current == null || this.current.array) {
            throw new IllegalStateException("Current item must be an object.");
        }
        if (!this.current.needsComma) {
            this.current.needsComma = true;
        } else {
            this.writer.write(44);
        }
        this.writer.write(this.outputType.quoteName(name));
        this.writer.write(58);
        this.named = true;
        return this;
    }

    public JsonWriter object() throws IOException {
        if (this.current != null) {
            if (this.current.array) {
                if (!this.current.needsComma) {
                    this.current.needsComma = true;
                } else {
                    this.writer.write(44);
                }
            } else if (this.named || this.current.array) {
                this.named = false;
            } else {
                throw new IllegalStateException("Name must be set.");
            }
        }
        Array<JsonObject> array = this.stack;
        JsonObject jsonObject = new JsonObject(false);
        this.current = jsonObject;
        array.add(jsonObject);
        return this;
    }

    public JsonWriter array() throws IOException {
        if (this.current != null) {
            if (this.current.array) {
                if (!this.current.needsComma) {
                    this.current.needsComma = true;
                } else {
                    this.writer.write(44);
                }
            } else if (this.named || this.current.array) {
                this.named = false;
            } else {
                throw new IllegalStateException("Name must be set.");
            }
        }
        Array<JsonObject> array = this.stack;
        JsonObject jsonObject = new JsonObject(true);
        this.current = jsonObject;
        array.add(jsonObject);
        return this;
    }

    public JsonWriter value(Object value) throws IOException {
        if (this.current != null) {
            if (this.current.array) {
                if (!this.current.needsComma) {
                    this.current.needsComma = true;
                } else {
                    this.writer.write(44);
                }
            } else if (!this.named) {
                throw new IllegalStateException("Name must be set.");
            } else {
                this.named = false;
            }
        }
        this.writer.write(this.outputType.quoteValue(value));
        return this;
    }

    public JsonWriter object(String name) throws IOException {
        return name(name).object();
    }

    public JsonWriter array(String name) throws IOException {
        return name(name).array();
    }

    public JsonWriter set(String name, Object value) throws IOException {
        return name(name).value(value);
    }

    public JsonWriter pop() throws IOException {
        if (this.named) {
            throw new IllegalStateException("Expected an object, array, or value since a name was set.");
        }
        this.stack.pop().close();
        this.current = this.stack.size == 0 ? null : this.stack.peek();
        return this;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        this.writer.write(cbuf, off, len);
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void close() throws IOException {
        while (this.stack.size > 0) {
            pop();
        }
        this.writer.close();
    }

    private class JsonObject {
        final boolean array;
        boolean needsComma;

        JsonObject(boolean array2) throws IOException {
            this.array = array2;
            JsonWriter.this.writer.write(array2 ? 91 : 123);
        }

        /* access modifiers changed from: package-private */
        public void close() throws IOException {
            JsonWriter.this.writer.write(this.array ? 93 : 125);
        }
    }

    public enum OutputType {
        json,
        javascript,
        minimal;
        
        private static Pattern javascriptPattern;
        private static Pattern minimalNamePattern;
        private static Pattern minimalValuePattern;

        static {
            javascriptPattern = Pattern.compile("[a-zA-Z_$][a-zA-Z_$0-9]*");
            minimalValuePattern = Pattern.compile("[a-zA-Z_$][^:}\\], ]*");
            minimalNamePattern = Pattern.compile("[a-zA-Z0-9_$][^:}\\], ]*");
        }

        public String quoteValue(Object value) {
            if (this != json && (value == null || (value instanceof Number) || (value instanceof Boolean))) {
                return String.valueOf(value);
            }
            String string = String.valueOf(value).replace("\\", "\\\\");
            return (this != minimal || string.equals("true") || string.equals("false") || string.equals("null") || !minimalValuePattern.matcher(string).matches()) ? '\"' + string.replace("\"", "\\\"") + '\"' : string;
        }

        public String quoteName(String value) {
            String value2 = value.replace("\\", "\\\\");
            switch (this) {
                case minimal:
                    if (!minimalNamePattern.matcher(value2).matches()) {
                        return '\"' + value2.replace("\"", "\\\"") + '\"';
                    }
                    return value2;
                case javascript:
                    if (!javascriptPattern.matcher(value2).matches()) {
                        return '\"' + value2.replace("\"", "\\\"") + '\"';
                    }
                    return value2;
                default:
                    return '\"' + value2.replace("\"", "\\\"") + '\"';
            }
        }
    }
}
