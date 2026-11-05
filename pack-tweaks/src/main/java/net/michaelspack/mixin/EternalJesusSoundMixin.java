package net.michaelspack.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** A client-only sound is absent from a dedicated server's synced registry.
 * Local playback can resolve the existing sound asset directly by its id. */
@Pseudo
@Mixin(targets="com.eternity.jessemood.client.guihandler",remap=false)
public final class EternalJesusSoundMixin {
    @Redirect(method="display",at=@At(value="INVOKE",target="Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;"),require=1)
    private static Object localSound(RegistryObject<?> sound) {
        return sound.isPresent()?sound.get():SoundEvent.createVariableRangeEvent(sound.getId());
    }
}
