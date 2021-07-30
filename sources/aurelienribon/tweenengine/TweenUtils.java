package aurelienribon.tweenengine;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

public class TweenUtils {
    private static TweenEquation[] easings;

    public static TweenEquation parseEasing(String easingName) {
        if (easings == null) {
            easings = new TweenEquation[]{Linear.INOUT, Quad.f7IN, Quad.OUT, Quad.INOUT, Cubic.f3IN, Cubic.OUT, Cubic.INOUT, Quart.f8IN, Quart.OUT, Quart.INOUT, Quint.f9IN, Quint.OUT, Quint.INOUT, Circ.f2IN, Circ.OUT, Circ.INOUT, Sine.f10IN, Sine.OUT, Sine.INOUT, Expo.f6IN, Expo.OUT, Expo.INOUT, Back.f0IN, Back.OUT, Back.INOUT, Bounce.f1IN, Bounce.OUT, Bounce.INOUT, Elastic.f4IN, Elastic.OUT, Elastic.INOUT};
        }
        for (int i = 0; i < easings.length; i++) {
            if (easingName.equals(easings[i].toString())) {
                return easings[i];
            }
        }
        return null;
    }
}
