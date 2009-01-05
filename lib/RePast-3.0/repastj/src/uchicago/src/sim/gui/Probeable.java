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

import java.util.ArrayList;

/**
 * Interface for Displays that can be probed. When a display
 * is probed it typicaly queries the space it is displaying for the objects
 * at the probed coordinates, and then displays the accessible
 * (through get and set methods) parameters of those objects.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public interface Probeable {

  /**
   * Gets the objects at the coordinate x, y. X and Y are screen coordinates.
   *
   * @param x the x screen coordinate.
   * @param y the y screen coordinate.
   * @return the object at the x, y coordinate.
   */
  public ArrayList getObjectsAt(int x, int y);

  /**
   * Sets the new coordinates for specified moveable. This goes through
   * probeable as some translation between screen pixel coordinates and
   * the simulation coordinates may be necessary.
   *
   * @param moveable the moveable whose coordinates are changed
   * @param x the x coordinate in pixels
   * @param y the y coordinate in pixels
   */
  public void setMoveableXY(Moveable moveable, int x, int y);

}
