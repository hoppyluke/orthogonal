package com.supergreenowl.slugs.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.supergreenowl.slugs.model.Layer;
import com.supergreenowl.slugs.model.Runner;
import com.supergreenowl.slugs.model.Worker;
import com.supergreenowl.slugs.model.Box;
import com.supergreenowl.slugs.model.Creature;
import com.supergreenowl.slugs.model.Segment;

public class CreatureRenderer {

	ColourPalette palette;
	
	public CreatureRenderer(ColourPalette palette) {
		this.palette = palette;
	}
	
	public void render(ShapeRenderer renderer, Creature c) {
		renderer.setColor(getColour(c));
		
		for(Segment segment : c) {
			Box b = segment.getBoundingBox();
			renderer.rect(b.getLeft(), b.getBottom(), b.getWidth(), b.getHeight());
		}
	}
	
	private Color getColour(Creature c) {
		if(c instanceof Worker) return palette.worker;
		else if(c instanceof Layer) return palette.layer;
		else if(c instanceof Runner) return palette.runner;
		
		return Color.BLACK;
	}

}
