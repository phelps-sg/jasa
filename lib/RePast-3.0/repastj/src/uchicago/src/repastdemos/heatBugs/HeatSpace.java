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
package uchicago.src.repastdemos.heatBugs;

import java.awt.Point;

import uchicago.src.sim.space.Diffuse2D;
import cern.jet.random.Uniform;

/**
 * The environment that the heat bugs inhabit. This uses a Diffuse2D to
 * diffuse the heat absorbed from the bugs.
 *
 * @author Swarm Project and Nick Collier
 * @version $Revision$ $Date$
 */

public class HeatSpace extends Diffuse2D {

  public static final int HOT = 0;
  public static final int COLD = 1;

  private long maxHeat = Diffuse2D.MAX;
  private int[] xpoints = new int[9];
  private int[] ypoints = new int[9];

  public HeatSpace(double diffusionConstant, double evaporationRate,
                  int xSize, int ySize)
  {
    super(diffusionConstant, evaporationRate, xSize, ySize);
  }

  public void addHeat(int x, int y, int heat) {
    long heatHere = (long)this.getValueAt(x, y);

    if (heatHere + heat <= maxHeat) {
      heatHere += heat;
    } else {
      heatHere = maxHeat;
    }

    this.putValueAt(x, y, heatHere);
  }

  /**
   * Find the extreme hot or cold within this 9 cell neighborhood
   *
   * @return the extreme point
   */
  public Point findExtreme(int type, int x, int y) {

    long bestHeat = (long)this.getValueAt(x, y);

    // iterate through the space to find the extreme
    //Vector heatList = new Vector();
    int count = 0;

    for (int py = y - 1; py <= y + 1; py++) {
      for (int px = x - 1; px <= x + 1; px++) {

        boolean hereIsBetter, hereIsEqual;

        long heatHere = (long)this.getValueAt(px, py);
        hereIsBetter = (type == COLD) ? (heatHere < bestHeat) :
                                        (heatHere > bestHeat);
        hereIsEqual = (heatHere == bestHeat);

        if (hereIsBetter) {
          //Point p = new Point(px, py);
          xpoints[0] = px;
          ypoints[0] = py;
          //heatList.clear();
          //heatList.add(p);
          count = 1;
          bestHeat = heatHere;
        }

        if (hereIsEqual) {
          xpoints[count] = px;
          ypoints[count] = py;
          count++;
          //Point p = new Point(px, py);
          //heatList.add(p);
        }
      }
    }

    // choose a random index from within the list
    int index = Uniform.staticNextIntFromTo(0, count - 1);
    //Point bestPoint = (Point)heatList.elementAt(index);
    //bestPoint.x = xnorm(bestPoint.x);
    //bestPoint.y = ynorm(bestPoint.y);
    return new Point(xnorm(xpoints[index]), ynorm(ypoints[index]));
  }
}
