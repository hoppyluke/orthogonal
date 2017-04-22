package com.supergreenowl.slugs.model;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.supergreenowl.sgdx.GameClock;
import com.supergreenowl.sgdx.Clock;
import com.supergreenowl.sgdx.Resettable;

/**
 * The world in which the game occurs.
 * @author Luke
 *
 */
public class World implements Resettable {

	/**
	 * Minimum viable length of a line. Any lines smaller than this length will be removed.
	 */
	private static final float MINIMUM_LINE_LENGTH = 3f;
	
	private static final float[] LEVEL_DURATION =  { 30f, 15f };
	private static final float MIN_LEVEL_DURATION = LEVEL_DURATION[LEVEL_DURATION.length - 1];
	
	private static final float DIFFICULTY_INCREASE_PER_LEVEL = 0.2f;
	
	/**
	 * Pseudo-random number generator instance for controlling randomised events
	 */
	public static final Random generator = new Random();

	private final float width, height;
	
	private float difficultyModifier = 1f;
	private int level = 0;
	private float timeOfNextLevelUp = 0f;

	private Navigator navigator = new Navigator(this);
	
	private WorkerNest workerNest = new WorkerNest(this);
	private LayerNest layerNest = new LayerNest(this);

	private Array<Line> lines = new Array<Line>(false, 32);
	private Array<Line> linesToAdd = new Array<Line>(false, 8);
	
	/* To workaround the fact that Array<T> doesn't support concurrent iteration
	 * (and because it is apparently slightly quicker) all iterations of the
	 * creatures array within this class should use for(int i = 0, n = creatures.size; i < n; i++)...
	 * syntax. That lets the publicly exposed iterator be used externally.
	 */
	private Array<Creature> creatures = new Array<Creature>(false, 32);
	private Array<Creature> creaturesToRemove = new Array<Creature>(false, 8);
	
	private Runner runner;

	private boolean isGameOver = false;
	private GameOverReason gameOverReason = GameOverReason.NONE;
	
	private GameClock clock = new GameClock();
	
	private Pool<Line> linePool = new Pool<Line>() {
		@Override
		protected Line newObject() {
			return new Line();
		}
	};
	
	/**
	 * Creates a new world with of the specified size.
	 * @param width World width.
	 * @param height World height.
	 */
	public World(float width, float height) {
		this.width = width;
		this.height = height;
		
		runner = new Runner();
		runner.place(width / 2f, 0f, Direction.UP);
		add(runner);
	}

	/**
	 * Gets the width of this world.
	 * @return
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Gets the height of this world.
	 * @return
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * Gets a collection of all the lines in this world.
	 * @return Collection of lines.
	 */
	public Iterable<Line> getLines() {
		return lines;
	}

	/**
	 * Adds a line to this world.
	 * Lines are not directly added but queued for until the next {@link #update(float)} or an explicit call
	 * to {@link #flushLinesQueue()} is made.
	 * @param line to add.
	 */
	public void addLine(Line line) {
		// Cannot add directly as iteration might be in progress
		linesToAdd.add(line);
	}
	
	/**
	 * Adds a creature to this world.
	 * @param c Creature to add.
	 */
	public void add(Creature c) {
		c.setWorld(this);
		creatures.add(c);
	}

	/**
	 * Gets a collection of all the creatures in this world.
	 * Note that nested/concurrent iteration of this collection is not supported.
	 * @return Collection of creatures.
	 */
	public Iterable<Creature> getCreatures() {
		return creatures;
	}
	
	/**
	 * Gets a navigator constrained to this world's dimensions.
	 * @return Navigator.
	 */
	public Navigator getNavigator() {
		return navigator;
	}

	/**
	 * Turns the runner in this world toward the specified coordinates.
	 * @param x
	 * @param y
	 */
	public void turnRunner(float x, float y) {
		if(isGameOver) return;
		
		// Turn runner towards touch
		Segment head = runner.getHead();
		Direction runnerDirection = head.getDirection();
		
		if(runnerDirection.isHorizontal()) {
			float runnerY = head.getPosition().getY();
			if(y > runnerY) runner.turn(Direction.UP);
			else if(y < runnerY) runner.turn(Direction.DOWN);
		}
		else {
			float runnerX = head.getPosition().getX();
			if(x < runnerX) runner.turn(Direction.LEFT);
			else if(x > runnerX) runner.turn(Direction.RIGHT);
		}
	}
	
	/**
	 * Updates world state.
	 * @param elapsedTime Time in seconds since last update.
	 */
	public void update(float elapsedTime) {
		if(isGameOver) return;
		clock.tick(elapsedTime);

		flushLinesQueue();

		// Generate creatures
		workerNest.spawnCreatures(elapsedTime);
		layerNest.spawnCreatures(elapsedTime);
		
		for(int i = 0, n = creatures.size; i < n; i++) {
			Creature c = creatures.get(i);
			c.move(elapsedTime);
		}
		
		reapCreatures();
		cleanUpSmallLines();

		// Check if runner has hit a line
		Box collisionBox = runner.getCollisionBox();
		
		for(int i = 0, n = lines.size; i < n; i++) {
			Line l = lines.get(i);
			
			if(runner != l.getOwner() && collisionBox.intersects(l.getBoundingBox())) {
				isGameOver = true;
				gameOverReason = GameOverReason.LINE;
				break;
			}
		}
		
		// Update level
		calculateLevel();
	}

	/**
	 * Actually adds any lines that have been queued for addition to this world.
	 */
	public void flushLinesQueue() {
		// Actually add any new slime trails to the world
		if(linesToAdd.size > 0) {
			lines.addAll(linesToAdd);
			linesToAdd.clear();
		}
	}
	
	/**
	 * Gets the world clock.
	 * @return Clock.
	 */
	public Clock getClock() {
		return clock;
	}
	
	/**
	 * Gets the pool of lines used in this world.
	 * @return
	 */
	Pool<Line> getLinePool() {
		return linePool;
	}
	
	/**
	 * Removes any creatures that have been eaten by other creatures
	 * or moved out of bounds.
	 */
	private void reapCreatures() {
		
		creaturesToRemove.clear();
		
		// Critters that have died are queued and then all removed at once as
		// a critter that has just gone out of bounds could have just eaten another critter
		
		for(int i = 0, n = creatures.size; i < n; i++) {
			Creature c = creatures.get(i);
			
			// Check if this critter has gone out of bounds
			Segment head = c.getHead();
			Point p = head.getPosition();
			Box b = head.getBoundingBox();
			float x = p.getX();
			float y = p.getY();

			// creatures spawn off screen so only check for out of bounds once they have entered the world
			if(c.hasEnteredWorld()) {
				if(x < 0f || x > width || y < 0f || y > height) {
					if(runner == c) {
						isGameOver = true;
						gameOverReason = GameOverReason.OUT_OF_BOUNDS;
					}
					else creaturesToRemove.add(c);
				}
			}
			
			// Check if there are any other critters eaten by current critter
			if(c.canEatCreatures()) {
				for(int j = 0; j < n; j++) {
					if(i == j) continue;
					Creature otherCreature = creatures.get(j);
					if(!otherCreature.canBeEaten()) continue;
					
					if(b.intersects(otherCreature.getHead().getBoundingBox())) {
						if(runner == otherCreature) {
							isGameOver = true;
							gameOverReason = GameOverReason.CREATURE;
						}
						else creaturesToRemove.add(otherCreature);
					}
				}
			}
		}
		
		freeAllCreatures(creaturesToRemove);
		creatures.removeAll(creaturesToRemove, true); // actually remove dead creatures
	}
	
	/**
	 * Returns all creatures in the specified colleciton to the appropriate pool.
	 * @param creatures
	 */
	private void freeAllCreatures(Array<Creature> creatures) {
		for(int i = 0, n = creatures.size; i < n; i++) {
			Creature c = creatures.get(i);
			if(c instanceof Worker) workerNest.free((Worker)c);
			else if(c instanceof Layer) layerNest.free((Layer)c);
		}
	}
	
	/**
	 * Removes any small lines.
	 */
	private void cleanUpSmallLines() {
		Iterator<Line> lineIterator = lines.iterator();
		
		while(lineIterator.hasNext()) {
			Line l = lineIterator.next();
			if(l.getOwner() == null && l.getLength() < MINIMUM_LINE_LENGTH) {
				lineIterator.remove();
				linePool.free(l);
			}
		}
	}
	
	/**
	 * Reason that the game ended.
	 * @author Luke
	 *
	 */
	public static enum GameOverReason {
		/** Game is not over. */
		NONE,
		/** Game is over because player went out of bounds. */
		OUT_OF_BOUNDS,
		/** Game is over because player hit another creature. */
		CREATURE,
		/** Game is over because player hit a line. */
		LINE
	}
	
	/**
	 * Gets a flag indicating if the current game is over.
	 * @return
	 */
	public boolean isGameOver() {
		return isGameOver;
	}
	
	/**
	 * Gets the reason that the game ended.
	 * @return
	 */
	public GameOverReason getGameOverReason() {
		return gameOverReason;
	}
	
	/**
	 * Gets the current difficulty level of this world.
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Calculates the difficulty level for this world based on the current score.
	 * This must be called before reset as that resets the score :).
	 */
	public void calculateLevel() {
		
		if(clock.getTime() >= timeOfNextLevelUp) {
			
			// increase current level
			
			// calculate seconds to survive per level up based on current difficulty
			float levelUpIncrement = level < LEVEL_DURATION.length ?
					LEVEL_DURATION[level] : MIN_LEVEL_DURATION;
			
			timeOfNextLevelUp += levelUpIncrement;
			
			level++;
			
			// get 10% harder for every difficulty level
			if(level == 0) difficultyModifier = 1f;
			else difficultyModifier = 1f + (float)(level - 1) * DIFFICULTY_INCREASE_PER_LEVEL;
			
			// increase creature speeds by difficulty
			workerNest.setSpeedMultiplier(difficultyModifier);
			layerNest.setSpeedMultiplier(difficultyModifier);
			
			for(int i = 0, n = creatures.size; i < n; i++) {
				creatures.get(i).setSpeedMultiplier(difficultyModifier);
			}
		}
	}
	
	/**
	 * Resets this world to the initial state. Does not recalculate level.
	 */
	@Override
	public void reset() {
		clock.reset();
		
		level =  0;
		timeOfNextLevelUp = 0f;
		calculateLevel();
		
		freeAllCreatures(creaturesToRemove);
		creaturesToRemove.clear();
		freeAllCreatures(creatures);
		creatures.clear();
		
		layerNest.reset();
		workerNest.reset();
		
		linePool.freeAll(linesToAdd);
		linesToAdd.clear();
		linePool.freeAll(lines);
		lines.clear();
		
		// Reset runner, put it back to start point and add it back to this world
		runner.reset();
		runner.place(width / 2f, 0f, Direction.UP);
		add(runner);
		
		gameOverReason = GameOverReason.NONE;
		isGameOver = false;
	}
}
