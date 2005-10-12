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
package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.agent.KaplanStrategy;

import uk.ac.liv.auction.core.Account;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.Auctioneer;
import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.TransparentAuctioneer;
import uk.ac.liv.auction.core.ZeroFundsAccount;

import uk.ac.liv.auction.stats.DailyStatsReport;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class KaplanStrategyTest extends TestCase {
  
  RandomRobinAuction auction;
  
  MockTrader trader;
  
  KaplanStrategy strategy;
  
  DailyStatsReport dailyStats;
  
  Auctioneer auctioneer;
  
  MarketQuote quote;
  
  public static final double S = 0.1;
  public static final int T = 10;
  
  public KaplanStrategyTest( String name ) {
    super(name);   
  }
  
  public void setUp() {
    auction = new RandomRobinAuction();
    trader = new MockTrader(this, 0, 0, 10, true);
    strategy = new KaplanStrategy();
    strategy.setS(S);
    strategy.setT(T);
    trader.setStrategy(strategy);
    strategy.setAgent(trader);
    auction.setMaximumRounds(100);
    auction.register(trader);
    dailyStats = new DailyStatsReport();
    auction.setReport(dailyStats);
    dailyStats.setAuction(auction);
    quote = new MarketQuote(100, 110);
    auctioneer = new MockAuctioneer(quote);
    auction.setAuctioneer(auctioneer);
    auction.begin();    
  }
  
  public void testTimeRunningOut() { 
    try {
      auction.step();
      assertTrue(!strategy.timeRunningOut());      
      for( int i=0; i<89; i++ ) {
        auction.step();
      }       
      assertTrue(!strategy.timeRunningOut());
      auction.step();
      assertTrue(strategy.timeRunningOut());
      auction.step();
      assertTrue(strategy.timeRunningOut());
    } catch ( AuctionClosedException e ) {
      fail(e.getMessage());
    }
  }
  
  public void testSmallSpread() {
    try {
      auction.step();
      
      trader.setIsSeller(true);
      
      quote.setAsk(109);
      quote.setBid(100);
      assertTrue( strategy.smallSpread() );
      
      quote.setAsk(120);
      quote.setBid(100);
      assertTrue( !strategy.smallSpread() );
      
      quote.setBid(109);
      quote.setAsk(100);
      assertTrue( strategy.smallSpread() );
      
      
      quote.setAsk(1000);
      quote.setBid(1100);
      assertTrue( strategy.smallSpread() );
      
      trader.setIsSeller(false);
      
      quote.setAsk(1000);
      quote.setBid(1100);
      assertTrue( !strategy.smallSpread() );
      
    } catch ( AuctionClosedException e ) {
      fail(e.getMessage());
    }
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(KaplanStrategyTest.class);
  }
  
}

class MockAuctioneer extends TransparentAuctioneer {

  protected MarketQuote staticQuote;
  
  protected ZeroFundsAccount account;
  
  public MockAuctioneer( MarketQuote staticQuote ) {
    this.staticQuote = staticQuote; 
    account = new ZeroFundsAccount(this);
  }
  
  public void generateQuote() {
    currentQuote = staticQuote;
  }

  public void endOfAuctionProcessing() {   
    super.endOfAuctionProcessing();
  }

  public void endOfRoundProcessing() {
    super.endOfRoundProcessing();
  }

  public boolean shoutsVisible() {
    return true;
  }
  
  public Account getAccount() {
    return account;
  }
  
}