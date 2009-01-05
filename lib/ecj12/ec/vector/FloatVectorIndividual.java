
package ec.vector;

import ec.*;
import ec.util.*;
import java.io.Serializable;
import java.util.*;
import java.io.*;

/*
 * FloatVectorIndividual.java
 * Created: Thu Mar 22 13:13:20 EST 2001
 */

/**
 * FloatVectorIndividual is a VectorIndividual whose genome is an array of floats.
 * Gene values may range from species.mingene(x) to species.maxgene(x), inclusive.
 * The default mutation method randomizes genes to new values in this range,
 * with <tt>species.mutationProbability</tt>.
 *

 <p><b>Default Base</b><br>
 vector.float-vect-ind

 * @author Liviu Panait
 * @version 1.0
 */

public class FloatVectorIndividual extends VectorIndividual
    {
    public static final String P_FLOATVECTORINDIVIDUAL = "float-vect-ind";
    public float[] genome;

    public Parameter defaultBase()
        {
        return VectorDefaults.base().push(P_FLOATVECTORINDIVIDUAL);
        }

    public Object protoClone() throws CloneNotSupportedException
        {
        FloatVectorIndividual myobj = (FloatVectorIndividual) (super.protoClone());

        // must clone the genome
        myobj.genome = (float[])(genome.clone());
        
        return myobj;
        }

    public void setup(final EvolutionState state, final Parameter base)
        {
        // since VectorSpecies set its constraint values BEFORE it called
        // super.setup(...) [which in turn called our setup(...)], we know that
        // stuff like genomeSize has already been set...
        
        Parameter def = defaultBase();
        
        if (!(species instanceof FloatVectorSpecies)) 
            state.output.fatal("FloatVectorIndividual requires an FloatVectorSpecies", base, def);
        FloatVectorSpecies s = (FloatVectorSpecies) species;
        
        genome = new float[s.genomeSize];
        }
        
    public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind)
        {
        FloatVectorSpecies s = (FloatVectorSpecies) species;
        FloatVectorIndividual i = (FloatVectorIndividual) ind;
        float tmp;
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
            pieces[x] = new float[point1-point0];
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
            sum += ((float[])(pieces[x])).length;
        
        int runningsum = 0;
        float[] newgenome = new float[sum];
        for(int x=0;x<pieces.length;x++)
            {
            System.arraycopy(pieces[x], 0, newgenome, runningsum, ((float[])(pieces[x])).length);
            runningsum += ((float[])(pieces[x])).length;
            }
        // set genome
        genome = newgenome;
        }

    /** Destructively mutates the individual in some default manner.  The default form
        simply randomizes genes to a uniform distribution from the min and max of the gene values. */
    public void defaultMutate(EvolutionState state, int thread)
        {
        FloatVectorSpecies s = (FloatVectorSpecies) species;
        if (s.individualGeneMinMaxUsed())
            {
            if (s.mutationProbability>0.0)
                for(int x=0;x<genome.length;x++)
                    if (state.random[thread].nextBoolean(s.mutationProbability))
                        genome[x] = (float)((float)s.minGene(x) + state.random[thread].nextFloat()*((float)s.maxGene(x)-(float)s.minGene(x)));
            }
        else  // quite a bit faster
            {
            if (s.mutationProbability>0.0)
                for(int x=0;x<genome.length;x++)
                    if (state.random[thread].nextBoolean(s.mutationProbability))
                        genome[x] = (float)((float)s.minGene + state.random[thread].nextFloat()*((float)s.maxGene-(float)s.minGene));
            }

        /*if (mutationProbability>0.0)
          for(int x=0;x<genome.length;x++)
          if (state.random[thread].nextBoolean(mutationProbability))
          genome[x] = (float)(mingene + state.random[thread].nextFloat()*(maxgene-mingene)); */
        }

    /** Initializes the individual by randomly choosing floats uniformly from mingene to maxgene. */
    public void reset(EvolutionState state, int thread)
        {
        FloatVectorSpecies s = (FloatVectorSpecies) species;
        if (s.individualGeneMinMaxUsed())
            for(int x=0;x<genome.length;x++)
                genome[x] = (float)((float)s.minGene(x) + state.random[thread].nextFloat()*((float)s.maxGene(x)-(float)s.minGene(x)));
        else // quite a bit faster
            for(int x=0;x<genome.length;x++)
                genome[x] = (float)((float)s.minGene + state.random[thread].nextFloat()*((float)s.maxGene-(float)s.minGene));

/*        for(int x=0;x<genome.length;x++)
          genome[x] = (float)(mingene + state.random[thread].nextFloat()*(maxgene-mingene));
*/
        }

    public int hashCode()
        {
        // stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        for(int x=0;x<genome.length;x++)
            hash = ( hash << 1 | hash >>> 31 ) ^ Float.floatToIntBits(genome[x]);

        return hash;
        }

    public String genotypeToStringForHumans()
        {
        String s = "";
        for( int i = 0 ; i < genome.length ; i++ )
            s = s + " " + genome[i];
        return s;
        }
        
    public String genotypeToString()
        {
        StringBuffer s = new StringBuffer();
        s.append( Code.encode( genome.length ) );
        for( int i = 0 ; i < genome.length ; i++ )
            s.append( Code.encode( genome[i] ) );
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

        genome = new float[ lll ];

        // read in the genes
        for( int i = 0 ; i < genome.length ; i++ )
            {
            Code.decode( d );
            genome[i] = (float)(d.d);
            }
        }

/*
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

  // read in the next line.  The first item is the number of genes
  s = reader.readLine();
  d.reset(s);
  Code.decode( d );
  int lll = (int)(d.l);

  genome = new float[ lll ];

  // read in the genes
  for( int i = 0 ; i < genome.length ; i++ )
  {
  Code.decode( d );
  genome[i] = (float)(d.d);
  }
  }

  public void printIndividualForHumans(final EvolutionState state,
  final int log, 
  final int verbosity)
  {
  state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
  verbosity, log);
  fitness.printFitnessForHumans(state,log,verbosity);
  String s = "";
  for( int i = 0 ; i < genome.length ; i++ )
  s = s + " " + genome[i];
  state.output.println( s, verbosity, log );
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
  s.append( Code.encode( genome[i] ) );
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
  s.append( Code.encode( genome[i] ) );
  writer.println( s );
  }
*/

    public boolean equals(Object ind)
        {
        if (!(this.getClass().equals(ind.getClass()))) return false; // SimpleRuleIndividuals are special.
        FloatVectorIndividual i = (FloatVectorIndividual)ind;
        if( genome.length != i.genome.length )
            return false;
        for( int j = 0 ; j < genome.length ; j++ )
            if( genome[j] != i.genome[j] )
                return false;
        return true;
        }

    public Object getGenome()
        { return genome; }
    public void setGenome(Object gen)
        { genome = (float[]) gen; }
    public long genomeLength()
        { return genome.length; }
    }
