package ec;
import ec.util.*;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OptionalDataException;
import java.io.PrintWriter;

/* 
 * Evolve.java
 * 
 * Created: Wed Aug 11 17:49:01 1999
 * By: Sean Luke
 */

/**
 * Evolve is the main entry class for an evolutionary computation run.
 *
 * <p> An EC run is done with one of two argument formats:
 *
 * <p><tt>java ec.Evolve -file </tt><i>parameter_file [</i><tt>-p </tt><i>parameter=value]*</i>
 *
 * <p>This starts a new evolutionary run, using the parameter file <i>parameter_file</i>.
 * The user can provide optional overriding parameters on the command-line with the <tt>-p</tt> option.
 *
 * <p><tt>java ec.Evolve -checkpoint </tt><i>checkpoint_file</i>
 * 
 * <p>This starts up an evolutionary run from a previous checkpoint file.
 *
 * <p><b>Debugging with <a href="http://www.beanshell.org">BeanShell</a></b>.
 * The <tt>jdb</tt> debugger isn't very good.  In fact, it's just plain awful.
 * One approach around this is to use BeanShell, a full on-line shell system
 * for Java.  BeanShell basically provides you with a Java listener, just like
 * a Lisp listener (command line).  And it's free!  You should definitely try
 * it out.
 *
 * <p>To try out this feature, Evolve provides a special method, <tt>make</tt>,
 * which works much like Evolve.main(), except that instead of running the EvolutionState,
 * it just returns it.  You can then call the go() method on the EvolutionState to
 * pulse it through its steps (if your particular EvolutionState supports debug-pulsing).
 * Each go() method prints out the current state (evaluating, 
 * initializing, breeding, whatever),
 * plus the present generation or pseudogeneration.  Other than that, everything is printed
 * as normal.
 * For example, let's say you want to look at the third individual in the 4th generation.
 * In bsh, you might do something like:

 <p><table width=100% border=0 cellpadding=0 cellspacing=0>
 <tr><td bgcolor="#DDDDDD"><font size=-1><tt>
 <pre><b>jifsan> bsh</b>
 BeanShell 1.0 beta - by Pat Niemeyer (pat@pat.net)
 <b>bsh % e = ec.Evolve.make(new String[] {"-file", "params"});</b>

 | ECJ
 | An evolutionary computation system (version 1)
 | Copyright 1999 by Sean Luke
 | Mail: seanl@cs.umd.edu
 | URL: http://www.cs.umd.edu/users/seanl/
 | Date: Tuesday, November 16, 1999
 | Suggested Java version: 1.2.2/Hotspot
 | Minimum Java version: 1.1


 <b>bsh % e.go();</b>
 <i>0) DEBUG: INIT</i>
 Setting up
 Processing GP Types
 Processing GP Node Constraints
 Processing GP Function Sets
 Processing GP Tree Constraints
 WARNING:
 Ant trail file ended prematurely
 Initializing Generation 0
 Using default GPNodeSelector for #0 of pop.subpop.0.species.pipe.0
 Using default terminal probability for KozaNodeSelector gp.koza.xover.ns
 Using default nonterminal probability for KozaNodeSelector gp.koza.xover.ns
 Using default root probability for KozaNodeSelector gp.koza.xover.ns
 Using default GPNodeSelector for #1 of pop.subpop.0.species.pipe.0
 Using default terminal probability for KozaNodeSelector gp.koza.xover.ns
 Using default nonterminal probability for KozaNodeSelector gp.koza.xover.ns
 Using default root probability for KozaNodeSelector gp.koza.xover.ns
 Using default numTries for pop.subpop.0.species.pipe.0
 Using default maxDepth for pop.subpop.0.species.pipe.0
 <b>bsh % e.go();</b>
 <i>1) DEBUG: EVAL, Generation 0</i>
 <b>bsh % e.go();</b>
 <i>2) DEBUG: BREED, Generation 0</i>
 Generation 1
 <b>bsh % e.go(5);</b>
 <i>3) DEBUG: EVAL, Generation 1</i>
 <i>4) DEBUG: BREED, Generation 1</i>
 Generation 2
 <i>5) DEBUG: EVAL, Generation 2</i>
 <i>6) DEBUG: BREED, Generation 2</i>
 Generation 3
 <i>7) DEBUG: EVAL, Generation 3</i>
 <b>bsh % e.go();</b>
 <i>8) DEBUG: BREED, Generation 3</i>
 Generation 4
 <b>bsh % e.population.subpops[0].individuals[3].printIndividualForHumans(e,0,3000);</b>
 Evaluated: false
 Fitness: Raw=56.0 Adjusted=0.01754386 Hits=33
 Tree 0:
 &nbsp;(progn2 move (if-food-ahead (if-food-ahead
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(progn2 left right) (progn3 right (progn3
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;move (progn2 move left) right) left)) (progn3
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;right (progn3 move (progn2 move left) right)
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;left)))
 </pre></tt></font></td></tr></table>

 <p><b>Parameters</b><br>
 <table>

 <tr><td valign=top><tt>nostore</tt><br>
 <font size=-1> bool = <tt>true</tt> or <tt>false</tt> (default)</font></td>
 <td valign=top>(should the ec.util.Output facility <i>not</i> store announcements in memory?)</td></tr>

 <tr><td valign=top><tt>flush</tt><br>
 <font size=-1> bool = <tt>true</tt> or <tt>false</tt> (default)</font></td>
 <td valign=top>(should I flush all output as soon as it's printed (useful for debugging when an exception occurs))</td></tr>

 <tr><td valign=top><tt>verbosity</tt><br>
 <font size=-1>int &gt;= 0</font></td>
 <td valign=top>(the ec.util.Output object's verbosity)</td></tr>

 <tr><td valign=top><tt>evalthreads</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(the number of threads to spawn for evaluation)</td></tr>

 <tr><td valign=top><tt>breedthreads</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(the number of threads to spawn for breeding)</td></tr>

 <tr><td valign=top><tt>seed.</tt><i>n</i><br>
 <font size=-1>int != 0, or string  = <tt>time</tt></font></td>
 <td valign=top>(the seed for random number generator #<i>n</i>.  <i>n</i> should range from 0 to Max(evalthreads,breedthreads)-1.  If value is <tt>time</tt>, then the seed is based on the system clock plus <i>n</i>.)</td></tr>

 <tr><td valign=top><tt>state</tt><br>
 <font size=-1>classname, inherits and != ec.EvolutionState</font></td>
 <td valign=top>(the EvolutionState object class)</td></tr>

 <tr><td valign=top><tt>print-accessed-params</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</td>
 <td valign=top>(at the end of a run, do we print out a list of all the parameters requested during the run?)</td></tr>

 <tr><td valign=top><tt>print-used-params</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</td>
 <td valign=top>(at the end of a run, do we print out a list of all the parameters actually <i>used</i> during the run?)</td></tr>

 <tr><td valign=top><tt>print-unaccessed-params</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</td>
 <td valign=top>(at the end of a run, do we print out a list of all the parameters NOT requested during the run?)</td></tr>

 <tr><td valign=top><tt>print-unused-params</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</td>
 <td valign=top>(at the end of a run, do we print out a list of all the parameters NOT actually used during the run?)</td></tr>

 <tr><td valign=top><tt>print-all-params</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</td>
 <td valign=top>(at the end of a run, do we print out a list of all the parameters stored in the parameter database?)</td></tr>

 </table>
 * 
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class Evolve 
    {

    public final static String P_PRINTACCESSEDPARAMETERS = "print-accessed-params";
    public final static String P_PRINTUSEDPARAMETERS = "print-used-params";
    public final static String P_PRINTALLPARAMETERS = "print-all-params";
    public final static String P_PRINTUNUSEDPARAMETERS = "print-unused-params";
    public final static String P_PRINTUNACCESSEDPARAMETERS = "print-unaccessed-params";

    /** The argument indicating that we're starting up from a checkpoint file. */
    public static final String A_CHECKPOINT = "-checkpoint";
    
    /** The argument indicating that we're starting fresh from a new parameter file. */
    public static final String A_FILE = "-file";

    /** flush announcements parameter */
    public static final String P_FLUSH = "flush";

    /** nostore parameter */
    public static final String P_STORE = "store";

    /** verbosity parameter */
    public static final String P_VERBOSITY = "verbosity";

    /** evalthreads parameter */
    public static final String P_EVALTHREADS = "evalthreads";

    /** breedthreads parameter */
    public static final String P_BREEDTHREADS = "breedthreads";

    /** seed parameter */
    public static final String P_SEED = "seed";

    /** 'time' seed parameter value */
    public static final String V_SEED_TIME = "time";

    /** state parameter */
    public static final String P_STATE = "state";



    /** The entry method for an evolutionary run.  Loads either from a checkpoint file, 
        or loads a parameter file and sets up from that.  */
        
/*    public static void main(String[] args)
      {
      main2(args);
      // at this point, I believe the Output should be garbage-collectable.
      // so to force the output of gzipped and other buffered logs, we 
      // run finalizers.
      System.gc();
      //Runtime.getRuntime().runFinalization();
      System.exit(0);
      }
*/
            
    public static void main(String[] args)
        {
        // First we determine if we're loading from a checkpoint file
        
        EvolutionState state=null;
        ParameterDatabase parameters=null;
        Output output;
        MersenneTwisterFast[] random;
        int[] seeds;
        int breedthreads = 1;
        int evalthreads = 1;
        int verbosity;
        boolean store;
        int x;


        // first we load from a checkpoint file if the user requested
        // that and then we start up from there.

        for(x=0;x<args.length-1;x++)
            if (args[x].equals(A_CHECKPOINT))
                {
                try
                    {
                    System.err.println("Restoring from Checkpoint " + args[x+1]);
                    state=Checkpoint.restoreFromCheckpoint(args[x+1]);
                    state.run(EvolutionState.C_STARTED_FROM_CHECKPOINT);
                    }
                catch(OptionalDataException e)
                    {
                    Output.initialError(
                        "A ClassNotFoundException was generated upon" +
                        "starting up from a checkpoint." +
                        "\nHere it is:\n" + e); 
                    }
                catch(ClassNotFoundException e)
                    {
                    Output.initialError(
                        "A ClassNotFoundException was generated upon" +
                        "starting up from a checkpoint." +
                        "\nHere it is:\n" + e); 
                    }
                catch (IOException e)
                    { 
                    Output.initialError(
                        "An IO Exception was generated upon" +
                        "starting up, probably in setting up a log" +
                        "\nHere it is:\n" + e); 
                    }
                System.exit(0);
                }

        // at this point, we don't have a checkpoint file so we try
        // reading instead from a parameter file and starting up fresh
        
        if (state==null) // couldn't find a checkpoint argument
            {

            // 0. find the parameter database
            for(x=0;x<args.length-1;x++)
                if (args[x].equals(A_FILE))
                    {
                    try
                        {
                        parameters=new ParameterDatabase(
                            // not available in jdk1.1: new File(args[x+1]).getAbsoluteFile(),
                            new File(new File(args[x+1]).getAbsolutePath()),
                            args);
                        break;
                        }
                    catch(FileNotFoundException e)
                        { Output.initialError(
                            "A File Not Found Exception was generated upon" +
                            "reading the parameter file \"" + args[x+1] + 
                            "\".\nHere it is:\n" + e); }
                    catch(IOException e)
                        { Output.initialError(
                            "An IO Exception was generated upon reading the" +
                            "parameter file \"" + args[x+1] +
                            "\".\nHere it is:\n" + e); } 
                    }
            if (parameters==null)
                Output.initialError(
                    "No parameter file was specified." ); 


            // 1. create the output
            store = parameters.getBoolean(new Parameter(P_STORE),null,false);

            verbosity = parameters.getInt(new Parameter(P_VERBOSITY),null,0);
            if (verbosity<0)
                Output.initialError("Verbosity should be an integer >= 0.\n",
                                    new Parameter(P_VERBOSITY)); 

            output = new Output(store,verbosity);
            output.setFlush(
                parameters.getBoolean(new Parameter(P_FLUSH),null,false));


            // stdout is always log #0.  stderr is always log #1.
            // stderr accepts announcements, and both are fully verbose 
            // by default.
            output.addLog(ec.util.Log.D_STDOUT,Output.V_VERBOSE,false);
            output.addLog(ec.util.Log.D_STDERR,Output.V_VERBOSE,true);



            
            // 2. set up thread values

            breedthreads = parameters.getInt(
                new Parameter(P_BREEDTHREADS),null,1);

            if (breedthreads < 1)
                Output.initialError("Number of breeding threads should be an integer >0.",
                                    new Parameter(P_BREEDTHREADS));


            evalthreads = parameters.getInt(
                new Parameter(P_EVALTHREADS),null,1);

            if (evalthreads < 1)
                Output.initialError("Number of eval threads should be an integer >0.",
                                    new Parameter(P_EVALTHREADS));



            // 3. create the Mersenne Twister random number generators,
            // one per thread

            random = new MersenneTwisterFast[breedthreads > evalthreads ? 
                                             breedthreads : evalthreads];
            seeds = new int[breedthreads > evalthreads ? 
                            breedthreads : evalthreads];
           
            int time = (int)System.currentTimeMillis();  // safe because we're getting low-order bits 
            String seed_message = "Seed: ";
            for (x=0;x<random.length;x++)
                {
                int seed = 1;
                String tmp_s = parameters.getString(
                    new Parameter(P_SEED).push(""+x),null);
                if (tmp_s==null) // uh oh
                    {
                    Output.initialError("Seed should be an integer.",
                                        new Parameter(P_SEED).push(""+x));
                    
                    }
                else if (tmp_s.equalsIgnoreCase(V_SEED_TIME))
                    {
                    seed = time++;
                    if (seed==0)
                        Output.initialError("Whoa! This Java version is returning 0 for System.currentTimeMillis(), which ain't right.  This means you can't use '"+V_SEED_TIME+"' as a seed ",new Parameter(P_SEED).push(""+x));
                    else seed_message = seed_message + seed + " ";
                    }
                else
                    {
                    try
                        {
                        seed = parameters.getInt(new Parameter(P_SEED).push(""+x),null);
                        }
                    catch (NumberFormatException e)
                        {
                        Output.initialError("Invalid Seed Value (must be an integer):\n" + e);
                        }
                    seed_message = seed_message + seed + " ";
                    }
                    
                seeds[x] = seed;
                }

            for (x=0;x<random.length;x++)
                {
                for (int y=x+1;y<random.length;y++)
                    if (seeds[x]==seeds[y])
                        {
                        Output.initialError(P_SEED+"."+x+" ("+seeds[x]+") and "+P_SEED+"."+y+" ("+seeds[y]+") ought not be the same seed."); 
                        }
                random[x] = new MersenneTwisterFast(seeds[x]);
                }

            // 4.  Start up the evolution
            
            // what evolution state to use?
            state = (EvolutionState)
                parameters.getInstanceForParameter(new Parameter(P_STATE),null,
                                                   EvolutionState.class);
            state.parameters = parameters;
            state.random = random;
            state.output = output;
            state.evalthreads = evalthreads;
            state.breedthreads = breedthreads;

            output.systemMessage(Version.message());
            output.systemMessage("Threads:  breed/" + breedthreads + " eval/" + evalthreads);
            output.systemMessage(seed_message);
            
            try 
                {
                state.run(EvolutionState.C_STARTED_FRESH);
                }
            catch (IOException e)
                { 
                Output.initialError(
                    "An IO Exception was generated upon" +
                    "starting up, probably in setting up a log" +
                    "\nHere it is:\n" + e); 
                }
            
            // Possibly print out the run parameters

               
            // flush the output
            output.flush();

            PrintWriter pw = new PrintWriter(System.err);
            
            // before we print out access information, we need to still "get" these
            // parameters, so that they show up as accessed and gotten.
            parameters.getBoolean(new Parameter(P_PRINTUSEDPARAMETERS),null,false);
            parameters.getBoolean(new Parameter(P_PRINTACCESSEDPARAMETERS),null,false);
            parameters.getBoolean(new Parameter(P_PRINTUNUSEDPARAMETERS),null,false);
            parameters.getBoolean(new Parameter(P_PRINTUNACCESSEDPARAMETERS),null,false);
            parameters.getBoolean(new Parameter(P_PRINTALLPARAMETERS),null,false);
            
            //...okay, here we go...
            
            if (parameters.getBoolean(new Parameter(P_PRINTUSEDPARAMETERS),null,false))
                {
                pw.println("\n\nUsed Parameters\n===============\n");
                parameters.listGotten(pw);
                }

            if (parameters.getBoolean(new Parameter(P_PRINTACCESSEDPARAMETERS),null,false))
                {
                pw.println("\n\nAccessed Parameters\n===================\n");
                parameters.listAccessed(pw);
                }

            if (parameters.getBoolean(new Parameter(P_PRINTUNUSEDPARAMETERS),null,false))
                {
                pw.println("\n\nUnused Parameters\n"+
                           "================= (Ignore parent.x references) \n");
                parameters.listNotGotten(pw);
                }

            if (parameters.getBoolean(new Parameter(P_PRINTUNACCESSEDPARAMETERS),null,false))
                {
                pw.println("\n\nUnaccessed Parameters\n"+
                           "===================== (Ignore parent.x references) \n");
                parameters.listNotAccessed(pw);
                }

            if (parameters.getBoolean(new Parameter(P_PRINTALLPARAMETERS),null,false))
                {
                pw.println("\n\nAll Parameters\n==============\n");
                // list only the parameters visible.  Shadowed parameters not shown
                parameters.list(pw,false);
                }


            pw.flush();

            System.err.flush();
            System.out.flush();
            
            // finish by closing down Output.  This is because gzipped and other buffered
            // streams just don't shut write themselves out, and finalize isn't called
            // on them because Java's being obnoxious.  Pretty stupid.
            output.close();
            }
        }




    /** Creates an EvolutionState as normal, but returns
        it.  You can then pulse this EvolutionState with its go() method
        to get it to move through various rounds. */
    public static EvolutionState make(String[] args)
        {

        EvolutionState state=null;
        ParameterDatabase parameters=null;
        Output output;
        MersenneTwisterFast[] random;
        int[] seeds;
        int breedthreads = 1;
        int evalthreads = 1;
        int verbosity;
        boolean store;
        int x;


        // 0. find the parameter database
        for(x=0;x<args.length-1;x++)
            if (args[x].equals(A_FILE))
                {
                try
                    {
                    parameters=new ParameterDatabase(
                        // not available in jdk1.1: new File(args[x+1]).getAbsoluteFile(),
                        new File(new File(args[x+1]).getAbsolutePath()),
                        args);
                    break;
                    }
                catch(FileNotFoundException e)
                    { Output.initialError(
                        "A File Not Found Exception was generated upon" +
                        "reading the parameter file \"" + args[x+1] + 
                        "\".\nHere it is:\n" + e); }
                catch(IOException e)
                    { Output.initialError(
                        "An IO Exception was generated upon reading the" +
                        "parameter file \"" + args[x+1] +
                        "\".\nHere it is:\n" + e); } 
                }
        if (parameters==null)
            Output.initialError(
                "No parameter file was specified." ); 
        
        
        
        // 1. create the output
        store = parameters.getBoolean(new Parameter(P_STORE),null,false);
        
        verbosity = parameters.getInt(new Parameter(P_VERBOSITY),null,0);
        if (verbosity<0)
            Output.initialError("Verbosity should be an integer >= 0.\n",
                                new Parameter(P_VERBOSITY)); 
        
        output = new Output(store,verbosity);
        output.setFlush(
            parameters.getBoolean(new Parameter(P_FLUSH),null,false));
        
        
        // stdout is always log #0.  stderr is always log #1.
        // stderr accepts announcements, and both are fully verbose 
        // by default.
        output.addLog(ec.util.Log.D_STDOUT,Output.V_VERBOSE,false);
        output.addLog(ec.util.Log.D_STDERR,Output.V_VERBOSE,true);    

        
        
        // 2. set up thread values
        
        breedthreads = parameters.getInt(
            new Parameter(P_BREEDTHREADS),null,1);
        
        if (breedthreads < 1)
            Output.initialError("Number of breeding threads should be an integer >0.",
                                new Parameter(P_BREEDTHREADS));
        
        
        evalthreads = parameters.getInt(
            new Parameter(P_EVALTHREADS),null,1);
        
        if (evalthreads < 1)
            Output.initialError("Number of eval threads should be an integer >0.",
                                new Parameter(P_EVALTHREADS));


        
        // 3. create the Mersenne Twister random number generators,
        // one per thread
        
        random = new MersenneTwisterFast[breedthreads > evalthreads ? 
                                         breedthreads : evalthreads];
        seeds = new int[breedthreads > evalthreads ? 
                        breedthreads : evalthreads];
            
        int time = (int)System.currentTimeMillis();  // safe because we're getting low-order bits
        for (x=0;x<random.length;x++)
            {
            int seed = 1;
            String tmp_s = parameters.getString(
                new Parameter(P_SEED).push(""+x),null);
            if (tmp_s.equalsIgnoreCase(V_SEED_TIME))
                {
                seed = (int)System.currentTimeMillis(); // safe because we're getting low-order bits
                if (seed==0)
                    Output.initialError("Whoa! This Java version is returning 0 for System.currentTimeMillis(), which ain't right.  This means you can't use '"+V_SEED_TIME+"' as a seed ",new Parameter(P_SEED).push(""+x));
                }
            else
                {
                seed = parameters.getIntWithDefault(
                    new Parameter(P_SEED).push(""+x),null,0);
                if (seed==0)
                    Output.initialError("Seed should be an integer not equal to 0.",
                                        new Parameter(P_SEED).push(""+x));
                }
                
            seeds[x] = seed;
            }

        for (x=0;x<random.length;x++)
            {
            for (int y=x+1;y<random.length;y++)
                if (seeds[x]==seeds[y])
                    { 
                    Output.initialError(P_SEED+"."+x+" ("+seeds[x]+") and "+P_SEED+"."+y+" ("+seeds[y]+") ought not be the same seed."); 
                    }
            random[x] = new MersenneTwisterFast(seeds[x]);
            }
        
        // 4.  Start up the evolution
        
        // what evolution state to use?
        state = (EvolutionState)
            parameters.getInstanceForParameter(new Parameter(P_STATE),null,
                                               EvolutionState.class);
        state.parameters = parameters;
        state.random = random;
        state.output = output;
        state.evalthreads = evalthreads;
        state.breedthreads = breedthreads;
        
        output.systemMessage(Version.message());

        return state;
        }


    }

