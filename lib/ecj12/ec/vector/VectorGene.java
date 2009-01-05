package ec.vector;

import ec.*;
import ec.util.*;
import java.io.Serializable;
import java.util.*;
import java.io.*;


/*
 * VectorGene.java
 * Created: Thu Mar 22 13:13:20 EST 2001
 */

/**
 * VectorGene is an abstract superclass of objects which may be used in
 * the genome array of GeneVectorIndividuals.
 *

 <p><b>Default Base</b><br>
 vector.vect-gene

 * @author Sean Luke
 * @version 1.0
 */
public abstract class VectorGene implements Prototype
    {
    public static final String P_VECTORGENE = "vect-gene";

    public void setup(final EvolutionState state, final Parameter base)
        {
        // nothing by default
        }
        
    public Parameter defaultBase()
        {
        return VectorDefaults.base().push(P_VECTORGENE);
        }
    
    public Object protoClone() throws CloneNotSupportedException
        {
        return super.clone();
        }
        
    public final Object protoCloneSimple()
        {
        try { return protoClone(); }
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        } 

    /** Generates a hash code for this gene -- the rule for this is that the hash code
        must be the same for two genes that are equal to each other genetically. */
    public abstract int hashCode();
    
    /** Unlike the standard form for Java, this function should return true if this
        gene is "genetically identical" to the other gene. */
    public abstract boolean equals( final Object other );

    /**
       The reset method randomly reinitializes the gene.
    */
    public abstract void reset(final EvolutionState state, final int thread);

    /**
       Mutate the gene.  The default form just resets the gene.
    */
    public void mutate(final EvolutionState state, final int thread)
        {
        reset(state,thread);
        }

    /**
       Nice printing.  The default form simply calls printGene, but you might want to override this.
       @deprecated use printGeneForHumansToString instead
    */
    public void printGeneForHumans( final EvolutionState state, final int verbosity, final int log )
        { printGene(state,verbosity,log); }

    /** Prints the gene to a string in a human-readable fashion.  The default simply calls printGeneToString(). */
    public String printGeneToStringForHumans()
        { return printGeneToString(); }

    /** Prints the gene to a string in a fashion readable by readGeneFromString and parseable by readGene(state, reader).
        The default form simply calls printGeneToString(). 
        @deprecated use printGeneToString() instead. */
    public String printGeneToString(final EvolutionState state)
        { return printGeneToString(); }

    /** Prints the gene to a string in a fashion readable by readGeneFromString and parseable by readGene(state, reader).
        Override this.  The default form returns "Override Me!". */
    public String printGeneToString()
        { return "Override Me!"; }

    /** Reads a gene from a string, which may contain a final '\n'.
        Override this method.  The default form generates an error.
    */
    public void readGeneFromString(final String string, final EvolutionState state)
        { state.output.error("readGeneFromString(string,state) unimplemented in " + this.getClass()); }

    /**
       Prints the gene in a way that can be read by readGene().  The default form simply
       calls printGeneToString(state).   Override this gene to do custom writing to the log,
       or just override printGeneToString(...), which is probably easier to do.
       @deprecated use printGeneToString instead
    */
    public void printGene( final EvolutionState state, final int verbosity, final int log )
        { state.output.println(printGeneToString(state),verbosity,log);}

    /**
       Prints the gene in a way that can be read by readGene().  The default form simply
       calls printGeneToString(state).   Override this gene to do custom writing,
       or just override printGeneToString(...), which is probably easier to do.
       @deprecated use printGeneToString instead
    */
    public void printGene( final EvolutionState state, final PrintWriter writer )
        { writer.println(printGeneToString(state)); }

    /**
       Reads a gene printed by printGene(...).  The default form simply reads a line into
       a string, and then calls readGeneFromString() on that line.  Override this gene to do
       custom reading, or just override readGeneFromString(...), which is probably easier to do.
    */
    public void readGene(final EvolutionState state,
                         final LineNumberReader reader)
        throws IOException, CloneNotSupportedException
        { readGeneFromString(reader.readLine(),state); }
    }
