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

package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.core.*;

/**
 * A variation of a RandomRobinAuction in which a round
 * ends only when all agents have submitted shouts.
 *
 * @author Steve Phelps
 */

public class JADEAuction extends RandomRobinAuction {

  int numShoutsReceived = 0;

  public void initiateRound() throws AuctionClosedException {
    if ( closed() ) {
      throw new AuctionClosedException("Auction is closed.");
    }
    if ( maximumRounds > 0 && round >= maximumRounds ) {
      close();
    } else {
      numShoutsReceived = 0;
      requestShouts();
    }
  }

  public void finaliseRound() {
    updateQuoteLog(round++, getQuote());
    sweepDefunctTraders();
    auctioneer.endOfRoundProcessing();
    setChanged();
    notifyObservers();
    informRoundClosed();
    if ( logger != null ) {
      logger.endOfRound();
    }
  }

  public void newShout( Shout shout ) throws AuctionException {
    super.newShout(shout);
    numShoutsReceived++;
  }

  public boolean roundFinished() {
    return numShoutsReceived == getNumberOfTraders();
  }

}