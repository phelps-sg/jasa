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

package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.*;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPConstrainedTradingStrategy extends GPTradingStrategy {


  public boolean modifyShout( Shout.MutableShout shout ) {

    Shout newShout = super.modifyShout(shout, auction);

    if ( (agent.isSeller() && newShout.getPrice() < agent.getValuation(auction)) ||
          (agent.isBuyer() && newShout.getPrice() > agent.getValuation(auction)) ) {
      gpIndividual.illegalResult();
      return false;
    }

    return true;
  }

}