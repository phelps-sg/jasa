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

import uchicago.src.sim.space.Torus;


/**
 * A torus that can hold more than one object in its cells.
 * The cells themselves store their occupants in order of insertion,
 * The list of objects in a cell contains the first object
 * inserted at the beginning of the list and the last object inserted
 * at the end. The object returned by getObject is a
 * <code>OrderedMulti2DLocation</code>.
 *
 * @version $Revision$ $Date$
 */
public class OrderedMulti2DTorus extends OrderedMulti2DGrid implements Torus {

    /**
     * Creates this OrderedMulti2DTorus with the specified dimensions.
     * sparse specifies whether the grid will be sparsely filled or not.
     *
     * @param xSize the number of columns in the grid
     * @param ySize the number of rows in the grid
     * @param sparse whether the grid will be sparsely populated or not
     */
    public OrderedMulti2DTorus(int xSize, int ySize, boolean sparse) {
        super("OrderedMulti2DTorus", xSize, ySize, sparse);
    }

    public OrderedMulti2DTorus(String type, int xSize, int ySize, boolean sparse) {
        super(type, xSize, ySize, sparse);
    }
     
}

