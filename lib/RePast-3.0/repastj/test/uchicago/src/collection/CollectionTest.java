package uchicago.src.collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.util.Random;
//import junit.extensions.*;

public class CollectionTest extends TestCase {

  SparseObjectMatrix sparse;
  DoubleMatrix dmatrix;
  NewMatrix nmatrix;
  BitMatrix2D bmatrix;
  ByteMatrix2D bymatrix;

  int[][] data = {{53, 40, -4, 12, 53, 90, 39, 57, -7, 4, 91, 59, 45, 0,
		   -7, -8, 38, -7, 86, 52},
		  {94, -6, -2, 33, 29, 43, 71, 11, 93, 30, 84,
		   80, 0, -10, 86, 24, 10, 11, 51, 48},
		  {51, 25, 95, 72, 76, 77, 68, 84, 31,
		   33, 48, 11, 65, 62, 76, 9, 15, 85, 8, 5},
		  {43, 42, 9, 26, 86, 20, 18, 58, 29, 41, 70,
		   1, 49, 44, 53, 16, 48, 61, 28, 27},
		  {50, 51, 36, 47, -2, 94, 89, -8, -5, 67,
		   9, -8, 47, 79, 96, 57, 53, 96, 49, 94},
		  {12, 23, 2, 11, 3, 26, 42, 21, 5, -10,
		   1, 24, -3, 26, 46, 52, 9, 74, 87, 87},
		  {45, 63, 53, 62, 31, 12, 58, 59, 31, 66,
		   58, 24, 97, 78, 68, 12, 76, 49, 27, 59},
		  {97, 86, 19, 87, 87, 99, 43, 93, 41, 20,
		   -4, 80, 91, 32, 84, 97, 62, 7, 17, 9},
		  {23, 26, 93, 75, 23, 3, 29, 18, 74, 45,
		   -5, 68, 13, 39, 66, 88, 26, 4, -3, 61},
		  {9, 48, 45, 31, 8, 83, 41, 97, 90, 21,
		   39, 23, 6, 20, 77, 74, 68, -7, 64, 67},
		  {57, 42, 1, 80, 36, 48, 72, 84, 82, 0,
		   10, 85, 78, 75, 44, 95, 13, 57, 40, 20},
		  {3, 50, 31, -3, 48, 1, 67, 78, 78, 48, 61,
		   24, 68, -5, 23, 64, 67, 64, -4, 37},
		  {11, 73, 25, 19, 58, 7, 23, 58, 57, 86,
		   78, 79, 16, 85, 11, 38, 54, 41, 45, 28},
		  {-7, 25, 25, 94, 49, -3, -7, 26, 28, 84, 16,
		   53, 5, 22, 85, 92, 97, 82, 82, 31},
		  {0, 41, 86, -5, -4, 48, 81, 80, 52,
		   14, 22, 40, 99, 21, 87, 18, 5, 62, 31, 87},
		  {33, 16, 22, 61, 16, 30, 39, 1, 44, 91, 86,
		   38, 56, 69, 53, 80, 71, -8, 78, -7},
		  {-9, 18, 90, 12, 78, 76, 63, 0, 1, 99,
		   85, 89, 95, -4, 28, 36, 81, 3, 16, 18},
		  {23, 82, 36, -8, 1, 26, 69, 6, 14, 83, 63,
		   89, 90, 89, -2, 52, -7, 63, 61, 82},
		  {15, 70, 82, 93, -10, 73, 91, 21, 84,
		   81, 96, 95, 36, 35, 5, 97, 92, 20, 38, 67},
		  {64, 67, 18, 62, 35, 27, 38, 85, -3, 51, 45,
		   27, 22, 33, -8, 79, 32, -7, 58, 22},
		  {72, 31, 69, 11, 72, 95, -3, 25, 21, 5, 64,
		   75, 58, 45, 76, 28, -1, 90, 36, 85},
		  {27, 56, 7, 76, 88, 62, 58, 99, 74, 62, -9,
		   47, 45, 95, 4, 43, 14, 34, 40, 26},
		  {-10, 59, 40, 40, 34, 90, 71, 34, 64, -5,
		   68, 18, 13, 49, 92, 17, 23, 80, 72, 42},
		  {1, 37, 87, 39, 57, 7, 23, 2, 4, -2, 15, -4,
		   64, 0, 99, -10, 26, 70, -4, 0},
		  {75, 74, 61, 88, 60, 66, 54, 94, 16,
		   5, 50, -10, 20, 33, 34, 23, -4, -1, -3, 91},
		  {5, 99, -7, 18, 39, 22, 12, 43, 18, 61, 94,
		   27, 18, -10, -10, 52, 28, 14, 26, 87},
		  {97, 35, 94, -7, 93, 57, 65, 57, 94, 90,
		   65, 91, 35, 63, 95, 9, 91, 57, 87, 4},
		  {13, 16, 81, 80, 98, 30, 70, 76, 69, 31,
		   75, 1, 44, 49, 52, 37, 60, 56, 3, -2},
		  {93, 99, 12, 32, 76, 30, 90, 16, 27, 81,
		   80, 23, 37, 13, 89, -7, 49, 27, 67, 56},
		  {-5, 61, 34, -10, 4, 53, 67, 72, 21, 82,
		   25, 32, 4, 61, -4, 32, 84, 59, 75, -7}};
  byte[][] bitData =
  {{0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0},
   {1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0},
   {0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
   {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0},
   {0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0},
   {1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
   {1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0},
   {1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1},
   {0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0},
   {1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0},
   {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1},
   {1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0},
   {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1},
   {0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1},
   {1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0},
   {1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1},
   {1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1},
   {0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1},
   {1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
   {0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1},
   {0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
   {0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0},
   {0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0},
   {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1},
   {0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0},
   {1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1},
   {1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1},
   {0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1},
   {1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1},
   {0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0}};


  public CollectionTest(String name) {
    super(name);
  }
  

  protected void setUp() {
    Random.createUniform();
    setupSparseMatrix();
    setupDoubleMatrix();
    setupNewMatrix();
    setupByteMatrix();
    setupBitMatrix();
    
  }

  private void setupSparseMatrix() {
    sparse = new SparseObjectMatrix(data[0].length, data.length);
    for (int i = 0; i < sparse.getNumRows(); i++) {
      for (int j = 0; j < sparse.getNumCols(); j++) {
	sparse.put(j, i, new Integer(data[i][j]));
      }
    }
  }

  private void setupNewMatrix() {
    nmatrix = new NewMatrix(data[0].length, data.length);
    for (int i = 0; i < nmatrix.getNumRows(); i++) {
      for (int j = 0; j < nmatrix.getNumCols(); j++) {
	nmatrix.put(j, i, new Integer(data[i][j]));
      }
    }
  }

  private void setupByteMatrix() {
    bymatrix = new ByteMatrix2D(data.length, data[0].length);
    for (int i = 0; i < bymatrix.rows(); i++) {
      for (int j = 0; j < bymatrix.columns(); j++) {
	bymatrix.set(i, j, (byte)data[i][j]);
      }
    }
  }

 
  private void setupBitMatrix() {
    bmatrix = new BitMatrix2D(data.length, data[0].length);
    for (int i = 0; i < bmatrix.rows(); i++) {
      for (int j = 0; j < bmatrix.columns(); j++) {
	bmatrix.set(i, j, (byte)bitData[i][j]);
      }
    }
  }
 

  private void setupDoubleMatrix() {
    dmatrix = new DoubleMatrix(data[0].length, data.length);
    for (int i = 0; i < dmatrix.getNumRows(); i++) {
      for (int j = 0; j < dmatrix.getNumCols(); j++) {
	dmatrix.putDoubleAt(j, i,(double)data[i][j]);
      }
    }
  } 

  public void testSparseGet() {
    
    for (int i = 0; i < sparse.getNumRows(); i++) {
      for (int j = 0; j < sparse.getNumCols(); j++) {
	Integer val = (Integer)sparse.get(j, i);
	assertEquals(data[i][j], val.intValue());
      }
    }
  }

   public void testNewGet() {
    
    for (int i = 0; i < nmatrix.getNumRows(); i++) {
      for (int j = 0; j < nmatrix.getNumCols(); j++) {
	Integer val = (Integer)nmatrix.get(j, i);
	assertEquals(data[i][j], val.intValue());
      }
    }
  }

  public void testDoubleGet() {

    for (int i = 0; i < dmatrix.getNumRows(); i++) {
      for (int j = 0; j < dmatrix.getNumCols(); j++) {
	double val = dmatrix.getDoubleAt(j, i);
	assertEquals(data[i][j], val, 0);
      }
    }
  }

  public void testByteGet() {

    for (int i = 0; i < bymatrix.rows(); i++) {
      for (int j = 0; j < bymatrix.columns(); j++) {
	byte val = bymatrix.get(i, j);
	assertEquals((byte)data[i][j], val);
      }
    }
  }

  public void testBitGet() {
    for (int i = 0; i < bmatrix.rows(); i++) {
      for (int j = 0; j < bmatrix.columns(); j++) {
	byte val = bmatrix.get(i, j);
	assertEquals(bitData[i][j], val);
      }
    }
  }

  public void testDoubleInitialize() {
    dmatrix = new DoubleMatrix(100, 10);
    dmatrix.initialize(1.23);
    assertEquals(1.23, dmatrix.getDoubleAt(32, 4), 0);
  }

  public void testDoubleMatrixConstructor() {
    double[] foo = {3, 1.23, 33.9, 34.2, 234.1, 2, 5, 101.223};
    dmatrix = new DoubleMatrix(4, 2, foo);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 2; j++) {	
	assertEquals("x: " + i + ", j: " + j,
		     foo[j * 4 + i], dmatrix.getDoubleAt(i, j), 0);
      }
    }
  }

    

  public void testSparseRemove() {
    Integer val = (Integer)sparse.remove(14, 13);
    assertEquals(data[13][14], val.intValue());

    Object o = sparse.get(14, 13);
    assertTrue(o == null);
  }

  public void testNewRemove() {
    Integer val = (Integer)nmatrix.remove(14, 13);
    assertEquals(data[13][14], val.intValue());

    Object o = nmatrix.get(14, 13);
    assertTrue(o == null);
  }

  public void testDoubleRemove() {
    double d = dmatrix.removeDouble(14, 13);
    assertEquals(data[13][14], d, 0);
    assertTrue(dmatrix.getDoubleAt(14, 13) == 0);

    Double dub = (Double)dmatrix.remove(12, 23);
    assertEquals(new Double(data[23][12]), dub);
    assertTrue(dmatrix.getDoubleAt(12, 23) == 0);
  }

  public void testSize() {
    assertTrue(30 * 20 == sparse.size());
  }

  /*
   * Just for generating random data to paste in above
   */
  public void tesDummy() {
 
    
    System.out.print("{");
    for (int i = 0; i < 30; i++) {
      System.out.print("{");
      String row = "";
      for (int j = 0; j < 20; j++) {
	int val = Random.uniform.nextIntFromTo(0, 1);
	row += val + ", ";
      }

      row = row.substring(0, row.length() - 2);
      System.out.println(row + "},");
    }

    System.out.println("}");
  }
  
  
  public static Test suite() {
    return new TestSuite(uchicago.src.collection.CollectionTest.class);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
  

  
}
