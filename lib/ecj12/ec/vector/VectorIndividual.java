package ec.vector;

import ec.*;
import ec.util.*;

/*
 * VectorIndividual.java
 * Created: Tue Mar 13 15:03:12 EST 2001
 */

/**
 * VectorIndividual is the abstract superclass of simple individual representations
 * which consist of vectors of values (booleans, integers, floating-point, etc.)
 *
 * <p>This class contains two methods, defaultCrossover and defaultMutate, which can
 * be overridden if all you need is a simple crossover and a simple mutate mechanism.
 * the VectorCrossoverPipeline and VectorMutationPipeline classes use these methods to do their
 * handiwork.  For more sophisticated crossover and mutation, you'll need to write
 * a custom breeding pipeline.
 *
 * <p>The <i>kind</i> of default crossover and mutation, and associated information,
 * is stored in the VectorIndividual's VectorSpecies object, which is obtained through
 * the <tt>species</tt> variable.  For example, 
 * VectorIndividual assumes three common types of crossover as defined in VectorSpecies
 * which you should implement in your defaultCrossover method: one-point, 
 * two-point, and any-point (otherwise known as "uniform") crossover.
 *
 * <p>VectorIndividual is typically used for fixed-length vector representations;
 * however, it can also be used with variable-length representations.  Two methods have
 * been provided in all subclasses of VectorIndividual to help you there: split and
 * join, which you can use to break up and reconnect VectorIndividuals in a variety
 * of ways.  Note that you may want to override the reset() method to create individuals
 * with different initial lengths.
 *
 * <p>VectorIndividuals must belong to the species VectorSpecies (or some subclass of it).
 *
 
 * @author Sean Luke
 * @version 1.0
 */

public abstract class VectorIndividual extends Individual
    {
    /** Destructively crosses over the individual with another in some default manner.  In most
        implementations provided in ECJ, one-, two-, and any-point crossover is done with a 
        for loop, rather than a possibly more efficient approach like arrayCopy().  The disadvantage
        is that arrayCopy() takes advantage of a CPU's bulk copying.  The advantage is that arrayCopy()
        would require a scratch array, so you'd be allocing and GCing an array for every crossover.
        Dunno which is more efficient.  */
    public void defaultCrossover(EvolutionState state, int thread, 
                                 VectorIndividual ind) { }

    /** Destructively mutates the individual in some default manner.  The default version calls reset()*/
    public void defaultMutate(EvolutionState state, int thread) { reset(state,thread); }

    /** Initializes the individual. */
    public abstract void reset(EvolutionState state, int thread);

    /** Returns the gene array.  If you know the type of the array, you can cast it and work on
        it directly.  Otherwise, you can still manipulate it in general, because arrays (like
        all objects) respond to clone() and can be manipulated with arrayCopy without bothering
        with their type.  This might be useful in creating special generalized crossover operators
        -- we apologize in advance for the fact that Java doesn't have a template system.  :-( 
        The default version returns null. */
    public Object getGenome() { return null; }
    
    /** Sets the gene array.  See getGenome().  The default version does nothing.
     */
    public void setGenome(Object gen) { }

    /** Returns the length of the gene array.  By default, this method returns 0. */
    public long genomeLength() { return 0; }

    /** Splits the genome into n pieces, according to points, which *must* be sorted. 
        pieces.length must be 1 + points.length.  The default form does nothing -- be careful
        not to use this method if it's not implemented!  It should be trivial to implement it
        for your genome -- just like at the other implementations.  */
    public void split(int[] points, Object[] pieces) { }

    /** Joins the n pieces and sets the genome to their concatenation.  The default form does nothing. 
        It should be trivial to implement it
        for your genome -- just like at the other implementations.  */
    public void join(Object[] pieces) { }
    
    public long size() { return genomeLength(); }
    }
