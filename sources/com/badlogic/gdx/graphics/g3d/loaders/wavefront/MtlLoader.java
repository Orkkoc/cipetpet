package com.badlogic.gdx.graphics.g3d.loaders.wavefront;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute;
import java.util.ArrayList;
import java.util.Iterator;

/* compiled from: ObjLoader */
class MtlLoader {
    private static AssetManager assetManager;
    private static Texture emptyTexture = null;
    private ArrayList<Material> materials = new ArrayList<>();

    public MtlLoader() {
        if (emptyTexture == null) {
            assetManager = new AssetManager();
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB888);
            pm.setColor(0.5f, 0.5f, 0.5f, 1.0f);
            pm.fill();
            emptyTexture = new Texture(pm, false);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: java.lang.Class<com.badlogic.gdx.graphics.Texture>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v49, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: com.badlogic.gdx.graphics.Texture} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void load(java.lang.String r28, com.badlogic.gdx.files.FileHandle r29) {
        /*
            r27 = this;
            java.lang.String r6 = "default"
            com.badlogic.gdx.graphics.Color r7 = com.badlogic.gdx.graphics.Color.WHITE
            com.badlogic.gdx.graphics.Color r16 = com.badlogic.gdx.graphics.Color.WHITE
            com.badlogic.gdx.graphics.Texture r19 = emptyTexture
            com.badlogic.gdx.Files r22 = com.badlogic.gdx.Gdx.files
            r0 = r22
            r1 = r28
            com.badlogic.gdx.files.FileHandle r10 = r0.internal(r1)
            if (r10 == 0) goto L_0x001a
            boolean r22 = r10.exists()
            if (r22 != 0) goto L_0x001b
        L_0x001a:
            return
        L_0x001b:
            java.io.BufferedReader r15 = new java.io.BufferedReader
            java.io.InputStreamReader r22 = new java.io.InputStreamReader
            java.io.InputStream r23 = r10.read()
            r22.<init>(r23)
            r23 = 4096(0x1000, float:5.74E-42)
            r0 = r22
            r1 = r23
            r15.<init>(r0, r1)
            r17 = r16
            r8 = r7
        L_0x0032:
            java.lang.String r12 = r15.readLine()     // Catch:{ IOException -> 0x01db }
            if (r12 == 0) goto L_0x01e5
            int r22 = r12.length()     // Catch:{ IOException -> 0x01db }
            if (r22 <= 0) goto L_0x005a
            r22 = 0
            r0 = r22
            char r22 = r12.charAt(r0)     // Catch:{ IOException -> 0x01db }
            r23 = 9
            r0 = r22
            r1 = r23
            if (r0 != r1) goto L_0x005a
            r22 = 1
            r0 = r22
            java.lang.String r22 = r12.substring(r0)     // Catch:{ IOException -> 0x01db }
            java.lang.String r12 = r22.trim()     // Catch:{ IOException -> 0x01db }
        L_0x005a:
            java.lang.String r22 = "\\s+"
            r0 = r22
            java.lang.String[] r21 = r12.split(r0)     // Catch:{ IOException -> 0x01db }
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            int r22 = r22.length()     // Catch:{ IOException -> 0x01db }
            if (r22 == 0) goto L_0x0032
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            r23 = 0
            char r22 = r22.charAt(r23)     // Catch:{ IOException -> 0x01db }
            r23 = 35
            r0 = r22
            r1 = r23
            if (r0 == r1) goto L_0x0032
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            java.lang.String r22 = r22.toLowerCase()     // Catch:{ IOException -> 0x01db }
            java.lang.String r23 = "newmtl"
            boolean r22 = r22.equals(r23)     // Catch:{ IOException -> 0x01db }
            if (r22 == 0) goto L_0x0106
            com.badlogic.gdx.graphics.g3d.materials.Material r13 = new com.badlogic.gdx.graphics.g3d.materials.Material     // Catch:{ IOException -> 0x01db }
            r22 = 3
            r0 = r22
            com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute[] r0 = new com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute[r0]     // Catch:{ IOException -> 0x01db }
            r22 = r0
            r23 = 0
            com.badlogic.gdx.graphics.g3d.materials.TextureAttribute r24 = new com.badlogic.gdx.graphics.g3d.materials.TextureAttribute     // Catch:{ IOException -> 0x01db }
            r25 = 0
            java.lang.String r26 = "diffuseTexture"
            r0 = r24
            r1 = r19
            r2 = r25
            r3 = r26
            r0.<init>(r1, r2, r3)     // Catch:{ IOException -> 0x01db }
            r22[r23] = r24     // Catch:{ IOException -> 0x01db }
            r23 = 1
            com.badlogic.gdx.graphics.g3d.materials.ColorAttribute r24 = new com.badlogic.gdx.graphics.g3d.materials.ColorAttribute     // Catch:{ IOException -> 0x01db }
            java.lang.String r25 = "diffuseColor"
            r0 = r24
            r1 = r25
            r0.<init>(r8, r1)     // Catch:{ IOException -> 0x01db }
            r22[r23] = r24     // Catch:{ IOException -> 0x01db }
            r23 = 2
            com.badlogic.gdx.graphics.g3d.materials.ColorAttribute r24 = new com.badlogic.gdx.graphics.g3d.materials.ColorAttribute     // Catch:{ IOException -> 0x01db }
            java.lang.String r25 = "specularColor"
            r0 = r24
            r1 = r17
            r2 = r25
            r0.<init>(r1, r2)     // Catch:{ IOException -> 0x01db }
            r22[r23] = r24     // Catch:{ IOException -> 0x01db }
            r0 = r22
            r13.<init>((java.lang.String) r6, (com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute[]) r0)     // Catch:{ IOException -> 0x01db }
            r0 = r27
            java.util.ArrayList<com.badlogic.gdx.graphics.g3d.materials.Material> r0 = r0.materials     // Catch:{ IOException -> 0x01db }
            r22 = r0
            r0 = r22
            r0.add(r13)     // Catch:{ IOException -> 0x01db }
            r0 = r21
            int r0 = r0.length     // Catch:{ IOException -> 0x01db }
            r22 = r0
            r23 = 1
            r0 = r22
            r1 = r23
            if (r0 <= r1) goto L_0x0103
            r22 = 1
            r6 = r21[r22]     // Catch:{ IOException -> 0x01db }
            r22 = 46
            r23 = 95
            r0 = r22
            r1 = r23
            java.lang.String r6 = r6.replace(r0, r1)     // Catch:{ IOException -> 0x01db }
        L_0x00fa:
            com.badlogic.gdx.graphics.Color r7 = com.badlogic.gdx.graphics.Color.WHITE     // Catch:{ IOException -> 0x01db }
            com.badlogic.gdx.graphics.Color r16 = com.badlogic.gdx.graphics.Color.WHITE     // Catch:{ IOException -> 0x023c }
            r17 = r16
            r8 = r7
            goto L_0x0032
        L_0x0103:
            java.lang.String r6 = "default"
            goto L_0x00fa
        L_0x0106:
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            java.lang.String r22 = r22.toLowerCase()     // Catch:{ IOException -> 0x01db }
            java.lang.String r23 = "kd"
            boolean r22 = r22.equals(r23)     // Catch:{ IOException -> 0x01db }
            if (r22 != 0) goto L_0x0126
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            java.lang.String r22 = r22.toLowerCase()     // Catch:{ IOException -> 0x01db }
            java.lang.String r23 = "ks"
            boolean r22 = r22.equals(r23)     // Catch:{ IOException -> 0x01db }
            if (r22 == 0) goto L_0x0180
        L_0x0126:
            r22 = 1
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            float r14 = java.lang.Float.parseFloat(r22)     // Catch:{ IOException -> 0x01db }
            r22 = 2
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            float r11 = java.lang.Float.parseFloat(r22)     // Catch:{ IOException -> 0x01db }
            r22 = 3
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            float r5 = java.lang.Float.parseFloat(r22)     // Catch:{ IOException -> 0x01db }
            r4 = 1065353216(0x3f800000, float:1.0)
            r0 = r21
            int r0 = r0.length     // Catch:{ IOException -> 0x01db }
            r22 = r0
            r23 = 4
            r0 = r22
            r1 = r23
            if (r0 <= r1) goto L_0x0155
            r22 = 4
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            float r4 = java.lang.Float.parseFloat(r22)     // Catch:{ IOException -> 0x01db }
        L_0x0155:
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            java.lang.String r22 = r22.toLowerCase()     // Catch:{ IOException -> 0x01db }
            java.lang.String r23 = "kd"
            boolean r22 = r22.equals(r23)     // Catch:{ IOException -> 0x01db }
            if (r22 == 0) goto L_0x0174
            com.badlogic.gdx.graphics.Color r7 = new com.badlogic.gdx.graphics.Color     // Catch:{ IOException -> 0x01db }
            r7.<init>()     // Catch:{ IOException -> 0x01db }
            r7.set(r14, r11, r5, r4)     // Catch:{ IOException -> 0x023c }
            r16 = r17
        L_0x016f:
            r17 = r16
            r8 = r7
            goto L_0x0032
        L_0x0174:
            com.badlogic.gdx.graphics.Color r16 = new com.badlogic.gdx.graphics.Color     // Catch:{ IOException -> 0x01db }
            r16.<init>()     // Catch:{ IOException -> 0x01db }
            r0 = r16
            r0.set(r14, r11, r5, r4)     // Catch:{ IOException -> 0x0240 }
            r7 = r8
            goto L_0x016f
        L_0x0180:
            r22 = 0
            r22 = r21[r22]     // Catch:{ IOException -> 0x01db }
            java.lang.String r22 = r22.toLowerCase()     // Catch:{ IOException -> 0x01db }
            java.lang.String r23 = "map_kd"
            boolean r22 = r22.equals(r23)     // Catch:{ IOException -> 0x01db }
            if (r22 == 0) goto L_0x0032
            r22 = 1
            r20 = r21[r22]     // Catch:{ IOException -> 0x01db }
            int r22 = r20.length()     // Catch:{ IOException -> 0x01db }
            if (r22 <= 0) goto L_0x01e1
            r0 = r29
            r1 = r20
            com.badlogic.gdx.files.FileHandle r22 = r0.child(r1)     // Catch:{ IOException -> 0x01db }
            java.lang.String r18 = r22.toString()     // Catch:{ IOException -> 0x01db }
            com.badlogic.gdx.assets.AssetManager r22 = assetManager     // Catch:{ IOException -> 0x01db }
            java.lang.Class<com.badlogic.gdx.graphics.Texture> r23 = com.badlogic.gdx.graphics.Texture.class
            r0 = r22
            r1 = r18
            r2 = r23
            r0.load(r1, r2)     // Catch:{ IOException -> 0x01db }
            com.badlogic.gdx.assets.AssetManager r22 = assetManager     // Catch:{ IOException -> 0x01db }
            r22.finishLoading()     // Catch:{ IOException -> 0x01db }
            com.badlogic.gdx.assets.AssetManager r22 = assetManager     // Catch:{ IOException -> 0x01db }
            java.lang.Class<com.badlogic.gdx.graphics.Texture> r23 = com.badlogic.gdx.graphics.Texture.class
            r0 = r22
            r1 = r18
            r2 = r23
            java.lang.Object r22 = r0.get(r1, r2)     // Catch:{ IOException -> 0x01db }
            r0 = r22
            com.badlogic.gdx.graphics.Texture r0 = (com.badlogic.gdx.graphics.Texture) r0     // Catch:{ IOException -> 0x01db }
            r19 = r0
            com.badlogic.gdx.graphics.Texture$TextureFilter r22 = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear     // Catch:{ IOException -> 0x01db }
            com.badlogic.gdx.graphics.Texture$TextureFilter r23 = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear     // Catch:{ IOException -> 0x01db }
            r0 = r19
            r1 = r22
            r2 = r23
            r0.setFilter(r1, r2)     // Catch:{ IOException -> 0x01db }
            goto L_0x0032
        L_0x01db:
            r9 = move-exception
            r16 = r17
            r7 = r8
        L_0x01df:
            goto L_0x001a
        L_0x01e1:
            com.badlogic.gdx.graphics.Texture r19 = emptyTexture     // Catch:{ IOException -> 0x01db }
            goto L_0x0032
        L_0x01e5:
            r15.close()     // Catch:{ IOException -> 0x01db }
            com.badlogic.gdx.graphics.g3d.materials.Material r13 = new com.badlogic.gdx.graphics.g3d.materials.Material
            r22 = 3
            r0 = r22
            com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute[] r0 = new com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute[r0]
            r22 = r0
            r23 = 0
            com.badlogic.gdx.graphics.g3d.materials.TextureAttribute r24 = new com.badlogic.gdx.graphics.g3d.materials.TextureAttribute
            r25 = 0
            java.lang.String r26 = "diffuseTexture"
            r0 = r24
            r1 = r19
            r2 = r25
            r3 = r26
            r0.<init>(r1, r2, r3)
            r22[r23] = r24
            r23 = 1
            com.badlogic.gdx.graphics.g3d.materials.ColorAttribute r24 = new com.badlogic.gdx.graphics.g3d.materials.ColorAttribute
            java.lang.String r25 = "diffuseColor"
            r0 = r24
            r1 = r25
            r0.<init>(r8, r1)
            r22[r23] = r24
            r23 = 2
            com.badlogic.gdx.graphics.g3d.materials.ColorAttribute r24 = new com.badlogic.gdx.graphics.g3d.materials.ColorAttribute
            java.lang.String r25 = "specularColor"
            r0 = r24
            r1 = r17
            r2 = r25
            r0.<init>(r1, r2)
            r22[r23] = r24
            r0 = r22
            r13.<init>((java.lang.String) r6, (com.badlogic.gdx.graphics.g3d.materials.MaterialAttribute[]) r0)
            r0 = r27
            java.util.ArrayList<com.badlogic.gdx.graphics.g3d.materials.Material> r0 = r0.materials
            r22 = r0
            r0 = r22
            r0.add(r13)
            r16 = r17
            r7 = r8
            goto L_0x001a
        L_0x023c:
            r9 = move-exception
            r16 = r17
            goto L_0x01df
        L_0x0240:
            r9 = move-exception
            r7 = r8
            goto L_0x01df
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g3d.loaders.wavefront.MtlLoader.load(java.lang.String, com.badlogic.gdx.files.FileHandle):void");
    }

    public Material getMaterial(String name) {
        String name2 = name.replace('.', '_');
        Iterator i$ = this.materials.iterator();
        while (i$.hasNext()) {
            Material mat = i$.next();
            if (mat.getName().equals(name2)) {
                return mat;
            }
        }
        return new Material("default", new MaterialAttribute[0]);
    }
}
