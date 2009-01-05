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
package uchicago.src.sim.topology.space2;

import java.awt.Dimension;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.topology.Context;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 16, 2003
 * Time: 2:38:05 PM
 * To change this template use Options | File Templates.
 */
public interface Discrete2DSpace extends Context{

  public Location getLocation(Object o);

  /**
   * Gets the size of the x dimension
   */
  public int getSizeX();

  /**
   * Gets the size of the y dimension
   */
  public int getSizeY();

  /**
   * Gets the dimension of the space
   */
  public Dimension getSize();

  /**
   * Gets the Object at the specified coordinate.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the Object at x,y
   */
  public Object getObjectAt(int x, int y);

  /**
   * Gets the value at the specified coordinate if appropriate.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the value at x, y
   */
  public double getValueAt(int x, int y);

  /**
   * Puts the specified Object at the specified coordinate.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param object the object to put
   */
  public void putObjectAt(int x, int y, Object object);

  /**
   * Puts the specified value at the specified coordinate.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param value the value to put at x,y
   */
  public void putValueAt(int x, int y, double value);

  /**
   * Gets the matrix collection class that contains all the values
   */
  public BaseMatrix getMatrix();

}
