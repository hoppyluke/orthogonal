package com.supergreenowl.slugs.model;

public class Box {

	private float left, right, top, bottom;
	
	private Point centre = new Point();
	
	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getRight() {
		return right;
	}

	public void setRight(float right) {
		this.right = right;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getBottom() {
		return bottom;
	}

	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	
	public void set(Box box) {
		if(box == null) throw new IllegalArgumentException("exepected non-null box");
		
		this.left = box.left;
		this.right = box.right;
		this.bottom = box.bottom;
		this.top = box.top;
	}
	
	/**
	 * Moves this box to be centred on the specified coordinates.
	 * @param x
	 * @param y
	 */
	public void moveTo(float x, float y) {
		float width = right - left;
		float height = top - bottom;
		
		left = x - (width / 2f);
		right = x + (width / 2f);
		top = y + (height / 2f);
		bottom = y - (height / 2f);
	}

	/**
	 * Determines if any part of this box touches any part of another box.
	 * This method is null-safe - it returns false if the other box is null.
	 * @param other
	 * @return
	 */
	public boolean intersects(Box other) {
		if(other == null) return false;
		return !(right < other.left || left > other.right || top < other.bottom || bottom > other.top);
	}
	
	/**
	 * Determines if this box wholly contains another box.
	 * THis method is null-safe - it returns false if the other box is null.
	 * @param other
	 * @return
	 */
	public boolean contains(Box other) {
		if(other == null) return false;
		return left <= other.left && right >= other.right && bottom <= other.bottom && top >= other.top;
	}
	
	/**
	 * Determines if a point falls within this box.
	 * This method is null-safe - it returns false if the point is null.
	 * @param p
	 * @return
	 */
	public boolean contains(Point p) {
		if(p == null) return false;
		return left <= p.getX() && right >= p.getX() && bottom <= p.getY() && top >= p.getY();
	}
	
	public float getWidth() {
		return right - left;
	}
	
	public float getHeight() {
		return top - bottom;
	}
	
	/**
	 * Returns the point at the centre of this box.
	 * @return
	 */
	public Point getCentre() {
		centre.set(left + ((right - left) / 2f), bottom + ((top - bottom) / 2f));
		return centre;
	}

	public void moveX(float displacement) {
		left += displacement;
		right += displacement;
	}
	
	public void moveY(float displacement) {
		top += displacement;
		bottom += displacement;
	}
	
	public void move(float amount, Direction direction) {
		switch(direction) {
		case LEFT:
			left -= amount;
			right -= amount;
			break;
		case RIGHT:
			left += amount;
			right += amount;
			break;
		case UP:
			bottom += amount;
			top += amount;
			break;
		case DOWN:
			bottom -= amount;
			top -= amount;
			break;
		}
	}
	
	public void grow(float amount, Direction direction) {
		switch(direction) {
		case LEFT:
			left -= amount;
			break;
		case RIGHT:
			right += amount;
			break;
		case UP:
			top += amount;
			break;
		case DOWN:
			bottom -= amount;
			break;
		}
		
	}
	
	public void shrink(float amount, Direction direction) {
		switch(direction) {
		case LEFT:
			right -= amount;
			break;
		case RIGHT:
			left += amount;
			break;
		case UP:
			bottom += amount;
			break;
		case DOWN:
			top -= amount;
			break;
		}
	}
	
	public void growX(float displacement) {
		if(displacement > 0f) right += displacement;
		else left += displacement;
	}
	
	public void growY(float displacement) {
		if(displacement > 0f) top += displacement;
		else bottom += displacement;
	}
	
	public void shrinkX(float displacement) {
		if(displacement > 0f) left += displacement;
		else right += displacement;
	}
	
	public void shrinkY(float displacement) {
		if(displacement > 0f) bottom += displacement;
		else top += displacement;
	}
	
	public void setSize(float width, float height) {
		right = left + width;
		top = bottom + height;
	}
	
	@Override
	public String toString() {
		return String.format("[%.2f,  %.2f, %.2f, %.2f]", left, bottom, top, right);
	}
	
	
}
