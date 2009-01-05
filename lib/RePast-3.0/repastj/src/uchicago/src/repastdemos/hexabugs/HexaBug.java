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
package uchicago.src.repastdemos.hexabugs;

// colt imports:
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;

import uchicago.src.reflector.BooleanPropertyDescriptor;
import uchicago.src.reflector.DescriptorContainer;
import uchicago.src.repastdemos.heatBugs.HeatSpace;
import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Diffuse2D;
import uchicago.src.sim.space.Object2DHexagonalTorus;
import cern.jet.random.Uniform;


/**
 * The agent for the HexaBugs (HeatBugs on a hexagonal space) simulation. This
 * pretty much follows the Swarm code.
 * 
 * @version $Revision$ $Date$
 * @see uchicago.src.repastdemos.hexabugs.HexaBugsModel
 */
public class HexaBug
    implements Drawable,
        DescriptorContainer {
    private int[] xOffset = {0, 1, 1, -1, -1, 0};
    private int[][] yOffset = {{-1, 0, 1, 1, 1, -1}, {-1, -1, 0, 1, 0, -1}};
    private double unhappiness = 0;
    private int x;
    private int y;
    private int idealTemp;
    private int outputHeat;
    private float randomMoveProb;
    private HexaSpace space;
    private Object2DHexagonalTorus world;
    //private Dimension worldSize;
    //private int xSize;
    //private int ySize;
    private boolean example = false;
    private Hashtable descriptors = new Hashtable();

    public HexaBug(HexaSpace space, Object2DHexagonalTorus world, int x, int y,
                   int idealTemp, int outputHeat, float randomMoveProb) {
        this.x = x;
        this.y = y;
        this.idealTemp = idealTemp;
        this.outputHeat = outputHeat;
        this.randomMoveProb = randomMoveProb;
        this.space = space;
        this.world = world;
        //worldSize = world.getSize();
        //xSize = worldSize.width;
        //ySize = worldSize.height;

        BooleanPropertyDescriptor bd = new BooleanPropertyDescriptor(
                                               "BDExample", false);
        descriptors.put("BDExample", bd);
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
        world.putObjectAt(x, y, this);
    }

    public void step() {
        long heatHere = (long) space.getValueAt(x, y);

        if (heatHere < idealTemp) {
            unhappiness = (double) (idealTemp - heatHere) / Diffuse2D.MAX;
        } else {
            unhappiness = (double) (heatHere - idealTemp) / Diffuse2D.MAX;
        }

        int type = (heatHere < idealTemp) ? HeatSpace.HOT : HeatSpace.COLD;
        Point p = space.findExtreme(type, x, y);

        if (Uniform.staticNextFloatFromTo(0.0f, 1.0f) < randomMoveProb) {
            int randomNeighbor = Uniform.staticNextIntFromTo(0, 5);
            p.x = world.xnorm(x + xOffset[randomNeighbor]);
            p.y = world.ynorm(y + yOffset[x % 2][randomNeighbor]);
        }
        if (unhappiness == 0) {
            space.addHeat(x, y, outputHeat);
        } else {
            int tries = 0;

            if (p.x != x || p.y != y) {
                while ((world.getObjectAt(p.x, p.y) != null) && tries < 10) {
                    int location = Uniform.staticNextIntFromTo(0, 5);
                    p.x = world.xnorm(x + xOffset[location]);
                    p.y = world.ynorm(y + yOffset[x % 2][location]);
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
        Graphics2D g2d = g.getGraphics();
        Rectangle r = g2d.getClipBounds();
        int x = (int) r.getMinX();
        int y = (int) r.getMinY();
        g.setDrawingCoordinates(x + DisplayConstants.CELL_WIDTH / 6,
                                y + DisplayConstants.CELL_HEIGHT / 6, 0);
        g.setDrawingParameters(DisplayConstants.CELL_WIDTH / 2,
                               DisplayConstants.CELL_HEIGHT / 2,
                               DisplayConstants.CELL_DEPTH / 2);
        g.drawFastRoundRect(Color.green);
    }
}