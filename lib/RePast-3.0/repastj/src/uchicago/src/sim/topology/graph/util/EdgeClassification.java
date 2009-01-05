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
package uchicago.src.sim.topology.graph.util;

import java.io.Serializable;

/**
 * This enumeration defines values for a Tree Edge, a Back Edge
 * and a Forward Edge.  Edge definitions taken from:
 * "Introduction to Algorithms" by Thomas Corman, Charles Leiserson
 * 	and Ronald Rivest, pg. 482.
 * 
 * @author Tom Howe
 * @version $Revision$  
 */
public class EdgeClassification implements Serializable {
	
	static final long serialVersionUID = 4541984793557154784L;
	private String name;
	
	/**
	 * An edge in the depth-first forest G.  Edge(u,v) is
	 * a tree edge if v was first discovered by exploring edge 
	 * (u,v).
	 */
	public static EdgeClassification TREE_EDGE = 
			new EdgeClassification("Tree");
	
	/**
	 * An edge (u,v) connecting a vertex u to an ancestor v in a depth
	 * first tree.  Self loops are considered to be back edges. 
	 */
	public static EdgeClassification BACK_EDGE = 
			new EdgeClassification("Back");
	
	/**
	 * An nontree edge (u, v) connecting a vertex u to a decendant
	 * v in a depth-first tree.
	 */		
	public static EdgeClassification FORWARD_EDGE =
		new EdgeClassification("Forward");

	private EdgeClassification(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
