package com.supergreenowl.slugs.model;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Line implements Poolable {

	private final Point start = new Point();
	private final Point end = new Point();
	private Direction direction;
	private final Box boundingBox = new Box();
	
	private LineCreature owner = null;
	
	public Line() { }
	
	public void set(float x1, float y1, float x2, float y2, Direction direction) {
		start.set(x1, y1);
		end.set(x2, y2);
		this.direction = direction;
	}
	
	/**
	 * Extends the length of this line by the specified amount.
	 * @param amount
	 */
	public void extend(float amount) {
		if(direction == Direction.LEFT) end.moveX(-1f * amount);
		else if(direction == Direction.RIGHT) end.moveX(amount);
		else if(direction == Direction.UP) end.moveY(amount);
		else if(direction == Direction.DOWN) end.moveY(-1f * amount);
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(Direction d) {
		this.direction = d;
	}
	
	public void setOwner(LineCreature c) {
		this.owner = c;
	}
	
	public LineCreature getOwner() {
		return owner;
	}
	
	/**
	 * Calculates the bounding box for this line.
	 * Callers should cache the returned box locally as every call to this method recalculates it.
	 * @return Updated bounding box.
	 */
	public Box getBoundingBox() {
		
		if(direction == Direction.RIGHT) {
			boundingBox.setLeft(start.getX());
			boundingBox.setRight(end.getX());
			boundingBox.setBottom(start.getY());
			boundingBox.setTop(start.getY());
		}
		else if(direction == Direction.LEFT) {
			boundingBox.setLeft(end.getX());
			boundingBox.setRight(start.getX());
			boundingBox.setBottom(start.getY());
			boundingBox.setTop(start.getY());
		}
		else if(direction == Direction.UP) {
			boundingBox.setLeft(start.getX());
			boundingBox.setRight(start.getX());
			boundingBox.setBottom(start.getY());
			boundingBox.setTop(end.getY());
		}
		else if(direction == Direction.DOWN) {
			boundingBox.setLeft(start.getX());
			boundingBox.setRight(start.getX());
			boundingBox.setTop(start.getY());
			boundingBox.setBottom(end.getY());
		}
		
		return boundingBox;
	}
	
	/**
	 * Updates this line to remove intersection with the specified box. This could create a second
	 * line if the box intersects the middle of this line only and not with the start or end.
	 * When the line is split, this line is updated to start at the edge of the box - it's end point is not changed.
	 * A new line will be added from the old start point to the edge of the box.
	 * This method assumes the line is not entirely within the box (which would remove the entire line).
	 * @param b Box to intersect with.
	 * @return New line that was created (if any) by the removal of part of this line. Null if no new
	 * line was created.
	 */
	public Line removeIntersection(Box b, Pool<Line> pool) {
		
		if(b.contains(start)) {
			// move start to box edge
			// shorten length so end does not move
			if(direction == Direction.LEFT) start.setX(b.getLeft());
			else if(direction == Direction.RIGHT) start.setX(b.getRight());
			else if(direction == Direction.UP) start.setY(b.getTop());
			else if(direction == Direction.DOWN) start.setY(b.getBottom());
		}
		else if(b.contains(end)) {
			// move end to box edge
			if(direction == Direction.LEFT) end.setX(b.getRight());
			else if(direction == Direction.RIGHT) end.setX(b.getLeft());
			else if(direction == Direction.UP) end.setY(b.getBottom());
			else if(direction == Direction.DOWN) end.setY(b.getTop());
			
			// if end of line == creature, when end is removed need to remove the line from the creature
			if(owner != null) owner.removeLine();
		}
		else {
			// Box bisects line - create new line from current start to box edge
			// Update this line to start at box edge and go to to current end
			Line newLine = pool.obtain();
			
			if(direction == Direction.LEFT) {
				newLine.set(start.getX(), start.getY(), b.getRight(), start.getY(), direction);
				start.setX(b.getLeft());
			}
			else if(direction == Direction.RIGHT) {
				newLine.set(start.getX(), start.getY(), b.getLeft(), start.getY(), direction);
				start.setX(b.getRight());
			}
			else if(direction == Direction.UP) {
				newLine.set(start.getX(), start.getY(), start.getX(), b.getBottom(), direction);
				start.setY(b.getTop());
			}
			else if(direction == Direction.DOWN) {
				newLine.set(start.getX(), start.getY(), start.getX(), b.getTop(), direction);
				start.setY(b.getBottom());
			}
			
			return newLine;
		}
		
		return null;
	}

	public float getLength() {
		switch(direction) {
		case UP: return end.getY() - start.getY();
		case DOWN: return start.getY() - end.getY();
		case LEFT: return start.getX() - end.getX();
		case RIGHT: return end.getX() - start.getX();
		default: throw new IllegalStateException("What direction is that?");
		}
	}
	
	public float getStartX() {
		return start.getX();
	}
	
	public float getStartY() {
		return start.getY();
	}
	
	@Override
	public String toString() {
		return String.format("(%.2f, %.2f) -> (%.2f, %.2f) %s", start.getX(), start.getY(),
				end.getX(), end.getY(),
				direction);
	}

	@Override
	public void reset() {
		owner = null;
	}
}
