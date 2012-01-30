package net.sourceforge.jasa.agent.strategy;

import junit.framework.TestCase;
import net.sourceforge.jasa.agent.MockTrader;
import net.sourceforge.jasa.agent.valuation.NoiseTraderForecaster;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.market.AuctionException;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.sim.PRNGTestSeeds;
import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

public class ReturnForecastStrategyTest extends TestCase {

	ReturnForecastStrategy strategy;
	
	RandomEngine prng;
	
	NoiseTraderForecaster forecaster;
	
	MockMarket market;
	
	MockTrader trader;
	
	ReturnForecastValuationPolicy valuationPolicy;
	
	protected void setUp() throws Exception {
		super.setUp();
		strategy = new ReturnForecastStrategy();
		market = new MockMarket();
		trader = new MockTrader(this, 1, 0, null);
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		forecaster = new NoiseTraderForecaster(prng);
		valuationPolicy =
				new ReturnForecastValuationPolicy();
		valuationPolicy.setForecaster(forecaster);
		trader.setValuationPolicy(valuationPolicy);
		strategy.setAgent(trader);
		strategy.auction = market;
		strategy.setPrng(prng);
	}
	
	public void testNoiseTraderForecast() {
		MarketQuote quote = new MarketQuote(1.00, 1.00);
		market.setQuote(quote);
		double priceForecast = valuationPolicy.getPriceForecast(market);
		System.out.println("priceForecast = " + priceForecast);
		assertTrue(priceForecast <= Math.exp(1));
	}
	
	public void testNoQuote() {
		MarketQuote quote = new MarketQuote(null, null);
		market.setQuote(quote);
		Order currentShout = new Order();
		Order newShout = strategy.modifyOrder(currentShout, market);
		System.out.println("newShout = " + newShout);
		assertFalse(Double.isNaN(newShout.getPrice()));
	}

	public static class MockMarket implements Market {

		protected MarketQuote quote;
		
		@Override
		public void clear(Order ask, Order bid, double price) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clear(Order ask, Order bid, double buyerCharge,
				double sellerPayment, int quantity) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean closed() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getAge() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Auctioneer getAuctioneer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getDay() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Order getLastOrder() throws ShoutsNotVisibleException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNumberOfTraders() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getRemainingTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getRound() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean orderAccepted(Order shout)
				throws ShoutsNotVisibleException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void placeOrder(Order shout) throws AuctionException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void printState() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeOrder(Order shout) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean transactionsOccurred() throws ShoutsNotVisibleException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public MarketQuote getQuote() {
			return quote;
		}

		public void setQuote(MarketQuote quote) {
			this.quote = quote;
		}

		@Override
		public double getLastTransactionPrice() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getCurrentPrice() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
