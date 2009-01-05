package ec.es;
import ec.*;
import ec.simple.*;
import java.io.*;
import ec.util.*;

/* 
 * MuCommaLambdaBreeder.java
 * 
 * Created: Thu Sep  7 17:27:47 2000
 * By: Sean Luke
 */

/**
 * MuCommaLambdaBreeder is a Breeder which, together with ESEvolutionState
 * and ESSelection, implements the (mu,lambda) breeding strategy and gathers
 * the comparison data you can use to implement a 1/5-rule mutation mechanism.
 * 
 * For more information, see the ESEvolutionState class documentation.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class MuCommaLambdaBreeder extends Breeder implements ESBreederForm
    {    
    public void setup(final EvolutionState state, final Parameter base) { }

    /** Sets all subpopulations in pop to the expected lambda size.  Does not fill new slots with individuals. */
    public Population setToLambda(Population pop, ESEvolutionState state)
        {
        for(int x=0;x<pop.subpops.length;x++)
            {
            int s = state.lambda[x];
            
            // check to see if the array's not the right size
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
        
    public Population breedPopulation(EvolutionState state) throws CloneNotSupportedException
        {
        int numinds[][] = 
            new int[state.breedthreads][state.population.subpops.length];
        int from[][] = 
            new int[state.breedthreads][state.population.subpops.length];
            
        // sort evaluation to get the Mu best of each subpopulation
        
        for(int x=0;x<state.population.subpops.length;x++)
            {
            final Individual[] i = state.population.subpops[x].individuals;
            QuickSort.qsort(i,
                            new SortComparator()
                                {
                                // gt implies that object a should appear after object b in the sorted array.
                                // we want this to be the case if object a has WORSE fitness
                                public boolean gt(Object a, Object b)
                                    {
                                    return ((Individual)b).fitness.betterThan(
                                        ((Individual)a).fitness);
                                    }
                                // gt implies that object a should appear before object b in the sorted array
                                // we want this to be the case if object a has BETTER fitness
                                public boolean lt(Object a, Object b)
                                    {
                                    return ((Individual)a).fitness.betterThan(
                                        ((Individual)b).fitness);
                                    }
                                });
            }

        // now the subpops are sorted so that the best individuals
        // appear in the lowest indexes.

        ESEvolutionState ess = (ESEvolutionState)state;
        Population newpop = setToLambda((Population) state.population.emptyClone(),ess);

        // create the count array
        ess.count = new int[ess.breedthreads];

        // divvy up the lambda individuals to create

        for(int y=0;y<state.breedthreads;y++)
            for(int x=0;x<state.population.subpops.length;x++)
                {
                // figure numinds
                if (y<state.breedthreads-1) // not last one
                    numinds[y][x]=
                        ess.lambda[x]/state.breedthreads;
                else // in case we're slightly off in division
                    numinds[y][x]=
                        ess.lambda[x]/state.breedthreads +
                        (ess.lambda[x] - (ess.lambda[x] / state.breedthreads)  // note integer division
                         *state.breedthreads);                   
                
                // figure from
                from[y][x]=
                    (ess.lambda[x]/
                     state.breedthreads) * y;
                }
            
        int[][] bettercount= new int[state.breedthreads][state.population.subpops.length];

        if (state.breedthreads==1)
            {
            breedPopChunk(newpop,ess,bettercount,numinds[0],from[0],0);
            }
        else
            {
            Thread[] t = new Thread[state.breedthreads];
                
            // start up the threads
            for(int y=0;y<state.breedthreads;y++)
                {
                MuLambdaBreederThread r = new MuLambdaBreederThread();
                r.threadnum = y;
                r.newpop = newpop;
                r.numinds = numinds[y];
                r.from = from[y];
                r.me = this;
                r.state = ess;
                t[y] = new Thread(r);
                t[y].start();
                }
                
            // gather the threads
            for(int y=0;y<state.breedthreads;y++) try
                {
                t[y].join();
                }
            catch(InterruptedException e)
                {
                state.output.fatal("Whoa! The main breeding thread got interrupted!  Dying...");
                }
            }

        // determine our comparisons
        for(int x=0;x<ess.population.subpops.length;x++)
            {
            int total = 0;
            for(int y=0;y<state.breedthreads;y++)
                total += bettercount[y][x];
            if (((double)total)/state.population.subpops[x].individuals.length > 0.2)
                ess.comparison[x] = ess.C_OVER_ONE_FIFTH_BETTER;
            else if (((double)total)/state.population.subpops[x].individuals.length < 0.2)
                ess.comparison[x] = ess.C_UNDER_ONE_FIFTH_BETTER;
            else ess.comparison[x] = ess.C_EXACTLY_ONE_FIFTH_BETTER;
            }
        return postProcess(newpop,state.population,ess);
        }

    /** A hook for Mu+Lambda, not used in Mu,Lambda */

    public Population postProcess(Population newpop, Population oldpop, ESEvolutionState state)
        {
        return newpop;
        }
    
    
    public boolean childBetter(Population newpop,
                               int subpopulation, int thread, ESEvolutionState ess)
        {
        // determine my position in the array
        int pos = (ess.lambda[subpopulation] % ess.breedthreads == 0 ? 
                   ess.lambda[subpopulation]/ess.breedthreads :     // note integer division
                   ess.lambda[subpopulation]/ess.breedthreads + 1) * 
            thread + ess.count[thread]-1; // -1 because we've already advanced one
        
        // determine the parent
        int parent = pos / (ess.lambda[subpopulation] / ess.mu[subpopulation]); // note outer integer division
        
        // is the child better than the parent?
        return newpop.subpops[subpopulation].individuals[pos].fitness.betterThan(
            ess.population.subpops[subpopulation].individuals[parent].fitness);
        }
    
    
    /** A private helper function for breedPopulation which breeds a chunk
        of individuals in a subpopulation for a given thread.
        Although this method is declared
        public (for the benefit of a private helper class in this file),
        you should not call it. */
    
    public void breedPopChunk(Population newpop, ESEvolutionState state, 
                              int[][] bettercount,
                              int[] numinds, int[] from, int threadnum) throws CloneNotSupportedException
        {
        // reset the appropriate count slot
        state.count[threadnum]=0;
        
        for(int subpop=0;subpop<newpop.subpops.length;subpop++)
            {
            BreedingPipeline bp = (BreedingPipeline) newpop.subpops[subpop].
                species.pipe_prototype.protoCloneSimple();
            
            // check to make sure that the breeding pipeline produces
            // the right kind of individuals.  Don't want a mistake there! :-)
            if (!bp.produces(state,newpop,subpop,threadnum))
                state.output.fatal("The Breeding Pipeline of subpopulation " + subpop + " does not produce individuals of the expected species " + newpop.subpops[subpop].species.getClass().getName() + " or fitness " + newpop.subpops[subpop].f_prototype );
            bp.prepareToProduce(state,subpop,threadnum);
            
            
            // start breedin'!
            
            int upperbound = from[subpop]+numinds[subpop];
            for(int x=from[subpop];x<upperbound;x++)
                {
                int prevcount = state.count[threadnum];
                if (bp.produce(1,1,x,subpop, newpop.subpops[subpop].individuals,
                               state,threadnum) != 1)
                    state.output.fatal("Whoa! Breeding Pipeline for subpop " + subpop + " is not producing one individual at a time, as is required by the MuLambda strategies.");
                if (state.count[threadnum]-prevcount != 1)
                    state.output.fatal("Whoa!  Breeding Pipeline for subpop " + subpop + " used an ESSelector more or less than exactly once.  Number of times used: " + (state.count[threadnum]-prevcount));
                if (childBetter(newpop,subpop,threadnum,state))
                    bettercount[threadnum][subpop]++;
                }
            bp.finishProducing(state,subpop,threadnum);
            }
        }
    }


/** A private helper class for implementing multithreaded breeding */
class MuLambdaBreederThread implements Runnable
    {
    Population newpop;
    public int[][] bettercount;
    public int[] numinds;
    public int[] from;
    public MuCommaLambdaBreeder me;
    public ESEvolutionState state;
    public int threadnum;
    public void run()
        {
        try { me.breedPopChunk(newpop,state,bettercount,numinds,from,threadnum); }
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        }
    }


