package com.supergreenowl.slugs.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.supergreenowl.slugs.model.World;

public class SplashScreen extends com.supergreenowl.sgdx.SplashScreen {

	private static final String LOGO_PATH = "supergreenowl.png";
	
	public SplashScreen(Game game) {
		super(game, LOGO_PATH);
		setBackgroundColour(Color.WHITE);
	}

	/**
	 * Creates the game world and a world screen to display it.
	 */
	@Override
	protected void load() {
		World w = new World(240f, 400f);
		WorldScreen worldScreen = new WorldScreen(getGame(), w);
		
		setNextScreen(worldScreen);
	}
}
