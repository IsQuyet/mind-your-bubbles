package io.github.isquyet.mindyourbubbles.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class MindYourBubblesConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mind-your-bubbles.json");
	private static final AirBarVisibilityMode DEFAULT_VISIBILITY_MODE = AirBarVisibilityMode.VANILLA;
	private static final boolean DEFAULT_SMOOTH_AIR_BAR_ANIMATION = true;

	private static MindYourBubblesConfig instance = new MindYourBubblesConfig();

	private AirBarVisibilityMode visibilityMode = DEFAULT_VISIBILITY_MODE;
	private boolean smoothAirBarAnimation = DEFAULT_SMOOTH_AIR_BAR_ANIMATION;

	public static MindYourBubblesConfig get() {
		return instance;
	}

	public static void load() {
		if (Files.notExists(CONFIG_PATH)) {
			instance = new MindYourBubblesConfig();
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			MindYourBubblesConfig config = GSON.fromJson(reader, MindYourBubblesConfig.class);
			if (config == null || config.visibilityMode == null) {
				instance = new MindYourBubblesConfig();
				save();
				return;
			}

			instance = config;
		} catch (IOException | JsonParseException exception) {
			MindYourBubblesClient.LOGGER.warn("Failed to load Mind Your Bubbles config. Using defaults.", exception);
			instance = new MindYourBubblesConfig();
		}
	}

	public static void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException exception) {
			MindYourBubblesClient.LOGGER.warn("Failed to save Mind Your Bubbles config.", exception);
		}
	}

	public AirBarVisibilityMode visibilityMode() {
		return visibilityMode == null ? DEFAULT_VISIBILITY_MODE : visibilityMode;
	}

	public void setVisibilityMode(AirBarVisibilityMode visibilityMode) {
		this.visibilityMode = visibilityMode == null ? DEFAULT_VISIBILITY_MODE : visibilityMode;
	}

	public boolean smoothAirBarAnimation() {
		return smoothAirBarAnimation;
	}

	public void setSmoothAirBarAnimation(boolean smoothAirBarAnimation) {
		this.smoothAirBarAnimation = smoothAirBarAnimation;
	}
}
