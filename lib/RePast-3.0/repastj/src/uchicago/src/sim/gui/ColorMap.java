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

import java.awt.Color;

import cern.colt.map.OpenIntObjectHashMap;

/**
 * A customizable map of java.awt.Color(s) to Integers.
 * Useful for displaying collections of Integers, such as an Object2DGrid,
 * as colors.<p>
 *
 * Note that using the method that take int arguments is faster than
 * those that take the Integer arguments.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ColorMap {

  public final static Color white 	= new Color(255, 255, 255);
  public final static Color lightGray = new Color(192, 192, 192);
  public final static Color gray 	= new Color(128, 128, 128);
  public final static Color darkGray 	= new Color(64, 64, 64);
  public final static Color black 	= new Color(0, 0, 0);
  public final static Color red 	= new Color(255, 0, 0);
  public final static Color pink 	= new Color(255, 175, 175);
  public final static Color orange 	= new Color(255, 200, 0);
  public final static Color yellow 	= new Color(255, 255, 0);
  public final static Color green 	= new Color(0, 255, 0);
  public final static Color magenta	= new Color(255, 0, 255);
  public final static Color cyan 	= new Color(0, 255, 255);
  public final static Color blue 	= new Color(0, 0, 255);

  private OpenIntObjectHashMap map = new OpenIntObjectHashMap();

  /**
   * Maps the specified color to the specified Integer
   *
   * @param i the integer to map the color to
   * @param c the color to map to the Integer
   */
  public void mapColor(Integer i, Color c) {
    map.put(i.intValue(), c);
  }

  /**
   * Maps the specified color to the specified int
   *
   * @param i the int to map the color to
   * @param c the color to map to the int
   */
  public void mapColor(int i, Color c) {
    map.put(i, c);
  }

  /**
   * Maps the color specified by the red, green, and blue values to
   * to the int i. Red, green, and blue should be doubles in the range [0, 1]
   * where 1 is the equivalent of the full color (255), and 0 is no color.
   * This system makes it easy to set color values within a loop. For example,
   * <code><pre>
   * for (int i = 0; i < 64; i++) { <br>
   *   map.setColor(i, i / 63.0, 0, 0)<br>
   * }<br></code></pre>
   * This will set 64 shades of red.
   *
   * @param i the int to map the color to
   * @param red a double in the range of [0, 1]
   * @param blue a double in the range of [0, 1]
   * @param green a double in the range of [0, 1]
   */
  public void mapColor(int i, double red, double green, double blue) {
    if (red < 0 || green < 0 || blue < 0) 
      throw new IllegalArgumentException("Red, green, and blue must be >= 0");

    if (red >= 1.0)
      red = 1;

    if (blue >= 1.0)
      blue = 1;

    if (green >= 1.0)
      green = 1;

    int redVal = (int)(255 * red);
    int greenVal = (int)(255 * green);
    int blueVal = (int)(255 * blue);

    map.put(i, new Color(redVal, greenVal, blueVal));
  }

  /**
   * Gets the color mapped to the specified Integer
   */
  public  Color getColor(Integer i) {
    return (Color)map.get(i.intValue());
  }

  /**
   * Gets the color mapped to the specified int
   */
  public  Color getColor(int i) {
    return (Color)map.get(i);
  }
}


