package com.supergreenowl.slugs.model;

public class Runner extends LineCreature {

	private static final float WIDTH = 8f;
	private static final float HEIGHT = WIDTH * 3f;
	
	private final Box collisionBox = new Box();
	
	public Runner() {
		super(WIDTH, HEIGHT, true, true);
	}
	
	public Box getCollisionBox() {
		collisionBox.set(getHead().getBoundingBox());
		
		// collision box excludes line point to avoid collision with own line
		// can't just check line.owner == this as on turn, old line is at line point and no longer owned
		collisionBox.shrink(getWidth() / 2f + 0.01f, getHead().getDirection());
		return collisionBox;
	}
}