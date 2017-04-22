package com.supergreenowl.sgdx;

import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * An object that can be reset to its initial state. 
 * This is functionally equivalent but semantically distinct from the {@link Poolable} interface.
 * @author Luke
 * @see com.badlogic.gdx.utils.Pool.Poolable
 */
public interface Resettable extends Poolable {

	/**
	 * Resets this object to the initial state.
	 */
	@Override
	void reset();
}
