package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<PooledEffect> {
    private final ParticleEffect effect;

    public ParticleEffectPool(ParticleEffect effect2, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.effect = effect2;
    }

    /* access modifiers changed from: protected */
    public PooledEffect newObject() {
        return new PooledEffect(this.effect);
    }

    public PooledEffect obtain() {
        PooledEffect effect2 = (PooledEffect) super.obtain();
        effect2.reset();
        return effect2;
    }

    public class PooledEffect extends ParticleEffect {
        PooledEffect(ParticleEffect effect) {
            super(effect);
        }

        public void reset() {
            super.reset();
        }

        public void free() {
            ParticleEffectPool.this.free(this);
        }
    }
}
