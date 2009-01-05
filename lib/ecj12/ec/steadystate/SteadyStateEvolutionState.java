package ec.steadystate;
import ec.*;
import ec.util.ParameterDatabase;
import ec.util.Parameter;
import ec.util.MersenneTwister;
import ec.util.Output;
import ec.util.Checkpoint;
import java.io.IOException;
import ec.util.Parameter;

/* 
 * SteadyStateEvolutionState.java
 * 
 * Created: Tue Aug 10 22:14:46 1999
 * By: Sean Luke
 */

/**
 * A SteadyStateEvolutionState is an EvolutionState which implements a simple
 * form of steady-state evolution.
 *
 * <p>First, all the individuals in the population are created and evaluated.
 * <b>(A)</b> Then 1 individual is selected by the breder for removal from the
 * population.  They are replaced by the result of breeding the other
 * individuals in the population.  Then just those newly-bred individuals are
 * evaluted.  Goto <b>(A)</b>.
 *
 * <p>Evolution stops when an ideal individual is found (if quitOnRunComplete
 * is set to true), or when the number of individuals evaluated exceeds the
 * parameter value numGenerations.  SteadyStateEvolutionState uses the "generations"
 * instance variable to store the number of individuals evaluated -- yes, that's
 * a bit dumb, but it's a historical weirdness.  We might fix that in the future.
 *
 * <p>Every once in a while (once every <i>pseudogeneration</i> evaluations),
 * the system will garbage collect and write out a checkpoint file. 

 <p><b>Additional constraints:</b>
 <ul>
 <li> The breeder must be SteadyStateBreeder, or a subclass of it.
 <li> The evaluator must be a SteadyStateEvaluator, or a subclass of it.
 <li> All Species must implement the SteadyStateSpeciesForm interface.
 </ul>
 
 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><tt>breed</tt><br>
 <font size=-1>classname, inherits or = ec.steadystate.SteadyStateBreeder</font></td>
 <td valign=top>(the class for breeder)</td></tr>
 <tr><td valign=top><tt>eval</tt><br>
 <font size=-1>classname, inherits or = ex.steadystate.SteadyStateEvaluator</font></td>
 <td valign=top>(the class for evaluator)</td></tr>
 <tr><td valign=top><tt>pseudogeneration</tt><br>
 <font size=-1>int >= 1</font></td>
 <td valign=top>(How often )</td></tr>


 * @author Sean Luke
 * @version 1.0 
 */

public class SteadyStateEvolutionState extends EvolutionState
    {
    /** base parameter for steady-state */
    public static final String P_STEADYSTATE = "steady";
    public static final String P_PSEUDOGENERATION = "pseudogeneration";

    public int newIndividuals[];

    public boolean inNextPseudogeneration;
    public boolean firstTimeAround;
    public int pseudogeneration;
    
    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);

        // double check that we have valid evaluators and breeders and exchangers
        if (!(breeder instanceof SteadyStateBreeder))
            state.output.error("You've chosen to use Steady-State Evolution, but your breeder is not of the class SteadyStateBreeder.",base);
        if (!(evaluator instanceof SteadyStateEvaluator))
            state.output.warning("You've chosen to use Steady-State Evolution, but your evaluator is not of the class SteadyStateEvaluator.",base);
        if (!(exchanger instanceof SteadyStateExchangerForm))
            state.output.warning("You've chosen to use Steady-State Evolution, but your exchanger does not implement the SteadyStateExchangerForm.",base);
        if (!(statistics instanceof SteadyStateStatisticsForm))
            state.output.warning("You've chosen to use Steady-State Evolution, but your statistics does not implement the SteadyStateStatisticsForm.",base);

        pseudogeneration = parameters.getInt(new Parameter(P_PSEUDOGENERATION),null,1);
        if (pseudogeneration==0)
            output.fatal("The pseudogeneration must be an integer >0.",base);
        }
    
    
    /** Performs the evolutionary run.  Garbage collection and checkpointing are done only once every <i>pseudogeneration</i> evaluations.  The only Statistics calls made are preInitializationStatistics(), postInitializationStatistics(), occasional postEvaluationStatistics (done once every <i>pseudogeneration</i> evaluations), and finalStatistics(). */

    public void run(int condition) throws IOException
        {
        try
            {
            if (condition == C_STARTED_FRESH)
                {
                output.message("Setting up");
                setup(this,null);  // a garbage Parameter
                output.message("Initializing Generation 0");
                statistics.preInitializationStatistics(this);
                population = initializer.initialPopulation(this);

                // double-check to make sure that all the Species are of SteadyStateSpeciesForm.
                for (int pop=0; pop<population.subpops.length;pop++)
                    if (!(population.subpops[pop].species instanceof SteadyStateSpeciesForm))
                        output.error("You've chosen to use Steady-State Evolution, but the species for subpopulation " + pop + " is is not a SteadyStateSpeciesForm Species",null);
                output.exitIfErrors();
                newIndividuals = new int[population.subpops.length];

                statistics.postInitializationStatistics(this);
                exchanger.initializeContacts(this);
                inNextPseudogeneration = true;
                firstTimeAround=true;
                }
            else // condition == C_STARTED_FROM_CHECKPOINT
                {
                // the one thing we didn't do
                if (firstTimeAround)
                    {
                    for(int pop=0;pop<population.subpops.length;pop++)
                        generation += population.subpops[pop].individuals.length;
                    firstTimeAround = false;
                    }
                else
                    {
                    inNextPseudogeneration = false;     
                    for(int pop=0;pop<population.subpops.length;pop++)
                        {
                        generation++;
                        if (generation % pseudogeneration == 0)
                            inNextPseudogeneration = true;
                        }

                    if (inNextPseudogeneration)
                        output.message("Pseudogeneration " + (generation / pseudogeneration + 1));
                    }
                }
            
            /* the big loop -- simplified to remove all the statistics
               calls, since they'd be on a per-individual basis rather
               than a per-population basis.*/
            
            int result = R_SUCCESS;
            while ( true )
                {
                if (firstTimeAround)
                    ((SteadyStateStatisticsForm)statistics).preInitialEvaluationStatistics(this);
                    
                evaluator.evaluatePopulation(this);
                
                if (firstTimeAround)
                    ((SteadyStateStatisticsForm)statistics).postInitialEvaluationStatistics(this);
                else
                    ((SteadyStateStatisticsForm)statistics).individualsEvaluatedStatistics(this);

                if (evaluator.runComplete(this) && quitOnRunComplete)
                    { output.message("Found Ideal Individual"); break; }
                
                if (generation >= numGenerations-1)
                    {
                    result = R_FAILURE;
                    break;
                    }
                
                if (inNextPseudogeneration)
                    {
                    population = exchanger.preBreedingExchangePopulation(this);
                    String exchangerWantsToShutdown = exchanger.runComplete(this);
                    if (exchangerWantsToShutdown!=null)
                        { output.message(exchangerWantsToShutdown); break; }
                    }
                    
                
                // gc
                
                if (gc && inNextPseudogeneration)
                    {
                    if (aggressivegc) aggressiveGC();
                    else gc();
                    }
                
                // breed
                

                population = breeder.breedPopulation(this);
                ((SteadyStateStatisticsForm)statistics).individualsBredStatistics(this);

                if (inNextPseudogeneration)
                    population = exchanger.postBreedingExchangePopulation(this);
                    
                if (checkpoint && (inNextPseudogeneration))
                    Checkpoint.setCheckpoint(this);

                if (firstTimeAround)
                    {
                    inNextPseudogeneration = false;     
                    for(int pop=0;pop<population.subpops.length;pop++)
                        generation += population.subpops[pop].individuals.length;
                    if (generation >= pseudogeneration)
                        {
                        inNextPseudogeneration = true;
                        ((SteadyStateStatisticsForm)statistics).nextPseudogenerationStatistics(this);
                        output.message("Pseudogeneration " + (generation / pseudogeneration));
                        }
                    firstTimeAround = false;
                    }
                else
                    {
                    inNextPseudogeneration = false;     
                    for(int pop=0;pop<population.subpops.length;pop++)
                        {
                        generation++;
                        if (generation % pseudogeneration == 0)
                            inNextPseudogeneration = true;
                        }

                    if (inNextPseudogeneration)
                        {
                        ((SteadyStateStatisticsForm)statistics).nextPseudogenerationStatistics(this);
                        output.message("Pseudogeneration " + (generation / pseudogeneration));
                        }
                    }
                }
            
            /* finish up -- we completed. */
            ((SteadyStateBreeder)breeder).finishPipelines(this);
            statistics.finalStatistics(this,result);
            finisher.finishPopulation(this,result);
            exchanger.closeContacts(this,result);
            }
        
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        }
    }
