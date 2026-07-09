package io.github.isquyet.mindyourbubbles.client.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAirSupplyAccessor {
	@Accessor("DATA_AIR_SUPPLY_ID")
	static EntityDataAccessor<Integer> mindYourBubbles$getDataAirSupplyId() {
		throw new AssertionError();
	}
}
