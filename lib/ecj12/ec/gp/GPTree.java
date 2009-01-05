
/* 
 * GPTree.java
 * 
 * Created: Sat Jan 22 19:20:13 2000
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

package ec.gp;
import ec.*;
import ec.util.*;
import ec.EvolutionState;
import java.io.*;

/* 
 * GPTree.java
 * 
 * Created: Fri Aug 27 17:14:02 1999
 * By: Sean Luke
 */

/**
 * GPTree is a GPNodeParent which holds the root GPNode of a tree
 * of GPNodes.  GPTrees typically fill out an array held in a GPIndividual
 * (their "owner") and their roots are evaluated to evaluate a Genetic
 * programming tree.
 *
 * GPTrees also have <i>constraints</i>, which are shared, and define items
 * shared among several GPTrees.
 *
 * <p>GPTrees can print themselves for humans in one of two ways.  First, a GPTree can print
 * the tree as a Koza-style Lisp s-expression, which is the default.  
 * Second, a GPTree can print the tree as a LaTeX2e code snippet, which can be inserted
 * into a LaTeX2e file and will result in a picture of the tree!  Cool, no?
 *
 * <p>You turn this feature on with the <b>latex</b> parameter below.
 * Here's how it works.  To insert the code, you'll need to include the
 * <tt>epic</tt>,<tt>ecltree</tt>, and probably the <tt>fancybox</tt> packages,
 * in that order.  You'll also need to define the command <tt>\gpbox</tt>, which
 * takes one argument (the string name for the GPNode) and draws a box with that
 * node.  Lastly, you might want to set a few parameters dealing with the 
 * <tt>ecltree</tt> package.
 *
 * <p>Here's an example which looks quite good (pardon the double-backslashes
 * in front of the usepackage statements -- javadoc is freaking out if I put
 * a single backslash.  So you'll need to remove the extra backslash in order
 * to try out this example):
 
 <p><table width=100% border=0 cellpadding=0 cellspacing=0>
 <tr><td bgcolor="#DDDDDD"><font size=-1><tt>
 <pre>

 \documentclass[]{article}
 \\usepackage{epic}     <b>% required by ecltree and fancybox packages</b>
 \\usepackage{ecltree}  <b>% to draw the GP trees</b>
 \\usepackage{fancybox} <b>% required by \Ovalbox</b>

 \begin{document}

 <b>% minimum distance between nodes on the same line</b>
 \setlength{\GapWidth}{1em}    

 <b>% draw with a thick dashed line, very nice looking</b>
 \thicklines \drawwith{\dottedline{2}}   

 <b>% draw an oval and center it with the rule.  You may want to fool with the
 % rule values, though these seem to work quite well for me.  If you make the
 % rule smaller than the text height, then the GP nodes may not line up with
 % each other horizontally quite right, so watch out.</b>
 \newcommand{\gpbox}[1]{\Ovalbox{#1\rule[-.7ex]{0ex}{2.7ex}}}
                
 <b>% Here's the tree which the GP system spat out</b>
 \begin{bundle}{\gpbox{progn3}}\chunk{\begin{bundle}{\gpbox{if-food-ahead}}
 \chunk{\begin{bundle}{\gpbox{progn3}}\chunk{\gpbox{right}}
 \chunk{\gpbox{left}}\chunk{\gpbox{move}}\end{bundle}}
 \chunk{\begin{bundle}{\gpbox{if-food-ahead}}\chunk{\gpbox{move}}
 \chunk{\gpbox{left}}\end{bundle}}\end{bundle}}\chunk{\begin{bundle}{\gpbox{progn2}}
 \chunk{\begin{bundle}{\gpbox{progn2}}\chunk{\gpbox{move}}
 \chunk{\gpbox{move}}\end{bundle}}\chunk{\begin{bundle}{\gpbox{progn2}}
 \chunk{\gpbox{right}}\chunk{\gpbox{left}}\end{bundle}}\end{bundle}}
 \chunk{\begin{bundle}{\gpbox{if-food-ahead}}\chunk{\begin{bundle}{\gpbox{if-food-ahead}}
 \chunk{\gpbox{move}}\chunk{\gpbox{left}}\end{bundle}}
 \chunk{\begin{bundle}{\gpbox{if-food-ahead}}\chunk{\gpbox{left}}\chunk{\gpbox{right}}
 \end{bundle}}\end{bundle}}\end{bundle}

 \end{document}
 </pre></tt></font></td></tr></table>

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>tc</tt><br>
 <font size=-1>String</font></td>
 <td valign=top>(The tree's constraints)</td></tr>
 <tr><td valign=top><i>base</i>.<tt>latex</tt><br>
 <font size=-1>bool = <tt>true</tt> or <tt>false</tt> (default)</td>
 <td valign=top>(print for humans using latex?)</td></tr>
 </table>

 <p><b>Default Base</b><br>
 gp.tree

 * @author Sean Luke
 * @version 1.0 
 */

public class GPTree implements GPNodeParent
    {
    public static final String P_TREE = "tree";
    public static final String P_TREECONSTRAINTS = "tc";
    public static final String P_USELATEX = "latex";
    public static final int NO_TREENUM = -1;

    /** the root GPNode in the GPTree */
    public GPNode child;

    /** the owner of the GPTree */
    public GPIndividual owner;

    /** constraints on the GPTree  -- don't access the constraints through
        this variable -- use the constraints() method instead, which will give
        the actual constraints object. */
    public byte constraints;
    
    /** Use latex to print for humans? */
    public boolean useLatex;

    public final GPTreeConstraints constraints() 
        { return GPTreeConstraints.constraints[constraints]; }

    public Parameter defaultBase()
        {
        return GPDefaults.base().push(P_TREE);
        }

    /** Returns true if I am "genetically" the same as tree,
        though we may have different owners. */
    public boolean treeEquals(GPTree tree)
        {
        return child.rootedTreeEquals(tree.child);
        }

    /** Returns a hash code for comparing different GPTrees.  In
        general, two trees which are treeEquals(...) should have the
        same hash code. */
    public int treeHashCode()
        {
        return child.rootedTreeHashCode();
        }

    /** Proto-clones the tree but does NOT deep clone it.  The 
        tree's child is still the same, and not a copy.   Owner is not changed. */
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

    /** Deep-clones the tree, including all the nodes.  Owner is not changed.*/
    public GPTree deepClone()
        {
        try 
            {
            GPTree newtree = (GPTree)(protoClone());
            newtree.child = (GPNode)(child.cloneReplacing());
            newtree.child.parent = newtree;
            newtree.child.argposition = 0;
            return newtree;
            }
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        }
    
    /** An expensive function which determines my tree number -- only
        use for errors, etc. Returns ec.gp.GPTree.NO_TREENUM if the 
        tree number could not be
        determined (might happen if it's not been assigned yet). */
    public int treeNumber()
        {
        if (owner==null) return NO_TREENUM;
        if (owner.trees==null) return NO_TREENUM;
        for(int x=0;x<owner.trees.length;x++)
            if (owner.trees[x]==this) return x;
        return NO_TREENUM;
        }


    /** Sets up a prototypical GPTree with those features it shares with
        other GPTrees in its position in its GPIndividual, and nothhing more.

        This must be called <i>after</i> the GPTypes and GPNodeConstraints 
        have been set up.  Presently they're set up in GPInitializer,
        which gets called before this does, so we're safe. */
    public void setup(final EvolutionState state, final Parameter base)
        {
        Parameter def = defaultBase();

        // print for humans using latex?
        useLatex = state.parameters.getBoolean(base.push(P_USELATEX),def.push(P_USELATEX),false);

        // determine my constraints -- at this point, the constraints should have been loaded.
        String s = state.parameters.getString(base.push(P_TREECONSTRAINTS),
                                              def.push(P_TREECONSTRAINTS));
        if (s==null)
            state.output.fatal("No tree constraints are defined for the GPTree " + base + ".");
        else constraints = GPTreeConstraints.constraintsFor(s,state).constraintNumber;
        
        state.output.exitIfErrors();  // because I promised
        // we're not loading the nodes at this point
        }



    /** Prints out the tree in single-line fashion suitable for reading
        in later by computer. O(n). 
        The default version of this method simply calls child's 
        printRootedTree(...) method. */

    public void printTree(final EvolutionState state, final int log,
                          final int verbosity)
        {
        child.printRootedTree(state,log,verbosity,0);
        // printRootedTree doesn't print a '\n', so I need to do so here
        state.output.println("",verbosity,log);
        }

    /** Prints out the tree in single-line fashion suitable for reading
        in later by computer. O(n). 
        The default version of this method simply calls child's 
        printRootedTree(...) method. */

    public void printTree(final EvolutionState state,
                          final PrintWriter writer)
        {
        child.printRootedTree(state,writer,0);
        // printRootedTree doesn't print a '\n', so I need to do so here
        writer.println();
        }

    /** Reads in the tree from a form printed by printTree. */
    public void readTree(final EvolutionState state,
                         final LineNumberReader reader) throws IOException, CloneNotSupportedException
        {
        int linenumber = reader.getLineNumber();

        // the next line will be the child
        String s = reader.readLine();
        if (s==null)  // uh oh
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "No Tree found.");
        else
            child = child.readRootedTree(linenumber,new DecodeReturn(s),
                                         constraints().treetype,
                                         constraints().functionset,this,0,state);
        }

    /** Prints out the tree in a readable Lisp-like fashion. O(n). 
        The default version of this method simply calls child's 
        printRootedTreeForHumans(...) method. */
    
    public void printTreeForHumans(final EvolutionState state, final int log,
                                   final int verbosity)
        {
        if (useLatex) state.output.print(child.makeLatexTree(),verbosity,log);
        else child.printRootedTreeForHumans(state,log,verbosity,0,0);
        // printRootedTreeForHumans doesn't print a '\n', so I need to do so here
        state.output.println("",verbosity,log);
        }

    /** Builds a new randomly-generated rooted tree and attaches it to the GPTree. */

    public void buildTree(final EvolutionState state, final int thread) throws CloneNotSupportedException
        {
        child = constraints().init.newRootedTree(state,
                                                 constraints().treetype,
                                                 thread,
                                                 this,
                                                 constraints().functionset,
                                                 0,
                                                 GPNodeBuilder.NOSIZEGIVEN);
        }
    }
