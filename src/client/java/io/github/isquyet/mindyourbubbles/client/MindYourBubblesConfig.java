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
	private static final long MAX_CONFIG_SIZE_BYTES = 8192L;
	private static final AirBarVisibilityMode DEFAULT_VISIBILITY_MODE = AirBarVisibilityMode.VANILLA;
	private static final boolean DEFAULT_SMOOTH_AIR_BAR_ANIMATION = true;

	private static MindYourBubblesConfig instance = new MindYourBubblesConfig();

	private AirBarVisibilityMode visibilityMode = DEFAULT_VISIBILITY_MODE;
	private Boolean smoothAirBarAnimation = DEFAULT_SMOOTH_AIR_BAR_ANIMATION;

	public static MindYourBubblesConfig get() {
		return instance;
	}

	public static void load() {
		if (Files.notExists(CONFIG_PATH)) {
			instance = new MindYourBubblesConfig();
			save();
			return;
		}

		try {
			long configSize = Files.size(CONFIG_PATH);
			if (configSize > MAX_CONFIG_SIZE_BYTES) {
				MindYourBubblesClient.LOGGER.warn("Mind Your Bubbles config is too large ({} bytes). Backing it up and using defaults.", configSize);
				recoverWithDefaults();
				return;
			}

			MindYourBubblesConfig config;
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				config = GSON.fromJson(reader, MindYourBubblesConfig.class);
			}

			if (config == null) {
				MindYourBubblesClient.LOGGER.warn("Mind Your Bubbles config is empty or invalid. Backing it up and using defaults.");
				recoverWithDefaults();
				return;
			}

			boolean configChanged = config.normalizeDefaults();
			instance = config;
			if (configChanged) {
				save();
			}
		} catch (IOException | JsonParseException exception) {
			MindYourBubblesClient.LOGGER.warn("Failed to load Mind Your Bubbles config. Backing it up and using defaults.", exception);
			recoverWithDefaults();
		}
	}

	private static void recoverWithDefaults() {
		instance = new MindYourBubblesConfig();
		if (backupConfig()) {
			save();
			return;
		}

		MindYourBubblesClient.LOGGER.warn("Using default Mind Your Bubbles config for this session because the existing config could not be backed up.");
	}

	private static boolean backupConfig() {
		if (Files.notExists(CONFIG_PATH)) {
			return true;
		}

		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			Files.move(CONFIG_PATH, timestampedBackupPath());
			return true;
		} catch (IOException exception) {
			MindYourBubblesClient.LOGGER.warn("Failed to back up Mind Your Bubbles config.", exception);
			return false;
		}
	}

	private static Path timestampedBackupPath() {
		return CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + "." + System.currentTimeMillis() + ".bak");
	}

	private boolean normalizeDefaults() {
		boolean configChanged = false;
		if (visibilityMode == null) {
			visibilityMode = DEFAULT_VISIBILITY_MODE;
			configChanged = true;
		}

		if (smoothAirBarAnimation == null) {
			smoothAirBarAnimation = DEFAULT_SMOOTH_AIR_BAR_ANIMATION;
			configChanged = true;
		}

		return configChanged;
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
		return smoothAirBarAnimation == null ? DEFAULT_SMOOTH_AIR_BAR_ANIMATION : smoothAirBarAnimation;
	}

	public void setSmoothAirBarAnimation(boolean smoothAirBarAnimation) {
		this.smoothAirBarAnimation = smoothAirBarAnimation;
	}
}
