package ec.multiobjective.spea2;
import java.io.*;
import ec.util.DecodeReturn;
import ec.util.Parameter;
import ec.util.Code;
import ec.multiobjective.MultiObjectiveFitness;
import ec.Fitness;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.Prototype;

/* 
 * SPEA2MultiObjectiveFitness.java
 * 
 * Created: Wed Jun 26 11:20:32 PDT 2002
 * By: Robert Hubley, Institute for Systems Biology
 *     (based on MultiObjectiveFitness.java by Sean Luke)
 */

/**
 * SPEA2MultiObjectiveFitness is a subclass of Fitness which implements
 * basic multiobjective fitness functions along with support for the
 * ECJ SPEA2 (Strength Pareto Evolutionary Algorithm) extensions.
 *
 * <p>The object contains two items: an array of floating point values
 * representing the various multiple fitnesses (ranging from 0.0 (worst)
 * to infinity (best)), and a single SPEA2 fitness value which represents
 * the individual's overall fitness ( a function of the number of 
 * individuals it dominates and it's raw score where 0.0 is the best).

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>numobjectives</tt><br>
 (else)<tt>multi.numobjectives</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(the number of fitnesses in the multifitness array)</td></tr>
 </table>

 * @author Robert Hubley (based on MultiObjectiveFitness by Sean Luke)
 * @version 1.0 
 */

public class SPEA2MultiObjectiveFitness extends MultiObjectiveFitness
    {
    public static final String SPEA2FIT_PREAMBLE = "SPEA2Fitness: ";

    /** SPEA2 overall fitness */
    public double SPEA2Fitness;

    /** SPEA2 strength (# of nodes it dominates) */
    public double SPEA2Strength;

    /** SPEA2 RAW fitness */
    public double SPEA2RawFitness;

    /** SPEA2 NN distance */
    public double SPEA2kthNNDistance;

    /** Returns the sum of the squared differences between the vector
        fitness values.
    */
    public float calcDistance(SPEA2MultiObjectiveFitness otherFit)
        {
        float s = 0;
        for (int i = 0; i < multifitness.length; i++)
            {
            s += (multifitness[i] - otherFit.multifitness[i]) *
                (multifitness[i] - otherFit.multifitness[i]);
            }
        return s;
        }

    /** Prints the fitness in the computer-readable form:
        <p><tt> Fitness: [</tt><i>fitness values encoded with ec.util.Code, separated by spaces</i><tt>]</tt>
    */
    public void printFitness(EvolutionState state, final int log, 
                             final int verbosity)
        {
        super.printFitness(state,log,verbosity);
        state.output.println(SPEA2FIT_PREAMBLE +  Code.encode(SPEA2Fitness), verbosity, log);
        }

    /** Prints the fitness in the computer-readable form:
        <p><tt> Fitness: [</tt><i>fitness values encoded with ec.util.Code, separated by spaces</i><tt>]</tt>
    */
    public void printFitness(final EvolutionState state,
                             final PrintWriter writer)
        {
        super.printFitness(state,writer);
        writer.println(SPEA2FIT_PREAMBLE +  Code.encode(SPEA2Fitness));
        }

    /** Prints the fitness in the human-readable form:
        <p><tt> Fitness: [</tt><i>fitness values separated by spaces</i><tt>]</tt>
    */
    public void printFitnessForHumans(final EvolutionState state, 
                                      final int log, 
                                      final int verbosity)
        {
        super.printFitnessForHumans(state,log,verbosity);
        state.output.println("S=" + SPEA2Strength + " R=" + SPEA2RawFitness + " D= " + SPEA2kthNNDistance + " F=" + SPEA2Fitness, verbosity,log);
        }


    public void readFitness(final EvolutionState state,
                            final LineNumberReader reader)
        throws IOException, CloneNotSupportedException
        {
        super.readFitness(state,reader);
    
        String s = reader.readLine();
        int linenumber = reader.getLineNumber();
        if (s==null || s.length() < SPEA2FIT_PREAMBLE.length()) // uh oh
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad SPEA2Fitness.");
        DecodeReturn d = new DecodeReturn(s, SPEA2FIT_PREAMBLE.length());
        Code.decode(d);
        if (d.type!=DecodeReturn.T_DOUBLE)
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad '" + SPEA2FIT_PREAMBLE + "' line.");
        SPEA2Fitness = d.d;

        // NOTE: At this time I am not reading/writing the SPEA2 strength, raw, 
        //       and distance values.  These are intermediate values to the 
        //       overal SPEA2Fitness and so are not really worth preserving.

        }


    }
