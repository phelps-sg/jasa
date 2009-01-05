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
package uchicago.src.sim.space;


import java.awt.Dimension;
import java.util.Arrays;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.collection.DoubleMatrix;


/**
 * Discrete 2nd order approximation of 2d diffusion with
 * evaporation. Essentialy a java implementation of Diffuse2d in the
 * <a href="http://www.santafe.edu/projects/swarm"> Swarm</a> simulation
 * toolkit. Toroidal in shape and works with number values. This
 * space simulates concurency through the use of a read and write
 * matrix. Any writes to the space, write to the write matrix, and
 * any reads to the read matrix. The diffuse() method then diffuses
 * the write matrix and copies the new values into the read matrix.<p>
 *
 * For an example of a Diffuse2d space see the heatBugs example. See
 * {@link #diffuse() diffuse} for a brief explanation of the diffusion
 * algorithm.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class Diffuse2D implements Discrete2DSpace, Torus {

    public static final long MAX = 0x7FFF;
    public static final long MIN = -MAX;
    
    protected double diffCon;
    protected double evapRate;
    protected DoubleMatrix readMatrix;
    protected DoubleMatrix writeMatrix;
    protected int xSize, ySize;

    protected int x, prevX, nextX;
    protected int y, prevY, nextY;

    /**
     * Constructs a Diffuse2d space with the specificed dimensions
     *
     * @param xSize size of the x dimension
     * @param ySize size of the y dimension
     */
    public Diffuse2D(int xSize, int ySize) {
        this(1.0, 1.0, xSize, ySize);
    }

    /**
     * Constructs a Diffuse2d space with the specified diffusion constant,
     * evaporation rate, and dimensions
     *
     * @param diffusionConstant the diffusion constant
     * @param evaporationRate the evaporation rate
     * @param xSize size of the x dimension
     * @param ySize size of the y dimension
     */
    public Diffuse2D(double diffusionConstant, double evaporationRate,
        int xSize, int ySize) {
        diffCon = diffusionConstant;
        evapRate = evaporationRate;
        this.xSize = xSize;
        this.ySize = ySize;
        readMatrix = new DoubleMatrix(xSize, ySize);
        writeMatrix = new DoubleMatrix(xSize, ySize);
    }

    /**
     * Sets the diffusion constant for this Diffuse2d space
     */
    public void setDiffusionConstant(double diffusionConstant) {
        diffCon = diffusionConstant;
    }

    /**
     * Sets the evaporation rate for this Diffuse2d space
     */
    public void setEvaporationRate(double rate) {
        evapRate = rate;
    }

    private void computeRow() {
        int endX = xSize - 1;

        prevX = endX;
        x = 0;

        while (x < endX) {
            nextX = x + 1;
            computeVal();

            prevX = x; 
            x = nextX;
        }
        nextX = 0;
        computeVal();
    }

    private void computeVal() {
        long val = (long) readMatrix.getDoubleAt(x, y);
        long sum = 0;

        sum += (long) readMatrix.getDoubleAt(prevX, prevY);
        sum += 4 * (long) readMatrix.getDoubleAt(x, prevY);
        sum += (long) readMatrix.getDoubleAt(nextX, prevY);
        sum += 4 * (long) readMatrix.getDoubleAt(prevX, y);
        sum += 4 * (long) readMatrix.getDoubleAt(nextX, y);
        sum += (long) readMatrix.getDoubleAt(prevX, nextY);
        sum += 4 * (long) readMatrix.getDoubleAt(x, nextY);
        sum += (long) readMatrix.getDoubleAt(nextX, nextY);
        sum -= 20 * val;

        double delta = sum / 20.0;

        double d = val + delta * diffCon;

        d *= evapRate;

        //System.out.println("x: " + x + " y: " + y + "val: " + val +
        //		       " delta: " + delta + " d: " + d);

        // do the rounding a la Swarm Diffuse2d code.
//        long newState = d < 0 ? 0L : d >= MAX ? MAX : (long) d;
        long newState = d < MIN ? MIN : d >= MAX ? MAX : (long) d;

        //System.out.println("x: " + x + " y: " + y + " state: " + newState);

        /*
         if (d < 0)
         newState = 0L;
         else if (d + 0.5 >= MAX)
         newState = MAX;
         else
         newState = (long)Math.floor(d + 0.5);
         */

        writeMatrix.putDoubleAt(x, y, newState);
    }

    /**
     * Runs the diffusion with the current rates and values. Following the Swarm
     * class, it is roughly newValue = evap(ownValue + diffusionConstant *
     * (nghAvg - ownValue)) where nghAvg is the weighted average of a cells
     * eight neighbors, and ownValue is the current value for the current cell.<p>
     *
     * Values from the readMatrix are used to calculate diffusion. This value
     * is then written to the writeMatrix. When this has been done for every
     * cell in the grid, the writeMatrix is copied to the readMatrix.
     */
    public void diffuse() {

        int endY = ySize - 1;

        prevY = endY;
        y = 0;
        while (y < endY) {
            nextY = y + 1;

            computeRow();
            prevY = y;
            y = nextY;
        }
        nextY = 0;
        computeRow();

        // copies the writeMatrix into the readMatrix
        writeMatrix.copyMatrixTo(readMatrix);
    }

    /**
     * Copies the writeLattice to the readLattice
     */
    public void update() {
        writeMatrix.copyMatrixTo(readMatrix);
    }

    /**
     * Gets the von Neumann neighbors of the specified coordinate.
     * doubles are returned in west, east, north, south order.
     * The double at x, y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @return an array of doubles in west, east, north, south order
     */
    public double[] getVonNeumannNeighbors(int x, int y) {
        return getVonNeumannNeighbors(x, y, 1, 1);
    }

    /**
     * Gets the extended von Neumann neighbors of the specified coordinate. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * doubles are returned in west, east, north, south order with the
     * most distant object first. The double at x,y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @return an array of doubles in west, east, north,
     * south order with the most distant object first.
     */

    public double[] getVonNeumannNeighbors(int x, int y, int xExtent, int yExtent) {
        double[] array = new double[(xExtent * 2) + (yExtent * 2)];
        int index = 0;

        int normX = xnorm(x);
        int normY = xnorm(y);

        for (int i = x - xExtent; i < x; i++) {
            array[index++] = readMatrix.getDoubleAt(i, normY);
        }

        for (int i = x + xExtent; i > x; i--) {
            array[index++] = readMatrix.getDoubleAt(i, normY);
        }

        for (int i = y - yExtent; i < y; i++) {
            array[index++] = readMatrix.getDoubleAt(normX, i);
        }

        for (int i = y + yExtent; i > y; i--) {
            array[index++] = readMatrix.getDoubleAt(normX, i);
        }

        return array;
    }

    /**
     * Gets the Moore neighbors of the specified coordinate. doubles are
     * returned by row starting with the "NW corner" and ending with the
     * "SE corner."  The double at x, y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @return an array of doubles ordered by row starting
     * with the "NW corner" and ending with the "SE corner."
     */

    public double[] getMooreNeighbors(int x, int y) {
        return getMooreNeighbors(x, y, 1, 1);
    }

    /**
     * Gets the extended Moore neighbors of the specified coordinate. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * doubles are returned by row starting with the "NW corner" and ending with
     * the "SE corner." The double at x,y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @return an array of doubles ordered by row starting
     * with the "NW corner" and ending with the "SE corner."
     */

    public double[] getMooreNeighbors(int x, int y, int xExtent, int yExtent) {
        double[] array = new double[xExtent * yExtent * 4 + (xExtent * 2) + (yExtent * 2)];
        int index = 0;

        for (int j = y - yExtent; j <= y + yExtent; j++) {
            for (int i = x - xExtent; i <= x + xExtent; i++) {
                if (!(j == y && i == x)) {
                    array[index++] = readMatrix.getDoubleAt(xnorm(i), ynorm(j));
                }
            }
        }

        return array;
    }

    /**
     * Finds the maximum grid cell value within a specified range from
     * the specified origin coordinate.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param range the range out from the coordinate to search
     * @param includeOrigin include object at origin in search
     * @param neighborhoodType the type of neighborhood to search. Can be one
     * of Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
     * @return the Objects determined to be the maximum.

     */
    public double[] findMaximum(int x, int y, int range, boolean includeOrigin,
        int neighborhoodType) {
        double[] dArray;

        if (neighborhoodType == VON_NEUMANN) {
            dArray = this.getVonNeumannNeighbors(x, y, range, range);
        } else {
            dArray = this.getMooreNeighbors(x, y, range, range);
        }

        // need to extend the array here
        if (includeOrigin) {
            double[] newArray = new double[dArray.length + 1];

            System.arraycopy(dArray, 0, newArray, 0, dArray.length);
            newArray[newArray.length - 1] = getValueAt(x, y);
            dArray = newArray;
        }

        return compareMax(dArray);
    }

    // need better algorithm for this
    protected double[] compareMax(double[] array) {
        if (array.length > 0) {
            Arrays.sort(array);
            int endIndex = array.length - 1;
            double max = array[endIndex];
            double val = array[endIndex - 1];

            int index = 1;

            while (max == val && index < array.length) {
                index++;
                val = endIndex - index;
            }

            double[] retVal = new double[index];

            System.arraycopy(array, array.length - index, retVal, 0, index);
            return retVal;
        }

        return new double[0];
    }

    /**
     * Finds the minimum grid cell value within a specified range from
     * the specified origin coordinate.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param range the range out from the coordinate to search
     * @param includeOrigin include object at origin in search
     * @param neighborhoodType the type of neighborhood to search. Can be one
     * of Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
     * @return the Objects determined to be the maximum.
     */
    public double[] findMinimum(int x, int y, int range, boolean includeOrigin,
        int neighborhoodType) {
        double[] dArray;

        if (neighborhoodType == VON_NEUMANN) {
            dArray = this.getVonNeumannNeighbors(x, y, range, range);
        } else {
            dArray = this.getMooreNeighbors(x, y, range, range);
        }

        // need to extend the array here
        if (includeOrigin) {
            double[] newArray = new double[dArray.length + 1];

            System.arraycopy(dArray, 0, newArray, 0, dArray.length);
            newArray[newArray.length - 1] = getValueAt(x, y);
            dArray = newArray;
        }

        return compareMin(dArray);
    }

    protected double[] compareMin(double[] array) {
        if (array.length > 0) {
            Arrays.sort(array);
            double min = array[0];
            double val = array[1];
            int endIndex = array.length - 1;
            int index = 1;

            while (min == val) {
                index++;
                if (index <= endIndex) val = array[index];
                else break;
            }

            double[] retVal = new double[index];

            System.arraycopy(array, 0, retVal, 0, index);
            return retVal;
        }

        return new double[0];
    }   

    // Discrete2dSpace interface
    /**
     * Gets the size of the x dimension
     */
    public int getSizeX() {
        return xSize;
    }

    /**
     * Gets the size of the y dimension
     */
    public int getSizeY() {
        return ySize;
    }

    /**
     * Gets the dimension of the space
     */
    public Dimension getSize() {
        return new Dimension(xSize, ySize);
    }

    /**
     * Gets the object (a Long) at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the Object at x,y
     */
    public Object getObjectAt(int x, int y) {
        return new Long((long) readMatrix.getDoubleAt(xnorm(x), ynorm(y)));
    }

    /**
     * Gets the value at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the value at x, y
     */
    public double getValueAt(int x, int y) {
        return readMatrix.getDoubleAt(xnorm(x), ynorm(y));
    }

    /**
     * Puts the specified Object at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param object the object to put
     * @exception java.lang.IllegalArgumentException if object is not an
     * instance of Number
     */
    public void putObjectAt(int x, int y, Object object) {
        if (!(object instanceof Number)) {
            throw new IllegalArgumentException("object must be a Number");
        }
        Number number = (Number) object;

        writeMatrix.putDoubleAt(xnorm(x), ynorm(y), number.doubleValue());
    }

    /**
     * Puts the specified value at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param value the value to put
     */
    public void putValueAt(int x, int y, double value) {
        writeMatrix.putDoubleAt(xnorm(x), ynorm(y), value);
    }

    /**
     * Gets the matrix collection class that contains all the values. In
     * this case the readMatrix is returned.
     *
     * @return the write matrix.
     */
    public BaseMatrix getMatrix() {
        return readMatrix;
    }

    /**
     * Normalize the x value to the toroidal coordinates
     *
     * @param x the value to normalize
     * @return the normalized value
     */
    protected int xnorm(int x) {
        if (x > xSize - 1 || x < 0) {
            while (x < 0) x += xSize;
            return x % xSize;
        }

        return x;
    }

    /**
     * Normalize the y value to the toroidal coordinates
     *
     * @param y the value to normalize
     * @return the normalized value
     */
    protected int ynorm(int y) {
        if (y > ySize - 1 || y < 0) {
            while (y < 0) y += ySize;
            return y % ySize;
        }
        return y;
    }
}
