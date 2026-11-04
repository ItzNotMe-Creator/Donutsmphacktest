package dev.donutlab;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.*;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class DonutLab implements ClientModInitializer {
    private boolean held, sprint, bright, hud = true;
    private double oldGamma;
    private final List<String> results = new ArrayList<>();
    private BlockPos waypoint;
    private String waypointWorld;
    private int scanIndex;
    private BlockPos scanOrigin;
    private String scanWorld;
    private static final int SIDE = 65, TOTAL = SIDE * SIDE * SIDE;
    @Override public void onInitializeClient() {
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            MinecraftClient c = MinecraftClient.getInstance();
            if (!hud || c.player == null || c.options.hudHidden) return;
            ctx.fill(6, 6, 225, 35, 0xBB0B1020);
            ctx.fill(6, 6, 8, 35, 0xFF56E0CD);
            ctx.drawTextWithShadow(c.textRenderer, "DONUT LAB  |  " + (sprint ? "SPRINT ON" : "EXPLORER"), 14, 11, 0xFF56E0CD);
            ctx.drawTextWithShadow(c.textRenderer, "XYZ " + c.player.getBlockPos().toShortString(), 14, 23, 0xFFE2E8F0);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean down = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
            if (down && !held && client.currentScreen == null) client.setScreen(new Panel());
            held = down;
            if (client.player == null || client.world == null) { scanOrigin = null; return; }
            if (sprint && client.options.forwardKey.isPressed() && !client.player.isSneaking()) client.player.setSprinting(true);
            if (scanOrigin != null) {
                if (!client.world.getRegistryKey().getValue().toString().equals(scanWorld)) { scanOrigin = null; return; }
                for (int n = 0; n < 2048 && scanIndex < TOTAL; n++, scanIndex++) {
                    int i = scanIndex;
                    BlockPos pos = scanOrigin.add(i % SIDE - 32, (i / SIDE) % SIDE - 32, i / (SIDE * SIDE) - 32);
                    if (!client.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) continue;
                    BlockEntity be = client.world.getBlockEntity(pos);
                    if ((be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity || be instanceof ShulkerBoxBlockEntity || be instanceof AbstractFurnaceBlockEntity || be instanceof HopperBlockEntity) && results.size() < 100) {
                        results.add(client.world.getBlockState(pos).getBlock().getName().getString() + "  " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                    }
                }
                if (scanIndex >= TOTAL) { scanOrigin = null; client.player.sendMessage(Text.literal("Donut Lab: scan complete. " + results.size() + " storage/workstation blocks (maximum 100)."), false); }
            }
        });
    }
    private class Panel extends Screen {
        Panel() { super(Text.literal("Donut Lab")); }
        private void button(String label, int x, int y, Runnable action) {
            addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> action.run()).dimensions(x,y,190,20).build());
        }
        @Override protected void init() {
            int x = width / 2 - 200, y = 66;
            button("Auto sprint: " + (sprint ? "ON" : "OFF"), x,y, () -> { sprint = !sprint; clearAndInit(); });
            button("Brightness: " + (bright ? "MAX" : "DEFAULT"), x,y+26, () -> {
                if (!bright) { oldGamma = client.options.getGamma().getValue(); client.options.getGamma().setValue(1.0); }
                else client.options.getGamma().setValue(oldGamma);
                bright = !bright; clearAndInit();
            });
            button("Scan nearby storage", x,y+52, () -> {
                if (client.player != null && client.world != null) {
                    results.clear(); scanIndex = 0; scanOrigin = client.player.getBlockPos(); scanWorld = client.world.getRegistryKey().getValue().toString();
                }
            });
            button("Save waypoint", x,y+78, () -> {
                if (client.player != null && client.world != null) { waypoint = client.player.getBlockPos(); waypointWorld = client.world.getRegistryKey().getValue().toString(); }
            });
            button("Copy coordinates",x,y+104, () -> {
                if (client.player != null) client.keyboard.setClipboard(client.player.getBlockPos().toShortString());
            });
            button("Clear results / waypoint",x,y+130, () -> { results.clear(); waypoint = null; scanOrigin = null; });
            button("Coordinates HUD: " + (hud ? "ON" : "OFF"),x,y+156, () -> { hud = !hud; clearAndInit(); });
        }
        @Override public boolean shouldPause() { return false; }
        @Override public void render(DrawContext ctx, int mx, int my, float delta) {
            ctx.fill(0,0,width,height,0xEE0B1020);
            ctx.fill(0,0,width,3,0xFF56E0CD);
            int x=width/2-200;
            ctx.drawTextWithShadow(textRenderer,"DONUT / LAB",x,22,0xFF56E0CD);
            ctx.drawTextWithShadow(textRenderer,"Exploration toolkit  •  1.21.11",x,39,0xFFA7B2CB);
            super.render(ctx,mx,my,delta);
            int rx=width/2+4, y=66;
            ctx.drawTextWithShadow(textRenderer,scanOrigin == null ? "STORAGE RESULTS ("+results.size()+")" : "SCANNING " + (scanIndex*100/TOTAL)+"%",rx,y,0xFF56E0CD);
            y+=18;
            int max=Math.max(0,(height-130)/12);
            for (String s: results.subList(0,Math.min(results.size(),max))) { ctx.drawTextWithShadow(textRenderer,textRenderer.trimToWidth(s,195),rx,y,0xFFE2E8F0); y+=12; }
            if (results.isEmpty()) ctx.drawTextWithShadow(textRenderer,"Search radius: 32 blocks",rx,y,0xFFA7B2CB);
            if(client.player != null) ctx.drawTextWithShadow(textRenderer,"XYZ " + client.player.getBlockPos().toShortString(),x,height-44,0xFFE2E8F0);
            if(waypoint != null && client.world != null) {
                String dist=client.world.getRegistryKey().getValue().toString().equals(waypointWorld) && client.player != null ? " | " + (int)Math.sqrt(waypoint.getSquaredDistance(client.player.getBlockPos()))+"m" : " | other dimension";
                ctx.drawTextWithShadow(textRenderer,"Waypoint " + waypoint.toShortString()+dist,x,height-30,0xFF56E0CD);
            }
            ctx.drawTextWithShadow(textRenderer,"Right Shift opens menu  •  Scans only loaded chunks",x,height-16,0xFF8793AC);
        }
    }
}
