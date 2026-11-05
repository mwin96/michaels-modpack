package net.michaelspack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/** Optional Backrooms trips return near their entry, without changing the bed spawn. */
public final class BackroomsReturn {
 private static final String KEY="michaelspack_backrooms_return";
 public static void remember(ServerPlayer player,ResourceKey<Level> destination){
  if(!destination.location().getNamespace().equals("mbackrooms") || player.level().dimension().location().getNamespace().equals("mbackrooms"))return;
  var root=player.getPersistentData();var persisted=root.getCompound(Player.PERSISTED_NBT_TAG);var trip=new CompoundTag();
  trip.putString("dimension",player.level().dimension().location().toString());trip.putDouble("x",player.getX());trip.putDouble("y",player.getY());trip.putDouble("z",player.getZ());trip.putFloat("yaw",player.getYRot());trip.putFloat("pitch",player.getXRot());
  persisted.put(KEY,trip);root.put(Player.PERSISTED_NBT_TAG,persisted);
 }
 public static boolean returnNearEntry(ServerPlayer player){
  if(!player.level().dimension().location().getNamespace().equals("mbackrooms"))return false;
  var trip=player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getCompound(KEY);
  var id=ResourceLocation.tryParse(trip.getString("dimension"));if(id==null)return false;
  var level=player.server.getLevel(ResourceKey.create(Registries.DIMENSION,id));if(level==null)return false;
  BlockPos origin=BlockPos.containing(trip.getDouble("x"),trip.getDouble("y"),trip.getDouble("z"));
  for(int radius=0;radius<=4;radius++)for(int dy:new int[]{0,1,-1,2,-2})for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
   if(Math.max(Math.abs(dx),Math.abs(dz))!=radius)continue;
   BlockPos p=origin.offset(dx,dy,dz);level.getChunkAt(p);
   if(!safe(level,p,player))continue;
   player.teleportTo(level,p.getX()+.5,p.getY(),p.getZ()+.5,trip.getFloat("yaw"),trip.getFloat("pitch"));
   player.setPortalCooldown();return true;
  }
  return false; // Preserve upstream fallback if the original place has become unsafe.
 }
 public static void enterSafely(ServerPlayer player,ServerLevel destination,double x,double y,double z,float yaw,float pitch){
  if(destination.dimension().location().getNamespace().equals("mbackrooms") && !player.level().dimension().location().getNamespace().equals("mbackrooms")){
   BlockPos origin=BlockPos.containing(x,y,z);
   for(int radius=0;radius<=16;radius++)for(int dy:new int[]{0,1,-1,2,-2})for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
    if(Math.max(Math.abs(dx),Math.abs(dz))!=radius)continue;
    BlockPos p=origin.offset(dx,dy,dz);destination.getChunkAt(p);
    if(!safe(destination,p,player))continue;
    player.teleportTo(destination,p.getX()+.5,p.getY(),p.getZ()+.5,yaw,pitch);return;
   }
   player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No clear Backrooms arrival spot nearby. Try placing the portal elsewhere."));return;
  }
  player.teleportTo(destination,x,y,z,yaw,pitch);
 }
 private static boolean safe(ServerLevel level,BlockPos p,ServerPlayer player){
  if(!level.getWorldBorder().isWithinBounds(p))return false;
  for(var b:new BlockPos[]{p,p.above()}){
   var state=level.getBlockState(b);
   if(!state.getCollisionShape(level,b).isEmpty() || !state.getFluidState().isEmpty())return false;
   var id=net.minecraftforge.registries.ForgeRegistries.BLOCKS.getKey(state.getBlock());
   if(id!=null && (id.getPath().contains("portal") || id.getPath().contains("teleporter")))return false;
   if(state.is(Blocks.FIRE)||state.is(Blocks.SOUL_FIRE)||state.is(Blocks.SWEET_BERRY_BUSH))return false;
  }
  var below=level.getBlockState(p.below());
  if(!below.isFaceSturdy(level,p.below(),Direction.UP)||below.is(Blocks.MAGMA_BLOCK)||below.is(Blocks.CAMPFIRE)||below.is(Blocks.SOUL_CAMPFIRE)||below.is(Blocks.CACTUS))return false;
  return level.noCollision(player,new AABB(p.getX()+.2,p.getY(),p.getZ()+.2,p.getX()+.8,p.getY()+1.8,p.getZ()+.8));
 }
}
