package ec.simple;
import java.io.Serializable;
import ec.Fitness;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.Prototype;
import ec.util.*;
import java.io.*;

/*
 * SimpleFitness.java
 *
 * Created: Tue Aug 10 20:10:42 1999
 * By: Sean Luke
 */

/**
 * A simple default fitness, consisting of a single floating-point value
 * which ranges from 0 (worst), and where fitness A is superior to fitness
 * B if and only if A > B.  Fitness values may range from [0.0,infinity),
 * so in theory "infinity" could be the ideal fitness -- but in fact you can
 * have any maximum ideal fitness because when you set a fitness, you specify
 * whether or not it is the ideal.
 *
 <p><b>Default Base</b><br>
 simple.fitness

 * @author Sean Luke
 * @version 1.0
 */

public class SimpleFitness implements Fitness
    {
    public static final String FITNESS_PREAMBLE = "Fitness: ";
    public static final String P_FITNESS = "fitness";
    
    protected float fitness;
    protected boolean isIdeal;

    public Parameter defaultBase()
        {
        return SimpleDefaults.base().push(P_FITNESS);
        }

    public Object protoClone() throws CloneNotSupportedException
        { 
        return super.clone();
        }

    public Object protoCloneSimple()
        {
        try { return protoClone(); }
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        } 

    /**
       Deprecated -- now redefined to set the fitness but ALWAYS say that it's not ideal.
       If you need to specify that it's ideal, you should use the new function 
       setFitness(final EvolutionState state, float _f, boolean _isIdeal).
       @deprecated
    */
    public void setFitness(final EvolutionState state, float _f)
        {
        setFitness(state,_f,false);
        }
        
    public void setFitness(final EvolutionState state, float _f, boolean _isIdeal)
        {
        // we now allow f to be *any* value, positive or negative
        if (_f == Float.POSITIVE_INFINITY || _f == Float.NEGATIVE_INFINITY || Float.isNaN(_f))
            {
            state.output.warning("Bad fitness: " + _f + ", setting to 0.");
            fitness = 0;
            }
        else fitness = _f;
        isIdeal = _isIdeal;
        }

    public float fitness()
        {
        return fitness;
        }

    public void setup(final EvolutionState state, Parameter base) 
        {
        }

    public boolean isIdealFitness()
        {
        return isIdeal;
        }

    public boolean equivalentTo(final Fitness _fitness)
        {
        return ((SimpleFitness)_fitness).fitness() == fitness();
        }

    public boolean betterThan(final Fitness _fitness)
        {
        return ((SimpleFitness)_fitness).fitness() < fitness();
        }

    /** Presently does not encode the fact that the fitness is ideal or not */
    public void printFitness(final EvolutionState state, 
                             final int log, 
                             final int verbosity)
        {
        state.output.println(FITNESS_PREAMBLE + Code.encode(fitness()) , verbosity,log);
        }

    /** Presently does not encode the fact that the fitness is ideal or not */
    public void printFitness(final EvolutionState state,
                             final PrintWriter writer)
        {
        writer.println(FITNESS_PREAMBLE + Code.encode(fitness()));
        }

    /** Presently does not print the fact that the fitness is ideal or not */
    public void printFitnessForHumans(final EvolutionState state, 
                                      final int log, 
                                      final int verbosity)
        {
        state.output.println(FITNESS_PREAMBLE + fitness(), verbosity,log);
        }

    /** Presently does not decode the fact that the fitness is ideal or not */
    public void readFitness(final EvolutionState state, 
                            final LineNumberReader reader)
        throws IOException, CloneNotSupportedException
        {
        int linenumber = reader.getLineNumber();
        String s = reader.readLine();
        if (s==null || s.length() < FITNESS_PREAMBLE.length()) // uh oh
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad Fitness.");
        DecodeReturn d = new DecodeReturn(s, FITNESS_PREAMBLE.length());
        Code.decode(d);
        if (d.type!=DecodeReturn.T_FLOAT)
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad Fitness.");
        setFitness(state,(float)d.d,false);
        }
    }
