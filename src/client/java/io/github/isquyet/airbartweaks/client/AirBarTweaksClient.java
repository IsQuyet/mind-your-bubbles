package io.github.isquyet.airbartweaks.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirBarTweaksClient implements ClientModInitializer {
	public static final String MOD_ID = "air-bar-tweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		AirBarTweaksConfig.load();
	}
}