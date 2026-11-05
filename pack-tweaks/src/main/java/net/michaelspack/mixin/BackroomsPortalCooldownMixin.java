package net.michaelspack.mixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo
@Mixin(targets="fr.meulti.mbackrooms.block.custom.PortalBlock",remap=false)
public final class BackroomsPortalCooldownMixin {
 @Redirect(method="m_7892_",at=@At(value="INVOKE",target="Lfr/meulti/mbackrooms/event/LevelTeleportingEvents/DimensionTeleporter;teleportPlayer(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;DDD)V"),require=1)
 private void deferEntry(net.minecraft.server.level.ServerPlayer player,net.minecraft.resources.ResourceKey<Level> dimension,double x,double y,double z){net.michaelspack.BackroomsPortalTravel.enter(player,dimension,x,y,z);}
 @Redirect(method="m_7892_",at=@At(value="INVOKE",target="Lfr/meulti/mbackrooms/event/OverworldTeleport;execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V"),require=1)
 private void deferExit(net.minecraft.world.level.LevelAccessor world,Entity entity){net.michaelspack.BackroomsPortalTravel.exit(world,entity);}
 @Inject(method="m_7892_",at=@At("HEAD"),cancellable=true,require=1)
 private void avoidImmediateReentry(BlockState state,Level level,BlockPos pos,Entity entity,CallbackInfo ci){if(entity.isOnPortalCooldown())ci.cancel();}
}
