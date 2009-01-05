package ec.gp;
import ec.*;
import ec.steadystate.*;
import ec.util.*;
import java.io.*;

/* 
 * GPSteadyStateSpecies.java
 * 
 * Created: Fri Jun 14 18:02:35 EDT 2002
 * By: Sean Luke
 */

/**
 * GPSteadyStateSpecies is a subclass of GPSpecies which implements the
 * SteadyStateSpeciesForm; this basically means it provides a deselector
 * to pick individuals to die.
 *
 <p><b>Default Base</b><br>
 gp.steady-state-species

 *
 * @author Sean Luke
 * @version 1.0 
 */

public class GPSteadyStateSpecies extends GPSpecies implements SteadyStateSpeciesForm
    {
    public static final String P_GPSTEADYSTATESPECIES = "steady-state-species";

    public SelectionMethod deselector;

    public Parameter defaultBase()
        {
        return GPDefaults.base().push(P_GPSTEADYSTATESPECIES);
        }

    public SelectionMethod deselector() { return deselector; }

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);
        deselector = (SelectionMethod)(state.parameters.getInstanceForParameter(
                                           base.push(P_DESELECTOR), defaultBase().push(P_DESELECTOR),
                                           SelectionMethod.class));     
        deselector.setup(state,base.push(P_DESELECTOR));
        }    
    }
