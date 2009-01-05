package ec;
import ec.util.Parameter;

/* 
 * Evaluator.java
 * 
 * Created: Tue Aug 10 20:53:30 1999
 * By: Sean Luke
 */

/**
 * An Evaluator is a singleton object which is responsible for the
 * evaluation process during the course of an evolutionary run.  Only
 * one Evaluator is created in a run, and is stored in the EvolutionState
 * object.
 *
 * <p>Evaluators typically do their work by applying an instance of some
 * subclass of Problem to individuals in the population.  Evaluators come
 * with a Problem prototype which they may clone as necessary to create
 * new Problem spaces to evaluate individuals in (Problems may be reused
 * to prevent constant cloning).
 *
 * <p>Evaluators may be multithreaded, with one Problem instance per thread
 * usually.  The number of threads they may spawn (excepting a parent
 * "gathering" thread) is governed by the EvolutionState's evalthreads value.
 *
 * <p>Be careful about spawning threads -- this system has no few synchronized 
 * methods for efficiency's sake, so you must either divvy up evaluation in 
 * a thread-safe fashion, or 
 * otherwise you must obtain the appropriate locks on individuals in the population
 * and other objects as necessary.
 *
 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i><tt>.problem</tt><br>
 <font size=-1>classname, inherits and != ec.Problem</font></td>
 <td valign=top>(the class for the Problem prototype p_problem)</td></tr>
 </table>
 * @author Sean Luke
 * @version 1.0 
 */

public abstract class Evaluator implements Singleton
    {
    public static final String P_PROBLEM = "problem";

    public Problem p_problem;

    /** Evaluates the fitness of an entire population.  You will
        have to determine how to handle multiple threads on your own,
        as this is a very domain-specific thing. */
    public abstract void evaluatePopulation(final EvolutionState state);

    /** Returns true if an ideal individual has been found or some
        other run result has shortcircuited the run so that it should
        end prematurely right now. */
    public abstract boolean runComplete(final EvolutionState state);

    public void setup(final EvolutionState state, final Parameter base)
        {
        // Load my problem
        p_problem = (Problem)(state.parameters.getInstanceForParameter(
                                  base.push(P_PROBLEM),null,Problem.class));
        p_problem.setup(state,base.push(P_PROBLEM));
        }

    }
