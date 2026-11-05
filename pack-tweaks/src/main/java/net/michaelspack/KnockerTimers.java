package net.michaelspack;
import java.util.WeakHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

/** Saved counters are global; advance them once per server tick, not per player. */
public final class KnockerTimers {
 private static final WeakHashMap<MinecraftServer,long[]> LAST=new WeakHashMap<>();
 public static void write(Object owner,double value,String name,LevelAccessor world){
  try {
   var field=owner.getClass().getField(name);double before=field.getDouble(owner);
   if(value==before+1 && world instanceof ServerLevel level){
    long[] ticks=LAST.computeIfAbsent(level.getServer(),key->new long[]{Long.MIN_VALUE,Long.MIN_VALUE});
    int index=name.equals("spawn_rate_timer")?0:1;long now=level.getServer().overworld().getGameTime();
    if(ticks[index]==now)return;
    ticks[index]=now;
   }
   field.setDouble(owner,value);
  }catch(ReflectiveOperationException ex){throw new IllegalStateException("Knocker timer field unavailable",ex);}
 }
}
