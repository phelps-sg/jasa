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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StreamTokenizer;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.collection.DoubleMatrix;


/**
 * A class that represents a raster image as a space.  This is designed to be
 * used in conjunction with a GIS system such as ESRI ArcGis or GRASS.
 * The raster image is treated as a continuous system of coordinates with
 * cells that hold values underlying.  Much of the code for reading the
 * ESRI ASCII Raster format is based on code by <a href:"http://www.geog.leeds.ac.uk/staff/i.turnton/i.turton.html">Ian Turton</a> Centre for Computational
 * Geography University of Leeds, LS2 9Jt, 1998. <BR>
 * <a href="mailto:ian.geog.leeds.ac.uk">i.turton@geog.leeds.ac.uk</a>, as 
 * included in <a href="http://geotools.sourceforge.net">GeoTools</a>, an open
 * source gis visualization framework.
 **/

public class RasterSpace implements Serializable, Discrete2DSpace {

    double sparseness;
    double originx;
    double originy;
    double termx;
    double termy;
    double cellSize;
    double nodata;
    double min = Double.NEGATIVE_INFINITY;
    double max = Double.POSITIVE_INFINITY;
    double missing = Double.NaN;
    int height;
    int width;
	
    DoubleMatrix matrix;
	
    /**
     * Constructs an empty RasterSpace.
     * @param top The uppermost y coordinate
     * @param left The leftmost x coordinate
     * @param cellSize The size of a raster cell
     * @param h The height of the space
     * @param w The width of the space
     **/
    public RasterSpace(double top, double left, double cellSize, int h, int w) {
        this.cellSize = cellSize;
        originx = Math.floor(left) * cellSize;
        originy = top - h;
        originy = Math.floor(originy / cellSize) * cellSize;
        termy = top;
        height = h;
        width = w;
        matrix = new DoubleMatrix(h, w);
    }

    /**
     * Constructs an empty RasterSpace.
     * @param left The westernmost x coordinate
     * @param bottom The southernmost y coordinate
     * @param right The easternmost x coordinate
     * @param top The northernmost y coordinate
     * @param cellSize The size of the raster cell
     **/
    public RasterSpace(double left, double bottom, double right, double top, double cellSize, int w, int h) {
        this.cellSize = cellSize;
        originx = left;
        originy = bottom;
        termx = right;
        termy = top;
        height = h;
        width = w;
        matrix = new DoubleMatrix(h, w);
    }

    /**
     * Creates a RasterSpace from an ESRI ASCII Raster file.
     * @param name The name of the ESRI ASCII raster file.
     * @throws java.io.IOException if there is a problem reading the file
     **/
    public RasterSpace(String name) throws IOException {
        FileInputStream stream = new FileInputStream(name);

        init(stream);
    }

    /**
     * Creates a RasterSpace from an ESRI ASCII Raster file connected to
     * the specified input stream.
     * @param stream The input stream for the raster file of the ESRI
     * ASCII raster file.
     * @throws java.io.IOException if there is a problem reading from the
     * stream
     **/
    public RasterSpace(InputStream stream) throws IOException {
        init(stream);
    }

    private void init(InputStream stream) throws IOException {
        int type;
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StreamTokenizer st = new StreamTokenizer(r);

        st.parseNumbers();
        st.wordChars('_', '_');
        st.eolIsSignificant(false);
        st.lowerCaseMode(true);
        //cols
        type = st.nextToken();
        type = st.nextToken();
        width = (int) st.nval;
        //rows
        type = st.nextToken();
        type = st.nextToken();
        height = (int) st.nval;
        //xllcorner
        type = st.nextToken();
        type = st.nextToken();
        originx = st.nval;
        //yllcorner
        type = st.nextToken();
        type = st.nextToken();
        originy = st.nval;
        //cellSize
        type = st.nextToken();
        type = st.nextToken();
        cellSize = st.nval;
        //termx and termy
        termx = Math.floor(originx) + cellSize * width;
        termy = Math.floor(originy) + cellSize * height;
        //missing
        type = st.nextToken();
        if (type == StreamTokenizer.TT_NUMBER) {
            st.pushBack();
            nodata = missing;
        } else {
            type = st.nextToken();
            nodata = st.nval;
        }
        st.ordinaryChars('E', 'E');
        matrix = new DoubleMatrix(width, height);
        double d1;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                st.nextToken();
                d1 = st.nval;
                type = st.nextToken();
                if (type != StreamTokenizer.TT_NUMBER && type != StreamTokenizer.TT_EOF) {
                    st.nextToken();
                    d1 = d1 * Math.pow(10.0, st.nval);
                } else {
                    st.pushBack();
                }
                min = Math.min(min, d1);
                max = Math.max(max, d1);
                matrix.putDoubleAt(j, i, d1);
            }
        }
    }

    /**
     * Determines if a set of coordinates is on the raster.
     **/
    public final boolean onSurface(double x, double y) {
        return (x >= originx && x < originx + (width * cellSize) && 
                y >= originy && y < originy + ((height) * cellSize));
    }

    public final int getCellRow(double y) {
        int row = ((int) Math.floor(((double) (height) * cellSize - y + originy) / cellSize));

        if (row < 0)
            row = 0;
        return row;
    }

    public final int getCellCol(double x) {
        int col = ((int) Math.floor((x - originx) / cellSize));

        if (col < 0)
            col = 0;
        return col;
    }

    public BaseMatrix getMatrix() {
        return matrix;
    }

    public double getOriginX() {
        return originx;
    }

    public double getOriginY() {
        return originy;
    }

    public double getTermX() {
        return termx;
    }

    public double getTermY() {
        return termy;
    }

    /**
     * Return the object at an integer point.
     * @param x The column
     * @param y The Row
     **/
    public Object getObjectAt(int x, int y) {
        return matrix.get(x, y);
    }

    /**
     * Return the object at a coordinate point.
     * @param x The x coordinate
     * @param y The y cooredinate
     **/
    public Object getObjectAt(double x, double y) {
        return matrix.get(getCellCol(x), getCellRow(y));
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public int getSizeX() {
        return width;
    }

    public int getSizeY() {
        return height;
    }

    /**
     * Get the value at an integer point.
     * @param x The column
     * @param y The row
     **/
    public double getValueAt(int x, int y) {
        Object o = matrix.get(x, y);

        if (o instanceof Number) {
            Number n = (Number) o;

            return n.doubleValue();
        } else {
            throw new IllegalArgumentException("Object cannot be converted to a long");
        }
    }
	
    /**
     * Get the value at a coordinate point.
     * @param x The x coordinate
     * @param y The y coordinate
     **/
    public double getValueAt(double x, double y) {
        Object o = matrix.get(getCellCol(x), getCellRow(y));

        if (o instanceof Number) {
            Number n = (Number) o;

            return n.doubleValue();
        } else {
            throw new IllegalArgumentException("Object cannot be converted to a long");
        }
    }

    /**
     * Put an object at an integer point.
     * @param x The column
     * @param y The row
     **/
    public void putObjectAt(int x, int y, Object object) {
        matrix.put(x, y, object);
    }

    /**
     * Put an object at a coordinate point.
     * @param x The x coordinate
     * @param y The y coordinate
     **/
    public void putObjectAt(double x, double y, Object object) {
        matrix.put(getCellCol(x), getCellCol(y), object);
    }

    /**
     * Put a value at an integer point.
     * @param x The column
     * @param y The row
     **/
    public void putValueAt(int x, int y, double value) {
        matrix.putDoubleAt(x, y, value);
    }
	
    /**
     * Put a value at a coordinate point.
     * @param x The x coordinate
     * @param y The y coordinate
     **/
    public void putValueAt(double x, double y, double value) {
        matrix.putDoubleAt(getCellCol(x), getCellRow(y), value);
    }

    public double getCellSize() {
        return cellSize;
    }
}
	
