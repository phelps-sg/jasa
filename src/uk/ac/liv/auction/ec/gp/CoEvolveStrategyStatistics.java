package uk.ac.liv.auction.ec.gp;

import ec.*;

import uk.ac.liv.auction.ec.gp.func.*;
import uk.ac.liv.ec.gp.*;

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
      if ( best[i] instanceof GPGenericIndividual ) {
      	GPGenericIndividual individual = (GPGenericIndividual) best[i];
      	if ( individual.getGPObject() instanceof GPTradingStrategy ) {
	        GPTradingStrategy strategy = (GPTradingStrategy) individual.getGPObject();
	        println("Best strategy of subpopulation " + i);
	        printIndividual(individual);
	        println();
	        println("Price stats:");
	        println(strategy.getPriceStats().toString());
	        println();
	        println("Misbehaved? " + individual.misbehaved());
	        println();
	        print("type: ");
	        //if (strategy.getAgent().isSeller()) {
	//          println("seller");
	        //} else {
	//          println("buyer");
	        //}
	        println();
	        println("--------");
	        println();
      	}
      }
    }
  }

}