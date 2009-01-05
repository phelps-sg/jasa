package ec.app.ant;
import ec.util.*;
import ec.*;
import ec.gp.*;

/* 
 * AntData.java
 * 
 * Created: Wed Nov  3 18:32:13 1999
 * By: Sean Luke
 */

/**
 * Since Ant doesn't actually pass any information, this
 * object is effectively empty.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class AntData extends GPData
    {
    public GPData copyTo(final GPData gpd) 
        { return gpd; }
    }
