package net.michaelspack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** ArPhEx 5.0.2 builds the same untextured unit sphere every render stage.
 * Its existing begin(false) path retains the static VBO and skips rebuilding it.
 * All item/creature conditions, transforms and drawing calls remain unchanged.
 */
@Pseudo
@Mixin(targets="net.arphex.procedures.RenderTest6Procedure",remap=false)
public final class ArphexSphereCacheMixin {
    @ModifyArg(method="execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
        at=@At(value="INVOKE",target="Lnet/arphex/procedures/RenderTest6Procedure;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;Z)Z"),index=2,require=1)
    private static boolean retainStaticSphere(boolean rebuild) { return false; }
}
