package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.io.File;

public class ParticleEffect implements Disposable {
    private final Array<ParticleEmitter> emitters;

    public ParticleEffect() {
        this.emitters = new Array<>(8);
    }

    public ParticleEffect(ParticleEffect effect) {
        this.emitters = new Array<>(true, effect.emitters.size);
        int n = effect.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.add(new ParticleEmitter(effect.emitters.get(i)));
        }
    }

    public void start() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).start();
        }
    }

    public void reset() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).reset();
        }
    }

    public void update(float delta) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).update(delta);
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).draw(spriteBatch);
        }
    }

    public void draw(SpriteBatch spriteBatch, float delta) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).draw(spriteBatch, delta);
        }
    }

    public void allowCompletion() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).allowCompletion();
        }
    }

    public boolean isComplete() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            if (!this.emitters.get(i).isComplete()) {
                return false;
            }
        }
        return true;
    }

    public void setDuration(int duration) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            emitter.setContinuous(false);
            emitter.duration = (float) duration;
            emitter.durationTimer = 0.0f;
        }
    }

    public void setPosition(float x, float y) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).setPosition(x, y);
        }
    }

    public void setFlip(boolean flipX, boolean flipY) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).setFlip(flipX, flipY);
        }
    }

    public void flipY() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).flipY();
        }
    }

    public Array<ParticleEmitter> getEmitters() {
        return this.emitters;
    }

    public ParticleEmitter findEmitter(String name) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            if (emitter.getName().equals(name)) {
                return emitter;
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0069 A[SYNTHETIC, Splitter:B:21:0x0069] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void save(java.io.File r12) {
        /*
            r11 = this;
            r6 = 0
            java.io.FileWriter r7 = new java.io.FileWriter     // Catch:{ IOException -> 0x004c }
            r7.<init>(r12)     // Catch:{ IOException -> 0x004c }
            r3 = 0
            r2 = 0
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.ParticleEmitter> r8 = r11.emitters     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            int r5 = r8.size     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            r4 = r3
        L_0x000d:
            if (r2 >= r5) goto L_0x0046
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.ParticleEmitter> r8 = r11.emitters     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.Object r0 = r8.get(r2)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            com.badlogic.gdx.graphics.g2d.ParticleEmitter r0 = (com.badlogic.gdx.graphics.g2d.ParticleEmitter) r0     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            int r3 = r4 + 1
            if (r4 <= 0) goto L_0x0020
            java.lang.String r8 = "\n\n"
            r7.write(r8)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
        L_0x0020:
            r0.save(r7)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.String r8 = "- Image Path -\n"
            r7.write(r8)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            r8.<init>()     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.String r9 = r0.getImagePath()     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.String r9 = "\n"
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            r7.write(r8)     // Catch:{ IOException -> 0x0074, all -> 0x0071 }
            int r2 = r2 + 1
            r4 = r3
            goto L_0x000d
        L_0x0046:
            if (r7 == 0) goto L_0x004b
            r7.close()     // Catch:{ IOException -> 0x006d }
        L_0x004b:
            return
        L_0x004c:
            r1 = move-exception
        L_0x004d:
            com.badlogic.gdx.utils.GdxRuntimeException r8 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0066 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0066 }
            r9.<init>()     // Catch:{ all -> 0x0066 }
            java.lang.String r10 = "Error saving effect: "
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch:{ all -> 0x0066 }
            java.lang.StringBuilder r9 = r9.append(r12)     // Catch:{ all -> 0x0066 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0066 }
            r8.<init>(r9, r1)     // Catch:{ all -> 0x0066 }
            throw r8     // Catch:{ all -> 0x0066 }
        L_0x0066:
            r8 = move-exception
        L_0x0067:
            if (r6 == 0) goto L_0x006c
            r6.close()     // Catch:{ IOException -> 0x006f }
        L_0x006c:
            throw r8
        L_0x006d:
            r8 = move-exception
            goto L_0x004b
        L_0x006f:
            r9 = move-exception
            goto L_0x006c
        L_0x0071:
            r8 = move-exception
            r6 = r7
            goto L_0x0067
        L_0x0074:
            r1 = move-exception
            r6 = r7
            goto L_0x004d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.ParticleEffect.save(java.io.File):void");
    }

    public void load(FileHandle effectFile, FileHandle imagesDir) {
        loadEmitters(effectFile);
        loadEmitterImages(imagesDir);
    }

    public void load(FileHandle effectFile, TextureAtlas atlas) {
        loadEmitters(effectFile);
        loadEmitterImages(atlas);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x005a A[SYNTHETIC, Splitter:B:18:0x005a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadEmitters(com.badlogic.gdx.files.FileHandle r9) {
        /*
            r8 = this;
            java.io.InputStream r2 = r9.read()
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.ParticleEmitter> r5 = r8.emitters
            r5.clear()
            r3 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ IOException -> 0x003d }
            java.io.InputStreamReader r5 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x003d }
            r5.<init>(r2)     // Catch:{ IOException -> 0x003d }
            r6 = 512(0x200, float:7.175E-43)
            r4.<init>(r5, r6)     // Catch:{ IOException -> 0x003d }
        L_0x0016:
            com.badlogic.gdx.graphics.g2d.ParticleEmitter r0 = new com.badlogic.gdx.graphics.g2d.ParticleEmitter     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            r0.<init>((java.io.BufferedReader) r4)     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            r4.readLine()     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            java.lang.String r5 = r4.readLine()     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            r0.setImagePath(r5)     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.ParticleEmitter> r5 = r8.emitters     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            r5.add(r0)     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            java.lang.String r5 = r4.readLine()     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            if (r5 != 0) goto L_0x0036
        L_0x0030:
            if (r4 == 0) goto L_0x0035
            r4.close()     // Catch:{ IOException -> 0x005e }
        L_0x0035:
            return
        L_0x0036:
            java.lang.String r5 = r4.readLine()     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            if (r5 != 0) goto L_0x0016
            goto L_0x0030
        L_0x003d:
            r1 = move-exception
        L_0x003e:
            com.badlogic.gdx.utils.GdxRuntimeException r5 = new com.badlogic.gdx.utils.GdxRuntimeException     // Catch:{ all -> 0x0057 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0057 }
            r6.<init>()     // Catch:{ all -> 0x0057 }
            java.lang.String r7 = "Error loading effect: "
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ all -> 0x0057 }
            java.lang.StringBuilder r6 = r6.append(r9)     // Catch:{ all -> 0x0057 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0057 }
            r5.<init>(r6, r1)     // Catch:{ all -> 0x0057 }
            throw r5     // Catch:{ all -> 0x0057 }
        L_0x0057:
            r5 = move-exception
        L_0x0058:
            if (r3 == 0) goto L_0x005d
            r3.close()     // Catch:{ IOException -> 0x0060 }
        L_0x005d:
            throw r5
        L_0x005e:
            r5 = move-exception
            goto L_0x0035
        L_0x0060:
            r6 = move-exception
            goto L_0x005d
        L_0x0062:
            r5 = move-exception
            r3 = r4
            goto L_0x0058
        L_0x0065:
            r1 = move-exception
            r3 = r4
            goto L_0x003e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.ParticleEffect.loadEmitters(com.badlogic.gdx.files.FileHandle):void");
    }

    public void loadEmitterImages(TextureAtlas atlas) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            String imagePath = emitter.getImagePath();
            if (imagePath != null) {
                String imageName = new File(imagePath.replace('\\', '/')).getName();
                int lastDotIndex = imageName.lastIndexOf(46);
                if (lastDotIndex != -1) {
                    imageName = imageName.substring(0, lastDotIndex);
                }
                Sprite sprite = atlas.createSprite(imageName);
                if (sprite == null) {
                    throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
                }
                emitter.setSprite(sprite);
            }
        }
    }

    public void loadEmitterImages(FileHandle imagesDir) {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            ParticleEmitter emitter = this.emitters.get(i);
            String imagePath = emitter.getImagePath();
            if (imagePath != null) {
                emitter.setSprite(new Sprite(loadTexture(imagesDir.child(new File(imagePath.replace('\\', '/')).getName()))));
            }
        }
    }

    /* access modifiers changed from: protected */
    public Texture loadTexture(FileHandle file) {
        return new Texture(file, false);
    }

    public void dispose() {
        int n = this.emitters.size;
        for (int i = 0; i < n; i++) {
            this.emitters.get(i).getSprite().getTexture().dispose();
        }
    }
}
