package com.supergreenowl.sgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Keeps track of a number of high scores.
 * Supports scoring systems were either higher or lower scores are more desirable.
 * @author Luke
 *
 */
public class HighScoreBoard {

	private int numberOfScores;
	private HighScore[] scores;
	private boolean isHigherBetter;
	
	public HighScoreBoard() {
		
	}
	
	/**
	 * Creates a new high score board where higher scores are better.
	 * @param numberOfScores Number of scores to track on the board,
	 */
	public HighScoreBoard(int numberOfScores) {
		this(numberOfScores, true);
	}
	
	/**
	 * Creates a new high score board.
	 * @param numberOfScores Number of scores to track on the board,
	 * @param isHigherBetter Determines if higher or lower score value is more desirable.
	 */
	public HighScoreBoard(int numberOfScores, boolean isHigherBetter) {
		this.numberOfScores = numberOfScores;
		this.scores = new HighScore[numberOfScores];
		this.isHigherBetter = isHigherBetter;
	}
	
	/**
	 * Attempts to add a new score to the board.
	 * @param score Score to add.
	 * @return True if the score made it to the high score board; false otherwise.
	 */
	public boolean addScore(HighScore score) {
		int insertionPoint = -1;
		
		for(int i = 0; i < scores.length; i++) {
			HighScore h = scores[i];
			if(h == null
					|| (isHigherBetter && score.score > h.score)
					|| (!isHigherBetter && score.score < h.score)) {
				insertionPoint = i;
				break;
			}
		}
		
		if(insertionPoint == -1) return false; // not a high score
		
		if(insertionPoint < scores.length - 1) {
			// score is not last on board so need to move some other scores down the board
			int numberOfScoresToCopy = scores.length - insertionPoint - 1;
			System.arraycopy(scores, insertionPoint, scores, insertionPoint + 1, numberOfScoresToCopy);
		}
	
		// insert new score to board
		scores[insertionPoint] = score;
		return true; // high score!
	}
	
	/**
	 * Attempts to add a new score (without an associated name) to the board.
	 * @param score Score to add.
	 * @return True if the score made it to the high score board; false otherwise.
	 */
	public boolean addScore(int score) {
		HighScore hs = new HighScore();
		hs.score = score;
		return addScore(hs);
	}
	
	/**
	 * Attempts to add a new score to the board with an associated name.
	 * @param score Score to add.
	 * @param name Name to associate with the score on the board.
	 * @return True if the score made it to the high score board; false otherwise.
	 */
	public boolean addScore(int score, String name) {
		HighScore hs = new HighScore();
		hs.score = score;
		hs.name = name;
		return addScore(hs);
	}
	
	/*
	 * Gets an ordered array of scores on this board.
	 * This returns a new array each time - callers should cache the returned array locally.
	 */
	public HighScore[] getScores() {
		
		// return a copy of scores array
		// prevents callers from messing with actual array 
		HighScore[] clone = new HighScore[numberOfScores];
		for(int i = 0; i < scores.length; i++) {
			HighScore hs = scores[i];
			if(hs == null) clone[i] = null;
			else clone[i] = hs.copy();
		}
		
		return clone;
	}
	
	/**
	 * Gets the high score at the specified position.
	 * @param index Zero-based index of the score (0 is top score).
	 * @return Score.
	 */
	public HighScore getScore(int index) {
		if(index < 0 || index >= numberOfScores)
			throw new IllegalArgumentException("index out of range");
		
		return scores[index];
	}
	
	/**
	 * Gets the top score.
	 * @return Score.
	 */
	public HighScore getTopScore() {
		return scores[0];
	}
	
	/**
	 * Persists this score board to local storage. 
	 * @param boardName Name to save the board as.
	 */
	public void save(String boardName) {
		Json json = new Json();
		String serializedBoard = json.toJson(this);
		Gdx.files.local(boardName).writeString(serializedBoard, false);
	}
	
	/**
	 * Loads a saved score board.
	 * @param boardName Name of the saved board.
	 * @return Board or null if no board with the specified name could be found.
	 */
	public static HighScoreBoard load(String boardName) {
		FileHandle savedBoard = Gdx.files.local(boardName);
		if(!savedBoard.exists()) return null;
		
		Json json = new Json();
		return json.fromJson(HighScoreBoard.class, savedBoard);
	}
}
