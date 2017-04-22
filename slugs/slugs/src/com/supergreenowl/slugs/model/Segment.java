package com.supergreenowl.slugs.model;

import com.supergreenowl.sgdx.Resettable;

/**
 * Part of a creature.
 * A creature is represented by a doubly-linked list of segments.
 * The structure necessary to maintain this list is internal to the segment class itself.
 * This is done to avoid allocation of new list node wrapper objects on list insert operations
 * (e.g. if {@code java.util.List<E>} was used).
 * @author Luke
 *
 */
public class Segment implements Resettable {

	private final Box box = new Box();
	private Direction direction;
	
	private Segment next = null, previous = null;
	
	/**
	 * Positions a segment in the world and sets its size. The segment is centered on the point specified.
	 * @param x x coordinate of point to place segment at.
	 * @param y y coordinate of point to place segment at.
	 * @param width Width of the segment.
	 * @param height Height of the segment.
	 * @param direction Direction for the segment to face.
	 */
	void place(float x, float y, float width, float height, Direction direction) {
		if(direction.isHorizontal()) box.setSize(height, width);
		else box.setSize(width, height);
		
		box.moveTo(x, y);
		this.direction = direction;
	}
	
	/**
	 * Gets the next segment in this creature.
	 * @return
	 */
	public Segment getNext() {
		return next;
	}
	
	/**
	 * Gets the previous segment in this creature.
	 * @return
	 */
	public Segment getPrevious() {
		return previous;
	}
	
	/**
	 * Inserts a segment in the list after this segment.
	 * @param segment Segment to insert; {@code null} to truncate the list at this segment.
	 */
	void insert(Segment segment) {
		if(this.next != null && this.next == segment) {
			throw new IllegalStateException("cannot set next to current next");
		}
		
		if(segment != null) {
			segment.previous = this;
			segment.next = this.next;
			if(this.next != null) this.next.previous = segment;
		}
		
		this.next = segment;
	}
	
	/**
	 * Gets the position of the centre of this segment.
	 * @return Centre point.
	 */
	public Point getPosition() {
		return box.getCentre();
	}
	
	/**
	 * Gets the length of this segment.
	 * The size is the width if it is facing horizontally and the height otherwise.
	 * @return Size of the segment.
	 */
	public float getSize() {		
		return direction.isHorizontal() ? box.getWidth() : box.getHeight();
	}
	
	/**
	 * Determines if this segment is currently active.
	 * A segment becomes inactive when its size has decreased to zero.
	 * @return Flag indicating if this segment is active.
	 */
	public boolean isActive() {
		
		// if direction has not been set yet, creature is not active
		if(direction == null) return false;
		
		return getSize() >= 0f;
	}
	
	/**
	 * Gets the direction that this segment is facing.
	 * @return Direction.
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Sets the direction that this segment is facing.
	 * @param direction New direction to face.
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Determines if this segment contains the specified point.
	 * @param point Location to test.
	 * @return True if and only if the location falls within the bounds of this segment.
	 */
	public boolean contains(Point point) {
		return box.contains(point);
	}
	
	/**
	 * Gets a box that defines the area of this segment.
	 * @return Box.
	 */
	public Box getBoundingBox() {
		return box;
	}
	
	/**
	 * Moves this segment by the specified amount.
	 * The head of a multi-segment creature grows and the tail shrinks.
	 * @param distance Distance to move.
	 */
	void move(float distance) {
		
		boolean isHead = previous == null;
		boolean isTail = next == null;
		
		// sole segment moves
		if(isHead && isTail) box.move(distance, direction);
		
		// head of multi-segment creature grows
		else if(isHead) box.grow(distance, direction);
		
		// tail of multi-segment creature shrinks
		else if(isTail) box.shrink(distance, direction); 

		// middle section of multi-segment creature stays still
	}
	
	/**
	 * Splits this segment in two. This segment remains as head and target
	 * segment is set to the tail.
	 * @param target Segment to set as the tail of this segment.
	 * @param creatureWidth Width of the creature that this segment belongs to.
	 */
	void split(Segment target, float creatureWidth) {
		// make target a copy of this segment
		target.box.set(box);
		target.direction = direction;
		
		// calculate point to split this segment and target
		float splitPoint;
		
		switch (direction) {
		case LEFT:
			splitPoint = box.getLeft() + creatureWidth;
			target.box.setLeft(splitPoint);
			box.setRight(splitPoint);
			break;
			
		case RIGHT:
			splitPoint = box.getRight() - creatureWidth;
			target.box.setRight(splitPoint);
			box.setLeft(splitPoint);
			break;
			
		case UP:
			splitPoint = box.getTop() - creatureWidth;
			target.box.setTop(splitPoint);
			box.setBottom(splitPoint);
			break;
			
		case DOWN:
			splitPoint = box.getBottom() + creatureWidth;
			target.box.setBottom(splitPoint);
			box.setTop(splitPoint);
			break;
		}
		
		// insert target into list
		insert(target);
	}
	
	/**
	 * Clears the next and previous pointers for this segment.
	 */
	void clear() {
		next = null;
		previous = null;
	}
	
	/**
	 * Shrinks this segment by the specified amount in its current direction.
	 * @param amount Amount to shrink by.
	 */
	void shrink(float amount) {
		box.shrink(amount, direction);
	}

	@Override
	public void reset() {
		next = null;
		previous = null;
		direction = null;
		box.setBottom(0f);
		box.setLeft(0f);
		box.setRight(0f);
		box.setTop(0f);
	}
}
