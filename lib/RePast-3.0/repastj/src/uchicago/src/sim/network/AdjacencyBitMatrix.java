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

/**
 * A social network adjacency matrix. This is used as an itermediary data
 * structure when moving between <code>Nodes</code> and <code>Edges</code>
 * and other kinds of network representations. The matrix is assumed to
 * be square and that the rows and columns refer to the same nodes.<p>
 *
 * This matrix stores its elements as bits, that is, as either 0 or 1. It
 * setting at matrix element to a value larger than 0, sets that element to
 * 1. If you need to store values between -127 - 127, use an
 * <code>AdjacencyByteMatrix</code>. For larger values use a <code>
 * AdjacencyDoubleMatrix</code>.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.network.Node
 * @see uchicago.src.sim.network.Edge
 */

import java.util.List;

import uchicago.src.collection.BitMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public class AdjacencyBitMatrix extends AbstractAdjacencyMatrix {

  private BitMatrix2D matrix;

  /**
   * Constructs an AdjacencyBitMatrix with the specified number of rows
   * and columns. The intial state of every ij is 0.
   *
   * @param rows the number of rows in the matrix
   * @param cols the number of cols in the matrix
   */
  public AdjacencyBitMatrix(int rows, int cols) {
    matrix = new BitMatrix2D(rows, cols);
  }

  /**
   * Constructs an AdjacencyBitMatrix with the specified row/col labels. The
   * matrix row and column size are set equal to the size of the lables vector.
   * This initial state of every ij is 0.
   *
   * @param labels the row & column labels
   */
  public AdjacencyBitMatrix(List labels) {
    matrix = new BitMatrix2D(labels.size(), labels.size());
    this.labels = labels;
  }


  /**
   * Constructs an AdjacencyBitMatrix from the specified two dimensional byte
   * array. The matrix element at ij is set to 0 if byte[i][j] = 0, otherwise
   * the matrix element at ij is set to 1.
   *
   * @param m the 2D byte array to construct this AdjacencyMatrix from.
   */
  public AdjacencyBitMatrix(byte[][] m){
    matrix = new BitMatrix2D(m);
  }


  /**
   * Copies the matrix elements from the specified DenseDoubleMatrix2D.
   * The matrix element at ij is set to 0 if the element at DoubleMatrix[i][j]
   * = 0, otherwise the matrix element at ij is set to 1.
   *
   * @param m the actual matrix data for this AdjacencyMatrix
   */
  public void setMatrix(DenseDoubleMatrix2D m) {
    int rows = m.rows();
    int cols = m.columns();

    matrix = new BitMatrix2D(rows, cols);

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        matrix.set(i, j, (byte)m.getQuick(i, j));
      }
    }
  }


  /**
   * Sets the matrix for this AdjacencyByteMatrix to the specified
   * byte[][]. The matrix elements are copied form the specified byte[][]
   * and any subsequent changes to that byte[][] will <b>not</b> be
   * reflected in this AdjacencyBitArray.<p>
   *
   * The matrix element at ij is set to 0 if byte[i][j] = 0, otherwise
   * the matrix element at ij is set to 1.
   *
   * @param m the actual matrix data for this AdjacencyMatrix
   */
  public void setMatrix(byte[][] m) {
    matrix = new BitMatrix2D(m);
  }


  /**
   * Gets the specified row of data for this matrix.
   *
   * @param row the index of the row to get
   */
  public DenseDoubleMatrix1D getRow(int row) {
    return matrix.getRow(row);
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
        rowSums += matrix.get(i, j);
      }
    }

    return rowSums / matrix.rows();
  }

  /**
   * Sets a data value in this matrix. If val equals the matrix element
   * at row, col will be set to 0, otherwise it is set to 1.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @para val the value to set ij to.
   */
  public void set(int row, int col, byte val) {
    matrix.set(row, col, val);
  }

  /**
   * Sets a data value in this matrix. If val equals the matrix element
   * at row, col will be set to 0, otherwise it is set to 1.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @para val the value to set ij to.
   */
  public void set(int row, int col, double val) {
    matrix.set(row, col, (byte)val);
  }



  /**
   * Gets the value at row, col. This returns a double rather than a
   * byte for implementation reasons.
   *
   * @param row the row index (i)
   * @param col the col index (j)
   * @return the byte value at row, col (ij) as a double.
   */
  public double get(int row, int col) {
    return matrix.get(row, col);
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