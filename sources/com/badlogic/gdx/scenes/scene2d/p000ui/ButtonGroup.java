package com.badlogic.gdx.scenes.scene2d.p000ui;

import com.badlogic.gdx.utils.Array;

/* renamed from: com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup */
public class ButtonGroup {
    private final Array<Button> buttons = new Array<>();
    private Array<Button> checkedButtons = new Array<>(1);
    private Button lastChecked;
    private int maxCheckCount = 1;
    private int minCheckCount = 1;
    private boolean uncheckLast = true;

    public ButtonGroup() {
    }

    public ButtonGroup(Button... buttons2) {
        add(buttons2);
        this.minCheckCount = 1;
    }

    public void add(Button button) {
        boolean shouldCheck;
        if (button == null) {
            throw new IllegalArgumentException("button cannot be null.");
        }
        button.buttonGroup = null;
        if (button.isChecked() || this.buttons.size < this.minCheckCount) {
            shouldCheck = true;
        } else {
            shouldCheck = false;
        }
        button.setChecked(false);
        button.buttonGroup = this;
        this.buttons.add(button);
        if (shouldCheck) {
            button.setChecked(true);
        }
    }

    public void add(Button... buttons2) {
        if (buttons2 == null) {
            throw new IllegalArgumentException("buttons cannot be null.");
        }
        for (Button add : buttons2) {
            add(add);
        }
    }

    public void remove(Button button) {
        if (button == null) {
            throw new IllegalArgumentException("button cannot be null.");
        }
        button.buttonGroup = null;
        this.buttons.removeValue(button, true);
    }

    public void remove(Button... buttons2) {
        if (buttons2 == null) {
            throw new IllegalArgumentException("buttons cannot be null.");
        }
        for (Button remove : buttons2) {
            remove(remove);
        }
    }

    public void setChecked(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null.");
        }
        int i = 0;
        int n = this.buttons.size;
        while (i < n) {
            Button button = this.buttons.get(i);
            if (!(button instanceof TextButton) || !text.equals(((TextButton) button).getText())) {
                i++;
            } else {
                button.setChecked(true);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean canCheck(Button button, boolean newState) {
        if (button.isChecked == newState) {
            return false;
        }
        if (newState) {
            if (this.maxCheckCount != -1 && this.checkedButtons.size >= this.maxCheckCount) {
                if (!this.uncheckLast) {
                    return false;
                }
                int old = this.minCheckCount;
                this.minCheckCount = 0;
                this.lastChecked.setChecked(false);
                this.minCheckCount = old;
            }
            this.checkedButtons.add(button);
            this.lastChecked = button;
        } else if (this.checkedButtons.size <= this.minCheckCount) {
            return false;
        } else {
            this.checkedButtons.removeValue(button, true);
        }
        return true;
    }

    public void uncheckAll() {
        int old = this.minCheckCount;
        this.minCheckCount = 0;
        int n = this.buttons.size;
        for (int i = 0; i < n; i++) {
            this.buttons.get(i).setChecked(false);
        }
        this.minCheckCount = old;
    }

    public Button getChecked() {
        if (this.checkedButtons.size > 0) {
            return this.checkedButtons.get(0);
        }
        return null;
    }

    public Array<Button> getAllChecked() {
        return this.checkedButtons;
    }

    public Array<Button> getButtons() {
        return this.buttons;
    }

    public void setMinCheckCount(int minCheckCount2) {
        this.minCheckCount = minCheckCount2;
    }

    public void setMaxCheckCount(int maxCheckCount2) {
        this.maxCheckCount = maxCheckCount2;
    }

    public void setUncheckLast(boolean uncheckLast2) {
        this.uncheckLast = uncheckLast2;
    }
}
