package com.supergreenowl.slugs.model;

import com.supergreenowl.sgdx.Resettable;

public class FollowLineBehaviour implements CreatureBehaviour, Resettable {

	private final Creature creature;
	private Line line = null;
	
	private boolean isCentred = false;
	private float threshold;
	
	public FollowLineBehaviour(Creature creature) {
		this.creature = creature;
		this.threshold = creature.getWidth() / 8f;
	}
	
	@Override
	public void think() {
		if(line == null) throw new IllegalStateException("Cannot follow null line");
		
		Direction d = line.getDirection();
		
		if(!isCentred) {
			float difference = 0f;
			if(d.isHorizontal())
				difference = Math.abs(creature.getHead().getPosition().getY() - line.getStartY());
			else difference = Math.abs(creature.getHead().getPosition().getX() - line.getStartX());
			
			if(difference <= threshold) isCentred = true;
		}
		
		if(isCentred) creature.turn(d);
	}
	
	public void setLine(Line line) {
		this.line = line;
		isCentred = false;
	}
	
	public boolean hasLine() {
		return line != null && line.getLength() >= 0f;
	}

	@Override
	public void reset() {
		line = null;
		isCentred = false;
		// Not resetting creature. Behaviour will stay with creature.
	}

}
