package com.supergreenowl.slugs.view;

import com.badlogic.gdx.graphics.Color;
import com.supergreenowl.slugs.model.World;

public class ColourPalette {

	public final Color background, worker, layer, runner, line, text;
	
	public ColourPalette(Color background, Color worker, Color layer, Color runner, Color line, Color text) {
		this.background = background;
		this.worker = worker;
		this.layer = layer;
		this.runner = runner;
		this.line = line;
		this.text = text;
	}
	
	public static final ColourPalette FIFTY_SHADES = new ColourPalette(
			Color.WHITE,
			new Color(0.85f, 0.85f, 0.85f, 1f), // worker 
			new Color(0.5f, 0.5f, 0.5f, 1f), // layer
			new Color(0.3f, 0.3f, 0.3f, 1f), // runner
			new Color(0.75f, 0.75f, 0.75f, 1f), // line
			Color.BLACK
		);

	public static final ColourPalette PURPLE = new ColourPalette(
			Color.WHITE,
			new Color(0.63f, 0.5f, 0.63f, 1f), // worker 
			new Color(0.5f, 0.25f, 0.5f, 1f), // layer
			new Color(0.25f, 0.03f, 0.25f, 1f), // runner
			new Color(0.5f, 0.42f, 0.5f, 1f), // line
			Color.BLACK
		);
	
	public static final ColourPalette BLUE = new ColourPalette(
			Color.WHITE,
			new Color(0.39f, 0.59f, 0.71f, 1f),
			new Color(0.24f, 0.24f, 0.63f, 1f),
			new Color(0.03f, 0.03f, 0.38f, 1f),
			new Color(0.5f, 0.5f, 0.63f, 1f),
			Color.BLACK
		);
	
	public static final ColourPalette GREEN = new ColourPalette(
			Color.WHITE,
			new Color(0.39f, 0.71f, 0.50f, 1f), // worker 
			new Color(0.25f, 0.5f, 0.25f, 1f), // layer
			new Color(0.03f, 0.25f, 0.03f, 1f), // runner
			new Color(0.5f, 0.55f, 0.5f, 1f), // line
			Color.BLACK
		);
	
	public static final ColourPalette YELLOW = new ColourPalette(
			Color.WHITE,
			new Color(0.86f, 0.86f, 0.5f, 1f), // worker 
			new Color(0.94f, 0.86f, 0.13f, 1f), // layer
			new Color(0.78f, 0.71f, 0f, 1f), // runner
			new Color(0.63f, 0.63f, 0.5f, 1f), // line
			Color.BLACK
		);
	
	public static final ColourPalette ORANGE = new ColourPalette(
			Color.WHITE,
			new Color(1f, 0.71f, 0.5f, 1f), // worker 
			new Color(0.86f, 0.35f, 0f, 1f), // layer
			new Color(0.55f, 0.16f, 0f, 1f), // runner
			new Color(0.63f, 0.56f, 0.5f, 1f), // line
			Color.BLACK
		);
	
	public static final ColourPalette RED = new ColourPalette(
			Color.WHITE,
			new Color(1f, 0.75f, 0.75f, 1f), // worker 
			new Color(0.78f, 0f, 0f, 1f), // layer
			new Color(0.5f, 0f, 0f, 1f), // runner
			new Color(0.77f, 0.59f, 0.59f, 1f), // line
			Color.BLACK
		);
	
	public static final ColourPalette[] RAINBOW = {
		FIFTY_SHADES,
		BLUE,
		PURPLE,
		GREEN,
		YELLOW,
		ORANGE,
		RED
	};
	
	/**
	 * Chooses a colour palette at random from the available palettes.
	 * @return Selected palette.
	 */
	public static ColourPalette randomPalette() {
		int palette = World.generator.nextInt(RAINBOW.length);
		return RAINBOW[palette];
	}
}
