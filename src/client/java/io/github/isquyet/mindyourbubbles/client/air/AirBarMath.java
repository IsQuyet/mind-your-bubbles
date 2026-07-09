package io.github.isquyet.mindyourbubbles.client.air;

public final class AirBarMath {
	public static final int FULL_BUBBLE_COUNT = 10;
	public static final int VANILLA_POPPING_AIR_OFFSET = -2;

	private AirBarMath() {
	}

	public static boolean isAirFull(int actualAir, int maxAir) {
		return maxAir <= 0 || actualAir >= maxAir;
	}

	public static int clampAir(int actualAir, int maxAir) {
		return Math.max(0, Math.min(actualAir, maxAir));
	}

	public static int getAirBubbleCount(int actualAir, int maxAir, int offset) {
		if (maxAir <= 0) {
			return FULL_BUBBLE_COUNT;
		}

		int bubbleCount = (int) Math.ceil((double) (actualAir + offset) * FULL_BUBBLE_COUNT / maxAir);
		return Math.max(0, Math.min(bubbleCount, FULL_BUBBLE_COUNT));
	}
}
