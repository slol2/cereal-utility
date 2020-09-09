package me.cereal.utility.module.modules.render;

import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;

import java.util.Stack;
import java.util.function.Function;

/**
 * Created by 086 on 12/12/2017.
 *
 * @see me.cereal.utility.mixin.client.MixinEntityRenderer
 */
@Module.Info(name = "Brightness", description = "Makes everything brighter!", category = Module.Category.RENDER)
public class Brightness extends Module {

    private static float currentBrightness = 0;
    private static boolean inTransition = false;
    private final Setting<Boolean> transition = register(Settings.b("Transition", true));
    private final Setting<Float> seconds = register(Settings.floatBuilder("Seconds").withMinimum(0f).withMaximum(10f).withValue(1f).withVisibility(o -> transition.getValue()).build());
    private final Setting<Transition> mode = register(Settings.enumBuilder(Transition.class).withName("Mode").withValue(Transition.SINE).withVisibility(o -> transition.getValue()).build());
    private final Stack<Float> transitionStack = new Stack<>();

    public static float getCurrentBrightness() {
        return currentBrightness;
    }

    public static boolean isInTransition() {
        return inTransition;
    }

    public static boolean shouldBeActive() {
        return isInTransition() || currentBrightness == 1; // if in transition or enabled
    }

    private void addTransition(boolean isUpwards) {
        if (transition.getValue()) {
            int length = (int) (seconds.getValue() * 20);
            float[] values;
            switch (mode.getValue()) {
                case LINEAR:
                    values = linear(length, isUpwards);
                    break;
                case SINE:
                    values = sine(length, isUpwards);
                    break;
                default:
                    values = new float[]{0};
                    break;
            }
            for (float v : values) {
                transitionStack.add(v);
            }

            inTransition = true;
        }
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        addTransition(true);
    }

    @Override
    protected void onDisable() {
        setAlwaysListening(true);
        super.onDisable();
        addTransition(false);
    }

    @Override
    public void onUpdate() {
        if (inTransition) {
            if (transitionStack.isEmpty()) {
                inTransition = false;
                setAlwaysListening(false);
                currentBrightness = isEnabled() ? 1 : 0;
            } else {
                currentBrightness = transitionStack.pop();
            }
        }
    }

    private float[] createTransition(int length, boolean upwards, Function<Float, Float> function) {
        float[] transition = new float[length];
        for (int i = 0; i < length; i++) {
            float v = function.apply(((float) i / (float) length));
            if (upwards) v = 1 - v;
            transition[i] = v;
        }
        return transition;
    }

    private float[] linear(int length, boolean polarity) { // length of 20 = 1 second
        return createTransition(length, polarity, d -> d);
    }

    private float sine(float x) { // (sin(pi*x-(pi/2)) + 1) / 2
        return ((float) Math.sin(Math.PI * x - Math.PI / 2) + 1) / 2;
    }

    private float[] sine(int length, boolean polarity) {
        return createTransition(length, polarity, this::sine);
    }

    public enum Transition {
        LINEAR, SINE

    }
}
