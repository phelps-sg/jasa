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
package uchicago.src.collection;

//import java.util.Arrays;
import java.util.BitSet;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Matrix for storing elements as bits (0 or 1).
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class BitMatrix2D {

  private BitSet matrix;
  private int rows, cols;

  public BitMatrix2D(int rows, int cols) {
    matrix = new BitSet(rows * cols);
    this.rows = rows;
    this.cols = cols;
  }

  public BitMatrix2D(byte[][] vals) {
    rows = vals.length;
    cols = vals.length == 0 ? 0 : vals[0].length;
    for (int i = 0; i < vals.length; i++) {
      for (int j = 0; j < vals[0].length; j++) {
        if (vals[i][j] > 0) matrix.set(i * cols + j);
      }
    }
  }

  public DenseDoubleMatrix1D getRow(int row) {
    double[] da = new double[cols];
    int start = row * cols;
    for (int i = 0; i < cols; i++) {
      da[i] = matrix.get(start + i) ? 1 : 0;
    }

    return new DenseDoubleMatrix1D(da);
  }

  /**
   * Sets the value of the specified cell.
   */
  public void set(int row, int col, byte val) {
    if (val > 0) matrix.set(row * cols + col);
    else matrix.clear(row * cols + col);
  }

  /**
   * Gets the value in the specified cell.
   */
  public byte get(int row, int col) {
    return (byte)(matrix.get(row * cols + col) ? 1 : 0);
  }

  /**
   * Returns the number of rows in this matrix.
   */
  public int rows() {
    return rows;
  }

  /**
   * Returns the number of columns in this matrix.
   */
  public int columns() {
    return cols;
  }

  /**
   * Returns the sum of all the cells.
   */
  public double zSum() {
    double sum = 0.0;
    for (int i = 0; i < matrix.size(); i++) {
      if (matrix.get(i)) sum++;
    }

    return sum;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append(rows);
    b.append(" x ");
    b.append(cols);
    b.append("\n");
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (j != 0) b.append(" ");
        if (matrix.get(i * cols + j)) b.append(1);
        else b.append(0);
      }
      b.append("\n");
    }

    return b.toString();
  }

  public static void main(String[] args) {
    BitMatrix2D matrix = new BitMatrix2D(10, 10);
    matrix.set(1, 4, (byte)1);
    matrix.set(8, 2, (byte)1);

    System.out.println(matrix.get(1, 4));
    System.out.println(matrix.getRow(8));

    System.out.println(matrix.toString());
  }
}



