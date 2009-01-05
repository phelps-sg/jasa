// ***********************************************************************************
// $Source$
// $RCSfile$
// $Revision$
// $Date$
// $Author$
// ***********************************************************************************



package anl.repast.gis.data.dbf;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * <p>Title: JDBF</p>
 * <p>Description: Used to read database (DBF) files.<p>
 *
 * Create a DBFReader object passing a file name to be opened, and use hasNextRecord and nextRecord
 * functions to iterate through the records of the file. <br>
 * The getFieldCount and getField methods allow you to find out what are the fields of the database file. </p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : FUCaM-GTM</p>
 * @author Bart Jourquin, adapted from original free lib by SV Consulting (http://www.svcon.com/)
 * @version 1.0
 */
public class DBFReader {

    private DataInputStream stream = null;
    private JDBField fields[] = null;
    private byte nextRecord[] = null;

    /**
     * Opens a DBF file for reading.
     * @param s String
     * @throws JDBFException
     */
    public DBFReader(String s) throws JDBFException {
        stream = null;
        fields = null;
        nextRecord = null;
        try {
            init(new FileInputStream(s.trim()));
        }
        catch (FileNotFoundException filenotfoundexception) {
            throw new JDBFException(filenotfoundexception);
        }
    }

    /**
     * Opens a stream, containing DBF for reading.
     * @param inputstream InputStream
     * @throws JDBFException
     */
    public DBFReader(InputStream inputstream) throws JDBFException {
        stream = null;
        fields = null;
        nextRecord = null;
        init(inputstream);
    }

    /**
     * Initialises the reader
     * @param inputstream InputStream
     * @throws JDBFException
     */
    private void init(InputStream inputstream) throws JDBFException {
        try {
            stream = new DataInputStream(inputstream);
            int i = readHeader();
            fields = new JDBField[i];
            int j = 1;
            for (int k = 0; k < i; k++) {
                fields[k] = readFieldHeader();
                j += fields[k].getLength();
            }

            if (stream.read() < 1)
                throw new JDBFException("Unexpected end of file reached.");
            nextRecord = new byte[j];
            try {
                stream.readFully(nextRecord);
            }
            catch (EOFException eofexception) {
                nextRecord = null;
                stream.close();
            }
        }
        catch (IOException ioexception) {
            throw new JDBFException(ioexception);
        }
    }

    /**
     * Reads header
     * @throws IOException
     * @throws JDBFException
     * @return int
     */
    private int readHeader() throws IOException, JDBFException {
        byte abyte0[] = new byte[16];
        try {
            stream.readFully(abyte0);
        }
        catch (EOFException eofexception) {
            throw new JDBFException("Unexpected end of file reached.");
        }
        int i = abyte0[8];
        if (i < 0)
            i += 256;
        i += 256 * abyte0[9];
        i = --i / 32;
        i--;
        try {
            stream.readFully(abyte0);
        }
        catch (EOFException eofexception1) {
            throw new JDBFException("Unexpected end of file reached.");
        }
        return i;
    }

    /**
     * Reads field header
     * @throws IOException
     * @throws JDBFException
     * @return JDBField
     */
    private JDBField readFieldHeader() throws IOException, JDBFException {
        byte abyte0[] = new byte[16];
        try {
            stream.readFully(abyte0);
        }
        catch (EOFException eofexception) {
            throw new JDBFException("Unexpected end of file reached.");
        }
        StringBuffer stringbuffer = new StringBuffer(10);
        for (int i = 0; i < 10; i++) {
            if (abyte0[i] == 0)
                break;
            stringbuffer.append((char)abyte0[i]);
        }

        char c = (char)abyte0[11];
        try {
            stream.readFully(abyte0);
        }
        catch (EOFException eofexception1) {
            throw new JDBFException("Unexpected end of file reached.");
        }
        int j = abyte0[0];
        int k = abyte0[1];
        if (j < 0)
            j += 256;
        if (k < 0)
            k += 256;
        return new JDBField(stringbuffer.toString(), c, j, k);
    }

    /**
     * Returns the field count of the database file.
     * @return int
     */
    public int getFieldCount() {
        return fields.length;
    }

    /**
     * Returns a field at a specified position.
     * @param i int
     * @return JDBField
     */
    public JDBField getField(int i) {
        return fields[i];
    }

    /**
     * Checks to see if there are more records in the file.
     * @return boolean
     */
    public boolean hasNextRecord() {
        return nextRecord != null;
    }

    /**
     * Returns an array of objects, representing one record in the database file.
     * @throws JDBFException
     * @return Object[]
     */
    public Object[] nextRecord() throws JDBFException {
        if (!hasNextRecord())
            throw new JDBFException("No more records available.");
        Object aobj[] = new Object[fields.length];
        int i = 1;
        for (int j = 0; j < aobj.length; j++) {
            int k = fields[j].getLength();
            StringBuffer stringbuffer = new StringBuffer(k);
            stringbuffer.append(new String(nextRecord, i, k));
            aobj[j] = fields[j].parse(stringbuffer.toString());
            i += fields[j].getLength();
        }

        try {
            stream.readFully(nextRecord);
        }
        catch (EOFException eofexception) {
            nextRecord = null;
        }
        catch (IOException ioexception) {
            throw new JDBFException(ioexception);
        }
        return aobj;
    }
}
