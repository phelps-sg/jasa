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
public class AdjacencyDoubleMatrix extends AbstractAdjacencyMatrix {

  private DenseDoubleMatrix2D matrix;

  /**
   * Constructs an AdjacencyMatrix with the specified number of rows
   * and columns. The intial state of every ij is 0.
   *
   * @param rows the number of rows in the matrix
   * @param cols the number of cols in the matrix
   */
  public AdjacencyDoubleMatrix(int rows, int cols) {
    matrix = new DenseDoubleMatrix2D(rows, cols);
  }

  /**
   * Constructs an AdjacencyMatrix with the specified row/col labels. The
   * matrix row and column size are set equal to the size of the lables vector.
   *
   * @param labels the row & column labels
   */
  public AdjacencyDoubleMatrix(List labels) {
    matrix = new DenseDoubleMatrix2D(labels.size(), labels.size());
    this.labels = labels;
  }

  /**
   * Constructs an AdjacencyMatrix from the specified DenseDoubleMatrix2D
   *
   * @param m the DenseDoubleMatrix2D to construct this AdjacencyMatrix from
   */

  public AdjacencyDoubleMatrix (DenseDoubleMatrix2D m) {
    matrix = m;
  }

  /**
   * Constructs an AdjacencyMatrix from the specified two dimensional double
   * array.
   *
   * @param m the 2D double array to construct this AdjacencyMatrix from.
   */
  public AdjacencyDoubleMatrix(double[][] m){
    matrix = new DenseDoubleMatrix2D(m);
  }


  /**
   * Sets the actual matrix for this AdjacencyMatrix.
   *
   * @param m the actual matrix data for this AdjacencyMatrix
   */
  public void setMatrix(DenseDoubleMatrix2D m) {
    matrix = m;
  }

  /**
   * Sets the actual matrix for this AdjacencyMatrix.
   *
   * @param m the actual matrix data for this AdjacencyMatrix
   */
  public void setMatrix(double[][] m) {
    matrix = new DenseDoubleMatrix2D(m);
  }

  /**
   * Gets the specified row of data for this matrix.
   *
   * @param row the index of the row to get
   */
  public DenseDoubleMatrix1D getRow(int row) {
    return (DenseDoubleMatrix1D)matrix.viewRow(row);
  }

  /**
   * Gets (computes) the density of this matrix.
   */
  public double getDensity() {
    double sum = matrix.zSum();
    return sum / matrix.rows() * (matrix.rows() - 1);
  }

  /**
   * Gets the average degree of this matrix.
   */
  public double getAvgDegree() {
    double rowSums = 0;
    for (int i = 0; i < matrix.rows(); i++) {
      for (int j = 0; j < matrix.columns(); j++) {
        rowSums += matrix.getQuick(i, j);
      }
    }

    return rowSums / matrix.rows();
  }

  /**
   * Sets a data value in this matrix.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @para val the value to set ij to.
   */
  public void set(int row, int col, double val) {
    matrix.setQuick(row, col, val);
  }

  /**
   * Gets the value at row, col.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @return the double value at row, col (ij)
   */
  public double get(int row, int col) {
    return matrix.getQuick(row, col);
  }

  /**
   * Returns the number of rows int matrix.
   */
  public int rows() {
    return matrix.rows();
  }

  /**
   * Returns the number of columns in the matrix.
   */
  public int columns() {
    return matrix.columns();
  }

  /**
   * Returns a String representation of only the actual data matrix.
   */
  public String matrixToString() {
    String m = matrix.toString();
    int index = m.indexOf("\n");
    return m.substring(index + 1, m.length());
  }

  /**
   * Returns a String representation of this AdjacencyMatrix (comment etc.)
   * together with the actual data matrix.
   */
  public String toString() {
    String s = "Matrix Name: " + this.matrixLabel + "\n";
    s += "Matrix Labels: ";
    for (int i = 0; i < labels.size(); i++) {
      if (i == 0) {
        s += (String)labels.get(i);
      } else {
        s += ", " + (String)labels.get(i);
      }
    }

    s += "\nComment: " + comment;


    return s + "\nAvg Degree: " + getAvgDegree() + "\n" + matrix.toString();
  }
}
