package com.supergreenowl.sgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A screen which displays a logo centred while loading content in the background.
 * Guarantees to be shown for at a specified minimum duration or until content has loaded
 * (whichever is later).
 * 
 * <p>To create a splash screen, derive from this class and override the {@link #load()} method to
 * implement loading of resources and call {@link #setNextScreen(Screen)} to specify the screen to
 * show after the splash screen.
 *   
 * @author Luke
 */
public abstract class SplashScreen extends Screen {
	
	private final SpriteBatch batch = new SpriteBatch();
	private final Texture logo;
	private float logoX, logoY;
	
	private boolean isLoadingComplete = false;
	private boolean isRendered = false;
	
	private final float minimumDuration;
	private final GameClock clock = new GameClock();
	
	private Screen nextScreen;
	
	/**
	 * Creates a new SplashScreen that shows for at least 3 seconds.
	 * @param game Game that screen is part of.
	 * @param logoPath Internal file path to the logo to show on splash screen.
	 */
	public SplashScreen(Game game, String logoPath) {
		this(game, logoPath, 3f);
	}
	
	/**
	 * Creates a new SplashScreen.
	 * @param game Game that screen is part of.
	 * @param logoPath Internal file path to the logo to show on splash screen.
	 * @param minimumDuration Minimum time in seconds that the splash screen should show for.
	 */
	public SplashScreen(Game game, String logoPath, float minimumDuration) {
		super(game, new ScreenViewport());
		logo = new Texture(logoPath);
		this.minimumDuration = minimumDuration;
		setTouchInputHandled(false);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		
		logoX = (float)(width - logo.getWidth()) / 2f;
		logoY = (float)(height - logo.getHeight()) / 2f;
	}

	@Override
	public void show() {
		super.show();
		isRendered = false;
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		logo.dispose();
	}

	@Override
	protected void update(float elapsed) {
		clock.tick(elapsed);
		
		if(!isRendered) return; // do nothing until rendered once
		
		if(!isLoadingComplete) {
			load();
			isLoadingComplete = true;
		}
		else if(clock.getTime() >= minimumDuration) {
			// loading is complete
			// and screen has been shown for minimum duration
			
			if(nextScreen != null) {
				getGame().setScreen(nextScreen);
				this.dispose();
			}
		}
	}

	@Override
	protected void draw(float elapsed) {
		batch.begin();
		batch.draw(logo, logoX, logoY);
		batch.end();
		
		isRendered = true;
	}
	
	/**
	 * Sets the next screen to be shown after this splash screen.
	 * @param nextScreen
	 */
	protected void setNextScreen(Screen nextScreen) {
		this.nextScreen = nextScreen;
	}
	
	/**
	 * Derived splash screens should override this method to load resources.
	 */
	protected abstract void load();
}
