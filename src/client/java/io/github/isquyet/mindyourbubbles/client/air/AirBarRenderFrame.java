package io.github.isquyet.mindyourbubbles.client.air;

public final class AirBarRenderFrame {
	private final int fromBubbleCount;
	private final int toBubbleCount;
	private final AirBarAnimationPhase phase;

	private AirBarRenderFrame(int fromBubbleCount, int toBubbleCount, AirBarAnimationPhase phase) {
		this.fromBubbleCount = fromBubbleCount;
		this.toBubbleCount = toBubbleCount;
		this.phase = phase;
	}

	public static AirBarRenderFrame stable(int bubbleCount) {
		return new AirBarRenderFrame(bubbleCount, bubbleCount, AirBarAnimationPhase.STABLE);
	}

	public static AirBarRenderFrame transition(int fromBubbleCount, int toBubbleCount, AirBarAnimationPhase phase) {
		return new AirBarRenderFrame(fromBubbleCount, toBubbleCount, phase);
	}

	public int fromBubbleCount() {
		return fromBubbleCount;
	}

	public int toBubbleCount() {
		return toBubbleCount;
	}

	public AirBarAnimationPhase phase() {
		return phase;
	}
}
