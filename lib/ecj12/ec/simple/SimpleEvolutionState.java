package ec.simple;
import ec.*;
import ec.util.ParameterDatabase;
import ec.util.Parameter;
import ec.util.MersenneTwister;
import ec.util.Output;
import ec.util.Checkpoint;
import java.io.IOException;
import ec.util.Parameter;

/* 
 * SimpleEvolutionState.java
 * 
 * Created: Tue Aug 10 22:14:46 1999
 * By: Sean Luke
 */

/**
 * A SimpleEvolutionState is an EvolutionState which implements a simple form
 * of generational evolution.
 *
 * <p>First, all the individuals in the population are created.
 * <b>(A)</b>Then all individuals in the population are evaluated.
 * Then the population is replaced in its entirety with a new population
 * of individuals bred from the old population.  Goto <b>(A)</b>.
 *
 * <p>Evolution stops when an ideal individual is found (if quitOnRunComplete
 * is set to true), or when the number of generations (loops of <b>(A)</b>)
 * exceeds the parameter value numGenerations.  Each generation the system
 * will perform garbage collection and checkpointing, if the appropriate
 * parameters were set.
 *
 * <p>This approach can be readily used for
 * most applications of Genetic Algorithms and Genetic Programming.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class SimpleEvolutionState extends EvolutionState
    {
    public static final int DEBUG_INIT = 0;
    public static final int DEBUG_EVAL = 1;
    public static final int DEBUG_BREED = 2;
    public static final int DEBUG_FINAL_SUCCESS = 3;
    public static final int DEBUG_FINAL_FAILURE = 4;
    public static final int DEBUG_END = 5;
    public int debugState;  // only used for debugging using the go() method
    public int debugNum;  // only used for debugging using the go() method

    public SimpleEvolutionState() 
        { 
        debugState = DEBUG_INIT;  /* unneccessary */
        debugNum = 0;  /* also unneccessary */
        }

    public void run(int condition) throws IOException
        {

        if (condition == C_STARTED_FRESH)
            {
            output.message("Setting up");
            setup(this,null);  // a garbage Parameter

            output.message("Initializing Generation 0");
            statistics.preInitializationStatistics(this);
            population = initializer.initialPopulation(this);
            statistics.postInitializationStatistics(this);
            exchanger.initializeContacts(this);
            }
        else // condition == C_STARTED_FROM_CHECKPOINT
            {
            // the last thing we didn't do
            generation++;
            output.message("Generation" + generation);
            }

        /* the big loop */
        
        int result = R_SUCCESS;
        while ( true )
            {
            //output.message("Evaluate");
            statistics.preEvaluationStatistics(this);
            evaluator.evaluatePopulation(this);
            statistics.postEvaluationStatistics(this);
            if (evaluator.runComplete(this) && quitOnRunComplete)
                {
                output.message("Found Ideal Individual");
                break;
                }

            if (generation == numGenerations-1)
                {
                result = R_FAILURE;
                break;
                }

            //output.message("Pre-Exchange");
            statistics.prePreBreedingExchangeStatistics(this);
            population = exchanger.preBreedingExchangePopulation(this);
            statistics.postPreBreedingExchangeStatistics(this);

            String exchangerWantsToShutdown = exchanger.runComplete(this);
            if (exchangerWantsToShutdown!=null)
                { output.message(exchangerWantsToShutdown); break; }

            statistics.preBreedingStatistics(this);
            // We may perform garbage collection immediately before breeding
            // in the hopes that it makes nice large VM spaces for the new
            // individuals to fit into.
            if (gc && generation%gcModulo + 1 == gcModulo)
                {
                if (aggressivegc) aggressiveGC();
                else gc();
                }

            //output.message("Breed");
            try { population = breeder.breedPopulation(this);}
            catch (CloneNotSupportedException e) 
                { throw new InternalError(); } // never happens
            
            //output.message("Post-Exchange");
            statistics.postBreedingStatistics(this);
            statistics.prePostBreedingExchangeStatistics(this);
            population = exchanger.postBreedingExchangePopulation(this);
            statistics.postPostBreedingExchangeStatistics(this);

            if (checkpoint && generation%checkpointModulo +1 == checkpointModulo) 
                {
                output.message("Checkpointing");
                statistics.preCheckpointStatistics(this);
                Checkpoint.setCheckpoint(this);
                statistics.postCheckpointStatistics(this);
                }

            generation++;
            output.message("Generation " + generation);
            }


        //Output.message("Finishing");
        /* finish up -- we completed. */
        statistics.finalStatistics(this,result);
        finisher.finishPopulation(this,result);
        exchanger.closeContacts(this,result);
        }



    
    public void go()
        {
        if (debugState==DEBUG_INIT)
            {
            output.message("" + (debugNum++) + ") DEBUG: INIT" + generation);
            
            output.message("Setting up");
            setup(this,null);  // a garbage Parameter
            
            output.message("Initializing Generation 0");
            exchanger.initializeContacts(this);
            statistics.preInitializationStatistics(this);
            population = initializer.initialPopulation(this);
            statistics.postInitializationStatistics(this);
            debugState=DEBUG_EVAL;
            }
        else if (debugState==DEBUG_EVAL)
            {
            output.message("" + (debugNum++) + ") DEBUG: EVAL, Generation " + generation);
            statistics.preEvaluationStatistics(this);
            evaluator.evaluatePopulation(this);
            statistics.postEvaluationStatistics(this);
            if (evaluator.runComplete(this) && quitOnRunComplete)
                {
                output.message("Found Ideal Individual");
                debugState=DEBUG_FINAL_SUCCESS;
                return;
                }
            if (generation == numGenerations-1)
                {
                debugState=DEBUG_FINAL_FAILURE;
                return;
                }
            debugState=DEBUG_BREED;
            }
        else if (debugState==DEBUG_BREED)
            {
            output.message("" + (debugNum++) + ") DEBUG: BREED, Generation " + generation);
            statistics.prePreBreedingExchangeStatistics(this);
            population = exchanger.preBreedingExchangePopulation(this);
            statistics.postPreBreedingExchangeStatistics(this);
            String exchangerWantsToShutdown = exchanger.runComplete(this);
            if (exchangerWantsToShutdown!=null)
                {
                output.message(exchangerWantsToShutdown); 
                debugState=DEBUG_FINAL_FAILURE;
                return;
                }

            statistics.preBreedingStatistics(this);
            // We may perform garbage collection immediately before breeding
            // in the hopes that it makes nice large VM spaces for the new
            // individuals to fit into.
            if (gc && generation%gcModulo + 1 == gcModulo)
                {
                if (aggressivegc) aggressiveGC();
                else gc();
                }
            
            try { population = breeder.breedPopulation(this);}
            catch (CloneNotSupportedException e) 
                { throw new InternalError(); } // never happens
            
            statistics.postBreedingStatistics(this);
            statistics.prePostBreedingExchangeStatistics(this);
            population = exchanger.postBreedingExchangePopulation(this);
            statistics.postPostBreedingExchangeStatistics(this);

            if (checkpoint && generation%checkpointModulo +1 == checkpointModulo) 
                {
                output.message("Checkpointing");
                statistics.preCheckpointStatistics(this);
                Checkpoint.setCheckpoint(this);
                statistics.postCheckpointStatistics(this);
                }

            generation++;
            output.message("Generation " + generation);
            debugState=DEBUG_EVAL;
            }
        else if (debugState==DEBUG_FINAL_SUCCESS || 
                 debugState==DEBUG_FINAL_FAILURE)
            {
            output.message("" + (debugNum++) + ") DEBUG: FINAL.  Cleaning up.");
            int result = (debugState==DEBUG_FINAL_SUCCESS ?
                          R_SUCCESS : R_FAILURE);
            statistics.finalStatistics(this,result);
            finisher.finishPopulation(this,result);
            exchanger.closeContacts(this,result);
            output.flush();
            debugState=DEBUG_END;
            }
        else // debugState==DEBUG_END
            {
            output.message((debugNum++) + ") DEBUG: END.  Nothing more to do.");
            }
        }
    }
