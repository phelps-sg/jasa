/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package test.uk.ac.liv.auction.heuristic;

import junit.framework.TestCase;

import uk.ac.liv.auction.heuristic.*;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class CompressedPayoffMatrixTest extends TestCase {
  
  CompressedPayoffMatrix matrix; 
  
  int[][] entries = new int[][] { new int[] { 1, 1, 0 },
                                  new int[] { 0, 1, 1 },
                                  new int[] { 1, 0, 1 },
                                  new int[] { 2, 0, 0 },
                                  new int[] { 0, 2, 0 },
                                  new int[] { 0, 0, 2 }
  };
  
  double[][] payoffs = new double[][] { new double[] { 10, 0, 0 },
                                        new double[] { 0, 10, 0 },
                                        new double[] { 0, 0, 10 },
                                        new double[] { 0, 0, 0 },
                                        new double[] { 0, 0, 0 },
                                        new double[] { 0, 0, 0 }
  };
  
  
  public CompressedPayoffMatrixTest( String name ) {
    super(name);
  }
  
  public void setUp() {
    matrix = new CompressedPayoffMatrix(2, 3);
    for( int i=0; i<entries.length; i++ ) {
      CompressedPayoffMatrix.Entry entry = 
        new CompressedPayoffMatrix.Entry(entries[i]);
      matrix.setCompressedPayoffs(entry, payoffs[i]);
    }
  }
  
  public void testRD() {
    double[] p = new double[] { 0.2, 0.2, 0.6 };
    matrix.plotRDflow(new uk.ac.liv.util.io.CSVWriter(System.out, 3), p, 0.001, 100);
  }
}

