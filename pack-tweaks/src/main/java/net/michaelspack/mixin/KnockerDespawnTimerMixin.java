package net.michaelspack.mixin;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
@Pseudo
@Mixin(targets="net.mcreator.theknocker.procedures.PlayertickProcedure",remap=false)
public final class KnockerDespawnTimerMixin {
 @Redirect(method="execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDD)V",at=@At(value="FIELD",target="Lnet/mcreator/theknocker/network/TheKnockerModVariables$MapVariables;despawn_timer:D",opcode=181),require=2)
 private static void once(@Coerce Object owner,double value,Event event,LevelAccessor world,double x,double y,double z){net.michaelspack.KnockerTimers.write(owner,value,"despawn_timer",world);}
}
