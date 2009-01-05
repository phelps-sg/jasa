package ec.gp;
import ec.*;
import ec.util.*;
import java.util.*;

/* 
 * GPFuncInfo.java
 * 
 * Created: Thu Oct 14 17:01:50 1999
 * By: Sean Luke
 */

/**
 * GPFuncInfo is a Prototype wrapper object which holds a single GPNode
 * and is stored in arrays keyed by the GPNode's type.  The purpose of
 * GPFuncInfo is to provide a hook for future development to add "facts"
 * about GPNode prototypes as they're stored away waiting to be cloned
 * into trees, without having to modify GPNode itself, which would
 * be brittle since it's the superclass of all GP function nodes.
 *
 <p><b>Default Base</b></br>
 gp.func-info
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class GPFuncInfo implements Prototype
    {
    public static final String P_GPFUNCINFO = "func-info";

    /** The stored node */
    public GPNode node;
    
    public Parameter defaultBase()
        {
        return GPDefaults.base().push(P_GPFUNCINFO);
        }

    public void setup(final EvolutionState state, final Parameter base)  
        {
        // presently, nothing.
        node = null; // be on the safe side
        return;
        }
    
    /** Override this to rearrange hash's contents as you see fit.
        hash contains arrays of GPFuncInfo objects of your class,
        keyed by a GPType (there's one array for every GPType there is).
        For example, you might sort elements in each array by some
        probability of occurrence, if you've subclassed GPFuncInfo to
        work with a GPNodeGenerator of some sort that requires this.
        This method only gets called on prototypes, though it might get
        called more than once, and with different values for hash. 
        Return the resultant hashtable (or if you've changed it to
        some other hashtable, return that). */

    public Hashtable arrange(final Hashtable hash)
        {
        // presently, nothing
        return hash;
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
    }
