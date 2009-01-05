package ec.vector;

import ec.*;
import ec.util.*;
import java.io.Serializable;
import java.util.*;
import java.io.*;

/*
 * GeneVectorIndividual.java
 * Created: Thu Mar 22 13:13:20 EST 2001
 */

/**
 * GeneVectorIndividual is a VectorIndividual whose genome is an array of VectorGenes.
 * The default mutation method calls the mutate() method on each gene independently
 * with <tt>species.mutationProbability</tt>.  Initialization calls reset(), which
 * should call reset() on each gene.  Do not expect that the genes will actually
 * exist during initialization -- see the default implementation of reset() as an example
 * for how to handle this.
 *

 <p><b>Default Base</b><br>
 vector.gene-vect-ind

 * @author Sean Luke
 * @version 1.0
 */

public class GeneVectorIndividual extends VectorIndividual
    {
    public static final String P_GENEVECTORINDIVIDUAL = "gene-vect-ind";
    public VectorGene[] genome;
    
    public Parameter defaultBase()
        {
        return VectorDefaults.base().push(P_GENEVECTORINDIVIDUAL);
        }

    public Object protoClone() throws CloneNotSupportedException
        {
        GeneVectorIndividual myobj = (GeneVectorIndividual) (super.protoClone());

        // must clone the genome
        myobj.genome = (VectorGene[])(genome.clone());
        for(int x=0;x<genome.length;x++)
            myobj.genome[x] = (VectorGene)(genome[x].protoClone());
        
        return myobj;
        }

    public void setup(final EvolutionState state, final Parameter base)
        {
        // since VectorSpecies set its constraint values BEFORE it called
        // super.setup(...) [which in turn called our setup(...)], we know that
        // stuff like genomeSize has already been set...
        
        Parameter def = defaultBase();
        
        if (!(species instanceof GeneVectorSpecies)) 
            state.output.fatal("GeneVectorIndividual requires a GeneVectorSpecies", base, def);
        GeneVectorSpecies s = (GeneVectorSpecies) species;
        
        // note that genome isn't initialized with any genes yet -- they're all null.
        // reset() needs
        genome = new VectorGene[s.genomeSize];
        reset(state,0);
        }
        
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind)
        {
        GeneVectorSpecies s = (GeneVectorSpecies) species;
        GeneVectorIndividual i = (GeneVectorIndividual) ind;
        VectorGene tmp;
        int point;

        if (genome.length != i.genome.length)
            state.output.fatal("Genome lengths are not the same for fixed-length vector crossover");
        switch(s.crossoverType)
            {
            case VectorSpecies.C_ONE_POINT:
                point = state.random[thread].nextInt((genome.length / s.chunksize)+1);
                for(int x=0;x<point*s.chunksize;x++)
                    { 
                    tmp = i.genome[x];
                    i.genome[x] = genome[x]; 
                    genome[x] = tmp; 
                    }
                break;
            case VectorSpecies.C_TWO_POINT: 
                int point0 = state.random[thread].nextInt((genome.length / s.chunksize)+1);
                point = state.random[thread].nextInt((genome.length / s.chunksize)+1);
                if (point0 > point) { int p = point0; point0 = point; point = p; }
                for(int x=point0*s.chunksize;x<point*s.chunksize;x++)
                    {
                    tmp = i.genome[x];
                    i.genome[x] = genome[x];
                    genome[x] = tmp;
                    }
                break;
            case VectorSpecies.C_ANY_POINT:
                for(int x=0;x<genome.length/s.chunksize;x++) 
                    if (state.random[thread].nextBoolean(s.crossoverProbability))
                        for(int y=x*s.chunksize;y<(x+1)*s.chunksize;y++)
                            {
                            tmp = i.genome[y];
                            i.genome[y] = genome[y];
                            genome[y] = tmp;
                            }
                break;
            }
        }

    /** Splits the genome into n pieces, according to points, which *must* be sorted. 
        pieces.length must be 1 + points.length */
    public void split(int[] points, Object[] pieces)
        {
        int point0, point1;
        point0 = 0; point1 = points[0];
        for(int x=0;x<pieces.length;x++)
            {
            pieces[x] = new VectorGene[point1-point0];
            System.arraycopy(genome,point0,pieces[x],point0,point1-point0);
            point0 = point1;
            if (x==pieces.length-2)
                point1 = genome.length;
            else point1 = points[x+1];
            }
        }
    
    /** Joins the n pieces and sets the genome to their concatenation.*/
    public void join(Object[] pieces)
        {
        int sum=0;
        for(int x=0;x<pieces.length;x++)
            sum += ((VectorGene[])(pieces[x])).length;
        
        int runningsum = 0;
        VectorGene[] newgenome = new VectorGene[sum];
        for(int x=0;x<pieces.length;x++)
            {
            System.arraycopy(pieces[x], 0, newgenome, runningsum, ((VectorGene[])(pieces[x])).length);
            runningsum += ((VectorGene[])(pieces[x])).length;
            }
        // set genome
        genome = newgenome;
        }

    /** Destructively mutates the individual in some default manner.  The default form
        simply randomizes genes to a uniform distribution from the min and max of the gene values. */
    public void defaultMutate(EvolutionState state, int thread)
        {
        GeneVectorSpecies s = (GeneVectorSpecies) species;
        if (s.mutationProbability>0.0)
            for(int x=0;x<genome.length;x++)
                if (state.random[thread].nextBoolean(s.mutationProbability))
                    genome[x].mutate(state,thread);
        }

    /** Initializes the individual by calling reset(...) on each gene. */
    public void reset(EvolutionState state, int thread)
        {
        GeneVectorSpecies s = (GeneVectorSpecies) species;
        try
            {
            for(int x=0;x<genome.length;x++)
                {
                // first create the gene if it doesn't exist
                if (genome[x]==null) genome[x] = (VectorGene)(s.genePrototype.protoClone());
                // now reset it
                genome[x].reset(state,thread);
                }
            }
        catch(CloneNotSupportedException e) { } // never happens
        }

    public int hashCode()
        {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        for(int x=0;x<genome.length;x++)
            hash = ( hash << 1 | hash >>> 31 ) ^ genome[x].hashCode();

        return hash;
        }

    public String genotypeToStringForHumans()
        {
        StringBuffer s = new StringBuffer();
        for( int i = 0 ; i < genome.length ; i++ )
            { s.append(" "); s.append(genome[i].printGeneToStringForHumans()); }
        return s.toString();
        }
        
    public String genotypeToString()
        {
        StringBuffer s = new StringBuffer();
        for( int i = 0 ; i < genome.length ; i++ )
            { s.append(" "); s.append(genome[i].printGeneToString()); }
        return s.toString();
        }

    protected void parseGenotype(final EvolutionState state,
                                 final LineNumberReader reader) throws IOException, CloneNotSupportedException
        {
        // read in the next line.  The first item is the number of genes
        String s = reader.readLine();
        DecodeReturn d = new DecodeReturn(s);
        Code.decode( d );
        int lll = (int)(d.l);

        genome = new VectorGene[ lll ];

        GeneVectorSpecies _species = (GeneVectorSpecies) species;
        for( int i = 0 ; i < genome.length ; i++ )
            {
            genome[i] = (VectorGene)(_species.genePrototype.protoClone());
            genome[i].readGene(state,reader);
            }
        }

/*
  public void readIndividual(final EvolutionState state, 
  final LineNumberReader reader)
  throws IOException, CloneNotSupportedException
  {
  // species was already set before readIndividual(...) was invoked...
  GeneVectorSpecies _species = (GeneVectorSpecies) species;
        
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

  // read in the next line.  The first item is the number of genes
  s = reader.readLine();
  d.reset(s);
  Code.decode( d );
  int lll = (int)(d.l);

  genome = new VectorGene[ lll ];

  for( int i = 0 ; i < genome.length ; i++ )
  {
  genome[i] = (VectorGene)(_species.genePrototype.protoClone());
  genome[i].readGene(state,reader);
  }

  }

  public void printIndividualForHumans(final EvolutionState state,
  final int log, 
  final int verbosity)
  {
  state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
  verbosity, log);
  fitness.printFitnessForHumans(state,log,verbosity);
  for( int i = 0 ; i < genome.length ; i++ )
  {
  state.output.println( "Gene " + i + ":", verbosity, log );
  genome[i].printGeneForHumans( state, verbosity, log );
  }
  state.output.println( "", verbosity, log );
  }

  public void printIndividual(final EvolutionState state,
  final int log, 
  final int verbosity)
  {
  state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
  verbosity, log);
  fitness.printFitness(state,log,verbosity);
  StringBuffer s = new StringBuffer();
  s.append( Code.encode( genome.length ) );
  for( int i = 0 ; i < genome.length ; i++ )
  genome[i].printGene(state,log,verbosity);
  state.output.println( s.toString(), verbosity, log );
  }

  public void printIndividual(final EvolutionState state,
  final PrintWriter writer)
  {
  writer.println(EVALUATED_PREAMBLE + Code.encode(evaluated));
  fitness.printFitness(state,writer);
  StringBuffer s = new StringBuffer();
  s.append( Code.encode( genome.length ) );
  for( int i = 0 ; i < genome.length ; i++ )
  genome[i].printGene(state,writer);
  writer.println( s );
  }
*/

    public boolean equals(Object ind)
        {
        if (!(this.getClass().equals(ind.getClass()))) return false;
        GeneVectorIndividual i = (GeneVectorIndividual)ind;
        if( genome.length != i.genome.length )
            return false;
        for( int j = 0 ; j < genome.length ; j++ )
            if( !(genome[j].equals(i.genome[j])))
                return false;
        return true;
        }

    public Object getGenome()
        { return genome; }
    public void setGenome(Object gen)
        { genome = (VectorGene[]) gen; }
    public long genomeLength()
        { return genome.length; }
    }
