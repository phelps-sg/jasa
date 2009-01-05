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
package uchicago.src.sim.topology.space.d3;

import java.util.ArrayList;

import uchicago.src.sim.topology.space.Location;

/**
 * A discrete 3 dimensional grid of objects, accessed by x, y and z
 * coordinates.
 *
 * @version $Revision$ $Date$
 * @author Mark Diggory
 */

public class Object3DGrid extends AbstractObject3DSpace implements Object3DSpace {

	/**
	 * Constructs a grid with the specified size.
	 * @param xSize the size of the lattice in the x dimension.
	 * @param ySize the size of the lattice in the y dimension.
     * @param zSize the size of the lattice in the z dimension.
	 */
	public Object3DGrid(int xSize, int ySize, int zSize) {
        super("Object3DGrid", xSize, ySize, zSize);
	}

    /**
     * Constructs a grid with the specified size.
     * @param xSize the size of the lattice in the x dimension.
     * @param ySize the size of the lattice in the y dimension.
     * @param zSize the size of the lattice in the z dimension.
     */
    public Object3DGrid(String type, int xSize, int ySize, int zSize) {
        super(type, xSize, ySize, zSize);
    }

	/**
	 * Gets the Location object at the specified coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
     * @param z the z coordinate
	 * @throws IndexOutOfBoundsException if the given coordinates are out of
	 * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
	 */
	public Location getLocation(int x, int y, int z) {
		
		Location cell = (Location) matrix[xnorm(x)][ynorm(y)][znorm(z)];
		
		if(cell == null){
			cell = new Object3DLocationImpl(xnorm(x), ynorm(y), znorm(z));
		}
		
		return cell;
	}

    /**
     * A grid cell who can only contain one element.
     */
    public class Object3DLocationImpl extends Abstract3DLocation implements Object3DLocation {

        protected Object3DLocationImpl(int x, int y, int z){
            super(x,y,z);
            capacity = 1;
            elements = new ArrayList();
        }
    }
    
}