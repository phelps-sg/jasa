package ec.gp;
import java.io.Serializable;
import ec.*;
import ec.util.*;
import java.util.*;
import java.io.*;

/* 
 * GPIndividual.java
 * 
 * Created: Fri Aug 27 17:07:45 1999
 * By: Sean Luke
 */

/**
 * GPIndividual is an Individual used for GP evolution runs.
 * GPIndividuals contain, at the very least, a nonempty array of GPTrees.
 * You can use GPIndividual directly, or subclass it to extend it as
 * you see fit.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>numtrees</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(number of trees in the GPIndividual)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>tree.</tt><i>n</i><br>
 <font size=-1>classname, inherits or = ec.gp.GPTree</font></td>
 <td valign=top>(class of tree <i>n</i> in the individual)</td></tr>
 </table>

 <p><b>Default Base</b><br>
 gp.individual

 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>tree.</tt><i>n</i></td>
 <td>tree <i>n</i> in the individual</td></tr>
 </table>

 *
 * @author Sean Luke
 * @version 1.0 
 */

public class GPIndividual extends Individual
    {
    public static final String P_INDIVIDUAL = "individual";
    public static final String P_NUMTREES = "numtrees";
    public static final String P_TREE = "tree";

    public static final String EVALUATED_PREAMBLE = "Evaluated: ";
    
    public GPTree[] trees;
    
    public Parameter defaultBase()
        {
        return GPDefaults.base().push(P_INDIVIDUAL);
        }

    public boolean equals(Object ind)
        {
        if (!(this.getClass().equals(ind.getClass()))) return false;  // GPIndividuals are special.
        GPIndividual i = (GPIndividual)ind;
        if (trees.length != i.trees.length) return false;
        // this default version works fine for most GPIndividuals.
        for(int x=0;x<trees.length;x++)
            if (!(trees[x].treeEquals(i.trees[x]))) return false;
        return true;
        }
    
    public int hashCode()
        {
        // stolen from GPNode.  It's a decent algorithm.
        int hash = this.getClass().hashCode();
        
        for(int x=0;x<trees.length;x++)
            hash =
                // Rotate hash and XOR
                (hash << 1 | hash >>> 31 ) ^
                trees[x].treeHashCode();
        return hash;
        }

    /** Sets up a prototypical GPIndividual with those features which it
        shares with other GPIndividuals in its species, and nothing more. */

    public void setup(final EvolutionState state, final Parameter base)
        {
        Parameter def = defaultBase();

        // set my evaluation to false
        evaluated = false;

        // how many trees?
        int t = state.parameters.getInt(base.push(P_NUMTREES),def.push(P_NUMTREES),1);  // at least 1 tree for GP!
        if (t <= 0) 
            state.output.fatal("A GPIndividual must have at least one tree.",
                               base.push(P_NUMTREES),def.push(P_NUMTREES));
        
        // load the trees
        trees = new GPTree[t];

        for (int x=0;x<t;x++)
            {
            Parameter p = base.push(P_TREE).push(""+x);
            trees[x] = (GPTree)(state.parameters.getInstanceForParameterEq(
                                    p,def.push(P_TREE).push(""+x),GPTree.class));
            trees[x].owner = this;
            trees[x].setup(state,p);
            }
        
        // now that our function sets are all associated with trees,
        // give the nodes a chance to determine whether or not this is
        // going to work for them (especially the ADFs).
        
        for (int x=0;x<t;x++)
            {
            for(int w = 0;w < trees[x].constraints().functionset.nodes.length;w++)
                {
                GPFuncInfo[] gpfi = trees[x].constraints().functionset.nodes[w];
                for (int y = 0;y<gpfi.length;y++)
                    gpfi[y].node.checkConstraints(state,x,this,base);
                }
            }
        // because I promised with checkConstraints(...)
        state.output.exitIfErrors();
        }

    /** A printer for the individual in a reasonable human-readable,
        fashion.  The default version prints out whether the
        individual has been evaluated, what its fitness is, and
        each of its trees in turn.     Don't write the species.
        Modify this function as you see fit.  */
    public void printIndividualForHumans(final EvolutionState state, final int log, 
                                         final int verbosity)
        {
        state.output.println(EVALUATED_PREAMBLE + (evaluated ? "true" : "false"), 
                             verbosity, log);
        fitness.printFitnessForHumans(state,log,verbosity);
        for(int x=0;x<trees.length;x++)
            {
            state.output.println("Tree " + x + ":",verbosity,log);
            trees[x].printTreeForHumans(state,log,verbosity);
            }
        }

    /** Prints the individual in a way that it can be read in again
        by computer.  The default version prints out whether the
        individual has been evaluated, what its fitness is, and
        each of its trees in turn.    Don't write the species.
        Modify this function as you see fit. */
    public void printIndividual(final EvolutionState state, final int log, 
                                final int verbosity)
        {
        state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
                             verbosity, log);
        fitness.printFitness(state,log,verbosity);
        for(int x=0;x<trees.length;x++)
            {
            state.output.println("Tree " + x + ":",verbosity,log);
            trees[x].printTree(state,log,verbosity);
            }   
        }
            
    /** Prints the individual in a way that it can be read in again
        by computer.  The default version prints out whether the
        individual has been evaluated, what its fitness is, and
        each of its trees in turn.  Don't write the species.
        Modify this function as you see fit. */
    public void printIndividual(final EvolutionState state,
                                final PrintWriter writer)
        {
        writer.println(EVALUATED_PREAMBLE + Code.encode(evaluated));
        fitness.printFitness(state,writer);
        for(int x=0;x<trees.length;x++)
            {
            writer.println("Tree " + x + ":");
            trees[x].printTree(state,writer);
            }   
        }

    /** Reads in the individual from a form printed by printIndividual().
        The Fitness should have already been assigned to the individual at this
        point.  Don't read in the species, it should also get set externally
        by whoever calls this function. */ 
    
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

        // Next, read my trees
        for(int x=0;x<trees.length;x++)
            {
            reader.readLine();  // throw it away -- it's the tree indicator
            trees[x].readTree(state,reader);
            }

        }
        
    public Individual deepClone()
        {
        GPIndividual myobj = (GPIndividual)(super.deepClone());
        // re-deep-clone the trees; a little inefficient, but it works.
        // we already light-cloned the trees, so everything is set up
        // except for the actual nodes. o 
        for(int x=0;x<trees.length;x++)
            myobj.trees[x] = (GPTree)(trees[x].deepClone());
        return myobj;
        }

    public Object protoClone() throws CloneNotSupportedException
        {
        GPIndividual myobj = (GPIndividual)(super.protoClone());
        
        // deep-cloned stuff -- note that although we're deep-cloning
        // the trees, GPTree only light-clones its nodes.  So the
        // trees won't get copied.  Which is okay for the purposes
        // of this object.
        myobj.trees = new GPTree[trees.length];
        for(int x=0;x<trees.length;x++)
            {
            myobj.trees[x] = (GPTree)(trees[x].protoClone());  // note light-cloned!
            myobj.trees[x].owner = myobj;  // reset owner away from me
            }
        return myobj;
        }

    /** Returns the "size" of the individual, namely, the number of nodes
        in all of its subtrees.  */
    public long size()
        {
        long size = 0;
        for(int x=0;x<trees.length;x++)
            size += trees[x].child.numNodes(GPNode.NODESEARCH_ALL);
        return size;
        }

    }
