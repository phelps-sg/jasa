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

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Steve Phelps
 */

public class MemoryResidentDataSeries implements DataWriter {

  Vector data;

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
    data.add(new Double(datum));
  }

  public double getDatum( int i ) {
    return ((Double) data.get(i)).doubleValue();
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

  public int size() {
    return data.size();
  }

}