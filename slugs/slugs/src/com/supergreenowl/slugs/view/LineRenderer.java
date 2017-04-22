package com.supergreenowl.slugs.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.supergreenowl.slugs.model.Box;
import com.supergreenowl.slugs.model.Line;

public class LineRenderer {

	// lines are modelled as 2 points with 0 width
	// introduce fake width so they are visible
	private static final float LINE_WIDTH = 2f;
	private static final float LINE_HALF_WIDTH = LINE_WIDTH / 2f;
	
	ColourPalette palette;
	
	public LineRenderer(ColourPalette palette) {
		this.palette = palette;
	}
	
	public void renderLines(ShapeRenderer renderer, Iterable<Line> lines) {
		renderer.setColor(palette.line);
		
		for(Line line : lines) {
			Box b = line.getBoundingBox();
			
			float x = b.getLeft() - LINE_HALF_WIDTH;
			float y = b.getBottom() - LINE_HALF_WIDTH;
			float w = b.getWidth() + LINE_WIDTH;
			float h = b.getHeight() + LINE_WIDTH;
			
			renderer.rect(x, y, w, h);
		}
	}
	
}
