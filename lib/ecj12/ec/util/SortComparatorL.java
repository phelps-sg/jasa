package ec.util;

/* 
 * SortComparatorL.java
 * 
 * Created: Wed Nov  3 16:10:02 1999
 * By: Sean Luke
 */

/**
 * The interface for passing objects to ec.util.QuickSort
 *
 * @author Sean Luke
 * @version 1.0 
 */

public interface SortComparatorL 
    {
    /** Returns true if a < b, else false */
    public boolean lt(long a, long b);

    /** Returns true if a > b, else false */
    public boolean gt(long a, long b);
    }
