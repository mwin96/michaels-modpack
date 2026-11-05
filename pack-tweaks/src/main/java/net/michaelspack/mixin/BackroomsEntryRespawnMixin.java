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
@Pseudo
@Mixin(targets="fr.meulti.mbackrooms.event.LevelTeleportingEvents.DimensionTeleporter",remap=false)
public final class BackroomsEntryRespawnMixin {
 @Redirect(method="teleportPlayer",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ServerPlayer;m_8999_(Lnet/minecraft/server/level/ServerLevel;DDDFF)V"),require=1)
 private static void clearArrival(ServerPlayer player,net.minecraft.server.level.ServerLevel destination,double x,double y,double z,float yaw,float pitch){net.michaelspack.BackroomsReturn.enterSafely(player,destination,x,y,z,yaw,pitch);}
 @Inject(method="teleportPlayer",at=@At("HEAD"),require=1)
 private static void rememberEntry(ServerPlayer player,ResourceKey<Level> dimension,double x,double y,double z,CallbackInfo ci){if(player!=null)net.michaelspack.BackroomsReturn.remember(player,dimension);}
 @Inject(method="teleportPlayer",at=@At("RETURN"),require=1)
 private static void cooldown(ServerPlayer player,ResourceKey<Level> dimension,double x,double y,double z,CallbackInfo ci){if(player!=null && player.level().dimension().equals(dimension))player.setPortalCooldown();}
 @Redirect(method="teleportPlayer",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ServerPlayer;m_9158_(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V"),require=1)
 private static void preserveBed(ServerPlayer player,ResourceKey<Level> dimension,BlockPos position,float angle,boolean forced,boolean message){net.michaelspack.BackroomsRespawn.setIfEnabled(player,dimension,position,angle,forced,message);}
}
