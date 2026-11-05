package net.michaelspack;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Destructive tests: accessible only with an explicit local validation JVM flag and operator permission. */
@Mod.EventBusSubscriber(modid="michaelspack")
public final class ReviewAudit {
    @SubscribeEvent public static void commands(RegisterCommandsEvent event) {
        if (!Boolean.getBoolean("michaelspack.validation")) return;
        event.getDispatcher().register(Commands.literal("packreview").requires(s->s.hasPermission(2))
            .then(Commands.argument("arguments",StringArgumentType.greedyString()).executes(c->{
                try {
                    String[] a=StringArgumentType.getString(c,"arguments").split(" ");
                    var s=c.getSource();
                    switch(a[0]) {
                        case "portal" -> portal(s,Integer.parseInt(a[1]),Integer.parseInt(a[2]));
                        case "flight" -> flight(s,a[1]);
                        case "dragonbullet" -> flight(s,"minecraft:ender_dragon",true);
                        case "dragon" -> dragon(s);
                        case "endstatus" -> endstatus(s);
                        case "respawn" -> {s.getServer().getLevel(Level.END).getDragonFight().tryRespawn();s.sendSuccess(()->Component.literal("Requested normal crystal-based respawn"),false);}
                        case "balance" -> balance(s);
                        case "lootr" -> lootr(s);
                        default -> throw new IllegalArgumentException("portal x z | flight entity | dragon | lootr");
                    }
                    return 1;
                } catch(Exception ex) {ex.printStackTrace();c.getSource().sendFailure(Component.literal(ex.toString()));return 0;}
            })));
    }
    static void save(CommandSourceStack s,String file,Map<String,Object> report) throws Exception {
        report.put("testedAt",java.time.Instant.now().toString());
        Files.createDirectories(Path.of("independent-tests"));
        Files.writeString(Path.of("independent-tests",file+".json"),new GsonBuilder().setPrettyPrinting().create().toJson(report));
        s.sendSuccess(()->Component.literal(report.toString()),false);
    }
    static void portal(CommandSourceStack s,int x,int z) throws Exception {
        var level=s.getServer().overworld();
        var structure=level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(new ResourceLocation("minecraft:stronghold"));
        var start=level.structureManager().getStartForStructure(SectionPos.of(x>>4,0,z>>4),structure,level.getChunk(x>>4,z>>4));
        if(start==null || !start.isValid()) throw new IllegalStateException("No stronghold start at supplied locate coordinates");
        var report=new LinkedHashMap<String,Object>();
        var frames=new ArrayList<BlockPos>();var portals=new ArrayList<String>();
        var owner=FakePlayerFactory.getMinecraft(level);
        for(var piece:start.getPieces()) {
            if(!piece.getClass().getSimpleName().contains("PortalRoom"))continue;
            var box=piece.getBoundingBox();report.put("naturalPortalRoom",box.toString());
            for(var p:BlockPos.betweenClosed(box.minX(),box.minY(),box.minZ(),box.maxX(),box.maxY(),box.maxZ()))
                if(level.getBlockState(p).is(Blocks.END_PORTAL_FRAME)) frames.add(p.immutable());
            for(var p:frames) {
                if(level.getBlockState(p).getValue(EndPortalFrameBlock.HAS_EYE))continue;
                owner.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.ENDER_EYE));
                Items.ENDER_EYE.useOn(new UseOnContext(owner,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(p),Direction.UP,p,false)));
            }
            for(var p:BlockPos.betweenClosed(box.minX(),box.minY(),box.minZ(),box.maxX(),box.maxY(),box.maxZ()))
                if(level.getBlockState(p).is(Blocks.END_PORTAL))portals.add(p.toShortString());
        }
        report.put("naturalFrameCount",frames.size());report.put("activatedPortalBlocks",portals);
        report.put("method","Natural stronghold generation; filled existing frames using vanilla EnderEyeItem.useOn, with no portal block commands.");
        save(s,"natural-portal",report);
    }
    static void dragon(CommandSourceStack s) throws Exception {
        var level=s.getServer().getLevel(Level.END);var player=s.getServer().getPlayerList().getPlayerByName("PackValidation");
        var report=new LinkedHashMap<String,Object>();
        var dragons=level.getEntities(net.minecraft.world.entity.EntityType.ENDER_DRAGON,e->true);
        if(dragons.isEmpty())throw new IllegalStateException("No naturally spawned dragon");
        var dragon=dragons.get(0);float initial=dragon.getHealth();int hits=0;
        while(dragon.getHealth()>0 && hits<100) {dragon.invulnerableTime=0;dragon.hurt(dragon.head,level.damageSources().playerAttack(player),10);hits++;}
        report.put("initialHealth",initial);report.put("remainingHealth",dragon.getHealth());report.put("headHitsAtTenDamage",hits);
        report.put("method","Controlled ordinary player attack damage to natural dragon head; no /kill. Death animation/exit portal must be checked after ticks. Not a survival playthrough.");
        save(s,"dragon-damage",report);
    }
    static void endstatus(CommandSourceStack s) throws Exception {
        var level=s.getServer().getLevel(Level.END);var report=new LinkedHashMap<String,Object>();
        var portals=new ArrayList<String>();
        for(var p:BlockPos.betweenClosed(-5,40,-5,5,100,5))if(level.getBlockState(p).is(Blocks.END_PORTAL))portals.add(p.toShortString());
        report.put("dragonFightPreviouslyKilled",level.getDragonFight().hasPreviouslyKilledDragon());
        report.put("livingDragons",level.getEntities(net.minecraft.world.entity.EntityType.ENDER_DRAGON,e->e.isAlive()).size());
        report.put("dragonMaxHealth",level.getEntities(net.minecraft.world.entity.EntityType.ENDER_DRAGON,e->e.isAlive()).stream().map(e->e.getMaxHealth()).toList());
        report.put("exitPortalBlocks",portals);
        var player=s.getServer().getPlayerList().getPlayerByName("PackValidation");
        report.put("playerDimension",player.level().dimension().location().toString());
        save(s,"end-status",report);
    }
    static void balance(CommandSourceStack s) throws Exception {
        var dragon=net.minecraft.world.entity.EntityType.ENDER_DRAGON.create(s.getServer().overworld());
        dragon.setPos(16,250,16);s.getServer().overworld().addFreshEntity(dragon);
        double configured=DragonBalance.HEALTH.get();var report=new LinkedHashMap<String,Object>();
        try {
            report.put("spawnMaxHealth",dragon.getMaxHealth());report.put("spawnHealth",dragon.getHealth());
            dragon.setHealth(100);DragonBalance.HEALTH.set(600.0);DragonBalance.apply(dragon);
            report.put("afterChangeTo600Health",dragon.getHealth());
            DragonBalance.HEALTH.set(configured);DragonBalance.apply(dragon);report.put("afterRestoreHealth",dragon.getHealth());
            dragon.setHealth(0);DragonBalance.apply(dragon);report.put("deadHealthAfterApply",dragon.getHealth());
            save(s,"dragon-balance",report);
        } finally {DragonBalance.HEALTH.set(configured);dragon.discard();}
    }
    static void flight(CommandSourceStack s,String id) throws Exception {
        flight(s,id,false);
    }
    static void flight(CommandSourceStack s,String id,boolean collisionOnly) throws Exception {
        boolean naturalDragon=id.equals("minecraft:ender_dragon");
        var level=naturalDragon?s.getServer().getLevel(Level.END):s.getServer().overworld();var type=ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
        if(type==null)throw new IllegalArgumentException(id);
        Entity entity=naturalDragon?level.getEntities(net.minecraft.world.entity.EntityType.ENDER_DRAGON,e->e.isAlive()).stream().findFirst().orElseThrow():type.create(level);
        if(!(entity instanceof LivingEntity living))throw new IllegalArgumentException("Not living");
        // Clear-air fixture above the loaded test spawn; no modification of creature damage handling.
        if(!naturalDragon){living.moveTo(16,250,16,0,0);living.setNoGravity(true);if(living instanceof Mob mob)mob.setNoAi(true);}
        boolean added=naturalDragon||level.addFreshEntity(living);
        living.xo=living.getX();living.yo=living.getY();living.zo=living.getZ();
        var owner=FakePlayerFactory.getMinecraft(level);owner.setPos(6,250,16);
        var gunId=new ResourceLocation("tacz:ak47");var ammoId=new ResourceLocation("tacz:762x39");
        var gun=new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("tacz:modern_kinetic_gun")));
        gun.getOrCreateTag().putString("GunId",gunId.toString());gun.getOrCreateTag().putString("GunFireMode","AUTO");
        owner.setItemInHand(InteractionHand.MAIN_HAND,gun);
        var api=Class.forName("com.tacz.guns.api.TimelessAPI");
        Object index=((Optional<?>)api.getMethod("getCommonGunIndex",ResourceLocation.class).invoke(null,gunId)).orElseThrow();
        Object data=index.getClass().getMethod("getGunData").invoke(index),bulletData=index.getClass().getMethod("getBulletData").invoke(index);
        var cacheClass=Class.forName("com.tacz.guns.resource.modifier.AttachmentCacheProperty");
        Object cache=cacheClass.getConstructor().newInstance();cacheClass.getMethod("eval",ItemStack.class,data.getClass()).invoke(cache,gun,data);
        var operatorClass=Class.forName("com.tacz.guns.api.entity.IGunOperator");
        Object operator=operatorClass.getMethod("fromLivingEntity",LivingEntity.class).invoke(null,owner);
        operatorClass.getMethod("updateCacheProperty",cacheClass).invoke(operator,cache);
        var bulletClass=Class.forName("com.tacz.guns.entity.EntityKineticBullet");
        var constructor=bulletClass.getConstructor(Level.class,LivingEntity.class,ItemStack.class,ResourceLocation.class,ResourceLocation.class,boolean.class,data.getClass(),bulletData.getClass());
        float initial=living.getHealth(),first=initial;int hits=0,flightTicks=0,shots=0;String firstEnd="";int candidateCount=0;
        try {
            while(living.isAlive() && shots<(id.startsWith("scp")?1000:200)) {
                if(living instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon dragon && dragon.getPhaseManager().getCurrentPhase().getPhase()==net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase.DYING)break;
                float before=living.getHealth();living.invulnerableTime=0;
                Entity aim=naturalDragon?((net.minecraft.world.entity.boss.enderdragon.EnderDragon)living).head:living;
                double aimY=aim.getY()+Math.min(aim.getBbHeight()*.5,3);
                owner.setPos(aim.getX()-12,aimY-1.6,aim.getZ());
                Projectile bullet=(Projectile)constructor.newInstance(level,owner,gun,ammoId,gunId,false,data,bulletData);
                float nativeSpeed=((Number)bulletData.getClass().getMethod("getSpeed").invoke(bulletData)).floatValue()/20.0F;
                bullet.setPos(aim.getX()-8,aimY,aim.getZ());bullet.setDeltaMovement(nativeSpeed,0,0);
                level.addFreshEntity(bullet);
                candidateCount=level.getEntities(bullet,living.getBoundingBox().inflate(1)).size();
                if(collisionOnly) {
                    var entityResult=Class.forName("com.tacz.guns.entity.EntityKineticBullet$EntityResult");
                    Object hitEntity=entityResult.getConstructor(Entity.class,Vec3.class,boolean.class).newInstance(aim,aim.position(),false);
                    var hitClass=Class.forName("com.tacz.guns.util.TacHitResult");Object hit=hitClass.getConstructor(entityResult).newInstance(hitEntity);
                    var hitMethod=bulletClass.getDeclaredMethod("onHitEntity",hitClass,Vec3.class,Vec3.class);hitMethod.setAccessible(true);
                    hitMethod.invoke(bullet,hit,bullet.position(),aim.position());
                } else for(int t=0;t<12 && !bullet.isRemoved();t++){bullet.tick();flightTicks++;}
                if(shots==0)firstEnd=bullet.position().toString();
                bullet.discard();shots++;if(living.getHealth()<before)hits++;
                if(shots==1)first=living.getHealth();
                if(shots>=12 && hits==0)break;
            }
            var report=new LinkedHashMap<String,Object>();report.put("entity",id);report.put("initialHealth",initial);report.put("afterFirstProjectile",first);
            report.put("remainingHealth",living.getHealth());report.put("dead",living.isDeadOrDying());report.put("projectiles",shots);report.put("damagingProjectiles",hits);report.put("flightTicks",flightTicks);
            report.put("entityAdded",added);report.put("initialMaxHealth",living.getMaxHealth());report.put("candidateCount",candidateCount);report.put("firstBulletEnd",firstEnd);report.put("targetBounds",living.getBoundingBox().toString());
            report.put("existingNaturalDragon",naturalDragon);
            report.put("method",collisionOnly?"Native AK47 onHitEntity pipeline against actual Dragon head part, collision supplied directly. No flight claim, no direct creature hurt calls. Cooldown reset; controlled damage test, not combat time-to-kill.":"Real AK47 bullet initialized with native gun/bullet data, stepped through projectile flight and collision in controlled clear air. Creature AI disabled, per-shot hurt cooldown cleared; no direct hurt calls. Not a complete encounter, gun-input/reload, armor or boss-phase test.");
            save(s,(collisionOnly?"collision-":"flight-")+id.replace(':','_'),report);
        } finally {if(!naturalDragon)living.discard();}
    }
    static void lootr(CommandSourceStack s) throws Exception {
        var level=s.getServer().overworld();var player=s.getServer().getPlayerList().getPlayerByName("PackValidation");
        if(player==null || player.level()!=level)throw new IllegalStateException("Test player must be in overworld");
        var block=ForgeRegistries.BLOCKS.getValue(new ResourceLocation("lootr:lootr_chest"));
        var pos=new BlockPos(8,70,8);var original=level.getBlockState(pos);int guns=0,unpaired=0,opened=0;
        try {
            for(int n=0;n<60;n++) {
                level.setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());level.setBlockAndUpdate(pos,block.defaultBlockState());
                var be=(net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity)level.getBlockEntity(pos);
                be.setLootTable(new ResourceLocation("minecraft:chests/simple_dungeon"),n+10000);
                block.use(level.getBlockState(pos),level,pos,player,InteractionHand.MAIN_HAND,new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
                var menu=player.containerMenu;
                if(menu instanceof net.minecraft.world.inventory.ChestMenu chest) {
                    opened++;var inventory=chest.getContainer();var ammo=new HashSet<String>();var gunIds=new ArrayList<String>();
                    for(int k=0;k<inventory.getContainerSize();k++) {
                        var item=inventory.getItem(k);var tag=item.getTag();if(tag==null)continue;
                        if(tag.contains("AmmoId"))ammo.add(tag.getString("AmmoId"));if(tag.contains("GunId"))gunIds.add(tag.getString("GunId"));
                    }
                    for(var id:gunIds){guns++;String expected=Map.of("tacz:glock_17","tacz:9mm","tacz:m1911","tacz:45acp","tacz:mp5","tacz:9mm","tacz:ak47","tacz:762x39","tacz:m4a1","tacz:556x45").get(id);if(expected==null||!ammo.contains(expected))unpaired++;}
                }
                player.closeContainer();
            }
        } finally {level.setBlockAndUpdate(pos,original);}
        var report=new LinkedHashMap<String,Object>();report.put("openedInstancedChests",opened);report.put("guns",guns);report.put("gunsWithoutMatchingAmmo",unpaired);
        report.put("method","60 fresh Lootr chest block entities opened through the block use/player menu path, deterministic vanilla dungeon loot seeds. Full 27-slot menu inventory inspected.");save(s,"lootr",report);
    }
}
