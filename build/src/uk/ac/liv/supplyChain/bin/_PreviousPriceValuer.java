/**
 * 
 */
package uk.ac.liv.supplyChain.bin;

import java.io.Serializable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import sun.security.krb5.internal.ag;
import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.agent.ValuationPolicy;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.speculation.SimpleTradingAgent;
import uk.ac.liv.auction.stats.EquilibriumReport;

/**
 * @author moyaux
 *
 */
public class _PreviousPriceValuer implements ValuationPolicy, Serializable {

	protected double previousPrice = 1;
	
	private SimpleTradingAgent agent;
	
	/* (non-Javadoc)
	 * @see uk.ac.liv.auction.agent.ValuationPolicy#determineValue(uk.ac.liv.auction.core.Auction)
	 */
	public double determineValue(Auction auction) {
		EquilibriumReport eqReport = new EquilibriumReport( (RandomRobinAuction) auction );
		//eqReport.calculate();
		double currentPrice = eqReport.calculateMidEquilibriumPrice();
		
		return currentPrice;
	}

	/* (non-Javadoc)
	 * @see uk.ac.liv.auction.agent.ValuationPolicy#consumeUnit(uk.ac.liv.auction.core.Auction)
	 */
	public void consumeUnit(Auction arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see uk.ac.liv.auction.agent.ValuationPolicy#eventOccurred(uk.ac.liv.auction.event.AuctionEvent)
	 */
	public void eventOccurred(AuctionEvent event) {
		//super.eventOccurred(event);
		/*
		if ( event instanceof EndOfDayEvent)
			System.out.println("End of Day");
		if ( event instanceof RoundClosingEvent)
			System.out.println("end of Round");
			*/
		if ( event instanceof RoundClosedEvent)
			processEndOfRound(event);
		/*
		if ( event instanceof AuctionOpenEvent ) {
			auctionOpen((AuctionOpenEvent) event);
		}
		*/
	}

	/* (non-Javadoc)
	 * @see uk.ac.liv.util.Resetable#reset()
	 */
	public void reset() {
		
	}//reset

	/* (non-Javadoc)
	 * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase, ec.util.Parameter)
	 */
	public void setup( ParameterDatabase parameters, Parameter base ) {
		//TODO
	}

	public void setAgent(AbstractTradingAgent agent){ this.agent = (SimpleTradingAgent) agent; }

	public void setAgent(TradingAgent agent)		{ this.agent = (SimpleTradingAgent) agent; }
	
	public void processEndOfRound(AuctionEvent event) {
		Auction auction = event.getAuction();
		
		// taken from EquilibriumPriceStrategy
		EquilibriumReport eqReport = new EquilibriumReport( (RandomRobinAuction) auction);
		eqReport.calculate();
		previousPrice = eqReport.calculateMidEquilibriumPrice();
	}//updatePreviousPrice
}
