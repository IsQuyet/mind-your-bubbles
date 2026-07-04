package io.github.isquyet.mindyourbubbles.client.air;

public final class AirBarAnimationState {
	private static final int POPPING_TICKS = 2;
	private static final int BLANK_TICKS = 1;

	private int lastPlayerId = Integer.MIN_VALUE;
	private int lastMaxAir = Integer.MIN_VALUE;
	private int lastActualAir = Integer.MIN_VALUE;
	private int lastVisualBubbleCount = Integer.MIN_VALUE;
	private int transitionFromBubbleCount = Integer.MIN_VALUE;
	private int transitionToBubbleCount = Integer.MIN_VALUE;
	private int transitionStartTick = Integer.MIN_VALUE;

	public AirBarRenderFrame update(int playerId, int actualAir, int maxAir, int currentTick) {
		int currentBubbleCount = AirBarMath.getAirBubbleCount(actualAir, maxAir, 0);
		int targetVisualBubbleCount = AirBarMath.getAirBubbleCount(actualAir, maxAir, -2);

		if (playerId != lastPlayerId || maxAir != lastMaxAir || lastActualAir == Integer.MIN_VALUE) {
			lastPlayerId = playerId;
			lastMaxAir = maxAir;
			lastActualAir = actualAir;
			lastVisualBubbleCount = targetVisualBubbleCount;
			clearTransition();
			return AirBarRenderFrame.stable(targetVisualBubbleCount);
		}

		if (actualAir > lastActualAir && transitionStartTick != Integer.MIN_VALUE) {
			if (currentBubbleCount >= transitionFromBubbleCount) {
				lastPlayerId = playerId;
				lastMaxAir = maxAir;
				lastActualAir = actualAir;
				lastVisualBubbleCount = currentBubbleCount;
				clearTransition();
				return AirBarRenderFrame.stable(currentBubbleCount);
			}

			if (currentBubbleCount > transitionToBubbleCount) {
				transitionToBubbleCount = currentBubbleCount;
			}
		}

		if (actualAir >= maxAir || (actualAir > lastActualAir && transitionStartTick == Integer.MIN_VALUE)) {
			lastPlayerId = playerId;
			lastMaxAir = maxAir;
			lastActualAir = actualAir;
			lastVisualBubbleCount = currentBubbleCount;
			clearTransition();
			return AirBarRenderFrame.stable(currentBubbleCount);
		}

		if (targetVisualBubbleCount < lastVisualBubbleCount
				&& (transitionStartTick == Integer.MIN_VALUE || targetVisualBubbleCount < transitionToBubbleCount)) {
			startTransition(targetVisualBubbleCount, currentTick);
		}

		lastPlayerId = playerId;
		lastMaxAir = maxAir;
		lastActualAir = actualAir;

		AirBarAnimationPhase phase = getTransitionPhase(currentTick);
		if (phase == AirBarAnimationPhase.STABLE) {
			return AirBarRenderFrame.stable(lastVisualBubbleCount);
		}

		return AirBarRenderFrame.transition(transitionFromBubbleCount, transitionToBubbleCount, phase);
	}

	public void reset(int playerId, int actualAir, int maxAir) {
		lastPlayerId = playerId;
		lastMaxAir = maxAir;
		lastActualAir = actualAir;
		lastVisualBubbleCount = maxAir <= 0
				? AirBarMath.FULL_BUBBLE_COUNT
				: AirBarMath.getAirBubbleCount(AirBarMath.clampAir(actualAir, maxAir), maxAir, -2);
		clearTransition();
	}

	private void startTransition(int targetVisualBubbleCount, int currentTick) {
		transitionFromBubbleCount = lastVisualBubbleCount;
		transitionToBubbleCount = targetVisualBubbleCount;
		transitionStartTick = currentTick;
	}

	private AirBarAnimationPhase getTransitionPhase(int currentTick) {
		if (transitionStartTick == Integer.MIN_VALUE) {
			return AirBarAnimationPhase.STABLE;
		}

		int elapsedTicks = currentTick - transitionStartTick;
		if (elapsedTicks < POPPING_TICKS) {
			return AirBarAnimationPhase.POPPING;
		}

		if (elapsedTicks < POPPING_TICKS + BLANK_TICKS) {
			return AirBarAnimationPhase.BLANK;
		}

		lastVisualBubbleCount = transitionToBubbleCount;
		clearTransition();
		return AirBarAnimationPhase.STABLE;
	}

	private void clearTransition() {
		transitionFromBubbleCount = Integer.MIN_VALUE;
		transitionToBubbleCount = Integer.MIN_VALUE;
		transitionStartTick = Integer.MIN_VALUE;
	}
}
