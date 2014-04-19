package net.sourceforge.jasa.market.auctioneer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.market.ClearingHouseAuctioneerTest;
import net.sourceforge.jasa.market.DuplicateShoutException;
import net.sourceforge.jasa.market.IllegalOrderException;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;

public class SingleSidedPricingTest extends TestCase {

	protected GenericAuctioneer auctioneer;
	
	protected SimpleTradingAgent buyer1, buyer2, seller;
	
	public static final double FIRST_PRICE = 101;
	public static final double SECOND_PRICE = 90;
	
	public static final double RESERVE_PRICE = 80;
	
	public void setUp() {
		auctioneer = new GenericAuctioneer();
		buyer1 = new SimpleTradingAgent(null);
		buyer2 = new SimpleTradingAgent(null);
		seller = new SimpleTradingAgent(null);
	}
	
	/**
	 * In a single-sided auction where there is a single 
	 * ask order representing the seller's reserve price,
	 * the ask and the bid quote should correspond to the 
	 * 1st-highest-bid and 2nd-highest-bid respectively
	 * 
	 */
	public void testQuote() {
		try {
			Order bid1 = new Order();
			bid1.setPrice(RESERVE_PRICE);
			bid1.setIsBid(true);
			bid1.setQuantity(1);
			bid1.setAgent(buyer1);
			Order bid2 = new Order();
			bid2.setPrice(FIRST_PRICE);
			bid2.setIsBid(true);
			bid2.setQuantity(1);
			bid2.setAgent(buyer2);
			Order ask = new Order();
			ask.setPrice(SECOND_PRICE);
			ask.setIsBid(false);
			ask.setQuantity(1);
			auctioneer.newOrder(ask);
			auctioneer.newOrder(bid1);
			auctioneer.newOrder(bid2);
			auctioneer.generateQuote();
			MarketQuote quote = auctioneer.getQuote();
			assertTrue("Bid quote should be 2nd-highest bid price", 
							quote.getBid() == SECOND_PRICE);
			assertTrue("Ask quote should be 1st-highest bid price",
							quote.getAsk() == FIRST_PRICE);
		} catch (DuplicateShoutException e) {
			e.printStackTrace();
			fail();
		} catch (IllegalOrderException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public static Test suite() {
		return new TestSuite(ClearingHouseAuctioneerTest.class);
	}

}
