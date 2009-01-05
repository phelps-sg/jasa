/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import uchicago.src.sim.util.SimUtilities;

/**
 * A Wrapper around java.awt.Graphics2D. Simplifies the drawing of circles,
 * rectangles, and so forth. Rectangles are generally faster to draw than
 * circles. The Displays should take care of layouts while objects that wish to
 * be drawn as a shape need only call the appropriate method.
 * <p>
 * 
 * Many of the draw methods have a drawFast counterpart. This is due to speed
 * problems with the Java2D api. In general the Java2D api is more flexible and
 * usually looks nicer than its ordinary AWT counterpart. Moreover, somethings
 * are easier to do with it (i.e. drawing borders). For now though, the drawFast
 * methods are better.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class SimGraphics {

	private Graphics2D g2;

	private RoundRectangle2D roundRect;

	private Rectangle2D rect;

	private Ellipse2D oval;

	//private Line2D line;
	private DisplaySurface surface;

	//private BasicStroke lineStroke = new BasicStroke(2);

	private int curX;

	private int curY;

	private int[] curLineX;

	private int[] curLineY;

	//private int curZ;

	private float xScale = 1;

	private float yScale = 1;

	private int origWidth, origHeight;

	private int curWidth = (int) DisplayConstants.CELL_WIDTH;

	private int curHeight = (int) DisplayConstants.CELL_HEIGHT;

	//private int curDepth;

	private Font currentFont = new Font("monospace", Font.PLAIN, 8);
	
	private static SimGraphics instance;

	public SimGraphics() {
		origWidth = curWidth;
		origHeight = curHeight;

		instance = this;
	}

	public static SimGraphics getInstance() {
		return instance;
	}

	/**
	 * Sets the wrapped graphics2D
	 * 
	 * @param g
	 *            the Graphics2D to wrap
	 */
	public void setGraphics(Graphics2D g) {
		g2 = g;
		g2.setFont(currentFont);
	}

	/**
	 * Gets the Graphics2D object around which this is a wrapper.
	 */
	public Graphics2D getGraphics() {
		return g2;
	}

	/**
	 * Sets the display surface associated with this SimGraphics instance.
	 * 
	 * @param surface
	 *            the display surface on which this draws
	 */
	public void setDisplaySurface(DisplaySurface surface) {
		this.surface = surface;
	}

	public int getDisplayWidth() {
		return surface.getWidth();
	}

	public int getDisplayHeight() {
		return surface.getHeight();
	}

	/**
	 * Sets the current font.
	 * 
	 * @param font
	 *            the new font
	 */
	public void setFont(Font font) {
		if (font != currentFont) {
			currentFont = font;
			g2.setFont(font);
		}
	}

	public float getXScale() {
		return xScale;
	}

	public void setXScale(float scale) {
		this.xScale = scale;
		curWidth = (int) (origWidth * scale);
	}

	public int getCellWidthScale() {
		return (int) (DisplayConstants.CELL_WIDTH * xScale);
	}

	public float getYScale() {
		return yScale;
	}

	public void setYScale(float scale) {
		this.yScale = scale;
		curHeight = (int) (origHeight * scale);
	}

	public int getCellHeightScale() {
		return (int) (DisplayConstants.CELL_HEIGHT * yScale);
	}

	/**
	 * Sets the coordinates for the next drawing operation. Z is ignored at this
	 * time.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param z
	 *            the z coordinate
	 */
	public void setDrawingCoordinates(float x, float y, float z) {
		curX = (int) x;
		curY = (int) y;
	}

	/**
	 * Sets the parameters for the next drawing operation. This scales the
	 * specified width etc. appropriately.
	 * 
	 * @param width
	 *            the new width
	 * @param height
	 *            the new height
	 * @param depth
	 *            the new depth
	 */
	public void setDrawingParameters(int width, int height, int depth) {
		curWidth = (int) (width * xScale);
		curHeight = (int) (height * yScale);
		//curDepth = depth;
	}

	/**
	 * Sets the parameters for the next drawing operation. This performs no
	 * scaling and assumes that the caller has scaled the parameters
	 * appropriately.
	 * 
	 * @param width
	 *            the new width
	 * @param height
	 *            the new height
	 * @param depth
	 *            the new depth
	 */
	public void setDrawingParametersNoScale(int width, int height, int depth) {
		curWidth = width;
		curHeight = height;
		//curDepth = depth;
	}

	public int getCurWidth() {
		return curWidth;
	}

	public int getCurHeight() {
		return curHeight;
	}

	private void drawInit(Color c) {
		if (g2.getPaint() != c)
			g2.setPaint(c);
	}

	/**
	 * Draws the specified image. This does not perform any scaling of the image
	 * and thus if you are using this to draw the image in a grid, the image
	 * might be too large for the grid cell. <code>drawImageToFit</code> will
	 * scale the image appropriately before drawing it.
	 * 
	 * @param img
	 *            the Image to draw.
	 */
	public void drawImage(Image img) {
		g2.drawImage(img, curX, curY, null);
	}

	/**
	 * Draws the specified image, scaling the image to the correct size if
	 * necessary.
	 * 
	 * @param img
	 *            the Image to draw.
	 */
	public void drawImageToFit(Image img) {
		g2.drawImage(img, curX, curY, curWidth, curHeight, null);
	}

	/**
	 * Draws a rounded rectangle of the specified color.
	 * 
	 * @param c
	 *            the color of the round rectangle
	 */
	public void drawRoundRect(Color c) {
		drawInit(c);
		if (roundRect == null) {
			roundRect = new RoundRectangle2D.Float(curX, curY, curWidth,
					curHeight, 3, 3);
		}

		roundRect.setFrame(curX, curY, curWidth, curHeight);
		g2.fill(roundRect);

	}

	/**
	 * Draws a rounded rectangle of the specified color, faster than
	 * drawRoundRect. This uses fillRoundRect where drawRoundRect uses
	 * g2.fill(roundRect).
	 * 
	 * @param c
	 *            the color of the round rectangle
	 */
	public void drawFastRoundRect(Color c) {
		drawInit(c);
		g2.fillRoundRect(curX, curY, curWidth, curHeight, 3, 3);
	}

	/**
	 * Draws a hollow rounded rectangle of the specified color.
	 * 
	 * @param c
	 *            the color of the round rectangle
	 */
	public void drawHollowRoundRect(Color c) {
		drawInit(c);
		if (roundRect == null) {
			roundRect = new RoundRectangle2D.Float(curX, curY, curWidth,
					curHeight, 3, 3);
		}

		roundRect.setFrame(curX, curY, curWidth, curHeight);
		g2.draw(roundRect);
	}

	/**
	 * Draws a hollow rounded rectangle with the specified color, faster than
	 * drawHollowCircle.
	 * 
	 * @param c
	 *            the color of the round rectangle
	 */
	public void drawHollowFastRoundRect(Color c) {
		drawInit(c);
		g2.drawRoundRect(curX, curY, curWidth, curHeight, 3, 3);
	}

	/**
	 * Draws a true circle with the specified color. The circle's radius will be
	 * current width / 2.
	 * 
	 * @param c
	 *            the color of the circle
	 */
	public void drawCircle(Color c) {
		drawInit(c);
		if (oval == null) {
			oval = new Ellipse2D.Float(curX, curY, curWidth, curHeight);
		}

		oval.setFrame(curX, curY, curWidth, curWidth);
		g2.draw(oval);
	}

	/**
	 * Draws a circle of the specified color at the current coordinates faster
	 * than drawCircle. The circle's radius will be the current width / 2.
	 * 
	 * @param c
	 *            the color of the circle
	 */
	public void drawFastCircle(Color c) {
		drawInit(c);
		g2.fillOval(curX, curY, curWidth, curWidth);
	}

	/**
	 * Draws a directed link (a line with a square head) from the specified
	 * coordinates to the specified coordinates in the specified color. A small
	 * square will be drawn on the link close to the to side.
	 * 
	 * @param c
	 *            the color of the link
	 * @param fromX
	 *            the x coordinate to draw from
	 * @param toX
	 *            the x coordinate to draw to
	 * @param fromY
	 *            the y coordinate to from
	 * @param toY
	 *            the y coordinate to draw to
	 */
	public void drawDirectedLink(Color c, int fromX, int toX, int fromY, int toY) {
		drawInit(c);
		g2.drawLine(fromX, fromY, toX, toY);
		int dx = toX + ((fromX - toX) / 6);
		int dy = toY + ((fromY - toY) / 6);
		g2.fillRect(dx - 2, dy - 2, 5, 5);

		/*
		 * if (line == null) { line = new Line2D.Float(fromX, fromY, toX, toY); }
		 */

		/*
		 * // do this the non Java2D way g2.drawLine(fromX, fromY, toX, toY);
		 * 
		 * int x = toX - 2; int y = toY - 2; g2.fillRect(x, y, 5, 5);
		 */
	}

	/**
	 * Draws a link (a line) from the specified coordinates to the specified
	 * coordinates in the specified color.
	 * 
	 * @param c
	 *            the color of the link
	 * @param fromX
	 *            the x coordinate to draw from
	 * @param toX
	 *            the x coordinate to draw to
	 * @param fromY
	 *            the y coordinate to from
	 * @param toY
	 *            the y coordinate to draw to
	 */
	public void drawLink(Color c, int fromX, int toX, int fromY, int toY) {
		drawInit(c);
		g2.drawLine(fromX, fromY, toX, toY);
	}

	/**
	 * Draws the specified string inside a rounded rectangle. The string will be
	 * clipped to fit inside the rectangle.
	 * 
	 * @param rectColor
	 *            the color of the rounded rectangle
	 * @param stringColor
	 *            the color of the string
	 * @param string
	 *            the string to draw
	 */

	public void drawStringInRoundRect(Color rectColor, Color stringColor,
			String string) {
		drawFastRoundRect(rectColor);
		drawString(string, stringColor);
	}

	/**
	 * Draws a string of the specified color in a hollow rounded rectangle of
	 * the specified color. The string is cliped to fit in the rectangle.
	 * 
	 * @param rectColor
	 *            the color of the circle
	 * @param stringColor
	 *            the color of the String
	 * @param string
	 *            the string to draw.
	 */

	public void drawStringInHollowRoundRect(Color rectColor, Color stringColor,
			String string) {
		drawHollowFastRoundRect(rectColor);
		drawString(string, stringColor);
	}

	/**
	 * Draws an oval in the specified color.
	 * 
	 * @param color
	 *            the color to draw the oval with
	 */
	public void drawOval(Color color) {
		drawInit(color);
		if (oval == null) {
			oval = new Ellipse2D.Float(curX, curY, curWidth, curHeight);
		}

		oval.setFrame(curX, curY, curWidth, curHeight);
		g2.fill(oval);
	}

	/**
	 * Draws an oval in the specified color faster than drawOval.
	 * 
	 * @param c
	 *            the color to draw the oval with
	 */
	public void drawFastOval(Color c) {
		drawInit(c);
		g2.fillOval(curX, curY, curWidth, curHeight);
	}

	/**
	 * Draws a hollow oval in the specified color faster than drawHollowOval.
	 * 
	 * @param color
	 */
	public void drawHollowFastOval(Color color) {
		drawInit(color);
		g2.drawOval(curX, curY, curWidth, curHeight);
	}

	/**
	 * Draws a hollow oval in the specified color.
	 * 
	 * @param color
	 *            the color to draw the oval with
	 */
	public void drawHollowOval(Color color) {
		drawInit(color);
		if (oval == null) {
			oval = new Ellipse2D.Float(curX, curY, curWidth, curHeight);
		}

		oval.setFrame(curX, curY, curWidth, curHeight);
		g2.draw(oval);
	}

	/**
	 * Draws the specified string in an oval using the specified colors. The
	 * string will be clipped to fit inside the oval.
	 * 
	 * @param ovalColor
	 *            the color of the oval
	 * @param stringColor
	 *            the color of the string
	 * @param string
	 *            the string to draw
	 */
	public void drawStringInOval(Color ovalColor, Color stringColor,
			String string) {
		drawFastOval(ovalColor);
		drawString(string, stringColor);
	}

	/**
	 * Draws the specified string in a hollow oval using the specified colors.
	 * The string will be clipped to fit inside the oval.
	 * 
	 * @param ovalColor
	 *            the color of the oval
	 * @param stringColor
	 *            the color of the string
	 * @param string
	 *            the string to draw
	 */
	public void drawStringInHollowOval(Color ovalColor, Color stringColor,
			String string) {
		drawHollowFastOval(ovalColor);
		drawString(string, stringColor);
	}

	/**
	 * Draws an oval shaped border using the current drawing parameters.
	 * 
	 * @param stroke
	 *            the stroke to draw the border with
	 * @param color
	 *            the color of the border
	 */
	public void drawOvalBorder(BasicStroke stroke, Color color) {
		drawInit(color);
		if (oval == null) {
			oval = new Ellipse2D.Float(curX, curY, curWidth, curHeight);
		}

		oval.setFrame(curX, curY, curWidth, curHeight);
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(stroke);
		g2.draw(oval);
		g2.setStroke(oldStroke);
	}

	/**
	 * Get the bounding rectangle for the specified string and the specified
	 * font.
	 * 
	 * @param s
	 *            the string
	 * @param f
	 *            the font
	 * @return the bounding rectangle for the string when drawn with the
	 *         specified font
	 */
	public Rectangle2D getStringBounds(String s, Font f) {
		FontRenderContext rContext = g2.getFontRenderContext();
		Rectangle2D bounds = f.getStringBounds(s, rContext);
		return bounds;
	}

	/**
	 * Get the bounding rectangle for the specified string using the current
	 * font.
	 * 
	 * @param s
	 *            the string
	 * @return the bounding rectangle for the string when drawn with the current
	 *         font.
	 */
	public Rectangle2D getStringBounds(String s) {
		return getStringBounds(s, currentFont);
	}

	/**
	 * Draws a rectangle at the current x and y coordinate with the current
	 * width and height in the specified color. This is faster than drawRect as
	 * it does a graphics.fillRect(...) and drawRect does a graphics.fill(rect)
	 * where rect is a Rectangle. As Java2D gets faster, this should no longer
	 * be necessary.
	 * 
	 * @param color
	 *            the color of the rectangle to draw
	 */
	public void drawFastRect(Color color) {
		drawInit(color);
		g2.fillRect(curX, curY, curWidth, curHeight);
	}

	/**
	 * Draws a rectangle of the specified color. Uses the current width and
	 * height as set by {@link #setDrawingParameters(int, int, int)
	 * setDrawingParameters}.
	 * 
	 * @param color
	 *            the color of the rectangle
	 */

	public void drawRect(Color color) {
		drawInit(color);

		if (rect == null) {
			rect = new Rectangle(curX, curY, curWidth, curHeight);
		}

		rect.setFrame(curX, curY, curWidth, curHeight);
		g2.fill(rect);
	}

	/**
	 * Draws a hollow rectangle of the specified color. Uses the current width
	 * and height as set by {@link #setDrawingParameters(int, int, int)
	 * setDrawingParameters}.
	 * 
	 * @param color
	 *            the color of the rectangle
	 */
	public void drawHollowRect(Color color) {
		drawInit(color);

		if (rect == null) {
			rect = new Rectangle(curX, curY, curWidth, curHeight);
		}

		rect.setFrame(curX, curY, curWidth, curHeight);
		g2.draw(rect);
	}

	/**
	 * Draws the sides of a hollow rectangle in the four specifed colors. Uses
	 * the current with and height as set by setDrawingParameters.
	 * 
	 * @param top
	 *            the color of the top side
	 * @param bottom
	 *            the color of the bottom side
	 * @param left
	 *            the color of the left side
	 * @param right
	 *            the color of the right side
	 */

	public void draw4ColorHollowRect(Color top, Color bottom, Color left,
			Color right) {
		curX--;
		curY++;
		int toX = curX + curWidth - 1;
		int toY = curY + curHeight - 1;

		drawInit(top);
		g2.drawLine(curX, curY, toX, curY);

		drawInit(bottom);
		g2.drawLine(curX, toY, toX, toY);

		drawInit(right);
		g2.drawLine(toX, curY, toX, toY);

		drawInit(left);
		g2.drawLine(curX, curY, curX, toY);
	}

	/**
	 * Draws a hollow rectangle of the specified color faster than
	 * drawHollowRect. Uses the current width and height as set by
	 * {@link #setDrawingParameters(int, int, int) setDrawingParameters}.
	 * 
	 * @param color
	 *            the color of the rectangle
	 */
	public void drawHollowFastRect(Color color) {
		drawInit(color);
		g2.drawRect(curX, curY, curWidth, curHeight);
	}

	/**
	 * Draws the specified string in a rectangle using the specified colors. The
	 * string will be clipped to fit inside the rectangle
	 * 
	 * @param rectColor
	 *            the color of the rectangle
	 * @param stringColor
	 *            the color of the string
	 * @param string
	 *            the string to draw
	 */
	public void drawStringInRect(Color rectColor, Color stringColor,
			String string) {
		drawFastRect(rectColor);
		drawString(string, stringColor);
	}

	/**
	 * Draws the specified string in a hollow rectangle using the specified
	 * colors. The string will be clipped to fit inside the rectangle
	 * 
	 * @param rectColor
	 *            the color of the rectangle
	 * @param stringColor
	 *            the color of the string
	 * @param string
	 *            the string to draw
	 */
	public void drawStringInHollowRect(Color rectColor, Color stringColor,
			String string) {
		drawHollowFastRect(rectColor);
		drawString(string, stringColor);
	}

	/**
	 * Draws an rectangular border using the current drawing parameters.
	 * 
	 * @param stroke
	 *            the stroke to draw the border with
	 * @param color
	 *            the color of the border
	 */
	public void drawRectBorder(BasicStroke stroke, Color color) {
		drawInit(color);

		if (rect == null) {
			rect = new Rectangle(curX, curY, curWidth, curHeight);
		}

		rect.setFrame(curX, curY, curWidth, curHeight);
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(stroke);
		g2.draw(rect);
		g2.setStroke(oldStroke);
	}

	/**
	 * Draws a string using the current drawing parameters. The string will be
	 * clipped to the current width and height.
	 * 
	 * @param string
	 *            the string to draw
	 * @param stringColor
	 *            the color the the string
	 */
	public void drawString(String string, Color stringColor) {

		Shape shape = g2.getClip();
		g2.setClip(curX, curY, curWidth, curHeight);
		drawInit(stringColor);

		FontRenderContext rContext = g2.getFontRenderContext();
		Rectangle2D bounds = currentFont.getStringBounds(string, rContext);

		int xCenter = curX + curWidth / 2;
		int yCenter = curY + curHeight / 2;

		int strX = xCenter - (int) bounds.getCenterX();
		int strY = yCenter - (int) bounds.getCenterY();

		g2.drawString(string, strX, strY);
		g2.setClip(shape);
	}

	public void setCurLineX(int[] xs) {
		curLineX = xs;
	}

	public void setCurLineY(int[] ys) {
		curLineY = ys;
	}

	public void drawLine(Color color) {
		g2.setColor(color);
		if (curLineY == null) {
			SimUtilities.showError(
					"The Y values for the line have not been set.  You should " +
					"stop your simulation and check your code.",
					new IllegalStateException("Y values for line not set"));
		}
		g2.drawPolyline(curLineX, curLineY, curLineX.length);
	}

	public void drawPolygon(Color color) {
		g2.setColor(color);
		g2.drawPolygon(curLineX, curLineY, curLineX.length);
	}

	public void fillPolygon(Color color) {
		g2.setColor(color);
		g2.fillPolygon(curLineX, curLineY, curLineX.length);
	}

	public void drawMultiPolygon(Color color, int[] polys) {
		g2.setColor(color);
		int j = 0;
		for (int i = 0; i < polys.length; i++) {
			int size = polys[i];
			int[] partX = new int[size];
			int[] partY = new int[size];
			for (int k = 0; k < size; k++) {
				partX[k] = curLineX[j];
				partY[k] = curLineY[j];
				j++;
			}
			g2.drawPolygon(partX, partY, partX.length);
		}
	}

}

