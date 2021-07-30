package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxRuntimeException;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.SplitPane */
public class SplitPane extends WidgetGroup {
    private Rectangle firstScissors;
    private Actor firstWidget;
    private Rectangle firstWidgetBounds;
    Rectangle handleBounds;
    Vector2 handlePosition;
    Vector2 lastPoint;
    float maxAmount;
    float minAmount;
    private float oldSplitAmount;
    private Rectangle secondScissors;
    private Actor secondWidget;
    private Rectangle secondWidgetBounds;
    float splitAmount;
    SplitPaneStyle style;
    boolean vertical;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public SplitPane(Actor firstWidget2, Actor secondWidget2, boolean vertical2, Skin skin) {
        this(firstWidget2, secondWidget2, vertical2, skin, "default-" + (vertical2 ? "vertical" : "horizontal"));
    }

    public SplitPane(Actor firstWidget2, Actor secondWidget2, boolean vertical2, Skin skin, String styleName) {
        this(firstWidget2, secondWidget2, vertical2, (SplitPaneStyle) skin.get(styleName, SplitPaneStyle.class));
    }

    public SplitPane(Actor firstWidget2, Actor secondWidget2, boolean vertical2, SplitPaneStyle style2) {
        this.splitAmount = 0.5f;
        this.maxAmount = 1.0f;
        this.firstWidgetBounds = new Rectangle();
        this.secondWidgetBounds = new Rectangle();
        this.handleBounds = new Rectangle();
        this.firstScissors = new Rectangle();
        this.secondScissors = new Rectangle();
        this.lastPoint = new Vector2();
        this.handlePosition = new Vector2();
        this.firstWidget = firstWidget2;
        this.secondWidget = secondWidget2;
        this.vertical = vertical2;
        setStyle(style2);
        setFirstWidget(firstWidget2);
        setSecondWidget(secondWidget2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
        initialize();
    }

    private void initialize() {
        addListener(new InputListener() {
            int draggingPointer = -1;

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (this.draggingPointer != -1) {
                    return false;
                }
                if ((pointer == 0 && button != 0) || !SplitPane.this.handleBounds.contains(x, y)) {
                    return false;
                }
                this.draggingPointer = pointer;
                SplitPane.this.lastPoint.set(x, y);
                SplitPane.this.handlePosition.set(SplitPane.this.handleBounds.f161x, SplitPane.this.handleBounds.f162y);
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == this.draggingPointer) {
                    this.draggingPointer = -1;
                }
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (pointer == this.draggingPointer) {
                    Drawable handle = SplitPane.this.style.handle;
                    if (!SplitPane.this.vertical) {
                        float delta = x - SplitPane.this.lastPoint.f165x;
                        float availWidth = SplitPane.this.getWidth() - handle.getMinWidth();
                        float dragX = SplitPane.this.handlePosition.f165x + delta;
                        SplitPane.this.handlePosition.f165x = dragX;
                        SplitPane.this.splitAmount = Math.min(availWidth, Math.max(0.0f, dragX)) / availWidth;
                        if (SplitPane.this.splitAmount < SplitPane.this.minAmount) {
                            SplitPane.this.splitAmount = SplitPane.this.minAmount;
                        }
                        if (SplitPane.this.splitAmount > SplitPane.this.maxAmount) {
                            SplitPane.this.splitAmount = SplitPane.this.maxAmount;
                        }
                        SplitPane.this.lastPoint.set(x, y);
                    } else {
                        float delta2 = y - SplitPane.this.lastPoint.f166y;
                        float availHeight = SplitPane.this.getHeight() - handle.getMinHeight();
                        float dragY = SplitPane.this.handlePosition.f166y + delta2;
                        SplitPane.this.handlePosition.f166y = dragY;
                        SplitPane.this.splitAmount = 1.0f - (Math.min(availHeight, Math.max(0.0f, dragY)) / availHeight);
                        if (SplitPane.this.splitAmount < SplitPane.this.minAmount) {
                            SplitPane.this.splitAmount = SplitPane.this.minAmount;
                        }
                        if (SplitPane.this.splitAmount > SplitPane.this.maxAmount) {
                            SplitPane.this.splitAmount = SplitPane.this.maxAmount;
                        }
                        SplitPane.this.lastPoint.set(x, y);
                    }
                    SplitPane.this.invalidate();
                }
            }
        });
    }

    public void setStyle(SplitPaneStyle style2) {
        this.style = style2;
        invalidateHierarchy();
    }

    public SplitPaneStyle getStyle() {
        return this.style;
    }

    public void layout() {
        if (!this.vertical) {
            calculateHorizBoundsAndPositions();
        } else {
            calculateVertBoundsAndPositions();
        }
        Actor firstWidget2 = this.firstWidget;
        Rectangle firstWidgetBounds2 = this.firstWidgetBounds;
        if (firstWidget2 != null) {
            firstWidget2.setX(firstWidgetBounds2.f161x);
            firstWidget2.setY(firstWidgetBounds2.f162y);
            if (firstWidget2.getWidth() != firstWidgetBounds2.width || firstWidget2.getHeight() != firstWidgetBounds2.height) {
                firstWidget2.setWidth(firstWidgetBounds2.width);
                firstWidget2.setHeight(firstWidgetBounds2.height);
                if (firstWidget2 instanceof Layout) {
                    Layout layout = (Layout) firstWidget2;
                    layout.invalidate();
                    layout.validate();
                }
            } else if (firstWidget2 instanceof Layout) {
                ((Layout) firstWidget2).validate();
            }
        }
        Actor secondWidget2 = this.secondWidget;
        Rectangle secondWidgetBounds2 = this.secondWidgetBounds;
        if (secondWidget2 != null) {
            secondWidget2.setX(secondWidgetBounds2.f161x);
            secondWidget2.setY(secondWidgetBounds2.f162y);
            if (secondWidget2.getWidth() != secondWidgetBounds2.width || secondWidget2.getHeight() != secondWidgetBounds2.height) {
                secondWidget2.setWidth(secondWidgetBounds2.width);
                secondWidget2.setHeight(secondWidgetBounds2.height);
                if (secondWidget2 instanceof Layout) {
                    Layout layout2 = (Layout) secondWidget2;
                    layout2.invalidate();
                    layout2.validate();
                }
            } else if (secondWidget2 instanceof Layout) {
                ((Layout) secondWidget2).validate();
            }
        }
    }

    public float getPrefWidth() {
        float width = (this.firstWidget instanceof Layout ? ((Layout) this.firstWidget).getPrefWidth() : this.firstWidget.getWidth()) + (this.secondWidget instanceof Layout ? ((Layout) this.secondWidget).getPrefWidth() : this.secondWidget.getWidth());
        if (!this.vertical) {
            return width + this.style.handle.getMinWidth();
        }
        return width;
    }

    public float getPrefHeight() {
        float height = (this.firstWidget instanceof Layout ? ((Layout) this.firstWidget).getPrefHeight() : this.firstWidget.getHeight()) + (this.secondWidget instanceof Layout ? ((Layout) this.secondWidget).getPrefHeight() : this.secondWidget.getHeight());
        if (this.vertical) {
            return height + this.style.handle.getMinHeight();
        }
        return height;
    }

    public float getMinWidth() {
        return 0.0f;
    }

    public float getMinHeight() {
        return 0.0f;
    }

    public void setVertical(boolean vertical2) {
        this.vertical = vertical2;
    }

    private void calculateHorizBoundsAndPositions() {
        Drawable handle = this.style.handle;
        float height = getHeight();
        float availWidth = getWidth() - handle.getMinWidth();
        float leftAreaWidth = (float) ((int) (this.splitAmount * availWidth));
        float handleWidth = handle.getMinWidth();
        this.firstWidgetBounds.set(0.0f, 0.0f, leftAreaWidth, height);
        this.secondWidgetBounds.set(leftAreaWidth + handleWidth, 0.0f, availWidth - leftAreaWidth, height);
        this.handleBounds.set(leftAreaWidth, 0.0f, handleWidth, height);
    }

    private void calculateVertBoundsAndPositions() {
        Drawable handle = this.style.handle;
        float width = getWidth();
        float height = getHeight();
        float availHeight = height - handle.getMinHeight();
        float topAreaHeight = (float) ((int) (this.splitAmount * availHeight));
        float bottomAreaHeight = availHeight - topAreaHeight;
        float handleHeight = handle.getMinHeight();
        this.firstWidgetBounds.set(0.0f, height - topAreaHeight, width, topAreaHeight);
        this.secondWidgetBounds.set(0.0f, 0.0f, width, bottomAreaHeight);
        this.handleBounds.set(0.0f, bottomAreaHeight, width, handleHeight);
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        validate();
        Color color = getColor();
        Drawable handle = this.style.handle;
        applyTransform(batch, computeTransform());
        Matrix4 transform = batch.getTransformMatrix();
        if (this.firstWidget != null) {
            ScissorStack.calculateScissors(getStage().getCamera(), transform, this.firstWidgetBounds, this.firstScissors);
            if (ScissorStack.pushScissors(this.firstScissors)) {
                if (this.firstWidget.isVisible()) {
                    this.firstWidget.draw(batch, color.f67a * parentAlpha);
                }
                batch.flush();
                ScissorStack.popScissors();
            }
        }
        if (this.secondWidget != null) {
            ScissorStack.calculateScissors(getStage().getCamera(), transform, this.secondWidgetBounds, this.secondScissors);
            if (ScissorStack.pushScissors(this.secondScissors)) {
                if (this.secondWidget.isVisible()) {
                    this.secondWidget.draw(batch, color.f67a * parentAlpha);
                }
                batch.flush();
                ScissorStack.popScissors();
            }
        }
        batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a);
        handle.draw(batch, this.handleBounds.f161x, this.handleBounds.f162y, this.handleBounds.width, this.handleBounds.height);
        resetTransform(batch);
    }

    public void setSplitAmount(float split) {
        this.splitAmount = Math.max(Math.min(this.maxAmount, split), this.minAmount);
        invalidate();
    }

    public float getSplit() {
        return this.splitAmount;
    }

    public void setMinSplitAmount(float minAmount2) {
        if (minAmount2 < 0.0f) {
            throw new GdxRuntimeException("minAmount has to be >= 0");
        } else if (minAmount2 >= this.maxAmount) {
            throw new GdxRuntimeException("minAmount has to be < maxAmount");
        } else {
            this.minAmount = minAmount2;
        }
    }

    public void setMaxSplitAmount(float maxAmount2) {
        if (maxAmount2 > 1.0f) {
            throw new GdxRuntimeException("maxAmount has to be >= 0");
        } else if (maxAmount2 <= this.minAmount) {
            throw new GdxRuntimeException("maxAmount has to be > minAmount");
        } else {
            this.maxAmount = maxAmount2;
        }
    }

    public void setFirstWidget(Actor widget) {
        if (this.firstWidget != null) {
            super.removeActor(this.firstWidget);
        }
        this.firstWidget = widget;
        if (widget != null) {
            super.addActor(widget);
        }
        invalidate();
    }

    public void setSecondWidget(Actor widget) {
        if (this.secondWidget != null) {
            super.removeActor(this.secondWidget);
        }
        this.secondWidget = widget;
        if (widget != null) {
            super.addActor(widget);
        }
        invalidate();
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
        throw new UnsupportedOperationException("Use ScrollPane#setWidget(null).");
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.SplitPane$SplitPaneStyle */
    public static class SplitPaneStyle {
        public Drawable handle;

        public SplitPaneStyle() {
        }

        public SplitPaneStyle(Drawable handle2) {
            this.handle = handle2;
        }

        public SplitPaneStyle(SplitPaneStyle style) {
            this.handle = style.handle;
        }
    }
}
