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
package uchicago.src.sim.network;

import java.util.ArrayList;
import java.util.Iterator;

import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;


/*
 * !!!!!!!! EXPERIMENTAL BETA CLASS DO NOT USE !!!!!!!!!
*
* @author Skye Bender-deMoll
* @version $Revision$ $Date$
**/
public class RolfeNet
{
	Class nodeClass;
	Class edgeClass;
	int nNodes = -1;
	double density = -1;
	double clustring =-1;
	double reciProb = -1;
	int steps = -1;

	NetUtilities netUtil = new NetUtilities();

//constructors
	public RolfeNet (){}
	public RolfeNet (Class node, Class edge)
	{
		nodeClass = node;
		edgeClass = edge;
	}
	public RolfeNet (Class node, Class edge, int size)
	{
		nodeClass = node;
		edgeClass = edge;
		nNodes = size;
	}
	public RolfeNet (Class node, Class edge, int size, double dens,
										double clust, double reci, int step)
	{
		nodeClass = node;
		edgeClass = edge;
		nNodes = size;
		density = dens;
		clustring = clust;
		reciProb = reci;
		steps = step;
	}
//accesors
	/**
	*Returns the Class of nodes to be used in constructing the network.  Must be set before
	*makeRolfeNet() is called.
	**/
	public Class getNodeClass()
	{
		return nodeClass;
	}
	/**
	*Sets the Class of nodes to be used in constructing the network.  Must be set before
	*makeRolfeNet() is called.
	**/
	public void setNodeClass(Class node)
	{
		nodeClass = node;
	}
	/**
	*Returns the Class of edges to be used in constructing the network.  Must be set before
	*makeRolfeNet() is called.
	**/
	public Class getEdgeClass()
	{
		return edgeClass;
	}
	/**
	*Sets the Class of edges to be used in constructing the network.  Must be set before
	*makeRolfeNet() is called.
	**/
	public void setEdgeClass(Class edge)
	{
		edgeClass = edge;
	}
	/**
	*Returns the int for the size (number of nodes) in the network to be constructed.  Must be set before
	*makeRolfeNet() is called.
	**/
	public int getSize()
	{
		return nNodes;
	}
	/**
	*Sets the int for the size (number of nodes) in the network to be constructed.  Must be set before
	*makeRolfeNet() is called.
	**/
	public void setSize(int size)
	{
		nNodes = size;
	}
	/**
	*Returns the double of the desired density of the network (ratio of number of existing
	*edges to the maximum possible number of edges)  Must be set before
	*makeRolfeNet() is called.
	**/
	public double getDensity()
	{
		return density;
	}
	/**
	*Sets the double of the desired density of the network (ratio of number of existing
	*edges to the maximum possible number of edges)  Must be set before
	*makeRolfeNet() is called.
	**/
	public void setDensity(double dens)
	{
		density = dens;
	}
	/**Gets the double of the parameter for the desired clustring (density of ego network).
	*Must be set before makeRolfeNet() is called.
	**/
	public double getClustring()
	{
		return clustring;
	}
	public void setClustring(double clust)
	{
		clustring = clust;
	}

	public double getReciProb()
	{
		return reciProb;
	}
	public void setReciProb(double reci)
	{
		reciProb = reci;
	}

	public int getSteps()
	{
		return steps;
	}

	public void setSteps(int step)
	{
		steps = step;
	}
	/**
	*DO NOT USE! Experimental test class for generating nets with a particular
	*distribution
	@throws IllegalAccessException, InstantiationException
	*/
	public ArrayList createRolfeNet()
		throws IllegalAccessException, InstantiationException
	{
		//CHECK RANGES
		if((nodeClass == null) || (edgeClass == null))
		{
			String error = "Unable to construct randomDensityNetwork: nodeClass or edgeClass was not set.";
			RepastException exception = new RepastException(error);
			SimUtilities.showError(error, exception);
		}

		ArrayList nodes = new ArrayList(nNodes);
		int jiTie = 0;
		int numParents = 0;
		double clustringMod = 0.0;
		double Pij = 0.0;
		for (int i=0; i<nNodes; i++)
		{
			nodes.add((Node)nodeClass.newInstance());
		}

		for (int s=0; s<steps; s++)
		{
			//pick a node i at random
			Node iNode = (Node)nodes.get(Random.uniform.nextIntFromTo(0, nNodes-1));
			//pick a node j (j != i) at random
			Node jNode = iNode;
			while (jNode.equals(iNode))
			{
				jNode = (Node)nodes.get(Random.uniform.nextIntFromTo(0, nNodes-1));
			}
			//find # of nodes with connections to both i and j
			numParents = NetUtilities.getNumDirectTriads(iNode, jNode);

			//find if j -> i tie exits
			jiTie = NetUtilities.getIJTie(jNode, iNode);
			//modify clustring
			if(numParents <= 2)
			{
				clustringMod = -0.1;
			}
			else
			{
				 clustringMod = 0.1;
			}
			//evaluate degegree "capper"
			double degreeCap = (1/(Math.pow(Math.E,((NetUtilities.getOutDegree(iNode)-5)))+1));
			//decide if i -> j tie will exist from prob calc
			Pij = (1-(1-density)*Math.pow(Math.E,(-(clustring+clustringMod)*numParents -reciProb*jiTie)))*degreeCap+(.5*NetUtilities.getIJTie(iNode,jNode));

			if (Random.uniform.nextDoubleFromTo(0,1) < Pij)
			{
				//only make new  i -> j tie if none exists
				if (NetUtilities.getIJTie(iNode,jNode)<1)
				{
					Edge edge = (Edge)edgeClass.newInstance();
					edge.setFrom(iNode);
					edge.setTo(jNode);
					iNode.addOutEdge(edge);
					jNode.addInEdge(edge);
				}
				//make j -> i tie
				if (NetUtilities.getIJTie(iNode,jNode)<1)
				{
					Edge edge = (Edge)edgeClass.newInstance();
					edge.setFrom(jNode);
					edge.setTo(iNode);
					jNode.addOutEdge(edge);
					iNode.addInEdge(edge);
				}
			}
			else
			{
				//if i -> j tie exists, remove it
				Iterator arcIter = ((ArrayList)iNode.getOutEdges()).iterator();
				while (arcIter.hasNext())
				{
					Edge arc = (Edge)arcIter.next();
					if (((Node)arc.getTo()).equals(jNode))
					{
						iNode.removeOutEdge(arc);
						jNode.removeInEdge(arc);
						break;
					}
				}
				//remove j -> i tie
				arcIter = ((ArrayList)jNode.getOutEdges()).iterator();
				while (arcIter.hasNext())
				{
					Edge arc = (Edge)arcIter.next();
					if (((Node)arc.getTo()).equals(iNode))
					{
						jNode.removeOutEdge(arc);
						iNode.removeInEdge(arc);
						break;
					}
				}
			}

			if ((s%10000)==0)
			{
				System.out.println("Step="+s+" clust="+NetUtilities.calcClustCoef(nodes)+" Dens="+NetUtilities.calcDensity(nodes));
			}
		}
		return nodes;
	}

	public ArrayList createRolfeNet(Class node, Class edge, int size, double dens, double clust, double reci, int step)
			throws IllegalAccessException, InstantiationException
	{
		nodeClass = node;
		edgeClass = edge;
		nNodes = size;
		density = dens;
		clustring = clust;
		reciProb = reci;
		steps = step;
		ArrayList nodeList = createRolfeNet();
		return nodeList;
	}
}
