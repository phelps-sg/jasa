package net.sourceforge.jasa.agent.strategy;

import java.util.Iterator;

import junit.framework.TestCase;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
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

	SimpleMarkupStrategy strategy;
	
	RandomEngine prng;
	
	NoiseTraderForecaster forecaster;
	
	MockMarket market;
	
	MockTrader trader;
	
	ReturnForecastValuationPolicy valuationPolicy;
	
	protected void setUp() throws Exception {
		super.setUp();
		strategy = new SimpleMarkupStrategy();
		market = new MockMarket();
		EventScheduler scheduler = new SpringSimulationController();
		trader = new MockTrader(this, 1, 0, scheduler);
		prng = new MersenneTwister64(PRNGTestSeeds.UNIT_TEST_SEED);
		forecaster = new NoiseTraderForecaster(prng);
		ForecastTradeDirectionPolicy direction = new ForecastTradeDirectionPolicy();
		direction.setPrng(prng);
		strategy.setTradeDirectionPolicy(direction);
		strategy.setPrng(prng);
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

}
