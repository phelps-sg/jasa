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

import uk.ac.liv.util.MathUtil;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class CompressedPayoffMatrixTest extends TestCase {
  
  CompressedPayoffMatrix pdMatrix; 
  
  int[][] pdEntries = new int[][] { 
      new int[] { 2, 0 },   
      new int[] { 1, 1 },
      new int[] { 0, 2 }      
  };
  
  double[][] pdPayoffs = new double[][] { 
      new double[] { 1, 0 },
      new double[] { 5, 1 },
      new double[] { 0, 3 }
  };
  
  
  public CompressedPayoffMatrixTest( String name ) {
    super(name);
  }
  
  public void initialiseMatrix( CompressedPayoffMatrix matrix,
                                  int[][] entries, double[][] payoffs ) {
    for( int i=0; i<entries.length; i++ ) {
      CompressedPayoffMatrix.Entry entry = 
        new CompressedPayoffMatrix.Entry(entries[i]);
      matrix.setCompressedPayoffs(entry, payoffs[i]);
    }
  }
  
  public void setUp() {
    pdMatrix = new CompressedPayoffMatrix(2, 2);
    initialiseMatrix(pdMatrix, pdEntries, pdPayoffs);
  }
  
  public void printPopulation( double[] population ) {
    for( int i=0; i<population.length; i++ ) {
      System.out.println("Strategy " + i + " = " + population[i]);
    }
  }
  
  public void checkPureStrategyEquilibrium( int strategy, CompressedPayoffMatrix matrix, double[] initialPopulation) {
    System.out.println("\nEvolving from ");
    printPopulation(initialPopulation);
    double[] finalPopulation = matrix.plotRDflow(initialPopulation, 10E-12, (int) 10E7);
    System.out.println("\nyields..");
    printPopulation(finalPopulation);    
    assertTrue(MathUtil.approxEqual(finalPopulation[strategy], 1, 0.01));
  }
  
  public void testPDequilibria() {
    checkPureStrategyEquilibrium(0, pdMatrix, new double[] { 0.3, 0.7 });
    checkPureStrategyEquilibrium(0, pdMatrix, new double[] { 1, 0 });
    checkPureStrategyEquilibrium(0, pdMatrix, new double[] { 0.01, 0.99 });
    checkPureStrategyEquilibrium(0, pdMatrix, new double[] { 0.7, 0.3 });
  }
}

