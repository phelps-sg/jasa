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
package uchicago.src.sim.topology.space.d2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import uchicago.src.collection.NewMatrix;
import uchicago.src.sim.topology.space.Location;
import uchicago.src.sim.util.SimUtilities;

/**
 * A discrete 2 dimensional grid of objects, accessed by x and y
 * coordinates.
 *
 * @version $Revision$ $Date$
 * @author Mark Diggory
 */

public class Object2DGrid extends AbstractObject2DSpace implements Object2DSpace {

	public static final int PGM_ASCII = 0;
	
	/**
	 * Constructs a grid with the specified size.
	 * @param xSize the size of the lattice in the x dimension.
	 * @param ySize the size of the lattice in the y dimension.
	 */
	public Object2DGrid(int xSize, int ySize) {
        this("Object2DGrid", xSize, ySize);
	}

    /**
     * Constructs a grid with the specified size.
     * @param xSize the size of the lattice in the x dimension.
     * @param ySize the size of the lattice in the y dimension.
     */
    public Object2DGrid(String type, int xSize, int ySize) {
        super(type);
        matrix = new NewMatrix(xSize, ySize);
    }
 
    /**
     * Constructs a grid from an InputStream. Only ASCII PGM format files
     * as the source of the InputStream are supported at this
     * time. Code adapted from Nelson Minar's implementation of
     * SugarScape with Swarm.
     */
    public Object2DGrid(InputStream stream) {
        this("Object2DGrid", stream);
    }
       
	/**
	 * Constructs a grid from an InputStream. Only ASCII PGM format files
	 * as the source of the InputStream are supported at this
	 * time. Code adapted from Nelson Minar's implementation of
	 * SugarScape with Swarm.
	 */
	public Object2DGrid(String type, InputStream stream) {
        super(type);
        
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));

		init(in);
	}

    /**
     * Constructs a grid from a file. Only ASCII PGM files are supported
     * at this time. Code adapted from Nelson Minar's implementation of
     * SugarScape with Swarm.
     */
    public Object2DGrid(String type, String fileName) {
        super(type);

        //StringTokenizer tok;

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));

            init(in);
        } catch (IOException ex) {
            SimUtilities.showError("Error Reading image file", ex);
            ex.printStackTrace();
        }
    }
    
    	/**
	 * Constructs a grid from a file. Only ASCII PGM files are supported
	 * at this time. Code adapted from Nelson Minar's implementation of
	 * SugarScape with Swarm.
	 */
	public Object2DGrid(String fileName) {
        this("Object2DGrid",fileName);
	}

	/**
	 * Gets the Object2DLocation object at the specified coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @throws IndexOutOfBoundsException if the given coordinates are out of
	 * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
	 */
	public Location getLocation(int x, int y) {
		
		Location cell = (Location) getMatrix().get(xnorm(x), ynorm(y));
		
		if(cell == null){
			cell = new Object2DLocationImpl(xnorm(x), ynorm(y));
		}
		
		return cell;
	}
	

	protected void init(BufferedReader in) {
		try {
			StringTokenizer tok;

			String str = in.readLine();

			if (!str.equals("P2")) {
				throw new UnsupportedEncodingException("File is not in PGM ascii format");
			}

			str = in.readLine();
			tok = new StringTokenizer(str);
			int xSize = Integer.valueOf(tok.nextToken()).intValue();
			int ySize = Integer.valueOf(tok.nextToken()).intValue();

			tok = null;
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
					this.getLocation(i, j).add(Double.valueOf(tok.nextToken()));
				}
			}
		} catch (IOException ex) {
			SimUtilities.showError("Error Reading image file", ex);
			ex.printStackTrace();
			System.exit(0);
		}

	}

    /**
     * A grid cell who can only contain one element.
     */
    public class Object2DLocationImpl extends Abstract2DLocation implements Object2DLocation {

        protected Object2DLocationImpl(int x, int y){
            super(x,y);
            capacity = 1;
            elements = new ArrayList();
        }
    }
    
}