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
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

public class ByteMatrix2D {

  private byte[] matrix;
  private int rows, cols;

  public ByteMatrix2D(int rows, int cols) {
    matrix = new byte[rows * cols];
    this.rows = rows;
    this.cols = cols;
  }

  public ByteMatrix2D(byte[][] vals) {
    rows = vals.length;
    cols = vals.length == 0 ? 0 : vals[0].length;
    matrix = new byte[rows * cols];
    for (int i = 0; i < rows; i++) {
      byte[] cRow = vals[i];
      System.arraycopy(cRow, 0, matrix, i * cols, cols);
    }
  }

  public DenseDoubleMatrix1D getRow(int row) {
    double[] da = new double[cols];
    int start = row * cols;
    for (int i = 0; i < cols; i++) {
      da[i] = matrix[start + i];
    }

    return new DenseDoubleMatrix1D(da);
  }

  /**
   * Sets the value of the specified cell.
   */
  public void set(int row, int col, byte val) {
    matrix[row * cols + col] = val;
  }

  /**
   * Gets the value in the specified cell.
   */
  public byte get(int row, int col) {
    return matrix[row * cols + col];
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
    for (int i = 0; i < matrix.length; i++) {
      sum += matrix[i];
    }

    return sum;
  }

  public ByteMatrix2D copy() {
    ByteMatrix2D copy = new ByteMatrix2D(rows, cols);
    System.arraycopy(this.matrix, 0, copy.matrix, 0, this.matrix.length);
    return copy;
  }

  /**
   * Multiply this matrix by the specified matrix and return the result
   * in a new matrix.
   */
  public ByteMatrix2D zMult(ByteMatrix2D B) {
    int m = rows;
    int n = cols;
    int p = B.cols;

    if (B.rows != n) {
      throw new IllegalArgumentException("Matrix2D inner dimensions do not agree");
    }

    ByteMatrix2D c = new ByteMatrix2D(rows, cols);
    for (int j = p; --j >= 0; ) {
      for (int i = m; --i >=0; ) {
        byte sum = 0;
        for (int k = n; --k >= 0; ) {
          sum += matrix[i * cols + k] * B.get(k, j);
        }

        c.set(i, j, (byte)(sum + c.get(i,j)));
      }
    }

    return c;
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
        b.append(matrix[i * cols + j]);
      }
      b.append("\n");
    }

    //System.out.println("bytematrix toString:\n" + b.toString());
    return b.toString();
  }

  public static void main(String[] args) {
    /*
    ByteMatrix2D a = new ByteMatrix2D(10, 10);
    a.set(3, 1, (byte)2);
    a.set(1, 9, (byte)1);

    ByteMatrix2D b = a.copy();
    System.out.println(a.toString());
    System.out.println(b.toString());
    */
    ByteMatrix2D x = new ByteMatrix2D(6, 6);
    x.set(0, 1, (byte)1);
    x.set(0, 4, (byte)1);
    x.set(1, 2, (byte)1);
    x.set(1, 5, (byte)1);
    x.set(2, 1, (byte)1);
    x.set(3, 4, (byte)1);
    x.set(4, 5, (byte)1);
    x.set(5, 1, (byte)1);

    System.out.println("x:");
    System.out.println(x);

    ByteMatrix2D x2 = x.zMult(x);
    System.out.println("x2:");
    System.out.println(x2);

    ByteMatrix2D x3 = x.zMult(x2);
    System.out.println("x3:");
    System.out.println(x3);

  }
}



