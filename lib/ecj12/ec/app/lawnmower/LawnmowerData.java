package ec.app.lawnmower;
import ec.util.*;
import ec.*;
import ec.gp.*;

/* 
 * LawnmowerData.java
 * 
 * Created: Wed Nov  3 18:32:13 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class LawnmowerData extends GPData
    {
    // return value
    public int x;
    public int y;

    public GPData copyTo(final GPData gpd) 
        {
        LawnmowerData d = (LawnmowerData)gpd;
        d.x = x;
        d.y = y;
        return gpd; 
        }
    }
