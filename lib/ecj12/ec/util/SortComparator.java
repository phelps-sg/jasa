package ec.util;

/* 
 * SortComparator.java
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

public interface SortComparator 
    {
    /** Returns true if a < b, else false */
    public boolean lt(Object a, Object b);

    /** Returns true if a > b, else false */
    public boolean gt(Object a, Object b);
    }
