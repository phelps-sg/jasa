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
    int nonTT = 0;
    for( int i=0; i<state.population.subpops.length; i++ ) {
    	for( int j=0; j<state.population.subpops[i].individuals.length; j++ ) {    		
      	GPGenericIndividual individual = (GPGenericIndividual) state.population.subpops[i].individuals[j];
      	if ( individual.getGPObject() instanceof GPTradingStrategy ) {
	        GPTradingStrategy strategy = (GPTradingStrategy) individual.getGPObject();
	        println("--------");
	        println("Stats for individual " + j);	        
	        println(strategy.getPriceStats().toString());	        	       	        
	        printIndividual(strategy.getGPIndividual());	 
	        System.out.println("Scheme: " + strategy.getGPIndividual().toScheme());	        
	        if ( strategy.getPriceStats().getMean() != 0 ) {
	        	nonTT++;	        		        
	        }
	        println("--------");
	        println();
      	}
      }
    	
    	println("Summary for generation " + state.generation + ": " + nonTT + " non truth-tellers");
    }
  }

}