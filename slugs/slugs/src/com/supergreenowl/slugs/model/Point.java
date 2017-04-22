package com.supergreenowl.slugs.model;

public class Point {

	private float x, y;
	
	public Point() {
		x = 0f;
		y = 0f;
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p) {
		if(p == null) throw new IllegalArgumentException("Expected non-null point");
		
		this.x = p.x;
		this.y = p.y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void set(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveX(float amount) {
		x += amount;
	}
	
	public void moveY(float amount) {
		y += amount;
	}
	
	/**
	 * Calculates the Euclidean distance between this point and another point.
	 * @param other Point to calculate distance to.
	 * @return Distrance between this point and specified point.
	 */
	public float getDistance(Point other) {
		if(other == null) throw new IllegalArgumentException("Expected non-null point");
		
		double x2 = Math.pow(other.x - x, 2d);
		double y2 = Math.pow(other.y - y, 2d);
		
		return (float)Math.sqrt(x2 + y2);
	}

	@Override
	public String toString() {
		return String.format("(%.2f,  %.2f)", x, y);
	}
}
