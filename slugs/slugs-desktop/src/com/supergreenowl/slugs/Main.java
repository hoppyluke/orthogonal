package com.supergreenowl.slugs;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Orthogonal";
		cfg.width = 384;
		cfg.height = 640;
		
		new LwjglApplication(new SlugsGame(), cfg);
	}
}
