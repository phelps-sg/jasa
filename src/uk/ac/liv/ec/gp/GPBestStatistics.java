package uk.ac.liv.ec.gp;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import java.io.*;

public class GPBestStatistics extends KozaStatistics {

  protected Individual best[];

  EvolutionState state;

  public void setup( final EvolutionState state, final Parameter base ) {
    super.setup(state, base);
    this.state = state;
  }

  public void print( String message ) {
    state.output.print(message, Output.V_NO_GENERAL, statisticslog);
  }

  public void println( String message ) {
    state.output.println(message, Output.V_NO_GENERAL, statisticslog);
  }

  public void println() {
    println("");
  }

  public void printIndividual( GPIndividual individual ) {
    individual.printIndividualForHumans(state, statisticslog, Output.V_NO_GENERAL);
  }


  public void writeObject( Object individual, String fileName ) {
    try {
      FileOutputStream file = new FileOutputStream(fileName);
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeObject(individual);
      out.close();
    } catch ( IOException e ) {
      System.err.println("Caught IO Exception: " + e);
      e.printStackTrace();
    }
  }

  public void findBestOfGeneration() {
    findBestOfGeneration(state.population.subpops.length);
  }

  public void findBestOfGeneration(int numSubpops ) {
    best = new Individual[state.population.subpops.length];
    for( int x=0; x<state.population.subpops.length; x++ ) {
      best[x] = state.population.subpops[x].individuals[0];
      for( int y=0; y<state.population.subpops[x].individuals.length; y++ ) {
        if ( state.population.subpops[x].individuals[y].fitness.betterThan(best[x].fitness) ) {
          best[x] = state.population.subpops[x].individuals[y];
        }
      }
    }
  }
}