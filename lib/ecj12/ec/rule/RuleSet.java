package ec.rule;
import java.io.Serializable;
import ec.*;
import ec.util.*;
import java.util.*;
import java.io.*;

/* 
 * RuleSet.java
 * 
 * Created: Tue Feb 20 13:19:00 2001
 * By: Liviu Panait and Sean Luke
 */

/**
 * RuleSet is a set of Rules, implemented straightforwardly as an arbitrary-length array of Rules.
 * A RuleIndividual is simply a list of RuleSets.  Most typically, a RuleIndividual contains a
 * single RuleSet, containing a variety of Rules.
 * RuleSets contain many useful subsetting and modification functions which you can use
 * in breeding operators which modify RuleSets and Rules.
 *
 * <p> Besides the Rules themselves, the only thing else a RuleSet contains is a pointer to a
 * corresponding RuleSetConstraints object, which holds all of its modification parameters.
 * See RuleSetConstraints for a description of these parameters.

 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>constraints</tt><br>
 <font size=-1>string</font></td>
 <td valign=top>(name of the rule set constraints)</td></tr>
 </table>
 
 <p><b>Default Base</b><br>
 rule.ruleset


 * @author Liviu Panait and Sean Luke
 * @version 1.0 
 */
public class RuleSet implements Prototype
    {

    /**
       The message to appear when printing the rule set
    */
    public final static String N_RULES = "Num: ";
    public final static String P_RULESET = "ruleset";
    /**
       The constraint for the rule set
    */
    public static final String P_CONSTRAINTS = "constraints";
    /**
       An index to a RuleSetConstraints
    */
    public byte constraints;

    /* Returns the RuleSet's constraints.  A good JIT compiler should inline this. */
    public final RuleSetConstraints constraints() 
        {
        return RuleSetConstraints.constraints[constraints];
        }

    /**
       The rules in the rule set
    */
    public Rule[] rules = new Rule[0];
    /**
       How many rules are there used in the rules array
    */
    public int numRules = 0;


    public Object protoClone() throws CloneNotSupportedException
        {
        RuleSet newRuleSet = (RuleSet)(super.clone());
        // copy the rules over
        if( rules != null )
            {
            newRuleSet.rules = (Rule[])(rules.clone());
            }
        else
            {
            newRuleSet.rules = null;
            }
        for(int x=0;x<numRules;x++)
            newRuleSet.rules[x] = (Rule)(rules[x].protoClone());
        return newRuleSet;
        }

    public final Object protoCloneSimple()
        {
        try { return protoClone(); }
        catch (CloneNotSupportedException e) 
            { throw new InternalError(); } // never happens
        } 

    /**
       How many rules are there used in the rules array
    */
    
    public int numRules() { return numRules; }
    /**
       A reset method for randomly reinitializing the RuleSet
    */
    public void reset(final EvolutionState state, final int thread)
        {
        // reinitialize the array of rules
        numRules = constraints().numRulesForReset(this,state,thread);

        rules = new Rule[ numRules ];

        for( int i = 0 ; i < rules.length ; i++ )
            {
            rules[i] = (Rule)(constraints().rulePrototype.protoCloneSimple());
            rules[i].reset(state,thread);
            }
        }

    /**
       Mutates rules in the RuleSet independently with the given probability.
    */
    public void mutateRules( final EvolutionState state, final int thread)
        {
        for( int i = 0 ; i < numRules ; i++ )
            {
            rules[i].mutate(state,thread);
            }
        while( state.random[thread].nextBoolean( constraints().p_del ) && numRules > constraints().minSize )
            {
            removeRandomRule( state, thread );
            }
        while( state.random[thread].nextBoolean( constraints().p_add ) && numRules < constraints().maxSize )
            {
            addRandomRule( state, thread );
            }
        if( state.random[thread].nextBoolean( constraints().p_randorder ) )
            {
            randomizeRulesOrder( state, thread );
            }
        }
        
    /**
       Should be called by pipelines to "fix up" the rulesets before they have been
       mutated or crossed over.  Override this method to do so.
    */
    public void preprocessRules(final EvolutionState state, final int thread)
        {
        }

    /**
       Should be called by pipelines to "fix up" the rulesets after they have been
       mutated or crossed over.  Override this method to do so.
    */
    public void postprocessRules(final EvolutionState state, final int thread)
        {
        }
        
    /**
       Randomizes the order of the rules in the rule set. It is helpful when the
       order of rule is important for the conflict resolution.
    */
    public void randomizeRulesOrder(final EvolutionState state, final int thread)
        {
        Rule temp;
        for( int i = numRules-1 ; i > 0 ; i-- )
            {
            int j = state.random[thread].nextInt( i+1 );
            temp = rules[i];
            rules[i] = rules[j];
            rules[j] = temp;
            }
        }

    /**
       Add a random rule to the rule set
    */
    public void addRandomRule(final EvolutionState state, final int thread)
        {
        Rule newRule = (Rule)(constraints().rulePrototype.protoCloneSimple());
        newRule.reset(state,thread);
        addRule(newRule);
        }

    /**
       Add a rule directly to the rule set.  Does not copy the rule.
    */
    public void addRule( Rule rule )
        {
        if( ( rules == null && numRules == 0 ) || ( numRules == rules.length ) )
            {
            Rule[] tempRules;
            if( rules == null )
                {
                tempRules = new Rule[2];
                }
            else
                {
                tempRules = new Rule[ (rules.length + 1 ) * 2 ];
                }
            if( rules != null )
                System.arraycopy( rules, 0, tempRules, 0, rules.length );
            rules = tempRules;
            }

        // add the rule and increase the counter
        rules[ numRules++ ] = rule;
        }

    /**
       Removes a rule from the rule set and returns it.  If index is out of bounds, then
       this method returns null.
    */
    public Rule removeRule( int index )
        {
        if (index >= numRules || index < 0 ) return null;
        // swap to the top
        Rule myrule = rules[index];
        rules[index] = rules[numRules-1];
        numRules--;
        return myrule; 
        }

    /**
       Removes a randomly-chosen rule from the rule set and returns it.  If there are no rules to remove,
       this method returns null.
    */
    public Rule removeRandomRule( final EvolutionState state, final int thread )
        {
        if (numRules <= 0) return null;
        else return removeRule(state.random[thread].nextInt(numRules));
        }

    /**
       Makes a copy of the rules in another RuleSet and adds the rule copies.
    */
    public void join( final RuleSet other )
        {
        // if there's not enough place to store the new rules, increase space
        if( rules.length <= numRules + other.numRules )
            {
            Rule[] tempRules = new Rule[ rules.length + other.rules.length ];
            System.arraycopy( rules, 0, tempRules, 0, numRules );
            rules = tempRules;
            }
        // copy in the new rules
        System.arraycopy( other.rules, 0, rules, numRules, other.numRules );
        // protoclone the rules
        try
            {
            for(int x=numRules;x<numRules+other.numRules;x++)
                rules[x] = (Rule)(rules[x].protoClone());
            }
        catch (CloneNotSupportedException e) { } // never happens
        numRules += other.numRules;
        }
        
    /**
       Clears out existing rules, and loads the rules from the other ruleset without protocloning them.
       Mostly for use if you create temporary rulesets (see for example RuleCrossoverPipeline)
    */
    public void copyNoClone( final RuleSet other )
        {
        // if there's not enough place to store the new rules, increase space
        if( rules.length <= other.numRules )
            {
            rules = new Rule[ other.numRules ];
            }
        // copy in the new rules
        // System.out.println(other.rules);
        System.arraycopy( other.rules, 0, rules, 0, other.numRules );
        numRules = other.numRules;
        }
        
    /**
       Splits the rule set into a number of disjoint rule sets, copying the rules and adding
       them to the sets as appropriate.  Each rule independently
       throws a die to determine which ruleset it will go into.  Sets must be already allocated.
       Comment: This function appends the splitted rulesets to the existing rulesets already in <i>sets</i>.
    */
    public RuleSet[] split( final EvolutionState state, final int thread, RuleSet[] sets )
        {
        for( int i = 0 ; i < numRules ; i++ )
            sets[ state.random[ thread ].nextInt( sets.length ) ].addRule(
                (Rule)(rules[i].protoCloneSimple()) );
        return sets;
        }
    
    /**
       Splits the rule set into a two disjoint rule sets, copying the rules and adding
       them to the sets as appropriate.  The value <i>prob</i> is the probability that an element will
       land in the first set.  Sets must be already allocated.
       Comment: This function appends the splitted rulesets to the existing rulesets already in <i>sets</i>.
    */
    public RuleSet[] splitIntoTwo( final EvolutionState state, final int thread, RuleSet[] sets, float prob )
        {
        for( int i = 0 ; i < numRules ; i++ )
            if (state.random[thread].nextBoolean(prob))
                sets[0].addRule((Rule)(rules[i].protoCloneSimple()) );
            else
                sets[1].addRule((Rule)(rules[i].protoCloneSimple()) );
        return sets;
        }
    

    /**
       Prints out the rule set in a readable fashion.
    */
    public void printRuleSetForHumans(final EvolutionState state, final int log,
                                      final int verbosity)
        {
        state.output.println( "Ruleset contains " + numRules + " rules",
                              verbosity, log );
        for( int i = 0 ; i < numRules ; i ++ )
            {
            state.output.println( "Rule " + i + ":", verbosity, log );
            rules[i].printRuleForHumans( state, verbosity, log );
            }
        }

    /**
       Prints the rule set such that the computer can read it later
    */
    public void printRuleSet(final EvolutionState state,
                             final int log, final int verbosity)
        {
        state.output.println(N_RULES + Code.encode(numRules), verbosity, log);
        for( int i = 0 ; i < numRules ; i ++ )
            rules[i].printRule(state,log,verbosity);
        }

    /**
       Prints the rule set such that the computer can read it later
    */
    public void printRuleSet(final EvolutionState state,
                             final PrintWriter writer)
        {
        writer.println( N_RULES + Code.encode(numRules) );
        for( int i = 0 ; i < numRules ; i ++ )
            rules[i].printRule(state,writer);
        }

    /**
       Reads the rule set
    */
    public void readRuleSet(final EvolutionState state,
                            final LineNumberReader reader)
        throws IOException, CloneNotSupportedException
        {
        int linenumber = reader.getLineNumber();
        String s = reader.readLine();
        if (s==null || !s.startsWith(N_RULES))
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Bad '" + N_RULES + "' line." + "\n-->" + s);
        DecodeReturn d = new DecodeReturn(s, N_RULES.length());
        Code.decode(d);
        if (d.type!=DecodeReturn.T_INT)
            state.output.fatal("Reading Line " + linenumber + ": " +
                               "Couldn't Decode '" + N_RULES + "' line." + "\n-->" + s);
        numRules = (int)d.l;

        rules = new Rule[ numRules ];
        for(int x=0;x<numRules;x++)
            {
            rules[x] = (Rule)(constraints().rulePrototype.protoClone());
            rules[x].readRule(state,reader);
            }
        }

    public Parameter defaultBase()
        {
        return RuleDefaults.base().push(P_RULESET);
        }

    public void setup(EvolutionState state, Parameter base)
        {        
        String constraintname = state.parameters.getString(
            base.push( P_CONSTRAINTS ),defaultBase().push(P_CONSTRAINTS));
        if (constraintname == null)
            state.output.fatal("No RuleSetConstraints name given",
                               base.push( P_CONSTRAINTS ),defaultBase().push(P_CONSTRAINTS));

        constraints = RuleSetConstraints.constraintsFor(constraintname,state).constraintNumber;
        state.output.exitIfErrors();
        }

    /**
       The hash code for the rule set.  This isn't a very good hash code,
       but it has the benefit of not being O(n lg n) -- otherwise, we'd have
       to do something like sort the rules in the individual first and then
       do an ordered hash code of some sort, ick.
    */
    public int hashCode()
        {
        int hash = this.getClass().hashCode();
        for(int x=0;x<rules.length;x++)
            if (rules[x] !=null) 
                hash += rules[x].hashCode();
        return hash;
        }

    public boolean equals( final Object _other )
        {
        if (!getClass().equals(_other.getClass()))  // not the same class, I'm conservative that way
            return false;
            
        RuleSet other = (RuleSet)_other;
        if( numRules != other.numRules )
            return false;  // quick and dirty
        if (numRules == 0 && other.numRules==0)
            return true;  // quick and dirty
            
        // we need to sort the rulesets.  First, let's clone
        // the rule arrays

        Rule[] srules = (Rule[])(rules.clone());
        Rule[] orules = (Rule[])(other.rules.clone());
        QuickSort.qsort(srules,srules[0]);
        QuickSort.qsort(orules,orules[0]);
        
        // Now march down and see if the rules are the same
        for(int x=0;x<numRules;x++)
            if (!(srules[x].equals(orules[x])))
                return false;

        return true;
        }

    }
