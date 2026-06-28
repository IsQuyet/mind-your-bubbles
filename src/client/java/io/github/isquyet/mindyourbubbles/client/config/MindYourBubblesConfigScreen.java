package io.github.isquyet.mindyourbubbles.client.config;

import io.github.isquyet.mindyourbubbles.client.MindYourBubblesConfig;
import io.github.isquyet.mindyourbubbles.client.AirBarVisibilityMode;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class MindYourBubblesConfigScreen {
	private MindYourBubblesConfigScreen() {
	}

	public static Screen create(Screen parent) {
		MindYourBubblesConfig config = MindYourBubblesConfig.get();
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("mind-your-bubbles.config.title"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(Component.translatable("mind-your-bubbles.config.category.general"));

		general.addEntry(entryBuilder
				.startEnumSelector(
						Component.translatable("mind-your-bubbles.config.visibility_mode"),
						AirBarVisibilityMode.class,
						config.visibilityMode()
				)
				.setDefaultValue(AirBarVisibilityMode.VANILLA)
				.setTooltip(Component.translatable("mind-your-bubbles.config.visibility_mode.tooltip"))
				.setEnumNameProvider(MindYourBubblesConfigScreen::visibilityModeName)
				.setSaveConsumer(config::setVisibilityMode)
				.build());

		general.addEntry(entryBuilder
				.startBooleanToggle(
						Component.translatable("mind-your-bubbles.config.smooth_air_bar_animation"),
						config.smoothAirBarAnimation()
				)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("mind-your-bubbles.config.smooth_air_bar_animation.tooltip"))
				.setSaveConsumer(config::setSmoothAirBarAnimation)
				.build());

		builder.setSavingRunnable(MindYourBubblesConfig::save);
		return builder.build();
	}

	private static Component visibilityModeName(Enum<?> mode) {
		return Component.translatable("mind-your-bubbles.config.visibility_mode." + mode.name().toLowerCase(Locale.ROOT));
	}
}
