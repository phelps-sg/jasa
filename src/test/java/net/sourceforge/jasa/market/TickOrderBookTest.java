package net.sourceforge.jasa.market;

import net.sourceforge.jasa.agent.MockTrader;
import junit.framework.TestCase;

public class TickOrderBookTest extends TestCase {

	TickOrderBook book;

	MarketSimulation auction;
	
	static final int TICK_SIZE = 4;
	
	public void setUp() {
		book = new TickOrderBook(TICK_SIZE);
		auction = new MarketSimulation();	
	}

	public void testRounding() {
		try {
			int quantity = 1000;
			double price = 0.123456;
			MockTrader trader = new MockTrader(this, 0, 0, auction);
			Order testOrder = new Order(trader, quantity, price, true);
			System.out.println("Before adding to book, testOrder = " + testOrder);
			book.add(testOrder);
			System.out.println("After adding to book, testOrder = " + testOrder);
			assertTrue(testOrder.getPrice() == 0.1235);
		} catch (DuplicateShoutException e) {
			fail(e.getMessage());
		}
	}

}
