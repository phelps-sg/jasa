package ec.util;

/* 
 * ParamClassLoadException.java
 * 
 * Created: Tue Aug 10 21:22:08 1999
 * By: Sean Luke
 */

/**
 * This exception is thrown by the Parameter Database when it fails to
 * locate and load a class specified by a given parameter as requested.
 * Most commonly this results in the program exiting with an error, so
 * it is defined as a RuntimeException so you don't have to catch it
 * or declare that you throw it.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class ParamClassLoadException extends RuntimeException
    {
    public ParamClassLoadException(String s)
        { super("\n"+s); }
    }
