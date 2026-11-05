package net.michaelspack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Suppresses automatic guide handouts on first join; earned books remain available. */
@Mod.EventBusSubscriber(modid="michaelspack")
public final class CleanFirstJoin {
    private static final String DONE="michaelspack:clean_first_join";
    private static final Map<UUID,Integer> pending=new HashMap<>();
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void joined(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var persisted=player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (!persisted.getBoolean(DONE)) pending.put(player.getUUID(),20);
    }
    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase!=TickEvent.Phase.END || pending.isEmpty()) return;
        var iterator=pending.entrySet().iterator();
        while(iterator.hasNext()) {
            var entry=iterator.next();
            var player=event.getServer().getPlayerList().getPlayer(entry.getKey());
            if(player==null){iterator.remove();continue;}
            for(int slot=0;slot<player.getInventory().getContainerSize();slot++) {
                var stack=player.getInventory().getItem(slot);
                var id=String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));
                if(id.equals("man:cassette_1safetyinstructions")
                        || id.equals("touhou_little_maid:smart_slab_init")
                        || id.equals("monsters_and_girls:monster_girls_bestiary")
                        || (id.equals("patchouli:guide_book") && stack.hasTag()
                            && stack.getTag().getString("patchouli:book").equals("touhou_little_maid:memorizable_gensokyo")))
                    player.getInventory().removeItemNoUpdate(slot);
            }
            if(entry.getValue()<=1) {
                var persisted=player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                persisted.putBoolean(DONE,true);
                player.getPersistentData().put(Player.PERSISTED_NBT_TAG,persisted);
                player.getInventory().setChanged();
                player.containerMenu.broadcastChanges();
                iterator.remove();
            } else entry.setValue(entry.getValue()-1);
        }
    }
}
