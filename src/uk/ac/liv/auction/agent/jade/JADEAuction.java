package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.core.*;


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
    logger.updateQuoteLog(round++, getQuote());
    sweepDefunctTraders();
    auctioneer.endOfRoundProcessing();
    setChanged();
    notifyObservers();
  }

  public void newShout( Shout shout ) throws AuctionException {
    super.newShout(shout);
    numShoutsReceived++;
  }

  public boolean roundFinished() {
    return numShoutsReceived == getNumberOfTraders();
  }

}