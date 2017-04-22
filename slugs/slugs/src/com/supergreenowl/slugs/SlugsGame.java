package com.supergreenowl.slugs;

import com.badlogic.gdx.Game;
import com.supergreenowl.slugs.view.SplashScreen;

public class SlugsGame extends Game {

	@Override
	public void create() {
		// Display splash screen
		setScreen(new SplashScreen(this));
	}
	
}
