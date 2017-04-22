package com.supergreenowl.slugs.model;

import java.util.Iterator;

/**
 * A creature that cleans up any lines it moves over.
 * @author Luke
 *
 */
public class Worker extends Creature {

	private static final float WORKER_SIZE = 5f;
	private static final float SPEED_NORMAL = BASE_SPEED * 1.1f;
	private static final float SPEED_SCARED = 2f * SPEED_NORMAL;
	private static final float SPEED_FOLLOWING = SPEED_NORMAL * 0.5f;
	
	private static final float SCARE_DISTANCE = 30f;
	private static final float SAFE_DISTANCE = 40f;
	
	private FollowWaypointsBehaviour normalBehaviour = new FollowWaypointsBehaviour(this);
	private FollowLineBehaviour followBehaviour = new FollowLineBehaviour(this);
	private RunFromBehaviour scaredBehaviour = new RunFromBehaviour(this);
	private CreatureBehaviour behaviour = normalBehaviour;
	
	private boolean isScared = false;
	
	public Worker() {
		super(WORKER_SIZE, WORKER_SIZE, true, false);
		setSpeed(SPEED_NORMAL);
	}

	@Override
	public void move(float time) {
		super.move(time);
		
		// Cleanup any lines that were moved over
		Box lastMove = getLastMove();
		World w = getWorld();
		Iterator<Line> lineIterator = w.getLines().iterator();
		
		while(lineIterator.hasNext()) {
			Line line = lineIterator.next();
			Box b = line.getBoundingBox();
			if(lastMove.intersects(b)) {
				if(lastMove.contains(b)) {
					// just ate the entire line - remove it from world
					lineIterator.remove();
					if(line.getOwner() != null) line.getOwner().removeLine();
					followBehaviour.setLine(null);
					w.getLinePool().free(line);
				}
				else {
					Line newLine = line.removeIntersection(lastMove, w.getLinePool());
					// If new line was created by cutting line in two, add it to world
					if(newLine != null) {
						w.addLine(newLine);
					}
					
					// won't follow when scared
					if(!isScared) {
						followBehaviour.setLine(line);
						behaviour = followBehaviour;
						setSpeed(SPEED_FOLLOWING);
					}
				}
			}
		}
				
		// Flush the lines queue so that any other workers
		// yet to move are aware of the new line (if any)
		w.flushLinesQueue();
	}
	
	@Override
	public void reset() {
		super.reset();
		normalBehaviour.reset();
		followBehaviour.reset();
		scaredBehaviour.reset();
		behaviour = normalBehaviour;
		isScared = false;
		setSpeed(SPEED_NORMAL);
	}

	@Override
	protected void think() {
		if(isScared) checkIfSafe();
		else checkIfScared();
		
		if(behaviour == followBehaviour && !followBehaviour.hasLine()) {
			followBehaviour.setLine(null);
			setSpeed(SPEED_NORMAL);
			behaviour = normalBehaviour;
		}
		
		behaviour.think(); // delegate thinking to current behaviour
	}
	
	private void checkIfScared() {
		for(Creature c : getWorld().getCreatures()) {
			if(c instanceof Worker) continue; // not scared of workers
			
			float distance = getHead().getPosition().getDistance(c.getHead().getPosition());
			
			if(distance <= SCARE_DISTANCE) {
				isScared = true;
				setSpeed(SPEED_SCARED);
				followBehaviour.setLine(null);
				scaredBehaviour.setScarer(c);
				behaviour = scaredBehaviour;
				break;
			}
		}
	}
	
	private void checkIfSafe() {
		// if any scary creatures are within safety difference, keep being scared
		Point p = getHead().getPosition();
		for(Creature c : getWorld().getCreatures()) {
			if(c instanceof Worker) continue; // not scared of workers
			
			float distance = p.getDistance(c.getHead().getPosition());
			if(distance <= SAFE_DISTANCE) return;
		}
		
		// nothing within safety distance - safe now
		isScared = false;
		setSpeed(SPEED_NORMAL);
		normalBehaviour.recalculateCourse();
		behaviour = normalBehaviour;
	}
}
