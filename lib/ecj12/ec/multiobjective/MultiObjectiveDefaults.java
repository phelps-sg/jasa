package ec.multiobjective;
import ec.util.Parameter;
import ec.*;

/* 
 * MultiObjectiveDefaults.java
 * 
 * Created: Thu Jan 20 17:16:09 2000
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public final class MultiObjectiveDefaults implements DefaultsForm 
    {
    public static final String P_MULTI = "multi";

    /** Returns the default base. */
    public static final Parameter base()
        {
        return new Parameter(P_MULTI);
        }
    }
