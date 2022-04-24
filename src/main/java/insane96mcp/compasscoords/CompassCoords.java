package insane96mcp.compasscoords;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

import java.util.ArrayList;

@Mod(CompassCoords.MOD_ID)
public class CompassCoords
{
    public static final String MOD_ID = "compasscoords";

    public CompassCoords() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CCConfig.CLIENT_SPEC);
    }

    @Mod.EventBusSubscriber()
    public static class PlayerUpdate {
        static Minecraft mc = Minecraft.getInstance();

        @SubscribeEvent
        public static void renderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
            if (mc.options.renderDebug && !mc.options.reducedDebugInfo)
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

            if (mainHand.getItem() instanceof CompassItem || offHand.getItem() instanceof CompassItem) {
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
                    for (int i = toDraw.size() - 1; i >= 0; i--) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(event.getMatrixStack(), left, top, text, -1873784752, 14737632);
                        top += mc.font.lineHeight;
                    }
                }
                case TOP_RIGHT -> {
                    int top = 2;
                    int left = scaledWidth - 2;
                    for (int i = toDraw.size() - 1; i >= 0; i--) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(event.getMatrixStack(), left - mc.font.width(text), top, text, -1873784752, 14737632);
                        top += mc.font.lineHeight;
                    }
                }
                case BOTTOM_LEFT -> {
                    int top = scaledHeight - mc.font.lineHeight - 1;
                    int left = 2;
                    for (int i = toDraw.size() - 1; i >= 0; i--) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(event.getMatrixStack(), left, top, text, -1873784752, 14737632);
                        top -= mc.font.lineHeight;
                    }
                }
                case BOTTOM_RIGHT -> {
                    int top = scaledHeight - mc.font.lineHeight - 1;
                    int left = scaledWidth - 2;
                    for (int i = toDraw.size() - 1; i >= 0; i--) {
                        String text = toDraw.get(i);
                        drawOnScreenWithBackground(event.getMatrixStack(), left - mc.font.width(text), top, text, -1873784752, 14737632);
                        top -= mc.font.lineHeight;
                    }
                }
            }
        }

        private static void drawOnScreenWithBackground(PoseStack mStack, int x, int y, String text, int backgroundColor, int textColor) {
            ForgeIngameGui.fill(mStack, x - 1, y - 1, x + mc.font.width(text) + 1, y + mc.font.lineHeight - 1, backgroundColor);
            mc.font.draw(mStack, text, x, y, textColor);
        }
    }
}
