package net.michaelspack.mixin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;
@Pseudo
@Mixin(targets="net.mcreator.theknocker.procedures.RandomspawnProcedure",remap=false)
public final class KnockerSpawnTimerMixin {
 @Redirect(method="execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",at=@At(value="FIELD",target="Lnet/mcreator/theknocker/network/TheKnockerModVariables$MapVariables;spawn_rate_timer:D",opcode=181),require=2)
 private static void once(@Coerce Object owner,double value,Event event,LevelAccessor world,double x,double y,double z,Entity entity){net.michaelspack.KnockerTimers.write(owner,value,"spawn_rate_timer",world);}
}
