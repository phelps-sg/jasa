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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.collection.NewMatrix;
import uchicago.src.sim.util.SimUtilities;


/**
 * A discrete 2 dimensional grid of objects, accessed by x and y
 * coordinates.
 *
 * @author Nick Collier
 */


public class Object2DGrid implements Discrete2DSpace {

    public static final int PGM_ASCII = 0;
    public static final int RASTER_ASCII = 1;

    //protected Matrix matrix;
    protected NewMatrix matrix;
    protected int xSize;
    protected int ySize;
    protected Neighborhooder VNneigh;
    protected Neighborhooder Mneigh;

    /**
     * Constructs a grid with the specified size.
     * @param xSize the size of the lattice in the x dimension.
     * @param ySize the size of the lattice in the y dimension.
     */
    public Object2DGrid(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        matrix = new NewMatrix(xSize, ySize);
        VNneigh = new VNNeighborhooder(this);
        Mneigh = new MooreNeighborhooder(this);
    }
  
    /**
     * Constructs a grid from an InputStream. Only ASCII PGM format files
     * as the ssource of the InputStream are supported at this
     * time. Code adapted from Nelson Minar's implementation of
     * SugarScape with Swarm.
     */
    public Object2DGrid(InputStream stream, int type) {
        if (type != PGM_ASCII) {
            throw new IllegalArgumentException("File type not supported.");
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(stream));

        init(in);
    }

    /**
     * Constructs a grid from a file. Only ASCII PGM files are supported
     * at this time. Code adapted from Nelson Minar's implementation of
     * SugarScape with Swarm.
     */
    public Object2DGrid(String fileName, int type) {
        if (type != PGM_ASCII) {
            throw new IllegalArgumentException("File type not supported.");
        }
    
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            init(in);
        } catch (IOException ex) {
            SimUtilities.showError("Error Reading image file", ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void init(BufferedReader in) {
        try {
            StringTokenizer tok;

            String str = in.readLine();

            if (!str.equals("P2")) {
                throw new UnsupportedEncodingException("File is not in PGM ascii format");
            }

            str = in.readLine();
            tok = new StringTokenizer(str);
            xSize = Integer.valueOf(tok.nextToken()).intValue();
            ySize = Integer.valueOf(tok.nextToken()).intValue();

            
            in.readLine();

            str = "";
            String line = in.readLine();

            while (line != null) {
                str += line + " ";
                line = in.readLine();
            }
            in.close();
  
            tok = new StringTokenizer(str);
            matrix = new NewMatrix(xSize, ySize);
            //System.out.println(xSize + " " + ySize);

            for (int i = 0; i < xSize; i++) {
                for (int j = 0; j < ySize; j++) {
                    matrix.put(i, j, Integer.valueOf(tok.nextToken()));
                }
            }
        } catch (IOException ex) {
            SimUtilities.showError("Error Reading image file", ex);
            ex.printStackTrace();
            System.exit(0);
        }
        VNneigh = new VNNeighborhooder(this);
        Mneigh = new MooreNeighborhooder(this);
    }
  
    /**
     * Gets the von Neumann neighbors of the object at x, y. Objects are returned
     * in west, east, north, south order. The object at x, y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls whether nulls (nothing at x,y) should be returned
     * @return a vector of objects (and possibly nulls) in west, east, north,
     * south order
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector getVonNeumannNeighbors(int x, int y, boolean returnNulls) {
        int[] extents = {1, 1};

        return VNneigh.getNeighbors(x, y, extents, returnNulls);
    }

    /**
     * Gets the extended von Neumann neighbors of the object at x, y. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * Objects are return in west, east, north, south order.The
     * most distant objects are returned first, that is, all the objects to the
     * west starting with the most distant, then those to the east and so on.
     * The Object at x,y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @param returnNulls whether nulls should be returned
     * @return a vector of objects (and possibly nulls) in west, east, north,
     * south order with the most distant object first.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector getVonNeumannNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        int[] extents = {xExtent, yExtent};

        return VNneigh.getNeighbors(x, y, extents, returnNulls);
    }

    /**
     * Gets the Moore neighbors of the object at x, y. Objects are returned by
     * row starting with the "NW corner" and ending with the "SE corner."
     * The Object at x, y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls should the returned Vector contain null objects
     * @return a vector of objects (and possibly nulls) ordered by row starting
     * with the "NW corner" and ending with the "SE corner."
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */

    public Vector getMooreNeighbors(int x, int y, boolean returnNulls) {
        int[] extents = {1, 1};

        return Mneigh.getNeighbors(x, y, extents, returnNulls);
    }

    /**
     * Gets the extended Moore neighbors of the object at x, y. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * Objects are returned by row starting with the "NW corner" and ending with
     * the "SE corner." The Object at x,y is not returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @param returnNulls should the returned Vector contain null objects
     * @return a vector of objects (and possibly nulls) ordered by row starting
     * with the "NW corner" and ending with the "SE corner."
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */

    public Vector getMooreNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        int[] extents = {xExtent, yExtent};

        return Mneigh.getNeighbors(x, y, extents, returnNulls);
    }

    /**
     * Sets the comparator class used by the findMaximum and findMinimum methods.
     *
     * @param comparator the comparator to use for finding maximum and minimum.
     */
    public void setComparator(Comparator comparator) {
        VNneigh.setComparator(comparator);
        Mneigh.setComparator(comparator);
    }

    /**
     * Finds the maximum grid cell occupant within a specified range from
     * the specified origin coordinate. Maximum is determined by the default
     * or user supplied comparator class. The default comparator compares
     * objects using the >, <, and = operators on the hashcode of the objects.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param range the range out from the coordinate to search
     * @param includeOrigin include object at origin in search
     * @param neighborhoodType the type of neighborhood to search. Can be one
     * of Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
     * @return the Objects determined to be the maximum.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector findMaximum(int x, int y, int range, boolean includeOrigin, 
        int neighborhoodType) {
        Vector v = new Vector();
        int[] extent = {range, range};

        if (neighborhoodType == VON_NEUMANN)
            v = VNneigh.findMaximum(x, y, extent, includeOrigin);
        if (neighborhoodType == MOORE)
            v = Mneigh.findMaximum(x, y, extent, includeOrigin);
        return v;
    }

    /**
     * Finds the minimum grid cell occupant within a specified range from
     * the specified origin coordinate. Minimum is determined by the default
     * or user supplied comparator class. The default comparator compares
     * objects using the >, <, and = operators on the hashcode of the objects.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param range the range out from the coordinate to search
     * @param includeOrigin include object at origin in search
     * @param neighborhoodType the type of neighborhood to search. Can be one
     * of Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
     * @return the Objects determined to be the maximum.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector findMinimum(int x, int y, int range, boolean includeOrigin,
        int neighborhoodType) {
        Vector v = new Vector();
        int[] extent = {range, range};

        if (neighborhoodType == VON_NEUMANN) 
            v = VNneigh.findMinimum(x, y, extent, includeOrigin);
        else if (neighborhoodType == MOORE)
            v = Mneigh.findMinimum(x, y, extent, includeOrigin);
        return v;
    }

    protected void rangeCheck(int x, int y) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize)
            throw new IndexOutOfBoundsException
                ("x or y coordinate is out of bounds");
    }

    // Discrete2dSpace interface
    /**
     * Gets the size of the x dimension.
     */
    public int getSizeX() {
        return xSize;
    }

    /**
     * Gets the size of the y dimension.
     */
    public int getSizeY() {
        return ySize;
    }

    /**
     * Gets the size as a Dimension.
     */
    public Dimension getSize() {
        return new Dimension(xSize, ySize);
    }

    /**
     * Gets the object at (x,y)
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Object getObjectAt(int x, int y) {
        rangeCheck(x, y);
        return matrix.get(x, y);
    }

    /**
     * Gets the double value at (x,y) if possible
     * @throws java.lang.IllegalArgumentException if object at x,y cannot
     * be converted to a number.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public double getValueAt(int x, int y) {
        rangeCheck(x, y);
        Object o = matrix.get(x, y);

        if (o instanceof Number) {
            Number n = (Number) o;

            return n.doubleValue();
        } else {
            throw new IllegalArgumentException("Object cannot be converted to a long");
        }
    }

    /**
     * Puts the specified object at (x,y)
     * @param x the x coordinate
     * @param y the y coordinate
     * @param object the object to put at (x,y)
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public void putObjectAt(int x, int y, Object object) {
        rangeCheck(x, y);
        matrix.put(x, y, object);
    }

    /**
     * Puts the specified double at (x,y)
     * @param x the x coordinate
     * @param y the y coordinate
     * @param value the double to put at (x,y)
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public void putValueAt(int x, int y, double value) {
        rangeCheck(x, y);
        matrix.put(x, y, new Double(value));
    }

    /**
     * Returns the matrix collection object associated with this 2d grid
     */
    public BaseMatrix getMatrix() {
        return matrix;
    }
}
