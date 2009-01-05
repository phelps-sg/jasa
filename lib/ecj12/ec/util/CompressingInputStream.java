package ec.util;
import java.io.*;
import java.util.zip.*;

/**
 * CompressingInputStream and CompressingOutputStream allow you to
 * perform compression on a stream, and flush it, and continue to use it.
 * This is useful if you're planning on compressing a stream socket.
 * Ordinarily compression does not send partial blocks across the stream
 * for efficiency's sake.  That's not good because we want to use a stream
 * socket and the other side will just block waiting for a big chunk of data
 * that may take a long time to show up.  This simulates the Z_PARTIAL_FLUSH
 * and Z_SYNC_FLUSH mechanisms in zlib.  The code was largely stolen from
 * the comments at
 * <p>
 * http://developer.java.sun.com/developer/bugParade/bugs/4255743.html <br>
 * http://developer.java.sun.com/developer/bugParade/bugs/4206909.html <br>
 */

public class CompressingInputStream extends InflaterInputStream 
    {

    public CompressingInputStream (final InputStream in) 
        {
        // Using Inflater with nowrap == true will omit headers and trailers
        super(in, new Inflater(true));
        }

    public CompressingInputStream (final InputStream in, boolean nowrap) 
        {
        // Using Inflater with nowrap == true will omit headers and trailers
        super(in, new Inflater(nowrap));
        }

    /**
     * available() should return the number of bytes that can be read without
     * running into blocking wait. Accomplishing this feast would eventually require
     * to pre-inflate a huge chunk of data, so we rather opt for a more relaxed
     * contract (java.util.zip.InflaterInputStream does not fit the bill). 
     * This code has been tested to work with BufferedReader.readLine();
     */
    public int available() throws IOException 
        {
        if (!inf.finished() && !inf.needsInput())  return 1;
        else return in.available();
        }
    } 
