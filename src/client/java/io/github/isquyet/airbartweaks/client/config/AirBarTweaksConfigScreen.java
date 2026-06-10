package io.github.isquyet.airbartweaks.client.config;

import io.github.isquyet.airbartweaks.client.AirBarTweaksConfig;
import io.github.isquyet.airbartweaks.client.AirBarVisibilityMode;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class AirBarTweaksConfigScreen {
	private AirBarTweaksConfigScreen() {
	}

	public static Screen create(Screen parent) {
		AirBarTweaksConfig config = AirBarTweaksConfig.get();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("air-bar-tweaks.config.title"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("air-bar-tweaks.config.category.general"));

		general.addEntry(entryBuilder
				.startEnumSelector(
						Component.translatable("air-bar-tweaks.config.visibility_mode"),
						AirBarVisibilityMode.class,
						config.visibilityMode()
				)
				.setDefaultValue(AirBarVisibilityMode.VANILLA)
				.setTooltip(Component.translatable("air-bar-tweaks.config.visibility_mode.tooltip"))
				.setEnumNameProvider(AirBarTweaksConfigScreen::visibilityModeName)
				.setSaveConsumer(config::setVisibilityMode)
				.build());

		builder.setSavingRunnable(AirBarTweaksConfig::save);
		return builder.build();
	}

	private static Component visibilityModeName(Enum<?> mode) {
		return Component.translatable("air-bar-tweaks.config.visibility_mode." + mode.name().toLowerCase(Locale.ROOT));
	}
}
