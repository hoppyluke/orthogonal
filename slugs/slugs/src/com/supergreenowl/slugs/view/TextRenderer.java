package com.supergreenowl.slugs.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renders text with rectangles.
 * Support only letters and numbers (case insensitive).
 * Unsupported characters are ignored.
 * @author Luke
 *
 */
public class TextRenderer {

	public static enum HorizontalAlignment {
		LEFT,
		CENTRE,
		RIGHT
	}
	
	public static enum VerticalAlignment {
		BOTTOM,
		MIDDLE,
		TOP
	}

	/** Fixed width of characters (as a multiple of {@code size}). */
	private static final float LETTER_WIDTH = 3f;

	/** Fixed height of characters (as a multiple of {@link size}). */
	private static final float LETTER_HEIGHT = 5f;
	
	private static final int MAX_RENDER_DIGITS = 5;
	private static final int MAX_RENDERABLE_NUMBER = 99999;
	
	/** Unit size for rendering. */
	private float size;
	
	private float spaceSize, letterSize, letterHeight, advanceIncrement;
	
	private HorizontalAlignment textAlign = HorizontalAlignment.LEFT;
	private VerticalAlignment verticalAlign = VerticalAlignment.BOTTOM;

	ColourPalette palette;
	
	private ShapeRenderer renderer;
	private float currentCharX, currentCharY, currentX, currentY;
	
	private int[] digits = new int[MAX_RENDER_DIGITS];

	/**
	 * Creates a new text renderer.
	 * @param scaleX Horizontal scaling for translating world to screen coordinates.
	 * @param scaleY Vertical scaling for translating world to screen coordinates.
	 * @param palette Colour palette to render in.
	 * @param size Unit size of the rectangles used to draw text (in world dimensions).
	 */
	public TextRenderer(ColourPalette palette, float size) {
		this.palette = palette;
		setSize(size);
	}

	/**
	 * Sets the text alignment of this text renderer.
	 * @param horizontal Horizontal alignment.
	 * @param vertical Vertical alignment.
	 */
	public void setAlignment(HorizontalAlignment horizontal, VerticalAlignment vertical) {
		this.textAlign = horizontal;
		this.verticalAlign = vertical;
	}
	
	/**
	 * Sets the unit size for this renderer.
	 * @param size
	 */
	public void setSize(float size) {
		this.size = size;
		this.spaceSize = size;
		this.letterSize = size * LETTER_WIDTH;
		this.letterHeight = size * LETTER_HEIGHT;
		this.advanceIncrement = letterSize + spaceSize;
	}
	
	/**
	 * Draws a number.
	 * @param renderer ShapeRenderer used to draw number.
	 * @param value Number to draw.
	 * @param x Location to draw at (in world coordinates).
	 * @param y Location for baseline of number (in world coordinates).
	 */
	public void render(ShapeRenderer renderer, int value, float x, float y) {
		if(value < 0 || value > MAX_RENDERABLE_NUMBER)
			throw new IllegalArgumentException("number to render is out of range");
		
		this.renderer = renderer;
		renderer.setColor(palette.text);
		
		// insert digits into array - in reverse order
		int i = 0;
		do {
			digits[i++] = value % 10;
			value = value / 10;
		}
		while(value > 0);
		
		positionCursor(x, y, i);
		
		// go through array backwards to render each digit in actual order
		for(int j = i - 1; j >= 0; j--) {
			renderDigit(digits[j], currentX, currentY);
			advanceCursor();
		}
		
		this.renderer = null;
	}

	/**
	 * Draws some text. Position of text relative to specified coordinates is
	 * determined by the current alignment settings.
	 * @param renderer ShareRenderer used to draw.
	 * @param text Text to draw.
	 * @param x Location to draw at (in world coordinates).
	 * @param y Location to draw at (in world coordinates).
	 * @see #setAlignment(HorizontalAlignment, VerticalAlignment)
	 */
	public void render(ShapeRenderer renderer, String text, float x, float y) {
		this.renderer = renderer;
		renderer.setColor(palette.text);

		// Text is case insensitive
		String textToRender = text.toLowerCase();

		int length = textToRender.length();
		positionCursor(x, y, length);

		for(int i = 0; i < length; i++) {
			char c = textToRender.charAt(i);

			if(Character.isLetterOrDigit(c) || '?' == c) {
				renderChar(c, currentX, currentY);
				advanceCursor();
			}
			else if(' ' == c) {
				advanceCursor();
			}
		}

		/* Don't hold reference to renderer.
		 * Not sure if this is a problem or not but LibGDX APIs 
		 * supply renderer as a parameter to each rendering call.
		 * Storing reference seems to go against that design. 
		 */
		this.renderer = null;
	}

	/**
	 * Positions the rendering cursor so as to render a sequence of characters
	 * at the specified coordinates. Adjusts for the alignment settings of this renderer.
	 * @param x
	 * @param y
	 * @param length Number of characters to render.
	 */
	private void positionCursor(float x, float y, int length) {
		float textWidth = (length * letterSize) + ((length - 1) * spaceSize);
		
		// scale world coordinates to screen coordinates
		currentX = x;
		currentY = y;

		switch(textAlign) {
		case RIGHT:
			currentX -= textWidth;
			break;
		case CENTRE:
			currentX -= (textWidth / 2f);
			break;
		case LEFT:
			// nothing to do if this is left aligned text
			break;
		}
		
		switch(verticalAlign) {
		case BOTTOM:
			// nothing to do.
			break;
		case MIDDLE:
			currentY -= (letterHeight / 2f);
			break;
		case TOP:
			currentY -= letterHeight;
			break;
		}
	}
	
	/**
	 * Advances the rendering cursor by the width of one character plus
	 * inter-character space.
	 */
	private void advanceCursor() {
		currentX += advanceIncrement;
	}
	
	/**
	 * Renders a single digit from 0-9.
	 * Result is undefined if digit is outside this range.
	 * @param d Digit to render.
	 * @param x
	 * @param y
	 */
	private void renderDigit(int d, float x, float y) {
		currentCharX = x;
		currentCharY = y;
		
		switch(d) {
		case 0:
			renderCharComponent(0f,  0f, 1f, 5f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(1f, 4f, 1f, 1f);
			renderCharComponent(2f,  0f,  1f,  5f);
			break;
		case 1:
			renderCharComponent(1f, 0f, 1f, 5f);
			break;
		case 2:
			renderCharComponent(0f, 0f, 3f, 1f);
			renderCharComponent(0f, 1f, 1f, 1f);
			renderCharComponent(0f, 2f, 3f, 1f);
			renderCharComponent(2f, 3f, 1f, 1f);
			renderCharComponent(0f, 4f, 3f, 1f);
			break;
		case 3:
			renderCharComponent(0f, 0f, 2f, 1f);
			renderCharComponent(0f, 2f, 2f, 1f);
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 4:
			renderCharComponent(0f, 2f, 1f,  3f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 5:
			renderCharComponent(0f, 0f, 3f,  1f);
			renderCharComponent(0f, 2f, 3f, 1f);
			renderCharComponent(0f, 4f, 3f, 1f);
			renderCharComponent(2f, 1f, 1f, 1f);
			renderCharComponent(0f, 3f, 1f, 1f);
			break;
		case 6:
			renderCharComponent(0f, 0f, 1f,  5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(2f, 1f, 1f, 1f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(1f, 4f, 2f, 1f);
			break;
		case 7:
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 8:
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(1f, 4f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 9:
			renderCharComponent(0f, 2f, 2f, 1f);
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(0f, 3f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		}
	}
	
	/**
	 * Renders a single character at the specified position.
	 * @param renderer
	 * @param c Character to render.
	 * @param x Coordinate for left of character.
	 * @param y Coordinate for bottom of character.
	 */
	private void renderChar(char c, float x, float y) {

		currentCharX = x;
		currentCharY = y;

		switch(c) {
		case '0':
		case 'o':
			renderCharComponent(0f,  0f, 1f, 5f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(1f, 4f, 1f, 1f);
			renderCharComponent(2f,  0f,  1f,  5f);
			break;
		case '1':
			renderCharComponent(1f, 0f, 1f, 5f);
			break;
		case '2':
			renderCharComponent(0f, 0f, 3f, 1f);
			renderCharComponent(0f, 1f, 1f, 1f);
			renderCharComponent(0f, 2f, 3f, 1f);
			renderCharComponent(2f, 3f, 1f, 1f);
			renderCharComponent(0f, 4f, 3f, 1f);
			break;
		case '3':
			renderCharComponent(0f, 0f, 2f, 1f);
			renderCharComponent(0f, 2f, 2f, 1f);
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case '4':
			renderCharComponent(0f, 2f, 1f,  3f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case '5':
		case 's':
			renderCharComponent(0f, 0f, 3f,  1f);
			renderCharComponent(0f, 2f, 3f, 1f);
			renderCharComponent(0f, 4f, 3f, 1f);
			renderCharComponent(2f, 1f, 1f, 1f);
			renderCharComponent(0f, 3f, 1f, 1f);
			break;
		case '6':
			renderCharComponent(0f, 0f, 1f,  5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(2f, 1f, 1f, 1f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(1f, 4f, 2f, 1f);
			break;
		case '7':
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case '8':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(1f, 4f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case '9':
			renderCharComponent(0f, 2f, 2f, 1f);
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(0f, 3f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'a':
			renderCharComponent(0f, 0f, 1f, 3f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			renderCharComponent(0f, 4f, 2f, 1f);
			break;
		case 'b':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(2f, 0f, 1f, 3f);
			break;
		case 'c':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(1f, 4f, 2f, 1f);
			break;
		case 'd':
			renderCharComponent(0f, 0f, 1f, 3f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'e':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(1f, 4f, 2f, 1f);
			renderCharComponent(2f, 2f, 1f, 3f);
			break;
		case 'f':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(1f, 4f, 2f, 1f);
			break;
		case 'g':
			renderCharComponent(0f, 0f, 3f, 1f);
			renderCharComponent(0f, 2f, 2f, 1f);
			renderCharComponent(0f, 3f, 1f, 1f);
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(2f, 1f, 1f, 4f);
			break;
		case 'h':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 3f);
			break;
		case 'i':
			renderCharComponent(1f, 0f, 1f, 3f);
			renderCharComponent(1f, 4f, 1f, 1f);
			break;
		case 'j':
			renderCharComponent(0f, 0f, 1f, 1f);
			renderCharComponent(1f, 0f, 1f, 3f);
			renderCharComponent(1f, 4f, 1f, 1f);
			break;
		case 'k':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 1f, 1f, 2f);
			renderCharComponent(2f, 0f, 1f, 1f);
			renderCharComponent(2f, 3f, 1f, 1f);
			break;
		case 'l':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			break;
		case 'm':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 3f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'n':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 4f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'p':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(1f, 4f, 2f, 1f);
			renderCharComponent(2f, 3f, 1f, 1f);
			break;
		case 'q':
			renderCharComponent(0f, 4f, 2f, 1f);
			renderCharComponent(0f, 3f, 1f, 1f);
			renderCharComponent(0f, 2f, 2f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'r':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 4f, 2f, 1f);
			break;
		case 't':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 2f, 1f);
			renderCharComponent(1f, 2f, 2f, 1f);
			break;
		case 'u':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'v':
			renderCharComponent(0f, 1f, 1f, 4f);
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(2f, 1f, 1f, 4f);
			break;
		case 'w':
			renderCharComponent(0f, 0f, 1f, 5f);
			renderCharComponent(1f, 1f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'x':
			renderCharComponent(0f, 0f, 1f, 2f);
			renderCharComponent(0f, 3f, 1f, 2f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 2f);
			renderCharComponent(2f, 3f, 1f, 2f);
			break;
		case 'y':
			renderCharComponent(0f, 2f, 1f, 3f);
			renderCharComponent(0f, 0f, 2f, 1f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 0f, 1f, 5f);
			break;
		case 'z':
			renderCharComponent(0f, 0f, 3f, 1f);
			renderCharComponent(0f, 1f, 1f, 1f);
			renderCharComponent(1f, 2f, 1f, 1f);
			renderCharComponent(2f, 3f, 1f, 1f);
			renderCharComponent(0f, 4f, 3f, 1f);
			break;
		case '?':
			renderCharComponent(1f, 0f, 1f, 1f);
			renderCharComponent(1f, 2f, 2f, 1f);
			renderCharComponent(2f, 3f, 1f, 1f);
			renderCharComponent(0f, 4f, 3f, 1f);
			break;
		}
	}

	/**
	 * Renders a single rectangle as a component of a character.
	 * All units are multiples of {@link size}.
	 * @param offsetX Horizontal offset relative to current character position.
	 * @param offsetY Horizontal offset relative to current character position.
	 * @param width Rectangle width.
	 * @param height Rectangle height.
	 */
	private void renderCharComponent(float offsetX, float offsetY, float width, float height) {
		float actualWidth = width * size;
		float actualHeight = height * size;

		float actualX = currentCharX + (offsetX * size);
		float actualY = currentCharY + (offsetY * size);

		renderer.rect(actualX, actualY, actualWidth, actualHeight);
	}
}
