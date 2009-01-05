package ec;
import ec.util.*;

/* 
 * Problem.java
 * 
 * Created: Fri Oct 15 14:16:17 1999
 * By: Sean Luke
 */

/**
 * Problem is a prototype which defines the problem against which we will
 * evaluate individuals in a population. 
 *
 * <p>Since Problems are Prototypes, you should expect a new Problem class to be
 * cloned and used, on a per-thread basis, for the evolution of each
 * chunk of individuals in a new population.  If you for some reason
 * need global Problem information, you will have to provide it
 * statically, or copy pointers over during the protoClone() process
 * (there is likely only one Problem prototype, depending on the
 * Evaluator class used).
 *
 * <p>Note that Problem does not implement a specific evaluation method.
 * Your particular Problem subclass will need to implement a some kind of
 * Problem Form (for example, SimpleProblemForm) appropriate to the kind of
 * evaluation being performed on the Problem.  These Problem Forms will provide
 * the evaluation methods necessary.
 *
 * @author Sean Luke
 * @version 2.0 
 */

public abstract class Problem implements Prototype
    {
    public static final String P_PROBLEM = "problem";
    
    /** Here's a nice default base for you -- you can change it if you like */
    public Parameter defaultBase()
        {
        return new Parameter(P_PROBLEM);
        }

    // default form does nothing
    public void setup(final EvolutionState state, final Parameter base) 
        { }

    // default form does nothing
    public Object protoClone() throws CloneNotSupportedException
        {
        return clone();
        }
    
    // implemented so you don't have to bother with it
    public Object protoCloneSimple()
        {
        try { return protoClone(); }
        catch (CloneNotSupportedException e) {  }  // do nothing, never happens
        return null;
        }
    }


