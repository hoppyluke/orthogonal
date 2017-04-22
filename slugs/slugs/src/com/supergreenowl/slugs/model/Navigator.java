package com.supergreenowl.slugs.model;

public class Navigator {
	
	private static final DirectionPair directionPair = new DirectionPair();
	
	private World world;
	
	private Direction edgeDirection;
	
	/**
	 * Creates a new navigator for the specified world.
	 * @param world
	 */
	public Navigator(World world) {
		this.world = world;
	}
	
	/**
	 * Selects the direction to move in to navigate from one point to another.
	 * @param start Start point to navigate from.
	 * @param end End point to navigate to.
	 * @return Direction to move in.
	 */
	public static Direction navigate(Point start, Point end) {
		float xMagnitude = Math.abs(end.getX() - start.getX());
		float yMagnitude = Math.abs(end.getY() - start.getY());
		
		if(xMagnitude >= yMagnitude) {
			return end.getX() > start.getX() ? Direction.RIGHT : Direction.LEFT;
		}
		else {
			return end.getY() > start.getY() ? Direction.UP : Direction.DOWN;
		}
	}
	
	/**
	 * Selects the direction to move in to navigate from one point to another
	 * for a creature which is already traveling in a certain direction.
	 * @param start Start point to navigate from.
	 * @param end End point to navigate to.
	 * @param currentDirection Current direction that the creature is traveling in.
	 * @return Direction to move in. Guaranteed not to be a 180 degree turn from current direction.
	 */
	public static Direction navigate(Point start, Point end, Direction currentDirection) {
		Direction newDirection = navigate(start, end);
		
		// Creatures must turn at right angles
		// If 180 degree turn is required, instead turn at 90 degrees
		if(currentDirection != newDirection && 
				currentDirection.isHorizontal() == newDirection.isHorizontal()) {
			
			if(currentDirection.isHorizontal())
				newDirection = end.getY() > start.getY() ? Direction.UP : Direction.DOWN;
			else newDirection = end.getX() > start.getX() ? Direction.RIGHT : Direction.LEFT;
			
		}
		
		return newDirection;
	}
	
	/**
	 * Selects the directions to move away from the end point.
	 * @param start Origin point to move from.
	 * @param end Point to navigate away from.
	 * @return Pair of directions to move away from the end point; one horizontal and one vertical.
	 * The primary direction is the direction that leads away quickest. The same instance is returned each time. 
	 */
	public static DirectionPair navigateAwayFrom(Point start, Point end) {
		float xMagnitude = Math.abs(end.getX() - start.getX());
		float yMagnitude = Math.abs(end.getY() - start.getY());
		
		Direction horizontal = end.getX() > start.getX() ? Direction.LEFT : Direction.RIGHT;
		Direction vertical = end.getY() > start.getY() ? Direction.DOWN : Direction.UP;
		
		if(xMagnitude <= yMagnitude) directionPair.set(horizontal, vertical);
		else directionPair.set(vertical, horizontal);
		
		return directionPair;
	}
	
	/**
	 * Creates a new point randomly generated that is on one of the edges of the world. 
	 * @return Edge point.
	 */
	public Point getRandomEdgePoint() {
		int selectedEdge = World.generator.nextInt(4);
		
		float x = 0, y = 0;
		
		switch(selectedEdge) {
		case 0: // top edge
			x = getRandomX();
			y = world.getHeight();
			edgeDirection = Direction.DOWN;
			break;
		case 1: // bottom edge
			x = getRandomX();
			y = 0f;
			edgeDirection = Direction.UP;
			break;
		case 2: // left edge
			x = 0f;
			y = getRandomY();
			edgeDirection = Direction.RIGHT;
			break;
		case 3: // right edge
			x = world.getWidth();
			y = getRandomY();
			edgeDirection = Direction.LEFT;
			break;
		}
		
		return new Point(x, y);
	}
	
	/**
	 * Gets the direction to the center of the world from the last
	 * generated edge point.
	 * @return Direction to center.
	 */
	public Direction getLastEdgeDirection() {
		return edgeDirection;
	}
	
	/**
	 * Creates a new random point within world boundaries.
	 * @return Random point.
	 */
	public Point getRandomPoint() {
		return new Point(getRandomX(), getRandomY());
	}
	
	/**
	 * Sets the specified point to a random point within this world.
	 * @param p
	 */
	public void setToRandomPoint(Point p) {
		if(p == null) throw new IllegalArgumentException("cannot set null point");
		p.set(getRandomX(), getRandomY());
	}
	
	/**
	 * Gets a random x coordinate within the world.
	 * @return
	 */
	private float getRandomX() {
		return World.generator.nextFloat() * world.getWidth();
	}
	
	/**
	 * Gets a random y coordinate within the world.
	 * @return
	 */
	private float getRandomY() {
		return World.generator.nextFloat() * world.getHeight();
	}
	
}
