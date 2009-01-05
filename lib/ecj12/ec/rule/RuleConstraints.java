package ec.rule;
import java.io.Serializable;
import ec.*;
import ec.util.*;
import java.util.*;
import java.io.*;

/* 
 * RuleConstraints.java
 * 
 * Created: Tue Feb 20 13:16:00 2001
 * By: Liviu Panait and Sean Luke
 */

/**
 * RuleConstraints is a class for constraints applicable to rules.
 * You can subclass this to add additional constraints information
 * for different kinds of rules.
 *
 * @author Liviu Panait and Sean Luke
 * @version 1.0 

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>size</tt><br>
 <font size=-1>int &gt;= 1</font></td>
 <td valign=top>(number of rule constraints)</td></tr>

 <tr><td valign=top><i>base.n</i>.<tt>name</tt><br>
 <font size=-1>String</font></td>
 <td valign=top>(name of rule constraint <i>n</i>)</td></tr>
 </table>

*/
public class RuleConstraints implements Clique
    {
    public static final int SIZE_OF_BYTE = 256;
    public final static String P_NAME = "name";
    public final static String P_SIZE = "size";

    /** The byte value of the constraints -- we can only have 256 of them */
    public byte constraintNumber;

    /** The name of the RuleConstraints object */
    public String name;

    /** A repository of all the RuleConstraints in the system. */
    public static Hashtable all;
    public static RuleConstraints[] constraints;
    public static byte numConstraints;

    static
        {
        all = new Hashtable();
        constraints = new RuleConstraints[SIZE_OF_BYTE];
        numConstraints = 0;
        }

    /** Converting the rule to a string ( the name ) */
    public String toString() { return name; }


    /** Sets up all the RuleConstraints, loading them from the parameter
        file.  This must be called before anything is called which refers
        to a type by name. */

    public static void setupConstraints(final EvolutionState state,
                                        final Parameter base)
        {
        state.output.message("Processing Rule Constraints");

        // How many RuleConstraints do we have?
        int x = state.parameters.getInt(base.push(P_SIZE),null,1);
        if (x<=0) 
            state.output.fatal("The number of rule constraints must be at least 1.",base.push(P_SIZE));

        // Load our constraints
        for (int y=0;y<x;y++)
            {
            RuleConstraints c;
            // Figure the constraints class
            if (state.parameters.exists(base.push(""+y)))
                c = (RuleConstraints)(state.parameters.getInstanceForParameterEq(
                                          base.push(""+y),null,RuleConstraints.class));
            else
                {
                state.output.message("No Rule Constraints specified, assuming the default class: ec.rule.RuleConstraints for " + base.push(""+y));
                c = new RuleConstraints();
                }
            c.setup(state,base.push(""+y));
            }
        
        // set our constraints array up
        Enumeration e = all.elements();
        while(e.hasMoreElements())
            {
            RuleConstraints c = (RuleConstraints)(e.nextElement());
            c.constraintNumber = numConstraints;
            constraints[numConstraints] = c;
            numConstraints++;
            }
       
        // stick our hashtable in statics; it'll serialize the objects for us
        state.statics.addElement(all);
        }


    public void setup(final EvolutionState state, final Parameter base)
        {
        // What's my name?
        name = state.parameters.getString(base.push(P_NAME),null);
        if (name==null)
            state.output.fatal("No name was given for this Rule Constraints.",
                               base.push(P_NAME));

        // Register me
        RuleConstraints old_constraints = (RuleConstraints)(all.put(name,this));
        if (old_constraints != null)
            state.output.fatal("The rule constraints \"" + name + "\" has been defined multiple times.", base.push(P_NAME));
        }

    /** You must guarantee that after calling constraintsFor(...) one or
        several times, you call state.output.exitIfErrors() once. */

    public static RuleConstraints constraintsFor(final String constraintsName,
                                                 final EvolutionState state)
        {
        RuleConstraints myConstraints = (RuleConstraints)(all.get(constraintsName));
        if (myConstraints==null)
            state.output.error("The rule constraints \"" + constraintsName + "\" could not be found.");
        return myConstraints;
        }


    private void writeObject(ObjectOutputStream out) throws IOException
        {
        // this wastes an hashtable pointer, but what the heck.

        out.defaultWriteObject();
        out.writeObject(all);
        out.writeObject(constraints);
        out.writeByte(numConstraints);
        }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
        {
        in.defaultReadObject();
        all = (Hashtable)(in.readObject());
        constraints = (RuleConstraints[]) in.readObject();
        numConstraints = in.readByte();
        }
    }
