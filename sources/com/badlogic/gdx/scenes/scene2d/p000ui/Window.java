package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Window */
public class Window extends Table {
    final Vector2 dragOffset;
    boolean dragging;
    boolean isModal;
    boolean isMovable;
    boolean keepWithinStage;
    private WindowStyle style;
    private String title;
    private int titleAlignment;
    private BitmapFontCache titleCache;

    public Window(String title2, Skin skin) {
        this(title2, (WindowStyle) skin.get(WindowStyle.class));
        setSkin(skin);
    }

    public Window(String title2, Skin skin, String styleName) {
        this(title2, (WindowStyle) skin.get(styleName, WindowStyle.class));
        setSkin(skin);
    }

    public Window(String title2, WindowStyle style2) {
        this.isMovable = true;
        this.dragOffset = new Vector2();
        this.titleAlignment = 1;
        this.keepWithinStage = true;
        if (title2 == null) {
            throw new IllegalArgumentException("title cannot be null.");
        }
        this.title = title2;
        setTouchable(Touchable.enabled);
        setClip(true);
        setStyle(style2);
        setWidth(150.0f);
        setHeight(150.0f);
        setTitle(title2);
        addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Window.this.toFront();
                return false;
            }
        });
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean z;
                if (button == 0) {
                    Window window = Window.this;
                    if (!Window.this.isMovable || Window.this.getHeight() - y > Window.this.getPadTop() || y >= Window.this.getHeight() || x <= 0.0f || x >= Window.this.getWidth()) {
                        z = false;
                    } else {
                        z = true;
                    }
                    window.dragging = z;
                    Window.this.dragOffset.set(x, y);
                }
                if (Window.this.dragging || Window.this.isModal) {
                    return true;
                }
                return false;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (Window.this.dragging) {
                    Window.this.translate(x - Window.this.dragOffset.f165x, y - Window.this.dragOffset.f166y);
                }
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                return Window.this.isModal;
            }

            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return Window.this.isModal;
            }

            public boolean keyDown(InputEvent event, int keycode) {
                return Window.this.isModal;
            }

            public boolean keyUp(InputEvent event, int keycode) {
                return Window.this.isModal;
            }

            public boolean keyTyped(InputEvent event, char character) {
                return Window.this.isModal;
            }
        });
    }

    public void setStyle(WindowStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style2;
        setBackground(style2.background);
        this.titleCache = new BitmapFontCache(style2.titleFont);
        this.titleCache.setColor(style2.titleFontColor);
        if (this.title != null) {
            setTitle(this.title);
        }
        invalidateHierarchy();
    }

    public WindowStyle getStyle() {
        return this.style;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        Stage stage = getStage();
        if (this.keepWithinStage && getParent() == stage.getRoot()) {
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if (getX() < 0.0f) {
                setX(0.0f);
            }
            if (getRight() > parentWidth) {
                setX(parentWidth - getWidth());
            }
            if (getY() < 0.0f) {
                setY(0.0f);
            }
            if (getTop() > parentHeight) {
                setY(parentHeight - getHeight());
            }
        }
        super.draw(batch, parentAlpha);
    }

    /* access modifiers changed from: protected */
    public void drawBackground(SpriteBatch batch, float parentAlpha) {
        float x;
        if (this.style.stageBackground != null) {
            Color color = getColor();
            batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
            Stage stage = getStage();
            Vector2 position = stageToLocalCoordinates(Vector2.tmp.set(0.0f, 0.0f));
            Vector2 size = stageToLocalCoordinates(Vector2.tmp2.set(stage.getWidth(), stage.getHeight()));
            this.style.stageBackground.draw(batch, position.f165x + getX(), position.f166y + getY(), size.f165x + getX(), size.f166y + getY());
        }
        super.drawBackground(batch, parentAlpha);
        float x2 = getX();
        float y = getY() + getHeight();
        BitmapFont.TextBounds bounds = this.titleCache.getBounds();
        if ((this.titleAlignment & 8) != 0) {
            x = x2 + getPadLeft();
        } else if ((this.titleAlignment & 16) != 0) {
            x = x2 + ((getWidth() - bounds.width) - getPadRight());
        } else {
            x = x2 + ((getWidth() - bounds.width) / 2.0f);
        }
        if ((this.titleAlignment & 2) == 0) {
            if ((this.titleAlignment & 4) != 0) {
                y -= getPadTop() - bounds.height;
            } else {
                y -= (getPadTop() - bounds.height) / 2.0f;
            }
        }
        this.titleCache.setColor(Color.tmp.set(getColor()).mul(this.style.titleFontColor));
        this.titleCache.setPosition((float) ((int) x), (float) ((int) y));
        this.titleCache.draw(batch, parentAlpha);
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public Actor hit(float x, float y, boolean touchable) {
        Actor hit = super.hit(x, y, touchable);
        return (hit != null || !this.isModal || (touchable && getTouchable() != Touchable.enabled)) ? hit : this;
    }

    public void setTitle(String title2) {
        this.title = title2;
        this.titleCache.setMultiLineText(title2, 0.0f, 0.0f);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitleAlignment(int titleAlignment2) {
        this.titleAlignment = titleAlignment2;
    }

    public void setMovable(boolean isMovable2) {
        this.isMovable = isMovable2;
    }

    public void setModal(boolean isModal2) {
        this.isModal = isModal2;
    }

    public void setKeepWithinStage(boolean keepWithinStage2) {
        this.keepWithinStage = keepWithinStage2;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), this.titleCache.getBounds().width + getPadLeft() + getPadRight());
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Window$WindowStyle */
    public static class WindowStyle {
        public Drawable background;
        public Drawable stageBackground;
        public BitmapFont titleFont;
        public Color titleFontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

        public WindowStyle() {
        }

        public WindowStyle(BitmapFont titleFont2, Color titleFontColor2, Drawable background2) {
            this.background = background2;
            this.titleFont = titleFont2;
            this.titleFontColor.set(titleFontColor2);
        }

        public WindowStyle(WindowStyle style) {
            this.background = style.background;
            this.titleFont = style.titleFont;
            this.titleFontColor = new Color(style.titleFontColor);
        }
    }
}
