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

package uk.ac.liv.util;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class HeavyweightDistribution extends CummulativeDistribution {

  protected ArrayList data;
  
  private static final int INITIAL_SIZE = 10000;
  
  static Logger logger = Logger.getLogger(HeavyweightDistribution.class);
  
  public HeavyweightDistribution( String name ) {
    super(name);
  }
  
  public void initialise() {
    super.initialise();
    data = new ArrayList(INITIAL_SIZE);    
  }
  
  public void newData( double datum ) {
    super.newData(datum);
    data.add(new Double(datum));
  }
  
  public double getTrimmedMean( double p ) {
    Collections.sort(data);
    int trimmedN = (int) ((p/2) * n);    
    double trimmedTotal = 0;
    for( int i=trimmedN; i<(n - trimmedN); i++ ) {
      trimmedTotal += ((Double) data.get(i)).doubleValue();
    }
    return trimmedTotal / (n-trimmedN*2);
  }
    
}
