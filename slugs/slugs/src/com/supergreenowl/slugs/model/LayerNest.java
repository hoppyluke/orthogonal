package com.supergreenowl.slugs.model;

/**
 * Nest that spawns layers.
 * @author Luke
 *
 */
public class LayerNest extends Nest<Layer> {

	private static final double SPAWN_RATE = 7d;
	private static final double SPAWN_VARIANCE = 3d;
	
	public LayerNest(World world) {
		super(world, SPAWN_RATE, SPAWN_VARIANCE);
	}
	
	@Override
	protected Layer newObject() {
		return new Layer();
	}
}
