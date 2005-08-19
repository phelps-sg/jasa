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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.AuctionError;
import uk.ac.liv.auction.core.ShoutsNotVisibleException;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import java.io.Serializable;

import java.util.*;

import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.collections.list.TreeList;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Resetable;

import org.apache.log4j.Logger;

/**
 * <p>
 * A report that keeps a historical record of the shouts in the market that lead
 * to the last N transactions. This logger is used to keep historical data that
 * is used by various different trading strategies.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class HistoricalDataReport extends AbstractAuctionReport implements
    Serializable, Resetable {
  
  protected LinkedList asks = new LinkedList();

  protected LinkedList bids = new LinkedList();

  protected TreeBag sortedShouts = new TreeBag();

  protected HashSet acceptedShouts = new HashSet();

  protected int memorySize = 10;

  protected int currentMemoryCell = 0;

  protected int[] memoryBids = new int[memorySize];

  protected int[] memoryAsks = new int[memorySize];

  protected double lowestAskPrice;

  protected double highestBidPrice;
  
  protected boolean useShortCut = true;

  static final String P_MEMORYSIZE = "memorysize";
  static final String P_USESHORTCUT = "useshortcut";

  static Logger logger = Logger.getLogger(HistoricalDataReport.class);
  
  
  public HistoricalDataReport() {
    counter = new TraverseCounter();
  }
  

  public void setup( ParameterDatabase parameters, Parameter base ) {
    memorySize = parameters.getIntWithDefault(base.push(P_MEMORYSIZE), null,
        memorySize);
    memoryBids = new int[memorySize];
    memoryAsks = new int[memorySize];
    for ( int i = 0; i < memorySize; i++ ) {
      memoryBids[i] = 0;
      memoryAsks[i] = 0;
    }
    logger.debug("memorysize = " + memorySize);
    
    useShortCut = parameters.getBoolean(base.push(P_USESHORTCUT), null, useShortCut);
    
  }

  public void updateTransPriceLog( TransactionExecutedEvent event ) {
    Object o;
    currentMemoryCell = (currentMemoryCell + 1) % memorySize;
    if ( memoryAsks[currentMemoryCell] > 0 || memoryBids[currentMemoryCell] > 0 ) {
      for ( int i = 0; i < memoryAsks[currentMemoryCell]; i++ ) {
        o = asks.removeFirst();
        sortedShouts.remove(o);
      }

      for ( int i = 0; i < memoryBids[currentMemoryCell]; i++ ) {
        o = bids.removeFirst();
        sortedShouts.remove(o);
      }
      memoryBids[currentMemoryCell] = 0;
      memoryAsks[currentMemoryCell] = 0;
//      acceptedShouts.clear();
      markMatched(asks);
      markMatched(bids);
    }
    if (useShortCut) counter.setValid(false);
  }

  public void initialise() {
    acceptedShouts.clear();
    bids.clear();
    asks.clear();
    sortedShouts.clear();
    for ( int i = 0; i < memorySize; i++ ) {
      memoryBids[i] = 0;
      memoryAsks[i] = 0;
    }
    initialisePriceRanges();
    if (useShortCut) counter.setValid(false);
  }

  public void reset() {
    initialise();
  }

  public void updateShoutLog( ShoutPlacedEvent event ) {
    
    Shout shout = event.getShout();
    addToSortedShouts(shout);
    if ( shout.isAsk() ) {
      asks.add(shout);
      memoryAsks[currentMemoryCell]++;
      if ( shout.getPrice() < lowestAskPrice ) {
        lowestAskPrice = shout.getPrice();
      }
    } else {
      bids.add(shout);
      memoryBids[currentMemoryCell]++;
      if ( shout.getPrice() > highestBidPrice ) {
        highestBidPrice = shout.getPrice();
      }
    }
    if (useShortCut) counter.setValid(false);
  }

  public void roundClosed( AuctionEvent event ) {
    markMatched(asks);
    markMatched(bids);
    //    if ( getNumberOfTrades() > memorySize ) {
    //   deleteOldShouts();
    //}
    initialisePriceRanges();
    if (useShortCut) counter.setValid(false);
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof RoundClosedEvent ) {
      roundClosed(event);
    } else if ( event instanceof ShoutPlacedEvent ) {
      updateShoutLog((ShoutPlacedEvent) event);
    } else if ( event instanceof TransactionExecutedEvent ) {
      updateTransPriceLog((TransactionExecutedEvent) event);
    }
  }

  public int getNumberOfTrades() {
    return acceptedShouts.size() / 2;
  }

  public double getHighestBidPrice() {
    return highestBidPrice;
  }

  public double getLowestAskPrice() {
    return lowestAskPrice;
  }

  public List getBids() {
    return bids;
  }

  public List getAsks() {
    return asks;
  }

  public boolean accepted( Shout shout ) {
    return acceptedShouts.contains(shout);
  }

  public int getNumberOfAsks( double price, boolean accepted ) {
    return getNumberOfShouts(asks, price, accepted);
  }

  public int getNumberOfBids( double price, boolean accepted ) {
    return getNumberOfShouts(bids, price, accepted);
  }

  public Iterator sortedShoutIterator() {
    if (useShortCut) {
	    if (counter.isValid())
	      counter.restart();
	    else
	      counter.reset();
    }
    return sortedShouts.iterator();
  }

  public void addToSortedShouts( Shout shout ) {
    sortedShouts.add(shout);
  }

  /**
   * 
   * @param shouts
   * @param price
   *        the sign of price controls whether higher shouts or lower shouts are needed
   * @param accepted
   * @return
   *        the number of shouts that meet the specified condition
   */
  public int getNumberOfShouts( List shouts, double price, boolean accepted ) {
    
    int shortcut = -1;

    if (useShortCut) {
      if (accepted) {
        if (shouts == asks) {
          if (price >= 0) {
            counter.updateNumOfAcceptedAsksAbove(price);
            shortcut = counter.getNumOfAcceptedAsksAbove();
          } else {
            counter.updateNumOfAsksBelow(-price);
            counter.updateNumOfRejectedAsksBelow(-price);
            shortcut = counter.getNumOfAsksBelow() - counter.getNumOfRejectedAsksBelow();
          }
        } else {
          if (price >= 0) {
            counter.updateNumOfBidsAbove(price);
            counter.updateNumOfRejectedBidsAbove(price);
            shortcut = counter.getNumOfBidsAbove() - counter.getNumOfRejectedBidsAbove();
          } else {
            counter.updateNumOfAcceptedBidsBelow(-price);
            shortcut = counter.getNumOfAcceptedBidsBelow();
          }
        }
      } else {
        if (shouts == asks) {
          if (price >= 0) {
            // never arrive here
            throw new Error("There is a bug. You should never be here!");
          } else {
            counter.updateNumOfAsksBelow(-price);
            shortcut = counter.getNumOfAsksBelow();
          }
        } else {
          if (price >= 0) {
            counter.updateNumOfBidsAbove(price);
            shortcut = counter.getNumOfBidsAbove();
          } else {
            // never arrive here
            throw new Error("There is a bug. You should never be here!");
          }
        }
      }
      assert shortcut >= 0;
      return shortcut;
    }
    
    int numShouts = 0;
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout shout = (Shout) i.next();
      if ( (price >= 0 && shout.getPrice() >= price)
          || (price < 0 && shout.getPrice() <= -price) ) {
        if ( accepted ) {
          if ( acceptedShouts.contains(shout) ) {
            numShouts++;
          }
        } else {
          numShouts++;
        }
      }
    }
    
//    if (numShouts != shortcut) {
//      logger.info(shortcut + " - " + numShouts);
//      logger.info("access " + (accepted ? "accepted " : "")
//          + ((shouts == asks) ? "asks" : "bids") + " at " + (int) (price)
//          + " with length=" + shouts.size());
//      if (shouts == asks) {
//        logger.info(counter.sortedAsks);
//        logger.info(counter.sortedAcceptedAsks);
//        logger.info(counter.sortedRejectedAsks);
//      } else {
//        logger.info(counter.sortedBids);
//        logger.info(counter.sortedAcceptedBids);
//        logger.info(counter.sortedRejectedBids);
//      }
//      logger.info("********************************************************");
//    }
    
    return numShouts;
  }

  public void produceUserOutput() {
  }

  public Map getVariables() {
    return new HashMap();
  }

  protected void initialisePriceRanges() {
    highestBidPrice = Double.NEGATIVE_INFINITY;
    lowestAskPrice = Double.POSITIVE_INFINITY;
  }
  
  protected void markMatched( List shouts ) {
    try {
      Iterator i = shouts.iterator();
      while ( i.hasNext() ) {
        Shout s = (Shout) i.next();
        if ( auction.shoutAccepted(s) ) {
          acceptedShouts.add(s);
        }
      }
    } catch ( ShoutsNotVisibleException e ) {
      throw new AuctionError(e);
    }
  }

  public void finalReport() {
  }

  public String toString() {
    return "(" + getClass() + " auction:" + auction + " memorySize:"
        + memorySize + " bids:" + bids + " asks:" + asks + ")";
  }
  
  private TraverseCounter counter;
  private static final Error CounterInvalid = new Error("Attempting to access invalid traverse counter in HistoricalDataReport");
 
  class TraverseCounter {
    
    private boolean valid;

    protected TreeList sortedAsks;
    protected TreeList sortedBids;
    protected TreeList sortedAcceptedAsks;
    protected TreeList sortedAcceptedBids;
    protected TreeList sortedRejectedAsks;
    protected TreeList sortedRejectedBids;

    protected int numOfAsksBelow;
    protected int numOfBidsAbove;
    protected int numOfAcceptedAsksAbove;
    protected int numOfAcceptedBidsBelow;
    protected int numOfRejectedAsksBelow;
    protected int numOfRejectedBidsAbove;

    protected ListIterator asksI;
    protected ListIterator bidsI;
    protected ListIterator acceptedAsksI;
    protected ListIterator acceptedBidsI;
    protected ListIterator rejectedAsksI;
    protected ListIterator rejectedBidsI;

    
    public void reset() {
      
      sortedAsks = new SortedTreeList("sortedAsks", asks);
      sortedBids = new SortedTreeList("sortedBids", bids);
      
      sortedAcceptedAsks = new SortedTreeList("sortedAcceptedAsks");
      sortedAcceptedBids = new SortedTreeList("sortedAcceptedBids");
      sortedRejectedAsks = new SortedTreeList("sortedRejectedAsks");
      sortedRejectedBids = new SortedTreeList("sortedRejectedBids");
      
      Shout s;

      Iterator i = asks.iterator();
      while (i.hasNext()) {
        s = (Shout) i.next();
        if (acceptedShouts.contains(s)) {
          sortedAcceptedAsks.add(s);
        } else {
          sortedRejectedAsks.add(s);
        }
      }

      i = bids.iterator();
      while (i.hasNext()) {
        s = (Shout) i.next();
        if (acceptedShouts.contains(s)) {
          sortedAcceptedBids.add(s);
        } else {
          sortedRejectedBids.add(s);
        }
      }
      
      restart();
    }

    public void restart() {
      valid = true;

      asksI = sortedAsks.listIterator();
      bidsI = sortedBids.listIterator();      
      acceptedAsksI = sortedAcceptedAsks.listIterator();
      acceptedBidsI = sortedAcceptedBids.listIterator();
      rejectedAsksI = sortedRejectedAsks.listIterator();
      rejectedBidsI = sortedRejectedBids.listIterator();

      numOfBidsAbove = sortedBids.size();      
      numOfAsksBelow = 0;
      numOfAcceptedAsksAbove = sortedAcceptedAsks.size();
      numOfAcceptedBidsBelow = 0;
      numOfRejectedAsksBelow = 0;
      numOfRejectedBidsAbove = sortedRejectedBids.size();
    }
    

    public void updateNumOfAsksBelow(double price) {
      while (asksI.hasNext())
        if (((Shout)asksI.next()).getPrice() <= price)
          numOfAsksBelow++;
        else {
          try {
            asksI.previous();
          } catch (Exception e) {
            logger.info(e);
            asksI.previous();
          }
          break;
        }
    }

    public void updateNumOfBidsAbove(double price) {
      while (bidsI.hasNext())
        if (((Shout)bidsI.next()).getPrice() < price)
          numOfBidsAbove--;
        else {
          try{
            bidsI.previous();
          } catch (Exception e) {
            logger.info(e);
            bidsI.previous();
          }
          break;
        }
    }

    public void updateNumOfAcceptedAsksAbove(double price) {
      while (acceptedAsksI.hasNext())
        if (((Shout)acceptedAsksI.next()).getPrice() < price)
          numOfAcceptedAsksAbove--;
        else {
          try{
            acceptedAsksI.previous();
          } catch (Exception e) {
            logger.info(e);
            acceptedAsksI.previous();
          }
          break;
        }
    }
    
    public void updateNumOfAcceptedBidsBelow(double price) {
      while (acceptedBidsI.hasNext())
        if (((Shout)acceptedBidsI.next()).getPrice() <= price)
          numOfAcceptedBidsBelow++;
        else {
          // NOTE: due to a possible bug in TreeList, NullPointerException may be
          // thrown. Simply doing it again seems working fine.
          try {
            acceptedBidsI.previous();
          } catch (Exception e) {
            logger.info(e);
            acceptedBidsI.previous();
          }
          break;
        }
    }
    
    public void updateNumOfRejectedAsksBelow(double price) {
      while (rejectedAsksI.hasNext())
        if (((Shout)rejectedAsksI.next()).getPrice() <= price)
          numOfRejectedAsksBelow++;
        else {
          try{
            rejectedAsksI.previous();
          } catch (Exception e) {
            logger.info(e);
            rejectedAsksI.previous();
          }
          break;
        }
    }
    
    public void updateNumOfRejectedBidsAbove(double price) {
      while (rejectedBidsI.hasNext())
        if (((Shout)rejectedBidsI.next()).getPrice() < price)
          numOfRejectedBidsAbove--;
        else {
          try {
            rejectedBidsI.previous();
          } catch (Exception e) {
            logger.info(e);
            rejectedBidsI.previous();
          }
          break;
        }
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }
    
    public boolean isValid() {
      return valid;
    }

    /**
     * @return Returns the numOfAsksBelow.
     */
    public int getNumOfAsksBelow() {
      if (!valid) throw CounterInvalid;
      return numOfAsksBelow;
    }
    /**
     * @return Returns the numOfBidsAbove.
     */
    public int getNumOfBidsAbove() {
      if (!valid) throw CounterInvalid;
      return numOfBidsAbove;
    }
    /**
     * @return Returns the numOfAcceptedAsksAbove.
     */
    public int getNumOfAcceptedAsksAbove() {
      if (!valid) throw CounterInvalid;
      return numOfAcceptedAsksAbove;
    }
    /**
     * @return Returns the numOfAcceptedBidsBelow.
     */
    public int getNumOfAcceptedBidsBelow() {
      if (!valid) throw CounterInvalid;
      return numOfAcceptedBidsBelow;
    }
    /**
     * @return Returns the numOfRejectedAsksBelow.
     */
    public int getNumOfRejectedAsksBelow() {
      if (!valid) throw CounterInvalid;
      return numOfRejectedAsksBelow;
    }
    /**
     * @return Returns the numOfRejectedBidsAbove.
     */
    public int getNumOfRejectedBidsAbove() {
      if (!valid) throw CounterInvalid;
      return numOfRejectedBidsAbove;
    }
  }
    
  static class SortedTreeList extends TreeList {
    private String name;
    
    public SortedTreeList(String name) {
      this.name = name;
    }
    
    public SortedTreeList(String name, Collection c) {
      super(c);
      this.name = name;
    }
    
    public boolean add(Object o) {
      insert(0, size()-1, o);
      return true;
    }
    
    private void insert(int b, int e, Object o) {
      if (b > e)
        add(b, o);
      else {
        int c = ((Comparable)o).compareTo(get((b+e)/2));
        
        if (c == 1)
          insert(1+((b+e)/2), e, o);
        else if (c == -1)
          insert(b, ((b+e)/2)-1, o);
        else
          add((b+e)/2, o);
      }
      
    }

    public String toString() {
      String s = "[";
      ListIterator iterator = listIterator();
      while (iterator.hasNext()) {
        s += (int)((Shout)iterator.next()).getPrice() + " "; 
      }
      s += "] "+name+"(size="+size()+")";
      return s;
    }
  }

}