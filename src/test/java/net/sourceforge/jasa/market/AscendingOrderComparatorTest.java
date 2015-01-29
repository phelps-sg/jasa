package net.sourceforge.jasa.market;

import net.sourceforge.jasa.agent.MockTrader;
import net.sourceforge.jasa.agent.strategy.MockMarket;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import junit.framework.TestCase;

public class AscendingOrderComparatorTest extends TestCase {

	protected MockTrader trader1;
	protected MockTrader trader2;
	protected MarketSimulation market;
	protected AscendingOrderComparator comparator = new AscendingOrderComparator();

	public void setUp() {
		market = new MarketSimulation();
		trader1 = new MockTrader(this, 0, 0, market);
		trader2 = new MockTrader(this, 0, 0, market);
	}
	
	public void testAscendingOrders() {
		Order order1 = new Order(trader1, 1, 10.0, true);
		Order order2 = new Order(trader2, 1, 15.0, true);
		assertTrue(comparator.compare(order1, order2) < 0);
	}
}
