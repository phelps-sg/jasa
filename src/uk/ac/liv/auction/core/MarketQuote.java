package uk.ac.liv.auction.core;

import java.io.Serializable;

public class MarketQuote implements Serializable {

  double ask;
  double bid;

  public MarketQuote( double ask, double bid ) {
    this.ask = ask;
    this.bid = bid;
  }

  public MarketQuote( Shout ask, Shout bid ) {
    if ( ask == null ) {
      this.ask = Double.MAX_VALUE;
    } else {
      this.ask = ask.getPrice();
    }
    if ( bid == null ) {
      this.bid = 0;
    } else {
      this.bid = bid.getPrice();
    }
  }

  public void setAsk( double ask ) { this.ask = ask; }
  public void setBid( double bid ) { this.bid = bid; }
  public double getAsk() { return ask; }
  public double getBid() { return bid; }

  public String toString() {
    return "(MarketQuote bid:" + bid + " ask:" + ask +")";
  }

}