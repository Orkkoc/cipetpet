package com.p002me.mygdxgame;

import com.badlogic.gdx.math.MathUtils;

/* renamed from: com.me.mygdxgame.Enemy */
public class Enemy {
    public int attack;
    public String direction = "left";
    public int duty;
    public int fullHealth;
    public int health;
    public boolean isAlive;
    public String type;

    /* renamed from: x */
    public int f196x;

    /* renamed from: y */
    public int f197y;

    public Enemy(int x, int y, String type2) {
        this.f196x = x;
        this.f197y = y;
        this.type = type2;
        this.duty = 0;
        if (this.type.equals("a")) {
            this.health = 1000;
            this.fullHealth = 1000;
            this.attack = 5;
        }
    }

    public void update() {
        if (this.duty == 0) {
            if (this.f196x > 630) {
                this.f196x--;
                this.direction = "left";
            } else if (this.f196x < 630) {
                this.f196x++;
                this.direction = "right";
            }
            if (this.f196x == 630) {
                this.duty = 1;
            }
        }
        if (this.duty == 1) {
            if (this.f197y > 350) {
                this.f197y--;
                this.direction = "down";
            } else if (this.f197y < 350) {
                this.f197y++;
                this.direction = "up";
            }
            if (this.f197y == 350) {
                if (this.f196x < 820) {
                    this.f196x++;
                    this.direction = "right";
                } else if (this.f196x > 820) {
                    this.f196x--;
                    this.direction = "left";
                }
                if (this.f196x == 820) {
                    this.duty = 2;
                }
            }
        }
        if (this.duty == 2) {
            if (this.f196x < AntGame.ants.get(0).f193x) {
                this.f196x++;
                Ant ant = AntGame.ants.get(0);
                ant.f193x--;
                this.direction = "right";
            } else if (this.f196x > AntGame.ants.get(0).f193x) {
                this.f196x--;
                AntGame.ants.get(0).f193x++;
                this.direction = "left";
            }
            if (this.f197y < AntGame.ants.get(0).f194y) {
                this.f197y++;
                Ant ant2 = AntGame.ants.get(0);
                ant2.f194y--;
                this.direction = "up";
            } else if (this.f197y > AntGame.ants.get(0).f194y) {
                this.f197y--;
                AntGame.ants.get(0).f194y++;
                this.direction = "down";
            }
            if (this.f196x < AntGame.ants.get(0).f193x + 32 && this.f196x > AntGame.ants.get(0).f193x - 32 && this.f197y < AntGame.ants.get(0).f194y + 32 && this.f197y > AntGame.ants.get(0).f194y - 32) {
                AntGame.ants.get(0).health -= this.attack;
                if (AntGame.ants.get(0).health > 0) {
                    this.health -= AntGame.ants.get(0).attack;
                }
            }
        }
        if (this.duty == 3) {
            for (int i = 0; i < AntGame.ants.size(); i++) {
                int x = AntGame.ants.get(i).f193x;
                int y = AntGame.ants.get(i).f194y;
                if (this.f196x >= x + 8 || this.f196x <= x - 8 || this.f197y >= y + 8 || this.f197y <= y - 8) {
                    if (this.f197y >= 570) {
                        if (this.f196x != 630) {
                            this.duty = 0;
                        }
                        if (this.f196x == 630) {
                            this.duty = 1;
                        }
                    } else {
                        this.duty = 1;
                    }
                } else if (MathUtils.random(0, 100) >= 75) {
                    this.health -= AntGame.ants.get(i).attack;
                } else {
                    AntGame.ants.get(i).health -= this.attack;
                }
            }
        }
    }
}
