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

package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;

/**
 * @author Steve Phelps
 */

public class GPConstrainedTradingStrategy extends GPTradingStrategy {


  public void modifyShout( Shout shout, Auction auction ) {

    super.modifyShout(shout, auction);

    if ( (agent.isSeller() && shout.getPrice() < agent.getPrivateValue()) ||
          (agent.isBuyer() && shout.getPrice() > agent.getPrivateValue()) ) {
      misbehaved = true;
    }

  }

}