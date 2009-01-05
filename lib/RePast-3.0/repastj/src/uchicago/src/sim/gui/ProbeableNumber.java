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

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.util.ByteWrapper;
import uchicago.src.sim.util.DoubleWrapper;
import uchicago.src.sim.util.FloatWrapper;
import uchicago.src.sim.util.IntWrapper;
import uchicago.src.sim.util.LongWrapper;

/**
 * Turns a primitive number into an Object that can be probed. This is
 * used to make spaces that contain primitive numbers probeable.
 */

public class ProbeableNumber implements CustomProbeable {

  private int x, y;
  private Discrete2DSpace grid;
  private DoubleWrapper wrapper;

  /**
   * Creates a Probeable number with the specified coordinates, in
   * the specified grid, out of the specified object.
   *
   * @param x the x coordinate for this number
   * @param y the y coordinate for this number
   * @param grid the grid from where this number came
   * @param o the primitive wrapper (e.g. Double) that carries the
   * value of the number
   *
   */
  public ProbeableNumber(int x, int y, Discrete2DSpace grid, Object o)
  {
    this.x = x;
    this.y = y;
    this.grid = grid;

    if (o instanceof Double) {
      wrapper = new DoubleWrapper(((Double)o).doubleValue());
    } else if (o instanceof Integer) {
      wrapper = new IntWrapper(((Integer)o).intValue());
    } else if (o instanceof Float) {
      wrapper = new FloatWrapper(((Float)o).floatValue());
    } else if (o instanceof Long) {
      wrapper = new LongWrapper(((Long)o).longValue());
    } else if (o instanceof Byte) {
      wrapper = new ByteWrapper(((Byte)o).byteValue());
    } else {
      throw new IllegalArgumentException("Object must be a Number");
    }

  }

  /**
   * Gets the x coordinate.
   */
  public int getX() {
    return x;
  }

  /**
   * Gets the y coordinate.
   */
  public int getY() {
    return y;
  }

  /**
   * Gets the primitive value of the the number.
   */
  public double getVal() {
    //return wrapper.doubleVal();
    return ((Number)grid.getObjectAt(x, y)).doubleValue();
  }

  /**
   * Sets the value of the number.
   */
  public void setVal(double val) {
    wrapper.setVal(val);
    grid.putObjectAt(x, y, wrapper.getWrappedNumber());
  }

  /**
   * Returns a list of properties suitable for probing.
   */
  public String[] getProbedProperties() {
    String[] p = {"val", "x", "y"};
    return p;
  }
}

