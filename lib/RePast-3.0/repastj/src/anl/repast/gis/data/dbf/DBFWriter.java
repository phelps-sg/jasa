// ***********************************************************************************
// $Source$
// $RCSfile$
// $Revision$
// $Date$
// $Author$
// ***********************************************************************************


package anl.repast.gis.data.dbf;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;


/**
 *
 * <p>Title: JDBF</p>
 * <p>Description: Used to write database (DBF) files.<br>
 *
 * Create a DBFWriter passing a file name and a list of fields, then add the
 * records one by one, and close it. Make sure you always close your DBF files,
 * even if there is an error writing some of the records. </p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : FUCaM-GTM</p>
 * @author Bart Jourquin, adapted from original free lib by SV Consulting (http://www.svcon.com/)
 * @version 1.0
 */


public class DBFWriter {

    private BufferedOutputStream stream = null;
    private int recCount = 0;
    private JDBField fields[] = null;
    private String fileName = null;
    private String dbfEncoding = null;

    /**
     * Opens a DBF file for writing.
     * @param s String
     * @param ajdbfield JDBField[]
     * @throws JDBFException
     */
    public DBFWriter(String s, JDBField ajdbfield[]) throws JDBFException {
        stream = null;
        recCount = 0;
        fields = null;
        fileName = null;
        dbfEncoding = null;
        fileName = s;
        try {
            init(new FileOutputStream(s), ajdbfield);
        }
        catch (FileNotFoundException filenotfoundexception) {
            throw new JDBFException(filenotfoundexception);
        }
    }

    /**
     * Opens an output stream for writing.
     * @param outputstream OutputStream
     * @param ajdbfield JDBField[]
     * @throws JDBFException
     */
    public DBFWriter(OutputStream outputstream, JDBField ajdbfield[]) throws JDBFException {
        stream = null;
        recCount = 0;
        fields = null;
        fileName = null;
        dbfEncoding = null;
        init(outputstream, ajdbfield);
    }

    /**
     * Opens a DBF file for writing, bytes in DBF will be converted from UNICODE to national charset.
     * @param s String
     * @param ajdbfield JDBField[]
     * @param s1 String
     * @throws JDBFException
     */
    public DBFWriter(String s, JDBField ajdbfield[], String s1) throws JDBFException {
        stream = null;
        recCount = 0;
        fields = null;
        fileName = null;
        dbfEncoding = null;
        fileName = s;
        try {
            dbfEncoding = s1;
            init(new FileOutputStream(s), ajdbfield);
        }
        catch (FileNotFoundException filenotfoundexception) {
            throw new JDBFException(filenotfoundexception);
        }
    }

    /**
     * Initializes the writer
     * @param outputstream OutputStream
     * @param ajdbfield JDBField[]
     * @throws JDBFException
     */
    private void init(OutputStream outputstream, JDBField ajdbfield[]) throws JDBFException {
        fields = ajdbfield;
        try {
            stream = new BufferedOutputStream(outputstream);
            writeHeader();
            for (int i = 0; i < ajdbfield.length; i++)
                writeFieldHeader(ajdbfield[i]);

            stream.write(13);
            stream.flush();
        }
        catch (Exception exception) {
            throw new JDBFException(exception);
        }
    }

    /**
     * Writes .dbf header
     * @throws IOException
     */
    private void writeHeader() throws IOException {
        byte abyte0[] = new byte[16];
        abyte0[0] = 3;
        Calendar calendar = Calendar.getInstance();
        abyte0[1] = (byte)(calendar.get(1) - 1900);
        abyte0[2] = (byte)calendar.get(2);
        abyte0[3] = (byte)calendar.get(5);
        abyte0[4] = 0;
        abyte0[5] = 0;
        abyte0[6] = 0;
        abyte0[7] = 0;
        int i = (fields.length + 1) * 32 + 1;
        abyte0[8] = (byte)(i % 256);
        abyte0[9] = (byte)(i / 256);
        int j = 1;
        for (int k = 0; k < fields.length; k++)
            j += fields[k].getLength();

        abyte0[10] = (byte)(j % 256);
        abyte0[11] = (byte)(j / 256);
        abyte0[12] = 0;
        abyte0[13] = 0;
        abyte0[14] = 0;
        abyte0[15] = 0;
        stream.write(abyte0, 0, abyte0.length);
        for (int l = 0; l < 16; l++)
            abyte0[l] = 0;

        stream.write(abyte0, 0, abyte0.length);
    }

    /**
     * Writes .dbf field header
     * @param jdbfield JDBField
     * @throws IOException
     */
    private void writeFieldHeader(JDBField jdbfield) throws IOException {
        byte abyte0[] = new byte[16];
        String s = jdbfield.getName();
        int i = s.length();
        if (i > 10)
            i = 10;
        for (int j = 0; j < i; j++)
            abyte0[j] = (byte)s.charAt(j);

        for (int k = i; k <= 10; k++)
            abyte0[k] = 0;

        abyte0[11] = (byte)jdbfield.getType();
        abyte0[12] = 0;
        abyte0[13] = 0;
        abyte0[14] = 0;
        abyte0[15] = 0;
        stream.write(abyte0, 0, abyte0.length);
        for (int l = 0; l < 16; l++)
            abyte0[l] = 0;

        abyte0[0] = (byte)jdbfield.getLength();
        abyte0[1] = (byte)jdbfield.getDecimalCount();
        stream.write(abyte0, 0, abyte0.length);
    }

    /**
     * Writes a record to the DBF file.
     * @param aobj Object[]
     * @throws JDBFException
     */
    public void addRecord(Object aobj[]) throws JDBFException {
        if (aobj.length != fields.length)
            throw new JDBFException("Error adding record: Wrong number of values. Expected " + fields.length + ", got " + aobj.length + ".");
        int i = 0;
        for (int j = 0; j < fields.length; j++)
            i += fields[j].getLength();

        byte abyte0[] = new byte[i];
        int k = 0;
        for (int l = 0; l < fields.length; l++) {
            String s = fields[l].format(aobj[l]);
            byte abyte1[];
            try {
                if (dbfEncoding != null)
                    abyte1 = s.getBytes(dbfEncoding);
                else
                    abyte1 = s.getBytes();
            }
            catch (UnsupportedEncodingException unsupportedencodingexception) {
                throw new JDBFException(unsupportedencodingexception);
            }
            for (int i1 = 0; i1 < fields[l].getLength(); i1++)
                abyte0[k + i1] = abyte1[i1];

            k += fields[l].getLength();
        }

        try {
            stream.write(32);
            stream.write(abyte0, 0, abyte0.length);
            stream.flush();
        }
        catch (IOException ioexception) {
            throw new JDBFException(ioexception);
        }
        recCount++;
    }

    /**
     * Closes the DBF file.
     * @throws JDBFException
     */
    public void close() throws JDBFException {
        try {
            stream.write(26);
            stream.close();
            RandomAccessFile randomaccessfile = new RandomAccessFile(fileName, "rw");
            randomaccessfile.seek(4L);
            byte abyte0[] = new byte[4];
            abyte0[0] = (byte)(recCount % 256);
            abyte0[1] = (byte)((recCount / 256) % 256);
            abyte0[2] = (byte)((recCount / 65536) % 256);
            abyte0[3] = (byte)((recCount / 16777216) % 256);
            randomaccessfile.write(abyte0, 0, abyte0.length);
            randomaccessfile.close();
        }
        catch (IOException ioexception) {
            throw new JDBFException(ioexception);
        }
    }
}
