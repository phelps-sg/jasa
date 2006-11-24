package uk.ac.liv.supplychain;

import java.io.Serializable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.agent.ValuationPolicy;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.RoundClosingEvent;

public class AVGPriceForecastValuer implements ValuationPolicy, Serializable {

	public static final String P_SMOOTHING_PAR = "smoothing";
	
	private double smoothingParameter = 0;
	private double forecastedPrice = 0 ;
	private TradingAgent agent;
	
	public double determineValue(Auction auction) {
		Auction[] agentAuctions	= ((SupplyChainAgent)agent ).getAuction();
		int auctionIdx			= (int) (( SupplyChainRandomRobinAuction) auction).getId();
		return forecastedPrice;
	}

	public void consumeUnit(Auction auction) {
		// TODO Auto-generated method stub
	} 
	

	public void eventOccurred(AuctionEvent event) {
		if ( event instanceof AuctionOpenEvent ) {
			SupplyChainRandomRobinAuction auction = (SupplyChainRandomRobinAuction) event.getAuction();
			forecastedPrice = auction.getTransactionPrice( auction.getAge() ); 
		}//AuctionOpenEvent
		if ( event instanceof RoundClosingEvent) {
			SupplyChainRandomRobinAuction auction = (SupplyChainRandomRobinAuction) event.getAuction();
			double previousPrice = auction.getTransactionPrice( auction.getAge() );
			forecastedPrice = smoothingParameter * previousPrice + ( 1-smoothingParameter ) * forecastedPrice;
		}//RoundClosingEvent
	}//eventOccurred

	public void setAgent(TradingAgent agent)		{ this.agent = agent; }

	public void reset() {
		// TODO Auto-generated method stub
	}//reset

	public void setup(ParameterDatabase parameters, Parameter base) {
		Parameter smoothingParamT = base.push(P_SMOOTHING_PAR); 
		smoothingParameter = parameters.getDouble(smoothingParamT, null, 0);
	}//setup
}
