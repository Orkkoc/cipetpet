package com.p002me.mygdxgame;

/* renamed from: com.me.mygdxgame.Ant */
public class Ant {
    public boolean atWar;
    public int attack;
    public String direction = "left";
    public int duty;
    public int fed = 0;
    public int fullHealth;
    public int health;
    public boolean isAlive;
    public boolean isBusy;
    public boolean isClicked;
    public int previousDuty;
    public int speedX;
    public int speedY;
    public String type;

    /* renamed from: x */
    public int f193x;

    /* renamed from: y */
    public int f194y;

    public Ant(int x, int y, String type2, boolean enemy) {
        this.f193x = x;
        this.f194y = y;
        this.type = type2;
        this.isAlive = true;
        this.isBusy = false;
        this.isClicked = false;
        this.atWar = false;
        this.duty = 0;
        this.speedX = 0;
        this.speedY = 0;
        if (type2.equals("k")) {
            this.health = 2000;
            this.fullHealth = 2000;
            this.attack = 20;
        } else if (type2.equals("i")) {
            this.health = 600;
            this.fullHealth = 600;
            this.attack = 5;
        } else if (type2.equals("a")) {
            this.health = 1000;
            this.fullHealth = 1000;
            this.attack = 10;
        }
    }

    public void update() {
        if (AntGame.enemies.size() > 0 && !this.atWar) {
            this.previousDuty = this.duty;
            this.duty = 9;
            this.isBusy = false;
            this.atWar = true;
        }
        if (this.type.equals("k") && this.duty == 0) {
            if (this.f193x < 900) {
                this.f193x++;
            }
            if (this.f193x > 900) {
                this.f193x--;
            }
            if (this.f194y > 380) {
                this.f194y--;
            }
            if (this.f194y < 380) {
                this.f194y++;
            }
        }
        if (this.duty == 1) {
            if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            } else if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 630) {
                if (this.f194y < 570) {
                    this.f194y++;
                    this.direction = "up";
                } else {
                    this.f194y--;
                    this.direction = "down";
                }
            }
            if (this.f193x == 630 && this.f194y == 570) {
                this.duty = 2;
            }
        }
        if (this.duty == 2) {
            int nearest = -1;
            if (AntGame.foods.size() > 0) {
                int distance = 1280;
                for (int i = 0; i < AntGame.foods.size(); i++) {
                    if (Math.abs(AntGame.foods.get(i).f198x - this.f193x) < distance) {
                        nearest = i;
                        distance = Math.abs(AntGame.foods.get(i).f198x - this.f193x);
                    }
                }
            }
            if (nearest > -1) {
                if (AntGame.foods.get(nearest).f198x > this.f193x) {
                    this.f193x++;
                    this.direction = "right";
                } else if (AntGame.foods.get(nearest).f198x < this.f193x) {
                    this.f193x--;
                    this.direction = "left";
                }
                if (AntGame.foods.get(nearest).f198x == this.f193x) {
                    this.duty = 3;
                    AntGame.foods.remove(nearest);
                    this.isBusy = true;
                }
            }
        }
        if (this.duty == 3) {
            if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            } else if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            }
            if (this.f193x == 630 && this.f194y > 360) {
                this.f194y--;
                this.direction = "down";
            }
            if (this.f194y == 360) {
                this.duty = 4;
            }
        }
        if (this.duty == 4) {
            if (this.f193x > 450) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 450) {
                AntGame.foodDepo++;
                this.duty = 1;
                this.isBusy = false;
            }
        }
        if (this.duty == 5) {
            if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            } else if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            }
            if (this.f193x == 630) {
                if (this.f194y < 350) {
                    this.f194y++;
                    this.direction = "up";
                } else if (this.f194y > 350) {
                    this.f194y--;
                    this.direction = "down";
                }
                if (this.f194y == 350) {
                    this.duty = 6;
                }
            }
        }
        if (this.duty == 6) {
            if (this.f193x > 440) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 440 && AntGame.foodDepo > 0) {
                AntGame.foodDepo--;
                this.duty = 7;
                this.isBusy = true;
            }
        }
        if (this.duty == 7) {
            if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            } else if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 630) {
                if (this.f194y < 350) {
                    this.f194y++;
                    this.direction = "up";
                } else if (this.f194y > 350) {
                    this.f194y--;
                    this.direction = "down";
                }
                if (this.f194y == 350) {
                    this.duty = 8;
                }
            }
        }
        if (this.duty == 8) {
            if (this.f193x < 720) {
                this.f193x++;
                this.direction = "right";
            }
            if (this.f193x == 720) {
                AntGame.ants.get(0).fed++;
                this.duty = 6;
                this.isBusy = false;
            }
        }
        if (this.duty == 9) {
            if (AntGame.enemies.size() == 0) {
                this.atWar = false;
                if (this.type.equals("k")) {
                    this.duty = 0;
                } else if (this.previousDuty == 0) {
                    this.duty = 1;
                } else if (this.previousDuty == 11 || this.previousDuty == 12) {
                    this.previousDuty = 10;
                } else if (this.previousDuty == 15) {
                    this.previousDuty = 14;
                } else {
                    this.duty = this.previousDuty;
                }
            } else if (!this.type.equals("k")) {
                int x = AntGame.enemies.get(0).f196x;
                int y = AntGame.enemies.get(0).f197y;
                if (y >= 570) {
                    if (this.f194y < 570) {
                        if (this.f193x < 630) {
                            if (!this.type.equals("a")) {
                                this.f193x++;
                                this.direction = "right";
                            } else {
                                this.f193x++;
                                this.direction = "right";
                            }
                        } else if (this.f193x > 630) {
                            if (!this.type.equals("a")) {
                                this.f193x--;
                                this.direction = "left";
                            } else {
                                this.f193x--;
                                this.direction = "left";
                            }
                        } else if (!this.type.equals("a")) {
                            this.f194y++;
                            this.direction = "up";
                        } else {
                            this.f194y++;
                            this.direction = "up";
                        }
                    } else if (this.f193x < x) {
                        if (!this.type.equals("a")) {
                            this.f193x++;
                            this.direction = "right";
                        } else {
                            this.f193x++;
                            this.direction = "right";
                        }
                    } else if (this.f193x > x) {
                        if (!this.type.equals("a")) {
                            this.f193x--;
                            this.direction = "left";
                        } else {
                            this.f193x--;
                            this.direction = "left";
                        }
                    }
                } else if (x == 630) {
                    if (this.f193x < x) {
                        if (!this.type.equals("a")) {
                            this.f193x++;
                            this.direction = "right";
                        } else {
                            this.f193x++;
                            this.direction = "right";
                        }
                    } else if (this.f193x > x) {
                        if (!this.type.equals("a")) {
                            this.f193x--;
                            this.direction = "left";
                        } else {
                            this.f193x--;
                            this.direction = "left";
                        }
                    }
                    if (this.f193x == 630) {
                        if (this.f194y > y) {
                            if (!this.type.equals("a")) {
                                this.f194y--;
                                this.direction = "down";
                            } else {
                                this.f194y--;
                                this.direction = "down";
                            }
                        } else if (this.f194y < y) {
                            if (!this.type.equals("a")) {
                                this.f194y++;
                                this.direction = "up";
                            } else {
                                this.f194y++;
                                this.direction = "up";
                            }
                        }
                    }
                } else if (y == 480) {
                    if (this.f194y == 480) {
                        if (this.f193x <= x) {
                            if (!this.type.equals("a")) {
                                this.f193x++;
                                this.direction = "right";
                            } else {
                                this.f193x++;
                                this.direction = "right";
                            }
                        } else if (this.f193x >= x) {
                            if (!this.type.equals("a")) {
                                this.f193x--;
                                this.direction = "left";
                            } else {
                                this.f193x--;
                                this.direction = "left";
                            }
                        }
                    } else if (this.f194y <= y) {
                        if (!this.type.equals("a")) {
                            this.f194y++;
                            this.direction = "up";
                        } else {
                            this.f194y++;
                            this.direction = "up";
                        }
                    } else if (this.f194y >= y) {
                        if (!this.type.equals("a")) {
                            this.f194y--;
                            this.direction = "down";
                        } else {
                            this.f194y--;
                            this.direction = "down";
                        }
                    }
                }
                if (this.f193x < x + 8 && this.f193x > x - 8 && this.f194y < y + 8 && this.f194y > y - 8) {
                    AntGame.enemies.get(0).duty = 3;
                }
            }
        }
        if (this.duty == 10) {
            if (AntGame.digging > 100) {
                this.duty = 13;
            }
            if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            } else if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 630) {
                if (this.f194y < 80) {
                    this.f194y++;
                    this.direction = "up";
                } else {
                    this.f194y--;
                    this.direction = "down";
                }
            }
            if (this.f193x == 630 && this.f194y == 80) {
                this.duty = 11;
            }
        }
        if (this.duty == 11) {
            if (this.f193x > 580) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 580) {
                AntGame.digging += 25;
                if (AntGame.digging < 75) {
                    this.duty = 12;
                } else {
                    this.duty = 13;
                }
            }
        }
        if (this.duty == 12) {
            if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            } else if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 630) {
                if (this.f194y < 570) {
                    this.f194y++;
                    this.direction = "up";
                } else if (this.f194y > 570) {
                    this.f194y--;
                    this.direction = "down";
                }
            }
            if (this.f193x == 630 && this.f194y == 570) {
                this.duty = 10;
            }
        }
        if (this.duty == 13) {
            this.duty = 1;
        }
        if (this.duty == 14) {
            if (this.f193x < 630) {
                this.f193x++;
                this.direction = "right";
            } else if (this.f193x > 630) {
                this.f193x--;
                this.direction = "left";
            }
            if (this.f193x == 630) {
                if (this.f194y < 570) {
                    this.f194y++;
                    this.direction = "up";
                } else if (this.f194y > 570) {
                    this.f194y--;
                    this.direction = "down";
                }
            }
            if (this.f193x == 630 && this.f194y == 570) {
                this.duty = 15;
            }
        }
        if (this.duty != 15 || this.f194y != 570) {
            return;
        }
        if (this.f193x < 400) {
            this.f193x++;
            this.direction = "right";
        } else if (this.f193x > 400) {
            this.f193x--;
            this.direction = "left";
        }
    }
}
