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

import java.util.List;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * A social network adjacency matrix. This is used as an itermediary data
 * structure when moving between <code>Nodes</code> and <code>Edges</code>
 * and other kinds of network representations. The matrix is assumed to
 * be square and that the rows and columns refer to the same nodes.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.network.Node
 * @see uchicago.src.sim.network.Edge
 */
public interface AdjacencyMatrix {


  /**
   * Sets the label for this matrix. This is used to indicated the type
   * of matrix (i.e. kinship etc.).
   *
   * @param mLabel the label for this matrix
   */
  public void setMatrixLabel(String mLabel);

  /**
   * Gets the label for this matrix. This is used to indicated the type
   * of matrix (i.e. kinship etc.).
   */
  public String getMatrixLabel();


  /**
   * Sets the actual matrix for this AdjacencyMatrix.
   *
   * @param m the actual matrix data for this AdjacencyMatrix
   */
  public void setMatrix(DenseDoubleMatrix2D m);

  /*
   * Sets the actual matrix for this AdjacencyMatrix.
   *
   * @param m the actual matrix data for this AdjacencyMatrix
   *
  public void setMatrix(double[][] m);
  */

  /**
   * Associates a comment with this matrix (e.g. the tick count at which
   * it was created.)
   *
   * @param comment the comment
   */
  public void setComment(String comment);

  /**
   * Gets the comment, if any, associated with this matrix.
   */
  public String getComment();

  /**
   * Gets the node labels for this matrix.
   */
  public List getLabels();

  /**
   * Gets the specified row of data for this matrix.
   *
   * @param row the index of the row to get
   */
  public DenseDoubleMatrix1D getRow(int row);

  /**
   * Gets (computes) the density of this matrix.
   */
  public double getDensity();

  /**
   * Gets the average degree of this matrix.
   */
  public double getAvgDegree();

  /**
   * Sets a data value in this matrix.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @para val the value to set ij to.
   */
  public void set(int row, int col, double val);

  /**
   * Gets the value at row, col.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @return the double value at row, col (ij)
   */
  public double get(int row, int col);

  /**
   * Returns the number of rows int matrix.
   */
  public int rows();

  /**
   * Returns the number of columns in the matrix.
   */
  public int columns();

  /**
   * Returns a String representation of only the actual data matrix.
   */
  public String matrixToString();

}