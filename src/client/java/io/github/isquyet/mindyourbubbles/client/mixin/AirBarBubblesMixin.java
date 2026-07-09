package io.github.isquyet.mindyourbubbles.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.isquyet.mindyourbubbles.client.MindYourBubblesConfig;
import io.github.isquyet.mindyourbubbles.client.AirBarVisibilityMode;
import io.github.isquyet.mindyourbubbles.client.air.AirBarAnimationPhase;
import io.github.isquyet.mindyourbubbles.client.air.AirBarAnimationState;
import io.github.isquyet.mindyourbubbles.client.air.AirBarMath;
import io.github.isquyet.mindyourbubbles.client.air.AirBarPolicy;
import io.github.isquyet.mindyourbubbles.client.air.AirBarRenderFrame;
import io.github.isquyet.mindyourbubbles.client.air.AirSupplySyncState;
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
	private int mindYourBubbles$currentDisplayedAir;

	@Unique
	private boolean mindYourBubbles$shouldSkipVanillaAirBar;

	@Unique
	private boolean mindYourBubbles$shouldRenderCustomAirBar;

	@Unique
	private boolean mindYourBubbles$observedAirBubbleThisFrame;

	@Unique
	private int mindYourBubbles$observedAirBubbleRight;

	@Unique
	private int mindYourBubbles$observedAirBubbleY;

	@Unique
	private boolean mindYourBubbles$hasLastObservedAirBubblePosition;

	@Unique
	private int mindYourBubbles$lastObservedAirBubbleRight;

	@Unique
	private int mindYourBubbles$lastObservedAirBubbleY;

	@Unique
	private int mindYourBubbles$lastObservedGuiWidth;

	@Unique
	private int mindYourBubbles$lastObservedGuiHeight;

	@Inject(method = "renderPlayerHealth", at = @At("HEAD"))
	private void mindYourBubbles$prepareAirBarRender(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
		Player player = getCameraPlayer();
		mindYourBubbles$currentPlayer = player;
		mindYourBubbles$shouldSkipVanillaAirBar = false;
		mindYourBubbles$shouldRenderCustomAirBar = false;
		mindYourBubbles$observedAirBubbleThisFrame = false;

		if (player == null) {
			return;
		}

		MindYourBubblesConfig config = MindYourBubblesConfig.get();
		mindYourBubbles$currentVisibilityMode = config.visibilityMode();
		mindYourBubbles$currentSmoothAirBarAnimation = config.smoothAirBarAnimation();

		int maxAir = player.getMaxAirSupply();
		int actualAir = mindYourBubbles$getRenderAirSupply(player, maxAir);
		mindYourBubbles$currentInWater = player.isEyeInFluid(FluidTags.WATER);
		mindYourBubbles$currentDisplayedAir = actualAir;
		mindYourBubbles$currentAirIsFull = AirBarMath.isAirFull(mindYourBubbles$currentDisplayedAir, maxAir);

		if (AirBarPolicy.shouldHideFullAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentAirIsFull)) {
			if (mindYourBubbles$currentSmoothAirBarAnimation) {
				mindYourBubbles$resetAirAnimation(player, mindYourBubbles$currentDisplayedAir, maxAir);
			}

			mindYourBubbles$shouldSkipVanillaAirBar = true;
			return;
		}

		if (maxAir > 0 && mindYourBubbles$shouldRenderCustomAirBar(mindYourBubbles$currentDisplayedAir, maxAir)) {
			mindYourBubbles$shouldSkipVanillaAirBar = true;
			mindYourBubbles$shouldRenderCustomAirBar = true;
		}
	}

	@WrapOperation(
			method = "renderPlayerHealth",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
			)
	)
	private void mindYourBubbles$wrapAirBubbleSprite(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> originalOperation) {
		if (!mindYourBubbles$isAirBubbleSprite(sprite)) {
			originalOperation.call(guiGraphics, sprite, x, y, width, height);
			return;
		}

		mindYourBubbles$recordObservedAirBubblePosition(guiGraphics, x, y, width);

		if (!mindYourBubbles$shouldSkipVanillaAirBar) {
			originalOperation.call(guiGraphics, sprite, x, y, width, height);
			return;
		}
	}

	@Inject(method = "renderPlayerHealth", at = @At("TAIL"))
	private void mindYourBubbles$renderForcedAirBar(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
		if (mindYourBubbles$currentPlayer == null) {
			mindYourBubbles$clearAirBarRenderState();
			return;
		}

		int maxAir = mindYourBubbles$currentPlayer.getMaxAirSupply();
		if (mindYourBubbles$shouldRenderCustomAirBar) {
			mindYourBubbles$renderCustomAirBar(guiGraphics, mindYourBubbles$currentPlayer, mindYourBubbles$getAirBubbleRight(guiGraphics), mindYourBubbles$getAirBubbleY(guiGraphics));
		} else if (maxAir > 0 && AirBarPolicy.shouldForceFullAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentAirIsFull, mindYourBubbles$currentInWater)) {
			mindYourBubbles$resetAirAnimation(mindYourBubbles$currentPlayer, mindYourBubbles$currentDisplayedAir, maxAir);
			mindYourBubbles$renderAirBarFrame(guiGraphics, mindYourBubbles$getAirBubbleRight(guiGraphics), mindYourBubbles$getAirBubbleY(guiGraphics), AirBarRenderFrame.stable(AirBarMath.FULL_BUBBLE_COUNT));
		}

		mindYourBubbles$clearAirBarRenderState();
	}

	@Unique
	private boolean mindYourBubbles$renderCustomAirBar(GuiGraphics guiGraphics, Player player, int right, int airY) {
		int maxAir = player.getMaxAirSupply();
		int actualAir = maxAir <= 0 ? player.getAirSupply() : mindYourBubbles$currentDisplayedAir;
		boolean shouldRender = AirBarPolicy.shouldRenderAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentInWater, actualAir, maxAir);

		if (maxAir <= 0 || !shouldRender) {
			mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
			return false;
		}

		AirBarRenderFrame renderFrame = mindYourBubbles$currentSmoothAirBarAnimation
				? mindYourBubbles$animationState.update(player.getId(), actualAir, maxAir, tickCount)
				: mindYourBubbles$getImmediateAirBarFrame(actualAir, maxAir);
		if (!mindYourBubbles$currentSmoothAirBarAnimation) {
			mindYourBubbles$resetAirAnimation(player, actualAir, maxAir);
		}

		mindYourBubbles$renderAirBarFrame(guiGraphics, right, airY, renderFrame);
		return true;
	}

	@Unique
	private boolean mindYourBubbles$shouldRenderCustomAirBar(int actualAir, int maxAir) {
		boolean airBarShouldBeVisible = AirBarPolicy.shouldRenderAirBar(mindYourBubbles$currentVisibilityMode, mindYourBubbles$currentInWater, actualAir, maxAir);
		return airBarShouldBeVisible
				&& (mindYourBubbles$currentSmoothAirBarAnimation || mindYourBubbles$currentVisibilityMode != AirBarVisibilityMode.VANILLA);
	}

	@Unique
	private AirBarRenderFrame mindYourBubbles$getImmediateAirBarFrame(int actualAir, int maxAir) {
		int currentBubbleCount = AirBarMath.getAirBubbleCount(actualAir, maxAir, 0);
		int targetBubbleCount = AirBarMath.getAirBubbleCount(actualAir, maxAir, -2);
		if (targetBubbleCount < currentBubbleCount) {
			return AirBarRenderFrame.transition(currentBubbleCount, targetBubbleCount, AirBarAnimationPhase.POPPING);
		}

		return AirBarRenderFrame.stable(targetBubbleCount);
	}

	@Unique
	private int mindYourBubbles$getRenderAirSupply(Player player, int maxAir) {
		int currentClientAirSupply = player.getAirSupply();
		if (maxAir <= 0) {
			return currentClientAirSupply;
		}

		int renderAirSupply = mindYourBubbles$currentVisibilityMode == AirBarVisibilityMode.VANILLA
				? currentClientAirSupply
				: AirSupplySyncState.getAirSupplyOrDefault(player.getId(), currentClientAirSupply);
		return AirBarMath.clampAir(renderAirSupply, maxAir);
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
	private void mindYourBubbles$recordObservedAirBubblePosition(GuiGraphics guiGraphics, int x, int y, int width) {
		int airBubbleRight = x + width;
		if (!mindYourBubbles$observedAirBubbleThisFrame || airBubbleRight > mindYourBubbles$observedAirBubbleRight) {
			mindYourBubbles$observedAirBubbleThisFrame = true;
			mindYourBubbles$observedAirBubbleRight = airBubbleRight;
			mindYourBubbles$observedAirBubbleY = y;
			mindYourBubbles$hasLastObservedAirBubblePosition = true;
			mindYourBubbles$lastObservedAirBubbleRight = airBubbleRight;
			mindYourBubbles$lastObservedAirBubbleY = y;
			mindYourBubbles$lastObservedGuiWidth = guiGraphics.guiWidth();
			mindYourBubbles$lastObservedGuiHeight = guiGraphics.guiHeight();
		}
	}

	@Unique
	private int mindYourBubbles$getAirBubbleRight(GuiGraphics guiGraphics) {
		if (mindYourBubbles$observedAirBubbleThisFrame) {
			return mindYourBubbles$observedAirBubbleRight;
		}

		if (mindYourBubbles$hasLastObservedAirBubblePosition && mindYourBubbles$lastObservedGuiSizeMatches(guiGraphics)) {
			return mindYourBubbles$lastObservedAirBubbleRight;
		}

		return guiGraphics.guiWidth() / 2 + 91;
	}

	@Unique
	private int mindYourBubbles$getAirBubbleY(GuiGraphics guiGraphics) {
		if (mindYourBubbles$observedAirBubbleThisFrame) {
			return mindYourBubbles$observedAirBubbleY;
		}

		if (mindYourBubbles$hasLastObservedAirBubblePosition && mindYourBubbles$lastObservedGuiSizeMatches(guiGraphics)) {
			return mindYourBubbles$lastObservedAirBubbleY;
		}

		int airY = guiGraphics.guiHeight() - 49;
		LivingEntity vehicle = getPlayerVehicleWithHealth();
		int vehicleHearts = getVehicleMaxHearts(vehicle);
		if (vehicleHearts > 0) {
			airY -= getVisibleVehicleHeartRows(vehicleHearts) * 10;
		}

		return airY;
	}

	@Unique
	private boolean mindYourBubbles$lastObservedGuiSizeMatches(GuiGraphics guiGraphics) {
		return mindYourBubbles$lastObservedGuiWidth == guiGraphics.guiWidth()
				&& mindYourBubbles$lastObservedGuiHeight == guiGraphics.guiHeight();
	}

	@Unique
	private void mindYourBubbles$clearAirBarRenderState() {
		mindYourBubbles$currentPlayer = null;
		mindYourBubbles$currentVisibilityMode = AirBarVisibilityMode.VANILLA;
		mindYourBubbles$currentSmoothAirBarAnimation = false;
		mindYourBubbles$currentAirIsFull = false;
		mindYourBubbles$currentInWater = false;
		mindYourBubbles$currentDisplayedAir = 0;
		mindYourBubbles$shouldSkipVanillaAirBar = false;
		mindYourBubbles$shouldRenderCustomAirBar = false;
		mindYourBubbles$observedAirBubbleThisFrame = false;
	}

}
