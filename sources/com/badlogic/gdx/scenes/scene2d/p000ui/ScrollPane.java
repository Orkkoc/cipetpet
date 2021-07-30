package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ScrollPane */
public class ScrollPane extends WidgetGroup {
    float amountX;
    float amountY;
    float areaHeight;
    float areaWidth;
    boolean cancelTouchFocus;
    private boolean clamp;
    private boolean disableX;
    private boolean disableY;
    int draggingPointer;
    float fadeAlpha;
    float fadeAlphaSeconds;
    float fadeDelay;
    float fadeDelaySeconds;
    private boolean fadeScrollBars;
    boolean flickScroll;
    private ActorGestureListener flickScrollListener;
    float flingTime;
    float flingTimer;
    private boolean forceOverscrollX;
    private boolean forceOverscrollY;
    final Rectangle hKnobBounds;
    final Rectangle hScrollBounds;
    final Vector2 lastPoint;
    float maxX;
    float maxY;
    private float overscrollDistance;
    private float overscrollSpeedMax;
    private float overscrollSpeedMin;
    private boolean overscrollX;
    private boolean overscrollY;
    private final Rectangle scissorBounds;
    boolean scrollX;
    boolean scrollY;
    private boolean scrollbarsOnTop;
    private boolean smoothScrolling;
    private ScrollPaneStyle style;
    boolean touchScrollH;
    boolean touchScrollV;
    final Rectangle vKnobBounds;
    final Rectangle vScrollBounds;
    float velocityX;
    float velocityY;
    float visualAmountX;
    float visualAmountY;
    private Actor widget;
    private final Rectangle widgetAreaBounds;
    private final Rectangle widgetCullingArea;

    public ScrollPane(Actor widget2) {
        this(widget2, new ScrollPaneStyle());
    }

    public ScrollPane(Actor widget2, Skin skin) {
        this(widget2, (ScrollPaneStyle) skin.get(ScrollPaneStyle.class));
    }

    public ScrollPane(Actor widget2, Skin skin, String styleName) {
        this(widget2, (ScrollPaneStyle) skin.get(styleName, ScrollPaneStyle.class));
    }

    public ScrollPane(Actor widget2, ScrollPaneStyle style2) {
        this.hScrollBounds = new Rectangle();
        this.vScrollBounds = new Rectangle();
        this.hKnobBounds = new Rectangle();
        this.vKnobBounds = new Rectangle();
        this.widgetAreaBounds = new Rectangle();
        this.widgetCullingArea = new Rectangle();
        this.scissorBounds = new Rectangle();
        this.lastPoint = new Vector2();
        this.fadeScrollBars = true;
        this.smoothScrolling = true;
        this.fadeAlphaSeconds = 1.0f;
        this.fadeDelaySeconds = 1.0f;
        this.cancelTouchFocus = true;
        this.flickScroll = true;
        this.overscrollX = true;
        this.overscrollY = true;
        this.flingTime = 1.0f;
        this.overscrollDistance = 50.0f;
        this.overscrollSpeedMin = 30.0f;
        this.overscrollSpeedMax = 200.0f;
        this.clamp = true;
        this.draggingPointer = -1;
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.widget = widget2;
        this.style = style2;
        if (widget2 != null) {
            setWidget(widget2);
        }
        setWidth(150.0f);
        setHeight(150.0f);
        addCaptureListener(new InputListener() {
            private float handlePosition;

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int i = -1;
                if (ScrollPane.this.draggingPointer != -1) {
                    return false;
                }
                if (pointer == 0 && button != 0) {
                    return false;
                }
                ScrollPane.this.getStage().setScrollFocus(ScrollPane.this);
                if (!ScrollPane.this.flickScroll) {
                    ScrollPane.this.resetFade();
                }
                if (ScrollPane.this.fadeAlpha == 0.0f) {
                    return false;
                }
                if (ScrollPane.this.scrollX && ScrollPane.this.hScrollBounds.contains(x, y)) {
                    event.stop();
                    ScrollPane.this.resetFade();
                    if (ScrollPane.this.hKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.hKnobBounds.f161x;
                        ScrollPane.this.touchScrollH = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                    }
                    ScrollPane scrollPane = ScrollPane.this;
                    float f = ScrollPane.this.amountX;
                    float max = Math.max(ScrollPane.this.areaWidth * 0.9f, ScrollPane.this.maxX * 0.1f);
                    if (x >= ScrollPane.this.hKnobBounds.f161x) {
                        i = 1;
                    }
                    scrollPane.setScrollX((((float) i) * max) + f);
                    return true;
                } else if (!ScrollPane.this.scrollY || !ScrollPane.this.vScrollBounds.contains(x, y)) {
                    return false;
                } else {
                    event.stop();
                    ScrollPane.this.resetFade();
                    if (ScrollPane.this.vKnobBounds.contains(x, y)) {
                        ScrollPane.this.lastPoint.set(x, y);
                        this.handlePosition = ScrollPane.this.vKnobBounds.f162y;
                        ScrollPane.this.touchScrollV = true;
                        ScrollPane.this.draggingPointer = pointer;
                        return true;
                    }
                    ScrollPane scrollPane2 = ScrollPane.this;
                    float f2 = ScrollPane.this.amountY;
                    float max2 = Math.max(ScrollPane.this.areaHeight * 0.9f, ScrollPane.this.maxY * 0.1f);
                    if (y < ScrollPane.this.vKnobBounds.f162y) {
                        i = 1;
                    }
                    scrollPane2.setScrollY((((float) i) * max2) + f2);
                    return true;
                }
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == ScrollPane.this.draggingPointer) {
                    ScrollPane.this.draggingPointer = -1;
                    ScrollPane.this.touchScrollH = false;
                    ScrollPane.this.touchScrollV = false;
                }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (pointer == ScrollPane.this.draggingPointer) {
                    if (ScrollPane.this.touchScrollH) {
                        float scrollH = this.handlePosition + (x - ScrollPane.this.lastPoint.f165x);
                        this.handlePosition = scrollH;
                        ScrollPane.this.setScrollPercentX((Math.min((ScrollPane.this.hScrollBounds.f161x + ScrollPane.this.hScrollBounds.width) - ScrollPane.this.hKnobBounds.width, Math.max(ScrollPane.this.hScrollBounds.f161x, scrollH)) - ScrollPane.this.hScrollBounds.f161x) / (ScrollPane.this.hScrollBounds.width - ScrollPane.this.hKnobBounds.width));
                        ScrollPane.this.lastPoint.set(x, y);
                    } else if (ScrollPane.this.touchScrollV) {
                        float scrollV = this.handlePosition + (y - ScrollPane.this.lastPoint.f166y);
                        this.handlePosition = scrollV;
                        ScrollPane.this.setScrollPercentY(1.0f - ((Math.min((ScrollPane.this.vScrollBounds.f162y + ScrollPane.this.vScrollBounds.height) - ScrollPane.this.vKnobBounds.height, Math.max(ScrollPane.this.vScrollBounds.f162y, scrollV)) - ScrollPane.this.vScrollBounds.f162y) / (ScrollPane.this.vScrollBounds.height - ScrollPane.this.vKnobBounds.height)));
                        ScrollPane.this.lastPoint.set(x, y);
                    }
                }
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                if (ScrollPane.this.flickScroll) {
                    return false;
                }
                ScrollPane.this.resetFade();
                return false;
            }
        });
        this.flickScrollListener = new ActorGestureListener() {
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                ScrollPane.this.resetFade();
                ScrollPane.this.amountX -= deltaX;
                ScrollPane.this.amountY += deltaY;
                ScrollPane.this.clamp();
                ScrollPane.this.cancelTouchFocusedChild(event);
            }

            public void fling(InputEvent event, float x, float y, int button) {
                if (Math.abs(x) > 150.0f) {
                    ScrollPane.this.flingTimer = ScrollPane.this.flingTime;
                    ScrollPane.this.velocityX = x;
                    ScrollPane.this.cancelTouchFocusedChild(event);
                }
                if (Math.abs(y) > 150.0f) {
                    ScrollPane.this.flingTimer = ScrollPane.this.flingTime;
                    ScrollPane.this.velocityY = -y;
                    ScrollPane.this.cancelTouchFocusedChild(event);
                }
            }

            public boolean handle(Event event) {
                if (!super.handle(event)) {
                    return false;
                }
                if (((InputEvent) event).getType() == InputEvent.Type.touchDown) {
                    ScrollPane.this.flingTimer = 0.0f;
                }
                return true;
            }
        };
        addListener(this.flickScrollListener);
        addListener(new InputListener() {
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                ScrollPane.this.resetFade();
                if (ScrollPane.this.scrollY) {
                    ScrollPane.this.setScrollY(ScrollPane.this.amountY + ((Math.max(ScrollPane.this.areaHeight * 0.9f, ScrollPane.this.maxY * 0.1f) / 4.0f) * ((float) amount)));
                    return true;
                } else if (!ScrollPane.this.scrollX) {
                    return true;
                } else {
                    ScrollPane.this.setScrollX(ScrollPane.this.amountX + ((Math.max(ScrollPane.this.areaWidth * 0.9f, ScrollPane.this.maxX * 0.1f) / 4.0f) * ((float) amount)));
                    return true;
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void resetFade() {
        this.fadeAlpha = this.fadeAlphaSeconds;
        this.fadeDelay = this.fadeDelaySeconds;
    }

    /* access modifiers changed from: package-private */
    public void cancelTouchFocusedChild(InputEvent event) {
        Stage stage;
        if (this.cancelTouchFocus && (stage = getStage()) != null) {
            stage.cancelTouchFocus(this.flickScrollListener, this);
        }
    }

    /* access modifiers changed from: package-private */
    public void clamp() {
        if (this.clamp) {
            scrollX(this.overscrollX ? MathUtils.clamp(this.amountX, -this.overscrollDistance, this.maxX + this.overscrollDistance) : MathUtils.clamp(this.amountX, 0.0f, this.maxX));
            scrollY(this.overscrollY ? MathUtils.clamp(this.amountY, -this.overscrollDistance, this.maxY + this.overscrollDistance) : MathUtils.clamp(this.amountY, 0.0f, this.maxY));
        }
    }

    public void setStyle(ScrollPaneStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style2;
        invalidateHierarchy();
    }

    public ScrollPaneStyle getStyle() {
        return this.style;
    }

    public void act(float delta) {
        super.act(delta);
        boolean panning = this.flickScrollListener.getGestureDetector().isPanning();
        if (this.fadeAlpha > 0.0f && this.fadeScrollBars && !panning && !this.touchScrollH && !this.touchScrollV) {
            this.fadeDelay -= delta;
            if (this.fadeDelay <= 0.0f) {
                this.fadeAlpha = Math.max(0.0f, this.fadeAlpha - delta);
            }
        }
        if (this.flingTimer > 0.0f) {
            resetFade();
            float alpha = this.flingTimer / this.flingTime;
            this.amountX -= (this.velocityX * alpha) * delta;
            this.amountY -= (this.velocityY * alpha) * delta;
            clamp();
            if (this.amountX == (-this.overscrollDistance)) {
                this.velocityX = 0.0f;
            }
            if (this.amountX >= this.maxX + this.overscrollDistance) {
                this.velocityX = 0.0f;
            }
            if (this.amountY == (-this.overscrollDistance)) {
                this.velocityY = 0.0f;
            }
            if (this.amountY >= this.maxY + this.overscrollDistance) {
                this.velocityY = 0.0f;
            }
            this.flingTimer -= delta;
            if (this.flingTimer <= 0.0f) {
                this.velocityX = 0.0f;
                this.velocityY = 0.0f;
            }
        }
        if (!this.smoothScrolling || this.flingTimer > 0.0f || this.touchScrollH || this.touchScrollV || panning) {
            if (this.visualAmountX != this.amountX) {
                visualScrollX(this.amountX);
            }
            if (this.visualAmountY != this.amountY) {
                visualScrollY(this.amountY);
            }
        } else {
            if (this.visualAmountX != this.amountX) {
                if (this.visualAmountX < this.amountX) {
                    visualScrollX(Math.min(this.amountX, this.visualAmountX + Math.max(150.0f * delta, (this.amountX - this.visualAmountX) * 5.0f * delta)));
                } else {
                    visualScrollX(Math.max(this.amountX, this.visualAmountX - Math.max(150.0f * delta, ((this.visualAmountX - this.amountX) * 5.0f) * delta)));
                }
            }
            if (this.visualAmountY != this.amountY) {
                if (this.visualAmountY < this.amountY) {
                    visualScrollY(Math.min(this.amountY, this.visualAmountY + Math.max(150.0f * delta, (this.amountY - this.visualAmountY) * 5.0f * delta)));
                } else {
                    visualScrollY(Math.max(this.amountY, this.visualAmountY - Math.max(150.0f * delta, ((this.visualAmountY - this.amountY) * 5.0f) * delta)));
                }
            }
        }
        if (!panning) {
            if (this.overscrollX && this.scrollX) {
                if (this.amountX < 0.0f) {
                    resetFade();
                    this.amountX += (this.overscrollSpeedMin + (((this.overscrollSpeedMax - this.overscrollSpeedMin) * (-this.amountX)) / this.overscrollDistance)) * delta;
                    if (this.amountX > 0.0f) {
                        scrollX(0.0f);
                    }
                } else if (this.amountX > this.maxX) {
                    resetFade();
                    this.amountX -= (this.overscrollSpeedMin + (((this.overscrollSpeedMax - this.overscrollSpeedMin) * (-(this.maxX - this.amountX))) / this.overscrollDistance)) * delta;
                    if (this.amountX < this.maxX) {
                        scrollX(this.maxX);
                    }
                }
            }
            if (this.overscrollY && this.scrollY) {
                if (this.amountY < 0.0f) {
                    resetFade();
                    this.amountY += (this.overscrollSpeedMin + (((this.overscrollSpeedMax - this.overscrollSpeedMin) * (-this.amountY)) / this.overscrollDistance)) * delta;
                    if (this.amountY > 0.0f) {
                        scrollY(0.0f);
                    }
                } else if (this.amountY > this.maxY) {
                    resetFade();
                    this.amountY -= (this.overscrollSpeedMin + (((this.overscrollSpeedMax - this.overscrollSpeedMin) * (-(this.maxY - this.amountY))) / this.overscrollDistance)) * delta;
                    if (this.amountY < this.maxY) {
                        scrollY(this.maxY);
                    }
                }
            }
        }
    }

    public void layout() {
        float widgetWidth;
        float widgetHeight;
        float widgetHeight2;
        Drawable bg = this.style.background;
        Drawable hScrollKnob = this.style.hScrollKnob;
        Drawable vScrollKnob = this.style.vScrollKnob;
        float bgLeftWidth = 0.0f;
        float bgRightWidth = 0.0f;
        float bgTopHeight = 0.0f;
        float bgBottomHeight = 0.0f;
        if (bg != null) {
            bgLeftWidth = bg.getLeftWidth();
            bgRightWidth = bg.getRightWidth();
            bgTopHeight = bg.getTopHeight();
            bgBottomHeight = bg.getBottomHeight();
        }
        float width = getWidth();
        float height = getHeight();
        float scrollbarHeight = 0.0f;
        if (hScrollKnob != null) {
            scrollbarHeight = hScrollKnob.getMinHeight();
        }
        if (this.style.hScroll != null) {
            scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
        }
        float scrollbarWidth = 0.0f;
        if (vScrollKnob != null) {
            scrollbarWidth = vScrollKnob.getMinWidth();
        }
        if (this.style.vScroll != null) {
            scrollbarWidth = Math.max(scrollbarWidth, this.style.vScroll.getMinWidth());
        }
        this.areaWidth = (width - bgLeftWidth) - bgRightWidth;
        this.areaHeight = (height - bgTopHeight) - bgBottomHeight;
        if (this.widget != null) {
            if (this.widget instanceof Layout) {
                Layout layout = (Layout) this.widget;
                widgetWidth = layout.getPrefWidth();
                widgetHeight = layout.getPrefHeight();
            } else {
                widgetWidth = this.widget.getWidth();
                widgetHeight = this.widget.getHeight();
            }
            this.scrollX = this.forceOverscrollX || (widgetWidth > this.areaWidth && !this.disableX);
            this.scrollY = this.forceOverscrollY || (widgetHeight > this.areaHeight && !this.disableY);
            boolean fade = this.fadeScrollBars;
            if (!fade) {
                if (this.scrollY) {
                    this.areaWidth -= scrollbarWidth;
                    if (!this.scrollX && widgetWidth > this.areaWidth && !this.disableX) {
                        this.scrollX = true;
                    }
                }
                if (this.scrollX) {
                    this.areaHeight -= scrollbarHeight;
                    if (!this.scrollY && widgetHeight > this.areaHeight && !this.disableY) {
                        this.scrollY = true;
                        this.areaWidth -= scrollbarWidth;
                    }
                }
            }
            this.widgetAreaBounds.set(bgLeftWidth, bgBottomHeight, this.areaWidth, this.areaHeight);
            if (fade) {
                if (this.scrollX) {
                    this.areaHeight -= scrollbarHeight;
                }
                if (this.scrollY) {
                    this.areaWidth -= scrollbarWidth;
                }
            } else if (this.scrollbarsOnTop) {
                if (this.scrollX) {
                    this.widgetAreaBounds.height += scrollbarHeight;
                }
                if (this.scrollY) {
                    this.widgetAreaBounds.width += scrollbarWidth;
                }
            } else if (this.scrollX) {
                this.widgetAreaBounds.f162y += scrollbarHeight;
            }
            float widgetWidth2 = this.disableX ? width : Math.max(this.areaWidth, widgetWidth);
            if (this.disableY) {
                widgetHeight2 = height;
            } else {
                widgetHeight2 = Math.max(this.areaHeight, widgetHeight);
            }
            this.maxX = widgetWidth2 - this.areaWidth;
            this.maxY = widgetHeight2 - this.areaHeight;
            if (fade) {
                if (this.scrollX) {
                    this.maxY -= scrollbarHeight;
                }
                if (this.scrollY) {
                    this.maxX -= scrollbarWidth;
                }
            }
            scrollX(MathUtils.clamp(this.amountX, 0.0f, this.maxX));
            scrollY(MathUtils.clamp(this.amountY, 0.0f, this.maxY));
            if (this.scrollX) {
                if (hScrollKnob != null) {
                    this.hScrollBounds.set(bgLeftWidth, bgBottomHeight, this.areaWidth, this.style.hScroll != null ? this.style.hScroll.getMinHeight() : hScrollKnob.getMinHeight());
                    this.hKnobBounds.width = Math.max(hScrollKnob.getMinWidth(), (float) ((int) ((this.hScrollBounds.width * this.areaWidth) / widgetWidth2)));
                    this.hKnobBounds.height = hScrollKnob.getMinHeight();
                    this.hKnobBounds.f161x = this.hScrollBounds.f161x + ((float) ((int) ((this.hScrollBounds.width - this.hKnobBounds.width) * getScrollPercentX())));
                    this.hKnobBounds.f162y = this.hScrollBounds.f162y;
                } else {
                    this.hScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                    this.hKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                }
            }
            if (this.scrollY) {
                if (vScrollKnob != null) {
                    float vScrollWidth = this.style.vScroll != null ? this.style.vScroll.getMinWidth() : vScrollKnob.getMinWidth();
                    this.vScrollBounds.set((width - bgRightWidth) - vScrollWidth, (height - bgTopHeight) - this.areaHeight, vScrollWidth, this.areaHeight);
                    this.vKnobBounds.width = vScrollKnob.getMinWidth();
                    this.vKnobBounds.height = Math.max(vScrollKnob.getMinHeight(), (float) ((int) ((this.vScrollBounds.height * this.areaHeight) / widgetHeight2)));
                    this.vKnobBounds.f161x = (width - bgRightWidth) - vScrollKnob.getMinWidth();
                    this.vKnobBounds.f162y = this.vScrollBounds.f162y + ((float) ((int) ((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - getScrollPercentY()))));
                } else {
                    this.vScrollBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                    this.vKnobBounds.set(0.0f, 0.0f, 0.0f, 0.0f);
                }
            }
            if (this.widget.getWidth() != widgetWidth2 || this.widget.getHeight() != widgetHeight2) {
                this.widget.setWidth(widgetWidth2);
                this.widget.setHeight(widgetHeight2);
                if (this.widget instanceof Layout) {
                    Layout layout2 = (Layout) this.widget;
                    layout2.invalidate();
                    layout2.validate();
                }
            } else if (this.widget instanceof Layout) {
                ((Layout) this.widget).validate();
            }
        }
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        float y;
        if (this.widget != null) {
            validate();
            applyTransform(batch, computeTransform());
            if (this.scrollX) {
                this.hKnobBounds.f161x = this.hScrollBounds.f161x + ((float) ((int) ((this.hScrollBounds.width - this.hKnobBounds.width) * getScrollPercentX())));
            }
            if (this.scrollY) {
                this.vKnobBounds.f162y = this.vScrollBounds.f162y + ((float) ((int) ((this.vScrollBounds.height - this.vKnobBounds.height) * (1.0f - getScrollPercentY()))));
            }
            float y2 = this.widgetAreaBounds.f162y;
            if (!this.scrollY) {
                y = y2 - ((float) ((int) this.maxY));
            } else {
                y = y2 - ((float) ((int) (this.maxY - this.visualAmountY)));
            }
            if (!this.fadeScrollBars && this.scrollbarsOnTop && this.scrollX) {
                float scrollbarHeight = 0.0f;
                if (this.style.hScrollKnob != null) {
                    scrollbarHeight = this.style.hScrollKnob.getMinHeight();
                }
                if (this.style.hScroll != null) {
                    scrollbarHeight = Math.max(scrollbarHeight, this.style.hScroll.getMinHeight());
                }
                y += scrollbarHeight;
            }
            float x = this.widgetAreaBounds.f161x;
            if (this.scrollX) {
                x -= (float) ((int) this.visualAmountX);
            }
            this.widget.setPosition(x, y);
            if (this.widget instanceof Cullable) {
                this.widgetCullingArea.f161x = (-this.widget.getX()) + this.widgetAreaBounds.f161x;
                this.widgetCullingArea.f162y = (-this.widget.getY()) + this.widgetAreaBounds.f162y;
                this.widgetCullingArea.width = this.widgetAreaBounds.width;
                this.widgetCullingArea.height = this.widgetAreaBounds.height;
                ((Cullable) this.widget).setCullingArea(this.widgetCullingArea);
            }
            ScissorStack.calculateScissors(getStage().getCamera(), batch.getTransformMatrix(), this.widgetAreaBounds, this.scissorBounds);
            Color color = getColor();
            batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
            if (this.style.background != null) {
                this.style.background.draw(batch, 0.0f, 0.0f, getWidth(), getHeight());
            }
            batch.flush();
            if (ScissorStack.pushScissors(this.scissorBounds)) {
                drawChildren(batch, parentAlpha);
                ScissorStack.popScissors();
            }
            batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha * Interpolation.fade.apply(this.fadeAlpha / this.fadeAlphaSeconds));
            if (this.scrollX && this.scrollY && this.style.corner != null) {
                this.style.corner.draw(batch, this.hScrollBounds.width + this.hScrollBounds.f161x, this.hScrollBounds.f162y, this.vScrollBounds.width, this.vScrollBounds.f162y);
            }
            if (this.scrollX) {
                if (this.style.hScroll != null) {
                    this.style.hScroll.draw(batch, this.hScrollBounds.f161x, this.hScrollBounds.f162y, this.hScrollBounds.width, this.hScrollBounds.height);
                }
                if (this.style.hScrollKnob != null) {
                    this.style.hScrollKnob.draw(batch, this.hKnobBounds.f161x, this.hKnobBounds.f162y, this.hKnobBounds.width, this.hKnobBounds.height);
                }
            }
            if (this.scrollY) {
                if (this.style.vScroll != null) {
                    this.style.vScroll.draw(batch, this.vScrollBounds.f161x, this.vScrollBounds.f162y, this.vScrollBounds.width, this.vScrollBounds.height);
                }
                if (this.style.vScrollKnob != null) {
                    this.style.vScrollKnob.draw(batch, this.vKnobBounds.f161x, this.vKnobBounds.f162y, this.vKnobBounds.width, this.vKnobBounds.height);
                }
            }
            resetTransform(batch);
        }
    }

    public float getPrefWidth() {
        if (this.widget instanceof Layout) {
            return ((Layout) this.widget).getPrefWidth();
        }
        return 150.0f;
    }

    public float getPrefHeight() {
        if (this.widget instanceof Layout) {
            return ((Layout) this.widget).getPrefHeight();
        }
        return 150.0f;
    }

    public float getMinWidth() {
        return 0.0f;
    }

    public float getMinHeight() {
        return 0.0f;
    }

    public void setWidget(Actor widget2) {
        if (this.widget != null) {
            super.removeActor(this.widget);
        }
        this.widget = widget2;
        if (widget2 != null) {
            super.addActor(widget2);
        }
    }

    public Actor getWidget() {
        return this.widget;
    }

    public void addActor(Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public void addActorAt(int index, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public void addActorBefore(Actor actorBefore, Actor actor) {
        throw new UnsupportedOperationException("Use ScrollPane#setWidget.");
    }

    public boolean removeActor(Actor actor) {
        if (actor != this.widget) {
            return false;
        }
        setWidget((Actor) null);
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public Actor hit(float x, float y, boolean touchable) {
        if (x < 0.0f || x >= getWidth() || y < 0.0f || y >= getHeight()) {
            return null;
        }
        if (!this.scrollX || !this.hScrollBounds.contains(x, y)) {
            return (!this.scrollY || !this.vScrollBounds.contains(x, y)) ? super.hit(x, y, touchable) : this;
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void scrollX(float pixelsX) {
        this.amountX = pixelsX;
    }

    /* access modifiers changed from: protected */
    public void scrollY(float pixelsY) {
        this.amountY = pixelsY;
    }

    /* access modifiers changed from: protected */
    public void visualScrollX(float pixelsX) {
        this.visualAmountX = pixelsX;
    }

    /* access modifiers changed from: protected */
    public void visualScrollY(float pixelsY) {
        this.visualAmountY = pixelsY;
    }

    public void setScrollX(float pixels) {
        scrollX(MathUtils.clamp(pixels, 0.0f, this.maxX));
    }

    public float getScrollX() {
        return this.amountX;
    }

    public void setScrollY(float pixels) {
        scrollY(MathUtils.clamp(pixels, 0.0f, this.maxY));
    }

    public float getScrollY() {
        return this.amountY;
    }

    public float getVisualScrollX() {
        if (!this.scrollX) {
            return 0.0f;
        }
        return this.visualAmountX;
    }

    public float getVisualScrollY() {
        if (!this.scrollY) {
            return 0.0f;
        }
        return this.visualAmountY;
    }

    public float getScrollPercentX() {
        return MathUtils.clamp(this.amountX / this.maxX, 0.0f, 1.0f);
    }

    public void setScrollPercentX(float percentX) {
        scrollX(this.maxX * MathUtils.clamp(percentX, 0.0f, 1.0f));
    }

    public float getScrollPercentY() {
        return MathUtils.clamp(this.amountY / this.maxY, 0.0f, 1.0f);
    }

    public void setScrollPercentY(float percentY) {
        scrollY(this.maxY * MathUtils.clamp(percentY, 0.0f, 1.0f));
    }

    public void setFlickScroll(boolean flickScroll2) {
        if (this.flickScroll != flickScroll2) {
            this.flickScroll = flickScroll2;
            if (flickScroll2) {
                addListener(this.flickScrollListener);
            } else {
                removeListener(this.flickScrollListener);
            }
            invalidate();
        }
    }

    public void scrollTo(float x, float y, float width, float height) {
        float amountX2 = this.amountX;
        if (x + width > this.areaWidth + amountX2) {
            amountX2 = (x + width) - this.areaWidth;
        }
        if (x < amountX2) {
            amountX2 = x;
        }
        scrollX(MathUtils.clamp(amountX2, 0.0f, this.maxX));
        float amountY2 = this.amountY;
        if (amountY2 > ((this.maxY - y) - height) + this.areaHeight) {
            amountY2 = ((this.maxY - y) - height) + this.areaHeight;
        }
        if (amountY2 < this.maxY - y) {
            amountY2 = this.maxY - y;
        }
        scrollY(MathUtils.clamp(amountY2, 0.0f, this.maxY));
    }

    public void scrollToCenter(float x, float y, float width, float height) {
        float amountX2 = this.amountX;
        if (x + width > this.areaWidth + amountX2) {
            amountX2 = (x + width) - this.areaWidth;
        }
        if (x < amountX2) {
            amountX2 = x;
        }
        scrollX(MathUtils.clamp(amountX2, 0.0f, this.maxX));
        float amountY2 = this.amountY;
        float centerY = ((this.maxY - y) + (this.areaHeight / 2.0f)) - (height / 2.0f);
        if (amountY2 < centerY - (this.areaHeight / 4.0f) || amountY2 > (this.areaHeight / 4.0f) + centerY) {
            amountY2 = centerY;
        }
        scrollY(MathUtils.clamp(amountY2, 0.0f, this.maxY));
    }

    public float getMaxX() {
        return this.maxX;
    }

    public float getMaxY() {
        return this.maxY;
    }

    public float getScrollBarHeight() {
        if (this.style.hScrollKnob == null || !this.scrollX) {
            return 0.0f;
        }
        return this.style.hScrollKnob.getMinHeight();
    }

    public float getScrollBarWidth() {
        if (this.style.vScrollKnob == null || !this.scrollY) {
            return 0.0f;
        }
        return this.style.vScrollKnob.getMinWidth();
    }

    public boolean isScrollX() {
        return this.scrollX;
    }

    public boolean isScrollY() {
        return this.scrollY;
    }

    public void setScrollingDisabled(boolean x, boolean y) {
        this.disableX = x;
        this.disableY = y;
    }

    public boolean isDragging() {
        return this.draggingPointer != -1;
    }

    public boolean isPanning() {
        return this.flickScrollListener.getGestureDetector().isPanning();
    }

    public boolean isFlinging() {
        return this.flingTimer > 0.0f;
    }

    public void setVelocityX(float velocityX2) {
        this.velocityX = velocityX2;
    }

    public float getVelocityX() {
        if (this.flingTimer <= 0.0f) {
            return 0.0f;
        }
        float alpha = this.flingTimer / this.flingTime;
        float alpha2 = alpha * alpha * alpha;
        return this.velocityX * alpha2 * alpha2 * alpha2;
    }

    public void setVelocityY(float velocityY2) {
        this.velocityY = velocityY2;
    }

    public float getVelocityY() {
        return this.velocityY;
    }

    public void setOverscroll(boolean overscrollX2, boolean overscrollY2) {
        this.overscrollX = overscrollX2;
        this.overscrollY = overscrollY2;
    }

    public void setupOverscroll(float distance, float speedMin, float speedMax) {
        this.overscrollDistance = distance;
        this.overscrollSpeedMin = speedMin;
        this.overscrollSpeedMax = speedMax;
    }

    public void setForceOverscroll(boolean x, boolean y) {
        this.forceOverscrollX = x;
        this.forceOverscrollY = y;
    }

    public void setFlingTime(float flingTime2) {
        this.flingTime = flingTime2;
    }

    public void setClamp(boolean clamp2) {
        this.clamp = clamp2;
    }

    public void setFadeScrollBars(boolean fadeScrollBars2) {
        if (this.fadeScrollBars != fadeScrollBars2) {
            this.fadeScrollBars = fadeScrollBars2;
            if (!fadeScrollBars2) {
                this.fadeAlpha = this.fadeAlphaSeconds;
            }
            invalidate();
        }
    }

    public void setupFadeScrollBars(float fadeAlphaSeconds2, float fadeDelaySeconds2) {
        this.fadeAlphaSeconds = fadeAlphaSeconds2;
        this.fadeDelaySeconds = fadeDelaySeconds2;
    }

    public void setSmoothScrolling(boolean smoothScrolling2) {
        this.smoothScrolling = smoothScrolling2;
    }

    public void setScrollbarsOnTop(boolean scrollbarsOnTop2) {
        this.scrollbarsOnTop = scrollbarsOnTop2;
        invalidate();
    }

    public void setCancelTouchFocus(boolean cancelTouchFocus2) {
        this.cancelTouchFocus = cancelTouchFocus2;
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ScrollPane$ScrollPaneStyle */
    public static class ScrollPaneStyle {
        public Drawable background;
        public Drawable corner;
        public Drawable hScroll;
        public Drawable hScrollKnob;
        public Drawable vScroll;
        public Drawable vScrollKnob;

        public ScrollPaneStyle() {
        }

        public ScrollPaneStyle(Drawable background2, Drawable hScroll2, Drawable hScrollKnob2, Drawable vScroll2, Drawable vScrollKnob2) {
            this.background = background2;
            this.hScroll = hScroll2;
            this.hScrollKnob = hScrollKnob2;
            this.vScroll = vScroll2;
            this.vScrollKnob = vScrollKnob2;
        }

        public ScrollPaneStyle(ScrollPaneStyle style) {
            this.background = style.background;
            this.hScroll = style.hScroll;
            this.hScrollKnob = style.hScrollKnob;
            this.vScroll = style.vScroll;
            this.vScrollKnob = style.vScrollKnob;
        }
    }
}
