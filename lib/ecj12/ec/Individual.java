package ec;
import ec.util.Parameter;
import java.io.*;
import ec.util.*;

/*
 * Individual.java
 * Created: Tue Aug 10 19:58:13 1999
 */

/**
 * An Individual is an item in the EC population stew which is evaluated 
 * and assigned a fitness which determines its likelihood of selection.
 * Individuals are created most commonly by the newIndividual(...) method
 * of the ec.Species class.
 *
 * In general Individuals are immutable.  That is, once they are created
 * they should not be modified.  This protocol helps insure that they are
 * safe to read under multithreaded conditions.
 *
 * @author Sean Luke
 * @version 1.0
 */

public abstract class Individual implements Prototype
    {
    /** A string appropriate to put in front of whether or not the individual has been printed. */
    public static final String EVALUATED_PREAMBLE = "Evaluated: ";
    
    /** The fitness of the Individual. */
    public Fitness fitness;

    /** The species of the Individual.*/
    public Species species;
    
    /** Has the individual been evaluated and its fitness determined yet? */
    public boolean evaluated;

    /** Guaranteed DEEP-CLONES the individual.  You may need to override this
        method in Individual subclasses to guarantee this fact.   Some individuals
        (notably GPIndividuals) do not have protoClone() deep-clone them because
        that is expensive.  But if you need to guarantee that you have a unique
        individual, this is the way to do it.*/
        
    public Individual deepClone()
        {
        return (Individual) protoCloneSimple();
        }
        
    public Object protoClone() throws CloneNotSupportedException
        {
        Individual myobj = (Individual) (super.clone());
        
        if (myobj.fitness!=null) myobj.fitness = (Fitness)(fitness.protoClone());
        // put your deep-cloning code here...
        return myobj;
        } 

    public final Object protoCloneSimple()
        {
        try { return protoClone(); }
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        } 

    /** Returns true if I am genetically "equal" to ind.  This should
        mostly be interpreted as saying that we are of the same class
        and that we hold the same data. It should NOT be a pointer comparison. */
    public abstract boolean equals(Object ind);

    /** Returns a hashcode for the individual, such that individuals which
        are equals(...) each other always return the same
        hash code. */
    public abstract int hashCode();

    /** This should be used to set up only those things which you share in common
        with all other individuals in your species; individual-specific items
        which make you <i>you</i> should be filled in by Species.newIndividual(...),
        and modified by breeders. 
        @see Prototype#setup(EvolutionState,Parameter)
    */
    
    /** Overridden here because hashCode() is not expected to return the pointer
        to the object.  toString() normally uses hashCode() to print a unique identifier,
        and that's no longer the case.   You're welcome to override this anyway you 
        like to make the individual print out in a more lucid fashion. */
    public String toString()
        {
        return "" + this.getClass().getName() + "@" + 
            System.identityHashCode(this) + "{" + hashCode() + "}";
        }
        
    /** Print to a string the genotype of the Individual in a fashion readable by humans, and not intended
        to be parsed in again.  The fitness and evaluated flag should not be included.  The default form
        simply calls toString(), but you'll probably want to override this to something else. */
    public String genotypeToStringForHumans()
        {
        return toString();
        }
        
    /** Print to a string the genotype of the Individual in a fashion readable by humans, and not intended
        to be parsed in again.  The fitness and evaluated flag should not be included.  The default form
        simply calls toString(), which is almost certainly wrong, and you'll probably want to override
        this to something else. */
    public String genotypeToString()
        {
        return toString();
        }
                
    public abstract void setup(final EvolutionState state, final Parameter base);

    /** Should print the individual out in a pleasing way for humans,
        including its
        fitness, using state.output.println(...,verbosity,log)
        You can get fitness to print itself at the appropriate time by calling 
        fitness.printFitnessForHumans(state,log,verbosity);
                
        <p>The default form of this method simply prints out whether or not the
        individual has been evaluated, its fitness, and then Individual.genotypeToStringForHumans().
        Feel free to override this to produce more sophisticated behavior.
    */

    public void printIndividualForHumans(final EvolutionState state,
                                         final int log, 
                                         final int verbosity)
        {
        state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
                             verbosity, log);
        fitness.printFitnessForHumans(state,log,verbosity);
        state.output.println( genotypeToStringForHumans(), verbosity, log );
        }

    /** Should print the individual in a way that can be read by computer,
        including its fitness, using state.output.println(...,verbosity,log)
        You can get fitness to print itself at the appropriate time by calling 
        fitness.printFitness(state,log,verbosity);
                
        <p>The default form of this method simply prints out whether or not the
        individual has been evaluated, its fitness, and then Individual.genotypeToString().
        Feel free to override this to produce more sophisticated behavior.
    */

    public void printIndividual(final EvolutionState state,
                                final int log, 
                                final int verbosity)
        {
        state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
                             verbosity, log);
        fitness.printFitness(state,log,verbosity);
        state.output.println( genotypeToString(), verbosity, log );
        }

    /** Should print the individual in a way that can be read by computer,
        including its fitness.  You can get fitness to print itself at the
        appropriate time by calling fitness.printFitness(state,log,writer); 
        Usually you should try to use printIndividual(state,log,verbosity)
        instead -- use this method only if you can't print through the 
        Output facility for some reason.

        <p>The default form of this method simply prints out whether or not the
        individual has been evaluated, its fitness, and then Individual.genotypeToString().
        Feel free to override this to produce more sophisticated behavior.
    */

    public void printIndividual(final EvolutionState state,
                                final PrintWriter writer)
        {
        writer.println(EVALUATED_PREAMBLE + Code.encode(evaluated));
        fitness.printFitness(state,writer);
        writer.println( genotypeToString() );
        }

    /** Reads in the individual from a form printed by printIndividual().  The default
        simply reads in evaluation information, then fitness information, and then 
        calls parsegenotypeToString().  Feel free to override this to produce more sophisticated
        behavior. */ 

    public void readIndividual(final EvolutionState state, 
                               final LineNumberReader reader)
        throws IOException, CloneNotSupportedException
        {
        // First, was I evaluated?
        int linenumber = reader.getLineNumber();
        String s = reader.readLine();
        if (s==null || s.length() < EVALUATED_PREAMBLE.length()) // uh oh
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad 'Evaluated?' line.");
        DecodeReturn d = new DecodeReturn(s, EVALUATED_PREAMBLE.length());
        Code.decode(d);
        if (d.type!=DecodeReturn.T_BOOLEAN)
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad 'Evaluated?' line.");
        evaluated = (d.l!=0);

        // Next, what's my fitness?
        fitness.readFitness(state,reader);

        // next, read me in
        parseGenotype(state, reader);
        }

    /** This method is used only by the default version of readIndividual(state,reader),
        and it is intended to be overridden to parse in that part of the individual that
        was outputted in the genotypeToString() method.  The default version of this method
        exits the program with an "unimplemented" error.  You'll want to override this method,
        or to override readIndividual(...) to not use this method. */
    protected void parseGenotype(final EvolutionState state,
                                 final LineNumberReader reader) throws IOException, CloneNotSupportedException
        {
        state.output.fatal("parseIndividual not implemented in " + this.getClass());
        }


    /** Returns the "size" of the individual.  This is used for things like
        parsimony pressure.  The default form of this method returns 0 --
        if you care about parsimony pressure, you'll need to override the
        default to provide a more descriptive measure of size. */

    public long size() { return 0; }
    }

