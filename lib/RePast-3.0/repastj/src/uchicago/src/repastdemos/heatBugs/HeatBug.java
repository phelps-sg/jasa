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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Hashtable;

import uchicago.src.reflector.BooleanPropertyDescriptor;
import uchicago.src.reflector.DescriptorContainer;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Diffuse2D;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.SimUtilities;
import cern.jet.random.Uniform;

/**
 * The agent for the Heat Bugs simulation. This pretty much follows the
 * Swarm code.
 *
 * @author Swarm Project and Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.repastdemos.heatBugs.HeatBugsModel
 */

public class HeatBug implements Drawable, DescriptorContainer {

  private double unhappiness = 0;
  private int x, y;
  private int idealTemp, outputHeat;
  private float randomMoveProb;

  private HeatSpace space;
  private Object2DTorus world;
  private Dimension worldSize;

  private int xSize;
  private int ySize;

  private boolean example = false;
  private Hashtable descriptors = new Hashtable();


  public HeatBug(HeatSpace space, Object2DTorus world, int x,
    int y, int idealTemp, int outputHeat, float randomMoveProb)
  {
    this.x = x;
    this.y = y;
    this.idealTemp = idealTemp;
    this.outputHeat = outputHeat;
    this.randomMoveProb = randomMoveProb;
    this.space = space;
    this.world = world;
    worldSize = world.getSize();
    xSize = worldSize.width;
    ySize = worldSize.height;

    BooleanPropertyDescriptor bd = new BooleanPropertyDescriptor("BDExample", false);
    descriptors.put("BDExample", bd);

  }

  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;
    world.putObjectAt(x, y, this);
  }

  public void step() {
     long heatHere = (long)space.getValueAt(x, y);

    if (heatHere < idealTemp) {
      unhappiness = (double) (idealTemp - heatHere) / Diffuse2D.MAX;
    } else {
      unhappiness = (double) (heatHere - idealTemp) / Diffuse2D.MAX;
    }

    int type = (heatHere < idealTemp) ? HeatSpace.HOT : HeatSpace.COLD;
    Point p = space.findExtreme(type, x, y);

    if (Uniform.staticNextFloatFromTo(0.0f, 1.0f) < randomMoveProb) {
      p.x = x + Uniform.staticNextIntFromTo(-1, 1);
      p.y = y + Uniform.staticNextIntFromTo(-1, 1);
    }

    if (unhappiness == 0) {
      space.addHeat(x, y, outputHeat);
    } else {
      int tries = 0;

      if (p.x != x || p.y != y) {

        // get the neighbors
        int prevX = SimUtilities.norm(x - 1, xSize);
        int nextX = SimUtilities.norm(x + 1,  xSize);
        int prevY = SimUtilities.norm(y - 1, ySize);
        int nextY = SimUtilities.norm(y + 1, ySize);

        while ((world.getObjectAt(p.x, p.y) != null) && tries < 10) {

          int location = Uniform.staticNextIntFromTo(1, 8);

          switch (location) {
            case 1:
              p.x = prevX;
              p.y = prevY;
              break;
            case 2:
              p.x = x;
              p.y = prevY;
              break;
            case 3:
              p.x = nextX;
              p.y = prevY;
              break;
            case 4:
              p.x = nextX;
              p.y = y;
              break;
            case 5:
              p.x = prevX;
              p.y = y;
              break;
            case 6:
              p.x = prevX;
              p.y = nextY;
              break;
            case 7:
              p.x = x;
              p.y = nextY;
              break;
            case 8:
              p.x = nextX;
              p.y = nextY;
            default:
              break;
          }
          tries++;
        }
        if (tries == 10) {
          p.x = x;
          p.y = y;
        }

      }

      space.addHeat(x, y, outputHeat);
      world.putObjectAt(x, y, null);
      x = p.x;
      y = p.y;
      world.putObjectAt(x, y, this);
    }
  }

  public double getUnhappiness() {
    return unhappiness;
  }

  public void setUnhappiness(double value) {
    unhappiness = value;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

   public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getIdealTemp() {
    return idealTemp;
  }

  public void setIdealTemp(int idealTemp) {
    this.idealTemp = idealTemp;
  }


  public int getOutputHeat() {
    return outputHeat;
  }

  public void setOutputHeat(int outputHeat) {
    this.outputHeat = outputHeat;
  }

  public void incrementOutputHeat(int increment) {
    outputHeat += increment;
  }

  public float getRandomMoveProb() {
    return randomMoveProb;
  }

  public void setRandomMoveProb(float f) {
    randomMoveProb = f;
  }

  public void setBDExample(boolean val) {
    example = val;
  }

  public boolean getBDExample() {
    return example;
  }

  // DescriptorContainer interface
  public Hashtable getParameterDescriptors() {
    return descriptors;
  }


  public void draw(SimGraphics g) {
    g.drawFastRoundRect(Color.green);
    //g.draw4ColorHollowRect(Color.red, Color.green, Color.cyan, Color.pink);
  }
}
