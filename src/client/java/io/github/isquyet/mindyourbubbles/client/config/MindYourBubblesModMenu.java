package io.github.isquyet.mindyourbubbles.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.isquyet.mindyourbubbles.client.MindYourBubblesClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

public class MindYourBubblesModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
			return parent -> {
				MindYourBubblesClient.LOGGER.warn("Cloth Config is not installed; Mind Your Bubbles config screen is unavailable.");
				return parent;
			};
		}

		return MindYourBubblesConfigScreen::create;
	}
}
