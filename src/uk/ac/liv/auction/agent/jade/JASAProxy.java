/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

package uk.ac.liv.auction.agent.jade;

import jade.core.Agent;
import jade.core.AID;

/**
 * Super-class for JASA/JADE proxies.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class JASAProxy {

  /**
   * The JADE agent we are a proxy for.
   */
  protected AID targetJadeID;

  /**
   * The JADE agent that is communicating with this proxy.
   */
  protected Agent sender;

  public JASAProxy( AID targetJadeID, Agent sender ) {
//    if ( targetJadeID == null || sender == null ) {
//      throw new IllegalArgumentException("Must specify a sender and a tagetJadeID when constructing a JASAProxy");
//    }
    this.targetJadeID = targetJadeID;
    this.sender = sender;
  }

  public AID getSenderAID() {
    return sender.getAID();
  }

  public AID getTargetAID() {
    return targetJadeID;
  }

}