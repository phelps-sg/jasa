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

import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;


/**
 * Creates a RandomDensityNetwork. The network will have a density.
 * (ratio of # of existing edges to the maximum possible # of edges)
 * approximately equal to a specified double density.  The network is
 * created by looping over all i, j node pairs and deciding on the existence
 * of a link between the nodes by comparing the value of density to a uniform
 * random number.  If the boolean allowLoops is false, no self loops
 * (links from i to itself) will be permitted.  If the boolean isSymmetric is
 * true, all ties will be bidirectional (i -> j = j -> i). This is what is
 * generally referred to in the network literature as "random" network -
 * a class of networks which have been well studied analytically, but which
 * are structurally quite unlike most empirically observed "social" networks.
 * <p>
 *
 * Unless there are compelling reasons to do otherwise, use the appropriate
 * method in NetworkFactory to create a RandomDensityNetwork. 
 *
 * @author Skye Bender-deMoll
 * @version $Revision$ $Date$
 */
public class RandomDensityNet
{
  Class nodeClass;
  Class edgeClass;
  int nNodes = -1;
  double density = -1;
  boolean loops = false;
  boolean symmetric = false;

  //constructors
  public RandomDensityNet (){}
  public RandomDensityNet (Class node, Class edge)
  {
    nodeClass = node;
    edgeClass = edge;
  }
  public RandomDensityNet (Class node, Class edge, int size)
  {
    nodeClass = node;
    edgeClass = edge;
    nNodes = size;
  }
  public RandomDensityNet (Class node, Class edge, int size, double dens,
			   boolean allowLoops, boolean isSymmetric)
  {
    nodeClass = node;
    edgeClass = edge;
    nNodes = size;
    density = dens;
    loops = allowLoops;
    symmetric = isSymmetric;
  }

  /**
   * Returns the Class of nodes to be used in constructing the network.
   * Must be set before makeRandomDensityNet() is called.
   */
  public Class getNodeClass()
  {
    return nodeClass;
  }
  
  /**
   * Sets the Class of nodes to be used in constructing the network.
   * Must be set before makeRandomDensityNet() is called.
   */
  public void setNodeClass(Class node)
  {
    nodeClass = node;
  }
  
  /**
   * Returns the Class of edges to be used in constructing the network.
   * Must be set before makeRandomDensityNet() is called.
   */
  public Class getEdgeClass()
  {
    return edgeClass;
  }
  
  /**
   * Sets the Class of edges to be used in constructing the network.
   * Must be set before makeRandomDensityNet() is called.
   */
  public void setEdgeClass(Class edge)
  {
    edgeClass = edge;
  }
  
  /**
   * Returns the int for the size (number of nodes) in the network to
   * be constructed. Must be set before makeRandomDensityNet() is called.
   */
  public int getSize()
  {
    return nNodes;
  }
  
  /**
   * Sets the int for the size (number of nodes) in the network to be
   * constructed. Must be set before makeRandomDensityNet() is called.
   */
  public void setSize(int size)
  {
    nNodes = size;
  }
  
  /**
   * Returns the double of the desired density of the network
   * (ratio of number of existing edges to the maximum possible number of
   * edges)  Must be set before makeRandomDensityNet() is called.
   */
  public double getDensity()
  {
    return density;
  }
  
  /**
   * Sets the double of the desired density of the network (ratio of
   * number of existing edges to the maximum possible number of edges).
   * Must be set before makeRandomDensityNet() is called.
   */
  public void setDensity(double dens)
  {
    density = dens;
  }
  
  /**
   * Returns true if self-loops will be permitted in the constructed
   * network.  (Self-loops are edges from i to i)
   */
  public boolean isAllowLoops()
  {
    return loops;
  }
  
  /**
   * Sets the whether self-loops will be permitted in the constructed
   * network.  (Self-loops are edges from i to i)
   */
  public void setAllowLoops(boolean allow)
  {
    loops = allow;
  }

  /**
   * Returns true if the constructed network will be symmetric.
   * (in a symmetric network, all ties i -> j = j -> i)
   */
  public boolean isSymmetric()
  {
    return symmetric;
  }
  
  /**
   * Sets whether the constructed network will be symmetric.
   * (in a symmetric network, all ties i -> j = j -> i)
   */
  public void setSymmetric(boolean sym)
  {
    symmetric= sym;
  }


  /**
   * Returns an ArrayList network of size nodes of class nodeClass, connected
   * with edges of class edgeClass.  The network will have a density.
   * (ratio of # of existing edges to the maximum possible # of edges)
   * approximately equal to the  double density.  The network is created by
   * looping over all i, j node pairs and deciding on the existence of a link
   * between the nodes by comparing the value of density to a uniform random
   * number.  If the boolean allowLoops is false, no self loops (links from i
   * to itself) will be permitted.  If the boolean isSymmetric is true,
   * all ties will be bidirectional (i -> j = j -> i). This is what is
   * generally referred to in the network literature as "random" network -
   * a class of networks which have been well studied analytically, but which
   * are structurally quite unlike most empirically observed "social" networks.
   * <p>
   * 
   * Because of the node and edge class instantiation, the makeNet() methods
   * may throw an IllegalAccessException and/or InstantiationException and must
   * be called from wit hen a try/ catch block, or remotely via the
   * NetworkFactory class.
   *
   * Please use the appropriate method in NetworkFactory instead of this.
   *
   * @throws IllegalAccessException, InstantiationException
   */
  public ArrayList createRandomDensityNet()
    throws IllegalAccessException, InstantiationException
  {
    // check parameter ranges
    if((nodeClass == null) || (edgeClass == null))
      {
	String error = "Unable to construct randomDensityNetwork: nodeClass or edgeClass was not set.";
	RepastException exception = new RepastException(error);
	SimUtilities.showError(error, exception);
      }
    if((nNodes < 0) || (density < 0) || (density > 1))
      {
	String error = "Unable to construct randomDensityNetwork: parameter out of range";
	RepastException exception = new RepastException(error);
	SimUtilities.showError(error, exception);
      }
    ArrayList nodeList = new ArrayList(nNodes);
    //construct nodes
    for (int i = 0; i < nNodes; i++)
      {
	nodeList.add((Node)nodeClass.newInstance());
      }

    //if net is symmetric and self-loops are off, loop over all node pairs except i -> i
    if (symmetric && !loops)
      {
	for (int i = 0; i < nNodes; i++)
	  {
	    Node iNode = (Node)nodeList.get(i);
	    for (int j = i+1; j < nNodes; j++)
	      {
		Node jNode = (Node)nodeList.get(j);
		if (Random.uniform.nextDoubleFromTo(0,1) < density)
		  {
		    //make both i -> j and j -> i edges
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    Edge otherEdge = (Edge)edgeClass.newInstance();
		    otherEdge.setFrom(jNode);
		    otherEdge.setTo(iNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		    jNode.addOutEdge(otherEdge);
		    iNode.addInEdge(otherEdge);
		  }
	      }
	  }
      }
    //if net is symmetric and self-loops are on, loop over all node pairs
    else if (symmetric && loops)
      {
	for (int i = 0; i < nNodes; i++)
	  {
	    Node iNode = (Node)nodeList.get(i);
	    for (int j = 0; j < nNodes; j++)
	      {
		Node jNode = (Node)nodeList.get(j);
		if (Random.uniform.nextDoubleFromTo(0,1) < density)
		  {
		    //make both i -> j and j -> i edges
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    Edge otherEdge = (Edge)edgeClass.newInstance();
		    otherEdge.setFrom(jNode);
		    otherEdge.setTo(iNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		    jNode.addOutEdge(otherEdge);
		    iNode.addInEdge(otherEdge);
		  }
	      }
	  }
      }
    // net can be asymmetric, but loops are ok, so loop over all i and j
    else if (!symmetric && loops)
      {
	for (int i = 0; i < nNodes; i++)
	  {
	    Node iNode = (Node)nodeList.get(i);
	    for (int j = 0; j < nNodes; j++)
	      {
		Node jNode = (Node)nodeList.get(j);
		if (Random.uniform.nextDoubleFromTo(0,1) < density)
		  {
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		  }
	      }
	  }
      }
    // net can be asymmetric, but loops are Not ok, so loop over all i and j, (i != j)
    else
      {
	for (int i = 0; i < nNodes; i++)
	  {
	    Node iNode = (Node)nodeList.get(i);
	    for (int j = 0; j < nNodes; j++)
	      {
		if (i != j)
		  {
		    Node jNode = (Node)nodeList.get(j);
		    if (Random.uniform.nextDoubleFromTo(0,1) < density)
		      {
			Edge edge = (Edge)edgeClass.newInstance();
			edge.setFrom(iNode);
			edge.setTo(jNode);
			iNode.addOutEdge(edge);
			jNode.addInEdge(edge);

		      }
		  }
	      }
	  }
      }
    return nodeList;
  }
  /**
   * Sets the classes and parameters to the passed values and then calls
   * createRandomDensityNet().<p>
   *
   * Please use the appropriate method in NetworkFactory instead of this.
   *
   * @throws IllegalAccessException, InstantiationException
   */
  public ArrayList createRandomDensityNet(Class node, Class edge, int size,
					  double dens,boolean allowLoops,
					  boolean isSymmetric)
    throws IllegalAccessException, InstantiationException
  {
    nodeClass = node;
    edgeClass = edge;
    nNodes = size;
    density = dens;
    loops = allowLoops;
    symmetric = isSymmetric;
    ArrayList nodeList = createRandomDensityNet();
    return nodeList;
  }
}
