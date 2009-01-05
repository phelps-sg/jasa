package ec.app.multiplexerslow;
import ec.util.*;
import ec.*;
import ec.gp.*;

/* 
 * MultiplexerData.java
 * 
 * Created: Wed Nov  3 18:32:13 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class MultiplexerData extends GPData
    {
    // return value -- should ALWAYS be either 1 or 0
    public int x;

    public GPData copyTo(final GPData gpd) 
        { ((MultiplexerData)gpd).x = x; return gpd; }
    }
