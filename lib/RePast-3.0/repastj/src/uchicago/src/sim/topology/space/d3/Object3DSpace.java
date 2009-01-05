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

import uchicago.src.sim.topology.space.Location;
import uchicago.src.sim.topology.space.Space;

/**
 * @author Mark Diggory
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface Object3DSpace extends Space {

    /**
     * Clears the contents the specified location.
     *
     * @param x the x coordinate of the location to clear
     * @param y the y coordinate of the location to clear
     */
    public void removeLocation(int x, int y, int z);

    /**
     * Gets the Object2DLocation object at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Location getLocation(int x, int y, int z);

    /* (non-Javadoc)
     * @see uchicago.src.sim.space.Space#getSizeX()
     */
    public int getSizeX();

    /* (non-Javadoc)
     * @see uchicago.src.sim.space.Space#getSizeY()
     */
    public int getSizeY();

    /* (non-Javadoc)
     * @see uchicago.src.sim.space.Space#getSizeY()
     */
    public int getSizeZ();
    
    /**
     * Releases any superfluous memory. This is only usefull when
     * working with sparse grids.
     */
    public void trim();

    public int xnorm(int x);

    public int ynorm(int y);

    public int znorm(int z);
}