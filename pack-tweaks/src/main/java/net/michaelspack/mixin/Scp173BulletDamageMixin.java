package net.michaelspack.mixin;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SCP Additions subtracts six health from every damage event, which erases
 * TaCZ's split bullet damage. Skip only that threshold for actual TaCZ rounds;
 * leave health, gaze, movement, and all other damage/encounter rules intact. */
@Pseudo
@Mixin(targets="net.mcreator.scpadditions.event.Scp173DurabilityEvents",remap=false)
public final class Scp173BulletDamageMixin {
    @Inject(method="onLivingHurt",at=@At("HEAD"),cancellable=true,require=1)
    private static void allowBulletComponents(LivingHurtEvent event,CallbackInfo ci) {
        var direct=event.getSource().getDirectEntity();
        if(direct!=null && "tacz:bullet".equals(String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(direct.getType())))
            && "scp_additions:scp_173".equals(String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()))))ci.cancel();
    }
}
