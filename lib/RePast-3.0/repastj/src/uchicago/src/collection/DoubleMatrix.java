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

/**
 * DoubleMatrix: a matrix of doubles (and any other number).
 *
 * @version 1.0 6/29/99
 * @author Nick Collier
 */

public class DoubleMatrix implements BaseMatrix {


  private double[] matrix;
  private int sizeX;
  private int sizeY;

  /**
   * Constructs a matrix of the specified size, initializing the matrix
   * cells to 0. (This doesn't apprear to be correct actually, it just 
   * creates the array.)
   *
   * @param sizeX
   * @param sizeY
   */
  public DoubleMatrix(int sizeX, int sizeY) {
    matrix = new double[sizeX * sizeY];
    this.sizeX = sizeX;
    this.sizeY = sizeY;
  }

  /**
   * Constructs a matrix of the specified size, initializing the matrix
   * cells to the values in matrix. This constructor produces a copy of
   * double[] matrix internally.
   * 
   * @param sizeX
   * @param sizeY
   * @param matrix
   */
  public DoubleMatrix(int sizeX, int sizeY, double[] matrix){
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    if (matrix.length != sizeX * sizeY)
      throw new IllegalArgumentException("Matrix size is not equal to " +
					 "sizeX * sizeY");
    this.matrix = new double[matrix.length];
    System.arraycopy(matrix, 0, this.matrix, 0, matrix.length);
  }

  /**
   * 
   * Returns a double at (x,y) 
   * @param x
   * @param y
   * @return
   */
  public double getDoubleAt(int x, int y) {
    return matrix[y * sizeX + x];
  }

  /**
   * 
   * @param x
   * @param y
   * @param value
   */
  public void putDoubleAt(int x, int y, double value) {
    //System.out.println("col: " + x + " row: " + y);
    matrix[y * sizeX + x] = value;
  }

  /**
   * Copies a Double Matrix
   * 
   * @return a new DoubleMatrix which is a "deep" copy of this matrix.
   */
  public DoubleMatrix copyMatrix() {
    return new DoubleMatrix(sizeX, sizeY, matrix);
  }

  /**
   * Copies the double[] in this Matrix to the specified DoubleMatrix.
   * 
   * @param dm the matrix to copy
   */
  public void copyMatrixTo(DoubleMatrix dm) {
      if(matrix.length != dm.matrix.length){
         double[] aMatrix = new double[sizeY * sizeX];
         System.arraycopy(matrix, 0, aMatrix, 0, sizeY * sizeX);
         dm.matrix = aMatrix;
      }else{
        System.arraycopy(matrix, 0, dm.matrix , 0, sizeY * sizeX);
     }
  }

  /**
   * The x dimension of this Matrix
   * 
   * @return x dimension
   */
  public int getSizeX() {
    return sizeX;
  }

  /**
   * The y dimension of this Matrix
   * 
   * @return y dimension
   */
  public int getSizeY() {
    return sizeY;
  }

  /**
   * Fills the matrix with the passed in value
   *
   * @param value the value to fill the matrix with.
   */
  public void initialize(double value) {
    int len = sizeX * sizeY;
    for (int i = 0; i < len; i++) {
      matrix[i] = value;
    }
  }
  
  /**
   * 
   * @param index
   * @return
   */
  public Object get(int index) {
    return new Double(matrix[index]);
  }
  
  public Object get(int col, int row) {
    return new Double(getDoubleAt(col, row));
  }

  public void put(int index, Object obj) {
    if (!(obj instanceof Number))
      throw new IllegalArgumentException("object must be a Number");

    matrix[index] = ((Number)obj).doubleValue();
  }

  public void put(int col, int row, Object obj) {
    if (!(obj instanceof Number))
      throw new IllegalArgumentException("object must be a Number");
    putDoubleAt(col, row, ((Number)obj).doubleValue());
  }

  public Object remove(int col, int row) {
    Double d = new Double(getDoubleAt(col, row));
    putDoubleAt(col, row, 0d);
    return d;
  }

  public Object remove(int index) {
    Double d = new Double(matrix[index]);
    matrix[index] = 0;
    return d;
  }

  /**
   * Removes the value and the specified column and row, leaving 0 in
   * in its place.
   * @param col
   * @param row
   * @return
   */
  public double removeDouble(int col, int row) {
    double val = getDoubleAt(col, row);
    putDoubleAt(col, row, 0d);
    return val;
  }

  public int size() {
    return sizeX * sizeY;
  }

  public int getNumRows() {
    return sizeY;
  }

  public int getNumCols() {
    return sizeX;
  }

  public double[] getData(){
    return matrix;
  }
  
  /*
   * (non-Javadoc)
   * @see uchicago.src.collection.BaseMatrix#trim()
   */
  public void trim() {}
  
  /**
   * 
   * @param fileName
   */
  public void printToFile(String fileName) {
    try {
      java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter(fileName));

      //String value = "";
      for (int i = 0; i < sizeX; i++) {
        for (int j = 0; j < sizeY; j++) {
          out.write(this.getDoubleAt(i, j) + " ");
        }
        out.write("\n");
      }
      out.flush();
      out.close();
    } catch (java.io.IOException ex) {
      ex.printStackTrace();
    }
  }
}
