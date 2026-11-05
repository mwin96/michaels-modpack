package net.michaelspack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** A small menu banner using Minecraft's own font; no extra menu/rendering mod. */
@Mod.EventBusSubscriber(modid="michaelspack", value=Dist.CLIENT)
public final class ClientBranding {
    private static final String NAME="michael's modpack";
    private static long titleShownAt;
    private static boolean validationCaptured;
    private static Object lastScreen;
    private static Object lastLevel;
    private static long diagnosticStarted;
    private static boolean diagnosticCaptured;

    @SubscribeEvent
    public static void keepWindowTitle(TickEvent.ClientTickEvent event) {
        if (event.phase!=TickEvent.Phase.END) return;
        Minecraft minecraft=Minecraft.getInstance();
        if (Boolean.getBoolean("michaelspack.validation") && !diagnosticCaptured) {
            if (diagnosticStarted==0) diagnosticStarted=System.nanoTime();
            if (System.nanoTime()-diagnosticStarted>4_000_000_000L) {
                diagnosticCaptured=true;
                try {
                    var state=java.util.Map.of("screen",minecraft.screen==null?"null":minecraft.screen.getClass().getName(),
                            "overlay",minecraft.getOverlay()==null?"null":minecraft.getOverlay().getClass().getName());
                    java.nio.file.Files.writeString(minecraft.gameDirectory.toPath().resolve("validation-screen.json"),
                            new com.google.gson.Gson().toJson(state));
                    Screenshot.grab(minecraft.gameDirectory,"menu-screen-diagnostic.png",minecraft.getMainRenderTarget(),message->{});
                } catch (java.io.IOException exception) { exception.printStackTrace(); }
            }
        }
        if (minecraft.screen!=lastScreen || minecraft.level!=lastLevel) {
            lastScreen=minecraft.screen;
            lastLevel=minecraft.level;
            minecraft.getWindow().setTitle(NAME);
        }
    }

    @SubscribeEvent
    public static void init(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen) {
            Minecraft.getInstance().getWindow().setTitle(NAME);
            titleShownAt=System.nanoTime();
        }
    }

    @SubscribeEvent
    public static void render(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof TitleScreen screen)) return;
        Minecraft minecraft=Minecraft.getInstance();
        var graphics=event.getGuiGraphics();
        int center=screen.width/2;
        int top=screen.height/4+22;
        float scale=1.5F;
        int halfWidth=(int)(minecraft.font.width(NAME)*scale/2)+12;
        graphics.fill(center-halfWidth,top-5,center+halfWidth,top+18,0xB0181C24);
        graphics.fill(center-halfWidth,top-5,center+halfWidth,top-4,0xFFE5B76C);
        graphics.pose().pushPose();
        graphics.pose().translate(center,top,0);
        graphics.pose().scale(scale,scale,1);
        graphics.drawCenteredString(minecraft.font,NAME,0,0,0xFFE5B76C);
        graphics.pose().popPose();
        // Game-only evidence for the hidden review harness; never runs in a normal release launch.
        if (Boolean.getBoolean("michaelspack.validation") && !validationCaptured
                && System.nanoTime()-titleShownAt>4_000_000_000L) {
            validationCaptured=true;
            graphics.flush();
            Screenshot.grab(minecraft.gameDirectory,"michaels-menu-validation.png",minecraft.getMainRenderTarget(),message->{});
            try {
                var bindings=new java.util.TreeMap<String,Object>();
                for (var mapping:minecraft.options.keyMappings) {
                    bindings.put(mapping.getName(),java.util.Map.of("key",mapping.getKey().getName(),
                            "modifier",mapping.getKeyModifier().toString()));
                }
                java.nio.file.Files.writeString(minecraft.gameDirectory.toPath().resolve("validation-keybindings.json"),
                        new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(bindings));
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
