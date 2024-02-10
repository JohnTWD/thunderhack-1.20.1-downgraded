package thunder.hack.modules.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.Parent;

public class Velocity extends Module {

    /*
    TY <3
    https://github.com/SkidderMC/FDPClient/blob/main/src/main/java/net/ccbluex/liquidbounce/features/module/modules/combat/velocitys/vanilla/JumpVelocity.kt
     */

    public Setting<Boolean> onlyAura = new Setting<>("OnlyAura", false);
    public Setting<Boolean> pauseInWater = new Setting<>("PauseInFluids", false);
    public Setting<Boolean> cc = new Setting<>("CC", false);
    public Setting<Boolean> fishingHook = new Setting<>("FishingHook", true);
    public static Setting<Parent> antiPush = new Setting<>("AntiPush", new Parent(false, 0));
    public Setting<Boolean> blocks = new Setting<>("Blocks", true).withParent(antiPush);
    public Setting<Boolean> players = new Setting<>("Players", true).withParent(antiPush);
    public Setting<Boolean> water = new Setting<>("Water", true).withParent(antiPush);




    public Velocity() {
        super("Velocity", Module.Category.MOVEMENT);
    }

    private boolean flag;
    private int ccCooldown;

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) return;

        if(mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInWater.getValue())
            return;

        if (ccCooldown > 0) {
            ccCooldown--;
            return;
        }

        if (e.getPacket() instanceof EntityStatusS2CPacket pac
                && pac.getStatus() == 31
                && pac.getEntity(mc.world) instanceof FishingBobberEntity
                && fishingHook.getValue()) {
            FishingBobberEntity fishHook = (FishingBobberEntity) pac.getEntity(mc.world);
            if (fishHook.getHookedEntity() == mc.player) {
                e.cancel();
            }
        }

        // MAIN VELOCITY
        if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() == mc.player.getId() && (!onlyAura.getValue() || ModuleManager.aura.isEnabled())) {
                e.cancel();
                flag = true;
            }
        }

        // EXPLOSION
        if (e.getPacket() instanceof ExplosionS2CPacket) {
            e.cancel();
            flag = true;
        }

        // LAGBACK
        if (e.getPacket() instanceof PlayerPositionLookS2CPacket) {
            if(cc.getValue())
                ccCooldown = 5;
        }
    }


    @Override
    public void onUpdate() {
        if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) && pauseInWater.getValue())
            return;

        if (flag) {
            if (ccCooldown <= 0) {
                sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, BlockPos.ofFloored(mc.player.getPos()), Direction.DOWN));
            }
            flag = false;
        }
    }

}
