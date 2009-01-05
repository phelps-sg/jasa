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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import uchicago.src.sim.util.SimUtilities;

/**
 * Utility class to convert one kind of network representation to another.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class NetworkConvertor {

  private NetworkConvertor() {}

  /**
   * Converts the network(s) described by a list Nodes to a Vector of
   * AdjacencyMatrices.
   *
   * @deprecated use <code>nodesToMatrices(List nodeList, int matrixType)</code>
   * instead.
   * @return a Vector of AdjacencyMatrices
   * @see AdjacencyMatrix
   */

  public static Vector nodesToMatrices(List nodeList) {
    return nodesToMatrices(nodeList, NetworkConstants.LARGE);
  }

  /**
   * Converts the network(s) described by a list Nodes to a Vector of
   * AdjacencyMatrices.<p>
   *
   * The matrixType parameter refers to the size of the matrix elements, the
   * ij values. A matrix of type of NetworkConstants.BINARY contains on 0 or 1
   * as elements. A matrix of type of NetworkConstants.SMALL contains values
   * of -127 - 127 as elements. And a matrix of type NetworkConstants.LARGE
   * contains anything else. Matrix ij values are assigned the edge strength
   * of an edge, so unless you have explicitly set an edge strength of greater
   * than 1, use NetworkConstants.BINARY.
   *
   * @param nodeList the list of nodes that is the source for the matrix
   * @param matrixType the type of the matrix. type refers to the size of the
   * matrix elements (ij values) and can be one of NetworkConstants.BINARY,
   * NetworkConstants.LARGE, NetworkConstants.SMALL
   * @return a Vector of AdjacencyMatrices
   * @see AdjacencyMatrix
   */

  public static Vector nodesToMatrices(List nodeList, int matrixType) {
    Hashtable nodesToRow = new Hashtable(nodeList.size());
    Vector labels =  new Vector(nodeList.size());
    Hashtable matrixTypes = new Hashtable();

    for (int i = 0; i < nodeList.size(); i++) {
      Node node = (Node)nodeList.get(i);
      nodesToRow.put(node, new Integer(i));
      labels.add(node.getNodeLabel());
    }

    for (int i = 0; i < nodeList.size(); i++) {
      Node node = (Node)nodeList.get(i);
      int row = ((Integer)nodesToRow.get(node)).intValue();
      ArrayList edges = node.getOutEdges();

      for (int j = 0; j < edges.size(); j++) {
        Edge edge = (Edge)edges.get(j);
        Node toNode = edge.getTo();
        int col = ((Integer)nodesToRow.get(toNode)).intValue();
        String type = edge.getType();

        AdjacencyMatrix matrix = (AdjacencyMatrix)matrixTypes.get(type);
        if (matrix == null) {
          matrix = AdjacencyMatrixFactory.createAdjacencyMatrix(labels, matrixType);
          matrix.setMatrixLabel(type);
          matrixTypes.put(type, matrix);
        }

        matrix.set(row, col, edge.getStrength());
      }
    }

    Vector v = new Vector(matrixTypes.size());

    Enumeration e = matrixTypes.elements();
    while (e.hasMoreElements()) {
      v.add(e.nextElement());
    }

    return v;
  }

  /**
   * Converts the network(s) described in matrices to a list of nodes and links
   * of type nodeClass and type edgeClass. matrices must contain
   * AdjacencyMatrices that describe the same nodes, that is, each
   * AdjacencyMatrix must contain the same list of node labels, in the same
   * order.
   *
   * @param matrices a list of AdjacencyMatrices
   * @param nodeClass specifies the type of Nodes to create
   * @param edgeClass specifies the type of Edge to create
   *
   * @return a list of nodes of the type specified by nodeClass. These nodes
   * will contain edges of type edgeClass
   */
  public static Vector matricesToNodes(Vector matrices, Class nodeClass,
    Class edgeClass)
  {
    ConvertorUtil util = new ConvertorUtil();
    if (!util.labelCheck(matrices)) {
      throw new IllegalArgumentException("Matrices describe different nodes");
    }

    AdjacencyMatrix matrix = (AdjacencyMatrix)matrices.get(0);

    Vector vNodes = null;

    try {
      PairHash ph = util.makeNodes(matrix, nodeClass);
      Hashtable nodes = util.makeEdges(ph.rowsToNodes, matrices, edgeClass);

      vNodes = new Vector(nodes.size());

      if (matrix.getLabels().size() > 0) {
        List labels = matrix.getLabels();
        for (int i = 0; i < labels.size(); i++) {
          Integer row = (Integer)ph.rowsToLabel.get(labels.get(i));
          //Node n = (Node)nodes.get(row);
          //System.out.println(n.getNodeLabel() + ": " + n.getOutEdges().size());
          vNodes.add(nodes.get(row));
        }
      } else {
        for (int i = 0; i < nodes.size(); i++) {
          vNodes.add(nodes.get(new Integer(i)));
        }
      }
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Error converting matrix to nodes", ex);
      System.exit(0);
    } catch (InstantiationException ex) {
      SimUtilities.showError("Error converting matrix to nodes", ex);
      System.exit(0);
    }


    return vNodes;
  }
}

class PairHash {
  Hashtable rowsToNodes;
  Hashtable rowsToLabel;

  public PairHash(Hashtable rowsToNodes, Hashtable rowsToLabel) {
    this.rowsToNodes = rowsToNodes;
    this.rowsToLabel = rowsToLabel;
  }
}


class ConvertorUtil {

    public Hashtable makeEdges(Hashtable nodes, Vector matrices, Class edgeClass)
        throws IllegalAccessException, InstantiationException
    {
      for (int k = 0; k < matrices.size(); k++) {
        AdjacencyMatrix matrix = (AdjacencyMatrix)matrices.get(k);

        String type = matrix.getMatrixLabel();

        for (int i = 0; i < nodes.size(); i++) {
          Node source = (Node)nodes.get(new Integer(i));

          for (int j = 0; j < matrix.rows(); j++) {
            double val = matrix.get(i, j);
            if (val != 0) {

              Node dest = (Node)nodes.get(new Integer(j));
              Edge edge = (Edge)edgeClass.newInstance();
              edge.setType(type);
              edge.setFrom(source);
              edge.setTo(dest);
              edge.setStrength(val);
              source.addOutEdge(edge);
              dest.addInEdge(edge);
            }
          }
        }
      }

      return nodes;
    }


    public PairHash makeNodes(AdjacencyMatrix matrix, Class nodeClass)
      throws IllegalAccessException, InstantiationException //, ClassNotFoundException
    {
      List labels = matrix.getLabels();
      Hashtable rowsToNodes = new Hashtable(matrix.rows());
      Hashtable rowsToLabel = new Hashtable(matrix.rows());


      boolean useLabels = false;

      if (labels.size() != 0) {
        if (labels.size() != matrix.rows()) {
          throw new IllegalArgumentException("Number of matrix labels not equal to number of matrix rows");
        } else {
          useLabels = true;
        }
      }

      for (int i = 0; i < matrix.rows(); i++) {
        Node node = (Node)nodeClass.newInstance();
        Integer iVal = new Integer(i);
        rowsToNodes.put(iVal, node);
        if (useLabels) {
          node.setNodeLabel((String)labels.get(i));
          rowsToLabel.put(node.getNodeLabel(), iVal);
        }
      }

      return new PairHash(rowsToNodes, rowsToLabel);
    }


    public boolean labelCheck(Vector matrices) {
      if (matrices.size() > 1) {
        List labels = ((AdjacencyMatrix)matrices.get(0)).getLabels();
        for (int i = 1; i < matrices.size(); i++) {
          List otherLabels = ((AdjacencyMatrix)matrices.get(i)).getLabels();
          if (otherLabels.size() != labels.size()) {
            return false;
          }

          for (int j = 0; j < labels.size(); j++) {
            String label1 = (String)labels.get(j);
            String label2 = (String)otherLabels.get(j);
            if (!label1.equals(label2)) {
               return false;
            }
          }
        }
      }

      return true;
	}
}



