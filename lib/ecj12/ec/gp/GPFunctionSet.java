package ec.gp;
import java.io.*;
import ec.*;
import ec.util.*;
import java.util.*;

/* 
 * GPFunctionSet.java
 * 
 * Created: Wed Oct 13 22:35:06 1999
 * By: Sean Luke
 */

/**
 * GPFunctionSet is a Clique which represents a set of GPNode prototypes
 * forming a standard function set for forming certain trees in individuals.
 * GPFunctionSets instances have unique names with which they're referenced by
 * GPTreeConstraints objects indicating that they're used for certain trees.
 * GPFunctionSets store their GPNode Prototypes in three hashtables,
 * one for all nodes, one for nonterminals, and one for terminals.  Each
 * hashed item is an array of GPFuncInfo objects containing the GPNodes proper,
 * hashed by the return type of the GPNodes in the array.
 *
 * GPFunctionSets also contain prototypical GPFuncInfo nodes which they
 * clone to form their arrays.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>name</tt><br>
 <font size=-1>String</font></td>
 <td valign=top>(name of function set.  Must be different from other function set instances)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>size</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(number of functions in the function set)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>func.</tt><i>n</i><br>
 <font size=-1>classname, inherits and != ec.gp.GPNode</font></td>
 <td valign=top>(class of function node <i>n</i> in the set)</td></tr>

 <tr><td valign=top><i>base</i>.<tt>info</tt><br>
 <font size=-1>classname, inherits or == ec.gp.GPFuncInfo,<br>or nothing</font></td>
 <td valign=top>(class of the prototypical GPFuncInfo object.  If nothing, this defaults to ec.gp.GPFuncInfo)</td></tr>

 </table>

 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>info</tt></td>
 <td>p_funcinfo</td></tr>
 <tr><td valign=top><i>base</i>.<tt>func.</tt><i>n</i></td>
 <td>function node <i>n</i></td></tr>
 </table>
 
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class GPFunctionSet implements Clique
    {
    public final static String P_NAME = "name";
    public final static String P_SIZE = "size";
    public final static String P_FUNCINFO = "info";
    public final static String P_FUNC = "func";

    /** Name of the GPFunctionSet */
    public String name;

    /** The nodes that our GPTree can use: arrays of nodes hashed by type. */
    public Hashtable nodes_h;
    /** The nodes that our GPTree can use: nodes[type][thenodes]. */
    public GPFuncInfo[][] nodes;
    /** The nonterminals our GPTree can use: arrays of nonterminals hashed by type. */
    public Hashtable nonterminals_h;
    /** The nonterminals our GPTree can use: nonterminals[type][thenodes]. */
    public GPFuncInfo[][] nonterminals;
    /** The terminals our GPTree can use: arrays of terminals hashed by type. */
    public Hashtable terminals_h;
    /** The terminals our GPTree can use: terminals[type][thenodes]. */
    public GPFuncInfo[][] terminals;


    // some convenience methods which speed up various kinds
    // of mutation operators


    /** Nodes == a given arity, that is: nodesByArity[type][arity][thenodes] */
    public GPFuncInfo[][][]nodesByArity;

    /** Nonterminals <= a given arity, that is: nonterminalsUnderArity[type][arity][thenodes] --
        this will be O(n^2).  Obviously, the number of nonterminals at arity slot 0 is 0.*/
    public GPFuncInfo[][][]nonterminalsUnderArity;

    /** Nonterminals >= a given arity, that is: nonterminalsOverArity[type][arity][thenodes] --
        this will be O(n^2).  Obviously, the number of nonterminals at arity slot 0 is all the 
        nonterminals of that type. */
    public GPFuncInfo[][][]nonterminalsOverArity;


    /** The prototypical GPFuncInfo node.*/
    public GPFuncInfo p_funcinfo;
    
    /** A global storage facility for all known GPFunctionSet objects. */
    public static Hashtable all;
    
    static
        {
        all = new Hashtable();
        }

    /** Returns the name. */
    public String toString() { return name; }


    /** Sets up all the GPFunctionSet, loading them from the parameter
        file.  This must be called before anything is called which refers
        to a type by name. */

    public static void setupFunctionSets(final EvolutionState state,
                                         final Parameter base)
        {
        state.output.message("Processing GP Function Sets");
        // How many GPFunctionSets do we have?
        int x = state.parameters.getInt(base.push(P_SIZE),null,1);
        if (x<=0) 
            state.output.fatal("The number of GPFunctionSets must be at least 1.",base.push(P_SIZE));

        // Load our FunctionSet
        for (int y=0;y<x;y++)
            {
            GPFunctionSet c;
            // Figure the GPFunctionSet class
            if (state.parameters.exists(base.push(""+y)))
                c = (GPFunctionSet)(state.parameters.getInstanceForParameterEq(
                                        base.push(""+y),null,GPFunctionSet.class));
            else
                {
                state.output.message("No GPFunctionSet specified, assuming the default class: ec.gp.GPFunctionSet for " + base.push(""+y));
                c = new GPFunctionSet();
                }
            c.setup(state,base.push(""+y));
            }
        // stick our hashtable in statics; it'll serialize the objects for us
        state.statics.addElement(all);
        }

    
    /** Sets up the arrays based on the hashtables */

    public void postProcessFunctionSet()
        {
        nodes = new GPFuncInfo[nodes_h.size()][];
        terminals = new GPFuncInfo[terminals_h.size()][];
        nonterminals = new GPFuncInfo[nonterminals_h.size()][];

        Enumeration e = nodes_h.keys();
        while(e.hasMoreElements())
            {
            GPType gpt = (GPType)(e.nextElement());
            GPFuncInfo[] gpfi = (GPFuncInfo[])(nodes_h.get(gpt));
            nodes[gpt.type] = gpfi;
            }
        e = nonterminals_h.keys();
        while(e.hasMoreElements())
            {
            GPType gpt = (GPType)(e.nextElement());
            GPFuncInfo[] gpfi = (GPFuncInfo[])(nonterminals_h.get(gpt));
            nonterminals[gpt.type] = gpfi;
            }
        e = terminals_h.keys();
        while(e.hasMoreElements())
            {
            GPType gpt = (GPType)(e.nextElement());
            GPFuncInfo[] gpfi = (GPFuncInfo[])(terminals_h.get(gpt));
            terminals[gpt.type] = gpfi;
            }

        // set up arity-based arrays
        // first, determine the maximum arity
        int max_arity=0;
        for(int x=0;x<nodes.length;x++)
            for(int y=0;y<nodes[x].length;y++)
                if (max_arity < nodes[x][y].node.children.length)
                    max_arity = nodes[x][y].node.children.length;

        // next set up the == array
        nodesByArity = new GPFuncInfo[nodes.length][max_arity+1][];
        for(int x=0;x<nodes.length;x++)
            for(int a = 0; a <= max_arity; a++)
                {
                // how many nodes do we have?
                int num_of_a = 0;
                for(int y=0;y<nodes[x].length;y++)
                    if (nodes[x][y].node.children.length == a) num_of_a++;
                // allocate and fill
                nodesByArity[x][a] = new GPFuncInfo[num_of_a];
                int cur_a = 0;
                for(int y=0;y<nodes[x].length;y++)
                    if (nodes[x][y].node.children.length == a )
                        nodesByArity[x][a][cur_a++] = nodes[x][y];
                }

        // now set up the <= nonterminals array
        nonterminalsUnderArity = new GPFuncInfo[nonterminals.length][max_arity+1][];
        for(int x=0;x<nonterminals.length;x++)
            for (int a = 0;a <= max_arity; a++)
                {
                // how many nonterminals do we have?
                int num_of_a = 0;
                for(int y=0;y<nonterminals[x].length;y++)
                    if (nonterminals[x][y].node.children.length <= a) num_of_a++;
                // allocate and fill
                nonterminalsUnderArity[x][a] = new GPFuncInfo[num_of_a];
                int cur_a = 0;
                for(int y=0;y<nonterminals[x].length;y++)
                    if (nonterminals[x][y].node.children.length <= a )
                        nonterminalsUnderArity[x][a][cur_a++] = nonterminals[x][y];
                }



        // now set up the >= nonterminals array
        nonterminalsOverArity = new GPFuncInfo[nonterminals.length][max_arity+1][];
        for(int x=0;x<nonterminals.length;x++)
            for (int a = 0;a <= max_arity; a++)
                {
                // how many nonterminals do we have?
                int num_of_a = 0;
                for(int y=0;y<nonterminals[x].length;y++)
                    if (nonterminals[x][y].node.children.length >= a) num_of_a++;
                // allocate and fill
                nonterminalsOverArity[x][a] = new GPFuncInfo[num_of_a];
                int cur_a = 0;
                for(int y=0;y<nonterminals[x].length;y++)
                    if (nonterminals[x][y].node.children.length >= a )
                        nonterminalsOverArity[x][a][cur_a++] = nonterminals[x][y];
                }

        }




    /** Must be done <i>after</i> GPType and GPNodeConstraints have been set up */

    public void setup(final EvolutionState state, final Parameter base)
        {
        // What's my name?
        name = state.parameters.getString(base.push(P_NAME),null);
        if (name==null)
            state.output.fatal("No name was given for this function set.",
                               base.push(P_NAME));
        // Register me
        GPFunctionSet old_functionset = (GPFunctionSet)(all.put(name,this));
        if (old_functionset != null)
            state.output.fatal("The GPFunctionSet \"" + name + "\" has been defined multiple times.", base.push(P_NAME));

        // What's my function info class?

        if (state.parameters.exists(base.push(P_FUNCINFO)))
            p_funcinfo = (GPFuncInfo)(state.parameters.getInstanceForParameterEq(base.push(P_FUNCINFO),null,GPFuncInfo.class));
        else
            {
            state.output.message("Using default GPFuncInfo class for GPFunctionSet " + name + ".");
            p_funcinfo = new GPFuncInfo();
            }
        p_funcinfo.setup(state,base.push(P_FUNCINFO));
        

        // How many functions do I have?
        int numFuncs = state.parameters.getInt(base.push(P_SIZE),null,1);
        if (numFuncs < 1)
            state.output.error("The GPFunctionSet \"" + name + "\" has no functions.",
                               base.push(P_SIZE));
        
        Parameter p = base.push(P_FUNC);
        Vector tmp = new Vector();
        for(int x = 0; x < numFuncs; x++)
            {
            // load
            GPFuncInfo gpfi = (GPFuncInfo)(p_funcinfo.protoCloneSimple());
            Parameter pp = p.push(""+x);
            gpfi.node = (GPNode)(state.parameters.getInstanceForParameter(
                                     pp, null, GPNode.class));
            gpfi.node.setup(state,pp);

            // add to my collection
            tmp.addElement(gpfi);
            }

        // Make my hash tables
        nodes_h = new Hashtable();
        terminals_h = new Hashtable();
        nonterminals_h = new Hashtable();

        // Now set 'em up according to the types in GPType

        Enumeration e = GPType.all.elements();
        while(e.hasMoreElements())
            {
            GPType typ = (GPType)(e.nextElement());
            
            // make vectors for the type.
            Vector nodes_v = new Vector();
            Vector terminals_v = new Vector();
            Vector nonterminals_v = new Vector();

            // add GPFuncInfos as appropriate to each vector
            Enumeration v = tmp.elements();
            while (v.hasMoreElements())
                {
                GPFuncInfo i = (GPFuncInfo)(v.nextElement());
                if (typ.compatibleWith(i.node.constraints().returntype))
                    {
                    nodes_v.addElement(i);
                    if (i.node.children.length == 0)
                        terminals_v.addElement(i);
                    else nonterminals_v.addElement(i);
                    }
                }

            // turn nodes_h' vectors into arrays
            GPFuncInfo[] ii = new GPFuncInfo[nodes_v.size()];
            nodes_v.copyInto(ii);
            nodes_h.put(typ,ii);

            // turn terminals_h' vectors into arrays
            ii = new GPFuncInfo[terminals_v.size()];
            terminals_v.copyInto(ii);
            terminals_h.put(typ,ii);

            // turn nonterminals_h' vectors into arrays
            ii = new GPFuncInfo[nonterminals_v.size()];
            nonterminals_v.copyInto(ii);
            nonterminals_h.put(typ,ii);

            // Post-arrange the items
            nodes_h = p_funcinfo.arrange(nodes_h);
            terminals_h = p_funcinfo.arrange(terminals_h);
            nonterminals_h = p_funcinfo.arrange(nonterminals_h);
            }

        // I don't check to see if the generation mechanism will be valid here
        // -- I check that in GPTreeConstraints, where I can do the weaker check
        // of going top-down through functions rather than making sure that every
        // single function has a compatible argument function (an unneccessary check)

        state.output.exitIfErrors();  // because I promised when I called n.setup(...)

        // postprocess the function set
        postProcessFunctionSet();
        }


    /** Returns the function set for a given name.
        You must guarantee that after calling functionSetFor(...) one or
        several times, you call state.output.exitIfErrors() once. */

    public static GPFunctionSet functionSetFor(final String functionSetName,
                                               final EvolutionState state)
        {
        GPFunctionSet set = (GPFunctionSet)(all.get(functionSetName));
        if (set==null)
            state.output.error("The GP function set \"" + functionSetName + "\" could not be found.");
        return set;
        }


    private void writeObject(ObjectOutputStream out) throws IOException
        {
        // this wastes an hashtable pointer, but what the heck.

        out.defaultWriteObject();
        out.writeObject(all);
        }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
        {
        in.defaultReadObject();
        all = (Hashtable)(in.readObject());
        }
    }
