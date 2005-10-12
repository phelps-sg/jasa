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

package uk.ac.liv.auction.electricity;

import junit.framework.*;


import uk.ac.liv.auction.ElectricityTest;
import uk.ac.liv.auction.electricity.ElectricityTrader;

import uk.ac.liv.auction.agent.PureSimpleStrategy;

import uk.ac.liv.util.CummulativeDistribution;

/**
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ElectricityStatsTest extends ElectricityTest {

  /**
   * @uml.property name="sMPS"
   * @uml.associationEnd
   */
  CummulativeDistribution sMPS;

  /**
   * @uml.property name="sMPB"
   * @uml.associationEnd
   */
  CummulativeDistribution sMPB;

  /**
   * @uml.property name="pSA"
   * @uml.associationEnd
   */
  CummulativeDistribution pSA;

  /**
   * @uml.property name="pBA"
   * @uml.associationEnd
   */
  CummulativeDistribution pBA;

  /**
   * @uml.property name="pST"
   * @uml.associationEnd
   */
  CummulativeDistribution pST;

  /**
   * @uml.property name="pBT"
   * @uml.associationEnd
   */
  CummulativeDistribution pBT;

  static final int NUM_SCHEDULES = 10;

  public ElectricityStatsTest( String name ) {
    super(name);
    org.apache.log4j.BasicConfigurator.configure();
  }

  public void checkSMP() {
    System.out.println(sMPB);
    System.out.println(sMPS);
    System.out.println(pBA);
    System.out.println(pSA);
    System.out.println(pBT);
    System.out.println(pST);
    assertTrue(Double.isNaN(sMPB.getMean()) || sMPB.getMean() < 0.01);
    assertTrue(Double.isNaN(sMPS.getMean()) || sMPS.getMean() < 0.01);
  }

  public void testSMPrcap1() {
    // for( double k=0; k<1; k+=0.5 ) {
    // System.out.println("Testing with k = " + k);
    // for( int i=0; i<NUM_SCHEDULES; i++ ) {
    // System.out.println("Schedule " + i);
    // randomizePrivateValues();
    // experimentSetup(3, 3, 10, 10);
    // auctioneer.setK(k);
    // runExperiment();
    // checkSMP();
    // }
    // }
  }

  public void initStats() {
    super.initStats();
    sMPS = new CummulativeDistribution("SMPS");
    sMPB = new CummulativeDistribution("SMPB");
    pBA = new CummulativeDistribution("PBA");
    pSA = new CummulativeDistribution("PSA");
    pBT = new CummulativeDistribution("PBT");
    pST = new CummulativeDistribution("PST");
  }

  public void updateStats() {
    super.updateStats();
    // stats.calculateStrategicMarketPower();
    sMPS.newData(stats.getSMPS());
    sMPB.newData(stats.getSMPB());
    pBA.newData(stats.getPBA());
    pSA.newData(stats.getPSA());
    pBT.newData(stats.getPBT());
    pST.newData(stats.getPST());
  }

  public void assignStrategy( ElectricityTrader agent ) {
    PureSimpleStrategy strategy = new PureSimpleStrategy(agent, 0, agent
        .getCapacity());
    agent.setStrategy(strategy);
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(ElectricityStatsTest.class);
  }

}