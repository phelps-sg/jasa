// ***********************************************************************************
// $Source$
// $RCSfile$
// $Revision$
// $Date$
// $Author$
// ***********************************************************************************

package anl.repast.gis.data.dbf;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * <p>Title: JDBF</p>
 * <p>Description: A database field. Its attributes are name, type, length, and decimal count.</p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : FUCaM-GTM</p>
 * @author Bart Jourquin, adapted from original free lib by SV Consulting (http://www.svcon.com/)
 * @version 1.0
 */

public class JDBField {

    private String name = null;
    private char type = 0;
    private int length = 0;
    private int decimalCount = 0;
    private DecimalFormatSymbols DFS = new DecimalFormatSymbols();

    /**
     * Construct a database field with a given name, type, length, and decimal count.
     * @param s String
     * @param c char
     * @param i int
     * @param j int
     * @throws JDBFException
     */
    public JDBField(String s, char c, int i, int j) throws JDBFException {
        if (s.length() > 10)
            throw new JDBFException("The field name is more than 10 characters long: " + s);
        if (c != 'C' && c != 'N' && c != 'L' && c != 'D' && c != 'F')
            throw new JDBFException("The field type is not a valid. Got: " + c);
        if (i < 1)
            throw new JDBFException("The field length should be a positive integer. Got: " + i);
        if (c == 'C' && i >= 254)
            throw new JDBFException("The field length should be less than 254 characters for character fields. Got: " + i);
      // if (c == 'N' && i >= 21)
         //   throw new JDBFException("The field length should be less than 21 digits for numeric fields. Got: " + i);
        if (c == 'L' && i != 1)
            throw new JDBFException("The field length should be 1 characater for logical fields. Got: " + i);
        if (c == 'D' && i != 8)
            throw new JDBFException("The field length should be 8 characaters for date fields. Got: " + i);
        if (c == 'F' && i >= 21)
            throw new JDBFException("The field length should be less than 21 digits for floating point fields. Got: " + i);
        if (j < 0)
            throw new JDBFException("The field decimal count should not be a negative integer. Got: " + j);
        if ((c == 'C' || c == 'L' || c == 'D') && j != 0)
            throw new JDBFException("The field decimal count should be 0 for character, logical, and date fields. Got: " + j);
        if (j > i - 1) {
            throw new JDBFException("The field decimal count should be less than the length - 1. Got: " + j);
        } else {
            name = s;
            type = c;
            length = i;
            decimalCount = j;
            DFS.setDecimalSeparator('.');
            return;
        }
    }

    /**
     * Returns the name of the field.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the field.
     * @return char
     */
    public char getType() {
        return type;
    }

    /**
     * Returns the length of the field.
     * @return int
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the decimal count of the field.
     * @return int
     */
    public int getDecimalCount() {
        return decimalCount;
    }

    /**
     * Formats a value to its String representation, based on the fields
     * @param obj Object
     * @throws JDBFException
     * @return String
     */
    public String format(Object obj) throws JDBFException {
        if (type == 'N' || type == 'F') {
            if (obj == null)
                obj = new Double(0.0D);
            if (obj instanceof Number) {
                Number number = (Number)obj;
                StringBuffer stringbuffer = new StringBuffer(getLength());
                for (int i = 0; i < getLength(); i++)
                    stringbuffer.append("#");

                if (getDecimalCount() > 0)
                    stringbuffer.setCharAt(getLength() - getDecimalCount() - 1, '.');
                DecimalFormat decimalformat = new DecimalFormat(stringbuffer.toString(), DFS);
                String s1 = decimalformat.format(number);
                int k = getLength() - s1.length();
                if (k < 0)
                    throw new JDBFException("Value " + number + " cannot fit in pattern: '" + stringbuffer + "'.");
                StringBuffer stringbuffer2 = new StringBuffer(k);
                for (int l = 0; l < k; l++)
                    stringbuffer2.append(" ");

                return stringbuffer2 + s1;
            } else {
                throw new JDBFException("Expected a Number, got " + obj.getClass() + ".");
            }
        }
        if (type == 'C') {
            if (obj == null)
                obj = "";
            if (obj instanceof String) {
                String s = (String)obj;
                if (s.length() > getLength())
                    throw new JDBFException("'" + obj + "' is longer than " + getLength() + " characters.");
                StringBuffer stringbuffer1 = new StringBuffer(getLength() - s.length());
                for (int j = 0; j < getLength() - s.length(); j++)
                    stringbuffer1.append(' ');

                return s + stringbuffer1;
            } else {
                throw new JDBFException("Expected a String, got " + obj.getClass() + ".");
            }
        }
        if (type == 'L') {
            if (obj == null)
                obj = new Boolean(false);
            if (obj instanceof Boolean) {
                Boolean boolean1 = (Boolean)obj;
                return boolean1.booleanValue() ? "Y" : "N";
            } else {
                throw new JDBFException("Expected a Boolean, got " + obj.getClass() + ".");
            }
        }
        if (type == 'D') {
            if (obj == null)
                obj = new Date();
            if (obj instanceof Date) {
                Date date = (Date)obj;
                SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
                return simpledateformat.format(date);
            } else {
                throw new JDBFException("Expected a Date, got " + obj.getClass() + ".");
            }
        } else {
            throw new JDBFException("Unrecognized JDBFField type: " + type);
        }
    }

    /**
     * Parses a formatted String and returns an object of type, corresponding to this field.
     * @param s String
     * @throws JDBFException
     * @return Object
     */
    public Object parse(String s) throws JDBFException {
        s = s.trim();
        if (type == 'N' || type == 'F') {
            if (s.equals(""))
                s = "0";
            try {
                if (getDecimalCount() == 0)
                    return new Long(s);
                else
                    return new Double(s);
            }
            catch (NumberFormatException numberformatexception) {
                throw new JDBFException(numberformatexception);
            }
        }
        if (type == 'C')
            return s;
        if (type == 'L') {
            if (s.equals("Y") || s.equals("y") || s.equals("T") || s.equals("t"))
                return new Boolean(true);
            if (s.equals("N") || s.equals("n") || s.equals("F") || s.equals("f"))
                return new Boolean(false);
            else
                throw new JDBFException("Unrecognized value for logical field: " + s);
        }
        if (type == 'D') {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
            try {
                if ("".equals(s))
                    return null;
                else
                    return simpledateformat.parse(s);
            }
            catch (ParseException parseexception) {
                //throw new JDBFException(parseexception);
                return null;
            }
        } else {
            throw new JDBFException("Unrecognized JDBFField type: " + type);
        }
    }

    /**
     * Returns the String representation of the JDBField.
     * @return String
     */
    public String toString() {
        return name;
    }
}
