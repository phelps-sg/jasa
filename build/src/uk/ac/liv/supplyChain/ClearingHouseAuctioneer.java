package uk.ac.liv.supplyChain;

import java.util.Iterator;
import java.util.List;

import com.bbn.openmap.omGraphics.grid.GridData.Double;

import sun.security.krb5.internal.av;
import sun.security.krb5.internal.bi;
import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;

/**
 * works exactly as uk.ac.liv.auction.core.ClearingHouseAuctioneer, except that
 * a price different from zero is calculated when asks and bids cannot be matched.
 * How this price is calculated may not be accurate.
 * @author moyaux
 *
 */

public class ClearingHouseAuctioneer extends
		uk.ac.liv.auction.core.ClearingHouseAuctioneer {
	
	private double clearingPrice = 0;
	
	public double getClearingPrice()	{ return clearingPrice; }
	
	public void clear() {
		double askQuote = askQuote();
		double bidQuote = bidQuote();
	    clearingQuote = new MarketQuote(askQuote, bidQuote);
	    List shouts = shoutEngine.getMatchedShouts();
	    Iterator i = shouts.iterator();

	    
	    // if asks and bids can be matched
	    if ( i.hasNext() )
	    	while ( i.hasNext() ) {
	    		Shout bid = (Shout) i.next();
	    		Shout ask = (Shout) i.next();      
	    		clearingPrice = determineClearingPrice(bid, ask);
	    		clear(ask, bid, clearingPrice);
	    	}
	    else {
	    	if ( (bidQuote > 0) && ( !java.lang.Double.isInfinite(bidQuote) ) && ( !java.lang.Double.isNaN(bidQuote) ) ) {
	    		if ( (askQuote > 0) && ( !java.lang.Double.isInfinite(askQuote) ) && ( !java.lang.Double.isNaN(askQuote) ) ) {
	    			if ( bidQuote > askQuote ) {
	    				clearingPrice = bidQuote;
	    				//XXX System.out.println(auction.getAge()+" cas 1 ClearingHouseAuctioneer.clear newPrice ="+clearingPrice+" bidQuote="+bidQuote+" askQuote="+askQuote);
	    			}
	    			else {
	    				clearingPrice = askQuote;
	    				//XXX System.out.println(auction.getAge()+" cas 2 ClearingHouseAuctioneer.clear newPrice ="+clearingPrice+" bidQuote="+bidQuote+" askQuote="+askQuote);
	    			}
	    		}
	    		else {
	    			clearingPrice = bidQuote;
    				//XXX System.out.println(auction.getAge()+" cas 3 ClearingHouseAuctioneer.clear newPrice ="+clearingPrice+" bidQuote="+bidQuote+" askQuote="+askQuote);
    			}
	    	}
	    	else if ( (askQuote > 0) && ( !java.lang.Double.isInfinite(askQuote) ) && ( !java.lang.Double.isNaN(askQuote) ) ) {
				clearingPrice = askQuote;
				//XXX System.out.println(auction.getAge()+" cas 4 ClearingHouseAuctioneer.clear newPrice ="+clearingPrice+" bidQuote="+bidQuote+" askQuote="+askQuote);
			}
	    	else {
				clearingPrice = ((SupplyChainRandomRobinAuction) auction).getTransactionPrice();
				//XXX System.out.println(auction.getAge()+" cas 5 ClearingHouseAuctioneer.clear newPrice ="+clearingPrice+" bidQuote="+bidQuote+" askQuote="+askQuote);
			}
	    }//big else
	    ((SupplyChainRandomRobinAuction) auction).setNewTransactionPrice(clearingPrice);
	    //XXX System.out.println(auction.getAge()+" ClearingHouseAuctioneer.clear newPrice ="+clearingPrice);
	  }//clear
}//class