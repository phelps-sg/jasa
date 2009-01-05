package ec.util;
import java.io.*;
import java.util.zip.*;

/**
 * CompressingInputStream and DecompressingOutputStream allow you to
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

public class CompressingOutputStream extends DeflaterOutputStream 
    {
    public CompressingOutputStream(OutputStream output)
        {
        super(output, new Deflater(Deflater.DEFAULT_COMPRESSION, true));
        }

    public CompressingOutputStream (OutputStream output, int level, boolean nowrap) 
        {
        super(output, new Deflater(level,nowrap));
        }

    // stolen straight from http://developer.java.sun.com/developer/bugParade/bugs/4255743.html
    private static final byte [] EMPTYBYTEARRAY = new byte [0];
    
    /**
     * Insure all remaining data will be output.
     */
    public void flush() throws IOException
        {
        if(!def.finished())
            {
            /**
             * Now this is tricky: We force the Deflater to flush
             * its data by switching compression level.
             * As yet, a perplexingly simple workaround for 
             * http://developer.java.sun.com/developer/bugParade/bugs/4255743.html 
             */
            def.setInput(EMPTYBYTEARRAY, 0, 0);

            def.setLevel(Deflater.NO_COMPRESSION);
            deflate();

            def.setLevel(Deflater.DEFAULT_COMPRESSION);
            deflate();

            super.flush();
            }
        }
    }

