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
package uchicago.src.repastdemos.life;

import java.util.ArrayList;

import uchicago.src.sim.gui.Displayable;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.Object2DTorus;


/**
 * A grid space for LifeAgents. Agents inhabit the grid cells. This
 * class is responsible for creating new LifeAgents in those cells
 * that have 3 and only 3 LifeAgents for neighbors. This is a bounded
 * grid, that is optionally toriodal.
 */
public class LifeSpace implements Space {

  private Object2DGrid space;
  private LifeModel model;
  private int xSize, ySize;

  /**
   * Creates this LifeSpace of the specified width (xSize) and height
   * (ySize).
   */
  public LifeSpace(int xSize, int ySize, boolean toriodal, LifeModel model) {
    if (toriodal) space = new Object2DTorus(xSize,  ySize);
    else space = new Object2DGrid(xSize, ySize);
    this.model = model;
    this.xSize = xSize;
    this.ySize = ySize;
  }

  /**
   * Returns the width of this LifeSpace.
   */
  public int getXSize() {
    return xSize;
  }

  /**
   * Returns the height of this LifeSpace.
   */
  public int getYSize() {
    return ySize;
  }

  /**
   * Removes an agent from this space. Actual removal is not performed
   * until after all step() methods have completed.
   */
  public void remove(LifeAgent agent) {
    model.removeAgent(agent);
  }

  /**
   * Removes the agent at the specified coordinates. This occurs immediately.
   */
  public void removeAgentAt(int x, int y) {
    space.putObjectAt(x, y, null);
  }

  /**
   * Adds the agent at its coordinates.
   */
  public void addAgent(LifeAgent agent) {
    space.putObjectAt(agent.getX(), agent.getY(), agent);
  }

  /**
   * Returns the number of LifeAgents around the x, y coordinate. This
   * is a Moore neighborhood.
   */
  public int getNumNeighbors(int x, int y) {
    return space.getMooreNeighbors(x, y, false).size();
  }
  
  /**
   * Returns the Displayable appropriate for this space. In this case, this
   * returns an Object2DDisplay.
   */
  public Displayable getDisplay() {
    return new Object2DDisplay(space);
  }

  /**
   * Returns true if the cell at the specified coordinates is empty.
   */
  public boolean isEmptyAt(int x, int y) {
    return space.getObjectAt(x, y) == null;
  }

  /**
   * Iterates through each cell, if the cell is empty and has 3 neighbors then
   * new agent there.
   */
  public void step(ArrayList list) {
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
	if (space.getObjectAt(i, j) == null &&
	    space.getMooreNeighbors(i, j, false).size() == 3) {
	  model.addAgent(new LifeAgent(i, j, this));
	}
      }
    }
  }
}
			 
    
    
