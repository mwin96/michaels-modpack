package net.michaelspack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Keep the ordinary map readable; the existing F4 toggle still enables geology. */
@Pseudo
@Mixin(targets="com.anobs.extremedisasters.client.XaeroOverlayToggle", remap=false)
public final class DisasterMapDefaultMixin {
    @Shadow(remap=false) private static boolean enabled;

    @Inject(method="<clinit>", at=@At("RETURN"), remap=false, require=1)
    private static void ordinaryMapByDefault(CallbackInfo ci) {
        enabled = false;
    }
}
