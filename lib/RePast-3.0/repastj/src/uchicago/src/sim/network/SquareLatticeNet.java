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

import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;


/**
 * Creates a square lattice network.
 * Use NetworkFactory.createSquareLatticeNetwork rather than this class.
 *
 * DOCUMENTATION NOT COMPLETE!
 *
 * @author Skye Bender-deMoll
 * @version $Revision$ $Date$
 */
public class SquareLatticeNet
{
  Class nodeClass;
  Class edgeClass;
  int nCols = -1;
  int nRows = -1;
  int nNodes;     //nNodes will be nCols * nRows
  int radius = -1;
  boolean torus = true;

  //constructors
  public SquareLatticeNet (){}
  public SquareLatticeNet (Class node, Class edge)
  {
    nodeClass = node;
    edgeClass = edge;
  }
  public SquareLatticeNet (Class node, Class edge, int cols, int rows)
  {
    nodeClass = node;
    edgeClass = edge;
    nCols = cols;
    nRows = rows;
    nNodes = nCols * nRows;
  }
  public SquareLatticeNet (Class node, Class edge, int cols, int rows,
			   boolean wrapAround, int connectRadius)
  {
    nodeClass = node;
    edgeClass = edge;
    nCols = cols;
    nRows = rows;
    nNodes = nCols * nRows;
    torus = wrapAround;
    radius = connectRadius;
  }

  
  /**
   *Returns the Class of nodes to be used in constructing the network.
   * Must be set before makeSquareLatticeNet() is called.
   */
  public Class getNodeClass()
  {
    return nodeClass;
  }
  
  /**
   * Sets the Class of nodes to be used in constructing the network
   * Must be set beforemakeSquareLatticeNet() is called.
   */
  public void setNodeClass(Class node)
  {
    nodeClass = node;
  }
  
  /**
   * Returns the Class of edges to be used in constructing the network.
   * Must be set before makeSquareLatticeNet() is called.
   */
  public Class getEdgeClass()
  {
    return edgeClass;
  }
  
  /**
   * Sets the Class of edges to be used in constructing the network.
   * Must be set before makeSquareLatticeNet() is called.
   */
  public void setEdgeClass(Class edge)
  {
    edgeClass = edge;
  }

  public int getNumCols()
  {
    return nCols;
  }
  
  public int getNumRows()
  {
    return nRows;
  }
  
  public void setDimension(int cols, int rows)
  {
    nCols = cols;
    nRows = rows;
    nNodes = nCols * nRows;
  }

  /**
   * Returns the int for the size (number of nodes) in the network to be
   * constructed.  Must be set before makeSquareLatticeNet() is called.
   **/
  public int getSize()
  {
    return nNodes; //== nCols * nRows
  }

  public int getConnectRadius()
  {
    return radius;
  }
  
  /**
   * Sets the "radius" of neighbors along the grid which additional
   * connections will be generated to.  In general, the average degree of
   * each node will be 2*radius, but if nCols or nRows are not multiples of
   * radius, there will be some nodes with lower degree.
   */
  public void setConnectRadius(int connectRadius)
  {
    radius = connectRadius;
  }

  public boolean isWrapAround()
  {
    return torus;
  }
  public void setWrapAround(boolean wrapAround)
  {
    torus = wrapAround;
  }

  /**
   * Returns true if the constructed network will always be symmetric.
   * (in a symmetric network, all ties i -> j = j -> i)
   */
  public boolean isSymmetric()
  {
    return true;
  }

  /**
   * * Use NetworkFactory.createSquareLatticeNetwork rather than this class.
   * !!!!!!! DOCS NEEDED !!!!!!!!
   */
  public ArrayList createSquareLatticeNet()
    throws IllegalAccessException, InstantiationException
  {
    // check parameter ranges
    if((nodeClass == null) || (edgeClass == null))
      {
	String error = "Unable to construct lattice: nodeClass or edgeClass was not set.";
	RepastException exception = new RepastException(error);
	SimUtilities.showError(error, exception);
      }
    if((nNodes < 0) || (nRows < 1) || (nCols < 1))
      {
	String error = "Unable to construct lattice: dimensions out of range";
	RepastException exception = new RepastException(error);
	SimUtilities.showError(error, exception);
      }
    ArrayList nodeList = new ArrayList(nNodes);
    //construct nodes
    for (int i = 0; i < nNodes; i++)
      {
	nodeList.add((Node)nodeClass.newInstance());
      }

    if (torus)    //construct a lattice which "wrapsAround"
      {
	for (int i = 0; i < nNodes; i++)
	  {
	    Node iNode = (Node)nodeList.get(i);
	    int x = i%nCols;          //col "grid" coords for index i
	    int y = (int)Math.floor(i/nCols);   //row
	    int j; //index of other node
	    int xJ;
	    int yJ;
	    for (int r = 1; r <= (radius); r++)
	      {
		//"east" connection ("west" will be set by previous/last node)
		xJ = (x+r)%nCols;
		yJ = y;
		j =yJ*nCols+xJ;
		if (i != j) //traps 1 by n "ring"
		  {
		    Node jNode = (Node)nodeList.get(j);
		    //i -> j
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		    //j -> 1
		    Edge otherEdge = (Edge)edgeClass.newInstance();
		    otherEdge.setFrom(jNode);
		    otherEdge.setTo(iNode);
		    jNode.addOutEdge(otherEdge);
		    iNode.addInEdge(otherEdge);
		  }

		//"south" connection ("north" will be set by previous/last node)
		xJ = x;
		yJ = (y+r)%nRows;
		j =yJ*nCols+xJ;
		if (i != j)  //traps 1 by n "ring"
		  {
		    Node jNode = (Node)nodeList.get(j);
		    //i -> j
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		    //j -> 1
		    Edge otherEdge = (Edge)edgeClass.newInstance();
		    otherEdge.setFrom(jNode);
		    otherEdge.setTo(iNode);
		    jNode.addOutEdge(otherEdge);
		    iNode.addInEdge(otherEdge);
		  }
	      }
	  }
      }
    else    //construct a lattice with edges
      {
	for (int i = 0; i < nNodes; i++)
	  {
	    Node iNode = (Node)nodeList.get(i);
	    int j; //index of other node
	    for (int r = 1; r <= (radius); r++)
	      {
		//"east" connection ("west" will be set by previous node)
		j = i+r;
		//if j < index of last node on row
		if ( j <= ((int)Math.floor(i/nCols)*nCols+nCols-1))
		  {
		    Node jNode = (Node)nodeList.get(j);
		    //i -> j
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		    //j -> 1
		    Edge otherEdge = (Edge)edgeClass.newInstance();
		    otherEdge.setFrom(jNode);
		    otherEdge.setTo(iNode);
		    jNode.addOutEdge(otherEdge);
		    iNode.addInEdge(otherEdge);
		  }

		//"south" connection ("north" will be set by previous node)
		j = i+r*nCols;
		//if j < index of last node on col
		if ( j <= (nCols*nRows-nCols+(i%nCols)))
		  {
		    Node jNode = (Node)nodeList.get(j);
		    //i -> j
		    Edge edge = (Edge)edgeClass.newInstance();
		    edge.setFrom(iNode);
		    edge.setTo(jNode);
		    iNode.addOutEdge(edge);
		    jNode.addInEdge(edge);
		    //j -> 1
		    Edge otherEdge = (Edge)edgeClass.newInstance();
		    otherEdge.setFrom(jNode);
		    otherEdge.setTo(iNode);
		    jNode.addOutEdge(otherEdge);
		    iNode.addInEdge(otherEdge);
		  }
	      }
	  }
      }
    return nodeList;
  }
  
  /**
   * !!! DOCS NEEDED !!!<p>
   *
   * USE NetworkFactory.createSquareLatticeNetwork rather than this.
   *
   *
   * @throws IllegalAccessException, InstantiationException
   */
  public ArrayList createSquareLatticeNet (Class node, Class edge, int cols,
					   int rows, boolean wrapAround,
					   int connectRadius)
    throws IllegalAccessException, InstantiationException
  {
    nodeClass = node;
    edgeClass = edge;
    nCols = cols;
    nRows = rows;
    nNodes = nCols * nRows;
    torus = wrapAround;
    radius = connectRadius;
    ArrayList nodeList = createSquareLatticeNet();
    return nodeList;
  }
}
