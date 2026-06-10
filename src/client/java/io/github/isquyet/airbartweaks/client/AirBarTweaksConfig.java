package io.github.isquyet.airbartweaks.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class AirBarTweaksConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("air-bar-tweaks.json");
	private static final AirBarVisibilityMode DEFAULT_VISIBILITY_MODE = AirBarVisibilityMode.VANILLA;

	private static AirBarTweaksConfig instance = new AirBarTweaksConfig();

	private AirBarVisibilityMode visibilityMode = DEFAULT_VISIBILITY_MODE;

	public static AirBarTweaksConfig get() {
		return instance;
	}

	public static void load() {
		if (Files.notExists(CONFIG_PATH)) {
			instance = new AirBarTweaksConfig();
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			AirBarTweaksConfig config = GSON.fromJson(reader, AirBarTweaksConfig.class);
			if (config == null || config.visibilityMode == null) {
				instance = new AirBarTweaksConfig();
				save();
				return;
			}

			instance = config;
		} catch (IOException | JsonParseException exception) {
			AirBarTweaksClient.LOGGER.warn("Failed to load Air Bar Tweaks config. Using defaults.", exception);
			instance = new AirBarTweaksConfig();
		}
	}

	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException exception) {
			AirBarTweaksClient.LOGGER.warn("Failed to save Air Bar Tweaks config.", exception);
		}
	}

	public AirBarVisibilityMode visibilityMode() {
		return visibilityMode == null ? DEFAULT_VISIBILITY_MODE : visibilityMode;
	}

	public void setVisibilityMode(AirBarVisibilityMode visibilityMode) {
		this.visibilityMode = visibilityMode == null ? DEFAULT_VISIBILITY_MODE : visibilityMode;
	}
}
