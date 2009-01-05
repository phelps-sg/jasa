package ec.util;
import java.util.*;

/* 
 * Version.java
 * 
 * Created: Wed Aug 11 19:44:46 1999
 * By: Sean Luke
 */

/**
 * Version is a static class which stores version information for this
 * evolutionary computation system.
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class Version
    {
    public static final String name = "ECJ";
    public static final String version = "12";
    public static final String copyright = "2004";
    public static final String author = "Sean Luke";
    public static final String contributors = "L. Panait, G. Balan, Z. Skolicki, J. Bassett,\n|               R. Hubley, and A. Chircop";
    public static final String authorEmail = "sean@cs.gmu.edu";
    public static final String authorURL = "http://cs.gmu.edu/~eclab/projects/ecj/";
    public static final String date = "September 21, 2004";
    public static final String minimumJavaVersion = "1.2";

    public static final String message()
        {
        Properties p = System.getProperties();
        String javaVersion = p.getProperty("java.version");
        String javaVM = p.getProperty("java.vm.name");
        String javaVMVersion = p.getProperty("java.vm.version");
        if (javaVM!=null) javaVersion = javaVersion + " / " + javaVM;
        if (javaVM!=null && javaVMVersion!=null) javaVersion = javaVersion + "-" + javaVMVersion;
        
    
        return 
            "\n| " + name + 
            "\n| An evolutionary computation system (version " + version + ")" +
            "\n| Copyright " + copyright + " by " + author +
            "\n| Contributors: " + contributors +
            "\n| URL: " + authorURL +
            "\n| Mail: " + authorEmail +
            "\n| Date: " + date +
            "\n| Current Java: " + javaVersion +
            "\n| Required Minimum Java: " + minimumJavaVersion +
            "\n\n";
        }
    }
