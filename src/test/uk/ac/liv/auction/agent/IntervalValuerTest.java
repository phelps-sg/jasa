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

package test.uk.ac.liv.auction.agent;

import junit.framework.TestCase;

import uk.ac.liv.auction.agent.IntervalValuer;

import uk.ac.liv.auction.core.RoundRobinAuction;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class IntervalValuerTest extends TestCase {
    
  protected IntervalValuer valuer;
  
  protected ParameterDatabase paramDb;
  
  protected Parameter base;
  
  protected RoundRobinAuction auction;
  
  public static final double MIN_VALUE = 10;
  public static final double STEP = 5;
  
  public IntervalValuerTest( String name ) {
    super(name);
  }
  
  public void setUp() {
    valuer = assignValuer();
    paramDb = new ParameterDatabase();
    base = new Parameter("test");
    paramDb.set(base.push(IntervalValuer.P_MINVALUE), MIN_VALUE + "");
    paramDb.set(base.push(IntervalValuer.P_STEP), STEP + "");    
    auction = new RoundRobinAuction("test");
  }
  
  public void testStep() {
    double value;
    
    valuer.setup(paramDb, base);
    
    value = valuer.determineValue(auction);
    System.out.println("value = " + value);
    assertTrue(value == MIN_VALUE);
    
    valuer.setup(paramDb, base);
    
    value = valuer.determineValue(auction);
    System.out.println("value = " + value);
    assertTrue(value == MIN_VALUE + STEP);
  }
  
  public abstract IntervalValuer assignValuer();

}