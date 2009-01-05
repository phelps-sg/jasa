package ec.es;
import ec.*;
import ec.simple.*;
import java.io.*;
import ec.util.*;

/* 
 * ESSelection.java
 * 
 * Created: Thu Sep  7 19:08:19 2000
 * By: Sean Luke
 */

/**
 * ESSelection is a special SelectionMethod designed to be used with 
 * evolutionary strategies (ESEvolutionState) and its associated breeders.
 * The rule is simple: if your pipeline returns <i>N</i> children when
 * you called it, ESSelection objects must have been called exactly
 * <i>N</i> times (selecting <i>N</i> children altogether in that pass).
 * No more, no less.  You can use other selection methods (Tournament
 * Selection is a good choice) to fill the slack.
 * See ESEvolutionState for ideas on doing this with various
 * breeding pipeline examples.
 *

 <p><b>Default Base</b><br>
 es.select

 * @author Sean Luke
 * @version 1.0 
 */

public class ESSelection extends SelectionMethod 
    {
    public static final String P_ESSELECT = "select";

    public Parameter defaultBase()
        {
        return ESDefaults.base().push(P_ESSELECT);
        }

    /** Returns 1 */
    public int typicalIndsProduced()
        { return 1; }

    /** An alternative form of "produce" special to Selection Methods;
        selects an individual from the given subpopulation and 
        returns its position in that subpopulation. */
    public int produce(final int subpopulation,
                       final EvolutionState state,
                       final int thread)
        {
        ESEvolutionState ess;
        if (!(state instanceof ESEvolutionState))
            state.output.fatal("ESSelection was handed an EvolutionState that's not an ESEvolutionState.");
        ess = (ESEvolutionState) state;
        
        // determine my position in the array
        int pos = (ess.lambda[subpopulation] % ess.breedthreads == 0 ? 
                   ess.lambda[subpopulation]/ess.breedthreads :
                   ess.lambda[subpopulation]/ess.breedthreads + 1) * 
            thread + ess.count[thread];  // note integer division
        
        // determine the parent
        int parent = pos / ess.mu[subpopulation]; // note integer division

        // increment our count
        ess.count[thread]++;

        return parent;
        }


    /** A default version of produces -- this method always returns
        true under the assumption that the selection method works
        with all Fitnesses.  If this isn't the case, you should override
        this to return your own assessment. */
    public boolean produces(final EvolutionState state,
                            final Population newpop,
                            final int subpopulation,
                            final int thread)
        {
        return true;
        }


    /** A default version of prepareToProduce which does nothing.  */
    public void prepareToProduce(final EvolutionState s,
                                 final int subpopulation,
                                 final int thread)
        { return; }

    /** A default version of finishProducing, which does nothing. */
    public void finishProducing(final EvolutionState s,
                                final int subpopulation,
                                final int thread)
        { return; }

    public int produce(final int min, 
                       final int max, 
                       final int start,
                       final int subpopulation,
                       final Individual[] inds,
                       final EvolutionState state,
                       final int thread) throws CloneNotSupportedException
        {
        ESEvolutionState ess;
        if (min>1) // uh oh
            state.output.fatal("ESSelection used, but it's being asked to produce more than one individual.");
        if (!(state instanceof ESEvolutionState))
            state.output.fatal("ESSelection was handed an EvolutionState that's not an ESEvolutionState.");
        ess = (ESEvolutionState) state;
        
        // determine my position in the array
        int pos = (ess.lambda[subpopulation] % ess.breedthreads == 0 ? 
                   ess.lambda[subpopulation]/ess.breedthreads :
                   ess.lambda[subpopulation]/ess.breedthreads + 1) * 
            thread + ess.count[thread];  // note integer division
        
        // determine the parent
        int parent = pos / (ess.lambda[subpopulation] / ess.mu[subpopulation]); // note outer integer division

        // increment our count
        ess.count[thread]++;

        // and so we return the parent
        inds[start] = state.population.subpops[subpopulation].individuals[parent];

        //System.out.println("Parent for pos " + pos + " is " + 
        //    state.population.subpops[subpopulation].individuals[parent] + "at position" + parent);
        //state.population.subpops[subpopulation].individuals[parent].printIndividualForHumans(state,0,3000);

        // and so we return the parent
        return 1;
        }
    
    public void preparePipeline(Object hook) 
        {
        // default does nothing
        }
    }
