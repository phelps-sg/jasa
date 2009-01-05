package ec.app.ecsuite;

import ec.util.*;
import ec.*;
import ec.simple.*;
import ec.vector.*;

/* 
 * ECSuite.java
 * 
 * Created: Thu MAr 22 16:27:15 2001
 * By: Liviu Panait and Sean Luke
 */

/*
 * @author Liviu Panait and Sean Luke
 * @version 1.0 
 */

/**
   Several standard Evolutionary Computation functions are implemented.  The Rastrigin and
   and F1-F4 problems (Sphere, Rosenbrock, Step, Noisy-Quartic) from De Jong's test suite are
   minimization problems, and the function
   values are supposed to be non-negative, with 0.0 the absolute minimum.  As the SimpleFitness
   allows fitnesses between 0.0 and infinity, with infinity greater than the maximum, and it is
   used for maximization problems, the mapping x --> 1.0 / ( 1.0 + x ) is used to transform the
   problems into maximization ones, where 1.0 is the maximum and 0.0 is the worst value.

   <p><b>Parameters</b><br>
   <table>
   <tr><td valign=top><i>base</i>.<tt>type</tt><br>
   <font size=-1>String, one of: rosenbrock rastrigin sphere step noisy-quartic kdj-f1 kdj-f2 kdj-f3 [or] kdj-f4</font>/td>
   <td valign=top>(The vector problem to test against.  Some of the types are synonyms: kdj-f1 = sphere, kdj-f2 = rosenbrock, kdj-f3 = step, kdj-f4 = noisy-quartic.  "kdj" stands for "Ken DeJong", and the numbers are the problems in his test suite)</td></tr>
   </table>


*/
 
public class ECSuite extends Problem implements SimpleProblemForm
    {

    EvolutionState state;

    public static final String P_WHICH_PROBLEM = "type";
    public static final String P_ROSENBROCK = "rosenbrock";
    public static final String P_RASTRIGIN = "rastrigin";
    public static final String P_SPHERE = "sphere";
    public static final String P_STEP = "step";
    public static final String P_NOISY_QUARTIC = "noisy-quartic";
    public static final String P_F1 = "kdj-f1";
    public static final String P_F2 = "kdj-f2";
    public static final String P_F3 = "kdj-f3";
    public static final String P_F4 = "kdj-f4";

    public static final int PROB_ROSENBROCK = 0;
    public static final int PROB_RASTRIGIN = 1;
    public static final int PROB_SPHERE = 2;
    public static final int PROB_STEP = 3;
    public static final int PROB_NOISY_QUARTIC = 4;
    
    public int problemType = PROB_ROSENBROCK;  // defaults on Rosenbrock

    // for RASTRIGIN function
    public final static float A = 10.0f;

    // nothing....
    public void setup(final EvolutionState state_, final Parameter base)
        {
        state = state_;
        String wp = state.parameters.getStringWithDefault( base.push( P_WHICH_PROBLEM ), null, "" );
        if( wp.compareTo( P_ROSENBROCK ) == 0 || wp.compareTo (P_F2)==0 )
            problemType = PROB_ROSENBROCK;
        else if ( wp.compareTo( P_RASTRIGIN ) == 0 )
            problemType = PROB_RASTRIGIN;
        else if ( wp.compareTo( P_SPHERE ) == 0 || wp.compareTo (P_F1)==0) 
            problemType = PROB_SPHERE;
        else if ( wp.compareTo( P_STEP ) == 0 || wp.compareTo (P_F3)==0)
            problemType = PROB_STEP;
        else if ( wp.compareTo( P_NOISY_QUARTIC ) == 0 || wp.compareTo (P_F4)==0)
            problemType = PROB_NOISY_QUARTIC;
        else state.output.fatal(
            "Invalid value for parameter, or parameter not found.\n" +
            "Acceptable values are:\n" +
            "  " + P_ROSENBROCK + "(or " + P_F2 + ")\n" +
            "  " + P_RASTRIGIN + "\n" +
            "  " + P_SPHERE + "(or " + P_F1 + ")\n" +
            "  " + P_STEP + "(or " + P_F3 + ")\n" +
            "  " + P_NOISY_QUARTIC + "(or " + P_F4 + ")\n",
            base.push( P_WHICH_PROBLEM ) );
        }

    public void evaluate(final EvolutionState _state,
                         final Individual ind,
                         final int threadnum)
        {

        if( !( ind instanceof DoubleVectorIndividual ) )
            _state.output.fatal( "The individuals for this problem should be DoubleVectorIndividuals." );

        DoubleVectorIndividual temp = (DoubleVectorIndividual)ind;
        double[] genome = temp.genome;
        int len = genome.length;
        double value = 0;

        switch(problemType)
            {
            case PROB_ROSENBROCK:
                for( int i = 1 ; i < len ; i++ )
                    value += 100*(genome[i-1]*genome[i-1]-genome[i])*
                        (genome[i-1]*genome[i-1]-genome[i]) +
                        (1-genome[i-1])*(1-genome[i-1]);
                value = 1.0 / ( 1.0 + value );
                ((SimpleFitness)(ind.fitness)).setFitness( state, (float)value, value==1.0 );
                break;
                
            case PROB_RASTRIGIN:
                value = len * A;
                for( int i = 0 ; i < len ; i++ )
                    value += ( genome[i]*genome[i] - A * Math.cos( 2 * Math.PI * genome[i] ) );
                value = 1.0 / ( 1.0 + value );
                ((SimpleFitness)(ind.fitness)).setFitness( state, (float)value, value==1.0 );
                break;
                
            case PROB_SPHERE:
                for( int i = 0 ; i < len ; i++ )
                    value += genome[i]*genome[i];
                value = 1.0 / ( 1.0 + value );
                ((SimpleFitness)(ind.fitness)).setFitness( state, (float)value, value==1.0 );
                break;

            case PROB_STEP:
                for( int i = 0 ; i < len ; i++ )
                    value += 6 + Math.floor( genome[i] );
                value = 1.0 / ( 1.0 + value );
                ((SimpleFitness)(ind.fitness)).setFitness( state, (float)value, value==1.0 );
                break;

            case PROB_NOISY_QUARTIC:
                for( int i = 0 ; i < len ; i++ )
                    value += (i+1)*(genome[i]*genome[i]*genome[i]*genome[i]) + // no longer : Math.pow( genome[i], 4 ) +
                        state.random[threadnum].nextDouble();
                value = 1.0 / ( 1.0 + value );
                ((SimpleFitness)(ind.fitness)).setFitness( state, (float)value, value==1.0 );
                break;

            default:
                state.output.fatal( "ec.app.ecsuite.ECSuite has an invalid problem -- how on earth did that happen?" );
                break;
            }

        ind.evaluated = true;
        }

    public void describe(final Individual ind, 
                         final EvolutionState _state, 
                         final int threadnum,
                         final int log,
                         final int verbosity)
        {
        return;
        }
    }
