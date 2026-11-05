package net.michaelspack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Some upstream animation keyframes contain a lone '-' instead of a number. */
@Pseudo
@Mixin(targets="software.bernie.geckolib.core.molang.MolangParser",remap=false)
public abstract class MolangPlaceholderMixin {
    @ModifyVariable(method="parseExpression",at=@At("HEAD"),argsOnly=true,remap=false)
    private static String michaelspack$emptyKeyframe(String expression) {
        return expression!=null && expression.trim().equals("-") ? "0" : expression;
    }
}
