package thunder.hack.modules.movement;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.player.PlayerUtility;

public class NoSlow extends Module {
    public NoSlow() {
        super("NoSlow", Category.MOVEMENT);
    }

    private final Setting<Boolean> mainHand = new Setting<>("MainHand", true);
    public final Setting<Boolean> nocancelDBSlot = new Setting<>("nocancelDBSlot", true);
    private boolean returnSneak;

    @Override
    public void onUpdate() {
        if (returnSneak) {
            mc.options.sneakKey.setPressed(false);
            returnSneak = false;
        }

        if (mc.player.isUsingItem() && !mc.player.isRiding() && !mc.player.isFallFlying()) {
            if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
                sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            } else if (mainHand.getValue()) {
                sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, PlayerUtility.getWorldActionId(mc.world)));
            }
        }
    }

    public boolean canNoSlow() {
        return true;
    }
}
