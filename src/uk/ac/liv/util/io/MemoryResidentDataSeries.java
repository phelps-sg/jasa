/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import JSci.awt.DataSeries;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * <p>
 * A data writer that stores data in a memory-resident data structure
 * that can also be used as a data series for a JSci graph.
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
 * MemoryResidentDataSeries timeSeries = new MemoryResidentDataSeries();
 * for( int t=0; t<1000; t++ ) {
 *   timeSeries.newData(t);
 *   timeSeries.newData(getValue(t));
 * }
 * </code>
 *
 *
 * @author Steve Phelps
 */

public class MemoryResidentDataSeries implements DataWriter, DataSeries {

  Vector data;

  protected boolean isVisible = true;

  protected boolean isXCoordinate = true;

  protected double xCoord;

  static Logger logger = Logger.getLogger(MemoryResidentDataSeries.class);

  public MemoryResidentDataSeries( int initialCapacity ) {
    data = new Vector(initialCapacity);
  }

  public MemoryResidentDataSeries() {
    data = new Vector();
  }

  public void newData( int datum ) {
    newData((double) datum);
  }

  public void newData( long datum ) {
    newData((double) datum);
  }

  public void newData( double datum ) {
    logger.debug("newData(" + datum + ")");
    if ( isXCoordinate ) {
      xCoord = datum;
    } else {
      SeriesDatum d = new SeriesDatum(xCoord, datum);
      logger.debug("Adding " + d);
      data.add(d);
    }
    isXCoordinate = !isXCoordinate;
  }

  public void clear() {
    data.clear();
  }

  public float getValueAt( int datum ) {
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
    return (float) ((SeriesDatum) data.get(datum)).getX();
  }

  public float getYCoord( int datum ) {
    return (float) getDatum(datum);
  }

  public double getDatum( int i ) {
    double value = ((SeriesDatum) data.get(i)).getY();
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

  public void setVisible( boolean isVisible ) {
    this.isVisible = isVisible;
  }

  public boolean isVisible() {
    return isVisible;
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