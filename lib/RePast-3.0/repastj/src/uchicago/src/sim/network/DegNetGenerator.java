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

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import uchicago.src.collection.ByteMatrix2D;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

/**
 * Generates an adjacency matrix given a mean degree, a variance, and
 * a list of probilities. In general matrix generation works as follows:<p>
 *
 * 1. Every row in matrix is randomly assigned a degree (the vector sum of
 * the row). Each row's degree is a random number drawn from a normal
 * distribution. The mean of this distribution is the average nodal
 * degree specified by the user and the standard deviation of the the
 * distribution is also specified by the user. If the assigned degree
 * is less than 0 it is set to one. If it is greater than the maximum
 * degree (the size of the row - 1) then it is set to the maximun.<p>
 *
 * 2. An empty matrix is seeded with some X (user specifiable) number of rows.
 * The seeding process randomly chooses X number of rows from the list of
 * available rows. The available rows are those rows that haven't been through
 * the random assignment process (see 3).<p>
 *
 * 3. These seed rows are randomly assign a 1 to some number of cells
 * in that row. The amount of cells to set to one is determined according to
 * the degree of that row determined in step 1. For as many cells need to be
 * set to one, the actual individual cell to set is randomly determined
 * through a uniform distribution. No cell is set twice and the diagonal is
 * excluded. The individualy chosen cell is then set to one.<p>
 *
 * 4. New rows are then created on the basis of the cell values created in
 * step 3. These values are created according to some user specified probability.
 * If the matrix is to be symmetric (not a digraph) then the probability is 1.0.
 * The general idea here is to determine whether the links randomly created
 * in step 3. go both ways. So for example, if after step 3 the matrix
 * looks like:
 * <pre><code>
 *   A B C
 * A 0 0 0
 * B 1 0 1
 * C 0 0 0
 * </code></pre>
 * a link will be created from A to B, and from C to B with some
 * specified probability. The new rows corresponding to the nodes that
 * are the source of these new links are then used in step 6. For example,
 * if a link is created from A to B, row A will then be used in step 6.<p>
 *
 * Note that links are only created in a row if the current row sum for
 * that row is less than the intended degree for that row as determined in
 * step 1. However, if the matrix is to be made symmetric, it is made so
 * and the row sums are ignored.<p>
 *
 * 5. New links are then created using the powers of the matrix that
 * results from step 4. These links are created according to user
 * specified probablilities corresponding to the path length (powers).
 * The exact formula is as follows:
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
 * As in step 4., no links are created if the current row sum for a row is
 * greater than intended row sum. This holds both for symmetric and
 * directed matrices. If the matrix is to be symmetric then
 * this condition must hold for both nodes.
 *
 * 6. The list of rows created in step 4. undergo the random assignment
 * process described in step 3. If the sum of the row is greater than the
 * number of random links to be created then no assignment will occur.<p>
 *
 * 7. Step 4. is then repeated using the list of rows from step 6. in place of
 * the seeded rows. Only those cells actually assigned in step 6. will be
 * used in step 3. If step 6. fails to generate any new cells (that is,
 * column vals whose corresponding row can be used in step 3), then new
 * seeded rows are generated as in step 2.<p>
 *
 * 8. Steps 3.- 7 are repeated until all of the rows in the matrix have undergone
 * the random link assignment described in step 3.<p>
 *
 * Each row should undergo random link assignment once and only once. Rows that
 * undergo random link assignment in the same iteration and refer to each
 * other will not get those links created. So for example, if rows C and
 * A have just been randomly assigned links, such that:
 * <pre><code>
 *   A B C
 * A 0 1 1
 * B 0 0 0
 * C 0 1 0
 * </code></pre>
 * A link from C -> A will not be created. This assumes of course that
 * the matrix is not symmetrical.<p>
 *
 * The getMatrix method takes three arguments: the number of rows to seed,
 * an array of probabilities, and whether this is a directed graph or not.
 * The number of rows to seed determines the initial number of rows to seed
 * and the number of rows to seed when there are no new rows to work with
 * from a previous seed (as described in step 7). The first member of the
 * probabilities array is a float between 0 and 1.0 that determine the
 * probabilities mentioned in step 3 (i.e. the probability that if A -> B
 * then B -> A). The remaining probabilities refer to the power probilities -
 * the second member of the array is the probiblities the some node
 * will know another node 2 links away, and so on for the remaining members of
 * the array (i.e. the third member is for three links away). The last
 * argument determines wether the matrix is directed or not. If not then it
 * is symmetric and the first member of the probilities array is ignored as
 * if A -> B then B -> A.<p>
 *
 * <b>Note</b>: This class is still in beta and should only be used
 * in order to test it.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class DegNetGenerator {

  private int degree;
  //private float variance;
  private ByteMatrix2D matrix;
  private int size;

  // the rows left to calculate
  private HashSet remainingRows;

  // the rows that have been created
  private Hashtable allRows;

  private double avgDensity;

  // this contains Integers for cols
  // used to randomly iterate through the a the cells in a row

  private Vector colNums;

  // debug vars
  int matrixNum = 0;

  class Row {
    int rowSum = 0;
    int rowNum;
    int degree = 0;
    Vector rowsToDo = new Vector();

    public Row(int num, int degree) {
      rowNum = num;
      this.degree = degree;
    }

    public boolean equals(Object o) {
      if (o instanceof Row) {
        Row r = (Row)o;
        if (this.rowNum == r.rowNum) {
          return true;
        }

        return false;
      }

      return false;
    }
  };

  class RowDetails {
    int count = 0;
    Vector rowsToDo;

    public RowDetails(int count, Vector rToDo) {
      this.count = count;
      rowsToDo = rToDo;
    }
  };

  public DegNetGenerator(int degree, float variance, int size) {
    this.degree = degree;
    //this.variance = variance;
    matrix = new ByteMatrix2D(size, size);
    this.size = size;
    avgDensity = (double)(size / degree);
    remainingRows = new HashSet(size);

    colNums = new Vector(size);

    for (int i = 0; i < size; i++) {
      Integer val = new Integer(i);
      remainingRows.add(val);
      colNums.add(val);
    }

    Random.createNormal(degree, variance);
    Random.createUniform();

    initAllRows();
  }

  private void initAllRows() {
    allRows = new Hashtable(size);
    Vector intVec = new Vector(size);

    // is this necessary - shuffling the vector
    for (int i = 0; i < size; i++) {
      intVec.add(new Integer(i));
    }

    SimUtilities.shuffle(intVec, Random.uniform);

    for (int i = 0; i < intVec.size(); i++) {
      int row = ((Integer)intVec.get(i)).intValue();
      int vecSum = (int)Math.floor(Random.normal.nextDouble());
      if (vecSum < 1) {
        vecSum = 1;
      }

      Row newRow = new Row(row, vecSum);
      allRows.put(new Integer(row), newRow);
    }
  }

  /**
   * Returns a random matrix generated using the specified parameters.
   *
   * @param numRowsToSeed the number of rows to seed
   * @param edgeProbability the probability ...
   */

  public AdjacencyMatrix getMatrix(int numRowsToSeed, float[] edgeProbability,
      boolean isDigraph)
  {

    // generate the seed rows
    Collection lastRows = makeSeedRows(numRowsToSeed);
    Iterator iter = lastRows.iterator();
    while (iter.hasNext()) {
      Row r = (Row)iter.next();
      //System.out.println(r.rowNum);
      makeRow(r);
      remainingRows.remove(new Integer(r.rowNum));
    }

    //System.outprintln(matrixNum++ + " seeded:");
    //System.outprintln(matrix);
    //System.outprintln();

    boolean moreRows = true;

    while (moreRows) {
      if (isDigraph) {
        lastRows = makeLinkedRows(lastRows, edgeProbability[0], matrix);
      } else {
        //System.outprintln("symmetric");
        lastRows = makeSymmetric(lastRows);
      }

      //System.outprintln(matrixNum++ + " after make linked rows:");
      //System.outprintln(matrix);
      //System.outprintln();

      ByteMatrix2D powMatrix = matrix.copy();

      ByteMatrix2D[] powArray = new ByteMatrix2D[edgeProbability.length - 1];

      //System.out.println("starting matrix mult");
      for (int j = 0; j < edgeProbability.length - 1; j++) {
        powMatrix = powMatrix.zMult(matrix);
        powArray[j] = powMatrix.copy();
      }
      //System.out.println("ending matrix mult");

      makeLinkedRowsFromPower(edgeProbability, powArray, isDigraph);

      //System.outprintln(matrixNum++ + " after pow matrix:");
      //System.outprintln(matrix);
      //System.outprintln();

      boolean reseed = true;

      iter = lastRows.iterator();
      while (iter.hasNext()) {
        Row r = (Row)iter.next();
        makeRow(r);
        remainingRows.remove(new Integer(r.rowNum));
        if (r.rowsToDo.size() > 0)
          reseed = false;
      }

      //System.outprintln(matrixNum++ + " after make row:");
      //System.outprintln(matrix);
      //System.outprintln();

      if (reseed) {
        if (remainingRows.size() == 0) {
          moreRows = false;

          // in case some added in make row but that row already made
          makeSymmetric(lastRows);
        } else {
          lastRows = makeSeedRows(numRowsToSeed);

          iter = lastRows.iterator();
          while (iter.hasNext()) {
            Row r = (Row)iter.next();
            makeRow(r);
            remainingRows.remove(new Integer(r.rowNum));
          }

          //System.outprintln(matrixNum + " after seeded:");
          //System.outprintln(matrix);
          //System.outprintln();
        }
      }
    }

    return new AdjacencyByteMatrix(matrix);
  }

  private Collection makeSeedRows(int numRowsToSeed) {
    if (numRowsToSeed > size) {
      throw new IllegalArgumentException("number of rows to seed is greater than size of matrix");
    }

    if (numRowsToSeed > remainingRows.size()) {
      numRowsToSeed = remainingRows.size();
    }

    Vector lastRows = new Vector(numRowsToSeed);
    Object[] rowsToChoose = remainingRows.toArray();
    for (int i = 0; i < numRowsToSeed; i++) {
      int index = Random.uniform.nextIntFromTo(0, rowsToChoose.length - 1);
      Integer iVal = (Integer)rowsToChoose[index];
      Row row = (Row)allRows.get(iVal);
      while (lastRows.contains(row)) {
        index = Random.uniform.nextIntFromTo(0, rowsToChoose.length - 1);
        iVal = (Integer)rowsToChoose[index];
        row = (Row)allRows.get(iVal);
      }

      lastRows.add(row);
    }

    return lastRows;
  }

  /*
   * for each row in rows where ij = 1 make ji = 1
   */
  private Collection makeSymmetric(Collection rows) {
    Hashtable newRows = new Hashtable();
    Iterator iter = rows.iterator();
    while (iter.hasNext()) {
      Row r = (Row)iter.next();
      for (int j = 0; j < size; j++) {
        byte val = (byte)matrix.get(r.rowNum, j);
        if (val > 0 && matrix.get(j, r.rowNum) != val) {
          matrix.set(j, r.rowNum, val);
          Row newRow = (Row)newRows.get(new Integer(j));
          if (newRow == null) {
            newRow = (Row)allRows.get(new Integer(j));
            newRows.put(new Integer(j), newRow);
          }
          newRow.rowSum++;
        }
      }
    }

    return newRows.values();

  }

  /*
   * for each Row in rows, where ij = 1, make ji = 1 || ji = 0
   * depending on prob.
   *
   * m is the matrix from which the ij val is taken.
   *
   * Returns the rows (the j rows) created from the i rows.
   */
  private Collection makeLinkedRows(Collection rows, float prob,
    ByteMatrix2D m)
  {
    Hashtable newRows = new Hashtable();
    Iterator iter = rows.iterator();
    while (iter.hasNext()) {
      Row r = (Row)iter.next();
      Vector rowsToDo = r.rowsToDo;
      SimUtilities.shuffle(rowsToDo, Random.uniform);
      for (int j = 0; j < rowsToDo.size(); j++) {
        int col = ((Integer)rowsToDo.get(j)).intValue();
        byte val = (byte)m.get(r.rowNum, col);
        if (val > 0) {
          float pVal = Random.uniform.nextFloatFromTo(0f, 1.0f);
          if (pVal <= prob && matrix.get(col, r.rowNum) != 1) {

            Row newRow = (Row)newRows.get(new Integer(col));
            if (newRow == null) {
              newRow = (Row)allRows.get(new Integer(col));
              newRows.put(new Integer(col), newRow);
            }

            if (newRow.rowSum < newRow.degree) {
              matrix.set(col, r.rowNum, (byte)1);
              newRow.rowSum++;
            }
          }
        }
      }
    }

    // turn hashvals into vector right here
    return newRows.values();
  }

  private void makeLinkedRowsFromPower(float[] p, ByteMatrix2D[] powArray,
      boolean isDigraph)
  {
    // remove first elements of probs array
    float[] probs = new float[p.length - 1];
    System.arraycopy(p, 1, probs, 0, p.length - 1);

    // should calc this only once somewhere else
    float probSum = 0;
    for (int i = 0; i < probs.length; i++) {
      probSum += probs[i];
    }

    double dt = avgDensity * (1 - probSum);

    // dt = avgDensity;

    for (int i = 0; i < size; i++) {
      Row r = (Row)allRows.get(new Integer(i));
      double rowDegree = (r.degree / degree);

      SimUtilities.shuffle(colNums, Random.uniform);

      for (int l = 0; l < colNums.size(); l++) {
        Integer jVal = (Integer)colNums.get(l);
        int j = jVal.intValue();
        if (i != j) {
          if (isDigraph) {

            // make sure that the we don't have vector sum > degree for this
            // row
            if (matrix.get(i, j) < 1 && r.rowSum < r.degree) {
              if (hasLinkFromPower(probs, powArray, rowDegree, dt, i, j)) {
                matrix.set(i, j, (byte)1);
                r.rowSum++;
              }
            }
          } else {
            // is symmetric
            Row rj = (Row)allRows.get(jVal);
            if (matrix.get(i, j) < 1 && r.rowSum < r.degree &&
              rj.rowSum < rj.degree)
            {
              if (hasLinkFromPower(probs, powArray, rowDegree, dt, i, j)) {
                matrix.set(i, j, (byte)1);
                r.rowSum++;
                matrix.set(j, i, (byte)1);
                rj.rowSum++;
              }
            }
          }
        }
      }
    }
  }

  private boolean hasLinkFromPower(float[] probs, ByteMatrix2D[] powArray,
      double rowDegree, double dt, int i, int j)
  {

    // the equation here is a follows:
    // (i_intended_vec_sum / avgDensity) * (density +
    // (probs[0] * powArray[0]_ij) + (probs[1] * powArray[1]_ij) + ...
    // + (probs[n] * powArray[n]_ij)) >= uniform_float_from_1_to_0
    // where density is either:
    // size / degree or
    // (size / degree) * (1 - sum(probs[n]))

    double powVal = 0.0;
    for (int k = 0; k < powArray.length; k++) {
      double cell = powArray[k].get(i, j);
      powVal += cell * probs[0];
    }

    double eqVal = rowDegree * (dt + powVal);
    float pVal = Random.uniform.nextFloatFromTo(0f, 1.0f);
    return eqVal >= pVal;
  }



  /*
  private void makeLinkedRowsFromPower(Collection rows, float prob, DenseDoubleMatrix2D powMatrix)
  {
    //Iterator iter = rows.iterator();
    while (iter.hasNext()) {
      Row r = (Row)iter.next();
      System.out.println(r.rowNum + " - row sum entering power: " + r.rowSum);

      for (int j = 0; j < size; j++) {
        if (j != r.rowNum) {
          double val = powMatrix.getQuick(r.rowNum, j);
          if (val > 0) {
            float pVal = Random.uniform.nextFloatFromTo(0f, 1.0f);
            if (pVal <= prob) {
              if (matrix.getQuick(r.rowNum, j) != 1) {
                matrix.setQuick(r.rowNum, j, 1);
                r.rowSum++;
              }
            }
          }
        }
      }
    }
  }
  */

  private void makeRow(Row r) {

    r.rowsToDo = null;
    //System.outprintln("row num: " + r.rowNum);
    //System.outprintln("row sum: " + r.rowSum);

    int numArcs = r.degree;

    //System.outprintln("Num arcs: " + numArcs);

    if (r.rowSum >= numArcs) {
      // now rows to do as new cell values were generated
      r.rowsToDo = new Vector();
      //System.outprintln("returning without making rows");
      return;
    }

    numArcs = numArcs - r.rowSum;
    //System.outprintln("Num Arcs to be made: " + numArcs);
    int row = r.rowNum;


    if (numArcs >= size - r.rowSum) {
      RowDetails details = setRowToOne(row);
      r.rowsToDo = details.rowsToDo;
      r.rowSum += details.count;
      //System.outprintln("row sum for arcs > size: " + r.rowSum);

    } else {
      int cellVal = 1;

      // avoids problems with setting the cell randomly to 1
      // when the majority of the cells are to be set to one
      // and the rng will loop for a long time trying to find an
      // empty cell

      if (numArcs > (size - r.rowSum) / 2) {
        RowDetails details = setRowToOne(row);
        r.rowsToDo = details.rowsToDo;
        r.rowSum += details.count;
        cellVal = 0;

        // - 1 cuz already a 0 on the diagonal
        numArcs = size - numArcs - 1;
      } else {
        r.rowsToDo = new Vector();
      }


      for (int j = 0; j < numArcs; j++) {
        int col = Random.uniform.nextIntFromTo(0, size - 1);
        while (matrix.get(row, col) == cellVal || col == row) {
          col = Random.uniform.nextIntFromTo(0, size - 1);
        }

        matrix.set(row, col, (byte)cellVal);
        if (cellVal == 1) {
          if (remainingRows.contains(new Integer(col))) {
            r.rowsToDo.add(new Integer(col));
          }
          r.rowSum++;

        } else {
          r.rowsToDo.remove(new Integer(col));
          r.rowSum--;
        }
      }
    }
    //System.outprintln(r.rowNum + " sum = " + r.rowSum);
    //System.outprintln("out of make row");
  }

  /*
   * Returns a RowDetails object that contains the vector of rows that were set
   * excluding
   * those rows that have already been through makeRow, and the count of
   * the cells that were set to one. Note that the size of the vector and the
   * count might not be equal.
   */

  private RowDetails setRowToOne(int row) {
    Vector v = new Vector();
    int linkCount = 0;
    for (int i = 0; i < size; i++) {
      if (i != row) {
        if (matrix.get(row, i) != 1) {
          Integer iVal = new Integer(i);
          if (remainingRows.contains(iVal)) {
            v.add(iVal);
          }

          matrix.set(row, i, (byte)1);
          linkCount++;
        }
      }
    }

    return new RowDetails(linkCount, v);
  }

  /*
  public static void main(String[] args) {
    Getopt opt = new Getopt("NetGen", args, "s:d:t:p:hyn:f:");

    int c;
    String arg;
    int size = 0;
    int d = 0;
    float dev = -999;
    float[] ps = null;
    boolean isSym = false;
    int seed = 0;
    String file = "";


    while ((c = opt.getopt()) != -1) {
      switch (c) {

        case 's':
          arg = opt.getOptarg();
          try {
            size = Integer.parseInt(arg);

          } catch (NumberFormatException ex) {
            System.out.println("Size argument is not a valid number");
            System.exit(0);
          }
          break;

        case 'd':
          arg = opt.getOptarg();
          try {
            d = Integer.parseInt(arg);
          } catch (NumberFormatException ex) {
            System.out.println("Degree argument is not a valid number");
            System.exit(0);
          }

          break;

        case 't':
          arg = opt.getOptarg();
          try {
            dev = Float.parseFloat(arg);
          } catch (NumberFormatException ex) {
            System.out.println("Std dev argument is not a valid number");
            System.exit(0);
          }
          break;

        case 'n':
          arg = opt.getOptarg();
          try {
            seed = Integer.parseInt(arg);
          } catch (NumberFormatException ex) {
            System.out.println("Number of rows to seed argument (-n) is not a valid number");
            System.exit(0);
          }

          break;

        case 'p':
          arg = opt.getOptarg();
          StringTokenizer t = new StringTokenizer(arg, ",");
          ps = new float[t.countTokens()];
          int i = 0;
          while (t.hasMoreTokens()) {
            try {
              ps[i++] = Float.parseFloat(t.nextToken());
            } catch (NumberFormatException ex) {
              System.out.println("Probability argument is invalid");
              System.exit(0);
            }
          }
          break;

        case 'y':
          isSym = true;
          break;

        case 'f':
          file = opt.getOptarg();
          break;

        case 'h':
          System.out.println("Usage:");
          System.out.println("\t-s size of matrix (e.g. -s 20)");
          System.out.println("\t-d average degree of a node (e.g. -d 3)");
          System.out.println("\t-t standard deviation for average degree (e.g. -t 1)");
          System.out.println("\t-p probability list. (e.g. -p .25,.1,.01,.02)");
          System.out.println("\t-n number of rows to seed (e.g. -n 3)");
          System.out.println("\t-y if present then matrix will by symmetrical (e.g. -y)");
          System.out.println("\t-f name of file to append results to (e.g. -f ./results.txt)");
          System.out.println("\n\tArguments -s, -d, -t, -p, and -n are required");
          System.exit(0);

        case '?':
          System.exit(0);
          break;

        default:
      }
    }

    if (size < 1) {
      System.out.println("Size (-s) argument is missing or is not a valid number");
      System.exit(0);
    }

    if (d < 1) {
      System.out.println("Degree (-d) argument is missing or is not a valid number");
      System.exit(0);
    }

    if (seed < 1) {
      System.out.println("Seed (-n) argument is missing or is not a valid number");
      System.exit(0);
    }

    if (dev == -999) {
      System.out.println("Std Dev (-t) argument is missing or is not a valid number");
      System.exit(0);
    }

    if (ps == null) {
      System.out.println("Probabilities (-p) argument is missing");
      System.exit(0);
    }


    DegNetGenerator gen = new DegNetGenerator(d, dev, size);
    String out = new String("Size: " + size + "\n");
    out += "Avg. Node Degree: " + d + "\n";
    out += "Std Dev.: " + dev + "\n";
    out += "Number of rows to Seed: " + seed + "\n";
    out += "Probabilities: ";
    for (int i = 0; i < ps.length; i++) {
      if (i == 0) {
        out += ps[i];
      } else {
        out += ", " + ps[i];
      }
    }

    out += "\n";
    out += "Is Symmetric: " + isSym;

    System.out.println("\n" + out);
    AdjacencyMatrix m = gen.getMatrix(seed, ps, !isSym);
    System.out.println(m);

    if (file.length() != 0) {
      try {
        PrintWriter oFile = new PrintWriter(new FileOutputStream(file, true));
        oFile.println();
        oFile.println(out);
        oFile.println(m);
        oFile.flush();
      } catch (IOException ex) {
        System.out.println("Error writing to file " + file);
        ex.printStackTrace();
        System.exit(0);
      }
    }
  }
  */
}
