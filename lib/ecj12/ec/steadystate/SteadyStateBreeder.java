package ec.steadystate;
import ec.simple.*;
import ec.*;
import ec.util.*;

/* 
 * SteadyStateBreeder.java
 * 
 * Created: Tue Aug 10 21:00:11 1999
 * By: Sean Luke
 */

/**
 * A SteadyStateBreeder is an extension of SimpleBreeder which works in conjunction
 * with SteadyStateEvolutionState to breed individuals using a steady-state breeding
 * method.
 *
 * <p>SteadyStateBreeder marks 1 individual for death in each
 * subpopulation.  It then replaces those individuals in a subpopulation
 * with new individuals bred from the rest of the subpopulation.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class SteadyStateBreeder extends SimpleBreeder
    {
    /** If st.firstTimeAround, this acts exactly like SimpleBreeder.
        Else, it only breeds one new individual per subpopulation, to 
        place in position 0 of the subpopulation.  
    */
    BreedingPipeline[] bp;
    
    /** Loaded during the first iteration of breedPopulation */
    SelectionMethod deselectors[]; 
    
    public SteadyStateBreeder() { bp = null; deselectors = null; }


    /** Called to check to see if the breeding sources are correct -- if you
        use this method, you must call state.output.exitIfErrors() immediately 
        afterwards. */
    public void sourcesAreProperForm(final SteadyStateEvolutionState state,
                                     final BreedingPipeline[] breedingPipelines)
        {
        for(int x=0;x<breedingPipelines.length;x++)
            {
            if (!(breedingPipelines[x] instanceof SteadyStateBSourceForm))
                state.output.error("Breeding Pipeline of subpopulation " + x + " is not of SteadyStateBSourceForm");
            ((SteadyStateBSourceForm)(breedingPipelines[x])).sourcesAreProperForm(state);
            }
        // double check for the population too...
        deselectors = new SelectionMethod[state.population.subpops.length];
        
        SelectionMethod d;
        for(int x=0;x<state.population.subpops.length;x++)
            if (!((( d= ((SteadyStateSpeciesForm)(state.population.subpops[x].species)).deselector())
                   instanceof SteadyStateBSourceForm)))
                state.output.error("Deselector for subpopulation " + x + " is not of SteadyStateBSourceForm.");
            else 
                deselectors[x] = d;
        }
    
    /** Called whenever individuals have been replaced by new
        individuals in the population. */
    public void individualReplaced(final SteadyStateEvolutionState state,
                                   final int subpopulation,
                                   final int thread,
                                   final int individual)
        {
        for(int x=0;x<bp.length;x++)
            ((SteadyStateBSourceForm)bp[x]).
                individualReplaced(state,subpopulation,thread,individual);
        // let the deselector know
        ((SteadyStateBSourceForm)((SteadyStateSpeciesForm)
                                  (state.population.subpops[subpopulation].species)).deselector()).
            individualReplaced(state,subpopulation,thread,individual);
        }


    public Population breedPopulation(EvolutionState state) throws CloneNotSupportedException
        {
        final SteadyStateEvolutionState st = (SteadyStateEvolutionState) state;


        if (st.firstTimeAround) // first time
            {
            
            super.breedPopulation(st);
            
            // Load my steady-state breeding pipelines
            
            if (bp == null)
                {
                // set up the breeding pipelines
                bp = new BreedingPipeline[st.population.subpops.length];
                for(int pop=0;pop<bp.length;pop++)
                    {
                    bp[pop] = (BreedingPipeline)st.population.subpops[pop].species.
                        pipe_prototype.protoCloneSimple();
                    if (!bp[pop].produces(st,st.population,pop,0))
                        st.output.error("The Breeding Pipeline of subpopulation " + pop + " does not produce individuals of the expected species " + st.population.subpops[pop].species.getClass().getName() + " and with the expected Fitness class " + st.population.subpops[pop].f_prototype.getClass().getName());
                    }
                // are they of the proper form?
                sourcesAreProperForm(st,bp);
                // because I promised when calling sourcesAreProperForm
                st.output.exitIfErrors();
                
                // warm them up
                for(int pop=0;pop<bp.length;pop++)
                    {
                    bp[pop].prepareToProduce(state,pop,0);
                    deselectors[pop].prepareToProduce(state,pop,0);
                    }
                }
            }

        // yes, yes, this is after creating bp, so it's less efficient,
        // but safer because the sourcesAreProperForm() check is done before this


        // mark individuals for death

        for (int pop = 0; pop<st.population.subpops.length;pop++)
            {
            //SelectionMethod deselector = 
            //  ((SteadyStateSpeciesForm)(st.population.subpops[pop].species)).deselector();
            st.newIndividuals[pop] = deselectors[pop].produce(pop,st,0);
            }

        // create new individuals

        for(int pop=0;pop<st.population.subpops.length;pop++)
            bp[pop].produce(1,1,st.newIndividuals[pop],pop,
                            st.population.subpops[pop].individuals,st,0);
        
        return st.population;
        }
    
    public void finishPipelines(EvolutionState state)
        {
        for(int x = 0 ; x < bp.length; x++)
            {
            bp[x].finishProducing(state,x,0);
            deselectors[x].finishProducing(state,x,0);
            }
        }
    }
