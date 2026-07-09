package io.github.isquyet.mindyourbubbles.client.air;

public record AirBarRenderFrame(int fromBubbleCount, int toBubbleCount, AirBarAnimationPhase phase) {
	public static AirBarRenderFrame stable(int bubbleCount) {
		return new AirBarRenderFrame(bubbleCount, bubbleCount, AirBarAnimationPhase.STABLE);
	}

	public static AirBarRenderFrame transition(int fromBubbleCount, int toBubbleCount, AirBarAnimationPhase phase) {
		return new AirBarRenderFrame(fromBubbleCount, toBubbleCount, phase);
	}
}
