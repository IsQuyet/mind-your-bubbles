package io.github.isquyet.airbartweaks.client.mixin;

import io.github.isquyet.airbartweaks.client.AirBarTweaksConfig;
import io.github.isquyet.airbartweaks.client.AirBarVisibilityMode;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class AirBarBubblesMixin {
	@Inject(method = "renderAirBubbles", at = @At("HEAD"), cancellable = true)
	private void airBarTweaks$hideFullAirBar(GuiGraphics guiGraphics, Player player, int vehicleHearts, int y, int right, CallbackInfo callbackInfo) {
		if (AirBarTweaksConfig.get().visibilityMode() == AirBarVisibilityMode.WHEN_NOT_FULL && airIsFull(player)) {
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
}
