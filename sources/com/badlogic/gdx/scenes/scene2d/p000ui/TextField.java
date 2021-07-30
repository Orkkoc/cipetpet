package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField */
public class TextField extends Widget {
    private static final char BACKSPACE = '\b';
    private static final char BULLET = '';
    private static final char DELETE = '';
    private static final char ENTER_ANDROID = '\n';
    private static final char ENTER_DESKTOP = '\r';
    private static final char TAB = '\t';
    private float blinkTime;
    private Clipboard clipboard;
    int cursor;
    boolean cursorOn;
    boolean disabled;
    private CharSequence displayText;
    private final Rectangle fieldBounds;
    TextFieldFilter filter;
    boolean focusTraversal;
    private final FloatArray glyphAdvances;
    final FloatArray glyphPositions;
    boolean hasSelection;
    InputListener inputListener;
    float keyRepeatInitialTime;
    KeyRepeatTask keyRepeatTask;
    float keyRepeatTime;
    OnscreenKeyboard keyboard;
    long lastBlink;
    TextFieldListener listener;
    int maxLength;
    String messageText;
    private StringBuilder passwordBuffer;
    private char passwordCharacter;
    private boolean passwordMode;
    float renderOffset;
    boolean rightAligned;
    private final Rectangle scissor;
    int selectionStart;
    private float selectionWidth;
    private float selectionX;
    TextFieldStyle style;
    String text;
    private final BitmapFont.TextBounds textBounds;
    float textOffset;
    private int visibleTextEnd;
    private int visibleTextStart;

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$OnscreenKeyboard */
    public interface OnscreenKeyboard {
        void show(boolean z);
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldListener */
    public interface TextFieldListener {
        void keyTyped(TextField textField, char c);
    }

    public TextField(String text2, Skin skin) {
        this(text2, (TextFieldStyle) skin.get(TextFieldStyle.class));
    }

    public TextField(String text2, Skin skin, String styleName) {
        this(text2, (TextFieldStyle) skin.get(styleName, TextFieldStyle.class));
    }

    public TextField(String text2, TextFieldStyle style2) {
        this.keyboard = new DefaultOnscreenKeyboard();
        this.focusTraversal = true;
        this.fieldBounds = new Rectangle();
        this.textBounds = new BitmapFont.TextBounds();
        this.scissor = new Rectangle();
        this.glyphAdvances = new FloatArray();
        this.glyphPositions = new FloatArray();
        this.cursorOn = true;
        this.blinkTime = 0.32f;
        this.passwordCharacter = BULLET;
        this.keyRepeatTask = new KeyRepeatTask();
        this.keyRepeatInitialTime = 0.4f;
        this.keyRepeatTime = 0.1f;
        this.maxLength = 0;
        setStyle(style2);
        this.clipboard = Gdx.app.getClipboard();
        setText(text2);
        setWidth(getPrefWidth());
        setHeight(getPrefHeight());
        initialize();
    }

    private void initialize() {
        C01081 r0 = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() > 1) {
                    TextField.this.setSelection(0, TextField.this.text.length());
                }
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!super.touchDown(event, x, y, pointer, button)) {
                    return false;
                }
                if (pointer == 0 && button != 0) {
                    return false;
                }
                if (TextField.this.disabled) {
                    return true;
                }
                TextField.this.clearSelection();
                setCursorPosition(x);
                TextField.this.selectionStart = TextField.this.cursor;
                Stage stage = TextField.this.getStage();
                if (stage != null) {
                    stage.setKeyboardFocus(TextField.this);
                }
                TextField.this.keyboard.show(true);
                return true;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                TextField.this.lastBlink = 0;
                TextField.this.cursorOn = false;
                setCursorPosition(x);
                TextField.this.hasSelection = true;
            }

            private void setCursorPosition(float x) {
                TextField.this.lastBlink = 0;
                TextField.this.cursorOn = false;
                float x2 = x - (TextField.this.renderOffset + TextField.this.textOffset);
                for (int i = 0; i < TextField.this.glyphPositions.size; i++) {
                    if (TextField.this.glyphPositions.items[i] > x2) {
                        TextField.this.cursor = Math.max(0, i - 1);
                        return;
                    }
                }
                TextField.this.cursor = Math.max(0, TextField.this.glyphPositions.size - 1);
            }

            public boolean keyDown(InputEvent event, int keycode) {
                char c;
                char c2;
                char c3;
                char c4;
                if (TextField.this.disabled) {
                    return false;
                }
                BitmapFont bitmapFont = TextField.this.style.font;
                TextField.this.lastBlink = 0;
                TextField.this.cursorOn = false;
                Stage stage = TextField.this.getStage();
                if (stage == null || stage.getKeyboardFocus() != TextField.this) {
                    return false;
                }
                boolean repeat = false;
                boolean ctrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
                if (ctrl) {
                    if (keycode == 50) {
                        TextField.this.paste();
                        return true;
                    } else if (keycode == 31 || keycode == 133) {
                        TextField.this.copy();
                        return true;
                    } else if (keycode == 52 || keycode == 67) {
                        TextField.this.cut();
                        return true;
                    }
                }
                if (Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60)) {
                    if (keycode == 133) {
                        TextField.this.paste();
                    }
                    if (keycode == 112 && TextField.this.hasSelection) {
                        TextField.this.copy();
                        TextField.this.delete();
                    }
                    if (keycode == 21) {
                        if (!TextField.this.hasSelection) {
                            TextField.this.selectionStart = TextField.this.cursor;
                            TextField.this.hasSelection = true;
                        }
                        while (true) {
                            TextField textField = TextField.this;
                            int i = textField.cursor - 1;
                            textField.cursor = i;
                            if (i <= 0 || !ctrl || (((c2 = TextField.this.text.charAt(TextField.this.cursor)) < 'A' || c2 > 'Z') && ((c2 < 'a' || c2 > 'z') && (c2 < '0' || c2 > '9')))) {
                                repeat = true;
                            }
                        }
                        repeat = true;
                    }
                    if (keycode == 22) {
                        if (!TextField.this.hasSelection) {
                            TextField.this.selectionStart = TextField.this.cursor;
                            TextField.this.hasSelection = true;
                        }
                        int length = TextField.this.text.length();
                        while (true) {
                            TextField textField2 = TextField.this;
                            int i2 = textField2.cursor + 1;
                            textField2.cursor = i2;
                            if (i2 >= length || !ctrl || (((c = TextField.this.text.charAt(TextField.this.cursor - 1)) < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && (c < '0' || c > '9')))) {
                                repeat = true;
                            }
                        }
                        repeat = true;
                    }
                    if (keycode == 3) {
                        if (!TextField.this.hasSelection) {
                            TextField.this.selectionStart = TextField.this.cursor;
                            TextField.this.hasSelection = true;
                        }
                        TextField.this.cursor = 0;
                    }
                    if (keycode == 132) {
                        if (!TextField.this.hasSelection) {
                            TextField.this.selectionStart = TextField.this.cursor;
                            TextField.this.hasSelection = true;
                        }
                        TextField.this.cursor = TextField.this.text.length();
                    }
                    TextField.this.cursor = Math.max(0, TextField.this.cursor);
                    TextField.this.cursor = Math.min(TextField.this.text.length(), TextField.this.cursor);
                } else {
                    if (keycode == 21) {
                        while (true) {
                            TextField textField3 = TextField.this;
                            int i3 = textField3.cursor;
                            textField3.cursor = i3 - 1;
                            if (i3 <= 1 || !ctrl || (((c4 = TextField.this.text.charAt(TextField.this.cursor - 1)) < 'A' || c4 > 'Z') && ((c4 < 'a' || c4 > 'z') && (c4 < '0' || c4 > '9')))) {
                                TextField.this.clearSelection();
                                repeat = true;
                            }
                        }
                        TextField.this.clearSelection();
                        repeat = true;
                    }
                    if (keycode == 22) {
                        int length2 = TextField.this.text.length();
                        while (true) {
                            TextField textField4 = TextField.this;
                            int i4 = textField4.cursor + 1;
                            textField4.cursor = i4;
                            if (i4 >= length2 || !ctrl || (((c3 = TextField.this.text.charAt(TextField.this.cursor - 1)) < 'A' || c3 > 'Z') && ((c3 < 'a' || c3 > 'z') && (c3 < '0' || c3 > '9')))) {
                                TextField.this.clearSelection();
                                repeat = true;
                            }
                        }
                        TextField.this.clearSelection();
                        repeat = true;
                    }
                    if (keycode == 3) {
                        TextField.this.cursor = 0;
                        TextField.this.clearSelection();
                    }
                    if (keycode == 132) {
                        TextField.this.cursor = TextField.this.text.length();
                        TextField.this.clearSelection();
                    }
                    TextField.this.cursor = Math.max(0, TextField.this.cursor);
                    TextField.this.cursor = Math.min(TextField.this.text.length(), TextField.this.cursor);
                }
                if (repeat && (!TextField.this.keyRepeatTask.isScheduled() || TextField.this.keyRepeatTask.keycode != keycode)) {
                    TextField.this.keyRepeatTask.keycode = keycode;
                    TextField.this.keyRepeatTask.cancel();
                    Timer.schedule(TextField.this.keyRepeatTask, TextField.this.keyRepeatInitialTime, TextField.this.keyRepeatTime);
                }
                return true;
            }

            public boolean keyUp(InputEvent event, int keycode) {
                if (TextField.this.disabled) {
                    return false;
                }
                TextField.this.keyRepeatTask.cancel();
                return true;
            }

            public boolean keyTyped(InputEvent event, char character) {
                if (TextField.this.disabled) {
                    return false;
                }
                BitmapFont font = TextField.this.style.font;
                Stage stage = TextField.this.getStage();
                if (stage == null || stage.getKeyboardFocus() != TextField.this) {
                    return false;
                }
                if (character == 8 && (TextField.this.cursor > 0 || TextField.this.hasSelection)) {
                    if (!TextField.this.hasSelection) {
                        TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor - 1) + TextField.this.text.substring(TextField.this.cursor);
                        TextField.this.updateDisplayText();
                        TextField textField = TextField.this;
                        textField.cursor--;
                        TextField.this.renderOffset = 0.0f;
                    } else {
                        TextField.this.delete();
                    }
                }
                if (character == 127) {
                    if (TextField.this.cursor >= TextField.this.text.length() && !TextField.this.hasSelection) {
                        return true;
                    }
                    if (!TextField.this.hasSelection) {
                        TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + TextField.this.text.substring(TextField.this.cursor + 1);
                        TextField.this.updateDisplayText();
                        return true;
                    }
                    TextField.this.delete();
                    return true;
                } else if (character != 13 && character != 10 && TextField.this.filter != null && !TextField.this.filter.acceptChar(TextField.this, character)) {
                    return true;
                } else {
                    if ((character == 9 || character == 10) && TextField.this.focusTraversal) {
                        TextField.this.next(Gdx.input.isKeyPressed(59) || Gdx.input.isKeyPressed(60));
                    }
                    if (font.containsCharacter(character)) {
                        if (TextField.this.maxLength > 0 && TextField.this.text.length() + 1 > TextField.this.maxLength) {
                            return true;
                        }
                        if (!TextField.this.hasSelection) {
                            TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + character + TextField.this.text.substring(TextField.this.cursor, TextField.this.text.length());
                            TextField.this.updateDisplayText();
                            TextField.this.cursor++;
                        } else {
                            int minIndex = Math.min(TextField.this.cursor, TextField.this.selectionStart);
                            int maxIndex = Math.max(TextField.this.cursor, TextField.this.selectionStart);
                            TextField.this.text = (minIndex > 0 ? TextField.this.text.substring(0, minIndex) : "") + (maxIndex < TextField.this.text.length() ? TextField.this.text.substring(maxIndex, TextField.this.text.length()) : "");
                            TextField.this.cursor = minIndex;
                            TextField.this.text = TextField.this.text.substring(0, TextField.this.cursor) + character + TextField.this.text.substring(TextField.this.cursor, TextField.this.text.length());
                            TextField.this.updateDisplayText();
                            TextField.this.cursor++;
                            TextField.this.clearSelection();
                        }
                    }
                    if (TextField.this.listener == null) {
                        return true;
                    }
                    TextField.this.listener.keyTyped(TextField.this, character);
                    return true;
                }
            }
        };
        this.inputListener = r0;
        addListener(r0);
    }

    public void setMaxLength(int maxLength2) {
        this.maxLength = maxLength2;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setStyle(TextFieldStyle style2) {
        if (style2 == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style2;
        invalidateHierarchy();
    }

    public void setPasswordCharacter(char passwordCharacter2) {
        this.passwordCharacter = passwordCharacter2;
    }

    public TextFieldStyle getStyle() {
        return this.style;
    }

    private void calculateOffsets() {
        float visibleWidth = getWidth();
        if (this.style.background != null) {
            visibleWidth -= this.style.background.getLeftWidth() + this.style.background.getRightWidth();
        }
        float distance = this.glyphPositions.get(this.cursor) - Math.abs(this.renderOffset);
        if (distance <= 0.0f) {
            if (this.cursor > 0) {
                this.renderOffset = -this.glyphPositions.get(this.cursor - 1);
            } else {
                this.renderOffset = 0.0f;
            }
        } else if (distance > visibleWidth) {
            this.renderOffset -= distance - visibleWidth;
        }
        this.visibleTextStart = 0;
        this.textOffset = 0.0f;
        float start = Math.abs(this.renderOffset);
        int len = this.glyphPositions.size;
        float startPos = 0.0f;
        int i = 0;
        while (true) {
            if (i >= len) {
                break;
            } else if (this.glyphPositions.items[i] >= start) {
                this.visibleTextStart = i;
                startPos = this.glyphPositions.items[i];
                this.textOffset = startPos - start;
                break;
            } else {
                i++;
            }
        }
        this.visibleTextEnd = Math.min(this.displayText.length(), this.cursor + 1);
        while (this.visibleTextEnd <= this.displayText.length() && this.glyphPositions.items[this.visibleTextEnd] - startPos <= visibleWidth) {
            this.visibleTextEnd++;
        }
        this.visibleTextEnd = Math.max(0, this.visibleTextEnd - 1);
        if (this.hasSelection) {
            int minIndex = Math.min(this.cursor, this.selectionStart);
            int maxIndex = Math.max(this.cursor, this.selectionStart);
            float minX = Math.max(this.glyphPositions.get(minIndex), startPos);
            float maxX = Math.min(this.glyphPositions.get(maxIndex), this.glyphPositions.get(this.visibleTextEnd));
            this.selectionX = minX;
            this.selectionWidth = maxX - minX;
        }
        if (this.rightAligned) {
            this.textOffset = visibleWidth - (this.glyphPositions.items[this.visibleTextEnd] - startPos);
            if (this.hasSelection) {
                this.selectionX += this.textOffset;
            }
        }
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        Color fontColor;
        float textY;
        BitmapFont font = this.style.font;
        if (this.disabled) {
            fontColor = this.style.disabledFontColor;
        } else {
            fontColor = this.style.fontColor;
        }
        Drawable selection = this.style.selection;
        Drawable cursorPatch = this.style.cursor;
        Drawable background = this.style.background;
        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float textY2 = (this.textBounds.height / 2.0f) + font.getDescent();
        batch.setColor(color.f70r, color.f69g, color.f68b, color.f67a * parentAlpha);
        float bgLeftWidth = 0.0f;
        if (background != null) {
            background.draw(batch, x, y, width, height);
            bgLeftWidth = background.getLeftWidth();
            float bottom = background.getBottomHeight();
            textY = (float) ((int) ((((height - background.getTopHeight()) - bottom) / 2.0f) + textY2 + bottom));
        } else {
            textY = (float) ((int) ((height / 2.0f) + textY2));
        }
        calculateOffsets();
        Stage stage = getStage();
        boolean focused = stage != null && stage.getKeyboardFocus() == this;
        if (focused && this.hasSelection && selection != null) {
            selection.draw(batch, this.selectionX + x + bgLeftWidth + this.renderOffset, ((y + textY) - this.textBounds.height) - font.getDescent(), this.selectionWidth, this.textBounds.height + (font.getDescent() / 2.0f));
        }
        float yOffset = font.isFlipped() ? -this.textBounds.height : 0.0f;
        if (this.displayText.length() != 0) {
            font.setColor(fontColor.f70r, fontColor.f69g, fontColor.f68b, fontColor.f67a * parentAlpha);
            font.draw(batch, this.displayText, x + bgLeftWidth + this.textOffset, y + textY + yOffset, this.visibleTextStart, this.visibleTextEnd);
        } else if (!focused && this.messageText != null) {
            if (this.style.messageFontColor != null) {
                font.setColor(this.style.messageFontColor.f70r, this.style.messageFontColor.f69g, this.style.messageFontColor.f68b, this.style.messageFontColor.f67a * parentAlpha);
            } else {
                font.setColor(0.7f, 0.7f, 0.7f, parentAlpha);
            }
            if (this.style.messageFont != null) {
                BitmapFont bitmapFont = this.style.messageFont;
            } else {
                BitmapFont bitmapFont2 = font;
            }
            font.draw(batch, this.messageText, x + bgLeftWidth, y + textY + yOffset);
        }
        if (focused && !this.disabled) {
            blink();
            if (this.cursorOn && cursorPatch != null) {
                cursorPatch.draw(batch, ((((x + bgLeftWidth) + this.textOffset) + this.glyphPositions.get(this.cursor)) - this.glyphPositions.items[this.visibleTextStart]) - 1.0f, ((y + textY) - this.textBounds.height) - font.getDescent(), cursorPatch.getMinWidth(), this.textBounds.height + (font.getDescent() / 2.0f));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateDisplayText() {
        if (!this.passwordMode || !this.style.font.containsCharacter(this.passwordCharacter)) {
            this.displayText = this.text;
        } else {
            if (this.passwordBuffer == null) {
                this.passwordBuffer = new StringBuilder(this.text.length());
            }
            if (this.passwordBuffer.length() > this.text.length()) {
                this.passwordBuffer.setLength(this.text.length());
            } else {
                int n = this.text.length();
                for (int i = this.passwordBuffer.length(); i < n; i++) {
                    this.passwordBuffer.append(this.passwordCharacter);
                }
            }
            this.displayText = this.passwordBuffer;
        }
        this.style.font.computeGlyphAdvancesAndPositions(this.displayText, this.glyphAdvances, this.glyphPositions);
        if (this.selectionStart > this.text.length()) {
            this.selectionStart = this.text.length();
        }
    }

    private void blink() {
        long time = TimeUtils.nanoTime();
        if (((float) (time - this.lastBlink)) / 1.0E9f > this.blinkTime) {
            this.cursorOn = !this.cursorOn;
            this.lastBlink = time;
        }
    }

    public void copy() {
        if (this.hasSelection) {
            this.clipboard.setContents(this.text.substring(Math.min(this.cursor, this.selectionStart), Math.max(this.cursor, this.selectionStart)));
        }
    }

    public void cut() {
        if (this.hasSelection) {
            copy();
            delete();
        }
    }

    /* access modifiers changed from: package-private */
    public void paste() {
        String content = this.clipboard.getContents();
        if (content != null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length() && (this.maxLength <= 0 || this.text.length() + builder.length() + 1 <= this.maxLength); i++) {
                char c = content.charAt(i);
                if (this.style.font.containsCharacter(c) && (this.filter == null || this.filter.acceptChar(this, c))) {
                    builder.append(c);
                }
            }
            String content2 = builder.toString();
            if (!this.hasSelection) {
                this.text = this.text.substring(0, this.cursor) + content2 + this.text.substring(this.cursor, this.text.length());
                updateDisplayText();
                this.cursor += content2.length();
                return;
            }
            int minIndex = Math.min(this.cursor, this.selectionStart);
            int maxIndex = Math.max(this.cursor, this.selectionStart);
            this.text = (minIndex > 0 ? this.text.substring(0, minIndex) : "") + (maxIndex < this.text.length() ? this.text.substring(maxIndex, this.text.length()) : "");
            this.cursor = minIndex;
            this.text = this.text.substring(0, this.cursor) + content2 + this.text.substring(this.cursor, this.text.length());
            updateDisplayText();
            this.cursor = content2.length() + minIndex;
            clearSelection();
        }
    }

    /* access modifiers changed from: package-private */
    public void delete() {
        int minIndex = Math.min(this.cursor, this.selectionStart);
        int maxIndex = Math.max(this.cursor, this.selectionStart);
        this.text = (minIndex > 0 ? this.text.substring(0, minIndex) : "") + (maxIndex < this.text.length() ? this.text.substring(maxIndex, this.text.length()) : "");
        updateDisplayText();
        this.cursor = minIndex;
        clearSelection();
    }

    public void next(boolean up) {
        Stage stage = getStage();
        if (stage != null) {
            getParent().localToStageCoordinates(Vector2.tmp.set(getX(), getY()));
            TextField textField = findNextTextField(stage.getActors(), (TextField) null, Vector2.tmp2, Vector2.tmp, up);
            if (textField == null) {
                if (up) {
                    Vector2.tmp.set(Float.MIN_VALUE, Float.MIN_VALUE);
                } else {
                    Vector2.tmp.set(Float.MAX_VALUE, Float.MAX_VALUE);
                }
                textField = findNextTextField(getStage().getActors(), (TextField) null, Vector2.tmp2, Vector2.tmp, up);
            }
            if (textField != null) {
                stage.setKeyboardFocus(textField);
            } else {
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: com.badlogic.gdx.scenes.scene2d.Actor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: com.badlogic.gdx.scenes.scene2d.ui.TextField} */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0062, code lost:
        if (((r7.f166y > r13.f166y || (r7.f166y == r13.f166y && r7.f165x < r13.f165x)) ^ r15) != false) goto L_0x0064;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.badlogic.gdx.scenes.scene2d.p000ui.TextField findNextTextField(com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.Actor> r11, com.badlogic.gdx.scenes.scene2d.p000ui.TextField r12, com.badlogic.gdx.math.Vector2 r13, com.badlogic.gdx.math.Vector2 r14, boolean r15) {
        /*
            r10 = this;
            r8 = 0
            int r9 = r11.size
        L_0x0003:
            if (r8 >= r9) goto L_0x0082
            java.lang.Object r6 = r11.get(r8)
            com.badlogic.gdx.scenes.scene2d.Actor r6 = (com.badlogic.gdx.scenes.scene2d.Actor) r6
            if (r6 != r10) goto L_0x0010
        L_0x000d:
            int r8 = r8 + 1
            goto L_0x0003
        L_0x0010:
            boolean r0 = r6 instanceof com.badlogic.gdx.scenes.scene2d.p000ui.TextField
            if (r0 == 0) goto L_0x006a
            com.badlogic.gdx.scenes.scene2d.Group r0 = r6.getParent()
            com.badlogic.gdx.math.Vector2 r1 = com.badlogic.gdx.math.Vector2.tmp3
            float r2 = r6.getX()
            float r3 = r6.getY()
            com.badlogic.gdx.math.Vector2 r1 = r1.set(r2, r3)
            com.badlogic.gdx.math.Vector2 r7 = r0.localToStageCoordinates(r1)
            float r0 = r7.f166y
            float r1 = r14.f166y
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x0042
            float r0 = r7.f166y
            float r1 = r14.f166y
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x007e
            float r0 = r7.f165x
            float r1 = r14.f165x
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x007e
        L_0x0042:
            r0 = 1
        L_0x0043:
            r0 = r0 ^ r15
            if (r0 == 0) goto L_0x006a
            if (r12 == 0) goto L_0x0064
            float r0 = r7.f166y
            float r1 = r13.f166y
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x0060
            float r0 = r7.f166y
            float r1 = r13.f166y
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0080
            float r0 = r7.f165x
            float r1 = r13.f165x
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0080
        L_0x0060:
            r0 = 1
        L_0x0061:
            r0 = r0 ^ r15
            if (r0 == 0) goto L_0x006a
        L_0x0064:
            r12 = r6
            com.badlogic.gdx.scenes.scene2d.ui.TextField r12 = (com.badlogic.gdx.scenes.scene2d.p000ui.TextField) r12
            r13.set(r7)
        L_0x006a:
            boolean r0 = r6 instanceof com.badlogic.gdx.scenes.scene2d.Group
            if (r0 == 0) goto L_0x000d
            com.badlogic.gdx.scenes.scene2d.Group r6 = (com.badlogic.gdx.scenes.scene2d.Group) r6
            com.badlogic.gdx.utils.SnapshotArray r1 = r6.getChildren()
            r0 = r10
            r2 = r12
            r3 = r13
            r4 = r14
            r5 = r15
            com.badlogic.gdx.scenes.scene2d.ui.TextField r12 = r0.findNextTextField(r1, r2, r3, r4, r5)
            goto L_0x000d
        L_0x007e:
            r0 = 0
            goto L_0x0043
        L_0x0080:
            r0 = 0
            goto L_0x0061
        L_0x0082:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.p000ui.TextField.findNextTextField(com.badlogic.gdx.utils.Array, com.badlogic.gdx.scenes.scene2d.ui.TextField, com.badlogic.gdx.math.Vector2, com.badlogic.gdx.math.Vector2, boolean):com.badlogic.gdx.scenes.scene2d.ui.TextField");
    }

    public void setTextFieldListener(TextFieldListener listener2) {
        this.listener = listener2;
    }

    public void setTextFieldFilter(TextFieldFilter filter2) {
        this.filter = filter2;
    }

    public void setFocusTraversal(boolean focusTraversal2) {
        this.focusTraversal = focusTraversal2;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public void setMessageText(String messageText2) {
        this.messageText = messageText2;
    }

    public void setText(String text2) {
        if (text2 == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        BitmapFont font = this.style.font;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < text2.length() && (this.maxLength <= 0 || buffer.length() + 1 <= this.maxLength); i++) {
            char c = text2.charAt(i);
            if (font.containsCharacter(c) && (this.filter == null || this.filter.acceptChar(this, c))) {
                buffer.append(c);
            }
        }
        this.text = buffer.toString();
        updateDisplayText();
        this.cursor = 0;
        clearSelection();
        this.textBounds.set(font.getBounds(this.displayText));
        this.textBounds.height -= font.getDescent() * 2.0f;
        font.computeGlyphAdvancesAndPositions(this.displayText, this.glyphAdvances, this.glyphPositions);
    }

    public String getText() {
        return this.text;
    }

    public void setSelection(int selectionStart2, int selectionEnd) {
        if (selectionStart2 < 0) {
            throw new IllegalArgumentException("selectionStart must be >= 0");
        } else if (selectionEnd < 0) {
            throw new IllegalArgumentException("selectionEnd must be >= 0");
        } else {
            int selectionStart3 = Math.min(this.text.length(), selectionStart2);
            int selectionEnd2 = Math.min(this.text.length(), selectionEnd);
            if (selectionEnd2 == selectionStart3) {
                clearSelection();
                return;
            }
            if (selectionEnd2 < selectionStart3) {
                int temp = selectionEnd2;
                selectionEnd2 = selectionStart3;
                selectionStart3 = temp;
            }
            this.hasSelection = true;
            this.selectionStart = selectionStart3;
            this.cursor = selectionEnd2;
        }
    }

    public void selectAll() {
        setSelection(0, this.text.length());
    }

    public void clearSelection() {
        this.hasSelection = false;
    }

    public void setCursorPosition(int cursorPosition) {
        if (cursorPosition < 0) {
            throw new IllegalArgumentException("cursorPosition must be >= 0");
        }
        clearSelection();
        this.cursor = Math.min(cursorPosition, this.text.length());
    }

    public int getCursorPosition() {
        return this.cursor;
    }

    public OnscreenKeyboard getOnscreenKeyboard() {
        return this.keyboard;
    }

    public void setOnscreenKeyboard(OnscreenKeyboard keyboard2) {
        this.keyboard = keyboard2;
    }

    public void setClipboard(Clipboard clipboard2) {
        this.clipboard = clipboard2;
    }

    public float getPrefWidth() {
        return 150.0f;
    }

    public float getPrefHeight() {
        float prefHeight = this.textBounds.height;
        if (this.style.background != null) {
            return Math.max(this.style.background.getBottomHeight() + prefHeight + this.style.background.getTopHeight(), this.style.background.getMinHeight());
        }
        return prefHeight;
    }

    public void setRightAligned(boolean rightAligned2) {
        this.rightAligned = rightAligned2;
    }

    public void setPasswordMode(boolean passwordMode2) {
        this.passwordMode = passwordMode2;
    }

    public void setBlinkTime(float blinkTime2) {
        this.blinkTime = blinkTime2;
    }

    public void setDisabled(boolean disabled2) {
        this.disabled = disabled2;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isPasswordMode() {
        return this.passwordMode;
    }

    public TextFieldFilter getTextFieldFilter() {
        return this.filter;
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$KeyRepeatTask */
    class KeyRepeatTask extends Timer.Task {
        int keycode;

        KeyRepeatTask() {
        }

        public void run() {
            TextField.this.inputListener.keyDown((InputEvent) null, this.keycode);
        }
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldFilter */
    public interface TextFieldFilter {
        boolean acceptChar(TextField textField, char c);

        /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldFilter$DigitsOnlyFilter */
        public static class DigitsOnlyFilter implements TextFieldFilter {
            public boolean acceptChar(TextField textField, char key) {
                return Character.isDigit(key);
            }
        }
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$DefaultOnscreenKeyboard */
    public static class DefaultOnscreenKeyboard implements OnscreenKeyboard {
        public void show(boolean visible) {
            Gdx.input.setOnscreenKeyboardVisible(visible);
        }
    }

    /* renamed from: com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldStyle */
    public static class TextFieldStyle {
        public Drawable background;
        public Drawable cursor;
        public Color disabledFontColor;
        public BitmapFont font;
        public Color fontColor;
        public BitmapFont messageFont;
        public Color messageFontColor;
        public Drawable selection;

        public TextFieldStyle() {
        }

        public TextFieldStyle(BitmapFont font2, Color fontColor2, Drawable cursor2, Drawable selection2, Drawable background2) {
            this.background = background2;
            this.cursor = cursor2;
            this.font = font2;
            this.fontColor = fontColor2;
            this.selection = selection2;
        }

        public TextFieldStyle(TextFieldStyle style) {
            this.messageFont = style.messageFont;
            if (style.messageFontColor != null) {
                this.messageFontColor = new Color(style.messageFontColor);
            }
            this.background = style.background;
            this.cursor = style.cursor;
            this.font = style.font;
            if (style.fontColor != null) {
                this.fontColor = new Color(style.fontColor);
            }
            if (style.disabledFontColor != null) {
                this.disabledFontColor = new Color(style.disabledFontColor);
            }
            this.selection = style.selection;
        }
    }
}
