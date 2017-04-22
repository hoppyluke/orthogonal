package com.supergreenowl.slugs.model;

import com.supergreenowl.sgdx.Resettable;

/**
 * Creature behaviour that causes a creature to run from a certain point.
 * @author Luke
 *
 */
public class RunFromBehaviour implements CreatureBehaviour, Resettable {

	private Creature creature;
	private Direction direction;
	
	public RunFromBehaviour(Creature c) {
		this.creature = c;
	}
	
	/**
	 * Sets the creature to run from.
	 * @param c Creature to run from.
	 */
	public void setScarer(Creature c) {
		DirectionPair dp = Navigator.navigateAwayFrom(creature.getHead().getPosition(), c.getHead().getPosition());
		
		direction = dp.getPrimary().isOpposite(creature.getHead().getDirection())
				? dp.getSecondary() : dp.getPrimary();
	}
	
	@Override
	public void think() {
		if(direction == null) throw new IllegalStateException("nothing to run from");
		creature.turn(direction);
	}

	@Override
	public void reset() {
		direction = null;
	}
}
