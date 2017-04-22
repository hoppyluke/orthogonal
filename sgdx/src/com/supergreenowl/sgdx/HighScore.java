package com.supergreenowl.sgdx;

/**
 * High score record.
 * @author Luke
 */
public class HighScore {

	/**
	 * Score achieved.
	 */
	public int score;
	
	/**
	 * Name associated with the score (if any).
	 */
	public String name;
	
	/**
	 * Creates an exact copy of this score. 
	 */
	HighScore copy() {
		HighScore clone = new HighScore();
		clone.score = score;
		clone.name = name;
		return clone;
	}
}
