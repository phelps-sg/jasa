package ec.gp;
import ec.Initializer;
import ec.simple.SimpleInitializer;
import ec.util.Parameter;
import ec.EvolutionState;

/* 
 * GPInitializer.java
 * 
 * Created: Tue Oct  5 18:40:02 1999
 * By: Sean Luke
 */

/**
 * GPInitializer is a SimpleInitializer which sets up all the Cliques,
 * ( the initial
 * [tree/node]constraints, types, and function sets) for the GP system.
 * 
 * <p>Note that the Cliques must be set up in a very particular order:

 <ol><li>GPType</li><li>GPNodeConstraints</li><li>GPFunctionSets</li><li>GPTreeConstraints</li></ol>

 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><tt>gp.type</tt></td>
 <td>GPTypes</td></tr>
 <tr><td valign=top><tt>gp.nc</tt></td>
 <td>GPNodeConstraints</td></tr>
 <tr><td valign=top><tt>gp.tc</tt></td>
 <td>GPTreeConstraints</td></tr>
 <tr><td valign=top><tt>gp.fs</tt></td>
 <td>GPFunctionSets</td></tr>

 </table>

 * @author Sean Luke
 * @version 1.0 
 */

public class GPInitializer extends SimpleInitializer 
    {
    // used just here, so far as I know :-)
    public final static String P_TYPE = "type";
    public final static String P_NODECONSTRAINTS = "nc";
    public final static String P_TREECONSTRAINTS = "tc";
    public final static String P_FUNCTIONSETS = "fs";

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);

        // This is a good place to set up the types.  We use our own base off the
        // default GP base.  This MUST be done before loading constraints.
        GPType.setupTypes(state,GPDefaults.base().push(P_TYPE));

        // Now let's load our constraints and function sets also.
        // This is done in a very specific order, don't change it or things
        // will break.
        GPNodeConstraints.setupConstraints(
            state,GPDefaults.base().push(P_NODECONSTRAINTS));
        GPFunctionSet.setupFunctionSets(
            state,GPDefaults.base().push(P_FUNCTIONSETS));
        GPTreeConstraints.setupConstraints(
            state,GPDefaults.base().push(P_TREECONSTRAINTS));
        }
    }
