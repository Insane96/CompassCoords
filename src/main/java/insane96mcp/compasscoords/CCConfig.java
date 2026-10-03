package insane96mcp.compasscoords;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(modid = CompassCoords.MOD_ID, value = Dist.CLIENT)
public class CCConfig {
	public static final ModConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	static {
		final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = specPair.getLeft();
		CLIENT_SPEC = specPair.getRight();
	}

	public static class ClientConfig {

		private final ModConfigSpec.EnumValue<Position> positionConfig;
		private final ModConfigSpec.BooleanValue showYCoordConfig;

		public boolean showYCoord = false;
		public Position position = Position.BOTTOM_LEFT;

		public ClientConfig(final ModConfigSpec.Builder builder) {
			positionConfig = builder
					.comment("Coordinates Position")
					.defineEnum("Position", position);
			showYCoordConfig = builder
					.comment("If false, the Y coordinate is not displayed.")
					.define("Show Y Coordinate", showYCoord);
		}

		public void loadConfig() {
			this.showYCoord = this.showYCoordConfig.get();
			this.position = this.positionConfig.get();
		}
	}

	public enum Position {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent event) {
		if (event instanceof ModConfigEvent.Unloading)
			return;
		if (event.getConfig().getModId().equals(CompassCoords.MOD_ID) && event.getConfig().getType().equals(ModConfig.Type.CLIENT)) {
			CLIENT.loadConfig();
		}
	}
}
