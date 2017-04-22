package com.supergreenowl.slugs.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.supergreenowl.sgdx.GameClock;
import com.supergreenowl.sgdx.HighScore;
import com.supergreenowl.sgdx.HighScoreBoard;
import com.supergreenowl.sgdx.ShapeScreen;
import com.supergreenowl.slugs.model.Creature;
import com.supergreenowl.slugs.model.World;

public class WorldScreen extends ShapeScreen {

	private static final float RESET_PAUSE_DURATION = 1.5f;
	private static final String SCORES_NAME = "com.supergreenowl.slugs.scores";
	private static final String RETRY_TEXT = "try again?";
	
	private static final float TEXT_LINE_HEIGHT = 20f;
	
	private CreatureRenderer creatureRenderer;
	private LineRenderer lineRenderer;
	private TextRenderer textRenderer;
	private World world;
	
	private ColourPalette palette;
	
	private GameClock pauseClock = new GameClock();
	private boolean isReadyToReset = false;
	
	private HighScoreBoard scores = null;
	private boolean isScoreChecked = false;
	private boolean isHighScore = false;
	
	private HighScore previousScore;
	
	public WorldScreen(Game game, World world) {
		super(game, new FitViewport(world.getWidth(), world.getHeight()));
		
		setBackgroundColour(Color.BLACK);
		
		this.world = world;
		
		palette = ColourPalette.randomPalette();
		
		creatureRenderer = new CreatureRenderer(palette);
		lineRenderer = new LineRenderer(palette);
		textRenderer = new TextRenderer(palette, 2f);
		setDrawFilled(true);
		
		scores = HighScoreBoard.load(SCORES_NAME);
		if(scores == null) {
			scores = new HighScoreBoard(1);
		}
	}

	
	@Override
	protected void drawFilled(ShapeRenderer renderer, float elapsedSeconds) {
		
		// draw world background
		renderer.setColor(palette.background);
		renderer.rect(0f, 0f, world.getWidth(), world.getHeight());
		
		// Draw lines first so that creatures are on top of lines
		lineRenderer.renderLines(renderer, world.getLines());
				
		for(Creature c : world.getCreatures()) {
			creatureRenderer.render(renderer, c);
		}

		boolean isGameOver = world.isGameOver();
		int level = world.getLevel();
		int seconds = world.getClock().getTotalSeconds();
		
		if(!isGameOver) {
			textRenderer.setAlignment(TextRenderer.HorizontalAlignment.RIGHT, TextRenderer.VerticalAlignment.BOTTOM);
			textRenderer.render(renderer, seconds, world.getWidth() - 10f, 10f);
			if(level > 1) {
				textRenderer.setAlignment(TextRenderer.HorizontalAlignment.LEFT, TextRenderer.VerticalAlignment.BOTTOM);
				textRenderer.render(renderer, level, 10f, 10f);
			}
		}
		
		if(isGameOver) {
			
			float x = world.getWidth() / 2f;
			float y = world.getHeight() / 2f;
			
			textRenderer.setAlignment(TextRenderer.HorizontalAlignment.CENTRE, TextRenderer.VerticalAlignment.MIDDLE);
			
			if(isScoreChecked && isHighScore)
				textRenderer.render(renderer, "new record", x, y + (TEXT_LINE_HEIGHT * 3f));
			
			textRenderer.render(renderer, "level " + world.getLevel(), x, y + TEXT_LINE_HEIGHT + TEXT_LINE_HEIGHT);
			textRenderer.render(renderer, "survived " + seconds + " seconds", x, y);
			textRenderer.render(renderer, getGameOverText(world.getGameOverReason()), x, y - TEXT_LINE_HEIGHT);
			
			y -= TEXT_LINE_HEIGHT * 3f;
			
			if(isReadyToReset)
				textRenderer.render(renderer, RETRY_TEXT, x, y);
			
			if(previousScore != null) {
				y = world.getHeight() - TEXT_LINE_HEIGHT - TEXT_LINE_HEIGHT;
				textRenderer.render(renderer, "record " + previousScore.score + " seconds", x, y);
			}
		}
	}
	
	@Override
	protected void update(float elapsed) {
		// Update world
		world.update(elapsed);
		
		if(world.isGameOver()) {
			
			// Check if last score was a high score (once)
			if(!isScoreChecked) {
				isScoreChecked = true;
				int score = world.getClock().getTotalSeconds();
				previousScore = scores.getTopScore();
				isHighScore = scores.addScore(score);
				if(isHighScore) scores.save(SCORES_NAME);
			}
			
			pauseClock.tick(elapsed);
			if(pauseClock.getTime() > RESET_PAUSE_DURATION) isReadyToReset = true;
		}
	}

	@Override
	protected void onTouch(float x, float y) {
		
		if(isReadyToReset) {
			pauseClock.reset();
			world.reset();
			isReadyToReset = false;
			isScoreChecked = false;
			isHighScore = false;
			previousScore = null;
			
			setPalette(ColourPalette.randomPalette()); // change colour on retry
		}
		else if(!world.isGameOver()) {
			// Send direction to runner
			world.turnRunner(x, y);
		}
	}
	
	/**
	 * Sets the colour palette currently in use.
	 * @param palette
	 */
	private void setPalette(ColourPalette palette) {
		if(palette == this.palette || palette == null) return;
		
		setBackgroundColour(palette.background);
		lineRenderer.palette = palette;
		textRenderer.palette = palette;
		creatureRenderer.palette = palette;
		this.palette = palette;
	}
	
	private static String getGameOverText(World.GameOverReason reason) {
		switch (reason) {
		case OUT_OF_BOUNDS:
			return "stay on the screen";
		case LINE:
			return "avoid the lines";
		case CREATURE:
			return "avoid other creatures";

		default:
			return "game over";
		}
	}
}
