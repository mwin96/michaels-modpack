package net.michaelspack;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.event.server.ServerStartedEvent;

/** Small, isolated pack behavior in place of a large redundant boss addon. */
@Mod("michaelspack")
public final class PackTweaks {
    public PackTweaks() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON,DragonBalance.SPEC,"michaelspack-common.toml");
        net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus().addListener(DragonBalance::configChanged);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DragonBalance());
    }

    @SubscribeEvent
    public void loadedPauseConfig(ServerStartedEvent event) {
        try {
            Class.forName("com.dyxiaojiazi.unmannedonlinetickstasis.Config")
                    .getMethod("updatePatterns").invoke(null);
        } catch (ClassNotFoundException ignored) {
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to refresh the pause command whitelist",exception);
        }
    }

    @SubscribeEvent
    public void wildTremorzilla(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide
                || mob.tickCount % 20 != 0 || !mob.isAlive()) return;
        var id=ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        if (id==null || !id.toString().equals("alexscaves:tremorzilla")) return;
        if (mob instanceof TamableAnimal pet && pet.isTame()) return;
        if (mob.getTarget()!=null && mob.getTarget().isAlive()) return;
        Player target=mob.level().getNearestPlayer(mob,40);
        if (target!=null && !target.isCreative() && !target.isSpectator()) mob.setTarget(target);
    }
}
