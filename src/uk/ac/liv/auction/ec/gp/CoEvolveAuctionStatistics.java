package uk.ac.liv.auction.ec.gp;

import java.io.*;

import ec.*;
import ec.gp.koza.*;
import ec.util.Parameter;


/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class CoEvolveAuctionStatistics extends KozaStatistics {

  static final String P_PREFIX = "serfilenameprefix";
  static final String DEFAULT_PREFIX = "/tmp/auction";

  EvolutionState state;

  protected Individual best[];

  String fileNamePrefix;

  public void setup( final EvolutionState state, final Parameter base ) {
    super.setup(state, base);
    fileNamePrefix = state.parameters.getStringWithDefault(base.push(P_PREFIX), DEFAULT_PREFIX);
  }

  public void finalStatistics( final EvolutionState state, final int result ) {
    super.finalStatistics(state, result);
    this.state = state;
    findBestOfGeneration();
    for( int i=0; i<state.population.subpops.length; i++ ) {
      System.out.println("Serializing " + best[i]);
      writeIndividual(best[i], fileNamePrefix + "." + i);
    }
  }

  public void writeIndividual( Individual individual, String fileName ) {
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