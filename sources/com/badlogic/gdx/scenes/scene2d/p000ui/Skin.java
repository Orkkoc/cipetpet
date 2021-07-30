package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import java.lang.reflect.Method;
import java.util.Iterator;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Skin */
public class Skin implements Disposable {
    TextureAtlas atlas;
    ObjectMap<Class, ObjectMap<String, Object>> resources = new ObjectMap<>();

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Skin$TintedDrawable */
    public static class TintedDrawable {
        public Color color;
        public String name;
    }

    public Skin() {
    }

    public Skin(FileHandle skinFile) {
        FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
        if (atlasFile.exists()) {
            this.atlas = new TextureAtlas(atlasFile);
            addRegions(this.atlas);
        }
        load(skinFile);
    }

    public Skin(FileHandle skinFile, TextureAtlas atlas2) {
        this.atlas = atlas2;
        addRegions(atlas2);
        load(skinFile);
    }

    public Skin(TextureAtlas atlas2) {
        this.atlas = atlas2;
        addRegions(atlas2);
    }

    public void load(FileHandle skinFile) {
        try {
            getJsonLoader(skinFile).fromJson(Skin.class, skinFile);
        } catch (SerializationException ex) {
            throw new SerializationException("Error reading file: " + skinFile, ex);
        }
    }

    public void addRegions(TextureAtlas atlas2) {
        Array<TextureAtlas.AtlasRegion> regions = atlas2.getRegions();
        int n = regions.size;
        for (int i = 0; i < n; i++) {
            TextureAtlas.AtlasRegion region = regions.get(i);
            add(region.name, region, TextureRegion.class);
        }
    }

    public void add(String name, Object resource) {
        add(name, resource, resource.getClass());
    }

    public void add(String name, Object resource, Class type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null.");
        } else {
            ObjectMap<String, Object> typeResources = this.resources.get(type);
            if (typeResources == null) {
                typeResources = new ObjectMap<>();
                this.resources.put(type, typeResources);
            }
            typeResources.put(name, resource);
        }
    }

    public <T> T get(Class<T> type) {
        return get("default", type);
    }

    public <T> T get(String name, Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        } else if (type == Drawable.class) {
            return getDrawable(name);
        } else {
            if (type == TextureRegion.class) {
                return getRegion(name);
            }
            if (type == NinePatch.class) {
                return getPatch(name);
            }
            if (type == Sprite.class) {
                return getSprite(name);
            }
            ObjectMap<String, Object> typeResources = this.resources.get(type);
            if (typeResources == null) {
                throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
            }
            Object resource = typeResources.get(name);
            if (resource != null) {
                return resource;
            }
            throw new GdxRuntimeException("No " + type.getName() + " registered with name: " + name);
        }
    }

    public <T> T optional(String name, Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null.");
        } else if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        } else {
            ObjectMap<String, Object> typeResources = this.resources.get(type);
            if (typeResources == null) {
                return null;
            }
            return typeResources.get(name);
        }
    }

    public boolean has(String name, Class type) {
        ObjectMap<String, Object> typeResources = this.resources.get(type);
        if (typeResources == null) {
            return false;
        }
        return typeResources.containsKey(name);
    }

    public <T> ObjectMap<String, T> getAll(Class<T> type) {
        return this.resources.get(type);
    }

    public Color getColor(String name) {
        return (Color) get(name, Color.class);
    }

    public BitmapFont getFont(String name) {
        return (BitmapFont) get(name, BitmapFont.class);
    }

    public TextureRegion getRegion(String name) {
        TextureRegion region = (TextureRegion) optional(name, TextureRegion.class);
        if (region != null) {
            return region;
        }
        Texture texture = (Texture) optional(name, Texture.class);
        if (texture == null) {
            throw new GdxRuntimeException("No TextureRegion or Texture registered with name: " + name);
        }
        TextureRegion region2 = new TextureRegion(texture);
        add(name, region2, Texture.class);
        return region2;
    }

    public TiledDrawable getTiledDrawable(String name) {
        TiledDrawable tiled = (TiledDrawable) optional(name, TiledDrawable.class);
        if (tiled != null) {
            return tiled;
        }
        Drawable drawable = (Drawable) optional(name, Drawable.class);
        if (tiled == null) {
            TiledDrawable tiled2 = new TiledDrawable(getRegion(name));
            add(name, tiled2, TiledDrawable.class);
            return tiled2;
        } else if (drawable instanceof TiledDrawable) {
            return tiled;
        } else {
            throw new GdxRuntimeException("Drawable found but is not a TiledDrawable: " + name + ", " + drawable.getClass().getName());
        }
    }

    public NinePatch getPatch(String name) {
        NinePatch patch;
        int[] splits;
        NinePatch patch2 = (NinePatch) optional(name, NinePatch.class);
        if (patch2 != null) {
            NinePatch ninePatch = patch2;
            return patch2;
        }
        try {
            TextureRegion region = getRegion(name);
            if ((region instanceof TextureAtlas.AtlasRegion) && (splits = ((TextureAtlas.AtlasRegion) region).splits) != null) {
                NinePatch patch3 = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
                try {
                    int[] pads = ((TextureAtlas.AtlasRegion) region).pads;
                    if (pads != null) {
                        patch3.setPadding(pads[0], pads[1], pads[2], pads[3]);
                    }
                    patch2 = patch3;
                } catch (GdxRuntimeException e) {
                    throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
                }
            }
            if (patch2 == null) {
                patch = new NinePatch(region);
            } else {
                patch = patch2;
            }
            add(name, patch, NinePatch.class);
            return patch;
        } catch (GdxRuntimeException e2) {
            NinePatch ninePatch2 = patch2;
            throw new GdxRuntimeException("No NinePatch, TextureRegion, or Texture registered with name: " + name);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x002f A[SYNTHETIC, Splitter:B:14:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0058  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.badlogic.gdx.graphics.g2d.Sprite getSprite(java.lang.String r10) {
        /*
            r9 = this;
            java.lang.Class<com.badlogic.gdx.graphics.g2d.Sprite> r6 = com.badlogic.gdx.graphics.g2d.Sprite.class
            java.lang.Object r3 = r9.optional(r10, r6)
            com.badlogic.gdx.graphics.g2d.Sprite r3 = (com.badlogic.gdx.graphics.g2d.Sprite) r3
            if (r3 == 0) goto L_0x000c
            r4 = r3
        L_0x000b:
            return r4
        L_0x000c:
            com.badlogic.gdx.graphics.g2d.TextureRegion r5 = r9.getRegion(r10)     // Catch:{ GdxRuntimeException -> 0x003b }
            boolean r6 = r5 instanceof com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion     // Catch:{ GdxRuntimeException -> 0x003b }
            if (r6 == 0) goto L_0x005a
            r0 = r5
            com.badlogic.gdx.graphics.g2d.TextureAtlas$AtlasRegion r0 = (com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion) r0     // Catch:{ GdxRuntimeException -> 0x003b }
            r2 = r0
            boolean r6 = r2.rotate     // Catch:{ GdxRuntimeException -> 0x003b }
            if (r6 != 0) goto L_0x0028
            int r6 = r2.packedWidth     // Catch:{ GdxRuntimeException -> 0x003b }
            int r7 = r2.originalWidth     // Catch:{ GdxRuntimeException -> 0x003b }
            if (r6 != r7) goto L_0x0028
            int r6 = r2.packedHeight     // Catch:{ GdxRuntimeException -> 0x003b }
            int r7 = r2.originalHeight     // Catch:{ GdxRuntimeException -> 0x003b }
            if (r6 == r7) goto L_0x005a
        L_0x0028:
            com.badlogic.gdx.graphics.g2d.TextureAtlas$AtlasSprite r4 = new com.badlogic.gdx.graphics.g2d.TextureAtlas$AtlasSprite     // Catch:{ GdxRuntimeException -> 0x003b }
            r4.<init>((com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion) r2)     // Catch:{ GdxRuntimeException -> 0x003b }
        L_0x002d:
            if (r4 != 0) goto L_0x0058
            com.badlogic.gdx.graphics.g2d.Sprite r3 = new com.badlogic.gdx.graphics.g2d.Sprite     // Catch:{ GdxRuntimeException -> 0x0055 }
            r3.<init>((com.badlogic.gdx.graphics.g2d.TextureRegion) r5)     // Catch:{ GdxRuntimeException -> 0x0055 }
        L_0x0034:
            java.lang.Class<com.badlogic.gdx.graphics.g2d.NinePatch> r6 = com.badlogic.gdx.graphics.g2d.NinePatch.class
            r9.add(r10, r3, r6)     // Catch:{ GdxRuntimeException -> 0x003b }
            r4 = r3
            goto L_0x000b
        L_0x003b:
            r1 = move-exception
        L_0x003c:
            com.badlogic.gdx.utils.GdxRuntimeException r6 = new com.badlogic.gdx.utils.GdxRuntimeException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "No NinePatch, TextureRegion, or Texture registered with name: "
            java.lang.StringBuilder r7 = r7.append(r8)
            java.lang.StringBuilder r7 = r7.append(r10)
            java.lang.String r7 = r7.toString()
            r6.<init>((java.lang.String) r7)
            throw r6
        L_0x0055:
            r1 = move-exception
            r3 = r4
            goto L_0x003c
        L_0x0058:
            r3 = r4
            goto L_0x0034
        L_0x005a:
            r4 = r3
            goto L_0x002d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.p000ui.Skin.getSprite(java.lang.String):com.badlogic.gdx.graphics.g2d.Sprite");
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0033 A[SYNTHETIC, Splitter:B:13:0x0033] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0098  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.badlogic.gdx.scenes.scene2d.utils.Drawable getDrawable(java.lang.String r11) {
        /*
            r10 = this;
            java.lang.Class<com.badlogic.gdx.scenes.scene2d.utils.Drawable> r7 = com.badlogic.gdx.scenes.scene2d.utils.Drawable.class
            java.lang.Object r1 = r10.optional(r11, r7)
            com.badlogic.gdx.scenes.scene2d.utils.Drawable r1 = (com.badlogic.gdx.scenes.scene2d.utils.Drawable) r1
            if (r1 == 0) goto L_0x000c
            r2 = r1
        L_0x000b:
            return r2
        L_0x000c:
            java.lang.Class<com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable> r7 = com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable.class
            java.lang.Object r1 = r10.optional(r11, r7)
            com.badlogic.gdx.scenes.scene2d.utils.Drawable r1 = (com.badlogic.gdx.scenes.scene2d.utils.Drawable) r1
            if (r1 == 0) goto L_0x0018
            r2 = r1
            goto L_0x000b
        L_0x0018:
            com.badlogic.gdx.graphics.g2d.TextureRegion r6 = r10.getRegion(r11)     // Catch:{ GdxRuntimeException -> 0x0093 }
            boolean r7 = r6 instanceof com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion     // Catch:{ GdxRuntimeException -> 0x0093 }
            if (r7 == 0) goto L_0x009a
            r0 = r6
            com.badlogic.gdx.graphics.g2d.TextureAtlas$AtlasRegion r0 = (com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion) r0     // Catch:{ GdxRuntimeException -> 0x0093 }
            r4 = r0
            int[] r7 = r4.splits     // Catch:{ GdxRuntimeException -> 0x0093 }
            if (r7 == 0) goto L_0x0050
            com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable r2 = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable     // Catch:{ GdxRuntimeException -> 0x0093 }
            com.badlogic.gdx.graphics.g2d.NinePatch r7 = r10.getPatch(r11)     // Catch:{ GdxRuntimeException -> 0x0093 }
            r2.<init>((com.badlogic.gdx.graphics.g2d.NinePatch) r7)     // Catch:{ GdxRuntimeException -> 0x0093 }
        L_0x0031:
            if (r2 != 0) goto L_0x0098
            com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable r1 = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable     // Catch:{ GdxRuntimeException -> 0x0095 }
            r1.<init>((com.badlogic.gdx.graphics.g2d.TextureRegion) r6)     // Catch:{ GdxRuntimeException -> 0x0095 }
        L_0x0038:
            if (r1 != 0) goto L_0x0049
            java.lang.Class<com.badlogic.gdx.graphics.g2d.NinePatch> r7 = com.badlogic.gdx.graphics.g2d.NinePatch.class
            java.lang.Object r3 = r10.optional(r11, r7)
            com.badlogic.gdx.graphics.g2d.NinePatch r3 = (com.badlogic.gdx.graphics.g2d.NinePatch) r3
            if (r3 == 0) goto L_0x006a
            com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable r1 = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
            r1.<init>((com.badlogic.gdx.graphics.g2d.NinePatch) r3)
        L_0x0049:
            java.lang.Class<com.badlogic.gdx.scenes.scene2d.utils.Drawable> r7 = com.badlogic.gdx.scenes.scene2d.utils.Drawable.class
            r10.add(r11, r1, r7)
            r2 = r1
            goto L_0x000b
        L_0x0050:
            boolean r7 = r4.rotate     // Catch:{ GdxRuntimeException -> 0x0093 }
            if (r7 != 0) goto L_0x0060
            int r7 = r4.packedWidth     // Catch:{ GdxRuntimeException -> 0x0093 }
            int r8 = r4.originalWidth     // Catch:{ GdxRuntimeException -> 0x0093 }
            if (r7 != r8) goto L_0x0060
            int r7 = r4.packedHeight     // Catch:{ GdxRuntimeException -> 0x0093 }
            int r8 = r4.originalHeight     // Catch:{ GdxRuntimeException -> 0x0093 }
            if (r7 == r8) goto L_0x009a
        L_0x0060:
            com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable r2 = new com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable     // Catch:{ GdxRuntimeException -> 0x0093 }
            com.badlogic.gdx.graphics.g2d.Sprite r7 = r10.getSprite(r11)     // Catch:{ GdxRuntimeException -> 0x0093 }
            r2.<init>((com.badlogic.gdx.graphics.g2d.Sprite) r7)     // Catch:{ GdxRuntimeException -> 0x0093 }
            goto L_0x0031
        L_0x006a:
            java.lang.Class<com.badlogic.gdx.graphics.g2d.Sprite> r7 = com.badlogic.gdx.graphics.g2d.Sprite.class
            java.lang.Object r5 = r10.optional(r11, r7)
            com.badlogic.gdx.graphics.g2d.Sprite r5 = (com.badlogic.gdx.graphics.g2d.Sprite) r5
            if (r5 == 0) goto L_0x007a
            com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable r1 = new com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
            r1.<init>((com.badlogic.gdx.graphics.g2d.Sprite) r5)
            goto L_0x0049
        L_0x007a:
            com.badlogic.gdx.utils.GdxRuntimeException r7 = new com.badlogic.gdx.utils.GdxRuntimeException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "No Drawable, NinePatch, TextureRegion, Texture, or Sprite registered with name: "
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.StringBuilder r8 = r8.append(r11)
            java.lang.String r8 = r8.toString()
            r7.<init>((java.lang.String) r8)
            throw r7
        L_0x0093:
            r7 = move-exception
            goto L_0x0038
        L_0x0095:
            r7 = move-exception
            r1 = r2
            goto L_0x0038
        L_0x0098:
            r1 = r2
            goto L_0x0038
        L_0x009a:
            r2 = r1
            goto L_0x0031
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.p000ui.Skin.getDrawable(java.lang.String):com.badlogic.gdx.scenes.scene2d.utils.Drawable");
    }

    public String find(Object resource) {
        if (resource == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        ObjectMap<String, Object> typeResources = this.resources.get(resource.getClass());
        if (typeResources == null) {
            return null;
        }
        return typeResources.findKey(resource, true);
    }

    public Drawable newDrawable(String name) {
        return newDrawable(getDrawable(name));
    }

    public Drawable newDrawable(String name, float r, float g, float b, float a) {
        return newDrawable(getDrawable(name), new Color(r, g, b, a));
    }

    public Drawable newDrawable(String name, Color tint) {
        return newDrawable(getDrawable(name), tint);
    }

    public Drawable newDrawable(Drawable drawable) {
        if (drawable instanceof TextureRegionDrawable) {
            return new TextureRegionDrawable((TextureRegionDrawable) drawable);
        }
        if (drawable instanceof NinePatchDrawable) {
            return new NinePatchDrawable((NinePatchDrawable) drawable);
        }
        if (drawable instanceof SpriteDrawable) {
            return new SpriteDrawable((SpriteDrawable) drawable);
        }
        throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
    }

    public Drawable newDrawable(Drawable drawable, float r, float g, float b, float a) {
        return newDrawable(drawable, new Color(r, g, b, a));
    }

    public Drawable newDrawable(Drawable drawable, Color tint) {
        Sprite sprite;
        Sprite sprite2;
        if (drawable instanceof TextureRegionDrawable) {
            TextureRegion region = ((TextureRegionDrawable) drawable).getRegion();
            if (region instanceof TextureAtlas.AtlasRegion) {
                sprite2 = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) region);
            } else {
                sprite2 = new Sprite(region);
            }
            sprite2.setColor(tint);
            return new SpriteDrawable(sprite2);
        } else if (drawable instanceof NinePatchDrawable) {
            NinePatchDrawable patchDrawable = new NinePatchDrawable((NinePatchDrawable) drawable);
            patchDrawable.setPatch(new NinePatch(patchDrawable.getPatch(), tint));
            return patchDrawable;
        } else if (drawable instanceof SpriteDrawable) {
            SpriteDrawable spriteDrawable = new SpriteDrawable((SpriteDrawable) drawable);
            Sprite sprite3 = spriteDrawable.getSprite();
            if (sprite3 instanceof TextureAtlas.AtlasSprite) {
                sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasSprite) sprite3);
            } else {
                sprite = new Sprite(sprite3);
            }
            sprite.setColor(tint);
            spriteDrawable.setSprite(sprite);
            return spriteDrawable;
        } else {
            throw new GdxRuntimeException("Unable to copy, unknown drawable type: " + drawable.getClass());
        }
    }

    public void setEnabled(Actor actor, boolean enabled) {
        Method method = findMethod(actor.getClass(), "getStyle");
        if (method != null) {
            try {
                Object style = method.invoke(actor, new Object[0]);
                String name = find(style);
                if (name != null) {
                    Object style2 = get(name.replace("-disabled", "") + (enabled ? "" : "-disabled"), style.getClass());
                    Method method2 = findMethod(actor.getClass(), "setStyle");
                    if (method2 != null) {
                        try {
                            method2.invoke(actor, new Object[]{style2});
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }

    public void dispose() {
        if (this.atlas != null) {
            this.atlas.dispose();
        }
        Iterator<ObjectMap<String, Object>> it = this.resources.values().iterator();
        while (it.hasNext()) {
            Iterator i$ = it.next().values().iterator();
            while (i$.hasNext()) {
                Object resource = i$.next();
                if (resource instanceof Disposable) {
                    ((Disposable) resource).dispose();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Json getJsonLoader(final FileHandle skinFile) {
        Json json = new Json() {
            public <T> T readValue(Class<T> type, Class elementType, Object jsonData) {
                if (!(jsonData instanceof String) || CharSequence.class.isAssignableFrom(type)) {
                    return super.readValue(type, elementType, jsonData);
                }
                return Skin.this.get((String) jsonData, type);
            }
        };
        json.setTypeName((String) null);
        json.setUsePrototypes(false);
        json.setSerializer(Skin.class, new Json.ReadOnlySerializer<Skin>() {
            public Skin read(Json json, Object jsonData, Class ignored) {
                Iterator i$ = ((ObjectMap) jsonData).entries().iterator();
                while (i$.hasNext()) {
                    ObjectMap.Entry<String, ObjectMap> typeEntry = i$.next();
                    String className = (String) typeEntry.key;
                    try {
                        readNamedObjects(json, Class.forName(className), typeEntry.value);
                    } catch (ClassNotFoundException ex) {
                        throw new SerializationException((Throwable) ex);
                    }
                }
                return this;
            }

            private void readNamedObjects(Json json, Class type, ObjectMap<String, ObjectMap> valueMap) {
                Class addType;
                if (type == TintedDrawable.class) {
                    addType = Drawable.class;
                } else {
                    addType = type;
                }
                Iterator i$ = valueMap.entries().iterator();
                while (i$.hasNext()) {
                    ObjectMap.Entry<String, ObjectMap> valueEntry = i$.next();
                    String name = (String) valueEntry.key;
                    Object object = json.readValue(type, valueEntry.value);
                    if (object != null) {
                        try {
                            Skin.this.add(name, object, addType);
                        } catch (Exception ex) {
                            throw new SerializationException("Error reading " + type.getSimpleName() + ": " + ((String) valueEntry.key), ex);
                        }
                    }
                }
            }
        });
        json.setSerializer(BitmapFont.class, new Json.ReadOnlySerializer<BitmapFont>() {
            public BitmapFont read(Json json, Object jsonData, Class type) {
                String path = (String) json.readValue("file", String.class, jsonData);
                FileHandle fontFile = skinFile.parent().child(path);
                if (!fontFile.exists()) {
                    fontFile = Gdx.files.internal(path);
                }
                if (!fontFile.exists()) {
                    throw new SerializationException("Font file not found: " + fontFile);
                }
                String regionName = fontFile.nameWithoutExtension();
                try {
                    TextureRegion region = (TextureRegion) this.optional(regionName, TextureRegion.class);
                    if (region != null) {
                        return new BitmapFont(fontFile, region, false);
                    }
                    FileHandle imageFile = fontFile.parent().child(regionName + ".png");
                    if (imageFile.exists()) {
                        return new BitmapFont(fontFile, imageFile, false);
                    }
                    return new BitmapFont(fontFile, false);
                } catch (RuntimeException ex) {
                    throw new SerializationException("Error loading bitmap font: " + fontFile, ex);
                }
            }
        });
        json.setSerializer(Color.class, new Json.ReadOnlySerializer<Color>() {
            public Color read(Json json, Object jsonData, Class type) {
                if (jsonData instanceof String) {
                    return (Color) Skin.this.get((String) jsonData, Color.class);
                }
                ObjectMap objectMap = (ObjectMap) jsonData;
                String hex = (String) json.readValue("hex", String.class, null, jsonData);
                if (hex != null) {
                    return Color.valueOf(hex);
                }
                return new Color(((Float) json.readValue("r", Float.TYPE, Float.valueOf(0.0f), jsonData)).floatValue(), ((Float) json.readValue("g", Float.TYPE, Float.valueOf(0.0f), jsonData)).floatValue(), ((Float) json.readValue("b", Float.TYPE, Float.valueOf(0.0f), jsonData)).floatValue(), ((Float) json.readValue("a", Float.TYPE, Float.valueOf(1.0f), jsonData)).floatValue());
            }
        });
        json.setSerializer(TintedDrawable.class, new Json.ReadOnlySerializer() {
            public Object read(Json json, Object jsonData, Class type) {
                return Skin.this.newDrawable((String) json.readValue("name", String.class, jsonData), (Color) json.readValue("color", Color.class, jsonData));
            }
        });
        return json;
    }

    private static Method findMethod(Class type, String name) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
}
