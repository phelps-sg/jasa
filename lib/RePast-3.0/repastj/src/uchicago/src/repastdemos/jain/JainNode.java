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
package uchicago.src.repastdemos.jain;

import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.network.DefaultNode;

public class JainNode extends DefaultDrawableNode{

	private String species;
	private float writePop;
  private float readPop;
	
	public JainNode(){}
	
	public JainNode(int x, int y, String label){
		init(x, y, label);
	}
	
	public JainNode(int x, int y, float pop, String label){
		init(x, y, label);
		readPop = pop;
	}
	
	public void init(int x, int y, String label){
		species = label;
		OvalNetworkItem oval = new OvalNetworkItem(x, y);
		oval.setLabel(species);
		setDrawable(oval);
	}

	public float getPop(){
		return readPop;
	}
	
	public void setPop(float pop){
		readPop = pop;
	}

	public void makeEdgeTo(DefaultNode node, float strength, Color color){
		if(!(hasEdgeTo(node))){
			JainEdge edge = new JainEdge(this, node, strength, color);
			addOutEdge(edge);
			node.addInEdge(edge);
		}
	}

	public void calcPop(ArrayList nodeSet){
		float pop1 = 0.0f;
		ArrayList fromEdges = getInEdges();
		for(int i = 0 ; i < fromEdges.size() ; i ++){
			JainEdge edge = (JainEdge) fromEdges.get(i);
			pop1 += edge.getStrength() * ((JainNode) edge.getFrom()).getPop();
		}

		float pop2 = 0.0f;
		for(int i = 0 ; i < nodeSet.size() ; i++){
			JainNode node = (JainNode) nodeSet.get(i);
			fromEdges = node.getInEdges();
			for(int j = 0 ; j < fromEdges.size(); j++){
				JainEdge edge = (JainEdge) fromEdges.get(j);
				pop2 += edge.getStrength() * ((JainNode) edge.getFrom()).getPop();
			}
		}
		pop2 *= readPop;

		writePop = pop1 - pop2;
	}

	public void updatePop(){
		readPop = writePop;
	}
}
