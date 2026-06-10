package io.github.isquyet.airbartweaks.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.isquyet.airbartweaks.client.AirBarTweaksClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

public class AirBarTweaksModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
			return parent -> {
				AirBarTweaksClient.LOGGER.warn("Cloth Config is not installed; Air Bar Tweaks config screen is unavailable.");
				return parent;
			};
		}

		return AirBarTweaksConfigScreen::create;
	}
}
