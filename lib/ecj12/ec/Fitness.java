package ec;
import java.io.*;

/*
 * Fitness.java
 *
 * Created: Tue Aug 10 20:10:42 1999
 * By: Sean Luke
 */

/**
 * Fitness is a prototype which describes the fitness of an individual.
 * Every individual contains exactly one Fitness object.
 * Fitness objects are compared to each other with the equivalentTo()
 * and betterThan(), etc. methods. 
 *
 <p>Rules: 
 <table>
 <tr><td><b>comparison</b></td><td><b>method</b></td></tr>
 <tr><td>a &gt; b</td><td>a.betterThan(b)</td>
 <tr><td>a &gt;= b</td><td>a.betterThan(b) || a.equivalentTo(b)</td>
 <tr><td>a = b</td><td>a.equivalentTo(b)</td>
 </table>

 This applies even to multiobjective pareto-style dominance, eg:
 <ul>
 <li> a dominates b :: a &gt; b
 <li> a and b do not dominate each other :: a = b
 <li> b dominates a :: a &lt; b
 </ul>

 <p><b>Parameter bases</b><br>
 <table>

 <tr><td valign=top><tt>fit</tt></td>
 <td>default fitness base</td></tr>
 </table>

 * @author Sean Luke
 * @version 1.0
 */


public interface Fitness extends Prototype
    {
    public static final String P_FITNESS = "fit";

    /** Should return an absolute fitness value ranging from negative
        infinity to infinity, NOT inclusive (thus infinity, negative
        infinity, and NaN are NOT valid fitness values).  This shouldd
        be interpreted as: negative infinity is worse than the WORST
        possible fitness, and positive infinity is better than the IDEAL
        fitness.
        
        <p>You are free to restrict this range any way you like: for example,
        your fitness values might fall in the range [-5.32, 2.3]
        
        <p>Selection methods relying on fitness proportionate information will
        <b>assume the fitness is non-negative</b> and should throw an error
        if it is not.  Thus if you plan on using FitProportionateSelection, 
        BestSelection, or
        GreedyOverselection, for example, your fitnesses should assume that 0
        is the worst fitness and positive fitness are better.  If you're using
        other selection methods (Tournament selection, various ES selection
        procedures, etc.) your fitness values can be anything.
        
        <p>Similarly, if you're writing a selection method and it needs positive
        fitnesses, you should check for negative values and issue an error; and
        if your selection method doesn't need an <i>absolute</i> fitness
        value, it should use the equivalentTo() and betterThan() methods instead.
        
        <p> If your fitness scheme does not use a metric quantifiable to
        a single positive value (for example, MultiObjectiveFitness), you should 
        perform some reasonable translation.
    */
    public abstract float fitness();

    /** Should return true if this is a good enough fitness to end the run */
    public abstract boolean isIdealFitness();

    /** Should return true if this fitness is in the same equivalence class
        as _fitness, that is, neither is clearly bettter or worse than the
        other.  You may assume that _fitness is of the same class as yourself.
        worseThan(), equivalentTo() and betterThan() should be disjoint sets.
    */
    public abstract boolean equivalentTo(Fitness _fitness);
    
    /** Should return true if this fitness is clearly better than _fitness;
        You may assume that _fitness is of the same class as yourself. 
        worseThan(), equivalentTo() and betterThan() should be disjoint sets.
    */
    public abstract boolean betterThan(Fitness _fitness);

    /** Should print the fitness out in a pleasing way to humans, 
        using state.output.println(...,verbosity,log)
    */  

    public abstract void printFitnessForHumans(EvolutionState state, int log, 
                                               int verbosity);

    /** Should print the fitness out in a computer-readable fashion, 
        using state.output.println(...,verbosity,log).  You should use
        ec.util.Code to encode fitness values. */  

    public abstract void printFitness(EvolutionState state, int log, 
                                      int verbosity);
    
    /** Should print the fitness out in a computer-readable fashion, 
        using state.output.println(...,verbosity,log).  You should use
        ec.util.Code to encode fitness values.  
        Usually you should try to use printFitness(state,log,verbosity)
        instead -- use this method only if you can't print through the 
        Output facility for some reason.
    */

    public abstract void printFitness(final EvolutionState state,
                                      final PrintWriter writer);

    /** Reads in the fitness from a form printed by printFitness().*/ 

    public abstract void readFitness(final EvolutionState state, 
                                     final LineNumberReader reader)
        throws IOException, CloneNotSupportedException;
    }

