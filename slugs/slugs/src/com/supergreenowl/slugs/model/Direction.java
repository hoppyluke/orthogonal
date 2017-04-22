package com.supergreenowl.slugs.model;

public enum Direction {
	UP,
	DOWN,
	LEFT,
	RIGHT;

	/**
	 * Gets the direction that is anti-clockwise to this direction.
	 * @return
	 */
	public Direction getDirectionAntiClockwise() {
		switch(this) {
		case UP: return LEFT;
		case LEFT: return DOWN;
		case DOWN: return RIGHT;
		case RIGHT: return UP;
		}

		return this;
	}

	public boolean isHorizontal() {
		switch(this) {
		case UP: return false;
		case LEFT: return true;
		case DOWN: return false;
		case RIGHT: return true;
		default: return false;
		}
	}
	
	public boolean isOpposite(Direction other) {
		switch(this) {
		case UP: return other == DOWN;
		case LEFT: return other == RIGHT;
		case DOWN: return other == UP;
		case RIGHT: return other == LEFT;
		default: return false;
		}
	}
	
	/**
	 * Converts a scalar distance to vector displacement in this direction.
	 * The displacement is negative when this direction is LEFT or DOWN and positive otherwise.
	 * @param distance Scalar distance value.
	 * @return Vector displacement.
	 */
	public float getDisplacement(float distance) {
		return this == Direction.LEFT || this == Direction.DOWN ?
				distance * -1f
				: distance;
	}
}
