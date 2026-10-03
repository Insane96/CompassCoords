package insane96mcp.compasscoords;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.ArrayList;

@Mod(value = CompassCoords.MOD_ID, dist = Dist.CLIENT)
public class CompassCoords
{
    public static final String MOD_ID = "compasscoords";

    public CompassCoords(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CCConfig.CLIENT_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class PlayerUpdate {
        static Minecraft mc = Minecraft.getInstance();

        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY, ResourceLocation.fromNamespaceAndPath(MOD_ID, "coordinates"), PlayerUpdate::renderCoordinates);
        }

        public static void renderCoordinates(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
            if (mc.options.hideGui || mc.player == null)
                return;
            if (mc.getDebugOverlay().showDebugScreen() && !mc.showOnlyReducedInfo())
                return;

            int scaledWidth = mc.getWindow().getGuiScaledWidth();
            int scaledHeight = mc.getWindow().getGuiScaledHeight();

            ArrayList<String> toDraw = new ArrayList<>();

            ItemStack mainHand = mc.player.getMainHandItem();
            ItemStack offHand = mc.player.getOffhandItem();

            BlockPos pos = mc.player.blockPosition();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            float direction = Mth.wrapDegrees(mc.player.getYHeadRot());

            String d = "";
            if (direction > -22.5 && direction <= 22.5)
                d = "compasscoords.south";
            else if (direction > 22.5 && direction <= 67.5)
                d = "compasscoords.south_west";
            else if (direction > 67.5 && direction <= 112.5)
                d = "compasscoords.west";
            else if (direction > 112.5 && direction <= 157.5)
                d = "compasscoords.north_west";
            else if (direction > 157.5 || direction <= -157.5)
                d = "compasscoords.north";
            else if (direction > -157.5 && direction <= -112.5)
                d = "compasscoords.north_east";
            else if (direction > -112.5 && direction <= -67.5)
                d = "compasscoords.east";
            else if (direction > -67.5 && direction <= -22.5)
                d = "compasscoords.south_east";

            if (mainHand.getItem().equals(Items.COMPASS) || offHand.getItem().equals(Items.COMPASS)) {
                toDraw.add(String.format("X: %d", x));
                if (CCConfig.CLIENT.showYCoord)
                    toDraw.add(String.format("Y: %d", y));
                toDraw.add(String.format("Z: %d", z));
                toDraw.add(I18n.get(d));
            }

            switch (CCConfig.CLIENT.position) {
                case TOP_LEFT -> {
                    int top = 2;
                    int left = 2;
                    for (int i = 0; i < toDraw.size(); i++) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(guiGraphics, left, top, text, -1873784752, 14737632);
                        top += mc.font.lineHeight;
                    }
                }
                case TOP_RIGHT -> {
                    int top = 2;
                    int left = scaledWidth - 2;
                    for (int i = 0; i < toDraw.size(); i++) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(guiGraphics, left - mc.font.width(text), top, text, -1873784752, 14737632);
                        top += mc.font.lineHeight;
                    }
                }
                case BOTTOM_LEFT -> {
                    int top = scaledHeight - mc.font.lineHeight - 1;
                    int left = 2;
                    for (int i = toDraw.size() - 1; i >= 0; i--) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(guiGraphics, left, top, text, -1873784752, 14737632);
                        top -= mc.font.lineHeight;
                    }
                }
                case BOTTOM_RIGHT -> {
                    int top = scaledHeight - mc.font.lineHeight - 1;
                    int left = scaledWidth - 2;
                    for (int i = toDraw.size() - 1; i >= 0; i--) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(guiGraphics, left - mc.font.width(text), top, text, -1873784752, 14737632);
                        top -= mc.font.lineHeight;
                    }
                }
            }
        }

        private static void drawOnScreenWithBackground(GuiGraphics guiGraphics, int x, int y, String text, int backgroundColor, int textColor) {
            guiGraphics.fill(x - 1, y - 1, x + mc.font.width(text) + 1, y + mc.font.lineHeight - 1, backgroundColor);
            guiGraphics.drawString(mc.font, text, x, y, textColor);
        }
    }
}
