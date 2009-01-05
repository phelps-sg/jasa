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

import uchicago.src.collection.SparseObjectMatrix;
import uchicago.src.sim.gui.Displayable;
import cern.colt.map.OpenIntObjectHashMap;

/**
 * An infinte grid space for LifeAgents. This is "infinite" in that it
 * is a really large sparse matrix. InfiniteSpaceDisplay is a kind of
 * window into this grid. Agents inhabit the grid cells. This class is
 * responsible for creating new LifeAgents in those cells that have 3
 * and only 3 LifeAgents for neighbors.
 */
public class InfiniteLifeSpace implements Space {

  private SparseObjectMatrix space;
  private LifeModel model;

  private Object dummy = new Object();
  private OpenIntObjectHashMap checkedSpace = new OpenIntObjectHashMap();
  public static final int MAX = (int)Math.sqrt(Integer.MAX_VALUE) - 1;
  private int viewWidth;
  private int viewHeight;

  /**
   * Creates this InfiniteLifeSpace. viewWidth and viewHeight will be the
   * width of the initial view window into this space. The x, y origin
   * of the view window will be roughly the middle of the Space minus
   * one-half of the width / height.
   */
  public InfiniteLifeSpace(LifeModel model, int viewWidth, int viewHeight) {
    space = new SparseObjectMatrix(MAX, MAX);
    this.model = model;
    this.viewWidth = viewWidth;
    this.viewHeight = viewHeight;
  }

  /**
   * Returns the width of this InfiniteLifeSpace.
   */
  public int getXSize() {
    return MAX;
  }

  /**
   * Returns the height of this InfiniteLifeSpace.
   */
  public int getYSize() {
    return MAX;
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
    space.remove(x, y);
  }

  /**
   * Adds the agent at its coordinates.
   */
  public void addAgent(LifeAgent agent) {
    space.put(agent.getX(), agent.getY(), agent);    
  }

  /**
   * Returns the number of LifeAgents around the x, y coordinate. This
   * is a Moore neighborhood.
   */
  public int getNumNeighbors(int x, int y) {
    int ncount = 0;
    int xLeft = x == 0 ? 0 : 1;
    int xRight = x == MAX ? 0 : 1;
    int yTop = y == 0 ? 0 : 1;
    int yBottom = y == MAX ? 0 : 1;

   
    for (int j = y - yTop; j <= y + yBottom; j++) {
      for (int i = x - xLeft; i <= x + xRight; i++) {
        if (!(j == y && i == x)) {
          Object o = space.get(i, j);
          if (o != null) ncount++;
        }
      }
    }
    
    return ncount;
  }

  /**
   * Returns true if the cell at the specified coordinates is empty.
   */
  public boolean isEmptyAt(int x, int y) {
    return space.get(x, y) == null;
  }

  /**
   * Returns the Displayable appropriate for this space. In this case, this
   * returns an InfiniteSpaceDisplay.
   */
  public Displayable getDisplay() {
    int x = MAX / 2 - viewWidth / 2;
    int y = MAX / 2 - viewHeight / 2;
    return new InfiniteSpaceDisplay(space, x, y, viewWidth, viewHeight, MAX);
  }
  
  /**
   * Checks if any empty space is surrounded by 3 and only 3 LifeAgents,
   * and if so then creates new LifeAgent there.
   */
  public void step(ArrayList list) {
    int size = list.size();
    for (int k = 0; k < size; k++) {
      LifeAgent agent = (LifeAgent)list.get(k);
      int x = agent.getX();
      int y = agent.getY();
      int xLeft = x == 0 ? 0 : 1;
      int xRight = x == MAX ? 0 : 1;
      int yTop = y == 0 ? 0 : 1;
      int yBottom = y == MAX ? 0 : 1;
      
      for (int j = y - yTop; j <= y + yBottom; j++) {
	for (int i = x - xLeft; i <= x + xRight; i++) {
	  if (!(j == y && i == x)) {
	    int index = j * MAX + i;
	    if (!checkedSpace.containsKey(index)) {
	      if (isEmptyAt(i, j) && getNumNeighbors(i, j) == 3) {
		model.addAgent(new LifeAgent(i, j, this));
		checkedSpace.put(index, dummy);
	      }
	    }
	  }
	}
      }
    }
    checkedSpace.clear();
    space.trimToSize();
  }  
}
			 
    
    
