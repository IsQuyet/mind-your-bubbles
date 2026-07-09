package io.github.isquyet.mindyourbubbles.client.air;

import java.util.HashMap;
import java.util.Map;

public final class AirSupplySyncState {
	private static final Map<Integer, Integer> SYNCED_AIR_SUPPLIES = new HashMap<>();

	private AirSupplySyncState() {
	}

	public static void rememberAirSupply(int entityId, int airSupply) {
		SYNCED_AIR_SUPPLIES.put(entityId, airSupply);
	}

	public static int getAirSupplyOrDefault(int entityId, int fallbackAirSupply) {
		return SYNCED_AIR_SUPPLIES.getOrDefault(entityId, fallbackAirSupply);
	}

	public static void clear() {
		SYNCED_AIR_SUPPLIES.clear();
	}
}
