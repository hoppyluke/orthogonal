package com.supergreenowl.slugs.model;

/**
 * A primary direction and a secondary direction.
 * @author Luke
 *
 */
public class DirectionPair {

	private Direction primary, secondary;
	
	public DirectionPair() {
		this.primary = null;
		this.secondary = null;
	}
	
	public DirectionPair(Direction primary, Direction secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}
	
	public Direction getPrimary() {
		return this.primary;
	}
	
	public Direction getSecondary() {
		return this.secondary;
	}
	
	public void setPrimary(Direction primary) {
		this.primary = primary;
	}
	
	public void setSecondary(Direction secondary) {
		this.secondary = secondary;
	}
	
	public void set(Direction primary, Direction secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}
	
}
