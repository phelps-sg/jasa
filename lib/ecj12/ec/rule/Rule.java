package ec.rule;
import java.io.Serializable;
import ec.*;
import ec.util.*;
import java.util.*;
import java.io.*;

/* 
 * Rule.java
 * 
 * Created: Tue Feb 20 13:19:00 2001
 * By: Liviu Panait and Sean Luke
 */

/**
 * Rule is an abstract class for describing rules. It is abstract
 * because it is supposed to be extended by different classes
 * modelling different kinds of rules.
 * It provides the reset abstract method for randomizing the individual. 
 * It also provides the mutate function for mutating an individual rule
 * It also provides the clone function for cloning the rule.
 *
 * <p>You will need to implement some kind of artificial ordering between
 * rules in a ruleset (the lt and gt methods inherited from SortComparator)
 * so the ruleset can be sorted in such a way that it can be compared with
 * another ruleset for equality.  You should also implement hashCode
 * and equals 
 * in such a way that they aren't based on pointer information, but on actual
 * internal features. 
 *
 * <p>Every rule points to a RuleConstraints which handles information that
 * Rule shares with all the other Rules in a RuleSet.
 *
 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>constraints</tt><br>
 <font size=-1>string</font></td>
 <td valign=top>(name of the rule constraint)</td></tr>
 </table>
 
 <p><b>Default Base</b><br>
 rule.rule


 * @author Liviu Panait and Sean luke
 * @version 1.0 
 */
public abstract class Rule implements Prototype, SortComparator
    {
    public static final String P_RULE = "rule";
    public static final String P_CONSTRAINTS = "constraints";
    /**
       An index to a RuleConstraints
    */
    public byte constraints;

    /* Returns the Rule's constraints.  A good JIT compiler should inline this. */
    public final RuleConstraints constraints() 
        { 
        return RuleConstraints.constraints[constraints]; 
        }

    /** Generates a hash code for this rule -- the rule for this is that the hash code
        must be the same for two rules that are equal to each other genetically. */
    public abstract int hashCode();
    
    /** Unlike the standard form for Java, this function should return true if this
        rule is "genetically identical" to the other rule. */
    public abstract boolean equals( final Object other );

    /**
       The reset method randomly reinitializes the rule.
    */
    public abstract void reset(final EvolutionState state, final int thread);

    /**
       Mutate the rule.  The default form just resets the rule.
    */
    public void mutate(final EvolutionState state, final int thread)
        {
        reset(state,thread);
        }

    /**
       Nice printing.  The default form simply calls printRule, but you might want to override this.
    */
    public void printRuleForHumans( final EvolutionState state, final int log, final int verbosity )
        { printRule(state,verbosity,log); }

    /** Prints the rule to a string in a fashion readable by readRuleFromString.
        The default form simply calls toString() -- you should just override toString() 
        if you don't need the EvolutionState.
        @deprecated */
    public String printRuleToString(final EvolutionState state)
        { return toString(); }
        
    /** Reads a rule from a string, which may contain a final '\n'.
        Override this method.  The default form does nothing. */
    public void readRuleFromString(final String string, final EvolutionState state)
        { return; }

    /**
       Prints the rule in a way that can be read by readRule().  The default form simply
       calls printRuleToString(state).   Override this rule to do custom writing to the log,
       or just override printRuleToString(...), which is probably easier to do.
    */
    public void printRule( final EvolutionState state, final int log, final int verbosity )
        { state.output.println(printRuleToString(state),verbosity,log);}

    /**
       Prints the rule in a way that can be read by readRule().  The default form simply
       calls printRuleToString(state).   Override this rule to do custom writing,
       or just override printRuleToString(...), which is probably easier to do.
    */
    public void printRule( final EvolutionState state, final PrintWriter writer )
        { writer.println(printRuleToString(state)); }

    /**
       Reads a rule printed by printRule(...).  The default form simply reads a line into
       a string, and then calls readRuleFromString() on that line.  Override this rule to do
       custom reading, or just override readRuleFromString(...), which is probably easier to do.
    */
    public void readRule(final EvolutionState state,
                         final LineNumberReader reader)
        throws IOException, CloneNotSupportedException
        { readRuleFromString(reader.readLine(),state); }


    public Parameter defaultBase()
        {
        return RuleDefaults.base().push(P_RULE);
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

    public void setup(EvolutionState state, Parameter base)
        {
        String constraintname = state.parameters.getString(
            base.push( P_CONSTRAINTS ),defaultBase().push(P_CONSTRAINTS));
        if (constraintname == null)
            state.output.fatal("No RuleConstraints name given",
                               base.push( P_CONSTRAINTS ),defaultBase().push(P_CONSTRAINTS));

        constraints = RuleConstraints.constraintsFor(constraintname,state).constraintNumber;
        state.output.exitIfErrors();
        }

    }
