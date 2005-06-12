/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.util.io;

import java.util.Iterator;

import java.io.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;

/**
 * A class for writing data to CSV (comma-separated variables) text files.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class CSVWriter implements Parameterizable, Serializable, DataWriter {

  protected PrintStream out;

  protected int numColumns;

  protected int currentColumn = 0;

  protected char seperator = DEFAULT_SEPERATOR;
  
  protected boolean append = false;

  static final char DEFAULT_SEPERATOR = '\t';

  public static final String P_FILENAME = "filename";
  public static final String P_COLUMNS = "columns";
  public static final String P_APPEND = "append";

  public CSVWriter( OutputStream out, int numColumns, char seperator ) {
    this.out = new PrintStream(new BufferedOutputStream(out));
    this.numColumns = numColumns;
    this.seperator = seperator;
  }

  public CSVWriter( OutputStream out, int numColumns ) {
    this(out, numColumns, DEFAULT_SEPERATOR);
  }

  public CSVWriter() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    try {
      String fileName = parameters.getString(base.push(P_FILENAME), null);
      append = parameters.getBoolean(base.push(P_APPEND), null, append);
      out = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(fileName), append)));
      numColumns = parameters.getIntWithDefault(base.push(P_COLUMNS), null,
          numColumns);
    } catch ( FileNotFoundException e ) {
      throw new Error(e);
    }
  }

  public void newData( Iterator i ) {
    while ( i.hasNext() ) {
      newData(i.next());
    }
  }

  public void newData( Object[] data ) {
    for ( int i = 0; i < data.length; i++ ) {
      newData(data[i]);
    }
  }

  public void newData( Object data ) {
    out.print(data.toString());
    nextColumn();
  }

  public void newData( int data ) {
    out.print(data);
    nextColumn();
  }

  public void newData( long data ) {
    out.print(data);
    nextColumn();
  }

  public void newData( double data ) {
    out.print(data);
    nextColumn();
  }

  public void newData( float data ) {
    out.print(data);
    nextColumn();
  }

  public void newData( boolean data ) {
    if ( data ) {
      newData(1);
    } else {
      newData(0);
    }
  }

  public void flush() {
    out.flush();
  }

  public void close() {
    out.close();
  }

  public void setNumColumns( int numColumns ) {
    this.numColumns = numColumns;
  }

  protected void nextColumn() {
    currentColumn++;
    if ( currentColumn < numColumns ) {
      out.print(seperator);
    } else {
      out.println();
      currentColumn = 0;
    }
  }

  private void writeObject( java.io.ObjectOutputStream out ) throws IOException {
  }

  private void readObject( java.io.ObjectInputStream in ) throws IOException,
      ClassNotFoundException {
  }

}
