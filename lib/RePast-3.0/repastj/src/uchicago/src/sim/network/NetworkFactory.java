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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import uchicago.src.sim.util.SimUtilities;

/**
 * Creates a list of nodes with their links from various sources.
 *
 * Note: this class is not thread safe.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class NetworkFactory implements NetworkConstants {

  private NetworkFactory() {}

  /**
   * Creates a List of nodes (and their edges) from the specified file.
   * The returned Nodes will be of the type specified by nodeClass and
   * the edges of type edgeClass. The class nodeClass must implement
   * the Node interface and the class edgeClass much implement the
   * Edge interface. Supported formats are NetworkFactor.DL, and
   * NetworkFactor.EXCEL.<p>
   *
   * The matrix is assumed to be square for both the dl and Excel formats.
   * For Excel, each worksheet is treated as a matrix, and any worksheets
   * that do not contain matrices will cause an error. The worksheet name
   * is treated as the matrix label unless the name begins with Sheet
   * (Excel's generic worksheet name). The format for excel files is that
   * imported and exported by UCINet. The first cell is empty, and the
   * node labels begin on this first row in the second column. The column
   * node labels begin in first column on the second row. The actual data
   * begins in cell 2,2. For example,
   * <code><pre>
   *              | first_label | second_label | ...
   * -------------+-------------+--------------+----
   * first_label  | 0           | 1            | ...
   * -------------+-------------+--------------+----
   * second_label | 1           | 0            | ...
   * -------------+-------------+--------------+----
   * ...          | ...         | ...          | ...
   * </pre></code>
   *
   * If the matrix has no node labels, RePast will expect the first row and
   * column to be blank and as before, for the data to begin in cell 2,2.<p>
   *
   * The matrixType parameter refers to the size of the matrix elements, the
   * ij values. A matrix of type of NetworkConstants.BINARY contains on 0 or 1
   * as elements. A matrix of type of NetworkConstants.SMALL contains values
   * of -127 - 127 as elements. And a matrix of type NetworkConstants.LARGE
   * contains anything else.
   *
   *
   * @param fileName the name of the file
   * @param fileFormat the format of the file (NetworkFactory.DL
   * and NetworkFactory.EXCEL)
   * @param nodeClass specifies the class of the created nodes
   * @param edgeClass specified the class of the created edges
   * @param matrixType the type of the matrix. type refers to the size of the
   * matrix elements (ij values) and can be one of NetworkConstants.BINARY,
   * NetworkConstants.LARGE, NetworkConstants.SMALL
   */

  public static List getNetwork(String fileName, int fileFormat,
        Class nodeClass, Class edgeClass, int matrixType)
  {

    NetworkMatrixReader reader = null;
    if (fileFormat == NetworkConstants.DL) {
      reader = new DlReader(fileName);
    } else if (fileFormat == NetworkConstants.EXCEL) {
      reader = new ExcelMatrixReader(fileName);
    } else {
      throw new IllegalArgumentException("Unsupported file format");
    }

    Vector matrices = null;

    try {
      matrices = reader.getMatrices(matrixType);
    } catch (java.io.IOException ex) {
      SimUtilities.showError("Error creating network from file", ex);
      System.exit(0);
    }

    reader.close();

    return NetworkConvertor.matricesToNodes(matrices, nodeClass, edgeClass);
  }


   /**
   * Creates a List of nodes (and their edges) from the specified InputStream.
   * The returned Nodes will be of the type specified by nodeClass and
   * the edges of type edgeClass. The class nodeClass must implement
   * the Node interface and the class edgeClass much implement the
   * Edge interface. This assumes that the source of the stream is in DL
   * format. The matrix is assumed to be square.<p>
   *
   * The matrixType parameter refers to the size of the matrix elements, the
   * ij values. A matrix of type of NetworkConstants.BINARY contains on 0 or 1
   * as elements. A matrix of type of NetworkConstants.SMALL contains values
   * of -127 - 127 as elements. And a matrix of type NetworkConstants.LARGE
   * contains anything else.
   *
   *
   * @param stream the InputStream to read the data from.
   * @param nodeClass specifies the class of the created nodes
   * @param edgeClass specified the class of the created edges
   * @param matrixType the type of the matrix. type refers to the size of the
   * matrix elements (ij values) and can be one of NetworkConstants.BINARY,
   * NetworkConstants.LARGE, NetworkConstants.SMALL
   */
  public static List getDLNetworkFromStream(InputStream stream,
					    Class nodeClass, Class edgeClass,
					    int matrixType)
  {
     NetworkMatrixReader reader = new DlReader(stream);
     Vector matrices = null;

     try {
       matrices = reader.getMatrices(matrixType);
     } catch (java.io.IOException ex) {
       SimUtilities.showError("Error creating network from file", ex);
       System.exit(0);
     }

     reader.close();

     return NetworkConvertor.matricesToNodes(matrices, nodeClass, edgeClass);
  }


  /**
   * Creates a List of nodes (and their edges) from the specified file.
   * The returned Nodes will be of the type specified by nodeClass and
   * the edges of type edgeClass. The class nodeClass must implement
   * the Node interface and the class edgeClass much implement the
   * Edge interface. Supported formats are NetworkFactor.DL, and
   * NetworkFactor.EXCEL.<p>
   *
   * The matrix is assumed to be square for both the dl and Excel formats.
   * For Excel, each worksheet is treated as a matrix, and any worksheets
   * that do not contain matrices will cause an error. The worksheet name
   * is treated as the matrix label unless the name begins with Sheet
   * (Excel's generic worksheet name). The format for excel files is that
   * imported and exported by UCINet. The first cell is empty, and the
   * node labels begin on this first row in the second column. The column
   * node labels begin in first column on the second row. The actual data
   * begins in cell 2,2. For example,
   * <code><pre>
   *              | first_label | second_label | ...
   * -------------+-------------+--------------+----
   * first_label  | 0           | 1            | ...
   * -------------+-------------+--------------+----
   * second_label | 1           | 0            | ...
   * -------------+-------------+--------------+----
   * ...          | ...         | ...          | ...
   * </pre></code>
   *
   * If the matrix has no node labels, RePast will expect the first row and
   * column to be blank and as before, for the data to begin in cell 2,2.<p>
   *
   * @param fileName the name of the file
   * @param fileFormat the format of the file (NetworkFactory.DL
   * and NetworkFactory.EXCEL)
   * @param nodeClass specifies the class of the created nodes
   * @param edgeClass specified the class of the created edges
   * @deprecated use <code>NetworkFactory(String, int, Class, Class, int)
   * </code>
   * instead.
   */

  public static List getNetwork(String fileName, int fileFormat,
        Class nodeClass, Class edgeClass)
  {
    return NetworkFactory.getNetwork(fileName, fileFormat, nodeClass,
				     edgeClass, NetworkConstants.LARGE);
  }

  /**
   * Creates a list containing the specified number of nodes of the specified
   * type. The nodes are unlinked.
   */
  public static List createUnlinkedNetwork(int numNodes, Class nodeClass) {
    List l = new ArrayList();
    try {
      for (int i = 0; i < numNodes; i++) {
	Node node = (Node)nodeClass.newInstance();
	l.add(node);
      }
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Error creating node from " + nodeClass.getName(),
			     ex);
      System.exit(0);
    } catch (InstantiationException ex) {
      SimUtilities.showError("Error creating node from " + nodeClass.getName(),
			     ex);
      System.exit(0);
    }

    return l;
  }

  /**
   * Creates a square lattice network. MORE DOCS NEEDED HERE!!!
   *
   * @param cols the number of columns in the network
   * @param rows the number of rows in the network
   * @param wrapAround whether the links should wrap around the lattice
   * @param connectRadius the connect radius of the network
   * @param node the class to create the network nodes from
   * @param edge the class to create the network edges from
   */
  public static List createSquareLatticeNetwork(int cols, int rows, boolean
						wrapAround, int connectRadius,
						Class node, Class edge)
  {
    SquareLatticeNet net = new SquareLatticeNet(node, edge, cols, rows,
						wrapAround, connectRadius);
    List l = null;
    try {
      l = net.createSquareLatticeNet();
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Error creating SquareLatticeNet", ex);
      System.exit(0);
    } catch (InstantiationException ex) {
      SimUtilities.showError("Error creating SquareLatticeNet", ex);
      System.exit(0);
    }

    return l;
  }


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
   *
   * @param size the size of the network (number of nodes)
   * @param allowLoops whether self loops are allowed
   * @param isSymmetric whether the resulting network is to be symmetric
   * @param nodeClass the class for the network nodes
   * @param edgeClass the class for the network edges
   */
  public static List createRandomDensityNetwork(int size, double density,
						boolean allowLoops,
						boolean isSymmetric,
						Class nodeClass,
						Class edgeClass)
  {

    RandomDensityNet net = new RandomDensityNet(nodeClass, edgeClass, size,
						density, allowLoops,
						isSymmetric);
    List l = null;

    try {
      l = net.createRandomDensityNet();
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Error creating RandomDensityNet", ex);
      System.exit(0);
    } catch (InstantiationException ex) {
      SimUtilities.showError("Error creating RandomDensityNet", ex);
      System.exit(0);
    }

    return l;
  }


  /**
   * Creates a classic W-S network small world network (ring substrate).
   * <p>
   *
   * Note that when connectRadius is > 1 there can be funny effects if the
   * number of nodes is not a multiple of the radius. If it doesn't divide
   * evenly, this leaves out the extra links.
   *
   * @param size the size (number of nodes) in the network
   * @param connectRadius the connect radius of the network.
   * @param rewireProb the edge rewire probability
   * @param node the class to create the nodes from
   * @param edge the class to create the edges from
   */
  public static List createWattsStrogatzNetwork(int size, int connectRadius,
						double rewireProb,
						Class node, Class edge) {
    ArrayList list = new ArrayList();
    list.addAll(createSquareLatticeNetwork(size, 1, true,
					   connectRadius, node, edge));
    list = new ArrayList(NetUtilities.randomRewireSymmetric(list, rewireProb));
    return list;
  }


  /*
   * REMOVED FROM RELEASE AS IT DOESN'T WORK CORRECTLY.
   *
   * BETA AND DOESN'T SEEM TO WORK TOO WELL.
   *
   * Creates a random network from the specific parameters. The network is
   * created by assigning a degree to each node. This degree is a random number
   * drawn from a Normal distribution whose mean is specified by avgDegree and
   * whose variance is specified by variance. A new random number is drawn
   * for each node. Some number of nodes, that is rows in matrix speak, are
   * randomly seeded such that a number of links are created for that node
   * equal to the specific degree for that nodes.<p>
   *
   * The created network tries to approproximate a realistic network using the
   * edgeProbability array. The first member of this array specifies the
   * probability that if A -> B, then B -> A. This is ignored if the network
   * is to be symmetrical (i.e. isDigraph is false). The remaining members of
   * this array specify the probabilities that a one node will link to another
   * node a walk of length n away, where n is the nth member of the array.
   * So for example, if the 2nd member (index 1) of the array is .2, then
   * nodes have a .2 probability of forming a link with another node a walk
   * of length 2 way. In order to take into account that a node is more likely
   * to link with another node if it has multiple walks of length n to that node
   * and if it has walks of other length to that node, the probilities are
   * multiplied and added in the following manner:
   *
   * <code><pre>
   * if
   * (i_intended_vec_sum / avgDensity) * (((matrix_size / avgDensity) *
   * (1 - Sum(probs[n]))) + (probs[0] * matrix^2_ij) +
   * (probs[1] * powArray^3_ij) + ... + (probs[n] * powArray^n_ij))
   * >= uniform_float_from_1_to_0
   * then
   * there is a link
   *
   *
   * where:
   *      i_intended_vec_sum = the intended row sum as determined in step 1.
   *      avgDensity = the user specified mean degree
   *      probs[] = an array of probilities that specify the likelyhood of
   *      one node having a link to another. This array is indexed to the
   *      various matrix powers such that the first probility refers to the
   *      likelyhood of one node having a link to another if that node is
   *      a walk of length 2 away, and so on for the other powers.
   * </pre></code>
   *
   * For more information see the docs to the DegNetGenerator class.<p>
   *
   * <b>Note</b>: this is still in beta and should only be used to test the
   * characteristics of the randomly generated network. If you are happy
   * with the characteristics then use it.
   *
   * @nodeClass specifies the class of Node to create
   * @edgeClass specifies the class of Edge to create
   * @return a list of nodes of the type specified by nodeClass
   *
   * @see DegNetGenerator
   *

  public static List createRandomNetwork(int avgDegree, float variance,
					 int size,
      int numRowsToSeed, float[] edgeProbability, boolean isDigraph,
      Class nodeClass, Class edgeClass)
  {
    System.out.println("!!!! RANDOM NETWORK GENERATION IS STILL IN BETA !!!!!!\n" +
                      "!!!!! USE WITH CARE !!!!!!");
    DegNetGenerator gen = new DegNetGenerator(avgDegree, variance, size);
    AdjacencyMatrix m = gen.getMatrix(numRowsToSeed, edgeProbability, isDigraph);

    Vector v = new Vector();
    v.add(m);

    return NetworkConvertor.matricesToNodes(v, nodeClass, edgeClass);
  }
  */
}
