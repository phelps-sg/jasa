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
 * @author Steve Phelps
 */

public class MemoryResidentDataSeries implements DataWriter, DataSeries {

  Vector data;

  protected boolean isVisible = true;

  protected boolean isTime = true;

  protected double time;

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
    if ( isTime ) {
      time = datum;
    } else {
      TimeSeriesDatum d = new TimeSeriesDatum(time, datum);
      logger.debug("Adding " + d);
      data.add(d);
    }
    isTime = !isTime;
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
    return (float) ((TimeSeriesDatum) data.get(datum)).getTime();
  }

  public float getYCoord( int datum ) {
    return (float) getDatum(datum);
  }

  public double getDatum( int i ) {
    double value = ((TimeSeriesDatum) data.get(i)).getValue();
    if ( Double.isNaN(value) || Double.isInfinite(value) ) {
      return 0;
    } else {
      return value;
    }
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


}

class TimeSeriesDatum {

  double time;

  double value;

  public TimeSeriesDatum( double time, double value ) {
    this.time = time;
    this.value = value;
  }

  public void setTime( double time ) { this.time = time; }
  public void setValue( double value ) { this.value = value; }

  public double getTime() { return time; }
  public double getValue() { return value; }

  public String toString() {
    return "(" + getClass() + " time:" + time + " value:" + value + ")";
  }

}