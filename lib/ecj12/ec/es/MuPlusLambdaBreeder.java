package ec.es;
import ec.*;
import ec.simple.*;
import java.io.*;
import ec.util.*;

/* 
 * MuPlusLambdaBreeder.java
 * 
 * Created: Thu Sep  7 18:49:42 2000
 * By: Sean Luke
 */

/**
 * MuPlusLambdaBreeder is a Breeder which, together with ESEvolutionState
 * and ESSelection, implements the (mu + lambda) breeding strategy and gathers
 * the comparison data you can use to implement a 1/5-rule mutation mechanism.
 * Note that MuPlusLambdaBreeder increases subpopulation sizes by their mu
 * values in the second generation and keep them at that size thereafter.
 *
 * For more information, see the ESEvolutionState class documentation.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class MuPlusLambdaBreeder extends MuCommaLambdaBreeder
    {
    /** Sets all subpopulations in pop to the expected mu+lambda size.  Does not fill new slots with individuals. */
    public Population setToMuPlusLambda(Population pop, ESEvolutionState state)
        {
        for(int x=0;x<pop.subpops.length;x++)
            {
            int s = state.mu[x]+state.lambda[x];
            
            // check to see if the array's big enough
            if (pop.subpops[x].individuals.length != s)
                // need to increase
                {
                Individual[] newinds = new Individual[s];
                System.arraycopy(pop.subpops[x].individuals,0,newinds,0,
                                 s < pop.subpops[x].individuals.length ? 
                                 s : pop.subpops[x].individuals.length);
                pop.subpops[x].individuals = newinds;
                }
            }
        return pop;
        }

    public Population postProcess(Population newpop, Population oldpop, ESEvolutionState state)
        {
        // first we need to expand newpop to mu+lambda in size
        newpop = setToMuPlusLambda(newpop,state);
        
        // now we need to dump the old population into the high end of the new population
         
        for(int x=0;x<newpop.subpops.length;x++)
            {
            for(int y=0;y<state.mu[x];y++)
                {
                newpop.subpops[x].individuals[y+state.lambda[x]] =
                    oldpop.subpops[x].individuals[y].deepClone();
                // Used to be non-cloned, namely:
                // System.arraycopy(oldpop.subpops[x].individuals,0,
                //            newpop.subpops[x].individuals,state.lambda[x],
                //           state.mu[x]);
                }
            }
        return newpop;
        }
    }
