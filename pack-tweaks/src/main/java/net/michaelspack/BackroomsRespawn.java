package net.michaelspack;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

/** Both upstream teleport helpers must honor the advertised respawn preference. */
public final class BackroomsRespawn {
    public static void setIfEnabled(ServerPlayer player, ResourceKey<Level> dimension, BlockPos position, float angle, boolean forced, boolean message) {
        try {
            Object option=Class.forName("fr.meulti.mbackrooms.config.BackroomsConfig")
                .getField("SET_SPAWN_POINT_WHEN_ENTERING_BACKROOMS").get(null);
            if(player.level().dimension().equals(dimension) && Boolean.TRUE.equals(((ForgeConfigSpec.ConfigValue<?>)option).get()))
                player.setRespawnPosition(dimension,position,angle,forced,message);
        } catch(ReflectiveOperationException ex) {throw new IllegalStateException("Backrooms respawn configuration unavailable",ex);}
    }
}
