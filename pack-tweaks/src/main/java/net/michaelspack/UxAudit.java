package net.michaelspack;

import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.LinkedHashMap;

/** Local-only focused regression fixtures, never registered in normal launches. */
@Mod.EventBusSubscriber(modid="michaelspack")
public final class UxAudit {
 @SubscribeEvent public static void register(RegisterCommandsEvent event){
  if(!Boolean.getBoolean("michaelspack.validation"))return;
  event.getDispatcher().register(Commands.literal("packux").requires(s->s.hasPermission(2))
   .then(Commands.literal("mining").executes(c->run(c.getSource(),"mining")))
   .then(Commands.literal("timers").executes(c->run(c.getSource(),"timers")))
   .then(Commands.literal("portal").executes(c->run(c.getSource(),"portal"))));
 }
 static int run(net.minecraft.commands.CommandSourceStack source,String test){try{
  var report=new LinkedHashMap<String,Object>();var level=source.getServer().overworld();
  var player=source.getServer().getPlayerList().getPlayerByName("PackValidation");
  if(player==null)throw new IllegalStateException("Validation player not joined");
  if(test.equals("mining")){
   var zombie=EntityType.ZOMBIE.create(level);zombie.setPos(600.5,70,600.5);
   player.teleportTo(level,602.5,70,600.5,0,0);zombie.setTarget(player);
   var goal=new n643064.zombie_tactics.ZombieMineGoal(zombie);
   var check=goal.getClass().getDeclaredMethod("checkBlock",BlockPos.class);check.setAccessible(true);
   BlockPos p=new BlockPos(601,70,600);
   for(var block:new net.minecraft.world.level.block.Block[]{Blocks.DIRT,Blocks.GLASS,Blocks.OAK_PLANKS,Blocks.STONE,Blocks.COBBLESTONE}){
    level.setBlockAndUpdate(p,block.defaultBlockState());boolean accepted=(boolean)check.invoke(goal,p);
    report.put(ForgeRegistries.BLOCKS.getKey(block).toString()+"Eligible",accepted);
    if(accepted){goal.start();for(int i=0;i<40;i++)goal.tick();report.put(ForgeRegistries.BLOCKS.getKey(block).toString()+"BrokenByGoal",level.getBlockState(p).isAir());goal.stop();}
   }
   level.removeBlock(p,false);report.put("maxHardness",n643064.zombie_tactics.Config.maxHardness);
  }else if(test.equals("timers")){
   level.setDayTime(18000);level.setBlockAndUpdate(new BlockPos(620,69,620),Blocks.BEDROCK.defaultBlockState());player.teleportTo(level,620.5,70,620.5,0,0);
   Class<?> vars=Class.forName("net.mcreator.theknocker.network.TheKnockerModVariables$MapVariables");
   Object data=vars.getMethod("get",LevelAccessor.class).invoke(null,level);
   var last=KnockerTimers.class.getDeclaredField("LAST");last.setAccessible(true);((java.util.Map<?,?>)last.get(null)).clear();
   vars.getField("spawn_rate_timer").setDouble(data,0);vars.getField("spawn_rate_reset").setDouble(data,5000);vars.getField("despawn_timer").setDouble(data,0);
   var spawn=Class.forName("net.mcreator.theknocker.procedures.RandomspawnProcedure").getMethod("execute",LevelAccessor.class,double.class,double.class,double.class,Entity.class);
   var despawn=Class.forName("net.mcreator.theknocker.procedures.PlayertickProcedure").getMethod("execute",LevelAccessor.class,double.class,double.class,double.class);
   for(int i=0;i<4;i++){spawn.invoke(null,level,620.5,70.,620.5,player);despawn.invoke(null,level,620.5,70.,620.5);}
   report.put("callbacksEach",4);report.put("spawnAdvance",vars.getField("spawn_rate_timer").getDouble(data));report.put("despawnAdvance",vars.getField("despawn_timer").getDouble(data));
  }else{
   var portal=ForgeRegistries.ITEMS.getValue(new ResourceLocation("mbackrooms:portal"));report.put("isBlockItem",portal instanceof BlockItem);
   report.put("recipePresent",source.getServer().getRecipeManager().byKey(new ResourceLocation("michaelspack:optional_backrooms_portals")).isPresent());
   int surface=Math.max(70,level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,642,640)+1);
   for(BlockPos p:BlockPos.betweenClosed(638,surface-1,638,645,surface-1,643))level.setBlockAndUpdate(p,Blocks.STONE.defaultBlockState());
   for(BlockPos p:BlockPos.betweenClosed(638,surface,638,645,surface+2,643))level.removeBlock(p,false);
   BlockPos bed=new BlockPos(640,surface,640);level.setBlockAndUpdate(bed,Blocks.RED_BED.defaultBlockState());level.setBlockAndUpdate(bed.south(),Blocks.RED_BED.defaultBlockState().setValue(net.minecraft.world.level.block.BedBlock.PART,net.minecraft.world.level.block.state.properties.BedPart.HEAD));
   player.setRespawnPosition(Level.OVERWORLD,bed,0,true,false);
   BlockPos floor=new BlockPos(642,surface-1,640);level.setBlockAndUpdate(floor,Blocks.STONE.defaultBlockState());level.removeBlock(floor.above(),false);
   player.teleportTo(level,641.5,surface,640.5,-90,0);player.setPortalCooldown(0);player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(portal,2));
   var placed=portal.useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(floor).add(0,.5,0),Direction.UP,floor,false)));
   var state=level.getBlockState(floor.above());report.put("entryPlacement",placed.toString());report.put("entryBlock",ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
   state.entityInside(level,floor.above(),player);BackroomsPortalTravel.drain(source.getServer());report.put("entryDimension",player.level().dimension().location().toString());report.put("entrySpawnPreserved",player.getRespawnDimension().equals(Level.OVERWORLD)&&bed.equals(player.getRespawnPosition()));
   report.put("entryFeetClear",player.level().getBlockState(player.blockPosition()).getCollisionShape(player.level(),player.blockPosition()).isEmpty());
   report.put("entryHeadClear",player.level().getBlockState(player.blockPosition().above()).getCollisionShape(player.level(),player.blockPosition().above()).isEmpty());
   report.put("entrySupported",!player.level().getBlockState(player.blockPosition().below()).getCollisionShape(player.level(),player.blockPosition().below()).isEmpty());
   var back=player.serverLevel();BlockPos exitfloor=player.blockPosition().offset(2,-1,0);back.setBlockAndUpdate(exitfloor,Blocks.STONE.defaultBlockState());back.removeBlock(exitfloor.above(),false);
   player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(portal));
   var exitPlaced=portal.useOn(new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(exitfloor).add(0,.5,0),Direction.UP,exitfloor,false)));
   var exit=back.getBlockState(exitfloor.above());report.put("exitPlacement",exitPlaced.toString());exit.entityInside(back,exitfloor.above(),player);
   report.put("immediateExitBlockedByCooldown",player.level().dimension().location().getNamespace().equals("mbackrooms"));
   player.setPortalCooldown(0);exit.entityInside(back,exitfloor.above(),player);BackroomsPortalTravel.drain(source.getServer());
   report.put("exitDimension",player.level().dimension().location().toString());report.put("exitPosition",player.position().toString());report.put("exitSpawnPreserved",player.getRespawnDimension().equals(Level.OVERWORLD)&&bed.equals(player.getRespawnPosition()));
   report.put("exitFeetClear",player.level().getBlockState(player.blockPosition()).getCollisionShape(player.level(),player.blockPosition()).isEmpty());
   report.put("exitHeadClear",player.level().getBlockState(player.blockPosition().above()).getCollisionShape(player.level(),player.blockPosition().above()).isEmpty());
   report.put("exitSupported",!player.level().getBlockState(player.blockPosition().below()).getCollisionShape(player.level(),player.blockPosition().below()).isEmpty());
   report.put("exitNearOriginalHeight",Math.abs(player.getY()-surface)<=2);report.put("exitCooldownActive",player.isOnPortalCooldown());
   player.setPortalCooldown(0);state.entityInside(level,floor.above(),player);BackroomsPortalTravel.drain(source.getServer());
   report.put("repeatEntryDimension",player.level().dimension().location().toString());
   player.setPortalCooldown(0);exit.entityInside(player.serverLevel(),exitfloor.above(),player);BackroomsPortalTravel.drain(source.getServer());
   report.put("repeatExitDimension",player.level().dimension().location().toString());report.put("repeatExitNearOriginalHeight",Math.abs(player.getY()-surface)<=2);report.put("repeatBedPreserved",player.getRespawnDimension().equals(Level.OVERWORLD)&&bed.equals(player.getRespawnPosition()));
   report.put("cooldownTestMethod","Native collision was blocked while cooldown active; fixture then cleared cooldown and manually drained the end-tick queue. Actual client-walk coverage is recorded separately.");
  }
  ReviewAudit.save(source,"ux-"+test,report);return 1;
 }catch(Exception ex){ex.printStackTrace();source.sendFailure(net.minecraft.network.chat.Component.literal(ex.toString()));return 0;}}
}
