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

import java.io.*;

import uk.ac.liv.util.MathUtil;
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

  public double[] getCompressedPayoffs( Entry entry ) {
    Vector v = matrix;
    int strategy;
    for( strategy=0; strategy<numStrategies-1; strategy++ ) {
      v = (Vector) v.get(entry.getNumAgents(strategy));
    }
    return (double[]) v.get(entry.getNumAgents(strategy));
  }

  public void setCompressedPayoffs( Entry entry,
                                      double[] compressedPayoffs ) {
    Vector v = matrix;
    int strategy;
    for( strategy=0; strategy<numStrategies-1; strategy++ ) {
      v = (Vector) v.get(entry.getNumAgents(strategy));
    }    
    v.set(entry.getNumAgents(strategy), compressedPayoffs);
  }

  public Iterator compressedEntryIterator() {
    return new Iterator() {
      
      Partitioner p = new Partitioner(numPlayers, numStrategies);
      
      public boolean hasNext() {
        return p.hasNext();
      }
      
      public Object next() {
        return new Entry((int[])p.next());
      }
      
      public void remove() {        
      }
            
    };    
  }

  public Iterator fullEntryIterator() {
    final CompressedPayoffMatrix matrix = this;
    return new Iterator() {
      
      BaseNIterator b = new BaseNIterator(numStrategies, numPlayers);
    
      public boolean hasNext() {
        return b.hasNext();        
      }
      
      public Object next() {
        return new FullEntry((int[])b.next(), matrix);               
      }
      
      public void remove() {        
      }
      
    };
  }

  public double[] getFullPayoffs( FullEntry entry ) {    
    Entry compressedEntry = entry.compress();
    double[] compressedPayoffs = getCompressedPayoffs(compressedEntry);
    double[] fullPayoffs = new double[numPlayers];
    for( int i=0; i<numPlayers; i++ ) {
      fullPayoffs[i] = compressedPayoffs[entry.getStrategy(i)];
    }
    return fullPayoffs;
  }
  
  public double payoff( double[] mixedStrategy ) {
    return payoff(false, -1, mixedStrategy);
  }
  
  public double payoff( int strategy, double[] population ) {
    return payoff(true, strategy, population);
  }
  
  public double payoff( boolean pure, int strategy, double[] mixedStrategy ) {
    logger.debug("\nCalculating payoff to " + strategy + "...\n");
    assert MathUtil.approxEqual(MathUtil.sum(mixedStrategy), 1); 
    double payoff = 0;
    Iterator entries = compressedEntryIterator();
    iterating: while ( entries.hasNext() ) {      
      Entry entry = (Entry) entries.next();
      logger.debug(entry);
      double[] payoffs = getCompressedPayoffs(entry);                       
      if ( pure ) {
        if ( entry.getNumAgents(strategy) == 0 ) {
          logger.debug("- NA");
          continue iterating;
        }
        entry = entry.removeSingleAgent(strategy);        
      }
      double probability = 1;
      for( int s=0; s<numStrategies; s++ ) {
        probability *= Math.pow(mixedStrategy[s], entry.getNumAgents(s));
      }      
      assert probability <= 1;
      if ( pure ) {
        payoff += probability * payoffs[strategy];
      } else {
        payoff += probability * MathUtil.sum(payoffs);
      }
      logger.debug(" probability = " + probability);
      for( int s=0; s<numStrategies; s++ ) {
        logger.debug("  payoff(" + s + ") = " + payoffs[s]);
      }
    }    
    return payoff;
  }
  

  public double evolveMixedStrategy( double[] population ) {       
    double totalDelta = 0;
    double[] delta = new double[numStrategies];
    double[] payoffs = new double[numStrategies];
    double totalPayoff = 0;
    for( int s=0; s<numStrategies; s++ ) {
      double payoff = payoff(s, population);
      payoffs[s] = payoff;
      logger.debug("Payoff to " + s +" = " + payoff);
      totalPayoff += payoff;
    }
    double averagePayoff = payoff(population);
    logger.debug("Average payoff is " + averagePayoff);
    for( int s=0; s<numStrategies; s++ ) {
      delta[s] = 0.1 * population[s] * (payoffs[s] - averagePayoff);      
      totalDelta += delta[s]*delta[s];
    }
    for( int s=0; s<numStrategies; s++ ) {
      population[s] += delta[s];
    }
    return totalDelta;
  }

  public double[] plotRDflow( double[] initialPopulation, double error, int maxIterations ) {
    return plotRDflow(null, initialPopulation, error, maxIterations);
  }
  
  public double[] plotRDflow( DataWriter out, double[] initialPopulation,
                              double error, int maxIterations ) {
    double[] population = initialPopulation;
    double diff;
    int iteration = 0;
    do {      
      if ( out != null ) {
        for( int i=0; i<population.length; i++ ) {
          out.newData(population[i]);
        }
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
      int numAgentsPerStrategy[] = new int[numStrategies];
      for( int s=0; s<numStrategies; s++ ) {
        Integer n = (Integer) fields.next();
        numAgentsPerStrategy[s] = n.intValue();
      }
      Entry entry = new Entry(numAgentsPerStrategy);
      double[] outcome = getCompressedPayoffs(entry);
      for( int s=0; s<numStrategies; s++ ) {
        Double payoff = (Double) fields.next();
        outcome[s] = payoff.doubleValue();
      }
    }
  }

  public void export( DataWriter out ) {
    Iterator entries = compressedEntryIterator();
    while ( entries.hasNext() ) {
      Entry entry = (Entry) entries.next();
      for( int s=0; s<numStrategies; s++ ) {
        out.newData(entry.getNumAgents(s));
      }
      double[] payoffs = getCompressedPayoffs(entry);
      for( int i=0; i<payoffs.length; i++ ) {
        out.newData(payoffs[i]);
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
      FullEntry fullEntry = (FullEntry) entries.next();
      for( int i=numPlayers-1; i>=0; i-- ) {
        nfgOut.print(fullEntry.getStrategy(i)+1);
      }
      nfgOut.print("\" ");
      double[] outcome = getFullPayoffs(fullEntry);
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
  
  public int getNumStrategies() {
    return numStrategies;    
  }
  
  public int getNumPlayers() {
    return numPlayers;
  }


  public static class Entry implements Cloneable {
  
    protected int[] numAgentsPerStrategy;
    
    public Entry( int[] numAgentsPerStrategy ) {
      this.numAgentsPerStrategy = numAgentsPerStrategy;
    }
    
    public int getNumAgents( int strategy ) {
      return numAgentsPerStrategy[strategy];
    }
    
    public Object clone() throws CloneNotSupportedException {      
      Entry newEntry = (Entry) super.clone();
      newEntry.numAgentsPerStrategy = (int[]) this.numAgentsPerStrategy.clone();
      return newEntry;    
    }
    
    public Entry removeSingleAgent( int strategy ) {
      try {
        Entry entry = (Entry) clone();
        if ( numAgentsPerStrategy[strategy] > 0 ) {
          entry.numAgentsPerStrategy[strategy]--;
        }
        return entry;
      } catch ( CloneNotSupportedException e ) {
        throw new Error(e);
      }
    }
    
    public String toString() {
      StringBuffer result = new StringBuffer("");      
      int numStrategies = numAgentsPerStrategy.length;
      for( int i=0; i<numStrategies-1; i++ ) {
        result.append(numAgentsPerStrategy[i] + "/");
      }
      result.append(numAgentsPerStrategy[numStrategies-1]);
      return result.toString();
    }
    
  }
  
  public static class FullEntry {
    
    protected int[] pureStrategyProfile;
    
    protected CompressedPayoffMatrix matrix;
    
    public FullEntry( int[] pureStrategyProfile, CompressedPayoffMatrix matrix ) {
      this.pureStrategyProfile = pureStrategyProfile;
      this.matrix = matrix;
    }
    
    public int getStrategy( int player ) {
      return pureStrategyProfile[player];
    }
    
    public Entry compress() {
      int[] numAgentsPerStrategy = new int[matrix.getNumStrategies()];
      for( int i=0; i<matrix.getNumPlayers(); i++ ) {
        numAgentsPerStrategy[getStrategy(i)]++;
      }
      return new Entry(numAgentsPerStrategy);      
    }
    
  }
  
}


