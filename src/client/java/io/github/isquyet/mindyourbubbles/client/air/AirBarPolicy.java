package io.github.isquyet.mindyourbubbles.client.air;

import io.github.isquyet.mindyourbubbles.client.AirBarVisibilityMode;

public final class AirBarPolicy {
	private AirBarPolicy() {
	}

	public static boolean shouldHideFullAirBar(AirBarVisibilityMode visibilityMode, boolean airIsFull) {
		return visibilityMode == AirBarVisibilityMode.WHEN_NOT_FULL && airIsFull;
	}

	public static boolean shouldForceFullAirBar(AirBarVisibilityMode visibilityMode, boolean airIsFull, boolean inWater) {
		return visibilityMode == AirBarVisibilityMode.ALWAYS && airIsFull && !inWater;
	}

	public static boolean shouldRenderAirBar(AirBarVisibilityMode visibilityMode, boolean inWater, int actualAir, int maxAir) {
		if (maxAir <= 0) {
			return false;
		}

		return inWater || actualAir < maxAir || visibilityMode == AirBarVisibilityMode.ALWAYS;
	}
}
