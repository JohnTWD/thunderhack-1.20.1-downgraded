package thunder.hack.injection;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scoreboard.class)
public abstract class MixinScoreBoard {
    @Final @Unique
    private Object2ObjectMap<String, Team> teamsByScoreHolder;

    @Inject(method = "removePlayerFromTeam", at = @At("HEAD"), cancellable = true)
    public void removeScoreHolderFromTeamHook(String playerName, Team team, CallbackInfo ci) {
        ci.cancel();
        if (teamsByScoreHolder.get(playerName) != team) {
            return;
        }
        teamsByScoreHolder.remove(playerName);
        team.getPlayerList().remove(playerName);
    }
}
