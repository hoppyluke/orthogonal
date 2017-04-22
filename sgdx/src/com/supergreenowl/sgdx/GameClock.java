package com.supergreenowl.sgdx;

/**
 * Measures elapsed game time.
 * @author Luke
 */
public class GameClock implements Clock, Resettable {

	private float totalTime = 0f;
	private float lastTick = 0f;
	
	/**
	 * Records the passage of time.
	 * @param elapsedTime Time in seconds that have passed.
	 */
	public void tick(float elapsedTime) {
		lastTick = elapsedTime;
		totalTime += elapsedTime;
	}
	
	/**
	 * Gets the total elapsed time measured by this clock.
	 * @return Total time.
	 */
	public float getTime() {
		return totalTime;
	}
	
	/**
	 * Gets the duration of the last tick.
	 * @return Last tick in seconds.
	 */
	public float getTick() {
		return lastTick;
	}
	
	/**
	 * Resets this clock back to zero.
	 */
	@Override
	public void reset() {
		totalTime = 0f;
		lastTick = 0f;
	}

	@Override
	public int getTotalSeconds() {
		return (int)totalTime;
	}
}
