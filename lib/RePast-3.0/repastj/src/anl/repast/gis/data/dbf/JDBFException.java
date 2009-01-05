// ***********************************************************************************
// $Source$
// $RCSfile$
// $Revision$
// $Date$
// $Author$
// ***********************************************************************************



package anl.repast.gis.data.dbf;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 *
 * <p>Title: JDBF</p>
 * <p>Description: Used to indicate a fatal error, while writing or reading a database (DBF) file.</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : FUCaM-GTM</p>
  * @author Bart Jourquin, adapted from original free lib by SV Consulting (http://www.svcon.com/)
 * @version 1.0
 */
public class JDBFException extends Exception {

    private Throwable detail = null;

    /**
     * Constructs a JDBFException with a given message.
     * @param s String
     */
    public JDBFException(String s) {
        this(s, null);
    }

    /**
     * Constructs a nested JDBFException, containing another exception.
     * @param throwable Throwable
     */
    public JDBFException(Throwable throwable) {
        this(throwable.getMessage(), throwable);
    }

    /**
     * Constructs a nested JDBFException, containing another exception and a specific message.
     * @param s String
     * @param throwable Throwable
     */
    public JDBFException(String s, Throwable throwable) {
        super(s);
        detail = throwable;
    }

    /**
     * Returns the message for the exception.
     * @return String
     */
    public String getMessage() {
        if (detail == null)
            return super.getMessage();
        else
            return super.getMessage();
    }

    /**
     * Prints the stack of the exception.
     * @param printstream PrintStream
     */
    public void printStackTrace(PrintStream printstream) {
        if (detail == null)
            super.printStackTrace(printstream);
        else
            synchronized (printstream) {
                printstream.println(this);
                detail.printStackTrace(printstream);
            }
    }

    /**
     * Prints the stack of the exception to the standard error.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack of the exception.
     * @param printwriter PrintWriter
     */
    public void printStackTrace(PrintWriter printwriter) {
        if (detail == null)
            super.printStackTrace(printwriter);
        else
            synchronized (printwriter) {
                printwriter.println(this);
                detail.printStackTrace(printwriter);
            }
    }
}
