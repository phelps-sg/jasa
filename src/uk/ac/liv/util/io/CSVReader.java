package uk.ac.liv.util.io;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class CSVReader  {

  BufferedReader in;
  char seperator;
  int numColumns;
  List types;
  static final char DEFAULT_SEPERATOR = '\t';


  public CSVReader( InputStream in, List types, char seperator ) {
    this.in = new BufferedReader(new InputStreamReader(in));
    this.numColumns = numColumns;
    this.seperator = seperator;
    this.types = types;
  }

  public CSVReader( InputStream in, List types ) {
    this(in, types, DEFAULT_SEPERATOR);
  }

  public List nextRecord() throws IOException {
    String line = in.readLine();
    List record = new ArrayList(types.size());
    if ( line == null ) {
      return null;
    }
    StringTokenizer tokens = new StringTokenizer(line,seperator+"");
    Iterator typeIt = types.iterator();
    while ( typeIt.hasNext() ) {
      String fieldStr = tokens.nextToken();
      Class type = (Class) typeIt.next();
      record.add(convert(fieldStr,type));
    }
    return (List) record;
  }

  public static Object convert( String str, Class type ) {
    Method valueOf = null;
    try {
      Class[] strParam = new Class[1];
      strParam[0] = str.getClass();
      valueOf = type.getDeclaredMethod("valueOf", strParam);
    } catch ( NoSuchMethodException e ) {
      valueOf = null;
    }
    if ( valueOf != null ) {
      Object[] params = new Object[1];
      params[0] = (Object) str;
      try {
        return valueOf.invoke(type, params);
      } catch ( InvocationTargetException e ) {
        throw new NumberFormatException(str);
      } catch ( IllegalArgumentException e ) {
        // fail silently?
      } catch ( IllegalAccessException e ) {
        // fail silently?
      }
    } else {
      // No valueOf method, just return a String
      return (Object) str;
    }
    return null;
  }

}