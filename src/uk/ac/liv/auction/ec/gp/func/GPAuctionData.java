package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.Shout;

import ec.gp.*;


public class GPAuctionData extends GPData {

  Shout shoutData;
  boolean boolData;

  public GPAuctionData() {
  }

  public void set( boolean data ) {
    this.boolData = data;
  }

  public void set( Shout data ) {
    this.shoutData = data;
  }

  public Shout getShoutData() {
    return shoutData;
  }

  public boolean getBoolData() {
    return boolData;
  }

  public GPData copyTo(GPData parm1) {
    /**@todo: implement this ec.gp.GPData abstract method*/
    return null;
  }
}