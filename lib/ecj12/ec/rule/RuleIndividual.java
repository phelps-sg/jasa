package ec.rule;
import java.io.Serializable;
import ec.*;
import ec.util.*;
import java.util.*;
import java.io.*;

/* 
 * RuleIndividual.java
 * 
 * Created: Tue May 29 18:20:20 EDT 2001
 * By: Sean Luke
 */

/**
 * RuleIndividual is an Individual with an array of RuleSets, each of which
 * is a set of Rules.  RuleIndividuals belong to some subclass of RuleSpecies
 * (or just RuleSpecies itself).
 *
 * <p>RuleIndividuals really have basically one parameter: the number
 * of RuleSets to use.  This is determined by the <tt>num-rulesets</tt>
 * parameter.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>num-rulesets</tt><br>
 <font size=-1>int >= 1</font></td>
 <td valign=top>(number of rulesets used)</td></tr>
 <tr><td valign=top><i>base</i>.<tt>ruleset</tt>.<i>n</i><br>
 <font size=-1>Classname, subclass of or = ec.rule.RuleSet</font></td>
 <td valign=top>(class of ruleset <i>n</i>)</td></tr>
 </table>
 
 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>ruleset</tt>.<i>n</i><br>
 <td>RuleSet <i>n</i></td></tr>
 </table>

 <p><b>Default Base</b><br>
 rule.individual

 * @author Sean Luke
 * @version 1.0 
 */
public class RuleIndividual extends Individual
    {
    public static final String P_RULESET = "ruleset";
    public static final String P_NUMRULESETS = "num-rulesets";
    public static final String P_INDIVIDUAL = "individual";
    public static final String EVALUATED_PREAMBLE = "Evaluated: ";
    
    /** The individual's rulesets. */
    public RuleSet[] rulesets;
    
    public Parameter defaultBase()
        {
        return RuleDefaults.base().push(P_INDIVIDUAL);
        }

    public Object protoClone() throws CloneNotSupportedException
        {
        RuleIndividual myobj = (RuleIndividual) (super.protoClone());   
        myobj.rulesets = new RuleSet[rulesets.length];
        for(int x=0;x<rulesets.length;x++) 
            myobj.rulesets[x] = (RuleSet)(rulesets[x].protoClone());
        return myobj;
        } 

    /** Called by pipelines before they've modified the individual and
        it might need to be "fixed"  -- basically a hook for you to override.
        By default, calls validateRules on each ruleset. */
    public void preprocessIndividual(final EvolutionState state, final int thread)
        {
        for (int x=0;x<rulesets.length;x++)
            rulesets[x].preprocessRules(state,thread);
        }

    /** Called by pipelines after they've modified the individual and
        it might need to be "fixed"  -- basically a hook for you to override.
        By default, calls validateRules on each ruleset. */
    public void postprocessIndividual(final EvolutionState state, final int thread)
        {
        for (int x=0;x<rulesets.length;x++)
            rulesets[x].postprocessRules(state,thread);
        }
        
    public boolean equals(Object ind)
        {
        // My loose definition: ind must be a 
        if (!getClass().equals(ind.getClass()))  // not the same class, I'm conservative that way
            return false;

        RuleIndividual other = (RuleIndividual)ind;
        if (rulesets.length != other.rulesets.length) return false;
        for(int x=0;x<rulesets.length;x++)
            if (!rulesets[x].equals(other.rulesets[x])) return false;
        return true;
        }

    public int hashCode()
        {
        int hash = this.getClass().hashCode();
        for(int x=0;x<rulesets.length;x++)
            // rotate hash and XOR
            hash =
                (hash << 1 | hash >>> 31 ) ^ rulesets[x].hashCode();
        return hash;
        }

    public void setup(final EvolutionState state, final Parameter base)
        {
        // I'm the top-level setup, I guess
        int numrulesets = state.parameters.getInt(
            base.push(P_NUMRULESETS), defaultBase().push(P_NUMRULESETS),
            1);  // need at least 1 ruleset!
        if (numrulesets == 0)
            state.output.fatal("RuleIndividual needs at least one RuleSet!",
                               base.push(P_NUMRULESETS), defaultBase().push(P_NUMRULESETS));

        rulesets  = new RuleSet[numrulesets];

        for(int x=0;x<numrulesets;x++)
            {
            rulesets[x] = (RuleSet)(state.parameters.getInstanceForParameterEq(
                                        base.push(P_RULESET).push(""+x),defaultBase().push(P_RULESET),
                                        RuleSet.class));
            rulesets[x].setup(state,base.push(P_RULESET).push(""+x));
            }
        }

    public void printIndividualForHumans(final EvolutionState state,
                                         final int log, 
                                         final int verbosity)
        {
        state.output.println(EVALUATED_PREAMBLE + (evaluated ? "true" : "false"), 
                             verbosity, log);
        fitness.printFitnessForHumans(state,log,verbosity);
        for(int x=0;x<rulesets.length;x++)
            {
            state.output.println("Ruleset " + x + ":", verbosity, log);
            rulesets[x].printRuleSetForHumans(state, log, verbosity);
            }
        }

    public void printIndividual(final EvolutionState state,
                                final int log, 
                                final int verbosity)
        {
        state.output.println(EVALUATED_PREAMBLE + Code.encode(evaluated), 
                             verbosity, log);
        fitness.printFitness(state, log, verbosity);
        for(int x=0;x<rulesets.length;x++)
            {
            state.output.println("Ruleset " + x + ":",verbosity, log);
            rulesets[x].printRuleSet(state,log,verbosity);
            }
        }

    public void printIndividual(final EvolutionState state,
                                final PrintWriter writer)
        {
        writer.println(EVALUATED_PREAMBLE + Code.encode(evaluated));
        fitness.printFitness(state,writer);
        for(int x=0;x<rulesets.length;x++)
            {
            writer.println("Ruleset " + x + ":");
            rulesets[x].printRuleSet(state,writer);
            }
        }

    /** Doesn't read in the species */
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

        // read my ruleset
        for(int x=0;x<rulesets.length;x++)
            {
            reader.readLine();  // throw it away -- it's the ruleset# indicator
            rulesets[x].readRuleSet(state,reader);
            }
        }

    public long size() 
        { 
        long size=0;
        for(int x=0;x<rulesets.length;x++) 
            size+= rulesets[x].numRules();
        return size;
        }
    
    public void reset(EvolutionState state, int thread)
        {
        for(int x=0;x<rulesets.length;x++) 
            rulesets[x].reset(state,thread);
        }

    }

