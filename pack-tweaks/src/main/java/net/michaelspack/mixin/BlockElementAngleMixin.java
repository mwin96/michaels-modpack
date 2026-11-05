package net.michaelspack.mixin;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The renderer accepts these rotations; the JSON loader's whitelist does not. */
@Mixin(targets="net.minecraft.client.renderer.block.model.BlockElement$Deserializer")
public abstract class BlockElementAngleMixin {
    @Inject(method="getAngle",at=@At("HEAD"),cancellable=true)
    private void michaelspack$allowFiniteModelAngles(JsonObject json,CallbackInfoReturnable<Float> result) {
        float angle=GsonHelper.getAsFloat(json,"angle");
        if (Float.isFinite(angle) && angle>=-360 && angle<=360) result.setReturnValue(angle);
    }
}
