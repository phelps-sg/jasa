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

import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import java.io.*;

import uk.ac.liv.util.MutableDoubleWrapper;
import uk.ac.liv.util.MutableIntWrapper;
import uk.ac.liv.util.Partitioner;
import uk.ac.liv.util.BaseNIterator;

import uk.ac.liv.util.io.DataWriter;
import uk.ac.liv.util.io.CSVReader;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */


public class CompressedPayoffMatrix {

  protected int numPlayers;

  protected int numStrategies;

  protected Vector matrix;
  
  static Logger logger = Logger.getLogger(CompressedPayoffMatrix.class);
  
  public CompressedPayoffMatrix( int numPlayers, int numStrategies ) {
    this.numPlayers = numPlayers;
    this.numStrategies = numStrategies;
    matrix = initialiseMatrix(numPlayers, numStrategies);
  }

  protected Vector initialiseMatrix( int numPlayers, int s ) {
    Vector v = new Vector(numPlayers);
    if ( s == 1 ) {
      for( int i=0; i<=numPlayers; i++ ) {
        v.add( new double[numStrategies] );
      }
      return v;
    } else {
      for( int i=0; i<=numPlayers; i++ ) {
        v.add(initialiseMatrix(numPlayers, s-1));
      }
      return v;
    }
  }

  public double[] getCompressedOutcome( int[] compressedEntry ) {
    Vector v = matrix;
    int i;
    for( i=0; i<compressedEntry.length-1; i++ ) {
      v = (Vector) v.get(compressedEntry[i]);
    }
    return (double[]) v.get(compressedEntry[i]);
  }

  public void setCompressedOutcome( int[] compressedEntry,
                                      double[] compressedOutcome ) {
    Vector v = matrix;
    int i;
    for( i=0; i<compressedEntry.length-1; i++ ) {
      v = (Vector) v.get(i);
    }
    v.set(compressedEntry[i], compressedOutcome);
  }

  public Iterator compressedEntryIterator() {
    return new Partitioner(numPlayers, numStrategies);
  }

  public Iterator fullEntryIterator() {
    return new BaseNIterator(numStrategies, numPlayers);
  }

  public double[] getFullOutcome( int[] fullEntry ) {
    int[] compressedEntry = new int[numStrategies];
    for( int i=0; i<fullEntry.length; i++ ) {
      compressedEntry[fullEntry[i]]++;
    }
    double[] compressedOutcome = getCompressedOutcome(compressedEntry);
    double[] fullOutcome = new double[numPlayers];
    for( int i=0; i<numPlayers; i++ ) {
      fullOutcome[i] = compressedOutcome[fullEntry[i]];
    }
    return fullOutcome;
  }
  
  public double[] mixedStrategyPayoffs( double[] mixedStrategy ) {
    double totalProbability = 0;
    double[] payoffs = new double[numStrategies];
    double[] totalPayoffs = new double[numStrategies];
    int[] strategyCounts = new int[numStrategies];
    Iterator entries = fullEntryIterator();
    while ( entries.hasNext() ) {
      int[] entry = (int[]) entries.next();
      double[] outcome = getFullOutcome(entry);      
      double probability = 1;
      for( int i=0; i<entry.length; i++ ) {
        probability *= mixedStrategy[entry[i]];        
      }            
      for( int s=0; s<numStrategies; s++ ) {
        strategyCounts[s] = 0;
        totalPayoffs[s] = 0;
      }
      for( int p=0; p<outcome.length; p++ ) {
        int strategy = entry[p];
        totalPayoffs[strategy] += outcome[p];
        strategyCounts[strategy]++;
      }
      for( int i=0; i<payoffs.length; i++ ) {
        if ( strategyCounts[i] > 0 ) {
          payoffs[i] += (totalPayoffs[i] * probability) / strategyCounts[i];
        }
      }
      totalProbability += probability;
    }     
    return payoffs;
  }
  
  public double evolveMixedStrategy( double[] population ) {        
    double[] payoffs = mixedStrategyPayoffs(population);
    double averagePayoff = 0;
    double totalDelta = 0;
    for( int i=0; i<numStrategies; i++ ) {
      averagePayoff += payoffs[i] * population[i];
    }       
    for( int s=0; s<numStrategies; s++ ) {      
      double delta = population[s] * (payoffs[s] - averagePayoff);
      population[s] += delta;
      totalDelta += delta*delta;
    }        
    return totalDelta;
  }
  
  public double size( double[] population ) {
    double size = 0;
    for( int i=0; i<population.length; i++ ) {
      size += population[i];
    }
    return size;
  }
  
  public double[] plotRDflow( DataWriter out, double[] initialPopulation, 
                            double error, int maxIterations ) {
    double[] population = initialPopulation;
    double diff;
    int iteration = 0;
    do {
      evolveMixedStrategy(population);                 
      for( int i=0; i<population.length; i++ ) {
        out.newData(population[i]);
      }
      diff = evolveMixedStrategy(population);                 
      iteration++;      
    } while (  diff > error && iteration < maxIterations );
    return population;
  }
    
  
  public void importFromCSV( CSVReader in ) throws IOException {
    List record;
    while ( (record = in.nextRecord()) != null ) {
      Iterator fields = record.iterator();
      int entry[] = new int[numStrategies];
      for( int s=0; s<numStrategies; s++ ) {
        Integer n = (Integer) fields.next();
        entry[s] = n.intValue();
      }
      double[] outcome = getCompressedOutcome(entry);
      for( int s=0; s<numStrategies; s++ ) {
        Double payoff = (Double) fields.next();
        outcome[s] = payoff.doubleValue();
      }      
    }
  }
  
  public void export( DataWriter out ) {
    Iterator entries = compressedEntryIterator();
    while ( entries.hasNext() ) {
      int[] entry = (int[]) entries.next();
      for( int i=0; i<entry.length; i++ ) {
        out.newData(entry[i]);
      }
      double[] outcome = getCompressedOutcome(entry);
      for( int i=0; i<outcome.length; i++ ) {
        out.newData(outcome[i]);
      }
    }
  }  

  public void exportToGambit( PrintWriter nfgOut ) {
    exportToGambit(nfgOut, "JASA NFG");
  }

  public void exportToGambit( PrintWriter nfgOut, String title ) {

    nfgOut.print("NFG 1 R \"" + title + "\" { ");
    for( int i=0; i<numPlayers; i++ ) {
      nfgOut.print("\"Player" + (i+1) + "\" ");
    }
    nfgOut.println("}");
    nfgOut.println();

    nfgOut.print("{ ");
    for( int i=0; i<numPlayers; i++ ) {
      nfgOut.print("{ ");
      for ( int j=0; j<numStrategies; j++ ) {
        nfgOut.print("\"Strategy" + j + "\" ");
      }
      nfgOut.println("}");
    }
    nfgOut.println("}");

    nfgOut.println("\"\"");
    nfgOut.println();

    nfgOut.println("{");
    int numEntries = 0;
    Iterator entries = fullEntryIterator();
    while ( entries.hasNext() ) {
      nfgOut.print("{ \"");
      int[] fullEntry = (int[]) entries.next();
      for( int i=fullEntry.length-1; i>=0; i-- ) {
        nfgOut.print(fullEntry[i]+1);
      }
      nfgOut.print("\" ");
      double[] outcome = getFullOutcome(fullEntry);
      for( int i=0; i<outcome.length; i++ ) {
        nfgOut.print(outcome[i]);
        if ( i < outcome.length-1 ) {
          nfgOut.print(",");
        }
        nfgOut.print(" ");
      }
      nfgOut.println("}");
      numEntries++;
    }
    nfgOut.println("}");
    for( int i=1; i<=numEntries; i++ ) {
      nfgOut.print(i);
      if ( i < numEntries ) {
        nfgOut.print(" ");
      }
    }
    nfgOut.flush();
  }
  
  
  public static void main( String[] args ) {
    
    CompressedPayoffMatrix payoffMatrix = new CompressedPayoffMatrix(6, 3);
    
    edu.cornell.lassp.houle.RngPack.RandomElement prng = new edu.cornell.lassp.houle.RngPack.RanMT(1234);
    
    String filename = args[0];
    
    try {

      FileInputStream file = new FileInputStream(filename);  
      CSVReader csvIn = new CSVReader(file, new Class[] {Integer.class, Integer.class, Integer.class, Double.class, Double.class, Double.class} );
      EquilibriaDistribution ed = new EquilibriaDistribution();

      payoffMatrix.importFromCSV(csvIn);

      for( int i=0; i<200; i++ ) {
        double x, y;
        do {
          x = prng.uniform(0, 1);
          y = prng.uniform(0, 1);
        } while ( x+y > 1 );
          
        double z = 1 - x - y;

        uk.ac.liv.util.io.CSVWriter rdPlot = new uk.ac.liv.util.io.CSVWriter( new FileOutputStream("/tmp/rdplot" + i + ".csv"), 3);
        double[] equilibrium = 
          payoffMatrix.plotRDflow(rdPlot, new double[] {x, y, z}, 0.00001, 200000);
        rdPlot.close();
        
        ed.newEquilibrium(equilibrium);
      }
      
      ed.generateReport();
      
    } catch ( Exception e  ) {
      e.printStackTrace();
    }
  }
  
}


class EquilibriaDistribution {

  protected HashMap equilibria;
  
  protected double precision = 100;
  
  static Logger logger = Logger.getLogger(EquilibriaDistribution.class);
  
  public EquilibriaDistribution() {
    equilibria = new HashMap();  
  }
  
  public void newEquilibrium( double[] mixedStrategy ) {
    double[] equilibrium = round(mixedStrategy);
    MutableIntWrapper instances;
    instances = (MutableIntWrapper) equilibria.get(equilibrium);
    if ( instances == null ) {
      instances = new MutableIntWrapper(1);
      equilibria.put(equilibrium, instances);
    } else {
      instances.value++;
    }    
  }
  
  public double[] round( double[] mixedStrategy ) {
    double[] result = new double[mixedStrategy.length];
    for( int i=0; i<mixedStrategy.length; i++ ) {
      result[i] = Math.round(mixedStrategy[i]*precision) / precision;
    }
    return result;
  }
  
  public void generateReport() {
    Iterator i = equilibria.keySet().iterator();
    while ( i.hasNext() ) {
      double[] profile = (double[]) i.next();
      MutableIntWrapper instanceCount = (MutableIntWrapper) equilibria.get(profile);
      StringBuffer s = new StringBuffer();
      for( int j=0; j<profile.length; j++ ) {
        s.append(profile[j] + " ");
      }
      s.append(": " + instanceCount.value);
      logger.info(s.toString());
    }
  }
  
}
