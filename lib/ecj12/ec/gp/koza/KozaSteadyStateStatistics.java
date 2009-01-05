package ec.gp.koza;
import ec.*;
import ec.simple.*;
import ec.gp.*;
import java.io.*;
import ec.util.*;
import ec.steadystate.*;

/* 
 * KozaSteadyStateStatistics.java
 * 
 * Created: Fri Nov  5 16:03:44 1999
 * By: Sean Luke
 */

/**
 * A version of KozaSteadyStateStatistics intended to be used with steady-state evolution.
 * Prints fitness information,
 * one pseudogeneration (or pseudo-pseudogeneration) per line.
 * If gather-full is true, then timing information, number of nodes
 * and depths of trees, etc. are also given.  No final statistics information
 * is given.
 *
 * <p> Each line represents a single pseudogeneration.  
 * The first items on a line are always:
 <ul>
 <li> The number of individuals evaluated so far
 <li> (if gather-full) "--" [normally: how long initialization took in milliseconds, how long the previous pseudogeneration took to breed to form this pseudogeneration]
 <li> (if gather-full) "--" [normally: how many bytes initialization took, or how how many bytes the previous pseudogeneration took to breed to form this pseudogeneration.]
 <li> (if gather-full) "--" [normally: How long evaluation took in milliseconds this pseudogeneration]
 <li> (if gather-full) "--" [normally: how many bytes evaluation took this pseudogeneration]
 </ul>

 <p>Then the following items appear, per subpopulation:
 <ul>
 <li> (if gather-full) The average number of nodes used per individual at the pseudogeneration mark
 <li> (if gather-full) [a|b|c...], representing the average number of nodes used in tree <i>a</i>, <i>b</i>, etc. of individuals at the pseudogeneration mark
 <li> (if gather-full) The average number of nodes used per individual so far in the run
 <li> (if gather-full) The average depth of any tree per individual at the pseudogeneration mark
 <li> (if gather-full) [a|b|c...], representing the average depth of tree <i>a</i>, <i>b</i>, etc. of individuals at the pseudogeneration mark
 <li> (if gather-full) The average depth of any tree per individual so far in the run
 <li> The mean raw fitness of the subpopulation at the pseudogeneration mark
 <li> The mean adjusted fitness of the subpopulation at the pseudogeneration mark
 <li> The mean hits of the subpopulation at the pseudogeneration mark
 <li> The best raw fitness of the subpopulation at the pseudogeneration mark
 <li> The best adjusted fitness of the subpopulation at the pseudogeneration mark
 <li> The best hits of the subpopulation at the pseudogeneration mark
 <li> The best raw fitness of the subpopulation so far in the run
 <li> The best adjusted fitness of the subpopulation so far in the run
 <li> The best hits of the subpopulation so far in the run
 </ul>

 Compressed files will be overridden on restart from checkpoint; uncompressed files will be 
 appended on restart.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base.</i><tt>gzip</tt><br>
 <font size=-1>boolean</font></td>
 <td valign=top>(whether or not to compress the file (.gz suffix added)</td></tr>
 <tr><td valign=top><i>base.</i><tt>file</tt><br>
 <font size=-1>String (a filename), or nonexistant (signifies stdout)</font></td>
 <td valign=top>(the log for statistics)</td></tr>
 <tr><td valign=top><i>base</i>.<tt>gather-full</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</font></td>
 <td valign=top>(should we full statistics on individuals (will run slower, though the slowness is due to off-line processing that won't mess up timings)</td></tr>
 </table>
 * @author Sean Luke
 * @version 1.0 
 */

public class KozaSteadyStateStatistics extends Statistics implements SteadyStateStatisticsForm
    {
    /** compress? */
    public static final String P_COMPRESS = "gzip";

    public static final String P_FULL = "gather-full";

    public boolean doFull;

    public Individual[] best_of_run_a;
    public long totalNodes[];
    public long totalDepths[];
    
    public long genNodes[][];
    public long genDepths[][];

    // timings
    public long lastTime;
    
    // usage
    public long lastUsage;
    
    /** log file parameter */
    public static final String P_STATISTICS_FILE = "file";

    /** The Statistics' log */
    public int statisticslog;


    public KozaSteadyStateStatistics() { statisticslog = 0; /* stdout */ }


    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);
        File statisticsFile = state.parameters.getFile(
            base.push(P_STATISTICS_FILE),null);

        if (statisticsFile!=null) try
            {
            statisticslog = state.output.addLog(statisticsFile,Output.V_NO_GENERAL-1,false,
                                                !state.parameters.getBoolean(base.push(P_COMPRESS),null,false),
                                                state.parameters.getBoolean(base.push(P_COMPRESS),null,false));
            }
        catch (IOException i)
            {
            state.output.fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + i);
            }
        doFull = state.parameters.getBoolean(base.push(P_FULL),null,false);
        }


    /** Called immediately before population initialization occurs. */
    public void preInitializationStatistics(final EvolutionState state) 
        {
        super.postInitializationStatistics(state);
        }
    /** Called immediately before the initial generation is evaluated. */
    public void preInitialEvaluationStatistics(final SteadyStateEvolutionState state)
        {
        for(int x=0;x<children.length;x++)
            ((SteadyStateStatisticsForm)children[x]).preInitialEvaluationStatistics(state);
        }
    /** Called immediately after the initial generation is evaluated. */
    public void postInitialEvaluationStatistics(final SteadyStateEvolutionState state)
        {
        for(int x=0;x<children.length;x++)
            ((SteadyStateStatisticsForm)children[x]).postInitialEvaluationStatistics(state);

        Individual[] best_i = new Individual[state.population.subpops.length];
        for(int x=0;x<state.population.subpops.length;x++)
            {
            for(int y=0;y<state.population.subpops[x].individuals.length;y++)
                {
                // best individual
                if (best_i[x]==null ||
                    state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness))
                    best_i[x] = state.population.subpops[x].individuals[y];
                }
            best_of_run_a[x] = best_i[x];
            }
        }
    
    /** Called each time new individuals are bred during the steady-state
        process.  You can look up the individuals in state.newIndividuals[] */
    public void postInitializationStatistics(final EvolutionState state)
        {
        super.postInitializationStatistics(state);
        // set up our best_of_run array -- can't do this in setup, because
        // we don't know if the number of subpopulations has been determined yet
        best_of_run_a = new Individual[state.population.subpops.length];
        
        // gather timings       
        if (doFull)
            {
            totalNodes = new long[state.population.subpops.length];
            totalDepths = new long[state.population.subpops.length];
            genNodes = new long[state.population.subpops.length][];
            for(int x=0;x<state.population.subpops.length;x++)
                genNodes[x] = new long[((GPIndividual)(state.population.subpops[x].species.i_prototype)).trees.length];
            genDepths = new long[state.population.subpops.length][];
            for(int x=0;x<state.population.subpops.length;x++)
                genDepths[x] = new long[((GPIndividual)(state.population.subpops[x].species.i_prototype)).trees.length];
            }
        }

    /** Prints out the statistics, but does not end with a println --
        this lets overriding methods print additional statistics on the same line */
    protected void _nextPseudogenerationStatistics(final EvolutionState state)
        {
        state.output.print("" + state.generation + " ", Output.V_NO_GENERAL, statisticslog);

        Individual[] best_i = new Individual[state.population.subpops.length];
                
        for(int x=0;x<state.population.subpops.length;x++)
            {
            if (doFull)
                {
                // filler for previous timings in other statistics objects
                state.output.print("-- -- -- -- ", Output.V_NO_GENERAL, statisticslog);
                
                long totNodesPerGen = 0;
                long totDepthPerGen = 0;

                // check to make sure they're the right class
                if ( !(state.population.subpops[x].species instanceof GPSpeciesForm ))
                    state.output.fatal("Subpopulation " + x +
                                       " is not of the species form GPSpeciesForm." + 
                                       "  Cannot do timing statistics with KozaSteadyStateStatistics.");
                
                long[] numNodes = new long[((GPIndividual)(state.population.subpops[x].species.i_prototype)).trees.length];
                long[] numDepth = new long[((GPIndividual)(state.population.subpops[x].species.i_prototype)).trees.length];
                
                for(int y=0;y<state.population.subpops[x].individuals.length;y++)
                    {
                    GPIndividual i = 
                        (GPIndividual)(state.population.subpops[x].individuals[y]);
                    for(int z=0;z<i.trees.length;z++)
                        {
                        numNodes[z] += i.trees[z].child.numNodes(GPNode.NODESEARCH_ALL);
                        numDepth[z] += i.trees[z].child.depth();
                        }
                    }
                
                for(int tr=0;tr<numNodes.length;tr++) totNodesPerGen += numNodes[tr];
                
                // totalNodes[x] += totNodesPerGen;

                state.output.print("" + ((double)totNodesPerGen)/state.population.subpops[x].individuals.length + " [", Output.V_NO_GENERAL, statisticslog);

                for(int tr=0;tr<numNodes.length;tr++)
                    {
                    if (tr>0) state.output.print("|",Output.V_NO_GENERAL, statisticslog);
                    state.output.print(""+((double)numNodes[tr])/state.population.subpops[x].individuals.length,Output.V_NO_GENERAL, statisticslog);
                    }
                state.output.print("] ",Output.V_NO_GENERAL, statisticslog);

                state.output.print("" + ((double)totalNodes[x])/(state.generation) + " ",
                                   Output.V_NO_GENERAL, statisticslog);

                for(int tr=0;tr<numDepth.length;tr++) totDepthPerGen += numDepth[tr];

                // totalDepths[x] += totDepthPerGen;

                state.output.print("" + ((double)totDepthPerGen)/
                                   (state.population.subpops[x].individuals.length *
                                    numDepth.length) 
                                   + " [", Output.V_NO_GENERAL, statisticslog);


                for(int tr=0;tr<numDepth.length;tr++)
                    {
                    if (tr>0) state.output.print("|",Output.V_NO_GENERAL, statisticslog);
                    state.output.print(""+((double)numDepth[tr])/state.population.subpops[x].individuals.length,Output.V_NO_GENERAL, statisticslog);
                    }
                state.output.print("] ",Output.V_NO_GENERAL, statisticslog);

                state.output.print("" + ((double)totalDepths[x])/(state.generation) + " ",
                                   Output.V_NO_GENERAL, statisticslog);
                }
            

            
            // fitness information
            float meanRaw = 0.0f;
            float meanAdjusted = 0.0f;
            long hits = 0;
            
            if (!(state.population.subpops[x].f_prototype instanceof KozaFitness))
                state.output.fatal("Subpopulation " + x +
                                   " is not of the fitness KozaFitness.  Cannot do timing statistics with KozaStatistics.");

            best_i[x] = null;
            for(int y=0;y<state.population.subpops[x].individuals.length;y++)
                {
                // best individual
                if (best_i[x]==null ||
                    state.population.subpops[x].individuals[y].fitness.betterThan(best_i[x].fitness))
                    best_i[x] = state.population.subpops[x].individuals[y];

                // mean for population
                meanRaw += ((KozaFitness)(state.population.subpops[x].individuals[y].fitness)).rawFitness();
                meanAdjusted += ((KozaFitness)(state.population.subpops[x].individuals[y].fitness)).adjustedFitness();
                hits += ((KozaFitness)(state.population.subpops[x].individuals[y].fitness)).hits;
                }
            
            // compute fitness stats
            meanRaw /= state.population.subpops[x].individuals.length;
            meanAdjusted /= state.population.subpops[x].individuals.length;
            state.output.print("" + meanRaw + " " + meanAdjusted + " " + 
                               ((double)hits)/state.population.subpops[x].individuals.length + " ", 
                               Output.V_NO_GENERAL, statisticslog);
            state.output.print("" + ((KozaFitness)(best_i[x].fitness)).rawFitness() +
                               " " + ((KozaFitness)(best_i[x].fitness)).adjustedFitness() +
                               " " + ((KozaFitness)(best_i[x].fitness)).hits + " ",
                               Output.V_NO_GENERAL, statisticslog);

            state.output.print("" + ((KozaFitness)(best_of_run_a[x].fitness)).rawFitness() +
                               " " + ((KozaFitness)(best_of_run_a[x].fitness)).adjustedFitness() +
                               " " + ((KozaFitness)(best_of_run_a[x].fitness)).hits + " ",
                               Output.V_NO_GENERAL, statisticslog);
            }
        // we're done!
        }

    public void nextPseudogenerationStatistics(final SteadyStateEvolutionState state)
        {
        _nextPseudogenerationStatistics(state);
        state.output.println("",Output.V_NO_GENERAL, statisticslog);
        }

    public void individualsBredStatistics(SteadyStateEvolutionState state)
        {
        for(int x=0;x<children.length;x++)
            ((SteadyStateStatisticsForm)children[x]).individualsBredStatistics(state);
        }
    
    public void individualsEvaluatedStatistics(SteadyStateEvolutionState state)
        {
        for(int x=0;x<children.length;x++)
            ((SteadyStateStatisticsForm)children[x]).individualsEvaluatedStatistics(state);

        for(int x=0;x<state.newIndividuals.length;x++) 
            {
            GPIndividual i = (GPIndividual)(state.population.subpops[x].individuals[state.newIndividuals[x]]);
            // now test to see if it's the new best_of_run_a[x]
            if (best_of_run_a[x]==null || i.fitness.betterThan(best_of_run_a[x].fitness))
                best_of_run_a[x] = i;
            
            if (doFull) for(int z=0;z<i.trees.length;z++)
                {
                totalNodes[x] += i.trees[z].child.numNodes(GPNode.NODESEARCH_ALL);
                totalDepths[x] += i.trees[z].child.depth();
                }
            }
        }
    public void finalStatistics(final EvolutionState state, final int result)
        {
        super.finalStatistics(state,result);
        }

    }
