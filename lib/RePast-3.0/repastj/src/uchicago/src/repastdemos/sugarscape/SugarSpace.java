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
package uchicago.src.repastdemos.sugarscape;

import java.awt.Dimension;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.Object2DTorus;

/**
 * The space in which the SugarAgents act. In particular this space implements
 * the growback rule G from Growing Artificial Societies.<p>
 *
 * The source has been annotated so see that for further details.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class SugarSpace {

  // the Sugar space is composed of two tori which track the maximum
  // amount of sugar, and the current value at each x,y coordinate.
  private Object2DTorus currentSugar;
  private Object2DTorus maxSugar;

  // default sugar grow rate and the default maximum sugar
  private int sugarGrowRate = 1;
  //private int globalMaxSugar = 4;
  private Dimension size;


  public SugarSpace(String sugarFile) {

    // constructs the maximum sugar torus using the values in the
    // pgm file, sugarspace.pgm.
    // we get this as a stream so can load it from a jar file.
    // if we didn't need to get this from a jar we could just pass
    // the file name to Object2DTorus.
    java.io.InputStream stream = getClass().getResourceAsStream(sugarFile);

    maxSugar = new Object2DTorus(stream, Object2DGrid.PGM_ASCII);

    currentSugar = new Object2DTorus(maxSugar.getSizeX(), maxSugar.getSizeY());

    // sets the current sugar to the maximum sugar.
    BaseMatrix m = maxSugar.getMatrix();
    for (int i = 0; i < m.getNumCols(); i++) {
      for (int j = 0; j < m.getNumRows(); j++) {
        Integer intg = (Integer)m.get(i, j);
        currentSugar.putObjectAt(i, j, new Integer(intg.intValue()));
      }
    }

    size = maxSugar.getSize();
    //System.out.println(size.width + ", " + size.height);
  }

  public Dimension getSize() {
    return size;
  }

  public Object2DGrid getCurrentSugar() {
    return currentSugar;
  }

  // The actual implementation of growback rule G, pg 182 (Appendix B).
  public void updateSugar() {
    int sugarAtSpot;
    int maxSugarAtSpot;

    for (int i = 0; i < size.width; i++) {
      for (int j = 0; j < size.height; j++) {
        sugarAtSpot = ((Integer)currentSugar.getObjectAt(i, j)).intValue();
        maxSugarAtSpot = ((Integer)maxSugar.getObjectAt(i, j)).intValue();

        if (sugarGrowRate == -1) {
          currentSugar.putObjectAt(i, j, new Integer(maxSugarAtSpot));
        } else {
          if (sugarAtSpot != maxSugarAtSpot) {
            if (sugarAtSpot + sugarGrowRate <= maxSugarAtSpot) {
              currentSugar.putObjectAt(i, j, new Integer(sugarAtSpot + sugarGrowRate));
            } else {
              currentSugar.putObjectAt(i, j, new Integer(maxSugarAtSpot));
            }
          }
        }
      }
    }
  }

  // takes all the sugar at this coordinate, leaving no sugar.
  public int takeSugarAt(int x, int y) {
    Integer i = (Integer)currentSugar.getObjectAt(x, y);
    currentSugar.putObjectAt(x, y, new Integer(0));
    return i.intValue();
  }

  // gets the amount of sugar at this x,y coordinate
  public int getSugarAt(int x, int y) {
    Integer i = (Integer)currentSugar.getObjectAt(x, y);
    return i.intValue();
  }

  public int getXSize() {
    return size.width;
  }

  public int getYSize() {
    return size.height;
  }
}
















