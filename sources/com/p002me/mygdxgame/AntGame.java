package com.p002me.mygdxgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/* renamed from: com.me.mygdxgame.AntGame */
public class AntGame extends Game implements ApplicationListener {
    private static final int FRAME_COLS = 2;
    private static final int FRAME_ROWS = 2;
    public static ArrayList<Ant> ants;
    public static int digging = 0;
    public static ArrayList<Enemy> enemies;
    public static int foodDepo = 0;
    public static ArrayList<Food> foods;
    private Texture alert;
    private boolean anyClicked = false;
    private Animation askerDownAnimation;
    private TextureRegion askerDownCurrentFrame;
    private TextureRegion[] askerDownFrames;
    private Texture askerDownT;
    private float askerDownTime;
    private Animation askerLeftAnimation;
    private TextureRegion askerLeftCurrentFrame;
    private TextureRegion[] askerLeftFrames;
    private float askerLeftTime;
    private Animation askerRightAnimation;
    private TextureRegion askerRightCurrentFrame;
    private TextureRegion[] askerRightFrames;
    private Texture askerRightT;
    private float askerRightTime;
    private Texture askerT;
    private Animation askerUpAnimation;
    private TextureRegion askerUpCurrentFrame;
    private TextureRegion[] askerUpFrames;
    private Texture askerUpT;
    private float askerUpTime;
    private Texture audioOffT;
    private boolean audioOn = true;
    private Texture audioOnT;
    private SpriteBatch batch;

    /* renamed from: bg */
    private Texture f195bg;
    private Texture bg2;
    private Texture bg3;
    private Texture bg4;
    private Texture bgYedek;
    private OrthographicCamera camera;
    private Texture[] credits;
    private float creditsTime;
    private boolean digged = false;
    private String ekran = "menu";
    private Animation enemyDownAnimation;
    private TextureRegion enemyDownCurrentFrame;
    private TextureRegion[] enemyDownFrames;
    private Texture enemyDownT;
    private float enemyDownTime;
    private Animation enemyLeftAnimation;
    private TextureRegion enemyLeftCurrentFrame;
    private TextureRegion[] enemyLeftFrames;
    private float enemyLeftTime;
    private Animation enemyRightAnimation;
    private TextureRegion enemyRightCurrentFrame;
    private TextureRegion[] enemyRightFrames;
    private Texture enemyRightT;
    private float enemyRightTime;
    private float enemySpawner;
    private Texture enemyT;
    private Animation enemyUpAnimation;
    private TextureRegion enemyUpCurrentFrame;
    private TextureRegion[] enemyUpFrames;
    private Texture enemyUpT;
    private float enemyUpTime;
    private Vector3 firstTap;
    private Texture food1T;
    private Texture food2T;
    private Texture food3T;
    private Texture food4T;
    private Texture gameOverBg;
    private Texture healthBar;
    private Texture healthBar1;
    private Texture healthBar1V;
    private Texture healthBar2;
    private Texture healthBar2V;
    private Texture healthBar3;
    private Texture healthBar3V;
    private Texture healthBar4;
    private Texture healthBar4V;
    private Texture healthBarV;
    private boolean isSoldier = false;
    private Texture isciBusyDownT;
    private Texture isciBusyRightT;
    private Texture isciBusyT;
    private Texture isciBusyUpT;
    private Texture isciDownT;
    private Texture isciRightT;
    private Texture isciT;
    private Texture isciUpT;
    private Texture kazAktifT;
    private Texture kazT;
    private Texture kraliceFedT;
    private Texture kraliceT;
    private Texture kraliceyiBesleAktifT;
    private Texture kraliceyiBesleT;
    private Texture mainMenuT;
    private Music music;
    private Texture[] options;
    private float optionsTime;
    private Texture[] play;
    private float playTime;
    private Animation queenAnimation;
    private TextureRegion queenCurrentFrame;
    private Animation queenFedAnimation;
    private TextureRegion queenFedCurrentFrame;
    private TextureRegion[] queenFedFrames;
    private TextureRegion[] queenFrames;
    private Texture soundOffT;
    private boolean soundOn = true;
    private Texture soundOnT;
    private float stateTime;
    private float time;
    private int timer = 60;
    private Animation workerBusyDownAnimation;
    private TextureRegion workerBusyDownCurrentFrame;
    private TextureRegion[] workerBusyDownFrames;
    private float workerBusyDownTime;
    private Animation workerBusyLeftAnimation;
    private TextureRegion workerBusyLeftCurrentFrame;
    private TextureRegion[] workerBusyLeftFrames;
    private float workerBusyLeftTime;
    private Animation workerBusyRightAnimation;
    private TextureRegion workerBusyRightCurrentFrame;
    private TextureRegion[] workerBusyRightFrames;
    private float workerBusyRightTime;
    private Animation workerBusyUpAnimation;
    private TextureRegion workerBusyUpCurrentFrame;
    private TextureRegion[] workerBusyUpFrames;
    private float workerBusyUpTime;
    private Animation workerDownAnimation;
    private TextureRegion workerDownCurrentFrame;
    private TextureRegion[] workerDownFrames;
    private float workerDownTime;
    private Animation workerLeftAnimation;
    private TextureRegion workerLeftCurrentFrame;
    private TextureRegion[] workerLeftFrames;
    private float workerLeftTime;
    private Animation workerRightAnimation;
    private TextureRegion workerRightCurrentFrame;
    private TextureRegion[] workerRightFrames;
    private float workerRightTime;
    private Animation workerUpAnimation;
    private TextureRegion workerUpCurrentFrame;
    private TextureRegion[] workerUpFrames;
    private float workerUpTime;
    private Texture yemekToplaAktifT;
    private Texture yemekToplaT;

    public void create() {
        int index;
        int index2;
        int index3;
        int index4;
        int index5;
        int index6;
        int index7;
        int index8;
        int index9;
        int index10;
        int index11;
        int index12;
        int index13;
        int index14;
        int index15;
        int index16;
        int index17;
        int index18;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 1280.0f, 720.0f);
        this.batch = new SpriteBatch();
        this.time = 0.0f;
        this.creditsTime = 0.0f;
        this.optionsTime = 0.0f;
        this.playTime = 0.0f;
        this.stateTime = 0.0f;
        this.workerLeftTime = 0.0f;
        this.workerRightTime = 0.0f;
        this.workerUpTime = 0.0f;
        this.workerDownTime = 0.0f;
        this.enemyLeftTime = 0.0f;
        this.enemyRightTime = 0.0f;
        this.enemyUpTime = 0.0f;
        this.enemyDownTime = 0.0f;
        this.askerLeftTime = 0.0f;
        this.askerRightTime = 0.0f;
        this.askerUpTime = 0.0f;
        this.askerDownTime = 0.0f;
        this.alert = new Texture(Gdx.files.internal("alert.png"));
        this.credits = new Texture[10];
        this.options = new Texture[10];
        this.play = new Texture[10];
        for (int i = 1; i < 11; i++) {
            this.credits[i - 1] = new Texture(Gdx.files.internal("credits" + i + ".jpg"));
        }
        for (int i2 = 1; i2 < 11; i2++) {
            this.options[i2 - 1] = new Texture(Gdx.files.internal("options" + i2 + ".jpg"));
        }
        for (int i3 = 1; i3 < 11; i3++) {
            this.play[i3 - 1] = new Texture(Gdx.files.internal("play" + i3 + ".jpg"));
        }
        this.bgYedek = new Texture(Gdx.files.internal("GameScreen1.jpg"));
        this.f195bg = new Texture(Gdx.files.internal("GameScreen1.jpg"));
        this.bg2 = new Texture(Gdx.files.internal("GameScreen2.jpg"));
        this.bg3 = new Texture(Gdx.files.internal("GameScreen3.jpg"));
        this.bg4 = new Texture(Gdx.files.internal("GameScreen4.jpg"));
        this.mainMenuT = new Texture(Gdx.files.internal("mainMenu.jpg"));
        this.gameOverBg = new Texture(Gdx.files.internal("gameOver.jpg"));
        this.audioOnT = new Texture(Gdx.files.internal("audioButtonOn.png"));
        this.audioOffT = new Texture(Gdx.files.internal("audioButtonOff.png"));
        this.soundOnT = new Texture(Gdx.files.internal("soundButtonOn.png"));
        this.soundOffT = new Texture(Gdx.files.internal("soundButtonOff.png"));
        this.food1T = new Texture(Gdx.files.internal("bread1.png"));
        this.food2T = new Texture(Gdx.files.internal("bread2.png"));
        this.food3T = new Texture(Gdx.files.internal("bread3.png"));
        this.food4T = new Texture(Gdx.files.internal("bread4.png"));
        this.healthBar = new Texture(Gdx.files.internal("healthBarFull.png"));
        this.healthBar1 = new Texture(Gdx.files.internal("healthBar1.png"));
        this.healthBar2 = new Texture(Gdx.files.internal("healthBar2.png"));
        this.healthBar3 = new Texture(Gdx.files.internal("healthBar3.png"));
        this.healthBar4 = new Texture(Gdx.files.internal("healthBar4.png"));
        this.healthBarV = new Texture(Gdx.files.internal("healthBarFullV.png"));
        this.healthBar1V = new Texture(Gdx.files.internal("healthBar1V.png"));
        this.healthBar2V = new Texture(Gdx.files.internal("healthBar2V.png"));
        this.healthBar3V = new Texture(Gdx.files.internal("healthBar3V.png"));
        this.healthBar4V = new Texture(Gdx.files.internal("healthBar4V.png"));
        this.music = Gdx.audio.newMusic(Gdx.files.internal("ambient.mp3"));
        this.music.setLooping(true);
        if (this.audioOn) {
            this.music.play();
        }
        this.kraliceT = new Texture(Gdx.files.internal("queenSheet.png"));
        TextureRegion[][] cpy = TextureRegion.split(this.kraliceT, this.kraliceT.getWidth() / 2, this.kraliceT.getHeight() / 2);
        this.queenFrames = new TextureRegion[4];
        this.queenFrames = new TextureRegion[4];
        int index19 = 0;
        int i4 = 0;
        while (i4 < 2) {
            int j = 0;
            while (true) {
                index18 = index19;
                if (j >= 2) {
                    break;
                }
                index19 = index18 + 1;
                this.queenFrames[index18] = cpy[i4][j];
                j++;
            }
            i4++;
            index19 = index18;
        }
        this.queenAnimation = new Animation(0.05f, this.queenFrames);
        this.stateTime = 0.0f;
        this.kraliceFedT = new Texture(Gdx.files.internal("queenSheet.png"));
        TextureRegion[][] cpy2 = TextureRegion.split(this.kraliceFedT, this.kraliceFedT.getWidth() / 2, this.kraliceFedT.getHeight() / 2);
        this.queenFedFrames = new TextureRegion[4];
        this.queenFedFrames = new TextureRegion[4];
        int index20 = 0;
        int i5 = 0;
        while (i5 < 2) {
            int j2 = 0;
            while (true) {
                index17 = index20;
                if (j2 >= 2) {
                    break;
                }
                index20 = index17 + 1;
                this.queenFedFrames[index17] = cpy2[i5][j2];
                j2++;
            }
            i5++;
            index20 = index17;
        }
        this.queenFedAnimation = new Animation(0.05f, this.queenFedFrames);
        this.stateTime = 0.0f;
        this.isciT = new Texture(Gdx.files.internal("workerSheet.png"));
        TextureRegion[][] cpy3 = TextureRegion.split(this.isciT, this.isciT.getWidth() / 2, this.isciT.getHeight() / 2);
        this.workerLeftFrames = new TextureRegion[4];
        this.workerLeftFrames = new TextureRegion[4];
        int index21 = 0;
        int i6 = 0;
        while (i6 < 2) {
            int j3 = 0;
            while (true) {
                index16 = index21;
                if (j3 >= 2) {
                    break;
                }
                index21 = index16 + 1;
                this.workerLeftFrames[index16] = cpy3[i6][j3];
                j3++;
            }
            i6++;
            index21 = index16;
        }
        this.workerLeftAnimation = new Animation(0.05f, this.workerLeftFrames);
        this.workerLeftTime = 0.0f;
        this.isciUpT = new Texture(Gdx.files.internal("workerSheet6.png"));
        TextureRegion[][] cpy4 = TextureRegion.split(this.isciUpT, this.isciUpT.getWidth() / 2, this.isciUpT.getHeight() / 2);
        this.workerUpFrames = new TextureRegion[4];
        this.workerUpFrames = new TextureRegion[4];
        int index22 = 0;
        int i7 = 0;
        while (i7 < 2) {
            int j4 = 0;
            while (true) {
                index15 = index22;
                if (j4 >= 2) {
                    break;
                }
                index22 = index15 + 1;
                this.workerUpFrames[index15] = cpy4[i7][j4];
                j4++;
            }
            i7++;
            index22 = index15;
        }
        this.workerUpAnimation = new Animation(0.05f, this.workerUpFrames);
        this.workerUpTime = 0.0f;
        this.isciDownT = new Texture(Gdx.files.internal("workerSheet5.png"));
        TextureRegion[][] cpy5 = TextureRegion.split(this.isciDownT, this.isciDownT.getWidth() / 2, this.isciDownT.getHeight() / 2);
        this.workerDownFrames = new TextureRegion[4];
        this.workerDownFrames = new TextureRegion[4];
        int index23 = 0;
        int i8 = 0;
        while (i8 < 2) {
            int j5 = 0;
            while (true) {
                index14 = index23;
                if (j5 >= 2) {
                    break;
                }
                index23 = index14 + 1;
                this.workerDownFrames[index14] = cpy5[i8][j5];
                j5++;
            }
            i8++;
            index23 = index14;
        }
        this.workerDownAnimation = new Animation(0.05f, this.workerDownFrames);
        this.workerDownTime = 0.0f;
        this.isciRightT = new Texture(Gdx.files.internal("workerSheet4.png"));
        TextureRegion[][] cpy6 = TextureRegion.split(this.isciRightT, this.isciRightT.getWidth() / 2, this.isciRightT.getHeight() / 2);
        this.workerRightFrames = new TextureRegion[4];
        this.workerRightFrames = new TextureRegion[4];
        int index24 = 0;
        int i9 = 0;
        while (i9 < 2) {
            int j6 = 0;
            while (true) {
                index13 = index24;
                if (j6 >= 2) {
                    break;
                }
                index24 = index13 + 1;
                this.workerRightFrames[index13] = cpy6[i9][j6];
                j6++;
            }
            i9++;
            index24 = index13;
        }
        this.workerRightAnimation = new Animation(0.05f, this.workerRightFrames);
        this.workerRightTime = 0.0f;
        this.askerT = new Texture(Gdx.files.internal("soldierSheet1.png"));
        TextureRegion[][] cpy7 = TextureRegion.split(this.askerT, this.askerT.getWidth() / 2, this.askerT.getHeight() / 2);
        this.askerLeftFrames = new TextureRegion[4];
        this.askerLeftFrames = new TextureRegion[4];
        int index25 = 0;
        int i10 = 0;
        while (i10 < 2) {
            int j7 = 0;
            while (true) {
                index12 = index25;
                if (j7 >= 2) {
                    break;
                }
                index25 = index12 + 1;
                this.askerLeftFrames[index12] = cpy7[i10][j7];
                j7++;
            }
            i10++;
            index25 = index12;
        }
        this.askerLeftAnimation = new Animation(0.05f, this.askerLeftFrames);
        this.askerLeftTime = 0.0f;
        this.askerUpT = new Texture(Gdx.files.internal("soldierSheet6.png"));
        TextureRegion[][] cpy8 = TextureRegion.split(this.askerUpT, this.askerUpT.getWidth() / 2, this.askerUpT.getHeight() / 2);
        this.askerUpFrames = new TextureRegion[4];
        this.askerUpFrames = new TextureRegion[4];
        int index26 = 0;
        int i11 = 0;
        while (i11 < 2) {
            int j8 = 0;
            while (true) {
                index11 = index26;
                if (j8 >= 2) {
                    break;
                }
                index26 = index11 + 1;
                this.askerUpFrames[index11] = cpy8[i11][j8];
                j8++;
            }
            i11++;
            index26 = index11;
        }
        this.askerUpAnimation = new Animation(0.05f, this.askerUpFrames);
        this.askerUpTime = 0.0f;
        this.askerDownT = new Texture(Gdx.files.internal("soldierSheet5.png"));
        TextureRegion[][] cpy9 = TextureRegion.split(this.askerDownT, this.askerDownT.getWidth() / 2, this.askerDownT.getHeight() / 2);
        this.askerDownFrames = new TextureRegion[4];
        this.askerDownFrames = new TextureRegion[4];
        int index27 = 0;
        int i12 = 0;
        while (i12 < 2) {
            int j9 = 0;
            while (true) {
                index10 = index27;
                if (j9 >= 2) {
                    break;
                }
                index27 = index10 + 1;
                this.askerDownFrames[index10] = cpy9[i12][j9];
                j9++;
            }
            i12++;
            index27 = index10;
        }
        this.askerDownAnimation = new Animation(0.05f, this.askerDownFrames);
        this.askerDownTime = 0.0f;
        this.askerRightT = new Texture(Gdx.files.internal("soldierSheet4.png"));
        TextureRegion[][] cpy10 = TextureRegion.split(this.askerRightT, this.askerRightT.getWidth() / 2, this.askerRightT.getHeight() / 2);
        this.askerRightFrames = new TextureRegion[4];
        this.askerRightFrames = new TextureRegion[4];
        int index28 = 0;
        int i13 = 0;
        while (i13 < 2) {
            int j10 = 0;
            while (true) {
                index9 = index28;
                if (j10 >= 2) {
                    break;
                }
                index28 = index9 + 1;
                this.askerRightFrames[index9] = cpy10[i13][j10];
                j10++;
            }
            i13++;
            index28 = index9;
        }
        this.askerRightAnimation = new Animation(0.05f, this.askerRightFrames);
        this.askerRightTime = 0.0f;
        this.isciBusyT = new Texture(Gdx.files.internal("workerBusySheet1.png"));
        TextureRegion[][] cpy11 = TextureRegion.split(this.isciBusyT, this.isciBusyT.getWidth() / 2, this.isciBusyT.getHeight() / 2);
        this.workerBusyLeftFrames = new TextureRegion[4];
        this.workerBusyLeftFrames = new TextureRegion[4];
        int index29 = 0;
        int i14 = 0;
        while (i14 < 2) {
            int j11 = 0;
            while (true) {
                index8 = index29;
                if (j11 >= 2) {
                    break;
                }
                index29 = index8 + 1;
                this.workerBusyLeftFrames[index8] = cpy11[i14][j11];
                j11++;
            }
            i14++;
            index29 = index8;
        }
        this.workerBusyLeftAnimation = new Animation(0.05f, this.workerBusyLeftFrames);
        this.workerBusyLeftTime = 0.0f;
        this.isciBusyRightT = new Texture(Gdx.files.internal("workerBusySheet4.png"));
        TextureRegion[][] cpy12 = TextureRegion.split(this.isciBusyRightT, this.isciBusyRightT.getWidth() / 2, this.isciBusyRightT.getHeight() / 2);
        this.workerBusyRightFrames = new TextureRegion[4];
        this.workerBusyRightFrames = new TextureRegion[4];
        int index30 = 0;
        int i15 = 0;
        while (i15 < 2) {
            int j12 = 0;
            while (true) {
                index7 = index30;
                if (j12 >= 2) {
                    break;
                }
                index30 = index7 + 1;
                this.workerBusyRightFrames[index7] = cpy12[i15][j12];
                j12++;
            }
            i15++;
            index30 = index7;
        }
        this.workerBusyRightAnimation = new Animation(0.05f, this.workerBusyRightFrames);
        this.workerBusyRightTime = 0.0f;
        this.isciBusyUpT = new Texture(Gdx.files.internal("workerBusySheet6.png"));
        TextureRegion[][] cpy13 = TextureRegion.split(this.isciBusyUpT, this.isciBusyUpT.getWidth() / 2, this.isciBusyUpT.getHeight() / 2);
        this.workerBusyUpFrames = new TextureRegion[4];
        this.workerBusyUpFrames = new TextureRegion[4];
        int index31 = 0;
        int i16 = 0;
        while (i16 < 2) {
            int j13 = 0;
            while (true) {
                index6 = index31;
                if (j13 >= 2) {
                    break;
                }
                index31 = index6 + 1;
                this.workerBusyUpFrames[index6] = cpy13[i16][j13];
                j13++;
            }
            i16++;
            index31 = index6;
        }
        this.workerBusyUpAnimation = new Animation(0.05f, this.workerBusyUpFrames);
        this.workerBusyUpTime = 0.0f;
        this.isciBusyDownT = new Texture(Gdx.files.internal("workerBusySheet5.png"));
        TextureRegion[][] cpy14 = TextureRegion.split(this.isciBusyDownT, this.isciBusyDownT.getWidth() / 2, this.isciBusyDownT.getHeight() / 2);
        this.workerBusyDownFrames = new TextureRegion[4];
        this.workerBusyDownFrames = new TextureRegion[4];
        int index32 = 0;
        int i17 = 0;
        while (i17 < 2) {
            int j14 = 0;
            while (true) {
                index5 = index32;
                if (j14 >= 2) {
                    break;
                }
                index32 = index5 + 1;
                this.workerBusyDownFrames[index5] = cpy14[i17][j14];
                j14++;
            }
            i17++;
            index32 = index5;
        }
        this.workerBusyDownAnimation = new Animation(0.05f, this.workerBusyDownFrames);
        this.workerBusyDownTime = 0.0f;
        this.enemyT = new Texture(Gdx.files.internal("enemySheet.png"));
        TextureRegion[][] cpy15 = TextureRegion.split(this.enemyT, this.enemyT.getWidth() / 2, this.enemyT.getHeight() / 2);
        this.enemyLeftFrames = new TextureRegion[4];
        this.workerLeftFrames = new TextureRegion[4];
        int index33 = 0;
        int i18 = 0;
        while (i18 < 2) {
            int j15 = 0;
            while (true) {
                index4 = index33;
                if (j15 >= 2) {
                    break;
                }
                index33 = index4 + 1;
                this.enemyLeftFrames[index4] = cpy15[i18][j15];
                j15++;
            }
            i18++;
            index33 = index4;
        }
        this.enemyLeftAnimation = new Animation(0.05f, this.enemyLeftFrames);
        this.enemyLeftTime = 0.0f;
        this.enemyRightT = new Texture(Gdx.files.internal("enemySheet4.png"));
        TextureRegion[][] cpy16 = TextureRegion.split(this.enemyRightT, this.enemyRightT.getWidth() / 2, this.enemyRightT.getHeight() / 2);
        this.enemyRightFrames = new TextureRegion[4];
        this.enemyRightFrames = new TextureRegion[4];
        int index34 = 0;
        int i19 = 0;
        while (i19 < 2) {
            int j16 = 0;
            while (true) {
                index3 = index34;
                if (j16 >= 2) {
                    break;
                }
                index34 = index3 + 1;
                this.enemyRightFrames[index3] = cpy16[i19][j16];
                j16++;
            }
            i19++;
            index34 = index3;
        }
        this.enemyRightAnimation = new Animation(0.05f, this.enemyRightFrames);
        this.enemyRightTime = 0.0f;
        this.enemyUpT = new Texture(Gdx.files.internal("enemySheet6.png"));
        TextureRegion[][] cpy17 = TextureRegion.split(this.enemyUpT, this.enemyUpT.getWidth() / 2, this.enemyUpT.getHeight() / 2);
        this.enemyUpFrames = new TextureRegion[4];
        this.enemyUpFrames = new TextureRegion[4];
        int index35 = 0;
        int i20 = 0;
        while (i20 < 2) {
            int j17 = 0;
            while (true) {
                index2 = index35;
                if (j17 >= 2) {
                    break;
                }
                index35 = index2 + 1;
                this.enemyUpFrames[index2] = cpy17[i20][j17];
                j17++;
            }
            i20++;
            index35 = index2;
        }
        this.enemyUpAnimation = new Animation(0.05f, this.enemyUpFrames);
        this.enemyUpTime = 0.0f;
        this.enemyDownT = new Texture(Gdx.files.internal("enemySheet5.png"));
        TextureRegion[][] cpy18 = TextureRegion.split(this.enemyDownT, this.enemyDownT.getWidth() / 2, this.enemyDownT.getHeight() / 2);
        this.enemyDownFrames = new TextureRegion[4];
        this.enemyDownFrames = new TextureRegion[4];
        int index36 = 0;
        int i21 = 0;
        while (i21 < 2) {
            int j18 = 0;
            while (true) {
                index = index36;
                if (j18 >= 2) {
                    break;
                }
                index36 = index + 1;
                this.enemyDownFrames[index] = cpy18[i21][j18];
                j18++;
            }
            i21++;
            index36 = index;
        }
        this.enemyDownAnimation = new Animation(0.05f, this.enemyDownFrames);
        this.enemyDownTime = 0.0f;
        this.yemekToplaT = new Texture(Gdx.files.internal("foodButton.png"));
        this.yemekToplaAktifT = new Texture(Gdx.files.internal("foodButton2.png"));
        this.kraliceyiBesleT = new Texture(Gdx.files.internal("queenButton.png"));
        this.kraliceyiBesleAktifT = new Texture(Gdx.files.internal("queenButton2.png"));
        this.kazT = new Texture(Gdx.files.internal("warriorButton.png"));
        this.kazAktifT = new Texture(Gdx.files.internal("warriorButton2.png"));
        this.firstTap = new Vector3();
        this.firstTap.set(0.0f, 0.0f, 0.0f);
        ants = new ArrayList<>();
        foods = new ArrayList<>();
        enemies = new ArrayList<>();
        Ant kralice = new Ant(900, 380, "k", false);
        kralice.fed = 25;
        ants.add(kralice);
    }

    public void dispose() {
        this.batch.dispose();
        this.f195bg.dispose();
        this.kraliceT.dispose();
        this.kraliceFedT.dispose();
        this.isciT.dispose();
        this.isciBusyT.dispose();
        this.yemekToplaT.dispose();
        for (int i = 0; i < 10; i++) {
            this.credits[i].dispose();
        }
    }

    public void render() {
        Gdx.f12gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Gdx.f12gl.glClear(16384);
        this.camera.update();
        this.batch.setProjectionMatrix(this.camera.combined);
        if (this.ekran.equals("menu")) {
            this.batch.begin();
            this.batch.draw(this.mainMenuT, 0.0f, 0.0f);
            this.batch.end();
            if (Gdx.input.justTouched()) {
                this.firstTap.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0.0f);
                this.camera.unproject(this.firstTap);
                int x = (int) this.firstTap.f170x;
                int y = (int) this.firstTap.f171y;
                if (x > 740 && x < 930 && y > 340 && y < 560) {
                    this.ekran = "play";
                } else if (x > 240 && x < 480 && y > 80 && y < 230) {
                    this.ekran = "credits";
                } else if (x > 500 && x < 700 && y > 250 && y < 400) {
                    this.ekran = "options";
                }
            }
        } else if (this.ekran.equals("options")) {
            this.optionsTime += Gdx.app.getGraphics().getDeltaTime();
            this.batch.begin();
            if (((double) this.optionsTime) < 0.1d) {
                this.batch.draw(this.credits[0], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.1d && ((double) this.optionsTime) < 0.2d) {
                this.batch.draw(this.options[1], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.2d && ((double) this.optionsTime) < 0.3d) {
                this.batch.draw(this.options[2], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.3d && ((double) this.optionsTime) < 0.4d) {
                this.batch.draw(this.options[3], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.4d && ((double) this.optionsTime) < 0.5d) {
                this.batch.draw(this.options[4], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.5d && ((double) this.optionsTime) < 0.6d) {
                this.batch.draw(this.options[5], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.6d && ((double) this.optionsTime) < 0.7d) {
                this.batch.draw(this.options[6], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.7d && ((double) this.optionsTime) < 0.8d) {
                this.batch.draw(this.options[7], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.8d && ((double) this.optionsTime) < 0.9d) {
                this.batch.draw(this.options[8], 0.0f, 0.0f);
            } else if (((double) this.optionsTime) > 0.9d) {
                this.batch.draw(this.options[9], 0.0f, 0.0f);
                if (this.soundOn) {
                    this.batch.draw(this.soundOnT, 550.0f, 125.0f);
                } else {
                    this.batch.draw(this.soundOffT, 550.0f, 125.0f);
                }
                if (this.audioOn) {
                    this.batch.draw(this.audioOnT, 700.0f, 125.0f);
                } else {
                    this.batch.draw(this.audioOffT, 700.0f, 125.0f);
                }
            }
            this.batch.end();
            if (Gdx.input.justTouched()) {
                this.firstTap.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0.0f);
                this.camera.unproject(this.firstTap);
                int x2 = (int) this.firstTap.f170x;
                int y2 = (int) this.firstTap.f171y;
                if (x2 > 475 && x2 < 800 && y2 > 440 && y2 < 600) {
                    this.ekran = "menu";
                    this.optionsTime = 0.0f;
                } else if (x2 > 550 && x2 < 675 && y2 > 125 && y2 < 250) {
                    this.soundOn = !this.soundOn;
                } else if (x2 > 700 && x2 < 825 && y2 > 125 && y2 < 250) {
                    this.audioOn = !this.audioOn;
                    if (!this.audioOn) {
                        this.music.pause();
                    } else {
                        this.music.play();
                    }
                }
            }
        } else if (this.ekran.equals("credits")) {
            this.creditsTime += Gdx.app.getGraphics().getDeltaTime();
            this.batch.begin();
            if (((double) this.creditsTime) < 0.1d) {
                this.batch.draw(this.credits[0], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.1d && ((double) this.creditsTime) < 0.2d) {
                this.batch.draw(this.credits[1], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.2d && ((double) this.creditsTime) < 0.3d) {
                this.batch.draw(this.credits[2], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.3d && ((double) this.creditsTime) < 0.4d) {
                this.batch.draw(this.credits[3], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.4d && ((double) this.creditsTime) < 0.5d) {
                this.batch.draw(this.credits[4], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.5d && ((double) this.creditsTime) < 0.6d) {
                this.batch.draw(this.credits[5], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.6d && ((double) this.creditsTime) < 0.7d) {
                this.batch.draw(this.credits[6], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.7d && ((double) this.creditsTime) < 0.8d) {
                this.batch.draw(this.credits[7], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.8d && ((double) this.creditsTime) < 0.9d) {
                this.batch.draw(this.credits[8], 0.0f, 0.0f);
            } else if (((double) this.creditsTime) > 0.9d) {
                this.batch.draw(this.credits[9], 0.0f, 0.0f);
            }
            this.batch.end();
            if (Gdx.input.justTouched()) {
                this.firstTap.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0.0f);
                this.camera.unproject(this.firstTap);
                int x3 = (int) this.firstTap.f170x;
                int y3 = (int) this.firstTap.f171y;
                if (x3 > 475 && x3 < 800 && y3 > 440 && y3 < 600) {
                    this.ekran = "menu";
                    this.creditsTime = 0.0f;
                }
            }
        } else if (this.ekran.equals("play")) {
            this.playTime += Gdx.app.getGraphics().getDeltaTime();
            this.batch.begin();
            if (((double) this.playTime) < 0.1d) {
                this.batch.draw(this.play[0], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.1d && ((double) this.playTime) < 0.2d) {
                this.batch.draw(this.play[1], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.2d && ((double) this.playTime) < 0.3d) {
                this.batch.draw(this.play[2], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.3d && ((double) this.playTime) < 0.4d) {
                this.batch.draw(this.play[3], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.4d && ((double) this.playTime) < 0.5d) {
                this.batch.draw(this.play[4], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.5d && ((double) this.playTime) < 0.6d) {
                this.batch.draw(this.play[5], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.6d && ((double) this.playTime) < 0.7d) {
                this.batch.draw(this.play[6], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.7d && ((double) this.playTime) < 0.8d) {
                this.batch.draw(this.play[7], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.8d && ((double) this.playTime) < 0.9d) {
                this.batch.draw(this.play[8], 0.0f, 0.0f);
            } else if (((double) this.playTime) > 0.9d) {
                this.batch.draw(this.play[9], 0.0f, 0.0f);
            }
            this.batch.end();
            if (this.playTime > 1.0f) {
                this.ekran = "oyun";
            }
        } else if (this.ekran.equals("oyun")) {
            this.time += Gdx.app.getGraphics().getDeltaTime();
            this.enemySpawner += Gdx.app.getGraphics().getDeltaTime();
            this.batch.begin();
            this.batch.draw(this.f195bg, 0.0f, 0.0f);
            if (!this.anyClicked) {
                this.batch.draw(this.yemekToplaT, 25.0f, 400.0f);
                this.batch.draw(this.kraliceyiBesleT, 25.0f, 250.0f);
                this.batch.draw(this.kazT, 25.0f, 100.0f);
            }
            if (this.anyClicked) {
                for (int i = 0; i < ants.size(); i++) {
                    if (ants.get(i).type.equals("a") && ants.get(i).isClicked) {
                        this.isSoldier = true;
                    }
                }
                if (!this.isSoldier) {
                    this.batch.draw(this.yemekToplaAktifT, 25.0f, 400.0f);
                    this.batch.draw(this.kraliceyiBesleAktifT, 25.0f, 250.0f);
                    this.batch.draw(this.kazAktifT, 25.0f, 100.0f);
                } else {
                    this.isSoldier = false;
                }
            }
            for (int i2 = 0; i2 < ants.size(); i2++) {
                if (ants.get(i2).type.equals("k")) {
                    if (ants.get(i2).fed < 5) {
                        queenAnim();
                        if (ants.get(0).fullHealth - ants.get(0).health == 0) {
                            this.batch.draw(this.healthBar, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < ants.get(0).fullHealth && ants.get(0).health > (ants.get(0).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < (ants.get(0).fullHealth / 5) * 4 && ants.get(0).health > (ants.get(0).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < (ants.get(0).fullHealth / 5) * 3 && ants.get(0).health > (ants.get(0).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < (ants.get(0).fullHealth / 5) * 2 && ants.get(0).health > (ants.get(0).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        }
                    } else {
                        queenFedAnim();
                        if (ants.get(0).fullHealth - ants.get(0).health == 0) {
                            this.batch.draw(this.healthBar, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < ants.get(0).fullHealth && ants.get(0).health > (ants.get(0).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < (ants.get(0).fullHealth / 5) * 4 && ants.get(0).health > (ants.get(0).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < (ants.get(0).fullHealth / 5) * 3 && ants.get(0).health > (ants.get(0).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        } else if (ants.get(0).health < (ants.get(0).fullHealth / 5) * 2 && ants.get(0).health > (ants.get(0).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) ants.get(0).f193x, (float) (ants.get(0).f194y + 64));
                        }
                    }
                } else if (ants.get(i2).type.equals("i")) {
                    if (ants.get(i2).isBusy) {
                        if (ants.get(i2).direction.equals("left")) {
                            workerBusyLeftAnim(i2);
                            if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                                this.batch.draw(this.healthBar, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                                this.batch.draw(this.healthBar1, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                                this.batch.draw(this.healthBar2, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                                this.batch.draw(this.healthBar3, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                                this.batch.draw(this.healthBar4, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            }
                        } else if (ants.get(i2).direction.equals("right")) {
                            workerBusyRightAnim(i2);
                            if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                                this.batch.draw(this.healthBar, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                                this.batch.draw(this.healthBar1, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                                this.batch.draw(this.healthBar2, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                                this.batch.draw(this.healthBar3, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                                this.batch.draw(this.healthBar4, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 64));
                            }
                        } else if (ants.get(i2).direction.equals("up")) {
                            workerBusyUpAnim(i2);
                            if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                                this.batch.draw(this.healthBarV, (float) ants.get(i2).f193x, (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                                this.batch.draw(this.healthBar1V, (float) ants.get(i2).f193x, (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                                this.batch.draw(this.healthBar2V, (float) ants.get(i2).f193x, (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                                this.batch.draw(this.healthBar3V, (float) ants.get(i2).f193x, (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                                this.batch.draw(this.healthBar4V, (float) ants.get(i2).f193x, (float) ants.get(i2).f194y);
                            }
                        } else if (ants.get(i2).direction.equals("down")) {
                            workerBusyDownAnim(i2);
                            if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                                this.batch.draw(this.healthBarV, (float) (ants.get(i2).f193x + 64), (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                                this.batch.draw(this.healthBar1V, (float) (ants.get(i2).f193x + 64), (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                                this.batch.draw(this.healthBar2V, (float) (ants.get(i2).f193x + 64), (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                                this.batch.draw(this.healthBar3V, (float) (ants.get(i2).f193x + 64), (float) ants.get(i2).f194y);
                            } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                                this.batch.draw(this.healthBar4V, (float) (ants.get(i2).f193x + 64), (float) ants.get(i2).f194y);
                            }
                        }
                    } else if (ants.get(i2).direction.equals("left")) {
                        workerLeftAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBar, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        }
                    } else if (ants.get(i2).direction.equals("right")) {
                        workerRightAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBar, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        }
                    } else if (ants.get(i2).direction.equals("up")) {
                        workerUpAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBarV, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        }
                    } else if (ants.get(i2).direction.equals("down")) {
                        workerDownAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBarV, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        }
                    }
                } else if (ants.get(i2).type.equals("a")) {
                    if (ants.get(i2).direction.equals("left")) {
                        askerLeftAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBar, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        }
                    } else if (ants.get(i2).direction.equals("right")) {
                        askerRightAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBar, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) ants.get(i2).f193x, (float) (ants.get(i2).f194y + 48));
                        }
                    } else if (ants.get(i2).direction.equals("up")) {
                        askerUpAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBarV, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4V, (float) (ants.get(i2).f193x + 8), (float) ants.get(i2).f194y);
                        }
                    } else if (ants.get(i2).direction.equals("down")) {
                        askerDownAnim(i2);
                        if (ants.get(i2).fullHealth - ants.get(i2).health == 0) {
                            this.batch.draw(this.healthBarV, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < ants.get(i2).fullHealth && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 4 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 3 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        } else if (ants.get(i2).health < (ants.get(i2).fullHealth / 5) * 2 && ants.get(i2).health > (ants.get(i2).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4V, (float) (ants.get(i2).f193x + 44), (float) ants.get(i2).f194y);
                        }
                    }
                }
            }
            for (int i3 = 0; i3 < enemies.size(); i3++) {
                if (enemies.get(i3).type.equals("a")) {
                    if (enemies.get(i3).direction.equals("left")) {
                        enemyLeftAnim(i3);
                        if (enemies.get(i3).fullHealth - enemies.get(i3).health == 0) {
                            this.batch.draw(this.healthBar, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < enemies.get(i3).fullHealth && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 4 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 3 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 2 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        }
                    } else if (enemies.get(i3).direction.equals("right")) {
                        enemyRightAnim(i3);
                        if (enemies.get(i3).fullHealth - enemies.get(i3).health == 0) {
                            this.batch.draw(this.healthBar, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < enemies.get(i3).fullHealth && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 4 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 3 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 2 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4, (float) enemies.get(i3).f196x, (float) (enemies.get(i3).f197y + 64));
                        }
                    } else if (enemies.get(i3).direction.equals("up")) {
                        enemyUpAnim(i3);
                        if (enemies.get(i3).fullHealth - enemies.get(i3).health == 0) {
                            this.batch.draw(this.healthBarV, (float) enemies.get(i3).f196x, (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < enemies.get(i3).fullHealth && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1V, (float) enemies.get(i3).f196x, (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 4 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2V, (float) enemies.get(i3).f196x, (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 3 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3V, (float) enemies.get(i3).f196x, (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 2 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4V, (float) enemies.get(i3).f196x, (float) enemies.get(i3).f197y);
                        }
                    } else if (enemies.get(i3).direction.equals("down")) {
                        enemyDownAnim(i3);
                        if (enemies.get(i3).fullHealth - enemies.get(i3).health == 0) {
                            this.batch.draw(this.healthBarV, (float) (enemies.get(i3).f196x + 64), (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < enemies.get(i3).fullHealth && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 4) {
                            this.batch.draw(this.healthBar1V, (float) (enemies.get(i3).f196x + 64), (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 4 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 3) {
                            this.batch.draw(this.healthBar2V, (float) (enemies.get(i3).f196x + 64), (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 3 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 2) {
                            this.batch.draw(this.healthBar3V, (float) (enemies.get(i3).f196x + 64), (float) enemies.get(i3).f197y);
                        } else if (enemies.get(i3).health < (enemies.get(i3).fullHealth / 5) * 2 && enemies.get(i3).health > (enemies.get(i3).fullHealth / 5) * 1) {
                            this.batch.draw(this.healthBar4V, (float) (enemies.get(i3).f196x + 64), (float) enemies.get(i3).f197y);
                        }
                    }
                }
            }
            for (int i4 = 0; i4 < foods.size(); i4++) {
                if (foods.get(i4).f198x < 600 && foods.get(i4).f198x > 400) {
                    this.batch.draw(this.food4T, (float) foods.get(i4).f198x, (float) foods.get(i4).f199y);
                } else if (foods.get(i4).f198x < 400 && foods.get(i4).f198x > 40) {
                    this.batch.draw(this.food1T, (float) foods.get(i4).f198x, (float) foods.get(i4).f199y);
                } else if (foods.get(i4).f198x < 1100 && foods.get(i4).f198x > 700) {
                    this.batch.draw(this.food3T, (float) foods.get(i4).f198x, (float) foods.get(i4).f199y);
                } else if (foods.get(i4).f198x < 1280 && foods.get(i4).f198x > 1100) {
                    this.batch.draw(this.food2T, (float) foods.get(i4).f198x, (float) foods.get(i4).f199y);
                }
            }
            int foodX = 200;
            int foodY = Input.Keys.f30F7;
            for (int i5 = 0; i5 < foodDepo; i5++) {
                this.batch.draw(this.food1T, (float) foodX, (float) foodY);
                if (foodX > 250) {
                    foodY += 30;
                    foodX = 170;
                }
                foodX += 30;
            }
            if (enemies.size() > 0) {
                this.batch.draw(this.alert, 0.0f, 0.0f);
            }
            this.batch.end();
            if (Gdx.input.justTouched()) {
                this.firstTap.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0.0f);
                this.camera.unproject(this.firstTap);
                int x4 = (int) this.firstTap.f170x;
                int y4 = (int) this.firstTap.f171y;
                if (x4 > ants.get(0).f193x && x4 < ants.get(0).f193x + 64 && y4 > ants.get(0).f194y && y4 < ants.get(0).f194y + 64 && ants.get(0).fed >= 5) {
                    int isciX = ants.get(0).f193x - 60;
                    for (int i6 = 0; i6 < 3; i6++) {
                        ants.add(new Ant(isciX, 330, "i", false));
                        isciX += 80;
                    }
                    Ant ant = ants.get(0);
                    ant.fed -= 5;
                }
                for (int i7 = 1; i7 < ants.size(); i7++) {
                    if (x4 > ants.get(i7).f193x && x4 < ants.get(i7).f193x + 64 && y4 > ants.get(i7).f194y && y4 < ants.get(i7).f194y + 64 && !ants.get(i7).isClicked) {
                        ants.get(i7).isClicked = true;
                        this.anyClicked = true;
                        for (int j = 1; j < ants.size(); j++) {
                            if (i7 != j) {
                                ants.get(j).isClicked = false;
                            }
                        }
                    }
                }
                if (x4 > 25 && x4 < 153 && y4 > 400 && y4 < 528) {
                    for (int i8 = 0; i8 < ants.size(); i8++) {
                        if (ants.get(i8).isClicked && ants.get(i8).type.equals("i") && ants.get(i8).duty != 9) {
                            ants.get(i8).isClicked = false;
                            ants.get(i8).duty = 1;
                            this.anyClicked = false;
                        }
                    }
                }
                if (x4 > 25 && x4 < 153 && y4 > 250 && y4 < 378) {
                    for (int i9 = 0; i9 < ants.size(); i9++) {
                        if (ants.get(i9).isClicked && ants.get(i9).type.equals("i") && ants.get(i9).duty != 9) {
                            ants.get(i9).isClicked = false;
                            ants.get(i9).duty = 5;
                            this.anyClicked = false;
                        }
                    }
                }
                if (!this.digged) {
                    if (x4 > 25 && x4 < 153 && y4 > 100 && y4 < 228) {
                        for (int i10 = 0; i10 < ants.size(); i10++) {
                            if (ants.get(i10).isClicked && ants.get(i10).duty != 9) {
                                ants.get(i10).isClicked = false;
                                ants.get(i10).duty = 10;
                                this.anyClicked = false;
                            }
                        }
                    }
                } else if (x4 > 25 && x4 < 153 && y4 > 100 && y4 < 228) {
                    for (int i11 = 0; i11 < ants.size(); i11++) {
                        if (ants.get(i11).isClicked) {
                            ants.get(i11).isClicked = false;
                            if (ants.get(i11).duty != 9) {
                                ants.get(i11).duty = 14;
                            }
                            ants.get(i11).type = "a";
                            this.anyClicked = false;
                        }
                    }
                }
            }
            if (this.time > 5.0f) {
                int x5 = MathUtils.random(40, 1240);
                if (x5 < 350 && x5 > 100) {
                    x5 = MathUtils.random(350, 1240);
                }
                if (x5 < 700 && x5 > 600) {
                    x5 = MathUtils.random(700, 1240);
                }
                foods.add(new Food(x5, 570));
                this.time = 0.0f;
            }
            if (this.enemySpawner > ((float) this.timer)) {
                int x6 = 1300;
                if (MathUtils.random(0, 100) >= 50) {
                    x6 = -50;
                }
                if (enemies.size() < 10) {
                    enemies.add(new Enemy(x6, 570, "a"));
                    this.enemySpawner = 30.0f;
                    this.timer -= 10;
                }
            }
            for (int i12 = 0; i12 < ants.size(); i12++) {
                ants.get(i12).update();
                if (ants.get(i12).health <= 0) {
                    if (ants.get(i12).type.equals("k")) {
                        ants.get(i12).isAlive = false;
                    } else {
                        ants.remove(i12);
                    }
                }
            }
            for (int i13 = 0; i13 < enemies.size(); i13++) {
                enemies.get(i13).update();
                if (enemies.get(i13).health <= 0) {
                    enemies.remove(i13);
                }
            }
            if (digging == 25 && !this.digged) {
                this.f195bg = this.bg2;
            } else if (digging == 50 && !this.digged) {
                this.f195bg = this.bg3;
            } else if (digging == 75 && !this.digged) {
                this.f195bg = this.bg4;
                this.digged = true;
            }
            if (!ants.get(0).isAlive) {
                this.ekran = "gameover";
                ants.get(0).isAlive = true;
                ants.get(0).health = 2000;
                ants.get(0).fed = 25;
                enemies = null;
                enemies = new ArrayList<>();
                this.timer = 60;
                foods = null;
                foods = new ArrayList<>();
                this.f195bg = this.bgYedek;
                digging = 0;
                this.digged = false;
            }
        } else if (this.ekran.equals("gameover")) {
            this.batch.begin();
            this.batch.draw(this.gameOverBg, 0.0f, 0.0f);
            this.batch.end();
            if (Gdx.input.justTouched()) {
                this.firstTap.set((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0.0f);
                this.camera.unproject(this.firstTap);
                int x7 = (int) this.firstTap.f170x;
                int y5 = (int) this.firstTap.f171y;
                if (x7 > 900 && x7 < 4200 && y5 > 40 && y5 < 240) {
                    this.ekran = "menu";
                    this.stateTime = 0.0f;
                }
            }
        }
    }

    public void resize(int width, int height) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void queenAnim() {
        this.stateTime += Gdx.graphics.getDeltaTime();
        this.queenCurrentFrame = this.queenAnimation.getKeyFrame(this.stateTime, true);
        this.batch.draw(this.queenCurrentFrame, (float) ants.get(0).f193x, (float) ants.get(0).f194y);
    }

    public void queenFedAnim() {
        this.stateTime += Gdx.graphics.getDeltaTime();
        this.queenFedCurrentFrame = this.queenFedAnimation.getKeyFrame(this.stateTime, true);
        this.batch.draw(this.queenFedCurrentFrame, (float) ants.get(0).f193x, (float) ants.get(0).f194y);
    }

    public void workerLeftAnim(int i) {
        this.workerLeftTime += Gdx.graphics.getDeltaTime();
        this.workerLeftCurrentFrame = this.workerLeftAnimation.getKeyFrame(this.workerLeftTime, true);
        this.batch.draw(this.workerLeftCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerUpAnim(int i) {
        this.workerUpTime += Gdx.graphics.getDeltaTime();
        this.workerUpCurrentFrame = this.workerUpAnimation.getKeyFrame(this.workerUpTime, true);
        this.batch.draw(this.workerUpCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerRightAnim(int i) {
        this.workerRightTime += Gdx.graphics.getDeltaTime();
        this.workerRightCurrentFrame = this.workerRightAnimation.getKeyFrame(this.workerRightTime, true);
        this.batch.draw(this.workerRightCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerDownAnim(int i) {
        this.workerDownTime += Gdx.graphics.getDeltaTime();
        this.workerDownCurrentFrame = this.workerDownAnimation.getKeyFrame(this.workerDownTime, true);
        this.batch.draw(this.workerDownCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void enemyLeftAnim(int i) {
        this.enemyLeftTime += Gdx.graphics.getDeltaTime();
        this.enemyLeftCurrentFrame = this.enemyLeftAnimation.getKeyFrame(this.enemyLeftTime, true);
        this.batch.draw(this.enemyLeftCurrentFrame, (float) enemies.get(i).f196x, (float) enemies.get(i).f197y);
    }

    public void enemyUpAnim(int i) {
        this.enemyUpTime += Gdx.graphics.getDeltaTime();
        this.enemyUpCurrentFrame = this.enemyUpAnimation.getKeyFrame(this.enemyUpTime, true);
        this.batch.draw(this.enemyUpCurrentFrame, (float) enemies.get(i).f196x, (float) enemies.get(i).f197y);
    }

    public void enemyRightAnim(int i) {
        this.enemyRightTime += Gdx.graphics.getDeltaTime();
        this.enemyRightCurrentFrame = this.enemyRightAnimation.getKeyFrame(this.enemyRightTime, true);
        this.batch.draw(this.enemyRightCurrentFrame, (float) enemies.get(i).f196x, (float) enemies.get(i).f197y);
    }

    public void enemyDownAnim(int i) {
        this.enemyDownTime += Gdx.graphics.getDeltaTime();
        this.enemyDownCurrentFrame = this.enemyDownAnimation.getKeyFrame(this.enemyDownTime, true);
        this.batch.draw(this.enemyDownCurrentFrame, (float) enemies.get(i).f196x, (float) enemies.get(i).f197y);
    }

    public void askerLeftAnim(int i) {
        this.askerLeftTime += Gdx.graphics.getDeltaTime();
        this.askerLeftCurrentFrame = this.askerLeftAnimation.getKeyFrame(this.askerLeftTime, true);
        this.batch.draw(this.askerLeftCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void askerUpAnim(int i) {
        this.askerUpTime += Gdx.graphics.getDeltaTime();
        this.askerUpCurrentFrame = this.askerUpAnimation.getKeyFrame(this.askerUpTime, true);
        this.batch.draw(this.askerUpCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void askerRightAnim(int i) {
        this.askerRightTime += Gdx.graphics.getDeltaTime();
        this.askerRightCurrentFrame = this.askerRightAnimation.getKeyFrame(this.askerRightTime, true);
        this.batch.draw(this.askerRightCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void askerDownAnim(int i) {
        this.askerDownTime += Gdx.graphics.getDeltaTime();
        this.askerDownCurrentFrame = this.askerDownAnimation.getKeyFrame(this.askerDownTime, true);
        this.batch.draw(this.askerDownCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerBusyLeftAnim(int i) {
        this.workerBusyLeftTime += Gdx.graphics.getDeltaTime();
        this.workerBusyLeftCurrentFrame = this.workerBusyLeftAnimation.getKeyFrame(this.workerBusyLeftTime, true);
        this.batch.draw(this.workerBusyLeftCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerBusyUpAnim(int i) {
        this.workerBusyUpTime += Gdx.graphics.getDeltaTime();
        this.workerBusyUpCurrentFrame = this.workerBusyUpAnimation.getKeyFrame(this.workerBusyUpTime, true);
        this.batch.draw(this.workerBusyUpCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerBusyRightAnim(int i) {
        this.workerBusyRightTime += Gdx.graphics.getDeltaTime();
        this.workerBusyRightCurrentFrame = this.workerBusyRightAnimation.getKeyFrame(this.workerBusyRightTime, true);
        this.batch.draw(this.workerBusyRightCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }

    public void workerBusyDownAnim(int i) {
        this.workerBusyDownTime += Gdx.graphics.getDeltaTime();
        this.workerBusyDownCurrentFrame = this.workerBusyDownAnimation.getKeyFrame(this.workerBusyDownTime, true);
        this.batch.draw(this.workerBusyDownCurrentFrame, (float) ants.get(i).f193x, (float) ants.get(i).f194y);
    }
}
