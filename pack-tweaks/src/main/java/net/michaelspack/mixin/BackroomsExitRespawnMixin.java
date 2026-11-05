package net.michaelspack.mixin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
@Pseudo
@Mixin(targets="fr.meulti.mbackrooms.event.OverworldTeleport",remap=false)
public final class BackroomsExitRespawnMixin {
 @Inject(method="execute",at=@At("HEAD"),cancellable=true,require=1)
 private static void returnToEntry(LevelAccessor world,Entity entity,CallbackInfo ci){if(entity instanceof ServerPlayer player && net.michaelspack.BackroomsReturn.returnNearEntry(player))ci.cancel();}
 @Inject(method="execute",at=@At("RETURN"),require=1)
 private static void cooldown(LevelAccessor world,Entity entity,CallbackInfo ci){if(entity instanceof ServerPlayer player)player.setPortalCooldown();}
 @Redirect(method="execute",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ServerPlayer;m_9158_(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V"),require=1)
 private static void preserveBed(ServerPlayer player,ResourceKey<Level> dimension,BlockPos position,float angle,boolean forced,boolean message){net.michaelspack.BackroomsRespawn.setIfEnabled(player,dimension,position,angle,forced,message);}
}
