package com.supergreenowl.sgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class Screen implements com.badlogic.gdx.Screen {
	
	private static final int CLEAR_MASK = GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT;
	
	private final Game game;
	private final Viewport viewport;
	private Color backgroundColour;
	
	private boolean isTouchInputHandled = true;
	private boolean isDisposed = false;
	
	private Vector2 touch = new Vector2();
	
	/**
	 * Creates a new screen.
	 * @param viewport Viewport to be used by this screen.
	 */
	public Screen(Game game, Viewport viewport) {
		this.game = game;
		this.viewport = viewport;
		Gdx.input.setInputProcessor(new ScreenInputAdapter());
	}
	
	@Override
	public void dispose() {
		// mark as disposed to prevent drawing
		isDisposed = true;
	}

	@Override
	public void hide() {
		// default is empty
	}

	@Override
	public void pause() {
		// default is empty
	}

	@Override
	public void render(float elapsedTime) {
		
		// update model
		update(elapsedTime);
		
		if(!isDisposed) { // do not draw if dispose is called during update()
			// Render
			Gdx.gl.glClear(CLEAR_MASK);
			draw(elapsedTime);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void resume() {
		// default is empty
	}

	@Override
	public void show() {
		// set background colour
		// clear colour is global OpenGL setting so only apply when screen is actually shown 
		if(backgroundColour != null) {
			Gdx.gl.glClearColor(backgroundColour.r, backgroundColour.g, backgroundColour.b, backgroundColour.a);
		}
	}
	
	/**
	 * Sets a switch that controls whether or not this screen handles touch/click input events.
	 * Touch input handling is on by default.
	 * @param handle True to handle touch events, false to not handle touch events.
	 */
	public void setTouchInputHandled(boolean handle) {
		this.isTouchInputHandled = handle;
	}
	
	/**
	 * Gets the game that this screen is part of.
	 * @return
	 */
	protected Game getGame() {
		return this.game;
	}
	
	/**
	 * Sets the background colour for this screen.
	 * The default colour is black.
	 * @param c New background colour.
	 */
	protected void setBackgroundColour(Color c) {
		this.backgroundColour = c;
	}
	
	/**
	 * Gets the current viewport for this screen.
	 * @return
	 */
	protected Viewport getViewport() {
		return viewport;
	}
	
	/**
	 * Gets the projection matrix for the camera.
	 * @return Projection matrix.
	 */
	protected Matrix4 getCameraProjectionMatrix() {
		return viewport.getCamera().combined;
	}
	
	/**
	 * Logic to update the model.
	 * @param elapsed Time in seconds that has elapsed since the last update.
	 */
	protected abstract void update(float elapsed);
	
	/**
	 * Logic to render the screen.
	 * @param elapsed Time in seconds that has elapsed since the last draw call.
	 */
	protected abstract void draw(float elapsed);
	
	/**
	 * Event raised when the user has touched/clicked on the screen at the specified coordinates.
	 * This will only be called if this screen is configured to handle touch input (which is the
	 * default setting). The default implementation is empty.
	 * @see #setTouchInputHandled(boolean)
	 * @param x x coordinate of touch/click in world space.
	 * @param y y coordinate of touch/click in world space.
	 */
	protected void onTouch(float x, float y) {
		// default empty implementation.
	}
	
	/**
	 * An input adapter that handles touch down events and forwards them to the onTouch method.
	 * @author Luke
	 *
	 */
	private class ScreenInputAdapter extends InputAdapter {

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if(!isTouchInputHandled) return false;
			
			touch.set(screenX, screenY);
			viewport.unproject(touch);
			
			onTouch(touch.x, touch.y);
			return true;
		}
	}
}
