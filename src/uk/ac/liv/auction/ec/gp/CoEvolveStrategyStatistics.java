package uk.ac.liv.auction.ec.gp;

import java.io.*;

import java.util.*;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.auction.electricity.ElectricityTrader;
import uk.ac.liv.auction.ec.gp.func.*;
import uk.ac.liv.ec.gp.GPBestStatistics;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class CoEvolveStrategyStatistics extends GPBestStatistics {


  public void postEvaluationStatistics( EvolutionState state ) {
    println("Strategies for Generation " + state.generation);
    println("<--------------------------->");
    println();
    findBestOfGeneration();
    for( int i=0; i<state.population.subpops.length; i++ ) {
      if ( best[i] instanceof GPTradingStrategy ) {
        GPTradingStrategy strategy = (GPTradingStrategy) best[i];
        println("Best strategy of subpopulation " + i);
        printIndividual(strategy);
        println();
        println("Price stats:");
        println(strategy.getPriceStats().toString());
        println();
        println("Misbehaved? " + strategy.misbehaved());
        println();
        print("type: ");
        if (strategy.getAgent().isSeller()) {
          println("seller");
        } else {
          println("buyer");
        }
        println();
        println("--------");
        println();
      }
    }
  }

}