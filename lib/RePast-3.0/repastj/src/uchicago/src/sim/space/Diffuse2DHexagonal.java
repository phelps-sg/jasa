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


//import java.awt.Dimension;
//import java.util.Arrays;

//import uchicago.src.collection.DoubleMatrix;
//import uchicago.src.collection.BaseMatrix;


/**
 * Discrete 2nd order approximation of 2d diffusion with evaporation
 * on a hexagonal grid. Based on the Objective C implementation of
 * Diffuse2d in the
 * <a href="http://www.santafe.edu/projects/swarm">Swarm</a>
 * simulation toolkit, with the addition of a hexagonal grid.
 * Toroidal in shape and works with
 * number values. This space simulates concurency through the use of a
 * read and write matrix. Any writes to the space, write to the write
 * matrix, and any reads to the read matrix. The diffuse() method then
 * diffuses the write matrix and copies the new values into the read
 * matrix.<p>
 *
 * The hexagonal cells are referenced by x, y coordinates as follows:
 * <pre>
 *       _
 *   _ / 1 \ _
 * / 0 \ _ / 2 \
 * \ _ / 4 \ _ /
 * / 3 \ _ / 5 \
 * \ _ / 7 \ _ /
 * / 6 \ _ / 8 \
 * \ _ /   \ _ /
 *
 * </pre>
 *
 * Here we have a 3 x 3 hexagonal grid. The first row of cells is 0,
 * 1, 2 such that 0,0 refers to cell 0, and 0,2 refers to cell 2. The
 * next row of cells is 3, 4, 5, so 1,0 refers to cell 3 and so
 * on. The last row of cells is 6, 7, and 8, so 2, 0 refers to cell 6.
 * The ring of neighbors with radius one that surrounds cell 4 is
 * composed of 1, 2, 5, 7, 3, and 0. The grid wraps as a toriod such
 * that cell -1, 0 refers to cell 2 and cell 0, -1 is cell 6.<p>
 *
 * See {@link #diffuse() diffuse} for a brief explanation of the diffusion
 * algorithm.
 */
public class Diffuse2DHexagonal extends Diffuse2D {

//    private int yUp, yDown;

    /**
     * Constructs a Diffuse2DHexagonal space with the specificed
     * dimensions
     *
     * @param xSize size of the x dimension
     * @param ySize size of the y dimension
     */
    public Diffuse2DHexagonal(int xSize, int ySize) {
        this(1.0, 1.0, xSize, ySize);
    }

    /**
     * Constructs a Diffuse2DHexagonal space with the specified
     * diffusion constant, evaporation rate, and dimensions
     *
     * @param diffusionConstant the diffusion constant
     * @param evaporationRate the evaporation rate
     * @param xSize size of the x dimension
     * @param ySize size of the y dimension
     */
    public Diffuse2DHexagonal(double diffusionConstant, double evaporationRate,
        int xSize, int ySize) {
        super(diffusionConstant, evaporationRate, xSize, ySize);
    }

    private void computeColumn() {
        int endY = ySize - 1;

        prevY = endY;
        y = 0;

        while (y < endY) {
            nextY = y + 1;
            computeVal();

            prevY = y;
            y = nextY;
        }
        nextY = 0;
        computeVal();
    }

    private void computeVal() { 

        long val = (long) readMatrix.getDoubleAt(x, y);
        long sum = 0;

        if (x % 2 == 0) {
            sum += (long) readMatrix.getDoubleAt(x, prevY);
            sum += (long) readMatrix.getDoubleAt(nextX, y);
            sum += (long) readMatrix.getDoubleAt(nextX, nextY);
            sum += (long) readMatrix.getDoubleAt(x, nextY);
            sum += (long) readMatrix.getDoubleAt(prevX, nextY);
            sum += (long) readMatrix.getDoubleAt(prevX, y);
        } else {
            sum += (long) readMatrix.getDoubleAt(x, prevY);
            sum += (long) readMatrix.getDoubleAt(nextX, prevY);
            sum += (long) readMatrix.getDoubleAt(nextX, y);
            sum += (long) readMatrix.getDoubleAt(x, nextY);
            sum += (long) readMatrix.getDoubleAt(prevX, y);
            sum += (long) readMatrix.getDoubleAt(prevX, prevY);
        }

        sum -= 6 * val;

        double delta = sum / 6.0;
        double d = val + delta * diffCon;

        d *= evapRate;

//        double newState = d < 0 ? 0.0 : d >= MAX ? (double) MAX : d;
        double newState = d < MIN ? MIN : d >= MAX ? MAX : d;
        
        writeMatrix.putDoubleAt(x, y, newState);
    }

    /**
     * Runs the diffusion with the current rates and values. Following
     * the Swarm class, it is roughly newValue = evap(ownValue +
     * diffusionConstant * (nghAvg - ownValue)) where nghAvg is the
     * weighted average of a cells six neighbors, and ownValue is the
     * current value for the current cell.<p>
     *
     * Values from the readMatrix are used to calculate diffusion. This value
     * is then written to the writeMatrix. When this has been done for every
     * cell in the grid, the writeMatrix is copied to the readMatrix.
     */
    public void diffuse() {

        int endX = xSize - 1;

        prevX = endX;
        x = 0;
        while (x < endX) {
            nextX = x + 1;

            computeColumn();
            prevX = x;
            x = nextX;
        }
        nextX = 0;
        computeColumn();
        writeMatrix.copyMatrixTo(readMatrix);
    }

    /**
     * The notion of a vonNeumann neighborhood is incoherent for a
     * hexagonal grid. Consequently this method always throws an
     * UnsupportedOperationException. To get neighbors see
     * {@link #getNeighbors(int, int, int) getNeighbors} or
     * {@link #getNeighbors(int, int) getNeighbors}.
     *
     * @throws UnsupportedOperationException when called
     */
    public double[] getVonNeumannNeighbors(int x, int y) {
        throw new UnsupportedOperationException("Cannot get VonNeumann or Moore neighbors from hexagonal spaces");
    }

    /**
     * The notion of a vonNeumann neighborhood is incoherent for a
     * hexagonal grid.  Consequently this method always throws an
     * UnsupportedOperationException. To get neighbors see
     * {@link #getNeighbors(int, int, int) getNeighbors} or
     * {@link #getNeighbors(int, int) getNeighbors}.
     *
     * @throws UnsupportedOperationException when called
     */

    public double[] getVonNeumannNeighbors(int x, int y, int xExtent,
        int yExtent) {
        throw new UnsupportedOperationException("Cannot get VonNeumann or Moore neighbors from hexagonal spaces");
    }

    /**
     * The notion of a Moore neighborhood is incoherent on a hexagonal
     * grid. Consequently this method always throws an
     * UnsupportedOperationException. To get neighbors see
     * {@link #getNeighbors(int, int, int) getNeighbors} or
     * {@link #getNeighbors(int, int) getNeighbors}.
     *
     * @throws UnsupportedOperationException when called
     */

    public double[] getMooreNeighbors(int x, int y) {
        throw new UnsupportedOperationException("Cannot get VonNeumann or Moore neighbors from hexagonal spaces");
    }

    /**
     * The notion of a Moore neighborhood is incoherent for a
     * hexagonal grid.  Consequently this method always throws an
     * UnsupportedOperationException. To get neighbors see
     * {@link #getNeighbors(int, int, int) getNeighbors} or
     * {@link #getNeighbors(int, int) getNeighbors}.
     *
     * @throws UnsupportedOperationException when called
     */

    public double[] getMooreNeighbors(int x, int y, int xExtent, int yExtent) {
        throw new UnsupportedOperationException("Cannot get VonNeumann or Moore neighbors from hexagonal spaces");
    }

    /**
     * Returns the ring of neighbors with a radius of 1 surrounding the
     * cell at x, y.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return an array of doubles in clockwise order starting with the
     * north or "12" neighboring cell
     */
    public double[] getNeighbors(int x, int y) {
        return getNeighbors(x, y, 1);
    }

    /**
     * Returns the rings of neighbors surrounding the cell at x, y. The number
     * of rings is specified by the radius parameter.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param radius the number of neighbor rings to return
     * @return an array of doubles beginning with the outermost ring of
     * neighbors, starting with the north or "12 o'clock" neighboring cell,
     * continuing clockwise and spiraling inwards
     */
    public double[] getNeighbors(int x, int y, int radius) {
        if (radius < 1) return new double[0];
    
        if (radius == 1) return singleExtent(x, y);
        if (radius == 2) return doubleExtent(x, y);
        else return gtTwoExtent(x, y, radius);
    }

    private double[] gtTwoExtent(int x, int y, int extent) {
        double[] rarray = new double[3 * extent * (extent + 1)];
        int destIndex = 0;

        x = xnorm(x);
        y = ynorm(y);
        if (x % 2 == 0) {
            for (int radius = extent; radius > 2; radius--) {
                double[] src = getEvenRing(x, y, radius);

                System.arraycopy(src, 0, rarray, destIndex, src.length);
                destIndex += src.length;
            }
        } else {
            for (int radius = extent; radius > 2; radius--) {
                double[] src = getOddRing(x, y, radius);

                System.arraycopy(src, 0, rarray, destIndex, src.length);
                destIndex += src.length;
            }
        }

        double[] src = doubleExtent(x, y);

        System.arraycopy(src, 0, rarray, destIndex, src.length);
    
        return rarray;
    }

    private double[] getEvenRing(int x, int y, int radius) {
        double[] rarray = new double[radius * 6];
        int yVal = y - radius;
    
        rarray[0] = getValueAt(x, yVal++);
    
        int aIndex = 1;
        int limit = x + radius;
        int xVal = x + 1;

        while (xVal <= limit) {
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            if (xVal > limit) {
                yVal++;
                break;
            }
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            yVal++;
        }

        xVal = x + radius;
        for (int i = 0; i < radius; i++)
            rarray[aIndex++] = getValueAt(xVal, yVal++);

        if (xVal % 2 != 0) {
            xVal--;
            yVal--;
            rarray[aIndex++] = getValueAt(xVal, yVal++);
        }

        xVal--;

        while (xVal > x) {
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            if (xVal == x) break;
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            yVal++;
        }

        yVal = y + radius;
        rarray[aIndex++] = getValueAt(x, yVal);
        rarray[aIndex++] = getValueAt(x - 1, yVal);

        yVal--;
        xVal = x - 2;
        limit = x - radius;
        while (xVal >= limit) {
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            if (xVal < limit) {
                yVal--;
                break;
            }
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            yVal--;
        }

        xVal = x - radius;
        for (int i = 0; i < radius; i++) 
            rarray[aIndex++] = getValueAt(xVal, yVal--);

        xVal++;
        if (xVal % 2 != 0) {
            yVal++;
            rarray[aIndex++] = getValueAt(xVal++, yVal--);
        }
    
        while (xVal < x) {
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            if (xVal == x) break;
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            yVal--;
        }

        //printArray(rarray);
        return rarray;
    }

    private double[] getOddRing(int x, int y, int radius) {
    
        double[] rarray = new double[radius * 6];

        rarray[0] = getValueAt(x, y - radius);
        rarray[1] = getValueAt(x + 1, y - radius);
    
        int aIndex = 2;
        int xVal = x + 2;
        int yVal = (y - radius) + 1;
        int limit = x + radius;
      
        while (xVal <= limit) {
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            if (xVal > limit) {
                yVal++;
                break;
            }
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            yVal++;
        }
    
        xVal = x + radius;
        for (int i = 0; i < radius; i++)
            rarray[aIndex++] = getValueAt(xVal, yVal++);

        if (xVal % 2 != 0) {
            yVal--;
            xVal--;
            rarray[aIndex++] = getValueAt(xVal, yVal++);
        }
      
        xVal--;
        while (xVal > x) {
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            yVal++;
        }

        rarray[aIndex++] = getValueAt(x, y + radius);
        yVal = y + radius - 1;
        xVal = x - 1;

        limit = x - radius;
        while (xVal >= limit) {
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            if (xVal < limit) {
                yVal--;
                break;
            }
            rarray[aIndex++] = getValueAt(xVal--, yVal);
            yVal--;
        }
    
        xVal = x - radius;
        for (int i = 0; i < radius; i++)
            rarray[aIndex++] = getValueAt(xVal, yVal--);

        xVal++;
        if (xVal % 2 != 0) {
            yVal++;
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            yVal--;
        }
    
        while (xVal < x) {
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            if (xVal == x) break;
            rarray[aIndex++] = getValueAt(xVal++, yVal);
            yVal--;
        }

        //printArray(rarray);
        return rarray;
    }
  
    /*
     private void printArray(double[] array) {
     System.out.println("array: ");
     for (int i = 0; i < array.length; i++) {
     System.out.print(array[i] + ", ");
     }

     System.out.println("\n");
     }
     */

    private double[] doubleExtent(int x, int y) {
        double[] rarray = new double[18];
        int normX = xnorm(x);
        int normY = ynorm(y);
    
        if (normX % 2 == 0) {
            rarray[0] = getValueAt(normX, normY - 2);
            rarray[1] = getValueAt(normX + 1, normY - 1);
            rarray[2] = getValueAt(normX + 2, normY - 1);
            rarray[3] = getValueAt(normX + 2, normY);
            rarray[4] = getValueAt(normX + 2, normY + 1);
            rarray[5] = getValueAt(normX + 1, normY + 2);
            rarray[6] = getValueAt(normX, normY + 2);
            rarray[7] = getValueAt(normX - 1, normY + 2);
            rarray[8] = getValueAt(normX - 2, normY + 1);
            rarray[9] = getValueAt(normX - 2, normY);
            rarray[10] = getValueAt(normX - 2, normY - 1);
            rarray[11] = getValueAt(normX - 1, normY - 1);
      
        } else {
            rarray[0] = getValueAt(normX, normY - 2);
            rarray[1] = getValueAt(normX + 1, normY - 2);
            rarray[2] = getValueAt(normX + 2, normY - 1);
            rarray[3] = getValueAt(normX + 2, normY);
            rarray[4] = getValueAt(normX + 2, normY + 1);
            rarray[5] = getValueAt(normX + 1, normY + 1);
            rarray[6] = getValueAt(normX, normY + 2);
            rarray[7] = getValueAt(normX - 1, normY + 1);
            rarray[8] = getValueAt(normX - 2, normY + 1);
            rarray[9] = getValueAt(normX - 2, normY);
            rarray[10] = getValueAt(normX - 2, normY - 1);
            rarray[11] = getValueAt(normX - 1, normY - 2);
        }

        System.arraycopy(singleExtent(x, y), 0, rarray, 12, 6);
        return rarray;
    }

    private double[] singleExtent(int x, int y) {
        double[] rarray = new double[6];
        int normX = xnorm(x);
        int normY = ynorm(y);
    
        if (normX % 2 == 0) {
            rarray[0] = getValueAt(normX, normY - 1);
            rarray[1] = getValueAt(normX + 1, normY);
            rarray[2] = getValueAt(normX + 1, normY + 1);
            rarray[3] = getValueAt(normX, normY + 1);
            rarray[4] = getValueAt(normX - 1, normY + 1);
            rarray[5] = getValueAt(normX - 1, normY);
        } else {
            int top = normY - 1;

            rarray[0] = getValueAt(normX, top);
            rarray[1] = getValueAt(normX + 1, top);
            rarray[2] = getValueAt(normX + 1, normY);
            rarray[3] = getValueAt(normX, normY + 1);
            rarray[4] = getValueAt(normX - 1, normY);
            rarray[5] = getValueAt(normX - 1, top);
        }

        return rarray;
    }
    
    /**
     * Finds the maximum grid cell value within a specified radius from
     * the specified origin coordinate. This searches through the
     * neighborhood rings and returns the maximum value.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param radius the neighborhood radius defining the area to search
     * @param includeOrigin include object at origin in search
     * @return the values determined to be the maximum.
     */
    public double[] findMaximum(int x, int y, int radius,
        boolean includeOrigin) {
        double[] dArray = getNeighbors(x, y, radius);

        // need to extend the array here
        if (includeOrigin) {
            double[] newArray = new double[dArray.length + 1];

            System.arraycopy(dArray, 0, newArray, 0, dArray.length);
            newArray[newArray.length - 1] = getValueAt(x, y);
            dArray = newArray;
        }

        return compareMax(dArray);
    }

    /**
     *
     * Finds the maximum grid cell value within a specified radius from
     * the specified origin coordinate. This searches through the
     * neighborhood rings and returns the maximum value.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param radius the neighborhood radius defining the area to search
     * @param includeOrigin include object at origin in search
     * @return the values determined to be the maximum.
     */
    public double[] findMinimum(int x, int y, int radius, boolean includeOrigin) {
        double[] dArray = this.getNeighbors(x, y, radius);

        // need to extend the array here
        if (includeOrigin) {
            double[] newArray = new double[dArray.length + 1];

            System.arraycopy(dArray, 0, newArray, 0, dArray.length);
            newArray[newArray.length - 1] = getValueAt(x, y);
            dArray = newArray;
        }

        return compareMin(dArray);
    }
}
