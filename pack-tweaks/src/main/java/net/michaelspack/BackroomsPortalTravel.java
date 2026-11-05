package net.michaelspack;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Travel after movement/collision processing, so its old coordinates cannot overwrite arrival. */
@Mod.EventBusSubscriber(modid="michaelspack")
public final class BackroomsPortalTravel {
 private record Pending(UUID player,ResourceKey<Level> source,Runnable travel){}
 private static final WeakHashMap<MinecraftServer,ArrayDeque<Pending>> QUEUES=new WeakHashMap<>();
 private static final WeakHashMap<MinecraftServer,HashSet<UUID>> PENDING=new WeakHashMap<>();
 private static void enqueue(ServerPlayer player,Runnable travel){
  if(player.isOnPortalCooldown())return;
  var server=player.getServer();if(server==null)return;
  if(!PENDING.computeIfAbsent(server,key->new HashSet<>()).add(player.getUUID()))return;
  player.setPortalCooldown();
  QUEUES.computeIfAbsent(server,key->new ArrayDeque<>()).add(new Pending(player.getUUID(),player.level().dimension(),travel));
 }
 public static void enter(ServerPlayer player,ResourceKey<Level> dimension,double x,double y,double z){
  enqueue(player,()->{try{
   Class.forName("fr.meulti.mbackrooms.event.LevelTeleportingEvents.DimensionTeleporter").getMethod("teleportPlayer",ServerPlayer.class,ResourceKey.class,double.class,double.class,double.class).invoke(null,player,dimension,x,y,z);
  }catch(ReflectiveOperationException ex){throw new IllegalStateException("Backrooms portal entry failed",ex);}});
 }
 public static void exit(LevelAccessor world,Entity entity){
  if(!(entity instanceof ServerPlayer player))return;
  enqueue(player,()->{try{
   Class.forName("fr.meulti.mbackrooms.event.OverworldTeleport").getMethod("execute",LevelAccessor.class,Entity.class).invoke(null,world,player);
  }catch(ReflectiveOperationException ex){throw new IllegalStateException("Backrooms portal exit failed",ex);}});
 }
 @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event){
  if(event.phase!=TickEvent.Phase.END)return;
  drain(event.getServer());
 }
 static void drain(MinecraftServer server){
  var queue=QUEUES.remove(server);if(queue==null)return;
  for(var pending:queue){
   PENDING.get(server).remove(pending.player());
   var player=server.getPlayerList().getPlayer(pending.player());
   if(player!=null && player.isAlive() && player.level().dimension().equals(pending.source()))pending.travel().run();
  }
 }
}
