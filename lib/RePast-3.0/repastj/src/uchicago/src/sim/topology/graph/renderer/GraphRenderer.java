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
package uchicago.src.sim.topology.graph.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uchicago.src.sim.topology.graph.Graph;

/**
 * This class should be used to render a graph.  It
 * can be subclassed in order to make more specific rules
 * about display particulars.
 *
 * @author Tom Howe
 * @version $Revision$
 */
public class GraphRenderer {
	private NodeStyle nodeStyle;
	private Map edgeColors;
	private RenderableGraphLayout gl;
	private Graph g;
	private Set nodes;
	//private Map nodeMap;
	private Graphics2D graphics;

	/**
	 *
	 */
	public GraphRenderer() {
		super();
		edgeColors = new HashMap();
		//nodeMap = new HashMap();
		gl = null;
	}

	public void addGraph(Graph g) throws Exception {
		if ((nodes != null) && !g.getNodes().equals(nodes)) {
			throw new Exception("This graph doesn't contain" + "the same node set");
		}

		edgeColors.put(g.getRelationType(), Color.red);
		this.g = g;
	}

	public void setEdgeColor(String type, Color c) {
		edgeColors.put(type, c);
	}

	public void render() {
		gl.update();
		Iterator i = g.iterator();
		while (i.hasNext()) {
			Object next = i.next();
			nodeStyle.processObject(next);
			graphics.setColor(nodeStyle.getColor());
			//graphics.fill(nodeStyle.)
		}
	}

	public void setOutput(Graphics2D g) {
		graphics = g;
	}

	public void setLayout(RenderableGraphLayout gl) {
		this.gl = gl;
	}
}
