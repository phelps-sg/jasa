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

package uk.ac.liv.auction.heuristic;

import java.io.*;

import java.util.HashMap;
import java.util.Iterator;

import uk.ac.liv.util.MutableIntWrapper;
import uk.ac.liv.util.MutableDoubleWrapper;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.AbstractSeedable;

import uk.ac.liv.util.io.CSVReader;
import uk.ac.liv.util.io.CSVWriter;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import org.apache.log4j.Logger;


public class EquilibriaDistribution extends AbstractSeedable {

  protected HashMap equilibria;

  protected int totalInstances;
  
  protected CompressedPayoffMatrix payoffMatrix;

  protected String payoffMatrixFileName;
  
  protected int numAgents;
  
  protected int numStrategies;
  
  protected int numRDSamples;
  
  protected String RDPrefix;
   
  static Logger logger = Logger.getLogger(EquilibriaDistribution.class);
  
  public static final String P_NUMAGENTS = "numagents";
  public static final String P_NUMSTRATEGIES = "numstrategies";
  public static final String P_PAYOFFMATRIX = "payoffmatrix";
  public static final String P_NUMRDSAMPLES = "numrdsamples";
  public static final String P_RDPREFIX = "rdprefix";
  public static final String P_EQUILIBRIA = "equilibria";

  public EquilibriaDistribution() {
    equilibria = new HashMap();
    totalInstances = 0;
  }

  public void newEquilibrium( double[] probabilities ) {
    MixedStrategy equilibrium = new MixedStrategy(probabilities);
    MutableIntWrapper instances;
    instances = (MutableIntWrapper) equilibria.get(equilibrium);
    if ( instances == null ) {
      instances = new MutableIntWrapper(1);
      equilibria.put(equilibrium, instances);
    } else {
      instances.value++;
    }
    totalInstances++;
  }

  public void generateReport() {
    Iterator i = equilibria.keySet().iterator();
    while ( i.hasNext() ) {
      MixedStrategy strategy = (MixedStrategy) i.next();
      MutableIntWrapper instanceCount =
        (MutableIntWrapper) equilibria.get(strategy);
      System.out.println(strategy + ": " + instanceCount.value / (double) totalInstances);
    }
  }
  
  public void setup( ParameterDatabase parameters, Parameter base ) {
    payoffMatrixFileName = parameters.getString(base.push(P_PAYOFFMATRIX), null);
    numAgents = parameters.getInt(base.push(P_NUMAGENTS), null, 1);
    numStrategies = parameters.getInt(base.push(P_NUMSTRATEGIES), null, 1);
    numRDSamples = parameters.getInt(base.push(P_NUMRDSAMPLES), null, 1);
    RDPrefix = parameters.getString(base.push(P_RDPREFIX), null);
    try {
      FileInputStream file = new FileInputStream(payoffMatrixFileName);
      CSVReader csvIn = new CSVReader(file, new Class[] {Integer.class, Integer.class, Integer.class, Double.class, Double.class, Double.class} );      
      payoffMatrix = new CompressedPayoffMatrix(numAgents, numStrategies);
      payoffMatrix.importFromCSV(csvIn);
    } catch ( IOException e ) {
      throw new Error(e);
    }
  }
  
  public void sampleRDTrajectories() throws FileNotFoundException {

    for( int i=0; i<numRDSamples; i++ ) {
      double x, y;
      do {
        x = prng.uniform(0, 1);
        y = prng.uniform(0, 1);
      } while ( x+y > 1 );

      double z = 1 - x - y;

      CSVWriter rdPlot = new CSVWriter( new FileOutputStream(RDPrefix + i + ".csv"), 3);
      double[] equilibrium =
        payoffMatrix.plotRDflow(rdPlot, new double[] {x, y, z}, 0.000001, 200000);
      rdPlot.close();

      newEquilibrium(equilibrium);
    }


  }
  
  public static void main( String[] args ) {

    try {
      
      String fileName = args[0];
      File file = new File(fileName);
      if ( ! file.canRead() ) {
        throw new Error("Cannot read parameter file " + fileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      ParameterDatabase parameters = new ParameterDatabase(file);
      EquilibriaDistribution ed = new EquilibriaDistribution();
      ed.setup(parameters, new Parameter(P_EQUILIBRIA));
      ed.sampleRDTrajectories();      
      ed.generateReport();

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }

}

class MixedStrategy {

  protected double[] probabilities;

  protected static double precision = 10;

  public MixedStrategy( double[] mixedStrategy ) {
    this.probabilities = new double[mixedStrategy.length];
    for( int i=0; i<mixedStrategy.length; i++ ) {
      probabilities[i] = mixedStrategy[i];
    }
    round();
  }

  public void round() {
    for( int i=0; i<probabilities.length; i++ ) {
      probabilities[i] = Math.round(probabilities[i]*precision) / precision;
    }
  }

  public boolean equals( Object other ) {
    for( int i=0; i<probabilities.length; i++ ) {
      if ( probabilities[i] != ((MixedStrategy) other).probabilities[i] ) {
        return false;
      }
    }
    return true;
  }

  public int hashCode() {
    int hash = 0;
    int base = 1;
    for( int i=0; i<probabilities.length; i++ ) {
      hash += probabilities[i] * base;
      base *= 2;
    }
    return hash;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    for( int i=0; i<probabilities.length; i++ ) {
      result.append(probabilities[i] + " ");
    }
    return result.toString();
  }

}
