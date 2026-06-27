package io.github.isquyet.airbartweaks.client.mixin;

import io.github.isquyet.airbartweaks.client.AirBarTweaksConfig;
import io.github.isquyet.airbartweaks.client.AirBarTweaksClient;
import io.github.isquyet.airbartweaks.client.AirBarVisibilityMode;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class AirBarBubblesMixin {
	@Shadow
	private int tickCount;

	@Shadow
	private int getAirBubbleYLine(int vehicleHearts, int y) {
		throw new AssertionError();
	}

	@Shadow
	@Final
	private static Identifier AIR_SPRITE;

	@Shadow
	@Final
	private static Identifier AIR_POPPING_SPRITE;

	@Shadow
	@Final
	private static Identifier AIR_EMPTY_SPRITE;

	@Unique
	private int airBarTweaks$lastPlayerId = Integer.MIN_VALUE;
	@Unique
	private int airBarTweaks$lastMaxAir = Integer.MIN_VALUE;
	@Unique
	private int airBarTweaks$lastActualAir = Integer.MIN_VALUE;
	@Unique
	private int airBarTweaks$lastVisualBubbleCount = Integer.MIN_VALUE;
	@Unique
	private int airBarTweaks$transitionFromBubbleCount = Integer.MIN_VALUE;
	@Unique
	private int airBarTweaks$transitionToBubbleCount = Integer.MIN_VALUE;
	@Unique
	private int airBarTweaks$transitionStartTick = Integer.MIN_VALUE;
	@Unique
	private static final boolean AIR_BAR_TWEAKS_DEBUG_ANIMATION = Boolean.getBoolean("airBarTweaks.debugAirAnimation")
			|| "true".equalsIgnoreCase(System.getenv("AIR_BAR_TWEAKS_DEBUG_AIR_ANIMATION"));
	@Unique
	private static final int AIR_BAR_TWEAKS_POPPING_TICKS = 2;
	@Unique
	private static final int AIR_BAR_TWEAKS_BLANK_TICKS = 1;
	@Unique
	private static final int AIR_BAR_TWEAKS_PHASE_STABLE = 0;
	@Unique
	private static final int AIR_BAR_TWEAKS_PHASE_POPPING = 1;
	@Unique
	private static final int AIR_BAR_TWEAKS_PHASE_BLANK = 2;

	@Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
	private void airBarTweaks$hideFullAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right, CallbackInfo callbackInfo) {
		if (AirBarTweaksConfig.get().visibilityMode() == AirBarVisibilityMode.WHEN_NOT_FULL && airIsFull(player)) {
			if (AirBarTweaksConfig.get().smoothAirBarAnimation()) {
				airBarTweaks$resetAirAnimation(player, player.getAirSupply(), player.getMaxAirSupply());
			}

			callbackInfo.cancel();
			return;
		}

		if (AirBarTweaksConfig.get().smoothAirBarAnimation() && airBarTweaks$renderSmoothAirBar(guiGraphics, player, vehicleHearts, y, right)) {
			callbackInfo.cancel();
		}
	}

	@ModifyVariable(method = "renderAirBubbles", at = @At(value = "STORE"), ordinal = 0)
	private boolean airBarTweaks$showFullAirBar(boolean inWater, GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right) {
		if (AirBarTweaksConfig.get().visibilityMode() == AirBarVisibilityMode.ALWAYS && airIsFull(player)) {
			return true;
		}

		return inWater;
	}

	private static boolean airIsFull(Player player) {
		int maxAir = player.getMaxAirSupply();
		return maxAir <= 0 || player.getAirSupply() >= maxAir;
	}

	@Unique
	private boolean airBarTweaks$renderSmoothAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right) {
		int maxAir = player.getMaxAirSupply();
		int actualAir = maxAir <= 0 ? player.getAirSupply() : airBarTweaks$clampAir(player.getAirSupply(), maxAir);
		boolean inWater = player.isEyeInFluid(FluidTags.WATER);
		boolean shouldRender = inWater || actualAir < maxAir || AirBarTweaksConfig.get().visibilityMode() == AirBarVisibilityMode.ALWAYS;

		if (maxAir <= 0 || !shouldRender) {
			airBarTweaks$resetAirAnimation(player, actualAir, maxAir);
			return false;
		}

		int playerId = player.getId();
		int currentBubbleCount = airBarTweaks$getAirBubbleCount(actualAir, maxAir, 0);
		int targetVisualBubbleCount = airBarTweaks$getAirBubbleCount(actualAir, maxAir, -2);

		if (playerId != airBarTweaks$lastPlayerId || maxAir != airBarTweaks$lastMaxAir || airBarTweaks$lastActualAir == Integer.MIN_VALUE) {
			airBarTweaks$lastPlayerId = playerId;
			airBarTweaks$lastMaxAir = maxAir;
			airBarTweaks$lastActualAir = actualAir;
			airBarTweaks$lastVisualBubbleCount = targetVisualBubbleCount;
			airBarTweaks$clearTransition();
			airBarTweaks$renderStableAirBar(guiGraphics, vehicleHearts, y, right, targetVisualBubbleCount);
			return true;
		}

		if (actualAir > airBarTweaks$lastActualAir && airBarTweaks$transitionStartTick != Integer.MIN_VALUE) {
			if (currentBubbleCount >= airBarTweaks$transitionFromBubbleCount) {
				airBarTweaks$lastPlayerId = playerId;
				airBarTweaks$lastMaxAir = maxAir;
				airBarTweaks$lastActualAir = actualAir;
				airBarTweaks$lastVisualBubbleCount = currentBubbleCount;
				airBarTweaks$clearTransition();
				airBarTweaks$renderStableAirBar(guiGraphics, vehicleHearts, y, right, currentBubbleCount);
				return true;
			}

			if (currentBubbleCount > airBarTweaks$transitionToBubbleCount) {
				airBarTweaks$transitionToBubbleCount = currentBubbleCount;
			}
		}

		if (actualAir >= maxAir || (actualAir > airBarTweaks$lastActualAir && airBarTweaks$transitionStartTick == Integer.MIN_VALUE)) {
			airBarTweaks$lastPlayerId = playerId;
			airBarTweaks$lastMaxAir = maxAir;
			airBarTweaks$lastActualAir = actualAir;
			airBarTweaks$lastVisualBubbleCount = currentBubbleCount;
			airBarTweaks$clearTransition();
			airBarTweaks$renderStableAirBar(guiGraphics, vehicleHearts, y, right, currentBubbleCount);
			return true;
		}

		if (targetVisualBubbleCount < airBarTweaks$lastVisualBubbleCount
				&& (airBarTweaks$transitionStartTick == Integer.MIN_VALUE || targetVisualBubbleCount < airBarTweaks$transitionToBubbleCount)) {
			airBarTweaks$startTransition(player, actualAir, maxAir, targetVisualBubbleCount);
		}

		airBarTweaks$lastPlayerId = playerId;
		airBarTweaks$lastMaxAir = maxAir;
		airBarTweaks$lastActualAir = actualAir;

		int phase = airBarTweaks$getTransitionPhase();
		if (phase == AIR_BAR_TWEAKS_PHASE_STABLE) {
			airBarTweaks$renderStableAirBar(guiGraphics, vehicleHearts, y, right, airBarTweaks$lastVisualBubbleCount);
			return true;
		}

		airBarTweaks$renderTransitionAirBar(guiGraphics, vehicleHearts, y, right, phase);
		airBarTweaks$debugAirAnimation(phase == AIR_BAR_TWEAKS_PHASE_POPPING ? "popping" : "blank", player, actualAir, maxAir);
		return true;
	}

	@Unique
	private void airBarTweaks$resetAirAnimation(Player player, int actualAir, int maxAir) {
		airBarTweaks$lastPlayerId = player.getId();
		airBarTweaks$lastMaxAir = maxAir;
		airBarTweaks$lastActualAir = actualAir;
		airBarTweaks$lastVisualBubbleCount = maxAir <= 0 ? 10 : airBarTweaks$getAirBubbleCount(airBarTweaks$clampAir(actualAir, maxAir), maxAir, -2);
		airBarTweaks$clearTransition();
	}

	@Unique
	private void airBarTweaks$startTransition(Player player, int actualAir, int maxAir, int targetVisualBubbleCount) {
		airBarTweaks$transitionFromBubbleCount = airBarTweaks$lastVisualBubbleCount;
		airBarTweaks$transitionToBubbleCount = targetVisualBubbleCount;
		airBarTweaks$transitionStartTick = tickCount;
		airBarTweaks$debugAirAnimation("start", player, actualAir, maxAir);
	}

	@Unique
	private int airBarTweaks$getTransitionPhase() {
		if (airBarTweaks$transitionStartTick == Integer.MIN_VALUE) {
			return AIR_BAR_TWEAKS_PHASE_STABLE;
		}

		int elapsedTicks = tickCount - airBarTweaks$transitionStartTick;
		if (elapsedTicks < AIR_BAR_TWEAKS_POPPING_TICKS) {
			return AIR_BAR_TWEAKS_PHASE_POPPING;
		}

		if (elapsedTicks < AIR_BAR_TWEAKS_POPPING_TICKS + AIR_BAR_TWEAKS_BLANK_TICKS) {
			return AIR_BAR_TWEAKS_PHASE_BLANK;
		}

		airBarTweaks$lastVisualBubbleCount = airBarTweaks$transitionToBubbleCount;
		airBarTweaks$clearTransition();
		return AIR_BAR_TWEAKS_PHASE_STABLE;
	}

	@Unique
	private void airBarTweaks$clearTransition() {
		airBarTweaks$transitionFromBubbleCount = Integer.MIN_VALUE;
		airBarTweaks$transitionToBubbleCount = Integer.MIN_VALUE;
		airBarTweaks$transitionStartTick = Integer.MIN_VALUE;
	}

	@Unique
	private void airBarTweaks$renderStableAirBar(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, int bubbleCount) {
		airBarTweaks$renderAirBar(guiGraphics, vehicleHearts, y, right, bubbleCount, bubbleCount, AIR_BAR_TWEAKS_PHASE_STABLE);
	}

	@Unique
	private void airBarTweaks$renderTransitionAirBar(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, int phase) {
		airBarTweaks$renderAirBar(guiGraphics, vehicleHearts, y, right, airBarTweaks$transitionFromBubbleCount, airBarTweaks$transitionToBubbleCount, phase);
	}

	@Unique
	private void airBarTweaks$renderAirBar(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, int fromBubbleCount, int toBubbleCount, int phase) {
		int airY = getAirBubbleYLine(vehicleHearts, y);
		for (int bubble = 1; bubble <= 10; bubble++) {
			Identifier sprite = airBarTweaks$getBubbleSprite(bubble, fromBubbleCount, toBubbleCount, phase);
			if (sprite == null) {
				continue;
			}

			int airX = right - (bubble - 1) * 8 - 9;
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, airX, airY, 9, 9);
		}
	}

	@Unique
	private Identifier airBarTweaks$getBubbleSprite(int bubble, int fromBubbleCount, int toBubbleCount, int phase) {
		if (phase == AIR_BAR_TWEAKS_PHASE_STABLE) {
			return bubble <= toBubbleCount ? AIR_SPRITE : AIR_EMPTY_SPRITE;
		}

		if (bubble <= toBubbleCount) {
			return AIR_SPRITE;
		}

		if (bubble <= fromBubbleCount) {
			return phase == AIR_BAR_TWEAKS_PHASE_POPPING ? AIR_POPPING_SPRITE : null;
		}

		return AIR_EMPTY_SPRITE;
	}

	@Unique
	private static int airBarTweaks$clampAir(int air, int maxAir) {
		return Math.max(0, Math.min(air, maxAir));
	}

	@Unique
	private static int airBarTweaks$getAirBubbleCount(int air, int maxAir, int offset) {
		int count = (int) Math.ceil((double) (air + offset) * 10.0D / maxAir);
		return Math.max(0, Math.min(count, 10));
	}

	@Unique
	private void airBarTweaks$debugAirAnimation(String phase, Player player, int actualAir, int maxAir) {
		if (!AIR_BAR_TWEAKS_DEBUG_ANIMATION) {
			return;
		}

		AirBarTweaksClient.LOGGER.info(
				"Air animation phase={}, tick={}, player={}, actualAir={}, maxAir={}, lastActualAir={}, lastVisualBubbleCount={}, transitionFrom={}, transitionTo={}, inWater={}",
				phase,
				tickCount,
				player.getId(),
				actualAir,
				maxAir,
				airBarTweaks$lastActualAir,
				airBarTweaks$lastVisualBubbleCount,
				airBarTweaks$transitionFromBubbleCount,
				airBarTweaks$transitionToBubbleCount,
				player.isEyeInFluid(FluidTags.WATER)
		);
	}
}
