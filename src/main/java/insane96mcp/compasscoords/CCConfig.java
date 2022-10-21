package insane96mcp.compasscoords;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = CompassCoords.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CCConfig {
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ClientConfig CLIENT;

	static {
		final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = specPair.getLeft();
		CLIENT_SPEC = specPair.getRight();
	}

	public static class ClientConfig {

		private final ForgeConfigSpec.ConfigValue<Position> positionConfig;
		private final ForgeConfigSpec.ConfigValue<Boolean> showYCoordConfig;

		public boolean showYCoord = false;
		public Position position = Position.BOTTOM_LEFT;

		public ClientConfig(final ForgeConfigSpec.Builder builder) {
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
		if (event.getConfig().getModId().equals(CompassCoords.MOD_ID) && event.getConfig().getType().equals(ModConfig.Type.CLIENT)) {
			CLIENT.loadConfig();
		}
	}
}
