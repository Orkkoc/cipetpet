package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.p000ui.Label;
import com.badlogic.gdx.scenes.scene2d.p000ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.p000ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.ObjectMap;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.Dialog */
public class Dialog extends Window {
    public static float fadeDuration = 0.4f;
    Table buttonTable;
    boolean cancelHide;
    Table contentTable;
    Actor previousKeyboardFocus;
    Actor previousScrollFocus;
    private Skin skin;
    ObjectMap<Actor, Object> values = new ObjectMap<>();

    public Dialog(String title, Skin skin2) {
        super(title, (Window.WindowStyle) skin2.get(Window.WindowStyle.class));
        this.skin = skin2;
        initialize();
    }

    public Dialog(String title, Skin skin2, String windowStyleName) {
        super(title, (Window.WindowStyle) skin2.get(windowStyleName, Window.WindowStyle.class));
        this.skin = skin2;
        initialize();
    }

    public Dialog(String title, Window.WindowStyle windowStyle) {
        super(title, windowStyle);
        initialize();
    }

    private void initialize() {
        setModal(true);
        defaults().space(6.0f);
        Table table = new Table(this.skin);
        this.contentTable = table;
        add((Actor) table).expand().fill();
        row();
        Table table2 = new Table(this.skin);
        this.buttonTable = table2;
        add((Actor) table2);
        this.contentTable.defaults().space(6.0f);
        this.buttonTable.defaults().space(6.0f);
        this.buttonTable.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                while (actor.getParent() != Dialog.this.buttonTable) {
                    actor = actor.getParent();
                }
                Dialog.this.result(Dialog.this.values.get(actor));
                if (!Dialog.this.cancelHide) {
                    Dialog.this.hide();
                }
                Dialog.this.cancelHide = false;
            }
        });
        addListener(new FocusListener() {
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    focusChanged(event);
                }
            }

            public void scrollFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    focusChanged(event);
                }
            }

            private void focusChanged(FocusListener.FocusEvent event) {
                Stage stage = Dialog.this.getStage();
                if (Dialog.this.isModal && stage != null && stage.getRoot().getChildren().peek() == Dialog.this) {
                    Actor newFocusedActor = event.getRelatedActor();
                    if (newFocusedActor == null || !newFocusedActor.isDescendantOf(Dialog.this)) {
                        event.cancel();
                    }
                }
            }
        });
    }

    public Table getContentTable() {
        return this.contentTable;
    }

    public Table getButtonTable() {
        return this.buttonTable;
    }

    public Dialog text(String text) {
        if (this.skin != null) {
            return text(text, (Label.LabelStyle) this.skin.get(Label.LabelStyle.class));
        }
        throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
    }

    public Dialog text(String text, Label.LabelStyle labelStyle) {
        return text(new Label((CharSequence) text, labelStyle));
    }

    public Dialog text(Label label) {
        this.contentTable.add((Actor) label);
        return this;
    }

    public Dialog button(String text) {
        return button(text, (Object) null);
    }

    public Dialog button(String text, Object object) {
        if (this.skin != null) {
            return button(text, object, (TextButton.TextButtonStyle) this.skin.get(TextButton.TextButtonStyle.class));
        }
        throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
    }

    public Dialog button(String text, Object object, TextButton.TextButtonStyle buttonStyle) {
        return button((Button) new TextButton(text, buttonStyle), object);
    }

    public Dialog button(Button button) {
        return button(button, (Object) null);
    }

    public Dialog button(Button button, Object object) {
        this.buttonTable.add((Actor) button);
        setObject(button, object);
        return this;
    }

    public Dialog show(Stage stage) {
        clearActions();
        this.previousKeyboardFocus = stage.getKeyboardFocus();
        this.previousScrollFocus = stage.getScrollFocus();
        pack();
        setPosition((float) Math.round((stage.getWidth() - getWidth()) / 2.0f), (float) Math.round((stage.getHeight() - getHeight()) / 2.0f));
        stage.addActor(this);
        stage.setKeyboardFocus(this);
        stage.setScrollFocus(this);
        if (fadeDuration > 0.0f) {
            getColor().f67a = 0.0f;
            addAction(Actions.fadeIn(fadeDuration, Interpolation.fade));
        }
        return this;
    }

    public void hide() {
        addAction(Actions.sequence(Actions.fadeOut(fadeDuration, Interpolation.fade), Actions.removeActor()));
    }

    /* access modifiers changed from: protected */
    public void setParent(Group parent) {
        Stage stage;
        super.setParent(parent);
        if (parent == null && (stage = getStage()) != null) {
            Actor actor = stage.getKeyboardFocus();
            if (actor == this || actor == null) {
                stage.setKeyboardFocus(this.previousKeyboardFocus);
            }
            Actor actor2 = stage.getScrollFocus();
            if (actor2 == this || actor2 == null) {
                stage.setScrollFocus(this.previousScrollFocus);
            }
        }
    }

    public void setObject(Actor actor, Object object) {
        this.values.put(actor, object);
    }

    public Dialog key(final int keycode, final Object object) {
        addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode2) {
                if (keycode == keycode2) {
                    Dialog.this.result(object);
                    if (!Dialog.this.cancelHide) {
                        Dialog.this.hide();
                    }
                    Dialog.this.cancelHide = false;
                }
                return false;
            }
        });
        return this;
    }

    /* access modifiers changed from: protected */
    public void result(Object object) {
    }

    public void cancel() {
        this.cancelHide = true;
    }
}
