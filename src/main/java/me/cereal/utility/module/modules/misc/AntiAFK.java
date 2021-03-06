package me.cereal.utility.module.modules.misc;

import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

import java.util.Random;

/**
 * Created by 086 on 16/12/2017.
 */
@Module.Info(name = "AntiAFK", category = Module.Category.MISC, description = "Moves in order not to get kicked. (May be invisible client-sided)")
public class AntiAFK extends Module {

    private final Setting<Boolean> swing = register(Settings.b("Swing", true));
    private final Setting<Boolean> turn = register(Settings.b("Turn", true));

    private final Random random = new Random();

    @Override
    public void onUpdate() {
        if (mc.playerController.getIsHittingBlock()) return;

        if (mc.player.ticksExisted % 40 == 0 && swing.getValue())
            mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        if (mc.player.ticksExisted % 15 == 0 && turn.getValue())
            mc.player.rotationYaw = random.nextInt(360) - 180;

        if (!(swing.getValue() || turn.getValue()) && mc.player.ticksExisted % 80 == 0) {
            mc.player.jump();
        }
    }
}
