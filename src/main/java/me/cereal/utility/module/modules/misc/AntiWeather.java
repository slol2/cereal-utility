package me.cereal.utility.module.modules.misc;

import me.cereal.utility.module.Module;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "AntiWeather", description = "Removes rain from your world", category = Module.Category.MISC)
public class AntiWeather extends Module {

    @Override
    public void onUpdate() {
        if (isDisabled()) return;
        if (mc.world.isRaining())
            mc.world.setRainStrength(0);
    }
}
