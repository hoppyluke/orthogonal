package com.supergreenowl.slugs.model;

/**
 * Creature that lays lines behind it.
 * @author Luke
 *
 */
public class Layer extends LineCreature {

	private static final float WIDTH = 8f;
	private static final float HEIGHT = WIDTH * 3f;
	
	public Layer() {
		super(WIDTH, HEIGHT, false, true);
	}

}
