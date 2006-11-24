package uk.ac.liv.supplychain;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.UniformPricingPolicy;

public class ThierrySPricingPolicy extends UniformPricingPolicy {
	
	public double determineClearingPrice( Shout bid, Shout ask, MarketQuote clearingQuote ) {
		//System.out.println("kInterval("+clearingQuote.getAsk()+" , "+ clearingQuote.getBid()+" ) = "+kInterval(clearingQuote.getAsk(), clearingQuote.getBid()));
		return kInterval(clearingQuote.getAsk(), clearingQuote.getBid());
	}
	
}
