package io.github.isquyet.mindyourbubbles.client.air;

public final class AirBarAnimationState {
	private static final int NO_PLAYER_ID = Integer.MIN_VALUE;
	private static final int NO_MAX_AIR = Integer.MIN_VALUE;
	private static final int NO_AIR_VALUE = Integer.MIN_VALUE;
	private static final int NO_BUBBLE_COUNT = Integer.MIN_VALUE;
	private static final int NO_TRANSITION_TICK = Integer.MIN_VALUE;
	private static final int POPPING_TICKS = 2;
	private static final int BLANK_TICKS = 1;

	private int lastPlayerId = NO_PLAYER_ID;
	private int lastMaxAir = NO_MAX_AIR;
	private int lastActualAir = NO_AIR_VALUE;
	private int lastVisualBubbleCount = NO_BUBBLE_COUNT;
	private int transitionFromBubbleCount = NO_BUBBLE_COUNT;
	private int transitionToBubbleCount = NO_BUBBLE_COUNT;
	private int transitionStartTick = NO_TRANSITION_TICK;

	public AirBarRenderFrame update(int playerId, int actualAir, int maxAir, int currentTick) {
		int currentBubbleCount = AirBarMath.getAirBubbleCount(actualAir, maxAir, 0);
		int targetVisualBubbleCount = AirBarMath.getAirBubbleCount(actualAir, maxAir, AirBarMath.VANILLA_POPPING_AIR_OFFSET);

		if (isNewAirContext(playerId, maxAir)) {
			return initializeAirContext(playerId, actualAir, maxAir, currentTick, currentBubbleCount, targetVisualBubbleCount);
		}

		if (isAirRecoveringDuringTransition(actualAir)) {
			if (currentBubbleCount >= transitionFromBubbleCount) {
				return stabilizeAirContext(playerId, actualAir, maxAir, currentBubbleCount);
			}

			if (currentBubbleCount > transitionToBubbleCount) {
				transitionToBubbleCount = currentBubbleCount;
			}
		}

		if (shouldStabilizeRecoveredAir(actualAir, maxAir)) {
			return stabilizeAirContext(playerId, actualAir, maxAir, currentBubbleCount);
		}

		if (shouldStartBubbleLossTransition(targetVisualBubbleCount)) {
			startTransition(targetVisualBubbleCount, currentTick);
		}

		rememberAirContext(playerId, actualAir, maxAir);
		return createCurrentRenderFrame(currentTick);
	}

	public void reset(int playerId, int actualAir, int maxAir) {
		lastPlayerId = playerId;
		lastMaxAir = maxAir;
		lastActualAir = actualAir;
		lastVisualBubbleCount = maxAir <= 0
				? AirBarMath.FULL_BUBBLE_COUNT
				: AirBarMath.getAirBubbleCount(AirBarMath.clampAir(actualAir, maxAir), maxAir, 0);
		clearTransition();
	}

	private boolean isNewAirContext(int playerId, int maxAir) {
		return playerId != lastPlayerId || maxAir != lastMaxAir || lastActualAir == NO_AIR_VALUE;
	}

	private AirBarRenderFrame initializeAirContext(int playerId, int actualAir, int maxAir, int currentTick, int currentBubbleCount, int targetVisualBubbleCount) {
		rememberAirContext(playerId, actualAir, maxAir);
		lastVisualBubbleCount = currentBubbleCount;
		clearTransition();

		if (targetVisualBubbleCount < currentBubbleCount) {
			startTransition(targetVisualBubbleCount, currentTick);
			return AirBarRenderFrame.transition(transitionFromBubbleCount, transitionToBubbleCount, AirBarAnimationPhase.POPPING);
		}

		return AirBarRenderFrame.stable(currentBubbleCount);
	}

	private boolean isAirRecoveringDuringTransition(int actualAir) {
		return actualAir > lastActualAir && hasActiveTransition();
	}

	private boolean shouldStabilizeRecoveredAir(int actualAir, int maxAir) {
		return actualAir >= maxAir || (actualAir > lastActualAir && !hasActiveTransition());
	}

	private boolean shouldStartBubbleLossTransition(int targetVisualBubbleCount) {
		return targetVisualBubbleCount < lastVisualBubbleCount
				&& (!hasActiveTransition() || targetVisualBubbleCount < transitionToBubbleCount);
	}

	private AirBarRenderFrame stabilizeAirContext(int playerId, int actualAir, int maxAir, int bubbleCount) {
		rememberAirContext(playerId, actualAir, maxAir);
		lastVisualBubbleCount = bubbleCount;
		clearTransition();
		return AirBarRenderFrame.stable(bubbleCount);
	}

	private void rememberAirContext(int playerId, int actualAir, int maxAir) {
		lastPlayerId = playerId;
		lastMaxAir = maxAir;
		lastActualAir = actualAir;
	}

	private AirBarRenderFrame createCurrentRenderFrame(int currentTick) {
		AirBarAnimationPhase phase = advanceTransitionPhase(currentTick);
		if (phase == AirBarAnimationPhase.STABLE) {
			return AirBarRenderFrame.stable(lastVisualBubbleCount);
		}

		return AirBarRenderFrame.transition(transitionFromBubbleCount, transitionToBubbleCount, phase);
	}

	private void startTransition(int targetVisualBubbleCount, int currentTick) {
		transitionFromBubbleCount = lastVisualBubbleCount;
		transitionToBubbleCount = targetVisualBubbleCount;
		transitionStartTick = currentTick;
	}

	private AirBarAnimationPhase advanceTransitionPhase(int currentTick) {
		if (!hasActiveTransition()) {
			return AirBarAnimationPhase.STABLE;
		}

		if (currentTick < transitionStartTick) {
			lastVisualBubbleCount = transitionToBubbleCount;
			clearTransition();
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

	private boolean hasActiveTransition() {
		return transitionStartTick != NO_TRANSITION_TICK;
	}

	private void clearTransition() {
		transitionFromBubbleCount = NO_BUBBLE_COUNT;
		transitionToBubbleCount = NO_BUBBLE_COUNT;
		transitionStartTick = NO_TRANSITION_TICK;
	}
}
