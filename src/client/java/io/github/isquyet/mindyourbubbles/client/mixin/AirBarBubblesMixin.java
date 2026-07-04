package io.github.isquyet.mindyourbubbles.client.mixin;

import io.github.isquyet.mindyourbubbles.client.MindYourBubblesConfig;
import io.github.isquyet.mindyourbubbles.client.AirBarVisibilityMode;
import io.github.isquyet.mindyourbubbles.client.air.AirBarAnimationPhase;
import io.github.isquyet.mindyourbubbles.client.air.AirBarAnimationState;
import io.github.isquyet.mindyourbubbles.client.air.AirBarMath;
import io.github.isquyet.mindyourbubbles.client.air.AirBarPolicy;
import io.github.isquyet.mindyourbubbles.client.air.AirBarRenderFrame;
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
	private final AirBarAnimationState mindYourBubbles$animationState = new AirBarAnimationState();

	@Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
	private void mindYourBubbles$hideFullAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right, CallbackInfo callbackInfo) {
		MindYourBubblesConfig config = MindYourBubblesConfig.get();
		AirBarVisibilityMode visibilityMode = config.visibilityMode();
		boolean smoothAirBarAnimation = config.smoothAirBarAnimation();
		int maxAir = player.getMaxAirSupply();
		int actualAir = player.getAirSupply();
		boolean airIsFull = AirBarMath.isAirFull(actualAir, maxAir);
		boolean inWater = player.isEyeInFluid(FluidTags.WATER);

		if (AirBarPolicy.shouldHideFullAirBar(visibilityMode, airIsFull)) {
			if (smoothAirBarAnimation) {
				mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			}

			callbackInfo.cancel();
			return;
		}

		if (smoothAirBarAnimation && mindYourBubbles$renderSmoothAirBar(guiGraphics, player, vehicleHearts, y, right, visibilityMode, inWater)) {
			callbackInfo.cancel();
			return;
		}

		if (maxAir > 0 && AirBarPolicy.shouldForceFullAirBar(visibilityMode, airIsFull, inWater)) {
			mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			mindYourBubbles$renderAirBarFrame(guiGraphics, vehicleHearts, y, right, AirBarRenderFrame.stable(AirBarMath.FULL_BUBBLE_COUNT));
			callbackInfo.cancel();
		}
	}

	@Unique
	private boolean mindYourBubbles$renderSmoothAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right, AirBarVisibilityMode visibilityMode, boolean inWater) {
		int maxAir = player.getMaxAirSupply();
		int actualAir = maxAir <= 0 ? player.getAirSupply() : AirBarMath.clampAir(player.getAirSupply(), maxAir);
		boolean shouldRender = AirBarPolicy.shouldRenderAirBar(visibilityMode, inWater, actualAir, maxAir);

		if (maxAir <= 0 || !shouldRender) {
			mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			return false;
		}

		AirBarRenderFrame renderFrame = mindYourBubbles$animationState.update(player.getId(), actualAir, maxAir, tickCount);
		mindYourBubbles$renderAirBarFrame(guiGraphics, vehicleHearts, y, right, renderFrame);
		return true;
	}

	@Unique
	private void mindYourBubbles$resetAirAnimation(Player player, int actualAir, int maxAir) {
		mindYourBubbles$animationState.reset(player.getId(), actualAir, maxAir);
	}

	@Unique
	private void mindYourBubbles$renderAirBarFrame(GuiGraphics guiGraphics, int vehicleHearts, int y, int right, AirBarRenderFrame renderFrame) {
		int airY = getAirBubbleYLine(vehicleHearts, y);
		for (int bubble = 1; bubble <= AirBarMath.FULL_BUBBLE_COUNT; bubble++) {
			Identifier sprite = mindYourBubbles$getBubbleSprite(bubble, renderFrame);
			if (sprite == null) {
				continue;
			}

			int airX = right - (bubble - 1) * 8 - 9;
			guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, airX, airY, 9, 9);
		}
	}

	@Unique
	private Identifier mindYourBubbles$getBubbleSprite(int bubble, AirBarRenderFrame renderFrame) {
		if (renderFrame.phase() == AirBarAnimationPhase.STABLE) {
			return bubble <= renderFrame.toBubbleCount() ? AIR_SPRITE : AIR_EMPTY_SPRITE;
		}

		if (bubble <= renderFrame.toBubbleCount()) {
			return AIR_SPRITE;
		}

		if (bubble <= renderFrame.fromBubbleCount()) {
			return renderFrame.phase() == AirBarAnimationPhase.POPPING ? AIR_POPPING_SPRITE : null;
		}

		return AIR_EMPTY_SPRITE;
	}

}
