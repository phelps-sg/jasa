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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class TruthTellingStrategy extends FixedQuantityStrategyImpl {
	
  public TruthTellingStrategy( AbstractTraderAgent agent ) {
    super(agent);
  }
  
  public TruthTellingStrategy() {
    super();
  }
	
  public boolean modifyShout( Shout.MutableShout shout ) {    
    shout.setPrice(agent.getValuation(auction));       
    return super.modifyShout(shout);
  }

	public void endOfRound( Auction auction ) {		
	}
}
