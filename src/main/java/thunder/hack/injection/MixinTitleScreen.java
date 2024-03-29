package thunder.hack.injection;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.ThunderHack;
import thunder.hack.gui.mainmenu.MainMenuScreen;
import thunder.hack.modules.client.MainSettings;
import thunder.hack.utility.SoundUtility;

import java.net.URI;

import static thunder.hack.modules.Module.mc;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void postInitHook(CallbackInfo ci) {
        if (MainSettings.customMainMenu.getValue() && !MainMenuScreen.getInstance().confirm) {
            mc.setScreen(MainMenuScreen.getInstance());
            mc.getSoundManager().reloadSounds();
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundUtility.MAINMENU_SOUNDEVENT, 1.0f));
        }
    }
}
