package ec;

/* 
 * Clique.java
 * 
 * Created: Wed Oct 13 15:12:23 1999
 * By: Sean Luke
 */

/**
 * Clique is a class pattern marking classes which 
 * create only a few instances, generally accessible through
 * some global mechanism, and every single
 * one of which gets its own distinct setup(...) call.  Cliques should
 * <b>not</b> be Cloneable, but they are Serializable.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public interface Clique extends Setup
    {
    }
