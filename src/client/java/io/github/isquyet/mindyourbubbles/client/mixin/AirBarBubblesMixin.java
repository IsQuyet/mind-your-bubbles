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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class AirBarBubblesMixin {
	@Shadow
	private int tickCount;

	@Shadow
	private Player getCameraPlayer() {
		throw new AssertionError();
	}

	@Shadow
	private LivingEntity getPlayerVehicleWithHealth() {
		throw new AssertionError();
	}

	@Shadow
	private int getVehicleMaxHearts(LivingEntity vehicle) {
		throw new AssertionError();
	}

	@Shadow
	private int getVisibleVehicleHeartRows(int vehicleHearts) {
		throw new AssertionError();
	}

	@Shadow
	@Final
	private static ResourceLocation AIR_SPRITE;

	@Shadow
	@Final
	private static ResourceLocation AIR_BURSTING_SPRITE;

	@Unique
	private final AirBarAnimationState mindYourBubbles$animationState = new AirBarAnimationState();

	@Unique
	private Player mindYourBubbles$currentPlayer;

	@Unique
	private AirBarVisibilityMode mindYourBubbles$currentVisibilityMode = AirBarVisibilityMode.VANILLA;

	@Unique
	private boolean mindYourBubbles$currentSmoothAirBarAnimation;

	@Unique
	private boolean mindYourBubbles$currentAirIsFull;

	@Unique
	private boolean mindYourBubbles$currentInWater;

	@Unique
	private boolean mindYourBubbles$shouldSkipVanillaAirBar;

	@Unique
	private boolean mindYourBubbles$shouldRenderCustomAirBar;

	@Unique
	private boolean mindYourBubbles$renderedCustomAirBar;

	@Inject(method = "renderPlayerHealth", at = @At("HEAD"))
	private void mindYourBubbles$prepareAirBarRender(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
		Player player = getCameraPlayer();
		mindYourBubbles$currentPlayer = player;
		mindYourBubbles$shouldSkipVanillaAirBar = false;
		mindYourBubbles$shouldRenderCustomAirBar = false;
		mindYourBubbles$renderedCustomAirBar = false;

		if (player == null) {
			return;
		}

		MindYourBubblesConfig config = MindYourBubblesConfig.get();
		mindYourBubbles$currentVisibilityMode = config.visibilityMode();
		mindYourBubbles$currentSmoothAirBarAnimation = config.smoothAirBarAnimation();

		int maxAir = player.getMaxAirSupply();
		int actualAir = player.getAirSupply();
		mindYourBubbles$currentAirIsFull = AirBarMath.isAirFull(actualAir, maxAir);
		mindYourBubbles$currentInWater = player.isEyeInFluid(FluidTags.WATER);

		if (AirBarPolicy.shouldHideFullAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentAirIsFull)) {
			if (mindYourBubbles$currentSmoothAirBarAnimation) {
				mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			}

			mindYourBubbles$shouldSkipVanillaAirBar = true;
			return;
		}

		if (mindYourBubbles$currentSmoothAirBarAnimation
				&& maxAir > 0
				&& AirBarPolicy.shouldRenderAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentInWater, actualAir, maxAir)) {
			mindYourBubbles$shouldSkipVanillaAirBar = true;
			mindYourBubbles$shouldRenderCustomAirBar = true;
		}
	}

	@Redirect(
			method = "renderPlayerHealth",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
			)
	)
	private void mindYourBubbles$redirectAirBubbleSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height) {
		if (!mindYourBubbles$isAirBubbleSprite(sprite)) {
			guiGraphics.blitSprite(sprite, x, y, width, height);
			return;
		}

		if (!mindYourBubbles$shouldSkipVanillaAirBar) {
			guiGraphics.blitSprite(sprite, x, y, width, height);
			return;
		}

		if (mindYourBubbles$shouldRenderCustomAirBar && !mindYourBubbles$renderedCustomAirBar && mindYourBubbles$currentPlayer != null) {
			mindYourBubbles$renderSmoothAirBar(guiGraphics, mindYourBubbles$currentPlayer, x + width, y);
			mindYourBubbles$renderedCustomAirBar = true;
		}
	}

	@Inject(method = "renderPlayerHealth", at = @At("TAIL"))
	private void mindYourBubbles$renderForcedAirBar(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
		if (mindYourBubbles$currentPlayer == null) {
			mindYourBubbles$clearAirBarRenderState();
			return;
		}

		int maxAir = mindYourBubbles$currentPlayer.getMaxAirSupply();
		if (!mindYourBubbles$renderedCustomAirBar && mindYourBubbles$shouldRenderCustomAirBar) {
			mindYourBubbles$renderSmoothAirBar(guiGraphics, mindYourBubbles$currentPlayer, mindYourBubbles$getAirBubbleRight(guiGraphics), mindYourBubbles$getAirBubbleY(guiGraphics));
		} else if (maxAir > 0 && AirBarPolicy.shouldForceFullAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentAirIsFull, mindYourBubbles$currentInWater)) {
			mindYourBubbles$resetAirAnimation(mindYourBubbles$currentPlayer, mindYourBubbles$currentPlayer.getAirSupply(), maxAir);
			mindYourBubbles$renderAirBarFrame(guiGraphics, mindYourBubbles$getAirBubbleRight(guiGraphics), mindYourBubbles$getAirBubbleY(guiGraphics), AirBarRenderFrame.stable(AirBarMath.FULL_BUBBLE_COUNT));
		}

		mindYourBubbles$clearAirBarRenderState();
	}

	@Unique
	private boolean mindYourBubbles$renderSmoothAirBar(GuiGraphics guiGraphics, Player player, int right, int airY) {
		int maxAir = player.getMaxAirSupply();
		int actualAir = maxAir <= 0 ? player.getAirSupply() : AirBarMath.clampAir(player.getAirSupply(), maxAir);
		boolean shouldRender = AirBarPolicy.shouldRenderAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentInWater, actualAir, maxAir);

		if (maxAir <= 0 || !shouldRender) {
			mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			return false;
		}

		AirBarRenderFrame renderFrame = mindYourBubbles$animationState.update(player.getId(), actualAir, maxAir, tickCount);
		mindYourBubbles$renderAirBarFrame(guiGraphics, right, airY, renderFrame);
		return true;
	}

	@Unique
	private void mindYourBubbles$resetAirAnimation(Player player, int actualAir, int maxAir) {
		mindYourBubbles$animationState.reset(player.getId(), actualAir, maxAir);
	}

	@Unique
	private void mindYourBubbles$renderAirBarFrame(GuiGraphics guiGraphics, int right, int airY, AirBarRenderFrame renderFrame) {
		for (int bubble = 1; bubble <= AirBarMath.FULL_BUBBLE_COUNT; bubble++) {
			ResourceLocation sprite = mindYourBubbles$getBubbleSprite(bubble, renderFrame);
			if (sprite == null) {
				continue;
			}

			int airX = right - (bubble - 1) * 8 - 9;
			guiGraphics.blitSprite(sprite, airX, airY, 9, 9);
		}
	}

	@Unique
	private ResourceLocation mindYourBubbles$getBubbleSprite(int bubble, AirBarRenderFrame renderFrame) {
		if (renderFrame.phase() == AirBarAnimationPhase.STABLE) {
			return bubble <= renderFrame.toBubbleCount() ? AIR_SPRITE : null;
		}

		if (bubble <= renderFrame.toBubbleCount()) {
			return AIR_SPRITE;
		}

		if (bubble <= renderFrame.fromBubbleCount()) {
			return renderFrame.phase() == AirBarAnimationPhase.POPPING ? AIR_BURSTING_SPRITE : null;
		}

		return null;
	}

	@Unique
	private boolean mindYourBubbles$isAirBubbleSprite(ResourceLocation sprite) {
		return AIR_SPRITE.equals(sprite) || AIR_BURSTING_SPRITE.equals(sprite);
	}

	@Unique
	private int mindYourBubbles$getAirBubbleRight(GuiGraphics guiGraphics) {
		return guiGraphics.guiWidth() / 2 + 91;
	}

	@Unique
	private int mindYourBubbles$getAirBubbleY(GuiGraphics guiGraphics) {
		int airY = guiGraphics.guiHeight() - 49;
		LivingEntity vehicle = getPlayerVehicleWithHealth();
		int vehicleHearts = getVehicleMaxHearts(vehicle);
		if (vehicleHearts > 0) {
			airY -= getVisibleVehicleHeartRows(vehicleHearts) * 10;
		}

		return airY;
	}

	@Unique
	private void mindYourBubbles$clearAirBarRenderState() {
		mindYourBubbles$currentPlayer = null;
		mindYourBubbles$currentVisibilityMode = AirBarVisibilityMode.VANILLA;
		mindYourBubbles$currentSmoothAirBarAnimation = false;
		mindYourBubbles$currentAirIsFull = false;
		mindYourBubbles$currentInWater = false;
		mindYourBubbles$shouldSkipVanillaAirBar = false;
		mindYourBubbles$shouldRenderCustomAirBar = false;
		mindYourBubbles$renderedCustomAirBar = false;
	}

}
