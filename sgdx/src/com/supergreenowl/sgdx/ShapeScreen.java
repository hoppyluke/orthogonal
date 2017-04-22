package com.supergreenowl.sgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Screen that uses simple shapes to display its content.
 * Subtypes should call one or more of {@link #setDrawFilled(boolean)},
 * {@link #setDrawLines(boolean)} or {@link #setDrawPoints(boolean)} to enable drawing
 * and override the corresponding draw method(s).
 * @author Luke
 *
 */
public abstract class ShapeScreen extends Screen {

	private ShapeRenderer renderer;
	
	private boolean drawLines, drawFilled, drawPoints;
	
	public ShapeScreen(Game game, Viewport viewport) {
		super(game, viewport);
		renderer = new ShapeRenderer();
	}

	@Override
	public void dispose() {
		super.dispose();
		renderer.dispose();
	}

	@Override
	protected void draw(float elapsed) {
		if(drawLines) {
			renderer.begin(ShapeType.Line);
			drawLines(renderer, elapsed);
			renderer.end();
		}
		
		if(drawFilled) {
			renderer.begin(ShapeType.Filled);
			drawFilled(renderer, elapsed);
			renderer.end();
		}
		
		if(drawPoints) {
			renderer.begin(ShapeType.Point);
			drawPoints(renderer, elapsed);
			renderer.end();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		renderer.setProjectionMatrix(getCameraProjectionMatrix());
	}

	/**
	 * Enables or disables drawing of outline shapes in this screen.
	 * Drawing is off by default.
	 * @param drawLines True to enable drawing, false to disable.
	 */
	protected void setDrawLines(boolean drawLines) {
		this.drawLines = drawLines;
	}
	
	/**
	 * Enables or disables drawing of filled shapes in this screen.
	 * Drawing is off by default.
	 * @param drawLines True to enable drawing, false to disable.
	 */
	protected void setDrawFilled(boolean drawFilled) {
		this.drawFilled = drawFilled;
	}
	
	/**
	 * Enables or disables drawing of points in this screen.
	 * Drawing is off by default.
	 * @param drawLines True to enable drawing, false to disable.
	 */
	protected void setDrawPoints(boolean drawPoints) {
		this.drawPoints = drawPoints;
	}
	
	/**
	 * Draws outline shapes.
	 * The default implementation is empty and should be overridden by screens that draw outline shapes.
	 * @param renderer Renderer to perform drawing.
	 * @param elapsedSeconds Number of seconds since last render call.
	 * @see #setDrawLines(boolean)
	 */
	protected void drawLines(ShapeRenderer renderer, float elapsedSeconds) { }
	
	/**
	 * Draws filled shapes.
	 * The default implementation is empty and should be overridden by screens that draw filled shapes.
	 * @param renderer Renderer to perform drawing.
	 * @param elapsedSeconds Number of seconds since last render call.
	 * @see #setDrawFilled(boolean)
	 */
	protected void drawFilled(ShapeRenderer renderer, float elapsedSeconds) { }
	
	/**
	 * Draws points.
	 * The default implementation is empty and should be overridden by screens that draw points.
	 * @param renderer Renderer to perform drawing.
	 * @param elapsedSeconds Number of seconds since last render call.
	 * @see #setDrawPoints(boolean)
	 */
	protected void drawPoints(ShapeRenderer renderer, float elapsedSeconds){ }
}
