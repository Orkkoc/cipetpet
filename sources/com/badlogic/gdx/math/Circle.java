package com.badlogic.gdx.math;

import java.io.Serializable;

public class Circle implements Serializable {
    public float radius;

    /* renamed from: x */
    public float f146x;

    /* renamed from: y */
    public float f147y;

    public Circle() {
    }

    public Circle(float x, float y, float radius2) {
        this.f146x = x;
        this.f147y = y;
        this.radius = radius2;
    }

    public Circle(Vector2 position, float radius2) {
        this.f146x = position.f165x;
        this.f147y = position.f166y;
        this.radius = radius2;
    }

    public boolean contains(float x, float y) {
        float x2 = this.f146x - x;
        float y2 = this.f147y - y;
        return (x2 * x2) + (y2 * y2) <= this.radius * this.radius;
    }

    public boolean contains(Vector2 point) {
        float x = this.f146x - point.f165x;
        float y = this.f147y - point.f166y;
        return (x * x) + (y * y) <= this.radius * this.radius;
    }

    public void set(float x, float y, float radius2) {
        this.f146x = x;
        this.f147y = y;
        this.radius = radius2;
    }
}
