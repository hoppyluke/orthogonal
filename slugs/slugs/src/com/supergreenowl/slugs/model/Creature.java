package com.supergreenowl.slugs.model;

import java.util.Iterator;

import com.supergreenowl.sgdx.Resettable;

/**
 * A creature in the world. Creates are represented by a list of segments.
 * @author Luke
 * @see Segment
 */
public abstract class Creature implements Iterable<Segment>, Resettable {
	
	/**
	 * The base speed for all creatures.
	 */
	static final float BASE_SPEED = 24f;
	
	private final Segment head = new Segment();
	private Segment tail = head;
	private int segments = 1;
	private float speed = BASE_SPEED;
	private float baseSpeed = BASE_SPEED;
	
	private final float width, height, growthSize;
	private final int maxSegments;
	private final Box lastMove = new Box();
	private float distance = 0f;
	private boolean hasEnteredWorld = false;
	
	private float speedMultiplier = 1f;
	
	private final boolean canBeEaten, canEatCreatures;
	
	private Segment[] segmentPool;
	private SegmentIterator iterator = new SegmentIterator(head);
	
	private World world;
	
	/**
	 * Creates a new creature of the specified size.
	 * @param width Width of this creature (when it is facing up).
	 * @param height Height of this creature (when it is facing up).
	 * @param canBeEaten Indicates if this creature can be eaten by other creatures.
	 * @param canEatCreatures Indicates if this creature can eat other 
	 */
	public Creature(float width, float height, boolean canBeEaten, boolean canEatCreatures) {
		if(width > height) {
			throw new IllegalArgumentException("creature width must not be greater than height");
		}
		
		this.width = width;
		this.height = height;
		this.growthSize = 2f * width;
		this.maxSegments = (int)Math.floor(height / width);
		this.canBeEaten = canBeEaten;
		this.canEatCreatures = canEatCreatures;
		
		// if this is multi-segment creature, create a pool of segments to reuse 
		if(maxSegments > 1) {
			segmentPool = new Segment[maxSegments - 1]; // head is never in pool
			
			for(int i = 0; i < segmentPool.length; i++) {
				segmentPool[i] = new Segment();
			}
		}
	}
	
	public float getWidth() {
		return this.width;
	}

	public boolean canEatCreatures() {
		return this.canEatCreatures;
	}
	
	public boolean canBeEaten() {
		return this.canBeEaten;
	}
	
	/**
	 * Sets the current world that this creature is in.
	 * @param world World that creature resides in.
	 */
	public void setWorld(World world) {
		this.world = world;
	}
	
	/**
	 * Places this creature in the world.
	 * The creature is positioned such that the front centre of its head is at the specified point.
	 * @param x x coordinate to place creature at.
	 * @param y y coordinate to place creature at.
	 * @param direction Direction for creature to face. 
	 */
	public void place(float x, float y, Direction direction) {
		
		if(segments > 1) throw new IllegalStateException("cannot place a multi-segment creature");
		
		// offset point so that front of head is at the specified point
		float offset = height / 2f;
		
		if(direction == Direction.RIGHT) x -= offset;
		else if(direction == Direction.LEFT) x += offset;
		else if(direction == Direction.UP) y -= offset;
		else if(direction == Direction.DOWN) y += offset;
		
		head.place(x, y, width, height, direction);
	}
	
	/**
	 * Sets the speed multiplier for this creature and recalculates the current speed.
	 * @param speedMultiplier
	 */
	public void setSpeedMultiplier(float speedMultiplier) {
		this.speedMultiplier = speedMultiplier;
		this.speed = this.baseSpeed * speedMultiplier;
	}
	
	/**
	 * Sets the current speed for this creature.
	 * @param speed Speed in world units/second.
	 */
	public void setSpeed(float speed) {
		this.baseSpeed = speed;
		this.speed = speed * this.speedMultiplier;
	}
	
	/**
	 * Gets the current speed of this creature.
	 * @return Speed.
	 */
	public float getSpeed() {
		return speed;
	}
	
	/**
	 * Gets a flag indicating if this creature has fully entered the world yet.
	 * @return
	 */
	public boolean hasEnteredWorld() {
		return hasEnteredWorld;
	}
	
	/**
	 * Turns this creature to face the specified direction.
	 * Adds a new segment if required.
	 * @param direction New direction to face.
	 */
	public void turn(Direction direction) {
		// cannot turn to current direction or opposite direction
		if(direction == null || direction.isHorizontal() == head.getDirection().isHorizontal())
			return;

		// A multi-segment creature grows when it turns
		if(maxSegments > 1) {
			if(!canGrow()) return;
			
			Segment next = getAvailableSegment();
			segments++;
			
			head.split(next, width);
			
			// if new segment is last, mark it as tail
			if(next.getNext() == null) tail = next;
		}
		
		// Turn head to face the new direction
		head.setDirection(direction);
	}
	
	/**
	 * Moves this creature forward in it's current direction.
	 * @param time Number of seconds to move for.
	 */
	public void move(float time) {
		
		think(); // allow subclasses to run AI
		
		distance = speed * time;
		
		lastMove.set(head.getBoundingBox());
		lastMove.grow(distance, head.getDirection());
		
		float currentSize = 0f;
		
		for(Segment s : this) {
			s.move(distance);
			currentSize += s.getSize();
		}
		
		if(!tail.isActive()) {
			if(head == tail) throw new IllegalStateException("head is inactive");
			
			// remove last segment
			segments--;
			Segment oldTail = tail;
			tail = tail.getPrevious();
			tail.insert(null);
			oldTail.clear();
		}
		
		// turning can cause creatures to grow slowly over time
		// (rounding error?) so shrink down to expected size 
		if(currentSize > height) tail.shrink(currentSize - height);
				
		if(!hasEnteredWorld) {
			Point p = head.getPosition();
			hasEnteredWorld = p.getX() > 0f
					&& p.getX() < world.getWidth()
					&& p.getY() > 0f
					&& p.getY() < world.getHeight();
				
		}
	}
	
	/**
	 * Gets the total distance moved by this creature on its last move.
	 * @return Last distance moved.
	 */
	public float getLastMoveDistance() {
		return distance;
	}
	
	/**
	 * Gets the segment iterator for this creature.
	 * Note that this doesn't support multiple concurrent/nested iterations. 
	 */
	@Override
	public Iterator<Segment> iterator() {
		// re-use same iterator each time
		// avoids multiple instantiations per creature per frame
		iterator.current = head; // reset before use
		
		return iterator;
	}
	
	@Override
	public void reset() {
		segments = 1;
		head.reset();
		tail = head;
		
		if(segmentPool != null) {
			for(int i = 0; i < segmentPool.length; i++) {
				segmentPool[i].reset();
			}
		}
		
		distance = 0f;
		hasEnteredWorld = false;
		speedMultiplier = 1f;
		speed = BASE_SPEED;
		baseSpeed = BASE_SPEED;
	}

	/**
	 * Determines if this Creature can add another segment.
	 * @return
	 */
	protected boolean canGrow() {
		return segments < maxSegments && head.getSize() >= growthSize;
	}
	
	/**
	 * Gets the head segment of this creature.
	 * @return Head segment.
	 */
	protected Segment getHead() {
		return head;
	}
	
	/**
	 * Gets a box that defines the area covered in the last move of this creature.
	 * @return
	 */
	protected Box getLastMove() {
		return lastMove;
	}
	
	/**
	 * Gets the world that this creature belongs to.
	 * @return
	 */
	protected World getWorld() {
		return world;
	}
	
	/**
	 * Runs any AI logic for this creature. The default implementation is empty -
	 * subclasses should override this method to run their AI.
	 */
	protected void think() {
		// Default empty implementation.
	}
	
	/**
	 * Gets the next available segment from the pool.
	 * Any segment that is inactive is considered available.
	 * @return Available segment.
	 */
	private Segment getAvailableSegment() {
		// linearly search segment pool for first non-active segment
		for(int i = 0; i < segmentPool.length; i++) {
			Segment current = segmentPool[i];
			if(!current.isActive()) return current;
		}
		
		throw new IllegalStateException("cannot add segment with no segments in pool");
	}
	
	/**
	 * Iterates over all the segments of a Creature.
	 * Does not support multiple iterations simultaneously.
	 * @author Luke
	 *
	 */
	public static class SegmentIterator implements Iterator<Segment> {
		
		private Segment current = null;
		
		public SegmentIterator(Segment segment) {
			current = segment;
		}
		
		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public Segment next() {
			Segment currentSegment = current;
			if(current != null) current = current.getNext();
			
			return currentSegment;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}