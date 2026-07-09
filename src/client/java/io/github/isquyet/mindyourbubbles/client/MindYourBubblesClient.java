package io.github.isquyet.mindyourbubbles.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MindYourBubblesClient implements ClientModInitializer {
	public static final String MOD_ID = "mind-your-bubbles";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		MindYourBubblesConfig.load();
	}
}
