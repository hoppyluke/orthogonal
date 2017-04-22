package com.supergreenowl.sgdx;

/**
 * Measures the passage of time.
 * @author Luke
 *
 */
public interface Clock {
	
	/**
	 * Gets the total elapsed time measured by this clock.
	 * @return Total time.
	 */
	float getTime();
	
	/**
	 * Gets the duration of the last tick.
	 * @return Last tick in seconds.
	 */
	float getTick();
	
	/**
	 * Gets the total number of whole seconds measured by this clock (rounds down).
	 * @return
	 */
	int getTotalSeconds();
}
