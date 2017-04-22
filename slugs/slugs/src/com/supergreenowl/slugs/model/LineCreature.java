package com.supergreenowl.slugs.model;

/**
 * A creature that places lines as it moves.
 * @author Luke
 *
 */
public abstract class LineCreature extends Creature {

	private Line line = null;
	
	private final Point linePoint = new Point();
	private boolean isLinePointSet = false;
	private final Point previousPosition = new Point();
	
	public LineCreature(float width, float height, boolean canBeEaten, boolean canEatCreatures) {
		super(width, height, canBeEaten, canEatCreatures);
	}

	@Override
	public void move(float time) {
		
		// ensure this is called before first move - no need to recalculate since last move
		if(!isLinePointSet) updateLinePoint();
		
		previousPosition.set(linePoint);
		super.move(time);
		
		updateLinePoint();
		
		Direction currentDirection = getHead().getDirection();
		if(line != null && currentDirection == line.getDirection()) {
			// extend current slime
			line.extend(getLastMoveDistance());
		}
		else {
			// either there is no line yet, or direction has changed
			// create new line from prev. position to current position
			
			// No longer own previous line (if any)
			if(line != null) line.setOwner(null);
			
			line = getWorld().getLinePool().obtain();
			line.set(previousPosition.getX(), previousPosition.getY(), linePoint.getX(), linePoint.getY(), currentDirection);
			line.setOwner(this);
			getWorld().addLine(line);
		}
	}
	
	/**
	 * Removes the current line from this creature, if any.
	 */
	public void removeLine() {
		if(line == null) return;
		
		line.setOwner(null);
		line = null;
	}
	
	@Override
	public void place(float x, float y, Direction direction) {
		super.place(x, y, direction);
		updateLinePoint();
	}

	@Override
	public void reset() {
		super.reset();
		line = null;
		isLinePointSet = false;
	}

	/**
	 * Updates the location of the point that the line is laid from.
	 * This point is a fixed distance of half creature width behind the front of the head.
	 */
	private void updateLinePoint() {
		linePoint.set(getHead().getPosition());
		
		// if head size is greature than width, line point is not centre of head
		float offset = (getHead().getSize() - getWidth()) / 2f;
		if(offset > 0f) {
			Direction direction = getHead().getDirection();
			
			if(direction == Direction.RIGHT) linePoint.moveX(offset);
			else if(direction == Direction.LEFT) linePoint.moveX(-1f * offset);
			else if(direction == Direction.UP) linePoint.moveY(offset);
			else if(direction == Direction.DOWN) linePoint.moveY(-1f * offset);
		}
		
		isLinePointSet = true;
	}
	
}