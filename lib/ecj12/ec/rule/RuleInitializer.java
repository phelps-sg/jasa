package ec.rule;

import ec.Initializer;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import ec.EvolutionState;

/* 
 * RuleInitializer.java
 * 
 * Created: Fri Sep 14 14:00:02 2001
 * By: Liviu Panait
 *
 */
 
/** A SimpleInitializer subclass designed to be used with rules.  Basically,
    the RuleInitializer sets up the RuleConstraints and RuleSetConstraints cliques
    at setup() time, and does nothing else different from SimpleInitializer. 
    The RuleInitializer also specifies the parameter bases for the RuleSetConstraints
    and RuleConstraints objects.  
 
    <p><b>Parameter bases</b><br>
    <table>
    <tr><td valign=top><tt>rule.rsc</tt></td>
    <td>RuleSetConstraints</td></tr>
    <tr><td valign=top><tt>rule.rc</tt></td>
    <td>RuleConstraints</td></tr>
    </table>
*/

public class RuleInitializer extends SimpleInitializer
    {
    // used just here, so far as I know :-)
    public final static String P_RULESETCONSTRAINTS = "rsc";
    public final static String P_RULECONSTRAINTS = "rc";

    /** Sets up the RuleConstraints and RuleSetConstraints cliques. */
    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);

        // Now let's load our constraints and function sets also.
        // This is done in a very specific order, don't change it or things
        // will break.
        RuleConstraints.setupConstraints(
            state, RuleDefaults.base().push( P_RULECONSTRAINTS ) );
        RuleSetConstraints.setupRuleSetConstraints(
            state, RuleDefaults.base().push( P_RULESETCONSTRAINTS ) );
        }
    }
