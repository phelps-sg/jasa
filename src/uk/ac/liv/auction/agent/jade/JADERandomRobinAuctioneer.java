package uk.ac.liv.auction.agent.jade;


import uk.ac.liv.auction.core.ContinuousDoubleAuctioneer;

public class JADERandomRobinAuctioneer extends JADEAuctionAdaptor {

  public JADERandomRobinAuctioneer() {
    super(new JADEAuction());
    ContinuousDoubleAuctioneer auctioneer = new ContinuousDoubleAuctioneer();
    auction.setAuctioneer(auctioneer);
  }

}