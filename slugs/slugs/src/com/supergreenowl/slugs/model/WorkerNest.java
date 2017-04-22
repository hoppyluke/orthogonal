package com.supergreenowl.slugs.model;

public class WorkerNest extends Nest<Worker> {

	private static final double SPAWN_RATE = 3d;
	private static final double SPAWN_VARIANCE = 2d;
	
	public WorkerNest(World world) {
		super(world, SPAWN_RATE, SPAWN_VARIANCE);
	}

	@Override
	protected Worker newObject() {
		return new Worker();
	}

}
