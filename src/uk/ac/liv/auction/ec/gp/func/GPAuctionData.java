/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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