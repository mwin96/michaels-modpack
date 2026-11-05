package net.michaelspack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
@Mod.EventBusSubscriber(modid="michaelspack",value=Dist.CLIENT)
public final class PortalTooltip {
 @SubscribeEvent public static void tooltip(ItemTooltipEvent event){
  if(!"mbackrooms:portal".equals(String.valueOf(ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem()))))return;
  event.getToolTip().add(Component.literal("Optional Backrooms trip."));
  event.getToolTip().add(Component.literal("Place one to enter; carry a spare to leave."));
  event.getToolTip().add(Component.literal("Brief cooldown between trips."));
 }
}
