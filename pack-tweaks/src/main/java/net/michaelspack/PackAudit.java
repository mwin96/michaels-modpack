package net.michaelspack;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid="michaelspack")
public final class PackAudit {
    @SubscribeEvent public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("michaelspack").requires(source->source.hasPermission(2))
            .then(Commands.literal("audit").executes(context->safely(context.getSource(),()->audit(context.getSource()))))
            .then(Commands.literal("gunprobe").then(Commands.argument("entity",StringArgumentType.greedyString())
                .executes(context->safely(context.getSource(),()->probe(context.getSource(),StringArgumentType.getString(context,"entity")))))));
    }
    @FunctionalInterface private interface Checked { int run() throws Exception; }
    private static int safely(net.minecraft.commands.CommandSourceStack source,Checked command) {
        try { return command.run(); }
        catch (Exception exception) { exception.printStackTrace();source.sendFailure(Component.literal(exception.toString()));return 0; }
    }
    private static int audit(net.minecraft.commands.CommandSourceStack source) throws java.io.IOException {
        var server=source.getServer();var level=source.getLevel();
        var report=new LinkedHashMap<String,Object>();
        report.put("minecraft","1.20.1");
        report.put("mods",ModList.get().getMods().stream().map(m->m.getModId()+"@"+m.getVersion()).toList());
        report.put("eyeOfEnderRecipe",server.getRecipeManager().byKey(new ResourceLocation("minecraft:ender_eye")).isPresent());
        report.put("entityIds",ForgeRegistries.ENTITY_TYPES.getKeys().stream().map(Object::toString).sorted().toList());
        report.put("itemIds",ForgeRegistries.ITEMS.getKeys().stream().map(Object::toString).sorted().toList());
        report.put("gameTime",level.getGameTime());
        var loot=new LinkedHashMap<String,Object>();
        for (String id : List.of("minecraft:chests/simple_dungeon","minecraft:chests/stronghold_library","idas:chests/dread_citadel/dread_citadel")) {
            var table=server.getLootData().getLootTable(new ResourceLocation(id));
            var params=new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN,source.getPosition()).create(LootContextParamSets.CHEST);
            int guns=0,ammo=0,artifacts=0,unpaired=0;
            for (int i=0;i<150;i++) {
              var generated=table.getRandomItems(params);
              var ammoIds=new java.util.HashSet<String>();
              for (ItemStack item:generated) if (item.getTag()!=null && item.getTag().contains("AmmoId")) ammoIds.add(item.getTag().getString("AmmoId"));
              for (ItemStack item : generated) {
                var key=ForgeRegistries.ITEMS.getKey(item.getItem());
                if (key!=null && key.toString().equals("tacz:modern_kinetic_gun")) {
                    guns++;
                    String gun=item.getTag()==null?"":item.getTag().getString("GunId");
                    String matching=java.util.Map.of("tacz:glock_17","tacz:9mm","tacz:m1911","tacz:45acp","tacz:mp5","tacz:9mm","tacz:ak47","tacz:762x39","tacz:m4a1","tacz:556x45").get(gun);
                    if (matching!=null && !ammoIds.contains(matching)) unpaired++;
                }
                if (key!=null && key.toString().equals("tacz:ammo")) ammo+=item.getCount();
                if (key!=null && key.getNamespace().equals("artifacts")) artifacts++;
              }
            }
            loot.put(id,java.util.Map.of("sampledChests",150,"guns",guns,"ammo",ammo,"artifacts",artifacts,"gunsWithoutMatchingAmmo",unpaired));
        }
        report.put("lootSampling",loot);
        Files.writeString(Path.of("pack-audit.json"),new GsonBuilder().setPrettyPrinting().create().toJson(report));
        source.sendSuccess(()->Component.literal("Pack audit written; Eye recipe="+report.get("eyeOfEnderRecipe")+"; loot="+loot),false);
        return 1;
    }
    private static int probe(net.minecraft.commands.CommandSourceStack source,String id) throws Exception {
        var level=source.getLevel();
        var type=ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
        if (type==null)throw new IllegalArgumentException("Unknown creature "+id);
        Entity entity=type.create(level);
        if (!(entity instanceof LivingEntity living))throw new IllegalArgumentException("Not a living creature "+id);
        living.moveTo(source.getPosition().x+5,Math.max(100,source.getPosition().y),source.getPosition().z,0,0);
        if (living instanceof Mob mob)mob.setNoAi(true);
        level.addFreshEntity(living);
        var owner=FakePlayerFactory.getMinecraft(level);
        owner.setPos(living.getX()-3,living.getY(),living.getZ());
        Entity bullet=ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("tacz:bullet")).create(level);
        if (bullet instanceof Projectile projectile)projectile.setOwner(owner);
        for (String field : List.of("gunId","ammoId")) {
            var reflected=bullet.getClass().getDeclaredField(field);reflected.setAccessible(true);
            reflected.set(bullet,new ResourceLocation(field.equals("gunId")?"tacz:ak47":"tacz:762x39"));
        }
        var damage=level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,new ResourceLocation("tacz:bullet")));
        DamageSource shot=new DamageSource(damage,bullet,owner);
        float start=living.getHealth();boolean accepted=living.hurt(shot,10);float after=living.getHealth();
        int shots=1;
        while (living.isAlive() && shots<300) {living.invulnerableTime=0;living.hurt(shot,10);shots++;}
        var result=new LinkedHashMap<String,Object>();
        result.put("entity",id);result.put("initialHealth",start);result.put("accepted",accepted);result.put("healthAfterFirstShot",after);
        result.put("dead",living.isDeadOrDying());result.put("shotsAtTenDamage",shots);
        result.put("scope","Actual TaCZ bullet entity and damage type through the creature's hurt pipeline. A controlled damage probe, not a complete simulated fight or flight-path test.");
        Path out=Path.of("gun-probes");Files.createDirectories(out);
        Files.writeString(out.resolve(id.replace(':','_')+".json"),new GsonBuilder().setPrettyPrinting().create().toJson(result));
        living.discard();bullet.discard();
        source.sendSuccess(()->Component.literal(result.toString()),false);return living.isDeadOrDying()?1:0;
    }
}
