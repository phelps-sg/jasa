/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import JSci.awt.DefaultGraph2DModel;

import java.util.Iterator;
import java.util.Vector;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * <p>
 * A data writer that stores data in a memory-resident data structure
 * that can also be used as a data series model for a JSci graph,
 * or a table model for a swing JTable component.
 * </p>
 *
 * <p>
 * Each datum written to the DataWriter is one half a 2-dimensional
 * coordinate.  The first datum is typically a time value.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <code>
 * DataSeriesWriter timeSeries = new DataSeriesWriter();<br>
 * for( int t=0; t&lt1000; t++ ) {<br>
 *   timeSeries.newData(t);<br>
 *   timeSeries.newData(getValue(t));<br>
 * }<br>
 * </code>
 *
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class DataSeriesWriter extends DefaultGraph2DModel.DataSeries
    implements DataWriter, Serializable {

  protected boolean isVisible = true;

  protected boolean isXCoordinate = true;

  protected double xCoord;

  protected Vector data = new Vector();

  static Logger logger = Logger.getLogger(DataSeriesWriter.class);

  public DataSeriesWriter() {
    super( new double[] {}, new double[] {} );
  }

  public void newData( int datum ) {
    newData((double) datum);
  }

  public void newData( long datum ) {
    newData((double) datum);
  }

  public void newData( double datum ) {
    if ( isXCoordinate ) {
      xCoord = datum;
    } else {
      SeriesDatum d = new SeriesDatum(xCoord, datum);
      data.add(d);
    }
    isXCoordinate = !isXCoordinate;
  }

  public void newData( float datum ) {
    newData((double) datum);
  }

  public void clear() {
    data.clear();
  }

  public float getValue( int datum ) {
    return (float) getDatum(datum);
  }

  public float getCoord( int datum, int dimension ) {
    switch( dimension ) {
      case 0:
        return getXCoord(datum);
      case 1:
        return getYCoord(datum);
      default:
        throw new Error("Invalid dimension- " + dimension);
    }
  }

  public float getXCoord( int datum ) {
    if ( datum > data.size()-1 ) {
      return 0f;
    } else {
      return (float) ( (SeriesDatum) data.get(datum)).getX();
    }
  }

  public float getYCoord( int datum ) {
    return (float) getDatum(datum);
  }

  public double getDatum( int i ) {
    SeriesDatum datum = (SeriesDatum) data.get(i);
    double value = 0;
    if ( datum != null ) {
      value = ( (SeriesDatum) data.get(i)).getY();
    }
    if ( Double.isNaN(value) || Double.isInfinite(value) ) {
      return 0;
    } else {
      return value;
    }
  }

  public void flush() {
  }

  public void close() {
  }

  public int length() {
    return data.size();
  }


  public void newData(Iterator i) {
    /**@todo Implement this uk.ac.liv.util.io.DataWriter method*/
    throw new java.lang.UnsupportedOperationException("Method newData() not yet implemented.");
  }

  public void newData(Object[] data) {
    /**@todo Implement this uk.ac.liv.util.io.DataWriter method*/
    throw new java.lang.UnsupportedOperationException("Method newData() not yet implemented.");
  }

  public void newData(Object data) {
    /**@todo Implement this uk.ac.liv.util.io.DataWriter method*/
    throw new java.lang.UnsupportedOperationException("Method newData() not yet implemented.");
  }

  public void newData(boolean data) {
    /**@todo Implement this uk.ac.liv.util.io.DataWriter method*/
    throw new java.lang.UnsupportedOperationException("Method newData() not yet implemented.");
  }

  public String toString() {
    StringBuffer out = new StringBuffer("( " + getClass() + " ");
    Iterator i = data.iterator();
    while ( i.hasNext() ) {
      Object datum = i.next();
      out.append(datum.toString());
    }
    out.append(")");
    return out.toString();
  }

}

class SeriesDatum {

  double x;

  double y;

  public SeriesDatum( double x, double y ) {
    this.x = x;
    this.y = y;
  }

  public void setX( double x ) { this.x = x; }
  public void setY( double y ) { this.y = y; }

  public double getX() { return x; }
  public double getY() { return y; }

  public String toString() {
    return "(" + getClass() + " x:" + x + " y:" + y + ")";
  }

}