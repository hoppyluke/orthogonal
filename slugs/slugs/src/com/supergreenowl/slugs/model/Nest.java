package com.supergreenowl.slugs.model;

import com.badlogic.gdx.utils.Pool;
import com.supergreenowl.sgdx.Resettable;

/**
 * Spawns creatures in the world at random intervals.
 * @author Luke
 * 
 * @param <C> Type of creatures to spawn.
 */
public abstract class Nest<C extends Creature> extends Pool<C> implements Resettable {

	/**
	 * Mean time in seconds between spawns.
	 */
	private final double spawnRate;
	
	/**
	 * Standard deviation for the spawn distribution.
	 */
	private final double spawnVariance;
	
	private final World world;
	
	private float speedMultiplier = 1f;
	private float lastSpawn = 0f;
	private float nextSpawn;
	
	/**
	 * Creates a new nest.
	 * @param world World to spawn creatures in.
	 * @param spawnRate Average number of seconds between creature spawns.
	 * @param spawnVariance Standard deviation for creature spawns, in seconds.
	 */
	public Nest(World world, double spawnRate, double spawnVariance) {
		super(32, 64);
		this.world = world;
		this.spawnRate = spawnRate;
		this.spawnVariance = spawnVariance;
		
		calculateNextSpawn();
	}
	
	/**
	 * Sets the speed multiplier for creatures spawned by this nest.
	 * @param speedMultiplier
	 */
	public void setSpeedMultiplier(float speedMultiplier) {
		this.speedMultiplier = speedMultiplier;
	}
	
	/**
	 * Spawns creatures as per the distribution of this nest.
	 * @param time Elapsed time in seconds since last spawn call.
	 */
	public void spawnCreatures(float time) {
		while(nextSpawn <= world.getClock().getTime()) {
			C creature = obtain();
			
			// Set start direction to a random point on the edge of the world
			Point startPoint = world.getNavigator().getRandomEdgePoint();
			Direction startDirection = world.getNavigator().getLastEdgeDirection();
			
			creature.place(startPoint.getX(), startPoint.getY(), startDirection);
			creature.setSpeedMultiplier(speedMultiplier);
			
			world.add(creature);
			lastSpawn = world.getClock().getTime();
			
			calculateNextSpawn();
		}
	}
	
	/**
	 * Resets this nest. Note that the speed multiplier is not reset.
	 */
	@Override
	public void reset() {
		lastSpawn = 0f;
		calculateNextSpawn();
	}

	/**
	 * Calculates the time at which the next creature should spawn.
	 */
	private void calculateNextSpawn() {
		float nextSpawnDuration = -1f;
		
		while(nextSpawnDuration < 0f) {
			nextSpawnDuration = (float) (World.generator.nextGaussian() * spawnVariance + spawnRate); 
		}
		
		nextSpawn = lastSpawn + nextSpawnDuration;
	}
}
