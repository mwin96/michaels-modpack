package net.michaelspack;

import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Active only in the isolated hidden validation client. */
@Mod.EventBusSubscriber(modid="michaelspack",value=Dist.CLIENT)
public final class ClientAudit {
    private static int ticks;
    private static int jesusCaptureAt=-1;
    private static final ArrayList<Integer> samples=new ArrayList<>();
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (!Boolean.getBoolean("michaelspack.validation") || event.phase!=TickEvent.Phase.END) return;
        Minecraft minecraft=Minecraft.getInstance();
        minecraft.options.getSoundSourceOptionInstance(net.minecraft.sounds.SoundSource.MASTER).set(0.0D);
        if (minecraft.level==null || minecraft.player==null) return;
        ticks++;
        var shaderRequest=minecraft.gameDirectory.toPath().resolve("validation-shader.request");
        if(ticks%20==0 && Files.exists(shaderRequest)) {
            try {
                boolean enabled=Files.readString(shaderRequest).trim().equals("on");Files.delete(shaderRequest);
                var iris=Class.forName("net.irisshaders.iris.Iris");Object config=iris.getMethod("getIrisConfig").invoke(null);
                config.getClass().getMethod("setShaderPackName",String.class).invoke(config,"MakeUp-UltraFast-9.6.zip");
                config.getClass().getMethod("setShadersEnabled",boolean.class).invoke(config,enabled);
                iris.getMethod("reload").invoke(null);
                Files.writeString(minecraft.gameDirectory.toPath().resolve("validation-shader-result.json"),"{\"requestedEnabled\":"+enabled+",\"packInUse\":"+iris.getMethod("isPackInUseQuick").invoke(null)+",\"at\":\""+java.time.Instant.now()+"\"}");
            } catch(Exception ex) {ex.printStackTrace();}
        }
        var jesusRequest=minecraft.gameDirectory.toPath().resolve("validation-jesus.request");
        if(ticks%20==0 && Files.exists(jesusRequest)) {
            try {
                Files.delete(jesusRequest);
                float savedHealth=minecraft.player.getHealth();
                try {minecraft.player.setHealth(2.0F);minecraft.player.hurt(minecraft.level.damageSources().generic(),1.0F);}
                finally {minecraft.player.setHealth(savedHealth);}
                var displaying=Class.forName("com.eternity.jessemood.client.guihandler").getDeclaredField("shouldDisplay");displaying.setAccessible(true);
                Files.writeString(minecraft.gameDirectory.toPath().resolve("validation-jesus-result.json"),"{\"displayTriggered\":"+displaying.getBoolean(null)+",\"lowHealthHurtPath\":true,\"muted\":true,\"at\":\""+java.time.Instant.now()+"\"}");
                jesusCaptureAt=ticks+2;
            } catch(Exception ex) {ex.printStackTrace();}
        }
        if(ticks==jesusCaptureAt)Screenshot.grab(minecraft.gameDirectory,"eternal-jesus-validation.png",minecraft.getMainRenderTarget(),message->{});
        if (ticks%20==0) samples.add(minecraft.getFps());
        if (samples.size()>120) samples.remove(0);
        var request=minecraft.gameDirectory.toPath().resolve("validation-capture.request");
        if (ticks==300 || ticks==1200 || (ticks%20==0 && Files.exists(request))) {
            var report=new LinkedHashMap<String,Object>();
            report.put("capturedAt",java.time.Instant.now().toString());
            report.put("validationOnly",true);
            report.put("worldTicks",ticks);
            report.put("fpsSamples",samples);
            report.put("gpu",GL11.glGetString(GL11.GL_RENDERER));
            report.put("resolution",minecraft.getWindow().getWidth()+"x"+minecraft.getWindow().getHeight());
            report.put("dimension",minecraft.level.dimension().location().toString());
            report.put("position",minecraft.player.position().toString());
            report.put("limitations","This machine is not the weakest friend's unknown computer. Hidden-window measurements are validation evidence, not a guarantee for other hardware.");
            try {
                String suffix=Files.exists(request)?"requested":Integer.toString(ticks);
                Files.deleteIfExists(request);
                Files.writeString(minecraft.gameDirectory.toPath().resolve("client-validation-"+suffix+".json"),new GsonBuilder().setPrettyPrinting().create().toJson(report));
                Screenshot.grab(minecraft.gameDirectory,"pack-validation-"+suffix+".png",minecraft.getMainRenderTarget(),message->{});
            } catch (Exception exception) { exception.printStackTrace(); }
        }
    }
}
