package io.github.isquyet.mindyourbubbles.client.mixin;

import io.github.isquyet.mindyourbubbles.client.MindYourBubblesConfig;
import io.github.isquyet.mindyourbubbles.client.AirBarVisibilityMode;
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
	private int mindYourBubbles$lastPlayerId = Integer.MIN_VALUE;
	@Unique
	private int mindYourBubbles$lastMaxAir = Integer.MIN_VALUE;
	@Unique
	private int mindYourBubbles$lastActualAir = Integer.MIN_VALUE;
	@Unique
	private int mindYourBubbles$lastVisualBubbleCount = Integer.MIN_VALUE;
	@Unique
	private int mindYourBubbles$transitionFromBubbleCount = Integer.MIN_VALUE;
	@Unique
	private int mindYourBubbles$transitionToBubbleCount = Integer.MIN_VALUE;
	@Unique
	private int mindYourBubbles$transitionStartTick = Integer.MIN_VALUE;
	@Unique
	private static final int MIND_YOUR_BUBBLES_POPPING_TICKS = 2;
	@Unique
	private static final int MIND_YOUR_BUBBLES_BLANK_TICKS = 1;
	@Unique
	private static final int MIND_YOUR_BUBBLES_PHASE_STABLE = 0;
	@Unique
	private static final int MIND_YOUR_BUBBLES_PHASE_POPPING = 1;
	@Unique
	private static final int MIND_YOUR_BUBBLES_PHASE_BLANK = 2;

	@Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
	private void mindYourBubbles$hideFullAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right, CallbackInfo callbackInfo) {
		if (MindYourBubblesConfig.get().visibilityMode() == AirBarVisibilityMode.WHEN_NOT_FULL && airIsFull(player)) {
			if (MindYourBubblesConfig.get().smoothAirBarAnimation()) {
				mindYourBubbles$resetAirAnimation(player, player.getAirSupply(), player.getMaxAirSupply());
			}

			callbackInfo.cancel();
			return;
		}

		if (MindYourBubblesConfig.get().smoothAirBarAnimation() && mindYourBubbles$renderSmoothAirBar(guiGraphics, player, vehicleHearts, y, right)) {
			callbackInfo.cancel();
		}
	}

	@ModifyVariable(method = "renderAirBubbles", at = @At(value = "STORE"), ordinal = 0)
	private boolean mindYourBubbles$showFullAirBar(boolean inWater, GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right) {
		if (MindYourBubblesConfig.get().visibilityMode() == AirBarVisibilityMode.ALWAYS && airIsFull(player)) {
			return true;
		}

		return inWater;
	}

	private static boolean airIsFull(Player player) {
		int maxAir = player.getMaxAirSupply();
		return maxAir <= 0 || player.getAirSupply() >= maxAir;
	}

	@Unique
	private boolean mindYourBubbles$renderSmoothAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right) {
		int maxAir = player.getMaxAirSupply();
		int actualAir = maxAir <= 0 ? player.getAirSupply() : mindYourBubbles$clampAir(player.getAirSupply(), maxAir);
		boolean inWater = player.isEyeInFluid(FluidTags.WATER);
		boolean shouldRender = inWater || actualAir < maxAir || MindYourBubblesConfig.get().visibilityMode() == AirBarVisibilityMode.ALWAYS;

		if (maxAir <= 0 || !shouldRender) {
			mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			return false;
		}

		int playerId = player.getId();
		int currentBubbleCount = mindYourBubbles$getAirBubbleCount(actualAir, maxAir, 0);
		int targetVisualBubbleCount = mindYourBubbles$getAirBubbleCount(actualAir, maxAir, -2);

		if (playerId != mindYourBubbles$lastPlayerId || maxAir != mindYourBubbles$lastMaxAir || mindYourBubbles$lastActualAir == Integer.MIN_VALUE) {
			mindYourBubbles$lastPlayerId = playerId;
			mindYourBubbles$lastMaxAir = maxAir;
			mindYourBubbles$lastActualAir = actualAir;
			mindYourBubbles$lastVisualBubbleCount = targetVisualBubbleCount;
			mindYourBubbles$clearTransition();
			mindYourBubbles$renderStableAirBar(guiGraphics, vehicleHearts, y, right, targetVisualBubbleCount);
			return true;
		}

		if (actualAir > mindYourBubbles$lastActualAir && mindYourBubbles$transitionStartTick != Integer.MIN_VALUE) {
			if (currentBubbleCount >= mindYourBubbles$transitionFromBubbleCount) {
				mindYourBubbles$lastPlayerId = playerId;
				mindYourBubbles$lastMaxAir = maxAir;
				mindYourBubbles$lastActualAir = actualAir;
				mindYourBubbles$lastVisualBubbleCount = currentBubbleCount;
				mindYourBubbles$clearTransition();
				mindYourBubbles$renderStableAirBar(guiGraphics, vehicleHearts, y, right, currentBubbleCount);
				return true;
			}

			if (currentBubbleCount > mindYourBubbles$transitionToBubbleCount) {
				mindYourBubbles$transitionToBubbleCount = currentBubbleCount;
			}
		}

		if (actualAir >= maxAir || (actualAir > mindYourBubbles$lastActualAir && mindYourBubbles$transitionStartTick == Integer.MIN_VALUE)) {
			mindYourBubbles$lastPlayerId = playerId;
			mindYourBubbles$lastMaxAir = maxAir;
			mindYourBubbles$lastActualAir = actualAir;
			mindYourBubbles$lastVisualBubbleCount = currentBubbleCount;
			mindYourBubbles$clearTransition();
			mindYourBubbles$renderStableAirBar(guiGraphics, vehicleHearts, y, right, currentBubbleCount);
			return true;
		}

		if (targetVisualBubbleCount < mindYourBubbles$lastVisualBubbleCount
				&& (mindYourBubbles$transitionStartTick == Integer.MIN_VALUE || targetVisualBubbleCount < mindYourBubbles$transitionToBubbleCount)) {
			mindYourBubbles$startTransition(player, actualAir, maxAir, targetVisualBubbleCount);
		}

		mindYourBubbles$lastPlayerId = playerId;
		mindYourBubbles$lastMaxAir = maxAir;
		mindYourBubbles$lastActualAir = actualAir;

		int phase = mindYourBubbles$getTransitionPhase();
		if (phase == MIND_YOUR_BUBBLES_PHASE_STABLE) {
			mindYourBubbles$renderStableAirBar(guiGraphics, vehicleHearts, y, right, mindYourBubbles$lastVisualBubbleCount);
			return true;
		}

		mindYourBubbles$renderTransitionAirBar(guiGraphics, vehicleHearts, y, right, phase);
		return true;
	}

	@Unique
	private void mindYourBubbles$resetAirAnimation(Player player, int actualAir, int maxAir) {
		mindYourBubbles$lastPlayerId = player.getId();
		mindYourBubbles$lastMaxAir = maxAir;
		mindYourBubbles$lastActualAir = actualAir;
		mindYourBubbles$lastVisualBubbleCount = maxAir <= 0 ? 10 : mindYourBubbles$getAirBubbleCount(mindYourBubbles$clampAir(actualAir, maxAir), maxAir, -2);
		mindYourBubbles$clearTransition();
	}

	@Unique
	private void mindYourBubbles$startTransition(Player player, int actualAir, int maxAir, int targetVisualBubbleCount) {
		mindYourBubbles$transitionFromBubbleCount = mindYourBubbles$lastVisualBubbleCount;
		mindYourBubbles$transitionToBubbleCount = targetVisualBubbleCount;
		mindYourBubbles$transitionStartTick = tickCount;
	}

	@Unique
	private int mindYourBubbles$getTransitionPhase() {
		if (mindYourBubbles$transitionStartTick == Integer.MIN_VALUE) {
			return MIND_YOUR_BUBBLES_PHASE_STABLE;
		}

		int elapsedTicks = tickCount - mindYourBubbles$transitionStartTick;
		if (elapsedTicks < MIND_YOUR_BUBBLES_POPPING_TICKS) {
			return MIND_YOUR_BUBBLES_PHASE_POPPING;
		}

		if (elapsedTicks < MIND_YOUR_BUBBLES_POPPING_TICKS + MIND_YOUR_BUBBLES_BLANK_TICKS) {
			return MIND_YOUR_BUBBLES_PHASE_BLANK;
		}

		mindYourBubbles$lastVisualBubbleCount = mindYourBubbles$transitionToBubbleCount;
		mindYourBubbles$clearTransition();
		return MIND_YOUR_BUBBLES_PHASE_STABLE;
	}

	@Unique
	private void mindYourBubbles$clearTransition() {
		mindYourBubbles$transitionFromBubbleCount = Integer.MIN_VALUE;
		mindYourBubbles$transitionToBubbleCount = Integer.MIN_VALUE;
		mindYourBubbles$transitionStartTick = Integer.MIN_VALUE;
	}

	@Unique
	private void mindYourBubbles$renderStableAirBar(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, int bubbleCount) {
		mindYourBubbles$renderAirBar(guiGraphics, vehicleHearts, y, right, bubbleCount, bubbleCount, MIND_YOUR_BUBBLES_PHASE_STABLE);
	}

	@Unique
	private void mindYourBubbles$renderTransitionAirBar(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, int phase) {
		mindYourBubbles$renderAirBar(guiGraphics, vehicleHearts, y, right, mindYourBubbles$transitionFromBubbleCount, mindYourBubbles$transitionToBubbleCount, phase);
	}

	@Unique
	private void mindYourBubbles$renderAirBar(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, int fromBubbleCount, int toBubbleCount, int phase) {
		int airY = getAirBubbleYLine(vehicleHearts, y);
		for (int bubble = 1; bubble <= 10; bubble++) {
			Identifier sprite = mindYourBubbles$getBubbleSprite(bubble, fromBubbleCount, toBubbleCount, phase);
			if (sprite == null) {
				continue;
			}

			int airX = right - (bubble - 1) * 8 - 9;
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, airX, airY, 9, 9);
		}
	}

	@Unique
	private Identifier mindYourBubbles$getBubbleSprite(int bubble, int fromBubbleCount, int toBubbleCount, int phase) {
		if (phase == MIND_YOUR_BUBBLES_PHASE_STABLE) {
			return bubble <= toBubbleCount ? AIR_SPRITE : AIR_EMPTY_SPRITE;
		}

		if (bubble <= toBubbleCount) {
			return AIR_SPRITE;
		}

		if (bubble <= fromBubbleCount) {
			return phase == MIND_YOUR_BUBBLES_PHASE_POPPING ? AIR_POPPING_SPRITE : null;
		}

		return AIR_EMPTY_SPRITE;
	}

	@Unique
	private static int mindYourBubbles$clampAir(int air, int maxAir) {
		return Math.max(0, Math.min(air, maxAir));
	}

	@Unique
	private static int mindYourBubbles$getAirBubbleCount(int air, int maxAir, int offset) {
		int count = (int) Math.ceil((double) (air + offset) * 10.0D / maxAir);
		return Math.max(0, Math.min(count, 10));
	}
}
