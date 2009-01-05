package ec.steadystate;
import ec.*;

/* 
 * SteadyStateSpeciesForm.java
 * 
 * Created: Tue Oct 19 17:42:27 1999
 * By: Sean Luke
 */

/**
 * This interface defines an additional method which Species must
 * adhere to in order to work with a steady-state evolution mechanism.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public interface SteadyStateSpeciesForm 
    {
    /** deselector parameter */
    public static final String P_DESELECTOR = "deselector";

    /** Returns the selection method to be used for deselecting
        individuals in subpopulations of this species. 
        By "deselecting", we mean choosing individuals for removal
        from the population, to be replaced with newly-bred individuals. */
    public SelectionMethod deselector();
    }
