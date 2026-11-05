package net.michaelspack;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

/** A configurable health adjustment; all vanilla fight phases remain intact. */
public final class DragonBalance {
    private static final ForgeConfigSpec.Builder BUILDER=new ForgeConfigSpec.Builder();
    static final ForgeConfigSpec.DoubleValue HEALTH=BUILDER.comment("Ender Dragon maximum health. Vanilla is 200; 400 suits the four-player gun-enabled pack. Changes preserve the current health fraction and never revive dying dragons.")
            .defineInRange("dragonMaxHealth",400.0,200.0,1024.0);
    static final ForgeConfigSpec SPEC=BUILDER.build();
    @SubscribeEvent public void joined(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide && event.getEntity() instanceof EnderDragon dragon)apply(dragon);
    }
    static void apply(EnderDragon dragon) {
        if(!dragon.isAlive() || dragon.isDeadOrDying() || dragon.getPhaseManager().getCurrentPhase().getPhase()==EnderDragonPhase.DYING)return;
        var attribute=dragon.getAttribute(Attributes.MAX_HEALTH);
        if(attribute==null)return;
        float fraction=dragon.getHealth()/dragon.getMaxHealth();
        double desired=HEALTH.get();
        if(attribute.getBaseValue()==desired)return;
        attribute.setBaseValue(desired);
        dragon.setHealth(Math.min(dragon.getMaxHealth(),fraction*dragon.getMaxHealth()));
    }
    static void configChanged(ModConfigEvent event) {
        if(event.getConfig().getSpec()!=SPEC)return;
        var server=ServerLifecycleHooks.getCurrentServer();
        if(server!=null)server.execute(()->{
            for(var level:server.getAllLevels())
                for(var dragon:level.getEntities(net.minecraft.world.entity.EntityType.ENDER_DRAGON,e->true))apply(dragon);
        });
    }
}
