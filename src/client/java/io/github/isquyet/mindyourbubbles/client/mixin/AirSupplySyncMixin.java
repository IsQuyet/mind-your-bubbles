package io.github.isquyet.mindyourbubbles.client.mixin;

import io.github.isquyet.mindyourbubbles.client.air.AirSupplySyncState;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class AirSupplySyncMixin {
	@Inject(method = "handleSetEntityData", at = @At("HEAD"))
	private void mindYourBubbles$rememberServerAirSupply(ClientboundSetEntityDataPacket packet, CallbackInfo callbackInfo) {
		EntityDataAccessor<Integer> airSupplyAccessor = EntityAirSupplyAccessor.mindYourBubbles$getDataAirSupplyId();
		for (SynchedEntityData.DataValue<?> syncedEntityDataValue : packet.packedItems()) {
			if (syncedEntityDataValue.id() != airSupplyAccessor.id()) {
				continue;
			}

			Object syncedValue = syncedEntityDataValue.value();
			if (syncedValue instanceof Integer syncedAirSupply) {
				AirSupplySyncState.rememberAirSupply(packet.id(), syncedAirSupply);
			}
		}
	}

	@Inject(method = "clearLevel", at = @At("HEAD"))
	private void mindYourBubbles$clearServerAirSupplies(CallbackInfo callbackInfo) {
		AirSupplySyncState.clear();
	}
}
