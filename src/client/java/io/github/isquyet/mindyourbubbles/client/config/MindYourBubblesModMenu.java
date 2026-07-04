package io.github.isquyet.mindyourbubbles.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.isquyet.mindyourbubbles.client.MindYourBubblesClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MindYourBubblesModMenu implements ModMenuApi {
	private static final String CLOTH_CONFIG_MOD_ID = "cloth-config";
	private static final String CONFIG_SCREEN_CLASS_NAME = "io.github.isquyet.mindyourbubbles.client.config.MindYourBubblesConfigScreen";

	@Override
	public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
		if (!FabricLoader.getInstance().isModLoaded(CLOTH_CONFIG_MOD_ID)) {
			return parent -> {
				MindYourBubblesClient.LOGGER.warn("Mind Your Bubbles in-game config requires Cloth Config. Edit config/mind-your-bubbles.json manually and restart the game, or install Cloth Config to use the Mod Menu config screen.");
				return parent;
			};
		}

		return this::createConfigScreen;
	}

	private Screen createConfigScreen(Screen parent) {
		try {
			Class<?> configScreenClass = Class.forName(CONFIG_SCREEN_CLASS_NAME);
			Method createMethod = configScreenClass.getMethod("create", Screen.class);
			Object configScreen = createMethod.invoke(null, parent);
			if (configScreen instanceof Screen screen) {
				return screen;
			}

			MindYourBubblesClient.LOGGER.warn("Mind Your Bubbles config screen factory returned an unexpected value. Falling back to the parent screen.");
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | LinkageError exception) {
			MindYourBubblesClient.LOGGER.warn("Failed to open the Mind Your Bubbles in-game config screen. Edit config/mind-your-bubbles.json manually and restart the game, or check that Cloth Config is installed correctly.", exception);
		}

		return parent;
	}
}
