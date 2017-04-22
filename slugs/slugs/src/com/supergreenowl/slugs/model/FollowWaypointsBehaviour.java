package com.supergreenowl.slugs.model;

import com.supergreenowl.sgdx.Resettable;

/**
 * Chooses a random number of points in the world and heads to each point in turn.
 * After visiting each point in turn, the creature will continue moving in a straight line.
 * @author Luke
 *
 */
public class FollowWaypointsBehaviour implements CreatureBehaviour, Resettable {
	
	private static final int MIN_WAYPOINTS = 2;
	private static final int MAX_WAYPOINTS = 5;
	
	private Creature creature;
	
	private int totalWaypoints;
	private int currentWaypoint = 0;
	
	private final Point waypoint = new Point(), halfway = new Point();
	private boolean isNavigatingToHalfwayPoint = false;
	private boolean isFirstPointSet = false;
	
	private Direction direction;
	
	public FollowWaypointsBehaviour(Creature creature) {
		this.creature = creature;
		totalWaypoints = World.generator.nextInt(MAX_WAYPOINTS) + MIN_WAYPOINTS;
	}
	
	@Override
	public void think() {

		// if final waypoint has been reached, keep going on current course
		if(currentWaypoint >= totalWaypoints) return;
		
		// Choose initial waypoint
		if(!isFirstPointSet) pickNextDestination();
		
		Point start = creature.getHead().getPosition();
		Point target = isNavigatingToHalfwayPoint ? halfway : waypoint;
		
		if(creature.getHead().contains(target)) {
			if(isNavigatingToHalfwayPoint) {
				isNavigatingToHalfwayPoint = false;
				target = waypoint;
			}
			else pickNextDestination();
		}
		
		if(currentWaypoint <= totalWaypoints) {
			direction = Navigator.navigate(start, target);
			creature.turn(direction);
		}
	}

	/**
	 * Replots the course from current creature location to current waypoint.
	 */
	public void recalculateCourse() {
		if(currentWaypoint >= totalWaypoints) return;
		if(waypoint != null) calculateHalfwayPoint();
	}
	
	/**
	 * Chooses a random point in the world as the next destination for this worker.
	 * Sets the waypoint to reach the point and the direction to reach the waypoint.
	 */
	private void pickNextDestination() {
		
		isFirstPointSet = true;
		
		if(++currentWaypoint > totalWaypoints) {
			isNavigatingToHalfwayPoint = false;
			return;
		}
		
		creature.getWorld().getNavigator().setToRandomPoint(waypoint);
		calculateHalfwayPoint();
	}
	
	/**
	 * Calculates the halfway-point for the creature to reach current destination. 
	 */
	private void calculateHalfwayPoint() {
		Point start = creature.getHead().getPosition();

		float xMagnitude = Math.abs(waypoint.getX() - start.getX());
		float yMagnitude = Math.abs(waypoint.getY() - start.getY());

		halfway.set(waypoint);
		isNavigatingToHalfwayPoint = true;

		if(xMagnitude >= yMagnitude) halfway.setY(start.getY());	
		else halfway.setX(start.getX());
	}

	@Override
	public void reset() {
		totalWaypoints = World.generator.nextInt(MAX_WAYPOINTS) + MIN_WAYPOINTS;
		currentWaypoint = 0;
		isFirstPointSet = false;
		isNavigatingToHalfwayPoint = false;
		direction = null;
	}
}
