package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class Json {
    private static final boolean debug = false;
    private final ObjectMap<Class, Object[]> classToDefaultValues;
    private final ObjectMap<Class, Serializer> classToSerializer;
    private final ObjectMap<Class, String> classToTag;
    private boolean ignoreUnknownFields;
    private JsonWriter.OutputType outputType;
    private final ObjectMap<String, Class> tagToClass;
    private String typeName;
    private final ObjectMap<Class, ObjectMap<String, FieldMetadata>> typeToFields;
    private boolean usePrototypes;
    private JsonWriter writer;

    public interface Serializable {
        void read(Json json, OrderedMap<String, Object> orderedMap);

        void write(Json json);
    }

    public interface Serializer<T> {
        T read(Json json, Object obj, Class cls);

        void write(Json json, T t, Class cls);
    }

    public Json() {
        this.typeName = "class";
        this.usePrototypes = true;
        this.typeToFields = new ObjectMap<>();
        this.tagToClass = new ObjectMap<>();
        this.classToTag = new ObjectMap<>();
        this.classToSerializer = new ObjectMap<>();
        this.classToDefaultValues = new ObjectMap<>();
        this.outputType = JsonWriter.OutputType.minimal;
    }

    public Json(JsonWriter.OutputType outputType2) {
        this.typeName = "class";
        this.usePrototypes = true;
        this.typeToFields = new ObjectMap<>();
        this.tagToClass = new ObjectMap<>();
        this.classToTag = new ObjectMap<>();
        this.classToSerializer = new ObjectMap<>();
        this.classToDefaultValues = new ObjectMap<>();
        this.outputType = outputType2;
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields2) {
        this.ignoreUnknownFields = ignoreUnknownFields2;
    }

    public void setOutputType(JsonWriter.OutputType outputType2) {
        this.outputType = outputType2;
    }

    public void addClassTag(String tag, Class type) {
        this.tagToClass.put(tag, type);
        this.classToTag.put(type, tag);
    }

    public Class getClass(String tag) {
        Class type = this.tagToClass.get(tag);
        if (type != null) {
            return type;
        }
        try {
            return Class.forName(tag);
        } catch (ClassNotFoundException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public String getTag(Class type) {
        String tag = this.classToTag.get(type);
        return tag != null ? tag : type.getName();
    }

    public void setTypeName(String typeName2) {
        this.typeName = typeName2;
    }

    public <T> void setSerializer(Class<T> type, Serializer<T> serializer) {
        this.classToSerializer.put(type, serializer);
    }

    public <T> Serializer<T> getSerializer(Class<T> type) {
        return this.classToSerializer.get(type);
    }

    public void setUsePrototypes(boolean usePrototypes2) {
        this.usePrototypes = usePrototypes2;
    }

    public void setElementType(Class type, String fieldName, Class elementType) {
        ObjectMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields == null) {
            fields = cacheFields(type);
        }
        FieldMetadata metadata = fields.get(fieldName);
        if (metadata == null) {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
        metadata.elementType = elementType;
    }

    private ObjectMap<String, FieldMetadata> cacheFields(Class type) {
        ArrayList<Field> allFields = new ArrayList<>();
        for (Class nextClass = type; nextClass != Object.class; nextClass = nextClass.getSuperclass()) {
            Collections.addAll(allFields, nextClass.getDeclaredFields());
        }
        ObjectMap<String, FieldMetadata> nameToField = new ObjectMap<>();
        int n = allFields.size();
        for (int i = 0; i < n; i++) {
            Field field = allFields.get(i);
            int modifiers = field.getModifiers();
            if (!Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers) && !field.isSynthetic()) {
                if (!field.isAccessible()) {
                    try {
                        field.setAccessible(true);
                    } catch (AccessControlException e) {
                    }
                }
                nameToField.put(field.getName(), new FieldMetadata(field));
            }
        }
        this.typeToFields.put(type, nameToField);
        return nameToField;
    }

    public String toJson(Object object) {
        return toJson(object, (Class) object == null ? null : object.getClass(), (Class) null);
    }

    public String toJson(Object object, Class knownType) {
        return toJson(object, knownType, (Class) null);
    }

    public String toJson(Object object, Class knownType, Class elementType) {
        StringWriter buffer = new StringWriter();
        toJson(object, knownType, elementType, (Writer) buffer);
        return buffer.toString();
    }

    public void toJson(Object object, FileHandle file) {
        toJson(object, (Class) object == null ? null : object.getClass(), (Class) null, file);
    }

    public void toJson(Object object, Class knownType, FileHandle file) {
        toJson(object, knownType, (Class) null, file);
    }

    public void toJson(Object object, Class knownType, Class elementType, FileHandle file) {
        Writer writer2 = null;
        try {
            writer2 = file.writer(false);
            toJson(object, knownType, elementType, writer2);
            if (writer2 != null) {
                try {
                    writer2.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception ex) {
            throw new SerializationException("Error writing file: " + file, ex);
        } catch (Throwable th) {
            if (writer2 != null) {
                try {
                    writer2.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    public void toJson(Object object, Writer writer2) {
        toJson(object, (Class) object == null ? null : object.getClass(), (Class) null, writer2);
    }

    public void toJson(Object object, Class knownType, Writer writer2) {
        toJson(object, knownType, (Class) null, writer2);
    }

    public void toJson(Object object, Class knownType, Class elementType, Writer writer2) {
        if (!(writer2 instanceof JsonWriter)) {
            writer2 = new JsonWriter(writer2);
        }
        ((JsonWriter) writer2).setOutputType(this.outputType);
        this.writer = (JsonWriter) writer2;
        try {
            writeValue(object, knownType, elementType);
        } finally {
            this.writer = null;
        }
    }

    public void writeFields(Object object) {
        Class type = object.getClass();
        Object[] defaultValues = getDefaultValues(type);
        ObjectMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields == null) {
            fields = cacheFields(type);
        }
        int i = 0;
        Iterator i$ = fields.values().iterator();
        while (i$.hasNext()) {
            FieldMetadata metadata = i$.next();
            Field field = metadata.field;
            try {
                Object value = field.get(object);
                if (defaultValues != null) {
                    int i2 = i + 1;
                    try {
                        Object defaultValue = defaultValues[i];
                        if (value == null && defaultValue == null) {
                            i = i2;
                        } else if (value == null || defaultValue == null || !value.equals(defaultValue)) {
                            i = i2;
                        } else {
                            i = i2;
                        }
                    } catch (IllegalAccessException e) {
                        ex = e;
                        int i3 = i2;
                        throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
                    } catch (SerializationException e2) {
                        ex = e2;
                        int i4 = i2;
                        ex.addTrace(field + " (" + type.getName() + ")");
                        throw ex;
                    } catch (Exception e3) {
                        runtimeEx = e3;
                        int i5 = i2;
                        SerializationException ex = new SerializationException((Throwable) runtimeEx);
                        ex.addTrace(field + " (" + type.getName() + ")");
                        throw ex;
                    }
                }
                this.writer.name(field.getName());
                writeValue(value, (Class) field.getType(), metadata.elementType);
            } catch (IllegalAccessException e4) {
                ex = e4;
            } catch (SerializationException e5) {
                ex = e5;
                ex.addTrace(field + " (" + type.getName() + ")");
                throw ex;
            } catch (Exception e6) {
                runtimeEx = e6;
                SerializationException ex2 = new SerializationException((Throwable) runtimeEx);
                ex2.addTrace(field + " (" + type.getName() + ")");
                throw ex2;
            }
        }
    }

    private Object[] getDefaultValues(Class type) {
        if (!this.usePrototypes) {
            return null;
        }
        if (this.classToDefaultValues.containsKey(type)) {
            return this.classToDefaultValues.get(type);
        }
        try {
            Object object = newInstance(type);
            ObjectMap<String, FieldMetadata> fields = this.typeToFields.get(type);
            if (fields == null) {
                fields = cacheFields(type);
            }
            Object[] values = new Object[fields.size];
            this.classToDefaultValues.put(type, values);
            int i = 0;
            Iterator i$ = fields.values().iterator();
            while (i$.hasNext()) {
                Field field = i$.next().field;
                int i2 = i + 1;
                try {
                    values[i] = field.get(object);
                    i = i2;
                } catch (IllegalAccessException ex) {
                    throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
                } catch (SerializationException ex2) {
                    ex2.addTrace(field + " (" + type.getName() + ")");
                    throw ex2;
                } catch (RuntimeException runtimeEx) {
                    SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
                    ex3.addTrace(field + " (" + type.getName() + ")");
                    throw ex3;
                }
            }
            return values;
        } catch (Exception e) {
            this.classToDefaultValues.put(type, null);
            return null;
        }
    }

    public void writeField(Object object, String name) {
        writeField(object, name, name, (Class) null);
    }

    public void writeField(Object object, String name, Class elementType) {
        writeField(object, name, name, elementType);
    }

    public void writeField(Object object, String fieldName, String jsonName) {
        writeField(object, fieldName, jsonName, (Class) null);
    }

    public void writeField(Object object, String fieldName, String jsonName, Class elementType) {
        Class type = object.getClass();
        ObjectMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields == null) {
            fields = cacheFields(type);
        }
        FieldMetadata metadata = fields.get(fieldName);
        if (metadata == null) {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
        Field field = metadata.field;
        if (elementType == null) {
            elementType = metadata.elementType;
        }
        try {
            this.writer.name(jsonName);
            writeValue(field.get(object), (Class) field.getType(), elementType);
        } catch (IllegalAccessException ex) {
            throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
        } catch (SerializationException ex2) {
            ex2.addTrace(field + " (" + type.getName() + ")");
            throw ex2;
        } catch (Exception runtimeEx) {
            SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
            ex3.addTrace(field + " (" + type.getName() + ")");
            throw ex3;
        }
    }

    public void writeValue(String name, Object value) {
        try {
            this.writer.name(name);
            if (value == null) {
                writeValue(value, (Class) null, (Class) null);
            } else {
                writeValue(value, (Class) value.getClass(), (Class) null);
            }
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeValue(String name, Object value, Class knownType) {
        try {
            this.writer.name(name);
            writeValue(value, knownType, (Class) null);
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeValue(String name, Object value, Class knownType, Class elementType) {
        try {
            this.writer.name(name);
            writeValue(value, knownType, elementType);
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeValue(Object value) {
        if (value == null) {
            writeValue(value, (Class) null, (Class) null);
        } else {
            writeValue(value, (Class) value.getClass(), (Class) null);
        }
    }

    public void writeValue(Object value, Class knownType) {
        writeValue(value, knownType, (Class) null);
    }

    public void writeValue(Object value, Class knownType, Class<?> elementType) {
        if (value == null) {
            try {
                this.writer.value((Object) null);
            } catch (IOException ex) {
                throw new SerializationException((Throwable) ex);
            }
        } else {
            Class actualType = value.getClass();
            if (actualType.isPrimitive() || actualType == String.class || actualType == Integer.class || actualType == Boolean.class || actualType == Float.class || actualType == Long.class || actualType == Double.class || actualType == Short.class || actualType == Byte.class || actualType == Character.class) {
                this.writer.value(value);
            } else if (value instanceof Serializable) {
                writeObjectStart(actualType, knownType);
                ((Serializable) value).write(this);
                writeObjectEnd();
            } else {
                Serializer serializer = this.classToSerializer.get(actualType);
                if (serializer != null) {
                    serializer.write(this, value, knownType);
                } else if (value instanceof Array) {
                    if (knownType == null || actualType == knownType) {
                        writeArrayStart();
                        Array array = (Array) value;
                        int n = array.size;
                        for (int i = 0; i < n; i++) {
                            writeValue(array.get(i), elementType, (Class) null);
                        }
                        writeArrayEnd();
                        return;
                    }
                    throw new SerializationException("Serialization of an Array other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                } else if (value instanceof Collection) {
                    if (knownType == null || actualType == knownType || actualType == ArrayList.class) {
                        writeArrayStart();
                        for (Object item : (Collection) value) {
                            writeValue(item, elementType, (Class) null);
                        }
                        writeArrayEnd();
                        return;
                    }
                    throw new SerializationException("Serialization of a Collection other than the known type is not supported.\nKnown type: " + knownType + "\nActual type: " + actualType);
                } else if (actualType.isArray()) {
                    if (elementType == null) {
                        elementType = actualType.getComponentType();
                    }
                    int length = Array.getLength(value);
                    writeArrayStart();
                    for (int i2 = 0; i2 < length; i2++) {
                        writeValue(Array.get(value, i2), (Class) elementType, (Class) null);
                    }
                    writeArrayEnd();
                } else if (value instanceof OrderedMap) {
                    if (knownType == null) {
                        knownType = OrderedMap.class;
                    }
                    writeObjectStart(actualType, knownType);
                    OrderedMap map = (OrderedMap) value;
                    Iterator i$ = map.orderedKeys().iterator();
                    while (i$.hasNext()) {
                        Object key = i$.next();
                        this.writer.name(convertToString(key));
                        writeValue(map.get(key), elementType, (Class) null);
                    }
                    writeObjectEnd();
                } else if (value instanceof ArrayMap) {
                    if (knownType == null) {
                        knownType = ArrayMap.class;
                    }
                    writeObjectStart(actualType, knownType);
                    ArrayMap map2 = (ArrayMap) value;
                    int n2 = map2.size;
                    for (int i3 = 0; i3 < n2; i3++) {
                        this.writer.name(convertToString(map2.keys[i3]));
                        writeValue((Object) map2.values[i3], elementType, (Class) null);
                    }
                    writeObjectEnd();
                } else if (value instanceof ObjectMap) {
                    if (knownType == null) {
                        knownType = OrderedMap.class;
                    }
                    writeObjectStart(actualType, knownType);
                    Iterator i$2 = ((ObjectMap) value).entries().iterator();
                    while (i$2.hasNext()) {
                        ObjectMap.Entry entry = (ObjectMap.Entry) i$2.next();
                        this.writer.name(convertToString(entry.key));
                        writeValue((Object) entry.value, elementType, (Class) null);
                    }
                    writeObjectEnd();
                } else if (value instanceof Map) {
                    if (knownType == null) {
                        knownType = OrderedMap.class;
                    }
                    writeObjectStart(actualType, knownType);
                    for (Map.Entry entry2 : ((Map) value).entrySet()) {
                        this.writer.name(convertToString(entry2.getKey()));
                        writeValue(entry2.getValue(), elementType, (Class) null);
                    }
                    writeObjectEnd();
                } else if (actualType.isEnum()) {
                    this.writer.value(value);
                } else {
                    writeObjectStart(actualType, knownType);
                    writeFields(value);
                    writeObjectEnd();
                }
            }
        }
    }

    public void writeObjectStart(String name) {
        try {
            this.writer.name(name);
            writeObjectStart();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectStart(String name, Class actualType, Class knownType) {
        try {
            this.writer.name(name);
            writeObjectStart(actualType, knownType);
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectStart() {
        try {
            this.writer.object();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectStart(Class actualType, Class knownType) {
        try {
            this.writer.object();
            if (knownType == null || knownType != actualType) {
                writeType(actualType);
            }
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeObjectEnd() {
        try {
            this.writer.pop();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeArrayStart(String name) {
        try {
            this.writer.name(name);
            this.writer.array();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeArrayStart() {
        try {
            this.writer.array();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeArrayEnd() {
        try {
            this.writer.pop();
        } catch (IOException ex) {
            throw new SerializationException((Throwable) ex);
        }
    }

    public void writeType(Class type) {
        if (this.typeName != null) {
            String className = this.classToTag.get(type);
            if (className == null) {
                className = type.getName();
            }
            try {
                this.writer.set(this.typeName, className);
            } catch (IOException ex) {
                throw new SerializationException((Throwable) ex);
            }
        }
    }

    public <T> T fromJson(Class<T> type, Reader reader) {
        return readValue(type, (Class) null, new JsonReader().parse(reader));
    }

    public <T> T fromJson(Class<T> type, Class elementType, Reader reader) {
        return readValue(type, elementType, new JsonReader().parse(reader));
    }

    public <T> T fromJson(Class<T> type, InputStream input) {
        return readValue(type, (Class) null, new JsonReader().parse(input));
    }

    public <T> T fromJson(Class<T> type, Class elementType, InputStream input) {
        return readValue(type, elementType, new JsonReader().parse(input));
    }

    public <T> T fromJson(Class<T> type, FileHandle file) {
        try {
            return readValue(type, (Class) null, new JsonReader().parse(file));
        } catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }

    public <T> T fromJson(Class<T> type, Class elementType, FileHandle file) {
        try {
            return readValue(type, elementType, new JsonReader().parse(file));
        } catch (Exception ex) {
            throw new SerializationException("Error reading file: " + file, ex);
        }
    }

    public <T> T fromJson(Class<T> type, char[] data, int offset, int length) {
        return readValue(type, (Class) null, new JsonReader().parse(data, offset, length));
    }

    public <T> T fromJson(Class<T> type, Class elementType, char[] data, int offset, int length) {
        return readValue(type, elementType, new JsonReader().parse(data, offset, length));
    }

    public <T> T fromJson(Class<T> type, String json) {
        return readValue(type, (Class) null, new JsonReader().parse(json));
    }

    public <T> T fromJson(Class<T> type, Class elementType, String json) {
        return readValue(type, elementType, new JsonReader().parse(json));
    }

    public void readField(Object object, String name, Object jsonData) {
        readField(object, name, name, (Class) null, jsonData);
    }

    public void readField(Object object, String name, Class elementType, Object jsonData) {
        readField(object, name, name, elementType, jsonData);
    }

    public void readField(Object object, String fieldName, String jsonName, Object jsonData) {
        readField(object, fieldName, jsonName, (Class) null, jsonData);
    }

    public void readField(Object object, String fieldName, String jsonName, Class elementType, Object jsonData) {
        OrderedMap jsonMap = (OrderedMap) jsonData;
        Class type = object.getClass();
        ObjectMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields == null) {
            fields = cacheFields(type);
        }
        FieldMetadata metadata = fields.get(fieldName);
        if (metadata == null) {
            throw new SerializationException("Field not found: " + fieldName + " (" + type.getName() + ")");
        }
        Field field = metadata.field;
        Object jsonValue = jsonMap.get(jsonName);
        if (jsonValue != null) {
            if (elementType == null) {
                elementType = metadata.elementType;
            }
            try {
                field.set(object, readValue(field.getType(), elementType, jsonValue));
            } catch (IllegalAccessException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            } catch (SerializationException ex2) {
                ex2.addTrace(field.getName() + " (" + type.getName() + ")");
                throw ex2;
            } catch (RuntimeException runtimeEx) {
                SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
                ex3.addTrace(field.getName() + " (" + type.getName() + ")");
                throw ex3;
            }
        }
    }

    public void readFields(Object object, Object jsonData) {
        OrderedMap<String, Object> jsonMap = (OrderedMap) jsonData;
        Class type = object.getClass();
        ObjectMap<String, FieldMetadata> fields = this.typeToFields.get(type);
        if (fields == null) {
            fields = cacheFields(type);
        }
        Iterator i$ = jsonMap.entries().iterator();
        while (i$.hasNext()) {
            ObjectMap.Entry<String, Object> entry = i$.next();
            FieldMetadata metadata = fields.get(entry.key);
            if (metadata != null) {
                Field field = metadata.field;
                if (entry.value != null) {
                    try {
                        field.set(object, readValue(field.getType(), metadata.elementType, (Object) entry.value));
                    } catch (IllegalAccessException ex) {
                        throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
                    } catch (SerializationException ex2) {
                        ex2.addTrace(field.getName() + " (" + type.getName() + ")");
                        throw ex2;
                    } catch (RuntimeException runtimeEx) {
                        SerializationException ex3 = new SerializationException((Throwable) runtimeEx);
                        ex3.addTrace(field.getName() + " (" + type.getName() + ")");
                        throw ex3;
                    }
                }
            } else if (!this.ignoreUnknownFields) {
                throw new SerializationException("Field not found: " + ((String) entry.key) + " (" + type.getName() + ")");
            }
        }
    }

    public <T> T readValue(String name, Class<T> type, Object jsonData) {
        return readValue(type, (Class) null, ((OrderedMap) jsonData).get(name));
    }

    public <T> T readValue(String name, Class<T> type, T defaultValue, Object jsonData) {
        Object jsonValue = ((OrderedMap) jsonData).get(name);
        return jsonValue == null ? defaultValue : readValue(type, (Class) null, jsonValue);
    }

    public <T> T readValue(String name, Class<T> type, Class elementType, Object jsonData) {
        return readValue(type, elementType, ((OrderedMap) jsonData).get(name));
    }

    public <T> T readValue(String name, Class<T> type, Class elementType, T defaultValue, Object jsonData) {
        Object jsonValue = ((OrderedMap) jsonData).get(name);
        return jsonValue == null ? defaultValue : readValue(type, elementType, jsonValue);
    }

    public <T> T readValue(Class<T> type, Class elementType, T t, Object jsonData) {
        return readValue(type, elementType, jsonData);
    }

    public <T> T readValue(Class<T> type, Object jsonData) {
        return readValue(type, (Class) null, jsonData);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v45, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: java.lang.Class<?>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: java.lang.Class<T>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: java.lang.Class<T>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: java.lang.Class<T>} */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T readValue(java.lang.Class<T> r26, java.lang.Class r27, java.lang.Object r28) {
        /*
            r25 = this;
            if (r28 != 0) goto L_0x0005
            r16 = 0
        L_0x0004:
            return r16
        L_0x0005:
            r0 = r28
            boolean r0 = r0 instanceof com.badlogic.gdx.utils.OrderedMap
            r21 = r0
            if (r21 == 0) goto L_0x0115
            r13 = r28
            com.badlogic.gdx.utils.OrderedMap r13 = (com.badlogic.gdx.utils.OrderedMap) r13
            r0 = r25
            java.lang.String r0 = r0.typeName
            r21 = r0
            if (r21 != 0) goto L_0x003f
            r5 = 0
        L_0x001a:
            if (r5 == 0) goto L_0x0020
            java.lang.Class r26 = java.lang.Class.forName(r5)     // Catch:{ ClassNotFoundException -> 0x0050 }
        L_0x0020:
            if (r26 == 0) goto L_0x00c7
            r0 = r25
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.Json$Serializer> r0 = r0.classToSerializer
            r21 = r0
            r0 = r21
            r1 = r26
            java.lang.Object r19 = r0.get(r1)
            com.badlogic.gdx.utils.Json$Serializer r19 = (com.badlogic.gdx.utils.Json.Serializer) r19
            if (r19 == 0) goto L_0x0069
            r0 = r19
            r1 = r25
            r2 = r26
            java.lang.Object r16 = r0.read(r1, r13, r2)
            goto L_0x0004
        L_0x003f:
            r0 = r25
            java.lang.String r0 = r0.typeName
            r21 = r0
            r0 = r21
            java.lang.Object r21 = r13.remove(r0)
            java.lang.String r21 = (java.lang.String) r21
            r5 = r21
            goto L_0x001a
        L_0x0050:
            r9 = move-exception
            r0 = r25
            com.badlogic.gdx.utils.ObjectMap<java.lang.String, java.lang.Class> r0 = r0.tagToClass
            r21 = r0
            r0 = r21
            java.lang.Object r26 = r0.get(r5)
            java.lang.Class r26 = (java.lang.Class) r26
            if (r26 != 0) goto L_0x0020
            com.badlogic.gdx.utils.SerializationException r21 = new com.badlogic.gdx.utils.SerializationException
            r0 = r21
            r0.<init>((java.lang.Throwable) r9)
            throw r21
        L_0x0069:
            java.lang.Object r17 = r25.newInstance(r26)
            r0 = r17
            boolean r0 = r0 instanceof com.badlogic.gdx.utils.Json.Serializable
            r21 = r0
            if (r21 == 0) goto L_0x0083
            r21 = r17
            com.badlogic.gdx.utils.Json$Serializable r21 = (com.badlogic.gdx.utils.Json.Serializable) r21
            r0 = r21
            r1 = r25
            r0.read(r1, r13)
            r16 = r17
            goto L_0x0004
        L_0x0083:
            r0 = r17
            boolean r0 = r0 instanceof java.util.HashMap
            r21 = r0
            if (r21 == 0) goto L_0x00cc
            r18 = r17
            java.util.HashMap r18 = (java.util.HashMap) r18
            com.badlogic.gdx.utils.ObjectMap$Entries r21 = r13.entries()
            java.util.Iterator r12 = r21.iterator()
        L_0x0097:
            boolean r21 = r12.hasNext()
            if (r21 == 0) goto L_0x00c3
            java.lang.Object r8 = r12.next()
            com.badlogic.gdx.utils.ObjectMap$Entry r8 = (com.badlogic.gdx.utils.ObjectMap.Entry) r8
            K r0 = r8.key
            r21 = r0
            r22 = 0
            V r0 = r8.value
            r23 = r0
            r0 = r25
            r1 = r27
            r2 = r22
            r3 = r23
            java.lang.Object r22 = r0.readValue(r1, (java.lang.Class) r2, (java.lang.Object) r3)
            r0 = r18
            r1 = r21
            r2 = r22
            r0.put(r1, r2)
            goto L_0x0097
        L_0x00c3:
            r16 = r18
            goto L_0x0004
        L_0x00c7:
            com.badlogic.gdx.utils.OrderedMap r17 = new com.badlogic.gdx.utils.OrderedMap
            r17.<init>()
        L_0x00cc:
            r0 = r17
            boolean r0 = r0 instanceof com.badlogic.gdx.utils.ObjectMap
            r21 = r0
            if (r21 == 0) goto L_0x010a
            r18 = r17
            com.badlogic.gdx.utils.ObjectMap r18 = (com.badlogic.gdx.utils.ObjectMap) r18
            com.badlogic.gdx.utils.Array r21 = r13.orderedKeys()
            java.util.Iterator r12 = r21.iterator()
        L_0x00e0:
            boolean r21 = r12.hasNext()
            if (r21 == 0) goto L_0x0106
            java.lang.Object r14 = r12.next()
            java.lang.String r14 = (java.lang.String) r14
            r21 = 0
            java.lang.Object r22 = r13.get(r14)
            r0 = r25
            r1 = r27
            r2 = r21
            r3 = r22
            java.lang.Object r21 = r0.readValue(r1, (java.lang.Class) r2, (java.lang.Object) r3)
            r0 = r18
            r1 = r21
            r0.put(r14, r1)
            goto L_0x00e0
        L_0x0106:
            r16 = r18
            goto L_0x0004
        L_0x010a:
            r0 = r25
            r1 = r17
            r0.readFields(r1, r13)
            r16 = r17
            goto L_0x0004
        L_0x0115:
            if (r26 == 0) goto L_0x0137
            r0 = r25
            com.badlogic.gdx.utils.ObjectMap<java.lang.Class, com.badlogic.gdx.utils.Json$Serializer> r0 = r0.classToSerializer
            r21 = r0
            r0 = r21
            r1 = r26
            java.lang.Object r19 = r0.get(r1)
            com.badlogic.gdx.utils.Json$Serializer r19 = (com.badlogic.gdx.utils.Json.Serializer) r19
            if (r19 == 0) goto L_0x0137
            r0 = r19
            r1 = r25
            r2 = r28
            r3 = r26
            java.lang.Object r16 = r0.read(r1, r2, r3)
            goto L_0x0004
        L_0x0137:
            r0 = r28
            boolean r0 = r0 instanceof com.badlogic.gdx.utils.Array
            r21 = r0
            if (r21 == 0) goto L_0x023f
            r4 = r28
            com.badlogic.gdx.utils.Array r4 = (com.badlogic.gdx.utils.Array) r4
            if (r26 == 0) goto L_0x0151
            java.lang.Class<com.badlogic.gdx.utils.Array> r21 = com.badlogic.gdx.utils.Array.class
            r0 = r21
            r1 = r26
            boolean r21 = r0.isAssignableFrom(r1)
            if (r21 == 0) goto L_0x018d
        L_0x0151:
            if (r26 != 0) goto L_0x0184
            com.badlogic.gdx.utils.Array r16 = new com.badlogic.gdx.utils.Array
            r16.<init>()
        L_0x0158:
            int r0 = r4.size
            r21 = r0
            r0 = r16
            r1 = r21
            r0.ensureCapacity(r1)
            r11 = 0
            int r15 = r4.size
        L_0x0166:
            if (r11 >= r15) goto L_0x0004
            r21 = 0
            java.lang.Object r22 = r4.get(r11)
            r0 = r25
            r1 = r27
            r2 = r21
            r3 = r22
            java.lang.Object r21 = r0.readValue(r1, (java.lang.Class) r2, (java.lang.Object) r3)
            r0 = r16
            r1 = r21
            r0.add(r1)
            int r11 = r11 + 1
            goto L_0x0166
        L_0x0184:
            java.lang.Object r21 = r25.newInstance(r26)
            com.badlogic.gdx.utils.Array r21 = (com.badlogic.gdx.utils.Array) r21
            r16 = r21
            goto L_0x0158
        L_0x018d:
            java.lang.Class<java.util.ArrayList> r21 = java.util.ArrayList.class
            r0 = r21
            r1 = r26
            boolean r21 = r0.isAssignableFrom(r1)
            if (r21 == 0) goto L_0x01d5
            if (r26 != 0) goto L_0x01cc
            java.util.ArrayList r16 = new java.util.ArrayList
            r16.<init>()
        L_0x01a0:
            int r0 = r4.size
            r21 = r0
            r0 = r16
            r1 = r21
            r0.ensureCapacity(r1)
            r11 = 0
            int r15 = r4.size
        L_0x01ae:
            if (r11 >= r15) goto L_0x0004
            r21 = 0
            java.lang.Object r22 = r4.get(r11)
            r0 = r25
            r1 = r27
            r2 = r21
            r3 = r22
            java.lang.Object r21 = r0.readValue(r1, (java.lang.Class) r2, (java.lang.Object) r3)
            r0 = r16
            r1 = r21
            r0.add(r1)
            int r11 = r11 + 1
            goto L_0x01ae
        L_0x01cc:
            java.lang.Object r21 = r25.newInstance(r26)
            java.util.ArrayList r21 = (java.util.ArrayList) r21
            r16 = r21
            goto L_0x01a0
        L_0x01d5:
            boolean r21 = r26.isArray()
            if (r21 == 0) goto L_0x020e
            java.lang.Class r6 = r26.getComponentType()
            if (r27 != 0) goto L_0x01e3
            r27 = r6
        L_0x01e3:
            int r0 = r4.size
            r21 = r0
            r0 = r21
            java.lang.Object r16 = java.lang.reflect.Array.newInstance(r6, r0)
            r11 = 0
            int r15 = r4.size
        L_0x01f0:
            if (r11 >= r15) goto L_0x0004
            r21 = 0
            java.lang.Object r22 = r4.get(r11)
            r0 = r25
            r1 = r27
            r2 = r21
            r3 = r22
            java.lang.Object r21 = r0.readValue(r1, (java.lang.Class) r2, (java.lang.Object) r3)
            r0 = r16
            r1 = r21
            java.lang.reflect.Array.set(r0, r11, r1)
            int r11 = r11 + 1
            goto L_0x01f0
        L_0x020e:
            com.badlogic.gdx.utils.SerializationException r21 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r22 = new java.lang.StringBuilder
            r22.<init>()
            java.lang.String r23 = "Unable to convert value to required type: "
            java.lang.StringBuilder r22 = r22.append(r23)
            r0 = r22
            r1 = r28
            java.lang.StringBuilder r22 = r0.append(r1)
            java.lang.String r23 = " ("
            java.lang.StringBuilder r22 = r22.append(r23)
            java.lang.String r23 = r26.getName()
            java.lang.StringBuilder r22 = r22.append(r23)
            java.lang.String r23 = ")"
            java.lang.StringBuilder r22 = r22.append(r23)
            java.lang.String r22 = r22.toString()
            r21.<init>((java.lang.String) r22)
            throw r21
        L_0x023f:
            r0 = r28
            boolean r0 = r0 instanceof java.lang.Float
            r21 = r0
            if (r21 == 0) goto L_0x02e8
            r10 = r28
            java.lang.Float r10 = (java.lang.Float) r10
            if (r26 == 0) goto L_0x025d
            java.lang.Class r21 = java.lang.Float.TYPE     // Catch:{ NumberFormatException -> 0x02e3 }
            r0 = r26
            r1 = r21
            if (r0 == r1) goto L_0x025d
            java.lang.Class<java.lang.Float> r21 = java.lang.Float.class
            r0 = r26
            r1 = r21
            if (r0 != r1) goto L_0x0261
        L_0x025d:
            r16 = r10
            goto L_0x0004
        L_0x0261:
            java.lang.Class r21 = java.lang.Integer.TYPE     // Catch:{ NumberFormatException -> 0x02e3 }
            r0 = r26
            r1 = r21
            if (r0 == r1) goto L_0x0271
            java.lang.Class<java.lang.Integer> r21 = java.lang.Integer.class
            r0 = r26
            r1 = r21
            if (r0 != r1) goto L_0x027b
        L_0x0271:
            int r21 = r10.intValue()     // Catch:{ NumberFormatException -> 0x02e3 }
            java.lang.Integer r16 = java.lang.Integer.valueOf(r21)     // Catch:{ NumberFormatException -> 0x02e3 }
            goto L_0x0004
        L_0x027b:
            java.lang.Class r21 = java.lang.Long.TYPE     // Catch:{ NumberFormatException -> 0x02e3 }
            r0 = r26
            r1 = r21
            if (r0 == r1) goto L_0x028b
            java.lang.Class<java.lang.Long> r21 = java.lang.Long.class
            r0 = r26
            r1 = r21
            if (r0 != r1) goto L_0x0295
        L_0x028b:
            long r21 = r10.longValue()     // Catch:{ NumberFormatException -> 0x02e3 }
            java.lang.Long r16 = java.lang.Long.valueOf(r21)     // Catch:{ NumberFormatException -> 0x02e3 }
            goto L_0x0004
        L_0x0295:
            java.lang.Class r21 = java.lang.Double.TYPE     // Catch:{ NumberFormatException -> 0x02e3 }
            r0 = r26
            r1 = r21
            if (r0 == r1) goto L_0x02a5
            java.lang.Class<java.lang.Double> r21 = java.lang.Double.class
            r0 = r26
            r1 = r21
            if (r0 != r1) goto L_0x02af
        L_0x02a5:
            double r21 = r10.doubleValue()     // Catch:{ NumberFormatException -> 0x02e3 }
            java.lang.Double r16 = java.lang.Double.valueOf(r21)     // Catch:{ NumberFormatException -> 0x02e3 }
            goto L_0x0004
        L_0x02af:
            java.lang.Class r21 = java.lang.Short.TYPE     // Catch:{ NumberFormatException -> 0x02e3 }
            r0 = r26
            r1 = r21
            if (r0 == r1) goto L_0x02bf
            java.lang.Class<java.lang.Short> r21 = java.lang.Short.class
            r0 = r26
            r1 = r21
            if (r0 != r1) goto L_0x02c9
        L_0x02bf:
            short r21 = r10.shortValue()     // Catch:{ NumberFormatException -> 0x02e3 }
            java.lang.Short r16 = java.lang.Short.valueOf(r21)     // Catch:{ NumberFormatException -> 0x02e3 }
            goto L_0x0004
        L_0x02c9:
            java.lang.Class r21 = java.lang.Byte.TYPE     // Catch:{ NumberFormatException -> 0x02e3 }
            r0 = r26
            r1 = r21
            if (r0 == r1) goto L_0x02d9
            java.lang.Class<java.lang.Byte> r21 = java.lang.Byte.class
            r0 = r26
            r1 = r21
            if (r0 != r1) goto L_0x02e4
        L_0x02d9:
            byte r21 = r10.byteValue()     // Catch:{ NumberFormatException -> 0x02e3 }
            java.lang.Byte r16 = java.lang.Byte.valueOf(r21)     // Catch:{ NumberFormatException -> 0x02e3 }
            goto L_0x0004
        L_0x02e3:
            r21 = move-exception
        L_0x02e4:
            java.lang.String r28 = java.lang.String.valueOf(r28)
        L_0x02e8:
            r0 = r28
            boolean r0 = r0 instanceof java.lang.Boolean
            r21 = r0
            if (r21 == 0) goto L_0x043d
            java.lang.String r28 = java.lang.String.valueOf(r28)
            r21 = r28
        L_0x02f6:
            r0 = r21
            boolean r0 = r0 instanceof java.lang.String
            r22 = r0
            if (r22 == 0) goto L_0x0439
            r20 = r21
            java.lang.String r20 = (java.lang.String) r20
            if (r26 == 0) goto L_0x030c
            java.lang.Class<java.lang.String> r22 = java.lang.String.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x0310
        L_0x030c:
            r16 = r21
            goto L_0x0004
        L_0x0310:
            java.lang.Class r22 = java.lang.Integer.TYPE     // Catch:{ NumberFormatException -> 0x0394 }
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x0320
            java.lang.Class<java.lang.Integer> r22 = java.lang.Integer.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x0326
        L_0x0320:
            java.lang.Integer r16 = java.lang.Integer.valueOf(r20)     // Catch:{ NumberFormatException -> 0x0394 }
            goto L_0x0004
        L_0x0326:
            java.lang.Class r22 = java.lang.Float.TYPE     // Catch:{ NumberFormatException -> 0x0394 }
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x0336
            java.lang.Class<java.lang.Float> r22 = java.lang.Float.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x033c
        L_0x0336:
            java.lang.Float r16 = java.lang.Float.valueOf(r20)     // Catch:{ NumberFormatException -> 0x0394 }
            goto L_0x0004
        L_0x033c:
            java.lang.Class r22 = java.lang.Long.TYPE     // Catch:{ NumberFormatException -> 0x0394 }
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x034c
            java.lang.Class<java.lang.Long> r22 = java.lang.Long.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x0352
        L_0x034c:
            java.lang.Long r16 = java.lang.Long.valueOf(r20)     // Catch:{ NumberFormatException -> 0x0394 }
            goto L_0x0004
        L_0x0352:
            java.lang.Class r22 = java.lang.Double.TYPE     // Catch:{ NumberFormatException -> 0x0394 }
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x0362
            java.lang.Class<java.lang.Double> r22 = java.lang.Double.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x0368
        L_0x0362:
            java.lang.Double r16 = java.lang.Double.valueOf(r20)     // Catch:{ NumberFormatException -> 0x0394 }
            goto L_0x0004
        L_0x0368:
            java.lang.Class r22 = java.lang.Short.TYPE     // Catch:{ NumberFormatException -> 0x0394 }
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x0378
            java.lang.Class<java.lang.Short> r22 = java.lang.Short.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x037e
        L_0x0378:
            java.lang.Short r16 = java.lang.Short.valueOf(r20)     // Catch:{ NumberFormatException -> 0x0394 }
            goto L_0x0004
        L_0x037e:
            java.lang.Class r22 = java.lang.Byte.TYPE     // Catch:{ NumberFormatException -> 0x0394 }
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x038e
            java.lang.Class<java.lang.Byte> r22 = java.lang.Byte.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x0395
        L_0x038e:
            java.lang.Byte r16 = java.lang.Byte.valueOf(r20)     // Catch:{ NumberFormatException -> 0x0394 }
            goto L_0x0004
        L_0x0394:
            r22 = move-exception
        L_0x0395:
            java.lang.Class r22 = java.lang.Boolean.TYPE
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x03a5
            java.lang.Class<java.lang.Boolean> r22 = java.lang.Boolean.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x03ab
        L_0x03a5:
            java.lang.Boolean r16 = java.lang.Boolean.valueOf(r20)
            goto L_0x0004
        L_0x03ab:
            java.lang.Class r22 = java.lang.Character.TYPE
            r0 = r26
            r1 = r22
            if (r0 == r1) goto L_0x03bb
            java.lang.Class<java.lang.Character> r22 = java.lang.Character.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x03c7
        L_0x03bb:
            r21 = 0
            char r21 = r20.charAt(r21)
            java.lang.Character r16 = java.lang.Character.valueOf(r21)
            goto L_0x0004
        L_0x03c7:
            boolean r22 = r26.isEnum()
            if (r22 == 0) goto L_0x03ec
            java.lang.Object[] r7 = r26.getEnumConstants()
            r11 = 0
            int r15 = r7.length
        L_0x03d3:
            if (r11 >= r15) goto L_0x03ec
            r22 = r7[r11]
            java.lang.String r22 = r22.toString()
            r0 = r20
            r1 = r22
            boolean r22 = r0.equals(r1)
            if (r22 == 0) goto L_0x03e9
            r16 = r7[r11]
            goto L_0x0004
        L_0x03e9:
            int r11 = r11 + 1
            goto L_0x03d3
        L_0x03ec:
            java.lang.Class<java.lang.CharSequence> r22 = java.lang.CharSequence.class
            r0 = r26
            r1 = r22
            if (r0 != r1) goto L_0x03f8
            r16 = r20
            goto L_0x0004
        L_0x03f8:
            com.badlogic.gdx.utils.SerializationException r22 = new com.badlogic.gdx.utils.SerializationException
            java.lang.StringBuilder r23 = new java.lang.StringBuilder
            r23.<init>()
            java.lang.String r24 = "Unable to convert value to required type: "
            java.lang.StringBuilder r23 = r23.append(r24)
            r0 = r23
            r1 = r21
            java.lang.StringBuilder r21 = r0.append(r1)
            java.lang.String r23 = " ("
            r0 = r21
            r1 = r23
            java.lang.StringBuilder r21 = r0.append(r1)
            java.lang.String r23 = r26.getName()
            r0 = r21
            r1 = r23
            java.lang.StringBuilder r21 = r0.append(r1)
            java.lang.String r23 = ")"
            r0 = r21
            r1 = r23
            java.lang.StringBuilder r21 = r0.append(r1)
            java.lang.String r21 = r21.toString()
            r0 = r22
            r1 = r21
            r0.<init>((java.lang.String) r1)
            throw r22
        L_0x0439:
            r16 = 0
            goto L_0x0004
        L_0x043d:
            r21 = r28
            goto L_0x02f6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Json.readValue(java.lang.Class, java.lang.Class, java.lang.Object):java.lang.Object");
    }

    private String convertToString(Object object) {
        if (object instanceof Class) {
            return ((Class) object).getName();
        }
        return String.valueOf(object);
    }

    private Object newInstance(Class type) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            ex = e;
            try {
                Constructor constructor = type.getDeclaredConstructor(new Class[0]);
                constructor.setAccessible(true);
                return constructor.newInstance(new Object[0]);
            } catch (SecurityException e2) {
            } catch (NoSuchMethodException e3) {
                if (type.isArray()) {
                    throw new SerializationException("Encountered JSON object when expected array of type: " + type.getName(), ex);
                } else if (!type.isMemberClass() || Modifier.isStatic(type.getModifiers())) {
                    throw new SerializationException("Class cannot be created (missing no-arg constructor): " + type.getName(), ex);
                } else {
                    throw new SerializationException("Class cannot be created (non-static member class): " + type.getName(), ex);
                }
            } catch (Exception privateConstructorException) {
                ex = privateConstructorException;
            }
        }
        throw new SerializationException("Error constructing instance of class: " + type.getName(), ex);
    }

    public String prettyPrint(Object object) {
        return prettyPrint(object, 0);
    }

    public String prettyPrint(String json) {
        return prettyPrint(json, 0);
    }

    public String prettyPrint(Object object, int singleLineColumns) {
        return prettyPrint(toJson(object), singleLineColumns);
    }

    public String prettyPrint(String json, int singleLineColumns) {
        StringBuilder buffer = new StringBuilder(512);
        prettyPrint(new JsonReader().parse(json), buffer, 0, singleLineColumns);
        return buffer.toString();
    }

    private void prettyPrint(Object object, StringBuilder buffer, int indent, int singleLineColumns) {
        if (object instanceof OrderedMap) {
            OrderedMap<String, ?> map = (OrderedMap) object;
            if (map.size == 0) {
                buffer.append("{}");
                return;
            }
            boolean newLines = !isFlat((ObjectMap<?, ?>) map);
            int start = buffer.length();
            loop0:
            while (true) {
                buffer.append(newLines ? "{\n" : "{ ");
                int i = 0;
                Iterator i$ = map.orderedKeys().iterator();
                while (i$.hasNext()) {
                    String key = i$.next();
                    if (newLines) {
                        indent(indent, buffer);
                    }
                    buffer.append(this.outputType.quoteName(key));
                    buffer.append(": ");
                    prettyPrint(map.get(key), buffer, indent + 1, singleLineColumns);
                    int i2 = i + 1;
                    if (i < map.size - 1) {
                        buffer.append(",");
                    }
                    buffer.append(newLines ? 10 : ' ');
                    if (newLines || buffer.length() - start <= singleLineColumns) {
                        i = i2;
                    } else {
                        buffer.setLength(start);
                        newLines = true;
                    }
                }
                break loop0;
            }
            if (newLines) {
                indent(indent - 1, buffer);
            }
            buffer.append('}');
        } else if (object instanceof Array) {
            Array array = (Array) object;
            if (array.size == 0) {
                buffer.append("[]");
                return;
            }
            boolean newLines2 = !isFlat(array);
            int start2 = buffer.length();
            loop2:
            while (true) {
                buffer.append(newLines2 ? "[\n" : "[ ");
                int i3 = 0;
                int n = array.size;
                while (i3 < n) {
                    if (newLines2) {
                        indent(indent, buffer);
                    }
                    prettyPrint(array.get(i3), buffer, indent + 1, singleLineColumns);
                    if (i3 < array.size - 1) {
                        buffer.append(",");
                    }
                    buffer.append(newLines2 ? 10 : ' ');
                    if (newLines2 || buffer.length() - start2 <= singleLineColumns) {
                        i3++;
                    } else {
                        buffer.setLength(start2);
                        newLines2 = true;
                    }
                }
                break loop2;
            }
            if (newLines2) {
                indent(indent - 1, buffer);
            }
            buffer.append(']');
        } else if (object instanceof String) {
            buffer.append(this.outputType.quoteValue((String) object));
        } else if (object instanceof Float) {
            Float floatValue = (Float) object;
            int intValue = floatValue.intValue();
            if (floatValue.floatValue() - ((float) intValue) == 0.0f) {
                object = Integer.valueOf(intValue);
            }
            buffer.append(object);
        } else if (object instanceof Boolean) {
            buffer.append(object);
        } else if (object == null) {
            buffer.append("null");
        } else {
            throw new SerializationException("Unknown object type: " + object.getClass());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x000f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isFlat(com.badlogic.gdx.utils.ObjectMap<?, ?> r4) {
        /*
            r2 = 0
            com.badlogic.gdx.utils.ObjectMap$Entries r3 = r4.entries()
            java.util.Iterator r1 = r3.iterator()
        L_0x0009:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto L_0x0023
            java.lang.Object r0 = r1.next()
            com.badlogic.gdx.utils.ObjectMap$Entry r0 = (com.badlogic.gdx.utils.ObjectMap.Entry) r0
            V r3 = r0.value
            boolean r3 = r3 instanceof com.badlogic.gdx.utils.ObjectMap
            if (r3 == 0) goto L_0x001c
        L_0x001b:
            return r2
        L_0x001c:
            V r3 = r0.value
            boolean r3 = r3 instanceof com.badlogic.gdx.utils.Array
            if (r3 == 0) goto L_0x0009
            goto L_0x001b
        L_0x0023:
            r2 = 1
            goto L_0x001b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.Json.isFlat(com.badlogic.gdx.utils.ObjectMap):boolean");
    }

    private static boolean isFlat(Array array) {
        int n = array.size;
        for (int i = 0; i < n; i++) {
            Object value = array.get(i);
            if ((value instanceof ObjectMap) || (value instanceof Array)) {
                return false;
            }
        }
        return true;
    }

    private static void indent(int count, StringBuilder buffer) {
        for (int i = 0; i < count; i++) {
            buffer.append(9);
        }
    }

    private static class FieldMetadata {
        Class elementType;
        Field field;

        public FieldMetadata(Field field2) {
            this.field = field2;
            Type genericType = field2.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
                if (actualTypes.length == 1) {
                    Type actualType = actualTypes[0];
                    if (actualType instanceof Class) {
                        this.elementType = (Class) actualType;
                    } else if (actualType instanceof ParameterizedType) {
                        this.elementType = (Class) ((ParameterizedType) actualType).getRawType();
                    }
                }
            }
        }
    }

    public static abstract class ReadOnlySerializer<T> implements Serializer<T> {
        public abstract T read(Json json, Object obj, Class cls);

        public void write(Json json, T t, Class knownType) {
        }
    }
}
