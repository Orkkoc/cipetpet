package com.badlogic.gdx.scenes.scene2d.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class Actions {
    public static <T extends Action> T action(Class<T> type) {
        Pool<T> pool = Pools.get(type);
        T action = (Action) pool.obtain();
        action.setPool(pool);
        return action;
    }

    public static AddAction add(Actor targetActor, Action action) {
        AddAction addAction = (AddAction) action(AddAction.class);
        addAction.setTargetActor(targetActor);
        addAction.setAction(action);
        return addAction;
    }

    public static MoveToAction moveTo(float x, float y) {
        return moveTo(x, y, 0.0f, (Interpolation) null);
    }

    public static MoveToAction moveTo(float x, float y, float duration) {
        return moveTo(x, y, duration, (Interpolation) null);
    }

    public static MoveToAction moveTo(float x, float y, float duration, Interpolation interpolation) {
        MoveToAction action = (MoveToAction) action(MoveToAction.class);
        action.setPosition(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static MoveByAction moveBy(float amountX, float amountY) {
        return moveBy(amountX, amountY, 0.0f, (Interpolation) null);
    }

    public static MoveByAction moveBy(float amountX, float amountY, float duration) {
        return moveBy(amountX, amountY, duration, (Interpolation) null);
    }

    public static MoveByAction moveBy(float amountX, float amountY, float duration, Interpolation interpolation) {
        MoveByAction action = (MoveByAction) action(MoveByAction.class);
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static SizeToAction sizeTo(float x, float y) {
        return sizeTo(x, y, 0.0f, (Interpolation) null);
    }

    public static SizeToAction sizeTo(float x, float y, float duration) {
        return sizeTo(x, y, duration, (Interpolation) null);
    }

    public static SizeToAction sizeTo(float x, float y, float duration, Interpolation interpolation) {
        SizeToAction action = (SizeToAction) action(SizeToAction.class);
        action.setSize(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static SizeByAction sizeBy(float amountX, float amountY) {
        return sizeBy(amountX, amountY, 0.0f, (Interpolation) null);
    }

    public static SizeByAction sizeBy(float amountX, float amountY, float duration) {
        return sizeBy(amountX, amountY, duration, (Interpolation) null);
    }

    public static SizeByAction sizeBy(float amountX, float amountY, float duration, Interpolation interpolation) {
        SizeByAction action = (SizeByAction) action(SizeByAction.class);
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static ScaleToAction scaleTo(float x, float y) {
        return scaleTo(x, y, 0.0f, (Interpolation) null);
    }

    public static ScaleToAction scaleTo(float x, float y, float duration) {
        return scaleTo(x, y, duration, (Interpolation) null);
    }

    public static ScaleToAction scaleTo(float x, float y, float duration, Interpolation interpolation) {
        ScaleToAction action = (ScaleToAction) action(ScaleToAction.class);
        action.setScale(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static ScaleByAction scaleBy(float amountX, float amountY) {
        return scaleBy(amountX, amountY, 0.0f, (Interpolation) null);
    }

    public static ScaleByAction scaleBy(float amountX, float amountY, float duration) {
        return scaleBy(amountX, amountY, duration, (Interpolation) null);
    }

    public static ScaleByAction scaleBy(float amountX, float amountY, float duration, Interpolation interpolation) {
        ScaleByAction action = (ScaleByAction) action(ScaleByAction.class);
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static RotateToAction rotateTo(float rotation) {
        return rotateTo(rotation, 0.0f, (Interpolation) null);
    }

    public static RotateToAction rotateTo(float rotation, float duration) {
        return rotateTo(rotation, duration, (Interpolation) null);
    }

    public static RotateToAction rotateTo(float rotation, float duration, Interpolation interpolation) {
        RotateToAction action = (RotateToAction) action(RotateToAction.class);
        action.setRotation(rotation);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static RotateByAction rotateBy(float rotationAmount) {
        return rotateBy(rotationAmount, 0.0f, (Interpolation) null);
    }

    public static RotateByAction rotateBy(float rotationAmount, float duration) {
        return rotateBy(rotationAmount, duration, (Interpolation) null);
    }

    public static RotateByAction rotateBy(float rotationAmount, float duration, Interpolation interpolation) {
        RotateByAction action = (RotateByAction) action(RotateByAction.class);
        action.setAmount(rotationAmount);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static ColorAction color(Color color) {
        return color(color, 0.0f, (Interpolation) null);
    }

    public static ColorAction color(Color color, float duration) {
        return color(color, duration, (Interpolation) null);
    }

    public static ColorAction color(Color color, float duration, Interpolation interpolation) {
        ColorAction action = (ColorAction) action(ColorAction.class);
        action.setEndColor(color);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static AlphaAction alpha(float a) {
        return alpha(a, 0.0f, (Interpolation) null);
    }

    public static AlphaAction alpha(float a, float duration) {
        return alpha(a, duration, (Interpolation) null);
    }

    public static AlphaAction alpha(float a, float duration, Interpolation interpolation) {
        AlphaAction action = (AlphaAction) action(AlphaAction.class);
        action.setAlpha(a);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static AlphaAction fadeOut(float duration) {
        return alpha(0.0f, duration, (Interpolation) null);
    }

    public static AlphaAction fadeOut(float duration, Interpolation interpolation) {
        AlphaAction action = (AlphaAction) action(AlphaAction.class);
        action.setAlpha(0.0f);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static AlphaAction fadeIn(float duration) {
        return alpha(1.0f, duration, (Interpolation) null);
    }

    public static AlphaAction fadeIn(float duration, Interpolation interpolation) {
        AlphaAction action = (AlphaAction) action(AlphaAction.class);
        action.setAlpha(1.0f);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static VisibleAction show() {
        return visible(true);
    }

    public static VisibleAction hide() {
        return visible(false);
    }

    public static VisibleAction visible(boolean visible) {
        VisibleAction action = (VisibleAction) action(VisibleAction.class);
        action.setVisible(visible);
        return action;
    }

    public static TouchableAction touchable(Touchable touchable) {
        TouchableAction action = (TouchableAction) action(TouchableAction.class);
        action.setTouchable(touchable);
        return action;
    }

    public static RemoveActorAction removeActor() {
        return (RemoveActorAction) action(RemoveActorAction.class);
    }

    public static RemoveActorAction removeActor(Actor removeActor) {
        RemoveActorAction action = (RemoveActorAction) action(RemoveActorAction.class);
        action.setRemoveActor(removeActor);
        return action;
    }

    public static DelayAction delay(float duration) {
        DelayAction action = (DelayAction) action(DelayAction.class);
        action.setDuration(duration);
        return action;
    }

    public static DelayAction delay(float duration, Action delayedAction) {
        DelayAction action = (DelayAction) action(DelayAction.class);
        action.setDuration(duration);
        action.setAction(delayedAction);
        return action;
    }

    public static SequenceAction sequence(Action action1) {
        SequenceAction action = (SequenceAction) action(SequenceAction.class);
        action.addAction(action1);
        return action;
    }

    public static SequenceAction sequence(Action action1, Action action2) {
        SequenceAction action = (SequenceAction) action(SequenceAction.class);
        action.addAction(action1);
        action.addAction(action2);
        return action;
    }

    public static SequenceAction sequence(Action action1, Action action2, Action action3) {
        SequenceAction action = (SequenceAction) action(SequenceAction.class);
        action.addAction(action1);
        action.addAction(action2);
        action.addAction(action3);
        return action;
    }

    public static SequenceAction sequence(Action action1, Action action2, Action action3, Action action4) {
        SequenceAction action = (SequenceAction) action(SequenceAction.class);
        action.addAction(action1);
        action.addAction(action2);
        action.addAction(action3);
        action.addAction(action4);
        return action;
    }

    public static SequenceAction sequence(Action action1, Action action2, Action action3, Action action4, Action action5) {
        SequenceAction action = (SequenceAction) action(SequenceAction.class);
        action.addAction(action1);
        action.addAction(action2);
        action.addAction(action3);
        action.addAction(action4);
        action.addAction(action5);
        return action;
    }

    public static SequenceAction sequence(Action... actions) {
        SequenceAction action = (SequenceAction) action(SequenceAction.class);
        for (Action addAction : actions) {
            action.addAction(addAction);
        }
        return action;
    }

    public static SequenceAction sequence() {
        return (SequenceAction) action(SequenceAction.class);
    }

    public static ParallelAction parallel(Action action1) {
        ParallelAction action = (ParallelAction) action(ParallelAction.class);
        action.addAction(action1);
        return action;
    }

    public static ParallelAction parallel(Action action1, Action action2) {
        ParallelAction action = (ParallelAction) action(ParallelAction.class);
        action.addAction(action1);
        action.addAction(action2);
        return action;
    }

    public static ParallelAction parallel(Action action1, Action action2, Action action3) {
        ParallelAction action = (ParallelAction) action(ParallelAction.class);
        action.addAction(action1);
        action.addAction(action2);
        action.addAction(action3);
        return action;
    }

    public static ParallelAction parallel(Action action1, Action action2, Action action3, Action action4) {
        ParallelAction action = (ParallelAction) action(ParallelAction.class);
        action.addAction(action1);
        action.addAction(action2);
        action.addAction(action3);
        action.addAction(action4);
        return action;
    }

    public static ParallelAction parallel(Action action1, Action action2, Action action3, Action action4, Action action5) {
        ParallelAction action = (ParallelAction) action(ParallelAction.class);
        action.addAction(action1);
        action.addAction(action2);
        action.addAction(action3);
        action.addAction(action4);
        action.addAction(action5);
        return action;
    }

    public static ParallelAction parallel(Action... actions) {
        ParallelAction action = (ParallelAction) action(ParallelAction.class);
        for (Action addAction : actions) {
            action.addAction(addAction);
        }
        return action;
    }

    public static ParallelAction parallel() {
        return (ParallelAction) action(ParallelAction.class);
    }

    public static RepeatAction repeat(int count, Action repeatedAction) {
        RepeatAction action = (RepeatAction) action(RepeatAction.class);
        action.setCount(count);
        action.setAction(repeatedAction);
        return action;
    }

    public static RepeatAction forever(Action repeatedAction) {
        RepeatAction action = (RepeatAction) action(RepeatAction.class);
        action.setCount(-1);
        action.setAction(repeatedAction);
        return action;
    }

    public static RunnableAction run(Runnable runnable) {
        RunnableAction action = (RunnableAction) action(RunnableAction.class);
        action.setRunnable(runnable);
        return action;
    }

    public static LayoutAction layout(boolean enabled) {
        LayoutAction action = (LayoutAction) action(LayoutAction.class);
        action.setLayoutEnabled(enabled);
        return action;
    }

    public static AfterAction after(Action action) {
        AfterAction afterAction = (AfterAction) action(AfterAction.class);
        afterAction.setAction(action);
        return afterAction;
    }
}
