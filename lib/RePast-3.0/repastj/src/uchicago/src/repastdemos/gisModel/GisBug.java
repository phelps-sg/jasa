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
package uchicago.src.repastdemos.gisModel;

import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.RasterSpace;
import cern.jet.random.Uniform;

/**
 * The Bugs that run about the RasterSpace. These bugs don't do anything
 * but move.
 *
 * @version $Revision$ $Date$
 */
public class GisBug implements Drawable {

  private double x, y;

  private RasterSpace space;
  private Object2DGrid world;
  //private Dimension worldSize;

  //private double xOrig;
  //private double yOrig;
  //private double xTerm;
  //private double yTerm; 
  private float randomMoveProb;
  private double maxDistance;

  public GisBug(RasterSpace space, Object2DGrid world, double x,
    double y, float randomMoveProb, double maxDistance)
  {
    this.x = x;
    this.y = y;
    this.randomMoveProb = randomMoveProb;
    this.space = space;
    this.world = world;
    //xOrig = space.getOriginX();
    //yOrig = space.getOriginY();
    this.maxDistance = maxDistance;
    //xTerm = space.getTermX();
    //yTerm = space.getTermY();
  }

  public int getX(){
    return space.getCellCol(x);
  }

  public int getY(){
    return space.getCellRow(y);
  }

  public void step() {
    if(Uniform.staticNextFloatFromTo(0,1) <= randomMoveProb){
      move();
    }
  }
 
  private void move(){
    double newX = 0;
    double newY = 0;
    do{
      double dX = Uniform.staticNextDoubleFromTo(-(maxDistance), maxDistance);
      double dY = Uniform.staticNextDoubleFromTo(-(maxDistance), maxDistance);
      newX = x + dX;
      newY = y + dY;
    } while ( newX == x && newY == y ||
      space.getCellCol(newX) > (space.getSizeX() - 1) || 
      space.getCellCol(newX) < 0 ||
      space.getCellRow(newY) > (space.getSizeY() - 1) ||
      space.getCellRow(newY) < 0  ||
      world.getObjectAt(space.getCellCol(newX), space.getCellRow(newY)) != null);
    world.putObjectAt(space.getCellCol(x), space.getCellRow(y), null);
    world.putObjectAt(space.getCellCol(newX), space.getCellRow(newY), this);
    x = newX;
    y = newY;
  }

  public void draw(SimGraphics g){
    g.drawFastRoundRect(Color.green);
  }
}
